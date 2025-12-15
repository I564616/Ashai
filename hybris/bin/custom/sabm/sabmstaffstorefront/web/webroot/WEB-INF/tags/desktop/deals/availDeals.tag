<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<div class="row deals-header">
	<div class="col-xs-12">
		<span class="status status-${b2bUnitData.b2BUnitStatus}"><spring:theme code="staff.portal.customer.searchResults.${b2bUnitData.b2BUnitStatus}" text="${b2bUnitData.b2BUnitStatus}" /></span>
	</div>
	<div class="col-xs-12 col-md-9">	
		<h1 class="offset-bottom-small">
			<spring:theme code="staff.portal.customer.searchResults.description"/><c:if test="${not empty b2bUnitData.uid}">${b2bUnitData.uid}</c:if>	
			<%--  display unit id  --%>
			<c:if test="${not empty b2bUnitData.name}">
				<spring:theme code="staff.portal.customer.searchResults.shortbar" />${b2bUnitData.name}</c:if>
			<c:if test="${not empty b2bUnitData.address.line1}">
				<spring:theme code="staff.portal.customer.searchResults.shortbar" />${b2bUnitData.address.line1}</c:if>
			<c:if test="${not empty b2bUnitData.address.town}">
				<spring:theme code="staff.portal.customer.searchResults.comma" />${b2bUnitData.address.town}</c:if>
			<c:if test="${not empty b2bUnitData.address.postalCode}">
				<spring:theme code="staff.portal.customer.searchResults.comma" />${b2bUnitData.address.postalCode}</c:if>
		</h1>
		<div ng-show="deals.length > 0">
			<p ng-hide="userStatus === 'inactive'">
				<spring:theme code="staff.portal.deals.changes.note" />
			</p>
			<p ng-show="userStatus === 'inactive'">
				<spring:theme code="staff.portal.deals.inactive.note" />
			</p>
		</div>
		<p class="offset-bottom-medium" ng-show="deals.length === 0">
			<spring:theme code="staff.portal.deals.noExclusiveDeals" />
		</p>
		<hr class="hr-1" ng-hide="deals.length === 0">
	</div>
	<div class="col-xs-12 col-md-9 text-right offset-bottom-small">
		<button type="button" ng-hide="userStatus === 'inactive' || deals.length === 0" class="btn btn-primary btn-large"
			ng-disabled="dealsActivated.length == 0" ng-click="confirmDealsChanges()">
			<spring:theme code="staff.portal.deals.changes.confirm.button" />
		</button>
		<c:url value="/backToCustomerSearchResults" var="backToCustomerSearchResultsLink"/>
		<a href="${backToCustomerSearchResultsLink }" ng-show="userStatus === 'inactive' || deals.length === 0" class="btn btn-primary btn-large">
			<!-- Note to DEV - Change this text and URL if not going to search results page -->
			<spring:theme code="staff.portal.deals.back.button" />
		</a>
	</div>
</div>