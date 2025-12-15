<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb"
	tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:if test="${isNAPGroup}">
    <style>
        .navbar-form .product-pick-selectors {
            display: none;
        }
    </style>
</c:if>

<c:url value="/search/autocomplete/${searchBox.uid}" var="autocompleteUrl"/>
<c:url value="/search/" var="searchUrl"/>

<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
<c:if test="${component.visible}">
	<div class="main-nav">
		<div class="container">
			<ul class="nav navbar-nav">
				<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP,ROLE_B2BORDERCUSTOMER')">
					<c:forEach items="${components}" var="component">
						<c:if test="${component.navigationNode.visible}">
							
					<cms:component component="${component}" evaluateRestriction="true" />
							
						</c:if>
					</c:forEach>
					<!--<li class="La dropdown megamenu-fw">
						<a href="#" class="dropdown-link" data-toggle="dropdown">Deals
							<div class="d-content">
								<div class="d-content-trans" id="d_circle">
									<span>10</span>
							    </div>
							</div>
						</a></li>-->
					
					<li class="hidden-sm hidden-md hidden-lg log-status">
						<a href="<c:url value='/your-business'/>"><spring:theme code="text.home.header.business"/></a>
					</li>
					<li class="hidden-sm hidden-md hidden-lg log-status">
						<a href="<c:url value='/your-business/billing'/>"><spring:theme code="text.home.header.business.account"/></a>
					</li>
					<li class="hidden-sm hidden-md hidden-lg log-status">
                        <a href="<c:url value='/your-notifications'/>"><spring:theme code="text.homepage.header.dropdown.notifications"/></a>
                    </li>
				</sec:authorize>
				<li class="hidden-sm hidden-md hidden-lg log-status">
					<a class="bde-view-only" href="<c:url value='/businessEnquiry'/>"><spring:theme code="text.homepage.header.dropdown.businessenquiry"/></a>
				</li>
				<li class="hidden-sm hidden-md hidden-lg log-status ${isNAPGroup? 'hide' : ''}">
					<a href="<c:url value='/autopay/landing'/>"><spring:theme code="text.homepage.header.dropdown.autopay"/></a>
				</li>
				<li class="hidden-sm hidden-md hidden-lg log-status logout-link">
					<a href="<c:url value='/logout'/>"><ycommerce:testId code="header_signOut"><spring:theme code="header.link.logout"/></ycommerce:testId></a>
				</li>
				
				
				<!-- Dev need update here for the impersonation -->
				
				<li class="hidden-sm hidden-md hidden-lg log-status inpersonate-link">
					<a href="<c:url value='/impersonate/change'/>"><ycommerce:testId code="header_signOut"><spring:theme code="header.link.impersonation"/></ycommerce:testId></a>
				</li>
				
				
			    <sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP,ROLE_B2BORDERCUSTOMER')">
					<li class="pull-right hidden-xs top-autocomplete">
						<form class="navbar-form" name="search_form_${searchBox.uid}"  method="get" action="${searchUrl}">
							<div class="input-group add-on ui-front">
						<input 
						  id="input_${searchBox.uid}" 						
							class="siteSearchInput form-control left" 
							type="text" 
							name="text"
							value="" 
							placeholder="Search Products"
							data-options='{"autocompleteUrl" : "${autocompleteUrl}",
                                          							"minCharactersBeforeRequest" : "",
                                          							"waitTimeBeforeRequest" : "${searchBox.waitTimeBeforeRequest}",
                                          							"displayProductImages" : ${searchBox.displayProductImages},
                                          							"appendTo" : "desktop-autocomplete"}'/>
								<div class="input-group-btn">
									<button class="btn btn-default" type="submit">
										<svg class="icon-search">
										    <use xlink:href="#icon-search"></use>    
										</svg>
									</button>
								</div>
							</div>
						</form>
						<div id="desktop-autocomplete"></div>
					</li>
				</sec:authorize>

			</ul>
		</div>
	</div>
</c:if>
</sec:authorize>

