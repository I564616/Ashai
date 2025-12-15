package com.apb.integration.price.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AsahiAccountPriceResponse
{
	@JsonProperty("accountNum")
	private String accountNum;
	@JsonProperty("subTotal")
	private Double subTotal;
	@JsonProperty("freight")
	private Double freight;
	@JsonProperty("gst")
	private Double gst;

	@JsonProperty("products")
	private List<AsahiProductPriceResponse> products = null;

	public String getAccountNum()
	{
		return accountNum;
	}

	public void setAccountNum(String accountNum)
	{
		this.accountNum = accountNum;
	}

	public Double getSubTotal()
	{
		return subTotal;
	}

	public void setSubTotal(Double subTotal)
	{
		this.subTotal = subTotal;
	}

	public Double getFreight()
	{
		return freight;
	}

	public void setFreight(Double freight)
	{
		this.freight = freight;
	}

	public Double getGst()
	{
		return gst;
	}

	public void setGst(Double gst)
	{
		this.gst = gst;
	}

	public List<AsahiProductPriceResponse> getProducts()
	{
		return products;
	}

	public void setProducts(List<AsahiProductPriceResponse> products)
	{
		this.products = products;
	}
}
