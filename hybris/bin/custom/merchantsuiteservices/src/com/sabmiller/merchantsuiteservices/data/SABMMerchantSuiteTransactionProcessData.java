package com.sabmiller.merchantsuiteservices.data;

import com.MerchantSuite.api.MaskedCreditCardDetails;

public class SABMMerchantSuiteTransactionProcessData {

        protected String action;

        protected String emailAddress;

        protected String token;

        protected long amount;

        protected long amountOriginal;

        protected long amountSurcharge;

        protected String authoriseId;

    protected String accountName;

    protected String accountNumber;

    protected String bsbNumber;

    protected String truncatedAccountNumber;

        protected String bankResponseCode;

    protected String cardHolderName;

    protected String expiryDate;

    protected String maskedCardNumber;

        protected MaskedCreditCardDetails cardDetails;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(final String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBsbNumber() {
        return bsbNumber;
    }

    public void setBsbNumber(final String bsbNumber) {
        this.bsbNumber = bsbNumber;
    }

    public String getTruncatedAccountNumber() {
        return truncatedAccountNumber;
    }

    public void setTruncatedAccountNumber(final String truncatedAccountNumber) {
        this.truncatedAccountNumber = truncatedAccountNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(final String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(final String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    protected String cardType;

        protected String currency;

        protected String internalNote;

        protected boolean is3DS;

        protected boolean isCVNPresent;

        protected boolean isTestTxn;

        protected String membershipID;

        protected String originalTxnNumber;

        protected String processedDateTime;

        protected String rrn;

        protected String receiptNumber;

        protected String reference1;

        protected String reference2;

        protected String reference3;

        protected String responseCode;

        protected String responseText;

        protected String paymentReason;

        protected String settlementDate;

        protected String source;

        protected boolean storeCard;

        protected String subType;

        protected String txnNumber;

    protected String type;

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(final long amount) {
        this.amount = amount;
    }

    public long getAmountOriginal() {
        return amountOriginal;
    }

    public void setAmountOriginal(final long amountOriginal) {
        this.amountOriginal = amountOriginal;
    }

    public long getAmountSurcharge() {
        return amountSurcharge;
    }

    public void setAmountSurcharge(final long amountSurcharge) {
        this.amountSurcharge = amountSurcharge;
    }


    public String getAuthoriseId() {
        return authoriseId;
    }

    public void setAuthoriseId(final String authoriseId) {
        this.authoriseId = authoriseId;
    }


    public String getBankResponseCode() {
        return bankResponseCode;
    }

    public void setBankResponseCode(final String bankResponseCode) {
        this.bankResponseCode = bankResponseCode;
    }


    public MaskedCreditCardDetails getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(final MaskedCreditCardDetails cardDetails) {
        this.cardDetails = cardDetails;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(final String cardType) {
        this.cardType = cardType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getInternalNote() {
        return internalNote;
    }

    public void setInternalNote(final String internalNote) {
        this.internalNote = internalNote;
    }

    public boolean isIs3DS() {
        return is3DS;
    }

    public void setIs3DS(final boolean is3DS) {
        this.is3DS = is3DS;
    }

    public boolean isCVNPresent() {
        return isCVNPresent;
    }

    public void setCVNPresent(final boolean CVNPresent) {
        isCVNPresent = CVNPresent;
    }

    public boolean isTestTxn() {
        return isTestTxn;
    }

    public void setTestTxn(final boolean testTxn) {
        isTestTxn = testTxn;
    }

    public String getMembershipID() {
        return membershipID;
    }

    public void setMembershipID(final String membershipID) {
        this.membershipID = membershipID;
    }

    public String getOriginalTxnNumber() {
        return originalTxnNumber;
    }

    public void setOriginalTxnNumber(final String originalTxnNumber) {
        this.originalTxnNumber = originalTxnNumber;
    }

    public String getProcessedDateTime() {
        return processedDateTime;
    }

    public void setProcessedDateTime(final String processedDateTime) {
        this.processedDateTime = processedDateTime;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(final String rrn) {
        this.rrn = rrn;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(final String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getReference1() {
        return reference1;
    }

    public void setReference1(final String reference1) {
        this.reference1 = reference1;
    }

    public String getReference2() {
        return reference2;
    }

    public void setReference2(final String reference2) {
        this.reference2 = reference2;
    }

    public String getReference3() {
        return reference3;
    }

    public void setReference3(final String reference3) {
        this.reference3 = reference3;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(final String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(final String responseText) {
        this.responseText = responseText;
    }

    public String getPaymentReason() {
        return paymentReason;
    }

    public void setPaymentReason(final String paymentReason) {
        this.paymentReason = paymentReason;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(final String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public boolean isStoreCard() {
        return storeCard;
    }

    public void setStoreCard(final boolean storeCard) {
        this.storeCard = storeCard;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(final String subType) {
        this.subType = subType;
    }

    public String getTxnNumber() {
        return txnNumber;
    }

    public void setTxnNumber(final String txnNumber) {
        this.txnNumber = txnNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

}
