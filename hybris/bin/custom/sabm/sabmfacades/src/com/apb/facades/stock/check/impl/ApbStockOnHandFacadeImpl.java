package com.apb.facades.stock.check.impl;

import java.util.List;

import jakarta.annotation.Resource;

import com.apb.facades.stock.check.ApbStockOnHandFacade;
import com.apb.integration.data.ApbStockonHandData;
import com.apb.integration.data.ApbStockonHandProductData;
import com.apb.integration.stock.service.AsahiStockIntegrationService;


public class ApbStockOnHandFacadeImpl implements ApbStockOnHandFacade
{

	@Resource(name = "asahiStockIntegrationService")
	private AsahiStockIntegrationService asahiStockIntegrationService;

	@Override
	public List<ApbStockonHandProductData> checkStock(String wareHouse, List<String> productList)
	{
		ApbStockonHandData stockData = asahiStockIntegrationService.getStockonHand(wareHouse, productList);
		if (stockData != null)
		{
			return stockData.getProductList();
		}
		return null;
	}

}
