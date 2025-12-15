/**
 *
 */
package com.sabmiller.integration.restclient.commons;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservices.log.data.WebServiceLogData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.sabmiller.integration.model.WebServiceLogModel;


/**
 * The Class SABMRestCallLogger.
 */

public class SABMRestClientHttpRequestInterceptor implements ClientHttpRequestInterceptor
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(SABMRestClientHttpRequestInterceptor.class);

	@Autowired
	private AsyncLogHelper asyncHelper;

	@Autowired
	private UserService userService;

	@Autowired
	private SessionService sessionService;


	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] reqBody,
			final ClientHttpRequestExecution execution) throws IOException
	{
		final String sessionAttrUserId = Config.getString("session.attr.user.invoking.sap.service", "CURRENT_USER_SAP_INVOCATION");



		ClientHttpResponse response = null;

		final String userId = sessionService.getAttribute(sessionAttrUserId);

		final WebServiceLogData webServiceLogData = new WebServiceLogData();
		webServiceLogData.setUserId(userId != null ? userId : userService.getCurrentUser().getUid());
		System.out.println("content type:" + request.getHeaders().getContentType() + "url:" + request.getURI().toString());

		webServiceLogData.setUrl(request.getURI().toString());
		webServiceLogData.setRequest(reqBody != null ? new String(reqBody, "UTF-8") : "");
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
		catch (final IOException e)
		{
			LOG.error("Error while calling rest service", e);
			webServiceLogData.setResponseDate(new Date());
			webServiceLogData.setFailed(true);

			final WebServiceLogModel log = asyncHelper.traceRequest(webServiceLogData);
			throw new SabmRestIoException(e, log);
		}

		asyncHelper.traceRequest(webServiceLogData);
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
