<%--TODO: to clean up as this page is not in use. Latest contact us page is in serviceRequest.jsp --%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url var="sendUrl" value="/sendContactUsEmail" />
<input type="hidden" class="sendUrl-hiddden" value="${sendUrl }">

<spring:theme code="text.authenticated.contact.us.service.request.url" var="serviceRequestUrl"/>


<div class="service-request offset-bottom-medium">
	<div class="h1"><spring:theme code="text.authenticated.contact.us.title" text="Contact Us"/></div>

	<div class="row">
		<div class="col-md-8">
			<p><spring:theme code="text.authenticated.contact.us.description1"/></p>
		</div>
	</div>
	<div class="row">
		<div class="col-md-8">
			<p><spring:theme code="text.authenticated.contact.us.description2"/></p>
		</div>

		<div class="col-md-3 col-md-offset-1">
			<p><a href="${serviceRequestUrl}" class="btn btn-primary"><spring:theme code="text.authenticated.contact.us.service.request" /></a><p>
		</div>
	</div>


	<div class="margin-top-20">
		<form:form action="${sendUrl}" method="post">
		<div class="row">
			<div class="col-xs-12 col-md-4">
				<div class="form-group">
		       		<label for="contactUsSubject"><spring:theme code="text.authenticated.contact.us.form.subject"/></label>
		       		<div class="subject">
					    <input id="contactUsSubject" name="subject" type="text" class="form-control" />
					    <span class="error hidden"><spring:theme code="text.authenticated.contact.us.empty.subject" /></span>
					</div>
		      	</div>
		      	<div class="form-group">
		       		<label for="contactUsMessage"><spring:theme code="text.authenticated.contact.us.form.message"/></label>
		       		<div>
					    <textarea id="contactUsMessage" rows="15" class="form-control" name=message required><spring:theme code="text.authenticated.contact.message.palceholder"/></textarea>
					    <span class="error hidden"><spring:theme code="text.authenticated.contact.us.empty.message" /></span>
					</div>
		      	</div>

		      	<div class="form-group">
		      		<button type="submit" id="btnSend" class="btn btn-primary" disabled><spring:theme code="text.authenticated.contact.us.form.send"/></button>
		      	</div>
			</div>
		</div>
		</form:form>
	</div>

</div>
