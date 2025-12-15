<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="subTotal" value="${cartData.subTotal}" />

<%--Removed and moved the contents to different jsp as per the requriement ACP-25 --%>


    <%-- <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
                <c:if test="${not empty savedCartCount and savedCartCount ne 0}">
                    <spring:url value="/my-account/saved-carts" var="listSavedCartUrl" htmlEscape="false"/>
                    <a href="${listSavedCartUrl}" class="save__cart--link cart__head--link">
                        <spring:theme code="saved.cart.total.number" arguments="${savedCartCount}"/>
                    </a>
                </c:if>
    </sec:authorize> --%>
    <cart:saveCart/>
    <cart:cartItems cartData="${cartData}"/>
    


<cart:ajaxCartTopTotalSection/>
<div class="row">
<div class="cart-actions--print checkoutcartsummary">
<div class="cart__actions border cart-bottom-btn">
    <div class="col-xs-12 col-sm-5 col-md-3 col-md-offset-9 col-sm-offset-7 ">        
    <div class="checkout-btn">                         
    <div class="bottom-checkout-btn">     
        <c:choose>
           <c:when test="${isCheckout eq true}">
                <button id="addCheckoutDetails" type="submit"
                        class="btn btn-primary btn-block btn--continue-checkout js-custom-checkout-button" <c:if test="${cartData.priceUpdated eq false || subTotal.value eq '0.0' || partialPriceError || cartData.outofStockItemAvailable}"><c:out value="disabled"/></c:if>>
                        <spring:theme code="checkout.summary.add.details" />

                 </button>
           </c:when>
           <c:otherwise>
                 <ycommerce:testId code="checkoutButton">
                        <%-- BONUS CHECKOUT BUTTON --%>
                        <c:choose>
                            <c:when test="${hasBonusStockProductOnly}">
                                <button id="checkoutButton"
                                       class="btn btn-primary btn-block btn--continue-checkout js-continue-checkout-button"
                                       data-checkout-url="${checkoutUrl}" <c:if test="${minOrderQtyCheck and cmsSite.uid eq 'sga'}"><c:out value="disabled"/></c:if>>     
                                       <c:choose>
                                            <c:when test="${cmsSite.uid eq 'sga'}">
                                                <spring:theme code="sga.cart.proceed.checkout"/>
                                            </c:when>
                                            <c:otherwise>
                                                <spring:theme code="cart.to.checkout"/>
                                            </c:otherwise>
                                       </c:choose>
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button id="checkoutButton"
                                       class="btn btn-primary btn-block btn--continue-checkout js-continue-checkout-button"
                                       data-checkout-url="${checkoutUrl}"  <c:if test="${cartData.priceUpdated eq false || subTotal.value eq '0.0' || minOrderQtyCheck || (cartData.allProductExcluded != null && cartData.allProductExcluded.booleanValue()) || cartData.outofStockItemAvailable}"><c:out value="disabled"/></c:if>>
                                       <c:choose>
                                            <c:when test="${cmsSite.uid eq 'sga'}">
                                                <spring:theme code="sga.cart.proceed.checkout"/>
                                            </c:when>
                                            <c:otherwise>
                                                <spring:theme code="cart.to.checkout"/>
                                            </c:otherwise>
                                                </c:choose>
                                </button>
                            </c:otherwise>
                        </c:choose>
                 </ycommerce:testId>
           </c:otherwise>
        </c:choose>
 </div>
</div>
</div> 
<div class="clearfix"></div> 
</div>
</div>
</div>

<c:if test="${cmsSite.uid eq 'sga' && not empty productRecommendation}">
	<%-- Switch recommendations from list to popup based on configuration value --%>
    <c:choose>
        <c:when test="${recommendationPopupEnabled}">
            <product:recommendedProductPopup/>
        </c:when>
        <c:otherwise>
            <product:recommendedProduct/>
        </c:otherwise>
    </c:choose>
</c:if>