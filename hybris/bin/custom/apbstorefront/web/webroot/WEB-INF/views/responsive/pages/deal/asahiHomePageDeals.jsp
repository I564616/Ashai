<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>


<div class="carousel-component" id="homepage-deals-component">
	<div class="headline" id="product-suggestions-headline"><b><spring:theme code="sga.homepage.deals.title"/></b></div>
          <hr class="PDPhrTag">
          <div class="col-xs-10 col-xs-push-1 col-sm-11 col-sm-push-0 col-md-12">
		<div class="carousel js-owl-carousel js-owl-lazy-reference js-owl-carousel-reference">
			<c:forEach items="${asahiDeals}" var="deal">
				<c:url value="${request.contextPath}${deal.conditionProduct.url}" var="dealUrl" />

				<div class="item">
					<a href="${dealUrl}" class="js-reference-item" data-quickview-title="<spring:theme code="popup.quick.view.select"/></span>">
						<div class="thumb">
							<c:choose>
                                <c:when test="${fn:length(deal.conditionProduct.images) == 0}">
                                    <theme:image code="img.missingProductImage.responsive.product" title="${deal.title}" alt="${deal.title}" />
                                </c:when>
                                <c:otherwise>
                                    <%--<c:forEach items="${deal.conditionProduct.images}" var="img">--%>
                                        <%--<c:if test="${img.format == 'product'}" >--%>
                                        <c:set var="img" value="${deal.conditionProduct.images[0]}"/>
                                            <img class="primaryImage" id="primaryImage" src="${img.url}" title="${deal.title}" alt="${deal.title}"/>
                                        <%--</c:if>--%>
                                    <%--</c:forEach>--%>
                                </c:otherwise>
                            </c:choose>
						</div> 
						<div class="deal-title">${deal.title}</div>
						
						<div class="view-deal">
                            <a href="${dealUrl}">
							    <button type="" class="btn btn-primary btn-template-block">VIEW DEAL</button>
                            </a>
						</div>
					</a>
				</div>

				
			</c:forEach>
		</div>
	</div>
</div>