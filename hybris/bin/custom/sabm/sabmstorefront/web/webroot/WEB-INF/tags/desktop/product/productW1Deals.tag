<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>

<input type="hidden" class="dealCurrentUrl" value="<c:url value="/cart/add/deal"/>">
<input type="hidden" class="recommendationCurrentUrl" value="<c:url value="/recommendation/add/deal"/>">

<div class="pdp-section" ng-controller="dealsCtrl" ng-init="init()" ng-show="deals.length != 0" ng-cloak>
	<div class="row">
		<div class="col-xs-12">
			<div class="row">
				<div class="col-xs-12 deal-items">
					<div class="deals-header offset-bottom-small text-right visible-md-block visible-lg-block" ng-init="filter.date = headerDate">
						<span class="inline toggle-all" ng-init="collapsedAll = true" ng-click="collapsedAllFunc()"><span ng-hide="collapsedAll">Hide</span><span ng-hide="!collapsedAll">Show</span> all details</span>
					</div>
                    <span class="hidden">
                      {{ disableProductPackType = !deal.isInDeliveryPackType ? 'disabled-productPackTypeNotAllowed' : '';}}
                    </span>
					<div class="deal" ng-repeat="deal in deals | limitTo: togglePDPLimit.limit" ng-init="dealInit(deal,true)">

           <deals:dealHead isCustomerView="${!bdeUser}" destinationPage="PDP"/>

            <div ng-if="!deal.single" class="row deal-item-body" ng-hide="deal.dealToggle" ng-init="deal.totalQty = calcTotalQty(deal)">
              <div class="col-xs-12 multi-products">
                <!-- Ranges -->
                <deals:dealRanges/>
                <div ng-class="disableProductPackType">
                    <!-- Free Product -->
                    <deals:dealFree/>
                    <!-- Selected Products -->
                    <deals:dealSelectable/>
                </div>

                <hr class="hr-1 visible-sm visible-md visible-lg">
              </div>

              <div class="col-xs-12">
                <div class="deal-cta">
                  <div class="deal-remaining" ng-show="!pageInactive" ng-class="disableProductPackType">
                      <div ng-hide="deal.remainingQty == null">
                        <span ng-show="deal.remainingQty > 0">You can order <strong ng-init="remaining">{{deal.remainingQty}} <span class="text-lowercase" ng-show="deal.remainingQty == 1">{{deal.ranges[0].baseProducts[0].uomS}}</span><span class="text-lowercase" ng-show="deal.remainingQty == 0 || deal.remainingQty > 1">{{deal.ranges[0].baseProducts[0].uomP}}</span> before {{deal.validTo | date:'dd-MM-yyyy'}}</strong></span>
                        <span ng-show="deal.remainingQty <= 0">You have reached your limit for this deal</span>
                      </div>
                      <div ng-hide="deal.remainingValue == null">
                        <span ng-show="deal.remainingValue > 0">You have <strong>$<span class="remaining">{{deal.remainingValue}}</span> left to claim before {{deal.validTo | date:'dd-MM-yyyy'}}</strong></span>
                        <span ng-show="deal.remainingValue <= 0">You have reached your limit for this deal</span>
                      </div>
                      <div ng-repeat="range in deal.ranges" ng-show="range.totalQty < range.minQty && deal.ranges.length < 2">You have <span class="bold">{{range.minQty - range.totalQty}}</span> more <span class="text-lowercase" ng-show="(range.minQty - range.totalQty) == 1">{{range.baseProducts[0].uomS}}</span><span class="text-lowercase" ng-show="(range.minQty - range.totalQty) == 0 || (range.minQty - range.totalQty) > 1">{{range.baseProducts[0].uomP}}</span> to add before proceeding</div>
                  </div>
                  <div class="deal-remaining" ng-show="pageInactive" ng-class="disableProductPackType">
                      <div class="grey">Change your delivery date in the header to purchase these deals</div>
                  </div>

                  <div class="deal-atc">
                      <button ng-if="deal.isInDeliveryPackType" class="btn btn-primary js-track-deals-addtocart" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0 || bdeViewOnly" ng-click="addToCart(deal, 'deals')"><spring:theme code="text.deals.list.view.add.to.cart" /></button>
                      <div ng-if="!deal.isInDeliveryPackType" class="btn btn-primary btn-invert btn-sm btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>
                  </div>
                </div>
                <span class="hidden" id="addText"><spring:theme code="text.recommendations.add"/></span>
                  <span class="hidden" id="addedText"><spring:theme code="text.recommendations.itemAdded"/></span>
                  <c:if test="${bdeUser}">
                    <div class="addRecommendation offset-bottom-xxsmall">
                      <a class="addRecommendationText" id="addRecommendation{{deal.code}}" ng-click="addToRecommendation(deal, 'deals')" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0 || bdeViewOnly">
                            <svg class="icon-star-normal" id="recommendationStar">
                                <use xlink:href="#icon-star-add"></use>
                            </svg>
                            <span id="recommendationText"><spring:theme code="text.recommendations.add"/></span>
                      </a>
                    </div>
                  </c:if>
              </div>
            </div>

            <hr class="hr-1">
					</div>

					<div class="more-deals" ng-show="deals.length > 3" ng-class="{open:togglePDPLimit.open}" ng-click="limitdealsPDP(deals)">See <span ng-show="!togglePDPLimit.open">more</span><span ng-show="togglePDPLimit.open">less</span> deals for this product 
						<svg class="icon-arrow-down">
							<use xlink:href="#icon-arrow-down"></use>    
						</svg>
					</div>

				</div>
			</div>
		</div>
	</div>
</div>
