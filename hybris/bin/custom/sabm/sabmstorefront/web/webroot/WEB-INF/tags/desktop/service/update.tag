<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="UPDATE_DETAILS_DELIVERY_OPTIONS" />

<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">
		<p>This enquiry should be used if you would like to permanently update your business unit details or delivery address.</p>
		
		<div class="row">
			<div class="col-xs-12 col-md-4 form-group">
	       			<label>Type of change<span class="required">*</span></label>
             		<div class="select">
  					 	<span class="arrow"></span>
             			<select name="price_type" class="form-control validate-input" ng-model="${srType}.type" ng-required="sr.type == '${srType}'">
             				<option value="" disabled selected>Select</option>
             				<option value="bu">Business Unit Details</option>
             				<option value="delivery">Delivery Details</option>
             				<option value="other">Other</option>
						</select>
						<span class="error" ng-show="(notComplete || serviceRequest.price_type.$touched) && (sr.type == '${srType}') && serviceRequest.price_type.$invalid">Please select the option above</span>
  					</div>
		    </div> 
		</div>

		<div class="row">
			<div class="col-sm-6 col-md-4 form-group">
				<label for="update-current">Current details<span class="required">*</span></label>
				<input id="update-current" name="update_current" class="form-control" type="text" ng-model="${srType}.current" ng-required="sr.type == '${srType}'">
			<span class="error" ng-show="(notComplete || serviceRequest.update_current.$touched) && (sr.type == '${srType}') && serviceRequest.update_current.$invalid">Please enter the current details above</span>
			</div>

			<div class="col-sm-6 col-md-4 form-group">
				<label for="update-new">New details<span class="required">*</span></label>
				<input id="update-new" name="update_new" class="form-control" type="text" ng-model="${srType}.new" ng-required="sr.type == '${srType}'">
			<span class="error" ng-show="(notComplete || serviceRequest.update_new.$touched) && (sr.type == '${srType}') && serviceRequest.update_new.$invalid">Please enter the new details above</span>
  					</div>
		</div>		

      	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="update-other">Other information</label>
				<textarea id="update-other" class="form-control" name="text" rows="15" ng-model="${srType}.otherinfo"></textarea>
			</div>
      	</div>
	</div>
</div>