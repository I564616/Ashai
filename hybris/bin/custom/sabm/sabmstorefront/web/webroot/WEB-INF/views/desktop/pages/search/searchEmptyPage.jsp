<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>


<c:url var="continueBrowsingUrl" value="/" />
<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<%-- <cms:pageSlot position="SideContent" var="feature" element="div" class="span-6 side-content-slot cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot> --%>
	<div class="" id="searchEmptyPage">
	    <span id="searchText" class="hidden">${searchPageData.freeTextSearch}</span>
		<div class="item_container_holder">
			<div class="title_holder">
				<h2>
				<c:choose>
				<c:when test="${not empty searchPageData.freeTextSearch}">
		      <spring:theme code="search.no.results_l" />${searchPageData.freeTextSearch}<spring:theme code="search.no.results_r" />
		      <h3><spring:theme code="search.no.results.description" /></h3>
		      <cms:pageSlot position="MiddleContent" var="comp" element="div" class="item_container">
				<cms:component component="${comp}"/>
			</cms:pageSlot>
				</c:when>
				<c:otherwise>
				<spring:theme code="search.no.results_E" text="You have searched for Empty Value" arguments="${searchPageData.freeTextSearch}"/><br></br>
				<a href="/" class="btn btn-primary"><spring:theme code="text.authenticated.contact.us.form.sent.btn" /></a>
				</c:otherwise>
			    
				</c:choose>
				</h2>
				
			</div>
			<%-- <div class="item_container">
				<nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" />
			</div> --%>
		</div>
		<cms:pageSlot position="BottomContent" var="comp" element="div" class="span-18 cms_disp-img_slot right last">
			<cms:component component="${comp}"/>
		</cms:pageSlot>
	</div>
</template:page>
