<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="single-checkout" tagdir="/WEB-INF/tags/responsive/checkout/single"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ attribute name="savedCardsUrl" required="true" %>


<input type="hidden" id="numberOfCards" name="numberOfCards" value="${creditCards.size()}" />
<input type="hidden" id="maxNumberOfCards" name="maxNumberOfCards" value="${maxNumberOfCards}" />
<input type="hidden" id="isSurchargeAdded" name="isSurchargeAdded" value="${isAddSurcharge}" />

<div id="creditCardSection">
    <div class="checkout-card-payment-section">
        <div class="card-with-surcharge">
            <p><spring:theme code="checkout.payment.pay.securely" /></p>
            <c:choose>
                <c:when test="${visaSurcharge == masterSurcharge}">
                    <p><spring:theme code="checkout.payment.visa.master.surcharge" />: ${visaSurcharge}%</p>
                </c:when>
                <c:otherwise>
                    <p><spring:theme code="checkout.payment.visa.surcharge" />: ${visaSurcharge}%</p>
                    <p><spring:theme code="checkout.payment.master.surcharge" />: ${masterSurcharge}%</p>
                </c:otherwise>
            </c:choose>
            <p><spring:theme code="checkout.payment.amex.surcharge" />: ${amexSurcharge}%</p>
        </div>
        <div class="card-without-surcharge">
            <p><spring:theme code="checkout.payment.pay.securely.wo.surcharge" /></p>
        </div>
        <div class="card-payment-content">
            <c:forEach items="${creditCards}" var="creditCard">
                <%--<input name="xx" type="radio" >
                       ${creditCard.cardType} ending in  ${creditCard.cardNumber} (${creditCard.expiryMonth}/${creditCard.expiryYear})
                <br> --%>
                <div class="checkout-card-list">
                    <input name="cardDetails" type="radio" class="checkout-card-item" id="checkout-card-item_${creditCard.cardType}" value="${creditCard.cardType}" data-token="${creditCard.token}" >
                    <c:choose>
                        <c:when test="${creditCard.cardType eq 'amex'}">
                            <c:set var="creditCardType" value="AMEX"/>
                        </c:when>
                        <c:when test="${creditCard.cardType eq 'master'}">
                            <c:set var="creditCardType" value="Mastercard"/>
                        </c:when>
                        <c:when test="${creditCard.cardType eq 'visa'}">
                            <c:set var="creditCardType" value="Visa"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="creditCardType" value=""/>
                        </c:otherwise>
                    </c:choose>
                    <label class="card_label credit-card-normal">&nbsp;&nbsp;<img src="${commonResourcePath}/images/${creditCard.cardType}.png" />&nbsp;&nbsp;${creditCardType} ending in  ${creditCard.cardNumber} (${creditCard.expiryMonth}/${creditCard.expiryYear})</label>
                </div>
            </c:forEach>
        </div>
        <div class="accountActions-bottom hide">
            <a href="${savedCardsUrl}">Manage Saved Cards</a>
        </div>
        <div class="checkout-add-new-card hide">
            <div class="add-card-type">
                <p><b>Select Card Type</b></p>
                <input type="radio" name="apbCreditCardType" id="asahiCreditCardTypeVISA"  value="VISA" />
                <label for="asahiCreditCardTypeVISA"><img src="${commonResourcePath}/images/visa.png"/></label>
                <input type="radio" name="apbCreditCardType" id="asahiCreditCardTypeMASTER" value="MASTERCARD" />
                <label for="asahiCreditCardTypeMASTER"><img src="${commonResourcePath}/images/master.png"/></label>
                <input type="radio" name="apbCreditCardType" id="asahiCreditCardTypeAMEX" value="AMEX" />
                <label for="asahiCreditCardTypeAMEX"><img src="${commonResourcePath}/images/amex.png"  /></label>
            </div>
            <div id="iframePaymentSection">
                <iframe id="checkoutIframe" name="my_iframe" class="iframe-content" src="${iframePostUrl}" frameBorder="0" ></iframe>
            </div>
            <div class="row">
                <div class="col-sm-6 col-md-3">
                    <single-checkout:securityCode/>
                </div>
                <div class="col-sm-12 col-md-12">
                    <formElement:formCheckbox idKey="saveCreditCard" labelCSS="checkout-keg-checkbox" labelKey="checkout.summary.save.card.check.message" path="saveCreditCard"/>
                </div>
            </div>
        </div>
    </div>
</div>