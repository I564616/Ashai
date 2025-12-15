<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="com.sabmiller.core.enums.SmartRecommendationType"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<c:choose>
    <c:when test="${show && !isNAPGroup}">
       <c:choose>
            <c:when test="${recommendationEnabled}">

                <c:choose>
                    <c:when test="${smartRecommendations ne null}" >

                        <product:productPricePopup />
                        <section class="row product-picks recommendation-component" data-smart-recommendation-group="${smartRecommendationGroup}"
                            data-recommendation-component-slot="${componentContentSlot}" data-recommendation-component-position="${componentPosition}">
                            <div class="text-center">
                                <h2 class="recommendation-component-title">
                                    <!-- <spring:theme code="${component.title}" /> -->
                                     <p><span><spring:theme code="text.new.recommendations.header.title"/></span> <spring:theme code="text.new.recommendations.header.subtitle"/></p>
                                     <p><spring:theme code="text.new.recommendations.header.subtext"/></p>
                                </h2>
                            </div>

                            <div id="newRecommendations" class="product-picks-items product-picks-div centered">
                                <c:if test="${smartRecommendations[SmartRecommendationType.MODEL2].isPresent()}">
                                    <!-- <div class="col-xs-10 addtocart-qty productImpressionTag recommendation-highlight" data-smart-recommendation-model="M2"> -->
                                        <!-- <span class="recommendinfo" title="Customers who place orders similar to yours often add this product.">i</span> -->
                                        <home:smartRecommendationCard title="${component.model2Title}" product="${smartRecommendations[SmartRecommendationType.MODEL2].get()}" smartRecommendationModel="MODEL2" count="0" />
                                    <!-- </div> -->
                                </c:if>
                                <c:if test="${smartRecommendations[SmartRecommendationType.MODEL3].isPresent()}">
                                    <!-- <div class="col-xs-10 addtocart-qty productImpressionTag recommendation-highlight" data-smart-recommendation-model="M3"> -->
                                        <!-- <span class="recommendinfo" title="This product is popular with customers like you based on location, venue and history.">i</span> -->
                                        <home:smartRecommendationCard title="${component.model3Title}" product="${smartRecommendations[SmartRecommendationType.MODEL3].get()}" smartRecommendationModel="MODEL3" count="1" />
                                    <!-- </div> -->
                                </c:if>
                                <c:if test="${smartRecommendations[SmartRecommendationType.MODEL1].isPresent()}">
                                    <!-- <div class="col-xs-10 addtocart-qty productImpressionTag recommendation-highlight" data-smart-recommendation-model="M1"> -->
                                        <!-- <span class="recommendinfo" title="This is our top pick at the moment!">i</span> -->
                                        <home:smartRecommendationCard title="${component.model1Title}" product="${smartRecommendations[SmartRecommendationType.MODEL1].get()}" smartRecommendationModel="MODEL1" count="2" />
                                    <!-- </div> -->
                                </c:if>
                            </div>
                            <div class="clearfix"></div>

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

                        </section>
                    </c:when>
                    <c:otherwise>
                        <!--This dude is not group A but still nothing to recommend...-->

                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <!-- This dude is group A, Poor dude...-->
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <!-- Just to let you know, something went wrong. Jeez.. go check it.. -->
    </c:otherwise>
</c:choose>
