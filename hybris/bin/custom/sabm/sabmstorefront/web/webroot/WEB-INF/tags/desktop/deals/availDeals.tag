<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<div class="deals-header">
	<h1>
		<span ng-show="pageInactive && dealsfiltered.length">View Deals for Different Delivery</span>
		<span ng-show="!pageInactive && dealsfiltered.length">Available Deals for Delivery</span>
		<span ng-show="!dealsfiltered.length">There are no deals available for</span>: 
		<span class="selectedDate">{{ customDateFormatWithWeek(filterDate)}}</span>
	</h1>
	<p ng-show="!dealsfiltered.length" class="offset-bottom-small">However there are deals available for alternative delivery dates. Please use the date selector below to view available deals.</p>
	<span ng-show="dealsfiltered.length" class="inline pull-right toggle-all visible-md-block visible-lg-block" ng-init="collapsedAll = true" ng-click="collapsedAllFunc()"><span ng-hide="collapsedAll">Hide</span><span ng-hide="!collapsedAll">Show</span> all details</span>
</div>