<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:url value="/your-business/update-profile" var="updateProfileUrl"/>
<spring:url value="/your-business/update-password" var="updatePasswordUrl"/>
<spring:url value="/your-business/update-email" var="updateEmailUrl"/>
<spring:url value="/your-business/address-book" var="addressBookUrl"/>
<spring:url value="/your-business/payment-details" var="paymentDetailsUrl"/>
<spring:url value="/your-business/orders" var="ordersUrl"/>

<template:page pageTitle="${pageTitle}">
	<div class="row">
		<cms:pageSlot position="SideContent" var="feature" class="accountPageSideContent">
			<cms:component component="${feature}" />
		</cms:pageSlot>
		<div class="col-md-9 col-lg-10">
			<cms:pageSlot position="TopContent" var="feature" element="div" class="accountPageTopContent">
				<cms:component component="${feature}" />
			</cms:pageSlot>
			<div class="account-section">
				<cms:pageSlot position="BodyContent" var="feature" element="div" class="accountPageBodyContent">
					<cms:component component="${feature}" />
				</cms:pageSlot>
			</div>
			<cms:pageSlot position="BottomContent" var="feature" element="div" class="accountPageBottomContent">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</div>
</template:page>