<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="loginUrl" value="/logout" />
<div class="email-sent">

	<div class="h1"><spring:theme code="text.account.email.sent.title" text="Check your inbox!"/></div>
	<p><spring:theme code="text.account.email.sent.description" arguments="${email }"/></p>
	<br/>
	<p><a href="${loginUrl}"><spring:theme code="text.account.email.sent.login"/></a></p> 
</div>
