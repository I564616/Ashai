 <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<span class="hidden">
  {{ disableProductPackType = !deal.isInDeliveryPackType ? 'disabled-productPackTypeNotAllowed' : '';
     bdeUser = '${bdeUser}';
     productQtyNotAllowed = !deal.isInDeliveryPackType && !bdeUser ? 'disabled-productPackTypeNotAllowed' : '';}}
</span>

  <!-- Ranges -->
  <div ng-repeat="range in deal.ranges | orderBy: 'title'" ng-init="rangeInit(deal,range)">
    <div class="row range-title" ng-show="range.title && deal.ranges.length > 1" ng-class="disableProductPackType">
      <div class="col-md-3"><h4>{{range.title}}</h4></div>
      <div class="col-md-9">
        <div class="inline-deal-remaining" ng-show="range.totalQty < range.minQty && deal.ranges.length > 1 && !pageInactive">You have <span class="bold">{{range.minQty - range.totalQty}}</span> more <span class="text-lowercase" ng-show="(range.minQty - range.totalQty) == 1">{{range.baseProducts[0].uomS}}</span><span class="text-lowercase" ng-show="(range.minQty - range.totalQty) == 0 || (range.minQty - range.totalQty) > 1">{{range.baseProducts[0].uomP}}</span> to add before proceeding</div>

        <div class="inline-deal-remaining grey" ng-show="pageInactive">Change your delivery date in the header to purchase these deals</div>
      </div>
    </div>					
    
    <!-- Base Product -->
    <div class="base-item" ng-repeat="base in range.baseProducts | orderBy: 'title'">
    <span class="hidden">
          {{ disableOutOfStock = base.cubStockStatus.code == 'outOfStock' ? 'disabled-productOutofStock' : '';
          }}
          </span>
      <div class="row item productImpressionTag deal-product-row">
    	<input type="hidden" name="productCodePost" value="{{base.productCode}}" />
    	<c:set var="prodName" value="{{base.title}}"/>
    	<c:set var="brand" value="{{base.brand}}"/>
    	<c:set var="category" value="{{base.categories[0].name}}"/>
        <div class="deal-img" ng-class="disableProductPackType">
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
        <div class="item-title" ng-class="disableProductPackType" >
            <h4>{{base.title}}</h4>
            <div class="h4 h4-subheader">{{base.packConfig}}</div>
            <div class="low-stock-status-label" ng-if="base.cubStockStatus.code == 'lowStock'"><spring:theme code="product.page.stockstatus.low"/></div>
			<div class="out-of-stock-status-label" ng-if="base.cubStockStatus.code == 'outOfStock'"><spring:theme code="product.page.stockstatus.oos"/></div>
        </div>
        <div class="item-actions" ng-class="[disableOutOfStock,productQtyNotAllowed]">
          <div class="item-qty clearfix">
            <ul class="select-quantity select-quantity-fixed">
              <li class="down disabled" qty-selector>
                <svg class="icon-minus">
                    <use xlink:href="#icon-minus"></use>    
                </svg>
              </li>
              <li><input class="qty-input" type="tel" ng-value="{{base.qty}}" ng-init="base.newQty = base.qty" ng-model="base.newQty" ng-change="calcInput(deal,range,base)" data-minqty="{{base.qty}}" maxlength="4" pattern="\d*"></li>
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
