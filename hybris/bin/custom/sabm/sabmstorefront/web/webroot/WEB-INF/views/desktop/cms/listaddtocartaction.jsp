<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="buttonType">submit</c:set>
<c:if test="${!isNAPGroup}">
<div class="cart clearfix">
	<c:url value="/cart/add" var="addToCartUrl" />

	<ycommerce:testId code="searchPage_addToCart_button_${product.code}">
		<form:form id="addToCartForm${product.code}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
			<input type="hidden" name="productCodePost" value="${product.code}" />
			<input type="hidden" name="productNamePost" value="${product.name}" />
			<input type="hidden" name="productPostPrice" value="${product.price.value}" />
			<input type="hidden" name="qty" class="qty" value="1">
			<input type="hidden" name="unit" class="addToCartUnit" value="">
			<input type="hidden" name="listOriginPos" value="${productListPosition}" />
            <c:choose>
                <c:when test="${!isProductPackTypeAllowed}">
                    <div class="btn btn-primary btn-invert btn-block btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>
                </c:when>
                <c:otherwise>
                	<button type="${buttonType}"
				class="btn btn-primary btn-block addToCartButton bde-view-only <c:if test="
			${not product.purchasable || product.stock.stockLevelStatus.code eq 'outOfStock' || product.cubStockStatus eq 'outOfStock'}">out-of-stock</c:if>"
				<c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' || product.cubStockStatus eq 'outOfStock'}"> disabled="disabled" aria-disabled="true"</c:if>>
				<c:choose>
				<c:when test="${product.cubStockStatus eq 'outOfStock'}">
					<spring:theme code="basket.out.of.stock"/>
				</c:when>
				<c:otherwise>
					<spring:theme code="basket.add.to.basket"/>
				</c:otherwise>
				</c:choose>
				</button>
                </c:otherwise>
            </c:choose>

		   <c:if test="${bdeUser}">
                <div class="text-center">

                    <span class="hidden" id="addText"><spring:theme code="text.recommendations.add"/></span>
                    <span class="hidden" id="addedText"><spring:theme code="text.recommendations.itemAdded"/></span>
                    <a class="addRecommendationText addRecommendationAction">
                        <svg class="icon-star-normal" id="recommendationStar">
                            <use xlink:href="#icon-star-add"></use>
                        </svg>
                        <span id="recommendationText"><spring:theme code="text.recommendations.add"/></span>
                    </a>
                </div>
             </c:if>
		</form:form>

	</ycommerce:testId>
</div>
</c:if>
