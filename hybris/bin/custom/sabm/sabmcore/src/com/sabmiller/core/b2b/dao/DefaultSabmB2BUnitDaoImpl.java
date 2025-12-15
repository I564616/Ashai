/**
 *
 */
package com.sabmiller.core.b2b.dao;

import de.hybris.platform.b2b.dao.impl.DefaultB2BUnitDao;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.AutoPayStatus;
import com.sabmiller.core.enums.LastUpdatedEntityType;
import com.sabmiller.core.model.B2BUnitGroupModel;
import com.sabmiller.core.model.LastUpdateTimeEntityModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SalesDataModel;
import com.sabmiller.core.model.SalesOrgDataModel;


/**
 * @author joshua.a.antony
 *
 */
public class DefaultSabmB2BUnitDaoImpl extends DefaultB2BUnitDao implements SabmB2BUnitDao
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmB2BUnitDaoImpl.class);

	private static final String FIND_TOP_LEVEL_B2B_UNIT_BY_PAYER = "SELECT {" + B2BUnitModel.PK + "} " + "FROM {"
			+ B2BUnitModel._TYPECODE + "} WHERE {" + B2BUnitModel.PAYERID + "}=?payerId AND {" + B2BUnitModel.ACCOUNTGROUP
			+ "}=?accountGroup";

	/** The Constant QUERY_B2B_UNIT_BY_ACCOUNT_ID. */
	private static final String QUERY_B2B_UNIT_BY_ACCOUNT_ID = "SELECT {" + B2BUnitModel.PK + "} FROM {" + B2BUnitModel._TYPECODE
			+ "} WHERE {" + B2BUnitModel.ACCOUNTGROUP + "}= ?accountGroup AND {" + B2BUnitModel.UID
			+ "} LIKE ?accountNumber ORDER BY {" + B2BUnitModel.UID + "} ASC";

	/** The Constant QUERY_B2B_UNIT_BY_ACCOUNT_ID. */
	private static final String QUERY_B2B_UNIT_BY_CUSTOMER_ZALB = "SELECT DISTINCT {" + B2BUnitModel.PK + "} FROM {"
			+ B2BUnitModel._TYPECODE + "} WHERE {" + B2BUnitModel.ACCOUNTGROUP + "}= ?accountGroup AND ({" + B2BUnitModel.UID
			+ "} LIKE ?accountNumber AND LOWER({" + B2BUnitModel.NAME + "}) LIKE ?name)";

	/** The Constant FIND_OLD_LAST_UPDATE_TIME_ENTITY_QUERY. */
	private static final String FIND_OLD_LAST_UPDATE_TIME_ENTITY_QUERY = "SELECT {" + LastUpdateTimeEntityModel.PK + "} FROM {"
			+ LastUpdateTimeEntityModel._TYPECODE + "} WHERE {" + LastUpdateTimeEntityModel.DELIVERYDATE
			+ "} <= ?deliveryBefore ORDER BY {" + LastUpdateTimeEntityModel.DELIVERYDATE + "}";


	private static final String BUSINESS_UNIT_ORDERS = "SELECT {order:" + OrderModel.PK + "} FROM {" + B2BUnitModel._TYPECODE
			+ " as unit" + " JOIN " + OrderModel._TYPECODE
			+ " as order ON   {order:unit} = {unit:pk} } WHERE {unit:pk} = ?unit and {order:date} >= ?date";

	private static final String FIND_LAST_UPDATE_TIME_ENTITY_QUERY = "SELECT {" + LastUpdateTimeEntityModel.PK + "} FROM {"
			+ LastUpdateTimeEntityModel._TYPECODE + "} WHERE {" + LastUpdateTimeEntityModel.DELIVERYDATE + "} = ?deliveryDate AND {"
			+ LastUpdateTimeEntityModel.ENTITYTYPE + "} = ?lastUpdatedEntityType AND {" + LastUpdateTimeEntityModel.B2BUNIT
			+ "} = ?b2bUnit";

	private static final String QUERY_B2BUNIT_BY_UID = "SELECT {" + B2BUnitModel.PK + "} FROM {" + B2BUnitModel._TYPECODE
			+ "} WHERE {" + B2BUnitModel.UID + "} = ?uid OR {" + B2BUnitModel.UID + "} LIKE ?likeUid";

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.dao.SabmB2BUnitDao#findTopLevelB2BUnit(java.lang.String)
	 */
	@Override
	public B2BUnitModel findTopLevelB2BUnit(final String payerId)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("payerId", payerId);
		params.put("accountGroup", SabmCoreConstants.ZADP);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_TOP_LEVEL_B2B_UNIT_BY_PAYER, params);
		final SearchResult<B2BUnitModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}


	@Override
	public List<B2BUnitModel> searchB2BUnit(final SearchB2BUnitQueryParam aueryParam)
	{
		final QueryUtil queryUtil = generateSearchQuery(aueryParam);

		queryUtil.log();

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(queryUtil.getQuery(), queryUtil.params);
		final SearchResult<B2BUnitModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult() : Collections.emptyList();
	}

	protected QueryUtil generateSearchQuery(final SearchB2BUnitQueryParam queryParam)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("In generateSearchQuery() queryParam is {}  ", ReflectionToStringBuilder.toString(queryParam));
		}
		final StringBuilder query = new StringBuilder("SELECT {b:pk} " + "FROM {" + B2BUnitModel._TYPECODE + " AS b ");
		if (StringUtils.isNotBlank(queryParam.getDivision()) || StringUtils.isNotBlank(queryParam.getDistributionChannel()))
		{
			query.append(" JOIN " + SalesDataModel._TYPECODE + " AS sd ON {b:salesData}={sd:pk}");
		}
		if (StringUtils.isNotBlank(queryParam.getSalesGroup()) || StringUtils.isNotBlank(queryParam.getSalesOffice())
				|| StringUtils.isNotBlank(queryParam.getCustomerGroup()) || StringUtils.isNotBlank(queryParam.getPriceGroup()))
		{
			query.append(" JOIN " + SalesOrgDataModel._TYPECODE + " AS sod ON {b:salesOrgData}={sod:pk}");
		}
		if (StringUtils.isNotBlank(queryParam.getBanner()) || StringUtils.isNotBlank(queryParam.getPrimaryBanner())
				|| StringUtils.isNotBlank(queryParam.getSubBanner()) || StringUtils.isNotBlank(queryParam.getSubChannel()))
		{
			query.append(" JOIN " + B2BUnitGroupModel._TYPECODE + " AS bug ON {b:sapGroup}={bug:pk} ");
		}
		if (StringUtils.isNotBlank(queryParam.getPlant()))
		{
			query.append(" JOIN " + PlantModel._TYPECODE + " AS p ON {b:plant}={p:pk}");
		}
		query.append("}");

		query.append(" WHERE ");

		return new QueryUtil(query).append(queryParam.getCustomer(), " {b:uid}=?customer ")
				.append(queryParam.getSalesOrgId(), " {b:salesOrgId}=?salesOrgId ")
				.append(queryParam.getSalesGroup(), " {sod:salesGroup}=?salesGroup ")
				.append(queryParam.getSalesOffice(), " {sod:salesOfficeCode}=?salesOfficeCode ")
				.append(queryParam.getCustomerGroup(), " {sod:customerGroup}=?customerGroup ")
				.append(queryParam.getPriceGroup(), " {sod:priceGroup}=?priceGroup ")
				.append(queryParam.getBanner(), " {bug:groupKey}=?banner ")
				.append(queryParam.getPrimaryBanner(), " {bug:primaryGroupKey}=?primaryGroupKey ")
				.append(queryParam.getSubBanner(), "{bug:subGroupKey}=?subGroupKey")
				.append(queryParam.getSubChannel(), " {bug:subChannel}=?subChannel ")
				.append(queryParam.getDivision(), "{sd:division}=?division")
				.append(queryParam.getDistributionChannel(), " {sd:distributionChannel}=?distributionChannel ")
				.append(queryParam.getPlant(), " {p:plantId}=?plantId ");
	}

	class QueryUtil
	{
		private final StringBuilder queryBuilder;
		Map<String, Object> params = new HashMap<String, Object>();

		public QueryUtil(final StringBuilder queryBuilder)
		{
			this.queryBuilder = queryBuilder;
		}

		private QueryUtil append(final String check, final String condition)
		{
			if (!StringUtils.isBlank(check))
			{
				if (!params.isEmpty())
				{
					queryBuilder.append(" AND ");
				}
				queryBuilder.append(condition);
				final String paramName = condition.substring(condition.indexOf("?") + 1, condition.length()).trim();
				params.put(paramName, check);
			}
			return this;
		}

		public String getQuery()
		{
			return queryBuilder.toString();
		}

		public void log()
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("query : {} ", getQuery());
				LOG.debug("Printing query parameters");
				for (final String key : params.keySet())
				{
					LOG.debug("key : {} , value : {} ", key, params.get(key));
				}
			}
		}
	}

	@Override
	public B2BUnitModel findBranch(final String payerId)
	{
		final List<B2BUnitModel> branches = findBranches(payerId);
		return !branches.isEmpty() ? branches.get(0) : null;
	}

	@Override
	public List<B2BUnitModel> findBranches(final String payerId)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("payerId", payerId);
		params.put("accountGroup", SabmCoreConstants.ZALB);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_TOP_LEVEL_B2B_UNIT_BY_PAYER, params);
		final SearchResult<B2BUnitModel> result = getFlexibleSearchService().search(fsq);

		LOG.debug("There are {} branches for Payer {} ", result.getCount(), payerId);
		return result.getCount() > 0 ? result.getResult() : Collections.emptyList();
	}

	@Override
	public List<B2BUnitModel> searchB2BUnitByAccount(final String accountNumber)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("accountNumber", "%" + accountNumber + "%");
		params.put("accountGroup", SabmCoreConstants.ZADP);

		LOG.debug("The search b2b Unit by account ID query [{}] - params [{}]", QUERY_B2B_UNIT_BY_ACCOUNT_ID, params);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_B2B_UNIT_BY_ACCOUNT_ID, params);
		final SearchResult<B2BUnitModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult() : Collections.emptyList();
	}

	@Override
	public List<B2BUnitModel> searchB2BUnitByCustomer(final String customerNumber, final String customerName)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder query = new StringBuilder(
				"SELECT DISTINCT {" + B2BUnitModel.PK + "} FROM {" + B2BUnitModel._TYPECODE + "} WHERE ");

		query.append("{accountGroup}=?accountGroup ");
		params.put("accountGroup", SabmCoreConstants.ZALB);

		if (StringUtils.isNotBlank(customerNumber))
		{
			query.append("AND {uid} LIKE ?customerNumber ");
			params.put("customerNumber", "%" + customerNumber + "%");
		}
		if (StringUtils.isNotBlank(customerName))
		{
			query.append("AND LOWER({name}) LIKE ?customerName ");
			params.put("customerName", "%" + StringUtils.lowerCase(customerName) + "%");
		}

		LOG.debug("The search b2b Unit by customer name/id query [{}] - params [{}]", query.toString(), params);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query.toString(), params);
		final SearchResult<B2BUnitModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult() : Collections.emptyList();
	}


	/**
	 * Find old LastUpdateTimeEntityModel while LastUpdateTimeEntityModel.deliveryDate < deliveryBefore.
	 *
	 * @param deliveryBefore
	 *           the delivery before
	 * @param batchSize
	 *           the batch size
	 * @return list of @LastUpdateTimeEntityModel
	 */
	@Override
	public List<LastUpdateTimeEntityModel> findOldLastUpdateTimeEntities(final Date deliveryBefore, final int batchSize)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("deliveryBefore", deliveryBefore);
		return doSearch(FIND_OLD_LAST_UPDATE_TIME_ENTITY_QUERY, params, batchSize, LastUpdateTimeEntityModel.class);
	}

	protected <T> List<T> doSearch(final String query, final Map<String, Object> params, final int batchSize,
			final Class<T> resultClass)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		if (params != null)
		{
			fQuery.addQueryParameters(params);

			//will fetch all LastUpdateTimeEntityModel data if batch size is 0
			if (batchSize > 0)
			{
				LOG.debug("Fetch RepDrivenDealConditionStatus by batch, batch size is {}", batchSize);
				//set paging data
				fQuery.setNeedTotal(Boolean.TRUE);
				fQuery.setStart(0);
				fQuery.setCount(batchSize);
			}
		}

		fQuery.setResultClassList(Collections.singletonList(resultClass));

		final SearchResult<T> searchResult = getFlexibleSearchService().search(fQuery);
		return searchResult.getResult();
	}

	@Override
	public List<OrderModel> findB2BunitOrders(final B2BUnitModel unit, final Date ordersAfterDate)
	{

		final Map<String, Object> attr = new HashMap<>(2);
		attr.put("unit", unit);
		attr.put("date", ordersAfterDate);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(BUSINESS_UNIT_ORDERS);

		query.getQueryParameters().putAll(attr);

		final SearchResult<OrderModel> result = getFlexibleSearchService().search(query);

		return result.getResult();
	}


	/**
	 * Find LastUpdateTimeEntityModel while LastUpdateTimeEntityModel.deliveryDate = deliveryBefore And
	 * LastUpdateTimeEntityModel.entitytype = lastentitytype And LastUpdateTimeEntityModel.b2bunit = b2bunit
	 *
	 * @param deliveryDate
	 *           the delivery date
	 * @param lastUpdatedEntityType
	 *           the entyty type
	 * @param B2BUnit
	 *           the B2BUnit
	 * @return @LastUpdateTimeEntityModel
	 */
	@Override
	public LastUpdateTimeEntityModel findLastUpdateTimeEntities(final Date deliveryDate,
			final LastUpdatedEntityType lastUpdatedEntityType, final B2BUnitModel B2BUnit)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("deliveryDate", deliveryDate);
		params.put("lastUpdatedEntityType", lastUpdatedEntityType);
		params.put("b2bUnit", B2BUnit);

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_LAST_UPDATE_TIME_ENTITY_QUERY, params);
		final SearchResult<LastUpdateTimeEntityModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}

	@Override
	public List<B2BUnitModel> findB2BUnitsByBannerAndPriceGroup(final String primaryGroupKey, final String subGroupKey,
			final String priceGroup)
	{
		final StringBuilder queryString = new StringBuilder(
				"SELECT {bu:" + B2BUnitModel.PK + "} FROM {" + B2BUnitModel._TYPECODE + " AS bu ");
		if (StringUtils.isNotBlank(primaryGroupKey) || StringUtils.isNotBlank(subGroupKey))
		{
			queryString.append(" JOIN " + B2BUnitGroupModel._TYPECODE + " AS bug ON {bu:" + B2BUnitModel.SAPGROUP + "}={bug:"
					+ B2BUnitGroupModel.PK + "}");
		}
		if (StringUtils.isNotBlank(priceGroup))
		{
			queryString.append(" JOIN " + SalesOrgDataModel._TYPECODE + " AS sod ON {bu:" + B2BUnitModel.SALESORGDATA + "}={sod:"
					+ SalesOrgDataModel.PK + "}");
		}
		queryString.append("}");
		queryString.append(" WHERE ");
		final QueryUtil searchQueryUtil = new QueryUtil(queryString);
		searchQueryUtil.append(primaryGroupKey, " {bug:" + B2BUnitGroupModel.PRIMARYGROUPKEY + "}=?primaryGroupKey ");
		searchQueryUtil.append(subGroupKey, " {bug:" + B2BUnitGroupModel.SUBGROUPKEY + "}=?subGroupKey ");
		searchQueryUtil.append(priceGroup, " {sod:" + SalesOrgDataModel.PRICEGROUP + "}=?priceGroup ");
		searchQueryUtil.log();
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(searchQueryUtil.getQuery(), searchQueryUtil.params);
		final SearchResult<B2BUnitModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult() : Collections.emptyList();
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.dao.SabmB2BUnitDao#findB2BUnitbyUID(java.lang.String)
	 */
	@Override
	public B2BUnitModel findB2BUnitbyUID(final String uid)
	{

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("likeUid", "%" + uid);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(QUERY_B2BUNIT_BY_UID, params);
		final SearchResult<B2BUnitModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}

}
