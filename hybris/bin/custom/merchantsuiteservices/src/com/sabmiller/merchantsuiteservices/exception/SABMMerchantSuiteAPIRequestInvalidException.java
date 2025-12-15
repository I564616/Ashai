package com.sabmiller.merchantsuiteservices.exception;

public class SABMMerchantSuiteAPIRequestInvalidException extends Exception {

    private String errorCode = "unknown";

    public SABMMerchantSuiteAPIRequestInvalidException(final String msg, final String errorCode){
        super(msg);
        this.errorCode = errorCode;
    }

    public SABMMerchantSuiteAPIRequestInvalidException(final String msg){
        super(msg);
    }

    public SABMMerchantSuiteAPIRequestInvalidException(final String msg, final Throwable e){
        super(msg, e);
    }
}

