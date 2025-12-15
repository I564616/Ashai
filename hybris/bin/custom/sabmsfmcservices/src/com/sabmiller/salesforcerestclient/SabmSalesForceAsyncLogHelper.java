/**
 *
 */
package com.sabmiller.salesforcerestclient;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservices.log.data.WebServiceLogData;

import java.io.StringWriter;

import jakarta.annotation.Resource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.xml.transform.StringSource;

import com.sabmiller.integration.model.WebServiceLogModel;


@Component
public class SabmSalesForceAsyncLogHelper
{

	private static final Logger LOG = Logger.getLogger(SabmSalesForceAsyncLogHelper.class);

	@Resource
	private ModelService modelService;

	@Resource
	private SessionService sessionService;

	public WebServiceLogModel traceRequest(final WebServiceLogData webServiceLogData)
	{
		//0 disabled, 1 errors, 2 all requests
		final int logLevel = Config.getInt("sap.service.log.requests", 0);

		if (logLevel > 0 && BooleanUtils.isTrue(webServiceLogData.getFailed()))
		{
			//error log
			return generateLog(webServiceLogData);

		}
		else if (logLevel == 2)
		{
			generateLogAsync(webServiceLogData);
		}
		return null;
	}

	@Async
	protected void generateLogAsync(final WebServiceLogData webServiceLogData)
	{
		if (Registry.getCurrentTenant() == null)
		{
			Registry.activateMasterTenant();
		}
		if (!sessionService.hasCurrentSession())
		{
			sessionService.createNewSession();
		}

		generateLog(webServiceLogData);
	}

	protected WebServiceLogModel generateLog(final WebServiceLogData webServiceLogData)
	{
		try
		{
			/*final Transformer tf = TransformerFactory.newInstance().newTransformer();

			final String requestXml = xmlFormatter(tf, webServiceLogData.getRequest());
			final String reponseXml = xmlFormatter(tf, webServiceLogData.getResponse());*/
			
			final String requestJsonAsString = webServiceLogData.getRequest();
			final String reponseJsonAsString = webServiceLogData.getResponse();

			LOG.debug("===========================Interface request begin==============================================");
			LOG.debug("User id" + webServiceLogData.getUserId());
			LOG.debug("Request uri : " + webServiceLogData.getUrl());
			LOG.debug("Service Request : " + requestJsonAsString);
			LOG.debug("==========================Interface request end==================================================");

			LOG.debug("==========================Inteface response begin================================================");
			LOG.debug("Status code-code: " + webServiceLogData.getResponseStatus());
			LOG.debug("Service Response: " + reponseJsonAsString);
			LOG.debug("==========================Interface response end=================================================");

			final WebServiceLogModel logModel = modelService.create(WebServiceLogModel.class);
			logModel.setUserId(webServiceLogData.getUserId());
			logModel.setSessionId(webServiceLogData.getSessionId());

			logModel.setUrl(StringUtils.abbreviate(webServiceLogData.getUrl(), 200));
			logModel.setRequest(requestJsonAsString);
			logModel.setResponse(reponseJsonAsString);
			logModel.setResponseStatus(webServiceLogData.getResponseStatus());
			logModel.setRequestDate(webServiceLogData.getRequestDate());
			logModel.setResponseDate(webServiceLogData.getResponseDate());
			if (webServiceLogData.getResponseDate() != null && webServiceLogData.getRequestDate() != null)
			{
				final long callTime = webServiceLogData.getResponseDate().getTime() - webServiceLogData.getRequestDate().getTime();
				logModel.setTotalTimeTaken(callTime);
			}
			try
			{
				modelService.save(logModel);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
				System.out.println("model saving" + e.getMessage());
			}
			return logModel;
		}
		catch (final Exception e)
		{
			LOG.error("error while saving log in integrations module", e);
		}

		return null;
	}


	private String xmlFormatter(final Transformer tf, final String input)
	{

		String formattedString = input;
		if (StringUtils.length(input) > 10)
		{

			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			final StringWriter stringWriter = new StringWriter();
			final StreamResult streamResult = new StreamResult(stringWriter);
			try
			{
				tf.transform(new StringSource(input), streamResult);
			}
			catch (final TransformerException e)
			{
				LOG.error("Error while formatting xml in logger");
				return input;
			}

			formattedString = stringWriter.toString();
		}
		return formattedString;
	}
}
