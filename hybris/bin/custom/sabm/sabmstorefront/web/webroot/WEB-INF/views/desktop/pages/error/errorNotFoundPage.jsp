<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<template:page pageTitle="${pageTitle}">
	<div class="margin-top-20">
   <c:url value="/" var="homeUrl" scope="request"/>
     <a href="${homeUrl }" class="btn btn-primary"><spring:theme code="text.page.notFound.home" /></a>
	</div>
	<cms:pageSlot position="MiddleContent" var="comp" element="div" class="span-20">
		<cms:component component="${comp}"/>
	</cms:pageSlot>
	<cms:pageSlot position="BottomContent" var="comp" element="div" class="">
		<cms:component component="${comp}"/>
	</cms:pageSlot>
	<cms:pageSlot position="SideContent" var="feature" element="div" class="span-4 narrow-content-slot cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
</template:page>