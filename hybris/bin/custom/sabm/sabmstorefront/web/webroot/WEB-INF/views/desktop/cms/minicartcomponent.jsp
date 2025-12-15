<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>


<c:url value="/cart/miniCart/${totalDisplay}" var="refreshMiniCartUrl"/>
<c:url value="/cart/rollover/${component.uid}" var="rolloverPopupUrl"/>
<c:url value="/cart/" var="cartUrl"/>

<c:set var="cartCode" value="${cartId}" scope="session" />

<ycommerce:testId code="miniCart_items_label">
	<a href="${cartUrl}" class="minicart bde-view-only">
		<span class="icon-mask">
			<svg class="icon-truck01">
			    <use xlink:href="#icon-truck01"></use>    
			</svg>
			<span class="count badge <c:if test='${totalItems == 0}'>hide</c:if>">
					${totalItems}
			</span>
		</span>
		<label class="items hidden-xs"><spring:theme code="basket.your.shopping.basket"/></label>

	</a>
	<p class="minicart-tooltip" style="display: none;"><spring:theme code="basket.items.added.to.oder" /></p>
	
	<div id="miniCartPopup"></div>
</ycommerce:testId>
<div class="miniCartLayer" data-refreshMiniCartUrl="${refreshMiniCartUrl}/?"  data-rolloverPopupUrl="${rolloverPopupUrl}" ></div>
<cart:popupLoseDeal />