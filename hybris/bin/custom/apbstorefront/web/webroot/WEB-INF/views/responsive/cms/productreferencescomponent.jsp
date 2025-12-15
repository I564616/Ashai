<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose>
	<c:when test="${not empty productReferences and component.maximumNumberProducts > 0}">
		<div class="carousel-component" id="product-suggestions-component">
			<div class="headline" id="product-suggestions-headline">${fn:escapeXml(component.title)}</div>
            <hr class="PDPhrTag">
            <div class="col-xs-10 col-xs-push-1 col-sm-11 col-sm-push-0 col-md-12">
				<div class="carousel js-owl-carousel js-owl-lazy-reference js-owl-carousel-reference">
					<c:forEach end="${component.maximumNumberProducts}"
						items="${productReferences}" var="productReference">
						<c:url value="${productReference.target.url}" var="productUrl" />

						<div class="item">
							<a href="${productUrl}" class="js-reference-item"
								data-quickview-title="<spring:theme code="popup.quick.view.select"/></span>">
								<div class="thumb">
									<product:productPrimaryReferenceImage
										product="${productReference.target}" format="product" />
								</div> <span class="brandContainer"><b>${fn:escapeXml(productReference.target.apbBrand.name)}</b></span>

								<c:if test="${component.displayProductTitles}">
									<span class="item-name">${fn:escapeXml(productReference.target.name)}</span>
									<div class="item-name">${fn:escapeXml(productReference.target.unitVolume.name)}</div>
									<div class="item-name">${fn:escapeXml(productReference.target.packageSize.name)}</div>
								</c:if> <c:if test="${component.displayProductPrices}">
									<div class="priceContainer">
										<format:fromPrice priceData="${productReference.target.price}" />
									</div>
								</c:if>
							</a>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
	</c:when>

	<c:otherwise>
		<component:emptyComponent />
	</c:otherwise>
</c:choose>