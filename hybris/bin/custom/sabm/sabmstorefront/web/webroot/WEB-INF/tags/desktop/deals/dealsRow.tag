<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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

<c:set var="requestOrigin" value="Home/Deals" />

<hr class="hr-1">

<div class="deal js-track-deal-row" ng-repeat="deal in deals | filter:dealDatesFilter | filter:dealBrandsFilter | filter:dealCategoriesFilter |  filter:dealBadgesFilter as dealsfiltered" ng-init="dealInit(deal,true)">
      <span class="hidden">
        {{ disableProductPackType = !deal.isInDeliveryPackType ? 'disabled-productPackTypeNotAllowed' : '';
           bdeUser = '${bdeUser}';
           productQtyNotAllowed = (!deal.isInDeliveryPackType || base.cubStockStatus.code == 'outOfStock') && !bdeUser ? 'disabled-productPackTypeNotAllowed' : '';}}
      </span>
	<div class="row deal-item-head" ng-class="{single: deal.single}">
	  <div class="col-xs-12 base-item">

          <div class="deal-head-left productImpressionTag" ng-class="disableProductPackType">
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

            <%--<div class="deal-img" ng-if="!deal.single"><img ng-src="{{deal.brandImage}}" alt=""></div>--%>

            <div class="deal-title">
              <h2 class="h4" data-deal-code="{{deal.code}}" data-ng-bind-html="getHtml(deal.title)"></h2>
              <div class="low-stock-status-label" ng-if="deal.single && deal.ranges[0].baseProducts[0].cubStockStatus.code == 'lowStock'"><spring:theme code="product.page.stockstatus.low"/></div>
              <span class="bold" ng-class="{'text-red' : deal.daysRemain <= 7}">
                                <span ng-if="deal.daysRemain == 1">
                                        <spring:theme code="text.deals.expires.in.one.day"/>&nbsp;
                                </span>
                                <span ng-if="deal.daysRemain != 1">
                                        <spring:theme code="text.deals.expires.in" arguments="{{deal.daysRemain}}"/>&nbsp;
                                </span>
                        </span>
                      <span><spring:theme code="text.deals.valid.from" arguments="{{customDateFormat(deal.validFrom) }},{{customDateFormat(deal.validTo) }}"/></span>
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

          
          <div class="item-qty clearfix" ng-init="rangeInit(deal,deal.ranges[0])">
              <ul class="select-quantity select-quantity-fixed " ng-class="[productQtyNotAllowed]">
                <li class="down disabled" qty-selector>
                  <svg class="icon-minus">
                      <use xlink:href="#icon-minus"></use>
                  </svg>
                </li>
                <li><input class="qty-input" id="qty-{{deal.code}}-{{base.productCode}}" type="tel" ng-value="{{base.qty}}" ng-init="base.newQty = base.qty" ng-model="base.newQty" ng-change="calcInput(deal,deal.range[0],base)" data-minqty="{{base.qty}}" maxlength="4" pattern="\d*"></li>
                <li class="up" qty-selector>
                  <svg class="icon-plus">
                      <use xlink:href="#icon-plus"></use>
                  </svg>
                </li>
              </ul>
              <div class="item-uom"  ng-class="[productQtyNotAllowed]">
                <span ng-show="base.newQty == 1">{{base.uomS}}</span>
                <span ng-show="base.newQty > 1">{{base.uomP}}</span>
                <span ng-show="base.newQty == 0">{{base.uomP}}</span>
              </div>

             <div ng-if="!deal.isInDeliveryPackType" class="btn btn-primary btn-invert btn-sm btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>
            <button ng-if="deal.isInDeliveryPackType" class="btn btn-primary js-track-deals-addtocart" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0 || bdeViewOnly || base.cubStockStatus.code == 'outOfStock'" ng-click="addToCart(deal, 'deals')">
            
                <span ng-if="base.cubStockStatus.code != 'outOfStock'"><spring:theme code="text.deals.list.view.add.to.cart" /></span>
	          	<span ng-if="base.cubStockStatus.code == 'outOfStock'"><spring:theme code="basket.out.of.stock" /></span>
            </button>
            

                  <span class="hidden" id="addText"><spring:theme code="text.recommendations.add"/></span>
                  <span class="hidden" id="addedText"><spring:theme code="text.recommendations.itemAdded"/></span>
          </div>
            <c:if test="${bdeUser}">
                <div class="addRecommendation">
                  <a class="addRecommendationText" id="addRecommendation{{deal.code}}" ng-click="addToRecommendation(deal, 'deals')" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0">
                       <svg class="icon-star-normal" id="recommendationStar">
                           <use xlink:href="#icon-star-add"></use>
                       </svg>
                       <span id="recommendationText"><spring:theme code="text.recommendations.add"/></span>
                  </a>
                </div>
             </c:if>

      </div>

      <button ng-if="!deal.single" class="btn btn-primary btn-invert deal-toggle pull-right js-track-show-details" ng-click="toggleThis(deal, $event)"><span ng-hide="deal.dealToggle"><spring:theme code="text.deals.list.view.hide" /></span><span ng-hide="!deal.dealToggle"><spring:theme code="text.deals.list.view.show" /></span> <spring:theme code="text.deals.list.view.details" /></button>


          <div class="row labels visible-sm visible-md visible-lg"  ng-class="disableProductPackType">
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

	<div ng-if="!deal.single" class="row deal-item-body" ng-hide="deal.dealToggle">
		<div class="col-xs-12 multi-products">
				<!-- Ranges -->
				<div ng-repeat="range in deal.ranges | orderBy: 'title'" ng-init="rangeInit(deal,range)">
					<div class="row range-title"  ng-class="disableProductPackType" ng-show="range.title && deal.ranges.length > 1">
						<div class="col-md-3"><h4>{{range.title}}</h4></div>
						<div class="col-md-9">
							<div class="inline-deal-remaining" ng-show="range.totalQty < range.minQty && deal.ranges.length > 1 && !pageInactive">You have <span class="bold">{{range.minQty - range.totalQty}}</span> more <span class="text-lowercase" ng-show="(range.minQty - range.totalQty) == 1">{{range.baseProducts[0].uomS}}</span><span class="text-lowercase" ng-show="(range.minQty - range.totalQty) == 0 || (range.minQty - range.totalQty) > 1">{{range.baseProducts[0].uomP}}</span> to add before proceeding</div>

							<div class="inline-deal-remaining grey" ng-show="pageInactive">Change your delivery date in the header to purchase these deals</div>
						</div>
					</div>					
					
					<!-- Base Product -->
					<div class="base-item"  ng-class="productQtyNotAllowed" ng-repeat="base in range.baseProducts | orderBy: 'title'">
					<span class="hidden">
          			{{ bdeUser = '${bdeUser}';
          			disableOutOfStock = base.cubStockStatus.code == 'outOfStock' && !bdeUser ? 'disabled-productOutofStock' : '';
			          }}
          			</span>
						<div class="row item productImpressionTag"><c:set var="prodName" value="{{base.title}}"/><c:set var="brand" value="{{base.brand}}"/><c:set var="category" value="{{base.categories[0].name}}"/>
							<div ng-class="productQtyNotAllowed">
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
            						data-dealsflag="true"><img ng-src="{{base.image}}" alt="{{base.title}}"></a>
							</div>
							<div class="item-title">
									<h4>{{base.title}}</h4>
									<div class="h4 h4-subheader">{{base.packConfig}}</div>
									<div class="low-stock-status-label" ng-if="base.cubStockStatus.code == 'lowStock'"><spring:theme code="product.page.stockstatus.low"/></div>
						  			<div class="out-of-stock-status-label" ng-if="base.cubStockStatus.code == 'outOfStock'"><spring:theme code="product.page.stockstatus.oos"/></div>
							</div>
              </div>
							<div class="item-actions" ng-class="[productQtyNotAllowed,disableOutOfStock]">
								<div class="item-qty clearfix">
									<ul class="select-quantity select-quantity-fixed">
										<li class="down disabled" qty-selector>
											<svg class="icon-minus">
											    <use xlink:href="#icon-minus"></use>    
											</svg>
										</li>
										<li><input class="qty-input" id="qty-{{deal.code}}-{{base.productCode}}" type="tel" ng-value="{{base.qty}}" ng-init="base.newQty = base.qty" ng-model="base.newQty" ng-change="calcInput(deal,range,base)" data-minqty="{{base.qty}}" maxlength="4" pattern="\d*"></li>
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
				<div class="col-xs-12 free"  ng-class="productQtyNotAllowed" ng-class="{'last':$last}" ng-repeat="free in deal.freeProducts">
					<div class="row item">
						<div class="deal-img">
							<a ng-href="{{free.url}}"><img ng-src="{{free.image}}" alt="{{free.title}}"></a>
						</div>
						<div class="item-title"  ng-class="productQtyNotAllowed">
								<h4>{{free.title}}</h4>
								<div class="h4 h4-subheader">{{free.packConfig}}</div>
                <div class="visible-xs" ng-class="productQtyNotAllowed">
                  <div class="item-qty clearfix">
                    <div class="num-free">
                        <span class="num">{{mapQty(deal)}} </span><span class="item-uom"><spring:theme code="deal.page.free" /></span>
                    </div>
                  </div>
                </div>
						</div>
						<div class="col-xs-4 col-sm-4 trim-left visible-sm visible-md visible-lg" ng-class="productQtyNotAllowed">
							<div class="item-qty clearfix">
								<div class="num-free">
									<span class="num">{{mapQty(deal)}} </span><span class="item-uom"><spring:theme code="deal.page.free" /></span>
								</div>
							</div>
						</div>
					</div>
				</div>

				<!-- Selected Products -->
				<div ng-show="deal.selectableProducts.length" class="free-list-title" ng-class="productQtyNotAllowed">
					<span><spring:theme code="deal.page.free.product.select.title"/></span>
				</div>
				<div class="col-xs-12 free selectable" ng-class="productQtyNotAllowed" ng-class="{'last':$last}" ng-repeat="selectable in deal.selectableProducts">
					<div class="row item">

								<div class="radioDeal radio">
								    <input id="freeSelect{{$parent.$index}}-{{$index}}" type="radio" name="freeSelect" ng-value="{{selectable.code}}" ng-click="deal.selectedItem = selectable.code">
								    <label for="freeSelect{{$parent.$index}}-{{$index}}"></label>
								</div>
						
                <div class="deal-img" ng-class="productQtyNotAllowed">
                  <a ng-href="{{selectable.url}}"><img ng-src="{{selectable.image}}" alt="Placeholder Image"></a>
                </div>
                <div class="item-title" ng-class="productQtyNotAllowed">
                    <h4>{{selectable.title}}</h4>
                    <div class="h4 h4-subheader">{{selectable.packConfig}}</div>
                    <div class="visible-xs">
                      <div class="item-qty clearfix">
                        <div class="num-free">
                            <span class="num">{{mapQty(deal)}} </span><span class="item-uom"><spring:theme code="deal.page.free" /></span>
                        </div>
                      </div>
                    </div>
                </div>
                <div class="col-xs-4 col-sm-4 trim-left visible-sm visible-md visible-lg" ng-class="productQtyNotAllowed">
                  <div class="item-qty clearfix">
                    <div class="num-free">
                      <span class="num">{{mapQty(deal)}} </span><span class="item-uom"><spring:theme code="deal.page.free" /></span>
                    </div>
                  </div>
                </div>
                <%--<div class="item-actions">
                  <div class="item-qty clearfix">
                    <div class="num-free"><span>{{mapQty(deal)}} </span><span class="mobile-block"></span><span class="item-uom"><spring:theme code="deal.page.free" /></span></div>
                  </div>
                </div>--%>

					</div>
				</div>
			<hr class="hr-1 visible-sm visible-md visible-lg">
		</div>

    <div class="col-xs-12">
			<div class="deal-cta">
				<div class="deal-remaining" ng-class="disableProductPackType" ng-show="!pageInactive">
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
                          <div ng-if="!deal.isInDeliveryPackType" class="btn btn-primary btn-invert btn-sm btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>
                          <button ng-if="deal.isInDeliveryPackType" class="btn btn-primary js-track-deals-addtocart" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0 || bdeViewOnly" ng-click="addToCart(deal, 'deals')"><spring:theme code="text.deals.list.view.add.to.cart" /></button>


				</div>
        <span class="hidden" id="addText"><spring:theme code="text.recommendations.add"/></span>
        <span class="hidden" id="addedText"><spring:theme code="text.recommendations.itemAdded"/></span>
        <c:if test="${bdeUser}">
          <div class="addRecommendation">
            <a class="addRecommendationText" id="addRecommendation{{deal.code}}" ng-click="addToRecommendation(deal, 'deals')" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0">
                  <svg class="icon-star-normal" id="recommendationStar">
                      <use xlink:href="#icon-star-add"></use>
                  </svg>
                  <span id="recommendationText"><spring:theme code="text.recommendations.add"/></span>
            </a>
          </div>
        </c:if>
			</div>
		</div>
	</div>

  <hr class="hr-1">
</div>

