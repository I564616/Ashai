/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 17-Aug-2021, 2:30:17 PM
 * ----------------------------------------------------------------
 *
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.apb.storefront.forms;

import java.io.Serial;
import java.io.Serializable;


import java.util.Objects;

public  class DiscrepancyForm  implements Serializable 
{

	/** Default serialVersionUID value. */

	@Serial
	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>DiscrepancyForm.materialNumber</code> property defined at extension <code>sabmfacades</code>. */
		
	private String materialNumber;

	/** <i>Generated property</i> for <code>DiscrepancyForm.qtyWithDelIssue</code> property defined at extension <code>sabmfacades</code>. */
		
	private String qtyWithDelIssue;

	/** <i>Generated property</i> for <code>DiscrepancyForm.expectedTotalPay</code> property defined at extension <code>sabmfacades</code>. */
		
	private String expectedTotalPay;

	/** <i>Generated property</i> for <code>DiscrepancyForm.expectedQty</code> property defined at extension <code>sabmfacades</code>. */
		
	private String expectedQty;
	
	private String qtyReceived;
	
	private String amtCharged;

	
	public DiscrepancyForm()
	{
		// default constructor
	}
	
	public void setMaterialNumber(final String materialNumber)
	{
		this.materialNumber = materialNumber;
	}

	public String getMaterialNumber() 
	{
		return materialNumber;
	}
	
	public void setQtyWithDelIssue(final String qtyWithDelIssue)
	{
		this.qtyWithDelIssue = qtyWithDelIssue;
	}

	public String getQtyWithDelIssue() 
	{
		return qtyWithDelIssue;
	}
	
	public void setExpectedTotalPay(final String expectedTotalPay)
	{
		this.expectedTotalPay = expectedTotalPay;
	}

	public String getExpectedTotalPay() 
	{
		return expectedTotalPay;
	}
	
	public void setExpectedQty(final String expectedQty)
	{
		this.expectedQty = expectedQty;
	}

	public String getExpectedQty() 
	{
		return expectedQty;
	}

	public String getQtyReceived() {
		return qtyReceived;
	}

	public void setQtyReceived(String qtyReceived) {
		this.qtyReceived = qtyReceived;
	}

	public String getAmtCharged() {
		return amtCharged;
	}

	public void setAmtCharged(String amtCharged) {
		this.amtCharged = amtCharged;
	}
	

}