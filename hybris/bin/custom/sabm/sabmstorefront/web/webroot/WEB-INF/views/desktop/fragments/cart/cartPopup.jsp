<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<spring:theme code="text.addToCart" var="addToCartText"/>
<spring:theme code="text.popupCartTitle" var="popupCartTitleText"/>
<c:url value="/cart" var="cartUrl"/>
<c:url value="/cart/checkout" var="checkoutUrl"/>


<c:if test="${numberShowing > 0 }">
	<div class="legend">
		<spring:theme code="basket.recently.added.products" />
	</div>
</c:if> 

<c:if test="${empty numberItemsInCart or numberItemsInCart eq 0}">
	<div class="cart_modal_popup empty-popup-cart">
		<spring:theme code="popup.cart.empty"/>
	</div>
</c:if>
<c:if test="${numberShowing > 0 }">
	<ul class="itemList">
	<c:forEach items="${entries}" var="entry" end="${numberShowing - 1}">
		<c:url value="${entry.product.url}" var="entryProductUrl"/>
		<li class="popupCartItem clearfix">
			<div class="itemThumb">
				<a href="${entryProductUrl}">
					<product:productPrimaryImage product="${entry.product}" format="cartIcon"/>
				</a>
			</div>
			<div class="itemDesc">
				<a class="itemName" href="${entryProductUrl}">
				<h5>${entry.product.name}</h5>
				<span>6 x 4 x 355ml Bottles</span>
				</a>
				<div class="itemQuantity"></span>${entry.quantity} Cases</div>
				
				<c:forEach items="${entry.product.baseOptions}" var="baseOptions">
					<c:forEach items="${baseOptions.selected.variantOptionQualifiers}" var="baseOptionQualifier">
						<c:if test="${baseOptionQualifier.qualifier eq 'style' and not empty baseOptionQualifier.image.url}">
							<div class="itemColor">
								<span class="label"><spring:theme code="product.variants.colour"/></span>
								<img src="${baseOptionQualifier.image.url}" alt="${baseOptionQualifier.value}" title="${baseOptionQualifier.value}"/>
							</div>
						</c:if>
						<c:if test="${baseOptionQualifier.qualifier eq 'size'}">
							<div class="itemSize">
								<span class="label"><spring:theme code="product.variants.size"/></span>
								${baseOptionQualifier.value}
							</div>
						</c:if>
					</c:forEach>
				</c:forEach>
				
				<c:if test="${not empty entry.deliveryPointOfService.name}">
					<div class="itemPickup"><span class="itemPickupLabel"><spring:theme code="popup.cart.pickup"/></span>${entry.deliveryPointOfService.name}</div>
				</c:if>
				
			</div>
		</li>
	</c:forEach>
	</ul>
</c:if>

