<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<%@ attribute name="isCustomerView" required="false" type="java.lang.Boolean"%>

<c:set var="tempImgPath" value="/_ui/desktop/SABMiller/img/placeholders/" />
<c:url value="1" var="firstBundle"/>
<c:set value="0" var="dealConditionCount"/>
<c:set value="" var="dealConditionProductCode"/>
<input type="hidden" class="dealCurrentUrl" value="<c:url value="/cart/add/deal"/>">
<input type="hidden" class="recommendationCurrentUrl" value="<c:url value="/recommendation/add/deal"/>">


<div class="deal product-row" ng-repeat="deal in deals" ng-init="dealInit(deal,false)" ng-if="deal.ranges[0].baseProducts[0].cubStockStatus.code != 'outOfStock'">
  <input type="hidden" class="recommendationId" value="{{deal.recommendationId}}"/>
  <input type="hidden" class="recommendationType" value="{{deal.recommendationType}}"/>
  <input type="hidden" name="dealCodePost" value="{{deal.code}}" />
  
  <deals:dealHead isCustomerView="${!bdeUser}"/>

  <div ng-if="!deal.single" class="row deal-item-body <c:choose>
	                  <c:when test="${bdeUser}">recommendationRepRow</c:when>
	                  <c:otherwise>recommendationCusRow</c:otherwise>
	                </c:choose>" ng-hide="deal.dealToggle" ng-init="deal.totalQty = calcTotalQty(deal)">
    <div class="col-xs-12 multi-products <c:if test="${!isInPackType}"> disabled-productPackTypeNotAllowed</c:if>">
      <!-- Ranges -->
      <deals:dealRanges />

      <!-- Free Product -->
      <deals:dealFree/>

      <!-- Selected Products -->
      <deals:dealSelectable/>

    </div>
    <div class="col-xs-12">
      <div class="deal-cta">
       <div class="<c:if test="${!isInPackType}"> disabled-productPackTypeNotAllowed</c:if>">
        <div class="deal-remaining" ng-show="!pageInactive">
            <div ng-hide="deal.remainingQty == null">
              <span ng-show="deal.remainingQty > 0">You can order <strong ng-init="remaining">{{deal.remainingQty}} <span class="text-lowercase" ng-show="deal.remainingQty == 1">{{deal.ranges[0].baseProducts[0].uomS}}</span><span class="text-lowercase" ng-show="deal.remainingQty == 0 || deal.remainingQty > 1">{{deal.ranges[0].baseProducts[0].uomP}}</span> before {{deal.validTo | date:'dd-MM-yyyy'}}</strong></span>
              <span ng-show="deal.remainingQty <= 0">You have reached your limit for this deal</span>
            </div>
            <div ng-hide="deal.remainingValue == null">
              <span ng-show="deal.remainingValue > 0">You have <strong>$<span class="remaining">{{deal.remainingValue}}</span> left to claim before {{deal.validTo | date:'dd-MM-yyyy'}}</strong></span>
              <span ng-show="deal.remainingValue <= 0">You have reached your limit for this deal</span>
            </div>
            <c:if test="${isCustomerView eq true}">
                <div ng-repeat="range in deal.ranges" ng-show="range.totalQty < range.minQty && deal.ranges.length < 2">You have <span class="bold">{{range.minQty - range.totalQty}}</span> more <span class="text-lowercase" ng-show="(range.minQty - range.totalQty) == 1">{{range.baseProducts[0].uomS}}</span><span class="text-lowercase" ng-show="(range.minQty - range.totalQty) == 0 || (range.minQty - range.totalQty) > 1">{{range.baseProducts[0].uomP}}</span> to add before proceeding</div>
            </c:if>
        </div>
        <div class="deal-remaining" ng-show="pageInactive">
            <div class="grey">Change your delivery date in the header to purchase these deals</div>
        </div>
        </div>

        <c:if test="${isCustomerView eq true}">
          <div class="deal-atc visible-xs margin-top-10">

          	<button ng-if="deal.isInDeliveryPackType" class="btn btn-primary js-track-deals-addtocart" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0 || bdeViewOnly" ng-click="addToCart(deal, 'deals')"><spring:theme code="text.deals.list.view.add.to.cart" /></button>
            <div ng-if="!deal.isInDeliveryPackType" class="btn btn-primary btn-invert btn-sm btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>

            <div class="delete"><span class="inline deleteRecommendation"><spring:theme code="text.iconCartRemove"/></span></div>
          </div>
        </c:if>
      </div>
    </div>
  </div>

  <hr class="hr-1">
</div>




