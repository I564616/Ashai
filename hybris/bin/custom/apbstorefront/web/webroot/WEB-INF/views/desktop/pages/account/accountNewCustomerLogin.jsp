<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<c:url value="/login/register" var="registerActionUrl" />
<user:register actionNameKey="register.submit" action="${registerActionUrl}"/>