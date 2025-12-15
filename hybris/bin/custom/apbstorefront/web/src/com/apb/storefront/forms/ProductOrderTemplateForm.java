package com.apb.storefront.forms;

public class ProductOrderTemplateForm {
	
	/** The template code. */
	private String templateCode;
	
	/** The product code. */
	private String product;
	
	/** The product quantity. */
	private String quantity;
	
	/** Existing Template. */
	private boolean existingTemplate;

	/**
	 * @return the templateCode
	 */
	public String getTemplateCode() {
		return templateCode;
	}

	/**
	 * @param templateCode the templateCode to set
	 */
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	/**
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the existingTemplate
	 */
	public boolean isExistingTemplate() {
		return existingTemplate;
	}

	/**
	 * @param existingTemplate the existingTemplate to set
	 */
	public void setExistingTemplate(boolean existingTemplate) {
		this.existingTemplate = existingTemplate;
	}
	
	
	
	

}
