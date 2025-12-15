/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.converters.populator.ProductGalleryImagesPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;


/**
 * AcceleratorVariantOptionDataPopulatorTest
 *
 * @author xiaowu.a.zhang
 * @data 2015-11-04
 *
 */
@UnitTest
public class AcceleratorVariantOptionDataPopulatorTest
{
	@Mock
	private VariantsService variantsService;
	@Mock
	private UrlResolver<ProductModel> productModelUrlResolver;
	@Mock
	private Converter<ProductModel, StockData> stockConverter;
	@Mock
	private CommercePriceService commercePriceService;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private ProductGalleryImagesPopulator<ProductModel, ProductData> productGalleryImagesPopulator;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@Mock
	private ProductUOMPopulator productUOMPopulator;
	
	@InjectMocks
	private AcceleratorVariantOptionDataPopulator acceleratorVariantOptionDataPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		
		 // acceleratorVariantOptionDataPopulator = new AcceleratorVariantOptionDataPopulator();
		  acceleratorVariantOptionDataPopulator.setVariantsService(variantsService);
		  acceleratorVariantOptionDataPopulator.setProductModelUrlResolver(productModelUrlResolver);
		  acceleratorVariantOptionDataPopulator.setStockConverter(stockConverter);
		  acceleratorVariantOptionDataPopulator.setCommercePriceService(commercePriceService);
		  acceleratorVariantOptionDataPopulator.setPriceDataFactory(priceDataFactory);
		  acceleratorVariantOptionDataPopulator.setProductGalleryImagesPopulator(productGalleryImagesPopulator);
		  acceleratorVariantOptionDataPopulator.setProductUOMPopulator(productUOMPopulator);
	}

	@Test
	public void testPopulator()
	{
		final VariantProductModel variantProductModel = mock(VariantProductModel.class);
		final VariantTypeModel variantTypeModel = mock(VariantTypeModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);

		given(variantProductModel.getBaseProduct()).willReturn(baseProduct);
		given(variantProductModel.getName()).willReturn("variantProductModel.getName");
		given(baseProduct.getVariantType()).willReturn(variantTypeModel);
		given(variantProductModel.getBaseProduct()).willReturn(baseProduct);
		given(asahiSiteUtil.isCub()).willReturn(true);

		final VariantOptionData variantOptionData = new VariantOptionData();
		acceleratorVariantOptionDataPopulator.populate(variantProductModel, variantOptionData);

		Assert.assertEquals("variantProductModel.getName", variantOptionData.getName());

	}
}
