package com.apb.integration.price.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AsahiPriceResponse
{
	@JsonProperty("priceResponse")
	private AsahiAccountPriceResponse priceResponse;

	public AsahiAccountPriceResponse getPriceResponse()
	{
		return priceResponse;
	}

	public void setPriceResponse(AsahiAccountPriceResponse priceResponse)
	{
		this.priceResponse = priceResponse;
	}
}
