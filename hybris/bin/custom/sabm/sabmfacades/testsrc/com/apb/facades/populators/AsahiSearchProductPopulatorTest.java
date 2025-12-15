/**
 *
 */
package com.apb.facades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.AsahiSearchProductData;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.cart.validation.strategy.AsahiBonusCartValidationStrategy;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.product.data.BrandData;
import com.apb.facades.product.data.PackageSizeData;
import com.apb.facades.product.data.UnitVolumeData;



/**
 * @author Saumya.Mittal1
 *
 */
@UnitTest
public class AsahiSearchProductPopulatorTest
{
	@InjectMocks
	private final AsahiSearchProductPopulator searchPopulator = new AsahiSearchProductPopulator();

	@Mock
	private AsahiBonusCartValidationStrategy asahiBonusCartValidationStrategy;

	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(asahiBonusCartValidationStrategy.getAllowedBonusQuantity(Mockito.anyString())).thenReturn(Long.valueOf(50));
		Mockito.when(asahiSiteUtil.isSga()).thenReturn(Boolean.TRUE);
		Mockito.when(asahiSiteUtil.isBDECustomer()).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testPopulate()
	{
		final ProductData source = new ProductData();
		source.setActive(Boolean.TRUE);
		source.setUrl("/p/1000000");
		source.setName("Lemonade");
		final BrandData brand = new BrandData();
		brand.setCode("brandcode");
		source.setApbBrand(brand);
		source.setCode("1000000");
		source.setMinQty(Integer.valueOf(1));
		source.setMaxQty(Integer.valueOf(10));
		final UnitVolumeData unitVolume = new UnitVolumeData();
		unitVolume.setCode("200006");
		source.setUnitVolume(unitVolume);
		final PackageSizeData packageSize = new PackageSizeData();
		packageSize.setCode("3000018");
		source.setPackageSize(packageSize);
		source.setDealsFlag(true);
		source.setDealsTitle(Arrays.asList("2 of X and get 1 Y FREE"));
		final AsahiSearchProductData target = new AsahiSearchProductData();
		searchPopulator.populate(source, target);
		Assert.assertEquals("Lemonade", target.getName());
		Assert.assertEquals(Boolean.TRUE, target.isDealsFlag());
	}

}
