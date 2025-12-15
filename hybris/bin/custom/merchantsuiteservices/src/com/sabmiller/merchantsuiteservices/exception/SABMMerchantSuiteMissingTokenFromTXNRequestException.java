package com.sabmiller.merchantsuiteservices.exception;

public class SABMMerchantSuiteMissingTokenFromTXNRequestException extends Exception {

    public SABMMerchantSuiteMissingTokenFromTXNRequestException(final String msg){
        super(msg);
    }

    public SABMMerchantSuiteMissingTokenFromTXNRequestException(final String msg, final Throwable e){
        super(msg, e);
    }
}
