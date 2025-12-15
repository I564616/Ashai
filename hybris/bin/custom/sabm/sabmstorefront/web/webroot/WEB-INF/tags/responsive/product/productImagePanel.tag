<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="galleryImages" required="true" type="java.util.List" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>

    <div class="col-sm-6 product-images">
        <div class="slick-hero">
         <c:forEach items="${galleryImages}" var="container" varStatus="varStatus">
                    <div>
                        <a href="#">
                            <img src="${container.product.url}"
                                 data-zoom-image="${container.superZoom.url}"
                                 alt="${container.thumbnail.altText}" >
                        </a>
                    </div>
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
			 <product:productGalleryThumbnail galleryImages="${galleryImages}" />
           
           
           
        </div>

    </div>










