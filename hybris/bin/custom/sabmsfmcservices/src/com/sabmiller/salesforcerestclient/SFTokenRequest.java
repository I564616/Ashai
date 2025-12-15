/**
 *
 */
package com.sabmiller.salesforcerestclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.hybris.platform.util.Config;

/**
 * @author biswaranjan.sahu
 *
 */
public class SFTokenRequest
{
	@JsonProperty("grant_type")
	private String grantType;
	
	@JsonProperty("assertion")
	private String assertion;
	
	public SFTokenRequest()
	{
		
	}
	
	public SFTokenRequest(String assertion)
	{
		this.grantType = Config.getString("salesforce.token.granttype", "");
		this.assertion = assertion;
	}
	
	public String getGrantType()
	{
		return grantType;
	}
	
	public void setGrantType(final String grantType)
	{
		this.grantType = grantType;
	}
	
	public String getAssertion()
	{
		return assertion;
	}
	
	public void setAssertion(final String assertion)
	{
		this.assertion = assertion;
	}
	
	public String getSaleforceAUthoniticateInfo(){
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("grant_type="+Config.getString("salesforce.jwt.token.grantType", ""));
		buffer.append("&client_id="+Config.getString("salesforce.jwt.token.clientId", ""));
		buffer.append("&client_secret="+Config.getString("salesforce.jwt.token.clientSecret", ""));
		buffer.append("&username="+Config.getString("salesforce.jwt.token.username", ""));
		buffer.append("&password="+Config.getString("salesforce.jwt.token.password", ""));
		return buffer.toString();
	}

	/*
	getSpecialSalesforceAuthenticateInfo : Get the Salesforce Authentication info for special cases
	*/
	public String getOldSalesforceAuthenticateInfo()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("grant_type="+Config.getString("salesforce.jwt.token.old.grantType", ""));
		buffer.append("&client_id="+Config.getString("salesforce.jwt.token.old.clientId", ""));
		buffer.append("&client_secret="+Config.getString("salesforce.jwt.token.old.clientSecret", ""));
		buffer.append("&username="+Config.getString("salesforce.jwt.token.old.username", ""));
		buffer.append("&password="+Config.getString("salesforce.jwt.token.old.password", ""));
		return buffer.toString();
	}
	

}
