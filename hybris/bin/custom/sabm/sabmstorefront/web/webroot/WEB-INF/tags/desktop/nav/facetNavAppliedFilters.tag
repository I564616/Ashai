<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageData" required="true"
	type="de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ attribute name="msgKey" required="false" %>

<c:set var="themeMsgKey" value="${not empty msgKey ? msgKey : 'search.page'}"/>

<c:if test="${not empty pageData.breadcrumbs}">
	<%-- <div class="total-results visible-md-block visible-lg-block">
	<!-- xx products found -->
	<spring:theme code="${themeMsgKey}.totalResults" arguments="${pageData.pagination.totalNumberOfResults}"/>
	</div> --%>
	<div class="refined-by">
		<spring:theme code ="search.refined.by"/>
	</div>
	<div class="facet_block applied-facets">
		<ul>
			<c:forEach items="${pageData.breadcrumbs}" var="breadcrumb">
				<li class="filter-selected"><c:url value="${breadcrumb.removeQuery.url}" var="removeQueryUrl" />
					<a href="${removeQueryUrl}">${breadcrumb.facetValueName}</a>
				</li>
			</c:forEach>
		</ul>
	</div>

	<form action="#" method="get">
		<!-- Here, The value of the 'q' is empty, the attribute 'value' which is used on the search results page, 
		the link = 'FreeTextSearch' + 'q' -->
		<%-- <input type="hidden" name="q" value="${fn:escapeXml(pageData.freeTextSearch)}" /> --%>
		<input type="hidden" name="q" value="${fn:escapeXml(searchText)}" />
		<input type="hidden" name="text" value="${fn:escapeXml(searchText)}" />
		<%-- <input type="hidden" name="text" value="${fn:escapeXml(searchPageData.freeTextSearch)}" /> --%>
		<a href="javascript:void(0)"	onclick="$(this).closest('form').submit()">
			<div class="filter-clear">CLEAR ALL SELECTIONS</div> 
		</a>
	</form>

</c:if>