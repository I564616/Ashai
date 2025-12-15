<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="WEBSITE_ERRORS" />

<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">
		<p>This enquiry should be used if you have found an error with the website.</p>
		
      	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="website-otherInfo">Please describe the issue below<span class="required">*</span></label>
				<textarea id="website-otherInfo" class="form-control" name="website_otherInfo" rows="15" ng-model="${srType}.otherinfo" ng-required="sr.type == '${srType}'"></textarea>
			<span class="error" ng-show="(notComplete || serviceRequest.website_otherInfo.$touched) && (sr.type == '${srType}') && serviceRequest.website_otherInfo.$invalid">Please enter details above</span>
			</div>
      	</div>
	</div>
</div>