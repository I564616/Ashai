<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ attribute name="isCustomerView" required="false" type="java.lang.Boolean"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ attribute name="destinationPage" required="false" type="java.lang.String"%>

	<div class="row deal-item-head <c:choose>
	                  <c:when test="${bdeUser}">recommendationRepRow</c:when>
	                  <c:otherwise>recommendationCusRow</c:otherwise>
	                </c:choose>" ng-class="{single: deal.single}">
    <div class="col-xs-12" >
        <span class="hidden">
          {{ disableProductPackType = !deal.isInDeliveryPackType ? 'disabled-productPackTypeNotAllowed' : '';
             bdeUser = '${bdeUser}';
             productQtyNotAllowed = (!deal.isInDeliveryPackType || base.cubStockStatus.code == 'outOfStock') && !bdeUser  ? 'disabled-productPackTypeNotAllowed' : '';}}
        </span>
      <div class="deal-head-left productImpressionTag" ng-class="disableProductPackType">
        <span class="recommendation-name">
          <spring:theme code="text.recommendations.recommendedBy"/>&nbsp;{{deal.recommendedBy}}
        </span>
      	<%-- TODO remove deal.code after IPT testing --%>
          <div class="deal-img">
         				<c:set var="prodName" value="{{deal.firstBaseProduct.title}}"/>
         				<c:set var="brand" value="{{deal.firstBaseProduct.brand}}"/>
         				<c:set var="category" value="{{deal.firstBaseProduct.categories[0].name}}"/>
                        <a ng-href="{{deal.dealImageUrl}}" class="js-track-product-link" 
                        			data-currencycode="{{deal.firstBaseProduct.price.currencyIso}}"
									data-name="${fn:escapeXml(prodName)}"
									data-id="{{deal.firstBaseProduct.productCode}}"
									data-price="{{deal.firstBaseProduct.price.value}}"
									data-brand="${fn:escapeXml(brand)}"
									data-category="${fn:escapeXml(category)}"
									data-variant=<c:choose>
													<c:when test="{{deal.firstBaseProduct.qty}} ge 1">
										 				"{{deal.firstBaseProduct.uomP}}"
										 			</c:when>
										 			<c:otherwise>
										 				"{{deal.firstBaseProduct.uomS}}"
										 			</c:otherwise>
											   </c:choose>
									data-position="{{$index + 1}}"
									data-url="{{deal.firstBaseProduct.url}}"
									data-actionfield="${fn:escapeXml(requestOrigin)}"
									data-list="${fn:escapeXml(requestOrigin)}"
            						data-dealsflag="true"><img ng-src="{{deal.dealImage}}" alt="{{deal.dealImageTitle}}"></a>
          </div>

        <div class="deal-title">
          <h2 class="h4" data-deal-code="{{deal.code}}" data-ng-bind-html="getHtml(deal.title)"></h2>
          <div class="low-stock-status-label" ng-if="deal.single && deal.ranges[0].baseProducts[0].cubStockStatus.code== 'lowStock'"><spring:theme code="product.page.stockstatus.low"/></div>
           <span class="bold" ng-class="{'text-red' : deal.daysRemain <= 7}">
							<span ng-if="deal.daysRemain == 1">
									<spring:theme code="text.deals.expires.in.one.day"/>&nbsp;
							</span>
							<span ng-if="deal.daysRemain != 1">
									<spring:theme code="text.deals.expires.in" arguments="{{deal.daysRemain}}"/>&nbsp;
							</span>
					</span>
          <span><spring:theme code="text.deals.valid.from" arguments="{{deal.validFrom | date:'dd/MM/yyyy'}},{{deal.validTo | date:'dd/MM/yyyy'}}"/></span>
        </div>

        <div class="row labels visible-xs">
          <div class="col-xs-12 deal-daterange">
            <ul class="list-inline">
              <li ng-show="deal.badges.indexOf(0) >= 0"><div class="lc-label small-text"><spring:theme code="text.deals.last.chance" /><svg class="icon-clock"><use xlink:href="#icon-clock"></use></svg></div></li>
              <li ng-show="deal.badges.indexOf(2) >= 0"><div class="lo-label small-text"><spring:theme code="text.deals.last.limitedoffer" /><svg class="icon-limitedOffer"><use xlink:href="#icon-limitedOffer"></use></svg></div></li>
              <li ng-show="deal.badges.indexOf(3) >= 0"><div class="oo-label small-text"><spring:theme code="text.deals.last.onlineonly" /><svg class="icon-pc"><use xlink:href="#icon-pc"></use></svg></div></li>
              <li ng-show="deal.active"><div class="ai-label small-text"><spring:theme code="text.deals.last.agreedInstore" /><svg class="icon-document"><use xlink:href="#icon-document"></use></svg></div></li>
            </ul>
          </div>
        </div>
      </div>

      <div ng-if="deal.single" class="deal-head-right" ng-init="base = deal.ranges[0].baseProducts[0]">
        <div class="item-qty clearfix deal-product-row" ng-init="rangeInit(deal,deal.ranges[0])">
          <input type="hidden" name="productCodePost" value="{{base.productCode}}" />
          <span class="hidden">
          {{ stockStatus=base.cubStockStatus.code;
          }}
          </span>
          
          <ul class="select-quantity select-quantity-fixed" ng-class="[productQtyNotAllowed]">
            <li class="down disabled" qty-selector>
              <svg class="icon-minus">
                  <use xlink:href="#icon-minus"></use>    
              </svg>
            </li>
            <li><input class="qty-input" id="qty-{{deal.code}}-{{base.productCode}}" type="tel"  ng-value="{{base.qty}}"  data-initQty="{{base.qty}}" ng-init="base.newQty = base.qty" ng-model="base.newQty" ng-change="calcInput(deal,deal.range[0],base)" data-minqty="{{deal.ranges[0].minQty}}" maxlength="4" pattern="\d*"></li>
            <li class="up" qty-selector>
              <svg class="icon-plus">
                  <use xlink:href="#icon-plus"></use>    
              </svg>
            </li>
          </ul><div class="item-uom" ng-class="[productQtyNotAllowed]">
            <span ng-show="base.newQty == 1">{{base.uomS}}</span>
            <span ng-show="base.newQty > 1">{{base.uomP}}</span>
            <span ng-show="base.newQty == 0">{{base.uomP}}</span>
          </div>
          
          
          
          

          <span class="non-recommendations-cta">
            <%--Add to cart button--%>
          	<button ng-if="deal.isInDeliveryPackType" class="btn btn-primary js-track-deals-addtocart" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0 || bdeViewOnly || base.cubStockStatus.code == 'outOfStock'" ng-click="addToCart(deal, 'deals')">
          	<span ng-if="base.cubStockStatus.code != 'outOfStock'"><spring:theme code="text.deals.list.view.add.to.cart" /></span>
          	<span ng-if="base.cubStockStatus.code == 'outOfStock'"><spring:theme code="basket.out.of.stock" /></span>
          	
          	</button>
          
            <div ng-if="!deal.isInDeliveryPackType && !bdeUser" class="btn btn-primary btn-invert btn-sm btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>

            <span class="hidden" id="addText"><spring:theme code="text.recommendations.add"/></span>
            <span class="hidden" id="addedText"><spring:theme code="text.recommendations.itemAdded"/></span>
          </span>
          <span class="recommendations-cta">
            <c:choose>
                  <c:when test="${isCustomerView eq true && destinationPage ne 'PDP'}">
                      <%--Add recommendation to cart button--%>
                      <button ng-if="deal.isInDeliveryPackType" id="recommendation-addToOrder-{{deal.code}}" class="recommendation-addToOrder btn btn-primary pull-right js-track-deals-addtocart" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0 || bdeViewOnly" ng-click="addToCart(deal, 'deals')"><spring:theme code="text.deals.list.view.add.to.cart" /></button>
                      <div ng-if="deal.isInDeliveryPackType" class="delete"><span class="inline deleteRecommendation"><spring:theme code="text.iconCartRemove"/></span></div>
                      <div ng-if="!deal.isInDeliveryPackType" class="btn btn-primary btn-invert btn-sm btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>
                  </c:when>
                  <c:otherwise>
                      <%--Delete deal--%>
                      <span class="inline deleteRecommendation"><spring:theme code="text.iconCartRemove"/></span>
                  </c:otherwise>
              </c:choose>
          </span>
        </div>

        <c:if test="${isCustomerView eq false}">
          <div class="addRecommendation">
            <a class="addRecommendationText" id="addRecommendation{{deal.code}}" ng-click="addToRecommendation(deal, 'deals')">
                  <svg class="icon-star-normal" id="recommendationStar">
                      <use xlink:href="#icon-star-add"></use>
                  </svg>
                  <span id="recommendationText"><spring:theme code="text.recommendations.add"/></span>
            </a>
          </div>
        </c:if>
        
      </div>

      <div ng-if="!deal.single" class="pull-right recommendations-cta toggle-body visible-sm visible-md visible-lg">
        <%--Toggle deal--%>
        <button class="btn btn-primary btn-invert deal-toggle js-track-show-details" ng-click="toggleThis(deal, $event)"><span ng-hide="deal.dealToggle"><spring:theme code="text.deals.list.view.hide" /></span><span ng-hide="!deal.dealToggle"><spring:theme code="text.deals.list.view.show" /></span> <spring:theme code="text.deals.list.view.details" /></button>
        
        <c:if test="${isCustomerView eq true && destinationPage ne 'PDP'}">
            <%--Add recommendation to cart--%>
            <button ng-if="deal.isInDeliveryPackType" id="recommendation-addToOrder-{{deal.code}}" class="recommendation-addToOrder btn btn-primary pull-right js-track-deals-addtocart" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0 || bdeViewOnly" ng-click="addToCart(deal, 'deals')"><spring:theme code="text.deals.list.view.add.to.cart" /></button>
            <div ng-if="!deal.isInDeliveryPackType" class="btn btn-primary btn-invert btn-sm btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>
        </c:if>
        <div class="delete"><span class="inline deleteRecommendation"><spring:theme code="text.iconCartRemove"/></span></div>
      </div>

      <div class="row labels visible-sm visible-md visible-lg">
        <div class="col-xs-12 deal-daterange">
          <ul class="list-inline">
            <li ng-show="deal.badges.indexOf(0) >= 0"><div class="lc-label small-text"><spring:theme code="text.deals.last.chance" /><svg class="icon-clock"><use xlink:href="#icon-clock"></use></svg></div></li>
            <li ng-show="deal.badges.indexOf(2) >= 0"><div class="lo-label small-text"><spring:theme code="text.deals.last.limitedoffer" /><svg class="icon-limitedOffer"><use xlink:href="#icon-limitedOffer"></use></svg></div></li>
            <li ng-show="deal.badges.indexOf(3) >= 0"><div class="oo-label small-text"><spring:theme code="text.deals.last.onlineonly" /><svg class="icon-pc"><use xlink:href="#icon-pc"></use></svg></div></li>
            <li ng-show="deal.active"><div class="ai-label small-text"><spring:theme code="text.deals.last.agreedInstore" /><svg class="icon-document"><use xlink:href="#icon-document"></use></svg></div></li>
          </ul>
        </div>
      </div>
		</div>
  </div>
