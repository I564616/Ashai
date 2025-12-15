package com.apb.integration.order.dto;



public class AsahiLineRequest
{

	private String isBonusStock;

	private Long quantity;

	private String externalDiscountDescription;

	private String externalDiscount;

	private String productId;

	private String currencyCode;

	private String salesUnit;
	
	private int lineNum;

	public String getIsBonusStock()
	{
		return isBonusStock;
	}

	public void setIsBonusStock(String isBonusStock)
	{
		this.isBonusStock = isBonusStock;
	}

	public Long getQuantity()
	{
		return quantity;
	}

	public void setQuantity(Long quantity)
	{
		this.quantity = quantity;
	}

	public String getExternalDiscountDescription()
	{
		return externalDiscountDescription;
	}

	public void setExternalDiscountDescription(String externalDiscountDescription)
	{
		this.externalDiscountDescription = externalDiscountDescription;
	}

	public String getExternalDiscount()
	{
		return externalDiscount;
	}

	public void setExternalDiscount(String externalDiscount)
	{
		this.externalDiscount = externalDiscount;
	}

	public String getProductId()
	{
		return productId;
	}

	public void setProductId(String productId)
	{
		this.productId = productId;
	}

	public String getCurrencyCode()
	{
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode)
	{
		this.currencyCode = currencyCode;
	}

	public String getSalesUnit()
	{
		return salesUnit;
	}

	public void setSalesUnit(String salesUnit)
	{
		this.salesUnit = salesUnit;
	}

	/**
	 * @return the lineNum
	 */
	public int getLineNum() {
		return lineNum;
	}

	/**
	 * @param lineNum the lineNum to set
	 */
	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
}
