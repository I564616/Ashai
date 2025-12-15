<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="single-checkout" tagdir="/WEB-INF/tags/responsive/checkout/single"%>

<spring:url value="/my-account/saved-cards" var="savedCardsUrl" htmlEscape="false"/>
<p id="ptest"><b><spring:theme code="checkout.summary.payment.method" /></b></p>
	<div class="radiobuttons_paymentselection">
			<c:if test="${not empty accErrorMsgs}">
				<c:forEach items="${accErrorMsgs}" var="msg">
					<c:choose>
						<c:when test="${'checkout.error.credit.block' eq msg.code}">
							<input type="hidden" name ="creditBlockError" value="${msg.code}"/>
						</c:when>
						<c:otherwise>
                            <input type="hidden" name ="creditCardError" value="${msg.code}"/>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</c:if>

        <c:forEach items="${paymentTypes}" var="paymentType" varStatus="loop">
             <p class="payment-option">
                <input name="paymentType" type="radio" id="PaymentTypeSelection_${paymentType.code}" value="${paymentType.code}" label="${paymentType.displayName}" <c:if test="${loop.index == 0}">checked="checked"</c:if>>
                <label for='PaymentTypeSelection_${paymentType.code}'><spring:theme code="checkout.summary.payment.type.${paymentType.code}.${cmsSite.uid}"/></label>
            	<c:if test="${cmsSite.uid eq 'sga'}"><span class="payment-info-icon" title='<spring:theme code="payment.method.tooltip.info.${paymentType.code}" />'>i</span></c:if>
            </p>
            <c:if test="${paymentType.code eq 'CARD'}">
                <single-checkout:creditCardForm savedCardsUrl="${savedCardsUrl}"/>
            </c:if>
        </c:forEach>
        <input type="hidden" id="paymentMethod" name="paymentMethod" value="ACCOUNT" />
        <input type="hidden" id="updateIframe" value="${updateIframe}" />
	</div>
