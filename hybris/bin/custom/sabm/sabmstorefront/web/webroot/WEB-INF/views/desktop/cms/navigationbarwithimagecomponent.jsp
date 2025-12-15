<%@ page trimDirectiveWhitespaces="true" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set value="${component.styleClass} ${dropDownLayout}" var="bannerClasses"/>
<c:url value="${component.link.url}" var="viewAllCategoryUrl" scope="request"/>

<input type="hidden" name="playAnimationHidden" id="playAnimationHidden" value="${playAnimation}" />
<c:if test="${(component.link.linkName eq 'Deals' && !isNAPGroup) || (component.link.linkName ne 'Deals') }">
<li class="<c:if test="${component.link.linkName eq 'Support'}">support</c:if> <c:if test="${component.link.linkName eq 'Deals'}">deals</c:if> La dropdown megamenu-fw ${bannerClasses} <c:if test="${not empty component.navigationNode.children}"> parent</c:if>">
<c:choose>
<c:when test="${component.link.linkName eq 'Support'}">
	<a href="<c:url value="${component.link.url}"/>" class="dropdown-link" data-toggle="dropdown"><span class="icon-mask">
		<svg class="small-header-icon">
			<use xlink:href="#icon-daybreak-support"></use>
 		
 </svg>
  <label> ${component.link.linkName}</label>
 </span>
	</c:when>
	<c:when test="${component.link.linkName eq 'Deals'}">
	<a href="<c:url value="${component.link.url}"/>"><span class="icon-mask">
		<svg class="small-header-icon">
            <use xlink:href="#icon-deals"></use>
        </svg>
         <label> ${component.link.linkName}</label>
		</span>
	</c:when>
	<c:otherwise>
		<a href="<c:url value="${component.link.url}"/>" class="dropdown-link" data-toggle="dropdown">${component.link.linkName}
	
	</c:otherwise>
	</c:choose>			
	<c:if test="${component.link.linkName eq 'Deals' and !isNAPGroup and showCircle}">
			<div class="d-content">
				<div class="d-content-trans <c:if test='${playAnimation eq false}'>no_transform</c:if>" id="d_circle">
					<span>${dealQuantity}</span>
			    </div>
			</div>
		</c:if>
	</a>

	<c:if test="${not empty component.navigationNode.children}">
		<ul class="dropdown-menu">
			<li>
				<div class="megamenu-content container">
					<div class="row">
					<div class="hidden-sm hidden-md hidden-lg">
						<div class="col-sm-2">
							<ul class="dropdown-menu-list">
								<li>
									<a href="${viewAllCategoryUrl}">
										<spring:theme code="text.navigationbar.link.viewAll" />&nbsp;${component.link.linkName}
									</a>
								</li>
							</ul>
						</div>
					</div>
					<c:forEach items="${component.navigationNode.children}" var="child">
						<c:if test='${fn:length(child.links)>0}'>
							<div class="col-sm-2">
								<ul class="dropdown-menu-list">
									<li class="collapser">
										<div class="collapser-header">${child.title}</div>
										<div class="collapser-content">
											<ul class="dropdown-menu-sublist">
												<c:forEach items="${child.links}" step="${component.wrapAfter}" varStatus="i">
													<c:forEach items="${child.links}" var="childlink" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
														<cms:component component="${childlink}" evaluateRestriction="true" element="li" class="Lc ${i.count < 2 ? 'left_col' : 'right_col'}"/>
													</c:forEach>
												</c:forEach>														
											</ul>
										</div>
									</li>
								</ul>
							</div>
						</c:if>
						</c:forEach>
						<%-- <div class="col-sm-2 hidden-xs">
							<ul class="dropdown-menu-list"><li><img src="${component.image.url }" alt="" class="img-responsive"></li></ul>
						</div> --%>
					</div>
					<div class="row hidden-xs">
						<div class="col-xs-12">
							<ul class="dropdown-menu-list">
								<li>
									<a class="link-cta view-all" href="${viewAllCategoryUrl}">
										<spring:theme code="text.navigationbar.link.viewAll" />&nbsp;${component.link.linkName}
									</a>
								</li>
							</ul>
						</div>
					</div>
				</div>
			</li>
		</ul>
	</c:if>
</li>
</c:if>