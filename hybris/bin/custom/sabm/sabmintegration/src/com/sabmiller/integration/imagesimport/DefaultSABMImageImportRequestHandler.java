package com.sabmiller.integration.imagesimport;

import de.hybris.platform.util.Config;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import com.sabmiller.integration.imagesimport.pojo.SkuVantageResponseItem;
import com.sabmiller.integration.restclient.commons.SABMAbstractGetRestRequestHandler;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;


/**
 * The Class DefaultSABMImageImportRequestHandler.
 */
public class DefaultSABMImageImportRequestHandler extends SABMAbstractGetRestRequestHandler<SkuVantageResponseItem[]> implements
		SABMImageImportRequestHandler

{
	/** The uri template map. */
	@Resource(name = "skuVantageUriTemplateMap")
	Map<String, Object> uriTemplateMap;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.integration.imagesimport.SABMImageImportRequestHandler#sendGetRequestSingleProduct(java.lang.String)
	 */
	@Override
	public SkuVantageResponseItem[] sendGetRequestSingleProduct(final String product) throws SABMIntegrationException
	{
		if (StringUtils.isNotEmpty(product))
		{
			final Map<String, Object> urlVariables = new HashMap<>(uriTemplateMap);

			urlVariables.put("ean", product);

			final String uriTemplate = getRequestUrl();

			if (StringUtils.isNotEmpty(uriTemplate))
			{
				return super.sendGetRequest(uriTemplate, urlVariables);
			}
		}

		return new SkuVantageResponseItem[0];
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.restclient.commons.SABMAbstractGetRestRequestHandler#getResponseClass()
	 */
	@Override
	public Class<SkuVantageResponseItem[]> getResponseClass()
	{
		return SkuVantageResponseItem[].class;
	}

	/**
	 * Gets the request url.
	 *
	 * @return the request url
	 */
	public String getRequestUrl()
	{
		return Config.getString("import.image.uri.template", "");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.restclient.commons.SABMAbstractGetRestRequestHandler#getRequestMethod()
	 */
	@Override
	public HttpMethod getRequestMethod()
	{
		return HttpMethod.GET;
	}

}
