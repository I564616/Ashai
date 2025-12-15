package com.apb.integration.price.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AsahiProductsPriceResponse
{
	@JsonProperty("Product")
	private List<AsahiProductPriceResponse> product = null;

	public List<AsahiProductPriceResponse> getProduct()
	{
		return product;
	}

	public void setProduct(List<AsahiProductPriceResponse> product)
	{
		this.product = product;
	}
}
