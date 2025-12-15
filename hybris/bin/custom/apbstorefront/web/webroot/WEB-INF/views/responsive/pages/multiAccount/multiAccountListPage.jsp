<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<spring:htmlEscape defaultHtmlEscape="false" />
	
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	
<%-- <c:set var="searchUrl" value="/my-account/orders?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/> --%>
<c:set var="searchUrl" value="/multiAccount" />
<c:url value="/" var="continueShoppingLink" scope="session" />

<div class="login-page__headline">
	<spring:theme code="sga.text.multi.account" />
</div>

<c:if test="${not empty searchPageData.results}">
<c:if  test="${!multiAccountSelfRegistration}">
	<c:if test="${!firstTimeLoggedIn}">
		<c:if test="${isDefaultUnitBelongsCurrSite && !isDefaultDisabled}">
			<div class="account-orderhistory-label">
				<spring:theme code="text.multi.account.unit.details.text.line.one" arguments="${totalItemsInCart}"/>
				<span><a class="site-anchor-link" href="${continueShoppingLink}"><spring:theme code="text.multi.account.continue.shopping"/></a></span>
				<spring:theme code="text.multi.account.unit.details.text.line.two" arguments="${defaultB2BUnitName}" />
			</div>
		</c:if>
		<c:if test="${!isDefaultUnitBelongsCurrSite}">
			<div class="account-orderhistory-label">
				<spring:theme code="text.multi.account.unit.other.site.message.${cmsSite.uid}" arguments="${otherSiteUrl}"/>
			</div>
		</c:if>
		<div class="account-orderhistory-label">
			<spring:theme code="text.multi.account.unit.details.text.line.three" />
		</div>
	</c:if>
</c:if>
	<div class="account-section">
		<div class="account-section-content	">
			<div class="account-orderhistory">
				<div class="account-orderhistory-pagination">
					<nav:paginationwithdisplay top="true" msgKey="text.multi.account.unit.page" showCurrentPageInfo="true" hideRefineButton="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" numberPagesShown="${numberPagesShown}" />
				</div>
				<div id="multiaccount-table" class="responsive-table">
					<table class="responsive-table multiaccount-table">
						<tr class="responsive-table-head hidden-xs">
							<th>
								<spring:theme code="text.multi.account.unit.id" />
							</th>
							<th>
							<c:choose>
							 <c:when test="${cmsSite.uid eq 'sga'}">
                        		<spring:theme code="text.multi.account.unit.name" />
                      </c:when>
                      <c:otherwise>
                        	<spring:theme code="apb.text.multi.account.unit.name" />
                      </c:otherwise>
                     </c:choose>
							</th>
							<th>
								<spring:theme code="text.multi.account.unit.address" />
							</th>
							<th>
								<spring:theme code="text.multi.account.unit.suburb" />
							</th>
							<th>
								<spring:theme code="text.multi.account.unit.postal.code" />
							</th>
							<th></th>
						</tr>
						<c:forEach items="${searchPageData.results}" var="unitData">
							<tr class="responsive-table-item multiaccount-grid">
								<ycommerce:testId code="orderHistoryItem_orderDetails_link">
									<td class="responsive-table-cell hidden-sm hidden-md hidden-lg multiaccount-grid-cell">
										<spring:theme code="text.multi.account.unit.id" />
									</td>
									<td class="responsive-table-cell multiaccount-grid-cell">
                        		${unitData.accountNumber}
									</td>
									<td class="responsive-table-cell hidden-sm hidden-md hidden-lg multiaccount-grid-cell">
										<spring:theme code="text.multi.account.unit.name" />
									</td>
									<td class="status multiaccount-grid-cell">
										${unitData.name}
									</td>
									<td class="responsive-table-cell hidden-sm hidden-md hidden-lg multiaccount-grid-cell">
										<spring:theme code="text.multi.account.unit.address" />
									</td>
									<td class="responsive-table-cell multiaccount-grid-cell">
									<c:if test="${not empty unitData.addresses[0].streetField}">
										${unitData.addresses[0].streetField}
									</c:if>
									</td>
									<td class="responsive-table-cell hidden-sm hidden-md hidden-lg multiaccount-grid-cell">
										<spring:theme code="text.multi.account.unit.suburb" />
									</td>
									<td class="responsive-table-cell responsive-table-cell-bold multiaccount-grid-cell">
										${unitData.addresses[0].town }
									</td>
									<td class="responsive-table-cell hidden-sm hidden-md hidden-lg multiaccount-grid-cell">
										<spring:theme code="text.multi.account.unit.postal.code" />
									</td>
									<td class="responsive-table-cell multiaccount-grid-cell">
										${unitData.addresses[0].postalCode }
									</td>
									<td class="responsive-table-cell multiaccount-grid-cell multiaccount-grid-btn">
										<c:choose>
											<c:when test="${defaultB2BUnitId == unitData.name}">
												<button onclick="document.location.href='${continueShoppingLink}'" class="btn btn-primary btn-template-block multiAccount-button multiAccount-button-disabled">
													<spring:theme code="text.multi.account.select.b2b.unit"/>
												</button>
											</c:when>
											<c:otherwise>
												<c:url value="/multiAccount/updateB2bUnit" var="multiAccountActionUrl" />
												<form action="${multiAccountActionUrl}" method="post" class="multi-account-form">
													<button type="submit" class="btn btn-primary btn-template-block multiAccount-button multiAccount-button-disabled">
														<spring:theme code="text.multi.account.select.b2b.unit"/>
													</button>
													<input type="hidden" id="b2bUnitID" name="b2bUnitID" value="${unitData.accountNumber}" />
													<input type="hidden" name ="${CSRFToken.parameterName}" value="${CSRFToken.token}"/>
												</form>
											</c:otherwise>
										</c:choose>
									</td>
								</ycommerce:testId>
							</tr>
						</c:forEach>
					</table>
				</div>
				<div class="account-orderhistory-pagination">
					<nav:asahipaginationwithnumbering top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" numberPagesShown="${numberPagesShown}" />
				</div>
			</div>
		</div>
	</div>

	
		</c:if>
</template:page>