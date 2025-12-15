package com.sabmiller.integration.sap.price;

import de.hybris.platform.util.Config;

import org.springframework.http.HttpMethod;

import com.sabmiller.integration.restclient.commons.DefaultSABMPostRestRequestHandler;
import com.sabmiller.integration.sap.cup.request.CustomerUnitPricingRequest;
import com.sabmiller.integration.sap.cup.response.CustomerUnitPricingResponse;


public class CupRequestHandler extends DefaultSABMPostRestRequestHandler<CustomerUnitPricingRequest, CustomerUnitPricingResponse>
{

	@Override
	public Class<CustomerUnitPricingResponse> getResponseClass()
	{
		return CustomerUnitPricingResponse.class;
	}


	@Override
	public String getRequestUrl()
	{

		return Config.getString("services.outbound.cup.url", "");
	}


	@Override
	public HttpMethod getRequestMethod()
	{
		return HttpMethod.POST;
	}

}
