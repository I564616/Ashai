package com.apb.integration.price.dto;

import java.util.List;


public class AsahiPriceRequest
{

	private List<AsahiAccountPriceRequest> priceRequest = null;

	public List<AsahiAccountPriceRequest> getPriceRequest()
	{
		return priceRequest;
	}

	public void setPriceRequest(List<AsahiAccountPriceRequest> priceRequest)
	{
		this.priceRequest = priceRequest;
	}

}
