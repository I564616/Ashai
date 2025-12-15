<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="templatesOrder" tagdir="/WEB-INF/tags/desktop/templatesOrder"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>

<template:page pageTitle="${pageTitle}">
	<cms:pageSlot position="TopContentSlot" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	<div id="errorSavingTemplate">
			<div class="errorSavingTemplate alert negative" style="display: none;"><spring:theme code="text.orderTemplateDetail.save.error" /></div>
	</div>
	<div id="succesSavingTemplate">
			<div class="succesSavingTemplate alert positive" style="display: none;"><spring:theme code="text.orderTemplateDetail.save.succes" /></div>
	</div>
	<templatesOrder:orderTemplateDetail/>
	<cms:pageSlot position="BottomContentSlot" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<nav:backToTop/>
</template:page>
