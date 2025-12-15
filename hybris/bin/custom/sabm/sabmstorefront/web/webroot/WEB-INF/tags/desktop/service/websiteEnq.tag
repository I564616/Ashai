<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="WEBSITE_FEEDBACK" />

<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">
		<p>This enquiry should be used if you have feedback or a query about the website.</p>
		
      	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="websiteEnq-otherInfo">Detail your enquiry/feedback<span class="required">*</span></label>
				<textarea id="websiteEnq-otherInfo" class="form-control" name="websiteEnq_otherInfo" rows="15" ng-model="${srType}.otherinfo" ng-required="sr.type == '${srType}'"></textarea>
			<span class="error" ng-show="(notComplete || serviceRequest.websiteEnq_otherInfo.$touched) && (sr.type == '${srType}') && serviceRequest.websiteEnq_otherInfo.$invalid">Please enter details above</span>
			</div>
      	</div>
	</div>
</div>