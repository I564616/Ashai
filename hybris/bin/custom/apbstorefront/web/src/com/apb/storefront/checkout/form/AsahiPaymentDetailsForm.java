package com.apb.storefront.checkout.form;

/**
 * Asahi payment details form .
 */
public class AsahiPaymentDetailsForm
{
	private String message;
	private String cardExpiry;
	private String cardNumber;
	private String CardTypeInfo;
	private String responseCode;
	private String cardToken;
	private String cardHolderName;



	public String getCardHolderName()
	{
		return cardHolderName;
	}

	public void setCardHolderName(final String cardHolderName)
	{
		this.cardHolderName = cardHolderName;
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
	public String getCardExpiry()
	{
		return cardExpiry;
	}

	/**
	 * @param cardExpiry
	 */
	public void setCardExpiry(final String cardExpiry)
	{
		this.cardExpiry = cardExpiry;
	}

	/**
	 * @return
	 */
	public String getCardNumber()
	{
		return cardNumber;
	}

	/**
	 * @param cardNumber
	 */
	public void setCardNumber(final String cardNumber)
	{
		this.cardNumber = cardNumber;
	}

	/**
	 * @return
	 */
	public String getCardTypeInfo()
	{
		return CardTypeInfo;
	}

	/**
	 * @param cardTypeInfo
	 */
	public void setCardTypeInfo(final String cardTypeInfo)
	{
		CardTypeInfo = cardTypeInfo;
	}

	/**
	 * @return
	 */
	public String getResponseCode()
	{
		return responseCode;
	}

	public void setResponseCode(final String responseCode)
	{
		this.responseCode = responseCode;
	}

	public String getCardToken()
	{
		return cardToken;
	}

	public void setCardToken(final String cardToken)
	{
		this.cardToken = cardToken;
	}



}
