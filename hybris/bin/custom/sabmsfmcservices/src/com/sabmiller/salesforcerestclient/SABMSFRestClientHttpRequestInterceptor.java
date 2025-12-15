/**
 *
 */
package com.sabmiller.salesforcerestclient;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservices.log.data.WebServiceLogData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.exacttarget.fuelsdk.annotations.RestObject;
import com.sabmiller.integration.model.WebServiceLogModel;
import com.sabmiller.integration.restclient.commons.AsyncLogHelper;
import jakarta.annotation.Resource;


/**
 * The Class SABMRestCallLogger.
 */

public class SABMSFRestClientHttpRequestInterceptor implements ClientHttpRequestInterceptor
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(SABMSFRestClientHttpRequestInterceptor.class);
	
	@Resource(name = "sabmSalesForceAsyncLogHelper")	
	private SabmSalesForceAsyncLogHelper sabmSalesForceAsyncLogHelper;

	@Autowired
	private UserService userService;

	@Autowired
	private SessionService sessionService;


	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] reqBody,
			final ClientHttpRequestExecution execution) throws IOException
	{
		//final String sessionAttrUserId = Config.getString("session.attr.user.invoking.sap.service", "CURRENT_USER_SAP_INVOCATION");
		
		String keyParam = Config.getString("salesforce.post.key.parameter", "");
		String userId = null;
		if (request.getHeaders().containsKey(keyParam))
		{
			userId = request.getHeaders().get(keyParam).get(0);			
			request.getHeaders().remove(keyParam);
		}
		
		String accessTokenkeyParam = Config.getString("salesforce.post.accesstoken.key.parameter", "");
		String requestForAccessToken=null;
		
		if (request.getHeaders().containsKey(accessTokenkeyParam))
		{
			requestForAccessToken = request.getHeaders().get(accessTokenkeyParam).get(0);			
			request.getHeaders().remove(accessTokenkeyParam);
		}
		
		ClientHttpResponse response = null;

		//final String userId = sessionService.getAttribute(sessionAttrUserId);

		final WebServiceLogData webServiceLogData = new WebServiceLogData();
		//webServiceLogData.setUserId(userId != null ? userId : userService.getCurrentUser().getUid());
		webServiceLogData.setUserId(userId != null ? userId : "admin");
		
		System.out.println("content type:" + request.getHeaders().getContentType() + "url:" + request.getURI().toString());

		webServiceLogData.setUrl(request.getURI().toString());
		if(requestForAccessToken != null){
			webServiceLogData.setRequest(requestForAccessToken);
		}else{
			webServiceLogData.setRequest(reqBody != null ? new String(reqBody, "UTF-8") : "");
		}
		webServiceLogData.setRequestDate(new Date());
		webServiceLogData.setSessionId(sessionService.getCurrentSession() != null
				? sessionService.getCurrentSession().getSessionId() : "No current session");

		try
		{
			response = execution.execute(request, reqBody);
			webServiceLogData.setResponseStatus(response.getStatusCode() + response.getStatusText());
			webServiceLogData.setResponse(getResponseString(response));
			webServiceLogData.setResponseDate(new Date());
		}
		catch (final Exception e)
		{
			LOG.error("Error while calling rest service", e);
			webServiceLogData.setResponseDate(new Date());
			webServiceLogData.setFailed(true);

			final WebServiceLogModel log = sabmSalesForceAsyncLogHelper.traceRequest(webServiceLogData);
			throw new SabmSFRestIoException(e, log);
		}

		sabmSalesForceAsyncLogHelper.traceRequest(webServiceLogData);
		return response;
	}


	/**
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private String getResponseString(final ClientHttpResponse response) throws IOException
	{
		final StringBuilder resStringBuilder = new StringBuilder();
		final InputStreamReader stream = new InputStreamReader(response.getBody(), "UTF-8");
		final BufferedReader bufferedReader = new BufferedReader(stream);
		try
		{
			String line = bufferedReader.readLine();
			while (line != null)
			{
				resStringBuilder.append(line);
				resStringBuilder.append('\n');
				line = bufferedReader.readLine();
			}
		}
		finally
		{
			bufferedReader.close();
			stream.close();
		}
		return resStringBuilder.toString();
	}
}
