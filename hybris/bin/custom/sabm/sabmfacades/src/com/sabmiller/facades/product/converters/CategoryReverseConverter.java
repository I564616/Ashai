/**
 *
 */
package com.sabmiller.facades.product.converters;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import org.apache.log4j.Logger;

import com.sabmiller.core.category.SabmCategoryService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;


/**
 * @author joshua.a.antony
 *
 */
public class CategoryReverseConverter implements Converter<CategoryData, CategoryModel>
{

	private static final Logger LOG = Logger.getLogger(CategoryReverseConverter.class);

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "categoryService")
	private SabmCategoryService categoryService;

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public CategoryModel convert(final CategoryData categoryData) throws ConversionException
	{
		final CategoryModel categoryModel = categoryService.categoryExist(categoryData.getCode()) ? categoryService
				.getCategoryForCode(catalogVersionDeterminationStrategy.offlineCatalogVersion(), categoryData.getCode())
				: modelService.<CategoryModel> create(CategoryModel.class);

		return convert(categoryData, categoryModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.dto.converter.Converter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public CategoryModel convert(final CategoryData categoryData, final CategoryModel categoryModel) throws ConversionException
	{
		categoryModel.setName(categoryData.getName());
		categoryModel.setCode(categoryData.getCode());
		categoryModel.setCatalogVersion(catalogVersionDeterminationStrategy.offlineCatalogVersion());

		LOG.info("name : " + categoryData.getName() + " , code : " + categoryData.getCode());

		return categoryModel;
	}

}
