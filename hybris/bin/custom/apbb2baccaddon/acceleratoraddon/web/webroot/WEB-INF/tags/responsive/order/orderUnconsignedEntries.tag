<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<div class="well well-quinary well-xs">
    <!--    Payment     -->
    <div class="row no-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="text.account.orderDetails.payment" />
        </div>
        <div class="col-md-12 order-ship-to no-padding">
        <c:choose>
                        <c:when test="${orderData.isPrepaid}">
                        	 <div class="value-title">
	                        	  <span><spring:theme code="text.account.orderDetails.payment.method.label" /></span>
	                        	  <span><spring:theme code="text.account.orderDetails.by.credit" /></span>
                        	  </div>   
                        	 <div class="value-order">
	                        	  <span><img src="${commonResourcePath}/images/${fn:toLowerCase(orderData.cardData.cardType)}.png"/> ${orderData.cardData.cardType} ending in  ${orderData.cardData.cardNumber} (${orderData.cardData.cardExpiry})</span>
                        	  </div>  
                        </c:when>
                        <c:otherwise>
                        	  <div class="value-title">
	                        	  <span><spring:theme code="text.account.orderDetails.payment.method.label" /></span>
	                        	  
	                        	  <c:choose>
	                        	  	<c:when test="${cmsSite.uid eq 'sga' and orderData.deliveryOrder}">
	                        	  		<span><spring:theme code="text.account.orderDetails.on.delivery" /></span>
	                        	  	</c:when>
	                        	  	<c:otherwise>
	                        	  		<span><spring:theme code="text.account.orderDetails.on.account" /></span>
	                        	  	</c:otherwise>
	                        	  </c:choose>
                        	  </div>  
                        	  <div class="value-order">
	                        	  <span><spring:theme code="text.account.orderDetails.POnumber" /></span>
	                        	  <span>
	                        	  	<c:choose>
	               					 <c:when test="${empty orderData.poNumber}">
	                   				 	<i><spring:theme code="text.account.orderDetails.no.POnumber" /></i>
	                				</c:when>
	               					 <c:otherwise>
	                   					  <span class="value-order"><i>${orderData.poNumber}</i></span>
	                				 </c:otherwise>
	            					</c:choose>  
								  </span>
                        	  </div>                       	  
                        </c:otherwise>
                    </c:choose>
         </div>
    </div>

    <!--    Delivery    -->
    <div class="row no-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="text.account.orderDetails.delivery" />
        </div>

        <div class="col-md-12 order-ship-to no-padding">
            <div class="value-title"><spring:theme code="text.account.orderDetails.deliveryaddress" /></div>
            <div class="value-order"><order:addressItem address="${order.deliveryAddress}"/></div>
        </div>

        <div class="col-md-12 order-ship-to no-padding">
            <div class="value-title"><spring:theme code="text.account.orderDetails.deliveryinstructions" /></div>
            <c:choose>
                <c:when test="${empty order.deliveryInstruction and empty order.deliveryAddress.deliveryInstruction}">
                    <div class="value-order"><i><spring:theme code="text.account.orderDetails.no.deliveryinstructions" /></i></div>
                </c:when>
                <c:when test="${not empty orderData.deliveryInstruction}">
                     <div class="value-order">${order.deliveryInstruction}</div>
                </c:when>
                <c:otherwise>
                    <div class="value-order">${order.deliveryAddress.deliveryInstruction}</div>
                 </c:otherwise>
            </c:choose>  
        </div>
        
        <div class="col-md-12 order-ship-to no-padding">
        	<c:choose>
        	<c:when test="${cmsSite.uid ne 'sga'}">
        	<div class="value-title"><spring:theme code="text.account.orderDetails.delivery.mode" /></div>
        	<c:choose>
        		<c:when test="${order.deferredDelivery != 'FALSE'}">
        			<div class="value-order"><spring:theme code="text.account.orderDetails.deferred.delivery" /></div>
        			<div class="order-history-defered-date"><spring:theme code="text.account.orderDetails.deferred.delivery.date" />${order.deliveryRequestDate}</div>
        		</c:when>
        		<c:otherwise>
        			<div class="value-order"><spring:theme code="text.account.orderDetails.standard.delivery" /></div>
                    <div class="order-history-defered-date"><spring:theme code="text.account.orderDetails.deferred.delivery.date" />
                    <c:choose>
                        <c:when test="${not empty order.deliveryRequestDate}">
                            ${order.deliveryRequestDate}
                        </c:when>
                        <c:otherwise>
                            <spring:theme code="text.account.orderHistory.inProgress" />
                        </c:otherwise>
                    </c:choose>
                    </div>
                </c:otherwise>
        	</c:choose>
        	</c:when>
        		<c:otherwise>
        			<div class="value-title"><spring:theme code="text.account.orderDetails.delivery.date" /></div>
        			<div class="order-history-defered-date">${order.deliveryRequestDate}</div>
        		</c:otherwise>
        		</c:choose>
        	
        </div>
    </div>

    <!--   Cart Summary  -->
    <div class="row no-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="text.account.orderDetails.cartsummary" />
            <div class="float-right">
                    <c:choose>
                        <c:when test="${order.totalUnitCount} > 1}">
                            <span class="">${order.totalUnitCount} <span> </span>
                            <spring:theme code="checkout.cart.items.total" />
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="">${order.totalUnitCount} <span> </span>
                            <spring:theme code="checkout.cart.items.total" />
                            </span>
                        </c:otherwise>
                    </c:choose>
              <c:if test="${fn:length(order.unconsignedEntries) > 3}" >
	               		<span id="seperater_tag">|</span>
	                		<a id="checkout_links_view_all" href="javascript:void(0)"><spring:theme code="checkout.link.view.all"/></a>
	        					<a id="checkout_links_collapse" class="hide" href="javascript:void(0)"><spring:theme code="checkout.link.collapse"/></a>
	        			  </c:if> 
            </div>
        </div>
    </div>
    <ul class="row item__list ">
        <li class="hidden-xs">
            <ul class="item__list--header">
    <!--
                <li class="item__toggle"></li>
                <li class="item__image"></li> 
    -->
                <div class="col-sm-3 col-md-3"><li class=""><spring:theme code="basket.page.item"/></li></div>
                <c:if test = "${!isNAPGroup}">
                <div class="col-sm-2 col-md-2"><li class=""><spring:theme code="basket.page.price"/></li></div>
                </c:if>
                <div class="col-sm-2 col-md-2"><li class="item__status"><spring:theme code="basket.page.status"/></li></div>
                <div class="col-sm-2 col-md-2"><li class="history-qty"><spring:theme code="basket.page.qty"/></li></div>
                <div class="col-sm-1 col-md-2"><li class="item__delivered__quantity"><spring:theme code="basket.page.deliveredqty"/></li></div>
                 <c:if test = "${!isNAPGroup}">
                <div class="col-sm-2 col-md-1"><li class="float-right"><spring:theme code="basket.page.total"/></li></div>
                </c:if>
            </ul>
        </li>

        <div id="orderhistory_storefront_table" class="storefront_table">
            <c:forEach items="${order.unconsignedEntries}" var="entry" varStatus="loop">
            	<c:if test="${!entry.isFreeGood}">
                	<order:orderEntryDetails orderEntry="${entry}" order="${order}" itemIndex="${loop.index}"/>
                </c:if>
            </c:forEach>
        </div>
    </ul>
</div>