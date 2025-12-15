<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.AbstractOrderData" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ attribute name="containerCSS" required="false" type="java.lang.String" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>



<spring:htmlEscape defaultHtmlEscape="true" />
<div class="js-cart-totals">
	<div class="row">
	    <div class="col-xs-8 col-sm-8 col-md-9 margin-left-summary">
	    <spring:theme code="basket.page.totals.subtotal"/>
            	<c:choose>
                	<c:when test="${cmsSite.uid eq 'sga'}">
						<span class="order-summary-line">
							<c:if test="${order.totalDiscounts.value > 0.0 or order.totalDiscounts.value < 0.0}">
								<br>(<spring:theme code="sga.cart.include.promotion.text"/>&nbsp;${order.totalDiscounts.formattedValue})
							</c:if>
							<c:if test="${order.orderCDL.value > 0.0 or order.orderCDL.value < 0.0}">
								<br>(<spring:theme code="sga.cart.include.cdl.text"/>&nbsp;${order.orderCDL.formattedValue})
							</c:if>
						</span>
                	</c:when>
                	<c:otherwise>
                		<spring:theme code="basket.page.totals.subtotalIncludes"/><format:price priceData="${order.portalWET}"/><spring:theme code="basket.page.totals.subtotalIncludes.text2"/>
                	</c:otherwise>
                </c:choose>
	    </div>
	    <div class="cart-totals-right text-right margin-right-summary"><ycommerce:testId code="Order_Totals_Subtotal"><format:price priceData="${order.subTotal}"/></ycommerce:testId></div>
    </div>
    
    <%-- <c:if test="${cmsSite.uid eq 'sga'}">
    <div class="row">
	    <div class="col-xs-8 col-sm-8 col-md-9 margin-left-summary"><spring:theme code="basket.page.totals.cdl"/></div>
		<div class="cart-totals-right text-right margin-right-summary"><format:price priceData="${order.orderCDL}"/></div>
	        
    </div>
    </c:if> --%>
    
	 <c:if test="${cmsSite.uid eq 'apb'}">
    <div class="row">
        <div class="col-xs-8 col-sm-8 col-md-9 margin-left-summary"><spring:theme code="basket.page.totals.netTax"/></div>
        <div class="cart-totals-right text-right margin-right-summary"><format:price priceData="${order.totalTax}"/></div>
    </div>
    </c:if>
    
    
    
    	<c:choose>
        	<c:when test="${cmsSite.uid ne 'sga'}">
	        	<div class="row">
		 			<div class="col-xs-8 col-sm-8 col-md-9 margin-left-summary"><spring:theme code="basket.page.totals.freight"/></div>
		    		<div class="cart-totals-right text-right margin-right-summary"><format:price priceData="${order.portalFreight}"/></div>
			    </div>
			     <div class="row">
	    			<div class="col-xs-8 col-sm-8 col-md-9 margin-left-summary" ><spring:theme code="basket.page.totals.surcharge"/></div>
	    			<div class="cart-totals-right text-right margin-right-summary"><format:price priceData="${order.deliveryCost}" displayFreeForZero="true"/></div>
				</div> 
        	</c:when>
        		<c:otherwise>
        			<div class="row">
	 					<div class="col-xs-8 col-sm-8 col-md-9 margin-left-summary"><spring:theme code="basket.page.totals.netTax"/></div>
        				<div class="cart-totals-right text-right margin-right-summary"><format:price priceData="${order.totalTax}"/></div>
     				</div>
        		</c:otherwise>
        		</c:choose>
    
	<div class="row order_summary_total">
	    <div class="col-xs-8 col-sm-8 col-md-9 margin-left-summary" >
		    <b>
		    <c:choose>
		    	<c:when test="${order.status == 'COMPLETED'}">
		    		<spring:theme code="basket.page.order.invoiced.total"/>
		    	</c:when>
		    	<c:otherwise>
		    		<spring:theme code="basket.page.order.total"/>
		    	</c:otherwise>
		    </c:choose></b><span id="seperater_tag">|</span>
	    	<span class="cart_top_item ">
                <c:choose>                        
					<c:when test="${order.totalUnitCount > 1 or fn:length(order.entries) == 0}">
						<spring:theme code="basket.page.totals.total.items" arguments="${order.totalUnitCount}"/>                        
					</c:when>                        
					<c:otherwise>                                                         
						<spring:theme code="basket.page.totals.total.items.one" arguments="${order.totalUnitCount}"/>                        
					</c:otherwise>                    
				</c:choose>
              </span>	
              <c:if test="${order.isPrepaid && isAddSurcharge}">                           
			  	<p class="cart-include-surcharge"><spring:theme code="cart.payment.include.surcharge"/></p>                    
			  </c:if> 
	    </div>
	    <div class="cart-totals-right text-right margin-right-summary">
			<span class="cart__top--amount cart-md-amount" >
                <b>
	                <c:choose>
	                    <c:when test="${showTax}">
	                       <format:price priceData="${order.totalPriceWithTax}"/>
	                    </c:when>
	                    <c:otherwise>
	                       <format:price priceData="${order.totalPriceWithTax}"/>
	                    </c:otherwise>
	                </c:choose>
                </b>
            </span>
		</div>
	</div>  
 
 </div>