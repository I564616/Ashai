package com.apb.integration.stock.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AsahiStockOnHandRes
{

	@JsonProperty("stockOnHandResponse")
	private List<AsahiStockOnHandResponse> stockOnHandResponse = null;

	public List<AsahiStockOnHandResponse> getStockOnHandResponse()
	{
		return stockOnHandResponse;
	}

	public void setStockOnHandResponse(List<AsahiStockOnHandResponse> stockOnHandResponse)
	{
		this.stockOnHandResponse = stockOnHandResponse;
	}
}
