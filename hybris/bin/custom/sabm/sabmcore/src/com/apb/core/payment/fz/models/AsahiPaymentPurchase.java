package com.apb.core.payment.fz.models;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import jakarta.annotation.Resource;

import com.apb.core.payment.fz.AsahiPaymentConfiguration;
import com.apb.core.payment.fz.AsahiPaymentGatewayContext;
import com.apb.core.payment.fz.exceptions.AsahiPaymentApiException;
import com.apb.core.payment.fz.exceptions.AsahiPaymentNetworkException;
import com.apb.core.payment.fz.net.AsahiPaymentResource;



/**
 * Payment Purchase service request class
 */
public class AsahiPaymentPurchase extends AsahiPaymentResource
{

	/**
	 * The Payment Provider ID
	 */
	private String id;
	/**
	 * The purchase amount
	 */
	private double amount;
	/**
	 * The purchase amount as a decimal
	 */
	private double decimal_amount;
	/**
	 * The total amount which has been captured
	 */
	private int captured_total;
	/**
	 * Indicates if a purchase has been captured or not
	 */
	private boolean captured;
	/**
	 * The authorization ID
	 */
	private String authorization;
	/**
	 * The card number
	 */
	private String card_number;
	/**
	 * The card holders name
	 */
	private String card_holder;
	/**
	 * The card expiry date
	 */
	private Date card_expiry;
	/**
	 * The card token
	 */
	private String card_token;
	/**
	 * Indicates if the transaction is successful
	 */
	private boolean successful;
	/**
	 * The message from the gateway for this transaction
	 */
	private String message;
	/**
	 * The reference for the transaction
	 */
	private String reference;
	/**
	 * The currency code for the transaction
	 */
	private String currency;
	/**
	 * The settlement date of the transaction
	 */
	private Date settlement_date;
	/**
	 * The date of the transaction
	 */
	private Date transaction_date;
	/**
	 * The Acquirer response code for the transaction
	 */
	private String response_code;

	/**
	 * The Request Retrieval Number
	 */
	private String rrn;

	/**
	 * The CVV match result
	 */
	private String cvv_match;

	/**
	 * The card type
	 */
	private String card_type;

	/**
	 * card category
	 */
	private String card_category;
	/**
	 * card sub category
	 */
	private String card_subcategory;
	/**
	 * transaction id
	 */
	private String transaction_id;
	/**
	 * captured amount
	 */
	private String captured_amount;


	@Resource(name = "asahiPaymentRefund")
	AsahiPaymentRefund asahiPaymentRefund;

	@Resource(name = "asahiPaymentCaptureRequest")
	AsahiPaymentCaptureRequest asahiPaymentCaptureRequest;

	private static final String urlSuffix = "purchases";
	private static final String findUrlSuffix = "purchases/%s";

	@Resource(name = "asahiPaymentConfiguration")
	AsahiPaymentConfiguration asahiPaymentConfiguration;


	/**
	 * Create a purchase with the option of capture or real-time capture
	 *
	 * @param amount
	 *           the amount to be charged (as an integer - i.e. $100.50 will be 10050)
	 * @param card_data
	 *           a HashMap<String,Object> of card data containing the card_expiry, card_number, card_holder,
	 *           card_security_code
	 * @param reference
	 *           the order reference, usually an invoice or order number
	 * @param ip
	 *           the customers IP address
	 * @param currency
	 *           the currency code for the order (e.g. AUD, USD etc)
	 * @param capture
	 *           indicates whether to capture this transaction immediately or not. If this is false the request will
	 *           become a pre-auth and a capture will be required to settle the funds.
	 * @param extraParameters
	 * @param ctx
	 *           the gateway context (authentication etc)
	 * @param card_token
	 * @return Purchase
	 * @throws IOException
	 * @throws AsahiPaymentNetworkException
	 * @throws AsahiPaymentApiException
	 */
	public AsahiPaymentResponse<AsahiPaymentPurchase> create(final double amount, final HashMap<String, Object> card_data,
			final String reference, final String ip, final String currency, final boolean capture,
			final HashMap<String, String> extraParameters, final AsahiPaymentGatewayContext ctx, final String card_token)
			throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		// Create the purchase message
		final AsahiPaymentPurchaseRequest request = new AsahiPaymentPurchaseRequest();
		request.setAmount(amount);
		request.setReference(reference);
		request.setCustomerIp(ip);
		request.setCapture(capture);
		request.setCurrency(currency);
		if (card_data != null)
		{
			request.setCard(card_data);
		}
		request.setCard_token(card_token);
		if (extraParameters != null)
		{
			request.setExtra(extraParameters);
		}

		final AsahiPaymentResponse<AsahiPaymentPurchase> response = doRequest(urlSuffix, request, RequestType.POST,
				AsahiPaymentPurchase.class, ctx);
		return response;
	}

	/**
	 * Finds a Purchase by the Purchase ID or the merchants reference
	 *
	 * @param idOrReference
	 *           the Fat Zebra ID or Reference for the record
	 * @return Purchase
	 */
	public AsahiPaymentPurchase find(final String idOrReference)
			throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		return find(idOrReference, asahiPaymentConfiguration.getContext());
	}

	/**
	 * Finds a Purchase by the Purchase ID or the merchants reference
	 *
	 * @param idOrReference
	 *           the Fat Zebra ID or Reference for the record
	 * @param ctx
	 *           the gateway context (authentication etc)
	 * @return Purchase
	 */
	public AsahiPaymentPurchase find(final String idOrReference, final AsahiPaymentGatewayContext ctx)
			throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		final AsahiPaymentResponse<AsahiPaymentPurchase> response = doRequest(String.format(findUrlSuffix, idOrReference), null,
				RequestType.GET, AsahiPaymentPurchase.class, ctx);
		return response.getResult();
	}

	/**
	 * Refunds the transaction for the amount specified
	 *
	 * @param amount
	 *           the amount to be refunded
	 * @param reference
	 *           the reference for the refund transaction
	 * @return boolean indicating outcome
	 */
	public boolean refund(final int amount, final String reference)
			throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		return refund(amount, reference, asahiPaymentConfiguration.getContext());
	}

	/**
	 * Refunds the transaction for the amount specified
	 *
	 * @param amount
	 *           the amount to be refunded
	 * @param reference
	 *           the reference for the refund transaction
	 * @param ctx
	 *           the gateway context (authentication etc)
	 * @return boolean indicating outcome
	 */
	public boolean refund(final double amount, final String reference, final AsahiPaymentGatewayContext ctx)
			throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		final AsahiPaymentRefund r = asahiPaymentRefund.create(amount, this.id, reference, ctx);
		return r.isSuccessful();
	}

	/**
	 * Performs a capture for a previously authorised, but not captured, transaction Note a capture can only be performed
	 * up to three days after the original authorisation
	 *
	 * @param amount
	 *           the amount to capture - must be less then or equal to the original transaction amount
	 * @return boolean indicating outcome
	 */
	public boolean capture(final int amount) throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		return capture(amount, asahiPaymentConfiguration.getContext());
	}

	/**
	 * Performs a capture for a previously authorised, but not captured, transaction Note a capture can only be performed
	 * up to three days after the original authorisation
	 *
	 * @param amount
	 *           the amount to capture - must be less then or equal to the original transaction amount
	 * @param ctx
	 *           the gateway context (authentication etc)
	 * @return boolean indicating outcome
	 */
	public boolean capture(final int amount, final AsahiPaymentGatewayContext ctx)
			throws IOException, AsahiPaymentNetworkException, AsahiPaymentApiException
	{
		final AsahiPaymentResponse<AsahiPaymentCaptureRequest> cr = asahiPaymentCaptureRequest.create(amount, this.id, ctx);

		if (cr.getResult().isSuccessful())
		{
			this.captured_total = amount;
			this.captured = true;
		}

		return cr.getResult().isSuccessful();
	}

	/**
	 * @return
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @return
	 */
	public double getAmount()
	{
		return amount;
	}

	/**
	 * @param amount
	 */
	public void setAmount(final double amount)
	{
		this.amount = amount;
	}

	/**
	 * @return
	 */
	public double getDecimal_amount()
	{
		return decimal_amount;
	}

	/**
	 * @param decimal_amount
	 */
	public void setDecimal_amount(final double decimal_amount)
	{
		this.decimal_amount = decimal_amount;
	}

	/**
	 * @return
	 */
	public int getCaptured_total()
	{
		return captured_total;
	}

	/**
	 * @param captured_total
	 */
	public void setCaptured_total(final int captured_total)
	{
		this.captured_total = captured_total;
	}

	/**
	 * @return
	 */
	public boolean isCaptured()
	{
		return captured;
	}

	/**
	 * @param captured
	 */
	public void setCaptured(final boolean captured)
	{
		this.captured = captured;
	}

	/**
	 * @return
	 */
	public String getAuthorization()
	{
		return authorization;
	}

	/**
	 * @param authorization
	 */
	public void setAuthorization(final String authorization)
	{
		this.authorization = authorization;
	}

	/**
	 * @return
	 */
	public String getCard_number()
	{
		return card_number;
	}

	/**
	 * @param card_number
	 */
	public void setCard_number(final String card_number)
	{
		this.card_number = card_number;
	}

	/**
	 * @return
	 */
	public String getCard_holder()
	{
		return card_holder;
	}

	/**
	 * @param card_holder
	 */
	public void setCard_holder(final String card_holder)
	{
		this.card_holder = card_holder;
	}

	/**
	 * @return
	 */
	public Date getCard_expiry()
	{
		return card_expiry;
	}

	/**
	 * @param card_expiry
	 */
	public void setCard_expiry(final Date card_expiry)
	{
		this.card_expiry = card_expiry;
	}

	/**
	 * @return
	 */
	public String getCard_token()
	{
		return card_token;
	}

	/**
	 * @param card_token
	 */
	public void setCard_token(final String card_token)
	{
		this.card_token = card_token;
	}

	/**
	 * @return
	 */
	public boolean isSuccessful()
	{
		return successful;
	}

	/**
	 * @param successful
	 */
	public void setSuccessful(final boolean successful)
	{
		this.successful = successful;
	}

	/**
	 * @return
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @param message
	 */
	public void setMessage(final String message)
	{
		this.message = message;
	}

	/**
	 * @return
	 */
	public String getReference()
	{
		return reference;
	}

	/**
	 * @param reference
	 */
	public void setReference(final String reference)
	{
		this.reference = reference;
	}

	/**
	 * @return
	 */
	public String getCurrency()
	{
		return currency;
	}

	/**
	 * @param currency
	 */
	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	/**
	 * @return
	 */
	public Date getSettlement_date()
	{
		return settlement_date;
	}

	/**
	 * @param settlement_date
	 */
	public void setSettlement_date(final Date settlement_date)
	{
		this.settlement_date = settlement_date;
	}

	/**
	 * @return
	 */
	public Date getTransaction_date()
	{
		return transaction_date;
	}

	/**
	 * @param transaction_date
	 */
	public void setTransaction_date(final Date transaction_date)
	{
		this.transaction_date = transaction_date;
	}

	/**
	 * @return
	 */
	public String getResponse_code()
	{
		return response_code;
	}

	/**
	 * @param response_code
	 */
	public void setResponse_code(final String response_code)
	{
		this.response_code = response_code;
	}

	/**
	 * @return
	 */
	public String getRrn()
	{
		return rrn;
	}

	/**
	 * @param rrn
	 */
	public void setRrn(final String rrn)
	{
		this.rrn = rrn;
	}

	/**
	 * @return
	 */
	public String getCvv_match()
	{
		return cvv_match;
	}

	/**
	 * @param cvv_match
	 */
	public void setCvv_match(final String cvv_match)
	{
		this.cvv_match = cvv_match;
	}

	/**
	 * @return
	 */
	public String getCard_type()
	{
		return card_type;
	}

	/**
	 * @param card_type
	 */
	public void setCard_type(final String card_type)
	{
		this.card_type = card_type;
	}

	/**
	 * @return
	 */
	public String getCard_category()
	{
		return card_category;
	}

	/**
	 * @param card_category
	 */
	public void setCard_category(final String card_category)
	{
		this.card_category = card_category;
	}

	/**
	 * @return
	 */
	public String getCard_subcategory()
	{
		return card_subcategory;
	}

	/**
	 * @param card_subcategory
	 */
	public void setCard_subcategory(final String card_subcategory)
	{
		this.card_subcategory = card_subcategory;
	}

	/**
	 * @return
	 */
	public String getTransaction_id()
	{
		return transaction_id;
	}

	/**
	 * @param transaction_id
	 */
	public void setTransaction_id(final String transaction_id)
	{
		this.transaction_id = transaction_id;
	}

	/**
	 * @return
	 */
	public String getCaptured_amount()
	{
		return captured_amount;
	}

	/**
	 * @param captured_amount
	 */
	public void setCaptured_amount(final String captured_amount)
	{
		this.captured_amount = captured_amount;
	}

	/**
	 * @return
	 */
	public AsahiPaymentRefund getAsahiPaymentRefund()
	{
		return asahiPaymentRefund;
	}

	/**
	 * @param asahiPaymentRefund
	 */
	public void setAsahiPaymentRefund(final AsahiPaymentRefund asahiPaymentRefund)
	{
		this.asahiPaymentRefund = asahiPaymentRefund;
	}

	/**
	 * @return
	 */
	public AsahiPaymentCaptureRequest getAsahiPaymentCaptureRequest()
	{
		return asahiPaymentCaptureRequest;
	}

	/**
	 * @param asahiPaymentCaptureRequest
	 */
	public void setAsahiPaymentCaptureRequest(final AsahiPaymentCaptureRequest asahiPaymentCaptureRequest)
	{
		this.asahiPaymentCaptureRequest = asahiPaymentCaptureRequest;
	}


}
