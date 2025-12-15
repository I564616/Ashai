<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<template:page pageTitle="${pageTitle}">
    <sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')" >
        <div class="row pt-30">
            <div class="col-md-offset-1 col-sm-offset-2 col-md-10 col-sm-8 col-xs-12">
                <div class="col-xs-12">
                    <a href="/storefront/sga/en/AUD/login" class="text-underline capitalize"><strong>Back to ALB Connect</strong></a>
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </sec:authorize>
    <dic class="row">
        <%-- <div id="globalMessages" class="col-sm-8">
            <common:globalMessages/>
        </div> --%>

        <div class="col-lg-1 col-md-1 col-sm-2 hidden-xs"></div>
        <div class="col-lg-6 col-md-8 col-sm-7 col-xs-12">
            <div class="col-lg-8 col-md-7 col-xs-12 no-padding">
                <c:url value="/j_spring_security_check" var="loginActionUrl" /><br>
                <user:login actionNameKey="login.login" action="${loginActionUrl}"/>
                <%-- <cms:pageSlot position="LeftContentSlot" var="feature" element="div" class="login-left-content-slot">
                    <cms:component component="${feature}"  element="div" class="login-left-content-component"/>
                </cms:pageSlot> --%>
            </div>
            <div class="col-lg-4 col-md-5 col-xs-12 no-padding">
                <%-- <cms:pageSlot position="RightContentSlot" var="feature" element="div" class="login-right-content-slot">
                    <cms:component component="${feature}"  element="div" class="login-right-content-component"/>
                </cms:pageSlot> --%>
            </div>
        </div>
        <div class="col-md-5 col-sm-3 hidden-xs"></div>
    </div>
</template:page>
