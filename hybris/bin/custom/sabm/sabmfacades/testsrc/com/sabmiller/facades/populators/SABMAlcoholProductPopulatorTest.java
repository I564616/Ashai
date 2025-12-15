/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.converters.populator.ProductBasicPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.VariantSelectedPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.SABMAlcoholProductModel;


/**
 * SABMAlcoholProductPopulatorTest
 *
 * @author xiaowu.a.zhang
 * @data 2015-10-29
 *
 */
@UnitTest
public class SABMAlcoholProductPopulatorTest
{
	@InjectMocks
	private SABMAlcoholProductPopulator sabmAlcoholProductPopulator;

	@Mock
	private ProductBasicPopulator<ProductModel, ProductData> productBasicPopulator;
	@Mock
	private VariantSelectedPopulator<ProductModel, ProductData> variantSelectedPopulator;
	@Mock
	private ProductPrimaryImagePopulator<ProductModel, ProductData> productPrimaryImagePopulator;
	@Mock
	private UrlResolver<ProductModel> productModelUrlResolver;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmAlcoholProductPopulator.setProductBasicPopulator(productBasicPopulator);
		sabmAlcoholProductPopulator.setVariantSelectedPopulator(variantSelectedPopulator);
		sabmAlcoholProductPopulator.setProductPrimaryImagePopulator(productPrimaryImagePopulator);
		sabmAlcoholProductPopulator.setProductModelUrlResolver(productModelUrlResolver);
	}

	@Test
	public void testPopulator()
	{
		final SABMAlcoholProductModel sabvpe = mock(SABMAlcoholProductModel.class);
		final PK pk = PK.parse("1234567");
		given(sabvpe.getPk()).willReturn(pk);
		given(sabvpe.getName()).willReturn("name1");
		given(sabvpe.getAbv()).willReturn("abv1");
		given(sabvpe.getStyle()).willReturn("style2");
		given(sabvpe.getCategoryVariety()).willReturn("categoryVariety3");
		given(sabvpe.getBrand()).willReturn("brand4");
		given(sabvpe.getSellingName()).willReturn("sellname");
		given(sabvpe.getPackConfiguration()).willReturn("packconfig");
		given(asahiSiteUtil.isCub()).willReturn(true);

		final ProductData productData = new ProductData();
		sabmAlcoholProductPopulator.populate(sabvpe, productData);
		Assert.assertEquals("name1", productData.getBaseName());
		Assert.assertEquals("abv1", productData.getAbv());
		Assert.assertEquals("style2", productData.getStyle());
		Assert.assertEquals("categoryVariety3", productData.getCategoryVariety());
		Assert.assertEquals("brand4", productData.getBrand());

	}
}
