package com.sabmiller.merchantsuiteservices.service.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.MerchantSuite.api.BankAccountDetails;
import com.MerchantSuite.api.CreditCardDetails;
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
import com.sabmiller.merchantsuiteservices.strategy.SABMGlobalMerchantSuiteStrategy;
import com.sabmiller.merchantsuiteservices.strategy.impl.SABMBankAccountMerchantSuiteStrategy;
import com.sabmiller.merchantsuiteservices.strategy.impl.SABMCreditCardMerchantSuiteStrategy;

@UnitTest
public class SABMMerchantSuiteServiceImplTest {

    private SABMMerchantSuiteTokenRequestData request;

    @InjectMocks
    SABMMerchantSuiteServiceImpl sabmMerchantSuiteService;

    private BankAccountDetails bankAccountDetails;

    private CreditCardDetails creditCardDetails;


    public Map<CheckoutPaymentType,SABMGlobalMerchantSuiteStrategy> strategyMap;

    @Mock
    SABMBankAccountMerchantSuiteStrategy sabmBankAccountMerchantSuiteStrategy;

    @Mock
    SABMCreditCardMerchantSuiteStrategy sabmCreditCardMerchantSuiteStrategy;


    SABMMerchantSuiteTokenTransactionData sabmMerchantSuiteTokenTransactionData;

    SABMCreditCardTransactionData sabmCreditCardTransactionData;

    private static final String AUTH="AUTH";

    TxnResp resp;

    TxnResponse txnResponse;


    @Before
    public void before()
    {
        MockitoAnnotations.initMocks(this);
        request = new SABMMerchantSuiteTokenRequestData();

        strategyMap = new HashMap<>();
        strategyMap.put(CheckoutPaymentType.ACCOUNT,sabmBankAccountMerchantSuiteStrategy);
        strategyMap.put(CheckoutPaymentType.CARD,sabmCreditCardMerchantSuiteStrategy);

    }

    @Test
    public void testinitiateTokenRequest_with_bankdetails()
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteAPIRequestInvalidException,
            SABMMerchantSuitePaymentErrorException {
        bankAccountDetails = new BankAccountDetails();
        request.setBankAccountDetails(bankAccountDetails);
        sabmMerchantSuiteService.setStrategyMap(strategyMap);
        when(this.strategyMap.get(CheckoutPaymentType.ACCOUNT).getToken(request)).thenReturn("Token");
        Assert.assertNotNull(sabmMerchantSuiteService.initiateTokenRequest(request));
    }

    @Test
    public void testinitiateTokenRequest_with_cardDetails()
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteAPIRequestInvalidException,
            SABMMerchantSuitePaymentErrorException {
        creditCardDetails = new CreditCardDetails();
        request.setCreditCardDetails(creditCardDetails);
        sabmMerchantSuiteService.setStrategyMap(strategyMap);
        when(this.strategyMap.get(CheckoutPaymentType.CARD).getToken(request)).thenReturn("Token");
        Assert.assertNotNull(sabmMerchantSuiteService.initiateTokenRequest(request));
    }

    @Test
    public void testinitiateTokenRequest_with_nopaymentdetails()
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteAPIRequestInvalidException,
            SABMMerchantSuitePaymentErrorException {
        sabmMerchantSuiteService.setStrategyMap(strategyMap);
        when(this.strategyMap.get(CheckoutPaymentType.CARD).getToken(request)).thenReturn("Token");
        Assert.assertNull(sabmMerchantSuiteService.initiateTokenRequest(request));
    }

    @Test
    public void testisApproved_Approved() throws SABMMerchantSuitePaymentErrorException {
        Assert.assertTrue(sabmMerchantSuiteService.isApproved("0","action"));
    }

    @Test
    public void testisApproved_Declined() throws SABMMerchantSuitePaymentErrorException {
        Assert.assertFalse(sabmMerchantSuiteService.isApproved("1","action"));
    }

    @Test(expected = SABMMerchantSuitePaymentErrorException.class)
    public void testisApproved_error() throws SABMMerchantSuitePaymentErrorException {
       sabmMerchantSuiteService.isApproved("2","action");
    }

    @Test(expected = SABMMerchantSuitePaymentErrorException.class)
    public void testisApproved_error3() throws SABMMerchantSuitePaymentErrorException {
        sabmMerchantSuiteService.isApproved("3","action");
    }

    @Test
    public void testinitiateTransactionRequests()
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteAPIRequestInvalidException {
        sabmMerchantSuiteTokenTransactionData = new SABMMerchantSuiteTokenTransactionData();
        sabmMerchantSuiteService.setStrategyMap(strategyMap);
        resp = new TxnResp();
        when(this.strategyMap.get(CheckoutPaymentType.ACCOUNT).processTransaction(sabmMerchantSuiteTokenTransactionData)).thenReturn(resp);
        Assert.assertEquals(sabmMerchantSuiteService.initiateTransactionRequest(sabmMerchantSuiteTokenTransactionData,CheckoutPaymentType.ACCOUNT),resp);
    }

    @Test
    public void testinitiateAuthKeyRequest()
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException {
        sabmCreditCardTransactionData = new SABMCreditCardTransactionData();
        sabmMerchantSuiteTokenTransactionData = new SABMMerchantSuiteTokenTransactionData();
        sabmMerchantSuiteService.setStrategyMap(strategyMap);
        resp = new TxnResp();
        when(this.strategyMap.get(CheckoutPaymentType.CARD).getAuthKey(sabmCreditCardTransactionData)).thenReturn(AUTH);
        Assert.assertEquals(sabmMerchantSuiteService.initiateAuthKeyRequest(sabmCreditCardTransactionData),AUTH);
    }

    @Test
    public void testinitiateCheckAuthKeyTransactionRequest()
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException {
        sabmMerchantSuiteService.setStrategyMap(strategyMap);
        txnResponse = new TxnResponse();
        when(this.strategyMap.get(CheckoutPaymentType.CARD).checkAuthKeyTransactionResponse(AUTH)).thenReturn(txnResponse);
        Assert.assertEquals(sabmMerchantSuiteService.initiateCheckAuthKeyTransactionRequest(AUTH),txnResponse);
    }



}
