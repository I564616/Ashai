package com.apb.facades.category;

import de.hybris.platform.commercefacades.product.data.CategoryData;

/**
 * The Interface ApbCategoryFacade.
 * 
 * @author Kuldeep.Singh1
 */
@FunctionalInterface
public interface ApbCategoryFacade {
	
	/**
	 * Import category.
	 *
	 * @param categoryData the category data
	 */
	public void importCategory(CategoryData categoryData);
}
