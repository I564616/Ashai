<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="templatesOrder" tagdir="/WEB-INF/tags/desktop/templatesOrder"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="business" tagdir="/WEB-INF/tags/desktop/business"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>


<c:set var="tempImgPath" value="/_ui/desktop/SABMiller/img/" />

<template:page pageTitle="${pageTitle}">
	<cms:pageSlot position="TopContentSlot" var="feature" element="div" class="row">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	
	<div id="globalMessages">
		<common:globalMessages/>
		<c:if test="${not empty param.message}">
			<div class="global-message ${param.messageType }">
			  ${param.message }
			</div>
        </c:if>
	</div> 
	<business:businessUnitDetail/>
	 
	 <user:sendWelcomeEmailPopup />
	
	<cms:pageSlot position="BottomContentSlot" var="feature" element="div" >
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<nav:backToTop/>
</template:page>
