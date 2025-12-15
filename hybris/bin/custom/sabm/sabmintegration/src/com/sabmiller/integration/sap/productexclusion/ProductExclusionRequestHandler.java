package com.sabmiller.integration.sap.productexclusion;

import de.hybris.platform.util.Config;

import org.springframework.http.HttpMethod;

import com.sabmiller.integration.restclient.commons.DefaultSABMPostRestRequestHandler;
import com.sabmiller.integration.sap.productexclusion.request.ProductExclusionRequest;


public class ProductExclusionRequestHandler extends DefaultSABMPostRestRequestHandler<ProductExclusionRequest, String>
{

	@Override
	public Class<String> getResponseClass()
	{
		return String.class;
	}


	@Override
	public String getRequestUrl()
	{

		return Config.getString("services.outbound.productexclusion.url", "");
	}

	@Override
	public HttpMethod getRequestMethod()
	{
		return HttpMethod.POST;
	}

}
