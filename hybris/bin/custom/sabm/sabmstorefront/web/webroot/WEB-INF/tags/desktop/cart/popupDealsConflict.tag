<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<div id="dealsConflictPopup" class="deals-conflict-popup mfp-hide">
	<h2 class="h1 offset-bottom-small">Choose your preferred deal</h2>
	<p class="offset-bottom-small">
		<c:choose>
			<c:when test="${not empty secondConflict}">
				<spring:theme code="text.car.modal.pop.text.title2" />
			</c:when>
			<c:otherwise>
				<spring:theme code="text.car.modal.pop.text.title1" />
			</c:otherwise>
		</c:choose>
	</p>
	<c:url value="/cart/selectConflictDeal" var="conflictDealUrl"/>
	<form name="dealsConflict" action="${conflictDealUrl}" method="POST">
		<ul>
			<li class="offset-bottom-small" ng-repeat="deal in dealsConflictData" ng-init="dealInit(deal)">
				<div class="deal-option productImpressionTag dealsImpressionTag">
					<div class="radio">
                        <c:set var="prodName" value="{{deal.firstBaseProduct.title}}"/>
                        <c:set var="brand" value="{{deal.firstBaseProduct.brand}}"/>
                        <c:set var="category" value="{{deal.firstBaseProduct.categories[0].name}}"/>
						<input id="dealConflict{{$index}}" type="radio" ng-model="selectedDeal.selected" ng-value="deal.code" class="js-track-product-link"
							   data-currencycode="{{deal.firstBaseProduct.price.currencyIso}}"
                               data-name="${fn:escapeXml(prodName)}"
                               data-productcode="{{deal.firstBaseProduct.productCode}}"
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
                               data-actionfield="${fn:escapeXml(requestOrigin)}/Review Your Order/Conflicting Deals Modal"
                               data-list="${fn:escapeXml(requestOrigin)}/Review Your Order/Conflicting Deals Modal"
                               data-dealsflag="true">
						<label for="dealConflict{{$index}}">
						    <div class="deal-title" data-ng-bind-html="getHtml(deal.title)"></div>
						</label>
					</div>
				</div>
			</li>
		</ul>
		<input type="hidden" name="CSRFToken" value="${CSRFToken}">
		<input type="hidden" name="dealCode" ng-value="selectedDeal.selected">
		<button class="btn btn-primary margin-top-10 btn-apply-deal"><spring:theme code="text.button.apply.actions.deal" /></button>
	</form>
</div>