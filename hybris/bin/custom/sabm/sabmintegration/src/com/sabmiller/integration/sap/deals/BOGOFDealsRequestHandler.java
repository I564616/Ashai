package com.sabmiller.integration.sap.deals;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import com.sabmiller.integration.restclient.commons.DefaultSABMPostRestRequestHandler;
import com.sabmiller.integration.sap.deals.bogof.request.PricingBOGOFDealsRequest;
import com.sabmiller.integration.sap.deals.bogof.response.PricingBOGOFDealsResponse;
import com.sabmiller.integration.sap.ordersimulate.request.SalesOrderSimulateRequest;


public class BOGOFDealsRequestHandler extends
		DefaultSABMPostRestRequestHandler<PricingBOGOFDealsRequest, PricingBOGOFDealsResponse>

{


	@Autowired
	private Converter<CartData, SalesOrderSimulateRequest> salesOrderSimulateRequestConverter;




	@Override
	public Class<PricingBOGOFDealsResponse> getResponseClass()
	{
		return PricingBOGOFDealsResponse.class;
	}


	@Override
	public String getRequestUrl()
	{

		return Config.getString("services.outbound.bogof.url", "");
	}


	@Override
	public HttpMethod getRequestMethod()
	{
		return HttpMethod.POST;
	}


}
