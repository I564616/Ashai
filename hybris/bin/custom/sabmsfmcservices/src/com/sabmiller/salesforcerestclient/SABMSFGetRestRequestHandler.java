package com.sabmiller.salesforcerestclient;

import java.util.Map;



/**
 * The Interface SABMGetRestRequestHandler.
 *
 * @param <R>
 *           the generic type
 */
public interface SABMSFGetRestRequestHandler<Response>
{

	/**
	 * Send get request.
	 *
	 * @param url
	 *           the url
	 * @param urlVariables
	 *           the url variables
	 * @return the r
	 * @throws SABMSFIntegrationException
	 *            the SABM integration exception
	 */
	Response sendGetRequest(String url, Map<String, Object> urlVariables) throws SABMSFIntegrationException;
}
