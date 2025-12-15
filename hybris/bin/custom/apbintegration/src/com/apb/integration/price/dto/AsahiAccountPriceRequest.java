package com.apb.integration.price.dto;

import java.util.List;


public class AsahiAccountPriceRequest
{


	private String accountNum;

	private String isFreightIncluded;

	private List<AsahiProductPriceRequest> products = null;


	public String getAccountNum()
	{
		return accountNum;
	}

	public void setAccountNum(String accountNum)
	{
		this.accountNum = accountNum;
	}

	public String getIsFreightIncluded()
	{
		return isFreightIncluded;
	}

	public void setIsFreightIncluded(String isFreightIncluded)
	{
		this.isFreightIncluded = isFreightIncluded;
	}

	public List<AsahiProductPriceRequest> getProducts()
	{
		return products;
	}

	public void setProducts(List<AsahiProductPriceRequest> products)
	{
		this.products = products;
	}
}
