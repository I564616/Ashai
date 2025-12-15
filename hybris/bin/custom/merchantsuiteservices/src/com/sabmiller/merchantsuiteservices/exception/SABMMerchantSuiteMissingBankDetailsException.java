package com.sabmiller.merchantsuiteservices.exception;

public class SABMMerchantSuiteMissingBankDetailsException extends Exception {

    public SABMMerchantSuiteMissingBankDetailsException(String message) {
        super(message);
    }

    public SABMMerchantSuiteMissingBankDetailsException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
