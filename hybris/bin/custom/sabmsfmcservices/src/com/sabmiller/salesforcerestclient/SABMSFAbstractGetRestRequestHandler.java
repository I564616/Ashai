/**
 *
 */
package com.sabmiller.salesforcerestclient;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


/**
 * The Class SABMAbstractGetRestRequestHandler.
 *
 * @param <WebServiceResponse>
 *           the generic type
 */
public class SABMSFAbstractGetRestRequestHandler<WebServiceResponse>
		implements SABMSFGetRestRequestHandler<WebServiceResponse>
{

	/**
	 * The sabm rest template.
	 */
	private RestTemplate sabmRestTemplate;

	/**
	 * The media types.
	 */
	private List<MediaType> mediaTypes;

	/**
	 * The content type.
	 */
	private MediaType contentType;

	/**
	 * The request method.
	 */
	private HttpMethod requestMethod;

	/**
	 * The response class.
	 */
	private Class<WebServiceResponse> responseClass;

	private String plainCredentials;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	/**
	 * The Constant LOG.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(SABMSFAbstractGetRestRequestHandler.class);

	/**
	 * Gets the response class.
	 *
	 * @return the response class
	 */
	public Class<WebServiceResponse> getResponseClass()
	{
		return responseClass;
	}

	/**
	 * Sets the response class.
	 *
	 * @param responseClass
	 *           the responseClass to set
	 */
	public void setResponseClass(final Class<WebServiceResponse> responseClass)
	{
		this.responseClass = responseClass;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.restclient.commons.SABMGetRestRequestHandler#sendGetRequest(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public WebServiceResponse sendGetRequest(final String url, final Map<String, Object> urlVariables)
			throws SABMSFIntegrationException
	{
		WebServiceResponse result = null;

		try
		{
			result = getSabmRestTemplate().getForObject(url, getResponseClass(), urlVariables);
		}
		catch (final RestClientException ex)
		{
			LOG.error("Rest get call exception", ex);
			throw new SABMSFIntegrationException("web service exception", ex);
		}
		return result;
	}

	/**
	 * Gets the http headers.
	 *
	 * @return the http headers
	 */
	protected HttpHeaders getHttpHeaders(String userId)
	{

		if (mediaTypes == null || mediaTypes.isEmpty())
		{
			mediaTypes = Arrays.asList(MediaType.APPLICATION_JSON);
		}

		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(mediaTypes);
		if (getContentType() != null)
		{
			headers.setContentType(getContentType());
		}

		/*if (plainCredentials != null)
		{

			headers.add("Authorization", "Basic " + getAuthorization(plainCredentials));
		}*/
		
		headers.setConnection("close");
		
		if(userId != null){
	        List<String> user = new ArrayList<String>();
	        user.add(userId);
	        headers.put(configurationService.getConfiguration().getString("salesforce.post.key.parameter"), user);
		}
		
		List<String> tokenRequest = new ArrayList<String>();
		tokenRequest.add("Salesforce Access Token Request");	        
        headers.put(configurationService.getConfiguration().getString("salesforce.post.accesstoken.key.parameter"), tokenRequest);
        
		return headers;
	}
	
	protected HttpHeaders getHttpHeadersForEmailSMSPost(String accessToken, String userId)
	{

		if (mediaTypes == null || mediaTypes.isEmpty())
		{
			mediaTypes = Arrays.asList(MediaType.APPLICATION_JSON);
		}

		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(mediaTypes);
		if (getContentType() != null)
		{
			headers.setContentType(getContentType());
		}
		
		headers.setConnection("close");

		if (accessToken != null)
		{

			headers.add("Authorization", "Bearer " + accessToken);
		}
		
		if(userId != null){
	        List<String> user = new ArrayList<String>();
	        user.add(userId);
	        headers.put(configurationService.getConfiguration().getString("salesforce.post.key.parameter"), user);
		}
		
		return headers;
	}
	
	

	/**
	 * Gets the media types.
	 *
	 * @return the media types
	 */
	public List<MediaType> getMediaTypes()
	{
		return mediaTypes;
	}

	/**
	 * Sets the media types.
	 *
	 * @param mediaTypes
	 *           the new media types
	 */
	public void setMediaTypes(final List<MediaType> mediaTypes)
	{
		this.mediaTypes = mediaTypes;
	}

	/**
	 * Gets the sabm rest template.
	 *
	 * @return the sabmRestTemplate
	 */
	public RestTemplate getSabmRestTemplate()
	{
		return sabmRestTemplate;
	}

	/**
	 * Sets the sabm rest template.
	 *
	 * @param sabmRestTemplate
	 *           the sabmRestTemplate to set
	 */
	public void setSabmRestTemplate(final RestTemplate sabmRestTemplate)
	{
		this.sabmRestTemplate = sabmRestTemplate;
	}

	/**
	 * Gets the request method.
	 *
	 * @return the request method
	 */
	public HttpMethod getRequestMethod()
	{
		return requestMethod;
	}

	/**
	 * Sets the request method.
	 *
	 * @param requestMethod
	 *           the requestMethod to set
	 */
	public void setRequestMethod(final HttpMethod requestMethod)
	{
		this.requestMethod = requestMethod;
	}

	/**
	 * Gets the content type.
	 *
	 * @return the contentType
	 */
	public MediaType getContentType()
	{
		return contentType;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType
	 *           the contentType to set
	 */
	public void setContentType(final MediaType contentType)
	{
		this.contentType = contentType;
	}

	public String getAuthorization(final String plainCredentials)
	{

		final byte[] plainCredsBytes = plainCredentials.getBytes();

		final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);

		return new String(base64CredsBytes);
	}

	/**
	 * @param plainCredentials
	 *           the plainCredentials to set
	 */
	public void setPlainCredentials(final String plainCredentials)
	{
		this.plainCredentials = plainCredentials;
	}



	public boolean getStubWebServiceEnabled()
	{
		return BooleanUtils.toBoolean(configurationService.getConfiguration().getString("sap.webservice.stub"));
	}



}
