<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="category" tagdir="/WEB-INF/tags/responsive/category"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>


<template:page pageTitle="${pageTitle}">
	<input id="productListPageCupLoad" data-cupRefreshInProgress="${cupRefreshInProgress}" type="hidden"/> 

	<div id="globalMessages">
		<common:globalMessages />
	</div>
	
	<div class="banner-image banner-top banner-image-desktop">
		<category:categoryBannerImage categoryData="${categoryData}" format="desktop" />
	</div>
	<div class="banner-image banner-top banner-image-mobile">
		<category:categoryBannerImage categoryData="${categoryData}" format="mobile" />
	</div>
	<cms:pageSlot position="Section1" var="feature">
		<cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot" />
	</cms:pageSlot>
	<nav:listTopRow searchPageData="${searchPageData}" />

	<div class="row">
		<div class="col-md-3 list-filter">

		<span class="num-products visible-md-block visible-lg-block">
			<label class="h3"><spring:theme code="${'search.page'}.totalResults" arguments="${searchPageData.pagination.totalNumberOfResults},${searchPageData.pagination.totalNumberOfResults >1 ?'s':''}" /></label>
		</span>

			<cms:pageSlot position="ProductLeftRefinements" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
		<div class="col-md-9 list-listing" id="resultsList">
			 <cms:pageSlot position="ProductListSlot" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot> 
		</div>
		
	</div>
	<nav:resultsListJQueryTemplates/>
</template:page>

<product:productPricePopup/>