package com.sabmiller.merchantsuiteservices.strategy.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.annotation.Resource;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.MerchantSuite.api.AuthKeyResponse;
import com.MerchantSuite.api.ProcessTxnData;
import com.MerchantSuite.api.TokenisationMode;
import com.MerchantSuite.api.TxnAuthKeyRequest;
import com.MerchantSuite.api.TxnResp;
import com.MerchantSuite.api.TxnResponse;
import com.MerchantSuite.api.TxnResultKeyRequest;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteAPIRequestInvalidException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.strategy.SABMGlobalMerchantSuiteStrategy;

public class SABMCreditCardMerchantSuiteStrategy extends SABMDefaultMerchantSuiteStrategy implements
        SABMGlobalMerchantSuiteStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SABMCreditCardMerchantSuiteStrategy.class.getName());

    @Resource
    ConfigurationService configurationService;

    @Resource
    UserService userService;

    @Override
    public String getAuthKey(final SABMCreditCardTransactionData request)
            throws  SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException {
        TxnAuthKeyRequest req = new TxnAuthKeyRequest(getCredetials());
        ProcessTxnData tx = new ProcessTxnData();
        AuthKeyResponse response;
        tx.setAmount(request.getTotalAmount());
        tx.setAmountOriginal(request.getAmount());
        tx.setAmountSurcharge(request.getSurcharge());
        tx.setReference1(request.getPaymentReference());
        tx.setReference2(request.getCartCode());
        tx.setReference3(request.getAccountNumber());
        tx.setAction("payment");
        tx.setEmailAddress(userService.getCurrentUser().getUid());
        tx.setCurrency(request.getCurrency());
        tx.setTestMode(false);
        tx.setTokenisationMode(TokenisationMode.DO_NOT_TOKENISE);
        tx.setAmexExpressCheckout(false);
        req.setProcessTxnData(tx);
        if (request.getInvoicePayment())
        {
            req.setWebHookUrl(createCompleteURL(DOMAIN_URL,WEB_HOOK_PATH_INVOICE));
            req.setRedirectionUrl(createCompleteURL(DOMAIN_URL,REDIRECT_PATH_INVOICE));
        }
        else if (request.getCartPayment())
        {
            req.setWebHookUrl(createCompleteURL(DOMAIN_URL,WEB_HOOK_PATH_CHECKOUT));
            req.setRedirectionUrl(createCompleteURL(DOMAIN_URL,REDIRECT_PATH_CHECKOUT));
        }
        response = sendAuthKeyRequest(req);
        if (Objects.nonNull(response) && handleResponse(response.getApiResponse()))
        {
            return response.getAuthKey();
        }
        else {
            throw new SABMMerchantSuiteAPIRequestInvalidException("No response found for request");
        }
    }

    private String createCompleteURL(final String domainConfig , final String pathConfig) throws SABMMerchantSuiteConfigurationException {
        String domain;
        String path;
        try {
            domain = configurationService.getConfiguration().getString(domainConfig);
            path = configurationService.getConfiguration().getString(pathConfig);
        }

        catch(NoSuchElementException e ) {
            throw new SABMMerchantSuiteConfigurationException("WEBHOOK Credentials configuration  not found");
        }
        LOG.info("URL is : " + domain.concat(path));
        return domain.concat(path);
    }


    @Override
    public TxnResponse checkAuthKeyTransactionResponse(final String authKey)
            throws SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException {
        TxnResultKeyRequest req = new TxnResultKeyRequest(getCredetials(),authKey);
        TxnResp response = sendCheckAuthKeyTransactionRequest(req);
        if (Objects.nonNull(response) && handleResponse(response.getApiResponse()))
        {
            return response.getTxnResponse();
        }
        else {
            throw new SABMMerchantSuiteAPIRequestInvalidException("No response found for request");
        }
    }
}
