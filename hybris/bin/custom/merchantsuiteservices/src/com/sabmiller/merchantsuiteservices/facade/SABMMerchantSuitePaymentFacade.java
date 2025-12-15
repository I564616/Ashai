package com.sabmiller.merchantsuiteservices.facade;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.sabmiller.core.model.InvoicePaymentModel;
import com.sabmiller.facades.merchant.suite.data.SABMBankDetailsData;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardValidationData;
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

public interface SABMMerchantSuitePaymentFacade  {

    /**
     * Method to initiate EFT/Bank Account transactions for Invoice Payment Page
     *
     * @param invoices
     * @param total
     * @param bankDetailsData
     * @param currencyIso
     * @return
     * @throws SABMMerchantSuiteCartTotalException
     * @throws SABMMerchantSuiteTokenException
     * @throws SABMMerchantSuiteTokenAPIException
     * @throws SABMMerchantSuiteMissingBankDetailsException
     * @throws SABMMerchantSuitePaymentErrorException
     * @throws SABMMerchantSuiteConfigurationException
     * @throws SABMMerchantSuiteInvalidInvoiceDataException
     */
    String initiateInvoiceEFTxn(Set<String> invoices, BigDecimal total, SABMBankDetailsData bankDetailsData,
            String currencyIso)
            throws SABMMerchantSuiteCartTotalException, SABMMerchantSuiteTokenException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuitePaymentErrorException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteInvalidInvoiceDataException, SABMMerchantSuiteAPIRequestInvalidException;

    boolean isInvoicePaid(String trackingNumber);

    boolean sendConfirmationEmail(InvoicePaymentModel invoicePaymentModel);

    boolean hasExceededInvoiceWaitTimeout(String trackingNumber);

    /**
     * Method to initiate CC transaction for Invoice Payment Page.
     * @param invoices
     * @param total
     * @param currencyIso
     * @param cardType
     * @return
     * @throws SABMMerchantSuiteCartTotalException
     * @throws SABMMerchantSuiteTokenException
     * @throws SABMMerchantSuiteTokenAPIException
     * @throws SABMMerchantSuiteMissingBankDetailsException
     * @throws SABMSurchargeCalculationException
     * @throws SABMMerchantSuiteConfigurationException
     * @throws SABMMerchantSuiteInvalidInvoiceDataException
     */
    SABMCreditCardTransactionData initiateInvoiceCCTxn(Set<String> invoices, BigDecimal total,
            String currencyIso, String cardType)
            throws SABMMerchantSuiteCartTotalException, SABMMerchantSuiteTokenException, SABMMerchantSuiteTokenAPIException,
            SABMMerchantSuiteMissingBankDetailsException, SABMSurchargeCalculationException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteInvalidInvoiceDataException, SABMMerchantSuiteAPIRequestInvalidException;

    /**
     * Method to initiate CC transaction for Checkout Payment Page.
     * @param cardType
     * @return
     * @throws SABMMerchantSuiteCartTotalException
     * @throws InvalidCartException
     * @throws SABMMerchantSuiteTokenException
     * @throws SABMMerchantSuiteTokenAPIException
     * @throws SABMMerchantSuiteMissingBankDetailsException
     * @throws SABMSurchargeCalculationException
     * @throws SABMMerchantSuiteConfigurationException
     */
    SABMCreditCardTransactionData initiateCheckoutCCTxn(String cardType)
            throws SABMMerchantSuiteCartTotalException, InvalidCartException, SABMMerchantSuiteTokenException,
            SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException, SABMSurchargeCalculationException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;


    String processInvoiceAuthKeyCCTxn(String authKey)
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenException,
            SABMMerchantSuitePaymentErrorException, SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    void processCheckoutAuthKeyCCTxn(String authKey)
            throws  SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuitePaymentErrorException, InvalidCartException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException, SABMMerchantSuiteWebHookPaymentAlreadyDone;

    void processInvoiceAuthKeyCCTxnForPostback(String responseString)
            throws
            SABMMerchantSuitePaymentErrorException, SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    SABMMerchantSuiteTransactionProcessData processCheckoutAuthKeyCCTxnForPostback(String responseString)
            throws
            SABMMerchantSuitePaymentErrorException, InvalidCartException, SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException;

    SABMCreditCardTransactionData processInitiateInvoiceCCTxn(Set<String> invoices, BigDecimal total, String currencyIso,
            UserModel userModel, String cardType)
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenException,
            SABMSurchargeCalculationException, SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    SABMCreditCardTransactionData processInitiateCheckoutCCTxn(CartModel cartModel, String cardType)
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenException,
            SABMSurchargeCalculationException, SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    String processInvoiceEFTxn(Set<String> invoices, BigDecimal total, String currencyIso,
            UserModel userModel, SABMBankDetailsData bankDetailsData)
            throws SABMMerchantSuiteTokenAPIException, SABMMerchantSuiteMissingBankDetailsException, SABMMerchantSuiteTokenException,
            SABMMerchantSuitePaymentErrorException, SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException;

    boolean isCartPaymentApproved();

    List<SABMCreditCardValidationData> fetchCreditCardValidationData();

    InvoicePaymentData getInvoice(String trackingNumber);
}
