<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>


<div class="payment-modal mfp-hide" id="payment-modal" ng-controller="formsCtrl" ng-init="ccBillingFormInit()">
    <c:set var="eftTermsAndCondLink">
        <spring:theme code="text.link.eft.terms.conditons" />
    </c:set>
    <h2><spring:theme code="text.account.billing.pay.modal.header"/></h2>

    <!-- removed as per SABMC-1424 -->
    <!-- Amount to Pay -->
    <div class="form-group">
        <label><spring:theme code="text.account.billing.pay.modal.amount"/></label>

        <div class="amountToPay"></div>
    </div>

    <div class="radio" id="radioPayBy" > <!-- ng-init="payBy = 'payByCard'" -->
        <div>
            <input id="payByCard" ng-click="resetEFTForm()" ng-model="payBy" type="radio" name="accountType" value="CREDIT_CARD" checked="checked">
            <label for="payByCard" class="h3 offset-bottom-xsmall"><spring:theme code="text.cart.payment.by.card"/></label>
        </div>
        <div>
            <input id="payByEFT" ng-click="resetEFTForm()" ng-model="payBy" type="radio" name="accountType" value="payByEFT">
            <label for="payByEFT" class="h3"><spring:theme code="text.cart.payment.by.eft"/></label>
        </div>

    </div>

    <div class="card-carddetails" ng-show=" payBy == 'CREDIT_CARD' ">
        <form id="makePaymentForm" method="post" name="ccForm" novalidate>
            <!-- Hidden Fields -->
            <input type="text" class="form-control hidden billingTokenInput" id="billingToken" name="token">
            <input type="text" class="form-control hidden billingCommunityCodeInput" id="billingCommunityCode"
                   name="communityCode">
            <input type="text" class="form-control hidden billingAccountTypeInput" id="billingAccountTypeId"
                   name="accountType" value="CREDIT_CARD">
            <input type="text" class="form-control hidden ignoreDuplicateInput" id="billingIgnoreDuplicateId"
                   name="ignoreDuplicate" value="">

            <!-- Card Number -->
            <div class="form-group">
                <label for="cardNumber"><spring:theme code="text.account.billing.pay.modal.card.number"/></label>
                <input type="text" pattern="[0-9]*" ng-blur="isCardNumberValid = cardType != ''" class="allowNumericOnly ccValidation form-control validate-input" id="cardNumber"
                       name="creditCardNumber" ng-model="cardNumber" ng-maxlength="cardLength" required>
                <!-- Messages -->
                <span class="error" ng-show="ccForm.creditCardNumber.$error.pattern">Please enter numbers only</span>
                <span class="error" ng-show="ccForm.creditCardNumber.$error.maxlength">Invalid card details</span>
                <span class="error"
                      ng-show="ccForm.submitted && ccForm.creditCardNumber.$error.required">Required</span>
                 <span class="error" ng-hide="isCardNumberValid"><spring:theme code="text.account.billing.pay.message.valid.cardNumber" /></span>
            </div>

            <!-- Name on Card -->
            <div class="form-group">
                <label for="nameOnCard"><spring:theme code="text.account.billing.pay.modal.card.name"/></label>
                <input type="text" class="form-control validate-input" pattern="[a-zA-Z \-']*" id="nameOnCard"
                       name="cardholderName" ng-model="cardName" ng-maxlength="200" required>
                <!-- Messages -->
                <span class="error"
                      ng-show="ccForm.cardholderName.$error.pattern || ccForm.cardholderName.$error.maxlength">Invalid card details</span>
                <span class="error" ng-show="ccForm.submitted && ccForm.cardholderName.$error.required">Required</span>
            </div>


            <div class="expiry-security form-group clearfix">
                <div>
                    <label class="expiry"><spring:theme code="text.account.billing.pay.modal.expiry.date"/></label>
                    <label class="security"><spring:theme code="text.account.billing.pay.modal.security.code"/></label>
                </div>

                <div class="select-list expiry">
                    <div class="day pull-left">
                        <div class="select">
                            <span class="arrow"></span>
                            <select name="expiryDateMonth" ng-change="expiryYear = ''" class="expiryDateMonth cc-expiry-form form-control validate-input"
                                    ng-model="expiryMonth" required>
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
                    <span class="error" ng-show="ccForm.expiryMonth.$error.required">Required</span>
                    <span class="error" ng-show="ccForm.expiryMonth.$error.pattern">Please enter numbers only</span>
                    <span class="pull-left slash-sign">/</span>

                    <div class="month pull-left">
                        <div class="select"  ng-class="{'hide': expiryMonth == 99 }">
                            <span class="arrow"></span>
                            <select name="expiryDateYear"
                                    class="expiryDateYear cc-expiry-form js-expiry-year form-control validate-input"
                                    ng-model="expiryYear" required>
                                <option value="" disabled selected>YY</option>
                            </select>
                        </div>

                        <input type="text" ng-class="{'hide': expiryMonth != 99 }" ng-minlength="2"  ng-keypress="isNumeric($event, 2)" ng-keyup="isNumeric($event, 2)" ng-model="expiryYear" class="allowNumericOnly expiryDateYear cc-expiry-form js-expiry-year form-control validate-input" />
                        <input type="hidden" id="expiryDateYearHidden" />
                    </div>
                </div>

                <div class="security">
                    <input type="text" class="allowNumericOnly form-control validate-input" id="securityCode" ng-keypress="isNumeric($event, cvvLength)" name="cvn" pattern="[0-9]*"
                           ng-model="cvn" ng-minlength="cvvLength" ng-maxlength="cvvLength" required>
                </div>
                <div class="clearfix"></div>
                <!-- Messages -->
                <span class="error" ng-show="ccForm.cvn.$error.pattern || ccForm.cvn.$error.maxlength">Invalid security code</span>
                <span class="error"
                      ng-show="ccForm.submitted && ccForm.cvn.$error.required">Security Code Required</span>
                <span class="error"
                      ng-show="expiryDateMonth != 99 && isExpiryDateInvalid"><spring:theme
                      code="text.account.billing.pay.message.invalid.expiry"/></span>
            </div>

            <div>
                <div class="offset-bottom-xxsmall"><spring:theme code="text.account.billing.pay.accept"/></div>
                <ul class="list-inline">
                    <li><img src="${staticHostPath}/_ui/desktop/SABMiller/img/visa.png" alt="Visa" ng-style="{ 'opacity' : (cardType == 'VISA') ? '1' : '.3' }"></li>
                    <li><img src="${staticHostPath}/_ui/desktop/SABMiller/img/mastercard.png" alt="Mastercard" ng-style="{ 'opacity' : (cardType == 'MASTER') ? '1' : '.3' }"></li>
                    <li><img src="${staticHostPath}/_ui/desktop/SABMiller/img/amex.png" alt="AMEX" ng-style="{ 'opacity' : (cardType == 'AMEX') ? '1' : '.3' }"></li>
                </ul>
            </div>
            <input type="hidden" value="{{cardType}}" id="cardType" />
            <p><spring:theme code="text.account.billing.pay.credit.card.payments.cue"/></p>

            <p><spring:theme code="text.account.billing.pay.safe.cue"/></p>
            <!-- <input type="submit" id="billingSubmitPayment" class="hidden" />  -->

        </form>
        <button id="billingButtonCCPayment" ng-click="ccForm.submitted = true" ng-disabled="ccForm.$invalid || cardType === ''" class="btn btn-primary processButton">
            <spring:theme code="text.account.billing.pay.make"/></button>
    </div>


    <div class="card-eftdetails ng-hide" ng-hide=" payBy == 'CREDIT_CARD' ">
        <label for=""><b><spring:theme code="text.eft.direct.debit.heading"/></b></label>
        <p><spring:theme code="text.eft.direct.debit.paragraph"/></p>
        <form id="makeEFTPaymentForm" method="post" name="eftForm" novalidate>
            <!-- Hidden Fields -->
            <input type="text" class="form-control hidden billingTokenInput" id="billingToken" name="token">
            <input type="text" class="form-control hidden billingCommunityCodeInput" id="billingCommunityCode"
                   name="communityCode">
            <input type="text" class="form-control hidden billingAccountTypeInput" id="billingAccountTypeId"
                   name="accountType" value="DIRECT_DEBIT">
            <input type="text" class="form-control hidden ignoreDuplicateInput" id="billingIgnoreDuplicateId"
                   name="ignoreDuplicate" value="">

            <!-- BSB -->
            <div class="form-group">
                <label for="bsb"><spring:theme code="text.account.billing.pay.modal.bsb.code"/></label>
                <input type="text" class="form-control validate-input" pattern="[0-9]*" id="bsb"
                       name="bsb" ng-model="bsb" ng-keypress="isNumeric($event, 99)" required>
                <!-- Messages -->
                <span class="error"
                      ng-show="eftForm.bsb.$error.pattern || eftForm.bsb.$error.maxlength">Invalid account details</span>
                <span class="error" ng-show="eftForm.submitted && eftForm.bsb.$error.required">Required</span>
            </div>

            <!-- Account Number -->
            <div class="form-group">
                <label for="accountNumber"><spring:theme code="text.account.billing.pay.modal.account.number"/></label>
                <input type="text" class="form-control validate-input" pattern="[0-9]*" id="accountNumber"
                       name="accountNumber" ng-model="accountNumber" ng-keypress="isNumeric($event, 99)" required>
                <!-- Messages -->
                <span class="error"
                      ng-show="eftForm.accountNumber.$error.pattern || eftForm.accountNumber.$error.maxlength">Invalid account details</span>
                <span class="error" ng-show="eftForm.submitted && eftForm.accountNumber.$error.required">Required</span>
            </div>

            <!-- Account name -->
            <div class="form-group">
                <label for="nameOnAccount"><spring:theme code="text.account.billing.pay.modal.account.name"/></label>
                <input type="text" class="form-control validate-input" pattern="[a-zA-Z \-']*" id="nameOnAccount"
                       name="accountName" ng-model="accountName" maxlength="34" required>
                <!-- Messages -->
                <span class="error"
                      ng-show="eftForm.accountName.$error.pattern || eftForm.accountName.$error.maxlength">Invalid account details</span>
                <span class="error" ng-show="eftForm.submitted && eftForm.accountName.$error.required">Required</span>
            </div>

            <p><spring:theme code="text.account.billing.pay.safe.cue"/></p>

            <div class="checkbox clearfix">
                <input id="agreeTerms" ng-model="agreeTerms" type="checkbox" class="form" required/>
                <label for="agreeTerms">
                    <spring:theme code="text.billing.agree.terms" arguments="${eftTermsAndCondLink}"/></label>
                <span class="error" ng-show="eftForm.submitted && !agreeTerms">Required</span>
            </div>

            <input type="submit" id="billingSubmitEFTPayment" class="hidden" />

        </form>
        <button id="billingButtonEFTPayment" ng-click="eftForm.submitted = true" ng-disabled="eftForm.$invalid" class="btn btn-primary processButton">
            <spring:theme code="text.account.billing.pay.make"/></button>
    </div>

    <div class="margin-top-20">
        <span class="inline" onclick="$.magnificPopup.close()">Cancel</span>
    </div>

</div>