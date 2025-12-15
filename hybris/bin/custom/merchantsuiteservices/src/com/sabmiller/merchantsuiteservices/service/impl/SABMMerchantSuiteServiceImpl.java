package com.sabmiller.merchantsuiteservices.service.impl;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mockito.internal.util.collections.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.sabmiller.merchantsuiteservices.service.SABMMerchantSuiteService;
import com.sabmiller.merchantsuiteservices.strategy.SABMGlobalMerchantSuiteStrategy;

public class SABMMerchantSuiteServiceImpl implements SABMMerchantSuiteService
{

    public Map<CheckoutPaymentType,SABMGlobalMerchantSuiteStrategy> strategyMap;

    private static final Logger LOG = LoggerFactory.getLogger(SABMMerchantSuiteServiceImpl.class.getName());

    public static final String ERROR = "Error";
    public static final String TOKEN = "token";

    public static final String APPROVED_CODE = "0";
    public static final String DECLINED_CODE = "1";

    protected static final Set<String> ERROR_CODE = Sets.newSet("2", "3");

    @Override
    public String initiateTokenRequest(final SABMMerchantSuiteTokenRequestData request)
            throws SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException, SABMMerchantSuitePaymentErrorException {
        if (Objects.nonNull(request.getBankAccountDetails()))
        {
            return this.strategyMap.get(CheckoutPaymentType.ACCOUNT).getToken(request);
        }
        else if (Objects.nonNull(request.getCreditCardDetails()))
        {
            return this.strategyMap.get(CheckoutPaymentType.CARD).getToken(request);
        }
        return null;
    }

    @Override
    public boolean isApproved(final String responseCode, final String action) throws SABMMerchantSuitePaymentErrorException {
        if (StringUtils.isNotBlank(action) && (ERROR.equals(action) || "Reject".equals(action)))
        {
            return false;
        }
        else if (APPROVED_CODE.equals(responseCode))
        {
            return true;
        }
        else if (DECLINED_CODE.equals(responseCode))
        {
            return false;
        }
        else if (ERROR_CODE.contains(responseCode))
        {
            throw new SABMMerchantSuitePaymentErrorException("Error, contact merchant suite");
        }
        //unknown error
        throw new SABMMerchantSuitePaymentErrorException("Unknown/unMapped error occurred");
    }

    @Override
    public TxnResp initiateTransactionRequest(SABMMerchantSuiteTokenTransactionData sabmMerchantSuiteTokenTransactionData,
            CheckoutPaymentType checkoutPaymentType)
            throws  SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {
        return this.strategyMap.get(checkoutPaymentType).processTransaction(sabmMerchantSuiteTokenTransactionData);
    }

    @Override
    public String initiateAuthKeyRequest(final SABMCreditCardTransactionData requestData)
            throws SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {
        return this.strategyMap.get(CheckoutPaymentType.CARD).getAuthKey(requestData);
    }

    @Override
    public TxnResponse initiateCheckAuthKeyTransactionRequest(String authKey)
            throws
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {
        return this.strategyMap.get(CheckoutPaymentType.CARD).checkAuthKeyTransactionResponse(authKey);
    }

    public void setStrategyMap(Map strategyMap) {
        this.strategyMap = strategyMap;
    }

    public Map getStrategyMap() {
        return strategyMap;
    }
}
