<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="msfTotal" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ attribute name="isInvoice" required="true" type="java.lang.Boolean"%>


<div class="price-popup mfp-hide" id="checkout-msf-popup">
    <h2 class="h1"><spring:theme code="text.pay.card.modal.title"/></h2>

    <p>
    <c:choose>
        <c:when test="${isInvoice == true}">
			<spring:theme code="text.pay.card.modal.invoiceDescription1"/> <span id="surcharge"></span> <spring:theme code="text.pay.card.modal.invoiceDescription2"/>
        </c:when>
        <c:otherwise>
			<spring:theme code="text.pay.card.modal.description1"/> <span id="surcharge"></span> <spring:theme code="text.pay.card.modal.description2"/>
        </c:otherwise>
    </c:choose>
	</p>
	
	<div>
	    <div class="row">
	        <div class="col-md-6 col-xs-6 col-sm-7">
	            <span class="col-md-offset-3 col-sm-offset-3"><spring:theme code="text.checkout.pay.card.model.orderAmount"/></span>
	        </div>
	        <div class="col-md-3 col-xs-5 col-sm-3 text-right">
	            <span id="amount"></span>
	        </div>
	    </div>
	    <div class="row">
	        <div class="col-md-6 col-xs-6 col-sm-7">
	            <span class="col-md-offset-3 col-sm-offset-3"><spring:theme code="text.checkout.pay.card.model.msf"/></span>
	        </div>
	        <div class="col-md-3 col-xs-5 col-sm-3 text-right">
	            <span id="msf"></span>
	        </div>
	    </div>
	    <div class="row bold">
	        <div class="col-md-6 col-xs-6 col-sm-7">
	            <span class="col-md-offset-3 col-sm-offset-3"><spring:theme code="text.checkout.pay.card.model.totalAmount"/></span>
	        </div>
	        <div class="col-md-3 col-xs-5 col-sm-3 text-right">
	            <span id="totalAmount"></span>
	        </div>
	    </div>
	</div>
	<br>
	
	<c:choose>
		<c:when test="${isInvoice eq true}">
			<p class="bold"><spring:theme code="text.pay.card.modal.invoiceNote"/></p>
		</c:when>
		<c:otherwise>
			<p class="bold"><spring:theme code="text.pay.card.modal.note"/></p>
		</c:otherwise>
	</c:choose>
	<br />
    <div class="row">
    	<div class="col-md-6 col-sm-6 hidden-xs">
    		<a href="javascript:void(0)" onclick="window.location.reload(); $.magnificPopup.close();" class="cancelMerchantService inline"><spring:theme code="text.checkout.pay.card.model.cancel" /></a>
    	</div>
    	<div class="col-md-6 col-sm-6 text-right">
    		<c:choose>
    		        <c:when test="${isInvoice == true}">
				    	<input type="button" class="btn btn-primary billing-msf-popup-button" value="<spring:theme code="text.checkout.pay.card.model.confirm"/>" /><br/>
    				</c:when>
    				<c:otherwise>
				    	<input type="button" class="btn btn-primary checkout-msf-popup-button" value="<spring:theme code="text.checkout.pay.card.model.confirm"/>" /><br/>
    				</c:otherwise>
    		</c:choose>
    	</div>
    </div>
    
    <div class="row">
    	<div class="col-xs-12 text-center hidden-sm hidden-md hidden-lg">
		  	<a href="javascript:void(0)" onclick="window.location.reload(); $.magnificPopup.close();" class="cancelMerchantService inline"><spring:theme code="text.checkout.pay.card.model.cancel" /></a>
    	</div>
    </div>
    
 </div>