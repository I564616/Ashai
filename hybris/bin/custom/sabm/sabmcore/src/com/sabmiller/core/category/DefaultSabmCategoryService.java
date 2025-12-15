/**
 *
 */
package com.sabmiller.core.category;

import de.hybris.platform.category.daos.CategoryDao;
import de.hybris.platform.category.impl.DefaultCategoryService;
import de.hybris.platform.category.model.CategoryModel;

import java.util.Collection;

import jakarta.annotation.Resource;

import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;


/**
 * @author joshua.a.antony
 *
 */
@SuppressWarnings("SE_BAD_FIELD")
public class DefaultSabmCategoryService extends DefaultCategoryService implements SabmCategoryService
{

	@Resource(name = "categoryDao")
	private CategoryDao categoryDao;

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.core.category.SabmCategoryService#getCategoryFromCategoryAttribute(com.sabmiller.core.enums.
	 * AlcoholCategoryAttribute)
	 */
	@Override
	public CategoryModel getCategoryFromCategoryAttribute(final String categoryAttribute)
	{
		return getCategoryForCode(catalogVersionDeterminationStrategy.offlineCatalogVersion(),
				getCategoryCodeFromCategoryAttribute(categoryAttribute));
	}


	@Override
	public boolean categoryExist(final String code)
	{
		final Collection<CategoryModel> categories = categoryDao.findCategoriesByCode(
				catalogVersionDeterminationStrategy.offlineCatalogVersion(), code);
		return categories != null && !categories.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sabmiller.core.category.SabmCategoryService#createCategory(com.sabmiller.core.enums.AlcoholCategoryAttribute)
	 */
	@Override
	public CategoryModel createCategory(final String categoryAttribute)
	{
		final String categoryName = getCategoryNameFromCategoryAttribute(categoryAttribute);
		final String code = getCategoryCodeFromCategoryAttribute(categoryAttribute);

		final CategoryModel categoryModel = getModelService().create(CategoryModel.class);
		categoryModel.setName(categoryName);
		categoryModel.setCode(code);
		categoryModel.setCatalogVersion(catalogVersionDeterminationStrategy.offlineCatalogVersion());

		getModelService().save(categoryModel);
		return categoryModel;
	}



	@Override
	public void attachProductInHybrisHierarchy(final SABMAlcoholProductModel productModel, final String categoryAttribute)
	{
		final CategoryModel categoryModel = findOrCreateCategory(categoryAttribute);
		categoryModel.getProducts().add(productModel);
		getModelService().save(categoryModel);
	}

	protected CategoryModel findOrCreateCategory(final String categoryAttribute)
	{
		final CategoryModel categoryModel = getCategoryFromCategoryAttribute(categoryAttribute);
		if (categoryModel == null)
		{
			return createCategory(categoryAttribute);
		}
		return categoryModel;
	}

	protected String getCategoryNameFromCategoryAttribute(final String categoryAttribute)
	{
		// YTODO Auto-generated method stub
		return categoryAttribute;//TODO : Joshua
	}

	protected String getCategoryCodeFromCategoryAttribute(final String categoryAttribute)
	{
		return "hy" + categoryAttribute;
	}


}
