<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="msfTotal" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ attribute name="paymentAmount" required="true" type="java.math.BigDecimal"%>
<%@ attribute name="isInvoice" required="true" type="java.lang.Boolean"%>

<div class="price-popup checkout-pay-credit-popup mfp-hide" id="merchant-service-fee">
    <h2 class="h1"><spring:theme code="text.checkout.pay.card.model.title"/></h2>

    <c:choose>
        <c:when test="${isInvoice eq true}">
            <msfTotal:msfTotal description="text.checkout.pay.card.model.invoiceDescription" amountLabel="text.checkout.pay.card.model.invoiceAmount" note="text.checkout.pay.card.model.invoiceNote" paymentAmount="${paymentAmount}"/>
        </c:when>
        <c:otherwise>
            <msfTotal:msfTotal description="text.checkout.pay.card.model.description" amountLabel="text.checkout.pay.card.model.orderAmount" note="text.checkout.pay.card.model.note" paymentAmount="${paymentAmount}"/>
        </c:otherwise>
    </c:choose>

    <br>
    <div class="row">
    	<div class="col-sm-6">
    		<a href="javascript:(function(){$.magnificPopup.close(); $('#payByCard').click();})();" class="cancelMerchantService inline"><spring:theme code="text.checkout.pay.card.model.cancel" /></a>
    	</div>
    	<div class="col-sm-6 text-right">
    		<form method="post" action="${ccPostUrl}">
	    		<input class="hidden" name="actionContextId" value="${actionContextId}">
	    		<input class="hidden" name="communityCode" value="${communityCode}">
	    		<input class="hidden" name="action" value="${action}">
	    		<input class="hidden" name="acceptSurcharge" value="true">
	    		<input type="submit" class="btn btn-primary" value="<spring:theme code="text.checkout.pay.card.model.confirm"/>" /><br/>
    		</form>
    	</div>
    </div>
    <c:if test='${not empty surchargeAmount}'>
		<input id="showMSFPopup" class="hidden"/>
	</c:if>
</div>