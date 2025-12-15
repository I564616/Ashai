<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>

<nav:steps current="3" />

<div id="globalMessages">
	<common:globalMessages />
</div>
<script id="dealsData" type="text/json">${ycommerce:generateJson(specificDeals)}</script>
<%-- <deals:dummyData /> --%>
<div id="dealPageWrapper" data-status="${b2bUnitData.b2BUnitStatus}" class="row page-productList user-${b2bUnitData.b2BUnitStatus}"
	ng-controller="dealsCtrl" ng-init="init()"
	data-dealsLoadInProgress="${dealsCallInProgress}" ng-cloak>
	<input type="hidden" id="uid" value="${b2bUnitData.uid }">
	<input type="hidden" id="specificDealCurrentUrl" value="<c:url value="/deals/addChanges"/>">
	<input type="hidden" id="specificDealNextUrl" value="<c:url value="/confirm-changes"/>">
	<!-- <form action="" name="newDeals" id="newDeals"> -->
	<a href="#confirmChanges" class="regular-popup" id="confirmDealsChangesLink"></a>
	<div class="col-xs-12">
		<deals:availDeals />
	</div>
	<div class="col-xs-12 col-md-9 deal-items">
		<deals:dealsRow />
	</div>
	<div class="col-xs-12 col-md-9 text-right">
		<button type="button" ng-hide="userStatus === 'inactive' || deals.length === 0" class="btn btn-primary btn-large"
			ng-disabled="dealsActivated.length == 0" ng-click="confirmDealsChanges()">
			<spring:theme code="staff.portal.deals.changes.confirm.button" />
		</button>		
		<div ng-show="userStatus === 'inactive' && deals.length > 0"> 
			<c:url value="/backToCustomerSearchResults" var="backToCustomerSearchResultsLink"/>
			<a href="${backToCustomerSearchResultsLink }" class="btn btn-primary btn-large">
				<!-- Note to DEV - Change this text and URL if not going to search results page -->
				<spring:theme code="staff.portal.deals.back.button" />
			</a>
		</div>
	</div>
	<!-- </form> -->
</div>