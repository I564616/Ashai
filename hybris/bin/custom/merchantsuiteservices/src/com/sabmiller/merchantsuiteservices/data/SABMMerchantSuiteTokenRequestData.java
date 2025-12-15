package com.sabmiller.merchantsuiteservices.data;

import com.MerchantSuite.api.BankAccountDetails;
import com.MerchantSuite.api.CreditCardDetails;

public class SABMMerchantSuiteTokenRequestData {

    public CreditCardDetails creditCardDetails;

    public BankAccountDetails bankAccountDetails;

    public String accountNumber;

    public CreditCardDetails getCreditCardDetails() {
        return creditCardDetails;
    }

    public void setCreditCardDetails(final CreditCardDetails creditCardDetails) {
        this.creditCardDetails = creditCardDetails;
    }

    public BankAccountDetails getBankAccountDetails() {
        return bankAccountDetails;
    }

    public void setBankAccountDetails(final BankAccountDetails bankAccountDetails) {
        this.bankAccountDetails = bankAccountDetails;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
