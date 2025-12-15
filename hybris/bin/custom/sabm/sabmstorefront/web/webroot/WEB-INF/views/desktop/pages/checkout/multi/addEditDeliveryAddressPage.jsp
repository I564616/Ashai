<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/desktop/checkout/multi" %>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<div class="row">
		<div class="col-md-10"><h1><spring:theme code="basket.page.checkout.checkout"/></h1></div>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="false"/>
</template:page>