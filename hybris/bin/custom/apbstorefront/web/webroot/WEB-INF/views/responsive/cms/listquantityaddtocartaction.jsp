<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:choose> 
  <c:when test="${product.stock.stockLevelStatus.code eq 'inStock' and empty product.stock.stockLevel}">
    <c:set var="maxQty" value="FORCE_IN_STOCK"/>
  </c:when>
  <c:otherwise>
    <c:set var="maxQty" value="${product.stock.stockLevel}"/>
  </c:otherwise>
</c:choose>

<c:set var="qtyMinus" value="1" />	
<div class="addtocart-component">  
    <div>
		<spring:htmlEscape defaultHtmlEscape="true" />
		<c:if test="${not product.multidimensional }">
		    <c:url value="/cart/add" var="addToCartUrl"/>
			<spring:url value="${product.url}/configuratorPage/{/configuratorType}" var="configureProductUrl" htmlEscape="false">
				<spring:param name="configuratorType" value="${configuratorType}" />
			</spring:url>
		
			<form:form id="addToCartForm${fn:escapeXml(product.code)}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
				<div class="container" style="margin: auto">
					<c:if test="${empty showAddToCart ? true : showAddToCart}">
				        <div class="qty-selector input-group js-qty-selector">
							<span class="input-group-btn" style>
							<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
								<button class="btn btn-default js-qty-selector-minus" style="display:inline" type="button" <c:if test="${qtyMinus <= 1}"><c:out value="disabled='disabled'"/></c:if>><span class="glyphicon glyphicon-minus" aria-hidden="true"></span></button>
							</span>
							<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
				            <input type="text" maxlength="3" style="display:inline; z-index:0" class="form-control js-qty-selector-input" size="1" value="${qtyMinus}" data-max="${maxQty}" data-min="1" name="pdpAddtoCartInput"  id="pdpAddtoCartInput" />
							<span class="input-group-btn">
							<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
								<button class="btn btn-default js-qty-selector-plus" style="display:inline" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
							</span>
							<input type="hidden" name="qty" value="5" id="qty" path="quantity" />
					        <ycommerce:testId code="addToCartButton">
					            <input type="hidden" name="productCodePost" value="${fn:escapeXml(product.code)}" id="addProductCode" />
					            <input type="hidden" name="productNamePost" value="${fn:escapeXml(product.name)}"/>
					            <input type="hidden" name="productPostPrice" value="${product.price.value}"/>
					
					            <c:choose>
					                <c:when test="${product.stock.stockLevelStatus.code eq 'outOfStock' }">
					                    <button type="submit" style="display:inline" class="btn btn-primary btn-block glyphicon glyphicon-shopping-cart list-add-to-cart addToCartButton"
					                            aria-disabled="true" disabled="disabled">
					                    </button>
					                </c:when>
					                <c:otherwise>
					                    &nbsp;<button type="submit" style="display:inline" class="btn btn-primary glyphicon glyphicon-shopping-cart js-enable-btn list-add-to-cart addToCartButton"
					                            disabled="disabled">
					                    </button>
					                </c:otherwise>
					            </c:choose>
					        </ycommerce:testId>
				        </div>
				    </c:if>
		        </div>
		    </form:form>
		
		    <form:form id="configureForm${fn:escapeXml(product.code)}" action="${configureProductUrl}" method="get" class="configure_form">
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
		    </form:form>
		</c:if>
    </div>
    
    
</div>

