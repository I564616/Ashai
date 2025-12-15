/**
 *
 */
package com.sabmiller.core.category;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;

import com.sabmiller.core.model.SABMAlcoholProductModel;


/**
 * @author joshua.a.antony
 *
 */
public interface SabmCategoryService extends CategoryService
{

	public CategoryModel getCategoryFromCategoryAttribute(String categoryAttribute);

	public CategoryModel createCategory(String categoryAttribute);

	public boolean categoryExist(final String code);

	/**
	 * If category exist, just update the product, else create the category first! The assumption is that in Hybris,
	 * there is only 1 category level, i.e a category will have 0 to many products, but cannot contain another
	 * categories. Also, the link from product to category is through the 'categoryAttribute' property in the product.
	 * This attribute will determine under which category the product needs to be created.
	 *
	 */
	public void attachProductInHybrisHierarchy(final SABMAlcoholProductModel productModel, String categoryAttribute);

}
