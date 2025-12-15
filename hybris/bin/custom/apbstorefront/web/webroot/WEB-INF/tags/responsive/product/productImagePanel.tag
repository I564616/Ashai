<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="galleryImages" required="true" type="java.util.List"%>
<%@ attribute name="productUnavailable" required="false" type="java.lang.String"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>

<c:set value="" var="pb25"/>
<c:if test="${cmsSite.uid eq 'sga'}">
    <c:set value="pb-25" var="pb25"/>
</c:if>

<div class="image-gallery js-gallery">

	<c:choose>
		<c:when test="${galleryImages == null || galleryImages.size() == 0}">
			<div class="carousel image-gallery__image js-gallery-image">
				<div class="item">
					<div class="${pb25}">
						<spring:theme code="img.missingProductImage.responsive.product"
							text="/" var="imagePath" />
						<c:choose>
							<c:when test="${originalContextPath ne null}">
								<c:url value="${imagePath}" var="imageUrl"
									context="${originalContextPath}" />
							</c:when>
							<c:otherwise>
								<c:url value="${imagePath}" var="imageUrl" />
							</c:otherwise>
						</c:choose>
						<c:if test="${cmsSite.uid eq 'sga'}">
                            <c:if test="${product.isPromotionActive}">
                                <div id="pdp-promotion-img" class="plp-promotion-img"><spring:theme code="sga.product.promotion.image.text"/></div>
                            </c:if>
							<c:if test="${fn:length(dealsData) > 0}" >
								<div class="pdp-deals-img">Deal</div>
							</c:if>
                        </c:if>
						<img class="lazyOwl" data-src="${imageUrl}" />

					</div>
				</div>
			</div>
		</c:when>
		<c:otherwise>
		
				<div>
                    <div class="pdp-imageThumbnail-col hidden-xs"  >
					   <product:productGalleryThumbnail galleryImages="${galleryImages}" productUnavailable="${productUnavailable}"/>
					</div>
					<div class="pdp-image-col">
                        <div class="carousel image-gallery__image js-gallery-image">
                            <c:forEach items="${galleryImages}" var="container" varStatus="varStatus">
                                <div id="pdpMainImage_${varStatus.index + 1}" class="item">
                                    <div class="${pb25} position-relative">
                                        <c:if test="${cmsSite.uid eq 'sga'}">
                                            <c:if test="${product.isPromotionActive}">
                                                <div id="pdp-promotion-img" class="plp-promotion-img"><spring:theme code="sga.product.promotion.image.text"/></div>
                                            </c:if>
                                        </c:if>
                                        <div>
                                            <c:if test="${cmsSite.uid eq 'sga'}">
                                                <c:if test="${product.newProduct}">
                                                    <span class="new-product new-product-pdp"><spring:theme code="sga.product.new.identifier"/></span>
                                                </c:if>
                                                <c:if test="${fn:length(dealsData) > 0}" >
                                                    <div class="pdp-deals-img">Deal</div>
                                                </c:if>
                                            </c:if>
                                            <img class="lazyOwl" data-src="${container.product.url}"
                                                alt="${fn:escapeXml(container.thumbnail.altText)}"
                                                data-zoom-image="${container.superZoom.url}">
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div> 
					</div>
					
					<c:choose>
						<c:when test="${cmsSite.uid eq 'sga'}">
<!--							Nothing needed for SGA. -->
						</c:when>
						<c:otherwise>
							<div class="col-sm-4 hidden-sm hidden-md hidden-lg" >
							   <product:productGalleryThumbnail galleryImages="${galleryImages}"  productUnavailable="${productUnavailable}"/>
							</div>
						</c:otherwise>
					</c:choose>
				</div>	
		</c:otherwise>
	</c:choose>
</div>


