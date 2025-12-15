package com.apb.core.dao.category.impl;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.constants.ApbQueryConstant;
import com.apb.core.dao.category.AsahiCategoryDao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.daos.impl.DefaultCategoryDao;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * The Class AsahiCategoryDaoImpl.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiCategoryDaoImpl extends DefaultCategoryDao implements AsahiCategoryDao{

	private static final String REFERENCE_CODE = "code";
	private static final String CATALOG_VERSION = "catalogVersion";
	/** The search restriction service. */
	@Resource(name="searchRestrictionService")
	private SearchRestrictionService searchRestrictionService;
	

	/**
	 * Find category by code.
	 *
	 * @param catalogVersion the catalog version
	 * @param code the code
	 * @return the category model
	 */
	@Override
	public CategoryModel findCategoryByCode(final CatalogVersionModel catalogVersion, final String code)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuilder builder = new StringBuilder(ApbQueryConstant.GET_CATEGORY_FOR_CODE);
		params.put(REFERENCE_CODE, code);
		params.put(CATALOG_VERSION, catalogVersion.getPk());
		
		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.addQueryParameters(params);
		this.searchRestrictionService.disableSearchRestrictions();
		final SearchResult<CategoryModel> result = getFlexibleSearchService().search(query);
		this.searchRestrictionService.enableSearchRestrictions();
		if(CollectionUtils.isNotEmpty(result.getResult())){
			return result.getResult().get(0);
		}
		return null;
	}
}
