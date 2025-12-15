package com.apb.core.service.category.impl;

import jakarta.annotation.Resource;

import com.apb.core.dao.category.AsahiCategoryDao;
import com.apb.core.service.category.AsahiCategoryService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.impl.DefaultCategoryService;
import de.hybris.platform.category.model.CategoryModel;

/**
 * The Class AsahiCategoryServiceImpl.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiCategoryServiceImpl extends DefaultCategoryService implements AsahiCategoryService {
	
	/** The asahi category dao. */
	@Resource(name="asahiCategoryDao")
	private AsahiCategoryDao asahiCategoryDao;
	
	/**
	 * Gets the category for code.
	 *
	 * @param catalogVersion the catalog version
	 * @param code the code
	 * @return the category for code
	 */
	@Override
	public CategoryModel getCategoryForCode(final CatalogVersionModel catalogVersion, final String code)
	{
		return this.asahiCategoryDao.findCategoryByCode(catalogVersion, code);
	}

}
