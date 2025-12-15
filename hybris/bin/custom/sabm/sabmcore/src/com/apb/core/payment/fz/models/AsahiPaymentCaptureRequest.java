package com.apb.core.payment.fz.models;

import java.io.IOException;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.apb.core.payment.fz.AsahiPaymentGatewayContext;
import com.apb.core.payment.fz.exceptions.AsahiPaymentApiException;
import com.apb.core.payment.fz.exceptions.AsahiPaymentNetworkException;
import com.apb.core.payment.fz.net.AsahiPaymentResource;
import com.google.gson.annotations.Expose;


/**
 *
 * This will be used to capture request to the gateway
 */
public class AsahiPaymentCaptureRequest extends AsahiPaymentResource
{
	/**
	 * The amount of the capture
	 */
	@Expose
	private double amount;

	/**
	 * The ID for the authorisation
	 */
	private String id;

	/**
	 * Indicates a successful capture
	 */
	private boolean successful;



	public String getId()
	{
		return id;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public boolean isSuccessful()
	{
		return successful;
	}

	public void setSuccessful(final boolean successful)
	{
		this.successful = successful;
	}

	public double getAmount()
	{
		return amount;
	}

	private static final String urlSuffix = "purchases/%s/capture";

	private static final Logger LOG = LoggerFactory.getLogger(AsahiPaymentCaptureRequest.class);

	/**
	 * use to make capture request
	 *
	 * @param amount
	 *           the amount of the capture
	 * @param transactionId
	 *           the authorisation transaction ID
	 * @param ctx
	 *           the gateway context for authentication
	 * @return CaptureRequest with result etc
	 * @throws IOException
	 * @throws AsahiPaymentNetworkException
	 * @throws AsahiPaymentApiException
	 */
	public AsahiPaymentResponse<AsahiPaymentCaptureRequest> create(final double amount, final String transactionId,
			final AsahiPaymentGatewayContext ctx) throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		final AsahiPaymentCaptureRequest request = new AsahiPaymentCaptureRequest();
		request.setAmount(amount);
		request.setTransactionId(transactionId);

		LOG.info("##### Capture request gateway context with request data #####");
		LOG.info("is sandbox enabled :" + ctx.isSandbox() + "username " + ctx.getUsername() + "token " + ctx.getToken()
				+ "Amount captured " + amount + "Transaction is " + transactionId);
		LOG.info("###### Capture request end ######");

		final AsahiPaymentResponse<AsahiPaymentCaptureRequest> response = doRequest(String.format(urlSuffix, transactionId),
				request, RequestType.POST, AsahiPaymentCaptureRequest.class, ctx);
		if (response.getResult() != null)
		{
			LOG.info("Inside AsahiPaymentCaptureRequest response has valid results");
		}
		else
		{
			LOG.info("No result");
		}
		return response;
	}

	/**
	 * Sets the amount of the capture
	 *
	 * @param value
	 *           the amount of the capture
	 */
	public void setAmount(final double value)
	{
		this.amount = value;
	}

	/**
	 * Sets the transaction ID of the authorisation
	 *
	 * @param value
	 *           the transaction ID
	 */
	public void setTransactionId(final String value)
	{
		this.id = value;
	}
}
