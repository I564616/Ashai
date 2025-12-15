<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="recommendation" tagdir="/WEB-INF/tags/desktop/recommendation"%>


<script id="dealsData" type="text/json">${ycommerce:generateJson(deals)}</script>
<%-- <deals:dummyData/> --%>
<div class="recommendations-header">
    <spring:theme code="text.recommendations.header"/>
</div>
<div class="rep-review-recommendation">
    <c:choose>
        <c:when test="${not empty recommendationData}">
        <div class="recommendations-subHeader">
            <spring:theme code="text.recommendations.review"/>
        </div>
        <div class="hidden-xs">
            <spring:theme code="text.recommendations.review.detail"/>
        </div>
        <div>
            <div id="globalMessages">
                <common:globalMessages />
            </div>
            <div id="errorSavingRecommendations">
                    <div class="errorSavingRecommendations alert negative" style="display:none"><spring:theme code="text.recommendations.update.error" /></div>
            </div>
            <div id="successSavingRecommendations">
                    <div class="successSavingRecommendations recommendation-message"><spring:theme code="text.recommendations.update.success"/></div>
            </div>
        </div>
        <br>
        <input type="hidden" id="recommendationIdsToDelete" />

        <div class="table-items" ng-controller="dealsCtrl"  ng-init="init()" ng-cloak>


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
                    <deals:recommendationDealRow isCustomerView="false"/>

                    <c:set var="recommendationCounter" value="${fn:length(deals)}"/>

                    <c:forEach items="${productRecommendation}" var="recommendation" varStatus="status">
                      <div class="product-row">
                        <input type="hidden" class="recommendationId" value="${recommendation.recommendationId}">
                        <input type="hidden" class="recommendationType" value="${recommendation.recommendationType}">

                        <recommendation:recommendProduct recommendProduct="${recommendation.product}" productListPosition="${recommendationCounter + status.count}" recommendedBy="${recommendation.recommendedBy}" isInPackType="${recommendation.isInDeliveryPackType}"/>
                      </div>
                    </c:forEach>
                  </div>
                </div>
            </div>
        </div>
        <br>
        <div class="">
            <div class="col-xs-12 col-md-2 col-md-offset-6 col-sm-3 col-sm-offset-4 trim-right">
                <div class="cancelRecommendationChange">
                    <a href="/sabmStore/en/recommendation" class="inline" ><spring:theme code="text.recommendations.cancel.changes"/></a>
                </div>
            </div>
            <div class="col-xs-12 col-md-4 col-sm-5 trim-both">
                <div class="updateRecommendation">
                    <button id="update-recommendation-button" class="btn btn-primary bde-view-only disabled"><spring:theme code="text.recommendations.update"/></button>
                </div>
            </div>
        </div>
    </c:when>
        <c:otherwise>
            <div class="no-rec-message" style="display:none;">
                <div class="recommendations-subHeader">
                    <spring:theme code="text.recommendations.review"/>
                </div>
                <div class="hidden-xs">
                    <spring:theme code="text.recommendations.review.detail"/>
                </div>
                <h3 class="margin-top-10">
                    <spring:theme code="text.recommendations.zero.recommendations"/>
                </h3>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<recommendation:popupUnsavedChanges />