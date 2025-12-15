package com.apb.integration.stock.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AsahiStockOnHandResponse
{

	@JsonProperty("warehouse")
	private String warehouse;
	@JsonProperty("products")
	private List<AsahiStockProductResponse> products = null;

	public String getWarehouse()
	{
		return warehouse;
	}

	public void setWarehouse(String warehouse)
	{
		this.warehouse = warehouse;
	}

	public List<AsahiStockProductResponse> getProducts()
	{
		return products;
	}

	public void setProducts(List<AsahiStockProductResponse> products)
	{
		this.products = products;
	}

}
