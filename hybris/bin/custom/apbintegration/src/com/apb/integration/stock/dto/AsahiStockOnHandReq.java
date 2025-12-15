package com.apb.integration.stock.dto;

import java.util.List;


public class AsahiStockOnHandReq
{
	private List<AsahiStockOnHandRequest> stockOnHandRequest = null;

	public List<AsahiStockOnHandRequest> getStockOnHandRequest()
	{
		return stockOnHandRequest;
	}

	public void setStockOnHandRequest(List<AsahiStockOnHandRequest> stockOnHandRequest)
	{
		this.stockOnHandRequest = stockOnHandRequest;
	}
}
