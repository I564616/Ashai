package com.sabmiller.integration.sap.deals;

import de.hybris.platform.util.Config;

import org.springframework.http.HttpMethod;

import com.sabmiller.integration.restclient.commons.DefaultSABMPostRestRequestHandler;
import com.sabmiller.integration.sap.deals.pricediscount.request.PricingDiscountConditionsRequest;
import com.sabmiller.integration.sap.deals.pricediscount.response.PricingDiscountConditionsResponse;


public class DiscountDealsRequestHandler extends
		DefaultSABMPostRestRequestHandler<PricingDiscountConditionsRequest, PricingDiscountConditionsResponse>

{

	@Override
	public String getRequestUrl()
	{

		return Config.getString("services.outbound.discount.url", "");
	}


	@Override
	public HttpMethod getRequestMethod()
	{
		return HttpMethod.POST;
	}

	@Override
	public Class<PricingDiscountConditionsResponse> getResponseClass()
	{
		return PricingDiscountConditionsResponse.class;
	}

}
