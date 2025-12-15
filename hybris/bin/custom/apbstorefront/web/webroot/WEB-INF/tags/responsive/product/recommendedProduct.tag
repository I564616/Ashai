<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<div class="carousel-component" id="product-suggestions-component">
	<div class="headline" id="product-suggestions-headline"><b><spring:theme code="sga.product.recommended.headline"/></b></div>
          <hr class="PDPhrTag">
          <div class="col-xs-10 col-xs-push-1 col-sm-11 col-sm-push-0 col-md-12">
		<div class="carousel js-owl-carousel js-owl-lazy-reference js-owl-carousel-reference">
			<c:forEach items="${productRecommendation}" var="reference">
				<c:url value="${reference.url}" var="productUrl" />

				<div class="item">
					<a href="${productUrl}" class="js-reference-item" data-quickview-title="<spring:theme code="popup.quick.view.select"/></span>">
						<div class="thumb">
							<product:productPrimaryReferenceImage product="${reference}" format="product" />
						</div> 
						<span class="brandContainer"><b>${fn:escapeXml(reference.apbBrand.name)}</b></span>
						<span class="item-name">${fn:escapeXml(reference.name)}</span>
						<div class="item-name">${fn:escapeXml(reference.unitVolume.name)}</div>
						<div class="item-name">${fn:escapeXml(reference.packageSize.name)}</div>
						
						<div>
						<c:if test="${!isNAPGroup}">
							<c:choose>
								  <c:when test="${not empty reference.price}">
								    <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" > 
	                                	<ycommerce:testId
	                                        code="productDetails_productNamePrice_label_${reference.code}">
	                                        <product:productPricePanel product="${reference}" />
	                                        
	                                 	</ycommerce:testId>
                            </sec:authorize> 
								 </c:when>
								 <c:otherwise>
								 	<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" > 
	                                	<ycommerce:testId
	                                        code="productDetails_productNamePrice_label_${reference.code}">
	                                        
															<p class="price">
																${textForNoPrice}
															</p>
	                                 </ycommerce:testId>
                           </sec:authorize> 
								 </c:otherwise>
							</c:choose>	
							</c:if>
						</div>
					</a>
				</div>
			</c:forEach>
		</div>
	</div>
</div>
