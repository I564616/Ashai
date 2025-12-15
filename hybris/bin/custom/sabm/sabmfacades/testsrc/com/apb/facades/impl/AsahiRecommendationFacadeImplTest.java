/**
 *
 */
package com.apb.facades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.facades.product.AsahiRecommendationFacadeImpl;
import com.apb.integration.data.AsahiProductInfo;
import com.sabmiller.core.enums.RecommendationStatus;
import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.core.recommendation.service.RecommendationService;
import com.sabmiller.facades.populators.SabmRecommendationPopulator;
import com.sabmiller.facades.recommendation.data.RecommendationData;


/**
 * @author Saumya.Mittal1
 *
 */
@UnitTest
public class AsahiRecommendationFacadeImplTest
{

	@InjectMocks
	private final AsahiRecommendationFacadeImpl asahiRecommendationFacade = new AsahiRecommendationFacadeImpl();

	private static final String BY_RECOMMENDEDDATE = "byRecommendedDate";


	@Mock
	RecommendationService recommendationService;

	@Mock
	private SabmRecommendationPopulator recommendationPopulator;

	@Mock
	private SessionService sessionService;

	@Mock
	private UserService userService;

	@Mock
	private ProductFacade productFacade;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetAsahiProductRecommendations()
	{
		final SearchPageData searchData = new SearchPageData();
		final RecommendationData recommendation = new RecommendationData();
		final SearchPageData<SABMRecommendationModel> recommendations = new SearchPageData<SABMRecommendationModel>();
		final List<SABMRecommendationModel> results = new ArrayList<SABMRecommendationModel>();
		final SABMRecommendationModel model = Mockito.mock(SABMRecommendationModel.class);
		Mockito.when(model.getProductCode()).thenReturn("pcode");
		Mockito.when(model.getStatus()).thenReturn(RecommendationStatus.RECOMMENDED);
		Mockito.when(model.getQty()).thenReturn(Integer.valueOf(1));
		Mockito.when(model.getProductCode()).thenReturn("pcode");
		Mockito.when(model.getPk()).thenReturn(PK.parse("865968565"));
		Mockito.when(model.getRecommendationType()).thenReturn(RecommendationType.PRODUCT);
		results.add(model);
		recommendations.setResults(results);
		Mockito.when(recommendationService.getPageableRecommendations(searchData, BY_RECOMMENDEDDATE)).thenReturn(recommendations);
		final Map<String, AsahiProductInfo> inclusionList = new HashMap<>();
		final AsahiProductInfo value = new AsahiProductInfo();
		value.setMaterialNumber("pcode");
		inclusionList.put("pcode", value);
		Mockito.when(sessionService.getAttribute(ApbCoreConstants.CUSTOMER_SESSION_INCLUSION_LIST)).thenReturn(inclusionList);
		final ProductData productData = new ProductData();
		productData.setCode("pcode");
		Mockito.when(productFacade.getProductForCodeAndOptions(Mockito.any(), Mockito.any())).thenReturn(productData);
		Mockito.doCallRealMethod().when(recommendationPopulator).populate(model, recommendation);
		final SearchPageData<RecommendationData> searchRecommendations = asahiRecommendationFacade
				.getAsahiProductRecommendations(searchData, "byRecommendedDate");
		Assert.assertNotNull(searchRecommendations);
	}

	@Test
	public void testUpdateProductRecommendation()
	{
		Mockito.doNothing().when(recommendationService).updateProductRecommendation(Mockito.any(), Mockito.any());
		final boolean result = asahiRecommendationFacade.updateProductRecommendation("pcode", Integer.valueOf(1));
		Assert.assertEquals(Boolean.TRUE, result);
	}


	@Test
	public void testGetTotalRepRecommendedProducts()
	{
		final List<SABMRecommendationModel> recomm = new ArrayList<SABMRecommendationModel>();
		recomm.add(new SABMRecommendationModel());
		Mockito.when(recommendationService.getRecommendations()).thenReturn(recomm);
		final Integer result = asahiRecommendationFacade.getTotalRepRecommendedProducts();
		Assert.assertEquals(Integer.valueOf(1), result);
	}


	@Test
	public void testDeleteRecommendationByProductId()
	{
		Mockito.doNothing().when(recommendationService).deleteRecommendationByProductId(Mockito.any());
		final boolean result = asahiRecommendationFacade.deleteRecommendationByProductId("pcode");
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void testDeleteAllRecommendations()
	{
		Mockito.doNothing().when(recommendationService).deleteAllRecommendations();
		final boolean result = asahiRecommendationFacade.deleteAllRecommendations();
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void testGetSgaProductRecommendations()
	{
		final Map<SmartRecommendationType, ProductData> sgaRecommendationMap = new HashMap<SmartRecommendationType, ProductData>();
		final ProductData productData = new ProductData();
		productData.setCode("pcode");
		sgaRecommendationMap.put(SmartRecommendationType.MODEL1, productData);
		Mockito.when(recommendationService.getSgaProductRecommendations()).thenReturn(sgaRecommendationMap );
		final Map<SmartRecommendationType, ProductData> map = asahiRecommendationFacade.getSgaProductRecommendations();
		Assert.assertEquals(true, map.containsKey(SmartRecommendationType.MODEL1));
	}
}
