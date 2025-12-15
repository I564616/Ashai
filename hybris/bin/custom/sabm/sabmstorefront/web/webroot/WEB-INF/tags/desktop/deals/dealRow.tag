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

<c:set var="tempImgPath" value="/_ui/desktop/SABMiller/img/placeholders/" />
<c:url value="1" var="firstBundle"/>
<c:set value="0" var="dealConditionCount"/>
<c:set value="" var="dealConditionProductCode"/>
<input type="hidden" class="dealCurrentUrl" value="<c:url value="/cart/add/deal"/>">
<input type="hidden" class="recommendationCurrentUrl" value="<c:url value="/recommendation/add/deal"/>">

<hr class="hr-1">

<div class="deal" ng-repeat="deal in deals | filter:dealDatesFilter | filter:dealBrandsFilter | filter:dealCategoriesFilter |  filter:dealBadgesFilter as dealsfiltered" ng-init="dealInit(deal,true)">

	<%--Header and Title of the deal--%>
  <deals:dealHead/>
	
	<div ng-if="!deal.single" class="row deal-item-body" ng-hide="deal.dealToggle">
		<div class="col-xs-12 multi-products">
				<!-- Ranges -->
				<div ng-repeat="range in deal.ranges | orderBy: 'title'" ng-init="rangeInit(deal,range)">
					<div class="row range-title" ng-show="range.title && deal.ranges.length > 1">
						<div class="col-md-3"><h4>{{range.title}}</h4></div>
						<div class="col-md-9">
							<div class="inline-deal-remaining" ng-show="range.totalQty < range.minQty && deal.ranges.length > 1 && !pageInactive">You have <span class="bold">{{range.minQty - range.totalQty}}</span> more <span class="text-lowercase" ng-show="(range.minQty - range.totalQty) == 1">{{range.baseProducts[0].uomS}}</span><span class="text-lowercase" ng-show="(range.minQty - range.totalQty) == 0 || (range.minQty - range.totalQty) > 1">{{range.baseProducts[0].uomP}}</span> to add before proceeding</div>

							<div class="inline-deal-remaining grey" ng-show="pageInactive">Change your delivery date in the header to purchase these deals</div>
						</div>
					</div>					
					
					<!-- Base Product -->
					<div class="base-item" ng-repeat="base in range.baseProducts | orderBy: 'title'">
					<span class="hidden">
          			{{ disableOutOfStock = !bdeViewOnly && base.cubStockStatus.code == 'outOfStock'  ? 'disabled-productOutofStock' : '';
          			}}
          			</span>
						<div class="row item productImpressionTag"><c:set var="prodName" value="{{base.title}}"/><c:set var="brand" value="{{base.brand}}"/><c:set var="category" value="{{base.categories[0].name}}"/>
							<div class="deal-img">
								<a ng-href="{{base.url}}" class="js-track-product-link" 
									data-currencycode="{{base.price.currencyIso}}"
									data-name="${fn:escapeXml(prodName)}"
									data-id="{{base.productCode}}"
									data-price="{{base.price.value}}"
									data-brand="${fn:escapeXml(brand)}"
									data-category="${fn:escapeXml(category)}"
									data-variant=<c:choose>
													<c:when test="{{base.qty}} ge 1">
										 				"{{base.uomP}}"
										 			</c:when>
										 			<c:otherwise>
										 				"{{base.uomS}}"
										 			</c:otherwise>
											   </c:choose>
									data-position="{{$index + 1}}"
									data-url="{{base.url}}"
									data-actionfield="${fn:escapeXml(requestOrigin)}"
									data-list="${fn:escapeXml(requestOrigin)}"
            						data-dealsflag="true"><img ng-src="{{base.image}}" alt="Placeholder Image"></a>
							</div>
							<div class="item-title">
									<h4>{{base.title}}</h4>
									<div class="h4 h4-subheader">{{base.packConfig}}</div>
							</div>
							<div class="item-actions" ng-class="disableOutOfStock">
								<div class="item-qty clearfix">
									<ul class="select-quantity select-quantity-fixed">
										<li class="down disabled" qty-selector>
											<svg class="icon-minus">
											    <use xlink:href="#icon-minus"></use>    
											</svg>
										</li>
										<li><input class="qty-input" id="qty-{{deal.code}}-{{base.productCode}}" type="tel" ng-value="{{base.recommendedBy || base.qty}}" ng-init="base.newQty = base.qty" ng-model="base.newQty" ng-change="calcInput(deal,range,base)" data-minqty="{{base.qty}}" maxlength="4" pattern="\d*"></li>
										<li class="up" qty-selector>
											<svg class="icon-plus">
											    <use xlink:href="#icon-plus"></use>    
											</svg>
										</li>
									</ul>
									<div class="item-uom">
									<span ng-show="base.newQty == 1">{{base.uomS}}</span>
									<span ng-show="base.newQty > 1">{{base.uomP}}</span>
									<span ng-show="base.newQty == 0">{{base.uomP}}</span>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!-- Free Product -->
				<deals:dealFree/>

        <!-- Selectable Product -->
        <deals:dealSelectable/>
			<hr class="hr-1 visible-sm visible-md visible-lg">
		</div>

    <div class="col-xs-12">
			<div class="deal-cta">
				<div class="deal-remaining" ng-show="!pageInactive">
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
				<div class="deal-remaining" ng-show="pageInactive">
						<div class="grey">Change your delivery date in the header to purchase these deals</div>
				</div>

				<div class="deal-atc">
						<button class="btn btn-primary js-track-deals-addtocart" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0 || bdeViewOnly" ng-click="addToCart(deal, 'deals')"><spring:theme code="text.deals.list.view.add.to.cart" /></button>
				</div>
				<span class="hidden" id="addText"><spring:theme code="text.recommendations.add"/></span>
                <span class="hidden" id="addedText"><spring:theme code="text.recommendations.itemAdded"/></span>
			    <a class="addRecommendationText" id="addRecommendation{{deal.code}}" ng-click="addToRecommendation(deal, 'deals')">
                    <svg class="icon-star-normal" id="recommendationStar">
                        <use xlink:href="#icon-star-add"></use>
                    </svg>
                    <span id="recommendationText"><spring:theme code="text.recommendations.add"/></span>
                </a>
			</div>
		</div>
	</div>

  <hr class="hr-1">
</div>

