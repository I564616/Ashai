
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<c:url value="/your-business/orderAdd/" var="orderAddToCartUrl" scope="session"/>
	<input type="hidden" class="orderAddToCartUrl" value="${orderAddToCartUrl }">

	<cms:pageSlot position="TopContent" var="feature" element="div" class="accountTopContentSlot">
		<cms:component component="${feature}" element="div" class="clearfix" />
	</cms:pageSlot>
	<cms:pageSlot position="BodyContent" var="feature" element="div" class="accountBodyContentSlot">
		<cms:component component="${feature}" element="div" class="clearfix" />
	</cms:pageSlot>
	<cms:pageSlot position="BottomContent" var="feature" element="div" class="accountBottomContentSlot">
		<cms:component component="${feature}" element="div" class="clearfix" />
	</cms:pageSlot>
</template:page>