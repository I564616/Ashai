/**
 * 
 */
package com.sabmiller.storefront.form;

import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateQuantityForm;

import java.util.List;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author marc.f.l.bautista
 *
 */
public class SABMUpdateRecommendationForm //extends UpdateQuantityForm
{
	private String recommendationId = "";
	private String recommendationType = "";
	private String productCode = "";
	private String status = "";
	private String unit = "";
	private List<SABMProdToRecommendationForm> baseProducts;
	
	@NotNull(message = "{basket.error.quantity.notNull}")
	@Min(value = 0, message = "{basket.error.quantity.invalid}")
	@Digits(fraction = 0, integer = 10, message = "{basket.error.quantity.invalid}")
	private Integer quantity;

	public void setQuantity(final Integer quantity)
	{
		this.quantity = quantity;
	}

	public Integer getQuantity()
	{
		return quantity;
	}
	
	/**
	 * @return the recommendationId
	 */
	public String getRecommendationId()
	{
		return recommendationId;
	}
	
	/**
	 * @param recommendationId the recommendationId to set
	 */
	public void setRecommendationId(final String recommendationId)
	{
		this.recommendationId = recommendationId;
	}
	
	/**
	 * @return the recommendationType
	 */
	public String getRecommendationType()
	{
		return recommendationType;
	}
	
	/**
	 * @param recommendationType the recommendationType to set
	 */
	public void setRecommendationType(final String recommendationType)
	{
		this.recommendationType = recommendationType;
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode()
	{
		return productCode;
	}

	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode)
	{
		this.productCode = productCode;
	}

	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * @return the unit
	 */
	public String getUnit()
	{
		return unit;
	}
	
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}

	/**
	 * @return the baseProducts
	 */
	public List<SABMProdToRecommendationForm> getBaseProducts()
	{
		return baseProducts;
	}

	/**
	 * @param baseProducts the baseProducts to set
	 */
	public void setBaseProducts(List<SABMProdToRecommendationForm> baseProducts)
	{
		this.baseProducts = baseProducts;
	}

}
