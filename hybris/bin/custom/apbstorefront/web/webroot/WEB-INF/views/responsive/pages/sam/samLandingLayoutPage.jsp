<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<spring:htmlEscape defaultHtmlEscape="false" />
	
<template:page pageTitle="${pageTitle}">
	
	<c:if test="${cmsPage.uid ne 'paymentdetail' && cmsPage.uid ne 'paymentConfirmation' && cmsPage.uid ne 'requestAccess'}">
		<div class="login-page__headline">
			<spring:theme code="text.invoices.payments.heading" />
		</div>
	
		
			<cms:pageSlot position="Section1" var="feature">
					<cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot"/>
			</cms:pageSlot>
	</c:if>
	
	<div class="row">
			<cms:pageSlot position="Section4" var="feature">
					<cms:component component="${feature}" />
			</cms:pageSlot>
			<cms:pageSlot position="Section3" var="feature">
					<cms:component component="${feature}" />
			</cms:pageSlot>
	</div>

</template:page>