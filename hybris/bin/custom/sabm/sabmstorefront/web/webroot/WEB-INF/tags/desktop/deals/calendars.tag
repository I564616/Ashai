<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="calendars">
	<c:url value="/deals" var="dealsUrl" /> 
	<div class="form-group">
		<h4 class="panel-title h4-alt offset-bottom-xsmall" ng-class="{open : dealCalanderShow}" ng-click="dealCalanderShow = !dealCalanderShow">Want to see deals for different dispatch dates?</h4>
		<p class="offset-bottom-small">Please select a dispatch date below:</p>
 		<div class="select">
			<span class="arrow"></span>
 			<select name="filter_date" class="form-control validate-input" ng-model="filterDate" ng-change="deliveryDateChange()" ng-options="item as formatDates(item) for item in enabledDates track by item"></select>
		</div>
	</div>                 
</div>