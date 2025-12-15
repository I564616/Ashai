<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<spring:url value="/checkout/multi/summary/view" var="summaryViewUrl"/>

<template:page pageTitle="${pageTitle}">
	
	<div class="login-page__headline">
		<spring:theme code="about.us.page.title" />
	</div>
	
	<c:choose>
        <c:when test="${cmsSite.uid eq 'sga'}">
			<cms:pageSlot position="Section5" var="feature" element="div">
				<cms:component component="${feature}" element="div" class="yComponentWrapper"/>
			</cms:pageSlot>
		</c:when>
		<c:otherwise>
			<div class="row no-margin"> 
				<div class="checkout_subheading row-margin-fix">
					<spring:theme code="homepage.loggedout.btn.ourstory" />
				</div>
			</div>

			<cms:pageSlot position="Section5" var="feature" element="div">
				<cms:component component="${feature}" element="div" class="yComponentWrapper"/>
			</cms:pageSlot>

			<br>
			<div class="row no-margin"> 
				<div class="checkout_subheading row-margin-fix">
					<spring:theme code="homepage.loggedout.btn.onpremise" />
				</div>
			</div>

			<cms:pageSlot position="Section6" var="feature" element="div">
				<cms:component component="${feature}" element="div" class="yComponentWrapper"/>
			</cms:pageSlot>
		</c:otherwise>
	</c:choose>
	
</template:page>
 <div class="col-sm-4 col-md-4">
     <cms:pageSlot position="BodyContent" var="feature" element="div" class="">
        <cms:component component="${feature}" />
    </cms:pageSlot>
 </div>
