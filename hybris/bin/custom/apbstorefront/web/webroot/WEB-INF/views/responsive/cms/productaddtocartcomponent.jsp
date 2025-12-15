<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

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

<div class="addtocart-component">
             <c:if test="${empty showAddToCart ? true : showAddToCart}">
             <div class="qty-selector input-group js-qty-selector">
                    <span class="input-group-btn">
                           <button class="btn btn-default js-qty-selector-minus" type="button" <c:if test="${qtyMinus <= 1}"><c:out value="disabled='disabled'"/></c:if> ><span class="glyphicon glyphicon-minus" aria-hidden="true" ></span></button>
                    </span>
                           <input type="text" maxlength="3" class="form-control js-qty-selector-input" size="1" value="${qtyMinus}" data-max="${maxQty}" data-min="${qtyMinus}" name="pdpAddtoCartInput"  id="pdpAddtoCartInput" />
                    <span class="input-group-btn">
                           <button class="btn btn-default js-qty-selector-plus" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
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
