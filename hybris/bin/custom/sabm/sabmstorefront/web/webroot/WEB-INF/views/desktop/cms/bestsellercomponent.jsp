<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>

<section class="related-products margin-top-40">
    <div class="row offset-bottom-small">
        <div class="col-xs-6"> 
            <h2><spring:theme code="text.homepage.bestsellers" /></h2>
        </div>
        <div class="col-xs-6">
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
        </div>
    </div>
    <c:choose>
		<c:when test="${not empty productData}">
			<div class="row product-pick">
        		<div class="slick-slider clearfix">        			
					<c:forEach items="${productData}" var="product" varStatus="status">
                        <div class="col-xs-12 col-sm-6 col-md-3 addtocart-qty slider-height productImpressionTag bestSellerTag">
                            <home:homePick title="" product="${product }" count="${status.count}" productListName="Home/Best Sellers"/>
                        </div>
                    </c:forEach>
        		</div>
        	</div>
		</c:when>
	</c:choose>
</section>