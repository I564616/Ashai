package com.apb.integration.credit.check.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AsahiCreditCheckRes
{
	@JsonProperty("creditCheckResponse")
	private List<AsahiCreditCheckResponse> creditCheckResponse = null;

	public List<AsahiCreditCheckResponse> getCreditCheckResponse()
	{
		return creditCheckResponse;
	}

	public void setCreditCheckResponse(List<AsahiCreditCheckResponse> creditCheckResponse)
	{
		this.creditCheckResponse = creditCheckResponse;
	}

}
