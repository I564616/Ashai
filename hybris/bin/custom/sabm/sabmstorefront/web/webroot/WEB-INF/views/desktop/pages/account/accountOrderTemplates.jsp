<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="templatesOrder" tagdir="/WEB-INF/tags/desktop/templatesOrder"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<template:page pageTitle="${pageTitle}">
	<cms:pageSlot position="TopContentSlot" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	<div class="row">
		<div class="col-xs-12">
	  		<h1><spring:theme code="text.account.orderTemplates.title" /></h1>
	  		<div><spring:theme code="text.orderTemplate.description" /></div>
	  		<templatesOrder:templatesListSort/>
	  		<templatesOrder:templatesListItems/>
		</div>
  	</div>
	<cms:pageSlot position="BottomContentSlot" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<nav:backToTop/>
</template:page>