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
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:url value="/search/autocomplete/SearchBox" var="autocompleteUrl"/>
<c:url value="/search/" var="searchUrl"/>
<c:url value="/cart/" var="cartUrl"/>
<c:url value="/recommendation" var="recommendationUrl"/>

<c:if test="${isNAPGroup}">
    <style>
        .navbar-form .product-pick-selectors {
            display: none;
        }
    </style>
</c:if>

<input type="hidden" id="nap-group-id" value="${isNAPGroup}">

<header id="header" class="global-header hidden-xs">
	<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP, ROLE_B2BORDERCUSTOMER')">
		<audio id="dealsAudio" preload="none">
			<source src="${staticHostPath}${siteStaticsContextPath}/img/pop_drip.mp3" type="audio/mpeg">
		</audio>
	</sec:authorize>
	<%-- Dev need update here for impersonation!!!! --%>
	<div class="impersonation">
		<div class="container">	
			<span><spring:theme code="text.header.impersonation.mode.username" arguments="${impersonate}"/></span>
		</div>
	</div>
	<c:if test="${bdeUser}">
	    <span class="hidden" id="bdeUser"></span>
    </c:if>
	<div class="container">
		<div class="row">
			<div class="col-sm-12">
		        <div class="siteLogo hidden-sm">
		          <sec:authorize access="hasAnyRole('ROLE_B2BASSISTANTGROUP') and !hasAnyRole('ROLE_B2BADMINGROUP', 'ROLE_B2BORDERCUSTOMER', 'ROLE_B2BINVOICECUSTOMER')">
		            <a href="/your-business"><svg class="icon-full-logo"><use xlink:href="#icon-full-logo"></use></svg></a>
		          </sec:authorize>
		          <sec:authorize access="hasAnyRole('ROLE_ANONYMOUS,ROLE_PAGROUP,ROLE_B2BADMINGROUP, ROLE_B2BORDERCUSTOMER, ROLE_B2BINVOICECUSTOMER')">
		            <a href="/"><svg class="icon-full-logo"><use xlink:href="#icon-full-logo"></use></svg></a>
		          </sec:authorize>
		        </div>
				
				<div class="siteLogo visible-sm">
					<sec:authorize access="hasAnyRole('ROLE_B2BASSISTANTGROUP') and !hasAnyRole('ROLE_B2BADMINGROUP, ROLE_B2BORDERCUSTOMER, ROLE_B2BINVOICECUSTOMER')">
					<a href="/your-business"><svg class="icon-small-logo-custom-tab"><use xlink:href="#icon-small-logo"></use></svg></a>
					</sec:authorize>
		            <sec:authorize access="hasAnyRole('ROLE_ANONYMOUS,ROLE_PAGROUP,ROLE_B2BADMINGROUP, ROLE_B2BORDERCUSTOMER, ROLE_B2BINVOICECUSTOMER')">
					<a href="/"><svg class="icon-small-logo-custom-tab"><use xlink:href="#icon-small-logo"></use></svg></a>
					</sec:authorize>
				</div>
				
				<div class="global-header-list megamenu">
					<ul class="clearfix">
						<div class="pull-right">
						<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')">
							<li><a href="<c:url value='/contactus'/>" title='<spring:theme code="text.homepage.header.contactus"/>'><spring:theme code="text.homepage.header.contactus"/></a></li>
						</sec:authorize>
							<c:choose>
								<c:when test="${empty hideHeaderLinks}">
									<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
										<c:set var="maxNumberChars" value="25"/>
										<c:if test="${fn:length(user.firstName) gt maxNumberChars}">
											<c:set target="${user}" property="firstName" value="${fn:substring(user.firstName, 0, maxNumberChars)}..."/>
										</c:if>
										<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP, ROLE_B2BORDERCUSTOMER')">
										<c:if test="${!isNAPGroup}">
										<cms:component uid="DealHeaderComponent"/>
										<li class="recommendation">
											<cms:component uid="RecommendationsHeaderComponent"/>
										</li>
										</c:if>
									    <cms:component uid="SupportHeaderComponent"/>
										
											<cms:pageSlot position="DeliveryDatepickerBar" var="datepicker" limit="1">
												<cms:component component="${datepicker}" element="li" class="delivery-header delivery-header-desktop" />
											</cms:pageSlot>
										

										<c:if test="${!isNAPGroup}">
										<c:if test="${requestScope['jakarta.servlet.forward.request_uri'] ne '/sabmStore/en/cart/'}">
											<li class="miniCart">
												<cms:pageSlot position="MiniCart" var="cart" limit="1">
													<cms:component component="${cart}" />
												</cms:pageSlot>
											</li>
										</c:if>
										</c:if>
										
										<li class="userInfoComponent">
											<a href="#">
												<svg class="icon-profile01">
													<use xlink:href="#icon-profile01"></use>
												</svg>
												<label>${user.firstName}</label>
											</a>
											
											<ul class="user-list">
												<li class="user-list-item relative">
													<div class="select-list">
													
														<input type="hidden" id="userSelectBusinessUnit" value="${user.currentB2BUnit.name }">
														<div  data-value="" class="select-btn header">
															 <label>${user.currentB2BUnit.name}</label>
														</div>
														
														<ul class="select-items header sub-menu">
															<div class="user-info" style="padding-bottom:1px;">
																<label>
																	<span>${user.firstName}&nbsp;${user.lastName}</span>
																	${user.currentB2BUnit.name}
																	<span><fmt:parseNumber value="${user.currentB2BUnit.uid}"/></span>
																</label>
															</div>
															<c:forEach items="${user.branches}" var="group">
																<c:if test="${group.name ne user.currentB2BUnit.name}">
																<li data-value="${user.firstName} - ${group.name }" data-id="${group.uid }" class="company-name"><a href="<c:url value="/header/b2bunit/${group.uid}"/>">${group.name}</a></li>
																</c:if>
															</c:forEach>

															<li class="highlight-link" data-url="<c:url value='/your-business'/>"><spring:theme code="text.home.header.business"/></li>

															<li class="highlight-link" data-url="<c:url value='/your-business/billing'/>"><spring:theme code="text.home.header.business.account"/></li>
															
															<li class="highlight-link bde-view-only" data-url="<c:url value='/businessEnquiry'/>"><spring:theme code="text.homepage.header.dropdown.businessenquiry"/></li>
															<li class="highlight-link" data-url="<c:url value='/your-notifications'/>"><spring:theme code="text.homepage.header.dropdown.notifications"/></li>
															<c:if test="${isAutoPayEnabled && !isNAPGroup}">
															<li class="highlight-link" data-url="<c:url value='/autopay/landing'/>"><spring:theme code="text.homepage.header.dropdown.autopay"/></li>
															</c:if>
															<div class="logout-link">
															<li class="highlight-link" data-url="<c:url value='/logout'/>"><ycommerce:testId code="header_signOut"><spring:theme code="header.link.logout"/></ycommerce:testId></li>
															</div>
															<%-- Dev need update here for impersonation --%>
															<li class="highlight-link inpersonate-link" data-url="<c:url value='/impersonate/change'/>"><spring:theme code="header.link.impersonation"/></li>
														</ul>
													</div>
												</li>
											</ul>
										</li>
										</sec:authorize>
										<sec:authorize access="!hasAnyRole('ROLE_B2BADMINGROUP, ROLE_B2BORDERCUSTOMER')">
											<li class="userInfoComponent">
												<a href="#">
													<svg class="icon-profile01">
														<use xlink:href="#icon-profile01"></use>
													</svg>
													<label>${user.firstName}</label>
												</a>
												
												<ul class="user-list">
													<li class="user-list-item relative">
														<div class="select-list">
														
															<input type="hidden" id="userSelectBusinessUnit" value="${user.currentB2BUnit.name }">
															<div  data-value="" class="select-btn header">
																 <label>${user.currentB2BUnit.name}</label>
															</div>
															
															<ul class="select-items header sub-menu">
																<div class="user-info">
																	<label>
																		<span>${user.firstName}&nbsp;${user.lastName}</span>
																		${user.currentB2BUnit.name}
																	</label>
																</div>
																<c:forEach items="${user.branches}" var="group">
																	<c:if test="${group.name ne user.currentB2BUnit.name}">
																	<li data-value="${user.firstName} - ${group.name }" data-id="${group.uid }" class="company-name"><a href="<c:url value="/header/b2bunit/${group.uid}"/>">${group.name}</a></li>
																	</c:if>
																</c:forEach>
																<li class="highlight-link" data-url="<c:url value='/your-business'/>"><spring:theme code="text.home.header.business"/></li>
																<li class="highlight-link" data-url="<c:url value='/your-business/billing'/>"><spring:theme code="text.home.header.business.account"/></li>
																
																<li class="highlight-link bde-view-only" data-url="<c:url value='/businessEnquiry'/>"><spring:theme code="text.homepage.header.dropdown.businessenquiry"/></li>
																<li class="highlight-link" data-url="<c:url value='/your-notifications'/>"><spring:theme code="text.homepage.header.dropdown.notifications"/></li>
																<c:if test="${isAutoPayEnabled}">
																<li class="highlight-link" data-url="<c:url value='/autopay/landing'/>"><spring:theme code="text.homepage.header.dropdown.autopay"/></li>
																</c:if>
																<div class="logout-link">
																<li class="highlight-link" data-url="<c:url value='/logout'/>"><ycommerce:testId code="header_signOut"><spring:theme code="header.link.logout"/></ycommerce:testId></li>
																</div>
																<%-- Dev need update here for impersonation --%>
																<li class="highlight-link inpersonate-link" data-url="<c:url value='/impersonate/change'/>"><spring:theme code="header.link.impersonation"/></li>
															</ul>
														</div>
													</li>
												</ul>
											</li>
										</sec:authorize>
									</sec:authorize>
								</c:when>
								<c:otherwise>
									<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP, ROLE_B2BORDERCUSTOMER')">
										<%--<li>
										<div class="delivery-header delivery-header-desktop">
						
											<!--	<svg class="icon-calendar offset-icon"><use xlink:href="#icon-calendar"></use></svg> -->
												<span><spring:theme code="text.account.order.delivery"/>&nbsp;</span>
												<span class="delivery-readonly"><fmt:formatDate value="${cartData.requestedDeliveryDate}" pattern="EE dd/MM/yyyy"/></span>
											</div>
										</li>--%>
										<cms:pageSlot position="DeliveryDatepickerBar" var="datepicker" limit="1">
											<cms:component component="${datepicker}" element="li" class="delivery-header delivery-header-desktop" />
										</cms:pageSlot>
									</sec:authorize>
								</c:otherwise>
							</c:choose>
						</div>
					</ul> <!-- END: .clearfix -->
					<div class="js-only-signout hidden">
						<a href="/logout"><ycommerce:testId code="header_signOut"><spring:theme code="header.link.logout"/></ycommerce:testId></a>
					</div>
				</div> <!-- END: .global-header-list -->
			</div>
		</div>
	</div>
</header>

<nav id="nav" class="global-navigation navbar megamenu navbar-default">
	<div class="navbar-header hidden-sm hidden-md hidden-lg">
	 	<%-- Dev need update here for impersonation --%>
	    <div class="impersonation">
			<div class="container">	
				<span><spring:theme code="text.header.impersonation.mode.username" arguments="${impersonate}"/></span>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-4">

				<a href="#" data-toggle="collapse" data-target="#navbar-collapse-1" class="navbar-toggle collapsed" aria-expanded="false">
          <div class="icon-hamburger"></div>
        </a>
			</div>
			<div class="col-xs-4">
				<div class="siteLogoCustom text-center">
					<a href="/"><svg class="icon-small-logo"><use xlink:href="#icon-full-logo"></use></svg></a>
				</div>
			</div>
			<div class="col-xs-4">
				<a href="#" class="navbar-close" style="display:none">&nbsp;</a>
				<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP,ROLE_B2BORDERCUSTOMER')">
					<span class="cart-mobile bde-view-only ${isNAPGroup ? 'hide' : ''}">
						<c:if test="${requestScope['jakarta.servlet.forward.request_uri'] ne '/sabmStore/en/cart/'}">
							<cms:pageSlot position="MiniCart" var="cart" limit="1">
								<cms:component component="${cart}" />
							</cms:pageSlot>
						</c:if>	
					</span>
				</sec:authorize>
				<div class="js-only-signout signout hidden">
					<a href="/logout"><ycommerce:testId code="header_signOut"><spring:theme code="header.link.logout"/></ycommerce:testId></a>
				</div>
			</div>
		</div>
	</div>
	<div id="navbar-collapse-1" class="navbar-collapse collapse">
		<div class="nav-content-container">
			<div class="mobile-head hidden-sm hidden-md hidden-lg">
			  <sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP,ROLE_B2BORDERCUSTOMER')">
					<div class="mobile-search form-group top-autocomplete">
						<form class="navbar-form" name="search_form_mobile"  method="get" action="${searchUrl}">
							<div class="input-group add-on ui-front">
						<input id="input_mobile" class="siteSearchInput form-control left" type="text" name="text" value="" 
							placeholder="Search Products" data-options='{"autocompleteUrl" : "${autocompleteUrl}", "minCharactersBeforeRequest" : "", "waitTimeBeforeRequest" : 100,
                                                                        							"displayProductImages" : true, "appendTo" : "mobile-autocomplete"}'/>
								<div class="input-group-btn">
									<button class="btn btn-default" type="submit">
									  <svg class="icon-search"><use xlink:href="#icon-search"></use></svg>
									</button>
								</div>
							</div>
						</form>
						<div id="mobile-autocomplete"></div>
					</div>
				</sec:authorize>
				<ul class="mobile-head-list nav navbar-nav">
					<c:choose>
						<c:when test="${empty hideHeaderLinks}">
							<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
								<li class="mobile-drop">
									<div class="select-list">
										<div data-value="" class="select-btn header">
											<svg class="icon-profile01">
												<use xlink:href="#icon-profile01"></use>
											</svg>
											<label>${user.firstName} - ${user.currentB2BUnit.name }</label>
											<span><fmt:parseNumber value="${user.currentB2BUnit.uid}"/></span>
										</div>
										<ul class="select-items header">
											<c:forEach items="${user.branches}" var="group">
												<c:if test="${group.name ne user.currentB2BUnit.name}">
												<li data-value="${user.firstName} - ${group.name}" data-id="${group.uid}"><a href="<c:url value="/header/b2bunit/${group.uid}"/>">${group.name }</a></li>
												</c:if>
											</c:forEach>
										</ul>
									</div>
								</li>
								<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP,ROLE_B2BORDERCUSTOMER')">
								<li>
								<cms:component uid="RecommendationsHeaderComponent"/>
								</li>
								<cms:component uid="DealHeaderComponent"/>
								
                                <li>
                                    <div class="mobile-calendar-container">
										<cms:pageSlot position="DeliveryDatepickerBar" var="datepicker" limit="1">
											<cms:component component="${datepicker}" element="div" class="delivery-header delivery-header-mobile" />
										</cms:pageSlot>
                                    </div>
                                </li>
								</sec:authorize>
							</sec:authorize>
						</c:when>
						<c:otherwise>
							<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP, ROLE_B2BORDERCUSTOMER')">
								<li>
                                    <div class="mobile-calendar-container">
										<cms:pageSlot position="DeliveryDatepickerBar" var="datepicker" limit="1">
											<cms:component component="${datepicker}" element="div" class="delivery-header delivery-header-mobile" />
										</cms:pageSlot>
                                    </div>
								</li>
							</sec:authorize>
						</c:otherwise>
					</c:choose>
					<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')">
						<li class="hidden-sm hidden-md hidden-lg log-status"><a href="<c:url value='/contactus'/>"><spring:theme code="text.homepage.header.contactus"/></a></li>
					</sec:authorize>
				</ul>
			</div>
			<nav:topNavigation/>
		</div>
	</div>
</nav>
<nav:breadcrumbs/>
