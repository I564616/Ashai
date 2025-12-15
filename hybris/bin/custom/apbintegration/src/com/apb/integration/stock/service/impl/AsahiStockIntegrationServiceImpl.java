package com.apb.integration.stock.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.integration.data.ApbStockonHandData;
import com.apb.integration.data.ApbStockonHandProductData;
import com.apb.integration.rest.client.AsahiRestClient;
import com.apb.integration.service.config.AsahiConfigurationService;
import com.apb.integration.stock.dto.AsahiStockOnHandReq;
import com.apb.integration.stock.dto.AsahiStockOnHandRequest;
import com.apb.integration.stock.dto.AsahiStockOnHandRes;
import com.apb.integration.stock.dto.AsahiStockOnHandResponse;
import com.apb.integration.stock.dto.AsahiStockProductResponse;
import com.apb.integration.stock.service.AsahiStockIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;


public class AsahiStockIntegrationServiceImpl implements AsahiStockIntegrationService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiStockIntegrationServiceImpl.class);
	private static final String INTEGRATION_STOCK_URL = "integration.stock.onHand.service.url.apb";
	private static final String INTEGRATION_STOCK_STUB = "integration.stock.onHand.service.stub.check.apb";
	private static final String INTEGRATION_STOCK_OFFLINE = "integration.stock.onHand.service.offline.apb";
	public static final String STOCK_MOCK_RESPONSE = "stubs/stockMockResponse.json";

	@Resource(name = "asahiRestClient")
	private AsahiRestClient asahiRestClient;

	@Resource(name = "asahiIconfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Override
	public ApbStockonHandData getStockonHand(final String wareHouse, final List<String> products)
	{
		final String url = this.asahiConfigurationService.getString(INTEGRATION_STOCK_URL, " ");

		AsahiStockOnHandRes stockResponse;
		final Gson gson = new Gson();

		if (this.asahiConfigurationService.getBoolean(INTEGRATION_STOCK_STUB, false))
		{
			stockResponse = stockServiceMockResponse();
		}
		else
		{
			try
			{
				final AsahiStockOnHandReq stockOnHandReq = new AsahiStockOnHandReq();
				final List<AsahiStockOnHandRequest> stockOnHandRequestList = new ArrayList<>();
				final AsahiStockOnHandRequest stockOnHandRequest = new AsahiStockOnHandRequest();
				stockOnHandRequest.setWarehouse(wareHouse);
				stockOnHandRequest.setProducts(products);
				stockOnHandRequestList.add(stockOnHandRequest);

				stockOnHandReq.setStockOnHandRequest(stockOnHandRequestList);

				LOGGER.debug("Stock Service Request ::" + gson.toJson(stockOnHandReq));
				LOGGER.debug("Stock Service URL ::" + url.replace('\n', '_').replace('\r', '_'));
				LOGGER.debug("StockCallStart");
				stockResponse = (AsahiStockOnHandRes) asahiRestClient.executePOSTRestRequest(url, stockOnHandReq,
						AsahiStockOnHandRes.class, "stock");
				LOGGER.debug("StockCallEnd");
				LOGGER.debug("Stock Service Response---" + gson.toJson(stockResponse));
			}
			catch (final Exception e)
			{
				stockResponse = createDefaultResponse(wareHouse, products);
				LOGGER.error("exception in get stock", e);
			}
		}
		ApbStockonHandData stockonHandData = new ApbStockonHandData();
		if (null != stockResponse)
		{
			stockonHandData = convertStockResponse(stockResponse);
		}
		return stockonHandData;
	}

	private AsahiStockOnHandRes createDefaultResponse(final String wareHouse, final List<String> products)
	{
		AsahiStockOnHandRes stockResponse = new AsahiStockOnHandRes();
		if (this.asahiConfigurationService.getBoolean(INTEGRATION_STOCK_OFFLINE, true))
		{
			stockResponse = stockServiceMockResponse();
		}
		return stockResponse;

	}

	private ApbStockonHandData convertStockResponse(final AsahiStockOnHandRes stockRes)
	{
		final ApbStockonHandData stockonHandData = new ApbStockonHandData();
		if (CollectionUtils.isNotEmpty(stockRes.getStockOnHandResponse()))
		{
			final AsahiStockOnHandResponse stockResponse = stockRes.getStockOnHandResponse().get(0);
			stockonHandData.setWareHouse(stockResponse.getWarehouse());
			if (CollectionUtils.isNotEmpty(stockResponse.getProducts()))
			{
				final List<ApbStockonHandProductData> productDataList = new ArrayList<>();
				for (final AsahiStockProductResponse productRes : stockResponse.getProducts())
				{
					final ApbStockonHandProductData productData = new ApbStockonHandProductData();
					if (StringUtils.isNoneEmpty(productRes.getAvailablePhysical()))
					{
						productData.setAvailablePhysical((long) Double.parseDouble(productRes.getAvailablePhysical()));
					}
					productData.setProductId(productRes.getProductId());
					productDataList.add(productData);

				}
				stockonHandData.setProductList(productDataList);
			}
		}
		return stockonHandData;

	}

	public AsahiStockOnHandRes stockServiceMockResponse()
	{

		final ObjectMapper mapper = new ObjectMapper();
		try
		{
			final ClassLoader classLoader = getClass().getClassLoader();

			return mapper.readValue(new File(classLoader.getResource(STOCK_MOCK_RESPONSE).getFile()), AsahiStockOnHandRes.class);

		}
		catch (final IOException exception)
		{
			LOGGER.error("exception in parsing mock response", exception);

			return null;
		}
	}
}
