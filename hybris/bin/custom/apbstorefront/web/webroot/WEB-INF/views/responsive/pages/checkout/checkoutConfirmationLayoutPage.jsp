<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<template:page pageTitle="${pageTitle}">
	<cms:pageSlot position="TopContent" var="feature" element="div" class="accountPageTopContent">
        <cms:component component="${feature}" element="div" class="accountPageTopContent-component"/>
    </cms:pageSlot>
    
	<%-- Fix ALM #384, don't show below message to any customers.
    <c:if test="${cmsSite.uid eq 'sga'}">
    	<c:if test="${deliveryDateInvalid}">
			<div class="row">					
				<div class="col-md-12">
			<p class="checkout-deliveryDateInvalid-message">
			<spring:theme code="sga.checkout.orderConfirmation.deliveryDateInvalid"/></p><br>
					</div>
					</div>
		</c:if>
	</c:if>
	--%>
				
    <div class="row checkout-confirmation">
    	<div class="col-xs-12 col-sm-6 col-md-7">
    		<cms:pageSlot position="SideContent" var="feature" class="accountPageSideContent" element="div">
		        <cms:component component="${feature}" element="div" class="accountPageSideContent-component"/>
		    </cms:pageSlot>
    	</div>
	    <div class="col-xs-12 col-sm-6 col-md-5 ">
	        <cms:pageSlot position="BodyContent" var="feature" element="div" class="account-section-content checkout__confirmation__content">
	            <cms:component component="${feature}" element="div" class="checkout__confirmation__content--component"/>
	        </cms:pageSlot>
	    </div>
    </div>
    <cms:pageSlot position="BottomContent" var="feature" element="div" class="accountPageBottomContent">
        <cms:component component="${feature}" element="div" class="accountPageBottomContent-component"/>
    </cms:pageSlot>
</template:page>