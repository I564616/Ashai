package com.apb.core.event;

import java.io.Serial;
import java.util.List;

import com.sabmiller.core.model.AsahiSAMInvoiceModel;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

/**
 * The class works as a event for "Payment Confirmation Email Process" 
 * functionality.
 */

public class AsahiPaymentConfirmationEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

	private String amountPaid;
	private String paymentDate;
	private String referenceNo;
	private String paymentReference;
	private String paymentMethod;
	private List<AsahiSAMInvoiceModel> asahiSAMInvoices;
	
		
	public List<AsahiSAMInvoiceModel> getAsahiSAMInvoices() {
		return asahiSAMInvoices;
	}

	public void setAsahiSAMInvoices(List<AsahiSAMInvoiceModel> asahiSAMInvoices) {
		this.asahiSAMInvoices = asahiSAMInvoices;
	}

	@Serial
	private static final long serialVersionUID = 1L;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

}
