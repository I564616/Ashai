<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="DELIVERY_ENQUIRY" />
<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12 ">
		<p>This enquiry should be used if you have a query about a delivery that has not yet arrived.</p>
		
		<div class="row form-group">
			<div class="col-md-4">
				<label for="order-num">Order number<span class="required">*</span></label>
				<input id="order-num" class="form-control" type="text" name="delivery_ordernumber" ng-pattern="/^\d+$/" ng-model="${srType}.ordernumber" ng-required="sr.type == '${srType}'">
				<span class="error" ng-show="serviceRequest.delivery_ordernumber.$error.pattern">Your order number consists of numbers only</span>
			    <span class="error" ng-show="(notComplete || serviceRequest.delivery_ordernumber.$touched) && (sr.type == '${srType}') && serviceRequest.delivery_ordernumber.$invalid">Please enter the order number above</span>
			</div>
		</div>

		<div class="row form-group">
			<div class="col-sm-6 col-md-4">
				<label for="order-date">Order date<span class="required">*</span></label>
				<div class="select">
					<span class="arrow"></span>
					<!-- Removed Date validation as part of INC0695994:Allow any date(Allowed any date for order date and expected delivery date in enquiry form) -->
					<input id="order-date" name="orderdate" data-container=".form-group" class="form-control basic-datepicker" type="text" readonly="readonly" ng-model="${srType}.date" ng-required="sr.type == '${srType}'">
				</div>
				<span class="error" ng-show="(notComplete || serviceRequest.orderdate.$touched) && (sr.type == '${srType}') && serviceRequest.orderdate.$invalid">Please select an option above</span>
			</div>

			<div class="col-sm-6 col-md-4">
				<label for="expected-delivery">Expected delivery date<span class="required">*</span></label>
				<div class="select">
						<span class="arrow"></span>
						<!-- Removed Date validation as part of INC0695994:Allow any date(Allowed any date for order date and expected delivery date in enquiry form) -->
						<input id="expected-delivery" name="expected_delivery_date" data-container=".form-group" class="form-control basic-datepicker" type="text" readonly="readonly" ng-model="${srType}.expected" ng-required="sr.type == '${srType}'">
				<span class="error" ng-show="(notComplete || serviceRequest.expected_delivery_date.$touched) && (sr.type == '${srType}') && serviceRequest.expected_delivery_date.$invalid">Please select an option above</span>
				</div>
			</div>
		</div>
		

      	<div class="row form-group">
      		<div class="col-md-8">
	       		<label for="deliveryEnquiryMessage"><spring:theme code="text.service.request.form.message"/></label>
				<textarea id="deliveryEnquiryMessage" class="form-control" name="text" rows="15" ng-model="${srType}.message"></textarea>
			</div>
      	</div>
	</div>
</div>