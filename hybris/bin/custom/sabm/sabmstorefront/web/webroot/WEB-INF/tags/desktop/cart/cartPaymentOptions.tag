<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<div class="row">
	<div class="col-md-12">
		<form method="post" name="ccForm" id="ccFormId" novalidate>
			<div>
				<c:choose>
					<c:when test="${isCashOnlyCustomer }">
					               
						<h2>
							<spring:theme code="text.cart.payment.title.onlca" />
						</h2>
 						<div class="form-group">
                    <label for="poNumber"><spring:theme code="text.cart.payment.po"/></label>
                    <input type="text" class="form-control" id="poNumber">
                	</div>
						<div class="card-carddetailsOnlca">
							<input type="text" class="form-control hidden communityCodeInput"
								id="communityCodeId" name="communityCode" value=""> <input
								type="text" class="form-control hidden tokenInput"
								id="tokenNumber" name="token" value=""> <input
								type="text" class="form-control hidden ignoreDuplicateInput"
								id="ignoreDuplicateId" name="ignoreDuplicate" value="">
							<input type="text" class="form-control hidden"
								id="customPONumberId" name="customPONumber" value="">
							<div class="form-group">
								<label for="cardNumber"><spring:theme
										code="text.cart.payment.cardnumber" /></label> <input type="text"
									pattern="[0-9]*" ng-model="cardNumber"
									class="form-control validate-input" id="cardNumber"
									name="creditCardNumber" ng-maxlength="19" required> <span
									class="error" ng-show="ccForm.creditCardNumber.$error.pattern"><spring:theme
										code="text.checkout.cc.error.numbers" /></span> <span class="error"
									ng-show="ccForm.creditCardNumber.$error.maxlength"><spring:theme
										code="text.account.billing.pay.message.invalid.card" /></span> <span
									class="error"
									ng-show="ccForm.submitted && ccForm.creditCardNumber.$error.required"><spring:theme
										code="form.field.required" /></span>
							</div>
							<div class="form-group">
								<label for="nameOnCard"><spring:theme
										code="text.account.billing.pay.modal.card.name" /></label> <input
									type="text" pattern="[a-zA-Z \-']*"
									class="form-control validate-input" ng-model="cardName"
									id="nameOnCard" name="cardholderName" ng-maxlength="200"
									required> <span class="error"
									ng-show="ccForm.cardholderName.$error.pattern || ccForm.cardholderName.$error.maxlength"><spring:theme
										code="text.account.billing.pay.message.invalid.card" /></span> <span
									class="error"
									ng-show="ccForm.submitted && ccForm.cardholderName.$error.required"><spring:theme
										code="form.field.required" /></span>
							</div>
							<div class="expiry-security form-group clearfix">
								<div>
									<label class="expiry"><spring:theme
											code="text.account.billing.pay.modal.expiry.date" /></label> <label
										class="security" for="securityCode"><spring:theme
											code="text.account.billing.pay.modal.security.code" /></label>
								</div>
								<div class="select-list expiry">
									<div class="day pull-left">
										<div class="select">
											<span class="arrow"></span> <select name="expiryDateMonth"
												ng-model="expiryDateMonth"
												class="cc-expiry-form form-control validate-input" required>
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
											</select>
										</div>
									</div>
									<span class="pull-left slash-sign">/</span>
									<div class="month pull-left">
										<div class="select">
											<span class="arrow"></span> <select name="expiryDateYear"
												ng-model="expiryDateYear"
												class="cc-expiry-form js-expiry-year form-control validate-input"
												required>
												<option value="" disabled selected>YY</option>
											</select>
										</div>
									</div>
								</div>
								<div class="security">
									<input type="text" pattern="[0-9]*"
										class="form-control validate-input" id="securityCode"
										name="cvn" ng-model="cvn" ng-maxlength="4" required>
								</div>
								<div class="clearfix"></div>
								<span class="error"
									ng-show="ccForm.cvn.$error.pattern || ccForm.cvn.$error.maxlength"><spring:theme
										code="text.account.billing.pay.message.invalid.security.code" /></span>
								<span class="error"
									ng-show="ccForm.submitted && ccForm.cvn.$error.required"><spring:theme
										code="text.account.billing.pay.message.security.code.required" /></span>
								<span class="error"
									ng-show="ccForm.submitted && ccForm.expiryDateMonth.$viewValue === undefined || ccForm.submitted && ccForm.expiryDateYear.$viewValue === undefined"><spring:theme
										code="text.account.billing.pay.message.invalid.expiry" /></span>
							</div>
							<span><spring:theme code="text.account.billing.pay.accept" /></span>
							<ul class="list-inline">
								<li><img src="${staticHostPath}/_ui/desktop/SABMiller/img/visa.png"
									alt="Visa"></li>
								<li><img src="${staticHostPath}/_ui/desktop/SABMiller/img/mastercard.png"
									alt="Mastercard"></li>
								<li><img src="${staticHostPath}/_ui/desktop/SABMiller/img/amex.png"
									alt="AMEX"></li>
							</ul>
							<span><spring:theme
									code="text.account.billing.pay.credit.card.payments.cue" /></span>
						</div>
					</c:when>
					<c:otherwise>
						<h2>
							<spring:theme code="text.cart.payment.title" />
						</h2>
						<div class="form-group">
							<label for="poNuber"><spring:theme
									code="text.cart.payment.po" /></label>
							<input type="text" class="form-control">
						</div>
						<div class="radio">
							<input id="payByAccount" type="radio" checked="checked">
							<label for="payByAccount" class="h3"><spring:theme
									code="text.cart.payment.by.account" /></label> <input id="payByCard"
								type="radio"> <label for="payByCard" class="h3"><spring:theme
									code="text.cart.payment.by.card" /></label>
						</div>
						
						
						
					</c:otherwise>
				</c:choose>
			</div>
		</form>
	</div>
</div>