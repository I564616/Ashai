/**
 *
 */
package com.sabmiller.core.cart.service.impl;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BCartService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.EntryOfferInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService.SessionAttributeLoader;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantProductModel;

import java.io.Serial;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.google.common.collect.Lists;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.b2b.services.SabmB2BEmployeeService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.cart.dao.SabmCommerceCartDao;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealConditionService;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.deals.services.response.ConflictGroup;
import com.sabmiller.core.deals.services.response.ConflictGroup.Conflict;
import com.sabmiller.core.deals.services.response.DealQualificationResponse;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.enums.MaxOrderQtyRuleType;
import com.sabmiller.core.enums.OrderSimulationStatus;
import com.sabmiller.core.enums.PackType;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.MaxOrderQtyModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.model.SabmCartRuleModel;
import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.strategy.SABMDeliveryShippingCarrierStrategy;
import com.sabmiller.facades.product.data.UomData;


/**
 * The Class DefaultSABMCartService.
 */
public class DefaultSABMCartService extends DefaultB2BCartService implements SABMCartService
{
	/**
	 * the serialVersionUID
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMCartService.class);

	private static final String DEFAULT_MAX_ORDER_QTY_RULE_DAYS = "7";

	private static final String ORDERED_QTY = "orderedQty";

	/** The sabm delivery shipping carrier strategy. */
	private SABMDeliveryShippingCarrierStrategy sabmDeliveryShippingCarrierStrategy;

	/** The user service. */
	@Resource(name = "userService")
	private UserService userService;

	/** The commerce cart dao. */
	@Resource
	private SabmCommerceCartDao commerceCartDao;

	/** The base site service. */
	@Resource
	private BaseSiteService baseSiteService;

	/** The cart factory. */
	@Resource
	private CartFactory cartFactory;

	/** The b2b unit service. */
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	/** The product service. */
	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "dealsService")
	private DealsService dealsService;

	private MessageSource messageSource;

	@Resource
	private I18NService i18nService;

	@Resource(name = "dealConditionService")
	private DealConditionService dealConditionService;

	/** The message source name. */
	@Value(value = "${message.source.name:messageSource}")
	private String messageSourceName;

	@Resource(name = "deliveryService")
	private DeliveryService deliveryService;

	@Resource(name = "sabmDeliveryDateCutOffService")
	private SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService;


	@Resource(name = "sabmB2BEmployeeService")
	private SabmB2BEmployeeService sabmB2BEmployeeService;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;


	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	@Resource
	private CMSSiteService cmsSiteService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	/**
	 * Gets the message source.
	 *
	 * @return the message source
	 */
	public MessageSource getMessageSource()
	{
		if (messageSource == null)
		{
			messageSource = Registry.getApplicationContext().getBean(messageSourceName, MessageSource.class);
		}

		return messageSource;
	}

	/*
	 * Method for update DeliveryInstructions of cart
	 *
	 * @see com.sabmiller.core.cart.services.SABMCartService#saveDeliveryInstructions(java.lang.String,
	 * de.hybris.platform.core.model.order.AbstractOrderModel)
	 */
	@Override
	public void saveDeliveryInstructions(final String deliveryInstructions, final AbstractOrderModel abstractOrder)
	{
		// save deliveryInstructions to AbstractOrderModel
		abstractOrder.setDeliveryInstructions(deliveryInstructions);
		getModelService().save(abstractOrder);
	}

	/*
	 * Method for update ShippingCarrier of the cart
	 *
	 * @see
	 * com.sabmiller.core.cart.services.SABMCartService#setShippingCarrier(de.hybris.platform.commerceservices.service.
	 * data.CommerceCheckoutParameter)
	 */
	@Override
	public boolean setShippingCarrier(final CommerceCheckoutParameter parameter)
	{
		return getSabmDeliveryShippingCarrierStrategy().setShippingCarrier(parameter);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.cart.service.SABMCartService#savePurchaseOrderNumber(java.lang.String,
	 * de.hybris.platform.core.model.order.AbstractOrderModel)
	 */
	@Override
	public void savePurchaseOrderNumber(final String poNumber, final AbstractOrderModel order)
	{
		LOG.info("poNumber is ", poNumber);
		//		SABMC- 1723
		if (StringUtils.isBlank(poNumber)
				&& null != getSessionService().getAttribute(SabmCoreConstants.SESSION_ATTR_IMPERSONATE_PA))
		{
			final UserModel pAUser = getSessionService().getAttribute(SabmCoreConstants.SESSION_ATTR_IMPERSONATE_PA);
			order.setPurchaseOrderNumber(pAUser.getUid());
			getModelService().save(order);
		}
		else if (StringUtils.isBlank(poNumber) && order.getUser() instanceof BDECustomerModel)
		{
			final EmployeeModel bde = sabmB2BEmployeeService.searchBDEByName(userService.getCurrentUser().getName());
			order.setPurchaseOrderNumber(bde.getUid());
			getModelService().save(order);

		}
		else if (StringUtils.isNotBlank(poNumber))
		{
			order.setPurchaseOrderNumber(StringUtils.trim(poNumber));
			getModelService().save(order);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.cart.service.SABMCartService#saveRequestedDeliveryDate(java.util.Date)
	 */
	@Override
	public boolean saveRequestedDeliveryDate(final Date date, final String packType)
	{
		boolean succes = true;
		final CartModel cart = getSessionCart();

		cart.setRequestedDeliveryDate(date);

		updateSimulationStatusToRecalculate(cart);

		try
		{
			getModelService().save(cart);
		}
		catch (final ModelSavingException e)
		{
			LOG.error("Error persisting Request Delivery Date: " + date + " to cart: " + cart, e);
			succes = false;
		}

		getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE, date);
		getSessionService().setAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE_PACKTYPE,
				StringUtils.isNotBlank(packType) ? packType
						: sabmDeliveryDateCutOffService.getDeliveryDatePackType(b2bCommerceUnitService.getParentUnit(), date)
								.get(PackType._TYPECODE));

		return succes;
	}

	@Override
	public void markCartForRecalculation()
	{
		final CartModel cartModel = getSessionCart();
		updateSimulationStatusToRecalculate(cartModel);
		getModelService().save(cartModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.order.impl.DefaultCartService#internalGetSessionCart()
	 */
	@Override
	protected CartModel internalGetSessionCart()
	{
		if (!asahiSiteUtil.isCub())
		{
			return super.internalGetSessionCart();
		}
		final CartModel cart = getSessionService().getOrLoadAttribute(SESSION_CART_PARAMETER_NAME,
				new SessionAttributeLoader<CartModel>()
				{
					@Override
					public CartModel load()
					{
						final UserModel currentUser = userService.getCurrentUser();
						CartModel cart = null;
						final B2BUnitModel selectedB2BUnit = b2bUnitService.getParent((B2BCustomerModel) currentUser);

						LOG.debug("Searching cart for user: {}, in site: {}", currentUser, baseSiteService.getCurrentBaseSite());

						//If the user is not B2BCustomer skip the search of the old cart
						if (currentUser instanceof B2BCustomerModel)
						{
							//Get the latest cart of the user by modified time
							cart = commerceCartDao.getCartForSiteAndUserAndB2BUnit(baseSiteService.getCurrentBaseSite(), currentUser,
									selectedB2BUnit);
						}

						//If there are no old cart related to the user or the user is not b2bCustomer, a new one is created
						if (cart != null)
						{
							return cart;
						}
						else
						{
							cart = cartFactory.createCart();
							if (selectedB2BUnit.getDefaultCarrier() != null)
							{
								final ShippingCarrierModel carrier = selectedB2BUnit.getDefaultCarrier();

								DeliveryModeModel deliveryMode = deliveryService
										.getDeliveryModeForCode(Config.getString(SabmCoreConstants.CART_DELIVERY_CUBARRANGED, ""));

								if (carrier != null && carrier.getCustomerOwned())
								{
									deliveryMode = deliveryService
											.getDeliveryModeForCode(Config.getString(SabmCoreConstants.CART_DELIVERY_CUSTOMERARRANGED, ""));
								}
								cart.setDeliveryMode(deliveryMode);
								cart.setDeliveryShippingCarrier(selectedB2BUnit.getDefaultCarrier());
								getModelService().save(cart);

							}
							return cart;
						}
					}
				});
		return cart;
	}

	private void updateSimulationStatusToRecalculate(final AbstractOrderModel cart)
	{
		if (cart.getOrderSimulationStatus() == null || cart.getOrderSimulationStatus().equals(OrderSimulationStatus.CALCULATED))
		{
			cart.setOrderSimulationStatus(OrderSimulationStatus.NEED_CALCULATION);
			cart.setCalculated(false);
		}
		else if (cart.getOrderSimulationStatus().equals(OrderSimulationStatus.CALCULATION_IN_PROGRESS))
		{
			cart.setOrderSimulationStatus(OrderSimulationStatus.UPDATE_DURING_CALCULATION);
			cart.setCalculated(false);
		}
	}

	/**
	 * Gets the sabm delivery shipping carrier strategy.
	 *
	 * @return the sabmDeliveryShippingCarrierStrategy
	 */
	public SABMDeliveryShippingCarrierStrategy getSabmDeliveryShippingCarrierStrategy()
	{
		return sabmDeliveryShippingCarrierStrategy;
	}

	/**
	 * Sets the sabm delivery shipping carrier strategy.
	 *
	 * @param sabmDeliveryShippingCarrierStrategy
	 *           the sabmDeliveryShippingCarrierStrategy to set
	 */
	public void setSabmDeliveryShippingCarrierStrategy(
			final SABMDeliveryShippingCarrierStrategy sabmDeliveryShippingCarrierStrategy)
	{
		this.sabmDeliveryShippingCarrierStrategy = sabmDeliveryShippingCarrierStrategy;
	}

	/**
	 * Get the applied deal for cart entry SABMC-852
	 *
	 * @param cartModel
	 *           the cart model
	 * @param triggerReject
	 *           if true,only the rejected deal will be apply to the result; if false,the rejected deal will not be apply
	 *           to the result
	 * @return the Map of the result key: entry number. value:applied deals
	 */
	@Override
	public Map<Integer, List<DealModel>> getEntryApplyDeal(final CartModel cartModel, final boolean triggerReject)
			throws ConversionException
	{
		final Map<ProductModel, List<DealModel>> productDealMap = new HashMap<>();
		List<DealModel> deals = null;
		//get all the available deal from the cart
		final Collection<DealModel> availableDeals = getAvailableDeals(cartModel, triggerReject);
		for (final DealModel deal : CollectionUtils.emptyIfNull(availableDeals))
		{
			generateBaseProductForDeal(deal, productDealMap);
		}

		final Map<Integer, List<DealModel>> entriesHaveDeal = new HashMap<Integer, List<DealModel>>();

		final List<AbstractOrderEntryModel> entries = cartModel.getEntries();

		//check every entry to find the deal which have been applied
		for (final AbstractOrderEntryModel entryModel : CollectionUtils.emptyIfNull(entries))
		{
			if (!entryModel.getIsFreeGood() //Just the base product could applied the deal
					&& (deals = productDealMap.get(entryModel.getProduct())) != null && !deals.isEmpty()) //productDealMap.containsKey(entryModel.getProduct()))
			{
				//deals = productDealMap.get(entryModel.getProduct());

				//if triggerTeject is true, there maybe have several rejected deal applied to one entry
				if (triggerReject)
				{
					entriesHaveDeal.put(entryModel.getEntryNumber(), deals);
				}
				else
				{
					if (deals.size() > 1)
					{
						LOG.warn("There is more than one deal:{} for one product:{},entry:{}", deals.get(0), entryModel.getProduct(),
								entryModel.getPk());

					}
					for (final DealModel deal : deals)
					{
						generateMapOfEntryAndDeal(deal, entryModel, entriesHaveDeal);
					}
				}
			}
		}
		return entriesHaveDeal;
	}

	/**
	 * Generate the map of entry and deal SABMC-852
	 *
	 * @param dealModel
	 *           the DealModel
	 * @param entryModel
	 *           the entryModel
	 * @param entriesHaveDeal
	 *           the Map to save the entry number and the deals
	 */
	protected void generateMapOfEntryAndDeal(final DealModel dealModel, final AbstractOrderEntryModel entryModel,
			final Map<Integer, List<DealModel>> entriesHaveDeal)
	{
		entriesHaveDeal.put(entryModel.getEntryNumber(), Lists.newArrayList(dealModel));
	}

	/**
	 * Generate all the base product from the deal SABMC-852
	 *
	 * @param deal
	 *           the DealModel
	 */
	protected void generateBaseProductForDeal(final DealModel deal, final Map<ProductModel, List<DealModel>> productDealMap)
			throws ConversionException
	{
		final List<AbstractDealConditionModel> dealConditions = deal.getConditionGroup().getDealConditions();
		final List<ProductModel> excluded = findExcluded(dealConditions);

		for (final AbstractDealConditionModel condition : dealConditions)
		{
			if (condition instanceof ProductDealConditionModel && BooleanUtils.isNotTrue(condition.getExclude()))
			{
				final ProductModel product = productService
						.getProductForCode(((ProductDealConditionModel) condition).getProductCode());

				if (product == null)
				{
					throw new ConversionException("Null product in deal: " + deal.getCode());
				}
				generateProductDealMap(productDealMap, product, deal);
			}
			else if (condition instanceof ComplexDealConditionModel)
			{
				final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) condition;

				//Get the products in the ranges by the brand
				final List<? extends ProductModel> materials = productService.getProductByHierarchy(complexCondition.getLine(),
						complexCondition.getBrand(), complexCondition.getVariety(), complexCondition.getEmpties(),
						complexCondition.getEmptyType(), complexCondition.getPresentation());

				if (CollectionUtils.isEmpty(materials))
				{
					throw new ConversionException("There are no products with brand: " + complexCondition.getBrand());
				}

				//Add the range product to the map and remove the excluded product
				for (final ProductModel product : CollectionUtils.emptyIfNull(CollectionUtils.subtract(materials, excluded)))
				{
					generateProductDealMap(productDealMap, product, deal);
				}
			}
		}
	}

	/**
	 * Generate the map of product and deals SABMC-852
	 *
	 * @param productDealMap
	 *           the product Deal Map
	 * @param product
	 *           the product
	 * @param deal
	 *           the deal
	 */
	protected void generateProductDealMap(final Map<ProductModel, List<DealModel>> productDealMap, final ProductModel product,
			final DealModel deal)
	{
		if (productDealMap.containsKey(product))
		{
			productDealMap.get(product).add(deal);
		}
		else
		{
			productDealMap.put(product, Lists.newArrayList(deal));
		}
	}

	/**
	 * Get the available deals from the cart SABMC-852
	 *
	 * @param cartModel
	 *           the session cart
	 * @param triggerReject
	 *           if true,only the rejected deal will be apply to the result; if false,the rejected deal will not be apply
	 *           to the result
	 * @return the Collection of deal
	 */
	protected Collection<DealModel> getAvailableDeals(final CartModel cartModel, final boolean triggerReject)
	{
		final List<CartDealConditionModel> complexDealConditions = cartModel.getComplexDealConditions();
		if (CollectionUtils.isEmpty(complexDealConditions))
		{
			return Collections.emptySet();
		}
		final Set<DealModel> allDeals = new HashSet<DealModel>();
		final Set<DealModel> rejectDeals = new HashSet<DealModel>();


		for (final CartDealConditionModel condition : CollectionUtils.emptyIfNull(complexDealConditions))
		{
			if (condition.getDeal() != null && DealTypeEnum.COMPLEX.equals(condition.getDeal().getDealType()))
			{
				if (DealConditionStatus.REJECTED.equals(condition.getStatus()))
				{
					rejectDeals.add(condition.getDeal());
				}
				else
				{
					allDeals.add(condition.getDeal());
				}
			}
		}

		// if triggerReject is true, only the rejected deal will be as the result.
		if (triggerReject)
		{
			return CollectionUtils.isEmpty(rejectDeals) ? Collections.emptySet() : rejectDeals;
		}

		if (CollectionUtils.isEmpty(allDeals))
		{
			return Collections.emptySet();
		}
		//remove the rejected deal from the condition list, in case there is one deal have two different deal status.
		return CollectionUtils.subtract(allDeals, rejectDeals);
	}

	/**
	 * Find all the complex deal by the triggerHash SABMC-852
	 *
	 * @param triggerHash
	 *           the deal triggerHash
	 * @return the list of deal
	 */
	protected List<DealModel> getDealsByOneDeal(final String triggerHash)
	{
		final B2BUnitModel unitModel = b2bCommerceUnitService.getParentUnit();
		if (unitModel != null)
		{
			getSessionService().setAttribute(SabmCoreConstants.SESSION_SELECT_B2BUNIT_UID_DATA, unitModel.getUid());
		}

		final Collection<DealModel> complexDeals = dealsService
				.getDealsByRepDrivenStatus(dealsService.filterOnlineDeals(unitModel.getComplexDeals()));

		final List<DealModel> dealModels = new ArrayList<>();
		for (final DealModel dealModel : CollectionUtils.emptyIfNull(complexDeals))
		{
			if (triggerHash.equals(dealModel.getTriggerHash()) && dealsService.isOnlySingleBogofDeal(dealModel))
			{
				dealModels.add(dealModel);
			}
		}

		return CollectionUtils.isEmpty(dealModels) ? Collections.emptyList() : dealModels;

	}

	/**
	 * Find the excluded products. SABMC-852
	 *
	 * @param dealConditions
	 *           the deal conditions
	 * @return the list
	 */
	protected List<ProductModel> findExcluded(final List<AbstractDealConditionModel> dealConditions)
	{
		final List<ProductModel> excludedProducts = new ArrayList<>();

		for (final AbstractDealConditionModel condition : dealConditions)
		{
			if (condition instanceof ProductDealConditionModel && BooleanUtils.isTrue(condition.getExclude()))
			{
				excludedProducts.add(productService.getProductForCode(((ProductDealConditionModel) condition).getProductCode()));
			}
		}
		return excludedProducts;
	}

	public String returnOfferTitle(final EntryOfferInfoData offerInfo, final OrderEntryData entry)
	{
		if (SabmCoreConstants.OFFER_TYPE_DISCOUNT.equals(offerInfo.getOfferType()))
		{
			return generateDiscountOfferTitle(offerInfo, entry);
		}
		else if (SabmCoreConstants.OFFER_TYPE_FREEGOOD.equals(offerInfo.getOfferType()))
		{
			return generateBOGOGOfferTitle(offerInfo, entry);
		}
		else
		{
			return StringUtils.EMPTY;
		}

	}

	/**
	 * @param offerInfo
	 * @param entry
	 * @return Buy {minQty} {unit} of {productCode} to receive {quantity} {unit} free; Buy {minQty} {unit} of
	 *         {productCode} to receive {quantity} {unit} of {product} free;
	 */
	private String generateBOGOGOfferTitle(final EntryOfferInfoData offerInfo, final OrderEntryData entry)
	{
		final StringBuffer title = new StringBuffer("");
		final Long scaleQuantity = offerInfo.getScaleQuantity() == 0 ? 1 : offerInfo.getScaleQuantity();
		final String packConfiguration = entry.getProduct().getPackConfiguration() == null ? ""
				: entry.getProduct().getPackConfiguration();
		if (offerInfo.getFreeGoodProduct().getCode().equals(entry.getProduct().getCode()))
		{
			title.append(getMessageSource().getMessage("basket.page.entry.bogof.same.product.message", new Object[]
			{ scaleQuantity,
					scaleQuantity > 1 ? offerInfo.getScaleUnit().getPluralName().toLowerCase()
							: offerInfo.getScaleUnit().getName().toLowerCase(),
					entry.getProduct().getName(), packConfiguration,
					offerInfo.getFreeGoodQuantity(), offerInfo.getFreeGoodQuantity() > 1
							? entry.getBaseUnit().getPluralName().toLowerCase() : entry.getBaseUnit().getName().toLowerCase() },
					i18nService.getCurrentLocale()));
		}
		else
		{
			title.append(getMessageSource().getMessage("basket.page.entry.bogof.different.product.message", new Object[]
			{ scaleQuantity,
					scaleQuantity > 1 ? offerInfo.getScaleUnit().getPluralName().toLowerCase()
							: offerInfo.getScaleUnit().getName().toLowerCase(),
					entry.getProduct().getName(), packConfiguration, offerInfo.getFreeGoodQuantity(),
					offerInfo.getFreeGoodQuantity() > 1 ? entry.getBaseUnit().getPluralName().toLowerCase()
							: entry.getBaseUnit().getName().toLowerCase(),
					offerInfo.getFreeGoodProduct().getName() }, i18nService.getCurrentLocale()));
		}
		return title.toString();
	}

	/**
	 *
	 * @param offerInfo
	 * @param entry
	 * @return offer title like: Buy a minimum of {minQty} {unit} of {productCode} to receive {isCurrency} {amount} off
	 *         per {unit}; Buy a minimum of {minQty} {unit} of {productCode} to receive {amount} off; Buy a minimum of
	 *         {minQty} {unit} of {productCode} to receive {amount}% off
	 */
	private String generateDiscountOfferTitle(final EntryOfferInfoData offerInfo, final OrderEntryData entry)
	{
		final StringBuffer title = new StringBuffer("");
		final Long scaleQuantity = offerInfo.getScaleQuantity() == 0 ? 1 : offerInfo.getScaleQuantity();
		final String packConfiguration = entry.getProduct().getPackConfiguration() == null ? ""
				: entry.getProduct().getPackConfiguration();
		//Commented as per SAP price condition cahnge : to display one discount title for multiple discount condition types.
		//Buy a minimum of <minQty> <unit> of <productCode> to receive <isCurrency> <amount> off per <unit>
		/*if (SabmCoreConstants.SCALE_AMOUNT_TYPE_PERUNIT.equals(offerInfo.getScaleAmountType()) || SabmCoreConstants.SCALE_AMOUNT_TYPE_PERCENTAGE.equals(offerInfo.getScaleAmountType()))
		{

			final String discountValue = (entry.getUnitDiscountAmount() != null ? entry.getUnitDiscountAmount().getFormattedValue() : offerInfo.getScaleAmount());


			 * final String discountValue = (offerInfo.getScaleAmount() != null ? offerInfo.getScaleAmount() :
			 * entry.getUnitDiscountAmount().getFormattedValue());

			UomData scaleUnit = offerInfo.getScaleUnit();

			if (StringUtils.equalsIgnoreCase(offerInfo.getScaleUnit().getCode(), "L"))
			{
				scaleUnit = entry.getBaseUnit();
			}

			title.append(getMessageSource().getMessage("basket.page.entry.discount.message.on.each.unit", new Object[]
			{ scaleQuantity, scaleQuantity > 1 ? scaleUnit.getPluralName().toLowerCase() : scaleUnit.getName().toLowerCase(),
					entry.getProduct().getName(), packConfiguration, discountValue, entry.getBaseUnit().getName() },
					i18nService.getCurrentLocale()));
		}
		//Buy a minimum of <minQty> <unit> of <productCode> to receive <amount> off
		if (SabmCoreConstants.SCALE_AMOUNT_TYPE_FIXED.equals(offerInfo.getScaleAmountType()))
		{
			title.append(getMessageSource().getMessage("basket.page.entry.discount.message.on.whole.unit", new Object[]
			{ scaleQuantity,
					scaleQuantity > 1 ? offerInfo.getScaleUnit().getPluralName().toLowerCase()
							: offerInfo.getScaleUnit().getName().toLowerCase(),
					entry.getProduct().getName(), packConfiguration, offerInfo.getScaleAmount() }, i18nService.getCurrentLocale()));
		}
		//Buy a minimum of <minQty> <unit> of <productCode> to receive <amount>% off
		if (SabmCoreConstants.SCALE_AMOUNT_TYPE_PERCENTAGE.equals(offerInfo.getScaleAmountType()))
		{
			title.append(getMessageSource().getMessage("basket.page.entry.percentage.discount.message", new Object[]
			{ scaleQuantity,
					scaleQuantity > 1 ? offerInfo.getScaleUnit().getPluralName().toLowerCase()
							: offerInfo.getScaleUnit().getName().toLowerCase(),
					entry.getProduct().getName(), packConfiguration, offerInfo.getScaleAmount() }, i18nService.getCurrentLocale()));
		}*/
		final String discountValue = (entry.getUnitDiscountAmount() != null ? entry.getUnitDiscountAmount().getFormattedValue() : offerInfo.getScaleAmount());

		UomData scaleUnit = offerInfo.getScaleUnit();

		if (StringUtils.equalsIgnoreCase(offerInfo.getScaleUnit().getCode(), "L"))
		{
			scaleUnit = entry.getBaseUnit();
		}

		title.append(getMessageSource().getMessage("basket.page.entry.discount.message.on.each.unit", new Object[]
		{ scaleQuantity, scaleQuantity > 1 ? scaleUnit.getPluralName().toLowerCase() : scaleUnit.getName().toLowerCase(),
				entry.getProduct().getName(), packConfiguration, discountValue, entry.getBaseUnit().getName() },
				i18nService.getCurrentLocale()));
		return title.toString();
	}


	@Override
	public String validateProductsBeforeAddtoCart(final String baseProducts)

	{
		ProductModel product = productService.getProductForCode(baseProducts);
		SABMAlcoholVariantProductEANModel eanProduct = null;
		while (product instanceof VariantProductModel)
		{
			if (product.getClass().equals(SABMAlcoholVariantProductEANModel.class))
			{
				eanProduct = (SABMAlcoholVariantProductEANModel) product;
				break;
			}

			product = ((VariantProductModel) product).getBaseProduct();
		}
		if (eanProduct.getPurchasable())
		{
			return null;
		}
		return getProductTitle(eanProduct);
	}

	/**
	 * get product display title
	 *
	 * @param product
	 * @return product title
	 */
	private String getProductTitle(final SABMAlcoholVariantProductEANModel product)
	{
		if (StringUtils.isNotEmpty(product.getSellingName()) && StringUtils.isNotEmpty(product.getPackConfiguration()))
		{
			return product.getSellingName() + " " + product.getPackConfiguration();
		}
		return product.getName();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.cart.service.SABMCartService#deleteCartDeal(java.util.List)
	 */
	@Override
	public boolean deleteCartDeal(final List<String> dealCode)
	{
		try
		{
			final CartModel cart = getSessionCart();
			final List<CartDealConditionModel> dealConditions = cart.getComplexDealConditions();
			final List<CartDealConditionModel> removedConditions = new ArrayList<CartDealConditionModel>();
			for (final String deal : dealCode)
			{
				if (dealConditions != null)
				{
					for (final CartDealConditionModel cartDealCondition : dealConditions)
					{

						if (!DealConditionStatus.REJECTED.equals(cartDealCondition.getStatus())
								&& deal.equals(cartDealCondition.getDeal().getCode())
								&& DealTypeEnum.COMPLEX.equals(cartDealCondition.getDeal().getDealType()))
						{
							removedConditions.add(cartDealCondition);
						}
					}
				}

			}
			getModelService().removeAll(removedConditions);
		}
		catch (final Exception e)
		{
			LOG.error("Delete deal fail", e);
			return false;
		}
		return true;
	}

	public ConflictGroup getAllConflictingDealsInCart(final CartModel cart, final B2BUnitModel b2bUnit)
	{

		final DealQualificationResponse dealQualificationResponse = dealConditionService.findQualifiedDeals(b2bUnit, cart);
		final ConflictGroup conflictGroup = dealQualificationResponse.getConflictGroup();
		return conflictGroup;
	}

	public boolean isConflictingDeals(final List<DealModel> dealModels, final CartModel cart, final B2BUnitModel b2bUnit)
	{
		final ConflictGroup conflictGroup = getAllConflictingDealsInCart(cart, b2bUnit);
		final List<Conflict> Conflicts = conflictGroup.getConflicts();
		for (final Conflict conflict : Conflicts)
		{
			if (conflict.getDeals().contains(dealModels))
			{
				return true;
			}
		}
		return false;
	}

	public List<DealModel> findConflictDealForCurrentDeal(final DealModel deal, final CartModel cart, final B2BUnitModel b2bUnit)
	{
		final ConflictGroup conflictGroup = getAllConflictingDealsInCart(cart, b2bUnit);
		final List<Conflict> Conflicts = conflictGroup.getConflicts();
		final Set<DealModel> conflictDeals = new HashSet<DealModel>();
		for (final Conflict conflict : Conflicts)
		{
			if (conflict.getDeals().contains(deal))
			{
				conflictDeals.addAll(conflict.getDeals());
			}
		}
		return new ArrayList<>(conflictDeals);
	}

	public void removeRejectedDealIfNotQualify(final CartModel cart, final B2BUnitModel b2bUnit)
	{
		final DealQualificationResponse dealQualificationResponse = dealConditionService.findQualifiedDeals(b2bUnit, cart);
		final List<CartDealConditionModel> removedDeal = new ArrayList<>();
		final List<CartDealConditionModel> cartDealConditions = cart.getComplexDealConditions();

		if (CollectionUtils.isEmpty(cartDealConditions))
		{
			return;
		}

		if (dealQualificationResponse != null && CollectionUtils.isNotEmpty(dealQualificationResponse.getGreenDeals()))
		{
			for (final CartDealConditionModel cartDealConditionModel : cartDealConditions)
			{
				//if the rejected deal is not contained in QualifiedDealList, it should be removed.
				if (DealConditionStatus.REJECTED.equals(cartDealConditionModel.getStatus())
						&& !(dealQualificationResponse.getGreenDeals().contains(cartDealConditionModel.getDeal())))
				{
					removedDeal.add(cartDealConditionModel);
				}
			}
		}
		else
		{
			//if there is no qualified deal. remove all the complex deal in the cart.
			for (final CartDealConditionModel cartDealConditionModel : cartDealConditions)
			{
				if (cartDealConditionModel.getDeal() == null
						|| DealTypeEnum.COMPLEX.equals(cartDealConditionModel.getDeal().getDealType()))
				{
					removedDeal.add(cartDealConditionModel);
				}
			}
		}

		if (!removedDeal.isEmpty())
		{
			CollectionUtils.removeAll(cartDealConditions, removedDeal);
			getModelService().removeAll(removedDeal);
			getModelService().save(cart);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.cart.service.SABMCartService#getCustomCartRules()
	 */
	@Override
	public List<SabmCartRuleModel> getCustomCartRules()
	{
		return commerceCartDao.getCustomCartRules();

	}

	public Map<String, Object> getOrderBasedProductMaxOrderQty(final ProductModel productModel,
			final MaxOrderQtyModel maxOrderQtyModel, final Date requestedDispatchDate)
	{
		final Map<String, Object> orderBasedProductMaxOrderQtyMap = new HashedMap<String, Object>();
		Integer orderedQty = 0;
		if(null != maxOrderQtyModel) {
			List<OrderEntryModel> orderEntryModels = Collections.emptyList();
			final CMSSiteModel cmsSiteModel = cmsSiteService.getCurrentSite();
			final Map<String, Date> maxOrderQtyDatesMap = getMaxOrderQtyStartAndEndDate(maxOrderQtyModel, requestedDispatchDate);
			if(MaxOrderQtyRuleType.CUSTOMER_RULE.equals(maxOrderQtyModel.getRuleType())) {
				orderEntryModels = commerceCartDao.getOrderEntriesForCustomerRule(productModel, maxOrderQtyModel.getB2bunit(),
						cmsSiteModel, maxOrderQtyDatesMap);
			} else if(MaxOrderQtyRuleType.PLANT_RULE.equals(maxOrderQtyModel.getRuleType())) {
				orderEntryModels = commerceCartDao.getOrderEntriesForPlantRule(productModel, maxOrderQtyModel.getPlant(),
						cmsSiteModel, maxOrderQtyDatesMap);
			} else if(MaxOrderQtyRuleType.GLOBAL_RULE.equals(maxOrderQtyModel.getRuleType())) {
				orderEntryModels = commerceCartDao.getOrderEntriesForGlobalRule(productModel, cmsSiteModel, maxOrderQtyDatesMap);
			}

			if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(orderEntryModels)) {
				orderedQty = orderEntryModels.stream().map(entry -> entry.getQuantity()).reduce(0L, (a, b) -> a + b).intValue();
				orderBasedProductMaxOrderQtyMap.put(SabmCoreConstants.MAX_ORDERQTY_END_DATE,
						maxOrderQtyDatesMap.get(SabmCoreConstants.MAX_ORDERQTY_END_DATE));
			}
		}
		orderBasedProductMaxOrderQtyMap.put(ORDERED_QTY, orderedQty);

		return orderBasedProductMaxOrderQtyMap;
	}

	/**
	 *
	 * @param maxOrderQtyModel
	 * @param requestedDispatchDate
	 * @return
	 */

	public Map<String, Date> getMaxOrderQtyStartAndEndDate(final MaxOrderQtyModel maxOrderQtyModel,
			final Date requestedDispatchDate)
	{
		LocalDate startDate = maxOrderQtyModel.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		final LocalDate endDate = maxOrderQtyModel.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
		final LocalDate requestedDispatchD = requestedDispatchDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		final Map<String, Date> maxOrderQtyDates = new HashedMap<String, Date>();
		final long maxOrderRuleDays = Long.parseLong(
				asahiConfigurationService.getString(ApbCoreConstants.CUB_MAX_ORDER_QTY_RULE_DAYS,
						ApbCoreConstants.DEFAULT_MAX_ORDER_QTY_RULE_DAYS));
		while (startDate.isBefore(endDate) || startDate.equals(endDate))
		{
			LocalDate newDate = startDate.plusDays(maxOrderRuleDays);
			if (newDate.isAfter(endDate))
			{
				newDate = endDate.plusDays(1);
			}
			if (requestedDispatchD.isBefore(newDate))
			{
				maxOrderQtyDates.put(SabmCoreConstants.MAX_ORDERQTY_START_DATE, DateUtils.truncate(
						Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), Calendar.DAY_OF_MONTH));
				maxOrderQtyDates.put(SabmCoreConstants.MAX_ORDERQTY_END_DATE, DateUtils
						.truncate(Date.from(newDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), Calendar.DAY_OF_MONTH));
				return maxOrderQtyDates;
			}
			startDate = newDate;
		}
		return maxOrderQtyDates;
	}

	public Map<String, Object> getFinalMaxOrderQty(final ProductModel productModel, final Date requestedDispatchDate)
	{
 		Integer finalMaxOrderQty = null;
 		Integer orderedQty = 0;
 		Integer configuredMaxOrderQty = null;
		Map<String, Object> orderBasedProductMaxOrderQtyMap = new HashedMap<String, Object>();
 		final SABMAlcoholVariantProductEANModel sabmAlcoholVariantProductEANModel = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel)productModel).getBaseProduct();
 		if(null != sabmAlcoholVariantProductEANModel && null != requestedDispatchDate) {
 			final MaxOrderQtyModel maxOrderQtyModel =this.productService.getMaxOrderQuantity(sabmAlcoholVariantProductEANModel);
 			configuredMaxOrderQty = this.getEffectiveMaxOrderQty(maxOrderQtyModel);
 			if(0 != ObjectUtils.defaultIfNull(configuredMaxOrderQty, 0)) {
				orderBasedProductMaxOrderQtyMap = this.getOrderBasedProductMaxOrderQty(productModel, maxOrderQtyModel,
						requestedDispatchDate);
				orderedQty = (Integer) orderBasedProductMaxOrderQtyMap.get(ORDERED_QTY);

 				finalMaxOrderQty = Math.subtractExact(configuredMaxOrderQty, orderedQty);
 			}
 		}
 		if(finalMaxOrderQty != null && finalMaxOrderQty < 0) {
 			finalMaxOrderQty = 0;
 		}
 		//finalMaxOrderQty = (null != finalMaxOrderQty) && (finalMaxOrderQty < 0) ? 0:finalMaxOrderQty;
		final Map<String, Object> qtyMap = new HashedMap<String, Object>();
 		qtyMap.put(SabmCoreConstants.FINAL_MAX_ORDER_QTY, finalMaxOrderQty);
 		qtyMap.put(SabmCoreConstants.CONFIGURED_MAX_QTY, configuredMaxOrderQty);
 		final long maxOrderRuleDays = Long.parseLong(
				asahiConfigurationService.getString(ApbCoreConstants.CUB_MAX_ORDER_QTY_RULE_DAYS, DEFAULT_MAX_ORDER_QTY_RULE_DAYS));
		qtyMap.put(ApbCoreConstants.CUB_MAXORDER_QTY_RULE_DAYS, maxOrderRuleDays);
 		if(orderedQty > 0) {
 			qtyMap.put(SabmCoreConstants.TOTAL_ORDERED_QTY, orderedQty);
			qtyMap.put(SabmCoreConstants.MAX_ORDERQTY_END_DATE,
					orderBasedProductMaxOrderQtyMap.get(SabmCoreConstants.MAX_ORDERQTY_END_DATE));
 		}
 		return qtyMap;
 	}

 	private Integer getEffectiveMaxOrderQty(final MaxOrderQtyModel maxOrderQtyModel) {
 		if (null != maxOrderQtyModel)
		{
			if (maxOrderQtyModel.getDefaultAvgMaxOrderQtyEnabled())
			{
				if (0 != ObjectUtils.defaultIfNull(maxOrderQtyModel.getDefaultAvgMaxOrderQty(), 0))
				{
					return maxOrderQtyModel.getDefaultAvgMaxOrderQty();
				}
			}
			else
			{
				return maxOrderQtyModel.getMaxOrderQty();
			}
		}
		return null;
 	}
}
