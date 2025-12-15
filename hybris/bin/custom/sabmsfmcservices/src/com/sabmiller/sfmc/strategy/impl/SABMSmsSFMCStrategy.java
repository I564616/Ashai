package com.sabmiller.sfmc.strategy.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Resource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabmiller.sfmc.client.SabmSFMCClient;
import com.sabmiller.sfmc.data.SFMCSMSRequestData;
import com.sabmiller.sfmc.data.SFMCSmsRequestToData;
import com.sabmiller.sfmc.exception.SFMCClientException;
import com.sabmiller.sfmc.exception.SFMCEmptySubscribersException;
import com.sabmiller.sfmc.exception.SFMCRequestKeyNotFoundException;
import com.sabmiller.sfmc.exception.SFMCRequestPayloadException;
import com.sabmiller.sfmc.pojo.SFMCRequest;
import com.sabmiller.sfmc.strategy.SABMGlobalSFMCStrategy;

import com.sabmiller.salesforcerestclient.SABMSalesForceAccessTokenRequestHandler;
import com.sabmiller.salesforcerestclient.SABMSalesForceCreditEmailSMSPostHandler;
import com.sabmiller.salesforcerestclient.SABMSalesForceTmdSMSPostHandler;
import com.sabmiller.salesforcerestclient.data.SalesforceSmsRequestData;
import com.sabmiller.salesforcerestclient.data.SalesforceSmsJsonData;
import com.sabmiller.salesforcerestclient.SalesForceEmailSmsPostResponse;
import java.security.Key;
import com.sabmiller.salesforcerestclient.SFTokenRequest;
import com.sabmiller.salesforcerestclient.SFTokenResponse;
import com.sabmiller.sfmc.pojo.SFMCRequestTo;

public class SABMSmsSFMCStrategy extends SABMDefaultSFMCStrategyImpl implements SABMGlobalSFMCStrategy {

    private static final String DEFAULT_SMS_DYNAMIC_URL = "/sms/v1/messageContact/{0}/{1}";

    private static final String SMS_URL_CONFIG="sfmc.sms.dynamic.url";
    
    @Resource(name = "sabmSalesForceAccessTokenRequestHandler")
    SABMSalesForceAccessTokenRequestHandler sabmSalesForceAccessTokenRequestHandler;
    
    @Resource(name = "sabmSalesForceCreditEmailSMSPostHandler")
    SABMSalesForceCreditEmailSMSPostHandler sabmSalesForceCreditEmailSMSPostHandler;
    
    @Resource(name = "sabmSalesForceTmdSMSPostHandler")
    SABMSalesForceTmdSMSPostHandler sabmSalesForceTmdSMSPostHandler;

    @Resource
    private SabmSFMCClient sabmSFMCClient;

    @Resource
    private ConfigurationService configurationService;
    
    @Override
    public Boolean send(final SFMCRequest request) throws SFMCClientException, SFMCRequestPayloadException,
            SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException {

        Boolean sendSuccess = false;
        if(CollectionUtils.isNotEmpty(request.getToList())){
        	try {
        		
        		SFTokenRequest sfTokenRequest = new SFTokenRequest();
                String authDetails = sfTokenRequest.getSaleforceAUthoniticateInfo();                
                
                SFTokenResponse tokenResponse = sabmSalesForceAccessTokenRequestHandler.sendPostTokenRequest(authDetails, request.getInitiatorEmail());
                
                for(SFMCRequestTo to : CollectionUtils.emptyIfNull(request.getToList()))
                { 
                    SalesforceSmsJsonData salesforceSmsJsonData = new SalesforceSmsJsonData(to.getTo(),to.getPk(),to.getDynamicData(),to.getEventId());
                    SalesforceSmsRequestData SalesforceSmsRequestData = new SalesforceSmsRequestData(salesforceSmsJsonData,to.getTo(),to.getPk(),to.getDynamicData(),to.getEventId());            
                   
                	ObjectMapper mapper = new ObjectMapper();
                	String jsonInString = mapper.writeValueAsString(SalesforceSmsRequestData);  
                    
                    if(tokenResponse != null && StringUtils.isNotEmpty(tokenResponse.getAccessToken())){
                    	
                    	if(to.getEventId().contains(configurationService.getConfiguration().getString("salesforce.post.handler"))){
                    		SalesForceEmailSmsPostResponse salesForceEmailSmsPostResponse =  sabmSalesForceCreditEmailSMSPostHandler.sendPostRequest(jsonInString,tokenResponse, request.getInitiatorEmail());
                    	}else {
                    		SalesForceEmailSmsPostResponse salesForceEmailSmsPostResponse =  sabmSalesForceTmdSMSPostHandler.sendPostRequest(jsonInString,tokenResponse, request.getInitiatorEmail());
                    		sendSuccess = salesForceEmailSmsPostResponse.getSuccess();
                    	}
                    }      
                }
        		
        	}catch (JsonProcessingException e) {
                throw new SFMCRequestPayloadException(e.getMessage());
            }catch (Exception e) {
            	throw new SFMCClientException(e.getMessage());
            }
        }
        
        if(CollectionUtils.isEmpty(request.getToList()))
        {
            throw new SFMCEmptySubscribersException("There are no subscribers in the request");
        }

        return sendSuccess;
    }

    /*@Override
    public Boolean send(final SFMCRequest request) throws SFMCClientException, SFMCRequestPayloadException,
            SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException {

        Boolean sendSuccess = false;
        if (CollectionUtils.isNotEmpty(request.getToList()))
        {
            final SFMCSMSRequestData smsData = new SFMCSMSRequestData();
            final List<SFMCSmsRequestToData> toData = new ArrayList<>();
            request.getToList().stream().forEach(to -> toData.add(new SFMCSmsRequestToData(to.getTo(),to.getPk(),to.getDynamicData())));
            SFMCSMSRequestData requestData = new SFMCSMSRequestData(toData);
            String url = createUrl(request.getKey(),false);
            String jsonInString = null;
            if (StringUtils.isNotEmpty(url))
            {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    jsonInString = mapper.writeValueAsString(requestData);
                    sendSuccess=this.sendRequest(jsonInString,url,request.getInitiatorEmail());
                } catch (JsonProcessingException e) {
                    throw new SFMCRequestPayloadException(e.getMessage());
                }
                catch (SFMCClientException e) {
                    throw new SFMCClientException(e.getMessage());
                }
            }
            else
            {
                throw new SFMCRequestKeyNotFoundException("Key not found in configuration");
            }
        }
        else
        {
            throw new SFMCEmptySubscribersException("There are no subscribers in the request");
        }

        return sendSuccess;
    }*/

    @Override
    public String createUrl(final String strategy,boolean batch) {
        String key = getKeyFromStrategy(strategy);
        if (StringUtils.isNotEmpty(key))
        {
            return MessageFormat.format(this.configurationService.getConfiguration().getString(SMS_URL_CONFIG,DEFAULT_SMS_DYNAMIC_URL),new String[]{key ,(batch) ? "sendBatch" : "send"});
        }
        return null;
    }
}
