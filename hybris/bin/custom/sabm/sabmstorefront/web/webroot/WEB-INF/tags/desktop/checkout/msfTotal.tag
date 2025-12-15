<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ attribute name="description" required="true" type="java.lang.String"%>
<%@ attribute name="amountLabel" required="true" type="java.lang.String"%>
<%@ attribute name="paymentAmount" required="true" type="java.math.BigDecimal"%>
<%@ attribute name="note" required="true" type="java.lang.String"%>

<fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${surchargeAmount}" var="formattedSurcharge"/>

<p><spring:theme code="${description}" arguments="${formattedSurcharge}"/></p>

<div>
    <div class="row">
        <div class="col-md-6 col-xs-6 col-sm-7">
            <span class="col-md-offset-3 col-sm-offset-3"><spring:theme code="${amountLabel}"/></span>
        </div>
        <div class="col-md-3 col-xs-5 col-sm-3 text-right">
            <span>$<fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${paymentAmount}"/></span>
        </div>
    </div>
    <div class="row">
        <div class="col-md-6 col-xs-6 col-sm-7">
            <span class="col-md-offset-3 col-sm-offset-3"><spring:theme code="text.checkout.pay.card.model.msf"/></span>
        </div>
        <div class="col-md-3 col-xs-5 col-sm-3 text-right">
            <span>$${formattedSurcharge}</span>
        </div>
    </div>
    <div class="row bold">
        <div class="col-md-6 col-xs-6 col-sm-7">
            <span class="col-md-offset-3 col-sm-offset-3"><spring:theme code="text.checkout.pay.card.model.totalAmount"/></span>
        </div>
        <div class="col-md-3 col-xs-5 col-sm-3 text-right">
            <span>$<fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${paymentAmount + surchargeAmount}"/></span>
        </div>
    </div>
</div>
<br>
<p class="bold"><spring:theme code="${note}"/></p>


