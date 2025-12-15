<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<c:forEach items="${pageData.facets}" var="facet">
	<c:choose>
		<c:when test="${facet.code eq 'availableInStores'}">
			<nav:facetNavRefinementStoresFacet facetData="${facet}" userLocation="${userLocation}"/>
		</c:when>
		<c:when test="${facet.code eq 'brand' && not empty categoryCode }">
		</c:when>
		<c:otherwise>
			<div class="panel-group accordion" role="tablist" aria-multiselectable="true">
			  <div class="panel panel-default">
			  	<c:choose>
					<c:when test="${empty categoryCode }"> 
					    <nav:facetNavRefinementFacet facetData="${facet}"/>
					</c:when>
					<c:otherwise>
						<c:if test="${facet.code ne 'sabmCategory' }">
							<nav:facetNavRefinementFacet facetData="${facet}"/>
						</c:if>
					</c:otherwise>
				</c:choose>
			  </div>
			</div>
		</c:otherwise>
	</c:choose>
</c:forEach>

	





