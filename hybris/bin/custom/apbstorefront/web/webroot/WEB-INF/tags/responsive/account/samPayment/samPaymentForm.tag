<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spring:url value="/my-account/saved-cards" var="savedCardsUrl" htmlEscape="false"/>
<%@ taglib prefix="single-checkout" tagdir="/WEB-INF/tags/responsive/checkout/single"%>
<div class="sam-card-payment-section">
	<c:if test="${cmsPage.uid ne 'directdebit'}">
		<div class="credit-card-label">
			<spring:theme code="sga.homepage.payment.credit.card" />
		</div>
	</c:if>
	<div class="radiobuttons_paymentselection">
		<c:if test="${not empty accErrorMsgs}">
			<c:forEach items="${accErrorMsgs}" var="msg">
				<c:choose>
					<c:when test="${'checkout.error.credit.block' eq msg.code}">
						<input type="hidden" name="creditBlockError" value="${msg.code}" />
					</c:when>
					<c:otherwise>
						<input type="hidden" name="creditCardError" value="${msg.code}" />
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:if>
		<c:forEach items="${paymentTypes}" var="paymentType">
			<p><input name="paymentType" type="radio" id="PaymentTypeSelection_${paymentType.code}" value="${paymentType.code}" label="${paymentType.displayName}">
				<label for='PaymentTypeSelection_${paymentType.code}'>
					<spring:theme code="checkout.summary.payment.type.${paymentType.code}" /></label>
			</p>
		</c:forEach>
		<input type="hidden" id="paymentMethod" name="paymentMethod" value="CARD" />

		<c:choose>
			<c:when test="${cmsPage.uid ne 'directdebit'}">
				
				<input type="hidden" id="numberOfCards" name="numberOfCards" value="${creditCards.size()}" />
				<input type="hidden" id="maxNumberOfCards" name="maxNumberOfCards" value="${maxNumberOfCards}" />
				<input type="hidden" id="isSurchargeAdded" name="isSurchargeAdded" value="${isAddSurcharge}" />
				<!--	This is for the Make a Payment and Checkout Page	-->
				<div id="creditCardSection">
					<div class="checkout-card-payment-section">
						<div class="card-with-surcharge">
							<p>
								<spring:theme code="checkout.payment.pay.securely" />
							</p>
							<c:choose>
								<c:when test="${visaSurcharge == masterSurcharge}">
									<p>
										<spring:theme code="checkout.payment.visa.master.surcharge" />: ${visaSurcharge}%</p>
								</c:when>
								<c:otherwise>
									<p>
										<spring:theme code="checkout.payment.visa.surcharge" />: ${visaSurcharge}%</p>
									<p>
										<spring:theme code="checkout.payment.master.surcharge" />: ${masterSurcharge}%</p>
								</c:otherwise>
							</c:choose>
							<p>
								<spring:theme code="checkout.payment.amex.surcharge" />: ${amexSurcharge}%</p>
						</div>
						<div class="card-without-surcharge">
						</div>
						<div class="card-payment-content">

							<c:forEach items="${creditCards}" var="creditCard">
								<%--<input name="xx" type="radio" >
				    		${creditCard.cardType} ending in  ${creditCard.cardNumber} (${creditCard.expiryMonth}/${creditCard.expiryYear})
				     <br> --%>
								<div class="checkout-card-list">
									<input name="cardDetails" type="radio" class="checkout-card-item credit-card-normal" id="checkout-card-item_${creditCard.cardType}" value="${creditCard.cardType}" data-token="${creditCard.token}">
									<c:choose>
										<c:when test="${creditCard.cardType eq 'amex'}">
											<c:set var="creditCardType" value="AMEX" />
										</c:when>
										<c:when test="${creditCard.cardType eq 'master'}">
											<c:set var="creditCardType" value="Mastercard" />
										</c:when>
										<c:when test="${creditCard.cardType eq 'visa'}">
											<c:set var="creditCardType" value="Visa" />
										</c:when>
										<c:otherwise>
											<c:set var="creditCardType" value="" />
										</c:otherwise>
									</c:choose>
									<label class="card_label credit-card-normal">&nbsp;&nbsp;<img src="${commonResourcePath}/images/${creditCard.cardType}.png" />&nbsp;&nbsp;${creditCardType} ending in ${creditCard.cardNumber} (${creditCard.expiryMonth}/${creditCard.expiryYear})</label>
								</div>
							</c:forEach>
						</div>
						<div class="accountActions-bottom hide">
							<a href="${savedCardsUrl}">Manage Saved Cards</a>
						</div>

						<div class="checkout-add-new-card hide">
							<div class="add-card-type">
								<p><b>Select Card Type</b></p>
								<input type="radio" name="apbCreditCardType" id="asahiCreditCardTypeVISA" value="VISA" />
								<label for="asahiCreditCardTypeVISA"><img src="${commonResourcePath}/images/visa.png" /></label>
								<input type="radio" name="apbCreditCardType" id="asahiCreditCardTypeMASTER" value="MASTERCARD" />
								<label for="asahiCreditCardTypeMASTER"><img src="${commonResourcePath}/images/master.png" /></label>
								<input type="radio" name="apbCreditCardType" id="asahiCreditCardTypeAMEX" value="AMEX" />
								<label for="asahiCreditCardTypeAMEX"><img src="${commonResourcePath}/images/amex.png" /></label>
							</div>
							<div id="iframePaymentSection">
								<iframe id="checkoutIframe" name="my_iframe" class="iframe-content" src="${iframePostUrl}" frameBorder="0"></iframe>
							</div>
							<div class="row">
								<div class="col-sm-6 col-md-3">
									<single-checkout:securityCode />
								</div>
								<div class="col-sm-12 col-md-12">
									<formElement:formCheckbox idKey="saveCreditCard" labelCSS="checkout-keg-checkbox" labelKey="checkout.summary.save.card.check.message" path="saveCreditCard" />
								</div>

							</div>


						</div>
					</div>
				</div>

			</c:when>
			<c:otherwise>

				<!--	This is for the Direct Debit Page	-->
				<div id="creditCardSection">
					<div class="checkout-card-payment-section">
						<input name="cardDetails" type="radio" class="checkout-card-item credit-card-normal hidden" id="checkout-card-item" value="addNewCard" checked>
						<div class="card-without-surcharge">
						</div>
						<div class="checkout-add-new-card">
							<div id="iframePaymentSection">
								<iframe id="checkoutIframe" name="my_iframe" class="iframe-content" src="${iframePostUrl}" frameBorder="0"></iframe>
							</div>
							<div class="row">
								<div class="col-sm-6 col-md-3">
									<single-checkout:securityCode />
								</div>
							</div>
						</div>
					</div>
				</div>

			</c:otherwise>
		</c:choose>
	</div>
</div>