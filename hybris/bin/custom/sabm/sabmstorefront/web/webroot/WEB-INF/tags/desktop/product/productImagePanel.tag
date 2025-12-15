<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="galleryImages" required="true" type="java.util.List"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>


<c:set var="requestOrigin" value="Home/ProductPage" />
<div class="col-sm-6 col-md-4 product-images video-carousel">
	<div class="slick-hero" id="primary_image">
		<c:forEach items="${sortedGallery}" var="container" varStatus="varStatus">
		<c:choose>
		<c:when test="${container.zoom.format == 'video'}">		
            <div>
                <a class="slick-video-hero popup-video" href="#">
                    <svg class="icon-play-button">
                        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-play-button"></use>
                    </svg>
                    <img src="/_ui/desktop/SABMiller/img/spinner.gif" alt="${container.zoom.altText}" title="${container.zoom.altText}" />
                </a>
                <div class="slick-video-mobile"></div>
            </div>
		</c:when>
		<c:otherwise>
			<div>
			
				<a href="${container.superZoom.url}" class="js-track-product-link"
					data-currencycode="${product.price.currencyIso}"
                    data-name="${fn:escapeXml(product.name)}"
                    data-id="${product.code}"
                    data-price="${product.price.value}"
                    data-brand="${fn:escapeXml(product.brand)}"
                    data-category="${fn:escapeXml(product.categories[0].name)}"
                    data-variant=<c:choose>
								<c:when test="${empty product.uomList}">
					 				"${product.unit}"
					 			</c:when>
					 			<c:otherwise>
					 				"${product.uomList[0].name}"
					 			</c:otherwise>
						   	  </c:choose>
                    data-position="1"
                    data-url="${product.url}"
                    data-actionfield="${fn:escapeXml(requestOrigin)}"
                    data-list="${fn:escapeXml(requestOrigin)}"
                    data-dealsflag="${product.dealsFlag}" >
                    <img src="${container.zoom.url}" alt="${container.zoom.altText}" title="${container.zoom.altText}" />
					<c:if test="${product.newProductFlag eq true}">
						<div class="badge badge-med badge-green badge-postion"><spring:theme code="text.product.title.new"/></div>
					</c:if>
					<c:if test="${product.dealsFlag eq true}">
                    	<div class="badge badge-med badge-red badge-postion"><spring:theme code="text.product.title.deal"/></div>
                    </c:if>
				</a>
			</div>
		</c:otherwise>
	</c:choose>			
	</c:forEach>
        
	</div>
	<div class="slider-nav-wrap pull-right">
		<ul class="slider-nav">
			<li class="slider-prev">
				<svg class="icon-arrow-left">
				    <use xlink:href="#icon-arrow-left"></use>
				</svg>
			</li>
			<li class="slider-next">
				<svg class="icon-arrow-right">
				    <use xlink:href="#icon-arrow-right"></use>
				</svg>
			</li>
		</ul>
	</div>
	<div class="slick-slider-thumbs clearfix">
		<c:forEach items="${sortedGallery}" var="container" varStatus="varStatus">
		<c:choose>
		<c:when test="${container.zoom.format == 'video'}">	
		<div class="slick-video-thumb">
                <div class="slick-img-wrap" data-url="${container.zoom.url}">
                    <a href="#"><img src="/_ui/desktop/SABMiller/img/spinner.gif" alt="${container.zoom.altText}" title="${container.zoom.altText}" /></a>
                </div>
            </div>				
			</c:when>
		<c:otherwise>
		<div>
				<div class="slick-img-wrap">
					<img src="${container.thumbnail.url}" alt="${container.thumbnail.altText}" title="${container.thumbnail.altText}" />
				</div>
			</div>
		</c:otherwise>
	</c:choose>			
            
        </c:forEach>
	</div>

</div>
