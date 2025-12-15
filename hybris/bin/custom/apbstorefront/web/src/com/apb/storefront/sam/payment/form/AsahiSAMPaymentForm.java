package com.apb.storefront.sam.payment.form;

import java.util.List;

import com.apb.facades.card.payment.AsahiCreditCardTypeEnum;
import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.storefront.checkout.form.AsahiPaymentDetailsForm;


/**
 * The Asahi SAM Payment Form.
 *
 * @author Ganesh.Muddliyar
 */
/**
 *
 */
public class AsahiSAMPaymentForm
{
	private String paymentReference;
	private AsahiCreditCardTypeEnum asahiCreditCardType;
	private AsahiPaymentDetailsForm asahiPaymentDetailsForm;
	private boolean saveCreditCard;
	private List<AsahiSAMInvoiceData> invoices;
	private String initialTotalAmount;
	private String totalPayableAmount;
	private String samPaymentReason;
	
	public String getSamPaymentReference() {
		return samPaymentReference;
	}

	public void setSamPaymentReference(String samPaymentReference) {
		this.samPaymentReference = samPaymentReference;
	}

	private int totalInvoiceCount;
	private String samPaymentReference;
	/**
	 * List of invoices
	 */
	private List<AsahiSAMInvoiceForm> asahiSamInvoiceForm;



	/**
	 * @return invoice count
	 */
	public int getTotalInvoiceCount()
	{
		return totalInvoiceCount;
	}

	/**
	 * @param totalInvoiceCount
	 */
	public void setTotalInvoiceCount(final int totalInvoiceCount)
	{
		this.totalInvoiceCount = totalInvoiceCount;
	}

	/**
	 * @return payment reason
	 */
	public String getSamPaymentReason()
	{
		return samPaymentReason;
	}

	/**
	 * @param samPaymentReason
	 */
	public void setSamPaymentReason(final String samPaymentReason)
	{
		this.samPaymentReason = samPaymentReason;
	}

	/**
	 * @return
	 */
	public String getInitialTotalAmount()
	{
		return initialTotalAmount;
	}

	/**
	 * @param initialTotalAmount
	 */
	public void setInitialTotalAmount(final String initialTotalAmount)
	{
		this.initialTotalAmount = initialTotalAmount;
	}

	/**
	 * @return
	 */
	public List<AsahiSAMInvoiceForm> getAsahiSamInvoiceForm()
	{
		return asahiSamInvoiceForm;
	}

	/**
	 * @param asahiSamInvoiceForm
	 */
	public void setAsahiSamInvoiceForm(final List<AsahiSAMInvoiceForm> asahiSamInvoiceForm)
	{
		this.asahiSamInvoiceForm = asahiSamInvoiceForm;
	}

	/**
	 * @return
	 */
	public String getTotalPayableAmount()
	{
		return totalPayableAmount;
	}

	/**
	 * @param totalPayableAmount
	 */
	public void setTotalPayableAmount(final String totalPayableAmount)
	{
		this.totalPayableAmount = totalPayableAmount;
	}

	/**
	 * @return
	 */
	public List<AsahiSAMInvoiceData> getInvoices()
	{
		return invoices;
	}

	/**
	 * @param invoices
	 */
	public void setInvoices(final List<AsahiSAMInvoiceData> invoices)
	{
		this.invoices = invoices;
	}

	/**
	 * @return
	 */
	public String getPaymentReference()
	{
		return paymentReference;
	}

	/**
	 * @param paymentReference
	 */
	public void setPaymentReference(final String paymentReference)
	{
		this.paymentReference = paymentReference;
	}

	/**
	 * @return
	 */
	public AsahiCreditCardTypeEnum getAsahiCreditCardType()
	{
		return asahiCreditCardType;
	}

	/**
	 * @param asahiCreditCardType
	 */
	public void setAsahiCreditCardType(final AsahiCreditCardTypeEnum asahiCreditCardType)
	{
		this.asahiCreditCardType = asahiCreditCardType;
	}

	/**
	 * @return
	 */
	public AsahiPaymentDetailsForm getAsahiPaymentDetailsForm()
	{
		return asahiPaymentDetailsForm;
	}

	/**
	 * @param asahiPaymentDetailsForm
	 */
	public void setAsahiPaymentDetailsForm(final AsahiPaymentDetailsForm asahiPaymentDetailsForm)
	{
		this.asahiPaymentDetailsForm = asahiPaymentDetailsForm;
	}

	/**
	 * @return
	 */
	public boolean isSaveCreditCard()
	{
		return saveCreditCard;
	}

	/**
	 * @param saveCreditCard
	 */
	public void setSaveCreditCard(final boolean saveCreditCard)
	{
		this.saveCreditCard = saveCreditCard;
	}

}
