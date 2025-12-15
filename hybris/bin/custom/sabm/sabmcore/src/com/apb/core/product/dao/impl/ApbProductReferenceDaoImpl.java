package com.apb.core.product.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.model.AlcoholTypeModel;
import com.apb.core.model.BrandModel;
import com.apb.core.model.FlavourModel;
import com.apb.core.model.ItemGroupsModel;
import com.apb.core.model.PackageSizeModel;
import com.apb.core.model.PackageTypeModel;
import com.apb.core.model.ProductGroupModel;
import com.apb.core.model.SubProductGroupModel;
import com.apb.core.model.UnitVolumeModel;
import com.apb.core.product.dao.ApbProductReferenceDao;
import com.apb.core.service.config.AsahiConfigurationService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * The Class ApbProductReferenceDaoImpl.
 * @author Kuldeep.Singh1
 */
public class ApbProductReferenceDaoImpl extends AbstractItemDao implements ApbProductReferenceDao{
	
	/** The Constant REFERENCE_CODE. */
	public static final String REFERENCE_CODE = "code";
	
	/** The Constant CURRENCY_ISO_CODE. */
	public static final String CURRENCY_ISO_CODE = "currencyIso";
	
	private static final String PREVIOUS_ORDER_ENTRIES_MONTHS = "previous.order.entries.month.sga";
	
	
	/** The search restriction service. */
	@Resource(name="searchRestrictionService")
	private SearchRestrictionService searchRestrictionService;
	
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	/**
	 * Gets the alcohol type for code.
	 *
	 * @param code the code
	 * @return the alcohol type for code
	 */
	@Override
	public AlcoholTypeModel getAlcoholTypeForCode(String code) {
		
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ALCOHOL_TYPE_FOR_CODE);
		params.put(REFERENCE_CODE, code);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<AlcoholTypeModel> result = getFlexibleSearchService().search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the product group for code.
	 *
	 * @param code the code
	 * @return the product group for code
	 */
	@Override
	public ItemGroupsModel getItemGroupForCode(String code) {

		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ITEM_GROUP_FOR_CODE);
		params.put(REFERENCE_CODE, code);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<ItemGroupsModel> result = getFlexibleSearchService().search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the package type for code.
	 *
	 * @param code the code
	 * @return the package type for code
	 */
	@Override
	public PackageTypeModel getPackageTypeForCode(String code) {
		
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_PACKAGE_TYPE_FOR_CODE);
		params.put(REFERENCE_CODE, code);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<PackageTypeModel> result = getFlexibleSearchService().search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the flavour for code.
	 *
	 * @param code the code
	 * @return the flavour for code
	 */
	@Override
	public FlavourModel getFlavourForCode(String code) {
		
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_FLAVOUR_FOR_CODE);
		params.put(REFERENCE_CODE, code);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<FlavourModel> result = getFlexibleSearchService().search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the brand for code.
	 *
	 * @param code the code
	 * @return the brand for code
	 */
	@Override
	public BrandModel getBrandForCode(String code) {

		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_BRAND_FOR_CODE);
		params.put(REFERENCE_CODE, code);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<BrandModel> result = getFlexibleSearchService().search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the unit for code.
	 *
	 * @param code the code
	 * @return the unit for code
	 */
	@Override
	public UnitModel getUnitForCode(String code) {
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_UNIT_FOR_CODE);
		params.put(REFERENCE_CODE, code);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<UnitModel> result = getFlexibleSearchService().search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the currency for iso code.
	 *
	 * @param currencyIso the currency iso
	 * @return the currency for iso code
	 */
	@Override
	public CurrencyModel getCurrencyForIsoCode(String currencyIso) {
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_CURRENCY_FOR_ISO_CODE);
		params.put(CURRENCY_ISO_CODE, currencyIso);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<CurrencyModel> result = getFlexibleSearchService().search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Find products by code.
	 *
	 * @param catalogVersion the catalog version
	 * @param code the code
	 * @return the list
	 */
	@Override
	public List<ProductModel> findProductsByCode(
			CatalogVersionModel catalogVersion, String code) {
		
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_PRODUCTS_FOR_CODE_AND_CATALOG_VERSION);
		params.put(REFERENCE_CODE, code);
		params.put("catalogVersion", catalogVersion.getPk());
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<ProductModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult();
		}
		return null;
	}

	/**
	 * Gets the package size for code.
	 *
	 * @param code the code
	 * @return the package size for code
	 */
	@Override
	public PackageSizeModel getPackageSizeForCode(String code) {
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_PACKAGE_SIZE_FOR_CODE);
		params.put(REFERENCE_CODE, code);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<PackageSizeModel> result = super.search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}
	
	/**
	 * Gets the unit volume for code.
	 *
	 * @param code the code
	 * @return the package size for code
	 */
	@Override
	public UnitVolumeModel getUnitVolumeForCode(String code) {
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_UNIT_VOLUME_FOR_CODE);
		params.put(REFERENCE_CODE, code);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<UnitVolumeModel> result = super.search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the sub product group for code.
	 *
	 * @param code the code
	 * @return the sub product group for code
	 */
	@Override
	public SubProductGroupModel getSubProductGroupForCode(String code) {
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_SUB_PRODUCT_GROUP_FOR_CODE);
		params.put(REFERENCE_CODE, code);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<SubProductGroupModel> result = super.search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}

	/**
	 * Gets the order entry by product and order id.
	 *
	 * @param productId the product id
	 * @param orderId the order id
	 * @return the order entry by product and order id
	 */
	@Override
	public List<OrderEntryModel> getOrderEntryByProductAndOrderId(String productId,
			String orderId) {
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_ORDER_ENTRY_FOR_PRODUCT_AND_ORDER_ID);
		params.put("product", productId);
		params.put("order", orderId);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<OrderEntryModel> result = super.search(query);
		
		return result.getResult();

	}

	@Override
	public ProductGroupModel getProductGroupForCode(String code) {
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_PRODUCT_GROUP_FOR_CODE);
		params.put(REFERENCE_CODE, code);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		
		final SearchResult<ProductGroupModel> result = super.search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}
	
	/** Get the last 3 months orders
	 * 
	 * @param user the current user
	 */
	@Override
	public List<OrderModel> findPreviousOrderEntries(final CustomerModel user){ 
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder queryBuilder = new StringBuilder(ApbQueryConstant.GET_PREVIOUS_ORDER_ENTRIES);
		params.put("user", user);
		
		Integer noOfMonths = Integer.parseInt(this.asahiConfigurationService.getString(PREVIOUS_ORDER_ENTRIES_MONTHS, "3"));
		Date currentDate = new Date(System.currentTimeMillis());
		params.put("currentDate", currentDate);
		
		//Multiplying 30 to reduce the no of Months
		Calendar calender = Calendar.getInstance();
		calender.setTime(currentDate);
		calender.add(Calendar.DATE, -30*noOfMonths);
		final Date previousDate =  calender.getTime();
		params.put("previousDate", previousDate);
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());
		query.addQueryParameters(params);		
		final SearchResult<OrderModel> result = super.search(query);
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult();
		}
		return null;
	}
}
