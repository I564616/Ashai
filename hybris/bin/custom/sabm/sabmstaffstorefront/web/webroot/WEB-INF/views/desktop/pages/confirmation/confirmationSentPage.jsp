<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<template:page pageTitle="${pageTitle}">
	<div class="row">

		<div class="col-lg-8">
			<c:url value="/" var="homeUrl" scope="request"/>

			<h1 class="h1"><spring:theme code="text.page.confirmationSent.title" /></h1>

			<p class="offset-bottom-large">
				<spring:theme code="text.page.confirmationSent.staticText" />
			</p>

			<p>

				<c:url value="/backToCustomerSearchResults" var="backToSearchResultsUrl"/>

				<a href="${backToSearchResultsUrl}" class="inline"><spring:theme code="text.page.confirmationSent.backToSearchResults" /></a>
			</p>

			<p>
				<c:url value="/customer-search" var="backToCustomerSearchUrl" />
				<a href="${backToCustomerSearchUrl}" class="inline"><spring:theme code="text.page.confirmationSent.backToCustomerSearch" /></a>
			</p>

		</div>

	</div>
	
</template:page>