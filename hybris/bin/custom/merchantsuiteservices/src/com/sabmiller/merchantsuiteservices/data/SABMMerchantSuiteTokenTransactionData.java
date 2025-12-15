package com.sabmiller.merchantsuiteservices.data;


public class SABMMerchantSuiteTokenTransactionData {

    long total;

    String currencyIso;

    String paymentId;

    String token;

    String accountNumber;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(final long total) {
        this.total = total;
    }

    public String getCurrencyIso() {
        return currencyIso;
    }

    public void setCurrencyIso(final String currencyIso) {
        this.currencyIso = currencyIso;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(final String paymentId) {
        this.paymentId = paymentId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }


}
