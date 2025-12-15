<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<spring:url value="/cart/miniCart/{/totalDisplay}" var="refreshMiniCartUrl" htmlEscape="false">
	<spring:param name="totalDisplay"  value="${totalDisplay}"/>
</spring:url>
<spring:url value="/cart/rollover/{/componentUid}" var="rolloverPopupUrl" htmlEscape="false">
	<spring:param name="componentUid"  value="${component.uid}"/>
</spring:url>
<c:url value="/cart" var="cartUrl"/>
<div class="nav-cart">  
	<a 	href="javascript:void(0)" data-href-url="${cartUrl}"
		class="mini-cart-link js-mini-cart-link"
		data-mini-cart-url="${rolloverPopupUrl}"
		data-mini-cart-refresh-url="${refreshMiniCartUrl}"
		data-mini-cart-name="<spring:theme code="text.cart"/>"
		data-mini-cart-empty-name="<spring:theme code="popup.cart.empty"/>"
		data-mini-cart-items-text="<spring:theme code="basket.items"/>"
		>
        <span class="">
            <span id="navigationMyCartLabel">CART</span>
            <ycommerce:testId code="miniCart_items_label">
            <span class="mini-cart-price js-mini-cart-price hidden-xs">
                <c:if test="${totalDisplay == 'TOTAL'}">
                        <format:price priceData="${totalPrice}" />
                    </c:if>
                    <c:if test="${totalDisplay == 'SUBTOTAL'}">
                        <c:choose>
							<c:when test="${cmsSite.uid eq 'sga' and !wasCheckoutInterfce and minicartSubTotal ne null}">
                      			<format:price priceData="${minicartSubTotal}" />
                      		</c:when>
                      		<c:otherwise>
                      			<format:price priceData="${subTotal}" />
                      		</c:otherwise>
                      	</c:choose>
                    </c:if>
                    
                    <c:if test="${totalDisplay == 'TOTAL_WITHOUT_DELIVERY'}">
                        <format:price priceData="${totalNoDelivery}" />
                    </c:if>
            </span>
            <span class="mini-cart-count js-mini-cart-count">
                <span class="nav-items-total">
                    ${totalItems lt 100 ? totalItems : "99+"}
                    <span class="items-desktop hidden-md hidden-lg hidden-sm hidden-xs">&nbsp;<spring:theme code="basket.items"/></span>
                </span>
            </span>
            </ycommerce:testId>
        </span>

	</a>
</div>
<div class="mini-cart-container js-mini-cart-container"></div>