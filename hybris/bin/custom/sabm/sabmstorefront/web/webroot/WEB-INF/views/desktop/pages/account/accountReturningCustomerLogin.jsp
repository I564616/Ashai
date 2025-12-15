<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<c:url value="/j_spring_security_check" var="loginActionUrl" />
<div class="col-md-4 col-md-push-6 login-form-col">
	<user:login actionNameKey="login.login" action="${loginActionUrl}"/>
</div>