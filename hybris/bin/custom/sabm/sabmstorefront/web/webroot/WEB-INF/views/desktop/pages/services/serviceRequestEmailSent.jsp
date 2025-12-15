<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="serviceRequestUrl" value="/serviceRequest" />
<c:url var="continueBrowsingUrl" value="/Beer/c/10" />
<div class="service-request offset-bottom-large">
	<div class="h1"><spring:theme code="text.contactus.sent.title" text="Message sent"/></div>
	<p><spring:theme code="text.service.request.sent.description"/></p>
	<div class="row">
		<div class="col-xs-12 col-sm-6 col-md-4 margin-top-10-xs">
			<a href="${continueBrowsingUrl}" class="btn btn-primary"><spring:theme code="text.service.request.sent.btn.continue" /></a>
		</div>
	</div>
</div>
<br/>
