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

<c:set var="themeMsgKey"
	value="${not empty msgKey ? msgKey : 'search.page'}" />
<c:set var="showCurrPage"
	value="${not empty showCurrentPageInfo ? showCurrentPageInfo : false}" />
<c:set var="hideRefBtn" value="${hideRefineButton ? true : false}" />
<c:set var="showTotals"
	value="${empty showTopTotals ? true : showTopTotals}" />


<c:if test="${searchPageData.pagination.totalNumberOfResults == 0 && top && showTotals}">
    <div class="paginationBar top clearfix">
		<ycommerce:testId code="searchResults_productsFound_label">
			<div class="totalResults">
				<spring:theme code="${themeMsgKey}.totalResults"
					arguments="${searchPageData.pagination.totalNumberOfResults}" />
			</div>
		</ycommerce:testId><!--  -->
	</div>
</c:if>
<c:if test="${searchPageData.pagination.totalNumberOfResults > 0}">
	<div class="pagination-bar ${(top)?"top":"bottom"}">
		<div class="pagination-toolbar">
			<div class="helper clearfix hidden-md hidden-lg"></div>
			<div class="sort-refine-bar">
				<div class="row">
					<c:if test="${not empty searchPageData.sorts}">
					
						
						<div class="col-xs-12 col-md-12 ">

							<div class="form-group sort_by">
								<label class="control-label " for="sortForm${top ? '1' : '2'}">
									<b><spring:theme code="${themeMsgKey}.sortTitle" /></b>
								</label>
								<form id="sortForm${top ? '1' : '2'}"
									name="sortForm${top ? '1' : '2'}" method="get" action="#">
									<!-- Remove for Sort option done as per the VD 34 -->
									<%-- <select id="sortOptions${top ? '1' : '2'}" name="sort" class="form-control">
                                       <!--   <option disabled><spring:theme code="${themeMsgKey}.sortTitle"/></option>-->
                                        <c:forEach items="${searchPageData.sorts}" var="sort">
                                            <option value="${fn:escapeXml(sort.code)}" ${sort.selected? 'selected="selected"' : ''}>
                                                <c:choose>
                                                    <c:when test="${not empty sort.name}">
                                                        ${fn:escapeXml(sort.name)}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <spring:theme code="${themeMsgKey}.sort.${sort.code}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </option>
                                        </c:forEach>
                                    </select> --%>

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

									<input type="hidden" name="sort" value="" />
									<c:catch var="errorException">
										<spring:eval expression="searchPageData.currentQuery.query"
											var="dummyVar" />
										<%-- This will throw an exception is it is not supported --%>
										
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

						<c:if test="${not hideRefBtn}">
						
							<div class="col-xs-12 col-sm-5 hidden-md hidden-lg">
								<ycommerce:testId code="searchResults_refine_button">
									<product:productRefineButton
										styleClass="btn btn-default js-show-facets" />
								</ycommerce:testId>
							</div>
			
						</c:if>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</c:if>
