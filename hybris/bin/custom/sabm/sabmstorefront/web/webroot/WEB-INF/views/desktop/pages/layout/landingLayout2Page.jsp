<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>

<c:if test="${not empty notifications}">
<div class="home-notifications">
	<c:forEach items="${notifications}" var="msg">
		<div class="home-notification-box" >${msg.text}
				<span class="remove-message">
                  <a class="hide-notification" id="${msg.code}" href="">
                    <svg class="icon-cross">
                        <use xlink:href="#icon-cross"></use>    
                    </svg>
                  </a>
			</span>
		</div>		
	</c:forEach>
</div>
</c:if>


	<cms:pageSlot position="Section1" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	

	<cms:pageSlot position="Section2A" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

	<cms:pageSlot position="Section2B" var="feature" element="div">
        <cms:component component="${feature}"/>
    </cms:pageSlot>

	<!-- Track Your Order - Homepage Module -->
	<c:if test="${isTrackDeliveryOrderFeatureEnabled}">
		<home:homeTrackOrders />
	</c:if>

	<!-- Order History - Homepage Module -->
	<home:homeOrders /> 


	<cms:pageSlot position="Section3" var="feature" element="">
		<cms:component component="${feature}"/>
		<hr class="offset-bottom-large">
	</cms:pageSlot>
	<cms:pageSlot position="Section4" var="feature" element="div" class="row">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<hr>
	<cms:pageSlot position="Section5" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

</template:page>