package com.apb.integration.stock.dto;

import java.util.List;


public class AsahiStockOnHandRequest
{

	private String warehouse;
	private List<String> products = null;

	public String getWarehouse()
	{
		return warehouse;
	}

	public void setWarehouse(String warehouse)
	{
		this.warehouse = warehouse;
	}

	public List<String> getProducts()
	{
		return products;
	}

	public void setProducts(List<String> products)
	{
		this.products = products;
	}

}
