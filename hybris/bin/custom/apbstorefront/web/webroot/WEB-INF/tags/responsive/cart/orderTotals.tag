<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="quote" tagdir="/WEB-INF/tags/responsive/quote" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>


<spring:htmlEscape defaultHtmlEscape="true" />
<!-- Updated the page for cart page customization ACP-25-->
<div class="js-cart-totals">
	<div class="row">
	    <div class="col-xs-8 col-md-8 margin-left-summary">
	    	<spring:theme code="basket.page.totals.subtotal"/>
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
	    <div class="cart-totals-right text-right margin-right-summary"><ycommerce:testId code="Order_Totals_Subtotal"><format:price priceData="${cartData.subTotal}"/></ycommerce:testId></div>
    </div>
    
   <%--  <c:if test="${cmsSite.uid eq 'sga'}">
	    <c:if test="${cartData.net &&  showTax}">
		    <div class="row">
		        <div class="col-xs-8 col-md-8 margin-left-summary"><spring:theme code="sga.basket.page.totals.container.deposit.levy"/></div>
		        <div class="cart-totals-right text-right margin-right-summary"><format:price priceData="${cartData.orderCDL}"/></div>
	        </div>
	    </c:if>
    </c:if> --%>
    
	<c:if test="${cartData.net &&  showTax}">
	    <div class="row">
	        <div class="col-xs-8 col-md-8 margin-left-summary"><spring:theme code="basket.page.totals.netTax"/></div>
	        <div class="cart-totals-right text-right margin-right-summary"><format:price priceData="${cartData.totalTax}"/></div>
        </div>
    </c:if>
    
   <c:if test="${cmsSite.uid ne 'sga'}">
    <div class="row">
	 	<div class="col-xs-8 col-md-8 margin-left-summary"><spring:theme code="basket.page.totals.freight"/></div>
	    <div class="cart-totals-right text-right margin-right-summary"><format:price priceData="${cartData.portalFreight}"/></div>
    </div>
    <div class="row">
	    <div class="col-xs-8 col-md-8 margin-left-summary" ><spring:theme code="basket.page.totals.surcharge"/></div>
	    <div class="cart-totals-right text-right margin-right-summary"><format:price priceData="${cartData.deliveryCost}" displayFreeForZero="true"/></div>
	</div> 
	</c:if>
	<div class="row cart__top--totals">
	    <div class="col-xs-8 col-md-8 margin-left-summary" >
		    <b><spring:theme code="basket.page.order.total"/></b>
	    	<span class="cart_top_item ">
                <c:choose>
			         <c:when test="${cartData.totalUnitCount > 1 or fn:length(cartData.entries) == 0}">
			            <spring:theme code="basket.page.totals.total.items" arguments="${cartData.totalUnitCount}"/>
			         </c:when>
			         <c:otherwise>
			            <spring:theme code="basket.page.totals.total.items.one" arguments="${cartData.totalUnitCount}"/>
			         </c:otherwise>
			     </c:choose>
              </span>               
				<c:if test="${cartData.isPrepaid && isAddSurcharge}">                          
						 <p class="cart-include-surcharge"><spring:theme code="cart.payment.include.surcharge"/></p>                    
				</c:if>                      
					
             
	    </div>
	
	    <div class="cart-totals-right text-right margin-right-summary">
			<span class="cart__top--amount cart-md-amount" >
                <b>
	                <c:choose>
	                    <c:when test="${showTax}">
	                       <format:price priceData="${cartData.totalPriceWithTax}"/>
	                    </c:when>
	                    <c:otherwise>
	                       <format:price priceData="${cartData.totalPrice}"/>
	                    </c:otherwise>
	                </c:choose>
                </b>
            </span>
		</div>
	</div>  
 
 </div>