<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="single-checkout" tagdir="/WEB-INF/tags/responsive/checkout/single"%>
<c:url value="/cart" var="cartUrl" />
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="checkout_subheading">
    <spring:theme code="checkout.title.cart.summary"/>
    <div class="float-right">
        <ycommerce:testId code="total_cart">
             <c:choose>
                <c:when test="${cartData.totalUnitCount > 1}">
                    <span>${cartData.totalUnitCount} <span> </span><spring:theme code="checkout.cart.items.total"/></span>
                </c:when> 
                <c:otherwise>
                    <span>${cartData.totalUnitCount} <span> </span><spring:theme code="checkout.cart.item.total"/></span>
                </c:otherwise>
            </c:choose>
        </ycommerce:testId>
        
        <c:if test="${fn:length(cartData.entries) > 3}" >
			 	 <span id="seperater_tag">|</span>
				 <a id="checkout_links_view_all" href="javascript:void(0)"><spring:theme code="checkout.link.view.all"/></a>
       		 <a id="checkout_links_collapse" class="hide" href="javascript:void(0)"><spring:theme code="checkout.link.collapse"/></a>
			</c:if>   
    </div>
</div>
    <ul class="item__list item__list__cart">
    <li class="hidden-xs">
        <ul class="item__list--header">
            <li class="item__info checkout__page__li__item" id="checkout_item_info"><spring:theme code="basket.page.item"/></li>
            <li class="item__price checkout__page__li__item" id="checkout_item_info"><spring:theme code="basket.page.price"/></li>
            <li class="item__quantity checkout__page__li__item" id="checkout_item_info"><spring:theme code="basket.page.qty"/></li>
            <li class="item__total--column checkout__page__li__item float-right" id="checkout_item_info"><spring:theme code="basket.page.total"/></li>
        </ul> 
	</li>
	<table id="checkout_table" class="checkout-tbl">
		<c:forEach items="${cartData.entries}" var="entryData" varStatus="loop">
		<c:if test="${!entryData.isFreeGood}">
	    	<single-checkout:checkoutCartListerItem entryData="${entryData}"  index="${loop.count}"/>
	    </c:if>
	    </c:forEach>
	</table>
	<c:if test="${cartData.showExclusionError != null && cartData.showExclusionError.booleanValue()}">
	    	<br><b><a class="site-anchor-link" href="${cartUrl}"><spring:theme code="sga.checkout.updatecart.link.text" text="Update my cart" /></a></b>
	</c:if>
</ul>