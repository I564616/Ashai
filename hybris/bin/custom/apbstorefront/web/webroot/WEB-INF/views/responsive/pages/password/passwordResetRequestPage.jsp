<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>

<template:page pageTitle="${pageTitle}">

	<c:if test="${not passwordRequestSent}">
		<div class="row">
			<div class="col-md-1 col-sm-2 hidden-xs"></div>
			<div class="col-md-10 col-sm-8 col-xs-12">
				<div class="col-md-5 col-sm-12 col-xs-12">
					<user:forgottenPwd />
				</div>
				<div class="col-md-5 col-md-offset-2 col-sm-12 col-xs-12 rightSlot">
					<c:if test="${cmsSite.uid eq 'sga'}">
						<user:forgottenEmail />
					</c:if>
				</div>
			</div>
		</div>
	</c:if>
</template:page>