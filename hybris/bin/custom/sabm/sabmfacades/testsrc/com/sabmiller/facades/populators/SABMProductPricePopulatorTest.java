/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;

import org.apache.commons.configuration2.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.cdlvalue.service.SabmCDLValueService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmPriceRowService;


/**
 * SABMProductPricePopulatorTest
 *
 * @author yaopeng
 *
 */
@UnitTest
public class SABMProductPricePopulatorTest
{
	@InjectMocks
	private SABMProductPricePopulator sabmProductPricePopulator;

	@Mock
	private SabmPriceRowService priceRowService;
	@Mock
	private UserService userService;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@Mock
	private AsahiCoreUtil asahiCoreUtil;
	@Mock
	private SabmCDLValueService sabmCDLValueService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmProductPricePopulator.setUserService(userService);
		sabmProductPricePopulator.setPriceRowService(priceRowService);
		sabmProductPricePopulator.setPriceDataFactory(priceDataFactory);
	}

	@Test
	public void testPopulator()
	{
		final UserModel user = mock(UserModel.class);
		final PK pk = PK.parse("1234567");
		given(user.getPk()).willReturn(pk);
		given(user.getUid()).willReturn("test@test.com");
		given(userService.getCurrentUser()).willReturn(user);

		final SABMAlcoholVariantProductEANModel sabvpe = mock(SABMAlcoholVariantProductEANModel.class);
		final PriceRowModel priceRow = mock(PriceRowModel.class);

		final CurrencyModel currencyModel = mock(CurrencyModel.class);

		final PK pk1 = PK.parse("123456799");
		final PK pk2 = PK.parse("12345678");
		final PK pk3 = PK.parse("1234567800");
		given(sabvpe.getPk()).willReturn(pk1);
		given(priceRow.getPk()).willReturn(pk2);
		given(priceRow.getUser()).willReturn(user);
		given(priceRow.getBasePrice()).willReturn(Double.valueOf(12.00));
		given(priceRow.getPrice()).willReturn(Double.valueOf(10.00));
		given(currencyModel.getPk()).willReturn(pk3);
		given(currencyModel.getIsocode()).willReturn("USD");
		given(priceRow.getCurrency()).willReturn(currencyModel);
		given(sabvpe.getPackConfiguration()).willReturn("packconfig");
		given(priceRowService.getPriceRowByProduct(sabvpe)).willReturn(priceRow);
		given(asahiSiteUtil.isCub()).willReturn(true);
		final PriceDataType priceType = PriceDataType.BUY;
		final PriceData priceData = new PriceData();
		priceData.setCurrencyIso(currencyModel.getIsocode());
		priceData.setPriceType(priceType);
		priceData.setValue(BigDecimal.valueOf(priceRow.getBasePrice().doubleValue()));
		given(priceDataFactory.create(priceType, BigDecimal.valueOf(priceRow.getBasePrice().doubleValue()), currencyModel))
				.willReturn(priceData);
		final ProductData productData = new ProductData();
		given(asahiCoreUtil.isNAPUserForSite()).willReturn(false);
		given(sabvpe.getLevel4()).willReturn("C");
		given(sabvpe.getPresentation()).willReturn("4X6");
		given(sabmCDLValueService.getCDLPrice("C", "4X6")).willReturn(new BigDecimal(1.47));
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBigDecimal(SabmCoreConstants.CUB_WET_PRICE_PERCENTAGE)).willReturn(new BigDecimal(1.29));

		sabmProductPricePopulator.populate(sabvpe, productData);
		Assert.assertEquals(BigDecimal.valueOf(12.0), productData.getBasePrice().getValue());

	}
}
