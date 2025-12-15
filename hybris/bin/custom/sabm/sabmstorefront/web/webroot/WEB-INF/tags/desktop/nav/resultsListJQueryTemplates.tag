<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>

<script id="resultsListItemsTemplate" type="text/x-jquery-tmpl">
	{{each(i, result) $data.results}}
<div class="col-sm-4 col-md-4 list-item addtocart-qty productImpressionTag" data-theme="d">
   <div id="productPackTypeNotAllowed{{= code}}">
        <input name="deliveryDatePackType" value="${deliveryDatePackType}" type="hidden"/>
        <input name="bdeUser" value="${bdeUser}" type="hidden"/>

		<a href="<c:url value="/" />{{= result.url}}" class="productMainLink js-track-product-link" title="{{= result.name}}"
				data-currencycode="{{= !$.isEmptyObject(result.price) ? result.price.currencyIso : ""}}"
				data-name="${fn:escapeXml("{{= result.name}}")}"
				data-id="{{= result.code}}"
				data-price="{{= !$.isEmptyObject(result.price) ? result.price.value : ""}}"
				data-brand="${fn:escapeXml("{{= result.brand}}")}"
				data-category="${fn:escapeXml(categoryData.name)}"
				data-variant={{if $.isEmptyObject(result.uomList)}}
					 				"{{= result.unit}}"
					 		 {{else}}
					 				"{{= result.uomList[0].name}}"
					 		 {{/if}}
				data-position="{{= i + 1 + $data.pagination.pageSize}}"
				data-url="{{= result.url}}"
				data-actionfield="${fn:escapeXml(requestOrigin)}"
				data-list="${fn:escapeXml(requestOrigin)}"
            	data-dealsflag="{{= result.dealsFlag}}">
		<div class="thumb">
			{{tmpl(result) "#resultsListItemImageTemplate"}}
		</div>
		<div class="list-item-title">
			<h3 class="need-clamp2">{{= result.name}}</h3>
			<div class="h3 h3-subheader need-clamp1">{{= result.packConfiguration}}</div>
		</div>
	</a>
<c:if test="${!isNAPGroup}">
	<div class="col-xs-7 trim-right-5" style="padding-left:0% !important;">
		<div class="list-item-price">
			<span><spring:theme code="text.product.your.param" arguments="{{= !$.isEmptyObject(uomList) ? uomList[0].name : result.unit}}"/></span>
			<div class="h1">
				{{= !$.isEmptyObject(result.price) ? result.price.formattedValue : ""}}
			</div>
		</div>
	</div>
	<!-- Start Savings --> 
	<div class="col-xs-5 trim-left-5" style="padding-right: 0 !important;">
		<div class="list-item-price text-right">			       
			{{if result.savingsPrice != null && result.savingsPrice.value > 0}}
				<span>
					<spring:theme code="product.price.save" />
				</span>
				<div class="list-item-saving">							
					{{= result.savingsPrice.formattedValue}}							
				</div>
			{{/if}}
		</div>
	</div>	
	<!-- End Savings -->
    <div class="row">
        <div class="col-xs-12" style="margin-top:-20px;">
			<!-- Low Stock Label-->
			{{if !$.isEmptyObject(result.cubStockStatus)}}
				{{if result.cubStockStatus.code == 'lowStock'}}
					<span class="low-stock-status-label"><spring:theme code="product.page.stockstatus.low"/></span>
				{{/if}}
			{{/if}}<br>
			{{if result.maxOrderQuantity != null && result.maxOrderQuantity > 0}}
                <span style="color:red;font-weight:700;"><spring:theme code="product.page.maxorderquantity" arguments="{{= result.maxOrderQuantityDays}},{{= result.maxOrderQuantity}}" /></span>
            {{/if}}
   
            {{if result.maxOrderQuantity == null}}
                <span style="color:red;"><br/></span>
            {{/if}}
            <product:productPriceInfo/>
        </div>
    </div>
</c:if>
   </div>
    <div id="productQtyNotAllowed{{= code}}" class="row list-qty">{{tmpl(result) "#resultsListItemProductQtyTemplate"}}</div>
	{{tmpl(result) "#resultsListItemProductAddToCartTemplate"}}
</div>
{{/each}}
</script>

<script id="resultsListItemProductQtyTemplate" type="text/x-jquery-tmpl">
<c:if test="${!isNAPGroup}">
	<div class="col-xs-6 trim-right-5-lg {{if !$.isEmptyObject(cubStockStatus) && cubStockStatus.code == 'outOfStock'  && (${bdeUser} != true)}} disabled-productOutofStock{{/if}}">
		<ul class="select-quantity clearfix">
			<li class="down disabled"><svg class="icon-minus"><use xlink:href="#icon-minus"></use></svg></li>
			<li><input class="qty-input min-1" min="0" type="tel" value="1" data-minqty="1" maxlength="3" pattern="\d*"></li>
			<li class="up"><svg class="icon-plus"><use xlink:href="#icon-plus"></use></svg></li>
		</ul>
	</div>
	<div class="col-xs-6 trim-left-5-lg {{if !$.isEmptyObject(cubStockStatus) && cubStockStatus.code == 'outOfStock'  && (${bdeUser} != true)}} disabled-productOutofStock{{/if}}">
		<div class="select-list">
			{{if !$.isEmptyObject(uomList)}}
				{{if uomList.length == 1}}
				<div class="select-single">{{= uomList[0].name}}</div>
				{{else}}
					<div data-value="" class="select-btn">{{= uomList[0].name}}</div>
					<ul class="select-items">
						{{each(i, uom) uomList}}
						<li data-value="{{= uom.code}}">{{= uom.name}}</li>
						{{/each}}
					</ul>
				{{/if}}
			{{/if}}
		</div>
</div>
</c:if>
</script>

<script id="resultsListItemProductAddToCartTemplate" type="text/x-jquery-tmpl">
<c:if test="${!isNAPGroup}">
	<div class="actions-container-for-ProductListComponent listAddPickupContainer clearfix">	
		<div class="ProductListComponent-ListAddToCartAction-Id">
			<div class="cart clearfix">
				<form method='post' id="addToCartForm{{= code}}" action="<c:url value="/" />cart/add" class="add_to_cart_form">
				<input type="hidden" name="productCodePost" value="{{= code}}" />
				<input type="hidden" name="productNamePost" value="{{= name}}" />
				<input type="hidden" id="qty" name="qty" class="qty" value="1" />
				<input type="hidden" name="unit" class="addToCartUnit" value="{{= !$.isEmptyObject(uomList) ? uomList[0].code : ''}}"/>
			<!-- OUT OF STOCK HANDLING -->
			<button type="submit"  id="addToCart{{= code}}" class="btn btn-primary btn-block addToCartButton"
			{{if !$.isEmptyObject(cubStockStatus) && cubStockStatus.code == 'outOfStock'}}
				disabled="disabled"><spring:theme code="basket.out.of.stock"/></button>
			{{else}}
				><spring:theme code="basket.add.to.basket"/></button>
			{{/if}}
			
			<div class="btn btn-primary btn-invert btn-block btn-changeDeliveryDate hidden" id="changeDeliveryDate{{= code}}" onclick={ACC.product.changedeliverydt()}>CHANGE DISPATCH DATE</div>

                <span class="hidden" id="addText"><spring:theme code="text.recommendations.add"/></span>
                <span class="hidden" id="addedText"><spring:theme code="text.recommendations.itemAdded"/></span>
                <c:if test="${bdeUser}">
                    <a class="addRecommendationText" id="addRecommendationText{{= code}}">
                        <svg class="icon-star-normal" id="recommendationStar">
                            <use xlink:href="#icon-star-add"></use>
                        </svg>
                        <span id="recommendationText"><spring:theme code="text.recommendations.add"/></span>
                    </a>
                </c:if>
			</form>
		</div>
	</div>
</div>
</c:if>
</script>


<script id="resultsListItemImageTemplate" type="text/x-jquery-tmpl">
	<div class="list-item-img">
		{{if $.isEmptyObject(images)}}
		<theme:image code="img.missingProductImage.thumbnail" alt="{{= name}}" title="{{= name}}"/>
		{{else}}
			{{if images.length == 1}}
			<img class="primaryImage" id="primaryImage" src="{{= images[0].url}}" title="{{= name}}" alt="{{= name}}"/>
			{{else}}
			<img class="primaryImage" id="primaryImage" src="{{= images[1].url}}" title="{{= name}}" alt="{{= name}}"/>
			{{/if}}
		{{/if}}
		{{if dealsFlag}}
			{{if dealsTitle}}
		<div id="dealBadge" class="badge badge-sm badge-red badge-postion" data-toggle="tooltip" data-container="body" data-original-title='
 {{each(i, title) dealsTitle}} <span class="deal-title"> <spring:theme code="text.product.title.deal"/>: </span>{{= title}} </p> {{/each}} '> <spring:theme code="text.product.title.deal" /></div>
			{{else}}
		<div id="dealBadge" class="badge badge-sm badge-red badge-postion" data-toggle="tooltip" data-container="body"> <spring:theme code="text.product.title.deal" /></div>
		{{/if}}

		{{else}}
		{{if newProductFlag}}
		<div class="badge badge-sm badge-green badge-postion"><spring:theme code="text.product.title.new"/></div>
		{{/if}}
		{{/if}}		
		
	</div>
</script>
