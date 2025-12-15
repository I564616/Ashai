<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="orderEntry" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryData" %>
<%@ attribute name="consignmentEntry" required="false"
              type="de.hybris.platform.commercefacades.order.data.ConsignmentEntryData" %>
<%@ attribute name="itemIndex" required="true" type="java.lang.Integer" %>
<%@ attribute name="targetUrl" required="false" type="java.lang.String" %>
<%@ attribute name="showStock" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showViewConfigurationInfos" required="false" type="java.lang.Boolean" %>
<%@ attribute name="viewConfigurationInfosBaseUrl" required="false" type="java.lang.String" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="grid" tagdir="/WEB-INF/tags/responsive/grid" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="varShowStock" value="${(empty showStock) ? true : showStock}" />
<c:set var="defaultViewConfigurationInfosBaseUrl" value="/my-account/order" />
<c:set var="displayFreeForZero" value="false" />

<c:choose>
	<c:when test="${orderEntry.orderEntryStatus == 'BONUS'}">
	<c:set var="displayFreeForZero" value="true" />
	</c:when>
	<c:when test="${orderEntry.orderEntryStatus == 'NOTSUPPLIED' || orderEntry.orderEntryStatus == 'CANCELLED'}">
    	<c:set var="noPrice" value="false" />
    </c:when>
	<c:when test="${order.orderType == 'Online' }">
		<c:set var="noPrice" value="false" />
	</c:when>
    <c:when test="${order.orderType != 'Online' && order.status != 'COMPLETED'}">
        <c:set var="noPrice" value="true" />
    </c:when>
    
    <c:otherwise>
        <c:set var="noPrice" value="false" />
    </c:otherwise>
</c:choose>
<c:if test="${cmsSite.uid eq 'sga'}">
	<c:set var="noPrice" value="false" />
</c:if>
<c:url value="${orderEntry.product.url}" var="productUrl" />
<c:set var="entryStock" value="${fn:escapeXml(orderEntry.product.stock.stockLevelStatus.code)}" />
<c:choose>
	<c:when test="${itemIndex+1 gt viewAllQuantity}">
		<c:choose>
			<c:when test="${cmsSite.uid eq 'apb' and orderEntry.isBonusStock}">
				<li class="item__list--item checkoutCartShowToggle bonus-stock-checkout hide">
			</c:when>
			<c:otherwise>
				<li class="item__list--item checkoutCartShowToggle hide">
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="${cmsSite.uid eq 'apb' and orderEntry.isBonusStock}">
		<li class="item__list--item bonus-stock-checkout">
	</c:when>
	<c:otherwise>
		<li class="item__list--item">
	</c:otherwise>
</c:choose>

    <div class="media mobile-table-fix">
        <div class="media-body">
            <%-- product name, code, promotions --%>
            <div class="col-sm-3 col-md-3">
                <div class="order-history-table-val order-history-prod-name">
                    <ycommerce:testId code="orderDetails_productName_link">
                        <a href="${orderEntry.product.purchasable ? productUrl : ''}">
                        	<c:if test="${cmsSite.uid eq 'sga'}">
								<div class = "sga-product-code">${fn:escapeXml(orderEntry.product.code)}&nbsp;
									<c:if test= "${orderEntry.product.isExcluded != null && orderEntry.product.isExcluded.booleanValue()}">
										<span class="product-unavailable-text">&mdash;&nbsp;<spring:theme code="sga.product.unavailable" /></span>
									</c:if>
								</div>
							</c:if>
                            <strong><span class="">${fn:escapeXml(orderEntry.product.apbBrand.name)}</span></strong>
                            ${fn:escapeXml(orderEntry.product.name)} 
                            <br>
                		 <span class="pack-size-val">${fn:escapeXml(orderEntry.product.unitVolume.name)}</span>${fn:escapeXml(orderEntry.product.packageSize.name)}
                        </a>
                        <p class="cart-page-out-of-stock">
                            <c:if test="${entryData.wetNotIncluded}">
                                <spring:theme code="order.detail.entry.wet.excluded" />
                            </c:if>
                        </p>
                    </ycommerce:testId>
                    <%-- availability --%>
                    </%-->

                    <common:configurationInfos entry="${orderEntry}" />
                    <c:if test="${empty showViewConfigurationInfos || showViewConfigurationInfos eq true}">
                        <common:viewConfigurationInfos baseUrl="${empty viewConhvfigurationInfosBaseUrl ? defaultViewConfigurationInfosBaseUrl : viewConfigurationInfosBaseUrl}" orderEntry="${orderEntry}" itemCode="${order.code}" />
                    </c:if>         
                </div>
            </div>
             <c:if test = "${!isNAPGroup}">
            <div class="col-xs-12 col-sm-2 col-md-2 order-history-vertical-align">
                <%-- price --%>
                <div class="order-history-table-val">
                    <span class="order-history-label col-xs-6 no-padding hidden-sm hidden-md hidden-lg"><spring:theme code="basket.page.itemPrice"/>:</span>
                    <c:choose>
	                    <c:when test="${orderEntry.isBonusStock}">
	                    	<ycommerce:testId code="orderDetails_productItemPrice_label">
	                    		<strong><spring:theme code="order.entry.bonus.text"/></strong>
	                    	 </ycommerce:testId>
	                    </c:when>
	                    <c:otherwise>
	                      <ycommerce:testId code="orderDetails_productItemPrice_label">
                       	 	<order:orderEntryPrice orderEntry="${orderEntry}" noPriceAvailable="${noPrice}" displayFreeForZero="${displayFreeForZero}" />
                    	  </ycommerce:testId>
	                     </c:otherwise>                    
                    </c:choose>
                </div>
            </div>
            </c:if>
            
            <div class="col-xs-12 col-sm-2 col-md-2 order-history-vertical-align">
                <%-- status --%>
                </%-->
                <div class="order-history-table-val">
                    <ycommerce:testId code="orderDetails_productQuantity_label">
                        <span class="order-history-label  col-xs-6 no-padding hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.orderHistoryListing.orderStatus"/>:</span>
                        <span class="qtyValue">
                        <c:choose>
	                        <c:when test="${orderEntry.isBonusStock and not empty orderEntry.orderEntryStatus}">
	                        	<span class=""><spring:theme code="order.detail.entry.status.${orderEntry.orderEntryStatus}"/></span>
	                        </c:when>
                        	<c:when test="${not empty orderEntry.orderEntryStatus}">
                        		<spring:theme code="order.detail.entry.status.${orderEntry.orderEntryStatus}"/>
                        	</c:when>
                        </c:choose>
                        </span>
                    </ycommerce:testId>
                </div>
            </div>

            <div class="col-xs-12 col-sm-2 col-md-2 order-history-vertical-align">
                <%-- quantity --%>
                <div class="order-history-table-val history-qty history-qty-val">
                    <ycommerce:testId code="orderDetails_productQuantity_label">
                        <span class="order-history-label col-xs-6 no-padding hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.order.qty"/>:</span>
                        <span class="qtyValue">
                            <c:choose>
                                <c:when test="${consignmentEntry ne null }">
                                    ${consignmentEntry.quantity}
                                </c:when>
                                <c:otherwise>
                                    ${orderEntry.quantity}
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </ycommerce:testId>
                </div>
            </div>
            
            <div class="col-xs-12 col-sm-1 col-md-2 order-history-vertical-align">
                <%-- delivered quantity --%>
                </%-->
                <div class="order-history-table-val">
                    <ycommerce:testId code="orderDetails_productQuantity_label">
                        <span class="order-history-label col-xs-6 no-padding hidden-sm hidden-md hidden-lg"><spring:theme code="basket.page.deliveredqty"/>:</span>
                       <c:choose>
                       	<c:when test="${cmsSite.uid eq 'sga' && order.status == 'COMPLETED'}">
                       		${orderEntry.quantity}
                       	</c:when>
                       	<c:otherwise>
                       		<span class="qtyValue">
                            ${orderEntry.invoicedQty}
                       		</span>
                       	</c:otherwise>
                       </c:choose>
                    </ycommerce:testId>
                </div>
            </div>
             <c:if test = "${!isNAPGroup}">
            <div class="col-xs-12 col-sm-2 col-md-1 order-history-vertical-align">
                <%-- total --%>
                <div class="order-history-table-val order-history-total">
                    <ycommerce:testId code="orderDetails_productTotalPrice_label">
                    		<c:choose>
	                        <c:when test="${orderEntry.isBonusStock}">
								<span class="order-history-label col-xs-6 no-padding hidden-sm hidden-md hidden-lg"><spring:theme code="basket.page.total"/>:</span>
	                        	<span class=""><strong><spring:theme code="order.entry.bonus.text"/></strong></span>
	                        </c:when>
                        	<c:when test="${not empty orderEntry.orderEntryStatus && cmsSite.uid eq 'apb'}">
                        		<span class="order-history-label col-xs-6 no-padding hidden-sm hidden-md hidden-lg"><spring:theme code="basket.page.total"/>:</span>
                        		<c:choose>
	                       			<c:when test="${orderEntry.orderEntryStatus == 'NOTSUPPLIED'}">
	                       				0
	                       			</c:when>
	                       			<c:otherwise>
	                       				<format:price priceData="${orderEntry.totalPrice}" displayFreeForZero="${displayFreeForZero}" noPriceAvailable="${noPrice}"/>
	                       			</c:otherwise>	                       
	                      		</c:choose>
                        	</c:when>
                        	<c:otherwise>
                        		<span class="order-history-label col-xs-6 no-padding hidden-sm hidden-md hidden-lg"><spring:theme code="basket.page.total"/>:</span>
                        		<format:price priceData="${orderEntry.totalPrice}" displayFreeForZero="${displayFreeForZero}" noPriceAvailable="${noPrice}"/>
                        	</c:otherwise>
                        </c:choose>                        
                    </ycommerce:testId>
                </div>
            </div>
            </c:if>
        </div>
    </div>
</li>
<c:if test="${not empty orderEntry.asahiDealTitle and not empty orderEntry.freeGoodEntryQty}">
	<li class="item__list--item">
        <div class="media mobile-table-fix">
            <div class="media-body">
                <!--Deal title-->
                <div class="col-sm-3 pt-10"><b>Deal: </b>${orderEntry.asahiDealTitle}</div>
                <!--Deal Price-->
                <c:if test="${!isNAPGroup}" >
                    <div class="col-xs-12 col-sm-2 col-md-2 order-history-vertical-align"><spring:theme code="sga.deal.price.free"/></div>
                </c:if>
                <div class="col-xs-12 col-sm-2 col-md-2 order-history-vertical-align"></div>
                <!--Deal qty-->
                <div class="col-xs-12 col-sm-2 col-md-2 order-history-vertical-align">${fn:escapeXml(orderEntry.freeGoodEntryQty)}</div>
                <div class="col-xs-12 col-sm-1 col-md-2 order-history-vertical-align"></div>
                <c:if test="${isNAPGroup}" >
                    <!-- Add extra div to align table on NAP user-->
                    <div class="col-xs-12 col-sm-1 col-md-2 order-history-vertical-align"></div>
                </c:if>
                <!--Deal price-->
                <div class="col-xs-12 col-sm-2 col-md-1 order-history-vertical-align order-history-total"><spring:theme code="sga.deal.price.free"/></div>
            </div>
        </div>
	</li>
</c:if>

    <c:if test="${empty targetUrl}">
        <spring:url value="/my-account/order/{/orderCode}/getReadOnlyProductVariantMatrix" var="targetUrl">
            <spring:param name="orderCode" value="${order.code}" />
        </spring:url>
    </c:if>
    <grid:gridWrapper entry="${orderEntry}" index="${itemIndex}" styleClass="display-none add-to-cart-order-form-wrap" targetUrl="${targetUrl}" />
</li>