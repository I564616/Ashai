<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%@ attribute name="facetData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetData"%>

<c:if test="${not empty facetData.values}">
	<c:set var="facetId" value="${fn:replace(facetData.name, ' ', '_')}" />
	<div class="panel-heading" role="tab" id="header-${facetData.name}">
		<h4 class="panel-title h4-alt">
			<a role="button" data-toggle="collapse" href="#${facetId}" aria-expanded="true" aria-controls="${facetId}">
				<spring:theme code="sabm.deal.facet.${facetData.name}.label"/>
			</a>
		</h4>
	</div>
	<ycommerce:testId code="facetNav_facet${facetData.name}_links">
		<div id="${facetId}" class="topFacetValues panel-collapse collapse deal-filter-collapse" role="tabpanel" aria-labelledby="${facetData.name}">
			<div class="panel-body">
				<ul class="facet_block ${facetData.multiSelect ? '' : 'indent'}">
					 <c:forEach items="${facetData.values}" var="facetValue">
						<li class="checkbox">
	 						<c:if test="${facetData.multiSelect}">
								<input type="hidden" class="facet-value" value="${facetValue.code}" />
								<%--<input type="hidden" name="text" value="${fn:escapeXml(searchPageData.freeTextSearch)}" />--%>
								<input class="facet-check" type="checkbox" ${facetValue.selected ? 'checked="checked"' : ''}/>
								<label class="facet_block-label">${facetValue.name}</label>
								<span class="facetValueCount"><spring:theme	code="search.nav.facetValueCount" arguments="${facetValue.count}" /></span>
							</c:if>
						</li>
					</c:forEach>
				</ul>
				<c:if test="${fn:length(facetData.values) > 5}">
					<span class="more">
					   <a href="#" class="moreFacetValues">
					   	<spring:theme code="search.page.showAllResults" />
					   </a>
					</span>
				</c:if>
			</div>
		</div>
	</ycommerce:testId>
</c:if>

