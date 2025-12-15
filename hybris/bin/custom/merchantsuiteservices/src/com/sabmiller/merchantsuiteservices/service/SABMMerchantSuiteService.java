package com.sabmiller.merchantsuiteservices.service;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;

import com.MerchantSuite.api.TxnResp;
import com.MerchantSuite.api.TxnResponse;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTokenRequestData;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTokenTransactionData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteAPIRequestInvalidException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteMissingBankDetailsException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenAPIException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenException;

public interface SABMMerchantSuiteService {


    public String initiateTokenRequest(final SABMMerchantSuiteTokenRequestData request)
            throws SABMMerchantSuiteTokenException, SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException, SABMMerchantSuitePaymentErrorException;

    boolean isApproved(String responseCode, String action) throws SABMMerchantSuitePaymentErrorException;

    TxnResp initiateTransactionRequest(SABMMerchantSuiteTokenTransactionData sabmMerchantSuiteTokenTransactionData,
            CheckoutPaymentType checkoutPaymentType)
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    String initiateAuthKeyRequest(SABMCreditCardTransactionData requestData)
            throws SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    TxnResponse initiateCheckAuthKeyTransactionRequest(String authKey)
            throws
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;
}
