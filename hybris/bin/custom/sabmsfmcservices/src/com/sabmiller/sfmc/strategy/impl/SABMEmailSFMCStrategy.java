package com.sabmiller.sfmc.strategy.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Resource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabmiller.sfmc.data.SFMCEmailRequestData;
import com.sabmiller.sfmc.data.SFMCEmailRequestToData;
import com.sabmiller.sfmc.exception.SFMCClientException;
import com.sabmiller.sfmc.exception.SFMCEmptySubscribersException;
import com.sabmiller.sfmc.exception.SFMCRequestKeyNotFoundException;
import com.sabmiller.sfmc.exception.SFMCRequestPayloadException;
import com.sabmiller.sfmc.pojo.SFMCRequest;
import com.sabmiller.sfmc.strategy.SABMGlobalSFMCStrategy;
import com.sabmiller.sfmc.enums.SFMCRequestEmailTemplate;

import com.sabmiller.salesforcerestclient.SABMSalesForceAccessTokenRequestHandler;
import com.sabmiller.salesforcerestclient.SABMSalesForceCreditEmailSMSPostHandler;
import com.sabmiller.salesforcerestclient.SABMSalesForceTmdSMSPostHandler;
import com.sabmiller.salesforcerestclient.SFTokenRequest;
import com.sabmiller.salesforcerestclient.SFTokenResponse;
import com.sabmiller.salesforcerestclient.data.SalesforceEmailRequestData;
import com.sabmiller.salesforcerestclient.data.SalesforceEmailJsonData;
import com.sabmiller.salesforcerestclient.SalesForceEmailSmsPostResponse;
import java.security.Key;
import com.sabmiller.sfmc.pojo.SFMCRequestTo;

public class SABMEmailSFMCStrategy extends SABMDefaultSFMCStrategyImpl implements SABMGlobalSFMCStrategy{

    @Resource
    private ConfigurationService configurationService;
    
    @Resource(name = "sabmSalesForceAccessTokenRequestHandler")
    SABMSalesForceAccessTokenRequestHandler sabmSalesForceAccessTokenRequestHandler;
    
    @Resource(name = "sabmSalesForceCreditEmailSMSPostHandler")
    SABMSalesForceCreditEmailSMSPostHandler sabmSalesForceCreditEmailSMSPostHandler;
    
    @Resource(name = "sabmSalesForceTmdSMSPostHandler")
    SABMSalesForceTmdSMSPostHandler sabmSalesForceTmdSMSPostHandler;

    @Autowired
    @Qualifier("salesforceEventMap")
    public Map<String, String> salesforceEventMap;


    private static final String DEFAULT_EMAIL_DYNAMIC_URL = "/messaging/v1/messageDefinitionSends/key:{0}/{1}";

    private static final String EMAIL_URL_CONFIG="sfmc.email.dynamic.url";
    
    @Override
    public Boolean send(final SFMCRequest request) throws SFMCClientException, SFMCRequestPayloadException,
            SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException {
        Boolean sendSuccess = false;
        if(CollectionUtils.isNotEmpty(request.getToList())){
        	try {
        		
        		SFTokenRequest sfTokenRequest = new SFTokenRequest();
                String authDetails = null;

                Set<String> matchingKeys = Set.of(
                        salesforceEventMap.get(SFMCRequestEmailTemplate.CREDITRECIEVEDEMAIL.getCode()),
                        salesforceEventMap.get(SFMCRequestEmailTemplate.CREDITAPPROVEDEMAIL.getCode())
                );

                if (matchingKeys.contains(request.getKey())) {
                    // Logic for Handling token URLs for above mentioned email templates
                    authDetails = sfTokenRequest.getOldSalesforceAuthenticateInfo();
                } else {
                    // Logic for other types of email templates
                    authDetails = sfTokenRequest.getSaleforceAUthoniticateInfo();
                }

                SFTokenResponse tokenResponse = sabmSalesForceAccessTokenRequestHandler.sendPostTokenRequest(authDetails, request.getInitiatorEmail());
                
                for(SFMCRequestTo to : CollectionUtils.emptyIfNull(request.getToList()))
                {  
                    SalesforceEmailJsonData salesforceEmailJsonData = new SalesforceEmailJsonData(to.getTo(),to.getPk(),to.getDynamicData(),to.getEventId());
                    SalesforceEmailRequestData salesforceEmailRequestData = new SalesforceEmailRequestData(salesforceEmailJsonData,to.getTo(),to.getPk(),to.getDynamicData(),to.getEventId());
                             
                	ObjectMapper mapper = new ObjectMapper();
                    String jsonInString = mapper.writeValueAsString(salesforceEmailRequestData); 
                    
                    if(tokenResponse != null && StringUtils.isNotEmpty(tokenResponse.getAccessToken())){
                    	
                    	if(to.getEventId().contains(configurationService.getConfiguration().getString("salesforce.post.handler"))){
                    		SalesForceEmailSmsPostResponse salesForceEmailSmsPostResponse =  sabmSalesForceCreditEmailSMSPostHandler.sendPostRequest(jsonInString,tokenResponse, request.getInitiatorEmail());
                    	}else {
                    		SalesForceEmailSmsPostResponse salesForceEmailSmsPostResponse =  sabmSalesForceTmdSMSPostHandler.sendPostRequest(jsonInString,tokenResponse, request.getInitiatorEmail());
                    		sendSuccess = salesForceEmailSmsPostResponse.getSuccess();
                    	}
                    }
                }
                
        	} catch (JsonProcessingException e) {
                throw new SFMCRequestPayloadException(e.getMessage());
            }                
            catch (Exception e) {
            	throw new SFMCClientException(e.getMessage());
            }
        }
        
        if(CollectionUtils.isEmpty(request.getToList()))
        {
            throw new SFMCEmptySubscribersException("There are no subscribers in the request");
        }
        return sendSuccess;
    }

   /* @Override
    public Boolean send(final SFMCRequest request) throws SFMCClientException, SFMCRequestPayloadException,
            SFMCRequestKeyNotFoundException, SFMCEmptySubscribersException {
        Boolean sendSuccess = false;
        if (CollectionUtils.isNotEmpty(request.getToList()))
        {
            final List<SFMCEmailRequestData> requestList = new ArrayList<>();
            final boolean isBatch = request.getToList().size() > 1 ? true : false;
            request.getToList().stream().forEach(to -> requestList.add(new SFMCEmailRequestData(new SFMCEmailRequestToData(to.getTo(),to.getPk(),to.getDynamicData()))));
            String url = createUrl(request.getKey(),isBatch);
            String jsonInString = null;
            if (StringUtils.isNotEmpty(url))
            {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    if (isBatch)
                    {
                        jsonInString = mapper.writeValueAsString(requestList);
                    }
                    else {
                        jsonInString = mapper.writeValueAsString(requestList.iterator().next());
                    }
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
        if (StringUtils.isNotEmpty(key)) {
            return MessageFormat.format(this.configurationService.getConfiguration().getString(EMAIL_URL_CONFIG, DEFAULT_EMAIL_DYNAMIC_URL),
                    new String[] { key, (batch) ? "sendBatch" : "send" });
        }
        return null;
    }
}
