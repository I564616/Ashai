<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	<cms:pageSlot position="Section1" var="feature">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

	<div>
		<cms:pageSlot position="Section2" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</div>
	<cms:pageSlot position="Section3" var="feature">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<nav:backToTop />
</template:page>