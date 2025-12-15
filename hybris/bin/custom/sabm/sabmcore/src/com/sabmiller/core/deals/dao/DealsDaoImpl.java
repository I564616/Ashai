/**
 *
 */
package com.sabmiller.core.deals.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.AsahiCatalogProductMappingModel;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.DealAssigneeModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;



/**
 * The Class DealsDaoImpl.
 *
 * @author joshua.a.antony
 */
public class DealsDaoImpl implements DealsDao
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DealsDaoImpl.class.getName());

	/** The Constant QUERY_DEAL_BY_CODE. */
	private static final String QUERY_DEAL_BY_CODE = "SELECT {d:pk} FROM {Deal AS d} WHERE {d:code}=?dealCode";

	/** The Constant DEALS_QUERY. */
	private static final String DEALS_QUERY = "SELECT {d:pk} FROM {Deal AS d} WHERE {d:b2bUnit}=?bunit AND NOT ({d:validFrom}>?validTo OR {d:validTo}<?validFrom)";

	/** The Constant DEALS_BY_PRODUCT_QUERY. */
	private static final String DEALS_BY_PRODUCT_QUERY = "SELECT {d:pk} FROM {Deal AS d JOIN DealConditionGroup AS dcg ON "
			+ "{d:conditionGroup}={dcg:pk} JOIN ProductDealCondition AS pdc ON {dcg:pk}={pdc:dealConditionGroup}} ";

	/** The Constant BASIC_DEAL_QUERY. */
	//pieces of queries to search deal - not sure if this will perform
	private static final String BASIC_DEAL_QUERY = "SELECT DISTINCT {d:PK} FROM {" + B2BUnitModel._TYPECODE + " as b JOIN "
			+ DealModel._TYPECODE + " as d ON {b:PK}={d:" + DealModel.B2BUNIT + "}}";

	/** The Constant ADV_DEAL_QUERY. */
	private static final String ADV_DEAL_QUERY = "SELECT DISTINCT {d:pk} " + "FROM {" + B2BUnitModel._TYPECODE + " AS b JOIN "
			+ DealModel._TYPECODE + " AS d ON {b:pk}={d:" + DealModel.B2BUNIT + "} " + "JOIN " + ProductDealConditionModel._TYPECODE
			+ " AS pdc ON {d:" + DealModel.CODE + "}={pdc:" + ProductDealConditionModel.DEALCODE + "}}";

	/** The Constant BRAND_CLAUSE_SUBQUERY. */
	private static final String BRAND_CLAUSE_SUBQUERY = "SELECT DISTINCT {mp:" + SABMAlcoholVariantProductMaterialModel.CODE
			+ "} FROM {" + SABMAlcoholVariantProductMaterialModel._TYPECODE + " AS mp JOIN "
			+ SABMAlcoholVariantProductEANModel._TYPECODE + " AS p ON {mp:baseProduct:PK}={p:PK} JOIN "
			+ SABMAlcoholProductModel._TYPECODE + " AS ap ON {p:baseProduct:PK}={ap:PK}} WHERE {ap:" + SABMAlcoholProductModel.BRAND
			+ "} IN (?brand)";

	/** The Constant CATEGORY_CLAUSE_SUBQUERY. */
	private static final String CATEGORY_CLAUSE_SUBQUERY = "SELECT DISTINCT {mp:" + SABMAlcoholVariantProductMaterialModel.CODE
			+ "} FROM {" + SABMAlcoholVariantProductMaterialModel._TYPECODE + " AS mp JOIN "
			+ SABMAlcoholVariantProductEANModel._TYPECODE
			+ " AS p ON {mp:baseProduct:PK}={p:PK} JOIN CategoryProductRelation as cp on {p:PK}={cp:target} JOIN "
			+ CategoryModel._TYPECODE + " as c ON {c:PK}={cp:source}} WHERE {c:" + CategoryModel.CODE + "} IN (?categoryCode)";

	/** The Constant CATEGORY_BRAND_CLAUSE_SUBQUERY. */
	private static final String CATEGORY_BRAND_CLAUSE_SUBQUERY = "SELECT DISTINCT {mp:"
			+ SABMAlcoholVariantProductMaterialModel.CODE + "} FROM {" + SABMAlcoholVariantProductMaterialModel._TYPECODE
			+ " AS mp JOIN " + SABMAlcoholVariantProductEANModel._TYPECODE
			+ " AS p ON {mp:baseProduct:PK}={p:PK} JOIN CategoryProductRelation as cp on {p:PK}={cp:target} JOIN "
			+ CategoryModel._TYPECODE + " as c ON {c:PK}={cp:source} JOIN " + SABMAlcoholProductModel._TYPECODE
			+ " AS ap ON {p:baseProduct:PK}={ap:PK}} WHERE {c:" + CategoryModel.CODE + "} IN (?categoryCode)" + " AND {ap:"
			+ SABMAlcoholProductModel.BRAND + "} IN (?brand)";

	/** The Constant DEALS_QUERY_BY_TYPE. */
	private static final String DEALS_QUERY_BY_TYPE = "SELECT {d:pk} FROM {Deal AS d} WHERE {d:b2bUnit}=?bunit AND {d:dealType}=?dealType";

	/** The Constant COMPLEX_DEALS_QUERY. */
	private static final String COMPLEX_DEALS_QUERY = "SELECT {d:pk} FROM {Deal AS d} WHERE {d:dealType}=?dealType AND {d:b2bUnits} LIKE ?b2bUnit";

	/** The Constant COMPLEX_DEALS_ALL_QUERY. */
	private static final String COMPLEX_DEALS_ALL_QUERY = "SELECT {d:pk} FROM {Deal AS d} WHERE {d:dealType}=?dealType";

	/** The Constant COMPLEX_DEALS_ALL_QUERY. */
	private static final String COMPLEX_DEALS_FROM_DATE_QUERY = "SELECT {d:pk} FROM {Deal AS d} WHERE {d:dealType}=?dealType AND  {d:validTo} > ?validTo";

	/** The Constant REPDRIVEN_EXCLUSIVE_DEALS_QUERY. */
	private static final String REPDRIVEN_EXCLUSIVE_DEALS_QUERY = "SELECT {d:pk} FROM {B2BUnit AS b JOIN Deal AS d ON {b:pk}={d:b2bUnit}} "
			+ "WHERE {b:pk}=?b2bunit AND {d:inStore}=?inStore AND {d:validTo}>=?toDate and {d:dealType}=?dealType";

	/** The Constant QUERY_ABSTRACTDEALCONDITION_BY_DATE. */
	private static final String QUERY_ABSTRACTDEALCONDITION_BY_DATE = "select {fgdb.PK} from {Deal As d},{DealConditionGroup As dcg},{FreeGoodsDealBenefit As fgdb} where {dcg.PK} = {d.conditionGroup} AND {fgdb.dealConditionGroup} = {dcg.PK} AND {d.ValidTo} < ?date";

	/** The Constant QUERY_ABSTRACTDEALCONDITION_BY_DATE_GARBAGE. */
	private static final String QUERY_ABSTRACTDEALCONDITION_BY_DATE_GARBAGE = "select {dc:pk} from {AbstractDealCondition AS dc} where {dc.dealconditiongroup} NOT IN ({{select DISTINCT {d.conditiongroup} from {deal AS d}}}) and {dc.creationtime}  <= ?date";

	/** The Constant QUERY_ABSTRACTDEALBENEFIT_BY_DATE. */
	private static final String QUERY_ABSTRACTDEALBENEFIT_BY_DATE = "select {db:pk} from {AbstractDealBenefit AS db} where {db.dealconditiongroup} IN ({{select DISTINCT {d.conditiongroup} from {deal AS d} where {d.validto} <= ?date }})";

	/** The Constant QUERY_ABSTRACTDEALBENEFIT_BY_DATE_GARBAGE. */
	private static final String QUERY_ABSTRACTDEALBENEFIT_BY_DATE_GARBAGE = "select {db:pk} from {AbstractDealBenefit AS db} where {db.dealconditiongroup} NOT IN ({{select DISTINCT {d.conditiongroup} from {deal AS d}}}) and {db.creationtime} <= ?date ";

	/** The Constant QUERY_DEAL_SCALES_BY_DATE. */
	private static final String QUERY_DEAL_SCALES_BY_DATE = "select {ds:pk} from {DealScale AS ds} where {ds.dealconditiongroup} IN ({{select DISTINCT {d.conditiongroup} from {deal AS d} where {d.validto} <= ?date }})";

	/** The Constant QUERY_DEAL_SCALES_BY_DATE_GARBAGE. */
	private static final String QUERY_DEAL_SCALES_BY_DATE_GARBAGE = "select {ds:pk} from {DealScale AS ds} where {ds.dealconditiongroup} NOT IN ({{select DISTINCT {d.conditiongroup} from {deal AS d}}}) and {ds.creationtime} <= ?date";

	/** The Constant QUERY_DEAL_CONDITION_BY_WITH_EXPIRED_DEALS. */
	private static final String QUERY_DEAL_CONDITION_BY_WITH_EXPIRED_DEALS = "select {dcg:pk} from {dealconditiongroup AS dcg} where {dcg.pk} IN ({{select DISTINCT {d.conditiongroup} from {deal AS d} where {d.validto} <= ?date }})";

	/** The Constant QUERY_DEAL_CONDITION_BY_DATE_GARBAGE. */
	private static final String QUERY_DEAL_CONDITION_BY_DATE_GARBAGE = "SELECT {dcg.pk} FROM {dealconditiongroup AS dcg LEFT JOIN deal AS d ON {dcg.pk} = {d.conditiongroup}} WHERE {d.conditiongroup} IS NULL AND {dcg.creationtime} <= ?date";

	/** The Constant QUERY_DEAL_CONDITION_BY_WITH_EXPIRED_DEALS. */
	private static final String QUERY_DEAL_ASSIGNEE_WITH_EXPIRED_DEALS = "select {da:pk} from {DealAssignee AS da} where {da.deal} IN ({{select DISTINCT {d.pk} from {deal AS d} where {d.validto} <= ?date }})";

	/** The Constant QUERY_DEAL_ASSIGNEE_WITH_EXPIRED_DEALS. */
	private static final String QUERY_DEAL_ASSIGNEE_BY_DATE_GARBAGE = "select {da:pk} from {DealAssignee AS da} where {da.deal} IS NULL and {da.creationtime} <= ?date";

	/** The Constant QUERY_CART_DEAL_CONDITION_WITH_EXPIRED_DEALS. */
	private static final String QUERY_CART_DEAL_CONDITION_WITH_EXPIRED_DEALS = "SELECT {cdc:pk} FROM {CartDealCondition as cdc JOIN Deal as d ON {cdc:deal}={d:pk}} WHERE {d.validto}<= ?date";

	/** The Constant QUERY_CART_DEAL_CONDITION_BY_NULL. */
	private static final String QUERY_CART_DEAL_CONDITION_BY_NULL = "select {cdc:pk} from {CartDealCondition AS cdc} where {cdc.deal} IS NULL";

	/** The Constant QUERY_CART_DEAL_CONDITION_BY_DATE_GARBAGE. */
	private static final String QUERY_CART_DEAL_CONDITION_BY_DATE_GARBAGE = "select {cdc:pk} from {CartDealCondition AS cdc} where {cdc.deal} NOT IN ({{select DISTINCT {d.pk} from {deal AS d}}}) and {cdc.creationtime} <= ?date ";

	/** The Constant QUERY_DEAL_BY_DATE. */
	private static final String QUERY_DEAL_BY_DATE = "SELECT {d:pk} FROM {Deal AS d} WHERE {d:ValidTo} < ?date";

	private static final String QUERY_SGA_DEALS_BY_CL = "SELECT {d:pk} FROM {"+AsahiDealModel._TYPECODE+" AS d JOIN "+ AsahiDealModel._ASAHICATALOGPRODUCTMAPPINGSTOASAHIDEALS +" AS ACD ON {ACD.target} = {d.pk} JOIN "+AsahiCatalogProductMappingModel._TYPECODE+" AS ACM ON {ACD.source}= {ACM.pk}} WHERE {ACM.catalogId} IN (?catalogHierarchies)";

	private static final String QUERY_SGA_DEALS_BY_CODE = "SELECT {d:pk} FROM {"+AsahiDealModel._TYPECODE+" AS d } WHERE {d.code} IN (?dealsToActivate)";

	private static final String QUERY_SGA_DEAL_BY_CODE = "SELECT {d:pk} FROM {" + AsahiDealModel._TYPECODE
			+ " AS d } WHERE {d.code} = ?code";

	/** The flexible search service. */
	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	/**
	 * Clean date.
	 *
	 * @param date
	 *           the date
	 * @return the date
	 */
	protected Date cleanDate(final Date date)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Find out the deals match current user and the filter what the user selected.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param fromDate
	 *           the from date
	 * @param toDate
	 *           the to date
	 * @param brand
	 *           the brand
	 * @param categoryCode
	 *           the category code
	 * @param specificDate
	 *           the specific date
	 * @return List<DealModel>
	 */
	@Override
	public List<DealModel> getDeals(final B2BUnitModel b2bUnitModel, final Date fromDate, final Date toDate,
			final List<String> brand, final List<String> categoryCode, final Date specificDate)
	{
		final Map<String, Object> params = new HashMap<>();

		final StringBuilder queryClause = new StringBuilder();

		queryClause.append(" WHERE {b:PK}=?bunit");
		params.put("bunit", b2bUnitModel);

		// If the from or to date is not empty,select the deals by from or to date
		builerDateClause(fromDate, toDate, specificDate, params, queryClause);

		//NOTE: IN is limited to 1000 items - may need to revisit this

		final StringBuilder query = new StringBuilder();
		if (isNotEmpty(categoryCode) && isNotEmpty(brand))
		{
			query.append(ADV_DEAL_QUERY);
			params.put("brand", brand);
			params.put("categoryCode", categoryCode);
			queryClause.append(" AND {pdc:").append(ProductDealConditionModel.PRODUCTCODE).append("} IN ({{")
					.append(CATEGORY_BRAND_CLAUSE_SUBQUERY).append("}})");

		}
		else if (isNotEmpty(categoryCode))
		{
			query.append(ADV_DEAL_QUERY);
			params.put("categoryCode", categoryCode);
			queryClause.append(" AND {pdc:").append(ProductDealConditionModel.PRODUCTCODE).append("} IN ({{")
					.append(CATEGORY_CLAUSE_SUBQUERY).append("}})");

		}
		else if (isNotEmpty(brand))
		{
			query.append(ADV_DEAL_QUERY);
			params.put("brand", brand);
			queryClause.append(" AND {pdc:").append(ProductDealConditionModel.PRODUCTCODE).append("} IN ({{")
					.append(BRAND_CLAUSE_SUBQUERY).append("}})");

		}
		else
		{
			query.append(BASIC_DEAL_QUERY);
		}

		LOG.debug("deal search query [{}{}] - params [{}]", query, queryClause, params);
		return queryDeals(query.toString() + queryClause.toString(), params);
	}

	protected <T> List<T> doSearch(final String query, final Map<String, Object> params, final int batchSize,
			final Class<T> resultClass)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		if (params != null)
		{
			fQuery.addQueryParameters(params);

			//will fetch all price row data if batch size is 0
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

		final SearchResult<T> searchResult = flexibleSearchService.search(fQuery);
		return searchResult.getResult();
	}


	/**
	 * Builer date clause.
	 *
	 * @param fromDate
	 *           the from date
	 * @param toDate
	 *           the to date
	 * @param specificDate
	 *           the specific date
	 * @param params
	 *           the params
	 * @param queryClause
	 *           the query clause
	 */
	private void builerDateClause(final Date fromDate, final Date toDate, final Date specificDate,
			final Map<String, Object> params, final StringBuilder queryClause)
	{
		if (fromDate != null || toDate != null)
		{
			if (fromDate != null && toDate != null)
			{
				queryClause.append(" AND NOT (").append("{d:").append(DealModel.VALIDFROM).append("} > ?validTo OR {d:")
						.append(DealModel.VALIDTO).append("} < ?validFrom)");
				params.put("validFrom", cleanDate(fromDate));
				params.put("validTo", cleanDate(toDate));
			}
			else if (fromDate != null)
			{
				queryClause.append(" AND ").append("{d:").append(DealModel.VALIDFROM).append("} <= ?validFrom AND {d:")
						.append(DealModel.VALIDTO).append("} >= ?validFrom");
				params.put("validFrom", cleanDate(fromDate));
			}
			else
			{
				queryClause.append(" AND ").append("{d:").append(DealModel.VALIDFROM).append("} <= ?validTo AND {d:")
						.append(DealModel.VALIDTO).append("} >= ?validTo");
				params.put("validTo", cleanDate(toDate));
			}
		}
		else if (null != specificDate)
		{
			final Date date = cleanDate(specificDate);
			queryClause.append(" AND {d:").append(DealModel.VALIDFROM).append("} <= ?validFrom");
			queryClause.append(" AND {d:").append(DealModel.VALIDTO).append("} >= ?validTo");
			params.put("validFrom", date);
			params.put("validTo", date);
		}
	}

	/**
	 * Checks if is not empty.
	 *
	 * @param list
	 *           the list
	 * @return true, if is not empty
	 */
	protected boolean isNotEmpty(final List<String> list)
	{
		if (!CollectionUtils.isEmpty(list))
		{
			for (final String str : list)
			{
				if (!StringUtils.isBlank(str))
				{
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getDeals(de.hybris.platform.b2b.model.B2BUnitModel, java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public List<DealModel> getDeals(final B2BUnitModel b2bUnitModel, final Date fromDate, final Date toDate)
	{

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("bunit", b2bUnitModel);
		params.put("validFrom", cleanDate(fromDate));
		params.put("validTo", cleanDate(toDate));
		return queryDeals(DEALS_QUERY, params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getDealsForProduct(de.hybris.platform.b2b.model.B2BUnitModel,
	 * java.lang.String, java.util.Date)
	 */
	@Override
	public List<DealModel> getDealsForProduct(final B2BUnitModel b2bUnitModel, final List<String> productCode, final Date fromDate,
			final Date toDate)
	{
		final Map<String, Object> params = new HashMap<>();
		final StringBuilder queryClause = new StringBuilder();

		queryClause.append(" WHERE {d:b2bUnit}=?bunit");
		params.put("bunit", b2bUnitModel);
		queryClause.append(" AND {pdc:productCode} IN (?productCode)");
		params.put("productCode", productCode);

		// If the from or to date is not empty,select the deals by from or to date
		builerDateClause(fromDate, toDate, null, params, queryClause);
		LOG.debug("deal search query [{}{}] - params [{}]", DEALS_BY_PRODUCT_QUERY, queryClause, params);
		return queryDeals(DEALS_BY_PRODUCT_QUERY + queryClause.toString(), params);
	}

	/**
	 * Query deals.
	 *
	 * @param query
	 *           the query
	 * @param params
	 *           the params
	 * @return the list
	 */
	private List<DealModel> queryDeals(final String query, final Map<String, Object> params)
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query, params);
		final SearchResult<DealModel> result = flexibleSearchService.search(fsq);

		return ListUtils.emptyIfNull(result.getResult());
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getDeal(java.lang.String)
	 */
	@Override
	public DealModel getDeal(final String dealCode)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("dealCode", dealCode);
		final List<DealModel> deals = queryDeals(QUERY_DEAL_BY_CODE, params);
		return !deals.isEmpty() ? deals.get(0) : null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getDealsByType(de.hybris.platform.b2b.model.B2BUnitModel,
	 * com.sabmiller.core.enums.DealTypeEnum)
	 */
	@Override
	public List<DealModel> getDealsByType(final B2BUnitModel b2bUnitModel, final DealTypeEnum dealType)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("bunit", b2bUnitModel);
		params.put("dealType", dealType);
		return queryDeals(DEALS_QUERY_BY_TYPE, params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getComplexDeals(de.hybris.platform.b2b.model.B2BUnitModel)
	 */
	@Override
	public List<DealModel> getComplexDeals(final B2BUnitModel b2bUnitModel)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("b2bUnit", "%" + b2bUnitModel.getUid() + "%");
		params.put("dealType", DealTypeEnum.COMPLEX);
		return queryDeals(COMPLEX_DEALS_QUERY, params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getComplexDeals()
	 */
	@Override
	public List<DealModel> getComplexDeals()
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("dealType", DealTypeEnum.COMPLEX);
		return queryDeals(COMPLEX_DEALS_ALL_QUERY, params);
	}


	@Override
	public List<DealModel> getComplexDealsToDate(final Date toDate)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(DealModel.DEALTYPE, DealTypeEnum.COMPLEX);
		params.put(DealModel.VALIDTO,toDate);
		return queryDeals(COMPLEX_DEALS_FROM_DATE_QUERY, params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getNonComplexDeals(de.hybris.platform.b2b.model.B2BUnitModel, boolean)
	 */
	@Override
	public List<DealModel> getNonComplexDeals(final B2BUnitModel b2bUnit, final boolean inStore)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("b2bUnit", b2bUnit);
		params.put("inStore", inStore);
		params.put("toDate", cleanDate(new Date()));
		params.put("dealType", DealTypeEnum.COMPLEX);
		LOG.debug("The Rep-Driven exclusive deals search query [{}] - params [{}]", REPDRIVEN_EXCLUSIVE_DEALS_QUERY, params);
		return queryDeals(REPDRIVEN_EXCLUSIVE_DEALS_QUERY, params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getAbstractDealCondition(java.lang.String)
	 */
	@Override
	public List<AbstractDealConditionModel> getAbstractDealCondition(final Date date, final int batchSize)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		final List<AbstractDealConditionModel> abstractDealCondition = new ArrayList<AbstractDealConditionModel>();
		abstractDealCondition
				.addAll(doSearch(QUERY_ABSTRACTDEALCONDITION_BY_DATE, params, batchSize, AbstractDealConditionModel.class));
		abstractDealCondition
				.addAll(doSearch(QUERY_ABSTRACTDEALCONDITION_BY_DATE_GARBAGE, params, batchSize, AbstractDealConditionModel.class));
		return abstractDealCondition;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getAbstractDealBenefit(java.lang.String)
	 */
	@Override
	public List<AbstractDealBenefitModel> getAbstractDealBenefit(final Date date, final int batchSize)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		final List<AbstractDealBenefitModel> abstractdealbenefit = new ArrayList<AbstractDealBenefitModel>();
		abstractdealbenefit.addAll(doSearch(QUERY_ABSTRACTDEALBENEFIT_BY_DATE, params, batchSize, AbstractDealBenefitModel.class));
		abstractdealbenefit
				.addAll(doSearch(QUERY_ABSTRACTDEALBENEFIT_BY_DATE_GARBAGE, params, batchSize, AbstractDealBenefitModel.class));
		return abstractdealbenefit;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getDealsScales(java.lang.String)
	 */
	@Override
	public List<DealScaleModel> getDealsScales(final Date date, final int batchSize)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		final List<DealScaleModel> dealScale = new ArrayList<DealScaleModel>();
		dealScale.addAll(doSearch(QUERY_DEAL_SCALES_BY_DATE, params, batchSize, DealScaleModel.class));
		dealScale.addAll(doSearch(QUERY_DEAL_SCALES_BY_DATE_GARBAGE, params, batchSize, DealScaleModel.class));
		return dealScale;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getDealConditionGroup(java.lang.String)
	 */
	@Override
	public List<DealConditionGroupModel> getDealConditionGroup(final Date date, final int batchSize)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		return doSearch(QUERY_DEAL_CONDITION_BY_DATE_GARBAGE, params, batchSize, DealConditionGroupModel.class);
	}

	@Override
	public List<DealConditionGroupModel> getDealConditionGroupForExpiredDeals(final Date date, final int batchSize) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		return doSearch(QUERY_DEAL_CONDITION_BY_WITH_EXPIRED_DEALS, params, batchSize, DealConditionGroupModel.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getDealAssignee(java.lang.String)
	 */
	@Override
	public List<DealAssigneeModel> getDealAssignee(final Date date, final int batchSize)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		return doSearch(QUERY_DEAL_ASSIGNEE_BY_DATE_GARBAGE, params, batchSize, DealAssigneeModel.class);
	}

	@Override
	public List<DealAssigneeModel> getDealAssigneeForExpiredDeals(final Date date, final int batchSize) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		return doSearch(QUERY_DEAL_ASSIGNEE_WITH_EXPIRED_DEALS, params, batchSize, DealAssigneeModel.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getCartDealCondition(java.lang.String)
	 */
	@Override
	public List<CartDealConditionModel> getCartDealCondition(final Date date, final int batchSize)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		return doSearch(QUERY_CART_DEAL_CONDITION_BY_DATE_GARBAGE, params, batchSize, CartDealConditionModel.class);
	}

	@Override
	public List<CartDealConditionModel> getCartDealConditionForExpiredDeals(final Date date, final int batchSize) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		return doSearch(QUERY_CART_DEAL_CONDITION_WITH_EXPIRED_DEALS, params, batchSize, CartDealConditionModel.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#getDealsbeforethirtydays(java.lang.String)
	 */
	@Override
	public List<DealModel> getDealsbeforethirtydays(final Date date, final int batchSize)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		return doSearch(QUERY_DEAL_BY_DATE, params, batchSize, DealModel.class);
	}

	@Override
	public List<AsahiDealModel> getSGASpecificDeals(final Collection<String> catalogHierarchies)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("catalogHierarchies", catalogHierarchies);
		params.put("date", new Date());
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(QUERY_SGA_DEALS_BY_CL);
		fQuery.addQueryParameters(params);
		final SearchResult<AsahiDealModel> searchResult = flexibleSearchService.search(fQuery);
		return searchResult.getResult();
	}

	@Override
	public List<AsahiDealModel> getSgaDealsForCode(final List<String> dealsToActivate)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("dealsToActivate", dealsToActivate);
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(QUERY_SGA_DEALS_BY_CODE);
		fQuery.addQueryParameters(params);
		final SearchResult<AsahiDealModel> searchResult = flexibleSearchService.search(fQuery);
		return searchResult.getResult();

	}

	@Override
	public AsahiDealModel getSgaDealByCode(final String dealCode)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", dealCode);
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(QUERY_SGA_DEAL_BY_CODE);
		fQuery.addQueryParameters(params);
		final SearchResult<AsahiDealModel> searchResult = flexibleSearchService.search(fQuery);
		return CollectionUtils.isNotEmpty(searchResult.getResult()) ? searchResult.getResult().get(0) : null;


	}


}
