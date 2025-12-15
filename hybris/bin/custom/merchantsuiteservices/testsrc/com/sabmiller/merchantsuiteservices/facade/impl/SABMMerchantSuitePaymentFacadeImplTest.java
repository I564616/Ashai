package com.sabmiller.merchantsuiteservices.facade.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration2.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.MerchantSuite.api.MaskedBankAccountDetails;
import com.MerchantSuite.api.MaskedCreditCardDetails;
import com.MerchantSuite.api.TxnResp;
import com.MerchantSuite.api.TxnResponse;
import com.sabmiller.core.cart.service.SABMCalculationService;
import com.sabmiller.core.model.InvoicePaymentModel;
import com.sabmiller.facades.merchant.suite.data.SABMBankDetailsData;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTransactionProcessData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteAPIRequestInvalidException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteCartTotalException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteInvalidInvoiceDataException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteMissingBankDetailsException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenAPIException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteWebHookPaymentAlreadyDone;
import com.sabmiller.merchantsuiteservices.exception.SABMSurchargeCalculationException;
import com.sabmiller.merchantsuiteservices.service.impl.SABMMerchantSuitePaymentServiceImpl;


@UnitTest
public class SABMMerchantSuitePaymentFacadeImplTest {

    @InjectMocks
    private SABMMerchantSuitePaymentFacadeImpl sabmMerchantSuitePaymentFacade;

    @Mock
    private SABMMerchantSuitePaymentServiceImpl sabmMerchantSuitePaymentService;

    @Mock
    private UserService userService;

    @Mock
    private PersistentKeyGenerator keyGenerator;

    @Mock
    private CartService cartService;

    @Mock
    private SABMCalculationService calculationService;

    @Mock
    private SABMMerchantSuitePaymentFacadeHelper sabmMerchantSuitePaymentFacadeHelper;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    Set<String> invoices;

    private static final String SAMPLE_INVOICE="123456";

    private static final String SAMPLE_CURRENCY="AUD";

    private static final String SAMPLE_CARDTYPE="VISA";

    private BigDecimal sampleTotal;

    @Mock
    InvoicePaymentModel invoicePaymentModel;

    private SABMBankDetailsData bankDetailsData;

    private BigDecimal total;

    private static final String TEST_TOKEN="12345678";


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private TxnResp resp;

    private TxnResponse response;

    private static final String SUCCESS_BANK_RESPONSE_CODE="00";

    private static final String CARD_NAME="cardName";

    private static final String CARD_NUMBER="cardName";

    private static final String CARD_EXPIRY="cardExpiry";

    private static final String TEST_BASE_URL="baseUrl.com";

    private static final String TEST_AUTH="1234566";

    private  static final String BASE_URL="merchant.suite.base.url";

    @Mock
    private CartModel cartModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        total = new BigDecimal(100);
        bankDetailsData = new SABMBankDetailsData();
        invoices =  new HashSet<>();
        resp = new TxnResp();
        response = new TxnResponse();
        MaskedBankAccountDetails bankAccountDetails = new MaskedBankAccountDetails();
        bankAccountDetails.setAccountNumber(CARD_NUMBER);
        bankAccountDetails.setAccountName(CARD_NAME);
        bankAccountDetails.setBSBNumber(CARD_NAME);
        MaskedCreditCardDetails creditCardDetails = new MaskedCreditCardDetails();
        creditCardDetails.setCardHolderName(CARD_NAME);
        creditCardDetails.setMaskedCardNumber(CARD_NUMBER);
        creditCardDetails.setExpiryDate(CARD_EXPIRY);
        response.setCardDetails(creditCardDetails) ;
        response.setBankAccountDetails(bankAccountDetails);
        response.setReference1(TEST_AUTH);
        resp.setTxnResponse(response);
    }

    @Test(expected = SABMMerchantSuiteInvalidInvoiceDataException.class)
   public void testInitiateInvoiceEFTxn_throw_SABMMerchantSuiteInvalidInvoiceDataException_noinvoice()
            throws SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteInvalidInvoiceDataException, SABMMerchantSuitePaymentErrorException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteTokenException, SABMMerchantSuiteAPIRequestInvalidException {
        sabmMerchantSuitePaymentFacade.initiateInvoiceEFTxn(invoices, sampleTotal , new SABMBankDetailsData(),SAMPLE_CURRENCY);
   }

    @Test(expected = SABMMerchantSuiteInvalidInvoiceDataException.class)
    public void testInitiateInvoiceEFTxn_throw_SABMMerchantSuiteInvalidInvoiceDataException_invalidAmount()
            throws SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteInvalidInvoiceDataException, SABMMerchantSuitePaymentErrorException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteTokenException, SABMMerchantSuiteAPIRequestInvalidException {
        invoices.add(SAMPLE_INVOICE);
        sabmMerchantSuitePaymentFacade.initiateInvoiceEFTxn(invoices, new BigDecimal(0) , new SABMBankDetailsData(),SAMPLE_CURRENCY);
    }

    @Test
    public void testInitiateInvoiceEFTxn()
            throws SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteInvalidInvoiceDataException, SABMMerchantSuitePaymentErrorException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteTokenException, SABMMerchantSuiteAPIRequestInvalidException {
        invoices.add(SAMPLE_INVOICE);
        when(sabmMerchantSuitePaymentService.initiateTokenRequest(bankDetailsData)).thenReturn(TEST_TOKEN);
        when(sabmMerchantSuitePaymentService.initiateInvoiceTxnRequest(ArgumentMatchers.any(BigDecimal.class),ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(resp);
        when(sabmMerchantSuitePaymentFacadeHelper.processInvoiceRedirect(ArgumentMatchers.any(),ArgumentMatchers.anyBoolean())).thenReturn(true);
        Assert.assertNotNull(sabmMerchantSuitePaymentFacade.initiateInvoiceEFTxn(invoices, new BigDecimal(100) , bankDetailsData,SAMPLE_CURRENCY));
    }

    @Test(expected = SABMMerchantSuiteInvalidInvoiceDataException.class)
    public void testInitiateInvoiceCCTxn_throw_SABMMerchantSuiteInvalidInvoiceDataException_noinvoice()
            throws SABMSurchargeCalculationException, SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteInvalidInvoiceDataException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException {
        sabmMerchantSuitePaymentFacade.initiateInvoiceCCTxn(invoices, sampleTotal,SAMPLE_CURRENCY,SAMPLE_CARDTYPE);
    }

    @Test(expected = SABMMerchantSuiteInvalidInvoiceDataException.class)
    public void testInitiateInvoiceCCTxn_throw_SABMMerchantSuiteInvalidInvoiceDataException_invalidAmount()
            throws SABMSurchargeCalculationException, SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteInvalidInvoiceDataException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException {
        invoices.add(SAMPLE_INVOICE);
        sabmMerchantSuitePaymentFacade.initiateInvoiceCCTxn(invoices, sampleTotal,SAMPLE_CURRENCY,SAMPLE_CARDTYPE);
    }

    @Test
    public void testInitiateInvoiceCCTxn()
            throws SABMSurchargeCalculationException, SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteInvalidInvoiceDataException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException {
        invoices.add(SAMPLE_INVOICE);
        when(sabmMerchantSuitePaymentFacadeHelper.calculateSurchargeForCreditCardType(ArgumentMatchers.any(CreditCardType.class),ArgumentMatchers.any(BigDecimal.class))).thenReturn(new BigDecimal(1));
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(BASE_URL)).thenReturn(TEST_BASE_URL);
        when(sabmMerchantSuitePaymentService.initiateAuthKeyRequest(ArgumentMatchers.any(SABMCreditCardTransactionData.class))).thenReturn(TEST_AUTH);
        Assert.assertNotNull(sabmMerchantSuitePaymentFacade.initiateInvoiceCCTxn(invoices, new BigDecimal(100),SAMPLE_CURRENCY,SAMPLE_CARDTYPE));
    }

    @Test(expected = InvalidCartException.class)
    public void testInitiateCheckoutCCTxn_throw_InvalidCartException_noSessionCart()
            throws SABMSurchargeCalculationException, SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteConfigurationException, InvalidCartException, SABMMerchantSuiteCartTotalException,
            SABMMerchantSuiteTokenException, SABMMerchantSuiteAPIRequestInvalidException {
        when(cartService.getSessionCart()).thenReturn(null);
        sabmMerchantSuitePaymentFacade.initiateCheckoutCCTxn(SAMPLE_CARDTYPE);
    }

    @Test(expected = InvalidCartException.class)
    public void testInitiateCheckoutCCTxn_throw_InvalidCartException_cartRequiresCalculation()
            throws SABMSurchargeCalculationException, SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteConfigurationException, InvalidCartException, SABMMerchantSuiteCartTotalException,
            SABMMerchantSuiteTokenException, SABMMerchantSuiteAPIRequestInvalidException {
        when(cartService.hasSessionCart()).thenReturn(true);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(calculationService.requiresCalculation(cartModel)).thenReturn(true);
        sabmMerchantSuitePaymentFacade.initiateCheckoutCCTxn(SAMPLE_CARDTYPE);
    }

    @Test(expected = SABMMerchantSuiteCartTotalException.class)
    public void testInitiateCheckoutCCTxn_throw_InvalidCartException_cartRequireCalculation()
            throws SABMSurchargeCalculationException, SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteConfigurationException, InvalidCartException, SABMMerchantSuiteCartTotalException,
            SABMMerchantSuiteTokenException, SABMMerchantSuiteAPIRequestInvalidException {
        when(cartService.hasSessionCart()).thenReturn(true);
        when(calculationService.requiresCalculation(cartModel)).thenReturn(false);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getTotalPrice()).thenReturn(0d);
        sabmMerchantSuitePaymentFacade.initiateCheckoutCCTxn(SAMPLE_CARDTYPE);
    }

    @Test
    public void testProcessInvoiceAuthKeyCCTxn()
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteTokenException, SABMMerchantSuitePaymentErrorException, SABMMerchantSuiteAPIRequestInvalidException {
        when( this.sabmMerchantSuitePaymentService.initiateCheckAuthKeyTxnRequest(TEST_AUTH)).thenReturn(response);
        when(sabmMerchantSuitePaymentFacadeHelper.processInvoiceRedirect(ArgumentMatchers.any(SABMMerchantSuiteTransactionProcessData.class),ArgumentMatchers.anyBoolean())).thenReturn(true);
        Assert.assertNotNull(sabmMerchantSuitePaymentFacade.processInvoiceAuthKeyCCTxn(TEST_AUTH));
    }

    @Test
    public void testProcessCheckoutAuthKeyCCTxn()
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteTokenException, SABMMerchantSuitePaymentErrorException, InvalidCartException,
            SABMMerchantSuiteAPIRequestInvalidException, SABMMerchantSuiteWebHookPaymentAlreadyDone {
        when( this.sabmMerchantSuitePaymentService.initiateCheckAuthKeyTxnRequest(TEST_AUTH)).thenReturn(response);
        when(sabmMerchantSuitePaymentFacadeHelper.processInvoiceRedirect(ArgumentMatchers.any(SABMMerchantSuiteTransactionProcessData.class),ArgumentMatchers.anyBoolean())).thenReturn(true);
        sabmMerchantSuitePaymentFacade.processCheckoutAuthKeyCCTxn(TEST_AUTH);
    }

}
