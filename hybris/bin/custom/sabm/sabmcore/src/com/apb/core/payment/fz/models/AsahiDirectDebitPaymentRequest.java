package com.apb.core.payment.fz.models;

import com.google.gson.annotations.Expose;

public class AsahiDirectDebitPaymentRequest {

	@Expose
	private String account_name;
	
	@Expose
	private String account_number;
	
	@Expose
	private String bsb;
	
	@Expose
	private String account_type;

	/**
	 * @return the account_name
	 */
	public String getAccount_name() {
		return account_name;
	}

	/**
	 * @param account_name the account_name to set
	 */
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}

	/**
	 * @return the account_number
	 */
	public String getAccount_number() {
		return account_number;
	}

	/**
	 * @param account_number the account_number to set
	 */
	public void setAccount_number(String account_number) {
		this.account_number = account_number;
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
	 * @return the account_type
	 */
	public String getAccount_type() {
		return account_type;
	}

	/**
	 * @param account_type the account_type to set
	 */
	public void setAccount_type(String account_type) {
		this.account_type = account_type;
	}
}
