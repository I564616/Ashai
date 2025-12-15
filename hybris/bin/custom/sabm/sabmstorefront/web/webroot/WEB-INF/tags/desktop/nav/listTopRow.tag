<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<c:set var="themeMsgKey" value="${'search.page'}" />
<div class="row">
	<div class="col-md-6">
		<c:choose>
			<c:when test="${not empty categoryData}">
				<h1>${categoryData.name}</h1>
			</c:when>
			
			<%--Display only searched text instead of whole query string (with facets/sort applied) --%>
			<c:when test="${not empty searchText}">
				<h1><spring:theme code="search.page.searchText_l" />${searchText}<spring:theme code="search.page.searchText_r"/></h1>
			</c:when>
		</c:choose>
		<span class="num-products visible-xs-block visible-sm-block">
			<label class="h3"><spring:theme code="${'search.page'}.totalResults" arguments="${searchPageData.pagination.totalNumberOfResults},${searchPageData.pagination.totalNumberOfResults >1 ?'s':''}" /></label>
		</span>
	</div>
	<div class="col-xs-6 visible-xs-block visible-sm-block trim-right-5 select-list">
		<div class="select-btn list-refine-btn">
			<spring:theme code="text.product.refine" />
		</div>
	</div>
	<div class="col-xs-6 col-md-4 col-md-offset-2 trim-left-5">
		<div class="list-sort">
			<div class="select-list">
				<form id="sort_form" method="get" action="#" class="sortForm">
					<input type="hidden" id="sortHiddenField" name="sort" />
					<%-- <c:catch var="errorException">
						<spring:eval expression="searchPageData.currentQuery.query" var="dummyVar" />
						This will throw an exception is it is not supported
						<input type="hidden" name="q" value="${searchPageData.freeTextSearch}" />
					</c:catch> --%>
					<c:catch var="errorException">
						<spring:eval expression="searchPageData.currentQuery.query" var="dummyVar" />
						<%-- This will throw an exception is it is not supported --%>						
						<c:choose>
							<c:when test="${not empty searchPageData.freeTextSearch}">
								<c:forEach items="${searchPageData.sorts}" var="sort">					
									<c:if test="${fn:contains(searchPageData.freeTextSearch, sort.code)}">
										<c:set var="sortContains" value="${searchPageData.currentQuery.query.value}" />
									</c:if>
								</c:forEach>
								<c:choose>
									<c:when test="${not empty sortContains}">
										<input type="hidden" name="q" value="${sortContains}" />										
									</c:when>
									<c:otherwise>
										<input type="hidden" name="q" value="${fn:toUpperCase(fn:substring(searchPageData.freeTextSearch, 0, 1))}${fn:substring(searchPageData.freeTextSearch, 1,fn:length(searchPageData.freeTextSearch))}" />										
									</c:otherwise>
								</c:choose>															
							</c:when>
							<c:otherwise>
								<input type="hidden" name="q" value="${searchPageData.currentQuery.query.value}" />
							</c:otherwise>
						  </c:choose>
				 		  <c:if test="${not empty searchText}">
							<input type="hidden" name="text" value="${searchText}" />
						</c:if>
					</c:catch>
				</form>
				<div data-value="" class="select-btn sort"></div>
				<ul class="select-items sort">
					<c:forEach items="${searchPageData.sorts}" var="sort">
						<!-- As part of Upgrade -->
						<%-- <c:if test="${sort.visible eq true}"> --%>
							<li data-value="${sort.code}" ${sort.selected ? 'data-selected="selected"' : ''}><c:choose>
									<c:when test="${not empty sort.name}">
										${sort.name}
									</c:when>
									<c:otherwise>
										<spring:theme code="${themeMsgKey}.sort.${sort.code}" />
									</c:otherwise>
								</c:choose></li>
						<%-- </c:if> --%>
					</c:forEach>
				</ul>
			</div>
			<span><spring:theme code="text.product.sort.by" /></span>
		</div>
	</div>
</div>
