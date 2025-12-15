<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="trackorder" tagdir="/WEB-INF/tags/desktop/order"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>


<!-- number count of shipment -->
<c:set var="shipmentCount" scope="session" value="${trackOrderData.size()}"/>
<c:set var="count" scope="request" value="0"/>

<c:if test="${not empty trackOrderData}">
	<div class="row">
		<div class="col-xs-12">
			<h2><spring:theme code="text.trackorder.header" /></h2>
			<h2><spring:theme code="text.trackorder.orderno" arguments="${trackOrderData[0].orderCode}"/></h2>
			<p><spring:theme code="text.trackorder.deliverydate"/> <fmt:formatDate value="${trackOrderData[0].requestedDeliveryDate}" pattern="dd/MM/yyyy"/>  </p>

			<!-- display if there's 2 or more shipments -->
			<c:if test="${shipmentCount > 1}">
				<spring:theme code="text.trackorder.shipment.notification" arguments="${shipmentCount}" />
			</c:if>
		</div>
	</div>


	<c:choose>
		<c:when test="${not empty trackOrderData}">
			<c:forEach items="${trackOrderData}" var="trackOrderData" varStatus="myIndex">
				<trackorder:trackOrderShipments shipment="${trackOrderData}">
				</trackorder:trackOrderShipments>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<spring:theme code="text.trackorder.empty" />
		</c:otherwise>
	</c:choose>

</c:if>

<c:if test="${empty trackOrderData}">
	<h2><spring:theme code="text.trackorder.empty" /></h2>
</c:if>

