package com.apb.core.payment.fz.models;

import com.google.gson.annotations.Expose;


/**
 * The refund request which is sent to the gateway
 */
public class AsahiPaymentRefundRequest
{
	/**
	 * The Original Transaction ID
	 */
	@Expose
	public String transaction_id;
	/**
	 * The refund reference
	 */
	@Expose
	public String reference;
	/**
	 * The refund amount
	 */
	@Expose
	public double amount;

	/**
	 * Sets the original transaction ID
	 *
	 * @param val
	 *           the original ID
	 */
	public void setOriginalTransactionId(final String val)
	{
		this.transaction_id = val;
	}

	/**
	 * Sets the reference for the refund
	 *
	 * @param val
	 *           the reference
	 */
	public void setReference(final String val)
	{
		this.reference = val;
	}

	/**
	 * Sets the amount for the refund
	 *
	 * @param val
	 *           the amount
	 */
	public void setAmount(final double val)
	{
		this.amount = val;
	}
}
