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
	
	<c:set var="req" value="${pageContext.request}"/>
	<c:set var="scheme" value="${req.scheme}"/>
	<c:set var="localName" value="${req.localName}"/>
	<c:set var="localPort" value="${req.localPort}"/>
	<c:url var="baseUrl" value="${scheme }://${localName }:${localPort }/samlsinglesignon/saml/asahiStaffPortal/sga/en/AUD/customer-search"/>

    <div id="ssologin" class="ssologin pt-30">
        <div class="content-wrap" style="text-align: center;">
            <img src="/asahiStaffPortal/_ui/responsive/common/images/spinner.gif" />
            <div class="login-page__headline">
                <div><spring:theme code="staff.portal.sso.redirect" /></div>
            </div>
        </div>

        <!-- Content for Error Page -->
        <!-- <div class="content-wrap">
            <svg class="icon-blue-cross">
                <use xlink:href="#icon-blue-cross"></use>
            </svg>
            <div class="h3">There seems to be a problem with your credentials. Please click the button below to try again. If the issue persists please call the Service Desk on 2244.</div>
        </div> -->
    </div>

</template:page>
