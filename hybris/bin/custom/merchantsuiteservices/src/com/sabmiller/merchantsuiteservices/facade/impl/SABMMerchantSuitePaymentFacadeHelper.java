package com.sabmiller.merchantsuiteservices.facade.impl;

import de.hybris.platform.commerceservices.order.dao.impl.DefaultCommerceCartDao;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.common.collect.Lists;
import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import com.sabmiller.commons.utils.SABMMathUtils;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.SABMMerchantSuitePaymentErrorType;
import com.sabmiller.core.event.PaymentConfirmationEmailEvent;
import com.sabmiller.core.model.InvoicePaymentDetailModel;
import com.sabmiller.core.model.InvoicePaymentModel;
import com.sabmiller.core.model.SabmCardTypeConfigurationModel;
import com.sabmiller.core.salesordercreate.service.SABMSalesOrderCreateService;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.invoice.SABMInvoiceData;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardValidationData;
import com.sabmiller.facades.ysdm.data.YSDMRequest;
import com.sabmiller.merchantsuiteservices.constants.MerchantsuiteservicesConstants;
import com.sabmiller.merchantsuiteservices.dao.InvoicePaymentDao;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTransactionProcessData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.exception.SABMSurchargeCalculationException;
import com.sabmiller.merchantsuiteservices.strategy.SABMMerchantSuitePaymentTypePersistenceStategy;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;

/**
 * Helper Class for Merchant Suite Facade
 * @author akshay.a.malik
 */
public class SABMMerchantSuitePaymentFacadeHelper {

    @Resource
    ModelService modelService;

    @Resource
    private CurrencyDao currencyDao;

    @Resource
    private UserService userService;

    @Resource
    private DefaultCommerceCartDao commerceCartDao;

    @Resource
    private SystemEmailService sabmSystemEmailService;

    @Resource(name = "sabmSalesOrderCreateService")
    private SABMSalesOrderCreateService salesOrderCreateService;

    @Resource(name = "salesOrderYSDMConverter")
    private Converter<AbstractOrderModel, YSDMRequest> salesOrderYSDMConverter;

    @Resource(name = "invoiceYSDMConverter")
    private Converter<InvoicePaymentModel, YSDMRequest> invoiceYSDMConverter;

    @Resource(name = "baseStoreService")
    private BaseStoreService baseStoreService;

    @Resource(name = "baseSiteService")
    private BaseSiteService baseSiteService;

    @Resource(name = "commonI18NService")
    private CommonI18NService commonI18NService;

    @Resource(name = "eventService")
    private EventService eventService;

    @Resource
    private SessionService sessionService;

    @Resource
    private EnumerationService enumerationService;

    @Resource
    private InvoicePaymentDao sabminvoicePaymentDao;

    private final static Logger LOG = LoggerFactory.getLogger(SABMMerchantSuitePaymentFacadeHelper.class.getName());

    private static final String APPROVED_RESPONSE_CODE = "0";

    private Map<String,SABMMerchantSuitePaymentTypePersistenceStategy> strategyMap;

    private static final String PAYMENT_PROVIDER="Merchant Suite";

    private static final String COMMA=",";

    private String invalidCardErrorBankResponseCode;
    private String declinedErrorCodes;
    private String gatewayErrorCodes;
    private String invalidCardErrorCode;
    private String expiredCardErrorCode;
    private String insufficientFundsErrorCode;
    private String invalidExpiryErrorCode;

    /**
     * Method to save invoice data
     * @param invoices
     * @param total
     * @param currencyIso
     * @param userModel
     * @param trackingNumber
     * @return
     */
    protected InvoicePaymentModel saveInvoiceData(final Set<String> invoices, final BigDecimal total, final String currencyIso,
            final UserModel userModel, final String trackingNumber)
    {
        final InvoicePaymentModel invoicePaymentModel = modelService.create(InvoicePaymentModel._TYPECODE);
        invoicePaymentModel.setPaymentCode(trackingNumber);
        invoicePaymentModel.setAmount(total);
        invoicePaymentModel.setUser(userModel);
        invoicePaymentModel.setInvoices(Lists.newArrayList(invoices));
        final List<CurrencyModel> list = currencyDao.findCurrenciesByCode(currencyIso);
        if (CollectionUtils.isNotEmpty(list))
        {
            invoicePaymentModel.setCurrency(list.get(0));
        }
        modelService.save(invoicePaymentModel);
        return invoicePaymentModel;
    }

    public void createInvoiceDetail(final InvoicePaymentModel invoice)
    {
        LOG.info("Creating invoice detail for invoice " + invoice.getPaymentCode());
        if (Objects.nonNull(invoice) && CollectionUtils.isNotEmpty(invoice.getInvoices()) ) {
            if (CollectionUtils.isEmpty(invoice.getInvoicesDetail())) {
                Map<String, SABMInvoiceData> invoiceMap = new HashMap<>();
                if (null != sessionService.getAttribute(SabmCoreConstants.SESSION_B2BUNIT_INVOICES_MAP)) {
                    invoiceMap = sessionService.getAttribute(SabmCoreConstants.SESSION_B2BUNIT_INVOICES_MAP);
                    LOG.info("Session map for creating invoice detail no empty : " + invoiceMap.size());
                }
                else{
                    LOG.info("Session map for creating invoice detail is empty : " + invoiceMap.size());
                }
                final List<InvoicePaymentDetailModel> list = new ArrayList<>();
                for (final String inv : invoice.getInvoices()) {
                    LOG.info("Comparing invoice map with invoice : Checking " + inv);
                    if (invoiceMap.containsKey(inv)) {
                        LOG.info("Comparing invoice map with invoice : Succesfull " + inv);
                        final SABMInvoiceData sabmInvoiceData = invoiceMap.get(inv);
                        final InvoicePaymentDetailModel model = modelService.create(InvoicePaymentDetailModel._TYPECODE);
                        model.setInvoiceNumber(sabmInvoiceData.getInvoiceNumber());
                        model.setInvoiceTrackingNumber(invoice.getPaymentCode());
                        model.setPurchaseOrderNumber(sabmInvoiceData.getPurchaseOrderNumber());
                        model.setAmount(sabmInvoiceData.getOpenAmount());
                        model.setType(sabmInvoiceData.getType());
                        list.add(model);
                    }
                    else{
                        LOG.info("Comparing invoice map with invoice : Not Found " + inv);
                    }
                }
                invoice.setInvoicesDetail(list);
                modelService.saveAll(invoice);
                LOG.info("Removing Session Attribute for Invoice Map");
            }
        }
    }

    /**
     * Create Payment Confirmation Email Event to send Payment Confirmation Email
     * @param invoice
     */
    public void createPaymentConfirmationEmailEvent(final InvoicePaymentModel invoice) {
        final PaymentConfirmationEmailEvent event = new PaymentConfirmationEmailEvent();
        event.setInvoicePayment(invoice);
        event.setBaseStore(baseStoreService.getCurrentBaseStore());
        event.setSite(baseSiteService.getCurrentBaseSite());
        event.setCustomer((CustomerModel) userService.getCurrentUser());
        event.setLanguage(commonI18NService.getCurrentLanguage());
        event.setCurrency(commonI18NService.getCurrentCurrency());
        eventService.publishEvent(event);
    }

    /**
     * Process Invoice Redirect
     * 1 - Check if payment is approved
     * 2 - Send Confirmation Email to the Customer
     * 3 - create invoice payment info
     * 4 - send payment notification to credit team
     * 5 - save invoice redirect
     * 6 - Initiating YSDM call to SAP
     * @param responseData
     * @param bankPayments
     * @return
     * @throws SABMMerchantSuitePaymentErrorException
     */
    protected boolean processInvoiceRedirect(final SABMMerchantSuiteTransactionProcessData responseData,Boolean bankPayments)
            throws SABMMerchantSuitePaymentErrorException {

        LOG.info("Checking if Payment is approved for invoice  tracking [{}]", responseData.getReference1() + "Response code : " +
                responseData.getResponseCode() + "with response text :" + responseData.getResponseText() +"    Bank Response Code : " + responseData.getBankResponseCode());
        //validate if payment is approved
        if (isApproved(responseData,bankPayments))
        {
            LOG.info("Payment approved for invoice tracking [{}]", responseData.getReference1());
            LOG.info("Fetching invoice for invoice tracking [{}]", responseData.getReference1());
            //fetch invoice payment model in the system
            final InvoicePaymentModel invoicePaymentModel = sabminvoicePaymentDao.getInvoice(responseData.getReference1());
            //null check
            if (null != invoicePaymentModel) {
                LOG.info("Creating invoice payment info for invoice tracking [{}]", responseData.getReference1());
                //create invoice payment info and attach the payment info to invoice payment model
                createInvoicePaymentInfo(invoicePaymentModel, responseData);

                LOG.info("Creating payment transaction for invoice tracking [{}]", responseData.getReference1());
                //save invoice redirect
                saveInvoiceRedirect(invoicePaymentModel, responseData);

                LOG.info("Creating invoice detail for invoice tracking [{}]", responseData.getReference1());
                //creating invoice details
                createInvoiceDetail(invoicePaymentModel);

                LOG.info("Sending payment notification for invoice tracking [{}]", responseData.getReference1());
                //send payment notification to credit team
                sendPaymentNotification(responseData, invoicePaymentModel);

				if(invoicePaymentModel.getPaymentInfo() instanceof CreditCardPaymentInfoModel){
                LOG.info("Initiating YSDM call to SAP...");
                salesOrderCreateService.createYSDMOrderInSAP(invoiceYSDMConverter.convert(invoicePaymentModel));
				}
                return true;
            }
            else
            {
                LOG.error("Invoice [{}] not found for user [{}]. Failed to approve payment.", responseData.getReference2(),
                        responseData.getReference3());
                throw new SABMMerchantSuitePaymentErrorException("Invoice not found: " + responseData.getReference2());
            }
        }
        else
        {
            LOG.warn("Payment declined for Invoice [{}], response code [{}], reason [{}]", responseData.getReference2(),
                    responseData.getResponseCode(), responseData.getResponseText());
            throw new SABMMerchantSuitePaymentErrorException("Payment Declines for reference" + responseData.getReference1());
        }
    }

    /**
     * Method to process checkout redirect
     *
     * 1 - validate if payment is approved
     * 2 - Search for cart and user
     * 3 - create payment info
     * 4 - save checkout redirect
     * 5 - send payment notification to Credit Team
     * 6 - Initiating YSDM call to SAP
     *
     * @param responseData
     * @return
     * @throws SABMMerchantSuitePaymentErrorException
     * @throws InvalidCartException
     */
    protected boolean processCheckoutRedirect(final SABMMerchantSuiteTransactionProcessData responseData)
            throws SABMMerchantSuitePaymentErrorException, InvalidCartException {
        try
        {
            LOG.info("Checking if Payment is approved for invoice  tracking [{}]", responseData.getReference1());
            //validate if payment is approved
            if (isApproved(responseData,false))
            {
                final UserModel user = userService.getUserForUID(responseData.getEmailAddress());
                final CartModel cartModel = commerceCartDao.getCartForCodeAndUser(responseData.getReference2(), user);
                LOG.info("Payment approved for cart [{}]", responseData.getReference2());
                LOG.info("Creating payment info for cart [{}]", responseData.getReference2());

                //create payment info
                createCartPaymentInfo(cartModel,responseData);
                if (cartModel != null)
                {
                    LOG.info("Creating payment transactions for cart [{}]", responseData.getReference2());
                    //save checkout redirect
                    saveCheckoutRedirect(cartModel, responseData);
                    LOG.info("Sending payment notification for cart [{}]", responseData.getReference2());
                    //send payment notification to Credit Team
                    sendPaymentNotification(responseData, null);
					if((cartModel.getPaymentInfo() instanceof CreditCardPaymentInfoModel)) {
                    LOG.info("Initiating YSDM call to SAP...");
                    salesOrderCreateService.createYSDMOrderInSAP(salesOrderYSDMConverter.convert(cartModel));
					}
                    return true;
                }
                else
                {
                    LOG.error("Cart [{}] not found for user [{}]. Failed to approve payment.", responseData.getReference1(),
                            responseData.getReference3());
                    throw new InvalidCartException("Cart not found");
                }
            }
        }
        catch (final UnknownIdentifierException | IllegalArgumentException e)
        {
            LOG.error("User [{}] not found for merchant suite payment reference", responseData.getReference1());
            throw new SABMMerchantSuitePaymentErrorException("Unable to find user for ", e);
        }
        return false;
    }

    /**
     * Create cart payment info
     * @param cartModel
     * @param sabmMerchantSuiteTransactionProcessData
     * @return
     * @throws SABMMerchantSuitePaymentErrorException
     */
    public PaymentInfoModel createCartPaymentInfo(final CartModel cartModel,
            final SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData)
            throws SABMMerchantSuitePaymentErrorException {
        final PaymentInfoModel paymentInfoModel = createPaymentInfo(sabmMerchantSuiteTransactionProcessData, cartModel.getUser());
        cartModel.setPaymentInfo(paymentInfoModel);
        cartModel.setCartCode(cartModel.getCode());
        try
        {
            modelService.saveAll(paymentInfoModel, cartModel);
        }
        catch (final ModelSavingException e)
        {
            LOG.error("Error saving payment details");
            throw new SABMMerchantSuitePaymentErrorException("Error saving payment details", e);
        }
        return paymentInfoModel;
    }

    /**
     * Create Payment Info
     * @param invoicePaymentModel
     * @param sabmMerchantSuiteTransactionProcessData
     * @return
     * @throws SABMMerchantSuitePaymentErrorException
     */
    public PaymentInfoModel createInvoicePaymentInfo(final InvoicePaymentModel invoicePaymentModel,
            final SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData)
            throws SABMMerchantSuitePaymentErrorException {
        //creating payment info according to the transaction type
        final PaymentInfoModel paymentInfoModel = createPaymentInfo(sabmMerchantSuiteTransactionProcessData, invoicePaymentModel.getUser());
        invoicePaymentModel.setPaymentInfo(paymentInfoModel);
        try
        {
            modelService.saveAll(invoicePaymentModel);
            modelService.refresh(paymentInfoModel);
            modelService.refresh(invoicePaymentModel);
        }
        catch (final ModelSavingException e)
        {
            LOG.error("Error saving payment details");
            throw new SABMMerchantSuitePaymentErrorException("Error saving payment details", e);
        }
        return paymentInfoModel;
    }

    /**
     * Method to save invoice redirect
     * @param invoicePaymentModel
     * @param responseData
     * @throws SABMMerchantSuitePaymentErrorException
     */
    public void saveInvoiceRedirect(final InvoicePaymentModel invoicePaymentModel,
            final SABMMerchantSuiteTransactionProcessData responseData) throws SABMMerchantSuitePaymentErrorException {
        PaymentTransactionModel paymentTransactionModel = null;
        if (invoicePaymentModel != null)
        {
            paymentTransactionModel = createPaymentTransaction(responseData,
                        invoicePaymentModel.getPaymentInfo(), invoicePaymentModel.getCurrency());
        }
        try
        {
            invoicePaymentModel.setTransaction(paymentTransactionModel);
            invoicePaymentModel.setPaid(true);
            modelService.saveAll(invoicePaymentModel.getTransaction(), invoicePaymentModel);
        }
        catch (final ModelSavingException e)
        {
            LOG.error("Error saving payment details");
            throw new SABMMerchantSuitePaymentErrorException("Error saving payment details", e);
        }
    }

    /**
     *
     * @param cartModel
     * @param responseData
     * @throws SABMMerchantSuitePaymentErrorException
     */
    public void saveCheckoutRedirect(final CartModel cartModel, final SABMMerchantSuiteTransactionProcessData responseData)
            throws SABMMerchantSuitePaymentErrorException {

        final PaymentTransactionModel paymentTransaction = createPaymentTransaction(responseData,
                cartModel.getPaymentInfo(),cartModel.getCurrency());
        paymentTransaction.setOrder(cartModel);
        cartModel.setPaymentStatus(PaymentStatus.PAID);
        try
        {
            modelService.saveAll(paymentTransaction,paymentTransaction,cartModel);
        }
        catch (final ModelSavingException e)
        {
            LOG.error("Error saving payment details");
            throw new SABMMerchantSuitePaymentErrorException("Error saving payment details", e);
        }
    }

    /**
     * Create Payment Transaction
     *
     * @param responseData
     * @param paymentInfoModel
     * @param currencyModel
     * @return
     */
    protected PaymentTransactionModel createPaymentTransaction(final SABMMerchantSuiteTransactionProcessData responseData, final PaymentInfoModel paymentInfoModel,CurrencyModel currencyModel)
    {
        //Add payment transaction
        final PaymentTransactionModel transactionModel = modelService.create(PaymentTransactionModel._TYPECODE);
        transactionModel.setCode(responseData.getReference1());
        transactionModel.setCurrency(currencyModel);
        transactionModel.setInfo(paymentInfoModel);
        transactionModel.setPaymentProvider(PAYMENT_PROVIDER);
        transactionModel.setPlannedAmount(BigDecimal.valueOf(responseData.getAmount()).divide(new BigDecimal(100)));
        //create payment transaction entry for actual amount
        final PaymentTransactionEntryModel entryModel = createPaymentTransactionEntry(responseData, BigDecimal.valueOf(responseData.getAmountOriginal()).divide(new BigDecimal(100))
                , currencyModel,PaymentTransactionType.CAPTURE);
        //create payment transaction entry for surcharge
        final PaymentTransactionEntryModel surchargeEntryModel = createPaymentTransactionEntry(responseData, BigDecimal.valueOf(responseData.getAmountSurcharge()).divide(new BigDecimal(100))
                , currencyModel,PaymentTransactionType.SURCHARGE);
        surchargeEntryModel.setPaymentTransaction(transactionModel);
        entryModel.setPaymentTransaction(transactionModel);
        modelService.saveAll(entryModel,surchargeEntryModel);
        return transactionModel;
    }

    /**
     * Method to create pAyment transaction entry
     * @param responseData
     * @param total
     * @param currencyModel
     * @param paymentTransactionType
     * @return
     */
    protected PaymentTransactionEntryModel createPaymentTransactionEntry(final SABMMerchantSuiteTransactionProcessData responseData, final BigDecimal total,
            final CurrencyModel currencyModel,PaymentTransactionType paymentTransactionType)
    {
        final Date date = Calendar.getInstance().getTime();
        //Add payment transaction info
        final PaymentTransactionEntryModel entryModel = modelService.create(PaymentTransactionEntryModel._TYPECODE);
        entryModel.setCode(responseData.getReference1() + "-" + UUID.randomUUID());
        entryModel.setAmount(total);
        entryModel.setTime(date);
        entryModel.setCurrency(currencyModel);
        entryModel.setRequestToken(responseData.getReference1());
        entryModel.setRequestId(responseData.getReceiptNumber());
        entryModel.setTransactionStatus(TransactionStatus.ACCEPTED.name());
        entryModel.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
        entryModel.setType(paymentTransactionType);
        entryModel.setResponseCode(responseData.getResponseCode());
        modelService.save(entryModel);
        return entryModel;
    }

    /**
     * Method to check if the payment is approved based on API resonse code and Bank Response code
     * @param sabmMerchantSuiteTransactionProcessData
     * @param bankPayments
     * @return
     * @throws SABMMerchantSuitePaymentErrorException
     */
    public boolean isApproved(SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData,Boolean bankPayments)
            throws SABMMerchantSuitePaymentErrorException {


        //bank account response check
        if (bankPayments && sabmMerchantSuiteTransactionProcessData.getResponseCode().equals(APPROVED_RESPONSE_CODE))
        {
            return true;
        }

        //cc response check
        else if (sabmMerchantSuiteTransactionProcessData.getResponseCode().equals(APPROVED_RESPONSE_CODE)) {
            return true;
        }

        //Insufficient funds exception
        else if (SabmStringUtils.splitStringAndReturnList(insufficientFundsErrorCode, COMMA).contains(sabmMerchantSuiteTransactionProcessData.getBankResponseCode())){
            LOG.info("Insufficient Funds for payment reference : " + sabmMerchantSuiteTransactionProcessData.getReference1());
            throw new SABMMerchantSuitePaymentErrorException("Merchant Suite Payment Error :",enumerationService.getEnumerationName(SABMMerchantSuitePaymentErrorType.NO_FUNDS));
        }

        //Expired card exception
        else if (SabmStringUtils.splitStringAndReturnList(expiredCardErrorCode, COMMA).contains(sabmMerchantSuiteTransactionProcessData.getBankResponseCode())){
            LOG.info("Expired Card for payment reference : " + sabmMerchantSuiteTransactionProcessData.getReference1());
            throw new SABMMerchantSuitePaymentErrorException("Merchant Suite Payment Error :",enumerationService.getEnumerationName(SABMMerchantSuitePaymentErrorType.EXPIRED_CARD));
        }

        //invalid card error check
        else if (SabmStringUtils.splitStringAndReturnList(invalidCardErrorBankResponseCode, COMMA).contains(sabmMerchantSuiteTransactionProcessData.getBankResponseCode())){
            LOG.info("Invalid Card for payment reference : " + sabmMerchantSuiteTransactionProcessData.getReference1());
            throw new SABMMerchantSuitePaymentErrorException("Merchant Suite Payment Error :",enumerationService.getEnumerationName(SABMMerchantSuitePaymentErrorType.INVALID_CARD));
        }


        else if (SabmStringUtils.splitStringAndReturnList(declinedErrorCodes, COMMA).contains(sabmMerchantSuiteTransactionProcessData.getResponseCode())) {
                LOG.info("Payment Error : Payment Declined for payment reference : " + sabmMerchantSuiteTransactionProcessData.getReference1());
                throw new SABMMerchantSuitePaymentErrorException("Merchant Suite Payment Error :",enumerationService.getEnumerationName(SABMMerchantSuitePaymentErrorType.DECLINED));
            }

            //gateway error check
        else if (SabmStringUtils.splitStringAndReturnList(gatewayErrorCodes, COMMA).contains(sabmMerchantSuiteTransactionProcessData.getResponseCode())){
                LOG.info("Gateway Error for payment reference : " + sabmMerchantSuiteTransactionProcessData.getReference1());
                throw new SABMMerchantSuitePaymentErrorException("Merchant Suite Payment Error :",enumerationService.getEnumerationName(SABMMerchantSuitePaymentErrorType.GATEWAY_ERROR));
            }

            //invalid card error check
        else if (SabmStringUtils.splitStringAndReturnList(invalidCardErrorCode, COMMA).contains(sabmMerchantSuiteTransactionProcessData.getResponseCode())){
                LOG.info("Invalid Card for payment reference : " + sabmMerchantSuiteTransactionProcessData.getReference1());
                throw new SABMMerchantSuitePaymentErrorException("Merchant Suite Payment Error :",enumerationService.getEnumerationName(SABMMerchantSuitePaymentErrorType.INVALID_CARD));
            }

            //Invalid expiry exception
        else if (SabmStringUtils.splitStringAndReturnList(invalidExpiryErrorCode, COMMA).contains(sabmMerchantSuiteTransactionProcessData.getResponseCode())){
                LOG.info("Invalid Expiry for payment reference : " + sabmMerchantSuiteTransactionProcessData.getReference1());
                throw new SABMMerchantSuitePaymentErrorException("Merchant Suite Payment Error :",enumerationService.getEnumerationName(SABMMerchantSuitePaymentErrorType.INVALID_EXPIRY));
            }

            //unkwown error
        else {
                LOG.info("Unknown error for payment reference : " + sabmMerchantSuiteTransactionProcessData.getReference1());
                throw new SABMMerchantSuitePaymentErrorException("Merchant Suite Payment Error : Unknown/unMapped error occurred",enumerationService.getEnumerationName(SABMMerchantSuitePaymentErrorType.INVALID_EXPIRY));
            }
    }

    /**
     * Method to create payment info
     * @param sabmMerchantSuiteTransactionProcessData
     * @param user
     * @return
     * @exception  SABMMerchantSuitePaymentErrorException
     */
    protected PaymentInfoModel createPaymentInfo(SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData, final UserModel user)
            throws SABMMerchantSuitePaymentErrorException {
        //if bsb number present , then create invoice payment info
        if (StringUtils.isNotEmpty(sabmMerchantSuiteTransactionProcessData.getBsbNumber()))
        {
            return strategyMap.get(MerchantsuiteservicesConstants.PAYMENT_METHOD.BANKTRANSFER).createPaymentInfo(sabmMerchantSuiteTransactionProcessData,user);
        }
        //if masked card number present , then create debit payment info
        else if (StringUtils.isNotEmpty(sabmMerchantSuiteTransactionProcessData.getMaskedCardNumber())){
            return strategyMap.get(MerchantsuiteservicesConstants.PAYMENT_METHOD.CREDITCARD).createPaymentInfo(sabmMerchantSuiteTransactionProcessData, user);
        }
        //unkwown error
        else {
            throw new SABMMerchantSuitePaymentErrorException("Debit info or bank info not found with request data");
        }
    }


    /**
     * Sends email to CUB notifying of a payment capture
     *
     * @param responseData
     *           westpac payment postback data
     */
    protected void sendPaymentNotification(final SABMMerchantSuiteTransactionProcessData responseData, final InvoicePaymentModel invoicePaymentModel)
    {
        try
        {
            final List<String> message = Lists.newArrayList("Payment capture successful!");
            String transactionType;
            final StringBuilder invoices = new StringBuilder();
            if (invoicePaymentModel != null)
            {
                transactionType = "Invoice";
                final Iterator<String> iterator = invoicePaymentModel.getInvoices().iterator();
                while (iterator.hasNext())
                {
                    invoices.append(iterator.next());
                    if (iterator.hasNext())
                    {
                        invoices.append(", ");
                    }
                }
            }
            else
            {
                transactionType = "Checkout";
            }

            final SimpleDateFormat formatOut = new SimpleDateFormat("dd-MM-yyyy");
            String dateStr = responseData.getProcessedDateTime();
            if (StringUtils.isNotBlank(responseData.getProcessedDateTime()))
            {
                try
                {
                    final Date date = SabmDateUtils.toCalendar(dateStr).getTime();
                    dateStr = formatOut.format(date);
                }
                catch (final ParseException e)
                {
                    LOG.warn("Merchant Suite date format is not according to specs - [{}]", responseData.getProcessedDateTime());
                }
            }
            message.add(" ");
            message.add("Customer ref. number: " + responseData.getReference3());
            message.add("Date: " + dateStr);
            message.add("Payment amount: " + SABMMathUtils.convertLongToBigDecimal(responseData.getAmountOriginal()));
            message.add("Surcharge amount: "
                    + getStoredSurchargeAmount(responseData.getEmailAddress(), responseData.getReference2(), invoicePaymentModel));
            message.add("Transaction type: " + transactionType);
            message.add(" ");
            message.add("Payment details");
            message.add("ReceiptNumber: " + responseData.getReceiptNumber());
            message.add("Payment ref. number: " + responseData.getReference1());

            if (StringUtils.isNotEmpty(responseData.getBsbNumber()))
            {
                message.add("Payment type: EFT");
            }
            else
            {
                message.add("Payment type: Credit Card");
            }

            if (StringUtils.isEmpty(responseData.getBsbNumber()))
            {
                message.add("Card type: " + responseData.getCardType());
            }
            else
            {
                message.add("Account Name: " + responseData.getAccountName());
                message.add("Account Number: " + responseData.getAccountNumber());
                message.add("BSB: " + responseData.getBsbNumber());
            }

          //  message.add("Summary code: " + responseData.get);

            message.add("Response code: " + responseData.getResponseCode());
            message.add("Response description: " + responseData.getResponseText());

            message.add(" ");
            message.add("Invoice(s) paid: " + invoices.toString());
            BigDecimal bg1 = SABMMathUtils.convertLongToBigDecimal(responseData.getAmountOriginal());
			BigDecimal bg2 = new BigDecimal(getStoredSurchargeAmount(responseData.getEmailAddress(), responseData.getReference2(), invoicePaymentModel));
			BigDecimal bg3 = bg1.add(bg2);
			SystemEmailMessageModel email = sabmSystemEmailService.constructSystemEmail(
					Config.getString("merchant.suite.email.payment.from", ""), Config.getString("merchant.suite.email.payment.to", ""),
					Config.getString("merchant.email.payment.displayName", ""), "<" + transactionType + "> payment for customer <"
							+ responseData.getReference3() + "> on <" + dateStr + "> for <$" + bg3.setScale(2).toString() + "> ",
					message, null);
            sabmSystemEmailService.send(email);
        }
        catch (final Exception e)
        {
            LOG.error("Error sending payment alert email to CUB", e);
        }
    }

    protected String getStoredSurchargeAmount(final String UID, final String customBasket,
            final InvoicePaymentModel invoicePaymentModel)
    {
        List<PaymentTransactionModel> transactions = null;
        if (invoicePaymentModel != null)
        {
            final PaymentTransactionModel transaction = invoicePaymentModel.getTransaction();

            if (transaction != null)
            {
                transactions = new ArrayList<PaymentTransactionModel>();
                transactions.add(transaction);
            }
        }
        else
        {
            final UserModel user = userService.getUserForUID(UID);
            final CartModel cartModel = commerceCartDao.getCartForCodeAndUser(customBasket, user);
            transactions = cartModel.getPaymentTransactions();
        }
        if (CollectionUtils.isNotEmpty(transactions))
        {
            for (final PaymentTransactionModel transaction : transactions)
            {
                for (final PaymentTransactionEntryModel entry : transaction.getEntries())
                {
                    if (PaymentTransactionType.SURCHARGE.equals(entry.getType())
                            && (StringUtils.equalsIgnoreCase(TransactionStatus.ACCEPTED.name(), entry.getTransactionStatus())
                            || StringUtils.equalsIgnoreCase(TransactionStatus.REVIEW.name(), entry.getTransactionStatus())))
                    {
                        return entry.getAmount().setScale(2).toString();
                    }
                }
            }
        }
        return "";
    }

    /**
     * Method to calculate surcharge  for credit card type
     * @param creditCardType
     * @param amount
     * @return
     * @throws SABMSurchargeCalculationException
     */
    public  BigDecimal calculateSurchargeForCreditCardType(CreditCardType creditCardType , BigDecimal amount)
            throws SABMSurchargeCalculationException {
        BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
        if (MapUtils.isNotEmpty(baseStoreModel.getSurchargeMap()))
        {
            Map<CreditCardType,BigDecimal> map = baseStoreModel.getSurchargeMap();
            if (map.containsKey(creditCardType))
            {
                BigDecimal surcharge = SABMMathUtils.percentage(amount,map.get(creditCardType));
                return SABMMathUtils.round(surcharge,2);
            }
           else {
                throw new SABMSurchargeCalculationException("No Matching Configuration found for Card Type : " + creditCardType + "for base store : " + baseStoreModel.getUid());
            }

        }
        else {
            throw new SABMSurchargeCalculationException("No Configuration for Surcharge exist for Base Store: " + baseStoreModel.getUid());
        }
    }

    /**
     * Fetch credit card validation data
     * @return
     */
    public  List<SABMCreditCardValidationData> fetchCreditCardValidationData()
    {
        List<SABMCreditCardValidationData> creditCardValidationDataList = new ArrayList<>();
        BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
        Map<CreditCardType, SabmCardTypeConfigurationModel> cardTypeToConfig = currentBaseStore.getCardTypeToConfig();
        if (MapUtils.isNotEmpty(cardTypeToConfig)) {
            cardTypeToConfig.forEach((k, v) -> {
                SABMCreditCardValidationData creditCardValidationData = new SABMCreditCardValidationData();
                creditCardValidationData.setCardType(k.toString());
                creditCardValidationData.setDigitValidation(v.getDigitsValidation());
                creditCardValidationData.setLengthValidation(v.getLengthValidation());
                creditCardValidationData.setSeriesValidation(v.getSeriesValidation());
                creditCardValidationData.setCvvValidation(v.getCvvValidation());
                creditCardValidationDataList.add(creditCardValidationData);
            });
        }
        return creditCardValidationDataList;
    }

    public Object createResponseObject(String response, Class txnResponse) {
        JaxbAnnotationModule var4 = new JaxbAnnotationModule();
        ObjectMapper var5 = new ObjectMapper();
        Object var6 = null;
        var5.registerModule(var4);
        var5.enable(SerializationFeature.INDENT_OUTPUT);
        var5.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //var5.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);

        try {
            var6 = var5.readValue(response, txnResponse);
        } catch (Exception var10) {
            var10.printStackTrace();
        }
        return var6;
    }

    public Map<String, SABMMerchantSuitePaymentTypePersistenceStategy> getStrategyMap() {
        return strategyMap;
    }

    public void setStrategyMap(
            final Map<String, SABMMerchantSuitePaymentTypePersistenceStategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public void setDeclinedErrorCodes(String declinedErrorCodes) {
        this.declinedErrorCodes = declinedErrorCodes;
    }

    public String getDeclinedErrorCodes() {
        return declinedErrorCodes;
    }

    public void setGatewayErrorCodes(String gatewayErrorCodes) {
        this.gatewayErrorCodes = gatewayErrorCodes;
    }

    public String getGatewayErrorCodes() {
        return gatewayErrorCodes;
    }

    public void setInvalidCardErrorCode(String invalidCardErrorCode) {
        this.invalidCardErrorCode = invalidCardErrorCode;
    }

    public String getInvalidCardErrorCode() {
        return invalidCardErrorCode;
    }

    public void setInvalidCardErrorBankResponseCode(String invalidCardErrorBankResponseCode) {
        this.invalidCardErrorBankResponseCode = invalidCardErrorBankResponseCode;
    }

    public String getInvalidCardErrorBankResponseCode() {
        return invalidCardErrorBankResponseCode;
    }

    public void setExpiredCardErrorCode(String expiredCardErrorCode) {
        this.expiredCardErrorCode = expiredCardErrorCode;
    }

    public String getExpiredCardErrorCode() {
        return expiredCardErrorCode;
    }

    public void setInsufficientFundsErrorCode(String insufficientFundsErrorCode) {
        this.insufficientFundsErrorCode = insufficientFundsErrorCode;
    }

    public String getInsufficientFundsErrorCode() {
        return insufficientFundsErrorCode;
    }

    public void setInvalidExpiryErrorCode(String invalidExpiryErrorCode) {
        this.invalidExpiryErrorCode = invalidExpiryErrorCode;
    }

    public String getInvalidExpiryErrorCode() {
        return invalidExpiryErrorCode;
    }
}

