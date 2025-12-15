package com.sabmiller.merchantsuiteservices.exception;

public class SABMSurchargeCalculationException extends Exception {

    public SABMSurchargeCalculationException(String message) {
        super(message);
    }

    public SABMSurchargeCalculationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
