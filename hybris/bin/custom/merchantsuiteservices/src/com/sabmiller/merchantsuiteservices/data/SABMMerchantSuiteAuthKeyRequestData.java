package com.sabmiller.merchantsuiteservices.data;

public class SABMMerchantSuiteAuthKeyRequestData {

    long amount;

    long surcharge;

    String paymentId;

    String currency;

    public long getAmount() {
        return amount;
    }

    public void setAmount(final long amount) {
        this.amount = amount;
    }

    public long getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(final long surcharge) {
        this.surcharge = surcharge;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(final String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }





}
