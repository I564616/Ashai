<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<script id="dealsData" type="text/json">${ycommerce:generateJson(deals)}</script>
<%--<deals:dummyData/>--%> 

<c:if test="${!bdeUser && not empty recommendationData && !isNAPGroup}">
<div id="recommendationCarousel">
    <div class= "">
    <hr class="hr-1">
    <section class="related-recommendations margin-top-20">
        <div class="row">
            <div class="col-sm-9 trim-right">
                <div class="col-xs-12 trim-right">
                    <h2><spring:theme code="cart.page.recommendations.title" /></h2>
                </div>
            </div>
            <div class="col-xs-3 col-sm-3 trim-left hidden-xs">
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
        <c:set var="productListName" value="Cart/CUB Recommendations"/>
        <div class="offset-left-small" ng-controller="dealsCtrl"  ng-init="init()" ng-cloak>
            <div id="slider-load" class="slick-slider clearfix">
                <cart:cartRecommendDeal count="${status.count}" productListName="${productListName}"/>
                
              	<c:set var="recommendationCounter" value="${fn:length(deals)}"/>
              	
                <c:forEach items="${productRecommendation}" var="recommendation" varStatus="status">
                    <div class="addtocart-qty cartRecommendations productImpressionTag recommendationsTag addToCartEventTag">
			            <input type="hidden" class="recommendationId" value="${recommendation.recommendationId}"/>
			            <input type="hidden" class="recommendationType" value="${recommendation.recommendationType}"/>
                        <cart:cartRecommendProduct isInPackType="${recommendation.isInDeliveryPackType}"  recommendProduct="${recommendation.product}"  recommendedBy="${recommendation.recommendedBy}" count="${recommendationCounter + status.count}" productListName="${productListName}"/>
                    </div>
                </c:forEach>
            </div>
        </div>
        <div class="col-xs-offset-5 visible-xs">
            <div class="slider-nav-wrap">
                <ul class="slider-nav">
                    <li class="slider-prev offset-bottom-small">
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
    </section>
    <hr class="hr-1">
    <br>
    </div>
  </div>
</c:if>