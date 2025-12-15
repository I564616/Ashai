<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<template:page pageTitle="${pageTitle}">

	<div class="row">
		<div id="globalMessages" class="col-sm-8">
			<common:globalMessages/>
		</div>
	</div>
	
    <div id="ssoErrorlogin" class="ssologin pt-30">
        <div class="content-wrap" style="text-align: center;">
         <!--    <img src="/asahiStaffPortal/_ui/responsive/common/images/spinner.gif" /> -->
            <div class="login-page__headline">
                <div><spring:theme code="staff.portal.sso.error" /></div>
            </div>
        </div>
    </div>

</template:page>
