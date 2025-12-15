/**
 *
 */
package com.sabmiller.core.order.impl;

import com.sabmiller.core.model.*;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BOrderService;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.strategies.CreateOrderFromCartStrategy;
import de.hybris.platform.order.strategies.SubmitOrderStrategy;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantProductModel;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.core.util.AsahiCoreUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.b2b.services.CUBStockInformationService;
import com.sabmiller.core.b2b.services.SabmB2BEmployeeService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsCacheService;
import com.sabmiller.core.enums.CUBStockStatus;
import com.sabmiller.core.order.SabmB2BOrderService;
import com.sabmiller.core.order.dao.SabmOrderDao;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.core.util.SABMFormatterUtils;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.UOMUtils;
import com.sabmiller.facades.smartOrders.json.SmartOrdersJson;
import com.sabmiller.facades.smartOrders.json.SmartOrdersProductsHistoryJson;
import com.sabmiller.facades.smartOrders.json.SmartOrdersProductsJson;


/**
 * @author joshua.a.antony
 *
 */
@SuppressWarnings("serial")
public class DefaultSabmB2BOrderService extends DefaultB2BOrderService implements SabmB2BOrderService
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmB2BOrderService.class.getName());
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	@Resource(name = "orderDao")
	private SabmOrderDao orderDao;

	@Resource(name = "productService")
	private SabmProductService productService;

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;
	@Resource(name = "sabFormatterUtil")
	private SABMFormatterUtils sabFormatterUtil;

	@Resource(name = "productModelUrlResolver")
	private UrlResolver<ProductModel> productModelUrlResolver;

	@Resource(name = "dealsCacheService")
	private DealsCacheService dealsCacheService;

	@Resource
	private CUBStockInformationService cubStockInformationService;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;
	
	@Resource(name = "sabmB2BEmployeeService")
	private SabmB2BEmployeeService sabmB2BEmployeeService;
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	/**
	 * @return the sabFormatterUtil
	 */
	public SABMFormatterUtils getSabFormatterUtil()
	{
		return sabFormatterUtil;
	}

	/**
	 * @param sabFormatterUtil
	 *           the sabFormatterUtil to set
	 */
	public void setSabFormatterUtil(final SABMFormatterUtils sabFormatterUtil)
	{
		this.sabFormatterUtil = sabFormatterUtil;
	}

	/**
	 * @return the sessionService
	 */
	@Override
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Override
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected UrlResolver<ProductModel> getProductModelUrlResolver()
	{
		return productModelUrlResolver;
	}

	/**
	 * @param productModelUrlResolver
	 *           the productModelUrlResolver to set
	 */
	public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver)
	{
		this.productModelUrlResolver = productModelUrlResolver;
	}

	@Override
	public OrderModel createOrderFromCart(final CartModel cart) throws InvalidCartException
	{
		if(asahiSiteUtil.isCub())
		{
   		//Fix for sap order number null while create order.
   		if (cart != null && StringUtils.isNotBlank(cart.getSapSalesOrderNumber()))
   		{
   
   			final OrderModel orderModel = super.createOrderFromCart(cart);
   			orderModel.setStatus(OrderStatus.CREATED);
   			//SABMC- 900
   			if (null != sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_IMPERSONATE_PA))
   			{
   
   				final UserModel pAUser = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_IMPERSONATE_PA);
   				orderModel.setPlacedBy(pAUser);
   				orderModel.setUserDisplayName(pAUser.getName());
   			}
   			else if (cart.getUser() instanceof BDECustomerModel)
   			{
   				final EmployeeModel bde = sabmB2BEmployeeService.searchBDEByName(cart.getUser().getName());
   
   				orderModel.setPlacedBy(bde);
   				orderModel.setUserDisplayName(bde.getName() + " - CUB");
   			}
   			else
   			{
   				orderModel.setPlacedBy(cart.getUser());
   				orderModel.setUserDisplayName(cart.getUser().getName());
   			}
   			return orderModel;
   		}
   		else
   		{
   			//throw new InvalidCartException(this.getClass().getName() + ":createOrderFromCart:: Invalidate Cart Exception");
   			LOG.info(this.getClass().getName() + ":createOrderFromCart:: Sap order number is null");
   			return null;
   		}
		}
		else
		{
			return super.createOrderFromCart(cart);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.order.SabmB2BOrderService#getOrderBySapSalesOrderNumber(java.lang.String)
	 */
	@Override
	public OrderModel getOrderBySapSalesOrderNumber(final String sapSalesOrderNumber)
	{
		return orderDao.getOrderBySapSalesOrderNumber(sapSalesOrderNumber);
	}

	@Override
	public ConsignmentModel lookupConsignment(final OrderModel orderModel, final ConsignmentStatus consignmentStatus)
	{
		LOG.debug("Looking up consignments for Order : {} and consignment status : {} ", orderModel, consignmentStatus);
		if (consignmentStatus != null)
		{
			for (final ConsignmentModel consModel : SetUtils.emptyIfNull(orderModel.getConsignments()))
			{
				if (consignmentStatus.equals(consModel.getStatus()))
				{
					return consModel;
				}
			}
		}
		return null;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.order.SabmB2BOrderService#getTopOrderByB2BUnit(de.hybris.platform.b2b.model.B2BUnitModel)
	 */
	@Override
	public List<OrderModel> getOrderByB2BUnit(final B2BUnitModel b2bUnitModel)
	{
		return orderDao.getOrderByB2BUnit(b2bUnitModel);
	}

	@Override
	public List<OrderModel> getTopOrder(int limit) {
		return orderDao.getTopOrder(limit);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.order.SabmB2BOrderService#lookupConsignment(de.hybris.platform.core.model.order.OrderModel,
	 * java.lang.String)
	 */
	@Override
	public ConsignmentModel lookupConsignment(final OrderModel orderModel, final String deliveryNumber)
	{
		LOG.debug("Looking up consignments for Order : {} and consignment code : {} ", orderModel, deliveryNumber);
		if (deliveryNumber != null)
		{
			for (final ConsignmentModel consModel : SetUtils.emptyIfNull(orderModel.getConsignments()))
			{
				if (deliveryNumber.equals(consModel.getCode()))
				{
					return consModel;
				}
			}
		}
		return null;
	}

	@Override
	public AbstractOrderEntryModel lookupOrderEntry(final OrderModel orderModel, final String material, final String lineNumber)
	{
		LOG.debug("Looking up for AbstractOrderEntryModel for Order : " + orderModel.getCode() + " and Material : " + material);

		final List<AbstractOrderEntryModel> orderEntries = fetchOrderEntries(orderModel, material);

		if (orderEntries.size() == 1)
		{
			return orderEntries.get(0);
		}
		if (orderEntries.size() > 1)
		{
			LOG.debug("There are total {} order entries for material {} ", orderEntries.size(), material);
			//Free goods condition, since thats the only case when we will have the same material number across different lines
			for (final AbstractOrderEntryModel entryModel : orderEntries)
			{
				if (lineNumber != null && entryModel.getSapLineNumber() != null
						&& Integer.valueOf(lineNumber).equals(Integer.valueOf(entryModel.getSapLineNumber())))
				{
					LOG.debug("Found match against line Number {} ", lineNumber);
					return entryModel;
				}
			}
			//No match found based on line number, just return the first entry
			return orderEntries.get(0);
		}
		LOG.error("Order entry not found for Order : {}  , Material : {} . Returning null", orderModel.getCode(), material);

		return null;
	}

	@Override
	public List<OrderModel> getB2BUnitOrdersByDeliveryDate(final B2BUnitModel parentUnit, final Date requestedDeliveryDate)
	{
		return orderDao.getB2BUnitOrdersByDeliveryDate(parentUnit, requestedDeliveryDate);
	}


	@Override
	public List<OrderModel> getOrdersByOrderStatus(final OrderStatus orderStatus)
	{
		return orderDao.getOrdersByOrderStatus(orderStatus);
	}

	@Override
	public List<OrderModel> getOrderByB2BUnit(final B2BUnitModel parentUnit, final Date dateFrom, final Date dateTo)
	{
		return orderDao.getOrderByB2BUnit(parentUnit, dateFrom, dateTo);
	}

	private List<AbstractOrderEntryModel> fetchOrderEntries(final OrderModel orderModel, final String material)
	{
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<AbstractOrderEntryModel>();
		for (final AbstractOrderEntryModel entryModel : ListUtils.emptyIfNull(orderModel.getEntries()))
		{
			LOG.debug("Comparing Material : " + material + " with the code stored in the AbstractOrderEntry "
					+ entryModel.getProduct().getCode());
			if (material.trim().equals(entryModel.getProduct().getCode().trim()))
			{
				orderEntries.add(entryModel);
			}
		}
		return orderEntries;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.order.SabmB2BOrderService#getOrderForConsignment(java.lang.String)
	 */
	@Override
	public OrderModel getOrderByConsignment(final String trackingId)
	{
		return orderDao.getOrderByConsignment(trackingId);
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.order.SabmB2BOrderService#getPagedOrdersByB2BUnit(de.hybris.platform.b2b.model.B2BUnitModel,
	 * int, java.util.Date)
	 */
	@Override
	public SmartOrdersJson getPagedOrdersByB2BUnit(final B2BUnitModel b2bUnitModel, final int page, final String date,
			final String sort)
	{
		final SmartOrdersJson smartOrdersJson = new SmartOrdersJson();
		final List<OrderModel> pagedOrdersByB2BUnit = new ArrayList<>();
		getOrdersToDisplay(b2bUnitModel, page, date, sort, smartOrdersJson, pagedOrdersByB2BUnit);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		final List<OrderModel> orders = new ArrayList<>();
		final List<String> dates = new ArrayList<>();
		final List<ProductModel> productCodes = new ArrayList<>();
		final Map<String, AbstractOrderEntryModel> uniqueProductModelMap = new HashMap<String, AbstractOrderEntryModel>();

		final Map<AbstractOrderEntryModel, List<SmartOrdersProductsHistoryJson>> historyjson = new HashMap<AbstractOrderEntryModel, List<SmartOrdersProductsHistoryJson>>();
		int size = pagedOrdersByB2BUnit.size();

		final List<SmartOrdersProductsJson> productsJson = new ArrayList<>();

		while (size < 6)
		{
			pagedOrdersByB2BUnit.add(size, null);
			size = pagedOrdersByB2BUnit.size();
		}
		Collections.reverse(pagedOrdersByB2BUnit);
		for (final OrderModel orderModel : pagedOrdersByB2BUnit)
		{
			if (null == orderModel)
			{
				dates.add("N/A");
				orders.add(null);
			}
			else
			{
				orders.add(orderModel);
				CollectionUtils.addAll(entries, orderModel.getEntries());
				dates.add(formatDate(orderModel.getCreationtime()));
			}

		}

		smartOrdersJson.setDates(dates);

		for (final AbstractOrderEntryModel entry : entries)
		{
			final SABMAlcoholVariantProductEANModel eanProduct = extractEANfromProduct(entry.getProduct());

			if (eanProduct != null)
			{
				if (!uniqueProductModelMap.containsKey(eanProduct.getCode() + "_" + entry.getUnit().getCode()))
				{
					uniqueProductModelMap.put(eanProduct.getCode() + "_" + entry.getUnit().getCode(), entry);
					productCodes.add(eanProduct);
				}
			}
		}

		convertHistoryJson(entries, orders, uniqueProductModelMap, historyjson);
		convertSmartOrdersProductsJson(historyjson, productsJson);
		sortSmartOrderProducts(productsJson);
		smartOrdersJson.setProducts(productsJson);
		sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_SMARTORDERJSON, smartOrdersJson);
		sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_SMARTORDERPRODUCTCODES, productCodes);
		return smartOrdersJson;
	}

	/**
	 * @param b2bUnitModel
	 * @param page
	 * @param date
	 * @param sort
	 * @param smartOrdersJson
	 * @param pagedOrdersByB2BUnit
	 */
	private void getOrdersToDisplay(final B2BUnitModel b2bUnitModel, final int page, final String date, final String sort,
			final SmartOrdersJson smartOrdersJson, final List<OrderModel> pagedOrdersByB2BUnit)
	{
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		Date date1 = sabFormatterUtil.parseDate(date, DATE_FORMAT);
		Date date2 = null;
		Date date3 = new Date();
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date3);
		calendar.add(Calendar.YEAR, -1);
		date3 = calendar.getTime();

		if (date1 != null && StringUtils.equals(sort, "d"))
		{
			final Calendar cal = Calendar.getInstance();
			cal.setTime(date1);
			cal.add(Calendar.HOUR_OF_DAY, 23);
			cal.add(Calendar.MINUTE, 59);
			cal.add(Calendar.SECOND, 59);
			date1 = cal.getTime();
			date2 = date1;
		}
		if (null != sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_PREVIOUSSMARTORDERDATE)
				&& null != sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_NEXTSMARTORDERDATE)
				&& (StringUtils.equals(sort, "n") || StringUtils.equals(sort, "p")))
		{
			date1 = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_PREVIOUSSMARTORDERDATE);
			date2 = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_NEXTSMARTORDERDATE);
		}

		final List<OrderModel> oneYearOldOrdersByB2BUnit = new ArrayList<>();
		oneYearOldOrdersByB2BUnit.addAll(orderDao.getPreviousPagedOrdersByB2BUnit(b2bUnitModel, page, date3));

		final List<OrderModel> previousOrdersByB2BUnit = new ArrayList<>();
		previousOrdersByB2BUnit.addAll(orderDao.getPreviousPagedOrdersByB2BUnit(b2bUnitModel, page, date1));
		final List<OrderModel> nextOrdersByB2BUnit = new ArrayList<>();
		if (date2 != null)
		{
			nextOrdersByB2BUnit.addAll(orderDao.getNextPagedOrdersByB2BUnit(b2bUnitModel, page, date2));
		}

		smartOrdersJson.setPreviousOrdersLink(previousOrdersByB2BUnit.size() == 7);
		smartOrdersJson.setNextOrdersLink(nextOrdersByB2BUnit.size() > 0);
		smartOrdersJson.setSeeThisTimeLastYearLink(oneYearOldOrdersByB2BUnit.size() > 0);
		if (StringUtils.equals(sort, "d"))
		{
			if (previousOrdersByB2BUnit.size() == 7)
			{
				previousOrdersByB2BUnit.remove(6);

			}
			pagedOrdersByB2BUnit.addAll(previousOrdersByB2BUnit);
		}
		else if (StringUtils.equals(sort, "n"))
		{
			if (nextOrdersByB2BUnit.size() <= 6)
			{
				nextOrdersByB2BUnit.clear();
				nextOrdersByB2BUnit.addAll(orderDao.getTotalPagedOrdersByB2BUnit(b2bUnitModel, page, "DESC"));
				smartOrdersJson.setNextOrdersLink(false);
				if (nextOrdersByB2BUnit.size() == 7)
				{
					smartOrdersJson.setPreviousOrdersLink(true);
					nextOrdersByB2BUnit.remove(6);
				}
			}
			else
			{
				nextOrdersByB2BUnit.remove(6);
				Collections.reverse(nextOrdersByB2BUnit);
				smartOrdersJson.setPreviousOrdersLink(true);
			}
			pagedOrdersByB2BUnit.addAll(nextOrdersByB2BUnit);
		}
		else if (StringUtils.equals(sort, "p"))
		{
			if (previousOrdersByB2BUnit.size() <= 6)
			{
				previousOrdersByB2BUnit.clear();
				previousOrdersByB2BUnit.addAll(orderDao.getTotalPagedOrdersByB2BUnit(b2bUnitModel, page, "ASC"));
				if (previousOrdersByB2BUnit.size() == 7)
				{
					previousOrdersByB2BUnit.remove(6);
					smartOrdersJson.setNextOrdersLink(true);
				}
				Collections.reverse(previousOrdersByB2BUnit);
				smartOrdersJson.setPreviousOrdersLink(false);
			}
			else
			{
				previousOrdersByB2BUnit.remove(6);
				smartOrdersJson.setNextOrdersLink(true);
			}

			pagedOrdersByB2BUnit.addAll(previousOrdersByB2BUnit);
		}
		else
		{
			if (previousOrdersByB2BUnit.size() == 7)
			{
				previousOrdersByB2BUnit.remove(6);
			}
			pagedOrdersByB2BUnit.addAll(previousOrdersByB2BUnit);
		}
		if (pagedOrdersByB2BUnit.size() > 0)
		{
			final Date firstOrderDatecreationtime = pagedOrdersByB2BUnit.get(pagedOrdersByB2BUnit.size() - 1).getCreationtime();
			final Date lastOrderDatecreationtime = pagedOrdersByB2BUnit.get(0).getCreationtime();
			smartOrdersJson.setDate(dateFormat.format(firstOrderDatecreationtime));
			sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_PREVIOUSSMARTORDERDATE, firstOrderDatecreationtime);
			sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_NEXTSMARTORDERDATE, lastOrderDatecreationtime);
		}
	}


	/**
	 * @param variant
	 * @return
	 */
	private SABMAlcoholVariantProductEANModel extractEANfromProduct(final ProductModel variant)
	{
		SABMAlcoholVariantProductEANModel eanProduct = null;
		if (variant instanceof VariantProductModel)
		{
			if (variant instanceof SABMAlcoholVariantProductMaterialModel)
			{
				eanProduct = (SABMAlcoholVariantProductEANModel) ((SABMAlcoholVariantProductMaterialModel) variant).getBaseProduct();
			}
		}
		return eanProduct;
	}

	/**
	 * @param variant
	 * @return
	 */
	private SABMAlcoholVariantProductEANModel searchEANfromProduct(final ProductModel variant)
	{
		if (null != variant)
		{
			//Searching the product with flexible search to apply all the personalizations.
			ProductModel eanProduct = productService.getProductForCodeSafe(variant.getCode());

			//Getting the
			while (eanProduct instanceof VariantProductModel)
			{
				if (eanProduct.getClass().getName().equals(SABMAlcoholVariantProductEANModel.class.getName()))
				{
					return (SABMAlcoholVariantProductEANModel) eanProduct;
				}
				//Searching the product with flexible search to apply all the personalizations.
				eanProduct = productService.getProductForCodeSafe(((VariantProductModel) eanProduct).getBaseProduct().getCode());
			}
		}
		return null;
	}


	/**
	 * @param entries
	 * @param orders
	 * @param uniqueProductModelMap
	 * @param historyjson
	 */
	private void convertHistoryJson(final List<AbstractOrderEntryModel> entries, final List<OrderModel> orders,
			final Map<String, AbstractOrderEntryModel> uniqueProductModelMap,
			final Map<AbstractOrderEntryModel, List<SmartOrdersProductsHistoryJson>> historyjson)
	{
		for (final Map.Entry<String, AbstractOrderEntryModel> entryMap : uniqueProductModelMap.entrySet())
		{
			final ProductModel model = entryMap.getValue().getProduct();
			final SABMAlcoholVariantProductEANModel eanProduct1 = extractEANfromProduct(model);
			final List<SmartOrdersProductsHistoryJson> listjson = new ArrayList<>();

			for (final OrderModel order : orders)
			{
				final SmartOrdersProductsHistoryJson historyjson1 = new SmartOrdersProductsHistoryJson();
				if (null == order)
				{
					historyjson1.setDate("N/A");
					historyjson1.setQty(0);
				}
				else
				{
					historyjson1.setDate(formatDate(order.getDate()));
					historyjson1.setQty(0);
					for (final AbstractOrderEntryModel entry : entries)
					{
						final SABMAlcoholVariantProductEANModel eanProduct2 = extractEANfromProduct(entry.getProduct());

						if (entry.getOrder().equals(order) && eanProduct2.equals(eanProduct1)
								&& (eanProduct2.getCode() + "_" + entry.getUnit().getCode()).equalsIgnoreCase(entryMap.getKey()))
						{
							historyjson1.setDate(formatDate(entry.getOrder().getDate()));
							if (order.getStatus().equals(OrderStatus.RETURNED))
							{
								historyjson1.setQty(-entry.getQuantity().intValue());
							}
							else
							{
								historyjson1.setQty(entry.getQuantity().intValue());
							}
							break;
						}
					}
				}
				listjson.add(historyjson1);
			}
			historyjson.put(entryMap.getValue(), listjson);
		}
	}

	/**
	 * @param historyjson
	 * @param productsJson
	 */
	private void convertSmartOrdersProductsJson(
			final Map<AbstractOrderEntryModel, List<SmartOrdersProductsHistoryJson>> historyjson,
			final List<SmartOrdersProductsJson> productsJson)
	{
		for (final Map.Entry<AbstractOrderEntryModel, List<SmartOrdersProductsHistoryJson>> entry : historyjson.entrySet())
		{
			final SmartOrdersProductsJson smartOrdersProductsJson = new SmartOrdersProductsJson();
			smartOrdersProductsJson.setHistory(entry.getValue());
			smartOrdersProductsJson.setCode(entry.getKey().getProduct().getCode());
			final ProductModel variant = entry.getKey().getProduct();
			final SABMAlcoholVariantProductEANModel eanProduct = searchEANfromProduct(variant);
			//Populating Stock Status
			try
			{
				populateStockStatus(entry.getKey().getProduct().getCode(), smartOrdersProductsJson);
			}
			catch (final Exception e)
			{
				LOG.debug("Unable to fetch Stock status for product %s due to", e);
			}

			if (eanProduct != null && eanProduct.getPurchasable())
			{
				smartOrdersProductsJson.setCode(eanProduct.getCode());
				smartOrdersProductsJson.setBrand("");
				if (eanProduct.getBaseProduct() != null)
				{
					final SABMAlcoholProductModel alcoholProduct = (SABMAlcoholProductModel) eanProduct.getBaseProduct();
					if (alcoholProduct.getBrand() != null)
					{
						smartOrdersProductsJson.setBrand(alcoholProduct.getBrand());
					}
				}
				smartOrdersProductsJson.setNewProductFlag(BooleanUtils.isTrue(eanProduct.getIsNewProduct()));
				if (Config.getBoolean("show.deal.titles", true))
				{
					/*
					 * smartOrdersProductsJson.setDealsFlag(dealsCacheService.getDealsFlag(eanProduct.getCode())); if
					 * (smartOrdersProductsJson.isDealsFlag()) { smartOrdersProductsJson
					 * .setDealsTitle(dealsCacheService.getDealTitlesForProduct(smartOrdersProductsJson.getCode())); }
					 */
					smartOrdersProductsJson.setDealsFlag(false);

					if(!asahiCoreUtil.isNAPUserForSite())
					{

						final List<String> deals = getDealTitlesForEanProduct(smartOrdersProductsJson.getCode());
						smartOrdersProductsJson.setDealsFlag(false);
						if (CollectionUtils.isNotEmpty(deals))
						{
							smartOrdersProductsJson.setDealsFlag(true);
							smartOrdersProductsJson.setDealsTitle(deals);
						}
					}
				}
				final String url = getProductModelUrlResolver().resolve(eanProduct);
				if (StringUtils.isNotEmpty(url))
				{
					smartOrdersProductsJson.setUrl(url);
				}

				if (StringUtils.isNotEmpty(eanProduct.getSellingName()) && StringUtils.isNotEmpty(eanProduct.getPackConfiguration()))
				{
					smartOrdersProductsJson.setTitle(eanProduct.getSellingName());
					smartOrdersProductsJson.setPackConfig(eanProduct.getPackConfiguration());
				}
				else
				{
					smartOrdersProductsJson.setTitle(eanProduct.getName());
				}

				if (null != eanProduct.getThumbnail())
				{
					smartOrdersProductsJson.setImage(eanProduct.getThumbnail().getURL());
				}
				double sum = 0, size1 = 0;
				for (final SmartOrdersProductsHistoryJson value1 : entry.getValue())
				{
					if (null != value1.getQty() && value1.getQty() > 0 && !value1.getDate().contains("N/A"))
					{
						sum = sum + value1.getQty();
						size1++;
					}
				}
				Integer avgQty = Integer.valueOf((int) Math.round(sum / size1));
				if (avgQty < 0)
				{
					avgQty = 0;
				}
				smartOrdersProductsJson.setQty(calculateSuggestedQtyUom(avgQty, eanProduct));
				smartOrdersProductsJson.setNumberOfTimesOrdered((int) size1);

				final Set<UnitModel> unitModels = eanProduct.getUnitList();
				if (CollectionUtils.isNotEmpty(unitModels))
				{
					smartOrdersProductsJson.setUomList(UOMUtils.getUomList(unitModels));
				}

				productsJson.add(smartOrdersProductsJson);
			}
		}
	}

	/**
	 * Returns suggested quantity based on Layer/Pallet to Case mappings Layer Logic : when qty >=85% and <=100% of layer
	 * round up to layer Pallet Logic : if suggested qty is > than last layer in pallet then round up to a pallet
	 *
	 * @param originalQty
	 * @param eanProduct
	 * @return
	 */
	public int calculateSuggestedQtyUom(final int originalQty, final SABMAlcoholVariantProductEANModel eanProduct)
	{

		LOG.debug("getFinalQty START -> originalQty : " + originalQty);

		int finalQty = originalQty;
		if (Arrays.asList(new String[]
		{ SabmCoreConstants.CASE_UOM_CODE, SabmCoreConstants.CASE_UOM_ALTERNATE_CODE1, SabmCoreConstants.CASE_UOM_ALTERNATE_CODE2 })
				.contains(eanProduct.getUnit().getCode()))
		{
			double layer = 0;
			double pallet = 0;


			for (final ProductUOMMappingModel uomMappingModel : eanProduct.getUomMappings())
			{
				if (unitService.isValid(uomMappingModel))
				{
					final String code = uomMappingModel.getFromUnit().getCode();
					if (StringUtils.equalsIgnoreCase(SabmCoreConstants.LAYER_UOM_CODE, code))
					{
						layer = uomMappingModel.getQtyConversion();
					}
					else if (StringUtils.equalsIgnoreCase(SabmCoreConstants.PALLET_UOM_CODE, code))

					{
						pallet = uomMappingModel.getQtyConversion();
					}
				}
			}

			final double roundedPallet = pallet * (Math.ceil(originalQty / pallet));

			if (originalQty >= roundedPallet - layer + 1 && originalQty <= roundedPallet)
			{
				finalQty = (int) roundedPallet;
			}
			else
			{
				final double layerOnlyQty = originalQty % pallet;
				final double roundedLayer = layer * (Math.ceil(layerOnlyQty / layer));

				final double unit85 = roundedLayer * 0.85;

				if (layerOnlyQty >= unit85 && layerOnlyQty <= roundedLayer)
				{
					finalQty = (int) (originalQty - layerOnlyQty + roundedLayer);
				}
				else
				{
					finalQty = originalQty;
				}
			}

			LOG.debug("getFinalQty END -> finalQty : " + finalQty);

		}

		return finalQty;
	}

	/**
	 * @param productsJson
	 */
	private void sortSmartOrderProducts(final List<SmartOrdersProductsJson> productsJson)
	{
		Collections.sort(productsJson, new Comparator<SmartOrdersProductsJson>()
		{
			@Override
			public int compare(final SmartOrdersProductsJson o1, final SmartOrdersProductsJson o2)
			{
				return o2.getQty().compareTo(o1.getQty());
			}
		});
	}

	/**
	 * @param date
	 *
	 */
	private String formatDate(final Date date)
	{
		final SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
		return df2.format(date);
	}

	/**
	 * Fetches the number of times the provided Product EAN has been ordered on Hybris by the given sub channel. This
	 * method takes all orders into consideration (phone/online).
	 *
	 * @param subChannel
	 *           the b2b unit's sub channel (Pub, Bar, Restaurant) to use when doing the count.
	 * @param productEAN
	 *           the product EAN to evaluate when getting the count.
	 *
	 * @return number of times product EAN has been ordered on Hybris with the given criteria.
	 */
	public int getProductOrderCountBySubChannelAndEAN(final String subChannel, final SABMAlcoholVariantProductEANModel productEAN)
	{
		int numberOfTimeEANWasOrdered = 0;

		final DateTime present = new DateTime();
		final DateTime past = present.minusMonths(SabmCoreConstants.BESTSELLER_MAX_CONSIDERATiON_TIME_IN_MONTHS);

		final List<OrderEntryModel> orderEntryModels = (List) orderDao.getProductOrderCountBySubChannelAndEAN(subChannel,
				productEAN, DateUtils.truncate(past.toDate(),Calendar.DATE)); //truncate date to exclude minutes hours seconds ms etc for good caching.

		if (CollectionUtils.isEmpty(orderEntryModels))
		{
			return 0;
		}

		for (final OrderEntryModel orderEntry : orderEntryModels)
		{
			numberOfTimeEANWasOrdered += orderEntry.getQuantity();
		}

		return numberOfTimeEANWasOrdered;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.order.SabmB2BOrderService#getLastOrderByCustomer(java.lang.String)
	 */
	@Override
	public OrderModel getLastOrderByCustomer(final UserModel user)
	{
		return orderDao.getLastOrderByCustomer(user);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.order.SabmB2BOrderService#getFirstOnlineOrderByCustomer(java.lang.String)
	 */
	@Override
	public OrderModel getFirstOnlineOrderByCustomer(final UserModel user)
	{
		return orderDao.getFirstOnlineOrderByCustomer(user);
	}


	@Override
	public Set<OrderModel> getB2BUnitOrdersTimePassesETA(
			final ConsignmentStatus consignmentStatus)
	{
		final List<ConsignmentModel> consignments = orderDao.getB2BUnitOrdersTimePassesETA(consignmentStatus);
		LOG.debug("B2BUnitOrdersTimePassesETA consignments", CollectionUtils.size(consignments));

		final Set<OrderModel> orders = new HashSet<>();
		for (final ConsignmentModel consignment : consignments)
		{
			final Date roundedDate = SabmDateUtils.plusMinutes(
					SabmDateUtils.roundDateToNearestQuarterHour(consignment.getEstimatedArrivedTime()),
					Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes", "")));

			LOG.debug("B2BUnitOrdersTimePassesETA consignment.getEstimatedArrivedTime() {} , roundedDate {}",
					consignment.getEstimatedArrivedTime(), roundedDate);


			if (new Date().after(roundedDate))
			{
				orders.add((OrderModel) consignment.getOrder());
			}
		}
		LOG.debug("B2BUnitOrdersTimePassesETA orders", CollectionUtils.size(orders));

		return orders;
	}

	private void populateStockStatus(final String productSKU, final SmartOrdersProductsJson target)
	{
		try
		{
			final B2BUnitModel parentB2bUnit = b2bCommerceUnitService.getParentUnit();
			final PlantModel plant = parentB2bUnit.getPlant();
			if (plant != null)
			{
				final CUBStockInformationModel cubStockInformationModel = cubStockInformationService
						.getCUBStockInformationForProductAndPlant(productSKU, plant);

				if (cubStockInformationModel != null)
				{
					setStockStatus(cubStockInformationModel, target);
				}
			}
		}
		catch (final ModelNotFoundException e)
		{
			LOG.warn("Model not found for EAN product or Plant:");
		}
		catch (final Exception e)
		{
			LOG.warn(e.getMessage());
		}
	}

	private void setStockStatus(final CUBStockInformationModel cubStockInformationModel, final SmartOrdersProductsJson target)
	{
		if (cubStockInformationModel != null)
		{
			if (cubStockInformationModel.getStockStatus().equals(CUBStockStatus.OUTOFSTOCK))
			{
				if (sabmConfigurationService.isLowStockFlagEnforced())
				{
					target.setCubStockStatus(StockLevelStatus.LOWSTOCK);
				}
				else
				{
					target.setCubStockStatus(StockLevelStatus.OUTOFSTOCK);
				}
			}
			else if (cubStockInformationModel.getStockStatus().equals(CUBStockStatus.LOWSTOCK))
			{
				target.setCubStockStatus(StockLevelStatus.LOWSTOCK);
			}
		}
	}

	private List<String> getDealTitlesForEanProduct(final String eanProduct)
	{
		String productSku = null;
		List<String> deals = null;

		try
		{
			productSku = productService.getMaterialCodeFromEan(eanProduct);
		}
		catch (final Exception e)
		{
			LOG.info("exception while fetching sku from EAN");
		}
		try
		{
			if (productSku != null)
			{
				deals = dealsCacheService.getDealTitlesForProduct(productSku);
			}
		}
		catch (final Exception e)
		{
			LOG.warn("exception while fetching deals for product sku");
		}

		return ListUtils.emptyIfNull(deals);
	}

	/**
	 * Gets all orders which were "Paid" or "Partly Paid" using "Credit Card" for customers on "P1" or "P2" AutoPay membership status
	 *
	 *  @return List<OrderModel>
	 */
	@Override
	public List<OrderModel> getOrdersByCreditCardPayment() {
		return orderDao.getOrdersByCreditCardPayment();
	}

	/**
	 * Retrieves all orders whose create date is less than or equal than specified date
	 *
	 * @param endDate
	 */
	@Override
    public List<OrderModel> getOrdersToDate(final Date endDate, final int limit) {
		return orderDao.getOrdersToDate(endDate, limit);
	}
	
	/* (non-Javadoc)
	 * @see com.sabmiller.core.order.SabmB2BOrderService#getOrderByCreatedDate(java.lang.String)
	 */
	@Override	
	public OrderModel getOrderByCartCode(final String cartCode)
	{
		// YTODO Auto-generated method stub
		return orderDao.getOrderByCartCode(cartCode);
	}
	
}
