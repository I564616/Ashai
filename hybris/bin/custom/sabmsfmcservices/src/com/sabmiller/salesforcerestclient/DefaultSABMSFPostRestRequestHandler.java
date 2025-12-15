/**
 *
 */
package com.sabmiller.salesforcerestclient;

import java.util.Map;

import org.joda.time.DateTime;
import java.util.Date;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;

import java.security.Key;
import java.security.interfaces.RSAPublicKey;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;

import jakarta.annotation.Resource;
import jakarta.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.core.Registry;

import java.text.MessageFormat;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import com.sabmiller.salesforcerestclient.SFTokenResponse;




/**
 * The Class DefaultSABMPostRestRequestHandler.
 *
 * @param <WebServiceRequest>
 *           the generic type
 * @param <WebServiceResponse>
 *           the generic type
 */
public class DefaultSABMSFPostRestRequestHandler<WebServiceRequest, WebServiceResponse>
		extends SABMSFAbstractGetRestRequestHandler<WebServiceResponse>
		implements SABMSFPostRestRequestHandler<WebServiceResponse, WebServiceRequest>
{

	/** The Constant LOG. */
	private final static Logger LOG = LoggerFactory.getLogger(DefaultSABMSFPostRestRequestHandler.class);
	
	private final static String DEFAULT_JWT_AUDIENCE = "https://{0}.salesforce.com";

    private final static String JWT_AUDIENCE = "salesforce.jwt.audience";
    
    private final static String DEFAULT_ACCESS_TOKEN_URL = "https://{0}.salesforce.com/services/oauth2/token";

    private final static String ACCESS_TOKEN_URL = "salesforce.post.url";
    
    private String requestUrl;
	private String requestTokenUrl;
	private String requestSalesForcePostUrl;
	private Map<String, String> requestTokemParams;
	
	@Resource(name = "configurationService")
    private ConfigurationService configurationService;
	
	

	/**
	 * Send post request.
	 *
	 * @param request
	 *           the request
	 * @return the web service response
	 * @throws SABMSFIntegrationException
	 *            the SABM integration exception
	 */
	@Override
	public WebServiceResponse sendPostTokenRequest(final String request, final String userId) throws SABMSFIntegrationException
	{
		final HttpEntity<String> requestEntity;

		requestEntity = new HttpEntity<>(request, getHttpHeaders(userId));
		ResponseEntity<WebServiceResponse> result = null;
		try
		{
			if (StringUtils.isNotEmpty(getTokenRequestUrl()))
			{			

				result = getSabmRestTemplate().exchange(getTokenRequestUrl(), getRequestMethod(), requestEntity, getResponseClass());								
				
			}
			else
			{
				throw new SABMSFIntegrationException("URL cannot be null");
			}

		}		
		catch (final RestClientException ex)
		{
			LOG.error("Rest post call exception: " + getTokenRequestUrl() + ":" + ex.getMessage());
			throw new SABMSFIntegrationException("web service exception", ex);
		}
		/*catch (final IOException e)
		{
			LOG.error("Error while calling rest service", e);
			
		}*/

		return result.getBody();
	}
	
	@Override
	public WebServiceResponse sendPostRequest(final String request, final SFTokenResponse tokenResponse, final String userId) throws SABMSFIntegrationException
	{
		final HttpEntity<String> requestEntity;

		requestEntity = new HttpEntity<>(request, getHttpHeadersForEmailSMSPost(tokenResponse.getAccessToken(), userId));
		ResponseEntity<WebServiceResponse> result = null;
		try
		{
			if (StringUtils.isNotEmpty(tokenResponse.getInstanceUrl()))
			{
				String url = tokenResponse.getInstanceUrl() + getSaleForcePostUrl();
				
				result = getSabmRestTemplate().exchange(url, getRequestMethod(), requestEntity, getResponseClass());
			}
			else
			{
				throw new SABMSFIntegrationException("URL cannot be null");
			}

		}
		catch (final RestClientException ex)
		{
			LOG.error("Rest post call exception: " + tokenResponse.getInstanceUrl() + getSaleForcePostUrl() + ":" + ex.getMessage());
			throw new SABMSFIntegrationException("web service exception", ex);
		}

		return result.getBody();
	}

	/**
	 * Gets the request url.
	 *
	 * @return the request url
	 */


	protected String getTokenRequestUrl()
	{
		String envType = configurationService.getConfiguration().getString("envType", "prod");
		String url = MessageFormat.format(this.configurationService.getConfiguration().getString(ACCESS_TOKEN_URL,DEFAULT_ACCESS_TOKEN_URL),new String[]{(envType.equals("prod")) ? "login" : "test"});
		this.requestTokenUrl = url;
		return this.requestTokenUrl;
	}
	
	protected String getSaleForcePostUrl()
	{
		return requestSalesForcePostUrl;
	}

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
