/**
 *
 */
package com.sabmiller.core.order.dao;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.daos.impl.DefaultOrderDao;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.AutoPayStatus;
import com.sabmiller.core.enums.OrderType;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.B2BUnitGroupModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;



/**
 * @author joshua.a.antony
 *
 */
public class DefaultSabmOrderDao extends DefaultOrderDao implements SabmOrderDao
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmOrderDao.class);

	private static final String FIND_ORDER_BY_SAP_ORDER_NUMBER = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ "} WHERE {" + OrderModel.SAPSALESORDERNUMBER + "} = ?sapSalesOrderNumber AND {" + OrderModel.VERSIONID + "} IS NULL"
			+ " ORDER BY {" + OrderModel.CREATIONTIME + "} DESC";

	private static final String FIND_ORDERS_BY_B2BUNIT = "SELECT {" + OrderModel.PK + "}, {" + OrderModel.CREATIONTIME + "}, {"
			+ OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.UNIT + "} = ?b2bUnit AND {"
			+ OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STATUS + "} IS NOT NULL"+ " ORDER BY {" + OrderModel.CREATIONTIME + "} DESC";

	private static final String FIND_TOP_ORDERS = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE + "!} WHERE {"
			+ OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STATUS + "} IS NOT NULL"+ " ORDER BY {" + OrderModel.DATE + "} DESC";

	private static final String FIND_ORDERS_BY_B2BUNIT_AND_REQUESTEDDELIVERYDATE = "SELECT {" + OrderModel.PK + "}, {"
			+ OrderModel.CREATIONTIME + "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.UNIT
			+ "} = ?b2bUnit AND {" + OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STATUS + "} IS NOT NULL AND { "+ OrderModel.REQUESTEDDELIVERYDATE
			+ "} = ?requestedDeliveryDate";

	private static final String FIND_DISPATCH_EMAIL_NOT_SENT_ORDERS = "SELECT {" + OrderModel.PK + "} " + "FROM {"
			+ OrderModel._TYPECODE + "} WHERE  {" + OrderModel.VERSIONID + "} IS NULL AND {"
			+ OrderModel.STATUS + "} = ?orderStatus AND {"
			+ OrderModel.MODIFIEDTIME + "} >= ?date";


	private static final String FIND_ORDERS_BY_B2BUNIT_AND_DATE = "SELECT {" + OrderModel.PK + "}, {" + OrderModel.CREATIONTIME
			+ "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.UNIT + "} = ?b2bUnit AND {"
			+ OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.CREATIONTIME + "} >= ?dateFrom AND {" + OrderModel.CREATIONTIME
			+ "} <= ?dateTo ORDER BY {" + OrderModel.CREATIONTIME + "} DESC";

	private static final String FIND_CONSIGNMENT_BY_TRACKING_ID = "SELECT {" + ConsignmentModel.PK + "} FROM {"
			+ ConsignmentModel._TYPECODE + "} WHERE {" + ConsignmentModel.TRACKINGID + "} =?trackingID";

	private static final String FIND_CONSIGNMENT_BY_STATUS_AND_ETA = "SELECT {c:" + ConsignmentModel.PK + "} FROM {"
			+ ConsignmentModel._TYPECODE + " AS c JOIN " + OrderModel._TYPECODE + " AS o ON {c:" + ConsignmentModel.ORDER + "} = {o:"
			+ OrderModel.PK + "}} WHERE  {" + ConsignmentModel.STATUS + "} = ?status AND {"
			+ ConsignmentModel.ESTIMATEDARRIVEDTIME + "} <= ?date";


	private static final String FIND_LAST_ORDER_BY_USER = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ "} WHERE" + "{" + OrderModel.USER + "} = ?user ORDER BY {" + OrderModel.CREATIONTIME + "} DESC";

	private static final String FIND_LAST_WEB_ORDER_BY_USER = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ "} WHERE" + "{" + OrderModel.USER + "} = ?user AND {" + OrderModel.SALESAPPLICATION
			+ "} IN (?salesApplication) AND {" + OrderModel.SITE
			+ "} IN ({{SELECT {PK} from {" + CMSSiteModel._TYPECODE + "} where {" + CMSSiteModel.UID + "}=?siteUid }}) ORDER BY {" + OrderModel.CREATIONTIME + "} DESC";

	private static final String FIND_FIRST_WEB_ORDER_BY_USER = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ "} WHERE" + "{" + OrderModel.USER + "} = ?user AND {" + OrderModel.SALESAPPLICATION
			+ "} IN (?salesApplication) AND {" + OrderModel.SITE
			+ "} IN ({{SELECT {PK} from {" + CMSSiteModel._TYPECODE + "} where {" + CMSSiteModel.UID + "}=?siteUid }}) ORDER BY {" + OrderModel.CREATIONTIME + "} ASC";

	private static final String FIND_ORDER_BY_CARTCODE = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ "!} WHERE {" + OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STATUS + "} IS NOT NULL AND {"
			+ OrderModel.CARTCODE + "} = ?cartCode ORDER BY {" + OrderModel.DATE + "} DESC";


	private static final String FIND_SITE_SPECIFIC_WEB_ORDERS_BY_USER_AND_B2BUNIT = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
		+ "} WHERE {" + OrderModel.USER + "} = ?user AND  {" + OrderModel.UNIT + "} =?b2bUnit AND {" + OrderModel.SITE
		+ "} IN ({{SELECT {PK} from {" + CMSSiteModel._TYPECODE + "} where {" + CMSSiteModel.UID + "}=?siteUid }}) AND {"+ OrderModel.ORDERTYPE +"}=?orderType";


	private static final int DEFAULT_PAGE_SIZE = 7;

	/** The Constant CODE. */
	private static final String CODE = "code";

	private static final String QUICKORDER_NUMBER_MONTHS = "quickorder.number.month.sga";

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/** The search restriction service. */
	@Resource
	private SearchRestrictionService searchRestrictionService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.order.dao.SabmOrderDao#getOrderBySapSalesOrderNumber(java.lang.String)
	 */
	@Override
	public OrderModel getOrderBySapSalesOrderNumber(final String sapSalesOrderNumber)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("sapSalesOrderNumber", sapSalesOrderNumber);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_ORDER_BY_SAP_ORDER_NUMBER, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}

	@Override
	public List<OrderModel> getOrderByB2BUnit(final B2BUnitModel b2bUnitModel)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("b2bUnit", b2bUnitModel);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_ORDERS_BY_B2BUNIT, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult();
	}

	@Override
	public List<OrderModel> getTopOrder(final int limit)
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_TOP_ORDERS);
		fsq.setCount(limit);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult();
	}

	@Override
	public List<OrderModel> getB2BUnitOrdersByDeliveryDate(final B2BUnitModel parentUnit, final Date requestedDeliveryDate)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("b2bUnit", parentUnit);
		params.put("requestedDeliveryDate", requestedDeliveryDate);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_ORDERS_BY_B2BUNIT_AND_REQUESTEDDELIVERYDATE, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult();
	}



	@Override
	public List<OrderModel> getOrdersByOrderStatus(final OrderStatus orderStatus)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderStatus", orderStatus);
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);

		params.put("date", cal.getTime());

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_DISPATCH_EMAIL_NOT_SENT_ORDERS, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult();
	}


	@Override
	public List<OrderModel> getOrderByB2BUnit(final B2BUnitModel parentUnit, final Date dateFrom, final Date dateTo)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("b2bUnit", parentUnit);
		params.put("dateFrom", dateFrom);
		params.put("dateTo", dateTo);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_ORDERS_BY_B2BUNIT_AND_DATE, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.order.daos.impl.DefaultOrderDao#findEntriesByProduct(de.hybris.platform.core.model.order.
	 * AbstractOrderModel, de.hybris.platform.core.model.product.ProductModel)
	 */
	@Override
	public List<AbstractOrderEntryModel> findEntriesByProduct(final AbstractOrderModel order, final ProductModel product)
	{
		if(asahiSiteUtil.isCub())
		{
		validateParameterNotNull(order, "order must not be null!");
		validateParameterNotNull(product, "product must not be null!");

		final List<AbstractOrderEntryModel> entries = order.getEntries();
		if (entries == null || entries.isEmpty() || product.getPk() == null)
		{
			return Collections.emptyList();
		}

		final List<AbstractOrderEntryModel> result = new ArrayList<AbstractOrderEntryModel>(entries.size());
		final PK productPk = product.getPk();
		for (final AbstractOrderEntryModel entry : entries)
		{
			if (null == entry)
			{
				continue;
			}
			if ((entry.getIsFreeGood() == null || !entry.getIsFreeGood().booleanValue()) && entry.getProduct() != null
					&& productPk.equals(entry.getProduct().getPk()))
			{
				result.add(entry);
			}
		}
		return result;
		}
		else
		{
			final List<AbstractOrderEntryModel> result = super.findEntriesByProduct(order, product);
			return result.stream().filter(e -> BooleanUtils.isFalse(e.getIsFreeGood())).collect(Collectors.toList());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.order.dao.SabmOrderDao#getOrderByConsignment(java.lang.String)
	 */
	@Override
	public OrderModel getOrderByConsignment(final String trackingId)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("trackingID", trackingId);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_CONSIGNMENT_BY_TRACKING_ID, params);
		final SearchResult<ConsignmentModel> result = getFlexibleSearchService().search(fsq);
		final ConsignmentModel consignmentModel = result.getCount() > 0 ? result.getResult().get(0) : null;
		return consignmentModel != null ? (OrderModel) consignmentModel.getOrder() : null;
	}

	@Override
	public List<OrderModel> getPreviousPagedOrdersByB2BUnit(final B2BUnitModel b2bUnitModel, final int page, Date dateTo)
	{
		final StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT {" + OrderModel.PK + "} ");
		queryString.append("FROM {" + OrderModel._TYPECODE + "}");
		queryString.append("WHERE {" + OrderModel.UNIT + "} = ?b2bUnit");
		queryString.append(" AND {" + OrderModel.VERSIONID + "} IS NULL");
		queryString.append(" AND {" + OrderModel.CREATIONTIME + "} < ?dateTo");
		queryString.append(" AND {" + OrderModel.STATUS + "} IN (?orderStatusIn)");
		queryString.append(" AND {" + OrderModel.STATUS + "} NOT IN (?orderStatusNotIn)");
		queryString.append(" ORDER BY {" + OrderModel.CREATIONTIME + "} DESC");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("b2bUnit", b2bUnitModel);
		params.put("orderStatusIn", Arrays.asList(OrderStatus.CREATED, OrderStatus.PROCESSING, OrderStatus.DISPATCHED,
				OrderStatus.RETURNED, OrderStatus.COMPLETED));
		params.put("orderStatusNotIn", OrderStatus.CANCELLED);
		if (null == dateTo)
		{
			dateTo = new Date();
		}
		params.put("dateTo", dateTo);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(queryString.toString(), params);
		fsq.setNeedTotal(true);
		fsq.setStart(page * DEFAULT_PAGE_SIZE);
		fsq.setCount(DEFAULT_PAGE_SIZE);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult();
	}

	@Override
	public List<OrderModel> getNextPagedOrdersByB2BUnit(final B2BUnitModel b2bUnitModel, final int page, Date dateTo)
	{
		final StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT {" + OrderModel.PK + "} ");
		queryString.append("FROM {" + OrderModel._TYPECODE + "}");
		queryString.append("WHERE {" + OrderModel.UNIT + "} = ?b2bUnit");
		queryString.append(" AND {" + OrderModel.VERSIONID + "} IS NULL");
		queryString.append(" AND {" + OrderModel.CREATIONTIME + "} > ?dateTo");
		queryString.append(" AND {" + OrderModel.STATUS + "} IN (?orderStatusIn)");
		queryString.append(" AND {" + OrderModel.STATUS + "} NOT IN (?orderStatusNotIn)");
		queryString.append(" ORDER BY {" + OrderModel.CREATIONTIME + "} ASC");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("b2bUnit", b2bUnitModel);
		params.put("orderStatusIn", Arrays.asList(OrderStatus.CREATED, OrderStatus.PROCESSING, OrderStatus.DISPATCHED,
				OrderStatus.RETURNED, OrderStatus.COMPLETED));
		params.put("orderStatusNotIn", OrderStatus.CANCELLED);
		if (null == dateTo)
		{
			dateTo = new Date();
		}
		params.put("dateTo", dateTo);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(queryString.toString(), params);
		fsq.setNeedTotal(true);
		fsq.setStart(page * DEFAULT_PAGE_SIZE);
		fsq.setCount(DEFAULT_PAGE_SIZE);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult();
	}

	@Override
	public List<OrderModel> getTotalPagedOrdersByB2BUnit(final B2BUnitModel b2bUnitModel, final int page, final String orderBy)
	{
		final StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT {" + OrderModel.PK + "} ");
		queryString.append("FROM {" + OrderModel._TYPECODE + "}");
		queryString.append("WHERE {" + OrderModel.UNIT + "} = ?b2bUnit");
		queryString.append(" AND {" + OrderModel.VERSIONID + "} IS NULL");
		queryString.append(" AND {" + OrderModel.STATUS + "} IN (?orderStatusIn)");
		queryString.append(" AND {" + OrderModel.STATUS + "} NOT IN (?orderStatusNotIn)");
		queryString.append(" ORDER BY {" + OrderModel.CREATIONTIME + "}" + orderBy);

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("b2bUnit", b2bUnitModel);
		params.put("orderStatusIn", Arrays.asList(OrderStatus.CREATED, OrderStatus.PROCESSING, OrderStatus.DISPATCHED,
				OrderStatus.RETURNED, OrderStatus.COMPLETED));
		params.put("orderStatusNotIn", OrderStatus.CANCELLED);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(queryString.toString(), params);
		fsq.setNeedTotal(true);
		fsq.setStart(page * DEFAULT_PAGE_SIZE);
		fsq.setCount(DEFAULT_PAGE_SIZE);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult();
	}

	/**
	 * Fetches the number of times the provided Product EAN has been ordered on Hybris by the given sub channel. This
	 * method takes all orders into consideration (phone/online).
	 *
	 * @param subChannel
	 *           the b2b unit's sub channel (Pub, Bar, Restaurant) to use when doing the count.
	 * @param productEAN
	 *           the product EAN to evaluate when getting the count.
	 * @param fromOrderDate
	 *           the from date to consider the orders from.
	 *
	 * @return order entries linked to the product EAN has been ordered on Hybris with the given criteria.
	 */
	public Collection<OrderEntryModel> getProductOrderCountBySubChannelAndEAN(final String subChannel,
			final SABMAlcoholVariantProductEANModel productEAN, final Date fromOrderDate)
	{
		final StringBuffer query = new StringBuffer();
		query.append("SELECT {entry.pk} FROM {");
		query.append(OrderEntryModel._TYPECODE + " AS entry ");
		query.append(" JOIN " + SABMAlcoholVariantProductMaterialModel._TYPECODE + " AS material");
		query.append(" ON {entry." + OrderEntryModel.PRODUCT + "} = {material." + SABMAlcoholVariantProductMaterialModel.PK + "}");
		query.append(" JOIN " + SABMAlcoholVariantProductEANModel._TYPECODE + " AS ean");
		query.append(" ON {material." + SABMAlcoholVariantProductMaterialModel.BASEPRODUCT + "} = {ean."
				+ SABMAlcoholVariantProductEANModel.PK + "}");
		query.append(" JOIN " + OrderModel._TYPECODE + " AS order");
		query.append(" ON {entry." + OrderEntryModel.ORDER + "} = {order." + OrderModel.PK + "}");
		query.append(" JOIN " + B2BUnitModel._TYPECODE + " AS b2bunit");
		query.append(" ON {order." + OrderModel.UNIT + "} = {b2bunit." + B2BUnitModel.PK + "}");
		query.append(" JOIN " + B2BUnitGroupModel._TYPECODE + " AS b2bUnitGroup");
		query.append(" ON {b2bunit." + B2BUnitModel.SAPGROUP + "} = {b2bUnitGroup." + B2BUnitGroupModel.PK + "}");
		query.append("}");
		query.append(" WHERE {ean." + SABMAlcoholVariantProductEANModel.CODE + "} = ?productEAN");
		query.append(" AND {b2bUnitGroup." + B2BUnitGroupModel.SUBCHANNEL + "} = ?subChannel");
		query.append(" AND {order." + OrderModel.CREATIONTIME + "} >= ?searchFromDate");

		final Map<String, Object> params = new HashMap<>();
		params.put("productEAN", productEAN.getCode());
		params.put("subChannel", subChannel);
		params.put("searchFromDate", fromOrderDate);

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query.toString(), params);
		LOG.debug("Searching with subChannel:[{}], productEAN:[{}], fromOrderDate: [{}]", subChannel, productEAN.getCode(),
				fromOrderDate.toString());
		LOG.debug("getProductOrderCountBySubChannelAndEAN query:[{}]", fsq.getQuery());

		final SearchResult<OrderEntryModel> result = getFlexibleSearchService().search(fsq);

		LOG.debug("Found [{}] results.", result.getCount());

		return result.getResult();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.order.dao.SabmOrderDao#getLastOrderByCustomer(java.lang.String)
	 */
	@Override
	public OrderModel getLastOrderByCustomer(final UserModel user)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", user);
		params.put("siteUid", SabmCoreConstants.CUB_STORE);
		params.put("salesApplication", Arrays.asList(SalesApplication.WEB, SalesApplication.WEBMOBILE));
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_LAST_WEB_ORDER_BY_USER, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);

		return result.getResult().size() > 0 ? result.getResult().get(0) : null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.order.dao.SabmOrderDao#getFirstOrderByCustomer(java.lang.String)
	 */
	@Override
	public OrderModel getFirstOnlineOrderByCustomer(final UserModel user)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", user);
		params.put("siteUid", SabmCoreConstants.CUB_STORE);
		params.put("salesApplication", Arrays.asList(SalesApplication.WEB, SalesApplication.WEBMOBILE));
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_FIRST_WEB_ORDER_BY_USER, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);

		return result.getResult().size() > 0 ? result.getResult().get(0) : null;
	}


	@Override
	public List<ConsignmentModel> getB2BUnitOrdersTimePassesETA(
			final ConsignmentStatus consignmentStatus)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", consignmentStatus);
		final Calendar cal = Calendar.getInstance();


		//SabmDateUtils.roundDateToNearestQuarterHour(consignmentModel.getEstimatedArrivedTime());
		// SabmDateUtils.minusMinutes(roundedDate,Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes.nextDelivery", "")))

		cal.add(Calendar.MINUTE, -Integer.valueOf(Config.getString("trackorder.ETA.time.window.minnutes.nextDelivery", "")));

		params.put("date", cal.getTime());

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_CONSIGNMENT_BY_STATUS_AND_ETA, params);
		final SearchResult<ConsignmentModel> result = getFlexibleSearchService().search(fsq);

		return result.getResult();
	}


	/**
	 * Gets all orders which were "Paid" or "Partly Paid" using "Credit Card" for customers on "P1" or "P2" AutoPay membership status
	 *
	 *  @return List<OrderModel>
	 */
	@Override
	public List<OrderModel> getOrdersByCreditCardPayment() {
		final Map<String, Object> params = new HashMap<>();
		params.put("paymentStatus", Arrays.asList(PaymentStatus.PAID, PaymentStatus.PARTPAID));
		params.put("autoPayStatus", Arrays.asList(AutoPayStatus.ACTIVE, AutoPayStatus.ACTIVE_WITH_ADTNL_DISCOUNT));

		final StringBuffer query = new StringBuffer("SELECT {");
		query.append("o.").append(OrderModel.PK).append("} FROM {");
		query.append(OrderModel._TYPECODE).append(" AS o JOIN ");
		query.append(CreditCardPaymentInfoModel._TYPECODE).append(" AS cc ON {o.");
		query.append(OrderModel.PAYMENTINFO).append("}={cc.").append(CreditCardPaymentInfoModel.PK);
		query.append("} JOIN ").append(B2BUnitModel._TYPECODE).append(" AS u ON {o.");
		query.append(OrderModel.UNIT).append("}={u.").append(B2BUnitModel.PK).append("}} WHERE {o.");
		query.append(OrderModel.PAYMENTINFO).append("} IS NOT NULL AND {o.");
		query.append(OrderModel.PAYMENTSTATUS).append("} IS NOT NULL AND {o.");
		query.append(OrderModel.PAYMENTSTATUS).append("} IN (?paymentStatus) AND {u.");
		query.append(B2BUnitModel.AUTOPAYSTATUS).append("} IS NOT NULL AND {u.");
		query.append(B2BUnitModel.AUTOPAYSTATUS).append("} IN (?autoPayStatus)");

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query.toString(), params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);

		return result.getResult();
	}

	/**
	 * Retrieves all orders whose create date is less than or equal than specified date
	 *
	 * @param endDate
	 */
	@Override
	public List<OrderModel> getOrdersToDate(final Date endDate, final int limit) {
		final String query = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
				+ "} WHERE {" + OrderModel.CREATIONTIME + "} < ?dateTo";
		final Map<String, Object> params = new HashMap<>();
		params.put("dateTo", endDate);
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query, params);
		flexibleSearchQuery.setCount(limit);
		return getFlexibleSearchService().<OrderModel>search(flexibleSearchQuery).getResult();
	}

	@Override
	public OrderModel getOrderByCartCode(final String cartCode)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("cartCode", cartCode);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_ORDER_BY_CARTCODE, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);

		return result.getResult().size() > 0 ? result.getResult().get(0) : null;

	}

	/**
	 * Find orders by order status.
	 *
	 * @param status
	 *           the status
	 * @return the list
	 */
	@Override
	public List<OrderModel> findOrdersByOrderStatus(final String status, final CMSSiteModel cmsSiteModel)
	{

		validateParameterNotNull(status, "status must not be null!");
		validateParameterNotNull(cmsSiteModel, "cmsSiteModel must not be null!");

		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ORDER_FOR_ORDER_STATUS);

		final List<String> items = Arrays.asList(status.split("\\s*,\\s*"));
		final List<OrderStatus> statusList = new ArrayList<>();
		for (final String stat : items)
		{
			statusList.add(OrderStatus.valueOf(stat));
		}

		params.put("status", statusList);
		params.put("site", cmsSiteModel.getPk());

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<OrderModel> result = super.search(query);

		return result.getResult();
	}

	/**
	 * Gets the order for code.
	 *
	 * @param catalogVersion
	 *           the catalog version
	 * @param code
	 *           the code
	 * @return the list
	 */
	@Override
	public OrderModel getOrderForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ORDER_FOR_CODE);
		params.put(CODE, code);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<OrderModel> result = super.search(query);

		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the order entry by backend uid.
	 *
	 * @param backendUid
	 *           the backend uid
	 * @return the order entry by backend uid
	 */
	@Override
	public OrderEntryModel getOrderEntryByBackendUid(final String backendUid)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder queryBuilder = new StringBuilder(ApbQueryConstant.GET_ORDER_ENTRY_FOR_BACKEND_UID);
		params.put("backendUid", backendUid);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());
		query.addQueryParameters(params);

		final SearchResult<OrderEntryModel> result = super.search(query);

		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the base site by uid.
	 *
	 * @param siteUid
	 *           the site uid
	 * @return the base site by uid
	 */
	@Override
	public BaseSiteModel getBaseSiteByUid(final String siteUid)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder queryBuilder = new StringBuilder(ApbQueryConstant.GET_BASE_SITE_BY_UID);
		params.put("uid", siteUid);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());
		query.addQueryParameters(params);

		final SearchResult<BaseSiteModel> result = super.search(query);

		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the base store by uid.
	 *
	 * @param storeUid
	 *           the store uid
	 * @return the base store by uid
	 */

	@Override
	public BaseStoreModel getBaseStoreByUid(final String storeUid)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder queryBuilder = new StringBuilder(ApbQueryConstant.GET_BASE_STORE_BY_UID);
		params.put("uid", storeUid);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());
		query.addQueryParameters(params);

		final SearchResult<BaseStoreModel> result = super.search(query);

		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the List of Orders for the User.
	 *
	 * @param user
	 *           for which the orders are required
	 * @return return the List of OrderModel acc to the criteria
	 */
	@Override
	public List<OrderModel> getOrderEntriesForUser(final AsahiB2BUnitModel unit)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder queryBuilder = new StringBuilder(ApbQueryConstant.GET_ORDER_ENTRY_FOR_ORDER_AND_USER);
		params.put("unit", unit.getPk());
		final Integer noOfMonths = Integer.parseInt(this.asahiConfigurationService.getString(QUICKORDER_NUMBER_MONTHS, "3"));
		final Date currentDate = new Date(System.currentTimeMillis());
		final Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		//Multiplying 30 to reduce the no of Months
		c.add(Calendar.DATE, -30 * noOfMonths);

		final Date previousDate = c.getTime();
		params.put("currentDate", currentDate);
		params.put("previousDate", previousDate);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());
		query.addQueryParameters(params);

		final SearchResult<OrderModel> result = super.search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}

	/**
	 * Gets the orders based on date and site.
	 *
	 * @param storeUid the store uid
	 * @return return the List of OrderModel acc to the criteria
	 */
	@Override
	public List<OrderModel> getOrdersBasedOnDateAndSite(final String siteUid,final Date startdate, final Date currentDate)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ORDER_BASED_ON_DATE_AND_SITE);
		params.put("startDate", startdate);
		params.put("currentDate", currentDate);
		params.put("uid", siteUid);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}

	@Override
	public int fetchOnlineOrderCountBasedOnUserB2BUnitAndSite(final UserModel user, final String siteUid, final AsahiB2BUnitModel currentUnit)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", user);
		params.put("siteUid", siteUid);
		params.put("orderType",OrderType.ONLINE );
		params.put("b2bUnit", currentUnit);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_SITE_SPECIFIC_WEB_ORDERS_BY_USER_AND_B2BUNIT, params);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(fsq);

		return result.getResult().size();
	}



}
