package com.sabmiller.merchantsuiteservices.strategy.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservices.log.data.WebServiceLogData;

import jakarta.annotation.Resource;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.MerchantSuite.api.APIResponse;
import com.MerchantSuite.api.AuthKeyResponse;
import com.MerchantSuite.api.Credentials;
import com.MerchantSuite.api.Sender;
import com.MerchantSuite.api.TokenAddRequest;
import com.MerchantSuite.api.TokenResp;
import com.MerchantSuite.api.TxnAuthKeyRequest;
import com.MerchantSuite.api.TxnRequest;
import com.MerchantSuite.api.TxnResp;
import com.MerchantSuite.api.TxnResponse;
import com.MerchantSuite.api.TxnResultKeyRequest;
import com.google.gson.Gson;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.integration.model.WebServiceLogModel;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTokenRequestData;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTokenTransactionData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteAPIRequestInvalidException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteMissingBankDetailsException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenAPIException;
import com.sabmiller.merchantsuiteservices.strategy.SABMGlobalMerchantSuiteStrategy;

public class SABMDefaultMerchantSuiteStrategy implements SABMGlobalMerchantSuiteStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SABMDefaultMerchantSuiteStrategy.class.getName());

    protected  static final String BASE_URL="merchant.suite.base.url";

    protected static final String WEB_HOOK_PATH_INVOICE = "merchant.suite.payment.invoice.web.hook.url";

    protected static final String WEB_HOOK_PATH_CHECKOUT = "merchant.suite.payment.checkout.web.hook.url";

    protected static final String REDIRECT_PATH_INVOICE="merchant.suite.payment.redirect.url.invoice";

    protected static final String REDIRECT_PATH_CHECKOUT="merchant.suite.payment.redirect.url.checkout";

    protected static final String DOMAIN_URL="website.sabmStore.https";

    protected static final String MERCHANT_NUMBER="merchant.suite.credentials.merchant.number";

    protected static final String API_USERNAME="merchant.suite.credentials.api.username";

    protected static final String API_PASSWORD="merchant.suite.credentials.api.password";

    @Resource
    private UserService userService;

    @Resource
    private ConfigurationService configurationService;

    @Resource
    private ModelService modelService;

    @Override
    public String getToken(final SABMMerchantSuiteTokenRequestData request)
            throws SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException, SABMMerchantSuitePaymentErrorException {
        //default behavior of gettoken method. Implement if there is any default behavior
        return StringUtils.EMPTY;
    }

    @Override
    public TokenResp sendTokenRequest(final TokenAddRequest request) throws SABMMerchantSuiteConfigurationException {
        try
        {
            request.setBaseUrl(configurationService.getConfiguration().getString(BASE_URL));
        }
        catch(NoSuchElementException e)
        {
            throw new SABMMerchantSuiteConfigurationException("Base URl configuration  not found");
        }
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Sending Token Request to Merchant Suite : " + request);
        }

        Sender s = new Sender();
        final TokenResp resp = s.submit(request);
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Token Response from Merchant Suite : " + resp);
        }
        createLogObject(request,resp,request.getBaseUrl() + "/tokens/",new Date(),request.getEmailAddress(),resp.getApiResponse().getResponseText());
        return resp;
    }

    @Override
    public TxnResp sendTokenTransactionRequest(final TxnRequest request) throws SABMMerchantSuiteConfigurationException {
        try
        {
            request.setBaseUrl(configurationService.getConfiguration().getString(BASE_URL));
        }
        catch(NoSuchElementException e)
        {
            throw new SABMMerchantSuiteConfigurationException("Base URl configuration  not found");
        }
        Sender s = new Sender();
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Sending Token Transaction Request to Merchant Suite : " + request);
        }
        TxnResp resp = s.submit(request);
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Token Transaction Response from Merchant Suite : " + resp);
        }
        if (Objects.nonNull(resp))
        {
            createLogObject(request,resp,request.getBaseUrl() + "/txns/",new Date(),request.getEmailAddress(),resp.getApiResponse().getResponseText());
        }
        return resp;
    }

    @Override
    public AuthKeyResponse sendAuthKeyRequest(final TxnAuthKeyRequest request) throws SABMMerchantSuiteConfigurationException {
        try
        {
            request.setBaseUrl(configurationService.getConfiguration().getString(BASE_URL));
        }
        catch(NoSuchElementException e)
        {
            throw new SABMMerchantSuiteConfigurationException("Base URl configuration  not found");
        }
        Sender s = new Sender();
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Sending Auth Key Request to Merchant Suite : " + request);
        }
        AuthKeyResponse resp = s.submit(request);
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Auth Key Response from Merchant Suite : " + resp);
        }
        if (Objects.nonNull(resp))
        {
            createLogObject(request,resp,request.getBaseUrl() +  "/txns/processtxnauthkey",new Date(),request.getProcessTxnData().getEmailAddress(),resp.getApiResponse().getResponseText());
        }
        return resp;
    }

    @Override
    public TxnResp sendCheckAuthKeyTransactionRequest(final TxnResultKeyRequest request) throws SABMMerchantSuiteConfigurationException {
        try
        {
            request.setBaseUrl(configurationService.getConfiguration().getString(BASE_URL));
        }
        catch(NoSuchElementException e)
        {
            throw new SABMMerchantSuiteConfigurationException("Base URl configuration  not found");
        }
        Sender s = new Sender();
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Sending Fetch Auth Key Result Request to Merchant Suite : " + request);
        }
        TxnResp resp =  s.submit(request);
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Fetch Auth Key Result Response to Merchant Suite : " + resp);
        }
        if (Objects.nonNull(resp))
        {
            createLogObject(request,resp,request.getBaseUrl() + "/txns/",new Date(),userService.getCurrentUser().getUid(),resp.getApiResponse().getResponseText());
        }
        return resp;
    }


    public boolean handleResponse(APIResponse resp) throws  SABMMerchantSuiteAPIRequestInvalidException {
        if (Objects.nonNull(resp)) {
            if (resp.getResponseCode()
                    .equals(0)) {
                return true;
            } else if (resp.getResponseCode()
                    .equals(1)) {
                throw new SABMMerchantSuiteAPIRequestInvalidException("Request is Invalid");
            }
        }
        return false;
    }

    @Override
    public String getAuthKey(final SABMCreditCardTransactionData creditCardTransactionData)
            throws SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException {
        return null;
    }

    @Override
    public TxnResp processTransaction(final SABMMerchantSuiteTokenTransactionData transactionData)
            throws
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {
        return null;
    }

    @Override
    public TxnResponse checkAuthKeyTransactionResponse(final String authKey)
            throws SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException {
        return null;
    }

    public Credentials getCredetials() throws SABMMerchantSuiteConfigurationException {
        Credentials credentials = null;
        try {
            credentials = new Credentials(configurationService.getConfiguration().getString(API_USERNAME).toString(),configurationService.getConfiguration().getString(API_PASSWORD).toString(),
                    configurationService.getConfiguration().getString(MERCHANT_NUMBER).toString());
        }
        catch(NoSuchElementException e)
        {
            throw new SABMMerchantSuiteConfigurationException("API Credentials configuration  not found");
        }
        return  credentials;
    }

    @Override
    public void logRequestAndResponse(WebServiceLogData webServiceLogData) {
        LOG.info("Creating Webservice Log for url : " + webServiceLogData.getUrl()) ;
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
    public void createLogObject(Object requestPayload, Object response, String url, Date requestedDate, String userEmail,
            String responseStatus) {
        WebServiceLogData webServiceLogData = new WebServiceLogData();
        Gson gson = new Gson();
        webServiceLogData.setRequest(gson.toJson(requestPayload));
        webServiceLogData.setResponse(gson.toJson(response));
        webServiceLogData.setUrl(url);
        webServiceLogData.setRequestDate(requestedDate);
        webServiceLogData.setResponseDate(new Date());
        webServiceLogData.setUserId(userEmail);
        webServiceLogData.setResponseStatus(responseStatus);
        this.logRequestAndResponse(webServiceLogData);
    }

}
