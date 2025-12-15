package com.sabmiller.sfmc.strategy.impl;

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

/**
 * Default Strategy Implementation for Email and SMS common functionality.
 */
public class SABMDefaultSFMCStrategyImpl implements SABMGlobalSFMCStrategy {

    @Resource
    private ModelService modelService;

    @Resource
    protected SabmSFMCClient sabmSFMCClient;

    public  Map strategyToKeyMap;


    @Override
    public Boolean send(final SFMCRequest request) throws SFMCClientException, SFMCRequestPayloadException,
            SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException {
        return true;
    }

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
     public void createLogObject(String requestPayload,ETRestConnection.Response response,String url,Date requestedDate,String userEmail) {
        WebServiceLogData webServiceLogData = new WebServiceLogData();
        webServiceLogData.setRequest(requestPayload);
        webServiceLogData.setResponse(response.getResponsePayload());
        webServiceLogData.setUrl(url);
        webServiceLogData.setRequestDate(requestedDate);
        webServiceLogData.setResponseDate(new Date());
        webServiceLogData.setUserId(userEmail);
        webServiceLogData.setResponseStatus(response.getResponseMessage());
        this.logRequestAndResponse(webServiceLogData);
    }

    @Override
    public Boolean sendRequest(String jsonPayload ,String url,String initiatorEmail) throws SFMCClientException {
        Boolean returnFlag = false;
        try {
            Date requestTime = new Date();
            ETClient etclient= sabmSFMCClient.getETClient();
            ETRestConnection restConnection = etclient.getRestConnection();
            ETRestConnection.Response response = restConnection.post(url,jsonPayload);
            if (response.getResponseMessage().equals("Accepted"))
            {
                returnFlag = Boolean.TRUE;
            }
            this.createLogObject(jsonPayload,response,url,requestTime,initiatorEmail);
        } catch (ETSdkException e) {
            throw new SFMCClientException(e.getMessage());
        }
        return returnFlag;
    }

    public String getKeyFromStrategy(Object strategy)
    {
        if (!this.strategyToKeyMap.containsKey(strategy))
        {
            return null;
        }
        return (String)strategyToKeyMap.get(strategy);
    }

    public void setStrategyToKeyMap(Map strategyToKeyMap) {
        this.strategyToKeyMap = strategyToKeyMap;
    }

    public Map getStrategyToKeyMap() {
        return strategyToKeyMap;
    }
}
