
/**
 * The SABMCartPopulator to populate the CartData attributes from CartModel
 *
 * @author yuxiao.wang
 * @date 2015-11-24
 */

package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commercefacades.order.converters.populator.CartPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.EntryOfferInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiSiteUtil;
import com.google.common.collect.Lists;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealConditionService;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.data.DealFreeProductJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deals.DealTitleData;
import com.sabmiller.integration.sap.enums.PricingCalculationType;
import java.math.BigDecimal;
import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * The Class SABMCartPopulator.
 *
 * @param <T>
 *           the generic type
 */
public class SABMCartPopulator<T extends CartData> extends CartPopulator<T>
{

	@Resource(name = "dealTitlePopulator")
	private Populator<List<DealModel>, DealJson> dealTitlePopulator;

	/** The cart service. */
	@Resource(name = "cartService")
	private SABMCartService cartService;

	@Resource(name = "dealsService")
	private DealsService dealsService;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;


	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;

	@Resource(name = "cartService")
	private SABMCartService sabmCartService;

	@Resource(name = "dealConditionService")
	private DealConditionService dealConditionService;

	@Resource(name = "sabmCartFacade")
	private SABMCartFacade cartFacade;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMCartPopulator.class);

	/**
	 * Populate the common
	 *
	 * @param prototype
	 *           the order data
	 * @param source
	 *           the order model
	 */
	@Override
	protected void addCommon(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		super.addCommon(source, prototype);
		if(!asahiSiteUtil.isCub())
		{
   		prototype.setPriceUpdated(source.getPriceUpdated());
   		if (source.getUnit() instanceof AsahiB2BUnitModel)
   		{
   			final AsahiB2BUnitModel asahiB2BUnit = (AsahiB2BUnitModel) source.getUnit();
   
   			if (asahiB2BUnit.getWarehouse() != null)
   			{
   				prototype.setWarehouse(asahiB2BUnit.getWarehouse().getCode());
   			}
   		}
		}
	}


	@Override
	public void populate(final CartModel source, final T target)
	{
		super.populate(source, target);
		if(asahiSiteUtil.isCub())
		{
   		addDeliveryAddress(source, target);
   		addDeliveryMethod(source, target);
   		target.setTotalDiscounts(createPrice(source, Math.abs(source.getTotalDiscounts())));
   		addDealCondition(source, target);
   		try
   		{
   			final Map<Integer, List<DealModel>> entryDeals = cartService.getEntryApplyDeal(source, false);
   			if (entryDeals != null)
   			{
   				addEntryDeals(entryDeals, target);
   			}
   		}
   		catch (final ConversionException e)
   		{
   			LOG.error("Exception occurred while fetching the deals for cart");
   		}
		}
	}

	/**
	 * Populate the common
	 *
	 * @param target
	 *           the CartData
	 * @param source
	 *           the CartModel
	 */
	protected void addDealCondition(final CartModel source, final CartData target)
	{
		final List<DealJson> autoAppliedDeals = new ArrayList<DealJson>();
		for (final CartDealConditionModel cartDealCondition : source.getComplexDealConditions())
		{
			if (cartDealCondition != null && DealConditionStatus.AUTOMATIC.equals(cartDealCondition.getStatus()))
			{
				final DealJson dealJson = new DealJson();
				dealTitlePopulator.populate(Lists.newArrayList(cartDealCondition.getDeal()), dealJson);

				autoAppliedDeals.add(dealJson);
			}
		}
		if (CollectionUtils.isNotEmpty(autoAppliedDeals))
		{
			target.setAutoAppliedDeals(autoAppliedDeals);
		}
	}

	/**
	 * Populate deal title for the applied deal of the cart entry
	 *
	 * @param target
	 *           the CartData
	 * @param entryDeals
	 *           the map of entry number and the applied deals
	 */
	protected void addEntryDeals(final Map<Integer, List<DealModel>> entryDeals, final CartData target)
	{

		final List<OrderEntryData> newEntries = new ArrayList<>();
		int dealSeqNo = 1;

		final Map<String, Integer> cartDeals = new LinkedHashMap<String, Integer>();

		for (final OrderEntryData entry : CollectionUtils.emptyIfNull(target.getEntries()))
		{
			final List<DealTitleData> dealTitleslist = new ArrayList<DealTitleData>();


			boolean isDiscountDealBenefitExists = false;
			if (entryDeals.containsKey(entry.getEntryNumber()) && entryDeals.get(entry.getEntryNumber()) != null)
			{
				final DealJson dealJson = new DealJson();
				final List<DealModel> dealsList = entryDeals.get(entry.getEntryNumber());
				isDiscountDealBenefitExists = dealsService.isDiscountDealExists(dealsList);
				dealTitlePopulator.populate(dealsList, dealJson);


				final DealTitleData dealTitleData = new DealTitleData();

				final String dealTitle = formatCartTitle(dealJson.getTitle());
				if (!cartDeals.containsKey(dealTitle))
				{
					dealTitleData.setDealSeqNo(String.valueOf(dealSeqNo));
					dealTitleData.setDealTitle(dealTitle);
					cartDeals.put(dealTitle, dealSeqNo);
					dealSeqNo = dealSeqNo + 1;
				}
				else
				{
					dealTitleData.setDealSeqNo(cartDeals.get(dealTitle).toString());
					dealTitleData.setDealTitle(dealTitle);
				}
				dealTitleslist.add(dealTitleData);

				LOG.debug("setChooseFreeGoodFlag CartData info: CartData:{},EntryNumber: {},FreeGoodEntryNumber: {} ",
						target.getCode(), entry.getEntryNumber(), entry.getFreeGoodEntryNumber());

				setChooseFreeGoodFlag(target.getEntries());
			}
			//get offer List deal info
			final List<EntryOfferInfoData> offerList = entry.getOfferData();
			for (final EntryOfferInfoData entryOfferInfoData : offerList)
			{
				String dealtitle = "";

				// Don't show SAP simulate discount deal with min 1 if hybris complex discount deal benefit exists
				if (isDiscountDealBenefitExists && SabmCoreConstants.OFFER_TYPE_DISCOUNT.equals(entryOfferInfoData.getOfferType())
						&& (entryOfferInfoData.getScaleQuantity() == 1
								|| (entryOfferInfoData.getScaleAmountType().equals(PricingCalculationType.PERCENTAGE.getType())
										&& entryOfferInfoData.getScaleQuantity() == 0)))
				{
					continue;
				}
				if (SabmCoreConstants.OFFER_TYPE_LIMITED.equals(entryOfferInfoData.getOfferType()))
				{
					final List<DealModel> deals = dealsService.getDeals(b2bCommerceUnitService.getParentUnit(), new Date(),
							forNextPeriodDate(new Date()));
					final DealModel limitedDeal = dealsService.findLimitedDealWithOfferInfo(entryOfferInfoData.getOfferType(), deals,
							entry.getProduct().getCode());

					if (limitedDeal != null)
					{
						final DealJson dealJson = new DealJson();
						dealTitlePopulator.populate(Lists.newArrayList(limitedDeal), dealJson);
						dealtitle = formatCartTitle(dealJson.getTitle());
					}
				}
				else
				{
					dealtitle = formatCartTitle(cartService.returnOfferTitle(entryOfferInfoData, entry));
				}
				if (StringUtils.isNotBlank(dealtitle))
				{
					final DealTitleData dealTitleData = new DealTitleData();

					if (!cartDeals.containsKey(dealtitle))
					{
						dealTitleData.setDealSeqNo(String.valueOf(dealSeqNo));
						dealTitleData.setDealTitle(dealtitle);
						cartDeals.put(dealtitle, dealSeqNo);
						dealSeqNo = dealSeqNo + 1;
						dealTitleslist.add(dealTitleData);
					}
					/*
					 * else { dealTitleData.setDealSeqNo(cartDeals.get(dealtitle).toString());
					 * dealTitleData.setDealTitle(dealtitle);
					 * 
					 * } dealTitleslist.add(dealTitleData);
					 */
				}
			}

			entry.setDealTitle(dealTitleslist);
			newEntries.add(entry);
		}
		target.setDealsTitleMap(cartDeals);
		target.setEntries(newEntries);
		if (cartDeals != null && cartDeals.size() > 0)
		{
			List<DealJson> autoAppliedDealsToCart = new ArrayList<DealJson>();
			final List<DealJson> autoAppliedForComplexDeals = target.getAutoAppliedDeals();
			final List<DealJson> autoAppliedNonComplexDeals = applyAutoDiscountDeal(cartDeals);
			autoAppliedDealsToCart = setAutoAppliedDealToCart(cartDeals, ListUtils.emptyIfNull(autoAppliedForComplexDeals),
					ListUtils.emptyIfNull(autoAppliedNonComplexDeals));

			target.setAutoAppliedAllDealsToCart(autoAppliedDealsToCart);
		}
	}


	/**
	 * @param date
	 * @return
	 */
	private Date forNextPeriodDate(final Date date)
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
	 * @param dealJson
	 * @return
	 */
	private String formatCartTitle(final String title)
	{
		final String formattedTitle = StringUtils.replaceEach(title, new String[]
		{ "<b>", "</b>" }, new String[]
		{ "", "" });
		return formattedTitle;
	}


	/**
	 * set can choose other free goods flag
	 *
	 * @param newEntries
	 *           the list of OrderEntryData
	 */
	protected void setChooseFreeGoodFlag(final List<OrderEntryData> newEntries)
	{
		if (CollectionUtils.isEmpty(newEntries))
		{
			return;
		}
		final List<DealJson> deals = sabmDealsSearchFacade.searchDeals(true);

		if (CollectionUtils.isEmpty(deals))
		{
			return;
		}

		for (final OrderEntryData entry : newEntries)
		{
			LOG.debug("setChooseFreeGoodFlag freeGoodEntryNumber info: entry.getEntryNumber {}, IsFreeGood:{}",
					entry.getEntryNumber(), entry.isIsFreeGood());

			if (entry.isIsFreeGood() && StringUtils.isNotEmpty(entry.getFreeGoodsForDeal()))
			{
				LOG.debug("setChooseFreeGoodFlag freeGoodEntryNumber info: freeGoodEntryNumber {}", entry.getEntryNumber());

				for (final DealJson deal : deals)
				{
					if (CollectionUtils.isNotEmpty(deal.getSelectableProducts()))
					{
						for (final DealFreeProductJson product : deal.getSelectableProducts())
						{
							if (entry.getFreeGoodsForDeal().equals(product.getCode()))
							{
								entry.setChooseFrees(Boolean.TRUE);
								return;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Apply Auto apply discount deal
	 *
	 * @param CartDeals
	 *           the CartDeals
	 */

	protected List<DealJson> applyAutoDiscountDeal(final Map<String, Integer> cartDeals)
	{
		final List<DealJson> autoAppliedNonComplexDeals = new ArrayList<DealJson>();
		for (final Entry<String, Integer> cartDeal : cartDeals.entrySet())
		{
			final String cartDealTitle = cartDeal.getKey();

			final B2BUnitModel unitModel = b2bCommerceUnitService.getParentUnit();
			final CartModel cartModel = sabmCartService.getSessionCart();
			final List<DealModel> fullyQualifiedDiscountDeals = dealConditionService
					.findFullyQualifiedDeals(dealsService.getValidatedNonComplexDeals(unitModel), cartModel);

			for (final DealModel fullyQualifiedDiscountDeal : CollectionUtils.emptyIfNull(fullyQualifiedDiscountDeals))
			{
				if (!cartFacade.cartContainsDCN(cartModel, fullyQualifiedDiscountDeal, true))
				{
					final DealJson discountDealJson = new DealJson();
					dealTitlePopulator.populate(Lists.newArrayList(fullyQualifiedDiscountDeal), discountDealJson);
					final String discountDealtitle = formatCartTitle(discountDealJson.getTitle());
					if ((discountDealtitle.trim()).equalsIgnoreCase(cartDealTitle.trim())
							&& !fullyQualifiedDiscountDeal.getDealType().equals(DealTypeEnum.COMPLEX))
					{
						autoAppliedNonComplexDeals.add(discountDealJson);

					}
				}

			}
		}
		return autoAppliedNonComplexDeals;
	}

	/**
	 * Set Auto apply All deals to cart
	 *
	 * @param CartDeals
	 *           the CartDeals
	 * @param complexDeals
	 *           the complexDeals
	 * @param nonComplexDeals
	 *           the nonComplexDeals
	 */

	protected List<DealJson> setAutoAppliedDealToCart(final Map<String, Integer> cartDeals, final List<DealJson> complexDeals,
			final List<DealJson> nonComplexDeals)
	{
		final List<DealJson> autoAppliedDeals = new ArrayList<DealJson>();
		for (final Entry<String, Integer> cartDeal : cartDeals.entrySet())
		{
			final String cartDealTitle = cartDeal.getKey();

			for (final DealJson complexDealJson : ListUtils.emptyIfNull(complexDeals))
			{

				final String comlexDealtitle = formatCartTitle(complexDealJson.getTitle());
				if ((comlexDealtitle.trim()).equalsIgnoreCase(cartDealTitle.trim()))
				{
					autoAppliedDeals.add(complexDealJson);
				}
			}
			for (final DealJson nonComplexDealJson : ListUtils.emptyIfNull(nonComplexDeals))
			{

				final String nonComlexDealtitle = formatCartTitle(nonComplexDealJson.getTitle());
				if ((nonComlexDealtitle.trim()).equalsIgnoreCase(cartDealTitle.trim()))
				{
					autoAppliedDeals.add(nonComplexDealJson);
				}
			}

		}
		return autoAppliedDeals;

	}
	
	@Override
	protected void addTotals(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		if(!asahiSiteUtil.isCub())
		{
   		prototype.setTotalPrice(createPrice(source, source.getTotalPrice()));
   		prototype.setPortalGST(createPrice(source, calcTotalTax(source)));
   		
   		if (asahiSiteUtil.isApb())
   		{
   			prototype.setTotalTax(createPrice(source, calcTotalTax(source)));
   		}
   		
   		if (asahiSiteUtil.isSga()) {
   			prototype.setOrderCDL(
   					createPrice(source, Double.valueOf(null != source.getOrderCDL() ? source.getOrderCDL().doubleValue() : 0.0D)));
   			
   			prototype.setSubTotal(createPrice(source, Double.valueOf(source.getSubtotal().doubleValue())));
   			
   			prototype.setMinicartSubTotal(createPrice(source, Double.valueOf(null != source.getMinicartSubTotal() ? source.getMinicartSubTotal().doubleValue() : 0.0D)));
   			
   		}else{
   			prototype.setSubTotal(createPrice(source, Double.valueOf(source.getSubtotal().doubleValue())));
   		}
   
   		prototype.setPortalWET(createPrice(source, source.getOrderWET()));
   		prototype.setPortalFreight(createPrice(source, source.getFreight()));
   		prototype.setDeliveryCost(createPrice(source, source.getDeliveryCost()));
   		prototype.setTotalPriceWithTax((createPrice(source, calcTotalWithTax(source))));
   		prototype.setPortalCreditSurcharge(createPrice(source, source.getCreditSurCharge()));
		}
		else
		{
			super.addTotals(source, prototype);
		}
	}
	
	@Override
	protected Double calcTotalWithTax(final AbstractOrderModel source)
	{
		if(!asahiSiteUtil.isCub())
		{
   		if (source == null)
   		{
   			throw new IllegalArgumentException("source order must not be null");
   		}
   		if (source.getTotalPrice() == null)
   		{
   			return 0.0d;
   		}
   
   		BigDecimal totalPrice = new BigDecimal(0);
   		if (null != source.getTotalPrice())
   		{
   			totalPrice = BigDecimal.valueOf(source.getTotalPrice().doubleValue());
   		}
   
   		// Add the taxes to the total price if the cart is net; if the total was null taxes should be null as well
   		if (Boolean.TRUE.equals(source.getNet()) && totalPrice.compareTo(BigDecimal.ZERO) != 0 && source.getOrderGST() != null)
   		{
   			totalPrice = totalPrice.add(BigDecimal.valueOf(source.getOrderGST().doubleValue()));
   
   			/*if(asahiSiteUtil.isSga()) {
   				totalPrice = totalPrice.add(BigDecimal.valueOf(source.getOrderCDL() != null ? source.getOrderCDL().doubleValue() :0.0D));
   			}*/
   		}
   
   		return totalPrice.doubleValue();
		}
		else
		{
			return super.calcTotalWithTax(source);
		}
	}
	
	protected Double calcTotalTax(final AbstractOrderModel source)
	{
		if (source.getTotalPrice() == null)
		{
			throw new IllegalArgumentException("source order must not be null");
		}
		if (source.getTotalTax() == null)
		{
			return 0.0d;
		}

		BigDecimal totalTax = new BigDecimal(0);
		if (null != source.getOrderGST())
		{
			totalTax = BigDecimal.valueOf(source.getOrderGST().doubleValue());
		}

		return totalTax.doubleValue();
	}

	@Override
	protected void addPromotions(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		if(!asahiSiteUtil.isCub())
		{
			addPromotions(source, null, prototype);
		}
		else
		{
			super.addPromotions(source, prototype);
		}
	}

	@Override
	protected void addPromotions(final AbstractOrderModel source, final PromotionOrderResults promoOrderResults,
			final AbstractOrderData prototype)
	{
		if(!asahiSiteUtil.isCub())
		{
			prototype.setTotalDiscounts(createPrice(source, source.getTotalDiscounts()));
		}
		else
		{
			super.addPromotions(source, promoOrderResults, prototype);
		}
	}
	

}
