<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>


<template:page pageTitle="${pageTitle}">

	<div class="row">
		<div id="globalMessages" class="col-sm-8">
			<common:globalMessages/>
		</div>
	</div>

	<div class="row">
		<c:url value="/j_spring_security_check" var="loginActionUrl" />
		<user:login actionNameKey="login.login" action="${loginActionUrl}"/>
	</div>

</template:page>
