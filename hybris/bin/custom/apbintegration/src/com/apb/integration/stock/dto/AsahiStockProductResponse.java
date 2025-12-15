package com.apb.integration.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AsahiStockProductResponse
{

	@JsonProperty("productId")
	private String productId;
	@JsonProperty("availablePhysical")
	private String availablePhysical;

	public String getProductId()
	{
		return productId;
	}

	public void setProductId(String productId)
	{
		this.productId = productId;
	}

	public String getAvailablePhysical()
	{
		return availablePhysical;
	}

	public void setAvailablePhysical(String availablePhysical)
	{
		this.availablePhysical = availablePhysical;
	}

}
