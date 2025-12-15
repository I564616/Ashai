package com.sabmiller.salesforcerestclient;

import com.sabmiller.salesforcerestclient.SFTokenResponse;

/**
 * The Interface SABMPostRestRequestHandler.
 *
 * @param <R>
 *           the generic type
 * @param <C>
 *           the generic type
 */
public interface SABMSFPostRestRequestHandler<Response, Request> extends SABMSFGetRestRequestHandler<Response>
{

	/**
	 * Send post request.
	 *
	 * @param request
	 *           the request
	 * @return the r
	 * @throws SABMSFIntegrationException
	 *            the SABM integration exception
	 */
	Response sendPostTokenRequest(String request, String userId) throws SABMSFIntegrationException;
	
	Response sendPostRequest(String request, SFTokenResponse accessToken, String userId) throws SABMSFIntegrationException;
}
