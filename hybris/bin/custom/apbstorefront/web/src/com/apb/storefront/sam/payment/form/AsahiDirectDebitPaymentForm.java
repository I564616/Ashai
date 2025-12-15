package com.apb.storefront.sam.payment.form;

/**
 * The Asahi Direct Debit Payment Form.
 *
 * @author Kuldeep.Singh1
 */
public class AsahiDirectDebitPaymentForm
{
	private String tokenType;
	private String token;
	private String accountName;
	private String accountNum;
	private String bsb;
	private String suburb;
	private String region;
	private String cardNumber;
	private String cardExpiry;
	private String cardHolderName;
	private String cardTypeInfo;
	private String cardToken;
	
	/**
	 * @return the tokenType
	 */
	public String getTokenType() {
		return tokenType;
	}
	/**
	 * @param tokenType the tokenType to set
	 */
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}
	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	/**
	 * @return the accountNum
	 */
	public String getAccountNum() {
		return accountNum;
	}
	/**
	 * @param accountNum the accountNum to set
	 */
	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}
	/**
	 * @return the bsb
	 */
	public String getBsb() {
		return bsb;
	}
	/**
	 * @param bsb the bsb to set
	 */
	public void setBsb(String bsb) {
		this.bsb = bsb;
	}
	/**
	 * @return the suburb
	 */
	public String getSuburb() {
		return suburb;
	}
	/**
	 * @param suburb the suburb to set
	 */
	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}
	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}
	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}
	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}
	/**
	 * @param cardNumber the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	/**
	 * @return the cardExpiry
	 */
	public String getCardExpiry() {
		return cardExpiry;
	}
	/**
	 * @param cardExpiry the cardExpiry to set
	 */
	public void setCardExpiry(String cardExpiry) {
		this.cardExpiry = cardExpiry;
	}
	/**
	 * @return the cardTypeInfo
	 */
	public String getCardTypeInfo() {
		return cardTypeInfo;
	}
	/**
	 * @param cardTypeInfo the cardTypeInfo to set
	 */
	public void setCardTypeInfo(String cardTypeInfo) {
		this.cardTypeInfo = cardTypeInfo;
	}
	/**
	 * @return the cardHolderName
	 */
	public String getCardHolderName() {
		return cardHolderName;
	}
	/**
	 * @param cardHolderName the cardHolderName to set
	 */
	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}
	/**
	 * @return the cardToken
	 */
	public String getCardToken() {
		return cardToken;
	}
	/**
	 * @param cardToken the cardToken to set
	 */
	public void setCardToken(String cardToken) {
		this.cardToken = cardToken;
	}
}
