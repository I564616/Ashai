/**
 *
 */
package com.sabmiller.integration.restclient.commons;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;



/**
 * The Class DefaultSABMPostRestRequestHandler.
 *
 * @param <WebServiceRequest>
 *           the generic type
 * @param <WebServiceResponse>
 *           the generic type
 */
public class DefaultSABMPostRestRequestHandler<WebServiceRequest, WebServiceResponse>
		extends SABMAbstractGetRestRequestHandler<WebServiceResponse>
		implements SABMPostRestRequestHandler<WebServiceResponse, WebServiceRequest>
{

	/** The Constant LOG. */
	private final static Logger LOG = LoggerFactory.getLogger(DefaultSABMPostRestRequestHandler.class);

	private String requestUrl;

	/**
	 * Send post request.
	 *
	 * @param request
	 *           the request
	 * @return the web service response
	 * @throws SABMIntegrationException
	 *            the SABM integration exception
	 */
	@Override
	public WebServiceResponse sendPostRequest(final WebServiceRequest request) throws SABMIntegrationException
	{
		final HttpEntity<WebServiceRequest> requestEntity;

		requestEntity = new HttpEntity<>(request, getHttpHeaders());
		ResponseEntity<WebServiceResponse> result = null;
		try
		{
			if (StringUtils.isNotEmpty(getRequestUrl()))
			{
				result = getCustomRestTemplate().exchange(getRequestUrl(), getRequestMethod(), requestEntity, getResponseClass());
			}
			else
			{
				throw new SABMIntegrationException("URL cannot be null");
			}

		}
		catch (final RestClientException ex)
		{
			LOG.error("Rest post call exception: " + getRequestUrl() + ":" + ex.getMessage());
			throw new SABMIntegrationException("web service exception", ex);
		}

		return result.getBody();
	}

	
	/**
	 * @return
	 */
	private RestTemplate getCustomRestTemplate()
	{
		RestTemplate restTemplate = getSabmRestTemplate();
		restTemplate.getMessageConverters().clear();
		restTemplate.getMessageConverters().add(new Jaxb2RootElementHttpMessageConverter());
		return restTemplate;
	}

	/**
	 * Gets the request url.
	 *
	 * @return the request url
	 */


	protected String getRequestUrl()
	{
		return requestUrl;
	}

	/**
	 * @param requestUrl
	 *           the requestUrl to set
	 */
	public void setRequestUrl(final String requestUrl)
	{
		this.requestUrl = requestUrl;
	}



}
