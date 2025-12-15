package com.apb.integration.price.service;

import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.integration.data.ApbPriceData;
import com.apb.integration.data.ApbProductPriceInfo;
import com.apb.integration.price.dto.ApbPriceRequestData;
import com.apb.integration.price.service.impl.AsahiPriceIntegrationServiceImpl;
import com.apb.integration.service.config.AsahiConfigurationService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;


@UnitTest
public class AsahiPriceRestServiceTest 
{

	private static final String ACCOUNT_NUMBER = "100686";
	private static final String PRODUCT_1 = "13330001";
	private static final String PRODUCT_2 = "13330002";
	private static final String INTEGRATION_PRICE_STUB = "integration.price.service.stub.check.apb";
	
	@InjectMocks
	private AsahiPriceIntegrationServiceImpl asahiPriceIntegrationService = new AsahiPriceIntegrationServiceImpl();


	@Mock
	private AsahiConfigurationService asahiConfigurationService;

	@Mock
	private ApbProductOfflinePriceService apbProductOfflinePriceService;

	@Before
	public void setUp() throws ImpExException
	{

		MockitoAnnotations.initMocks(this);

	}

	/**
	 * This is a sample test method.
	 */
	@Test
	public void testGetProductPrice()
	{
		Map<String, Map<String,Long>> productMap = new HashMap<>();
		Map<String,Long> lineAndQty = new HashMap<>();
		lineAndQty.put("0", 20L);
		
		Map<String,Long> lineAndQty2 = new HashMap<>();
		lineAndQty2.put("0", 20L);
		
		productMap.put(PRODUCT_1, lineAndQty);
		productMap.put(PRODUCT_2, lineAndQty2);
		
		List<String> productList = new ArrayList<>();
		productList.add(PRODUCT_1);
		productList.add(PRODUCT_2);
		ApbPriceData requestPriceData = new ApbPriceData();
		requestPriceData.setAccountNumber(ACCOUNT_NUMBER);
		requestPriceData.setFreight(20.00);
		List<ApbProductPriceInfo> productPriceInfoList = new ArrayList<>();
		ApbProductPriceInfo productPriceInfo1 =new ApbProductPriceInfo();
		productPriceInfo1.setCode(PRODUCT_1);
		productPriceInfo1.setNetPrice(200.00);
		productPriceInfo1.setWET(5.00);
		
		ApbProductPriceInfo productPriceInfo2 =new ApbProductPriceInfo();
		productPriceInfo2.setCode(PRODUCT_2);
		productPriceInfo2.setNetPrice(500.00);
		productPriceInfo2.setWET(20.00);
		productPriceInfoList.add(productPriceInfo1);
		productPriceInfoList.add(productPriceInfo2);
		requestPriceData.setProductPriceInfo(productPriceInfoList);
		
		given(asahiConfigurationService.getBoolean(INTEGRATION_PRICE_STUB, false)).willReturn(true);
		given(apbProductOfflinePriceService.getPricesForProducts(productMap,null, ACCOUNT_NUMBER)).willReturn(requestPriceData);
		ApbPriceRequestData request  = new ApbPriceRequestData();
		request.setAccNum(ACCOUNT_NUMBER);
		request.setFreightIncluded(Boolean.FALSE);
		request.setProductQuantityMap(productMap);
		
		ApbPriceData priceData = asahiPriceIntegrationService.getProductsPrice(request);
		Assert.assertEquals(ACCOUNT_NUMBER, priceData.getAccountNumber());
		Assert.assertEquals(productMap.size(), priceData.getProductPriceInfo().size());
		Assert.assertEquals(520.00, priceData.getProductPriceInfo().get(1).getNetPrice().doubleValue(),0.01);
	}

}
