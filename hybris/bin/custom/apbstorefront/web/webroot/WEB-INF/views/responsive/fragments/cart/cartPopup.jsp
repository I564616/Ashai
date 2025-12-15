<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<spring:theme code="text.addToCart" var="addToCartText"/>
<spring:theme code="text.popupCartTitle" var="popupCartTitleText"/>
<c:url value="/cart" var="cartUrl"/>
<c:url value="/cart/checkout" var="checkoutUrl"/>

<c:choose>
	<c:when test="${not empty cartData.quoteData}">
		<c:set var="miniCartProceed" value="quote.view"/>
	</c:when>
	<c:otherwise>
		<c:set var="miniCartProceed" value="checkout.checkout"/>
	</c:otherwise>
</c:choose>

<div class="mini-cart js-mini-cart">
	<ycommerce:testId code="mini-cart-popup">
		<div class="mini-cart-body">
			<c:choose>
				<c:when test="${numberShowing > 0 }">
						<ol class="mini-cart-list">
							<c:forEach items="${entries}" var="entry" end="${numberShowing - 1}">

							<c:if test = "${!entry.isFreeGood}">
								<c:url value="${entry.product.url}" var="entryProductUrl"/>
								<li class="mini-cart-item">
									<div class="thumb" style="width: 25%; float:left; margin: 0px;">
										<a href="${entryProductUrl}" class="thumb <c:if test= "${entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()}">product-unavailable-image</c:if>">
											<product:productPrimaryImage product="${entry.product}" format="cartIcon"/>
										</a>
									</div>
									<div class="minicart-details" style="width: 73%; float:right; margin: 0px;">
											 <c:if test= "${entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()}">
                            				 <div class="product-unavailable-text"><spring:theme code="sga.product.unavailable" /></div>
                           					 </c:if>
										<a href="${entryProductUrl}">
											<span><b>${fn:escapeXml(entry.product.apbBrand.name)} </b></span>
											${fn:escapeXml(entry.product.name)}
										</a>
										<div class="qty">
										<c:if test="${not empty entry.product.packageSize.name || not empty entry.product.unitVolume.name}">
											<a href="${entryProductUrl}">
												${fn:escapeXml(entry.product.packageSize.name)}<span class="float-right">${fn:escapeXml(entry.product.unitVolume.name)}</span>
											</a>
										</c:if>
										</div>
										<div class="qty"><spring:theme code="mini.cart.popup.cart.quantity"/>: ${entry.quantity} <span class="float-right">
										<c:choose>
                                            <c:when test="${entry.isBonusStock.booleanValue()}">
                                                <strong><span class=""><spring:theme code="order.entry.bonus.text"/></span></strong>
                                            </c:when>
                                            <c:otherwise>
                                                <strong><format:price priceData="${entry.basePrice}"/></strong>
                                            </c:otherwise>
                                         </c:choose>
										</span></div>
<!--										<%-- <div class="price"><format:price priceData="${entry.basePrice}"/></div> --%>-->
										<c:forEach items="${entry.product.baseOptions}" var="baseOptions">
											<c:forEach items="${baseOptions.selected.variantOptionQualifiers}" var="baseOptionQualifier">
												<c:if test="${baseOptionQualifier.qualifier eq 'style' and not empty baseOptionQualifier.image.url}">
													<div class="itemColor">
														<span class="label"><spring:theme code="product.variants.colour"/></span>
														<img src="${baseOptionQualifier.image.url}" alt="${fn:escapeXml(baseOptionQualifier.value)}" title="${fn:escapeXml(baseOptionQualifier.value)}"/>
													</div>
												</c:if>
												<c:if test="${baseOptionQualifier.qualifier eq 'size'}">
													<div class="itemSize">
														<span class="label"><spring:theme code="product.variants.size"/></span>
															${fn:escapeXml(baseOptionQualifier.value)}
													</div>
												</c:if>
											</c:forEach>
										</c:forEach>
										<c:if test="${not empty entry.deliveryPointOfService.name}">
											<div class="itemPickup"><span class="itemPickupLabel"><spring:theme code="popup.cart.pickup"/></span>&nbsp;${fn:escapeXml(entry.deliveryPointOfService.name)}</div>
										</c:if>
									</div>
								</li>
								<c:if test="${not empty entry.asahiDealTitle and not empty entry.freeGoodEntryQty}">
									<li class="mini-cart-item">
										<div class="thumb" style="width: 25%; float:left; margin: 0px;">
											<a href="${entryProductUrl}" class="thumb <c:if test= "${entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()}">product-unavailable-image</c:if>">
												<product:productPrimaryImage product="${entry.product}" format="cartIcon"/>
											</a>
										</div>
										<div class="minicart-details" style="width: 73%; float:right; margin: 0px;">
											<a href="${entryProductUrl}">
												<b>Deal: </b>${entry.asahiDealTitle}
											</a>
											<div class="qty">Qty: ${fn:escapeXml(entry.freeGoodEntryQty)}<span class="float-right"><b><spring:theme code="sga.deal.price.free"/></b></span></div>
										</div>
									</li>
								</c:if>
								<div class="cart-seperator"></div>
								</c:if>
							</c:forEach>
							<div id="showing-item-text"><span id="show-item"><spring:theme code="mini.cart.popup.cart.more.items" arguments="${numberShowing},${numberItemsInCart}"/></span></div>
														
							<c:if test="${unavProdCount > 0 }">
								<span id="show-item"><spring:theme code="popup.cart.unavailable" arguments="${unavProdCount}"/></span>
							</c:if>
						</ol>

						<div class="mini-cart-totals"> 
							<div class="key"><spring:theme code="mini.cart.popup.cart.sub.total"/></div>
							
						<c:choose>
							<c:when test="${cmsSite.uid eq 'sga' and !wasCheckoutInterfce}">
                      			<div class="value"><b><format:price priceData="${cartData.minicartSubTotal}"/></b></div>
                      		</c:when>
                      		<c:otherwise>
                      			<div class="value"><b><format:price priceData="${cartData.subTotal}"/></b></div>
                      		</c:otherwise>
                      	</c:choose>
							
							<c:if test="${cmsSite.uid eq 'sga'}">
								<div class="minicart-price-message"><spring:theme code="sga.product.details.page.price.exclude" /></div>
							</c:if>
						</div>
						
						<a href="${cartUrl}" class="btn btn-default btn-block mini-cart-checkout-button">
							<spring:theme code="mini.cart.page.view.cart"/>
						</a>
						<a href="${checkoutUrl}" id="mini-cart-checkout-btn" class="btn btn-primary btn-vd-primary btn-block mini-cart-checkout-button mini-cart-creditBlock" data-min-order-check ="${minOrderQtyCheck}" <c:if test="${minOrderQtyCheck}"><c:out value="disabled"/></c:if>>
							<c:choose>
                      	<c:when test="${cmsSite.uid eq 'sga'}">
                      		<spring:theme code="sga.cart.proceed.checkout"/>
                      	</c:when>
                      	<c:otherwise>
                      		<spring:theme code="mini.cart.page.view.checkout" />
                      	</c:otherwise>
                      </c:choose>
						</a>
				</c:when>

				<c:otherwise>
					<div>
						<div class="emptyCartSection">						
						${emptyCartMessage}
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</ycommerce:testId>
</div>

<style type="text/css">
	#cboxLoadedContent {
    margin-top: 0px;
    padding: 15px 40px 30px 40px;
    border-style: double;
    background-color: #E8E8E8;
    width: 242px;
    height: 500px;
}

#cboxLoadedContent {
    width: 242px;
    overflow: auto;
    height: 478px;
}
#cboxContent {
    float: left;
    width: 0px;
    height: 0px;
}
</style>