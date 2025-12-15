<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<c:set value="/asahiStaffPortal/sga/en/AUD/login" var="staffPortalUrl" />
<c:set value="${cmsSite.uid eq 'sga'}" var="isSga" />

<template:page pageTitle="${pageTitle}">
	<div class="row display-flex display-flex-row flex-full flex-xxs-wrap position-relative">
        <div class="col-md-1 col-sm-2 hidden-xs"></div>
        <div class="add-height col-md-10 col-sm-8 col-xs-12">
            <div class="col-md-5 col-sm-12 col-xs-12 no-padding">
                <cms:pageSlot position="LeftContentSlot" var="feature" element="div" class="login-left-content-slot">
                    <cms:component component="${feature}"  element="div" class="login-left-content-component"/>
                </cms:pageSlot>
            </div>
            <div class="col-md-5 col-sm-12 col-xs-12 no-padding">
                <cms:pageSlot position="RightContentSlot" var="feature" element="div" class="login-right-content-slot">
                    <cms:component component="${feature}"  element="div" class="login-right-content-component"/>
                </cms:pageSlot>
            </div>
        </div>
        <div class="col-md-1 col-sm-2 hidden-xs"></div>

        <c:if test="${isSga}">
            <div class="col-md-10 col-sm-8 col-xs-12 col-sm-offset-2 col-md-offset-1 position-absolute position-xxs-relative bottom-0 display-flex display-flex-row">
                <div class="login-left-content-slot border-top">
                    Are you an Asahi staff member? <a href="${staffPortalUrl}" class="text-underline cursor"><strong>Access the Staff Portal</strong></a>
                </div>
            </div>
        </c:if>
	</div>

</template:page>