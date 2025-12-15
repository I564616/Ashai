/**
 *
 */
package com.apb.facades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.model.ApbProductModel;
import com.apb.core.model.BrandModel;
import com.apb.core.util.AsahiCoreUtil;
import com.apb.facades.deal.data.AsahiDealData;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.AsahiFreeGoodsDealBenefitModel;
import com.sabmiller.core.model.AsahiProductDealConditionModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.facades.populators.SABMProductUrlPopulator;


/**
 * @author Saumya.Mittal1
 *
 */
public class AsahiDealProductDataPopulatorTest
{

	@InjectMocks
	private final AsahiDealProductDataPopulator dealPopulator = new AsahiDealProductDataPopulator();


	@Mock
	private ApbProductGalleryImagesPopulator<ApbProductModel, ProductData> apbProductGalleryImagesPopulator;

	@Mock
	private SABMProductUrlPopulator sabmProductUrlPopulator;

	@Mock
	private SabmProductService productService;

	@Mock
	private AsahiCoreUtil asahiCoreUtil;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulate()
	{
		final AsahiDealData target = new AsahiDealData();

		final AsahiDealModel source = Mockito.mock(AsahiDealModel.class);
		final AsahiProductDealConditionModel condition = Mockito.mock(AsahiProductDealConditionModel.class);
		Mockito.when(condition.getQuantity()).thenReturn(Integer.valueOf(2));
		Mockito.when(condition.getProductCode()).thenReturn("pcode");
		final AsahiFreeGoodsDealBenefitModel benefit = Mockito.mock(AsahiFreeGoodsDealBenefitModel.class);
		Mockito.when(benefit.getQuantity()).thenReturn(Integer.valueOf(2));
		Mockito.when(benefit.getProductCode()).thenReturn("pcode");
		final ApbProductModel product = Mockito.mock(ApbProductModel.class);
		Mockito.when(product.getName()).thenReturn("pname");
		final BrandModel brand = Mockito.mock(BrandModel.class);
		Mockito.when(brand.getName()).thenReturn("brandname");
		Mockito.when(product.getBrand()).thenReturn(brand);
		Mockito.when(productService.getProductForCodeSafe(Mockito.anyString())).thenReturn(product);
		Mockito.when(condition.getProductCode()).thenReturn("pcode");
		Mockito.when(benefit.getProductCode()).thenReturn("pcode");
		Mockito.when(source.getDealCondition()).thenReturn(condition);
		Mockito.when(source.getDealBenefit()).thenReturn(benefit);
		Mockito.when(asahiCoreUtil.getAsahiDealTitle(source)).thenReturn("title");
		dealPopulator.populate(source , target );
		Assert.assertEquals(Integer.valueOf(2), target.getConditionProduct().getQty());
	}

}
