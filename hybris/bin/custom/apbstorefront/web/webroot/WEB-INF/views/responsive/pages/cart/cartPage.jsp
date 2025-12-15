<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<script type="text/javascript">
var pageContextPath = "${pageContext.request.contextPath}";
</script>

<template:page pageTitle="${pageTitle}">
	<%--Removed and moved the contents to different jsp as per the requriement ACP-25 --%>
	<cart:cartValidation />
	<cart:cartPickupValidation />
	<input id="cartInclusionErrorType" value="${cartInclusionErrorType}" type="hidden">
	<div>
		<div>
			<cms:pageSlot position="TopContent" var="feature">
				<cms:component component="${feature}" element="div"
					class="yComponentWrapper" />
			</cms:pageSlot>
		</div>
		<cms:pageSlot position="BottomContentSlot" var="feature">
			<cms:component component="${feature}" element="div"
				class="yComponentWrapper" />
		</cms:pageSlot>
	
	</div>
</template:page>