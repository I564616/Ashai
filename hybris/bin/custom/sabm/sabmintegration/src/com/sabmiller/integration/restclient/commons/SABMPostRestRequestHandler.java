package com.sabmiller.integration.restclient.commons;

/**
 * The Interface SABMPostRestRequestHandler.
 *
 * @param <R>
 *           the generic type
 * @param <C>
 *           the generic type
 */
public interface SABMPostRestRequestHandler<Response, Request> extends SABMGetRestRequestHandler<Response>
{

	/**
	 * Send post request.
	 *
	 * @param request
	 *           the request
	 * @return the r
	 * @throws SABMIntegrationException
	 *            the SABM integration exception
	 */
	Response sendPostRequest(Request request) throws SABMIntegrationException;
}
