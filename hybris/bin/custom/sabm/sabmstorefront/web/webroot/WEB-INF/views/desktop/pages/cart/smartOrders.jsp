<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="smartOrders" tagdir="/WEB-INF/tags/desktop/cart/smartOrders" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>

<script id="smartOrdersData" type="text/json">${ycommerce:generateJson(smartOrders)}
</script>

<%--<smartOrders:dummyData/>--%>

<div id="smartCtrl" ng-controller="smartOrdersCtrl" ng-init="init()" ng-cloak>
	<div class="row">
		<div class="col-xs-12">
			<h1><spring:theme code="text.smartorders.title" /></h1>
			<p class="offset-bottom-small"><spring:theme code="text.smartorders.description" /></p>
		</div>
	</div>

	<div class="row margin-0 dropdown-options">
		<div class="col-xs-12 col-sm-4 col-md-3 trim-0">
			<div class="col-xs-12 col-sm-11 trim-0">
				<div class="select form-group">
					<span class="arrow"></span>
					<input id="smart-date" data-container=".form-group" placeholder="Please select your date" data-date-end-date="0d" class="form-control basic-datepicker" type="text" readonly="readonly" ng-model="delivery.date">
				</div>
				<!-- <span class="link-cta" ng-disabled="!orders.seeThisTimeLastYearLink" ng-click="yearAgo()">See this time last year</span> -->
			</div>
		</div>
		<div class="col-sm-5 col-md-6 visible-sm visible-md visible-lg trim-0 next-prev">
			<ul class="list-inline">
				<li>
					<span class="inline btn-height dateLink" ng-class="{disabled : !orders.previousOrdersLink}" ng-click="getData(orders.date, 'prev')">
						<span class="trim-right-5">&#10094;</span>
						<spring:theme code="text.smartorders.previous"/>
					</span>
				</li>
				<li>
					<span class="inline btn-height dateLink" ng-class="{disabled : !orders.nextOrdersLink}" ng-click="getData(orders.date, 'next')">
						<spring:theme code="text.smartorders.next" />
						<span class="trim-left-10">&#10095;</span>
					</span>
				</li>
			</ul>
		</div>
		<div class="col-xs-12 col-sm-3 col-md-3 trim-0 select-list sortby">
			<div class="col-xs-12 col-sm-11 pull-right trim-0">
				<div class="select-btn"></div>
				<!-- Working only in desktop browser-->
		        <ul class="select-items">
		            <li class="hidden"><spring:theme code="text.smartorders.sort.title" /></li>
		            <li data-value="Highest" ng-click="sortSmartOrders('qty')"><spring:theme code="text.smartorders.sort.highest" /></li>
		            <li data-value="Brand" ng-click="sortSmartOrders('brand')"><spring:theme code="text.smartorders.sort.brand" /></li>
		            <li data-value="Ordered" ng-click="sortSmartOrders('numberOfTimesOrdered')"><spring:theme code="text.smartorders.sort.ordered" /></li>
		            <li data-value="Deals" ng-click="sortSmartOrders('dealsFlag')"><spring:theme code="text.smartorders.sort.deals" /></li>
		        </ul>
		        <!-- Angular Fallback for mobile -->
		        <div class="mobile-sortby select">
		        	<span class="arrow"></span>
			        <select ng-model="selected" ng-change="sortSmartOrders()">
			        	<option value="" selected="selected"><spring:theme code="text.smartorders.sort.title" /></option>
			        	<option value="qty"><spring:theme code="text.smartorders.sort.highest" /></option>
			        	<option value="brand"><spring:theme code="text.smartorders.sort.brand" /></option>
			        	<option value="numberOfTimesOrdered"><spring:theme code="text.smartorders.sort.ordered" /></option>
			        	<option value="dealsFlag"><spring:theme code="text.smartorders.sort.deals" /></option>
			        </select>
			    </div>
		    </div>
		</div>
	</div>

	<div class="row visible-md visible-lg margin-0">
		<div class="col-xs-12 prod-headers trim-0">
			<div class="col-xs-12 col-sm-3 img-title-head"><spring:theme code="text.smartorders.heading.item" /></div>
			<div class="col-xs-12 col-sm-9 col-md-9 trim-0">
				<div class="col-xs-12 col-sm-7 history-head trim-left">
					<ul>
						<li class="col-xs-2 trim-0 date" ng-repeat="date in orders.dates track by $index">{{date}}</li>
					</ul>
				</div>
				<div class="col-xs-12 col-sm-5 text-left trim-0 cta-head">
					<spring:theme code="text.smartorders.heading.suggested" />
				</div>
			</div>
		</div>
	</div>

	<div class="row visible-sm margin-0">
		<div class="col-xs-12 prod-headers trim-0">
			<div class="col-xs-3 img-title-head"><spring:theme code="text.smartorders.heading.item" /></div>
			<div class="col-xs-9 history-head trim-0">
				<ul>
					<li class="col-xs-2 trim-0 date" ng-repeat="date in orders.dates track by $index">{{date}}</li>
				</ul>
			</div>
		</div>
	</div>

    <span id="dealTitle" class="hidden"><spring:theme code='text.product.title.deal'/></span>
	<div class="row" ng-repeat="prod in orders.products track by $index" ng-class="{lowstock: prod.cubStockStatus.code == 'lowStock'}">

        <span class="hidden">
            {{deliveryDatePackType = '${deliveryDatePackType}';
            unit = prod.uomList[0].name.toUpperCase() === 'KEG' ? 'KEG' : 'PACK';
            isProductPackTypeAllowed = deliveryDatePackType.indexOf(unit) != -1 ? true : false;}}
        	disableOutOfStock = prod.cubStockStatus.code == 'outOfStock' ? 'disabled-productOutofStock' : '';}}
        
        </span>

		<div class="col-xs-12 prod-row productImpressionTag" ng-class="!isProductPackTypeAllowed ? 'disabled-productPackTypeNotAllowed' : ''">

            <div class="col-xs-12 col-sm-3 col-md-3 trim-0 prod-title-image">
               	<div class="col-xs-4 trim-0 prod-img-block">
                   	<a href="{{prod.url}}" class="js-track-product-link list-item-img"
                   		data-currencycode="AUD"
                   		data-id="{{prod.code}}"
                   		data-sku="{{prod.code}}"
                   		data-name="{{prod.title}}"
                   		data-name="{{prod.title}}"
                   		data-url="{{prod.url}}"
                   		data-imgUrl="{{prod.image}}"
                   		data-category="SmartOrders"
                   		data-brand="{{prod.brand}}"
                		data-variant="{{prod.uomList[0].name}}"
                		data-qty="{{prod.qty}}"
                		data-list="Home/Smart Orders"
                		data-dealsFlag="{{prod.dealsFlag}}"
                		data-position="{{$index+1}}"><img ng-src="{{prod.image}}" alt="Product Image">
                       	<div ng-switch on="prod.dealsFlag">
                           	<div ng-switch-when="true">
                               	<div id="dealBadge" class="badge badge-small badge-red badge-postion" data-toggle="tooltip" data-container="body" data-original-title= "{{deal[$index]}}"><spring:theme code="text.product.title.deal"/>
                               	</div>
                           	</div>
                           	<div ng-switch-default>
                             	<div ng-if="prod.newProductFlag == true">
                                  	<div class="badge badge-small badge-green badge-postion"><spring:theme code="text.product.title.new"/></div>
                              	</div>
                           	</div>
                       	</div>
                   	</a>
               	</div>
                <div class="col-xs-10 col-sm-8 col-md-9 trim-right prod-title">
                	<a href="{{prod.url}}">
                    	<h4>{{prod.title}}</h4>
                    	<div class="h4 h4-subheader">{{prod.packConfig}}</div>
                    </a>
                <div class="low-stock-status-label" ng-if="prod.cubStockStatus.code == 'lowStock'"><spring:theme code="product.page.stockstatus.low"/></div> 
                    
                </div>
            </div> <!-- END IMAGE BLOCK -->

			<div class="col-xs-12 visible-xs prod-headers mobile-prod-headers" ng-init="toggle = false">
				<svg class="icon-arrows left" ng-click="toggle = false" ng-show="toggle">
                   <use xlink:href="#icon-arrow-left"></use>
                </svg>
				<svg class="icon-arrows right" ng-click="toggle = true" ng-show="!toggle">
                   <use xlink:href="#icon-arrow-right"></use>
                </svg>
				<div class="col-xs-12 dates trim-0" ng-class="{next: toggle, prev: !toggle}">
					<ul>
						<li class="col-xs-2 date trim-0 {{$index}}" ng-repeat="date in orders.dates track by $index">{{date}}</li>
					</ul>
				</div>
			</div>

			<div class="col-xs-12 col-sm-9 col-md-9 trim-0">

	            <div class="col-xs-12 col-sm-12 col-md-7 trim-0 prod-history" ng-init="toggle = false">
	            	<div class="col-xs-12 quantities trim-0" ng-class="{next: toggle, prev: !toggle}">
		                <ul class="list-inline">
		                    <li ng-repeat="week in prod.history track by $index" class="text-right">
		                    	<span class="qty"><span ng-show="week.qty != 0">{{week.qty}}</span>
		                    	<span ng-show="week.qty === 0">-</span>
		                    </li>
		                </ul>
		            </div>
	            </div> <!-- END HISTORY -->

				<div class="col-xs-12 col-sm-12 col-md-5 trim-0 prod-cta">
					<ul class="select-quantity select-quantity-fixed" ng-class="disableOutOfStock">
						<li class="down" qty-selector-service>
							<svg class="icon-minus">
							    <use xlink:href="#icon-minus"></use>
							</svg>
						</li>
						<li><input id="item-qty-{{$index}}" name="item-qty-{{$index}}" class="qty-input" ng-keyup="enteredQty($event,$index)" type="tel" ng-value="prod.qty" data-val="newQty" data-scope="prod" data-minqty="0" maxlength="3" max="20" pattern="\d*"></li>
						<li class="up" qty-selector-service>
							<svg class="icon-plus">
							    <use xlink:href="#icon-plus"></use>
							</svg>
						</li>
					</ul>

					<input ng-if="prod.cubStockStatus.code == 'outOfStock'" id="outOfStockProductsPresent" value="true" type="hidden"/>

					<div class="select-list" ng-if="prod.uomList.length > 0">
	                    <div class="select-single" ng-if="prod.uomList.length == 1">{{prod.uomList[0].name}}</div>
	                    <div class="select-dropdown" ng-if="prod.uomList.length > 1">
	                        <div class="select-btn" data-value="">{{prod.uomList[0].name}}</div>
	                        <ul class="select-items text-left">
	                            <li data-val="newUnit" ng-value="{{uom.code}}" data-value="{{uom.code}}" ng-click="unitChange($event,$parent.$index,uom.code)" ng-repeat="uom in prod.uomList">{{uom.name}}</li>
	                        </ul>
	                    </div>
	                </div>
	                <form class="add_to_cart_form" style="display: inline-block">
						<button ng-if="isProductPackTypeAllowed" ng-click="addToCart(prod)" ng-disabled="prod.newQty == 0 || bdeViewOnly || prod.cubStockStatus.code == 'outOfStock'" class="btn btn-primary btn-smartOrders">
						
						<span ng-if="prod.cubStockStatus.code == 'outOfStock'"><spring:theme code="basket.out.of.stock" text="Add to order"/></span>
						<span ng-if="prod.cubStockStatus.code != 'outOfStock'"><spring:theme code="basket.add.to.basket" text="Add to order"/></span>
						</button>
					</form>
				    <div ng-if="!isProductPackTypeAllowed" class="btn btn-primary btn-invert btn-block btn-changeDeliveryDate disabled-productPackTypeNotAllowed">
				        <input id="changeDeliveryDatePresent" value="true" type="hidden"/>
				        <input id="item-isDisabled-{{$index}}" name="item-isDisabled-{{$index}}" ng-value="setDisable($index)" data-val="isDisabled" data-scope="prod" type="hidden">
				        <spring:theme code="basket.change.delivery.date"/>
				    </div>
				</div> <!-- END CTA -->
			</div>
		</div> <!-- END PRODUCT ROW -->

		<div class="col-xs-12">
			<hr class="hr-1">
		</div>
	</div>

	<div class="row smart-orders-foot">
		<div class="col-xs-12 col-sm-6 col-md-5">
			<p><spring:theme code="text.smartorders.footer" /></p>
		</div>
		<div class="col-xs-12 col-sm-6 col-md-7 text-right">
			<c:url value="/Beer/c/10" var="beer" />
			<button class="btn btn-primary" ng-disabled="bdeViewOnly" ng-click="checkAndAddAllToCart()"><spring:theme code="basket.add.all.to.basket" text="Add all items to order"/></button>
			<a href="${beer}" class="inline">
				<span>&#10094;</span>
                <spring:theme code="general.continue.shopping" />
            </a>
		</div>
	</div>
	<hr/>
	<!-- OUT OF STOCK -->
    <common:addItemsPopup isAddToCartTemplate="true"/>
</div>



