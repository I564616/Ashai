<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="GENERAL_ENQUIRY" />

<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">
		<p>This enquiry should be used if you have an enquiry for CUB and it does not fit into one of the other enquiry categories.</p>
		
      	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="general-otherInfo">Your message<span class="required">*</span></label>
				<textarea id="general-otherInfo" class="form-control" name="general_otherInfo" rows="15" ng-model="${srType}.otherinfo" ng-required="sr.type == '${srType}'"></textarea>
			 <span class="error" ng-show="(notComplete || serviceRequest.general_otherInfo.$touched) && (sr.type == '${srType}') && serviceRequestgeneral_otherInfo.$invalid">Please enter details above</span>
			</div>
      	</div>
	</div>
</div>