package com.sabmiller.salesforcerestclient;

import org.apache.hc.core5.http.HttpHost;

import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;


/**
 * A factory for creating SABMHttpComponentsClientHttpRequest objects.
 */
public class SABMSFHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory
{

	/** The http host. */
	private HttpHost httpHost;

	/** The connection timeout. */
	private int connectionTimeout = 300000;

	/** The auth cache. */
	private AuthCache authCache;

	/** The basic auth scheme. */
	private BasicScheme basicAuthScheme;

	/** The local httpcontext. */
	private BasicHttpContext localHttpcontext;

	/**
	 * Instantiates a new SABM http components client http request factory.
	 *
	 * @param sabmRestClient
	 *           the sabm rest client
	 */
	public SABMSFHttpComponentsClientHttpRequestFactory(final SABMSFRestClient sabmRestClient)
	{

		super(sabmRestClient.getHttpClient());
		this.setConnectTimeout(connectionTimeout);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.http.client.HttpComponentsClientHttpRequestFactory#createHttpContext(org.springframework.http.
	 * HttpMethod, java.net.URI)
	 */
	@Override
	protected HttpContext createHttpContext(final HttpMethod httpMethod, final URI uri)
	{
		authCache.put(httpHost, basicAuthScheme);
		localHttpcontext.setAttribute("AuthCache", authCache);

		return localHttpcontext;
	}




	/**
	 * Sets the http host.
	 *
	 * @param httpHost
	 *           the new http host
	 */
    public void setHttpHost(final HttpHost httpHost)
	{
		this.httpHost = httpHost;
	}



	/**
	 * Sets the connection timeout.
	 *
	 * @param connectionTimeout
	 *           the new connection timeout
	 */
	public void setConnectionTimeout(final int connectionTimeout)
	{
		this.connectionTimeout = connectionTimeout;
	}


	/**
	 * Sets the auth cache.
	 *
	 * @param authCache
	 *           the new auth cache
	 */
    public void setAuthCache(final AuthCache authCache)
	{
		this.authCache = authCache;
	}



	/**
	 * Sets the basic auth scheme.
	 *
	 * @param basicAuthScheme
	 *           the new basic auth scheme
	 */
    public void setBasicAuthScheme(final BasicScheme basicAuthScheme)
	{
		this.basicAuthScheme = basicAuthScheme;
	}



	/**
	 * Sets the local httpcontext.
	 *
	 * @param localHttpcontext
	 *           the new local httpcontext
	 */
    public void setLocalHttpcontext(final BasicHttpContext localHttpcontext)
	{
		this.localHttpcontext = localHttpcontext;
	}



}
