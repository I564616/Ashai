package com.apb.storefront.sam.payment.form;

import com.apb.storefront.checkout.form.AsahiPaymentDetailsForm;


public class AsahiDirectDebitForm {

	private String personalName;
	private String currentDate;
	private String saveCreditCard;
	private AsahiDirectDebitPaymentForm asahiDirectDebitPaymentForm;
	private AsahiPaymentDetailsForm asahiPaymentDetailsForm;
	
	/**
	 * @return the asahiDirectDebitPaymentForm
	 */
	public AsahiDirectDebitPaymentForm getAsahiDirectDebitPaymentForm() {
		return asahiDirectDebitPaymentForm;
	}
	/**
	 * @param asahiDirectDebitPaymentForm the asahiDirectDebitPaymentForm to set
	 */
	public void setAsahiDirectDebitPaymentForm(
			AsahiDirectDebitPaymentForm asahiDirectDebitPaymentForm) {
		this.asahiDirectDebitPaymentForm = asahiDirectDebitPaymentForm;
	}
	/**
	 * @return the personalName
	 */
	public String getPersonalName() {
		return personalName;
	}
	/**
	 * @param personalName the personalName to set
	 */
	public void setPersonalName(String personalName) {
		this.personalName = personalName;
	}
	/**
	 * @return the currentDate
	 */
	public String getCurrentDate() {
		return currentDate;
	}
	/**
	 * @param currentDate the currentDate to set
	 */
	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}
	/**
	 * @return the saveCreditCard
	 */
	public String getSaveCreditCard() {
		return saveCreditCard;
	}
	/**
	 * @param saveCreditCard the saveCreditCard to set
	 */
	public void setSaveCreditCard(String saveCreditCard) {
		this.saveCreditCard = saveCreditCard;
	}
	/**
	 * @return the asahiPaymentDetailsForm
	 */
	public AsahiPaymentDetailsForm getAsahiPaymentDetailsForm() {
		return asahiPaymentDetailsForm;
	}
	/**
	 * @param asahiPaymentDetailsForm the asahiPaymentDetailsForm to set
	 */
	public void setAsahiPaymentDetailsForm(AsahiPaymentDetailsForm asahiPaymentDetailsForm) {
		this.asahiPaymentDetailsForm = asahiPaymentDetailsForm;
	}
}
