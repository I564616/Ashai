<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="count" required="true" type="java.lang.String"%>
<%@ attribute name="productListName" required="false" type="java.lang.String"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<div class="addtocart-qty cartRecommendations productImpressionTag recommendationsTag" ng-repeat="deal in deals" ng-init="dealInit(deal,true)">
	<input type="hidden" class="dealCurrentUrl" value="<c:url value="/cart/add/deal"/>">
	<input type="hidden" class="recommendationId" value="{{deal.recommendationId}}"/>
	<input type="hidden" class="recommendationType" value="{{deal.recommendationType}}"/>

    <div class="product-pick">
        <div ng-class="!deal.isInDeliveryPackType ? 'disabled-productPackTypeNotAllowed':''">
        <div class="">
            <h3 class="product-pick-title">
                <span>
                    <spring:theme code="text.recommendations.recommendedBy"/>&nbsp;{{deal.recommendedBy}}
                </span>
            </h3>
            <hr class="hr-title"/>
            <br/>

        </div>
        <div class="card-content">
            <%--IMAGE--%>
            <div class="col-xs-4 col-sm-4 col-md-3">
                <div id="product-image">
                    <div class="list-item-img">
                    	<c:set var="prodName" value="{{deal.firstBaseProduct.title}}"/>
         				<c:set var="brand" value="{{deal.firstBaseProduct.brand}}"/>
         				<c:set var="category" value="{{deal.firstBaseProduct.categories[0].name}}"/>
                        <img ng-src="{{deal.dealImage}}" alt="{{deal.dealImageTitle}}" class="js-track-product-link" 
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
									data-actionfield="${productListName}"
									data-list="${productListName}"
            						data-dealsflag="true">
                        <div id="dealBadge" class="badge badge-small badge-postion badge-red">
                            <spring:theme code="text.product.title.deal"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-9 col-sm-8 col-xs-8 trim-right">
                <div class="product-details-container">
                    <div class="product-pick-description">
                        <h3 class="col-xs-12 dealTitle" data-ng-bind-html="getHtml(deal.title)"></h3>
                        <div class="col-xs-12 low-stock-status-label" ng-if="deal.firstBaseProduct.cubStockStatus.code == 'lowStock'"><spring:theme code="product.page.stockstatus.low"/></div>
                    </div>
                    
                </div>
            </div>
        </div>
        </div>
        <div class="product-footer">
            <div class="actions-separator">
                <hr />
            </div>
            <c:set var="buttonType">submit</c:set>
            <div class="recommendation-cart-actions">
                <div ng-if="!deal.isInDeliveryPackType && !bdeUser" class="btn btn-primary btn-invert btn-changeDeliveryDate btn-carousel"><spring:theme code="basket.change.delivery.date"/></div>
                <div ng-if="deal.isInDeliveryPackType || bdeUser">
                    <div class="title">
                        <spring:theme code="text.recommendations.cartpage.question"/>
                    </div>
                    <span type="${buttonType}" id="recommendation-addToOrder-{{deal.code}}" class="inline bde-view-only" ng-disabled="!dealValid(deal) || pageInactive || deal.remainingQty <= 0 || deal.remainingValue <= 0 || bdeViewOnly" ng-click="addToCart(deal, 'deals')">
                        <spring:theme code="text.recommendations.cartpage.response.yes"/>
                    </span>
                    <span class="vertical-bar">&nbsp;|&nbsp;</span>
                    <span class="inline deleteRecommendation" onclick="rm.tagManager.trackRecommendation('remove', RcmndRejectedCart | ${fn:escapeXml(name)}')">
                        <spring:theme code="text.recommendations.cartpage.response.no"/>
                    </span>
                </div>
            </div>
        </div>
    </div>
</div>
