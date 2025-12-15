/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;



/**
 * @author xue.zeng
 *
 */
@UnitTest
public class SABMProductBasicPopulatorTest
{
	@Mock
	private ModelService modelService;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@InjectMocks
	private SABMProductBasicPopulator sabmProductBasicPopulator;
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		//sabmProductBasicPopulator = new SABMProductBasicPopulator();
		sabmProductBasicPopulator.setModelService(modelService);
	}

	@Test
	public void testPopulator()
	{
		final ProductModel mockModel = mock(ProductModel.class);
		given(mockModel.getSearchable()).willReturn(true);
		given(mockModel.getPurchasable()).willReturn(true);
		given(mockModel.getVisible()).willReturn(true);
		given(mockModel.getIsNewProduct()).willReturn(true);
		given(mockModel.getSellingName(Locale.ENGLISH)).willReturn(null);
		given(asahiSiteUtil.isCub()).willReturn(true);
		final ProductData productData = new ProductData();
		sabmProductBasicPopulator.populate(mockModel, productData);
		Assert.assertEquals(true, productData.isSearchable());
		Assert.assertEquals(true, productData.getPurchasable());
		Assert.assertEquals(true, productData.isVisible());
	}
}
