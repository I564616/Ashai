package com.apb.integration.price.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AsahiProductPriceResponse
{

	@JsonProperty("productId")
	private String productId;
	@JsonProperty("listPrice")
	private double listPrice;
	@JsonProperty("netPrice")
	private double netPrice;
	@JsonProperty("wet")
	private double wet;
	@JsonProperty("lineNbr")
	private String lineNbr;
	@JsonProperty("bonus")
	private boolean bonus; 

	public boolean isBonus() {
		return bonus;
	}

	public void setBonus(boolean bonus) {
		this.bonus = bonus;
	}

	public String getProductId()
	{
		return productId;
	}

	public void setProductId(final String productId)
	{
		this.productId = productId;
	}

	public double getListPrice()
	{
		return listPrice;
	}

	public void setListPrice(final double listPrice)
	{
		this.listPrice = listPrice;
	}

	public double getNetPrice()
	{
		return netPrice;
	}

	public void setNetPrice(final double netPrice)
	{
		this.netPrice = netPrice;
	}

	public double getWet()
	{
		return wet;
	}

	public void setWet(final double wet)
	{
		this.wet = wet;
	}

	public String getLineNbr()
	{
		return lineNbr;
	}

	public void setLineNbr(final String lineNbr)
	{
		this.lineNbr = lineNbr;
	}
}
