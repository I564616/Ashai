/**
 *
 */
package com.sabmiller.facades.storefinder.populators;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.sabmiller.facades.search.converters.populator.SABMSearchResultProductPopulator;


/**
 * @author xue.zeng
 *
 */
@UnitTest
public class SearchPageProductUOMPopulatorTest
{
	
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@Mock
	private SabmPriceRowService priceRowService;
	@Mock
	private Populator<FeatureList, ProductData> productFeatureListPopulator;
	@Mock
	private CommonI18NService commonI18NService;
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
	private SABMSearchResultProductPopulator pageProductUOMPopulator;
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		//pageProductUOMPopulator = new SABMSearchResultProductPopulator();
		pageProductUOMPopulator.setPriceRowService(priceRowService);
		pageProductUOMPopulator.setImageFormatMapping(imageFormatMapping);
	}

	@Test
	public void testPopulator()
	{
		final SearchResultValueData searchResultValueData = mock(SearchResultValueData.class);
		final LanguageModel languageModel = mock(LanguageModel.class);
		final Map<String, Object> map = new HashMap<String, Object>();
		final List<String> uoms = new ArrayList<>();
		uoms.add("case_:_CASE");
		map.put("uoms", uoms);
		map.put("code", "sampleproduct");
		final ProductData productData = new ProductData();
		searchResultValueData.setValues(map);
		given(searchResultValueData.getValues()).willReturn(map);
		//given(searchResultValueData.getValues().get("code")).willReturn("sampleproduct");
		given(priceRowService.getPriceRowByProductCode("sampleproduct")).willReturn(null);
		given(asahiSiteUtil.isCub()).willReturn(true);
		given(commonI18NService.getCurrentLanguage()).willReturn(languageModel);
		given(commonI18NService.getLocaleForLanguage(languageModel)).willReturn(Locale.ENGLISH);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat("thumbnail")).willReturn(null);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat("product")).willReturn(null);
		given(productDataUrlResolver.resolve(productData)).willReturn(null);
		given(asahiCoreUtil.isNAPUserForSite()).willReturn(false);
		given(userService.getCurrentUser()).willReturn(b2bCustomer);
		pageProductUOMPopulator.populate(searchResultValueData, productData);
		Assert.assertEquals("case", productData.getUomList().get(0).getCode());
		Assert.assertEquals("CASE", productData.getUomList().get(0).getName());
	}
}
