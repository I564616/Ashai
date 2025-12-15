package com.sabmiller.merchantsuiteservices.exception;

public class SABMMerchantSuiteConfigurationException extends Exception {

    public SABMMerchantSuiteConfigurationException(final String msg){
        super(msg);
    }

    public SABMMerchantSuiteConfigurationException(final String msg, final Throwable e){
        super(msg, e);
    }

}
