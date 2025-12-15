package com.sabmiller.salesforcerestclient;

import de.hybris.platform.webservices.log.data.WebServiceLogData;

import java.util.Date;

import com.exacttarget.fuelsdk.ETRestConnection;
import com.sabmiller.sfmc.exception.SFMCClientException;
import com.sabmiller.sfmc.exception.SFMCEmptySubscribersException;
import com.sabmiller.sfmc.exception.SFMCRequestKeyNotFoundException;
import com.sabmiller.sfmc.exception.SFMCRequestPayloadException;
import com.sabmiller.sfmc.pojo.SFMCRequest;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;

/**
 * Global SFMC Strategy
 */
public interface SABMTraceLogSalesforceRequest {

    
    public String createUrl(String key,boolean batch);

    /**
     * Method to log request and response object in Hybris for every succersful service call.
     * @param webServiceLogData
     */
    public void logRequestAndResponse(WebServiceLogData webServiceLogData);

    /**
     * MEthod to create log object.
     * @param requestPayload
     * @param response
     * @param url
     * @param requestedDate
     * @param userEmail
     */
    public void createLogObject(String requestPayload,ClientHttpResponse response,String url,Date requestedDate,String userEmail) throws IOException;

   
}
