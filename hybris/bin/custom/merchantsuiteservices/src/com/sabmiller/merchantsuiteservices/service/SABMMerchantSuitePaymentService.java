package com.sabmiller.merchantsuiteservices.service;

import java.math.BigDecimal;

import com.MerchantSuite.api.TxnResp;
import com.MerchantSuite.api.TxnResponse;
import com.sabmiller.facades.merchant.suite.data.SABMBankDetailsData;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.merchantsuiteservices.data.MerchantSuiteResponseData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteAPIRequestInvalidException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteMissingBankDetailsException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenAPIException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenException;

public interface SABMMerchantSuitePaymentService {

    boolean isApproved(MerchantSuiteResponseData responseData) throws SABMMerchantSuitePaymentErrorException;

    public String initiateTokenRequest(SABMBankDetailsData bankDetailsData)
            throws SABMMerchantSuiteTokenException, SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException, SABMMerchantSuitePaymentErrorException;

    String initiateAuthKeyRequest(SABMCreditCardTransactionData creditCardTransactionData)
            throws SABMMerchantSuiteTokenException, SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    TxnResponse initiateCheckAuthKeyTxnRequest(String authKey)
            throws SABMMerchantSuiteTokenException, SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    TxnResp initiateInvoiceTxnRequest(BigDecimal total, String currencyIso, String paymentId, String token)
            throws SABMMerchantSuiteTokenException, SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

}
