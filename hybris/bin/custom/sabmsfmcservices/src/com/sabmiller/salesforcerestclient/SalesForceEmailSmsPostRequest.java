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
public class SalesForceEmailSmsPostRequest
{
	
	@JsonProperty("id")
	private String id;

	
		
	public String getId()
	{
		return this.id;
	}

	public void setId(final String id)
	{
		this.id = id;
	}
	
	

}
