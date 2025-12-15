<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="recommendation" tagdir="/WEB-INF/tags/desktop/recommendation"%>

<template:page pageTitle="${pageTitle}">
	<cms:pageSlot position="TopContentSlot" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	<cms:pageSlot position="BottomContentSlot" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<nav:backToTop/>
</template:page>
