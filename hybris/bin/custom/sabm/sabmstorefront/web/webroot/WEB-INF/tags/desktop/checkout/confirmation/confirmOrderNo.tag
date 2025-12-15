<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/your-business/order/${orderData.code}" var="orderInfo" />

<div class="row">
	<div class="col-xs-12">
		 <h2><spring:theme code="checkout.orderConfirmation.orderNumber"/></h2>
         <div class="offset-bottom-small relative clearfix">
            <h3><a class="inline" href="${orderInfo}"><spring:theme code="checkout.orderConfirmation.code" arguments="${orderData.sapSalesOrderNumber}"/></a></h3>
         </div>
	</div>
</div>