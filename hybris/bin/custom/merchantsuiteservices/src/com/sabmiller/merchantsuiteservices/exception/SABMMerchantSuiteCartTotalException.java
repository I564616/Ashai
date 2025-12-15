package com.sabmiller.merchantsuiteservices.exception;

public class SABMMerchantSuiteCartTotalException extends Exception{

    public SABMMerchantSuiteCartTotalException(final String msg){
        super(msg);
    }

    public SABMMerchantSuiteCartTotalException(final String msg, final Throwable e){
        super(msg, e);
    }
}
