package com.sabmiller.facades.order.impl;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BOrderFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantProductModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.card.payment.AsahiPaymentCaptureRequestService;
import com.apb.core.order.services.AsahiSendOrderToBackenedService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbCustomerAccountService;
import com.apb.core.services.AsahiOrderService;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.comparator.order.entry.AsahiOrderEntryDataComparator;
import com.apb.facades.order.data.AsahiQuickOrderData;
import com.apb.facades.order.data.AsahiQuickOrderEntryData;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.sabmiller.commons.enumerations.OrderToCartStatus;
import com.sabmiller.core.b2b.services.CUBStockInformationService;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.b2b.services.SabmOrderTemplateService;
import com.sabmiller.core.comparators.TrackOrderDataComparator;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.CUBStockStatus;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.notification.service.NotificationService;
import com.sabmiller.core.order.SabmB2BOrderService;
import com.sabmiller.core.ordersplitting.SabmConsignmentService;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.dataimport.response.SalesOrderDataImportResponse;
import com.sabmiller.facades.order.SABMOrderFacade;
import com.sabmiller.facades.order.data.TrackOrderData;
import com.sabmiller.facades.order.json.OrderHistoryJson;
import com.sabmiller.facades.populators.SabmTrackOrderPopulator;
import com.sabmiller.facades.smartOrders.json.SmartOrdersJson;


/**
 * The Class DefaultSABMOrderFacade.
 */
public class DefaultSABMOrderFacade extends DefaultB2BOrderFacade implements SABMOrderFacade
{

	private static final String ORDER_NOT_FOUND = "Order with guid %s not found for current user in current BaseStore";

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMOrderFacade.class);

	/** The Constant YSR1. */
	private static final String YSR1 = "YSR1";

	/** The Constant B2B. */
	private static final String B2B = "B2B";

	private static final String PICKING_IN_PROGRESS = "20";

	private static final String IN_PROGRESS = "10";

	/** The order history json converter. */
	@Resource
	private Converter<OrderModel, OrderHistoryJson> orderHistoryJsonConverter;

	/** The b2b order service. */
	@Resource(name = "b2bOrderService")
	private SabmB2BOrderService b2bOrderService;

	/** The cart facade. */
	@Resource(name = "cartFacade")
	private SABMCartFacade cartFacade;

	/** The order reverse converter. */
	@Resource(name = "sabmOrderReverseConverter")
	private Converter<OrderData, OrderModel> orderReverseConverter;

	/** The sabm order template service. */
	@Resource(name = "sabmOrderTemplateService")
	private SabmOrderTemplateService sabmOrderTemplateService;

	/** The consignment service. */
	@Resource(name = "sabmConsignmentService")
	private SabmConsignmentService consignmentService;

	/** The b2b commerce unit service. */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	/** The default sabm b2 b unit service. */
	@Resource
	private SabmB2BUnitService defaultSabmB2BUnitService;

	@Resource(name = "productService")
	private SabmProductService sabmProductService;

	@Resource(name = "sabmDeliveryDateCutOffService")
	private SABMDeliveryDateCutOffService deliveryDateCutOffService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "sabmTrackOrderPopulator")
	private SabmTrackOrderPopulator sabmTrackOrderPopulator;

	@Resource(name = "sabmTrackOrderBasicPopulator")
	private Populator<ConsignmentModel,TrackOrderData> sabmTrackOrderBasicPopulator;

	@Resource
	private CUBStockInformationService cubStockInformationService;

	@Resource(name = "orerStatusDisplayMapping")
	private Map<OrderStatus, String> orerStatusDisplayMapping;

	@Resource(name = "trackOrderDataComparator")
	private TrackOrderDataComparator trackOrderDataComparator;

	@Resource(name = "b2bCommerceUnitFacade")
	private SabmB2BCommerceUnitFacade b2bUnitFacade;

	/** The asahi order service. */
	@Resource(name = "asahiOrderService")
	private AsahiOrderService asahiOrderService;

	/** The apb product basic reverse converter. */
	private Converter<OrderData, OrderModel> asahiOrderReverseConverter;

	/** The apb product basic reverse converter. */
	private Converter<List<OrderModel>, AsahiQuickOrderData> asahiQuickOrderConverter;

	/** The search restriction service. */
	@Resource(name = "searchRestrictionService")
	private SearchRestrictionService searchRestrictionService;

	@Resource(name = "customerAccountService")
	private ApbCustomerAccountService customerAccountService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "inclusionExclusionProductStrategy")
	private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;

	@Resource(name = "asahiPaymentCaptureRequestService")
	private AsahiPaymentCaptureRequestService asahiPaymentCaptureRequestService;

	@Resource(name = "asahiSendOrderToBackenedService")
	private AsahiSendOrderToBackenedService asahiSendOrderToBackenedService;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "notificationService")
	private NotificationService notificationService;



	/**
	 * @return the asahiQuickOrderConverter
	 */
	public Converter<List<OrderModel>, AsahiQuickOrderData> getAsahiQuickOrderConverter()
	{
		return asahiQuickOrderConverter;
	}

	/**
	 * @param asahiQuickOrderConverter
	 *           the asahiQuickOrderConverter to set
	 */
	public void setAsahiQuickOrderConverter(final Converter<List<OrderModel>, AsahiQuickOrderData> asahiQuickOrderConverter)
	{
		this.asahiQuickOrderConverter = asahiQuickOrderConverter;
	}

	/**
	 * @return the sabmProductService
	 */
	public SabmProductService getSabmProductService()
	{
		return sabmProductService;
	}

	/**
	 * @param sabmProductService
	 *           the sabmProductService to set
	 */
	public void setSabmProductService(final SabmProductService sabmProductService)
	{
		this.sabmProductService = sabmProductService;
	}


	/**
	 * Gets the order history.
	 *
	 * @param dateFrom
	 *           the date from
	 * @param dateTo
	 *           the date to
	 * @return the order history
	 */
	@Override
	public List<OrderHistoryJson> getOrderHistory(final Date dateFrom, final Date dateTo)
	{
		List<OrderHistoryJson> orderList = null;

		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();

		List<OrderModel> orderResults;
		if (dateFrom != null && dateTo != null)
		{
			final Calendar cal = Calendar.getInstance();
			cal.setTime(dateTo);
			cal.add(Calendar.HOUR_OF_DAY, 23);
			cal.add(Calendar.MINUTE, 59);
			cal.add(Calendar.SECOND, 59);
			orderResults = b2bOrderService.getOrderByB2BUnit(b2bCommerceUnitService.getParentUnit(), dateFrom, cal.getTime());
		}
		else
		{
			orderResults = b2bOrderService.getOrderByB2BUnit(b2bCommerceUnitService.getParentUnit());
		}
		orderList = Converters.convertAll(orderResults, orderHistoryJsonConverter);
		LOG.debug("Order history {} for customer {}", orderList, currentCustomer);

		return orderList != null ? orderList : Collections.<OrderHistoryJson> emptyList();
	}

	/**
	 *
	 * @param count
	 * @return
	 */
	@Override
	public List<OrderHistoryJson> getTopOrderHistory(final int count)
	{

		final List<OrderModel> orderResults = b2bOrderService.getTopOrder(count);

		final List<OrderHistoryJson> orderList = Converters.convertAll(orderResults, orderHistoryJsonConverter);

		if(LOG.isDebugEnabled()) {
			final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
			LOG.debug("Order history {} for customer {}", orderList, currentCustomer);
		}

		return ListUtils.emptyIfNull(orderList);
	}

	@Override
	public List<TrackOrderData> getActiveOrderByB2BUnit()
	{
		final List<TrackOrderData> trackOrderList = new ArrayList<TrackOrderData>();

		List<OrderModel> orderResults = b2bOrderService.getOrderByB2BUnit(b2bCommerceUnitService.getParentUnit());


		// all ordered retreved from DAO order status can't be null, order requestDeliveryDate can't be null
		// as per https://ab-inbev.atlassian.net/browse/B2BCUB3-232, order status in Cancelled will be be shown in Track my delivery
		orderResults = orderResults.stream().filter(order -> orerStatusDisplayMapping.containsKey(order.getStatus()))
				.filter(order -> !order.getStatus().equals(OrderStatus.CANCELLED))
				.filter(order -> SabmDateUtils.isAfterDay(order.getRequestedDeliveryDate(), DateUtils.addDays(new Date(), -2)))
				.collect(Collectors.toList());

		for (final OrderModel orderModel : orderResults)
		{

			trackOrderList.addAll(getTrackOrderData(orderModel,sabmTrackOrderBasicPopulator));
		}

		return trackOrderList;
	}

	@Override
	public OrderModel getOrderBySapSalesOrderNumber(final String orderCode)
	{
		return b2bOrderService.getOrderBySapSalesOrderNumber(orderCode);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMOrderFacade#getOrderForCodeAndAddToCart(java.lang.String)
	 */
	@Override
	public Map<OrderToCartStatus, Object> addToCartForOrderCode(final String orderCode)
	{
		final OrderModel orderModel = b2bOrderService.getOrderForCode(orderCode);
		if (orderModel == null)
		{
			throw new UnknownIdentifierException(
					"Order with code " + orderCode + " not found for current user in current BaseStore");
		}

		final Map<OrderToCartStatus, Object> mapReturn = new HashMap<>();
		final List<String> invalidproductTitles = new ArrayList<String>();
		final List<String> excludedproductTitles = new ArrayList<String>();
		final List<CartModificationData> cartModificationDatas = new ArrayList<CartModificationData>();
		for (final AbstractOrderEntryModel abstractOrderEntryModel : orderModel.getEntries())
		{
			this.addToCartForOrder(abstractOrderEntryModel, invalidproductTitles, excludedproductTitles, cartModificationDatas);
		}
		mapReturn.put(OrderToCartStatus.INVALID_PRODUCT_TITLES, invalidproductTitles);
		mapReturn.put(OrderToCartStatus.EXCLUDED_PRODUCT_TITLES, excludedproductTitles);
		mapReturn.put(OrderToCartStatus.CART_MODIFICATION_DATAS, cartModificationDatas);
		return mapReturn;
	}

	@Override
	public Map<OrderToCartStatus, Object> addToCartForOrderCode(final String orderCode, final List<String> entryNumbers)
	{
		final OrderModel orderModel = b2bOrderService.getOrderForCode(orderCode);
		if (orderModel == null)
		{
			throw new UnknownIdentifierException(
					"Order with code " + orderCode + " not found for current user in current BaseStore");
		}

		final Map<OrderToCartStatus, Object> mapReturn = new HashMap<>();
		final List<String> invalidproductTitles = new ArrayList<String>();
		final List<String> excludedproductTitles = new ArrayList<String>();
		final List<CartModificationData> cartModificationDatas = new ArrayList<CartModificationData>();
		for (final AbstractOrderEntryModel abstractOrderEntryModel : orderModel.getEntries())
		{
			if (entryNumbers.contains(abstractOrderEntryModel.getEntryNumber().toString()))
			{
				this.addToCartForOrder(abstractOrderEntryModel, invalidproductTitles, excludedproductTitles, cartModificationDatas);
			}
		}
		mapReturn.put(OrderToCartStatus.INVALID_PRODUCT_TITLES, invalidproductTitles);
		mapReturn.put(OrderToCartStatus.EXCLUDED_PRODUCT_TITLES, excludedproductTitles);
		mapReturn.put(OrderToCartStatus.CART_MODIFICATION_DATAS, cartModificationDatas);
		return mapReturn;
	}

	/**
	 * Add the order template entry's product to cart.
	 *
	 * @param orderCode
	 *           the order template code
	 * @return Map<OrderToCartStatus, Object> the map of the result
	 */
	@Override
	public Map<OrderToCartStatus, Object> addToTemplate(final String orderCode)
	{
		final SABMOrderTemplateModel orderTemplate = this.getOrderTemplateByCode(orderCode);

		final Map<OrderToCartStatus, Object> mapReturn = new HashMap<>();

		if (orderTemplate == null || CollectionUtils.isEmpty(orderTemplate.getEntries()))
		{
			mapReturn.put(OrderToCartStatus.EMPTY_ADD_TO_CART, true);
		}
		else
		{

			b2bUnitFacade.removeSapUnavailabilityEntriesFromTemplate(orderTemplate);

			final List<String> invalidproductTitles = new ArrayList<>();
			final List<String> excludedproductTitles = new ArrayList<>();
			final List<CartModificationData> cartModificationDatas = new ArrayList<>();

			boolean haveNoQuantity = true;
			for (final AbstractOrderEntryModel abstractOrderEntryModel : orderTemplate.getEntries())
			{
				if (abstractOrderEntryModel != null && abstractOrderEntryModel.getQuantity() != null
						&& abstractOrderEntryModel.getQuantity().longValue() > 0)
				{
					this.addToCartForOrder(abstractOrderEntryModel, invalidproductTitles, excludedproductTitles,
							cartModificationDatas);
					haveNoQuantity = false;
				}
			}
			if (haveNoQuantity)
			{
				mapReturn.put(OrderToCartStatus.EMPTY_ADD_TO_CART, true);
			}
			else
			{
				mapReturn.put(OrderToCartStatus.INVALID_PRODUCT_TITLES, invalidproductTitles);
				mapReturn.put(OrderToCartStatus.EXCLUDED_PRODUCT_TITLES, excludedproductTitles);
				mapReturn.put(OrderToCartStatus.CART_MODIFICATION_DATAS, cartModificationDatas);
			}
		}

		return mapReturn;
	}

	/**
	 * Adds the to cart for order.
	 *
	 * @param abstractOrderEntryModel
	 *           the abstract order entry model
	 * @param invalidproductTitles
	 *           the invalidproduct titles
	 * @param excludedproductTitles
	 *           the excludedproduct titles
	 * @param cartModificationDatas
	 *           the cart modification datas
	 */
	private void addToCartForOrder(final AbstractOrderEntryModel abstractOrderEntryModel, final List<String> invalidproductTitles,
			final List<String> excludedproductTitles, final List<CartModificationData> cartModificationDatas)
	{
		boolean isLeadVariant = false;
		String variantProductCode = null;
		boolean isVariantOutofStock = false;

		//Do not add Free SKUs and rejected (cancelled) items
		if (BooleanUtils.isNotTrue(abstractOrderEntryModel.getIsFreeGood())
				&& BooleanUtils.isNotTrue(abstractOrderEntryModel.getRejected()))
		{
			ProductModel variant = abstractOrderEntryModel.getProduct();
			SABMAlcoholVariantProductEANModel eanProduct = null;

			//Checking if the source product is instanceof SABMAlcoholVariantProductEANModel because the attribute UomMappings belongs to it.
			while (variant instanceof VariantProductModel)
			{
				if (variant.getClass().equals(SABMAlcoholVariantProductEANModel.class))
				{
					eanProduct = (SABMAlcoholVariantProductEANModel) variant;
					break;
				}
				variantProductCode = variant.getCode();
				isLeadVariant = isLeadVariant(((VariantProductModel) variant).getBaseProduct(), (VariantProductModel) variant);
				if (BooleanUtils.isTrue(isLeadVariant))
				{
					isVariantOutofStock = isProductOutOfStock(variantProductCode);
				}
				variant = ((VariantProductModel) variant).getBaseProduct();
			}

			//Get the product what can be added to the cart.
			if (eanProduct != null)
			{
				final String deliveryDatePackType = sessionService
						.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE_PACKTYPE);
				if (BooleanUtils.isTrue(eanProduct.getPurchasable()) && BooleanUtils.isFalse(isVariantOutofStock)
						&& isPackTypeAllowed(deliveryDatePackType, abstractOrderEntryModel.getUnit().getCode()))
				{
					addAvailableCartModification(abstractOrderEntryModel, cartModificationDatas, eanProduct);
				}
				else
				{
					excludedproductTitles.add(getProductTitle(eanProduct));
				}
			}
			else
			{
				invalidproductTitles.add(getProductTitle(eanProduct));
			}
		}
	}




	/**
	 * Adds the available cart modification.
	 *
	 * @param abstractOrderEntryModel
	 *           the abstract order entry model
	 * @param cartModificationDatas
	 *           the cart modification datas
	 * @param product
	 *           the product
	 */
	private void addAvailableCartModification(final AbstractOrderEntryModel abstractOrderEntryModel,
			final List<CartModificationData> cartModificationDatas, final SABMAlcoholVariantProductEANModel product)
	{
		final CartModificationData cartModificationData = orderAddToCart(abstractOrderEntryModel, product);
		if (null != cartModificationData)
		{
			cartModificationDatas.add(cartModificationData);
		}
	}

	/**
	 * Gets the order template by code.
	 *
	 * @author yuxiao.wang
	 * @param orderCode
	 *           the order code
	 * @return SABMOrderTemplateModel
	 */
	private SABMOrderTemplateModel getOrderTemplateByCode(final String orderCode)
	{
		//get the b2b unit
		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
		SABMOrderTemplateModel orderTemplate = null;

		if (b2bUnit != null)
		{
			try
			{
				orderTemplate = sabmOrderTemplateService.findOrderTemplateByCode(orderCode, b2bUnit);

			}
			catch (final AmbiguousIdentifierException | UnknownIdentifierException e)
			{
				LOG.error("Error getting Order Template with code: " + orderCode + " for B2BUnit: " + b2bUnit, e);
			}
		}

		return orderTemplate;

	}

	/**
	 * get product display title.
	 *
	 * @param product
	 *           the product
	 * @return product title
	 */
	private String getProductTitle(final SABMAlcoholVariantProductEANModel product)
	{
		if(null!= product) {
			if (StringUtils.isNotEmpty(product.getSellingName()) && StringUtils.isNotEmpty(product.getPackConfiguration()))
			{
				return product.getSellingName() + " " + product.getPackConfiguration();
			}
			return product.getName();
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Order add to cart.
	 *
	 * @param abstractOrderEntryModel
	 *           the abstract order entry model
	 * @param product
	 *           the product
	 * @return the cart modification data
	 */
	private CartModificationData orderAddToCart(final AbstractOrderEntryModel abstractOrderEntryModel,
			final SABMAlcoholVariantProductEANModel product)
	{
		try
		{
			if (abstractOrderEntryModel.getUnit() != null && StringUtils.isNotEmpty(abstractOrderEntryModel.getUnit().getCode()))
			{
				return cartFacade.addToCart(product.getCode(), abstractOrderEntryModel.getUnit().getCode(),
						abstractOrderEntryModel.getQuantity().longValue());

			}
			return cartFacade.addToCart(product.getCode(), abstractOrderEntryModel.getQuantity().longValue());
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.error("Failed of product[{}] in order[{}] to add to cart.", product, abstractOrderEntryModel, e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.facades.order.SABMOrderFacade#persistOrder(de.hybris.platform.commercefacades.order.data.OrderData)
	 */
	@Override
	public SalesOrderDataImportResponse persistOrder(final OrderData orderData)
	{
		final boolean orderExist = orderExist(orderData.getSapSalesOrderNumber());

		final boolean fakeCheckEnabled = Config.getBoolean("inbound.service.sales.order.fake.check", false);
		if (fakeCheckEnabled && !YSR1.equals(orderData.getProcessingTypeCode()) && !orderExist
				&& B2B.equals(orderData.getDataOriginCategoryCode()))
		{
			LOG.warn("This is Fake Order {} Request from SAP. Unfortunately SAP triggers this unwanted sales order update everytime "
					+ "sales order create service is invoked and the inability of SAP to prevent this leaves us with no option but to use "
					+ "this hacky check and ignore the order update", orderData.getSapSalesOrderNumber());

			final SalesOrderDataImportResponse response = new SalesOrderDataImportResponse();
			response.setFake(true);
			response.setOrderId(orderData.getSapSalesOrderNumber());
			return response;
		}

		final OrderModel orderModel = orderReverseConverter.convert(orderData);
		getModelService().save(orderModel);

		consignmentService.recalculateConsignments(orderModel);

		getModelService().save(orderModel);

		final SalesOrderDataImportResponse response = new SalesOrderDataImportResponse();
		response.setSource(orderModel.getSalesApplication().name());
		response.setOrderId(orderModel.getCode());
		response.setExist(orderExist);
		return response;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMOrderFacade#orderExist(java.lang.String)
	 */
	@Override
	public boolean orderExist(final String salesOrderNumber)
	{
		return b2bOrderService.getOrderBySapSalesOrderNumber(salesOrderNumber) != null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMOrderFacade#getEmailByOrder(java.lang.String)
	 */
	@Override
	public String getEmailByOrder(final String orderCode)
	{
		final AbstractOrderModel order = b2bOrderService.getAbstractOrderForCode(orderCode);

		if (order != null && order.getUser() != null)
		{
			return StringUtils.trimToEmpty(order.getUser().getUid());
		}

		return StringUtils.EMPTY;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.order.SABMOrderFacade#smartOrdersJson(java.lang.String, java.lang.String)
	 */
	@Override
	public SmartOrdersJson smartOrdersJson(final String date, final String sort)
	{
		return b2bOrderService.getPagedOrdersByB2BUnit(b2bCommerceUnitService.getParentUnit(), 0, date, sort);
	}

	@Override
	public String getCutoffTime(final B2BUnitModel unit, final Date requestedDeliveryDate)
	{
		return deliveryDateCutOffService.getCutOffTime(unit, requestedDeliveryDate);
	}

	//To Check if the product is OOS
	private boolean isProductOutOfStock(final String productCode)
	{
		final B2BUnitModel parentB2bUnit = b2bCommerceUnitService.getParentUnit();
		final PlantModel plant = parentB2bUnit.getPlant();
		if (plant != null)
		{
			final CUBStockInformationModel cubStockInformationModel = cubStockInformationService
					.getCUBStockInformationForProductAndPlant(productCode, plant);

			if (cubStockInformationModel != null)
			{
				if (cubStockInformationModel.getStockStatus().equals(CUBStockStatus.OUTOFSTOCK))
				{
					return true;
				}
			}
		}
		return false;
	}

	//To check if variant is the Lead Variant of the product
	private boolean isLeadVariant(final ProductModel baseProduct, final VariantProductModel variantProduct)
	{
		final SABMAlcoholVariantProductMaterialModel materialModel = sabmProductService.getMaterialFromEan(baseProduct);
		if (materialModel.equals(variantProduct))
		{
			return true;
		}
		/*
		 * final List<VariantProductModel> variants = (List<VariantProductModel>) baseProduct.getVariants(); if
		 * (CollectionUtils.isNotEmpty(variants)) { return variants.get(0).equals(variantProduct) ? true : false; }
		 */
		return false;
	}

	private Boolean isPackTypeAllowed(final String deliveryDatePackType, final String unitCode)
	{
		if (StringUtils.isBlank(deliveryDatePackType))
		{
			return false;
		}

		String packType = unitCode.toUpperCase();
		if (!packType.equals("KEG"))
		{
			packType = "PACK";
		}

		return deliveryDatePackType.toUpperCase().contains(packType);
	}

	protected List<TrackOrderData> getTrackOrderData(final OrderModel orderModel, final Populator<ConsignmentModel,TrackOrderData> populator){
		final List<TrackOrderData> list = new ArrayList<TrackOrderData>();

		// OrderModel  orderModel = b2bOrderService.getOrderForCode(orderCode);

		int i = 1;

		if (orderModel != null)
		{
			for (final ConsignmentModel consignmentModel : orderModel.getConsignments())

			{
				final TrackOrderData trackOrderData = new TrackOrderData();
				trackOrderData.setShipmentId(i);
				populator.populate(consignmentModel, trackOrderData);
				list.add(trackOrderData);
				i++;
			}
		}

		//resort list by consignment estimated arrvial time  earliest - latest, null will be last

		// Collections.sort(list,trackOrderDataComparator);


		list.sort(Comparator.comparing(o -> o.getStartETA(), Comparator.nullsLast(Comparator.naturalOrder())));

		list.sort(Comparator.comparing(o -> o.getArrivedTime(), Comparator.nullsLast(Comparator.naturalOrder())));

		return list;
	}

	@Override
	public List<TrackOrderData> getTrackOrderData(final OrderModel orderModel)
	{
		return getTrackOrderData(orderModel,sabmTrackOrderPopulator);
	}

	/* (non-Javadoc)
	 * @see com.sabmiller.facades.order.SABMOrderFacade#getOrderByCreationDate(java.util.Date)
	 */
	@Override
	public OrderModel getOrderByCartCode(final String cartCode)
	{
		// YTODO Auto-generated method stub
		return b2bOrderService.getOrderByCartCode(cartCode);
	}

	/**
	 * Import order.
	 *
	 * @param orderData
	 *           the order data
	 */
	@Override
	public void importOrder(final OrderData orderData, final String siteUid)
	{
		// Fetching Order based on code
		this.searchRestrictionService.disableSearchRestrictions();

		String orderCode = orderData.getSalesOrderId();
		if (StringUtils.isNotEmpty(orderData.getPortalOrderId()))
		{
			orderCode = orderData.getPortalOrderId();
		}
		OrderModel existingOrder = this.asahiOrderService.getOrderForCode(orderCode);
		// Check if this product already exist in hybris if yes then update otherwise create new.
		if (null != existingOrder)
		{
		// update existing Order
			if (existingOrder.getStatus().equals(OrderStatus.DELIVERED) || existingOrder.getStatus().equals(OrderStatus.COMPLETED)
						|| existingOrder.getStatus().equals(OrderStatus.IN_TRANSIT))
				{
					if (StringUtils.isNotEmpty(orderData.getStatusCode()) && !(orderData.getStatusCode().equals(PICKING_IN_PROGRESS)
							||orderData.getStatusCode().equals(IN_PROGRESS)))
						{
							existingOrder = this.asahiOrderReverseConverter.convert(orderData, existingOrder);
						// saving existing Order into hybris database
							getModelService().save(existingOrder);
						}
				}
				else
				{
					existingOrder = this.asahiOrderReverseConverter.convert(orderData, existingOrder);
					// saving existing Order into hybris database
					getModelService().save(existingOrder);
				}
		}
		else
		{
			//create new Order in hybris database
			OrderModel newOrder = getModelService().create(OrderModel.class);
			//calling converter to populate the OrderModel
			newOrder = this.asahiOrderReverseConverter.convert(orderData, newOrder);

			//saving new Order into hybris database
			getModelService().save(newOrder);
		}
		this.searchRestrictionService.enableSearchRestrictions();
	}

	/**
	 * Gets the paged order history for statuses.
	 *
	 * @param pageableData
	 *           the pageable data
	 * @return the paged order history for statuses
	 * @throws ParseException
	 */
	@Override
	public SearchPageData<OrderHistoryData> getPagedOrderHistory(final PageableData pageableData, final String cofoDate) throws ParseException
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final SearchPageData<OrderModel> orderResults = this.customerAccountService.getOrderList(currentCustomer, currentBaseStore,
				pageableData, cofoDate);

		return convertPageData(orderResults, getOrderHistoryConverter());
	}


	/**
	 * Gets the paged order history for statuses.
	 *
	 * @param pageableData
	 *           the pageable data
	 * @return the paged order history for statuses
	 * @throws ParseException
	 */
	@Override
	public String exportOrderCSV(final PageableData pageableData, final String cofoDate) throws ParseException
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final SearchPageData<OrderModel> orderResults = this.customerAccountService.getOrderList(currentCustomer, currentBaseStore,
				pageableData, cofoDate);

	    final List<OrderModel> orderModels = orderResults.getResults();

	    if(CollectionUtils.isNotEmpty(orderModels))
	    {
	   	 final List<OrderHistoryData> orders = Converters.convertAll(orderModels, getOrderHistoryConverter());
	   	 return asahiOrderService.exportOrderCSV(orders);
	    }

	   return null;
	}

	@Override
	public OrderData getOrderDetailsForCode(final String code)
	{
		if(!asahiSiteUtil.isCub())
		{
		final OrderModel orderModel = getOrderDetails(code);
		final OrderData orderData = getOrderConverter().convert(orderModel);
		if (asahiSiteUtil.isSga())
		{
			inclusionExclusionProductStrategy.updateProductData(orderData);
		}

		if (asahiSiteUtil.isApb() && CollectionUtils.isNotEmpty(orderData.getUnconsignedEntries())
				&& asahiCoreUtil.checkIfBonusEntryPresent(orderData.getUnconsignedEntries()))
		{
			final List<OrderEntryData> entries = orderData.getUnconsignedEntries();
			Collections.sort(entries,
					AsahiOrderEntryDataComparator.getComparator(AsahiOrderEntryDataComparator.SORT_BASED_ON.BONUS_STOCK_SORT));

			orderData.setUnconsignedEntries(entries);
		}
		return orderData;
		}
		else
		{
			final OrderModel orderModel = b2bOrderService.getOrderForCode(code);
			if (orderModel == null)
			{
				throw new UnknownIdentifierException("Order with code " + code + " not found for current user in current BaseStore");
			}
			return getOrderConverter().convert(orderModel);
		}

	}

	private AsahiB2BUnitModel getCurrentB2BUnit()
	{
		final UserModel user = getUserService().getCurrentUser();
		if (null != user && user instanceof B2BCustomerModel && !getUserService().isAnonymousUser(user))
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
			if (b2bUnit instanceof AsahiB2BUnitModel)
			{

				return (AsahiB2BUnitModel) b2bUnit;
			}
		}
		return null;
	}

	/**
	 * @return the asahiOrderReverseConverter
	 */
	public Converter<OrderData, OrderModel> getAsahiOrderReverseConverter()
	{
		return asahiOrderReverseConverter;
	}

	/**
	 * @param asahiOrderReverseConverter
	 *           the asahiOrderReverseConverter to set
	 */
	public void setAsahiOrderReverseConverter(final Converter<OrderData, OrderModel> asahiOrderReverseConverter)
	{
		this.asahiOrderReverseConverter = asahiOrderReverseConverter;
	}

	/**
	 * Gets the asahi order details for code.
	 *
	 * @param code
	 *           the code
	 * @return the asahiOrderReverseConverter
	 */
	@Override
	public OrderData getAsahiOrderDetailsForCode(final OrderModel orderModel)
	{
		OrderData orderDetails = new OrderData();

		if (null != orderModel)
		{
			// Trigger Order Confirmation Email SGA ...
			if (asahiSiteUtil.isSga())
			{
				final UserModel currentUser = getUserService().getCurrentUser();
				if(currentUser instanceof BDECustomerModel && BooleanUtils.isTrue(orderModel.getBdeOrder())) {
					this.customerAccountService.sendOrderConfirmationEmail(orderModel);
				} else if (currentUser instanceof B2BCustomerModel)
				{
					if (notificationService.getEmailPreferenceForNotificationType(NotificationType.ORDER_CONFIRMATION,
							(B2BCustomerModel) currentUser, ((B2BCustomerModel) currentUser).getDefaultB2BUnit()))
					{
						this.customerAccountService.sendOrderConfirmationEmail(orderModel);
					}
				}
			}
			//Payment Capture Start
			if(asahiSiteUtil.isApb())
			{
				this.asahiPaymentCaptureRequestService.createCaptureRequest(orderModel);
			}

			orderDetails = getOrderConverter().convert(orderModel);
		}
		return orderDetails;
	}

	/**
	 * This method will fetch the order details based on order code.
	 *
	 * @param code
	 * @return
	 */
	@Override
	public OrderModel getOrderDetails(final String code)
	{
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();

		OrderModel orderModel = null;
		if (getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			orderModel = getCustomerAccountService().getOrderDetailsForGUID(code, baseStoreModel);
		}
		else
		{
			orderModel = this.customerAccountService.getOrderForCode(getCurrentB2BUnit(), code, baseStoreModel);
		}

		if (orderModel == null)
		{
			throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND, code));
		}
		return orderModel;
	}

	/**
	 * This method will send order to Backend system.
	 *
	 * @param orderModel
	 */
	@Override
	public void sendOrderToBackendSystem(final OrderModel orderModel)
	{
		this.asahiSendOrderToBackenedService.sendOrderToBackendSystem(orderModel);
	}

	/**
	 * get the data of the quick order
	 */
	@Override
	public AsahiQuickOrderData getQuickOrders(final String sortCode)
	{
		AsahiQuickOrderData quickOrderData = null;
		List<OrderModel> orderEntry = null;
		final AsahiB2BUnitModel b2bUnitModel = this.getCurrentB2BUnit();

		if (null != b2bUnitModel)
		{
			orderEntry = this.asahiOrderService.getOrderEntriesForUser(b2bUnitModel);
		}

		if (CollectionUtils.isNotEmpty(orderEntry))
		{
			quickOrderData = getAsahiQuickOrderConverter().convert(orderEntry);
			inclusionExclusionProductStrategy.updateQuickOrderData(quickOrderData);
			if (CollectionUtils.isNotEmpty(quickOrderData.getEntries()))
			{
				Collections.sort(quickOrderData.getEntries(), new Comparator<AsahiQuickOrderEntryData>()
				{
					@Override
					public int compare(final AsahiQuickOrderEntryData quickOrderEntry1,
							final AsahiQuickOrderEntryData quickOrderEntry2)
					{
						if ("name".equalsIgnoreCase(sortCode))
						{
							return Objects.toString(quickOrderEntry1.getBrand(), "")
									.concat(Objects.toString(quickOrderEntry1.getName(), ""))
									.compareTo(Objects.toString(quickOrderEntry2.getBrand(), "")
											.concat(Objects.toString(quickOrderEntry2.getName(), "")));
						}
						else
						{
							return quickOrderEntry2.getLastOrderedDate().compareTo(quickOrderEntry1.getLastOrderedDate());
						}
					}
				});
			}

			final Map<String, Boolean> dateRangeExcludeMap = new HashMap<>();

			for (final String dateStr : quickOrderData.getDateRange())
			{
				boolean isExclude = true;
				final List<AsahiQuickOrderEntryData> asahiQuickOrderEntryDatas = quickOrderData.getEntries();
				if (CollectionUtils.isNotEmpty(asahiQuickOrderEntryDatas)) {
					for (final AsahiQuickOrderEntryData asahiQuickOrderEntryData : asahiQuickOrderEntryDatas)
					{
						final Map<String, Long> dateRangeEntryMap = asahiQuickOrderEntryData.getDateRange();
						if (null != dateRangeEntryMap && dateRangeEntryMap.containsKey(dateStr)
								&& !asahiQuickOrderEntryData.getIsExcluded())
						{
							isExclude = false;
							break;
						}
					}
				}
				dateRangeExcludeMap.put(dateStr, isExclude);
			}
			quickOrderData.setDateRangeExcluded(dateRangeExcludeMap);
		}
		return quickOrderData;
	}


	private <S, T> de.hybris.platform.core.servicelayer.data.SearchPageData<T> convertPaginationData(final de.hybris.platform.core.servicelayer.data.SearchPageData<S> source, final Converter<S, T> converter)
	{
		final de.hybris.platform.core.servicelayer.data.SearchPageData<T> result = new de.hybris.platform.core.servicelayer.data.SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}
}

