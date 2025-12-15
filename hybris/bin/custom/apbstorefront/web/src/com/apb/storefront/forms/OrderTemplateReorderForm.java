/*
 * 
 */
package com.apb.storefront.forms;

import java.util.List;

/**
 * The Class OrderTemplateReorderForm.
 */
public class OrderTemplateReorderForm {
	
	/** The template code. */
	private String templateCode;
	
	/** The template entries. */
	private List<OrderTemplateEntryReorderForm> templateEntries;
	
	/** The keep cart. */
	private boolean keepCart;
	
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
	 * @return the templateEntries
	 */
	public List<OrderTemplateEntryReorderForm> getTemplateEntries() {
		return templateEntries;
	}

	/**
	 * @param templateEntries the templateEntries to set
	 */
	public void setTemplateEntries(List<OrderTemplateEntryReorderForm> templateEntries) {
		this.templateEntries = templateEntries;
	}

	/**
	 * @return the keepCart
	 */
	public boolean isKeepCart() {
		return keepCart;
	}

	/**
	 * @param keepCart the keepCart to set
	 */
	public void setKeepCart(boolean keepCart) {
		this.keepCart = keepCart;
	}
}
