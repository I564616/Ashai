/**
 *
 */
package com.sabmiller.core.product.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.daos.impl.DefaultProductDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.List;
import java.util.Date;
import java.util.Map;

import jakarta.annotation.Resource;

import org.joda.time.DateTime;

import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.enums.LifecycleStatusType;
import com.sabmiller.core.enums.SAPAvailabilityStatus;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class DefaultSabmProductDao.
 *
 * @author joshua.a.antony
 */
public class DefaultSabmProductDao extends DefaultProductDao implements SabmProductDao
{

	/**
	 * Instantiates a new default sabm product dao.
	 *
	 * @param typecode
	 *           the typecode
	 */
	public DefaultSabmProductDao(final String typecode)
	{
		super(typecode);
	}

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;

	/** The Constant PRODUCT_BY_CODE_HIERARCHY_QUERY. */
	private static final String PRODUCT_BY_CODE_HIERARCHY_QUERY = "SELECT {" + SABMAlcoholVariantProductMaterialModel.PK + "} "
			+ "FROM {" + SABMAlcoholVariantProductMaterialModel._TYPECODE + "} WHERE {" + SABMAlcoholVariantProductMaterialModel.CODE
			+ "}=?code AND {" + SABMAlcoholVariantProductMaterialModel.HIERARCHY + "}=?hierarchy";

	/** The Constant PRODUCT_WITHOUT_IMAGES_QUERY. */
	private static final String PRODUCT_WITHOUT_IMAGES_QUERY = "SELECT {" + SABMAlcoholVariantProductEANModel.PK + "} FROM {"
			+ SABMAlcoholVariantProductEANModel._TYPECODE + "} WHERE {" + SABMAlcoholVariantProductEANModel.GALLERYIMAGES
			+ "} is null";

	/** The Constant PRODUCT_EAN_BY_MATERIAL_QUERY. */
	private static final String PRODUCT_EAN_BY_MATERIAL_QUERY = "SELECT {" + SABMAlcoholVariantProductMaterialModel.PK + "} FROM {"
			+ SABMAlcoholVariantProductMaterialModel._TYPECODE + "} WHERE {" + SABMAlcoholVariantProductMaterialModel.BASEPRODUCT
			+ "}=?eanProduct";

	/** The Constant DEALS_BRAND_QUERY_BY_PRODUCT. */
	private static final String DEALS_BRAND_QUERY_BY_PRODUCT = "SELECT {pk} from {SABMAlcoholProduct} where {level2}=?dealBrand";

	/** The Constant PRODUCT_MATERIAL_BY_LEVEL2_QUERY. */
	private static final String PRODUCT_MATERIAL_BY_LEVEL2_QUERY = "SELECT DISTINCT {mp:"
			+ SABMAlcoholVariantProductMaterialModel.PK + "} FROM {" + SABMAlcoholVariantProductMaterialModel._TYPECODE
			+ " AS mp JOIN " + SABMAlcoholVariantProductEANModel._TYPECODE + " AS ep ON {mp:baseProduct:PK}={ep:PK} JOIN "
			+ SABMAlcoholProductModel._TYPECODE + " AS ap ON {ep:baseProduct:PK}={ap:PK}} WHERE {ap:"
			+ SABMAlcoholProductModel.LEVEL2 + "} = ?dealBrand";

	/** The Constant PRODUCT_MATERIAL_BY_LEVELS_QUERY. */
	private static final String PRODUCT_MATERIAL_BY_LEVELS_QUERY = "SELECT DISTINCT {mp:"
			+ SABMAlcoholVariantProductMaterialModel.PK + "} FROM {" + SABMAlcoholVariantProductMaterialModel._TYPECODE
			+ " AS mp JOIN " + SABMAlcoholVariantProductEANModel._TYPECODE + "! AS ep ON {mp:baseProduct:PK}={ep:PK} JOIN "
			+ SABMAlcoholProductModel._TYPECODE + "! AS ap ON {ep:baseProduct:PK}={ap:PK}} WHERE";

	/*private static final String VIEWABLE_PRODUCT_EAN = "SELECT {" + SABMAlcoholVariantProductEANModel.PK + "} FROM {" + SABMAlcoholVariantProductEANModel._TYPECODE + "!} "
	+ "WHERE {" + SABMAlcoholVariantProductEANModel.CATALOGVERSION + "} = ?catalogVersion "
	+ "AND {" + SABMAlcoholVariantProductEANModel.LIFECYCLESTATUS + "} IN ({{SELECT {l:pk} FROM {" + LifecycleStatusType._TYPECODE + " AS l} "
	+ "WHERE {l:code} IN ('" + LifecycleStatusType.LIVE + "', '" + LifecycleStatusType.PREVIEW + "', '" + LifecycleStatusType.OBSOLETE + "')}}) "
      + "AND {" + SABMAlcoholVariantProductEANModel.SAPAVAILABILITYSTATUS + "} IN ({{SELECT {s:pk} FROM {" + SAPAvailabilityStatus._TYPECODE + " AS s} "
      + "WHERE {s:code} IN ('" + SAPAvailabilityStatus.X6 + "', '" + SAPAvailabilityStatus.X7 + "', '" + SAPAvailabilityStatus.X8 + "')}}) ";*/


   private static final String VIEWABLE_PRODUCT_EAN = "SELECT {" + SABMAlcoholVariantProductEANModel.PK + "} FROM {" + SABMAlcoholVariantProductEANModel._TYPECODE + "!} "
   	+ "WHERE {" + SABMAlcoholVariantProductEANModel.CATALOGVERSION + "} = ?catalogVersion "
   	+ "AND {" + SABMAlcoholVariantProductEANModel.LIFECYCLESTATUS + "} IN ({{SELECT {l:pk} FROM {" + LifecycleStatusType._TYPECODE + " AS l} "
   	+ "WHERE {l:code} IN ('" + LifecycleStatusType.LIVE + "', '" + LifecycleStatusType.PREVIEW + "', '" + LifecycleStatusType.OBSOLETE + "')}}) "
         + "AND {" + SABMAlcoholVariantProductEANModel.SAPAVAILABILITYSTATUS + "} IN ({{SELECT {s:pk} FROM {" + SAPAvailabilityStatus._TYPECODE + " AS s} "
         + "WHERE {s:code} IN ('" + SAPAvailabilityStatus.X6 + "', '" + SAPAvailabilityStatus.X7 + "')}}) ";

	private static final String SAP_AVAILABILITY_STATUS = " {ep.SapAvailabilityStatus} in ({{select {s:pk} from {SAPAvailabilityStatus AS s} WHERE {s:code} in (?sapAvailibilityStatuses)}})";

	private static final String PRODUCT_MATERIAL_BY_CODE = "SELECT {"+SABMAlcoholVariantProductMaterialModel.PK+"} FROM {"+SABMAlcoholVariantProductMaterialModel._TYPECODE+"} WHERE {"+SABMAlcoholVariantProductMaterialModel.CODE+"}=?"+SABMAlcoholVariantProductMaterialModel.CODE;

	private static final String GET_KEG_MATERIALS = "SELECT DISTINCT {mp:" + SABMAlcoholVariantProductMaterialModel.PK + "} FROM {"
			+ SABMAlcoholVariantProductMaterialModel._TYPECODE + " AS mp JOIN " + SABMAlcoholVariantProductEANModel._TYPECODE
			+ " AS ep ON {mp:baseProduct:PK}={ep:PK}} WHERE {ep:" + SABMAlcoholVariantProductEANModel.CONTAINER + "} = ?Keg AND {mp:"
			+ SABMAlcoholVariantProductMaterialModel.CATALOGVERSION + "} = ?catalogVersion AND {ep:leadsku}={mp.pk} ";

	private static final String FIND_ORDER_ENTRIES_FOR_CUSTOMER_RULE = "SELECT {OE:" + OrderEntryModel.PK + "} FROM {"
			+ OrderEntryModel._TYPECODE + " AS OE JOIN " + OrderModel._TYPECODE + " AS O ON {OE:" + OrderEntryModel.ORDER + "}={O:"
			+ OrderModel.PK + "}} WHERE {OE:" + OrderEntryModel.PRODUCT + "} = ?product AND {O:" + OrderModel.SITE
			+ "} = ?site AND {O:" + OrderModel.UNIT + "} =?unit AND {O:" + OrderModel.CREATIONTIME + "} >= ?startDate AND {O:"
			+ OrderModel.CREATIONTIME + "} <= ?endDate AND {O:" + OrderModel.STATUS + "} != ?orderStatus";
	private static final String FIND_ORDER_ENTRIES_FOR_PLANT_RULE = "SELECT {OE:" + OrderEntryModel.PK + "} FROM {"
			+ OrderEntryModel._TYPECODE + " AS OE JOIN " + OrderModel._TYPECODE + " AS O ON {OE:" + OrderEntryModel.ORDER + "}={O:"
			+ OrderModel.PK + "} JOIN " + B2BUnitModel._TYPECODE + " AS BU ON {BU:" + B2BUnitModel.PK + "}={O:" + OrderModel.UNIT
			+ "}} WHERE {OE:" + OrderEntryModel.PRODUCT + "} = ?product AND {O:" + OrderModel.SITE + "} = ?site AND {BU:"
			+ B2BUnitModel.PLANT + "} =?plant AND {O:" + OrderModel.CREATIONTIME + "} >= ?startDate AND {O:"
			+ OrderModel.CREATIONTIME + "} <= ?endDate AND {O:" + OrderModel.STATUS + "} != ?orderStatus";
	private static final String FIND_ORDER_ENTRIES_FOR_GLOBAL_RULE = "SELECT {OE:" + OrderEntryModel.PK + "} FROM {"
			+ OrderEntryModel._TYPECODE + " AS OE JOIN " + OrderModel._TYPECODE + " AS O ON {OE:" + OrderEntryModel.ORDER + "}={O:"
			+ OrderModel.PK + "}} WHERE {OE:" + OrderEntryModel.PRODUCT + "} = ?product AND {O:" + OrderModel.SITE
			+ "} = ?site AND {O:" + OrderModel.CREATIONTIME + "} >= ?startDate AND {O:" + OrderModel.CREATIONTIME
			+ "} <= ?endDate AND {O:" + OrderModel.STATUS + "} != ?orderStatus";
	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.dao.SabmProductDao#productExist(java.lang.String, java.lang.String)
	 */
	@Override
	public SABMAlcoholVariantProductMaterialModel findProductByCodeAndHierarchy(final String code, final String heirarchy)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("hierarchy", heirarchy);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(PRODUCT_BY_CODE_HIERARCHY_QUERY, params);
		final SearchResult<SABMAlcoholVariantProductMaterialModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.dao.SabmProductDao#findProductEANsWithoutImages()
	 */
	@Override
	public List<SABMAlcoholVariantProductEANModel> findProductEANsWithoutImages()
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(PRODUCT_WITHOUT_IMAGES_QUERY);
		final SearchResult<SABMAlcoholVariantProductEANModel> result = getFlexibleSearchService().search(fsq);
		return result.getResult();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.dao.SabmProductDao#findMaterialProductByEan(java.lang.String)
	 */
	@Override
	public SABMAlcoholVariantProductMaterialModel findMaterialProductByEan(final SABMAlcoholVariantProductEANModel eanProductModel)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("eanProduct", eanProductModel);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(PRODUCT_EAN_BY_MATERIAL_QUERY, params);
		final SearchResult<SABMAlcoholVariantProductMaterialModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.dao.DealsDao#SABMAlcoholProduct(java.lang.String)
	 */
	@Override
	public SABMAlcoholProductModel getSABMAlcoholProduct(final String dealBrand)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("dealBrand", dealBrand);
		return querySABMAlcoholProduct(DEALS_BRAND_QUERY_BY_PRODUCT, params);
	}


	/**
	 * Query sabm alcohol product.
	 *
	 * @param query
	 *           the query
	 * @param params
	 *           the params
	 * @return the SABM alcohol product model
	 */
	private SABMAlcoholProductModel querySABMAlcoholProduct(final String query, final Map<String, Object> params)
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query, params);
		final SearchResult<SABMAlcoholProductModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}

	/**
	 * Get the product by the Product's level2.
	 *
	 * @param dealBrand
	 *           the deal brand
	 * @return List<SABMAlcoholVariantProductMaterialModel>
	 */
	@Override
	public List<SABMAlcoholVariantProductMaterialModel> getProductByLevel2(final String dealBrand)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("dealBrand", dealBrand);
		return querySABMVariantProductMaterialProduct(PRODUCT_MATERIAL_BY_LEVEL2_QUERY, params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.dao.SabmProductDao#getProductByHierarchy(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<SABMAlcoholVariantProductMaterialModel> getProductByHierarchy(final String level1, final String level2,
			final String level3, final String level4, final String level5, final String level6)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		String query = PRODUCT_MATERIAL_BY_LEVELS_QUERY;
		boolean first = true;

		if (StringUtils.isNotEmpty(level1))
		{
			query += first ? "" : " AND";
			query += " {ap.level1}=?level1";
			params.put("level1", level1);
			first = false;
		}

		if (StringUtils.isNotEmpty(level2))
		{
			query += first ? "" : " AND";
			query += " {ap.level2}=?level2";
			params.put("level2", level2);
			first = false;
		}

		if (StringUtils.isNotEmpty(level3))
		{
			query += first ? "" : " AND";
			query += " {ap.level3}=?level3";
			params.put("level3", level3);
			first = false;
		}

		if (StringUtils.isNotEmpty(level4))
		{
			query += first ? "" : " AND";
			query += " {ep.level4}=?level4";
			params.put("level4", level4);
			first = false;
		}

		if (StringUtils.isNotEmpty(level5))
		{
			query += first ? "" : " AND";
			query += " {ep.level5}=?level5";
			params.put("level5", level5);
			first = false;
		}

		if (StringUtils.isNotEmpty(level6))
		{
			query += first ? "" : " AND";
			query += " {ep.level6}=?level6";
			params.put("level6", level6);
			first = false;
		}

		//Fetch product which sap product status are configured  to visible to user(Ex: X6,X7,X8)
		//No need to fetch products which are not belongs to sap availability statuses.

		if (CollectionUtils.isNotEmpty(sabmConfigurationService.getValidSapProductStatus()))
		{
			final List<String> sapProductAvailibilityStatuses = sabmConfigurationService.getValidSapProductStatus();
			query += first ? "" : " AND";
			query += SAP_AVAILABILITY_STATUS;
			params.put("sapAvailibilityStatuses", sapProductAvailibilityStatuses);
			first = false;
		}

		return querySABMVariantProductMaterialProduct(query, params);
	}

	/**
	 * Retrieve all materials filtered by lifecycle status and SAP availability status
	 *
	 * @param catalogVersion
	 */
	@Override
	public List<SABMAlcoholVariantProductEANModel> getMaterialProducts(final CatalogVersionModel catalogVersion) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("catalogVersion", catalogVersion);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(VIEWABLE_PRODUCT_EAN, params);
		return getFlexibleSearchService().<SABMAlcoholVariantProductEANModel>search(fsq).getResult();
	}

	/**
	 * Query sabm variant product material product.
	 *
	 * @param query
	 *           the query
	 * @param params
	 *           the params
	 * @return the list
	 */
	private List<SABMAlcoholVariantProductMaterialModel> querySABMVariantProductMaterialProduct(final String query,
			final Map<String, Object> params)
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query, params);
		final SearchResult<SABMAlcoholVariantProductMaterialModel> result = getFlexibleSearchService().search(fsq);
		return result.getCount() > 0 ? result.getResult() : Collections.emptyList();
	}

	@Override
	public List<SABMAlcoholVariantProductMaterialModel> findMaterialsByCode(final String code) {
		final SearchResult<SABMAlcoholVariantProductMaterialModel> searchResult = getFlexibleSearchService().search(PRODUCT_MATERIAL_BY_CODE,Collections.singletonMap(SABMAlcoholVariantProductMaterialModel.CODE,code));

		return searchResult.getResult();
	}

	/**
	 * @return the sabmConfigurationService
	 */
	public SabmConfigurationService getSabmConfigurationService()
	{
		return sabmConfigurationService;
	}

	/**
	 * @param sabmConfigurationService
	 *           the sabmConfigurationService to set
	 */
	public void setSabmConfigurationService(final SabmConfigurationService sabmConfigurationService)
	{
		this.sabmConfigurationService = sabmConfigurationService;
	}

	@Override
	public List<SABMAlcoholVariantProductMaterialModel> getKegMaterials(final CatalogVersionModel catalogVersion)
	{
		// YTODO Auto-generated method stub
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("Keg", "Keg");
		params.put("catalogVersion", catalogVersion);
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(GET_KEG_MATERIALS, params);
		final SearchResult<SABMAlcoholVariantProductMaterialModel> result = getFlexibleSearchService().search(fsq);
		final List<SABMAlcoholVariantProductMaterialModel> materials = result.getCount() > 0 ? result.getResult()
				: Collections.<SABMAlcoholVariantProductMaterialModel> emptyList();
		return materials;
	}

	@Override
	public List<OrderEntryModel> getOrderEntryForCustomerRule(final ProductModel productModel, final B2BUnitModel b2bUnitModel,
			final CMSSiteModel cmsSiteModel)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", new DateTime().minusWeeks(13).toDate());
		params.put("endDate", new Date());
		params.put("product", productModel);
		params.put("site", cmsSiteModel);
		params.put("unit", b2bUnitModel);
		params.put("orderStatus", OrderStatus.RETURNED);
		System.out.println("*********getOrderEntryForCustomerRule**********" +params.get("startDate") + "***********end date" + params.get("endDate"));
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ORDER_ENTRIES_FOR_CUSTOMER_RULE, params);
		System.out.println("*****************query" +query.toString());
		final SearchResult<OrderEntryModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}

	@Override
	public List<OrderEntryModel> getOrderEntryForGlobalRule(final ProductModel productModel, final CMSSiteModel cmsSiteModel)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		//DateTime().minusWeeks(13)
		params.put("startDate", new DateTime().minusWeeks(13).toDate());
		params.put("endDate", new Date());
		params.put("product", productModel);
		params.put("site", cmsSiteModel);
		params.put("orderStatus", OrderStatus.RETURNED);
		System.out.println("*********getOrderEntryForGlobalRule**********" +params.get("startDate") + "***********end date" + params.get("endDate"));
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ORDER_ENTRIES_FOR_GLOBAL_RULE, params);
		System.out.println("*****************query" +query.toString());
		final SearchResult<OrderEntryModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}

	@Override
	public List<OrderEntryModel> getOrderEntryForPlantRule(final ProductModel productModel, final PlantModel plantModel,
			final CMSSiteModel cmsSiteModel)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", new DateTime().minusWeeks(13).toDate());
		params.put("endDate", new Date());
		params.put("product", productModel);
		params.put("site", cmsSiteModel);
		params.put("plant", plantModel);
		params.put("orderStatus", OrderStatus.RETURNED);
		System.out.println("*********getOrderEntryForPlantRule**********" +params.get("startDate") + "***********end date" + params.get("endDate"));
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ORDER_ENTRIES_FOR_PLANT_RULE, params);
		System.out.println("*****************query" +query.toString());
		final SearchResult<OrderEntryModel> result = getFlexibleSearchService().search(query);
		if (CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult();
		}
		return null;
	}

}
