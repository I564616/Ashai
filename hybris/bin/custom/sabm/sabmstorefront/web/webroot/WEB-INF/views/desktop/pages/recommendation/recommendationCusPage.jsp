<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="recommendation" tagdir="/WEB-INF/tags/desktop/recommendation"%>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:url value="/Beer/c/10" var="continueShoppingUrl" scope="session"/>
<script id="dealsData" type="text/json">${ycommerce:generateJson(deals)}</script>

<%--<deals:dummyData/>--%>

<!-- <div class="recommendations-header">
    <spring:theme code="text.recommendations.header"/>
</div> -->
<c:choose>
    <c:when test="${not empty recommendationData}">
        <div class="recommendations-subHeader">
            <spring:theme code="text.recommendations.recommended"/>
        </div>
        <!-- <div class="hidden-xs">
            <spring:theme code="text.recommendations.recommended.detail"/>
        </div> -->
        <br>
        <div class="table-items"  ng-controller="dealsCtrl"  ng-init="init()" ng-cloak>
            <div class="custom-table">
                <div class="table-headers visible-md-block visible-lg-block visible-sm-block">
                    <div class="col-md-7 col-sm-6 col-xs-7">
                        <div class="row">
                            <div class="col-md-3 col-sm-3 col-sm-3"><spring:theme code="text.recommendations.table.header.item" /></div>
                        </div>
                    </div>
                    <div class="col-md-5 col-sm-6 col-xs-5">
                        <div class="inline header-qty"><span><spring:theme code="text.recommendations.table.header.quantity"/></span></div>
                        <div class="inline header-uom"><span><spring:theme code="text.recommendations.table.header.uom"/></span></div>
                    </div>
                </div>
                
                <div class="table-body">
                    <div class="deal-items">
                      <deals:recommendationDealRow isCustomerView="true"/>
                    
                      <c:set var="recommendationCounter" value="${fn:length(deals)}"/>
                      
                      <c:forEach items="${productRecommendation}" var="recommendation" varStatus="status">


                            <div class="product-row">
                              <input type="hidden" class="recommendationId" value="${recommendation.recommendationId}"/>
                              <input type="hidden" class="recommendationType" value="${recommendation.recommendationType}"/>

                              <recommendation:recommendProduct recommendProduct="${recommendation.product}" productListPosition="${recommendationCounter + status.count}"
                              recommendedBy="${recommendation.recommendedBy}" isInPackType="${recommendation.isInDeliveryPackType}"/>
                            </div>
                      </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="no-rec-message" style="display:none;">
            <div class="text-center">
            <div class="recommendations-subHeader">
                <spring:theme code="text.recommendations.no.recommendations"/>
            </div>
            <div>
                <a href="${continueShoppingUrl}">
                <svg class="icon-arrow-left">
                    <use xlink:href="#icon-arrow-left"></use>
                </svg>
                <spring:theme code="general.continue.shopping"/></a>
            </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
