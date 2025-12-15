package com.apb.storefront.forms;

/**
 *
 */
public class ApbKegReturnKegSizForm
{
	private Integer code;
	private String kegSize;
	private Integer kegQuantity;
	
	public ApbKegReturnKegSizForm()
	{}
	
	public ApbKegReturnKegSizForm(String kegSize,Integer kegQuantity) {
		this.kegSize=kegSize;
		this.kegQuantity=kegQuantity;
	}

	/**
	 * @return the code
	 */
	public Integer getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final Integer code)
	{
		this.code = code;
	}



	/**
	 * @return the kegSize
	 */
	public String getKegSize()
	{
		return kegSize;
	}

	/**
	 * @param kegSize
	 *           the kegSize to set
	 */
	public void setKegSize(final String kegSize)
	{
		this.kegSize = kegSize;
	}

	/**
	 * @return the kegQuantity
	 */
	public Integer getKegQuantity()
	{
		return kegQuantity;
	}

	/**
	 * @param kegQuantity
	 *           the kegQuantity to set
	 */
	public void setKegQuantity(final Integer kegQuantity)
	{
		this.kegQuantity = kegQuantity;
	}
}
