<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose>
	<c:when test="${cmsSite.uid eq 'sga'}">
		<div class="checkout-success">
			<div class="checkout-success__body">
				<div class="checkout-success__body__headline">
					<img src="${commonResourcePath}/images/done.png"  />  <b><spring:theme code="sga.checkout.orderConfirmation.thankYouForOrder" /></b>
				</div>
				<p><spring:theme code="sga.order.confirmation.our.portal.order.line1"/><strong>&nbsp;${orderData.b2bCustomerData.uid}  </strong> </p>
				<p><spring:theme code="sga.checkout.orderConfirmation.line1"/></p>
				<p><spring:theme code="sga.checkout.orderConfirmation.line2.part1"/><span>   <a href="${contextPath}/my-account/orders"><spring:theme code="sga.checkout.orderConfirmation.line2.orderHistory"/></a> </span><spring:theme code="sga.checkout.orderConfirmation.line2.part2"/></p>
			</div>		
		</div>
	</c:when>
	<c:otherwise>
		<div class="checkout-success">
			<div class="checkout-success__body">
				<div class="checkout-success__body__headline">
					<img src="${commonResourcePath}/images/done.png"  />  <b><spring:theme code="checkout.orderConfirmation.thankYouForOrder" /></b>
				</div>
				<p><spring:theme code="order.confirmation.our.portal.order.id"/><strong>&nbsp;${orderCode}</strong> </p>
				<p><spring:theme code="checkout.orderConfirmation.line1"/></p>
				<p><spring:theme code="checkout.orderConfirmation.line2.part1"/><span>   <a href="${contextPath}/my-account/orders"><spring:theme code="checkout.orderConfirmation.line2.orderHistory"/></a> </span><spring:theme code="checkout.orderConfirmation.line2.part2"/></p>
			</div>		
		</div>
	</c:otherwise>
</c:choose>


