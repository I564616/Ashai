/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.product.CommerceProductService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;


/**
 * @author xiaowu.a.zhang
 *
 */
@UnitTest
public class SABMProductCategoriesPopulatorTest
{
	@Mock
	private Converter<CategoryModel, CategoryData> categoryConverter;
	@Mock
	private CommerceProductService commerceProductService;
	@Mock
	private ModelService modelService;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@InjectMocks
	private SABMProductCategoriesPopulator sabmProductCategoriesPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		sabmProductCategoriesPopulator.setModelService(modelService);
		sabmProductCategoriesPopulator.setCategoryConverter(categoryConverter);
		sabmProductCategoriesPopulator.setCommerceProductService(commerceProductService);
	}


	@Test
	public void testPopulate()
	{
		final VariantProductModel source = mock(VariantProductModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);
		final CategoryModel category1 = mock(CategoryModel.class);
		final CategoryModel category2 = mock(CategoryModel.class);
		final List<CategoryModel> supercategories = new ArrayList<CategoryModel>();
		supercategories.add(category1);
		supercategories.add(category2);
		final List<CategoryData> supercategoriesData = new ArrayList<CategoryData>();
		final CategoryData categoryData1 = mock(CategoryData.class);
		final CategoryData categoryData2 = mock(CategoryData.class);
		supercategoriesData.add(categoryData1);
		supercategoriesData.add(categoryData2);

		given(source.getBaseProduct()).willReturn(baseProduct);
		given(commerceProductService.getSuperCategoriesExceptClassificationClassesForProduct(baseProduct))
				.willReturn(supercategories);
		given(categoryConverter.convert(category1)).willReturn(categoryData1);
		given(categoryConverter.convert(category2)).willReturn(categoryData2);
		given(asahiSiteUtil.isCub()).willReturn(true);

		final ProductData result = new ProductData();
		sabmProductCategoriesPopulator.populate(source, result);

		Assert.assertEquals(2, result.getCategories().size());
		Assert.assertTrue(result.getCategories().contains(categoryData1));
		Assert.assertTrue(result.getCategories().contains(categoryData2));
	}

}
