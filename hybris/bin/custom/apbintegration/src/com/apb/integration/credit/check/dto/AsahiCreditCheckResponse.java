package com.apb.integration.credit.check.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AsahiCreditCheckResponse
{

	@JsonProperty("accountNum")
	private String accountNum;
	@JsonProperty("creditRemaining")
	private double creditRemaining;
	@JsonProperty("isBlocked")
	private String isBlocked;

	public String getAccountNum()
	{
		return accountNum;
	}

	public void setAccountNum(String accountNum)
	{
		this.accountNum = accountNum;
	}

	public double getCreditRemaining()
	{
		return creditRemaining;
	}

	public void setCreditRemaining(double creditRemaining)
	{
		this.creditRemaining = creditRemaining;
	}

	public String getIsBlocked()
	{
		return isBlocked;
	}

	public void setIsBlocked(String isBlocked)
	{
		this.isBlocked = isBlocked;
	}
}
