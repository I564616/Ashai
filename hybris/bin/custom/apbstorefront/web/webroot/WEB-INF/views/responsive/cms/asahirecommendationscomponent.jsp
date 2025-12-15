<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="com.sabmiller.core.enums.SmartRecommendationType"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/responsive/home" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<c:choose>
    <c:when test="${show}">
       <c:choose>
            <c:when test="${recommendationEnabled}">

                <c:choose>
                    <c:when test="${smartRecommendations ne null}" >

                        <section class="product-picks recommendation-component" data-recommendation-component-slot="${componentContentSlot}" data-recommendation-component-position="${componentPosition}">
                            <div class="col-xs-12 clearfix recommendation-component-heading"></div>

                            <div class="col-xs-12 col-lg-11 recommendations-section centered text-center">
                                <c:if test="${smartRecommendations[SmartRecommendationType.MODEL1] ne null}">
                                    <div class="recommendations-section-1 product-item" data-smart-recommendation-model="M1">
                                        <home:homePick title="${component.model1Title}" product="${smartRecommendations[SmartRecommendationType.MODEL1]}" smartRecommendationModel="MODEL1" count="0" />
                                    </div>
                                </c:if>
                            
                                <c:if test="${smartRecommendations[SmartRecommendationType.MODEL2] ne null}">
                                    <div class="recommendations-section-2 product-item" data-smart-recommendation-model="M2">
                                        <home:homePick title="${component.model2Title}" product="${smartRecommendations[SmartRecommendationType.MODEL2]}" smartRecommendationModel="MODEL2" count="1" />
                                    </div>
                                </c:if>

                                <c:if test="${smartRecommendations[SmartRecommendationType.MODEL3] ne null}">
                                    <div class="recommendations-section-3 product-item blue" data-smart-recommendation-model="M3">
                                        <home:homePick title="${component.model3Title}" product="${smartRecommendations[SmartRecommendationType.MODEL3]}" smartRecommendationModel="MODEL3" count="2" />
                                    </div>
                                </c:if>
                            </div>
                            <div class="clearfix"></div>
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
