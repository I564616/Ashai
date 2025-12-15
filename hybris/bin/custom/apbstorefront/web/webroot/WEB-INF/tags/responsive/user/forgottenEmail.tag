<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="login-section forgotten-email">
    <div class="login-page__headline">
		<spring:theme code="forgottenEmail.alb.title" />
	</div>
    <div class="description">
        <spring:theme code="forgottenEmail.alb.description1" />
    </div>
    <ul>
        <li><spring:theme code="forgottenEmail.alb.fullname" /></li>
        <li><spring:theme code="forgottenEmail.alb.account.number" /></li>
        <li><spring:theme code="forgottenEmail.alb.abn" /></li>
    </ul>
    <div class="description">
        <spring:theme code="forgottenEmail.alb.description2"/>
    </div>
    <div class="contact-info">
       <%--  <strong><spring:theme code="forgottenEmail.alb.phone" /></strong> <spring:theme code="forgottenEmail.alb.phone.value" /> <br> --%>
        <strong><spring:theme code="forgottenEmail.alb.email" /></strong> <spring:theme code="forgottenEmail.alb.email.value" />
    </div>
</div>