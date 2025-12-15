<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<c:url value="/checkout/j_spring_security_check" var="loginAndCheckoutActionUrl" />
<div class="col-md-12">
	<user:login actionNameKey="checkout.login.loginAndCheckout" action="${loginAndCheckoutActionUrl}"/>
</div>