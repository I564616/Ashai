
package com.sabmiller.salesforcerestclient;

import de.hybris.platform.util.Config;

import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;


public class SABMSalesForceTmdSMSPostHandler extends DefaultSABMSFPostRestRequestHandler<SalesForceEmailSmsPostRequest, SalesForceEmailSmsPostResponse>
{


	@Override
	public Class<SalesForceEmailSmsPostResponse> getResponseClass()
	{
		return SalesForceEmailSmsPostResponse.class;
	}

/*	@Override
	public String getRequestUrl()
	{

		return Config.getString("salesforce.token.email.rest.uri", "");
	}*/
	
	@Override
	protected String getSaleForcePostUrl()
	{
		return Config.getString("salesforce.tmd.sms_post_url", "");
	}
	
	@Override
	public HttpMethod getRequestMethod()
	{
		return HttpMethod.POST;
		//return HttpMethod.GET;
	}	
	
	@Override
	public MediaType getContentType()
	{
		return MediaType.APPLICATION_JSON;
	}

	
}

