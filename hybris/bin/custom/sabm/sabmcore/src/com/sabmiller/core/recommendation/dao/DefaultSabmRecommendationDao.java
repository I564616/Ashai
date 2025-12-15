/**
 *
 */
package com.sabmiller.core.recommendation.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchParameter;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchService;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.enums.RecommendationStatus;
import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.core.model.AsahiCatalogProductMappingModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.core.model.SmartRecommendationModel;


/**
 * @author raul.b.abatol.jr
 *
 */
public class DefaultSabmRecommendationDao extends AbstractItemDao implements SabmRecommendationDao
{
	protected final static String SELECTCLAUSE = "SELECT {" + SABMRecommendationModel.PK + "} FROM {"
			+ SABMRecommendationModel._TYPECODE + "} ";
	protected final static String ORDERBYCLAUSE = " ORDER BY {" + SABMRecommendationModel.RECOMMENDEDDATE + "} DESC";

	protected final static String FIND_RECOMMENDATION_FOR_B2BUNIT_ID = SELECTCLAUSE + "WHERE " + " {"
			+ SABMRecommendationModel.B2BUNIT + "}= ?b2bUnit " + "AND {" + SABMRecommendationModel.PK + "}= ?id " + ORDERBYCLAUSE;

	protected final static String FIND_RECOMMENDATION_FOR_B2BUNIT = SELECTCLAUSE + "WHERE " + " {"
			+ SABMRecommendationModel.B2BUNIT + "}= ?b2bUnit " + "AND {" + SABMRecommendationModel.STATUS + "}= ?status "
			+ ORDERBYCLAUSE;

	protected final static String FIND_RECOMMENDATION_FOR_B2BUNIT_PRODUCT_ID = SELECTCLAUSE + "WHERE " + " {"
			+ SABMRecommendationModel.B2BUNIT + "}= ?b2bUnit " + "AND {" + SABMRecommendationModel.PRODUCTCODE + "}= ?productID "
			+ "AND {" + SABMRecommendationModel.STATUS + "}= ?status " + ORDERBYCLAUSE;

	protected final static String FIND_RECOMMENDATION_FOR_B2BUNIT_DEAL_ID = SELECTCLAUSE + "WHERE " + " {"
			+ SABMRecommendationModel.B2BUNIT + "}= ?b2bUnit " + "AND {" + SABMRecommendationModel.DEALCODE + "}= ?dealID " + "AND {"
			+ SABMRecommendationModel.STATUS + "}= ?status " + ORDERBYCLAUSE;

	protected final static String FIND_PRODUCTRECOMMENDATION_ELIGIBLE_FOR_EXPIRY = SELECTCLAUSE + "WHERE" + "{"
			+ SABMRecommendationModel.RECOMMENDEDDATE + "} < ?expiryDate AND {" + SABMRecommendationModel.STATUS
			+ "} = ?status AND {" + SABMRecommendationModel.RECOMMENDATIONTYPE + "}=?productType";

	protected final static String FIND_DEALRECOMMENDATION_ELIGIBLE_FOR_EXPIRY = "SELECT {SR:" + SABMRecommendationModel.PK
			+ "} FROM {" + SABMRecommendationModel._TYPECODE + " AS SR JOIN " + DealModel._TYPECODE + " AS D ON {SR:"
			+ SABMRecommendationModel.DEALCODE + "}={D:" + DealModel.CODE + "}} WHERE {D:" + DealModel.VALIDTO
			+ "} < ?currentDate AND {SR:" + SABMRecommendationModel.STATUS + "} = ?status AND {SR:"
			+ SABMRecommendationModel.RECOMMENDATIONTYPE + "}=?dealType";

	protected final static String FIND_RECOMMENDEDDEAL_NOTINDEAL_ELIGIBLE_FOR_EXPIRY = "SELECT {SR:" + SABMRecommendationModel.PK
			+ "} FROM {" + SABMRecommendationModel._TYPECODE + " AS SR } WHERE {SR:" + SABMRecommendationModel.DEALCODE
			+ "} NOT IN ({{SELECT DISTINCT {D:" + DealModel.CODE + "} FROM {" + DealModel._TYPECODE + " AS D} WHERE {D:"
			+ DealModel.VALIDTO + "} < ?currentDate}}) AND {SR:" + SABMRecommendationModel.STATUS + "} = ?status AND {SR:"
			+ SABMRecommendationModel.RECOMMENDATIONTYPE + "}=?dealType";

	protected final static String FIND_SMART_RECOMMENDATION = "Select {" + SmartRecommendationModel.PK + "} FROM {"
			+ SmartRecommendationModel._TYPECODE + "} WHERE {" + SmartRecommendationModel.PRODUCT + "} = ?product "
			+ " AND {" + SmartRecommendationModel.TYPE + "} = ?type";

	protected final static String FIND_ALL_SMART_RECOMMENDATIONS = "SELECT {" + SmartRecommendationModel.PK + "} FROM {"
			+ SmartRecommendationModel._TYPECODE + "}";

	private static final String FIND_RECOMMENDATIONS_BY_B2BUNIT = SELECTCLAUSE + "WHERE " + " {" + SABMRecommendationModel.B2BUNIT
			+ "}= ?b2bUnit ";

	private static final String FILTER_CUB_RECOMMENDATIONS = " AND " + " {" + SABMRecommendationModel.ISASAHIRECOMMENDATION
			+ "}= ?isAsahi ";

	protected final static String CUB_RECOMMENDATIONS = "SELECT {" + SABMRecommendationModel.PK + "} FROM {"
			+ SABMRecommendationModel._TYPECODE + "} WHERE " + " {" + SABMRecommendationModel.ISASAHIRECOMMENDATION
			+ " } =?isAsahi ";
	
	protected final static String ASAHI_PRODUCT_MAPPING = "SELECT {" + AsahiCatalogProductMappingModel.PK + "} FROM {"
			+ AsahiCatalogProductMappingModel._TYPECODE + "} WHERE " + " {" + AsahiCatalogProductMappingModel.CATALOGID
			+ "} IN (?catalogIds) ";

	private static final String BRAND_NAME_ASC = "byBrandNameAsc";
	private static final String BRAND_NAME_DESC = "byBrandNameDesc";

	protected final static String ORDER_BY_BRAND_DESC = " ORDER BY {" + SABMRecommendationModel.APBPRODUCTBRANDNAME + "} DESC";
	protected final static String ORDER_BY_BRAND_ASC = " ORDER BY {" + SABMRecommendationModel.APBPRODUCTBRANDNAME + "} ASC";


	/** The paged flexible search service. */
	@Resource(name = "paginatedFlexibleSearchService")
	private PaginatedFlexibleSearchService paginatedFlexibleSearchService;


	@Override
	public List<SABMRecommendationModel> getRecommendations(final B2BUnitModel b2bUnit)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("b2bUnit", b2bUnit.getPk());
		params.put("status", RecommendationStatus.RECOMMENDED);
		return doSearch(FIND_RECOMMENDATION_FOR_B2BUNIT, params, SABMRecommendationModel.class);
	}

	@Override
	public List<SABMRecommendationModel> getRecommendationsByID(final B2BUnitModel b2bUnit, final String id)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("b2bUnit", b2bUnit.getPk());
		params.put("id", id);
		return doSearch(FIND_RECOMMENDATION_FOR_B2BUNIT_ID, params, SABMRecommendationModel.class);
	}

	@Override
	public List<SABMRecommendationModel> getRecommendationsByProductID(final B2BUnitModel b2bUnit, final String productID)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("b2bUnit", b2bUnit.getPk());
		params.put("productID", productID);
		params.put("status", RecommendationStatus.RECOMMENDED);
		return doSearch(FIND_RECOMMENDATION_FOR_B2BUNIT_PRODUCT_ID, params, SABMRecommendationModel.class);
	}

	@Override
	public List<SABMRecommendationModel> getRecommendationsByDealID(final B2BUnitModel b2bUnit, final String dealID)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("b2bUnit", b2bUnit.getPk());
		params.put("dealID", dealID);
		params.put("status", RecommendationStatus.RECOMMENDED);
		return doSearch(FIND_RECOMMENDATION_FOR_B2BUNIT_DEAL_ID, params, SABMRecommendationModel.class);
	}

	@Override
	public List<SABMRecommendationModel> getEligibleRecommendationsForExpiry(final Date expiryDate)
	{
		final Map<String, Object> params = new HashMap<>();
		final Date currentDate = new Date();
		params.put("expiryDate", expiryDate);
		params.put("status", RecommendationStatus.RECOMMENDED);
		params.put("productType", RecommendationType.PRODUCT);
		params.put("dealType", RecommendationType.DEAL);
		params.put("currentDate", currentDate);
		params.put("isAsahi", Boolean.FALSE);

		//Get Product Recommendations
		final List<SABMRecommendationModel> recommendationList = doSearch(
				FIND_PRODUCTRECOMMENDATION_ELIGIBLE_FOR_EXPIRY + FILTER_CUB_RECOMMENDATIONS, params,
				SABMRecommendationModel.class);

		//Get Deal Recommendations
		final List<SABMRecommendationModel> dealRecommendationList = doSearch(FIND_DEALRECOMMENDATION_ELIGIBLE_FOR_EXPIRY, params,
				SABMRecommendationModel.class);

		final List<SABMRecommendationModel> dealRecommendationnotindealList = doSearch(
				FIND_RECOMMENDEDDEAL_NOTINDEAL_ELIGIBLE_FOR_EXPIRY, params, SABMRecommendationModel.class);

		final List<SABMRecommendationModel> dealreclist = Stream
				.concat(dealRecommendationList.stream(), dealRecommendationnotindealList.stream()).collect(Collectors.toList());


		//Merging all the results into a single list
		if (CollectionUtils.isNotEmpty(recommendationList) && CollectionUtils.isNotEmpty(dealreclist))
		{
			return Stream.concat(recommendationList.stream(), dealreclist.stream()).collect(Collectors.toList());
		}
		else if (CollectionUtils.isNotEmpty(dealreclist))
		{
			return dealreclist;
		}

		return recommendationList;
	}

	@Override
	public SmartRecommendationModel getSmartRecommendation(final ProductModel product, final SmartRecommendationType type) {
		final Map<String, Object> params = new HashMap<>();
		params.put("product", product);
		params.put("type", type);

		return getFlexibleSearchService().searchUnique(new FlexibleSearchQuery(FIND_SMART_RECOMMENDATION, params));
	}

	@Override
	public List<SmartRecommendationModel> getAllSmartRecommendations() {
		final SearchResult<SmartRecommendationModel> searchResult = getFlexibleSearchService().<SmartRecommendationModel>search(FIND_ALL_SMART_RECOMMENDATIONS);
		return searchResult.getCount() > 1 ? searchResult.getResult() : Collections.emptyList();
	}

	protected <T> List<T> doSearch(final String query, final Map<String, Object> params, final Class<T> resultClass)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		if (params != null)
		{
			fQuery.addQueryParameters(params);
		}

		fQuery.setResultClassList(Collections.singletonList(resultClass));
		final SearchResult<T> searchResult = search(fQuery);
		return searchResult.getResult();
	}

	@Override
	protected <T> SearchResult<T> search(final FlexibleSearchQuery searchQuery)
	{
		return this.flexibleSearchService.search(searchQuery);
	}

	@Override
	public List<SABMRecommendationModel> getAllRecommendations()
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("isAsahi", Boolean.FALSE);
		return doSearch(CUB_RECOMMENDATIONS, params, SABMRecommendationModel.class);
	}


	@Override
	public SearchPageData<SABMRecommendationModel> getPagedRecommendationsByB2BUnit(final B2BUnitModel b2bUnit,
			final SearchPageData searchData, final String sortCode)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("b2bUnit", b2bUnit.getPk());
		final PaginatedFlexibleSearchParameter parameter = new PaginatedFlexibleSearchParameter();
		final StringBuilder query = new StringBuilder();
		query.append(FIND_RECOMMENDATIONS_BY_B2BUNIT);
		appendOrderByClauses(query, sortCode);
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString(),
				params);
		parameter.setFlexibleSearchQuery(flexibleSearchQuery);
		parameter.setSearchPageData(searchData);
		return this.paginatedFlexibleSearchService.search(parameter);
	}
	
	@Override
	public List<AsahiCatalogProductMappingModel> getProductMappingBasedOnCatalogId(List<String> catalogIds)
	{
		//String finalCatalogids = catalogIds.stream().collect(Collectors.joining(","));
		final Map<String, Object> params = new HashMap<>();
		params.put("catalogIds", catalogIds);
		return doSearch(ASAHI_PRODUCT_MAPPING, params, AsahiCatalogProductMappingModel.class);
	}


	/**
	 * @param query
	 * @param searchData
	 */
	private void appendOrderByClauses(final StringBuilder query, final String sortCode)
	{
		if (StringUtils.isNotBlank(sortCode))
		{
			if (sortCode.equalsIgnoreCase(BRAND_NAME_ASC))
   		{
				query.append(ORDER_BY_BRAND_ASC);
   		}

			else if (sortCode.equalsIgnoreCase(BRAND_NAME_DESC))
   		{
				query.append(ORDER_BY_BRAND_DESC);
   		}
   		else {
				query.append(ORDERBYCLAUSE);
   		}
		}

	}

	

}
