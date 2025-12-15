<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<c:set value="/asahiStaffPortal/_ui/responsive/common/images/ALB_Horizontal_Transparent_H70px_Homepage_Logo.png" var="siteLogo" />

<cms:pageSlot position="TopHeaderSlot" var="component" element="div" >
	<cms:component component="${component}" />
</cms:pageSlot>

<header class="js-mainHeader">
	<nav class="navigation navigation--top">
		<div class="row">
		    <div id="log_in_header" class="col-xs-5">
                <div class="nav__left">
                    <ul class="nav__links">
                        <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" >
                            <li class="liOffcanvas nav-left-padding-li col-xs-12">
                                <ycommerce:testId code="header_signOut">
                                    <c:url value="/saml/logout" context="/samlsinglesignon" var="ssoLogoutLink"/>
                                    <a href="${fn:escapeXml(ssoLogoutLink)}">
                                        <spring:theme code="header.link.logout" />
                                    </a>
                                </ycommerce:testId>
                            </li>
                        </sec:authorize>
                    </ul>
                </div>
		    </div>
			<div class="col-xs-2">
				<div class="nav__left js-site-logo">
				    <c:choose>
				        <c:when test="${not empty logo}">
				            <cms:pageSlot position="SiteLogo" var="logo" limit="1">
                                <cms:component component="${logo}" element="div" class="yComponentWrapper"/>
                            </cms:pageSlot>
				        </c:when>
				        <c:otherwise>
				            <img src="${siteLogo}" />
				        </c:otherwise>
				    </c:choose>
				</div>
			</div>
			<div class="col-xs-5"></div>
		</div>
	</nav>
	<%-- a hook for the my account links in desktop/wide desktop--%>
	<div class="hidden-xs hidden-sm js-secondaryNavAccount collapse" id="accNavComponentDesktopOne">
		<ul class="nav__links">

		</ul>
	</div>
	<div class="hidden-xs hidden-sm js-secondaryNavCompany collapse" id="accNavComponentDesktopTwo">
		<ul class="nav__links js-nav__links">

		</ul>
	</div>

</header>

<script type="text/javascript">
    // var customerLoggedIn= ${customerLoggedIn};
    // var showB2BUnitDropDown = ${b2bUnitDropDown};
    var excludeClasses = '';
    var excludeIds = '';
    var excludeClassesOnAjax = '';
    var excludeIdsOnAjax = '';
</script>

<cms:pageSlot position="BottomHeaderSlot" var="component" element="div"	class="container-fluid breadcrumb-wrapper">
	<cms:component component="${component}" />
</cms:pageSlot>
