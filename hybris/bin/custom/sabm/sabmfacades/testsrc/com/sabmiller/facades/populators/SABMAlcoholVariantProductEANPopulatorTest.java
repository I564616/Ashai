/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.UnitModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;


/**
 * SABMAlcoholVariantProductEANPopulatorTest
 *
 * @author xiaowu.a.zhang
 * @data 2015-10-29
 *
 */
@UnitTest
public class SABMAlcoholVariantProductEANPopulatorTest
{
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@InjectMocks
	private SABMAlcoholVariantProductEANPopulator sabmAlcoholVariantProductEANPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulator()
	{
		final SABMAlcoholVariantProductEANModel sabvpeM = mock(SABMAlcoholVariantProductEANModel.class);
		final PK pk = PK.parse("123568");
		given(sabvpeM.getPk()).willReturn(pk);
		given(sabvpeM.getContainer()).willReturn("sabvpeM.getContainer");
		given(sabvpeM.getCapacity()).willReturn("sabvpeM.getCapacity");
		given(sabvpeM.getPresentation()).willReturn("sabvpeM.getPresentation");
		given(sabvpeM.getWeight()).willReturn("sabvpeM.getWeight");
		given(sabvpeM.getWidth()).willReturn("sabvpeM.getWidth");
		given(sabvpeM.getHeight()).willReturn("sabvpeM.getHeight");
		given(sabvpeM.getLength()).willReturn("sabvpeM.getLength");
		given(sabvpeM.getEan()).willReturn("sabvpeM.getEan");
		given(sabvpeM.getWetEligible()).willReturn(true);
		given(sabvpeM.getLevel4()).willReturn("C");
		final UnitModel unitModel = mock(UnitModel.class);
		given(unitModel.getName()).willReturn("Case1");
		given(sabvpeM.getUnit()).willReturn(unitModel);
		given(asahiSiteUtil.isCub()).willReturn(true);

		final ProductData productData = new ProductData();
		sabmAlcoholVariantProductEANPopulator.populate(sabvpeM, productData);

		Assert.assertEquals("sabvpeM.getContainer", productData.getContainer());
		Assert.assertEquals("sabvpeM.getCapacity", productData.getCapacity());
		Assert.assertEquals("sabvpeM.getPresentation", productData.getPresentation());
		Assert.assertEquals("sabvpeM.getWeight", sabvpeM.getWeight());
		Assert.assertEquals("sabvpeM.getWidth", sabvpeM.getWidth());
		Assert.assertEquals("sabvpeM.getHeight", sabvpeM.getHeight());
		Assert.assertEquals("sabvpeM.getLength", sabvpeM.getLength());
		Assert.assertEquals("sabvpeM.getEan", sabvpeM.getEan());
		Assert.assertEquals("Case1", productData.getUnit());
	}

}
