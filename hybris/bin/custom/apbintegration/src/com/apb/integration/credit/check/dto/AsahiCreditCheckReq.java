package com.apb.integration.credit.check.dto;

import java.util.List;


public class AsahiCreditCheckReq
{

	private List<AsahiCreditCheckRequest> creditCheckRequest = null;

	public List<AsahiCreditCheckRequest> getCreditCheckRequest()
	{
		return creditCheckRequest;
	}

	public void setCreditCheckRequest(List<AsahiCreditCheckRequest> creditCheckRequest)
	{
		this.creditCheckRequest = creditCheckRequest;
	}
}
