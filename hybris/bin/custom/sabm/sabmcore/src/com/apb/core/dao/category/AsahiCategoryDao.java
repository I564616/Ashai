package com.apb.core.dao.category;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.daos.CategoryDao;
import de.hybris.platform.category.model.CategoryModel;

/**
 * The Interface AsahiCategoryDao.
 * 
 * @author Kuldeep.Singh1
 */
public interface AsahiCategoryDao extends CategoryDao{

	/**
	 * Find category by code.
	 *
	 * @param catalogVersion the catalog version
	 * @param code the code
	 * @return the category model
	 */
	public CategoryModel findCategoryByCode(final CatalogVersionModel catalogVersion, final String code);
}
