<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="ORDER_ENQUIRY" />

<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">	
		<div class="row">
			<div class="col-sm-6 col-md-4 form-group">
				<label for="order-number">Order Number (if applicable)</label>
				<input id="order-number" class="form-control" type="text" ng-model="${srType}.ordernumber">
			</div>
		</div>
		
	 	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="order-yourmsg">Your message <span class="required">*</span></label>
				<textarea id="order-yourmsg" class="form-control" name="order_your_msg" rows="15" ng-model="${srType}.yourmsg" ng-required="sr.type == '${srType}'"></textarea>
			<span class="error" ng-show="(notComplete || serviceRequest.order_your_msg.$touched) && (sr.type == '${srType}') && serviceRequest.order_your_msg.$invalid">Please enter details above</span>
			</div>
      	</div>
	</div>
</div>