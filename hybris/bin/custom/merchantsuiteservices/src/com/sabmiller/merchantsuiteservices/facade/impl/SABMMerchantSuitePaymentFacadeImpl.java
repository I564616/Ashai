package com.sabmiller.merchantsuiteservices.facade.impl;

import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import jakarta.annotation.Resource;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.MerchantSuite.api.MaskedBankAccountDetails;
import com.MerchantSuite.api.MaskedCreditCardDetails;
import com.MerchantSuite.api.TxnResp;
import com.MerchantSuite.api.TxnResponse;
import com.sabmiller.commons.utils.SABMMathUtils;
import com.sabmiller.core.b2b.services.impl.DefaultSabmB2BCustomerServiceImpl;
import com.sabmiller.core.cart.service.SABMCalculationService;
import com.sabmiller.core.model.InvoicePaymentModel;
import com.sabmiller.facades.merchant.suite.data.SABMBankDetailsData;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardValidationData;
import com.sabmiller.merchantsuiteservices.constants.MerchantsuiteservicesConstants;
import com.sabmiller.merchantsuiteservices.dao.InvoicePaymentDao;
import com.sabmiller.merchantsuiteservices.data.InvoicePaymentData;
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
import com.sabmiller.merchantsuiteservices.facade.SABMMerchantSuitePaymentFacade;
import com.sabmiller.merchantsuiteservices.service.impl.SABMMerchantSuitePaymentServiceImpl;
import com.sabmiller.merchantsuiteservices.strategy.impl.SABMDefaultMerchantSuiteStrategy;

/**
 *
 * Merchant Suite Payment Facade class to initiate any interaction with MS/Payment Related tasks
 * @author akshay.a.malik
 * @see com.sabmiller.merchantsuiteservices.facade.SABMMerchantSuitePaymentFacade for interface details
 * @see SABMMerchantSuitePaymentFacadeHelper for helper methods.
 *
 */
public class SABMMerchantSuitePaymentFacadeImpl implements SABMMerchantSuitePaymentFacade {

    //LOG Object
    private final static Logger LOG = LoggerFactory.getLogger(SABMMerchantSuitePaymentFacadeImpl.class.getName());

    @Resource
    private SABMMerchantSuitePaymentServiceImpl sabmMerchantSuitePaymentService;

    @Resource
    private UserService userService;

    @Resource(name = "merchantSuitePaymentReferenceIdGenerator")
    private PersistentKeyGenerator keyGenerator;

    @Resource
    private CartService cartService;

    @Resource(name = "calculationService")
    private SABMCalculationService calculationService;

    @Resource(name = "sabminvoicePaymentDao")
    private InvoicePaymentDao invoicePaymentDao;

    @Resource
    private SABMMerchantSuitePaymentFacadeHelper sabmMerchantSuitePaymentFacadeHelper;

    private  static final String BASE_URL="merchant.suite.base.url.without.version";

    private  static final String TEST_MODE="merchant.suite.test.mode";

    private Converter<InvoicePaymentModel, InvoicePaymentData> invoicePaymentConverter;

    @Resource
    private ConfigurationService configurationService;

    @Resource
    private SABMDefaultMerchantSuiteStrategy sabmDefaultMerchantSuiteTokenStrategy;

    @Resource
    CommerceCartService commerceCartService;

    @Resource
    DefaultSabmB2BCustomerServiceImpl defaultSabmB2BCustomerService;

    @Resource
    FlexibleSearchService flexibleSearchService;


    @Override
    public String initiateInvoiceEFTxn(final Set<String> invoices, final BigDecimal total, SABMBankDetailsData bankDetailsData,
            String currencyIso)
            throws SABMMerchantSuiteTokenException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuitePaymentErrorException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteInvalidInvoiceDataException, SABMMerchantSuiteAPIRequestInvalidException {
        //Empty check for invoices
        if (CollectionUtils.isEmpty(invoices))
        {
            throw new SABMMerchantSuiteInvalidInvoiceDataException("Invoice list is empty");
        }
        //total below zero check for total
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0 || StringUtils.isBlank(currencyIso))
        {
            throw new SABMMerchantSuiteInvalidInvoiceDataException("Invalid total and/or currency");
        }
        //initiate processing for EFT transactions
        return processInvoiceEFTxn(invoices, total, currencyIso, userService.getCurrentUser(),
                bankDetailsData);
    }

    @Override
    public boolean isInvoicePaid(final String trackingNumber)
    {
        final UserModel user = userService.getCurrentUser();
        final InvoicePaymentModel invoicePaymentModel = invoicePaymentDao.getInvoice(trackingNumber, user);
        if (invoicePaymentModel != null && BooleanUtils.isTrue(invoicePaymentModel.getPaid()))
        {
            LOG.info("Sending payment confirmation email for reference ::" + trackingNumber);
            sendConfirmationEmail(invoicePaymentModel);
            return true;
        }
        return false;
    }


    private void sendInvoicePaymentConfirmationEmail(final TxnResponse response)
    {
        final UserModel user = userService.getUserForUID(response.getEmailAddress());
        final InvoicePaymentModel invoicePaymentModel = invoicePaymentDao.getInvoice(response.getReference1(), user);
        if (invoicePaymentModel != null && BooleanUtils.isTrue(invoicePaymentModel.getPaid()))
        {
            LOG.info("Sending payment confirmation email for reference ::" + response.getReference1());
            sendConfirmationEmail(invoicePaymentModel);
        }
    }


    @Override
    public boolean sendConfirmationEmail(final InvoicePaymentModel invoicePaymentModel)
    {
        LOG.info("Sending payment confirmation email for invoice tracking [{}]", invoicePaymentModel.getPaymentCode());
        //Send Confirmation Email to the Customer
        sabmMerchantSuitePaymentFacadeHelper.createPaymentConfirmationEmailEvent(invoicePaymentModel);
        return invoicePaymentModel != null && BooleanUtils.isTrue(invoicePaymentModel.getPaid());
    }

    @Override
    public boolean hasExceededInvoiceWaitTimeout(final String trackingNumber)
    {
        final InvoicePaymentModel invoicePaymentModel = invoicePaymentDao.getInvoice(trackingNumber);
        return invoicePaymentModel != null && invoicePaymentModel.getPaymentInfo() != null
                && checkTimeout(invoicePaymentModel.getPaymentInfo().getCreationtime());
    }

    protected boolean checkTimeout(final Date date)
    {
        final Calendar cal = Calendar.getInstance();
        final Date now = cal.getTime();
        cal.setTime(date);
        cal.add(Calendar.MILLISECOND, Config.getInt("merchant.suite.payment.wait.timeout", 120000));
        return now.after(cal.getTime());
    }


    @Override
    public SABMCreditCardTransactionData initiateInvoiceCCTxn(final Set<String> invoices, final BigDecimal total,
            String currencyIso,String cardType)
            throws SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteMissingBankDetailsException, SABMSurchargeCalculationException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteInvalidInvoiceDataException, SABMMerchantSuiteAPIRequestInvalidException {

        //empty check for invoices
        if (CollectionUtils.isEmpty(invoices))
        {
            LOG.info("Invoice List is Empty..throwing SABMMerchantSuiteInvalidInvoiceDataException");
            throw new SABMMerchantSuiteInvalidInvoiceDataException("Invoice list is empty");
        }
        //empty check for amount
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0 || StringUtils.isBlank(currencyIso))
        {
            LOG.info("Invalid total and/or currency..throwing SABMMerchantSuiteInvalidInvoiceDataException");
            throw new SABMMerchantSuiteInvalidInvoiceDataException("Invalid total and/or currency");
        }
        return processInitiateInvoiceCCTxn(invoices, total, currencyIso, userService.getCurrentUser(),cardType);
    }

    @Override
    public SABMCreditCardTransactionData initiateCheckoutCCTxn(String cardType)
            throws SABMMerchantSuiteCartTotalException, InvalidCartException, SABMMerchantSuiteTokenException,
            SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException, SABMSurchargeCalculationException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {
       //check for session cart
        if (!cartService.hasSessionCart())
        {
            throw new InvalidCartException("No cart available");
        }
        final CartModel cartModel = cartService.getSessionCart();
        //check if the cart requires calculation
        //HC-437 commenting below section as for /paybycard mockup to avoid calculating the cart
        boolean flag = configurationService.getConfiguration().getBoolean("cub.order.dummy.price.flag",true);
        if(!flag) {
            if (calculationService.requiresCalculation(cartModel)) {
                throw new InvalidCartException("Cart not calculated");
            }
        }
        //check if the total price is greater than 0
        if (cartModel.getTotalPrice() <= 0d)
        {
            throw new SABMMerchantSuiteCartTotalException("Cart is empty or items are free");
        }
        return processInitiateCheckoutCCTxn(cartModel,cardType);
    }

    @Override
    public String processInvoiceAuthKeyCCTxn(String authKey)
            throws
            SABMMerchantSuitePaymentErrorException, SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {
        TxnResponse response = this.sabmMerchantSuitePaymentService.initiateCheckAuthKeyTxnRequest(authKey);
        SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData = new SABMMerchantSuiteTransactionProcessData();
        populateCC(sabmMerchantSuiteTransactionProcessData,response);        
		if(Objects.nonNull(response.getReference1()) && !this.isInvoicePaidForPostback(response)){
      	  sabmMerchantSuitePaymentFacadeHelper.processInvoiceRedirect(sabmMerchantSuiteTransactionProcessData,false);
        }
        return sabmMerchantSuiteTransactionProcessData.getReference1();
    }

    @Override
    public void processCheckoutAuthKeyCCTxn(String authKey)
            throws
            SABMMerchantSuitePaymentErrorException, InvalidCartException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException, SABMMerchantSuiteWebHookPaymentAlreadyDone {
        TxnResponse response = this.sabmMerchantSuitePaymentService.initiateCheckAuthKeyTxnRequest(authKey);
        SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData = new SABMMerchantSuiteTransactionProcessData();
        populateCC(sabmMerchantSuiteTransactionProcessData,response);        
		if (Objects.nonNull(response.getEmailAddress()) && Objects.nonNull(response.getReference2()) && !this.isCartPaymentPaidforSameTransCode(response))
        {
      	  sabmMerchantSuitePaymentFacadeHelper.processCheckoutRedirect(sabmMerchantSuiteTransactionProcessData); 
        }else{
      	  throw new SABMMerchantSuiteWebHookPaymentAlreadyDone("WebHook Payemnt Already Done");
        }
    }


    @Override
    public void processInvoiceAuthKeyCCTxnForPostback(String responseString) throws SABMMerchantSuitePaymentErrorException {
        LOG.info("Creating transaction response from Json..");
        TxnResponse response = (TxnResponse) this.sabmMerchantSuitePaymentFacadeHelper.createResponseObject(responseString,TxnResponse.class);
        if(Objects.nonNull(response.getReference1()) && !this.isInvoicePaidForPostback(response))
        {
            LOG.info("Logging Webhook Request for invoice payment ref : " + response.getReference1());
            sabmDefaultMerchantSuiteTokenStrategy.createLogObject(response.getReference1(),response," Invoice Webhook for Merchant suite",new Date(),response.getEmailAddress(),response.getResponseText());
            LOG.info("Invoice : " + response.getReference1() + " :: NOT PAID :: RECORDING PAYMENT NOW..");
            SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData = new SABMMerchantSuiteTransactionProcessData();
            populateCC(sabmMerchantSuiteTransactionProcessData,response);
            sabmMerchantSuitePaymentFacadeHelper.processInvoiceRedirect(sabmMerchantSuiteTransactionProcessData,false);
            LOG.info("Sending payment confirmation email for reference ::" + response.getReference1());
            isInvoicePaid(response.getReference1());
        }
        else
        {
            LOG.info("Invoice ref : " + response.getReference1() + " ALREADY PAID...Ignoring the Postback");
        }
    }


    @Override
    public SABMMerchantSuiteTransactionProcessData processCheckoutAuthKeyCCTxnForPostback(String responseString)
            throws InvalidCartException, SABMMerchantSuitePaymentErrorException {
        TxnResponse response = (TxnResponse) this.sabmMerchantSuitePaymentFacadeHelper.createResponseObject(responseString,TxnResponse.class);
        SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData = new SABMMerchantSuiteTransactionProcessData();
        if (Objects.nonNull(response.getEmailAddress()) && Objects.nonNull(response.getReference2()) && !this.isCartPaymentApprovedForPostback(response))
        {
            LOG.info("Logging Webhook Request for checkout payment ref : " + response.getReference1());
            sabmDefaultMerchantSuiteTokenStrategy.createLogObject(response.getReference1(),response,"Checkout Webhook for Merchant suite",new Date(),response.getEmailAddress(),response.getResponseText());
            LOG.info("Checkout : " + response.getReference1() + " :: NOT PAID :: RECORDING PAYMENT NOW..");
            populateCC(sabmMerchantSuiteTransactionProcessData,response);
            sabmMerchantSuitePaymentFacadeHelper.processCheckoutRedirect(sabmMerchantSuiteTransactionProcessData);
            LOG.info("Checkout : " + response.getReference1() + " :: PAID :: Placing  order now");
        }
        else
        {
            LOG.info("Checkout ref : " + response.getReference1() + " ALREADY PAID...Ignoring the Postback");
        }
        return sabmMerchantSuiteTransactionProcessData;
    }

    private boolean isCartPaymentApprovedForPostback(TxnResponse txnResponse)
    {
        final UserModel user = userService.getUserForUID(txnResponse.getEmailAddress());
        final CartModel cartModel = commerceCartService.getCartForCodeAndUser(txnResponse.getReference2(), user);
        if (Objects.nonNull(cartModel)) {
            if (Objects.nonNull(cartModel.getPaymentInfo()) &&  cartModel.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
            {
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    public boolean isInvoicePaidForPostback(final TxnResponse txnResponse)
    {
        final UserModel user = userService.getUserForUID(txnResponse.getEmailAddress());
        final InvoicePaymentModel invoicePaymentModel = invoicePaymentDao.getInvoice(txnResponse.getReference1(), user);
        if (Objects.nonNull(invoicePaymentModel.getPaymentInfo()))
        {
            return true;
        }
        return false;
    }


    @Override
    public SABMCreditCardTransactionData processInitiateInvoiceCCTxn(final Set<String> invoices, final BigDecimal total, final String currencyIso,
            final UserModel userModel, String cardType)
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException,
            SABMSurchargeCalculationException, SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {
        LOG.info("Generating Payment Id...");
        //step 1 - generate payment id
        final String id = generatePaymentID(MerchantsuiteservicesConstants.PAYMENT_MODE.INVOICE);
        LOG.info("Generated Payment Id: "+ id);
        LOG.info("Saving Invoice Data for Payment Id: "+ id);
        //step 2 - save invoice data
        sabmMerchantSuitePaymentFacadeHelper.saveInvoiceData(invoices,total,currencyIso,userModel,id);
        SABMCreditCardTransactionData creditCardTransactionData  = new SABMCreditCardTransactionData();
        populateTransactionParameters(creditCardTransactionData,total,cardType,currencyIso,id);
        creditCardTransactionData.setInvoicePayment(true);
        creditCardTransactionData.setCartPayment(false);
        LOG.info("Creating Auth Key Request for Payment Id: "+ id);
        String auth = sabmMerchantSuitePaymentService.initiateAuthKeyRequest(creditCardTransactionData);
        LOG.info("Auth Key Generated for Payment Id: "+ id + "::::" + auth);
        creditCardTransactionData.setAuthKey(auth);
        return creditCardTransactionData;
    }

    private void populateTransactionParameters(final SABMCreditCardTransactionData creditCardTransactionData,final BigDecimal amount,String cardType,String currencyIso,String paymentReference)
            throws SABMSurchargeCalculationException {
        BigDecimal surcharge = sabmMerchantSuitePaymentFacadeHelper.calculateSurchargeForCreditCardType(CreditCardType.valueOf(cardType),amount);
        BigDecimal totalAmount = SABMMathUtils.add(amount,surcharge);
        creditCardTransactionData.setDisplayAmount(amount.toString());
        creditCardTransactionData.setDisplaySurcharge(surcharge.toString());
        creditCardTransactionData.setDisplayTotalAmount(totalAmount.toString());
        creditCardTransactionData.setSurcharge(SABMMathUtils.convertBigDecimaltoLong(surcharge));
        creditCardTransactionData.setAmount(SABMMathUtils.convertBigDecimaltoLong(amount));
        creditCardTransactionData.setTotalAmount(SABMMathUtils.convertBigDecimaltoLong(totalAmount));
        creditCardTransactionData.setDisplayAmount(SABMMathUtils.convertLongToBigDecimal(creditCardTransactionData.getAmount()).toString());
        creditCardTransactionData.setDisplaySurcharge(SABMMathUtils.convertLongToBigDecimal(creditCardTransactionData.getSurcharge()).toString());
        creditCardTransactionData.setDisplayTotalAmount(SABMMathUtils.convertLongToBigDecimal(creditCardTransactionData.getTotalAmount()).toString());
        creditCardTransactionData.setPaymentReference(paymentReference);
        creditCardTransactionData.setCurrency(currencyIso);
        creditCardTransactionData.setTestMode(configurationService.getConfiguration().getBoolean(TEST_MODE,false));
        creditCardTransactionData.setPaymentUrl(configurationService.getConfiguration().getString(BASE_URL));
    }

    @Override
    public SABMCreditCardTransactionData processInitiateCheckoutCCTxn(CartModel cartModel,String cardType)
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException,
            SABMSurchargeCalculationException, SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {
        LOG.info("Generating Payment Id...");
        //step 1 - generate payment id
        final String id = generatePaymentID(MerchantsuiteservicesConstants.PAYMENT_MODE.CHECKOUT);
        LOG.info("Generated Payment Id: "+ id);
        SABMCreditCardTransactionData creditCardTransactionData  = new SABMCreditCardTransactionData();
        //populating the request object
        populateTransactionParameters(creditCardTransactionData,new BigDecimal(cartModel.getTotalPrice()),cardType,cartModel.getCurrency().getIsocode(),id);
        //setting information for cart payments
        creditCardTransactionData.setCartPayment(true);
        creditCardTransactionData.setInvoicePayment(false);
        creditCardTransactionData.setCartCode(cartModel.getCode());
        LOG.info("Creating Auth Key Request for Payment Id: "+ id);
        //initate auth key transaction to fetch auth key
        String auth = sabmMerchantSuitePaymentService.initiateAuthKeyRequest(creditCardTransactionData);
        LOG.info("Auth Key Generated for Payment Id: "+ id + "::::" + auth);
        creditCardTransactionData.setAuthKey(auth);
        return creditCardTransactionData;
    }

    @Override
    public String processInvoiceEFTxn(final Set<String> invoices, final BigDecimal total, final String currencyIso,
            final UserModel userModel, SABMBankDetailsData bankDetailsData)
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenException,
            SABMMerchantSuitePaymentErrorException, SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException {

        LOG.info("Generating Payment Id...");
        //Step 1 : Generate Payment Id
        final String paymentId = generatePaymentID(MerchantsuiteservicesConstants.PAYMENT_MODE.INVOICE);
        LOG.info("Generated Payment Id: "+ paymentId);
        LOG.info("Saving Invoice Data for Payment Id: "+ paymentId);
        //Step 2 :Save Invoice Data
        sabmMerchantSuitePaymentFacadeHelper.saveInvoiceData(invoices,total,currencyIso,userModel,paymentId);
        LOG.info("Creating Token Request for Payment Id: "+ paymentId);
        //step 3 - Tokenize Bank Details
        String token = sabmMerchantSuitePaymentService.initiateTokenRequest(bankDetailsData);
        LOG.info("Created Token for Payment Id: "+ paymentId + ":::::" + token);
        LOG.info("Creating EFT Invoice Transaction Request for Payment Id: "+ paymentId);
        //step 3 - EFT Transaction Request
        TxnResp response = sabmMerchantSuitePaymentService.initiateInvoiceTxnRequest(total,currencyIso,paymentId,token);
        LOG.info("EFT Invoice Transaction Request for Payment Id: "+ paymentId + ":: COMPLETE");
        SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData = new SABMMerchantSuiteTransactionProcessData();
        populate(sabmMerchantSuiteTransactionProcessData,response.getTxnResponse());
        LOG.info("Processing  Invoice Redirect Request"+ paymentId);
        sabmMerchantSuitePaymentFacadeHelper.processInvoiceRedirect(sabmMerchantSuiteTransactionProcessData,true);
        return paymentId;
    }

    void  populate(SABMMerchantSuiteTransactionProcessData target,TxnResponse source)
    {
        try {
            BeanUtils.copyProperties(target,source);
        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }
        MaskedBankAccountDetails bankAccountDetails = source.getBankAccountDetails();
        target.setBsbNumber(bankAccountDetails.getBSBNumber());
        target.setAccountName(bankAccountDetails.getAccountName());
        target.setAccountNumber(bankAccountDetails.getAccountNumber());

    }

    void populateCC(SABMMerchantSuiteTransactionProcessData target,TxnResponse source)
    {
        try {
            BeanUtils.copyProperties(target,source);
        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {
        }
        MaskedCreditCardDetails creditCardDetails = source.getCardDetails();
        target.setCardHolderName(creditCardDetails.getCardHolderName());
        target.setMaskedCardNumber(creditCardDetails.getMaskedCardNumber());
        target.setExpiryDate(creditCardDetails.getExpiryDate());
    }

    @Override
    public InvoicePaymentData getInvoice(final String trackingNumber)
    {
        final UserModel user = userService.getCurrentUser();
        final InvoicePaymentModel invoice = invoicePaymentDao.getInvoice(trackingNumber, user);

        if (invoice != null)
        {
            return invoicePaymentConverter.convert(invoice);
        }
        return new InvoicePaymentData();
    }

    @Override
    public boolean isCartPaymentApproved()
    {
        if (cartService.hasSessionCart())
        {
            final CartModel cartModel = cartService.getSessionCart();
            if (cartModel.getPaymentInfo() instanceof CreditCardPaymentInfoModel
                    && CollectionUtils.isNotEmpty(cartModel.getPaymentTransactions()))
            {

                for (final PaymentTransactionModel transactionModel : cartModel.getPaymentTransactions())
                {
                    for (final PaymentTransactionEntryModel entryModel : transactionModel.getEntries())
                    {
                        if (TransactionStatus.ACCEPTED.name().equals(entryModel.getTransactionStatus()))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public Converter<InvoicePaymentModel, InvoicePaymentData> getInvoicePaymentConverter() {
        return invoicePaymentConverter;
    }

    public void setInvoicePaymentConverter(
            final Converter<InvoicePaymentModel, InvoicePaymentData> invoicePaymentConverter) {
        this.invoicePaymentConverter = invoicePaymentConverter;
    }

    @Override
    public List<SABMCreditCardValidationData> fetchCreditCardValidationData()
    {
        return sabmMerchantSuitePaymentFacadeHelper.fetchCreditCardValidationData();
    }
    protected String generatePaymentID(final MerchantsuiteservicesConstants.PAYMENT_MODE payment_mode)
    {
        return payment_mode.getCode() + keyGenerator.generate();
    }
	
	private boolean isCartPaymentPaidforSameTransCode(TxnResponse txnResponse)
    {
        final UserModel user = userService.getUserForUID(txnResponse.getEmailAddress());
        final CartModel cartModel = commerceCartService.getCartForCodeAndUser(txnResponse.getReference2(), user);
        if (Objects.nonNull(cartModel)) {
            if (Objects.nonNull(cartModel.getPaymentInfo()) &&  cartModel.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
            {
               if(Objects.nonNull(txnResponse.getReference1()) && ((CreditCardPaymentInfoModel)cartModel.getPaymentInfo()).getPaymentReference().equalsIgnoreCase(txnResponse.getReference1())) 
            	{
               	return true;
            	}
               else{
               	return false;
               }
            }
            else {
                return false;
            }
        }
        return true;
    }
}