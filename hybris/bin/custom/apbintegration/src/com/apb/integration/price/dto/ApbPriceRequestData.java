package com.apb.integration.price.dto;

import java.util.Map;


/**
 *
 */
public class ApbPriceRequestData
{

	private Map<String,  Map<String, Long>> productQuantityMap;
	private Map<String, Map<String, Long>> bonusStatusMap;
	private String accNum;
	private boolean isFreightIncluded;

	/**
	 * @return
	 */
	public Map<String,  Map<String, Long>> getProductQuantityMap()
	{
		return productQuantityMap;
	}

	/**
	 * @param productQuantityMap
	 */
	public void setProductQuantityMap(final Map<String,  Map<String, Long>> productQuantityMap)
	{
		this.productQuantityMap = productQuantityMap;
	}

	/**
	 * @return
	 */
	public Map<String, Map<String, Long>> getBonusStatusMap()
	{
		return bonusStatusMap;
	}

	/**
	 * @param bonusStatusMap
	 */
	public void setBonusStatusMap(final Map<String, Map<String, Long>> bonusStatusMap)
	{
		this.bonusStatusMap = bonusStatusMap;
	}

	/**
	 * @return
	 */
	public String getAccNum()
	{
		return accNum;
	}

	/**
	 * @param accNum
	 */
	public void setAccNum(final String accNum)
	{
		this.accNum = accNum;
	}

	/**
	 * @return
	 */
	public boolean isFreightIncluded()
	{
		return isFreightIncluded;
	}

	/**
	 * @param isFreightIncluded
	 */
	public void setFreightIncluded(final boolean isFreightIncluded)
	{
		this.isFreightIncluded = isFreightIncluded;
	}

}
