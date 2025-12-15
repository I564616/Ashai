<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<template:page pageTitle="${pageTitle}">
    <div class="row">
        <div id="globalMessages" class="col-sm-8">
            <common:globalMessages/>
        </div>
    </div>

    <div id="page-portalLogin" class="item left">
        <div class="content-wrap">
            <img class="page-hero visible-sm-block visible-md-block visible-lg-block"
                 src="/staffPortal/_ui/desktop/SABMiller/img/loading-page.png" alt="">
            <img class="page-hero visible-xs-block" src="/staffPortal/_ui/desktop/SABMiller/img/loading-page-mobile.png"
                 alt="">
            <div class="clearfix"></div>
        </div>
    </div>
    <div class="item right">
        <div class="content-wrap">
        </div>

        <!-- Content for Error Page -->
        <div class="content-wrap">
            <svg class="icon-blue-cross">
                <use xlink:href="#icon-blue-cross"></use>
            </svg>


            <div class="h3"><spring:theme code="staff.portal.sso.error" /></div>
            <%--<c:url value="/saml/login" context="/samlsinglesignon" var="backToLoginPageLink"/>--%>
            <%-- <c:url value="/login" var="backToLoginPageLink"/>
            <a href="${backToLoginPageLink}" class="btn btn-primary btn-large margin-top-30">Go to Login Page</a> --%>
        </div>


    </div>

</template:page>
