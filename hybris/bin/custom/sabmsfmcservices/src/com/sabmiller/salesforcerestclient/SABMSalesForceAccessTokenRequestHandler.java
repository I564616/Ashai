
package com.sabmiller.salesforcerestclient;

import de.hybris.platform.util.Config;

import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;



public class SABMSalesForceAccessTokenRequestHandler extends DefaultSABMSFPostRestRequestHandler<SFTokenRequest, SFTokenResponse>
{


	@Override
	public Class<SFTokenResponse> getResponseClass()
	{
		return SFTokenResponse.class;
	}


	/*@Override
	public String getRequestUrl()
	{

		return Config.getString("salesforce.token.email.rest.uri", "");
	}*/	
		

	@SuppressWarnings("static-access")
	@Override
	public MediaType getContentType()
	{
		return MediaType.APPLICATION_FORM_URLENCODED;
	}

	@Override
	public HttpMethod getRequestMethod()
	{
		return HttpMethod.POST;
		//return HttpMethod.GET;
	}
	

}

