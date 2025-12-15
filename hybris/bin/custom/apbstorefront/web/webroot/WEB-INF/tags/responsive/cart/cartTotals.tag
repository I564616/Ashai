<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="quote" tagdir="/WEB-INF/tags/responsive/quote" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<c:url value="/cart/checkout" var="checkoutUrl" scope="session" />
<c:url value="/quote/create" var="createQuoteUrl" scope="session" />
<c:set var="showTax" value="true" />
<!-- Updated the page for cart page customization ACP-25-->
<div class="js-cart-totals">
	<div class="row">
		<div class="col-xs-8 col-sm-9 col-md-8 margin-left-summary">
			<spring:theme code="basket.page.totals.subtotal" />
			<span class="cart-include">
				<c:choose>
					<c:when test="${cmsSite.uid eq 'sga'}">
					<c:if test="${cartData.totalDiscounts.value > 0.0 or cartData.totalDiscounts.value < 0.0}">
						<br>(<spring:theme code="sga.cart.include.promotion.text"/>&nbsp;${cartData.totalDiscounts.formattedValue})
					</c:if>
					<c:if test="${cartData.orderCDL.value > 0.0 or cartData.orderCDL.value < 0.0}">
						<br>(<spring:theme code="sga.cart.include.cdl.text"/>&nbsp;${cartData.orderCDL.formattedValue})
					</c:if>
					</c:when>
					<c:otherwise>
						<spring:theme code="basket.page.totals.subtotalIncludes"/><format:price priceData="${cartData.portalWET}"/><spring:theme code="basket.page.totals.subtotalIncludes.text2"/>
					</c:otherwise>
				</c:choose>
			</span>
		</div>
		
		<div class="cart-totals-right text-left">
			<ycommerce:testId code="Order_Totals_Subtotal">
				<format:price priceData="${cartData.subTotal}" showNAIfPriceError="true" />
			</ycommerce:testId>
		</div>
	</div>
	
	<c:if test="${cartData.net &&  showTax}">
		<div class="row">
			<div class="col-xs-8 col-sm-9 col-md-8 margin-left-summary">
				<spring:theme code="basket.page.totals.netTax" />
			</div>
			<div class="cart-totals-right text-left">
				<c:choose>
			         <c:when test="${cmsSite.uid eq 'sga'}">
			            <format:price priceData="${cartData.portalGST}" showNAIfPriceError="true" />
			         </c:when>
			         <c:otherwise>
			             <format:price priceData="${cartData.totalTax}" showNAIfPriceError="true" />
			         </c:otherwise>
			     </c:choose>
			</div>
		</div>
	</c:if>
	
	<c:if test="${cmsSite.uid ne 'sga'}">		<!--	Delivery Surcharge and Freight are not need for SGA.		-->
		<div class="row">
			<div class="col-xs-8 col-sm-9 col-md-8 margin-left-summary">
				<spring:theme code="basket.page.totals.freight" />
			</div>
			<div class="cart-totals-right text-left">
				<format:price priceData="${cartData.portalFreight}" showNAIfPriceError="true" />
			</div>
		</div>
	
		<div class="row">
			<div class="col-xs-8 col-sm-9 col-md-8 margin-left-summary">
				<spring:theme code="basket.page.totals.surcharge" />

			</div>
			<div class="cart-totals-right text-left">
				<format:price priceData="${cartData.deliveryCost}" displayFreeForZero="true" />
			</div>
		</div>
	</c:if>


	<div class="row js-cart-top-totals cart__top--totals">
		<div class="col-xs-8 col-sm-9 col-md-8 margin-left-summary"><b><spring:theme code="basket.page.order.total"/></b>
			<span class="cart_top_item">
			     <c:choose>
			          <c:when test="${cartData.totalUnitCount > 1 or fn:length(cartData.entries) == 0}">
			            <spring:theme code="basket.page.totals.total.items" arguments="${cartData.totalUnitCount}"/>
			         </c:when>
			         <c:otherwise>
			            <spring:theme code="basket.page.totals.total.items.one" arguments="${cartData.totalUnitCount}"/>
			         </c:otherwise>
			     </c:choose>
				</span>
			<p class="cart-include-surcharge hide hidden">
				<spring:theme code="cart.payment.include.surcharge" />
			</p>
		</div>
		<div class="cart-totals-right text-left">
			<ycommerce:testId code="cart_totalPrice_label">
				<span class="cart__top--amount cart-md-amount">
	                        <c:choose>
	                            <c:when test="${showTax}">
	                               <format:price priceData="${cartData.totalPriceWithTax}" showNAIfPriceError="true"/>
	                            </c:when>
	                            <c:otherwise>
	                               <format:price priceData="${cartData.totalPrice}" showNAIfPriceError="true"/>
	                            </c:otherwise>
	                        </c:choose>
	                    </span>
			</ycommerce:testId>
		</div>
	</div>
 </div>