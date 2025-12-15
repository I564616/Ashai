package com.sabmiller.merchantsuiteservices.service.impl;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.MerchantSuite.api.BankAccountDetails;
import com.MerchantSuite.api.TxnResp;
import com.MerchantSuite.api.TxnResponse;
import com.sabmiller.commons.utils.SABMMathUtils;
import com.sabmiller.facades.merchant.suite.data.SABMBankDetailsData;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.merchantsuiteservices.data.MerchantSuiteResponseData;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTokenRequestData;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTokenTransactionData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteAPIRequestInvalidException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteMissingBankDetailsException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenAPIException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenException;
import com.sabmiller.merchantsuiteservices.service.SABMMerchantSuitePaymentService;
import com.sabmiller.merchantsuiteservices.service.SABMMerchantSuiteService;
import com.sabmiller.merchantsuiteservices.strategy.impl.SABMMerchantSuiteEFTPersistenceStategyImpl;

public class SABMMerchantSuitePaymentServiceImpl implements SABMMerchantSuitePaymentService {

    private final static Logger LOG = LoggerFactory.getLogger(SABMMerchantSuitePaymentServiceImpl.class.getName());

    @Resource
    private SABMMerchantSuiteService sabmMerchantSuiteService;

    private Map<String,SABMMerchantSuiteEFTPersistenceStategyImpl> strategyMap;

    @Resource
    private B2BCommerceUnitService b2bCommerceUnitService;

    @Override
    public boolean isApproved(final MerchantSuiteResponseData responseData) throws SABMMerchantSuitePaymentErrorException {
        return sabmMerchantSuiteService.isApproved(responseData.getResponseCode(), responseData.getAction());
    }

    @Override
    public String initiateTokenRequest(final SABMBankDetailsData bankDetailsData)
            throws SABMMerchantSuiteTokenException, SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException, SABMMerchantSuitePaymentErrorException {
        SABMMerchantSuiteTokenRequestData sabmMerchantSuiteTokenRequestData = new SABMMerchantSuiteTokenRequestData();
        BankAccountDetails bankAccountDetails = new BankAccountDetails();
        bankAccountDetails.setAccountName(bankDetailsData.getAccountName());
        bankAccountDetails.setAccountNumber(bankDetailsData.getAccountNumber());
        bankAccountDetails.setBSBNumber(bankDetailsData.getBsb());
        sabmMerchantSuiteTokenRequestData.setBankAccountDetails(bankAccountDetails);
        sabmMerchantSuiteTokenRequestData.setAccountNumber(b2bCommerceUnitService.getRootUnit().getUid());
        return  sabmMerchantSuiteService.initiateTokenRequest(sabmMerchantSuiteTokenRequestData);
    }


    @Override
    public String initiateAuthKeyRequest(final SABMCreditCardTransactionData requestData)
            throws SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {
        requestData.setAccountNumber(b2bCommerceUnitService.getRootUnit().getUid());
        return  sabmMerchantSuiteService.initiateAuthKeyRequest(requestData);
    }

    @Override
    public TxnResponse initiateCheckAuthKeyTxnRequest(final String authKey)
            throws
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {
        return  sabmMerchantSuiteService.initiateCheckAuthKeyTransactionRequest(authKey);
    }


    @Override
    public TxnResp initiateInvoiceTxnRequest(final BigDecimal total, final String currencyIso, String paymentId, String token)
            throws SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {
        //Creating Data Object
        SABMMerchantSuiteTokenTransactionData sabmMerchantSuiteTokenTransactionData = new SABMMerchantSuiteTokenTransactionData();
        sabmMerchantSuiteTokenTransactionData.setAccountNumber(b2bCommerceUnitService.getRootUnit().getUid());
        sabmMerchantSuiteTokenTransactionData.setCurrencyIso(currencyIso);
        sabmMerchantSuiteTokenTransactionData.setPaymentId(paymentId);
        sabmMerchantSuiteTokenTransactionData.setTotal(SABMMathUtils.convertBigDecimaltoLong(total));
        sabmMerchantSuiteTokenTransactionData.setToken(token);
        return  sabmMerchantSuiteService.initiateTransactionRequest(sabmMerchantSuiteTokenTransactionData, CheckoutPaymentType.ACCOUNT);
    }

    public void setStrategyMap(Map strategyMap) {
        this.strategyMap = strategyMap;
    }

    public Map getStrategyMap() {
        return strategyMap;
    }
}
