<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<template:page pageTitle="${pageTitle}">
	<div class="row">
		<div id="globalMessage" class="col-xs-12">
			<common:globalMessages/>
		</div> 
	</div>
	<div class="row relative">

		
		<%-- <div class="col-xs-12">
	    	<h1><spring:theme code="login.title"/></h1>
	  	</div> --%>
		
		<cms:pageSlot position="RightContentSlot" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
		
		<div class="col-md-6 col-md-pull-4 login-features">
			<cms:pageSlot position="LeftContentSlot" var="feature" element="div" class="login-banner clearfix">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div>
	</div>

</template:page>