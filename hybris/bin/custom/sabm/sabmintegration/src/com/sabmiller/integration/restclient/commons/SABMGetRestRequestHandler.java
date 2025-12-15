package com.sabmiller.integration.restclient.commons;

import java.util.Map;



/**
 * The Interface SABMGetRestRequestHandler.
 *
 * @param <R>
 *           the generic type
 */
public interface SABMGetRestRequestHandler<Response>
{

	/**
	 * Send get request.
	 *
	 * @param url
	 *           the url
	 * @param urlVariables
	 *           the url variables
	 * @return the r
	 * @throws SABMIntegrationException
	 *            the SABM integration exception
	 */
	Response sendGetRequest(String url, Map<String, Object> urlVariables) throws SABMIntegrationException;
}
