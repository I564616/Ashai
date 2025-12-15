<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="storepickup"
	tagdir="/WEB-INF/tags/desktop/storepickup"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages />
	</div>

	<nav:listTopRow searchPageData="${searchPageData}"/>

	<%-- <div class="search-results-top">
		<h1 class="offset-bottom-small"><spring:theme code="search.page.searchText" arguments="${searchPageData.freeTextSearch}"/></h1>
		<h2 class="offset-bottom-large"><spring:theme code="search.page.totalResults" arguments="${searchPageData.pagination.totalNumberOfResults}"/></h2>
	</div>
	<div class="row">
		<div class="col-xs-6 visible-xs-block visible-sm-block trim-right-5 select-list">
			<div class="select-btn list-refine-btn">
				<spring:theme code="text.product.refine" />
			</div>
		</div>
	</div>
	 --%>
	
	
	<div class="row page-productList">
		<div class="col-md-3 list-filter">

		<span class="num-products visible-md-block visible-lg-block">
			<label class="h3"><spring:theme code="${'search.page'}.totalResults" arguments="${searchPageData.pagination.totalNumberOfResults},${searchPageData.pagination.totalNumberOfResults >1 ?'s':''}" /></label>
		</span>

			<cms:pageSlot position="ProductLeftRefinements" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
		<div class="col-md-9 list-listing" id="resultsList">
			<cms:pageSlot position="SearchResultsListSlot" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</div>

	<storepickup:pickupStorePopup />
</template:page>

<nav:resultsListJQueryTemplates/>
<product:productPricePopup/>