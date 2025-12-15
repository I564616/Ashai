<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="facetData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetData"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<c:if test="${not empty facetData.values}">
	<c:set var="facetId" value="${fn:replace(facetData.name, ' ', '_')}" />
	<div class="panel-heading" role="tab" id="header-${facetData.name}">
		<span class="panel-title h4-alt">
			<a role="button" data-toggle="collapse" href="#${facetId}" aria-expanded="true" aria-controls="${facetId}">
				<spring:theme code="search.nav.facetTitle" arguments="${facetData.name}" />
			</a>
		</span>
	</div>
	<ycommerce:testId code="facetNav_facet${facetData.name}_links">
		<c:if test="${not empty facetData.values}">
			<div id="${facetId}" class="topFacetValues panel-collapse collapse in" role="tabpanel" aria-labelledby="${facetData.name}">
				<div class="panel-body">
					<ul class="facet_block ${facetData.multiSelect ? '' : 'indent'}">
						<c:forEach items="${facetData.values}" var="facetValue">
							<li class="checkbox">
								<c:if test="${facetData.multiSelect}">
									<form action="#" method="get">
										<input type="hidden" name="q" value="${fn:escapeXml(facetValue.query.query.value)}" />
										<%-- <input type="hidden" name="text" value="${fn:escapeXml(searchPageData.freeTextSearch)}" />  --%>
										<input type="hidden" name="text" value="${fn:escapeXml(searchText)}" />
										<input class="facet-check" type="checkbox" ${facetValue.selected ? 'checked="checked"' : ''} onchange="$(this).closest('form').submit(); rm.tagManager.trackListingFilter('${facetId}');" />
										<label class="facet_block-label">${facetValue.name}</label>
										<span class="facetValueCount"><spring:theme code="search.nav.facetValueCount" arguments="${facetValue.count}" /></span>

									</form>
								</c:if> 
								<c:if test="${not facetData.multiSelect}">
									<c:url value="${facetValue.query.url}" var="facetValueQueryUrl" />
									<a href="${facetValueQueryUrl}&amp;text=${searchPageData.freeTextSearch}">${facetValue.name}</a>&nbsp;
									<span class="facetValueCount"><spring:theme code="search.nav.facetValueCount" arguments="${facetValue.count}" /></span>
								</c:if>
							</li>
						</c:forEach>
					</ul>
					<c:if test="${fn:length(facetData.values) > 5}">
						<span class="more">
							<a href="#" class="moreFacetValues">
								<spring:theme code="search.page.showAllResults"/>
							</a>
						</span>
					</c:if>
				</div>
			</div>
		</c:if>
	</ycommerce:testId>
</c:if>
