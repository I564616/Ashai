package com.sabmiller.merchantsuiteservices.strategy;

import de.hybris.platform.webservices.log.data.WebServiceLogData;

import java.util.Date;

import com.MerchantSuite.api.AuthKeyResponse;
import com.MerchantSuite.api.TokenAddRequest;
import com.MerchantSuite.api.TokenResp;
import com.MerchantSuite.api.TxnAuthKeyRequest;
import com.MerchantSuite.api.TxnRequest;
import com.MerchantSuite.api.TxnResp;
import com.MerchantSuite.api.TxnResponse;
import com.MerchantSuite.api.TxnResultKeyRequest;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTokenRequestData;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTokenTransactionData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteAPIRequestInvalidException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteMissingBankDetailsException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenAPIException;

public interface SABMGlobalMerchantSuiteStrategy {

    public String getToken(SABMMerchantSuiteTokenRequestData request)
            throws SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException, SABMMerchantSuitePaymentErrorException;

    public TokenResp sendTokenRequest(TokenAddRequest request) throws SABMMerchantSuiteConfigurationException;

    TxnResp sendTokenTransactionRequest(TxnRequest request) throws SABMMerchantSuiteConfigurationException;

    AuthKeyResponse sendAuthKeyRequest(TxnAuthKeyRequest request) throws SABMMerchantSuiteConfigurationException;

    TxnResp sendCheckAuthKeyTransactionRequest(TxnResultKeyRequest request) throws SABMMerchantSuiteConfigurationException;

    String getAuthKey(SABMCreditCardTransactionData creditCardTransactionData)
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    TxnResp processTransaction(SABMMerchantSuiteTokenTransactionData transactionData)
            throws SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    TxnResponse checkAuthKeyTransactionResponse(String authKey)
            throws  SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    void logRequestAndResponse(WebServiceLogData webServiceLogData);

    void createLogObject(Object requestPayload, Object response, String url, Date requestedDate, String userEmail, String responseStatus);
}
