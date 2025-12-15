<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<!-- Updated the page for cart page customization ACP-25-->
<c:set var="errorStatus" value="<%= de.hybris.platform.catalog.enums.ProductInfoStatus.valueOf(\"ERROR\") %>" />
<c:url value="/cart/removeAllProducts" var="removeAllProductsUrl"/>

<script type ="text/javascript">
var removeAllProductsUrl ='${removeAllProductsUrl}';
</script>
<input type="hidden" name="updatedEntryNumber" value="${fn:escapeXml(entryNumber)}" />
<input type="hidden" name="updatedQtyMsg" value="<spring:theme code="${fn:escapeXml(qtyUpdateMsg)}"/>" />
<input type="hidden" name="updateMsgTimeout" value="${msgTimeOut}" />
<input type="hidden" name="isMessageTypeError" value="${error}" />
		<%--<div id="showSpinner"></div>--%>
<ul class="item__list item__list__cart">
    <li class="hidden-xs">
        <ul class="item__list--header">
            <!--<li class="item__toggle"></li>  Removing this for alignment issues.-->
            <div class="col-sm-1 col-md-1 cart-values image-tablet-fix no-padding-left"><li class=""><spring:theme code="basket.page.item"/></li></div> <!--Moving this here to move title left-->
            <div class="col-sm-3 col-md-4 cart-values"><li class=""></li></div>
<!--            <%-- <li class="item__price"><spring:theme code="basket.page.skuid"/></li> --%>-->
            <div class="col-sm-1 col-md-2 no-padding-desktop-left price-tablet-fix cart-values"><li class=""><spring:theme code="basket.page.price"/></li></div>
<!--            <%-- <li class="item__price"><spring:theme code="basket.page.uom"/></li> --%>-->
            <div class="col-sm-3 col-md-2 no-padding-desktop-left cart-values"><li class=""><spring:theme code="basket.page.qty"/></li></div>
<!--            <%-- <li class="item__delivery"><spring:theme code="basket.page.delivery"/></li> --%>-->
            <div class="col-sm-1 col-md-2 no-padding-desktop-left no-padding-right cart-values"><li class=""><spring:theme code="basket.page.total"/></li></div>
           	<div class="col-sm-2 col-md-1 float-right remove-header-tablet-fix cart-values no-padding"><li class="float-right">
           		<c:choose>
					    <c:when test="${not empty cartData.entries}">
					       <a id="removeAllProducts" href data-removeAllProductsUrl="${removeAllProductsUrl}"><spring:theme code="basket.page.removeall"/></a>
					    </c:when>
					    <c:otherwise>
					       <spring:theme code="basket.page.removeall"/>
					    </c:otherwise>
					</c:choose>
           		
            </li></div>
        </ul>
    </li>
    
    <li class="visible-xs hidden-sm hidden-md  hidden-lg">
    		<ul class="item__list_mobile--header" >
    			<li class="item__removeall--column">
    				<c:choose>
					    <c:when test="${not empty cartData.entries}">
					       <a id="removeAllProducts" href data-removeAllProductsUrl="${removeAllProductsUrl}"><spring:theme code="basket.page.removeall"/></a>
					    </c:when>
					    <c:otherwise>
					       <spring:theme code="basket.page.removeall"/>
					    </c:otherwise>
					</c:choose>
					<span class="item__list_cart_count">
						<c:choose>
	                        <c:when test="${fn:length(cartData.entries) > 1 or fn:length(cartData.entries) == 0}">
	                            <spring:theme code="basket.page.totals.total.items" arguments="${fn:length(cartData.entries)}"/>
	                        </c:when>
	                        <c:otherwise>
	                            <spring:theme code="basket.page.totals.total.items.one" arguments="${fn:length(cartData.entries)}"/>
	                        </c:otherwise>
	                     </c:choose>
                     </span>
    			</li>
    			
    		</ul>
     </li>

	<li class="row" id="itemTable"> 
        <div class="storefront_table">
 
         <c:if test="${not empty cartData.entries}">
              <table class="no-border cart-tbl">
                <c:forEach items="${cartData.entries}" var="entry" varStatus="entryIndex">
				<c:if test="${!entry.isFreeGood}">
                    <tr>
                        <td>
                            <cart:cartItem cartData="${cartData}" entry="${entry}" index="${entryIndex.count}" />
                        </td>
                    </tr>
					<tr class="deal-line-item">
						<c:if test="${not empty entry.asahiDealTitle and not empty entry.freeGoodEntryQty}">
							<td class="deal-cart-item">
								<div class="row">
									<div class="col-sm-8 col-sm-push-1 col-md-10 deal-seperator"></div>
								</div>
								<div>
									<div class="col-sm-1 col-md-1 cart-values image-tablet-fix no-padding-left"></div>
									<div class="col-sm-3 col-md-4 py-20 pl-0"><b>Deal: </b> ${entry.asahiDealTitle}</div>
									<div class="hidden-xs">
										<div class="col-sm-1 col-md-2 no-padding py-20"><spring:theme code="sga.deal.price.free"/></div>
										<div class="col-sm-3 col-md-2 no-padding py-20">${fn:escapeXml(entry.freeGoodEntryQty)} </div>
										<div class="col-sm-1 col-md-2 no-padding py-20"><spring:theme code="sga.deal.price.free"/></div>
									</div>
									<div class="hidden-sm hidden-md hidden-lg">
										<div>
											<span><b>Qty: </b>${fn:escapeXml(entry.freeGoodEntryQty)}</span>
											<span class="float-right"><b><spring:theme code="sga.deal.price.free"/></b></span>
										</div>
									</div>
								</div>
							</td>
						</c:if>
					</tr>
				</c:if>
                </c:forEach>
                
                </table>
            </c:if>
            
        </div>
   </li>
   <c:if test="${empty cartData.rootGroups}">
	   <li id="emptyCart">
	   	
			<cms:pageSlot position="EmptyCartMiddleContent" var="feature">
				<cms:component component="${feature}" element="div"
					class="yComponentWrapper content__empty" />
			</cms:pageSlot>
		
	   </li>
   </c:if>
</ul>

<product:productOrderFormJQueryTemplates />
<storepickup:pickupStorePopup />