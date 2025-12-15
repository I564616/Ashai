<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<ul class="list-group notification-list">
	<li class="list-group-item header">
		<div class="row">
			<div class="col-md-6 col-sm-6 col-xs-12 name"><spring:theme code="text.invoicediscrepancy.tableheader.name" /></div>
			<div class="col-md-6 col-sm-6 col-xs-6 hidden-xs email"><spring:theme code="text.invoicediscrepancy.tableheader.email" /></div>
		</div>
	</li>
	
	<div ng-class="{'scrollable': notificationUsers.length + 1 > 9}">
		<li class="list-group-item body">
			<div class="row selected">
				<div class="col-md-6 col-sm-6 col-xs-12">
					<label class="custom-checkbox" for="currentEmail">
						<span class="current-userName">${user.name} 
							<input type="hidden" id="curEmail" value="${user.uid}" /> 
							<input type="hidden" id="curUserRole" value="${user.userRole}" /> 
							<i ng-show="!isBDEUser">(You)</i></span>
						<input type="checkbox" ng-disabled="false" id="currentEmail" ng-checked="false" ng-click="validateForm()" />
						<span class="check-mask"></span>
					</label>
				</div>
				<div class="col-md-6 col-md-offset-0 col-sm-6 col-sm-offset-0 col-xs-11 col-xs-offset-1"><span class="current-email">${user.email}</span></div>
			</div>
		</li>
		<li class="list-group-item body" ng-repeat="notification in notificationUsers | orderBy: 'name'">
			<div class="row">
				<div class="col-md-6 col-sm-6 col-xs-12">
					<label class="custom-checkbox" for="email{{$index}}">
						<span>{{notification.name}}</span>
						<input type="checkbox" id="email{{$index}}" name="email{{$index}}" ng-model="emails[notification.uid]" ng-click="validateForm()"/>
						<input type="hidden" value="{{notification.uid}}" />
						<span class="check-mask"></span>
					</label>
				</div>
				<div class="col-md-6 col-md-offset-0 col-sm-6 col-sm-offset-0 col-xs-11 col-xs-offset-1">{{notification.email}}</div>
			</div>
		</li>
	</div>
</ul>
