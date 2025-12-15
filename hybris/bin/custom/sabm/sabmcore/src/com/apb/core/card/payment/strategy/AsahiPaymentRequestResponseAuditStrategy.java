package com.apb.core.card.payment.strategy;


import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionData;

import com.apb.facades.card.payment.AsahiPaymentDetailsData;
import com.apb.facades.card.payment.AsahiPaymentPurchaseResponseData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;


/**
 *
 */
public interface AsahiPaymentRequestResponseAuditStrategy
{

	/**
	 * @param asahiPaymentDetailsData
	 * @param cartData
	 * @param defaultCurrency
	 * @param capture
	 */
	void makePaymentRequestAuditEntry(PaymentTransactionData paymentTransactionData);

	PaymentTransactionData createPaymentTransactionData(final AsahiPaymentDetailsData asahiPaymentDetailsData,
			final String defaultCurrency, final boolean capture);

	void updatePaymentTransactionEntryData(PaymentTransactionData paymentTransactionData,
			AsahiPaymentPurchaseResponseData asahiPaymentPurchaseResponseData);

	/**
	 * Method to make audit entry for SAM Transactions.
	 * 
	 * @param paymentTransactionData
	 * @param asahiSAMPaymentData
	 */
	void makeSAMPaymentRequestAuditEntry(PaymentTransactionData paymentTransactionData, AsahiSAMPaymentData asahiSAMPaymentData);
}
