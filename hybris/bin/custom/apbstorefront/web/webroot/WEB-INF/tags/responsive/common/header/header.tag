<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>

<jsp:useBean id="asahiConfigJSUtil" class="com.apb.core.util.AsahiAdhocCoreUtil" />
<cms:pageSlot position="TopHeaderSlot" var="component" element="div" class="container">
	<cms:component component="${component}" />
</cms:pageSlot>

<c:set var="customerLoggedIn" value="0" />
<c:if test="${isCustomerLoggedIn}">
	<c:set var="customerLoggedIn" value="1" />
</c:if>
<input type="hidden" name="isAnonymousUser" value="${not isCustomerLoggedIn}" />

<c:set var="b2bUnitDropDown" value="false" />
<c:if test="${showB2BUnitDropDown}">
	<c:set var="b2bUnitDropDown" value="true" />
</c:if>
<c:choose>
	<c:when test="${cmsSite.uid ne 'sga'}">
		<input type="hidden" id="existSuperUser" value="${existSuperUser}"/>
	</c:when>
     <c:otherwise>
      <input type="hidden" id="existSuperUser" value="true"/>
	</c:otherwise>
</c:choose>

<c:set value="${false}" var="isBDECUSTOMERGROUP" />
<sec:authorize access="hasAnyRole('ROLE_BDECUSTOMERGROUP')" >
    <c:set value="${true}" var="isBDECUSTOMERGROUP" />
</sec:authorize>
<c:set value="${false}" var="isUserLoggedIn" />
<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
    <c:set value="${true}" var="isUserLoggedIn" />
</sec:authorize>

	<nav id="navigation-bar" class="navigation navigation--top">
<!--        It should not be hidden on mobile and tablet, since the mobile logo has been hidden now. @SM -->
            <div id="log_in_header" class="hidden-xs hidden-sm col-md-5 add-bottom-padding">
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

<!-- 
Do not need "Hi <Account Name>" anymore. 

								<li class="logged_in js-logged_in">
									<ycommerce:testId code="header_LoggedUser">
										<spring:theme code="header.welcome" arguments="${user.firstName},${user.lastName}" htmlEscape="true" />
									</ycommerce:testId>
								</li>
 -->
							</sec:authorize>
<!--                            This is the My Account section-->
                            <div class="hidden-xs hidden-sm">
                                 <cms:pageSlot position="HeaderLinks" var="link">
                                     <cms:component component="${link}" element="li" />
                                 </cms:pageSlot>
                            </div>

							<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')" >
								<li class="liOffcanvas nav-left-padding-li">
									<ycommerce:testId code="header_Login_link">
										<a href="<c:url value='/login'/>">
											<spring:theme code="header.link.login.apb" />
										</a>
									</ycommerce:testId>
								</li>
                                <div class="hidden-xs hidden-sm">
                                    <li class="navigationSeparator nav-left-padding-li">
                                        <p> | </p>
                                    </li>
                                    <li class="liOffcanvas nav-left-padding-li">
                                        <ycommerce:testId code="header_Login_link">
                                            <a href="<c:url value='/register'/>">
                                                <spring:theme code="header.link.registration" />
                                            </a>
                                        </ycommerce:testId>
                                    </li>
                                </div>
							</sec:authorize>
<!--
							<li class="liOffcanvas">
								<cms:pageSlot position="MiniCart" var="cart" element="div">
									<cms:component component="${cart}" element="div"/>
								</cms:pageSlot>
							</li>	
-->
							<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" >
								
								<li id="myAccountLabel" class="addPaddingMyAccount add-nav-bottom-padding">
									<a class="myAccountLinksHeader collapsed js-myAccount-toggle"><b>MY ACCOUNT</b></a>
								</li>
                                
                                <div class="hidden-xs hidden-sm">
                                        <li class="addPadding-nav navigationSeparator nav-left-logged-in-padding-symbol">
                                            <p> | </p>
                                        </li>
                                </div>

								<li class="liOffcanvas addPadding-nav nav-left-logged-in-padding">
									<ycommerce:testId code="header_signOut">
										<a href="<c:url value='/logout'/>" id="header-nav-logout-text">
											<spring:theme code="header.link.logout" />
										</a>
									</ycommerce:testId>
								</li>
							</sec:authorize>

						</c:if>
					</ul>
				</div>
			</div>
			<div class="hidden-xs hidden-sm col-md-2 add-bottom-padding">
				<div class="nav__left nav__logo js-site-logo">
					<cms:pageSlot position="SiteLogo" var="logo" limit="1">
						<cms:component component="${logo}" element="div" class="yComponentWrapper"/>
					</cms:pageSlot>
				</div>
			</div>
			<div class="hidden-xs hidden-sm col-md-5 add-bottom-padding pr-40">
				<div class="nav__right" id="navRightText">
					<ul class="nav__links nav__links--account">
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

<!--
Do not need to show "Hi <Account Name>" anymore.

								<li class="logged_in js-logged_in">
									<ycommerce:testId code="header_LoggedUser">
										<spring:theme code="header.welcome" arguments="${user.firstName},${user.lastName}" htmlEscape="true" />
									</ycommerce:testId>
								</li>
-->
							</sec:authorize>

<!--
							 <cms:pageSlot position="HeaderLinks" var="link">
								 <cms:component component="${link}" element="li" />
							 </cms:pageSlot>
-->


							<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')" >
<!--
Log In, Register & Minicart should not be displayed on the right anymore. Can deleted this from here. @SM
								<li class="liOffcanvas">
									<ycommerce:testId code="header_Login_link">
										<a href="<c:url value='/login'/>">
											<spring:theme code="header.link.login.apb" />
										</a>
									</ycommerce:testId>
								</li>
-->
                                <div class="hidden-md hidden-lg">
                                    <li class="liOffcanvas">
                                        <ycommerce:testId code="header_Login_link">
                                            <a href="<c:url value='/register'/>">
                                                <spring:theme code="header.link.registration" />
                                            </a>
                                        </ycommerce:testId>
                                    </li>
                                </div>
							</sec:authorize>

<!--
							<li class="liOffcanvas">
								<cms:pageSlot position="MiniCart" var="cart" element="div">
									<cms:component component="${cart}" element="div"/>
								</cms:pageSlot>
							</li>	
-->
						<!-- Populate B2BUnits -->
							<c:if test="${showB2BUnitDropDown eq 'true'}" >
								<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
								<li class="liOffcanvas b2bSelect">
								<a href="#" id="b2bUnits">Drop</a>
								<ul class="b2bDropDown">
								   <c:forEach items="${b2bUnits}" var="b2bUnit" varStatus="status">
								    <li data-value='${b2bUnit.key}'>
								    	<a href="#">${b2bUnit.key}</a>
								    </li>
								    </c:forEach>
								</ul>
								</li>
								</sec:authorize>
							</c:if>

                        <!-- Recommendation count -->
                        <c:if test="${isBDECUSTOMERGROUP}">
                            <%-- set sessionStorage
                            <c:if test="${not empty recommendationsCount}">
                                <script type="text/javascript"> sessionStorage.setItem('recommendations-count', recommendationsCount); </script>
                            </c:if> --%>

                            <li class="addPadding-nav">
                                <a href="<c:url value='/recommendation'/>" ><spring:theme code="header.link.recommendations" /></a>
                            </li>
                            <li class="addPadding-nav position-relative">
                                <div class="position-relative" data-recommendation-count="${not empty recommendationsCount ? recommendationsCount : null}">
                                   <img src="${commonResourcePath}/images/Star_Orange.svg" />
                                   <span class="recommendation-count">${recommendationsCount}</span>
                                </div>
                                <span class="recommendation-tooltip" style="display: none;"><i class="glyphicon glyphicon-triangle-top"></i><spring:theme code="sga.text.recommendation.page.item.added" /></span>
                            </li>
                            <li class="addPadding-nav navigationSeparator nav-left-logged-in-padding-symbol">
                                <p> | </p>
                            </li>
                        </c:if>

                        <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" >
                                <li class="liOffcanvas ">
                                    <cms:pageSlot position="MiniCart" var="cart" element="div">
                                        <cms:component component="${cart}" element="div"/>
                                    </cms:pageSlot>
                                </li>
<!--
Log out should not be displayed on the right anymore. Can deleted this from here. @SM
                                <li class="liOffcanvas">
                                    <ycommerce:testId code="header_signOut">
                                        <a href="<c:url value='/logout'/>">
                                            <spring:theme code="header.link.logout" />
                                        </a>
                                    </ycommerce:testId>
                                </li>
-->
                         </sec:authorize>

						</c:if>
					</ul>
				</div>
			</div>
	</nav>
<header class="js-mainHeader" id="full-header">
	<%-- a hook for the my account links in desktop/wide desktop--%>
	<div class="hidden-xs hidden-sm js-secondaryNavAccount collapse" id="accNavComponentDesktopOne">
		<ul class="nav__links">

		</ul>
	</div>
	<div class="hidden-xs hidden-sm js-secondaryNavCompany collapse" id="accNavComponentDesktopTwo">
		<ul class="nav__links js-nav__links">

		</ul>
	</div>
	<nav class="navigation navigation--middle js-navigation--middle">
		<div class="container-fluid" id="search-bar-container-fluid">
		
			<div class="row">
				<div class="mobile__nav__row mobile__nav__row--table">
					<div class="mobile__nav__row--table-group">
						<div class="mobile__nav__row--table-row">
						<c:if test="${cmsPage.uid ne 'checkoutPage'}">
							<div class="mobile__nav__row--table-cell visible-xs hidden-sm">
								<button class="mobile__nav__row--btn btn mobile__nav__row--btn-menu js-toggle-sm-navigation"
										type="button">
									<span class="glyphicon glyphicon-menu-hamburger"></span>
								</button>
							</div>

							<div class="mobile__nav__row--table-cell visible-xs mobile__nav__row--seperator">
								<ycommerce:testId code="header_search_activation_button">
									<button	class="mobile__nav__row--btn btn mobile__nav__row--btn-search js-toggle-xs-search hidden-sm hidden-md hidden-lg" type="button">
										<span class="glyphicon glyphicon-search"></span>
									</button>
								</ycommerce:testId>
							</div>
						</c:if>

<%-- 							<c:if test="${empty hideHeaderLinks}">
								<ycommerce:testId code="header_StoreFinder_link">
									<div class="mobile__nav__row--table-cell hidden-sm hidden-md hidden-lg mobile__nav__row--seperator">
										<a href="<c:url value="/store-finder"/>" class="mobile__nav__row--btn mobile__nav__row--btn-location btn">
											<span class="glyphicon glyphicon-map-marker"></span>
										</a>
									</div>
								</ycommerce:testId>
							</c:if> --%>

							<cms:pageSlot position="MiniCart" var="cart" element="div" class="miniCartSlot componentContainer mobile__nav__row--table hidden-sm hidden-md hidden-lg">
								<cms:component component="${cart}" element="div" class="mobile__nav__row--table-cell" />
							</cms:pageSlot>

						</div>
					</div>
				</div>
			</div>
			<div class="desktop__nav">
                <c:if test="${cmsPage.uid ne 'checkoutPage' and cmsPage.uid ne 'multiAccount'}">    <!-- Adding this to hide the hamburger menu on checkout page on tablet. -->
                    <div class="nav__left col-xs-12 col-sm-12 hidden-md hidden-lg" id="hamburger-nav-menu">
                        
                        <div class="col-xs-7 col-sm-9 visible-xs visible-sm mobile-menu">
                            <button class="btn js-toggle-sm-navigation" type="button" id="hamburger-menu-btn">
                                <div class="hamburger-menu">		
									<c:choose>
										<c:when test="${cmsSite.uid ne 'sga'}">
											<img class="hamburger-menu-icon" src="${commonResourcePath}/images/hamburger_menu.svg"  />
										</c:when>
										 <c:otherwise>
										  	<img class="hamburger-menu-icon" src="${commonResourcePath}/images/hamburger_menu_sga.svg"  /> 
										</c:otherwise>
									</c:choose>
                                    <span id="hamburger-text"><spring:theme code="nav.hamburger.menu"/></span>
                                </div>
                            </button>
                        </div>
                        <div class="col-xs-5 col-sm-3">
                            <div class="site-search" id="search-bar-nav-res">
                                <cms:pageSlot position="SearchBox" var="component">
                                    <cms:component component="${component}" element="div"/>
                                </cms:pageSlot>
                            </div>
                        </div>
                    </div>
                    
                    <div class="nav__right col-xs-6 col-xs-6 hidden-xs">
                        <ul class="nav__links nav__links--shop_info">
                            <li>
    <%-- 							<c:if test="${empty hideHeaderLinks}">
                                    <ycommerce:testId code="header_StoreFinder_link">
                                        <div class="nav-location hidden-xs">
                                            <a href="<c:url value="/store-finder"/>" class="btn">
                                                <span class="glyphicon glyphicon-map-marker"></span>
                                            </a>
                                        </div>
                                    </ycommerce:testId>
                                </c:if> --%>

                            </li>
    <%-- 
                            <li>
                                <cms:pageSlot position="MiniCart" var="cart" element="div" class="componentContainer">
                                    <cms:component component="${cart}" element="div"/>
                                </cms:pageSlot>
                            </li> --%>
                        </ul>
                    </div>
                </c:if>
			</div>
		</div>
	</nav>
	<a id="skiptonavigation"></a>
	<nav:topNavigation />
</header>

<script type="text/javascript">
    var customerLoggedIn= ${customerLoggedIn};
    var showB2BUnitDropDown = ${b2bUnitDropDown};
    var excludeClasses = '${asahiConfigJSUtil.getConfigValue("asahi.spinner.exclude.js.classes")}';
    var excludeIds = '${asahiConfigJSUtil.getConfigValue("asahi.spinner.exclude.js.ids")}';
    var excludeClassesOnAjax = '${asahiConfigJSUtil.getConfigValue("asahi.spinner.exclude.ajax.classes")}';
    var excludeIdsOnAjax = '${asahiConfigJSUtil.getConfigValue("asahi.spinner.exclude.ajax.ids")}';

    // To set up Recommendation count for staff portal users only
    var isBDECUSTOMERGROUP = ${isBDECUSTOMERGROUP};
    var isUserLoggedIn = ${isUserLoggedIn};
    if (isBDECUSTOMERGROUP) {
        if (${not empty recommendationsCount}) {
            sessionStorage.setItem('recommendations-count', ${recommendationsCount});
        }
    }

    if (!isUserLoggedIn) {
        /* Check if site is in iframe. Do not clear sessionStorage while in the SmartEdit */
         if (window.location === window.parent.location && window.parent.location.pathname !== '/smartedit/') {
             console.log('Not in the SmartEdit iframe');
             sessionStorage.clear();
         } else {
             console.log('In the SmartEdit iframe');
         }
    }
</script>
<c:remove var="isCustomerLoggedIn" scope="session" />