<%@ taglib prefix="c" uri="jakarta.tags.core"%>
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

<spring:htmlEscape defaultHtmlEscape="true" />
<c:set value="/asahiStaffPortal/_ui/responsive/common/images/GATORADE-NO-SUGAR-CONNECT-BG-2560x1364-01-21.jpg" var="bg" />

<template:master pageTitle="${pageTitle}">

	<jsp:attribute name="pageCss">
		<jsp:invoke fragment="pageCss" />
	</jsp:attribute>

	<jsp:attribute name="pageScripts">
		<jsp:invoke fragment="pageScripts" />
	</jsp:attribute>

	<jsp:body>
	    <div class="bg_image" style="background-image: url(${bg});" ng-app="asahistaffstorefront">
            <!-- <div id="mobile-header" class="branding-mobile">
                <div class="branding-mobile">
                    <div class="col-xs-4 col-sm-4 no-padding-left">
                        <div class="js-mobile-logo">
                            <%--populated by JS acc.navigation--%>
                        </div>
                    </div>
                </div>
            </div> -->
            <main data-currency-iso-code="${fn:escapeXml(currentCurrency.isocode)}">
                <%-- <spring:theme code="text.skipToContent" var="skipToContent" />
                <a href="#skip-to-content" class="skiptocontent" data-role="none">${fn:escapeXml(skipToContent)}</a>
                <spring:theme code="text.skipToNavigation" var="skipToNavigation" />
                <a href="#skiptonavigation" class="skiptonavigation" data-role="none">${fn:escapeXml(skipToNavigation)}</a> --%>


                <header:header hideHeaderLinks="${hideHeaderLinks}" />

                <a id="skip-to-content"></a>

                <c:choose>
                    <c:when test="${cmsPage.uid eq 'login' || (isForgetPassword != null && isForgetPassword == 'true')}">
                        <div class="pageBodyContent">
                            <div class="bodyContentGlobal" id="opacity-background-body-content"> <!--Opacity is different for the login page.-->
                                <common:globalMessages />
                                <jsp:doBody />
                            </div>
                            <%-- <footer:footer /> --%>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="pageBodyContent">
                            <div class="bodyContentGlobal"> <!--20px margin has been added to all page content as per UI Design.-->
                                <common:globalMessages />
                                <jsp:doBody />
                            </div>
                            <%-- <footer:footer /> --%>
                        </div>
                    </c:otherwise>
                </c:choose>
            </main>
        </div>
	</jsp:body>

</template:master>
