<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="UPDATE_EXISTING_ENQUIRY" />

<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">	
		
		<div class="row">
			<div class="col-sm-6 col-md-4 form-group">
				<label for="updateExisting-number">Case Number (if applicable)</label>
				<input id="updateExisting-number" class="form-control" type="text" ng-model="${srType}.casenumber">
			</div>
		</div>
		
	 	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="updateExisting-yourmsg">Your message <span class="required">*</span></label>
				<textarea id="updateExisting-yourmsg" class="form-control" name="updateExisting_yourmsg" rows="15" ng-model="${srType}.yourmsg" ng-required="sr.type == '${srType}'"></textarea>
			<span class="error" ng-show="(notComplete || serviceRequest.updateExisting_yourmsg.$touched) && (sr.type == '${srType}') && serviceRequest.updateExisting_yourmsg.$invalid">Please enter details above</span>
			</div>
      	</div>
	</div>
</div>