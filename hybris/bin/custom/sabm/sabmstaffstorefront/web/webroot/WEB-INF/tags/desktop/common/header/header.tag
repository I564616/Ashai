<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="hideHeaderLinks" required="false" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header"  %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>

<%-- Test if the UiExperience is currently overriden and we should show the UiExperience prompt --%>
<c:if test="${uiExperienceOverride and not sessionScope.hideUiExperienceLevelOverridePrompt}">
	<c:url value="/_s/ui-experience?level=" var="clearUiExperienceLevelOverrideUrl"/>
	<c:url value="/_s/ui-experience-level-prompt?hide=true" var="stayOnDesktopStoreUrl"/>
	<div class="backToMobileStore">
		<a href="${clearUiExperienceLevelOverrideUrl}"><span class="greyDot">&lt;</span><spring:theme code="text.swithToMobileStore" /></a>
		<span class="greyDot closeDot"><a href="${stayOnDesktopStoreUrl}">x</a></span>
	</div>
</c:if>



<div id="header" class="global-header">
	<cms:pageSlot position="TopHeaderSlot" var="component">
		<cms:component component="${component}"/>
	</cms:pageSlot>
	<div class="container">
	<div class="row">
		<div class="col-xs-4 col-xs-offset-4 col-sm-offset-0 col-sm-3">
			<div class="siteLogo hidden-xs hidden-sm">
<!-- 				<a href=""> -->
					<svg class="icon-full-logo">
					  <use xlink:href="#icon-full-logo"></use>    
					</svg>
<!-- 				</a> -->
			</div>
			<div class="siteLogo visible-sm">
				<svg class="icon-small-logo-custom-tab"><use xlink:href="#icon-small-logo"></use></svg>
			</div>
			<div class="siteLogoCustom text-center hidden-md hidden-lg">
				<a href="/"><svg class="icon-small-logo"><use xlink:href="#icon-full-logo"></use></svg></a>
			</div>
		</div>

		<div class="col-xs-4 col-sm-9">
					<ul class="nav clearfix">
						<c:if test="${empty hideHeaderLinks}">
							<c:if test="${uiExperienceOverride}">
								<li class="backToMobileLink">
									<c:url value="/_s/ui-experience?level=" var="backToMobileStoreUrl"/>
									<a href="${backToMobileStoreUrl}"><spring:theme code="text.backToMobileStore"/></a>
								</li>
							</c:if>

<%-- 							<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')"> --%>
<%-- 								<c:set var="maxNumberChars" value="25"/> --%>
<%-- 								<c:if test="${fn:length(user.firstName) gt maxNumberChars}"> --%>
<%-- 									<c:set target="${user}" property="firstName" value="${fn:substring(user.firstName, 0, maxNumberChars)}..."/> --%>
<%-- 								</c:if> --%>
<%-- 								<li class="logged_in"><ycommerce:testId code="header_LoggedUser"><spring:theme code="header.welcome" arguments="${user.firstName},${user.lastName}" htmlEscape="true"/></ycommerce:testId></li> --%>
<%-- 							</sec:authorize> --%>
<%-- 							<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')"> --%>
<%-- 								<li><ycommerce:testId code="header_Login_link"><a href="<c:url value="/login"/>"><spring:theme code="header.link.login"/></a></ycommerce:testId></li> --%>
<%-- 							</sec:authorize> --%>
							<%-- <li><ycommerce:testId code="header_myAccount"><a href="<c:url value="/my-account"/>"><spring:theme code="header.link.account"/></a></ycommerce:testId></li> --%>
						</c:if>
			<%-- 
						<cms:pageSlot position="HeaderLinks" var="link">
							<cms:component component="${link}" element="li"/>
						</cms:pageSlot> --%>
					
						<%-- <c:if test="${empty hideHeaderLinks}">
							<li><a href="<c:url value="/store-finder"/>"><spring:theme code="general.find.a.store" /></a></li>
							<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')"><li><ycommerce:testId code="header_signOut"><a href="<c:url value='/logout'/>"><spring:theme code="header.link.logout"/></a></ycommerce:testId></li></sec:authorize>
						</c:if>
						 --%>

						<c:url value="/saml/logout" context="/samlsinglesignon" var="ssoLogoutLink"/>
						<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
							<li>
								<ycommerce:testId code="header_signOut">
									<a href="${ssoLogoutLink}"/>
										<spring:theme code="header.link.logout"/>
									</a>
								</ycommerce:testId>
							</li>
						</sec:authorize>
					</ul>
		</div>	
		</div>
	</div>
</div>

<nav:breadcrumbs/>
