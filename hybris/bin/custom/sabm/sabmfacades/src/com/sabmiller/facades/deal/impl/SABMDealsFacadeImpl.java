package com.sabmiller.facades.deal.impl;

import de.hybris.platform.commercefacades.order.data.EntryOfferInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealConditionService;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.EntryOfferInfoModel;
import com.sabmiller.facades.complexdeals.data.ComplexDealData;
import com.sabmiller.facades.dataimport.response.DataImportResponse;
import com.sabmiller.facades.deal.SABMDealsFacade;
import com.sabmiller.facades.deal.data.DealData;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.LostdealJson;
import com.sabmiller.facades.populators.SABMDealTitlePopulator;

import jakarta.annotation.Resource;


/**
 * The Class SABMDealsFacadeImpl.
 */
public class SABMDealsFacadeImpl implements SABMDealsFacade
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMDealsFacadeImpl.class.getName());

	/** The deals service. */
	protected DealsService dealsService;

	/** The deal condition service. */
	protected DealConditionService dealConditionService;

	/** The user service. */
	protected UserService userService;

	/** The deal converter. */
	protected Converter<DealModel, DealData> dealConverter;

	/** The cart service. */
	private SABMCartService cartService;

	/** The entry offer info converter. */
	private Converter<EntryOfferInfoModel, EntryOfferInfoData> entryOfferInfoConverter;

	/** The order entry converter. */
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

	/** The complex deal reverse converter. */
	private Converter<ComplexDealData, DealModel> complexDealReverseConverter;

	private ModelService modelService;

	/**
	 * Gets the cart service.
	 *
	 * @return the cartService
	 */
	public SABMCartService getCartService()
	{
		return cartService;
	}

	/**
	 * Sets the cart service.
	 *
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final SABMCartService cartService)
	{
		this.cartService = cartService;
	}

	/** The sabm deal title populator. */
	private SABMDealTitlePopulator sabmDealTitlePopulator;

	/**
	 * Gets the sabm deal title populator.
	 *
	 * @return the sabmDealTitlePopulator
	 */
	public SABMDealTitlePopulator getSabmDealTitlePopulator()
	{
		return sabmDealTitlePopulator;
	}

	/**
	 * Sets the sabm deal title populator.
	 *
	 * @param sabmDealTitlePopulator
	 *           the sabmDealTitlePopulator to set
	 */
	public void setSabmDealTitlePopulator(final SABMDealTitlePopulator sabmDealTitlePopulator)
	{
		this.sabmDealTitlePopulator = sabmDealTitlePopulator;
	}

	/** The b2b unit service. */
	private SabmB2BUnitService b2bUnitService;

	/** The Constant DATE_SAFE_FORMAT. */
	protected static final String DATE_SAFE_FORMAT = "yyyy-MM-dd";

	/** The Constant ENCODING. */
	protected static final String ENCODING = "UTF-8";


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.deal.SABMDealsFacade#importComplexDeal(com.sabmiller.facades.complexdeals.data.
	 * ComplexDealData)
	 */
	@Override
	public DataImportResponse importComplexDeal(final ComplexDealData complexDeal, DealsService.ImportContext importContext)
	{
		final DealModel deal = getDealsService().getDeal(complexDeal.getCode());

		final boolean exist = deal != null;

		final DealModel populatedDeal = getComplexDealReverseConverter().convert(complexDeal,exist?deal:getModelService().create(DealModel.class));

		getDealsService().importComplexDeal(populatedDeal,importContext);

		final DataImportResponse dataImportResponse = new DataImportResponse();
		dataImportResponse.setExist(exist);
		return dataImportResponse;

	}

	@Override
	public DealsService.ImportContext createImportContext() {
		return getDealsService().createImportContext();
	}

	/**
	 * For next period date.
	 *
	 * @param date
	 *           the date
	 * @return the date
	 */
	// default, the next two (2) weeks, will be shown in the deals list.
	protected Date forNextPeriodDate(final Date date)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, Config.getInt("deal.valid.next.default.day", 14));
		return cal.getTime();
	}


	/**
	 * Convert deals.
	 *
	 * @param deals
	 *           the deals
	 * @return the list
	 */
	protected List<DealData> convertDeals(final List<DealModel> deals)
	{
		final List<DealData> dealDatas = Lists.newArrayListWithCapacity(deals.size());
		if (CollectionUtils.isNotEmpty(deals))
		{
			for (final DealModel deal : deals)
			{
				dealDatas.add(dealConverter.convert(deal));
			}
		}
		return dealDatas;
	}

	/**
	 * Un-encode values - all values must be URL safe as it goes in the URL query string.
	 *
	 * @param list
	 *           list of values to convert
	 * @return converted values
	 */
	protected List<String> unencodeValues(final List<String> list)
	{
		final List<String> unencodedList = Lists.newArrayListWithCapacity(list.size());

		for (final String str : list)
		{
			try
			{
				unencodedList.add(URLDecoder.decode(str, ENCODING));
			}
			catch (final UnsupportedEncodingException e)
			{
				LOG.error("unable to decode value [{}]", str, e);
			}
		}

		return unencodedList;
	}

	/**
	 * Parse a date from a string.
	 *
	 * @param date
	 *           date as string in the following format - @see DATE_SAFE_FORMAT
	 * @return a java date
	 */
	protected Date convertDate(final String date)
	{
		if (StringUtils.isNotBlank(date))
		{
			final SimpleDateFormat format = new SimpleDateFormat(DATE_SAFE_FORMAT);
			try
			{
				return format.parse(date);
			}
			catch (final ParseException e)
			{
				LOG.error("Error converting date [{}]", date);
			}
		}
		return null;
	}

	/**
	 * Sets the deals service.
	 *
	 * @param dealsService
	 *           the new deals service
	 */
	public void setDealsService(final DealsService dealsService)
	{
		this.dealsService = dealsService;
	}

	/**
	 * Sets the deal condition service.
	 *
	 * @param dealConditionService
	 *           the new deal condition service
	 */
	public void setDealConditionService(final DealConditionService dealConditionService)
	{
		this.dealConditionService = dealConditionService;
	}

	/**
	 * Sets the user service.
	 *
	 * @param userService
	 *           the new user service
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Sets the deal converter.
	 *
	 * @param dealConverter
	 *           the deal converter
	 */
	public void setDealConverter(final Converter<DealModel, DealData> dealConverter)
	{
		this.dealConverter = dealConverter;
	}

	/**
	 * Gets the b2b unit service.
	 *
	 * @return the b2b unit service
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * Sets the b2b unit service.
	 *
	 * @param b2bUnitService
	 *           the new b2b unit service
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	/**
	 * Gets the deals service.
	 *
	 * @return the deals service
	 */
	public DealsService getDealsService()
	{
		return dealsService;
	}

	/**
	 * Gets the user service.
	 *
	 * @return the user service
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * Gets the deal converter.
	 *
	 * @return the deal converter
	 */
	public Converter<DealModel, DealData> getDealConverter()
	{
		return dealConverter;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.deal.SABMDealsFacade#isLostDeal(java.lang.String, int, java.lang.String)
	 */
	/*
	 * to check whether the cart will lost deal after reduce product of delete items.
	 *
	 * @see com.sabmiller.facades.deal.SABMDealsFacade#isLostDeal(java.lang.String, int)
	 */
	@Override
	public LostdealJson isLostDeal(final String entryNumber, final int quantity, final String uom)
	{
		final Map<String, List<ItemModel>> lostdeals = dealsService.getLostDeal(cartService.getSessionCart(), entryNumber, quantity,
				uom);
		final List<ItemModel> complexDeals = lostdeals.get("COMPLEX");
		final LostdealJson lostDealJson = new LostdealJson();
		final List<String> titles = new ArrayList<>();
		final List<String> code = new ArrayList<>();
		final List<DealModel> complexDealModelList = new ArrayList<>();
		boolean isDiscountDealBenefitExists = false;
		if (!complexDeals.isEmpty())
		{
			for (final ItemModel deal : complexDeals)
			{
				final DealJson dealJson = new DealJson();
				final DealModel complexDeal = (DealModel) deal;
				final List<DealModel> dealList = new ArrayList<DealModel>();
				dealList.add(complexDeal);
				sabmDealTitlePopulator.populate(dealList, dealJson);
				titles.add(dealJson.getTitle());
				complexDealModelList.addAll(dealList);
			}
			isDiscountDealBenefitExists = dealsService.isDiscountDealExists(complexDealModelList);
		}

		final List<ItemModel> discountDeals = lostdeals.get("DISCOUNT");
		if (!discountDeals.isEmpty())
		{
			for (final ItemModel object : discountDeals)
			{
				final EntryOfferInfoModel discountDeal = (EntryOfferInfoModel) object;
				final EntryOfferInfoData offerInfo = new EntryOfferInfoData();
				final OrderEntryData entry = new OrderEntryData();
				final AbstractOrderEntryModel currentEntry = cartService.getEntryForNumber(cartService.getSessionCart(),
						Integer.valueOf(entryNumber));
				orderEntryConverter.convert(currentEntry, entry);
				entryOfferInfoConverter.convert(discountDeal, offerInfo);

				// Don't show SAP simulate discount deal with min 1 if hybris complex discount deal benefit exists
				if (isDiscountDealBenefitExists && SabmCoreConstants.OFFER_TYPE_DISCOUNT.equals(offerInfo.getOfferType())
						&& offerInfo.getScaleQuantity() == 1)
				{
					continue;
				}
				final String title = cartService.returnOfferTitle(offerInfo, entry);
				titles.add(title);

			}
		}

		final List<ItemModel> limitedDeals = lostdeals.get("LIMITED");
		if (!limitedDeals.isEmpty())
		{
			for (final ItemModel deal : limitedDeals)
			{
				final DealJson dealJson = new DealJson();
				final DealModel limitedDeal = (DealModel) deal;
				final List<DealModel> dealList = new ArrayList<DealModel>();
				dealList.add(limitedDeal);
				sabmDealTitlePopulator.populate(dealList, dealJson);
				titles.add(dealJson.getTitle());

			}
		}

		final List<ItemModel> deletedDeals = lostdeals.get("DELETED");
		if (!deletedDeals.isEmpty())
		{
			for (final ItemModel deal : deletedDeals)
			{
				final DealModel deletedDeal = (DealModel) deal;
				code.add(deletedDeal.getCode());
				final DealJson dealJson = new DealJson();
				final List<DealModel> dealList = new ArrayList<DealModel>();
				dealList.add(deletedDeal);
				sabmDealTitlePopulator.populate(dealList, dealJson);
				titles.add(dealJson.getTitle());				
			}
		}

		lostDealJson.setCode(code);
		lostDealJson.setIsLost(!titles.isEmpty());
		lostDealJson.setTitle(new HashSet<>(titles));
		return lostDealJson;
	}

	/**
	 * Gets the entry offer info converter.
	 *
	 * @return the entryOfferInfoConverter
	 */
	public Converter<EntryOfferInfoModel, EntryOfferInfoData> getEntryOfferInfoConverter()
	{
		return entryOfferInfoConverter;
	}

	/**
	 * Sets the entry offer info converter.
	 *
	 * @param entryOfferInfoConverter
	 *           the entryOfferInfoConverter to set
	 */
	public void setEntryOfferInfoConverter(final Converter<EntryOfferInfoModel, EntryOfferInfoData> entryOfferInfoConverter)
	{
		this.entryOfferInfoConverter = entryOfferInfoConverter;
	}

	/**
	 * Gets the order entry converter.
	 *
	 * @return the orderEntryConverter
	 */
	public Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return orderEntryConverter;
	}

	/**
	 * Sets the order entry converter.
	 *
	 * @param orderEntryConverter
	 *           the orderEntryConverter to set
	 */
	public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter)
	{
		this.orderEntryConverter = orderEntryConverter;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.deal.SABMDealsFacade#deleteCartDeal(java.util.List)
	 */
	@Override
	public boolean deleteCartDeal(final List<String> dealCode)
	{

		return cartService.deleteCartDeal(dealCode);
	}


	protected Converter<ComplexDealData, DealModel> getComplexDealReverseConverter() {
		return complexDealReverseConverter;
	}

	public void setComplexDealReverseConverter(Converter<ComplexDealData, DealModel> complexDealReverseConverter) {
		this.complexDealReverseConverter = complexDealReverseConverter;
	}

	protected ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}
}
