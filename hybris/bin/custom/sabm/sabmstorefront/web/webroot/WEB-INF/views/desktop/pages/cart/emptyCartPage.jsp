<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<template:page pageTitle="${pageTitle}">
	<spring:theme code="basket.add.to.cart" var="basketAddToCart" />
	<div id="globalMessages">
		<common:globalMessages/>
	</div>

	<cms:pageSlot position="TopContentSlot" var="feature" element="div">
		<cms:component component="${feature}" />
	</cms:pageSlot>
	<c:url value="/deals" var="continueShoppingUrl" scope="session" />
	<div class="row">
		<div class="col-md-12">
			<h1><spring:theme code="text.empty.cart.title" /></h1>
			<div>
				<spring:theme code="text.empty.cart.description" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-3 offset-bottom-medium margin-top-20">
			<a class="btn btn-primary" href="${continueShoppingUrl}"><spring:theme code="text.empty.cart.browse.products" /></a>
		</div>
	</div>
	<home:homeOrders />

	<div class="row">
		<div class="col-md-6">
			<cms:pageSlot position="CenterLeftContentSlot" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
		<div class="col-md-6">
			<cms:pageSlot position="CenterRightContentSlot" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</div>
	<cms:pageSlot position="BottomContentSlot" var="feature" element="div"
		class="span-24">
		<cms:component component="${feature}" />
	</cms:pageSlot>

</template:page>
