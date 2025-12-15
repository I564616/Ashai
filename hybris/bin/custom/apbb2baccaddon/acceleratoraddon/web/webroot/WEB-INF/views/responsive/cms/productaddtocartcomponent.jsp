<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="isForceInStock" value="${product.stock.stockLevelStatus.code eq 'inStock' and empty product.stock.stockLevel}"/>
<c:choose> 
  <c:when test="${isForceInStock}">
    <c:set var="maxQty" value="FORCE_IN_STOCK"/>
  </c:when>
  <c:otherwise>
    <c:set var="maxQty" value="${product.stock.stockLevel}"/>
  </c:otherwise>
</c:choose>

<c:set var="qtyMinus" value="1" />
<c:url value="/cart/add?action=addBonus" var="addBonusToCartUrl"/>
<c:set var="isProductUnavailable" value="${false}"/>
<c:if test="${(disablePdp !=null && disablePdp.booleanValue()) || product.isExcluded}" >
	<c:set var="isProductUnavailable" value="${true}"/>
</c:if>

<div class="addtocart-component">
             <c:if test="${empty showAddToCart ? true : showAddToCart}">
             <div class="qty-selector input-group js-qty-selector">
                    <span class="input-group-btn">
                           <button class="btn btn-default js-qty-selector-minus" type="button" <c:if test="${qtyMinus <= 0 || product.licenseRequired || product.stock.stockLevelStatus eq 'outOfStock' || isProductUnavailable}"><c:out value="disabled"/></c:if> ><span class="glyphicon glyphicon-minus" aria-hidden="true" ></span></button>
                    </span>
                           <input type="text" maxlength="3" class="form-control js-qty-selector-input" size="1" value="${qtyMinus}" data-max="${maxQty}" data-min="${qtyMinus}" name="pdpAddtoCartInput"  id="pdpAddtoCartInput" <c:if test="${product.licenseRequired || product.stock.stockLevelStatus eq 'outOfStock' || isProductUnavailable}"><c:out value="disabled"/></c:if> />
                    <span class="input-group-btn">
                           <button class="btn btn-default js-qty-selector-plus" type="button" <c:if test="${product.licenseRequired || product.stock.stockLevelStatus eq 'outOfStock' || isProductUnavailable}"><c:out value="disabled"/></c:if>><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
                    </span>
                    <div class="actions">
		        <c:if test="${multiDimensionalProduct}" >
		                <c:url value="${product.url}/orderForm" var="productOrderFormUrl"/>
		                <a href="${productOrderFormUrl}" class="btn btn-default btn-block btn-icon js-add-to-cart glyphicon-list-alt">
		                    <spring:theme code="order.form" />
		                </a>
		                
		        </c:if>
        <action:actions element="div"  parentComponent="${component}"/>
             </div>
             </div>
             </c:if>
             
             
</div>
<form id="addBonusToCartForm${fn:escapeXml(product.code)}" action="${addBonusToCartUrl}" method="post" class="add_bonus_to_cart_form bonus-form">
	<c:if test="${cmsSite.uid eq 'apb' and asmMode}">
		<div class="container">
		<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
			<input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty js-bonus-qty-input js-qty-selector-input" value="1">
			<ycommerce:testId code="addBonusToCartButton">
				<input type="hidden" name="productCodePost" value="${fn:escapeXml(product.code)}" id="addProductCode" />
				<input type="hidden" name="productNamePost" value="${fn:escapeXml(product.name)}"/>
				<input type="hidden" name ="${CSRFToken.parameterName}" value="${CSRFToken.token}"/>
				<div class="bonus-stock-plp bonus-stock-pdp" id="${fn:escapeXml(product.code)}">
					<button class="btn btn-vd-primary btn-primary" style="width: 50px;" <c:if test="${product.allowedBonusQty lt 1}"><c:out value="disabled='disabled'"/></c:if>><spring:theme code="order.entry.bonus.text"/></button>
				</div>
			</ycommerce:testId>
		</sec:authorize>
	</div>
	</c:if>
</form>