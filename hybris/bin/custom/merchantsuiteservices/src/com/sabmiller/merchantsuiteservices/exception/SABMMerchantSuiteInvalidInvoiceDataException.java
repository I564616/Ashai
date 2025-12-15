package com.sabmiller.merchantsuiteservices.exception;

public class SABMMerchantSuiteInvalidInvoiceDataException extends Exception {

    public SABMMerchantSuiteInvalidInvoiceDataException(final String msg){
        super(msg);
    }

    public SABMMerchantSuiteInvalidInvoiceDataException(final String msg, final Throwable e){
        super(msg, e);
    }
}
