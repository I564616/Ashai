<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:theme code="resetPwd.title" var="pageTitle" />

<template:page pageTitle="${pageTitle}">
	<div class="row">
		<div class="col-md-1 col-sm-2 hidden-xs"></div>
		<div class="col-md-10 col-sm-8 col-xs-12">
			<div class="col-md-5 col-sm-12 col-xs-12">
				<div class="login-left-content-slot">
					<div class="login-section">
						<user:updatePwd />
					</div>
				</div>
			</div>
		</div>
		</div>
</template:page>
