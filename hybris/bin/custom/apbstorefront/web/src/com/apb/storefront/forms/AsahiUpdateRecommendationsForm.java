package com.apb.storefront.forms;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


/**
 * The Class AsahiUpdateRecommendationsForm.
 */
public class AsahiUpdateRecommendationsForm {
	
	/** The recommendation type. */
	private String recommendationType;
	

	/** The product code. */
	private String productCode;
	
	
	/** The action. */
	private String action;
	

	@Digits(fraction = 0, integer = 10, message = "{basket.error.quantity.invalid}")
	
	private Integer quantity;
	
	/**
	 * Gets the recommendation type.
	 *
	 * @return the recommendationType
	 */
	public String getRecommendationType() {
		return recommendationType;
	}

	/**
	 * Sets the recommendation type.
	 *
	 * @param recommendationType the recommendationType to set
	 */
	public void setRecommendationType(String recommendationType) {
		this.recommendationType = recommendationType;
	}

	/**
	 * Gets the product code.
	 *
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * Sets the product code.
	 *
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * Gets the quantity.
	 *
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity.
	 *
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}


}
