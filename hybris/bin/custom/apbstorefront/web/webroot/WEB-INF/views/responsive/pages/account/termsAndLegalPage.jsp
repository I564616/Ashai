<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>

<spring:url value="/checkout/multi/summary/view" var="summaryViewUrl"/>

<template:page pageTitle="${pageTitle}">
    <div class="login-page__headline">
        <spring:theme code="terms.and.legal.page.title" />
    </div>
	<cms:pageSlot position="Section-2" var="feature" class="termsAndConditions-section" element="div">
		<cms:component component="${feature}" element="div" class="clearfix"/>
	</cms:pageSlot>

</template:page>
 <div class="col-sm-4 col-md-4">
     <cms:pageSlot position="BodyContent" var="feature" element="div" class="">
        <cms:component component="${feature}" />
    </cms:pageSlot>
 </div>
