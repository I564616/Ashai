<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="searchUrl" required="true"%>
<%@ attribute name="searchPageData" required="true"
	type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData"%>
<%@ attribute name="top" required="true" type="java.lang.Boolean"%>
<%@ attribute name="showTopTotals" required="false"
	type="java.lang.Boolean"%>
<%@ attribute name="supportShowAll" required="true"
	type="java.lang.Boolean"%>
<%@ attribute name="supportShowPaged" required="true"
	type="java.lang.Boolean"%>
<%@ attribute name="additionalParams" required="false"
	type="java.util.HashMap"%>
<%@ attribute name="msgKey" required="false"%>
<%@ attribute name="showCurrentPageInfo" required="false"
	type="java.lang.Boolean"%>
<%@ attribute name="hideRefineButton" required="false"
	type="java.lang.Boolean"%>
<%@ attribute name="numberPagesShown" required="false"
	type="java.lang.Integer"%>
<%@ attribute name="quickOrderTemplateCount" required="false"
	type="java.lang.Integer"%>
<%@ taglib prefix="pagination"
	tagdir="/WEB-INF/tags/responsive/nav/pagination"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="themeMsgKey" value="${not empty msgKey ? msgKey : 'search.page'}" />
<c:set var="showCurrPage" value="${not empty showCurrentPageInfo ? showCurrentPageInfo : false}" />
<c:set var="hideRefBtn" value="${hideRefineButton ? true : false}" />
<c:set var="showTotals" value="${empty showTopTotals ? true : showTopTotals}" />

<c:if
	test="${(searchPageData.pagination.totalNumberOfResults - quickOrderTemplateCount) == 0 && top && showTotals}">

    <div class="paginationBar top clearfix">
		<ycommerce:testId code="searchResults_productsFound_label">
			<div class="totalResults">
				<spring:theme code="${themeMsgKey}.totalResults"
					arguments="${searchPageData.pagination.totalNumberOfResults - quickOrderTemplateCount}" />
			</div>
		</ycommerce:testId>
	</div> 
</c:if>
<c:if test="${searchPageData.pagination.totalNumberOfResults - quickOrderTemplateCount > 0}">
	<div class="pagination-bar ${(top)?"top":"bottom"}">
		<div class="pagination-toolbar">
			<div class="helper clearfix hidden-md hidden-lg"></div>
			<div class="sort-refine-bar">
				<div class="row">
                    <c:if test="${top && showTotals}">
                        <div class="col-xs-12 col-sm-4 col-md-4">
                            <ycommerce:testId code="searchResults_productsFound_label">
                                <c:choose>
                                    <c:when test="${showCurrPage}">
                                        <c:choose>
                                            <c:when test="${searchPageData.pagination.totalNumberOfResults - quickOrderTemplateCount == 1}">
                                                <spring:theme code="${themeMsgKey}.totalResultsSingleOrder"/>
                                            </c:when>
                                            <c:when test="${searchPageData.pagination.numberOfPages <= 1}">
                                                <spring:theme code="${themeMsgKey}.totalResultsSinglePag"
                                                              arguments="${searchPageData.pagination.totalNumberOfResults - quickOrderTemplateCount}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="currentPageItems"
                                                       value="${(searchPageData.pagination.currentPage + 1) * searchPageData.pagination.pageSize}"/>
                                                <c:set var="upTo"
                                                       value="${(currentPageItems > searchPageData.pagination.totalNumberOfResults ? searchPageData.pagination.totalNumberOfResults : currentPageItems)}"/>
                                                <c:set var="currentPage"
                                                       value="${searchPageData.pagination.currentPage * searchPageData.pagination.pageSize + 1} - ${upTo}"/>
                                                <spring:theme code="${themeMsgKey}.totalResultsCurrPage"
                                                              arguments="${currentPage},${searchPageData.pagination.totalNumberOfResults}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:theme code="${themeMsgKey}.totalResults"
                                                      arguments="${searchPageData.pagination.totalNumberOfResults}"/>
                                    </c:otherwise>
                                </c:choose>
                            </ycommerce:testId>
                         </div>
                    </c:if>
					<c:if test="${not empty searchPageData.sorts && cmsPage.uid ne 'multiAccount'}">
						<!--
						<div class="col-xs-12 col-sm-5 col-md-6 text-align-right">
                         <div class="form-group pull-right">
                             <label class="control-label " for="sortForm${top ? '1' : '2'}">
                                        <spring:theme code="${themeMsgKey}.display"/>
                            </label>
                              <select class="form-control">
                                 <option value="12">12</option>
                                 <option value="24">24</option>
                                 <option value="48">48</option>
                                 <option value="All">All</option>
                             </select>  
                             <ul class="sortby_list">
                                    <li ${sort.selected? 'class="sel"' : ''} data-option="12">12</li>
                                    <li data-option="24">24</li>
                                    <li data-option="48">48</li>
                                    <li data-option="All">All</li>
                             </ul>
                        </div> -->
						
						<div class="col-xs-12 col-sm-8 col-md-8 text-align-right">
							<div class="form-group sort_by">
								<label class="control-label " for="sortForm${top ? '1' : '2'}">
									<b><spring:theme code="${themeMsgKey}.sortTitle" /></b>
								</label>
								<form id="sortForm${top ? '1' : '2'}"
									name="sortForm${top ? '1' : '2'}" method="get" action="#">
									<!-- Remove for Sort option done as per the VD -->

									<ul id="sortOptions${top ? '1' : '2'}" class="sortby_list">
										<c:forEach items="${searchPageData.sorts}" var="sort">
											<li data-option="${fn:escapeXml(sort.code)}"
												${sort.selected? 'class="sel"' : ''}><c:choose>
													<c:when test="${not empty sort.name}">
                                    					${fn:escapeXml(sort.name)}
                                    				</c:when>
													<c:otherwise>
														<spring:theme code="${themeMsgKey}.sort.${sort.code}" />
													</c:otherwise>
												</c:choose></li>
										</c:forEach>
									</ul>

								<input type="hidden" name="sort" value=""/>
								<c:if test="${cmsPage.uid eq 'orders'}">
									<input type="hidden" name="startDate" value="${request.getParameter('startDate')}" />
									<input type="hidden" name="endDate" value="${request.getParameter('endDate')}" />
								</c:if>
									<c:catch var="errorException">
										<spring:eval expression="searchPageData.currentQuery.query"
											var="dummyVar" />
											<!--This will throw an exception is it is not supported -->
										
										<input type="hidden" name="q"
											value="${searchPageData.currentQuery.query.value}" />
									</c:catch>

									<c:if test="${supportShowAll}">
										<ycommerce:testId code="searchResults_showAll_link">
											<input type="hidden" name="show" value="Page" />
										</ycommerce:testId>
									</c:if>
									<c:if test="${supportShowPaged}">
										<ycommerce:testId code="searchResults_showPage_link">
											<input type="hidden" name="show" value="All" />
										</ycommerce:testId>
									</c:if>
									<c:if test="${not empty additionalParams}">
										<c:forEach items="${additionalParams}" var="entry">
											<input type="hidden" name="${fn:escapeXml(entry.key)}"
												value="${fn:escapeXml(entry.value)}" />
										</c:forEach>
									</c:if>
								</form>
							</div>
						</div>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</c:if>
