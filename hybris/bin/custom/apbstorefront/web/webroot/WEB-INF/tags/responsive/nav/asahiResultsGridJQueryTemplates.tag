<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ attribute name="starOutLine" required="false" type="java.lang.String" %>
<%@ attribute name="starBlue" required="false" type="java.lang.String" %>

<%@ attribute name="parentComponent" required="false" type="de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel"%>

<c:set value="" var="hasRecommendation" />
<c:set value="${false}" var="isBDECUSTOMERGROUP" />
<sec:authorize access="hasAnyRole('ROLE_BDECUSTOMERGROUP')" >
    <c:set value="recommendation" var="hasRecommendation" />
    <c:set value="${true}" var="isBDECUSTOMERGROUP" />
</sec:authorize>

<script id="resultsGridItemsTemplate" type="text/x-jquery-tmpl">
	{{each(i, result) $data.results}}
			{{tmpl(result) "#resultsGridItemProductTemplate"}}
		
	{{/each}}
</script>



<script id="resultsGridItemProductTemplate" type="text/x-jquery-tmpl">

		<spring:theme code="text.addToCart" var="addToCartText"/>
		<c:set value="${not empty product.potentialPromotions}" var="hasPromotion"/>
		<c:set value="${request.contextPath}" var="context" /> 
		<div class="product-item ${hasRecommendation}">
		<ycommerce:testId code="product_wholeProduct">
		<div class="row">				
			<div class="col-xs-5 col-sm-12 ${dealsFlag}">
			<input type="hidden" id="productDataCode" value="{{= code}}">
				<a class="thumb thumb-pdp" href="<c:out value="${context}" />{{= url}}" title={{= name}}>
					{{if dealsFlag}}
						<div class="plp-deals-img">Deal</div>
						<div id="sga-deals-tooltip-content" class="hide hidden">
							{{each(i, title) dealsTitle}}
								<div class="item"><b>Deal: </b>{{= title}}</div>
							{{/each}}
						</div>
					{{/if}}

					{{if $.isEmptyObject(images)}}
						<c:if test="${cmsSite.uid eq 'sga'}">
							{{if isPromotionActive}}
								<span class="plp-promotion-img"><spring:theme code="sga.product.promotion.image.text"/></span>
							{{/if}}
						</c:if>
						<theme:image code="img.missingProductImage.responsive.product" title="{{= name}}" alt="{{= name}}" />
					{{else}}
						{{each(i, val) images}}
							{{if val.format ==  'product'}}
								<c:if test="${cmsSite.uid eq 'sga'}">
									{{if newProduct}}  
										<div class="new-product-container"><div class="new-product"><spring:theme code="sga.product.new.identifier"/></div></div>
									{{/if}}  
									{{if isPromotionActive}}
										<span class="plp-promotion-img"><spring:theme code="sga.product.promotion.image.text"/></span>
									{{/if}}
								</c:if>
								<img class="primaryImage" id="primaryImage" src="{{= val.url}}" title="{{= name}}" alt="{{= name}}"/>
							{{/if}}
						{{/each}}				
					{{/if}}
				</a>
		</div>
			<div class="col-xs-7 col-sm-12 no-padding"> 
			<div class="details">
			<div class="hidden-xs">
				<a class="name" href="<c:out value="${context}" />{{= url}}">
					<c:if test="${cmsSite.uid eq 'sga'}">
						<div id="plpProductCode" class="sga-product-code">{{= code}}</div>	
					</c:if> 
					{{if brand}}
 						<span class="product-brand" title="{{= brand.name}} {{= name}}">{{html productName.substr(0,47)}}</span>
					{{else}}
						<span class="product-brand" title=" {{= name}}">{{html productName.substr(0,47)}}</span>
					{{/if}}
					{{if productName.length > 40}}<span>...</span>
					{{/if}}
				</a>
			</div>
			<div class="hidden-sm hidden-md hidden-lg">
				<a class="name" href="<c:out value="${context}" />{{= url}}">
					<c:if test="${cmsSite.uid eq 'sga'}">
						<div id="plpProductCode" class="sga-product-code">{{= code}}</div>	
					</c:if>
					{{if brand}}	
						<span class="product-brand" title="{{= brand.name}} {{= name}}">{{html productName.substr(0,42)}}</span>
					{{else}}
						<span class="product-brand" title="{{= name}}">{{html productName.substr(0,42)}}</span>
					{{/if}}
					{{if productName.length > 42}}<span>...</span>
					{{/if}}
				</a>
			</div>
			<div class="pack_size">
				<span class="pull-left product-unit-volume" title="{{= portalUnitVolume}}">{{= portalUnitVolume}}</span>
				<span class="pull-right">{{= packageSize}}</span>
				<div class="clearfix"></div>

			</div>

			<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" >
				<ycommerce:testId code="product_productPrice">
					<div class="mobile_stock_price_display price">
					<div>
						{{if stock.stockLevelStatus.code == 'lowStock'}}
							<span class="pull-left plp_low_stock_status_name"> {{= stock.stockLevelStatusName}} </span>
						{{/if}}
						{{if stock.stockLevelStatus.code == 'outOfStock'}}
							<span class="pull-left plp_no_stock_status_name"> {{= stock.stockLevelStatusName}} </span>
						{{/if}}
					</div>
					<c:if test="${!isNAPGroup}">
					<span class="price_strike pull-left" id="price_ls_{{= code}}"></span>
					<span class="pull-right plp_price_display" id="price_ns_{{= code}}"></span>
					</c:if>
					<div class="clearfix"></div>
					</div>


					</div>
				</ycommerce:testId>
			</sec:authorize>

		</div> 
	</div>
			<div class="clearfix"></div>
			<div class="col-xs-12 col-sm-12">
	<div class="addtocart">
			<div class="addtocart-component">  
		<c:choose> 
  			<c:when test="{{= stock.stockLevelStatus.code}} eq 'inStock' and empty {{= stock.stockLevel}}">
   					<c:set var="maxQty" value="FORCE_IN_STOCK"/>
  			</c:when>
  			<c:otherwise>
    			<c:set var="maxQty" value="{{= stock.stockLevel}}"/>
  			</c:otherwise>
		</c:choose>

		<c:set var="qtyMinus" value="1" />	
		
    	<div>
		<c:if test="${not multidimensional}">
		    <c:url value="/cart/add" var="addToCartUrl"/>
			<c:url value="/cart/add?action=addBonus" var="addBonusToCartUrl"/>
			<spring:url value="/storefront/apb/en/AUD{{= url}}/configuratorPage/{/configuratorType}" var="configureProductUrl" htmlEscape="false">
				<spring:param name="configuratorType" value="${configuratorType}" />
			</spring:url>

		    <c:if test="${!isNAPGroup}">
			<form id="addToCartForm" action="${addToCartUrl}" method="post" class="add_to_cart_form addToCartForm{{= code}}">
				<div class="container">
					<c:if test="${empty showAddToCart ? true : showAddToCart}">
					<!--  Disabled add to cart component if liquour license is not available -->
					<input type="hidden" name="productLiquor" value="{{= licenseRequired}}" />	

					<c:choose>
						<c:when test="${cmsSite.uid eq 'sga'}">
							<div class="qty-selector input-group js-qty-selector">
							<span class="input-group-btn" style>
							<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
								<button class="btn btn-default js-qty-selector-minus" style="display:inline" type="button"  <c:if test="${qtyMinus <= 1}"><c:out value="disabled='disabled'"/></c:if> {{if stock.stockLevelStatus.code == 'outOfStock'}}<c:out value="disabled='disabled'"/>{{/if}}><span class="glyphicon glyphicon-minus" aria-hidden="true"></span></button>
							</span>
							<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
				            
							{{if stock.stockLevelStatus.code == 'outOfStock'}}
							<input type="text" maxlength="3" style="display:inline" class="form-control js-qty-selector-input" size="1" value="${qtyMinus}" data-max="${maxQty}" data-min="1" name="pdpAddtoCartInput"  id="pdpAddtoCartInput" disabled/>
							{{else}}
							<input type="text" maxlength="3" style="display:inline" class="form-control js-qty-selector-input" size="1" value="${qtyMinus}" data-max="${maxQty}" data-min="1" name="pdpAddtoCartInput"  id="pdpAddtoCartInput" />
							{{/if}}	
							<span class="input-group-btn">
							<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
								<button class="btn btn-default js-qty-selector-plus" style="display:inline" type="button" {{if stock.stockLevelStatus.code == 'outOfStock'}}<c:out value="disabled='disabled'"/>{{/if}}><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
							</span>
							<input type="hidden" name="qty" value="1" id="qty" path="quantity" class="qty js-qty-selector-input" />
					        <ycommerce:testId code="addToCartButton">
					            <input type="hidden" name="productCodePost" value="{{= code}}" id="addProductCode" />
					            <input type="hidden" name="productNamePost" value={{= name}}"/>
					            <input type="hidden" name="productPostPrice" value/>
								<input type="hidden" name ="${CSRFToken.parameterName}" value="${CSRFToken.token}"/>
								<input type="hidden" name="productLiquor" value="{{= licenseRequired}}" />
								<c:if test="${accessType ne 'PAY_ONLY'}">
								{{if stock.stockLevelStatus.code == 'outOfStock'}}
										<c:if test="${!isNAPGroup}">
     									<button type="submit" id="add-to-cart-button-PLP" class="btn btn-primary btn-block list-add-to-cart addToCartButton addToCartButtonPLP" aria-disabled="true" disabled ><img class="add-to-cart-icon" src="/storefront/_ui/responsive/common/images/white_cart.svg"  />
					                    </button>
										</c:if>
								{{else}}
										<c:if test="${!isNAPGroup}">
										<button type="submit" id="add-to-cart-button-PLP" class="btn btn-primary js-enable-btn list-add-to-cart addToCartButton addToCartButtonPLP"><img class="add-to-cart-icon" src="/storefront/_ui/responsive/common/images/white_cart.svg"  />
					                    </button>
										</c:if>
								{{/if}}
								</c:if>
					        </ycommerce:testId>


				        </div>

                        <!-- SGA Add Bonus Button -->
                        <c:if test="${cmsSite.uid eq 'sga' && isBDECUSTOMERGROUP}">
                            <div class="bonus-stock-plp bonus-stock-pdp mt-10">
                                <button
                                    type="button"
                                    class="btn btn-vd-primary btn-primary "
                                    {{if allowedBonusQty < 1}}<c:out value="disabled='disabled'"/>{{/if}}
                                    data-product-code="{{= code}}"
                                    data-add-bonus-url="${addBonusToCartUrl}"
                                    data-product-name="{{= name}}"
                                    data-csrf-token="${CSRFToken.token}"
                                    onclick="ACC.product.addBonus(this)">
                                    <spring:theme code="order.entry.bonus.text"/>
                                </button>
                            </div>
                        </c:if>

                        <c:if test="${not empty starOutLine && not empty starBlue}">
                            <!-- Recommendation feature -->
                            <div class="recommendation-action text-left input-group cursor mt-5 ml-xs-0" data-product-code="{{= code}}" onclick="ACC.recommendation.add(this)">
                                <div class="inline-block">
                                    <img id="star-blue-outline" src="${starOutLine}" />
                                    <img id="star-blue" class="hidden" src="${starBlue}" />
                                </div>
                                <div class="inline-block recommendation-text-wrapper"><spring:theme code="sga.text.recommendation.add.to" /></div>
                            </div>
                        </c:if>
						</c:when>
						<c:otherwise>
							{{if licenseRequired}}
                                <div class="qty-selector input-group js-qty-selector">
                                    <span class="input-group-btn" style>
                                    <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                                        <button class="btn btn-default js-qty-selector-minus" style="display:inline" type="button" disabled ><span class="glyphicon glyphicon-minus" aria-hidden="true"></span></button>
                                    </span>
                                    <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                                    <input type="text" maxlength="3" style="display:inline"  class="form-control js-qty-selector-input" size="1" value="${qtyMinus}" data-max="${maxQty}" data-min="1" name="pdpAddtoCartInput"  id="pdpAddtoCartInput" disabled />
                                    <span class="input-group-btn">
                                    <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                                        <button class="btn btn-default js-qty-selector-plus" style="display:inline" type="button" disabled ><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
                                    </span>
                                    <c:if test="${!isNAPGroup}">
                                    <button type="submit" id="add-to-cart-button-PLP" class="btn btn-primary btn-block list-add-to-cart addToCartButton addToCartButtonPLP"
                                     aria-disabled="true" disabled ><img class="add-to-cart-icon" src="/storefront/_ui/responsive/common/images/white_cart.svg"  />
                                    </button>
                                    </c:if>
                                    <input type="hidden" name="productLiquor" value="{{= licenseRequired}}" />
                                </div>
                            {{else}}
						<div class="qty-selector input-group js-qty-selector">
							<span class="input-group-btn" style>
							<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
								<button class="btn btn-default js-qty-selector-minus" style="display:inline" type="button" <c:if test="${qtyMinus <= 1}"><c:out value="disabled='disabled'"/></c:if>><span class="glyphicon glyphicon-minus" aria-hidden="true"></span></button>
							</span>
							<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
				            <input type="text" maxlength="3" style="display:inline" class="form-control js-qty-selector-input" size="1" value="${qtyMinus}" data-max="${maxQty}" data-min="1" name="pdpAddtoCartInput"  id="pdpAddtoCartInput" />
							<span class="input-group-btn">
							<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
								<button class="btn btn-default js-qty-selector-plus" style="display:inline" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
							</span>
							<input type="hidden" name="qty" value="1" id="qty" path="quantity" class="qty js-qty-selector-input" />
					        <ycommerce:testId code="addToCartButton">
					            <input type="hidden" name="productCodePost" value="{{= code}}" id="addProductCode" />
					            <input type="hidden" name="productNamePost" value={{= name}}"/>
					            <input type="hidden" name="productPostPrice" value/>
								<input type="hidden" name ="${CSRFToken.parameterName}" value="${CSRFToken.token}"/>
								<input type="hidden" name="productLiquor" value="{{= licenseRequired}}" />
     									<button type="submit" id="add-to-cart-button-PLP" class="btn btn-primary js-enable-btn list-add-to-cart addToCartButton addToCartButtonPLP"><img class="add-to-cart-icon" src="/storefront/_ui/responsive/common/images/white_cart.svg"  />
					                    </button>
					        </ycommerce:testId>
				        </div>

						{{/if}}
					
						</c:otherwise>
					</c:choose>
					
				    </c:if>
		        </div>
		    </form>
		    </c:if>
			<form id="addBonusToCartForm{{= code}}" action="${addBonusToCartUrl}" method="post" class="add_bonus_to_cart_form">
				<c:if test="${cmsSite.uid eq 'apb' and asmMode}">
					<div class="container">
						<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
							<input type="hidden" name="qty" value="1" id="qty" path="quantity" class="qty js-bonus-qty-input js-qty-selector-input" />
							<ycommerce:testId code="addBonusToCartButton">
								<input type="hidden" name="productCodePost" value="{{= code}}" id="addProductCode" />
								<input type="hidden" name="productNamePost" value="{{= name}}"/>
								<input type="hidden" name ="${CSRFToken.parameterName}" value="${CSRFToken.token}"/>
								<div class="bonus-stock-plp" id="{{= code}}">
									<button class="btn btn-vd-primary btn-primary" {{if licenseRequired}}disabled{{/if}}><spring:theme code="order.entry.bonus.text"/></button>
								</div>
							</ycommerce:testId>
						</sec:authorize>
					</div>
				</c:if>
			</form>
		
		    <form id="configureForm{{= code}}" action="${configureProductUrl}" method="get" class="configure_form">
		        <c:if test="${product.configurable}">
		            <c:choose>
		                <c:when test="${product.stock.stockLevelStatus.code eq 'outOfStock' }">
		                    <button id="configureProduct" type="button" class="btn btn-primary btn-block"
		                            disabled="disabled">
		                        <spring:theme code="basket.configure.product"/>
		                    </button>
		                </c:when>
		                <c:otherwise>
		                    <button id="configureProduct" type="button" class="btn btn-primary btn-block js-enable-btn" disabled="disabled"
		                            onclick="location.href='${configureProductUrl}'">
		                        <spring:theme code="basket.configure.product"/>
		                    </button>
		                </c:otherwise>
		            </c:choose>
		        </c:if>
		    </form>
		</c:if>
    </div>
    
    
</div>
</div>

		</ycommerce:testId>
		</div>
</div>
</div>

</script>
