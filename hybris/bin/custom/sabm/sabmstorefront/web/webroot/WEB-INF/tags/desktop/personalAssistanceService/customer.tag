<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:theme code="text.personal.assistance.form.msg.customer.no" var="customerNo"/>
<spring:theme code="text.personal.assistance.form.msg.customer.name" var="customerName"/>
<spring:theme code="text.personal.assistance.form.msg.invalid.details" var="invalidDetails"/>

<div class="row margin-top-20" ng-if="pa.type == 'customer'">
	<div class="col-xs-12">
      	<div class="row">
      		<div class="col-md-4 form-group">
	       		<label for="customer-no">${customerNo}</span></label>
				<div class="input-icon-group">
					<input id="customer-no" ng-pattern="/^\d+$/" name="customer_no" class="form-control" minlength="3" type="text" ng-model="$parent.customer_no" ng-required="pa.type == 'customer' && !$parent.customer_name.length">
				</div>
				<span class="error" ng-show="paRequest.customer_no.$error.pattern">${invalidDetails}</span>
			</div>
			<div class="col-md-4 form-group">
	       		<label for="customer-name">${customerName}</span></label>
				<div class="input-icon-group">
					<input id="customer-name" ng-pattern="string" name="customer_name" class="form-control" minlength="3" type="text" ng-model="$parent.customer_name" ng-required="pa.type == 'customer' && !$parent.customer_no.length">
				</div>
			</div>
      	</div>
	</div>
</div>