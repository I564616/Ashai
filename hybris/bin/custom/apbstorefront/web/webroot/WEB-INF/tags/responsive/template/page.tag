
<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="pageCss" required="false" fragment="true"%>
<%@ attribute name="pageScripts" required="false" fragment="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>

<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/responsive/common/header"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/responsive/common/footer"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<c:set value="${false}" var="isBDECUSTOMERGROUP" />
<sec:authorize access="hasAnyRole('ROLE_BDECUSTOMERGROUP')" >
    <c:set value="${true}" var="isBDECUSTOMERGROUP" />
</sec:authorize>

<spring:htmlEscape defaultHtmlEscape="true" />
<template:master pageTitle="${pageTitle}">

	<jsp:attribute name="pageCss">
		<jsp:invoke fragment="pageCss" />
	</jsp:attribute>

	<jsp:attribute name="pageScripts">
		<jsp:invoke fragment="pageScripts" />
	</jsp:attribute>

	<jsp:body>
		<!-- Google Tag Manager (noscript) -->
		<noscript>
			<iframe src="https://www.googletagmanager.com/ns.html?id=${googleTagManagerId}"
				height="0" width="0" style="display:none;visibility:hidden">
			</iframe>
		</noscript>
		<!-- End Google Tag Manager (noscript) -->
		<!--        <div class="bg_image">-->
        <div class="bg_image" style="background-image: url(${media});" >
            <div id="mobile-header" class="branding-mobile hidden-md hidden-lg">

                <div class="col-xs-6 col-sm-6 no-padding-left-text">
                <!--            Login Register stuff needs to go here, exactly as header.tag-->
                    <div class="nav__left">
                        <ul class="nav__links nav__links--account no-padding-left-text">
                            <c:if test="${empty hideHeaderLinks}">
                                <c:if test="${uiExperienceOverride}">
                                    <li class="backToMobileLink"><c:url
                                            value="/_s/ui-experience?level=" var="backToMobileStoreUrl" />
                                        <a href="${backToMobileStoreUrl}"> <spring:theme
                                                code="text.backToMobileStore" />
                                        </a>
                                    </li>
                                </c:if>

                                <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
                                    <c:set var="maxNumberChars" value="25" />
                                    <c:if test="${fn:length(user.firstName) gt maxNumberChars}">
                                        <c:set target="${user}" property="firstName"
                                            value="${fn:substring(user.firstName, 0, maxNumberChars)}..." />
                                    </c:if>

                                   </sec:authorize>
                                
                                <div class="hidden-xs hidden-sm header-my-account-text">
                                </div>

                                <sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')" >
                                    <li class="liOffcanvas addPadding-nav">
                                        <ycommerce:testId code="header_Login_link">
                                            <a class="no-padding-left-text" href="<c:url value='/login'/>">
                                                <spring:theme code="header.link.login.apb" />
                                            </a>
                                        </ycommerce:testId>
                                    </li>
                                </sec:authorize>
                                   <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" >
                                    <li class="liOffcanvas addPadding-nav">
                                        <ycommerce:testId code="header_signOut">
                                            <a href="<c:url value='/logout'/>">
                                                <spring:theme code="header.link.logout" />
                                            </a>
                                        </ycommerce:testId>
                                    </li>
                                </sec:authorize>

                            </c:if>
                        </ul>
                    </div>
                </div>
                <div class="col-md-4 no-padding-left">
                    <div class="js-mobile-logo">
                    <%--populated by JS acc.navigation--%>
                   

                    </div>
                </div>
                <div class="col-xs-6 col-sm-6 no-padding-right-text">
                    <div class="nav__right" id="navRightText">
                        <ul class="nav__links nav__links--account pull-sm-right no-padding-left">
                            <c:if test="${empty hideHeaderLinks}">
                                <c:if test="${uiExperienceOverride}">
                                    <li class="backToMobileLink"><c:url
                                            value="/_s/ui-experience?level=" var="backToMobileStoreUrl" />
                                        <a href="${backToMobileStoreUrl}"> <spring:theme
                                                code="text.backToMobileStore" />
                                        </a>
                                    </li>
                                </c:if>

                                <sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')" >
                                    <li class="liOffcanvas nav-padding-top">
                                        <ycommerce:testId code="header_Login_link">
                                            <a href="<c:url value='/register'/>">
                                                <spring:theme code="header.link.registration" />
                                            </a>
                                        </ycommerce:testId>
                                    </li>
                                </sec:authorize>

                                <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
                                    <c:set var="maxNumberChars" value="25" />
                                    <c:if test="${fn:length(user.firstName) gt maxNumberChars}">
                                        <c:set target="${user}" property="firstName"
                                            value="${fn:substring(user.firstName, 0, maxNumberChars)}..." />
                                    </c:if>

    
<!--    Do not need to show "Hi <Account Name>" anymore.-->

                                    <li class="logged_in js-logged_in hidden-sm hidden-xs hidden-md hidden-lg">
                                        <ycommerce:testId code="header_LoggedUser">
                                            <spring:theme code="header.welcome" arguments="${user.firstName},${user.lastName}" htmlEscape="true" />
                                        </ycommerce:testId>
                                    </li>
    
                                </sec:authorize>

                                <c:if test="${isBDECUSTOMERGROUP}">
                                    <li class="addPadding-nav hidden-xs">
                                        <a href="<c:url value='/recommendation'/>" ><spring:theme code="header.link.recommendations" /></a>
                                    </li>
                                    <li class="addPadding-nav position-relative hidden-xs">
                                        <div class="position-relative" data-recommendation-count="${not empty recommendationsCount ? recommendationsCount : null}">
                                           <img src="${commonResourcePath}/images/Star_Orange.svg" />
                                           <span class="recommendation-count">${recommendationsCount}</span>
                                        </div>
                                        <span class="recommendation-tooltip" style="display: none;"><i class="glyphicon glyphicon-triangle-top"></i><spring:theme code="sga.text.recommendation.page.item.added" /></span>
                                    </li>
                                    <li class="addPadding-nav nav-left-logged-in-padding-symbol hidden-xs">
                                        <p> | </p>
                                    </li>
                                </c:if>

                                <sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')" >
                                </sec:authorize>

                            <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" >
                                    <li class="liOffcanvas">
                                        <cms:pageSlot position="MiniCart" var="cart" element="div">
                                            <cms:component component="${cart}" element="div"/>
                                        </cms:pageSlot>
                                    </li>
   
                             </sec:authorize>

                            </c:if>
                        </ul>
                    </div>
                </div>
    <!-- Login Register stuff needs to go here, exactly as header.tag-->
                </div>
           
            <main data-currency-iso-code="${fn:escapeXml(currentCurrency.isocode)}">
                <spring:theme code="text.skipToContent" var="skipToContent" />
                <a href="#skip-to-content" class="skiptocontent" data-role="none">${fn:escapeXml(skipToContent)}</a>
                <spring:theme code="text.skipToNavigation" var="skipToNavigation" />
                <a href="#skiptonavigation" class="skiptonavigation" data-role="none">${fn:escapeXml(skipToNavigation)}</a>


                <header:header hideHeaderLinks="${hideHeaderLinks}" />

                <a id="skip-to-content"></a>
                
                <c:if test ="${cmsSite.uid eq 'sga'}">
                	<div id="sgaSite">
						<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" >
							<span id="userIsLoggedIn"></span>
							<input type="hidden" id="makeLoginCall" value="${makeLoginCall}" />
						</sec:authorize>
					</div>
                </c:if>
                
                <c:choose>
                    <c:when test="${cmsPage.uid eq 'homepage' && isForgetPassword != 'true' && isPasswordUpdated!='true'}">
						<c:choose>
							<c:when test="${cmsSite.uid eq 'sga'}">
	<!--							pageBodyContent is differenet for SGA. -->
								<div class="pageBodyContent" id="this-is-homepage"> 
								   <div class="bodyContentGlobal" id="no-background-body-content"> <!-- 20px margin has been removed from the homepage.-->
										<common:globalMessages />
										<cart:cartRestoration />
										<jsp:doBody />
									</div>
									<footer:footer />
								</div>
							</c:when>
							<c:otherwise>
	<!--							pageBodyContent for APB. -->
								<div class="pageBodyContent" id="page-body-homepage"> 
									<div class="bodyContentGlobal" id="no-background-body-content"> <!--20px margin has been removed from the homepage.-->
										<common:globalMessages />
										<cart:cartRestoration />
										<jsp:doBody />
									</div>
									<footer:footer />
								</div>
							</c:otherwise>
						</c:choose>
                    </c:when> 
                    <c:when test="${cmsPage.uid eq 'login' || (isForgetPassword != null && isForgetPassword == 'true')  
                    || (isPasswordUpdated !=null && isPasswordUpdated=='true')}">
                        <div class="pageBodyContent">
                            <cms:pageSlot position="BottomHeaderSlot" var="component" element="div"	class="container-fluid">
                                <cms:component component="${component}" />
                            </cms:pageSlot>
                            <div class="bodyContentGlobal display-flex display-flex-column justify-content-stretch" id="opacity-background-body-content"> <!--Opacity is different for the login page.-->
                                <common:globalMessages />
                                <cart:cartRestoration />
                                <jsp:doBody />
                            </div>
                            <footer:footer />
                        </div>
                    </c:when>
                    <c:when test="${cmsPage.uid eq 'checkoutPage'}">
                        <div class="pageBodyContent only-header-height" id="page-body-checkout"> 
                            <div class="bodyContentGlobal only-header-height"> <!--Opacity is different for the login page.-->
                                <common:globalMessages />
                                <cart:cartRestoration />
                                <jsp:doBody />
                            </div>
                            <footer:footer />
                        </div>
                    </c:when>
                    <c:when test="${cmsPage.uid eq 'multiAccount'}">
                        <div class="pageBodyContent">
                            <div class="bodyContentGlobal">
                                <!--Opacity is different for the login page.-->
                                <common:globalMessages />
                                <cart:cartRestoration />
                                <jsp:doBody />
                            </div>
                            <footer:footer />
                        </div>
                    </c:when>
                    <c:when test="${cmsPage.uid eq 'cartPage'}">
                        <div class="pageBodyContent" id="page-body-cart">
                            <cms:pageSlot position="BottomHeaderSlot" var="component" element="div"	class="container-fluid">
                                <cms:component component="${component}" />
                            </cms:pageSlot>
                            <div class="bodyContentGlobal"> <!--May not need condition for checkout page once, known bug is fixed.-->
                                <common:globalMessages />
                                <cart:cartRestoration />
                                <jsp:doBody />
                            </div>
                            <footer:footer />
                        </div>
                    </c:when>
					<c:when test="${cmsPage.uid eq 'productDetails'}">
                        <div class="pageBodyContent no-iOS-scroll">
                            <cms:pageSlot position="BottomHeaderSlot" var="component" element="div"	class="container-fluid">
                                <cms:component component="${component}" />
                            </cms:pageSlot>
                            <div class="bodyContentGlobal"> <!--20px margin has been added to all page content as per UI Design.-->
                                <common:globalMessages />
                                <cart:cartRestoration />
                                <jsp:doBody />
                            </div>
                            <footer:footer />
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="pageBodyContent">
                            <cms:pageSlot position="BottomHeaderSlot" var="component" element="div"	class="container-fluid">
                                <cms:component component="${component}" />
                            </cms:pageSlot>
                            <div class="bodyContentGlobal"> <!--20px margin has been added to all page content as per UI Design.-->
                                <common:globalMessages />
                                <cart:cartRestoration />
                                <jsp:doBody />
                            </div>
                            <footer:footer />
                        </div>
                    </c:otherwise>
                </c:choose>
            </main>
        </div>
	</jsp:body>
</template:master>
