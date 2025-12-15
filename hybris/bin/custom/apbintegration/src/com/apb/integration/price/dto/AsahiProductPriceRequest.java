package com.apb.integration.price.dto;

/**
 * The Class AsahiProductPriceRequest.
 */
public class AsahiProductPriceRequest
{

	private String productId;
	private Long quantity;
	private String lineNbr;
	private boolean bonus;


	public String getProductId()
	{
		return productId;
	}

	public void setProductId(final String productId)
	{
		this.productId = productId;
	}

	public Long getQuantity()
	{
		return quantity;
	}

	public void setQuantity(final Long quantity)
	{
		this.quantity = quantity;
	}

	public String getLineNbr()
	{
		return lineNbr;
	}

	public void setLineNbr(final String lineNbr)
	{
		this.lineNbr = lineNbr;
	}

	/**
	 * @return the bonus
	 */
	public boolean isBonus() {
		return bonus;
	}

	/**
	 * @param bonus the bonus to set
	 */
	public void setBonus(boolean bonus) {
		this.bonus = bonus;
	}
}
