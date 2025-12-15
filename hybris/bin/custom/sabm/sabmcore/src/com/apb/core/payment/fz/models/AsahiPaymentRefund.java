package com.apb.core.payment.fz.models;

import java.io.IOException;
import java.util.Date;

import jakarta.annotation.Resource;

import com.apb.core.payment.fz.AsahiPaymentConfiguration;
import com.apb.core.payment.fz.AsahiPaymentGatewayContext;
import com.apb.core.payment.fz.exceptions.AsahiPaymentApiException;
import com.apb.core.payment.fz.exceptions.AsahiPaymentNetworkException;
import com.apb.core.payment.fz.net.AsahiPaymentResource;


/**
 * Represents a refund from the Gateway
 */
public class AsahiPaymentRefund extends AsahiPaymentResource
{
	/**
	 * The gateway ID
	 */
	private String id;
	/**
	 * The reference for the transaction
	 */
	private String reference;
	/**
	 * The refunded amount
	 */
	private double amount;
	/**
	 * The authorization ID
	 */
	private String authorization;
	/**
	 * The transaction message
	 */
	private String message;
	/**
	 * The card holders name
	 */
	private String card_holder;
	/**
	 * The card number
	 */
	private String card_number;
	/**
	 * The card expiry date
	 */
	private Date card_expiry;
	/**
	 * The card type
	 */
	private String card_type;
	/**
	 * The transaction date
	 */
	private Date transaction_date;
	/**
	 * Indicates if the transaction was successful
	 */
	private boolean successful;
	/**
	 * The response code from the acquirer
	 */
	private String response_code;

	@Resource(name = "asahiPaymentPurchase")
	private AsahiPaymentPurchase asahiPaymentPurchase;

	@Resource(name = "asahiPaymentConfiguration")
	AsahiPaymentConfiguration asahiPaymentConfiguration;

	/**
	 * Refunds a transaction based on the original transaction ID
	 *
	 * @param amount
	 *           the refund amount
	 * @param originalTransactionId
	 *           the original transaction ID
	 * @return Refund object representing result
	 * @throws IOException
	 * @throws AsahiPaymentNetworkException
	 * @throws AsahiPaymentApiException
	 */
	public AsahiPaymentRefund create(final double amount, final String originalTransactionId)
			throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		return create(amount, originalTransactionId, asahiPaymentConfiguration.getContext());
	}

	/**
	 * Refunds a transaction based on the original transaction ID
	 *
	 * @param amount
	 *           the refund amount
	 * @param originalTransactionId
	 *           the original transaction ID
	 * @param ctx
	 *           the gateway context for authentication
	 * @return Refund object representing result
	 * @throws IOException
	 * @throws AsahiPaymentNetworkException
	 * @throws AsahiPaymentApiException
	 */
	public AsahiPaymentRefund create(final double amount, final String originalTransactionId, final AsahiPaymentGatewayContext ctx)
			throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		final AsahiPaymentPurchase p = asahiPaymentPurchase.find(originalTransactionId, ctx);
		return create(amount, originalTransactionId, p.getReference(), ctx);
	}

	/**
	 * Refunds a transaction based on the original transaction ID with a reference
	 *
	 * @param amount
	 *           the refund amount
	 * @param originalTransactionId
	 *           the original transaction ID
	 * @param reference
	 *           the refund reference
	 * @return Refund object representing result
	 * @throws IOException
	 * @throws AsahiPaymentNetworkException
	 * @throws AsahiPaymentApiException
	 */
	public AsahiPaymentRefund create(final double amount, final String originalTransactionId, final String reference)
			throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		return create(amount, originalTransactionId, reference, asahiPaymentConfiguration.getContext());
	}

	/**
	 * Refunds a transaction based on the original transaction ID with a reference
	 *
	 * @param amount
	 *           the refund amount
	 * @param originalTransactionId
	 *           the original transaction ID
	 * @param reference
	 *           the refund reference
	 * @param ctx
	 *           the gateway context for authentication
	 * @return Refund object representing result
	 * @throws IOException
	 * @throws AsahiPaymentNetworkException
	 * @throws AsahiPaymentApiException
	 */
	public AsahiPaymentRefund create(final double amount, final String originalTransactionId, final String reference,
			final AsahiPaymentGatewayContext ctx) throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		final AsahiPaymentRefundRequest request = new AsahiPaymentRefundRequest();
		request.setAmount(amount);
		request.setReference(reference);
		request.setOriginalTransactionId(originalTransactionId);

		final AsahiPaymentResponse<AsahiPaymentRefund> response = doRequest("refunds", request, RequestType.POST,
				AsahiPaymentRefund.class, ctx);
		return response.getResult();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCard_holder() {
		return card_holder;
	}

	public void setCard_holder(String card_holder) {
		this.card_holder = card_holder;
	}

	public String getCard_number() {
		return card_number;
	}

	public void setCard_number(String card_number) {
		this.card_number = card_number;
	}

	public Date getCard_expiry() {
		return card_expiry;
	}

	public void setCard_expiry(Date card_expiry) {
		this.card_expiry = card_expiry;
	}

	public String getCard_type() {
		return card_type;
	}

	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}

	public Date getTransaction_date() {
		return transaction_date;
	}

	public void setTransaction_date(Date transaction_date) {
		this.transaction_date = transaction_date;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getResponse_code() {
		return response_code;
	}

	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}


}
