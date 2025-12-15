package com.sabmiller.merchantsuiteservices.exception;

public class SABMMerchantSuiteTokenAPIException extends Exception {

    private String errorCode = "unknown";

    public SABMMerchantSuiteTokenAPIException(final String msg, final String errorCode){
        super(msg);
        this.errorCode = errorCode;
    }

    public SABMMerchantSuiteTokenAPIException(final String msg){
        super(msg);
    }

    public SABMMerchantSuiteTokenAPIException(final String msg, final Throwable e){
        super(msg, e);
    }
}
