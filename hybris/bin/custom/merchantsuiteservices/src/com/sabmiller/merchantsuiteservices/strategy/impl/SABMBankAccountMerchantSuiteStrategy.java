package com.sabmiller.merchantsuiteservices.strategy.impl;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import jakarta.annotation.Resource;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.MerchantSuite.api.APIResponse;
import com.MerchantSuite.api.CreditCardDetails;
import com.MerchantSuite.api.TokenAddRequest;
import com.MerchantSuite.api.TokenResp;
import com.MerchantSuite.api.TokenisationMode;
import com.MerchantSuite.api.TxnRequest;
import com.MerchantSuite.api.TxnResp;
import com.sabmiller.core.enums.SABMMerchantSuitePaymentErrorType;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTokenRequestData;
import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTokenTransactionData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteAPIRequestInvalidException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteMissingBankDetailsException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuitePaymentErrorException;
import com.sabmiller.merchantsuiteservices.strategy.SABMGlobalMerchantSuiteStrategy;

/**
 * Bank AccountStrategy
 */
public class SABMBankAccountMerchantSuiteStrategy extends SABMDefaultMerchantSuiteStrategy implements SABMGlobalMerchantSuiteStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SABMBankAccountMerchantSuiteStrategy.class.getName());

    @Resource
    ConfigurationService configurationService;

    private static final String DEFAULT_ACTION="payment";

    private static final String DEFAULT_TYPE="ecommerce";

    private static final String DEFAULT_SUB_TYPE="single";
    private String successCode;
    private String invalidAccountNumberErrorCode;
    private String invalidBSBErrorCode;

    @Resource
    private EnumerationService enumerationService;
    private static final String COMMA=",";

    @Override
    public String getToken(final SABMMerchantSuiteTokenRequestData request)
            throws SABMMerchantSuiteMissingBankDetailsException,
            SABMMerchantSuiteConfigurationException, SABMMerchantSuiteAPIRequestInvalidException, SABMMerchantSuitePaymentErrorException {
        //Creating new TokenAddRequest object
        TokenAddRequest tokenAddRequest = new TokenAddRequest(getCredetials());
        if (Objects.isNull(request.getBankAccountDetails())) {
            throw new SABMMerchantSuiteMissingBankDetailsException("Missing bank details from the request");
        }
        tokenAddRequest.setBankAccountDetails(request.getBankAccountDetails());
        tokenAddRequest.setReference1(request.getAccountNumber());
        //Send Generate Token Request
        TokenResp response = sendTokenRequest(tokenAddRequest);

        if (Objects.nonNull(response) && handleBankResponse(response.getApiResponse()))
        {
            return response.getTokenResponse().getToken();
        }
        else {
            throw new SABMMerchantSuiteAPIRequestInvalidException("No response found for request");
        }
    }

    public boolean handleBankResponse(APIResponse resp)
            throws SABMMerchantSuitePaymentErrorException {
        if (Objects.nonNull(resp)) {
            if (SabmStringUtils.splitStringAndReturnList(successCode, COMMA).contains(resp.getResponseCode().toString())) {
                return true;
            }
            else if (SabmStringUtils.splitStringAndReturnList(invalidAccountNumberErrorCode, COMMA).contains(resp.getResponseCode().toString())) {
                throw new SABMMerchantSuitePaymentErrorException("Merchant Suite Payment Error : Response code :" + resp.getResponseCode() + SABMMerchantSuitePaymentErrorType.INVALID_ACCOUNT_NUMBER ,
                        enumerationService.getEnumerationName(SABMMerchantSuitePaymentErrorType.INVALID_ACCOUNT_NUMBER));
            }
            else if (SabmStringUtils.splitStringAndReturnList(invalidBSBErrorCode, COMMA).contains(resp.getResponseCode().toString())) {
                throw new SABMMerchantSuitePaymentErrorException("Merchant Suite Payment Error :  Response code :  " + resp.getResponseCode() + SABMMerchantSuitePaymentErrorType.INVALID_BSB,
                        enumerationService.getEnumerationName(SABMMerchantSuitePaymentErrorType.INVALID_BSB));
            }
            else {
                throw new SABMMerchantSuitePaymentErrorException("Merchant Suite Payment Error : Response code : " + resp.getResponseCode() + SABMMerchantSuitePaymentErrorType.OTHER,
                        enumerationService.getEnumerationName(SABMMerchantSuitePaymentErrorType.OTHER));
            }
        }
        return false;
    }


    @Override
    public TxnResp processTransaction(final SABMMerchantSuiteTokenTransactionData transactionData)
            throws SABMMerchantSuiteConfigurationException,
            SABMMerchantSuiteAPIRequestInvalidException {
        //Creating new TxnRequest object
        TxnRequest transactionRequest = new TxnRequest(getCredetials());
        transactionRequest.setAmount(transactionData.getTotal());
        //Creating CreditCardDetails object to store token generated using getToken call
        CreditCardDetails creditCardDetails = new CreditCardDetails();
        //setting token in card number instead
        creditCardDetails.setCardNumber(transactionData.getToken());
        transactionRequest.setReference3(transactionData.getAccountNumber());
        transactionRequest.setCardDetails(creditCardDetails);
        transactionRequest.setCurrency(transactionData.getCurrencyIso());
        transactionRequest.setReference1(transactionData.getPaymentId());
        transactionRequest.setAction(DEFAULT_ACTION);
        transactionRequest.setType(DEFAULT_TYPE);
        transactionRequest.setSubType(DEFAULT_SUB_TYPE);
        transactionRequest.setTokenisationMode(TokenisationMode.DO_NOT_TOKENISE);
        //sending tokenised transaction request
        TxnResp response = sendTokenTransactionRequest(transactionRequest);
        if (Objects.nonNull(response) && handleResponse(response.getApiResponse()))
        {
            return response;
        }
        else {
            throw new SABMMerchantSuiteAPIRequestInvalidException("No response found for request");
        }
    }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode;
    }

    public String getSuccessCode() {
        return successCode;
    }

    public void setInvalidAccountNumberErrorCode(String invalidAccountNumberErrorCode) {
        this.invalidAccountNumberErrorCode = invalidAccountNumberErrorCode;
    }

    public String getInvalidAccountNumberErrorCode() {
        return invalidAccountNumberErrorCode;
    }

    public void setInvalidBSBErrorCode(String invalidBSBErrorCode) {
        this.invalidBSBErrorCode = invalidBSBErrorCode;
    }

    public String getInvalidBSBErrorCode() {
        return invalidBSBErrorCode;
    }
}
