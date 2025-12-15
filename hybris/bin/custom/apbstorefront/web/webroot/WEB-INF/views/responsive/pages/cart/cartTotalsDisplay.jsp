<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>



<%-- Verified that theres a pre-existing bug regarding the setting of showTax; created issue  --%>
<c:set var="allProductExcluded" value="false"/>
<%-- Condition to disable the button when all the products are unavailable --%>
<c:if test="${cartData.allProductExcluded != null && cartData.allProductExcluded.booleanValue()}">
	<c:set var="allProductExcluded" value="true"/>
</c:if>
<%---Updated the page as per the requirement of ACP-25  --%>
<div id="saveCartShowMsg"></div>
<div id="saveCartSpinner"></div>
<c:if test="${not isCheckout}">
	<div class="cart-header">
	    <div class="row">
	        <div class="col-xs-12 col-sm-5 no-padding">
	            <h1 class="cart-headline">
	                <spring:theme code="text.shopping.cart"/>       
	            </h1>
	            <div class="border"></div>
	            
	        </div>
	        
	    </div>
	</div>
</c:if>

<!--<%-- <c:if test="${not empty cartData.rootGroups}"> --%>-->
    <c:url value="/cart/checkout" var="checkoutUrl" scope="session"/>
    <c:url value="/quote/create" var="createQuoteUrl" scope="session"/>
    <c:url value="/" var="continueShoppingUrl" scope="session"/>
    <c:set var="showTax" value="true"/>
    <c:set var="subTotal" value="${cartData.subTotal}" />
					    <div class="row no-padding">
					        <div class="col-md-12 cart-actions--print checkoutcartsummary checkoutpage_summary">
					            <div class="cart__actions border">
					                <div class="row">
					                	<div class="no-padding-mobile col-xs-12 col-sm-7 col-md-6 col-lg-6">
									        <div class="cart-totals">
									            <cart:cartTotals cartData="${cartData}" showTax="true"/>
									            <cart:ajaxCartTotals/>
									        </div>
									     
					   					 </div>
					    <div class="col-sm-3 col-md-3 hidden-xs"></div>
                    	<div class="col-xs-12 col-sm-5 col-md-3 ">
                    	<div class="checkout-btn">
                    	 <div class="top-checkout-btn">
							<input type = "hidden" id="hasBonusStockProductOnly" value = "${hasBonusStockProductOnly}"/>
                            <c:choose>
                                <c:when test="${isCheckout eq true}">
                                    <c:choose>
                                        <c:when test="${cartData.priceUpdated eq false || subTotal.value eq '0.0' || empty deliveryAddresses || allProductExcluded || isExcluded || cartData.outofStockItemAvailable}">
                                            <input type = "hidden" id="checkoutPossible" value = "disabled"/>
                                        </c:when>
                                        <c:otherwise>
                                            <input type = "hidden" id="checkoutPossible" value = "enabled"/>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:set var="isCheckoutDisabled" value="${cartData.priceUpdated eq false || subTotal.value eq '0.0' || empty deliveryAddresses || isExcluded || (cartData.allProductExcluded != null && cartData.allProductExcluded.booleanValue()) || cartData.outofStockItemAvailable}"/>

                                    <button id="addCheckoutDetails" type="submit" class="btn btn-primary btn-block btn--continue-checkout js-custom-checkout-button" ${isCheckoutDisabled ? "disabled" : ""}>
                                        <spring:theme code="checkout.summary.add.details"/>
                                    </button>



                                    <div class="checkout-term-legal">
                                        <spring:url value="/termsAndLegal" var="getTermsAndConditionsUrl"/>
                                        <spring:theme code="checkout.summary.agree.terms.legal" arguments="${getTermsAndConditionsUrl}" />
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <ycommerce:testId code="checkoutButton">
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
                                                <button id="checkoutButton" class="btn btn-primary btn-block btn--continue-checkout js-continue-checkout-button" data-checkout-url="${checkoutUrl}" <c:if test="${cartData.priceUpdated eq false || subTotal.value eq '0.0' || partialPriceError || minOrderQtyCheck || allProductExcluded || cartData.outofStockItemAvailable}"><c:out value="disabled"/></c:if>>
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
                         <a class="continueShopping" href="${continueShoppingUrl}">
                           	 <span class=""><spring:theme code="cart.page.continue"/></span>
                       	</a>
                       
                       	</div>
                    </div>

                </div>
            </div>
        </div>
    </div>
<!--
    <%-- <div class="row">
        <cart:exportCart/>

        
    </div> --%>
   <%--  </c:if> --%>
-->

<!--
<%-- <div class="row">
    <div class="col-xs-12 col-md-5 col-lg-6">
        <div class="cart-voucher">
            <cart:cartVoucher cartData="${cartData}"/>
        </div>
    </div>
</div>
 --%>
-->

<cart:ajaxCartTopTotalSection/>