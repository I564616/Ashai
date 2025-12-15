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
public class SFTokenResponse
{
	@JsonProperty("AuthKey")
	private String authKey;

	@JsonProperty("animals")
	private List<String> animals;
	
	@JsonProperty("access_token")
	private String accessToken;
	
	@JsonProperty("scope")
	private String scope;
	
	@JsonProperty("instance_url")
	private String instanceUrl;
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("token_type")
	private String tokenType;
	
	
	public String getAccessToken()
	{
		return this.accessToken;
	}

	public void setAccessToken(final String accessToken)
	{
		this.accessToken = accessToken;
	}
	
	public String getScope()
	{
		return this.scope;
	}

	public void setScope(final String scope)
	{
		this.scope = scope;
	}
	
	public String getInstanceUrl()
	{
		return this.instanceUrl;
	}

	public void setInstanceUrl(final String instanceUrl)
	{
		this.instanceUrl = instanceUrl;
	}
	
	public String getId()
	{
		return this.id;
	}

	public void setId(final String id)
	{
		this.id = id;
	}
	
	public String getTokenType()
	{
		return this.tokenType;
	}

	public void setTokenType(final String tokenType)
	{
		this.tokenType = tokenType;
	}
		


	/**
	 * @return the animals
	 */
	public List<String> getAnimals()
	{
		return animals;
	}

	/**
	 * @param animals
	 *           the animals to set
	 */
	public void setAnimals(final List<String> animals)
	{
		this.animals = animals;
	}

	public String getAuthKey()
	{
		return this.authKey;
	}

	public void setAuthKey(final String var1)
	{
		this.authKey = var1;
	}

}
