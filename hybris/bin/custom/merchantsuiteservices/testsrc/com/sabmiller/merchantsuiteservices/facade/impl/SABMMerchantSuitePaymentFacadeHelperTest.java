package com.sabmiller.merchantsuiteservices.facade.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.commerceservices.order.dao.impl.DefaultCommerceCartDao;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.MerchantSuite.api.TxnResp;
import com.MerchantSuite.api.TxnResponse;
import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.core.model.InvoicePaymentModel;
import com.sabmiller.core.salesordercreate.service.SABMSalesOrderCreateService;
import com.sabmiller.facades.merchant.suite.data.SABMBankDetailsData;
import com.sabmiller.facades.ysdm.data.YSDMRequest;
import com.sabmiller.merchantsuiteservices.constants.MerchantsuiteservicesConstants;
import com.sabmiller.merchantsuiteservices.dao.InvoicePaymentDao;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTransactionProcessData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.strategy.SABMMerchantSuitePaymentTypePersistenceStategy;
import com.sabmiller.merchantsuiteservices.strategy.impl.SABMMerchantSuiteCreditCardPersistenceStategyImpl;
import com.sabmiller.merchantsuiteservices.strategy.impl.SABMMerchantSuiteEFTPersistenceStategyImpl;

public class SABMMerchantSuitePaymentFacadeHelperTest {

    @InjectMocks
    SABMMerchantSuitePaymentFacadeHelper sabmMerchantSuitePaymentFacadeHelper;

    @Mock
    ModelService modelService;

    @Mock
    private CurrencyDao currencyDao;

    @Mock
    private UserService userService;

    @Mock
    private DefaultCommerceCartDao commerceCartDao;

    @Mock
    private SystemEmailService sabmSystemEmailService;

    @Mock
    private SABMSalesOrderCreateService salesOrderCreateService;

    @Mock
    private Converter<AbstractOrderModel, YSDMRequest> salesOrderYSDMConverter;

    @Mock
    private Converter<InvoicePaymentModel, YSDMRequest> invoiceYSDMConverter;

    @Mock
    private BaseStoreService baseStoreService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private EventService eventService;

    @Mock
    private SessionService sessionService;

    @Mock
    private InvoicePaymentDao sabminvoicePaymentDao;

    private static final String APPROVED_RESPONSE_CODE = "0";

    private static final ArrayList<String> APPROVED_BANK_CODE = new ArrayList<>(Arrays.asList("00", "08", "16","78"));

    private static final ArrayList<String> DECLINED_BANK_CODE = new ArrayList<>(Arrays.asList());

    private static final ArrayList<String> ERROR_BANK_CODE = new ArrayList<>(Arrays.asList());

    private static final String PAYMENT_PROVIDER="Merchant Suite";

    Set<String> invoices;

    private static final String SAMPLE_INVOICE="123456";

    private static final String SAMPLE_TRACKING="123456";

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


    private static final String TEST_EMAIL="email@test.com";

    private static final String TEST_CART_ID="12345";

    private static final String TEST_AUTH="1234566";

    private  static final String BASE_URL="merchant.suite.base.url";

    @Mock
    CurrencyModel currencyModel;

    List<CurrencyModel> currencyList;

    @Mock
    UserModel userModel;


    private  SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData;

    private Map<String,SABMMerchantSuitePaymentTypePersistenceStategy> strategyMap;

    @Mock
    PaymentTransactionModel paymentTransactionModel;

    @Mock
    PaymentTransactionEntryModel paymentTransactionEntryModel;

    @Mock
    CartModel cartModel;
    
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModel;





    @Mock
    private SABMMerchantSuiteCreditCardPersistenceStategyImpl sabmMerchantSuiteCreditCardPersistenceStategy;

    @Mock
    private SABMMerchantSuiteEFTPersistenceStategyImpl sabmMerchantSuiteEFTPersistenceStategy;

    public SABMMerchantSuitePaymentFacadeHelperTest() {
    }

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        currencyList = new ArrayList<>();
        currencyList.add(currencyModel);
        invoices =  new HashSet<>();
        sabmMerchantSuiteTransactionProcessData = new SABMMerchantSuiteTransactionProcessData();
        sabmMerchantSuiteTransactionProcessData.setReference1(SAMPLE_TRACKING);
        strategyMap = new HashMap<>();
        strategyMap.put(MerchantsuiteservicesConstants.PAYMENT_METHOD.BANKTRANSFER,sabmMerchantSuiteEFTPersistenceStategy);
        strategyMap.put(MerchantsuiteservicesConstants.PAYMENT_METHOD.CREDITCARD,sabmMerchantSuiteCreditCardPersistenceStategy);
        sabmMerchantSuitePaymentFacadeHelper.setStrategyMap(strategyMap);
    }

    @Test
    public void testSaveInvoiceData()
    {
        invoices.add(SAMPLE_INVOICE);
        when(modelService.create(InvoicePaymentModel._TYPECODE)).thenReturn(invoicePaymentModel);
        when(currencyDao.findCurrenciesByCode(ArgumentMatchers.anyString())).thenReturn(currencyList);
        Assert.assertNotNull(sabmMerchantSuitePaymentFacadeHelper.saveInvoiceData(invoices,new BigDecimal(100),SAMPLE_CURRENCY,userModel,SAMPLE_TRACKING));
        verify(this.modelService, times(1)).save(invoicePaymentModel);
    }

    public void testSendPaymentConfirmationEmail()
    {
        when(sabminvoicePaymentDao.getInvoice(SAMPLE_TRACKING)).thenReturn(invoicePaymentModel);
        when(invoicePaymentModel.getInvoices()).thenReturn(new ArrayList<String>(invoices));
    }

    @Test(expected = SABMMerchantSuitePaymentErrorException.class)
    public void testProcessInvoiceRedirect_throw_SABMMerchantSuitePaymentErrorException_approvedbankcode()
            throws SABMMerchantSuitePaymentErrorException {
        sabmMerchantSuiteTransactionProcessData.setResponseCode(APPROVED_RESPONSE_CODE);
        sabmMerchantSuiteTransactionProcessData.setBankResponseCode("99");
        this.sabmMerchantSuitePaymentFacadeHelper.processInvoiceRedirect(sabmMerchantSuiteTransactionProcessData,false);
    }

    @Test(expected = SABMMerchantSuitePaymentErrorException.class)
    public void testProcessInvoiceRedirect_throw_SABMMerchantSuitePaymentErrorException_notapprovedbankcode()
            throws SABMMerchantSuitePaymentErrorException {
        sabmMerchantSuiteTransactionProcessData.setResponseCode("01");
        sabmMerchantSuiteTransactionProcessData.setBankResponseCode("99");
        when(sabminvoicePaymentDao.getInvoice(SAMPLE_TRACKING)).thenReturn(invoicePaymentModel);
        when(invoicePaymentModel.getUser()).thenReturn(userModel);
        this.sabmMerchantSuitePaymentFacadeHelper.processInvoiceRedirect(sabmMerchantSuiteTransactionProcessData,false);
    }

    @Test(expected = SABMMerchantSuitePaymentErrorException.class)
    public void testProcessInvoiceRedirect_throw_SABMMerchantSuitePaymentErrorException_noPaymentDetailsFound()
            throws SABMMerchantSuitePaymentErrorException {
        sabmMerchantSuiteTransactionProcessData.setResponseCode(APPROVED_RESPONSE_CODE);
        sabmMerchantSuiteTransactionProcessData.setBankResponseCode("00");
        when(sabminvoicePaymentDao.getInvoice(SAMPLE_TRACKING)).thenReturn(invoicePaymentModel);
        when(invoicePaymentModel.getUser()).thenReturn(userModel);
        this.sabmMerchantSuitePaymentFacadeHelper.processInvoiceRedirect(sabmMerchantSuiteTransactionProcessData,false);
    }


    @Test
    public void testProcessInvoiceRedirect()
            throws SABMMerchantSuitePaymentErrorException {
        sabmMerchantSuiteTransactionProcessData.setMaskedCardNumber(CARD_NUMBER);
        sabmMerchantSuiteTransactionProcessData.setExpiryDate(CARD_EXPIRY);
        sabmMerchantSuiteTransactionProcessData.setResponseCode(APPROVED_RESPONSE_CODE);
        sabmMerchantSuiteTransactionProcessData.setCardType("VC");
        sabmMerchantSuiteTransactionProcessData.setBankResponseCode("00");
        when(sabminvoicePaymentDao.getInvoice(SAMPLE_TRACKING)).thenReturn(invoicePaymentModel);
        when(modelService.create(PaymentTransactionModel._TYPECODE)).thenReturn(paymentTransactionModel);
        when(modelService.create(PaymentTransactionEntryModel._TYPECODE)).thenReturn(paymentTransactionEntryModel);
        when(invoicePaymentModel.getUser()).thenReturn(userModel);
        Assert.assertTrue(this.sabmMerchantSuitePaymentFacadeHelper.processInvoiceRedirect(sabmMerchantSuiteTransactionProcessData,false));
    }

    @Test(expected = SABMMerchantSuitePaymentErrorException.class)
    public void testProcessCheckoutRedirect_throw_SABMMerchantSuitePaymentErrorException_approvedbankcode()
            throws SABMMerchantSuitePaymentErrorException, InvalidCartException {
        sabmMerchantSuiteTransactionProcessData.setResponseCode(APPROVED_RESPONSE_CODE);
        sabmMerchantSuiteTransactionProcessData.setBankResponseCode("99");
        this.sabmMerchantSuitePaymentFacadeHelper.processCheckoutRedirect(sabmMerchantSuiteTransactionProcessData);
    }

    @Test(expected = SABMMerchantSuitePaymentErrorException.class)
    public void testProcessCheckoutRedirect_throw_SABMMerchantSuitePaymentErrorException_notapprovedbankcode()
            throws SABMMerchantSuitePaymentErrorException, InvalidCartException {
        sabmMerchantSuiteTransactionProcessData.setResponseCode("01");
        sabmMerchantSuiteTransactionProcessData.setBankResponseCode("99");
        when(sabminvoicePaymentDao.getInvoice(SAMPLE_TRACKING)).thenReturn(invoicePaymentModel);
        when(invoicePaymentModel.getUser()).thenReturn(userModel);
        this.sabmMerchantSuitePaymentFacadeHelper.processCheckoutRedirect(sabmMerchantSuiteTransactionProcessData);
    }

    @Test(expected = SABMMerchantSuitePaymentErrorException.class)
    public void testProcessCheckoutRedirect_throw_SABMMerchantSuitePaymentErrorException_noPaymentDetailsFound()
            throws SABMMerchantSuitePaymentErrorException, InvalidCartException {
        sabmMerchantSuiteTransactionProcessData.setResponseCode(APPROVED_RESPONSE_CODE);
        sabmMerchantSuiteTransactionProcessData.setBankResponseCode("00");
        sabmMerchantSuiteTransactionProcessData.setEmailAddress(TEST_EMAIL);
        sabmMerchantSuiteTransactionProcessData.setReference2(TEST_CART_ID);
        when (userService.getUserForUID(TEST_EMAIL)).thenReturn(userModel);
        when(commerceCartDao.getCartForCodeAndUser(TEST_CART_ID, userModel)).thenReturn(cartModel);
        when(cartModel.getUser()).thenReturn(userModel);
        when(cartModel.getCode()).thenReturn(TEST_CART_ID);
        this.sabmMerchantSuitePaymentFacadeHelper.processCheckoutRedirect(sabmMerchantSuiteTransactionProcessData);
    }

    @Test
    public void testProcessCheckoutRedirect()
            throws SABMMerchantSuitePaymentErrorException, InvalidCartException {
        sabmMerchantSuiteTransactionProcessData.setMaskedCardNumber(CARD_NUMBER);
        sabmMerchantSuiteTransactionProcessData.setExpiryDate(CARD_EXPIRY);
        sabmMerchantSuiteTransactionProcessData.setResponseCode(APPROVED_RESPONSE_CODE);
        sabmMerchantSuiteTransactionProcessData.setCardType("VC");
        sabmMerchantSuiteTransactionProcessData.setResponseCode(APPROVED_RESPONSE_CODE);
        sabmMerchantSuiteTransactionProcessData.setBankResponseCode("00");
        sabmMerchantSuiteTransactionProcessData.setEmailAddress(TEST_EMAIL);
        sabmMerchantSuiteTransactionProcessData.setReference2(TEST_CART_ID);
        when(modelService.create(PaymentTransactionModel._TYPECODE)).thenReturn(paymentTransactionModel);
        when(modelService.create(PaymentTransactionEntryModel._TYPECODE)).thenReturn(paymentTransactionEntryModel);
        when (userService.getUserForUID(TEST_EMAIL)).thenReturn(userModel);
        when(commerceCartDao.getCartForCodeAndUser(TEST_CART_ID, userModel)).thenReturn(cartModel);
        when(cartModel.getUser()).thenReturn(userModel);
        when(cartModel.getCode()).thenReturn(TEST_CART_ID);
        when(this.sabmMerchantSuiteCreditCardPersistenceStategy.createPaymentInfo(ArgumentMatchers.any(SABMMerchantSuiteTransactionProcessData.class),ArgumentMatchers.any(UserModel.class))).thenReturn(creditCardPaymentInfoModel);
        Assert.assertTrue(this.sabmMerchantSuitePaymentFacadeHelper.processCheckoutRedirect(sabmMerchantSuiteTransactionProcessData));
    }

}
