package com.apb.integration.price.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.integration.data.ApbPriceData;
import com.apb.integration.data.ApbProductPriceInfo;
import com.apb.integration.price.dto.ApbPriceRequestData;
import com.apb.integration.price.dto.AsahiAccountPriceRequest;
import com.apb.integration.price.dto.AsahiAccountPriceResponse;
import com.apb.integration.price.dto.AsahiPriceRequest;
import com.apb.integration.price.dto.AsahiPriceResponse;
import com.apb.integration.price.dto.AsahiProductPriceRequest;
import com.apb.integration.price.dto.AsahiProductPriceResponse;
import com.apb.integration.price.service.ApbProductOfflinePriceService;
import com.apb.integration.price.service.AsahiPriceIntegrationService;
import com.apb.integration.rest.client.AsahiRestClient;
import com.apb.integration.service.config.AsahiConfigurationService;
import com.google.gson.Gson;


public class AsahiPriceIntegrationServiceImpl implements AsahiPriceIntegrationService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiPriceIntegrationServiceImpl.class);
	private static final String INTEGRATION_PRICE_SERVICE_URL = "integration.price.service.url.apb";
	private static final String INTEGRATION_PRICE_STUB = "integration.price.service.stub.check.apb";
	private static final String INTEGRATION_PRICE_OFFLINE = "integration.price.service.offline.apb";


	@Resource(name = "asahiRestClient")
	private AsahiRestClient asahiRestClient;

	@Resource(name = "asahiIconfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource
	private ApbProductOfflinePriceService apbProductOfflinePriceService;

	@Override
	public ApbPriceData getProductsPrice(final ApbPriceRequestData requestData)
	{
		ApbPriceData priceData;
		if (StringUtils.isEmpty(requestData.getAccNum()))
		{
			return null;
		}
		final AsahiPriceRequest priceRequest = createPriceRequest(requestData.getProductQuantityMap(),
				requestData.getBonusStatusMap(), requestData.getAccNum(),
				requestData.isFreightIncluded());
		
		List<AsahiAccountPriceRequest> accountPriceRequests = priceRequest.getPriceRequest();
		
		List<String> bonusLineNmbrs = new ArrayList<>();
		for(AsahiAccountPriceRequest accountPriceRequest: accountPriceRequests){
			if(null != accountPriceRequest){
				List<AsahiProductPriceRequest> asahiProductPriceRequests = accountPriceRequest.getProducts();
				for(AsahiProductPriceRequest asahiProductPriceRequest : asahiProductPriceRequests){
					if(null != asahiProductPriceRequest && asahiProductPriceRequest.isBonus() && StringUtils.isNotEmpty(asahiProductPriceRequest.getLineNbr())){
						bonusLineNmbrs.add(asahiProductPriceRequest.getLineNbr());
					}
				}
			}
		}
		
		final Gson gson = new Gson();
		if (this.asahiConfigurationService.getBoolean(INTEGRATION_PRICE_STUB, false))
		{
			LOGGER.info("Calling Mock price Service with Request---" + gson.toJson(priceRequest));

			priceData = priceServiceMockResponse(requestData.getProductQuantityMap(), requestData.getBonusStatusMap(),
					requestData.getAccNum());
		}
		else
		{
			final String url = this.asahiConfigurationService.getString(INTEGRATION_PRICE_SERVICE_URL, " ");
			try
			{
				LOGGER.debug("Calling Price Service with url---" + url.replace('\n', '_').replace('\r', '_'));
				LOGGER.info("PriceCallStart");
				LOGGER.info("Calling Price Service with Request---" + gson.toJson(priceRequest));
				final AsahiPriceResponse priceResponse = (AsahiPriceResponse) asahiRestClient.executePOSTRestRequest(url,
						priceRequest, AsahiPriceResponse.class, "price");
				LOGGER.info("PriceCallEnd");
				priceData = convertResponsePrice(priceResponse, requestData.getAccNum(), bonusLineNmbrs);
				LOGGER.info("Returning Price Service Response---" + gson.toJson(priceResponse));
			}
			catch (final Exception e)
			{
				priceData = createDefaultResponse(requestData.getProductQuantityMap(), requestData.getBonusStatusMap(),
						requestData.getAccNum());
				LOGGER.error("exception in fetching price", e);
			}
		}
		return addWETToNetPrice(priceData);
	}

	private ApbPriceData createDefaultResponse(final Map<String, Map<String, Long>> productQuantityMap,
			final Map<String, Map<String, Long>> map, final String accNum)
	{
		ApbPriceData priceData = new ApbPriceData();
		if (this.asahiConfigurationService.getBoolean(INTEGRATION_PRICE_OFFLINE, false))
		{
			priceData = priceServiceMockResponse(productQuantityMap, map, accNum);
		}
		return priceData;

	}

	private ApbPriceData convertResponsePrice(final AsahiPriceResponse priceResponse, final String accNum, List<String> bonusLineNmbrs)
	{
		if (priceResponse != null)
		{
			final ApbPriceData priceData = new ApbPriceData();
			final AsahiAccountPriceResponse accountPriceResponse = priceResponse.getPriceResponse();
			priceData.setAccountNumber(null != accountPriceResponse.getAccountNum() ? accountPriceResponse.getAccountNum() : accNum);
			priceData.setFreight(null != accountPriceResponse.getFreight() ? accountPriceResponse.getFreight() : 0D);
			priceData.setGST(null != accountPriceResponse.getGst() ? accountPriceResponse.getGst() : 0D);
			priceData.setSubTotal(null != accountPriceResponse.getSubTotal() ? accountPriceResponse.getSubTotal() : 0D);
			if (CollectionUtils.isNotEmpty(accountPriceResponse.getProducts()))
			{
				final List<ApbProductPriceInfo> productList = new ArrayList<>();
				for (final AsahiProductPriceResponse productPriceResponse : accountPriceResponse.getProducts())
				{
					final ApbProductPriceInfo productData = new ApbProductPriceInfo();
					productData.setCode(productPriceResponse.getProductId());
					productData.setWET(productPriceResponse.getWet());
					productData.setListPrice(productPriceResponse.getListPrice());
					productData.setNetPrice(productPriceResponse.getNetPrice());
					productData.setLineNumber(productPriceResponse.getLineNbr());
					if(StringUtils.isNotEmpty(productData.getLineNumber()) && CollectionUtils.isNotEmpty(bonusLineNmbrs) && bonusLineNmbrs.contains(productData.getLineNumber())){
						productData.setBonus(true);
					}else{
						productData.setBonus(false);
					}
					
					productList.add(productData);
				}
				priceData.setProductPriceInfo(productList);
			}
			LOGGER.info("Subtotal===" + priceData.getSubTotal());
			return priceData;
		}
		return null;
	}

	private ApbPriceData priceServiceMockResponse(final Map<String, Map<String, Long>> productQuantityMap,
			final Map<String, Map<String, Long>> bonusStatusMap, final String accNum)
	{
		if (null != accNum)
		{
			return apbProductOfflinePriceService.getPricesForProducts(productQuantityMap, bonusStatusMap, accNum);
		}
		return null;
	}

	private AsahiPriceRequest createPriceRequest(final Map<String, Map<String, Long>> productQuantityMap,
			Map<String, Map<String, Long>> bonusStatusMap, final String accNum,
			final boolean isFreightIncluded)
	{
		if (bonusStatusMap == null)
		{
			bonusStatusMap = new HashMap<String, Map<String, Long>>();
		}
		final AsahiPriceRequest priceRequest = new AsahiPriceRequest();

		final List<AsahiAccountPriceRequest> accountList = new ArrayList<>();
		final AsahiAccountPriceRequest accountPriceRequest = new AsahiAccountPriceRequest();
		accountPriceRequest.setAccountNum(accNum);
		accountPriceRequest.setIsFreightIncluded(String.valueOf(isFreightIncluded));

		final List<AsahiProductPriceRequest> productList = new ArrayList<>();

		for (final Map.Entry<String, Map<String, Long>> entry : productQuantityMap.entrySet())
		{
			final AsahiProductPriceRequest productPriceRequest = new AsahiProductPriceRequest();
			productPriceRequest.setProductId(entry.getKey());
			final Map<String, Long> entryAndQty = entry.getValue();
			final Map.Entry<String, Long> lineAndQty = entryAndQty.entrySet().iterator().next();
			productPriceRequest.setQuantity(lineAndQty.getValue());
			productPriceRequest.setLineNbr(lineAndQty.getKey());
			productPriceRequest.setBonus(Boolean.FALSE);
			productList.add(productPriceRequest);
		}
		for (final Map.Entry<String, Map<String, Long>> entry : bonusStatusMap.entrySet())
		{
			final AsahiProductPriceRequest productPriceRequest = new AsahiProductPriceRequest();
			productPriceRequest.setProductId(entry.getKey());
			final Map<String, Long> entryAndQty = entry.getValue();
			final Map.Entry<String, Long> lineAndQty = entryAndQty.entrySet().iterator().next();
			productPriceRequest.setQuantity(lineAndQty.getValue());
			productPriceRequest.setBonus(Boolean.TRUE);
			productPriceRequest.setLineNbr(lineAndQty.getKey());
			productList.add(productPriceRequest);
		}
		accountPriceRequest.setProducts(productList);
		accountList.add(accountPriceRequest);
		priceRequest.setPriceRequest(accountList);

		return priceRequest;
	}

	private ApbPriceData addWETToNetPrice(final ApbPriceData priceData)
	{
		if (null != priceData && CollectionUtils.isNotEmpty(priceData.getProductPriceInfo()))
		{
			for (final ApbProductPriceInfo productData : priceData.getProductPriceInfo())
			{
				productData.setNetPrice(productData.getNetPrice() + productData.getWET());
			}
		}
		return priceData;
	}
}
