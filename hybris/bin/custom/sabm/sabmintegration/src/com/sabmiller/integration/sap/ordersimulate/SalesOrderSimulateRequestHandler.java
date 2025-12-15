package com.sabmiller.integration.sap.ordersimulate;

import de.hybris.platform.util.Config;

import org.springframework.http.HttpMethod;

import com.sabmiller.integration.restclient.commons.DefaultSABMPostRestRequestHandler;
import com.sabmiller.integration.sap.ordersimulate.request.SalesOrderSimulateRequest;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse;


public class SalesOrderSimulateRequestHandler extends
		DefaultSABMPostRestRequestHandler<SalesOrderSimulateRequest, SalesOrderSimulateResponse>

{

	@Override
	public Class<SalesOrderSimulateResponse> getResponseClass()
	{
		return SalesOrderSimulateResponse.class;
	}


	@Override
	public String getRequestUrl()
	{

		return Config.getString("sap.ordersimulate.rest.uri", "");
	}


	@Override
	public HttpMethod getRequestMethod()
	{
		return HttpMethod.POST;
	}

}
