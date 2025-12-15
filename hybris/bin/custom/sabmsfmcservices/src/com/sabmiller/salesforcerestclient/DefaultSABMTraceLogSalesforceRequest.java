package com.sabmiller.salesforcerestclient;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservices.log.data.WebServiceLogData;

import jakarta.annotation.Resource;

import java.util.Date;
import java.util.Map;

import com.exacttarget.fuelsdk.ETClient;
import com.exacttarget.fuelsdk.ETRestConnection;
import com.exacttarget.fuelsdk.ETSdkException;
import com.sabmiller.integration.model.WebServiceLogModel;
import com.sabmiller.sfmc.client.SabmSFMCClient;
import com.sabmiller.sfmc.exception.SFMCClientException;
import com.sabmiller.sfmc.exception.SFMCEmptySubscribersException;
import com.sabmiller.sfmc.exception.SFMCRequestKeyNotFoundException;
import com.sabmiller.sfmc.exception.SFMCRequestPayloadException;
import com.sabmiller.sfmc.pojo.SFMCRequest;
import com.sabmiller.sfmc.strategy.SABMGlobalSFMCStrategy;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


/**
 * Default Strategy Implementation for Email and SMS common functionality.
 */
public class DefaultSABMTraceLogSalesforceRequest implements SABMTraceLogSalesforceRequest {

    @Resource
    private ModelService modelService;

   
    @Override
    public String createUrl(final String key, final boolean batch) {
        return null;
    }

    @Override
    public void logRequestAndResponse(WebServiceLogData webServiceLogData) {
        WebServiceLogModel webServiceLogModel = this.modelService.create(WebServiceLogModel.class);
        webServiceLogModel.setRequest(webServiceLogData.getRequest());
        webServiceLogModel.setResponse(webServiceLogData.getResponse());
        webServiceLogModel.setRequestDate(webServiceLogData.getRequestDate());
        webServiceLogModel.setResponseDate(webServiceLogData.getResponseDate());
        webServiceLogModel.setUrl(webServiceLogData.getUrl());
        webServiceLogModel.setUserId(webServiceLogData.getUserId());
        webServiceLogModel.setResponseStatus(webServiceLogData.getResponseStatus());
        modelService.save(webServiceLogModel);
    }

    @Override
     public void createLogObject(String requestPayload,ClientHttpResponse response,String url,Date requestedDate,String userEmail) throws IOException {
        
    	WebServiceLogData webServiceLogData = new WebServiceLogData();
        webServiceLogData.setRequest(requestPayload);
        webServiceLogData.setResponse(getResponseString(response));
        webServiceLogData.setUrl(url);
        webServiceLogData.setRequestDate(requestedDate);
        webServiceLogData.setResponseDate(new Date());
        webServiceLogData.setUserId(userEmail);
        webServiceLogData.setResponseStatus(response.getStatusCode() + response.getStatusText());       
        
        
        this.logRequestAndResponse(webServiceLogData);
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
		
		System.out.println("responseBody="+resStringBuilder.toString());
		
		return resStringBuilder.toString();
	}

   
}
