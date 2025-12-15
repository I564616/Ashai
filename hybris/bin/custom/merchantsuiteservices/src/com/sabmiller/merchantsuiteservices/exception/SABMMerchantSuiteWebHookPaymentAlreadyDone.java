package com.sabmiller.merchantsuiteservices.exception;

public class SABMMerchantSuiteWebHookPaymentAlreadyDone extends Exception {

    private String errorCode = "unknown";

    public SABMMerchantSuiteWebHookPaymentAlreadyDone(final String msg, final String errorCode){
        super(msg);
        this.errorCode = errorCode;
    }

    public SABMMerchantSuiteWebHookPaymentAlreadyDone(final String msg){
        super(msg);
    }

    public SABMMerchantSuiteWebHookPaymentAlreadyDone(final String msg, final Throwable e){
        super(msg, e);
    }
}

