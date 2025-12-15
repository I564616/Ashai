<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<template:page pageTitle="${pageTitle}">
	<script id="dealsData" type="text/json">${ycommerce:generateJson(deals)}</script>
	 <%--<deals:dummyData />  --%>
	<div id="globalMessages">
		<common:globalMessages/>
		<div id="errorSaveToTemplate" class="alert negative hidden"><spring:theme code="text.product.save.to.template.error" /></div>
	</div>
	<cms:pageSlot position="Section1" var="comp" element="div" class="section1 cms_disp-img_slot">
		<cms:component component="${comp}"/>
	</cms:pageSlot>
	<product:productDetailsPanel product="${product}" galleryImages="${galleryImages}"/>
	<cms:pageSlot position="Section3" var="feature" element="div" class="section3 cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

	<product:productAccordion />
	<cms:pageSlot position="Section4" var="feature" element="div" class="section4 cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
</template:page>