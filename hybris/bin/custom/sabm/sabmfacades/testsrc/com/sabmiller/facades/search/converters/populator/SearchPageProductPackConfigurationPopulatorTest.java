/**
 *
 */
package com.sabmiller.facades.search.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.product.SabmPriceRowService;


/**
 * @author xue.zeng
 *
 */
@UnitTest
public class SearchPageProductPackConfigurationPopulatorTest
{

	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@Mock
	private SabmPriceRowService priceRowService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private Populator<FeatureList, ProductData> productFeatureListPopulator;
	@Mock
	private ImageFormatMapping imageFormatMapping;
	@Mock
	private UrlResolver<ProductData> productDataUrlResolver;
	@Mock
	private AsahiCoreUtil asahiCoreUtil;
	@Mock
	private SABMCartService sabmCartService;
	@Mock
	private UserService userService;
	@Mock
	private B2BCustomerModel b2bCustomer;

	@InjectMocks
	private SABMSearchResultProductPopulator packConfigurationPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		packConfigurationPopulator.setPriceRowService(priceRowService);
		packConfigurationPopulator.setCommonI18NService(commonI18NService);
		packConfigurationPopulator.setProductFeatureListPopulator(productFeatureListPopulator);
		packConfigurationPopulator.setImageFormatMapping(imageFormatMapping);
		packConfigurationPopulator.setProductDataUrlResolver(productDataUrlResolver);
	}

	@Test
	public void testConvert()
	{
		final SearchResultValueData resultValueData = mock(SearchResultValueData.class);
		final Map<String, Object> map = mock(Map.class);
		final LanguageModel languageModel = mock(LanguageModel.class);
		final ProductData productData = new ProductData();
		given(map.get("sellingName")).willReturn("sellingName");
		given(resultValueData.getValues()).willReturn(map);
		given(resultValueData.getValues().get("sellingName")).willReturn("sellingName");
		given(resultValueData.getValues().get("code")).willReturn("testProduct");
		given(resultValueData.getValues().get("packConfiguration")).willReturn("packConfiguration");
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commonI18NService.getLocaleForLanguage(languageModel)).willReturn(Locale.ENGLISH);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat("thumbnail")).willReturn(null);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat("product")).willReturn(null);
		given(productDataUrlResolver.resolve(productData)).willReturn(null);
		given(asahiSiteUtil.isCub()).willReturn(true);
		given(priceRowService.getPriceRowByProductCode("testProduct")).willReturn(null);
		given(asahiCoreUtil.isNAPUserForSite()).willReturn(false);
		given(userService.getCurrentUser()).willReturn(b2bCustomer);
		packConfigurationPopulator.populate(resultValueData, productData);
		Assert.assertNotNull(productData);
		Assert.assertEquals("packConfiguration",productData.getPackConfiguration());
	}
}
