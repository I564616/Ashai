<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageData" required="true"
	type="de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld"%>

<c:set var="facetCount" value="${fn:length(pageData.breadcrumbs)}" />

<c:if test="${not empty pageData.breadcrumbs}">
	<nav id="plp-filters-bar" class="navigation navigation--bottom js_navigation--bottom js-enquire-offcanvas-navigation" role="navigation">		<!--		ID has been added for SGA. Does not affect APB even if left as in.-->
		<ul class="sticky-nav-top hidden-lg hidden-md js-sticky-user-group hidden-md hidden-lg">
			<li></li>
		</ul>
	</nav>

	<div class="facet js-facet">

		<div class="facet__name js-facet-name">
			<span class="glyphicon facet__arrow"></span>
			<spring:theme code="search.nav.applied.facets" />
			<span class="hidden-md hidden-lg"> (${facetCount})</span> 
			<span class="hidden-xs hidden-sm facet__clear">
				<c:if test="${not empty searchQuery}">
				(<a href="${clearAllUrl}"><span><label:message
									messageCode="text.desktop.applied.facet.filter.clearall.apb" /></span></a>)
				</c:if>
			</span>
			<div class="clearfix"></div>
		</div>

		<div class="facet__values js-facet-values">
			<ul class="facet__list">
				<li class="hidden-md hidden-lg clear-text"><c:if
						test="${not empty searchQuery}">
						<a href="${clearAllUrl}"> <span><label:message
									messageCode="text.mobile.applied.facet.filter.clearall.apb" /></span></a>
					</c:if></li>
				<c:forEach items="${pageData.breadcrumbs}" var="breadcrumb">
					<li><c:url value="${breadcrumb.removeQuery.url}"
							var="removeQueryUrl" /> <a href="${removeQueryUrl}"><span
							class="glyphicon glyphicon-remove hidden-xs hidden-sm"></span></a>
						${fn:escapeXml(breadcrumb.facetName)}: ${fn:escapeXml(breadcrumb.facetValueName)}&nbsp; <a
						href="${removeQueryUrl}"><span
							class="glyphicon glyphicon-remove hidden-md hidden-lg"></span></a></li>
				</c:forEach>
			</ul>
		</div>
	</div>

</c:if>