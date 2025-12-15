/**
 *
 */
package com.sabmiller.facades.search.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.Map;

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
import com.sabmiller.core.product.SabmPriceRowService;


/**
 * @author xue.zeng
 *
 */
@UnitTest
public class SABMSearchResultProductPopulatorTest
{
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private SabmPriceRowService priceRowService;
	@Mock
	private ProductService productService;
	@Mock
	private CommonI18NService commonI18NService;
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

	@InjectMocks
	private SABMSearchResultProductPopulator sabmSearchResultProductPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		//  sabmSearchResultProductPopulator = new SABMSearchResultProductPopulator();
		  sabmSearchResultProductPopulator.setPriceDataFactory(priceDataFactory);
		  sabmSearchResultProductPopulator.setPriceRowService(priceRowService);
		  sabmSearchResultProductPopulator.setProductService(productService);
		  sabmSearchResultProductPopulator.setCommonI18NService(commonI18NService);

	}

	@Test
	public void testPopulatePrices()
	{
		final SearchResultValueData resultValueData = mock(SearchResultValueData.class);
		final Map<String, Object> map = mock(Map.class);
		map.put("code", "eanVariant01");
		map.put("wetEligible", true);
		map.put("level4", "C");
		map.put("presentation", "4X6");
		given(map.get("code")).willReturn("eanVariant01");
		given(resultValueData.getValues()).willReturn(map);
		given(resultValueData.getValues().get("code")).willReturn("eanVariant01");
		given(asahiSiteUtil.isCub()).willReturn(true);

		final PriceRowModel mockPriceRow = mock(PriceRowModel.class);

		given(priceRowService.getPriceRowByProduct("eanVariant01")).willReturn(mockPriceRow);

		given(mockPriceRow.getPrice()).willReturn(Double.valueOf(1.0));
		given(commonI18NService.getCurrentCurrency()).willReturn(mock(CurrencyModel.class));

		final PriceData priceData = mock(PriceData.class);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(Double.valueOf(1.0)), mock(CurrencyModel.class)))
				.willReturn(priceData);

		final ProductData productData = new ProductData();
		resultValueData.setValues(map);

		given(asahiCoreUtil.isNAPUserForSite()).willReturn(false);
		given(asahiCoreUtil.isNAPUserForSite()).willReturn(false);
		given(sabmCDLValueService.getCDLPrice("C", "4X6")).willReturn(new BigDecimal(1.47));
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getBigDecimal(SabmCoreConstants.CUB_WET_PRICE_PERCENTAGE)).willReturn(new BigDecimal(1.29));

		sabmSearchResultProductPopulator.populatePrices(resultValueData, productData);
		Assert.assertNotNull(productData);
	}
}
