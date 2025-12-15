package com.sabmiller.sfmc.strategy;

import de.hybris.platform.webservices.log.data.WebServiceLogData;

import java.util.Date;

import com.exacttarget.fuelsdk.ETRestConnection;
import com.sabmiller.sfmc.exception.SFMCClientException;
import com.sabmiller.sfmc.exception.SFMCEmptySubscribersException;
import com.sabmiller.sfmc.exception.SFMCRequestKeyNotFoundException;
import com.sabmiller.sfmc.exception.SFMCRequestPayloadException;
import com.sabmiller.sfmc.pojo.SFMCRequest;

/**
 * Global SFMC Strategy
 */
public interface SABMGlobalSFMCStrategy {

    /**
     * MEthod to send request to SFMC
     * @param request
     * @return
     * @throws SFMCClientException
     * @throws SFMCRequestPayloadException
     * @throws SFMCRequestKeyNotFoundException
     * @throws SFMCEmptySubscribersException
     */
    public Boolean send(SFMCRequest request)
            throws SFMCClientException, SFMCRequestPayloadException, SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException;

    /**
     * Method to create URL GLobal SFMC Services
     * @param key
     * @param batch
     * @return
     */
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
    public void createLogObject(String requestPayload,ETRestConnection.Response response,String url,Date requestedDate,String userEmail);

    /**
     * Method to send request to SFMC
     * @param jsonPayload
     * @param url
     * @param initatorEmail
     * @return
     * @throws SFMCClientException
     */
    public Boolean sendRequest(String jsonPayload, String url, String initatorEmail) throws SFMCClientException;
}
