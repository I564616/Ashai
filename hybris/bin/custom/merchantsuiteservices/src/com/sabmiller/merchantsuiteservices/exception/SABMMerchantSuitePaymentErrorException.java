package com.sabmiller.merchantsuiteservices.exception;

public class SABMMerchantSuitePaymentErrorException extends Exception{

    public String errorType;

    public SABMMerchantSuitePaymentErrorException(final String msg){
        super(msg);
    }

    public SABMMerchantSuitePaymentErrorException(final String msg,String errorType){
        super(msg);
        this.errorType=errorType;
    }

    public String getErrorType() {
        return errorType;
    }

    public SABMMerchantSuitePaymentErrorException(final String msg, final Throwable e){
        super(msg, e);
    }

}
