/**
 *
 */
package com.sabmiller.salesforcerestclient;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author biswaranjan.sahu
 *
 */
public class SalesForceEmailSmsPostResponse
{
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("success")
	private boolean success;

	
		
	public String getId()
	{
		return this.id;
	}

	public void setId(final String id)
	{
		this.id = id;
	}
	
	public boolean getSuccess()
	{
		return this.success;
	}

	public void setSuccess(final boolean success)
	{
		this.success = success;
	}
	
	

}
