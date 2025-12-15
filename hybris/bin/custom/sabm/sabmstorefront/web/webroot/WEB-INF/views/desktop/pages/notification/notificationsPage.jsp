<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="business" tagdir="/WEB-INF/tags/desktop/business"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<template:page pageTitle="${pageTitle}">
	<div id="simulationErrors">
    </div>
    <div id="globalMessages">
        <common:globalMessages/>
    </div>
     <div id="successSavingNotifications">
             <div class="successSavingNotifications recommendation-message"><spring:theme code="text.notification.save.success" arguments="${user.currentB2BUnit.name}"/></div>
     </div>
    <cms:pageSlot position="TopContentSlot" var="feature">
        <cms:component component="${feature}"/>
    </cms:pageSlot>
    <business:notifications/>

	<nav:backToTop/>
</template:page>
