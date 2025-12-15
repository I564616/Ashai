<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/login" var="loginUrl" scope="request"/>
<c:choose>
	<c:when test="${param.submitted}">
	<div class="email-sent">
	<div class="h1"><spring:theme code="text.registration.form.submitted.title" text="Your registration details submitted"/></div>
	<p><spring:theme code="text.registration.form.submitted.title.description" text="Thank you for choosing to contact the CUB Online Team. We will get in touch with you shortly."/></p>
	<br/>
	<p><a href="${loginUrl}"><spring:theme code="text.account.email.sent.login"/></a></p> 
</div>
	
	
</c:when>
<c:otherwise>			
<user:registrationRequest/>
</c:otherwise>
</c:choose>