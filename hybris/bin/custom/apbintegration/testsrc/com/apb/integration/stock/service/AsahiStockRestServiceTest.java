package com.apb.integration.stock.service;

import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.integration.data.ApbStockonHandData;
import com.apb.integration.rest.client.AsahiRestClient;
import com.apb.integration.service.config.AsahiConfigurationService;
import com.apb.integration.stock.dto.AsahiStockOnHandRequest;
import com.apb.integration.stock.dto.AsahiStockOnHandRes;
import com.apb.integration.stock.dto.AsahiStockOnHandResponse;
import com.apb.integration.stock.dto.AsahiStockProductResponse;
import com.apb.integration.stock.service.impl.AsahiStockIntegrationServiceImpl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;


@UnitTest
public class AsahiStockRestServiceTest
{

	private static final String WAREHOUSE = "SVC";
	private static final String PRODUCT_1 = "13330001";
	private static final String PRODUCT_2 = "13330002";
	private static final String INTEGRATION_STOCK_STUB = "integration.stock.onHand.service.stub.check.apb";

	@InjectMocks
	private final AsahiStockIntegrationServiceImpl asahiStockIntegrationService = new AsahiStockIntegrationServiceImpl();

	@Mock
	private AsahiRestClient asahiRestClient;

	@Mock
	private AsahiConfigurationService asahiConfigurationService;

	AsahiStockOnHandRequest stockOnHandRequest = new AsahiStockOnHandRequest();

	@Before
	public void setUp() throws ImpExException
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * This is a sample test method.
	 */
	@Test
	public void testGetStockonHand()
	{
		List<String> productList = new ArrayList<>();
		productList.add(PRODUCT_1);
		productList.add(PRODUCT_2);
		AsahiStockOnHandRes stockOnHandRes = new AsahiStockOnHandRes();
		List<AsahiStockOnHandResponse> stockOnHandResponseList = new ArrayList<>();

		AsahiStockOnHandResponse stockOnHandResponse = new AsahiStockOnHandResponse();
		stockOnHandResponse.setWarehouse(WAREHOUSE);
		List<AsahiStockProductResponse> productsList = new ArrayList<>();
		AsahiStockProductResponse stockProductResponse1 = new AsahiStockProductResponse();
		stockProductResponse1.setProductId(PRODUCT_1);
		stockProductResponse1.setAvailablePhysical("5");

		AsahiStockProductResponse stockProductResponse2 = new AsahiStockProductResponse();
		stockProductResponse2.setProductId(PRODUCT_2);
		stockProductResponse2.setAvailablePhysical("10");

		productsList.add(stockProductResponse1);
		productsList.add(stockProductResponse2);
		stockOnHandResponse.setProducts(productsList);

		stockOnHandResponseList.add(stockOnHandResponse);

		stockOnHandRes.setStockOnHandResponse(stockOnHandResponseList);

		given(asahiConfigurationService.getBoolean(INTEGRATION_STOCK_STUB, false)).willReturn(true);

		AsahiStockIntegrationServiceImpl stockIntegrationService = Mockito.spy(asahiStockIntegrationService);
		Mockito.doReturn(stockOnHandRes).when(stockIntegrationService).stockServiceMockResponse();

		ApbStockonHandData stockonHandData = stockIntegrationService.getStockonHand(WAREHOUSE, productList);
		Assert.assertEquals(stockOnHandRes.getStockOnHandResponse().get(0).getProducts().size(),
				stockonHandData.getProductList().size());
	}
}
