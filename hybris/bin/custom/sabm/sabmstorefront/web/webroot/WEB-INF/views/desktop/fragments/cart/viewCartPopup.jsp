<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<c:url value="/cart/" var="cartUrl"/>
<c:url value="/cart/updateQuantity" var="cartUpdateQuantityUrl" scope="session"/>
{
"addToCartLayer":"<spring:escapeBody javaScriptEscape="true">
<spring:theme code="text.addToCart" var="addToCartText"/>
<div id="addToCartLayer" class="miniCartPopup viewCartPopup col-sm-12">
	<input type="hidden" id="cartUpdateQuantityUrl" value="${cartUpdateQuantityUrl}">
	<c:choose>
		<c:when test="${empty cartData.entries}">
			<div class="highlight-link"> <spring:theme code="basket.view.empty.items"/> </div>
		</c:when> 
		<c:otherwise>
			<ul class="itemList">
		<c:forEach items="${cartData.entries}" var="entry" varStatus="cartDataEntriesLoop">
				<c:url value="${entry.product.url}" var="entryProductUrl"/>
				<c:set value="${cartDataEntriesLoop.index}" var="index" />
				<li class="popupCartItem clearfix" data-index="${index}">
								<div class="itemThumb">
								<a href="${entryProductUrl}" class="js-track-product-link"
					        	data-currencycode="${entry.basePrice.currencyIso}"
								data-name="${fn:escapeXml(entry.product.name)}"
								data-id="${entry.product.code}"
								data-sku="${entry.product.code}"
								data-price="${entry.basePrice.value}"
								data-coupon="${entry.product.dealsFlag}"
								data-brand="${fn:escapeXml(entry.product.brand)}"
								data-category="${fn:escapeXml(entry.product.categories[0].name)}"
								data-variant="${entry.unit.name}"
								data-position="${status.count}"
								data-url="${entry.product.url}"
								data-actionfield="View Cart Popup"><product:miniCartProductPrimaryImage product="${entry.product}" format="cartIcon"/></a>
					</div>
					<div class="itemDesc">
						<a class="itemName js-track-product-link" href="${entryProductUrl}"
								data-currencycode="${entry.basePrice.currencyIso}"
								data-name="${fn:escapeXml(entry.product.name)}"
								data-id="${entry.product.code}"
								data-price="${entry.basePrice.value}"
								data-brand="${fn:escapeXml(entry.product.brand)}"
								data-category="${fn:escapeXml(entry.product.categories[0].name)}"
								data-variant="${entry.unit.name}"
								data-position="${status.count}"
								data-url="${entry.product.url}"
								data-actionfield="View Cart Popup">
							<h5><c:out value="${entry.product.name}" /></h5>
							<span>${entry.product.packConfiguration}</span>
						</a>
						<div class="itemQuantity">
						<span id="itemQuantityAndUnit${index}">
							${entry.quantity}&nbsp;${entry.quantity > 1 ? entry.unit.pluralName.toLowerCase() : entry.unit.name.toLowerCase()}
						</span>
						</div>
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
					</div>
					<div class="itemQuantityUpdate">
						<br/>
						<c:choose>
							<c:when test="${not entry.isFreeGood && (entry.totalPrice.value >= 0 || entry.availabilityInfo == 'unavailable' || entry.availabilityInfo == 'blocked') }">
								<div class="minicart-update-item padding-right-1em float-right">
									<span class="inline">
										<spring:theme code="text.cart.update"/>
									</span>
								</div>
							</c:when>
							<c:otherwise>
								<div class="padding-right-1em float-right">
									<spring:theme code="text.cart.bonus"/>
								</div>
							</c:otherwise>
						</c:choose>
						<div class="row list-qty display-block">
							<c:choose>
								<c:when test="${not entry.isFreeGood && (entry.totalPrice.value >= 0 || entry.availabilityInfo == 'unavailable' || entry.availabilityInfo == 'blocked') }">
									<c:url value="/cart/update" var="cartUpdateFormAction" />
									<form:form id="updateMiniCartForm${index}" action="${cartUpdateFormAction}" method="post"
										modelAttribute="updateQuantityForm${entry.entryNumber}" novalidate="novalidate">
										<input type="hidden" name="fromIndex" value="${index}" class="fromIndex" />
										<input type="hidden" name="entryNumber" value="${entry.entryNumber}" class="entry-entryNumber" />
										<input type="hidden" name="productCode" value="${entry.product.code}" />
										<input type="hidden" name="initialQuantity" value="${entry.quantity}" id="initialQuantity${index}" />
										<input type="hidden" name="initialUnit" value="${entry.unit.code}" id="initialUnit${index}" />
										<input type="hidden" name="unit" value="${entry.unit.code}" id="updateEntryUnit${index}" />
										<input type="hidden" name="quantity" value="${entry.quantity}" id="updateEntryQuantity${index}" />
										<input type="hidden" name="entryUnit" class="entry-product-unit" value="${entry.baseUnit.name}" />
										<input type="hidden" name="entryPluralUnit" class="entry-product-plural-unit" value="${entry.baseUnit.pluralName}" />
										<input type="hidden" name="entryIndex" class="entry-loop-index" value="${index}" />
							
										<div class="col-xs-6 trim-right">
											<ul class="select-quantity clearfix">
												<li class="${entry.quantity > 1 ? 'down':'down disabled'}">
													<svg class="icon-minus">
													    <use xlink:href="#icon-minus"></use>    
													</svg>
												</li>
												<li>
													<label class="skip"><spring:theme code="basket.page.quantity" /></label>
														
													<input <c:if test="${not entry.updateable}">disabled="disable"</c:if> value="${entry.quantity}" data-minqty="1" type="tel" size="1" id="displayQuantity${index}" class="qty qty-input min-1" pattern="\d*"/>
												</li>
												<li class="up">
													<svg class="icon-plus">
													    <use xlink:href="#icon-plus"></use>    
													</svg>
												</li>
											</ul>
											<c:choose>
											<c:when test="${entry.baseUnit.code eq entry.unit.code}">
											</c:when>
											<c:otherwise>
											<span class="base-quantity" 
													data-base-unit="${entry.baseUnit.code}">
											<span>${entry.baseQuantity}</span>&nbsp;${entry.baseQuantity > 1 ? entry.baseUnit.pluralName : entry.baseUnit.name}</span>
											</c:otherwise>
											</c:choose>
										</div>
										<div class="col-xs-6 trim-left-5">
												<div class="select-list margin-right-1em display-grid">
													<c:if test="${not empty entry.product.uomList}">
														<c:choose>
															<c:when test="${fn:length(entry.product.uomList) eq 1}">
																<div class="select-single">${entry.product.uomList[0].name}</div>
															</c:when>
															<c:otherwise>
																<div data-value="${entry.unit.code}" class="select-btn sort minicart float-right">
																	${entry.unit.name}
																</div>
																<ul class="select-items">
																	<c:forEach items="${entry.product.uomList}" var="uom">
																		<li class="cart-entry" data-value="${uom.code}">${uom.name}</li>
																	</c:forEach>
																</ul>
															</c:otherwise>
														</c:choose>
													</c:if>
												</div>
										</div>
										
									</form:form>
								</c:when>
								<c:otherwise>
									<%--<form:form id="updateMiniCartForm${index}" method="post"
										modelAttribute="updateQuantityForm${entry.entryNumber}" novalidate="novalidate">
										<input type="hidden" name="initialQuantity" value="${entry.quantity}" id="initialQuantity${index}" />
										<input type="hidden" name="initialUnit" value="${entry.unit.code}" id="initialUnit${index}" />
										<input type="hidden" name="quantity" value="${entry.quantity}" id="updateEntryQuantity${index}" />
										<input type="hidden" name="entryNumber" value="${entry.entryNumber}" class="entry-entryNumber" />
										<input type="hidden" name="unit" value="${entry.unit.code}" id="updateEntryUnit${index}" />
										<input type="hidden" name="entryIndex" class="entry-loop-index" value="${index}" />
									</form:form>
									<span class="visible-md-block visible-lg-block base-quantity">${entry.baseQuantity}&nbsp;${entry.baseQuantity > 1 ? entry.baseUnit.pluralName.toLowerCase() : entry.baseUnit.name.toLowerCase()}</span>--%>
								</c:otherwise>
							</c:choose>
						</div>
						<span><spring:theme code="text.iconCartRemove" var="iconCartRemove"/></span>
						<c:if test="${entry.updateable and not entry.isFreeGood}">
							<div class="visible-md-block visible-lg-block text-center minicart-delete-item padding-right-1em float-right">
								<span data-index="${index}" class="inline submitRemoveProduct">${iconCartRemove}</span>
							</div>
						</c:if>
					</div>
				</li>
				</c:forEach>
			</ul>
			<a href="${cartUrl}" class="btn btn-primary offset-bottom-xsmall full-width">
				<spring:theme code="basket.proceed.to.backet"/>
			</a>
			<span>
				<spring:theme code="basket.recently.added.bonus.products"/>
			</span>
		</c:otherwise>
	</c:choose>
</div>
</spring:escapeBody>",
"totalItemCount":"${cartData.totalUnitCount}"
}