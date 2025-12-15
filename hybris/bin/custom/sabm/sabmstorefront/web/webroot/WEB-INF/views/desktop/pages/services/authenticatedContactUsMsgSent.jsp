<%--TODO: to clean up as this page is not in use. Latest contact us page is in serviceRequestEmailSent.jsp --%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="serviceRequestUrl" value="/" />
<c:url var="continueBrowsingUrl" value="/" />
<div class="service-request offset-bottom-medium">
	<div class="h1"><spring:theme code="text.authenticated.contact.us.form.sent.title" text="Message Sent"/></div>
	<p><spring:theme code="text.authenticated.contact.us.form.sent.description"/></p>
	<div class="row">
		<div class="col-xs-12 col-sm-6 col-md-4">
			<a href="${serviceRequestUrl}" class="btn btn-primary"><spring:theme code="text.authenticated.contact.us.form.sent.btn" /></a>
		</div>
	</div>
</div>
<br>
