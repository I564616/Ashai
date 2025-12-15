<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="count" required="true" type="java.lang.String"%>
<%@ attribute name="productListName" required="false" type="java.lang.String"%>
<%@ attribute name="recommendProduct" required="true" type="com.sabmiller.facades.recommendation.data.RecommendationProductData"%>
<%@ attribute name="recommendedBy" required="true" type="java.lang.String"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ attribute name="isInPackType" required="false" type="java.lang.Boolean"%>

<c:set var="product" value="${recommendProduct.product}" />

<c:url var="productUrl" value="${product.url}"/>
<c:set var="name" value="${product.name}"/>
<c:set var="packConfiguration" value="${product.packConfiguration}"/>
<c:set var="code" value="${product.code}"/>
<c:set var="stockLevelStatusCode" value="${product.stock.stockLevelStatus.code}"/>
<c:set var="purchasable" value="${product.purchasable}"/>

<c:set var="priceCurrencyIso" value="${product.price.currencyIso}"/>
<c:set var="priceValue" value="${product.price.value}"/>
<c:set var="brand" value="${product.brand}"/>
<c:set var="categories" value="${product.categories}"/>
<c:set var="unit" value="${product.unit}"/>
<c:set var="dealsFlag" value="${product.dealsFlag}"/>

<div class="product-pick">
    <div class="<c:if test="${!isInPackType}"> disabled-productPackTypeNotAllowed</c:if>">
        <h3 class="product-pick-title">
            <span>
                <spring:theme code="text.recommendations.recommendedBy"/>&nbsp;${recommendedBy}
            </span>
            ${title}

        </h3>
        <hr class="hr-title"/>
        <br/>

        <div class="card-content">
            <div class="col-xs-4 col-md-3 col-sm-4 trim-left-5">
                <div id="product-image">
                    <a href="${productUrl}" class="js-track-product-link"
                        data-currencycode="${priceCurrencyIso}"
                        data-name="${fn:escapeXml(name)}"
                        data-id="${code}"
                        data-price="${priceValue}"
                        data-brand="${fn:escapeXml(brand)}"
                        data-category=<c:choose>
                                        <c:when test="${not empty categories}">
                                            '${fn:escapeXml(categories[fn:length(categories) - 1].name)}'
                                        </c:when>
                                        <c:otherwise>
                                            ''
                                        </c:otherwise>
                                </c:choose>
                        data-variant="${unit}"
                        data-position="${count}"
                        data-url="${productUrl}"
                        data-actionfield="${productListName}"
                        data-list="${productListName}"
                        data-sku="${product.leadSkuId}"
                        data-qty="${recommendProduct.quantity}"
                        data-dealsflag="${dealsFlag}"
                        data-isSuggested="true"
				        data-isPromotion="${product.dealsFlag}"
				        data-isReOrder="false">

                        <product:productPrimaryImage product="${product}" format="product" isCart="true" />
                        </a>
                </div>
            </div>
            <div class="col-md-9 col-sm-8 col-xs-8 trim-right">
                <div class="product-pick-description">
                    <div class="">
                        <a href="${productUrl}" class="js-track-product-link"
                            data-currencycode="${priceCurrencyIso}"
                            data-name="${fn:escapeXml(name)}"
                            data-id="${code}"
                            data-price="${priceValue}"
                            data-brand="${fn:escapeXml(brand)}"
                            data-category=<c:choose>
                                        <c:when test="${not empty categories}">
                                            '${fn:escapeXml(categories[fn:length(categories) - 1].name)}'
                                        </c:when>
                                        <c:otherwise>
                                            ''
                                        </c:otherwise>
                                </c:choose>
                            data-variant="${unit}"
                            data-position="${count}"
                            data-url="${productUrl}"
                            data-actionfield="${productListName}"
                            data-list="${productListName}"
                            data-dealsflag="${dealsFlag}">
                            <h3 class="clamp-2">${name}</h3>
                        </a>
                        <div class="h3 h3-subheader clamp-1 product-pick-details">${empty packConfiguration ? '&nbsp;' : packConfiguration}</div>
                        <c:if test="${recommendProduct.product.cubStockStatus.code == 'lowStock'}">
                        	<div class="low-stock-status-label"><spring:theme code="product.page.stockstatus.low"/></div>
                        </c:if>
                        <div class="qty-unit">${recommendProduct.quantity}&nbsp;${recommendProduct.quantity > 1 ? recommendProduct.unit.pluralName : recommendProduct.unit.name}</div> 
                        <input type="hidden" class="qty-input" value="${recommendProduct.quantity}" />
                    	
                    </div>

                </div>
            </div>
        </div>
	</div>
	<div class="product-footer">
		<div class="actions-separator">
			<hr/>
			</div>
			<c:set var="buttonType">submit</c:set>
			<c:url value="/cart/add" var="addToCartUrl" />
			<div class="recommendation-cart-actions">
				<input type="hidden" name="productCodePost" value="${code}" />
				<input type="hidden" name="qty" value="${recommendProduct.quantity}" />
				<input type="hidden" name="unit" value="${recommendProduct.unit.code}"/>
				<input type="hidden" name="listOriginPos" value="${count}"/>

                <c:choose>
                    <c:when test="${!isInPackType}">
                        <div class="btn btn-primary btn-invert btn-changeDeliveryDate btn-carousel"><spring:theme code="basket.change.delivery.date"/></div>
                    </c:when>
                    <c:otherwise>
                      <div class="title">
                          <spring:theme code="text.recommendations.cartpage.question"/>
                      </div>
                      <c:choose>
                          <c:when test="${not purchasable || stockLevelStatusCode eq 'outOfStock' }">
                              <spring:theme code="text.recommendations.cartpage.outofStock"/>
                          </c:when>
                          <c:otherwise>
                          	<form action="" class="add_to_cart_form" style="display: inline">
                      	  	  <span type="${buttonType}" class="recommendation-addToOrder inline bde-view-only">
                                  <spring:theme code="text.recommendations.cartpage.response.yes"/>
                              </span>
                              </form>
                          </c:otherwise>
                      </c:choose>
                      <span class="vertical-bar">&nbsp;|&nbsp;</span>
                      <span class="inline deleteRecommendation" onclick="rm.tagManager.trackRecommendation('remove', 'RcmndRejectedCart | ${fn:escapeXml(name)}')">
                          <spring:theme code="text.recommendations.cartpage.response.no"/>
                      </span>
                    </c:otherwise>
                </c:choose>

		</div>
    </div>
</div>
