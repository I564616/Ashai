/**
 *
 */
package com.sabmiller.salesforcerestclient;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;


/**
 * The Class SABMRestClient.
 */
public class SABMSFRestClient
{

	/** The connection manager. */
	private HttpClientConnectionManager connectionManager;



	/**
	 * Sets the connection manager.
	 *
	 * @param connectionManager
	 *           the new connection manager
	 */
	public void setConnectionManager(final HttpClientConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
	}


	/**
	 * Gets the http client.
	 *
	 * @return the http client
	 */
	public CloseableHttpClient getHttpClient()
	{

		final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		httpClientBuilder.setConnectionManager(connectionManager);
		httpClientBuilder.disableCookieManagement();
		return httpClientBuilder.build();
	}


}