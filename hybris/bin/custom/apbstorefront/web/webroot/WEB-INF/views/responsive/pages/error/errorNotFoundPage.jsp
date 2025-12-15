<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>

<template:page pageTitle="${pageTitle}">
	
	<c:url value="/" var="homePageUrl" />

	<div class="error-page">
	<cms:pageSlot position="MiddleContent" var="comp" element="div" class="errorNotFoundPageMiddle">
		<cms:component component="${comp}" element="div" class="errorNotFoundPageMiddle-component" />
	</cms:pageSlot>
	
	<cms:pageSlot position="BottomContent" var="comp" element="div" class="errorNotFoundPageBottom">
		<cms:component component="${comp}" element="div" class="errorNotFoundPageBottom-component"/>
	</cms:pageSlot>
	
	<cms:pageSlot position="SideContent" var="feature" element="div" class="errorNotFoundPageSide">
		<cms:component component="${feature}" element="div" class="errorNotFoundPageSide-component"/>
	</cms:pageSlot>
	</div>
	

</template:page>