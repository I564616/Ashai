<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:url value="/" var="homePageUrl"/>

<div class="keg-returns-section">
	<div class="login-section">
		<div class="login-page__headline">
			<spring:theme code="updatePwd.title.asahi"/>
		</div>
	</div>
	<div class="user-register__body">
		<spring:theme code="text.account.confirmation.password.updated"/>
	</div>
	<div class="forgotten-password forgotten-password-home-link">
			<a href="${homePageUrl}" class="site-anchor-link"><spring:theme code="updatePwd.backTo.homepage"/></a>
		</div>
	<br>
	<div class="user-register__body">
    </div>
</div>
