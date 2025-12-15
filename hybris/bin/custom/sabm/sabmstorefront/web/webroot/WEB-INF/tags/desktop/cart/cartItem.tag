<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="entry" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryData"%>
<%@ attribute name="index" required="true" type="java.lang.Integer"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>

<c:url value="${entry.product.url}" var="productUrl" />
<c:set value="${index + 1}" var="count"/>

<product:productPackTypeAllowed unit="${entry.unit.code}"/>

 <div class="cartRow clearfix" data-index="${index}">
	<div class="col-md-4 cart-mob-row-1<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
		<div class="row">
			<div class="col-md-3 visible-md-block visible-lg-block cart-img">
				<a href="${productUrl}" class="js-track-product-link"
			        	data-currencycode="${entry.basePrice.currencyIso}"
						data-name="${fn:escapeXml(entry.product.name)}"
						data-id="${entry.product.code}"
						data-sku="${entry.product.code}"
						data-price="${entry.basePrice.value}"
						data-brand="${fn:escapeXml(entry.product.brand)}"
						data-category="${fn:escapeXml(entry.product.categories[0].name)}"
						data-variant="${entry.unit.name}"
						data-position="${count}"
						data-url="${entry.product.url}"
						data-sku="${entry.product.code}"
						data-coupon="${entry.product.dealsFlag}"
						data-actionfield="${fn:escapeXml(requestOrigin)}/Review Your Order"><product:productPrimaryImage product="${entry.product}" format="thumbnail" isCart="true"/></a>
			</div>
			<div class="col-xs-7 col-sm-9 trim-left-5-lg cart-name">
				<div class="itemName offset-bottom-xsmall">
					<a href="${productUrl}" class="js-track-product-link"
			        	data-currencycode="${entry.basePrice.currencyIso}"
						data-name="${fn:escapeXml(entry.product.name)}"
						data-id="${entry.product.code}"
						data-price="${entry.basePrice.value}"
						data-brand="${fn:escapeXml(entry.product.brand)}"
						data-category="${fn:escapeXml(entry.product.categories[0].name)}"
						data-variant="${entry.unit.name}"
						data-position="${count}"
						data-url="${entry.product.url}"
						data-actionfield="${fn:escapeXml(requestOrigin)}/Review Your Order">
						<h4 class="cartItem Clamp-2 offset-bottom-none">${entry.product.name}</h4>
						<c:if test="${not empty entry.product.packConfiguration}">
							<h4 class="cartItem Clamp-2 offset-bottom-none">${entry.product.packConfiguration}</h4>
						</c:if>
					</a>
				</div>
				<div class="visible-md-block visible-lg-block">
					
				<c:forEach items="${entry.dealTitle}" var="dealTitle" varStatus="dealsloop">
					<span class="bold text-blue">Deal&nbsp;</span><span class="deal-index">${dealTitle.dealSeqNo}</span>
				</c:forEach><span></span>
			
					<c:if test="${entry.isFreeGood}">				
						  <c:if test="${entry.chooseFrees}">
							<span class="chooseFreeProduct inline" data-deal-code="${entry.freeGoodsForDeal}"><spring:theme code="text.cart.choose.another.free"/></span>
						</c:if>
					</c:if>
				</div>
			</div>
			<div class="col-xs-5 col-sm-3 visible-xs-block visible-sm-block text-right h4">
				<cart:cartLineTotal index="${index}" entry="${entry}"/>
			</div>
		</div>
	</div>
	<div class="col-md-8<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
		<div class="row">
      <div class="col-md-5 cart-mob-row-2">
        <div class="cart-itemPrice h4">
          <span class="visible-xs-inline visible-sm-inline"><spring:theme code="basket.page.customer.unit" />&nbsp;</span> <format:price priceData="${entry.basePrice}" displayFreeForZero="${entry.isFreeGood}" />
          <c:if test="${entry.unitDiscountAmount.value > 0 and not entry.isFreeGood}">									
            <span class="visible-md-block visible-lg-block block text-normal"><spring:theme code="basket.page.save" />&nbsp;<format:price priceData="${entry.unitDiscountAmount}" /></span>							
          </c:if>
        </div>
        <div class="cart-itemPrice h4">
          <c:choose>
            <c:when test="${entry.wet.value >0}">
                <span class="visible-xs-inline visible-sm-inline"><spring:theme code="basket.page.customer.unit.wet" />&nbsp;</span>${entry.wet.formattedValue}
             </c:when>
            <c:otherwise>
                 <span class="visible-xs-inline visible-sm-inline"><spring:theme code="basket.page.customer.unit.wet" />&nbsp;</span>-&nbsp;&nbsp;&nbsp;
             </c:otherwise>
          </c:choose>
        </div>
        <div class="cart-itemPrice h4">
            <c:choose>
              <c:when test="${entry.deposit.value >0}">
                  <span class="visible-xs-inline visible-sm-inline"><spring:theme code="basket.page.customer.unit.deposit" />&nbsp;</span>${entry.deposit.formattedValue}
              </c:when>
              <c:otherwise>
                   <span class="visible-xs-inline visible-sm-inline"><spring:theme code="basket.page.customer.unit.deposit" />&nbsp;</span>-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
               </c:otherwise>
            </c:choose>
        </div>
      </div>
			<div class="col-md-7 cart-mob-row-3">
				<div class="row">
					<div class="col-xs-6 col-md-4 trim-right-5-lg cart-qty addtocart-qty">
						<c:choose>
							<c:when test="${not entry.isFreeGood && (entry.totalPrice.value >= 0 || entry.availabilityInfo == 'unavailable' || entry.availabilityInfo == 'blocked') }">
								<c:url value="/cart/update" var="cartUpdateFormAction" />
								<form:form id="updateCartForm${index}" action="${cartUpdateFormAction}" method="post"
									modelAttribute="updateQuantityForm${entry.entryNumber}" novalidate="novalidate"
									data-cart='{"cartCode" : "${cartData.code}","productPostPrice":"${entry.basePrice.value}","productName":"${entry.product.name}"}'>
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
									<span class="base-quantity" style="display: ${entry.baseUnit.code eq entry.unit.code ? 'none' : 'inline-block'}" data-base-unit="${entry.baseUnit.code}">
									<span>${entry.baseQuantity}</span>&nbsp;${entry.baseQuantity > 1 ? entry.baseUnit.pluralName : entry.baseUnit.name}</span>
									</c:otherwise>
									</c:choose>
								</form:form>
							</c:when>
							<c:otherwise>
								<form:form id="updateCartForm${index}" method="post"
									modelAttribute="updateQuantityForm${entry.entryNumber}" novalidate="novalidate"
									data-cart='{"cartCode" : "${cartData.code}","productPostPrice":"${entry.basePrice.value}","productName":"${entry.product.name}"}'>
									<input type="hidden" name="initialQuantity" value="${entry.quantity}" id="initialQuantity${index}" />
									<input type="hidden" name="initialUnit" value="${entry.unit.code}" id="initialUnit${index}" />
									<input type="hidden" name="quantity" value="${entry.quantity}" id="updateEntryQuantity${index}" />
									<input type="hidden" name="entryNumber" value="${entry.entryNumber}" class="entry-entryNumber" />
									<input type="hidden" name="unit" value="${entry.unit.code}" id="updateEntryUnit${index}" />
									<input type="hidden" name="entryIndex" class="entry-loop-index" value="${index}" />
								</form:form>
								<span class="visible-md-block visible-lg-block base-quantity">${entry.baseQuantity}&nbsp;${entry.baseQuantity > 1 ? entry.baseUnit.pluralName.toLowerCase() : entry.baseUnit.name.toLowerCase()}</span>
							</c:otherwise>
						</c:choose>
					</div>
					<spring:theme code="text.iconCartRemove" var="iconCartRemove" />
					<div class="col-xs-6 col-md-4 trim-left-5-lg trim-right-5-lg cart-pack">

						<div class="select-list">
							<c:if test="${not empty entry.product.uomList}">
								<c:choose>
									<c:when test="${fn:length(entry.product.uomList) eq 1}">
										<div class="select-single">${entry.product.uomList[0].name}</div>
									</c:when>
									<c:otherwise>
										<div data-value="${entry.unit.code}" class="select-btn sort">${entry.unit.name}</div>
										<ul class="select-items">
											<c:forEach items="${entry.product.uomList}" var="uom">
												<li class="cart-entry" data-value="${uom.code}">${uom.name}</li>
											</c:forEach>
										</ul>
									</c:otherwise>
								</c:choose>
							</c:if>
						</div>
						<c:if test="${entry.updateable and not entry.isFreeGood}">
							<div class="visible-md-block visible-lg-block text-center"><span data-index="${index}" 
									data-currencycode="${entry.basePrice.currencyIso}"
									data-name="${fn:escapeXml(entry.product.name)}"
									data-id="${entry.product.code}"
									data-price="${entry.basePrice.value}"
									data-brand="${fn:escapeXml(entry.product.brand)}"
									data-category="${fn:escapeXml(entry.product.categories[0].name)}"
									data-position="${count}"
									data-dealsflag="${entry.product.dealsFlag}"
									data-actionfield="${fn:escapeXml(requestOrigin)}" 
									data-uomlist='{"${entry.unit.code}":"${entry.unit.name}"<c:forEach items="${entry.product.uomList}" var="uom" varStatus="uomStatus"><c:if test="${uom.code ne entry.unit.code}">, "${uom.code}":"${uom.name}"</c:if></c:forEach>}' class="inline submitRemoveProduct">${iconCartRemove}</span></div>
						</c:if>
					</div>
					<div class="col-xs-12 col-md-4 cart-total text-right">
						<div class="visible-md-block visible-lg-block">
							<cart:cartLineTotal index="${index}" entry="${entry}"/>
						</div>

						<div class="visible-xs-block visible-sm-block">
							<c:if test="${not empty entry.dealTitle }">
									<c:forEach items="${entry.dealTitle}" var="dealTitle" varStatus="dealsloop">
										 
										<span class="bold text-blue">Deal&nbsp;</span><span class="deal-index">${dealTitle.dealSeqNo}</span>
								
								 
									</c:forEach><span></span>
							</c:if>
							<c:if test="${entry.updateable and not entry.isFreeGood}">
								<span href="javascript:void(0);" data-index="${index}" 
									data-currencycode="${entry.basePrice.currencyIso}"
									data-name="${fn:escapeXml(entry.product.name)}"
									data-id="${entry.product.code}"
									data-price="${entry.basePrice.value}"
									data-brand="${fn:escapeXml(entry.product.brand)}"
									data-category="${fn:escapeXml(entry.product.categories[0].name)}"
									data-position="${count}"
									data-dealsflag="${entry.product.dealsFlag}"
									data-actionfield="${fn:escapeXml(requestOrigin)}" 
									data-uomlist='{"${entry.unit.code}":"${entry.unit.name}"<c:forEach items="${entry.product.uomList}" var="uom" varStatus="uomStatus"><c:if test="${uom.code ne entry.unit.code}">, "${uom.code}":"${uom.name}"</c:if></c:forEach>}' class="submitRemoveProduct visible-xs-block visible-sm-block inline text-normal pull-right">${iconCartRemove}</span>
							</c:if>

							<div class="visible-xs-block visible-sm-block">
								
								<c:if test="${entry.isFreeGood}">				
									  <c:if test="${entry.chooseFrees}">
										<span class="chooseFreeProduct text-normal inline" data-deal-code="${entry.freeGoodsForDeal}"><spring:theme code="text.cart.choose.another.free"/></span>
									</c:if>
								</c:if>
							</div>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	<c:if test="${!isProductPackTypeAllowed}">
	<div class="col-xs-12 notallowed-notification"><spring:theme code="text.cart.product.packtype.notallowed1"/>
		<span data-index="${index}" 
			data-currencycode="${entry.basePrice.currencyIso}"
			data-name="${fn:escapeXml(entry.product.name)}"
			data-id="${entry.product.code}"
			data-price="${entry.basePrice.value}"
			data-brand="${fn:escapeXml(entry.product.brand)}"
			data-category="${fn:escapeXml(entry.product.categories[0].name)}"
			data-position="${count}"
			data-dealsflag="${entry.product.dealsFlag}"
			data-actionfield="${fn:escapeXml(requestOrigin)}" 
			data-uomlist='{"${entry.unit.code}":"${entry.unit.name}"<c:forEach items="${entry.product.uomList}" var="uom" varStatus="uomStatus"><c:if test="${uom.code ne entry.unit.code}">, "${uom.code}":"${uom.name}"</c:if></c:forEach>}' class="inline submitRemoveProduct"><spring:theme code="text.cart.product.packtype.notallowed2"/></span>
		<spring:theme code="text.cart.product.packtype.notallowed3"/></div>
	</c:if>
<%-- 	<div class="col-xs-12">
	<div class="row">
		<div class="col-xs-1"></div>
		<div class="col-xs-10"><cart:cartItemDealTitle entry="${entry}"/></div>
	</div>
		
	</div> --%>
	<c:if test="${entry.availabilityInfo eq 'lowStock'}">
		<div class="col-xs-12">
			<c:if test="${entry.baseQuantity > entry.sapConfirmedQuantity}">
				<c:choose>
					<c:when test="${entry.sapConfirmedQuantity == 1}">
						<span class="error"><spring:theme code="basket.page.item.lowstock" arguments="${entry.sapConfirmedQuantity},${entry.baseUnit.name.toLowerCase()}" /></span>
					</c:when>
					<c:otherwise>
						<span class="error"><spring:theme code="basket.page.item.lowstock" arguments="${entry.sapConfirmedQuantity},${entry.baseUnit.pluralName.toLowerCase()}" /></span>
					</c:otherwise>
				</c:choose>
			</c:if>
		</div>
	</c:if>
	<c:if test="${entry.availabilityInfo eq 'outOfStock'}">
		<div class="col-xs-12">
			<span class="error"><spring:theme code="basket.page.item.outofstock" /></span>
		</div>
	</c:if>
	<c:if test="${entry.availabilityInfo eq 'blocked'}">
		<div class="col-xs-12">
			<span class="error"><spring:theme code="basket.page.salesordersimulate.product.no.longer.available"  arguments="(${entry.info})"/></span>
		</div>
	</c:if>
	<c:if test="${entry.availabilityInfo eq 'unavailable'}">
		<div class="col-xs-12">
			<span class="error"><spring:theme code="basket.page.salesordersimulate.product.temporarily.unavailable" arguments="(${entry.info})" /></span>
		</div>
	</c:if>
	
	<div class="col-xs-12" id="maxorderqty${index}"></div>

	<div class="col-xs-12">
        <div class="order-error-message order-error-message-${index}">
            <c:if test="${not empty validationData}">
                <c:set var="errorMessage" value="${fn:split(validationData[index].statusCode, ':')}" />
                <c:out value="${errorMessage[1]}" />
            </c:if>
        </div>
    </div>
</div>
<div id="ajaxGrid${index}" style="display: none"></div>
