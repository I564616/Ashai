/*
 * 
 */
package com.apb.storefront.forms;

/**
 * The Class OrderTemplateReorderForm.
 */
public class OrderTemplateEntryReorderForm {
	
	/** The qty. */
	private long qty;
	
	/** The entry PK. */
	private String entryPK;

	/**
	 * @return the qty
	 */
	public long getQty() {
		return qty;
	}

	/**
	 * @param qty the qty to set
	 */
	public void setQty(long qty) {
		this.qty = qty;
	}

	/**
	 * @return the entryPK
	 */
	public String getEntryPK() {
		return entryPK;
	}

	/**
	 * @param entryPK the entryPK to set
	 */
	public void setEntryPK(String entryPK) {
		this.entryPK = entryPK;
	}
}
