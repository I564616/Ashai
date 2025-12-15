package com.apb.core.card.payment.impl;

import com.apb.core.card.payment.AsahiCreditCardPaymentService;
import com.apb.core.card.payment.AsahiPaymentResponseMapperUtil;
import com.apb.core.card.payment.strategy.AsahiPaymentRequestResponseAuditStrategy;
import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.exception.AsahiPaymentException;
import com.apb.core.payment.fz.AsahiPaymentGatewayContext;
import com.apb.core.payment.fz.exceptions.AsahiPaymentApiException;
import com.apb.core.payment.fz.exceptions.AsahiPaymentNetworkException;
import com.apb.core.payment.fz.models.AsahiPaymentPurchase;
import com.apb.core.payment.fz.models.AsahiPaymentResponse;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbNumberKeyGeneratorService;
import com.apb.facades.card.payment.AsahiPaymentDetailsData;
import com.apb.facades.card.payment.AsahiPaymentPurchaseResponseData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionData;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.sabmiller.core.enums.TaxType;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @param <T>
 */
public class AsahiCreditCardPaymentServiceImpl implements AsahiCreditCardPaymentService {
    @Autowired
    private UserService userService;

    @Resource(name = "asahiPaymentGatewayContext")
    AsahiPaymentGatewayContext asahiPaymentGatewayContext;

    @Resource(name = "asahiPaymentPurchase")
    AsahiPaymentPurchase asahiPaymentPurchase;

    @Resource(name = "asahiConfigurationService")
    private AsahiConfigurationService asahiConfigurationService;

    @Resource(name = "asahiPaymentRequestResponseAuditStrategy")
    private AsahiPaymentRequestResponseAuditStrategy asahiPaymentRequestResponseAuditStrategy;

    @Resource(name = "asahiPaymentResponseMapperUtil")
    private AsahiPaymentResponseMapperUtil asahiPaymentResponseMapperUtil;

    @Resource(name = "asahiPaymentResponse")
    AsahiPaymentResponse<AsahiPaymentPurchase> asahiPaymentResponse;

    @Autowired
    private ApbNumberKeyGeneratorService apbNumberKeyGeneratorService;


    @Autowired
    private CMSSiteService cmsSiteService;

    private static final String PAYMENT_TARGET_ACCOUNT_ID = "asahi.payment.target.account.id.";
    private static final String PAYMENT_TARGET_TRANSACTION_ID = "asahi.payment.target.transaction.token.id.";
    private static final String PAYMENT_TARGET_SANDBOX_ENABLED = "asahi.payment.target.sandbox.url.enabled.";

    private static final String DEFAULT_ACCOUNT_ID = "TESTAsahi";
    private static final String DEFAULT_TOKEN_ID = "0501434920a18146740843221689a77fa7028f76";
    private static final boolean CAPTURE = false;

    private static final String ASAHI_CUSTOMER_IP = "asahi.payment.target.default.customer.ip.";
    private static final String DEFAULT_CUSTOMER_IP = "127.0.0.1";

    private static final String DEFAULT_CURRENCY = "AUD";
    private static final String ALB_SITE_ID = "sga";
    private static Logger LOG = LoggerFactory.getLogger("AsahiCreditCardPaymentServiceImpl");


    /**
     * This method is to Pre-Authorize a Payment Transaction.
     *
     * @param asahiPaymentDetailsData
     * @param cartData
     * @throws AsahiPaymentException In case the payment is not successful.
     */
    public void makeCreditCardPaymentRequest(final AsahiPaymentDetailsData asahiPaymentDetailsData) throws AsahiPaymentException {
        AsahiPaymentPurchaseResponseData asahiPaymentPurchaseResponseData = null;
        boolean paymentSuccess = false;
        final List<String> paymentErrorMessages = new ArrayList<>();

        if (null != asahiPaymentDetailsData.getAsahiSAMPaymentData()) {
            asahiPaymentGatewayContext.setUsername(this.asahiConfigurationService
                    .getString(PAYMENT_TARGET_ACCOUNT_ID + "sam." + cmsSiteService.getCurrentSite().getUid(), DEFAULT_ACCOUNT_ID));
            asahiPaymentGatewayContext.setToken(this.asahiConfigurationService
                    .getString(PAYMENT_TARGET_TRANSACTION_ID + "sam." + cmsSiteService.getCurrentSite().getUid(), DEFAULT_TOKEN_ID));

            asahiPaymentGatewayContext.setSandbox(this.asahiConfigurationService
                    .getBoolean(PAYMENT_TARGET_SANDBOX_ENABLED + "sam." + cmsSiteService.getCurrentSite().getUid(), false));
            asahiPaymentGatewayContext.setSiteId(cmsSiteService.getCurrentSite().getUid());
            if(asahiConfigurationService.getBoolean("asahi.payment.mode.property.reset", true)) {
                asahiPaymentGatewayContext.setDirectDebit(false);
            }
        } else {
            asahiPaymentGatewayContext.setUsername(this.asahiConfigurationService
                    .getString(PAYMENT_TARGET_ACCOUNT_ID + cmsSiteService.getCurrentSite().getUid(), DEFAULT_ACCOUNT_ID));
            asahiPaymentGatewayContext.setToken(this.asahiConfigurationService
                    .getString(PAYMENT_TARGET_TRANSACTION_ID + cmsSiteService.getCurrentSite().getUid(), DEFAULT_TOKEN_ID));

            asahiPaymentGatewayContext.setSandbox(this.asahiConfigurationService
                    .getBoolean(PAYMENT_TARGET_SANDBOX_ENABLED + cmsSiteService.getCurrentSite().getUid(), false));

            asahiPaymentGatewayContext.setSiteId(cmsSiteService.getCurrentSite().getUid());
            if(asahiConfigurationService.getBoolean("asahi.payment.mode.property.reset", true)) {
                asahiPaymentGatewayContext.setDirectDebit(false);
            }
        }


        if (StringUtils.isEmpty(asahiPaymentDetailsData.getCustomerIP())) {
            // Go with the default ip address.
            LOG.info("Customer IP is empty : " + asahiPaymentDetailsData.getCustomerIP());
            asahiPaymentDetailsData.setCustomerIP(this.asahiConfigurationService
                    .getString(ASAHI_CUSTOMER_IP + cmsSiteService.getCurrentSite().getUid(), DEFAULT_CUSTOMER_IP));
        }

        final String referencePrefixCode = asahiConfigurationService
                .getString(ApbCoreConstants.PAYMETN_CREDIT_CARD_REFERENCE_PREFIX + cmsSiteService.getCurrentSite().getUid(), "");

        asahiPaymentDetailsData.setCardReference(cmsSiteService.getCurrentSite().getUid().equals(ALB_SITE_ID) ? generateALBPaymentReference(asahiPaymentDetailsData) : apbNumberKeyGeneratorService.generateCode(referencePrefixCode));

        //create transaction entry data before making the payment request call.
        final PaymentTransactionData PaymentTransactionData = asahiPaymentRequestResponseAuditStrategy
                .createPaymentTransactionData(asahiPaymentDetailsData, DEFAULT_CURRENCY, CAPTURE);

        try {
            try {
                LOG.info("Credit card payment will be made with details: ");
                LOG.info(String.format("User: %s\t Card Type: %s\t Payment amount: %s\t Card Reference: %s\t Card Number: %s\t Card Token: %s",
                        userService.getCurrentUser().getUid(), asahiPaymentDetailsData.getCardTypeInfo(), asahiPaymentDetailsData.getTotalAmount(), asahiPaymentDetailsData.getCardReference(), asahiPaymentDetailsData.getCardNumber(), asahiPaymentDetailsData.getCardToken()));
            } catch (NullPointerException npe) {
                LOG.error("Exception occured while logging card details : ", npe);
            }
            asahiPaymentResponse = asahiPaymentPurchase.create(asahiPaymentDetailsData.getTotalAmount().doubleValue(), null,
                    asahiPaymentDetailsData.getCardReference(), asahiPaymentDetailsData.getCustomerIP(), DEFAULT_CURRENCY, CAPTURE,
                    null, asahiPaymentGatewayContext, asahiPaymentDetailsData.getCardToken());

            if (null == asahiPaymentResponse) {
                throw new AsahiPaymentException("Did not receive any response on credit card payment request", paymentErrorMessages);
            }

            asahiPaymentPurchaseResponseData = asahiPaymentResponseMapperUtil.mapPaymentPurchaseResponse(asahiPaymentResponse);
            asahiPaymentRequestResponseAuditStrategy.updatePaymentTransactionEntryData(PaymentTransactionData,
                    asahiPaymentPurchaseResponseData);

            if (asahiPaymentResponse.isSuccessful() && CollectionUtils.isEmpty(asahiPaymentResponse.getErrors())) {
                if (asahiPaymentResponse.getResult().isSuccessful()) {
                    paymentSuccess = true;
                } else {
                    if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getResponse_code())) {
                        paymentErrorMessages
                                .add("Payment declined  with response code" + asahiPaymentResponse.getResult().getResponse_code());
                    }

                    if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getMessage())) {
                        paymentErrorMessages.add("Response Message " + asahiPaymentResponse.getResult().getMessage());
                    }
                }
            } else {
                LOG.info("Payment is not successful, Transaction Failure Message : " + asahiPaymentResponse.getErrors()
                        + " Http Status Response Code : " + asahiPaymentResponse.getResponseCode());
                paymentErrorMessages.add("Payment Success flag " + asahiPaymentResponse.isSuccessful());
                paymentErrorMessages.add("Http Status Response Code " + asahiPaymentResponse.getResponseCode());
                paymentErrorMessages.addAll(asahiPaymentResponse.getErrors());
            }
        } catch (IOException | AsahiPaymentNetworkException | AsahiPaymentApiException e) {
            paymentErrorMessages.add("Network/IO/Api Exception");
            LOG.info("Exception while making authorize request : " + paymentErrorMessages.toString(), e);
        } catch (final Exception e) {
            paymentErrorMessages.add("unknown exception occured.");
            LOG.info("Exception while making authorize request : " + paymentErrorMessages.toString(), e);
        } finally {
            try {
                final AsahiSAMPaymentData asahiSAMPaymentData = asahiPaymentDetailsData.getAsahiSAMPaymentData();
                if (asahiPaymentDetailsData.isSamPayment()) {
                    PaymentTransactionData.setSamPartialPayReason(asahiSAMPaymentData.getPartialPaymentReason());
                    PaymentTransactionData.setSamPaymentRef(asahiSAMPaymentData.getPaymentReference());
                    asahiPaymentRequestResponseAuditStrategy.makeSAMPaymentRequestAuditEntry(PaymentTransactionData,
                            asahiSAMPaymentData);

                } else {
                    asahiPaymentRequestResponseAuditStrategy.makePaymentRequestAuditEntry(PaymentTransactionData);
                }
            } catch (final Exception e) {
                paymentErrorMessages.add("Exception while making the audit entry");
                LOG.info("Exception while audit entries : " + paymentErrorMessages.toString(), e);
            }
        }

        if (!paymentSuccess) {
            throw new AsahiPaymentException("Credit card payment was not successful. ", paymentErrorMessages);
        }

        LOG.info("-----------------!!!!!Response from Payment!!!!!--------------------"
                + asahiPaymentResponse.getResult().toString());
        LOG.info("----------------------------------------------------------");
    }
    
    private String generateALBPaymentReference(final AsahiPaymentDetailsData asahiPaymentDetailsData) {
       StringBuilder timeStamp = new StringBuilder();
       return timeStamp.append(asahiPaymentDetailsData.getCustomerNumber()).append(new SimpleDateFormat("-ddMMyyHHmmss").format(new Date())).toString();
   }
}
