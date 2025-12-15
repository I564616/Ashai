package com.apb.core.card.payment;

import com.apb.core.exception.AsahiPaymentException;
import com.apb.facades.card.payment.AsahiPaymentDetailsData;


/**
 * @param <T>
 *
 */
public interface AsahiCreditCardPaymentService
{

	/**
	 * @param asahiPaymentDetailsData
	 * @param cartModel
	 * @throws AsahiPaymentException
	 */
	void makeCreditCardPaymentRequest(AsahiPaymentDetailsData asahiPaymentDetailsData) throws AsahiPaymentException;

}
