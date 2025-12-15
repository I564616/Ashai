<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="popup" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<script id="westpacResponse" type="text/json">${ycommerce:generateJson(westpacResponse)}</script>
<script id="ccValidationData" type="text/json">${ccValidationData}</script>


<form method="post" name="ccForm" id="ccFormId" novalidate action="${url}">
    <div>
        <c:choose>
            <c:when test="${isCashOnlyCustomer }">
                
                <h2 ng-init="payBy = 'CREDIT_CARD'"><spring:theme code="text.cart.payment.title.onlca" /></h2>
                <div class="form-group">
                    <label for="poNumber"><spring:theme code="text.cart.payment.po"/></label>
                    <input type="text" class="form-control" id="poNumber">
                </div>
                <div class="card-carddetailsOnlca">
                <input id="onlcaSelected" type="hidden" name="accountType" value="CREDIT_CARD">
            </c:when>
            <c:otherwise>
                <h2><spring:theme code="text.cart.payment.title"/></h2>
                <div class="form-group">
                    <label for="poNumber"><spring:theme code="text.cart.payment.po"/></label>
                    <input type="text" maxlength="35" class="form-control" id="poNumber">
                </div>
                <div class="radio" ng-init="payBy = 'payByAccount'">
                    <input id="payByAccount" ng-click="resetCCForm()" ng-model="payBy" type="radio" name="accountType" value="payByAccount" checked="checked">
                    <label for="payByAccount" class="h3"><spring:theme code="text.cart.payment.by.account"/></label>
                    <input id="payByCard" ng-click="resetCCForm()" ng-model="payBy" type="radio" name="accountType" value="CREDIT_CARD">
                    <label for="payByCard" class="h3"><spring:theme code="text.cart.payment.by.card"/></label>
                </div>
                <div class="card-carddetails">
            </c:otherwise>
        </c:choose>
            <input type="text" class="form-control hidden communityCodeInput" id="communityCodeId" name="communityCode" value="">
            <input type="text" class="form-control hidden tokenInput" id="tokenNumber" name="token" value="">
            <input type="text" class="form-control hidden ignoreDuplicateInput" id="ignoreDuplicateId" name="ignoreDuplicate"
                   value="">
            <input type="text" class="form-control hidden" id="customPONumberId" name="customPONumber" value="">
            <div class="form-group">
                <label for="cardNumber"><spring:theme code="text.cart.payment.cardnumber"/></label>
                <input type="text" pattern="[0-9]*" ng-model="cardNumber" ng-blur="isCardNumberValid = cardType != ''" class="allowNumericOnly ccValidation form-control validate-input" id="cardNumber"
                       name="creditCardNumber" ng-maxlength="cardLength" required>
                <span class="error cardMaxlength" ng-show="ccForm.creditCardNumber.$error.pattern"><spring:theme
                        code="text.checkout.cc.error.numbers"/></span>
                <span class="error" ng-show="ccForm.creditCardNumber.$error.maxlength"><spring:theme
                        code="text.account.billing.pay.message.invalid.card"/></span>
                <span class="error" ng-show="ccForm.submitted && ccForm.creditCardNumber.$error.required"><spring:theme
                        code="form.field.required"/></span>
                        
                 <span class="error" ng-hide="isCardNumberValid"><spring:theme code="text.account.billing.pay.message.valid.cardNumber" /></span>
            </div>
            <div class="form-group">
                <label for="nameOnCard"><spring:theme code="text.account.billing.pay.modal.card.name"/></label>
                <input type="text" pattern="[a-zA-Z \-']*" class="form-control validate-input" ng-model="cardName" id="nameOnCard"
                       name="cardholderName" ng-maxlength="200" required>
                <span class="error"
                      ng-show="ccForm.cardholderName.$error.pattern || ccForm.cardholderName.$error.maxlength"><spring:theme
                        code="text.account.billing.pay.message.invalid.card"/></span>
                <span class="error" ng-show="ccForm.submitted && ccForm.cardholderName.$error.required"><spring:theme
                        code="form.field.required"/></span>
            </div>
            <div class="expiry-security form-group clearfix">
                <div>
                    <label class="expiry"><spring:theme code="text.account.billing.pay.modal.expiry.date"/></label>
                    <label class="security" for="securityCode"><spring:theme
                            code="text.account.billing.pay.modal.security.code"/></label>
                </div>
                <div class="select-list expiry">
                    <div class="day pull-left">
                        <div class="select">
                            <span class="arrow"></span>
                            <select name="expiryDateMonth" ng-change="expiryDateYear = ''" ng-model="expiryDateMonth" class="expiryDateMonth cc-expiry-form form-control validate-input" required>
                                <option value="" disabled selected>MM</option>
                                <option value="01">01</option>
                                <option value="02">02</option>
                                <option value="03">03</option>
                                <option value="04">04</option>
                                <option value="05">05</option>
                                <option value="06">06</option>
                                <option value="07">07</option>
                                <option value="08">08</option>
                                <option value="09">09</option>
                                <option value="10">10</option>
                                <option value="11">11</option>
                                <option value="12">12</option>
                                <c:if test="${paymentTestMode}">
	                                <option value="99">99</option>
                                </c:if>
                            </select>
                        </div>
                    </div>
                    <span class="pull-left slash-sign">/</span>
                    <div class="month pull-left">
                        <div class="select" ng-class="{'hide': expiryDateMonth == 99 }">
	                        <span class="arrow"></span>
                            <select name="expiryDateYear" ng-model="expiryDateYear" class="expiryDateYear cc-expiry-form js-expiry-year form-control validate-input" required>
                                <option value="" disabled selected>YY</option>
                            </select>
                        </div>
                        
                       	<input type="text" ng-class="{'hide': expiryDateMonth != 99 }" ng-minlength="2" ng-keypress="isNumeric($event, 2)" ng-keyup="isNumeric($event, 2)" ng-model="expiryDateYear" class="allowNumericOnly expiryDateYear cc-expiry-form js-expiry-year form-control validate-input" />
                        <input type="hidden" id="expiryDateYearHidden" />
                    </div>
                </div>
                <div class="security"><input type="tel" ng-keypress="isNumeric($event, cvvLength)" class="allowNumericOnly form-control validate-input" id="securityCode"
                                             name="cvn" ng-model="cvn" ng-minlength="cvvLength" ng-maxlength="cvvLength" maxlength="cvvLength" required></div>
                <div class="clearfix"></div>
                <span class="error" ng-show="ccForm.cvn.$error.pattern || ccForm.cvn.$error.maxlength"><spring:theme
                        code="text.account.billing.pay.message.invalid.security.code"/></span>
                <span class="error" ng-show="ccForm.submitted && ccForm.cvn.$error.required"><spring:theme
                        code="text.account.billing.pay.message.security.code.required"/></span>
                <span class="error"
                      ng-show="expiryDateMonth != 99 && isExpiryDateInvalid"><spring:theme
                        code="text.account.billing.pay.message.invalid.expiry"/></span>
            </div>
            <span><spring:theme code="text.account.billing.pay.accept"/></span>
            <ul class="list-inline">
                <li><img src="${staticHostPath}/_ui/desktop/SABMiller/img/visa.png" alt="Visa" ng-style="{ 'opacity' : (cardType == 'VISA') ? '1' : '.3' }"></li>
                <li><img src="${staticHostPath}/_ui/desktop/SABMiller/img/mastercard.png" alt="Mastercard" ng-style="{ 'opacity' : (cardType == 'MASTER') ? '1' : '.3' }"></li>
                <li><img src="${staticHostPath}/_ui/desktop/SABMiller/img/amex.png" alt="AMEX" ng-style="{ 'opacity' : (cardType == 'AMEX') ? '1' : '.3' }"></li>
            </ul>
            
            <input type="hidden" id="cardType" />
            <span><spring:theme code="text.account.billing.pay.credit.card.payments.cue"/></span>
        </div>
    </div>

    <c:url var="placeOrderPoUrl" value="checkout/placeOrderByAccount"/>
    <button class="doCheckoutBut btn btn-primary btn-large processButton confirm-order" <c:if test="${!placeOrderDisable}">ng-disabled="payBy === 'CREDIT_CARD' && (cardType === '' || ccForm.$invalid)"</c:if>
             ng-click="ccForm.submitted = true" name="placeOrderBut" <c:if test="${placeOrderDisable}">disabled="disabled"</c:if>
            type="submit" data-checkout-po-url="${placeOrderPoUrl}"><spring:theme code="basket.page.checkout.placeOrder"/>
    </button>
</form>
<popup:merchantServiceFeePopup isInvoice="false" paymentAmount="${cartData.totalPrice.value}"/>
<popup:MSFPopup isInvoice="false" />
