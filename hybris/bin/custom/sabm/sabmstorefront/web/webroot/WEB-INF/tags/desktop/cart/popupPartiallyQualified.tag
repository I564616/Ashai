<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<script id="addressData" type="text/json">${ycommerce:generateJson(anotherDeliveryAddresses)}</script>
<div id="partiallyQualified" class="partially-qualified-Popup mfp-hide" ng-init="partialQualInit()">
	<form name="partiallyQualForm">
		<h2 class="h1 offset-bottom-small"><spring:theme code="text.pqd.modal.title" /></h2>
		<p class="offset-bottom-large"><spring:theme code="text.pqd.modal.subtitle" /></p>
		<div class="row offset-bottom-xsmall toggle-slide js-track-deal-row" ng-repeat="deal in partialQualDeals.deals">
			<div class="col-xs-12 checkbox" >
				<input id="check_{{deal.code}}" type="checkbox" name="partialQual" ng-change="resizeDealsData.dealsCheckbox(deal, 'check_{{deal.code}}')" ng-model="deal.selected" ng-value="deal.code" class="js-track-show-details"/>
				<label for="check_{{deal.code}}" data-ng-bind-html="getHtml(deal.title)" ></label>
			</div>
			<div class="col-xs-12 listing toggle-body" ng-repeat="range in deal.ranges" ng-init="range.totalQty = calcTotalQty(range)" ng-show="deal.selected" ng-init="range.totalQty = calcTotalQty(range)">
				<h3 ng-show="range.title && deal.ranges.length > 1">{{range.title}}</h3>
				<div class="row base-rows" ng-repeat="base in range.baseProducts">
					<div class="col-xs-12 col-sm-8 productImpressionTag dealsImpressionTag">
						<c:set var="prodName" value="{{base.title}}"/>
						<c:set var="brand" value="{{base.brand}}"/>
						<c:set var="category" value="{{base.categories[0].name}}"/>
						<h4 class="js-track-product-link"
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
							data-actionfield="${fn:escapeXml(requestOrigin)}/Review Your Order/Partially Qualified Deals Modal"
							data-list="${fn:escapeXml(requestOrigin)}/Review Your Order/Partially Qualified Deals Modal"
            				data-dealsflag="true">{{base.title}}</h4>
						<div class="h4 h4-subheader">{{base.packConfig}}</div>
					</div>
					<div class="col-xs-12 col-sm-4">
							<ul class="select-quantity select-quantity-fixed">
								<li class="down disabled" qty-selector>
									<svg class="icon-minus">
									    <use xlink:href="#icon-minus"></use>    
									</svg>
								</li>
								<li><input class="qty-input" type="tel" ng-init="base.newQty = base.qty" ng-value="base.qty" data-minqty="{{base.qty}}" maxlength="3" pattern="\d*"></li>
								<li class="up" qty-selector>
									<svg class="icon-plus">
									    <use xlink:href="#icon-plus"></use>    
									</svg>
								</li>
							</ul>
					</div>
				</div>
			</div>
		</div>
		<div class="row offset-bottom-small" ng-repeat="conflictGroup in partialQualDeals.conflicts">
			<div class="col-xs-12 offset-bottom-small">
				<p><spring:theme code="text.pqd.modal.subtitle.conflict" /></p>
			</div>
			<div class="col-xs-12 list-radio radio offset-bottom-small">
				<div class="toggle-slide js-track-deal-row" ng-repeat="deal in conflictGroup.conflict">
				    <div class="offset-bottom-xsmall">
				        <input id="conflict_{{$parent.$index}}{{$index}}_{{deal.code}}" type="radio" ng-change="resizeDealsData.conflictRadio(deal, 'conflict_{{$parent.$index}}{{$index}}_{{deal.code}}')" ng-model="conflictGroup.active" name="conflictActive{{$parent.$index}}" ng-value="deal" class="js-track-show-details">
				        <label for="conflict_{{$parent.$index}}{{$index}}_{{deal.code}}" data-ng-bind-html="getHtml(deal.title)"></label>
				    </div>
				    <div class="col-xs-12 listing toggle-body" ng-show="conflictGroup.active.code == deal.code">
				    	<div ng-repeat="range in deal.ranges" ng-init="range.totalQty = calcTotalQty(range)">
				    		<h3 ng-show="range.title && deal.ranges.length > 1" >{{range.title}}</h3>
				    		<div class="row base-rows" ng-repeat="base in range.baseProducts">
				    			<div class="col-xs-12 col-sm-8 productImpressionTag">
				    				<c:set var="prodName" value="{{base.title}}"/>
									<c:set var="brand" value="{{base.brand}}"/>
									<c:set var="category" value="{{base.categories[0].name}}"/>
				    				<h4 class="js-track-product-link"
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
										data-actionfield="${fn:escapeXml(requestOrigin)}/Review Your Order/Partially Qualified Deals Modal"
										data-list="${fn:escapeXml(requestOrigin)}/Review Your Order/Partially Qualified Deals Modal"
			            				data-dealsflag="true">{{base.title}}</h4>
				    				<div class="h4 h4-subheader">{{base.packConfig}}</div>
				    			</div>
				    			<div class="col-xs-12 col-sm-4">
				    					<ul class="select-quantity select-quantity-fixed">
				    						<li class="down disabled" qty-selector>
				    							<svg class="icon-minus">
				    							    <use xlink:href="#icon-minus"></use>    
				    							</svg>
				    						</li>
				    						<li><input class="qty-input" type="tel" ng-init="base.newQty = base.qty" ng-value="{{base.qty}}" data-minqty="{{base.qty}}" maxlength="3" pattern="\d*"></li>
				    						<li class="up" qty-selector>
				    							<svg class="icon-plus">
				    							    <use xlink:href="#icon-plus"></use>    
				    							</svg>
				    						</li>
				    					</ul>
				    			</div>
				    		</div>
				    	</div>
				    </div>
				</div>

			</div>

		</div>
		<div class="row">
			<div class="col-xs-12 margin-top-10">
			    <%--Deleted for googleTagManager--%>
				<%--<button class="btn btn-secondary" onclick="$.magnificPopup.close();trackDismissPQD()"><spring:theme code="text.pqd.model.button.no" /></button>--%>
                <button class="btn btn-secondary" onclick="rm.tagManager.addDealsImpressionAndPosition('Clicked', 'PQD', 'RejectDeal'); $.magnificPopup.close()"><spring:theme code="text.pqd.model.button.no" /></button>

				<button class="btn btn-primary pull-right js-track-deals-addtocart" id="selectThisDeal" ng-click="addPartialQualToCart();" onclick="rm.tagManager.addDealsImpressionAndPosition('Clicked', 'PQD', 'ApplyChosenDeal');" ng-disabled="!partialQualsValid()"><spring:theme code="text.pqd.model.button.addtocart" /></button>
			</div>
		</div>

	</form>
</div>