/**
 *
 */
package com.sabmiller.core.cart.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.order.dao.impl.DefaultCommerceCartDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.site.BaseSiteService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.model.OrderTemplateEntryModel;
import com.apb.core.model.OrderTemplateModel;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SabmCartRuleModel;


/**
 * @author joshua.a.antony
 *
 */
public class DefaultSabmCommerceCartDao extends DefaultCommerceCartDao implements SabmCommerceCartDao
{

	private final static Logger LOG = LoggerFactory.getLogger("DefaultSabmCommerceCartDao");

	protected final static String FIND_CARTS_FOR_SITE_AND_USER_AND_B2BUNIT = SELECTCLAUSE + "WHERE {" + CartModel.SITE
			+ "} = ?site AND {" + CartModel.USER + "} = ?user AND {" + CartModel.UNIT + "}= ?b2bUnit " + ORDERBYCLAUSE;

	protected final static String FIND_CART_RULES = "SELECT {pk} FROM {SabmCartRule} WHERE {status}=1";

	/** The Constant SORT_CODE_BY_DATE_MODIFIED. */
	protected static final String SORT_CODE_BY_DATE_MODIFIED = "byDateModified";

	/** The Constant SORT_CODE_BY_DATE_SAVED. */
	protected static final String SORT_CODE_BY_DATE_SAVED = "byDateSaved";

	/** The Constant SORT_CODE_BY_NAME. */
	protected static final String SORT_CODE_BY_NAME = "byName";

	/** The Constant SORT_CODE_BY_CODE. */
	protected static final String SORT_CODE_BY_CODE = "byCode";

	/** The Constant SORT_CODE_BY_TOTAL. */
	protected static final String SORT_CODE_BY_TOTAL = "byTotal";

	/** The Constant DATE_MODIFIED_SORT_CRITERIA. */
	protected static final String DATE_MODIFIED_SORT_CRITERIA = "{" + CartModel.MODIFIEDTIME + "} DESC";

	protected static final String DATE_CREATED_SORT_CRITERIA = "{" + CartModel.SAVETIME + "}";

	/** The Constant SORT_SAVED_CARTS_BY_CODE. */
	protected static final String SORT_SAVED_CARTS_BY_CODE = " ORDER BY {" + CartModel.NAME + "} DESC, "
			+ DATE_CREATED_SORT_CRITERIA;

	/** The Constant SORT_SAVED_CARTS_BY_NAME. */
	protected static final String SORT_SAVED_CARTS_BY_NAME = " ORDER BY {" + CartModel.NAME + "}, " + DATE_CREATED_SORT_CRITERIA;

	/** The Constant SORT_SAVED_CARTS_BY_DATE_SAVED. */
	protected static final String SORT_SAVED_CARTS_BY_DATE_SAVED = " ORDER BY {" + CartModel.SAVETIME + "} DESC";
	private static final String FIND_ORDER_ENTRIES_FOR_CUSTOMER_RULE = "SELECT {OE:" + OrderEntryModel.PK + "} FROM {"
			+ OrderEntryModel._TYPECODE + " AS OE JOIN " + OrderModel._TYPECODE + " AS O ON {OE:" + OrderEntryModel.ORDER + "}={O:"
			+ OrderModel.PK + "}} WHERE {OE:" + OrderEntryModel.PRODUCT + "} = ?product AND {O:" + OrderModel.SITE
			+ "} = ?site AND {O:" + OrderModel.UNIT + "} =?unit AND {O:" + OrderModel.REQUESTEDDELIVERYDATE
			+ "} >= ?startDate AND {O:" + OrderModel.REQUESTEDDELIVERYDATE + "} < ?endDate AND {O:" + OrderModel.STATUS
			+ "} NOT IN (?orderStatus)";
	private static final String FIND_ORDER_ENTRIES_FOR_PLANT_RULE = "SELECT {OE:" + OrderEntryModel.PK + "} FROM {"
			+ OrderEntryModel._TYPECODE + " AS OE JOIN " + OrderModel._TYPECODE + " AS O ON {OE:" + OrderEntryModel.ORDER + "}={O:"
			+ OrderModel.PK + "} JOIN " + B2BUnitModel._TYPECODE + " AS BU ON {BU:" + B2BUnitModel.PK + "}={O:" + OrderModel.UNIT
			+ "}} WHERE {OE:" + OrderEntryModel.PRODUCT + "} = ?product AND {O:" + OrderModel.SITE + "} = ?site AND {BU:"
			+ B2BUnitModel.PLANT + "} =?plant AND {O:" + OrderModel.REQUESTEDDELIVERYDATE + "} >= ?startDate AND {O:"
			+ OrderModel.REQUESTEDDELIVERYDATE + "} < ?endDate AND {O:" + OrderModel.STATUS + "} NOT IN (?orderStatus)";
	private static final String FIND_ORDER_ENTRIES_FOR_GLOBAL_RULE = "SELECT {OE:" + OrderEntryModel.PK + "} FROM {"
			+ OrderEntryModel._TYPECODE + " AS OE JOIN " + OrderModel._TYPECODE + " AS O ON {OE:" + OrderEntryModel.ORDER + "}={O:"
			+ OrderModel.PK + "}} WHERE {OE:" + OrderEntryModel.PRODUCT + "} = ?product AND {O:" + OrderModel.SITE
			+ "} = ?site AND {O:" + OrderModel.REQUESTEDDELIVERYDATE + "} >= ?startDate AND {O:" + OrderModel.REQUESTEDDELIVERYDATE
			+ "} < ?endDate AND {O:" + OrderModel.STATUS + "} NOT IN (?orderStatus)";

	/** The paged flexible search service. */
	@Resource(name = "pagedFlexibleSearchService")
	private PagedFlexibleSearchService pagedFlexibleSearchService;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.cart.dao.SabmCommerceCartDao#getCartsForSiteAndUser(de.hybris.platform.basecommerce.model.site
	 * .BaseSiteModel, de.hybris.platform.core.model.user.UserModel, de.hybris.platform.b2b.model.B2BUnitModel)
	 */
	@Override
	public List<CartModel> getCartsForSiteAndUserAndB2BUnit(final BaseSiteModel site, final UserModel user,
			final B2BUnitModel b2bUnit)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("site", site);
		params.put("user", user);
		params.put("b2bUnit", b2bUnit);
		return doSearch(FIND_CARTS_FOR_SITE_AND_USER_AND_B2BUNIT, params, CartModel.class);
	}

	@Override
	public CartModel getCartForSiteAndUserAndB2BUnit(final BaseSiteModel site, final UserModel user, final B2BUnitModel b2bUnit)
	{
		final List<CartModel> carts = getCartsForSiteAndUserAndB2BUnit(site, user, b2bUnit);
		if (carts != null && !carts.isEmpty())
		{
			return carts.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.cart.dao.SabmCommerceCartDao#getCustomCartRules()
	 */
	@Override
	public List<SabmCartRuleModel> getCustomCartRules()
	{
		return doSearch(FIND_CART_RULES, null, SabmCartRuleModel.class);

	}

	/**
	 * Gets the cart for code and user.
	 *
	 * @param name
	 *           the name
	 * @param defaultB2BUnit
	 *           the default B 2 B unit
	 * @return the cart for code and user
	 */
	@Override
	public List<OrderTemplateModel> getCartForCodeAndB2BUnit(final String name, final B2BUnitModel defaultB2BUnit)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("code", name);
		params.put("b2bunit", defaultB2BUnit);
		return doSearch(ApbQueryConstant.GET_CART_FOR_CODE_AND_B2BUNIT, params, OrderTemplateModel.class);
	}

	/**
	 * Gets the saved cart for code and B 2 B unit.
	 *
	 * @param pageableData
	 *           the pageable data
	 * @param b2bUnit
	 *           the b 2 b unit
	 * @return the saved cart for code and B 2 B unit
	 */
	@Override
	public SearchPageData<OrderTemplateModel> getSavedCartForCodeAndB2BUnit(final PageableData pageableData,
			final AsahiB2BUnitModel b2bUnit)
	{
		final Map<String, Object> params = new HashMap<String, Object>();

		final String query = ApbQueryConstant.GET_CART_FOR_B2BUNIT;
		params.put("b2bUnit", b2bUnit);

		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_CODE_BY_DATE_SAVED, query + SORT_SAVED_CARTS_BY_DATE_SAVED),
				createSortQueryData(SORT_CODE_BY_NAME, query + SORT_SAVED_CARTS_BY_NAME),
				createSortQueryData(SORT_CODE_BY_CODE, query + SORT_SAVED_CARTS_BY_CODE));
		return this.pagedFlexibleSearchService.search(sortQueries, SORT_CODE_BY_DATE_SAVED, params, pageableData);
	}

	/**
	 * Gets the saved cart for code and B 2 B unit.
	 *
	 * @param b2bUnit
	 *           the b 2 b unit
	 * @return the saved cart for code and B 2 B unit
	 */
	@Override
	public List<OrderTemplateModel> getAllSavedCartForB2BUnit(final AsahiB2BUnitModel b2bUnit)
	{
		final Map<String, Object> params = new HashMap<String, Object>();

		final FlexibleSearchQuery query = new FlexibleSearchQuery(ApbQueryConstant.GET_CART_FOR_B2BUNIT + SORT_SAVED_CARTS_BY_CODE) ;
		params.put("b2bUnit", b2bUnit);
		query.addQueryParameters(params);
		final SearchResult<OrderTemplateModel> result = getFlexibleSearchService().search(query);

		return result.getCount() > 0 ? result.getResult() : Collections.<OrderTemplateModel> emptyList();

	}

	/**
	 * Creates the sort query data.
	 *
	 * @param sortCode
	 *           the sort code
	 * @param query
	 *           the query
	 * @return the sort query data
	 */
	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}

	/**
	 * Gets the order template for code and B 2 B unit.
	 *
	 * @param templateCode
	 *           the template code
	 * @param defaultB2BUnit
	 *           the default B 2 B unit
	 * @return the order template for code and B 2 B unit
	 */
	@Override
	public OrderTemplateModel getOrderTemplateForCodeAndB2BUnit(final String templateCode, final AsahiB2BUnitModel defaultB2BUnit)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ORDER_TEMPLATE_FOR_CODE_AND_B2B_UNIT);
		params.put("code", templateCode);
		params.put("b2bunit", defaultB2BUnit);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		LOG.debug("Order template Query : " + query.toString());
		final SearchResult<OrderTemplateModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the order template entry for PK.
	 *
	 * @param orderTemplateEntryPK
	 *           the order template entry PK
	 * @return the order template entry for PK
	 */
	@Override
	public OrderTemplateEntryModel getOrderTemplateEntryForPK(final String orderTemplateEntryPK)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ORDER_TEMPLATE_ENTRY_FOR_PK);
		params.put("pk", orderTemplateEntryPK);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);

		final SearchResult<OrderTemplateEntryModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

	public List<OrderEntryModel> getOrderEntriesForCustomerRule(final ProductModel productModel, final B2BUnitModel b2bUnitModel,
			final CMSSiteModel cmsSiteModel, final Map<String, Date> maxOrderQtyDatesMap)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", maxOrderQtyDatesMap.get(SabmCoreConstants.MAX_ORDERQTY_START_DATE));
		params.put("endDate", maxOrderQtyDatesMap.get(SabmCoreConstants.MAX_ORDERQTY_END_DATE));
		params.put("product", productModel);
		params.put("site", cmsSiteModel);
		params.put("unit", b2bUnitModel);
		if (configurationService.getConfiguration().getBoolean("cub.order.filter.cancel", true)
				&& baseSiteService.getCurrentBaseSite() != null
				&& SabmCoreConstants.CUB_STORE.equalsIgnoreCase(baseSiteService.getCurrentBaseSite().getUid()))
		{
			final List<OrderStatus> statusList = Arrays.asList(OrderStatus.RETURNED, OrderStatus.CANCELLED);
			params.put("orderStatus", statusList);
		}
		else
		{
			params.put("orderStatus", OrderStatus.RETURNED);
		}
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ORDER_ENTRIES_FOR_CUSTOMER_RULE, params);
		final SearchResult<OrderEntryModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}
	public List<OrderEntryModel> getOrderEntriesForPlantRule(final ProductModel productModel, final PlantModel plantModel,
			final CMSSiteModel cmsSiteModel, final Map<String, Date> maxOrderQtyDatesMap)
	{

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", maxOrderQtyDatesMap.get(SabmCoreConstants.MAX_ORDERQTY_START_DATE));
		params.put("endDate", maxOrderQtyDatesMap.get(SabmCoreConstants.MAX_ORDERQTY_END_DATE));
		params.put("product", productModel);
		params.put("site", cmsSiteModel);
		params.put("plant", plantModel);
		if (configurationService.getConfiguration().getBoolean("cub.order.filter.cancel", true)
				&& baseSiteService.getCurrentBaseSite() != null
				&& SabmCoreConstants.CUB_STORE.equalsIgnoreCase(baseSiteService.getCurrentBaseSite().getUid()))
		{
			final List<OrderStatus> statusList = Arrays.asList(OrderStatus.RETURNED, OrderStatus.CANCELLED);
			params.put("orderStatus", statusList);
		}
		else
		{
			params.put("orderStatus", OrderStatus.RETURNED);
		}
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ORDER_ENTRIES_FOR_PLANT_RULE, params);
		final SearchResult<OrderEntryModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;

	}

	public List<OrderEntryModel> getOrderEntriesForGlobalRule(final ProductModel productModel, final CMSSiteModel cmsSiteModel,
			final Map<String, Date> maxOrderQtyDatesMap)
	{

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", maxOrderQtyDatesMap.get(SabmCoreConstants.MAX_ORDERQTY_START_DATE));
		params.put("endDate", maxOrderQtyDatesMap.get(SabmCoreConstants.MAX_ORDERQTY_END_DATE));
		params.put("product", productModel);
		params.put("site", cmsSiteModel);
		if (configurationService.getConfiguration().getBoolean("cub.order.filter.cancel", true)
				&& baseSiteService.getCurrentBaseSite() != null
				&& SabmCoreConstants.CUB_STORE.equalsIgnoreCase(baseSiteService.getCurrentBaseSite().getUid()))
		{
			final List<OrderStatus> statusList = Arrays.asList(OrderStatus.RETURNED, OrderStatus.CANCELLED);
			params.put("orderStatus", statusList);
		}
		else
		{
			params.put("orderStatus", OrderStatus.RETURNED);
		}
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ORDER_ENTRIES_FOR_GLOBAL_RULE, params);
		final SearchResult<OrderEntryModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;

	}

}
