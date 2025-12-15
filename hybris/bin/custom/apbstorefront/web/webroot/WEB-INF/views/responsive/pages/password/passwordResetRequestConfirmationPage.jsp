<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:url value="/login" var="loginUrl"/>

<template:page pageTitle="${pageTitle}">
	<div class="row">
        <div class="col-md-1 col-sm-2 hidden-xs"></div>
		<div class="col-md-10 col-sm-8 col-xs-12">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<div class="login-left-content-slot">
					<div class="login-section">
						<div class="login-page__headline">
							<spring:theme code="forgottenPwd.title.asahi"/>
						</div>
						<div class="login__description">
							<c:choose>
								<c:when test="${cmsSite.uid eq 'sga'}">
									<spring:theme code="account.confirmation.forgotten.password.link.sent.sga"/>
								</c:when>
								<c:otherwise>
									<spring:theme code="account.confirmation.forgotten.password.link.sent.asahi"/>
								</c:otherwise>
							</c:choose>
						</div>
						<div class="forgotten-password-link">
							<a href="${loginUrl}" class="site-anchor-link"><spring:theme code="forgottenPwd.proceedTo.login"/></a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>	
</template:page>
