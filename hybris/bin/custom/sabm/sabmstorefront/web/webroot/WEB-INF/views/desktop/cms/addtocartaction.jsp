<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="templatesOrder" tagdir="/WEB-INF/tags/desktop/templatesOrder"%>
<c:url value="${url}" var="addToCartUrl"/>


<form:form method="post" id="addToCartForm${product.code}" class="add_to_cart_form" action="${addToCartUrl}">

	<input type="hidden" name="productPurchable" id="productPurchable" value="${product.purchasable}"/>
	<input type="hidden" name="stockStatus" id="stockStatus" value="${product.cubStockStatus}"/>

	<c:if test="${product.purchasable}">
		<input type="hidden"  name="qty" class="qty" value="1">
		<input type="hidden" name="unit" class="addToCartUnit" value="">
	</c:if>
	<input type="hidden" name="productCodePost" value="${product.code}"/>
	<input type="hidden" class="addToCartUnit">
	<input type="hidden" name="listOriginPos" value="1"/>
	<div class="row summary-btns">
		<div class="col-md-6 offset-bottom-xsmall <c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>" id="save-to-template-pdp"><a href="#save-to-template" class="btn btn-secondary btn-block"><spring:theme code="basket.save.to.template" /></a></div>

    <c:choose>
        <c:when test="${!isProductPackTypeAllowed}">
            <div class="col-md-6"><button class="btn btn-primary btn-block bde-view-only btn-invert btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></button></div>
        </c:when>
        <c:otherwise>
            <div class="col-md-6"><button id="addToCartButton" type="submit" disabled="disabled" class="btn btn-primary btn-block bde-view-only"><spring:theme code="basket.add.to.basket"/></button></div>
        </c:otherwise>
    </c:choose>
	</div>
</form:form>
<templatesOrder:templateOrderPopup/>
