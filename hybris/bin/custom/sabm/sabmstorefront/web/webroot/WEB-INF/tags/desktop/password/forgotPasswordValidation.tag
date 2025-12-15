<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div id="validEmail">
	<div class="col-xs-12">
		<h1><spring:theme code="forgottenPwd.email.content.subject"/></h1>
	</div>
	<div class="col-md-12">
		<p><spring:theme code="forgottenPwd.email.content.body"/></p>
		<br> <br>
		<div class="links-list">
			<p>
				<c:url value="/login" var="loginUrl" />
				<a href="${loginUrl }"><spring:theme code="forgottenPwd.email.content.login"/></a>
			</p>
		</div>
	</div>
</div>