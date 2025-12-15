
package com.sabmiller.integration.sap.ordercreate;

import de.hybris.platform.util.Config;

import org.springframework.http.HttpMethod;

import com.sabmiller.integration.restclient.commons.DefaultSABMPostRestRequestHandler;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest;
import com.sabmiller.integration.sap.ordercreate.response.SalesOrderCreateResponse;


public class SalesOrderCreateRequestHandler
		extends DefaultSABMPostRestRequestHandler<SalesOrderCreateRequest, SalesOrderCreateResponse>
{


	@Override
	public Class<SalesOrderCreateResponse> getResponseClass()
	{
		return SalesOrderCreateResponse.class;
	}




	@Override
	public String getRequestUrl()
	{

		return Config.getString("sap.ordercreate.rest.uri", "");
	}


	@Override
	public HttpMethod getRequestMethod()
	{
		return HttpMethod.POST;
	}




}

