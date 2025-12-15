/**
 *
 */
package com.sabmiller.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.util.Config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sabm.core.model.DealNotificationEmailProcessModel;
import com.sabmiller.core.notification.service.NotificationService;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.DealRangeJson;
import com.sabmiller.facades.notification.DealEmailNotificationData;


/**
 * @author raul.b.abatol.jr
 *
 */
public class DealNotificationEmailContext extends AbstractEmailContext<DealNotificationEmailProcessModel>
{
	private CustomerData customerData;
	private List<DealEmailNotificationData> deals;
	private int copyRightYear;
	private String agreedInstoreDealUrl;



	private String lastChanceDealUrl;
	private String onlineOnlyDealUrl;
	private String limitedOfferDealUrl;

	private final String CATALOG_NAME = "sabmContentCatalog";
	private final String CATALOG_VERSION = "Online";
	private final String AGREED_INSTORE_DEAL = "agreedInstoreDeal";
	private final String ONLINE_ONLY_DEAL = "onlineOnlyDeal";
	private final String LAST_CHANCE_DEAL = "lastChanceDeal";
	private final String LIMITED_OFFER_DEAL = "limitedOfferDeal";


	@Resource(name = "customerConverter")
	private Converter<UserModel, CustomerData> customerConverter;


	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;

	@Resource(name = "notificationService")
	private NotificationService notificationService;


	@Resource(name = "mediaService")
	private MediaService mediaService;


	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	@Override
	public void init(final DealNotificationEmailProcessModel businessProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(businessProcessModel, emailPageModel);
		put(SECURE_BASE_URL, get(SECURE_BASE_URL) + "/login?targetUrl=");
		customerData = customerConverter.convert(businessProcessModel.getCustomer());
		final List<DealJson> sortedDeals = sabmDealsSearchFacade.searchDeals(businessProcessModel.getB2bUnit(),
				notificationService.getSafeNextAvailableDeliveryDate(businessProcessModel.getB2bUnit()));
		final List<DealEmailNotificationData> dealsList = new ArrayList<DealEmailNotificationData>();
		final CatalogVersionModel catalog = catalogVersionService.getCatalogVersion(CATALOG_NAME, CATALOG_VERSION);
		final MediaModel agreedInstoreDeal = mediaService.getMedia(catalog, AGREED_INSTORE_DEAL);
		final MediaModel lastChanceDeal = mediaService.getMedia(catalog, LAST_CHANCE_DEAL);
		final MediaModel onlineOnlyDeal = mediaService.getMedia(catalog, ONLINE_ONLY_DEAL);
		final MediaModel limitedOfferDeal = mediaService.getMedia(catalog, LIMITED_OFFER_DEAL);
		if (agreedInstoreDeal != null){
			agreedInstoreDealUrl = agreedInstoreDeal.getURL();
		}
		if (lastChanceDeal != null){
			lastChanceDealUrl = lastChanceDeal.getURL();
		}
		if (onlineOnlyDeal != null){
			onlineOnlyDealUrl = onlineOnlyDeal.getURL();
		}
		if (limitedOfferDeal != null){
			limitedOfferDealUrl = limitedOfferDeal.getURL();
		}
		for (final DealJson dealJson : sortedDeals)
		{
			if (dealJson != null){
				final DealEmailNotificationData notificationData = new DealEmailNotificationData();
				dealJson.setRanges(this.sortList(dealJson.getRanges()));
				notificationData.setActive(dealJson.isActive());
				notificationData.setDaysRemain(this.getDaysRemain(dealJson));
				if (CollectionUtils.isNotEmpty(dealJson.getRanges())
						&& CollectionUtils.isNotEmpty(dealJson.getRanges().get(0).getBaseProducts()))
				{
					notificationData.setImageUrl(dealJson.getRanges().get(0).getBaseProducts().get(0).getImage());
				}
				String dealTitle = dealJson.getTitle();
				if(StringUtils.isNotEmpty(dealTitle)){
					dealTitle = dealTitle.replace("<b>","<b><font color=\"#002f5f\">");
					dealTitle = dealTitle.replace("</b>", "</font></b>");
				}
				notificationData.setTitle(dealTitle);
				notificationData.setLastChance(isLastChance(dealJson));
				notificationData.setLimitedOffer(dealJson.getBadges().indexOf(2) >= 0);
				notificationData.setOnlineOnly(dealJson.getBadges().indexOf(3) >= 0);
				notificationData.setMinQty(dealJson.getRanges().get(0).getMinQty() == null ?
						0 : dealJson.getRanges().get(0).getMinQty());
				notificationData.setRangeTitle(dealJson.getRanges().size() == 1 ?
						dealJson.getBrands().get(0) : dealJson.getRanges().get(0).getTitle());
				notificationData.setValidFrom(SabmDateUtils.toString(new Date(dealJson.getValidFrom()),
						Config.getString("sabm.site.date.pattern", "dd/MM/yyyy")));
				notificationData.setValidTo(
						SabmDateUtils.toString(new Date(dealJson.getValidTo()), Config.getString("sabm.site.date.pattern", "dd/MM/yyyy")));
				dealsList.add(notificationData);
			}


		}
		final Comparator<DealEmailNotificationData> ALPHABETICAL_ORDER = new Comparator<DealEmailNotificationData>() {
			public int compare(final DealEmailNotificationData str1, final DealEmailNotificationData str2) {
				final int res = String.CASE_INSENSITIVE_ORDER.compare(str1.getRangeTitle(), str2.getRangeTitle());
				return res;
			}
		};

		Collections.sort(dealsList, ALPHABETICAL_ORDER);
		setDeals(dealsList);
	}

	@Override
	protected BaseSiteModel getSite(final DealNotificationEmailProcessModel businessProcessModel)
	{

		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final DealNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final DealNotificationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getLanguage();
	}

	/**
	 * @return the customerData
	 */
	public CustomerData getCustomerData()
	{
		return customerData;
	}

	/**
	 * @param customerData
	 *           the customerData to set
	 */
	public void setCustomerData(final CustomerData customerData)
	{
		this.customerData = customerData;
	}

	/**
	 * @return the deals
	 */
	public List<DealEmailNotificationData> getDeals()
	{
		return deals;
	}

	/**
	 * @param deals
	 *           the deals to set
	 */
	public void setDeals(final List<DealEmailNotificationData> deals)
	{
		this.deals = deals;
	}


	/**
	 * @param dealRanges
	 *           the unsorted list
	 * @return the sortedList
	 */
	public List<DealRangeJson> sortList(final List<DealRangeJson> dealRanges)
	{
		dealRanges.sort((final DealRangeJson d1, final DealRangeJson d2) -> d1.getTitle().compareToIgnoreCase(d2.getTitle()));

		for (final DealRangeJson dealRange : dealRanges)
		{
			dealRange.getBaseProducts().sort((final DealBaseProductJson base1, final DealBaseProductJson base2) -> base1.getTitle()
					.compareToIgnoreCase(base2.getTitle()));
		}
		return dealRanges;
	}


	private Integer getDaysRemain(final DealJson deal)
	{
		final Date d = new Date();
		final Long ms = d.getTime();
		final Long toDate = deal.getValidTo();
		final Long delta = Math.abs(toDate - ms) / 1000;
		final Double days = Math.floor(delta / 86400);
		return days.intValue() + 1;
	}

	/**
	 * @return the copyRightYear
	 */
	public int getCopyRightYear()
	{
		final LocalDate today = LocalDate.now();
		copyRightYear = today.getYear();
		return copyRightYear;
	}


	public String getAgreedInstoreDealUrl() {
		return agreedInstoreDealUrl;
	}

	public String getLastChanceDealUrl() {
		return lastChanceDealUrl;
	}

	public String getOnlineOnlyDealUrl() {
		return onlineOnlyDealUrl;
	}

	public String getLimitedOfferDealUrl() {
		return limitedOfferDealUrl;
	}

	public boolean isLastChance(final DealJson deal){
		if (getDaysRemain(deal) <= 1) {
			return true;
		}
		return false;
	}

}
