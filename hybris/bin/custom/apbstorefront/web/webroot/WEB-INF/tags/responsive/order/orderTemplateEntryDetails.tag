<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderTemplateData" %>
<%@ attribute name="orderEntry" required="true" type="de.hybris.platform.commercefacades.order.data.OrderTemplateEntryData" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ attribute name="itemIndex" required="true" type="java.lang.Integer" %>
<%@ attribute name="targetUrl" required="false" type="java.lang.String" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose> 
  <c:when test="${cmsSite.uid eq 'sga'}">
    <c:set var="maxQty" value="${orderEntry.maxQty}"/>
    <c:set var="oninput" value='oninput="ACC.savedcarts.onChange(this, ${itemIndex});"' />
    <c:set var="onclick" value='onclick="ACC.savedcarts.onChange(this, ${itemIndex});"' />
  </c:when>
  <c:otherwise>
    <c:set var="maxQty" value="100"/>
    <c:set var="oninput" value="" />
    <c:set var="onclick" value="" />
  </c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${(orderEntry.product.isExcluded != null && orderEntry.product.isExcluded) || !orderEntry.product.active ||  !orderEntry.product.approved}">
		<c:set var="isProductUnAvailble" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="isProductUnAvailble" value="false" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${cmsSite.uid eq 'sga' && orderEntry.product.stock != null && orderEntry.product.stock.stockLevelStatus != null && orderEntry.product.stock.stockLevelStatus.code eq 'outOfStock'}">
		<c:set var="isOutofStock" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="isOutofStock" value="false" />
	</c:otherwise>
</c:choose>

<c:set var="entryStock" value="${fn:escapeXml(orderEntry.product.stock.stockLevelStatus.code)}" />
<c:url value="${orderEntry.product.url}" var="productUrl"/>
<c:set value="" var="linkDisabled" />
<c:if test="${fn:length(savedCartData.templateEntry) eq 1}">
    <c:set value=" link-disable" var="linkDisabled" />
</c:if>

<c:set var="isQuickOrderTemplate" value="false"/>
<c:if test="${order.isQuickOrder}">
    <c:set var="isQuickOrderTemplate" value="true"/>
</c:if> 

<c:set value="${ (request.getParameter('sort') == 'A-Z' || empty request.getParameter('sort')) ? 'A-Z' : 'Z-A' }" var="queryParam" />

<li class="item__list--item order-template-item">
    <!--For Desktop-->
    <div class="desktop-viw row">
        <!-- image -->
        <div class="col-xs-4 col-sm-1 col-md-1 image-tablet-fix no-padding-left">
            <div class="item__image no-padding-left <c:if test= "${isProductUnAvailble}"> product-unavailable-image</c:if>">
                <c:choose>
                    <c:when test="${isProductUnAvailble}">
                        <a>
                    </c:when>
                    <c:otherwise>
                        <a href="${productUrl}">
                    </c:otherwise>
                </c:choose>
                <div class="thumb">
                    <c:if test="${orderEntry.product.dealsFlag eq true}" >
                        <div class="plp-deals-img deals-thumb">Deal</div>
                        <div id="sga-deals-tooltip-content" class="hide hidden">
                            <c:forEach items="${orderEntry.product.dealsTitle}" var="title">
                                <div class="item"><b>Deal: </b>${title}</div>
                            </c:forEach>
                        </div>
                    </c:if>
                    <product:productPrimaryImage product="${orderEntry.product}" format="thumbnail"/>
                </div>
                </a>
                <br/>
                <div class="item__removeall visible-xs">
                    <a href="#" class="js-ordertemplate_entry_delete${linkDisabled}" data-sort="${queryParam}" data-ordertemplateentry-id="${fn:escapeXml(orderEntry.pk)}" data-ordertemplate-id="${fn:escapeXml(order.code)}">
                        <spring:theme code="basket.page.entry.action.REMOVE" />
                    </a>
                </div>
            </div>
        </div>
		
        <!-- product name, code, promotions -->
        <div class="col-xs-8 col-sm-3 col-md-4 pull-xs-left-15">
            <div class="row no-padding-tablet-top item__info">
                
                <ycommerce:testId code="cart_product_">
					<c:choose>
						<c:when test="${isProductUnAvailble}">
							<a>
						</c:when>
						<c:otherwise>
							<a href="${productUrl}">
						</c:otherwise>
					</c:choose>
                        <ycommerce:testId code="cart_product_name">
                            <c:if test= "${cmsSite.uid eq 'sga'}">
                                <div class = "sga-product-code">${fn:escapeXml(orderEntry.product.code)}&nbsp;
                                    <c:if test= "${isProductUnAvailble}">
                                        <span class="product-unavailable-text">&mdash;&nbsp;<spring:theme code="sga.product.unavailable" /></span>
                                    </c:if>
                                </div>
                            </c:if>
                            <c:if test= "${(cmsSite.uid eq 'apb') && (isProductUnAvailble)}">
                                <span class="product-unavailable-text"><spring:theme code="sga.product.unavailable" /><br></span>
                            </c:if>
                            <span class="item__name brand__name">${fn:escapeXml(entry.product.apbBrand.name )}</span>
                        </ycommerce:testId>
                        <span class="item__name">${fn:escapeXml(orderEntry.product.apbBrand.name)}</span>
                        <span id="unitVolume" class="hidden-xs">${fn:escapeXml(orderEntry.product.name)}</span>
                    </a>
                    <p class="item_cart_product">
                        <c:if test="${not empty orderEntry.product.name || not empty orderEntry.product.unitVolume.name || not empty orderEntry.product.packageSize.name  }">
							<c:choose>
								<c:when test="${isProductUnAvailble}">
									<a>
								</c:when>
								<c:otherwise>
									<a href="${productUrl}">
								</c:otherwise>
							</c:choose>
							    <span id="unitVolume" class="visible-xs">${fn:escapeXml(orderEntry.product.name)}</span>
		                        <span id="packType" class="inline-block">${fn:escapeXml(orderEntry.product.unitVolume.name)}</span>
		                        <span id="packSize" class="inline-block">${fn:escapeXml(orderEntry.product.packageSize.name)}</span>
	                        </a>
                        </c:if>
                       
                    </p>
                     <c:choose>
							<c:when test="${orderEntry.product.stock !=null && orderEntry.product.stock.stockLevelStatus eq 'lowStock'}">
									<span class="cart_low_stock_status_name"> ${orderEntry.product.stock.stockLevelStatusName} </span>
							</c:when>
							<c:when test="${orderEntry.product.stock !=null && orderEntry.product.stock.stockLevelStatus eq 'outOfStock'}">
									<span class="cart_no_stock_status_name"> ${orderEntry.product.stock.stockLevelStatusName} </span>
							</c:when>
						</c:choose>
					<c:if test ="${cmsSite.uid ne 'sga'}">
	                    <span class="cart-page-out-of-stock"> 
                    		<c:if test="${entry.productOutOfStock}">
                            	<spring:theme htmlEscape="false" code="basket.page.product.out.of.stock" />
                        	</c:if>
	                     </span>
                    </c:if>
                </ycommerce:testId>
            </div>
        </div>

        <!-- price -->
        <div class="col-xs-8 col-sm-1 col-md-3 template-price-tablet-fix pl-xs-0 pull-xs-left-15">
            <div class="item__price">
                <%-- <span class="hidden-sm hidden-md hidden-lg"><spring:theme code="basket.page.itemPrice"/>:</span> --%>
                <ycommerce:testId code="orderDetails_productItemPrice_label">
                     <c:choose>
                        <c:when test="${orderEntry.priceUpdated}">
                            ${orderEntry.templateBasePrice.formattedValue}
                        </c:when>
                        <c:otherwise>
                            NA
                        </c:otherwise>
                     </c:choose>
                </ycommerce:testId>
            </div>
        </div>

        <!-- for small device only -->
        <div class="col-xs-4 visible-xs"></div>

        <!-- quantity -->
        <div class="col-xs-8 col-sm-3 col-md-3 template-quantity-tablet-fix pull-xs-left-10">
            <c:set var="qtyMinus" value="1" />
            <div class="addtocart-component add-to-cart-template-fix mt-xs-0">
                <div class="qty-selector input-group js-keg-qty-selector <c:if test= "${isProductUnAvailble}"> product-unavailable-container</c:if>">
                    <span class="input-group-btn" style>
                        <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                        <button
                            class="btn btn-default js-qty-selector-minus disable-spinner"
                            type="button"
                            ${onclick}
                            <c:if test="${orderEntry.quantity == 0 || isProductUnAvailble || isOutofStock}">disabled="disabled"</c:if>>
                            <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
                        </button>
                    </span>

                    <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                    <input
                        type="text"
                        maxlength="3"
                        style="display:inline"
                        <c:if test= "${isProductUnAvailble || isOutofStock}">disabled</c:if>
                        class="form-control js-qty-selector-input <c:if test= "${isProductUnAvailble || isOutofStock}">js-qty-selector-input-disabled</c:if>"
                        size="1"
                        value="${orderEntry.quantity}"
                        data-max="${maxQty}"
                        original-val="0"
                        data-min="1"
                        name="templateEntries[${itemIndex}].qty"
                        id="templateAddtoCartInput-${itemIndex}"
                        ${oninput} />

                    <span class="input-group-btn">
                        <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                        <button
                            class="btn btn-default js-qty-selector-plus disable-spinner"
                            ${onclick}
                            <c:if test= "${isProductUnAvailble || isOutofStock}">disabled</c:if>
                            style="display:inline"
                            type="button">
                            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                        </button>
                    </span>

                    <input type="hidden" name="templateEntries[${itemIndex}].entryPK" id="entryPK" value="${orderEntry.pk}" />
                </div>
                <c:if test="${cmsSite.uid eq 'sga' && !isQuickOrderTemplate}">
                    <div class="input-group block text-left">
                        <button
                            type="button"
                            id="js-saved-order-template-${itemIndex}"
                            data-index="${itemIndex}"
                            class="cursor px-0 textButton"
                            style="font-size: 14px;"
                            disabled="disabled"
                            onclick="ACC.savedcarts.bindSaveOrderTemplate(this);">Save Quantity</button>
                        <div class="save-cart-success success-${itemIndex} pt-0 hidden">Quantity Saved.</div>
                        <div class="save-cart-success fail-${itemIndex} pt-0 hidden">Quantity Save failed.</div>
                    </div>
                </c:if>
            </div>
        </div>
        <div class="col-sm-1 col-md-1 float-right hidden-xs">
            <div class="float-right">
                <c:if test="${!isQuickOrderTemplate}" >
                    <a href="#" class="js-ordertemplate_entry_delete${linkDisabled}" data-sort="${queryParam}" data-ordertemplateentry-id="${fn:escapeXml(orderEntry.pk)}" data-ordertemplate-id="${fn:escapeXml(order.code)}">
                        <spring:theme code="basket.page.entry.action.REMOVE" />
                    </a>
                </c:if>
            </div>
        </div>

        <div class="cart-total-update visible-xs">
            <ycommerce:testId code="cart_totalProductPrice_label">
                <div id="template-price" class="item__total js-item-total pull-right">
                    ${orderEntry.templateTotalPrice.formattedValue}
                </div>
            </ycommerce:testId>
        </div>
    </div> <!-- End of item -->

    <div class="row">
        <div class="col-sm-8 col-sm-push-1 col-md-10 deal-seperator"></div>
    </div>
    <div class="clear visible-xs"></div>
</li>
    