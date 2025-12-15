<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="entryData" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryData" %>
<%@ attribute name="index" required="true" type="java.lang.Integer" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<spring:htmlEscape defaultHtmlEscape="true"/>
	    	<c:choose>
	    		<c:when test="${index gt viewAllQuantity}">
	    			<tr class="checkoutCartShowToggle hide">
	    		</c:when>
	    		<c:otherwise>
	    			<tr>
	    		</c:otherwise>
	    	</c:choose>

	           <td>
	            	<c:if test="${not empty entryData}">
						<c:choose>
							<c:when test="${cmsSite.uid eq 'apb' and asmMode ne null and asmMode eq 'true' and entryData.isBonusStock}">
								<li class="item__list--item bonus-stock-checkout">
							</c:when>
							<c:otherwise>
								<li class="item__list--item no-border">
							</c:otherwise>
						</c:choose>
<!--                                <%-- product name, code, promotions --%>-->
                            <div class="item__info checkout__page__li__item ${product.isBonusStock}">
								<ycommerce:testId code="cart_product_">
									<a href="${request.contextPath}/${entryData.product.url}">
										<ycommerce:testId code="cart_product_name">
											<c:if test="${cmsSite.uid eq 'sga'}">
												<div class = "sga-product-code">${fn:escapeXml(entryData.product.code)}&nbsp;
-													<c:if test="${entryData.product.isExcluded != null && entryData.product.isExcluded.booleanValue()}">
														<span class="product-unavailable-text">&mdash;&nbsp;<spring:theme code="sga.product.unavailable" /></span>
													</c:if>
												</div>
											</c:if>
											
											<strong><span class="item__name">${fn:escapeXml(entryData.product.apbBrand.name )}</span></strong>
											<span class="item__name product__name__title">${fn:escapeXml(entryData.product.name)}</span>
											<p class="item_cart_product">
												<c:if test="${not empty  entryData.product.unitVolume.name || entryData.product.packageSize.name }">
													<a href="${request.contextPath}/${entryData.product.url}">
														<span id="unitVolume">${fn:escapeXml(entryData.product.unitVolume.name)}</span>
														&nbsp;&nbsp;&nbsp;&nbsp;
														<span id="packSize">${fn:escapeXml(entryData.product.packageSize.name)}</span>
													</a>
												</c:if>
											</p>
												<c:choose>
													<c:when test="${entryData.product.stock.stockLevelStatus eq 'lowStock'}">
													<div class="checkout_low_stock_status_name">
														<span class="checkout_low_stock_status_name"> ${entryData.product.stock.stockLevelStatusName} </span>
													</div>
													</c:when>
													<c:when test="${entryData.product.stock.stockLevelStatus eq 'outOfStock'}">
													<div class="checkout_low_stock_status_name">
														<span class="checkout_no_stock_status_name"> ${entryData.product.stock.stockLevelStatusName} </span>
													</div>
													</c:when>
												</c:choose>
											
										</ycommerce:testId>
									</a>

									<c:if test="${cmsSite.uid ne 'sga'}">
										<span class="cart-page-out-of-stock">
																			<c:if test="${entryData.productOutOfStock}">
																				<spring:theme htmlEscape="false" code="basket.page.product.out.of.stock" />
																			</c:if>
																		</span>
									</c:if>
								</ycommerce:testId> 
                            </div>


<!--                                <%-- price --%>-->
                            <%-- SGA BONUS PRICE --%>
                            <div class="item__price checkout__page__li__item">
	                            	<c:choose>
						               	<c:when test="${cmsSite.uid eq 'sga'}">
						               	    <c:choose>
                                                <c:when test="${entryData.isBonusStock}">
                                                    <strong><span class=""><spring:theme code="order.entry.bonus.text"/></span></strong>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:if test="${entry.product.isPromotionActive && entry.basePrice !=null && entry.basePrice.formattedValue != '$0.00' && entry.basePrice.formattedValue ne entry.discountPrice.formattedValue}">
                                                        <span class="price_strike pull-left"><format:price priceData="${entry.basePrice}" showNAIfPriceError="true" priceUpdated="${entry.calculated}"/></span>&nbsp;
                                                    </c:if>
                                                    <c:if test="${entryData.discountPrice !=null}">
                                                        <format:price priceData="${entryData.discountPrice}" showNAIfPriceError="true"/>
                                                    </c:if>
                                                    <c:if test="${entryData.discountPrice eq null }">
                                                        NA
                                                    </c:if>
                                                </c:otherwise>
                                            </c:choose>
				                		</c:when>
						               	<c:otherwise>
						               		<c:choose>
						               			<c:when test="${entryData.isBonusStock and asmMode}">
						               				<b><spring:theme code="order.entry.bonus.text" /></b>
						               			</c:when>
						               			<c:otherwise>
						               				<format:price priceData="${entryData.basePrice}" showNAIfPriceError="true"/>
						               			</c:otherwise>
						               		</c:choose>
						               	</c:otherwise>
					               	</c:choose>   
                            </div>
<!--                                <%-- quantity --%>-->
                            <div id="checkout_qty" class="item__quantity checkout__page__li__item">
                                <span class="hidden-sm hidden-md hidden-lg" id="qty-field"><spring:theme code="basket.page.quantity"/>:</span>
                                <span class="item__name" id="checkout_table_text">${fn:escapeXml(entryData.quantity)}</span>
                            </div> 

<!--                                <%-- total --%>-->
                            <ycommerce:testId code="cart_totalProductPrice_label">
								<c:choose>
									<c:when test="${entryData.product.stock.stockLevelStatus eq 'lowStock' || entryData.product.stock.stockLevelStatus eq 'outOfStock'}">
										<div class="checkout_stock_status_padding item__total js-item-total checkout__page__li__item wrap-checkout-price" id="checkout_table_text">
											<span class="item__name checkout__price" id="checkout_table_text">
                                                <c:choose>
                                                    <c:when test="${cmsSite.uid eq 'sga' && entryData.isBonusStock}">
                                                        <strong><span class=""><spring:theme code="order.entry.bonus.text"/></span></strong>
                                                    </c:when>
                                                    <c:when test="${cmsSite.uid eq 'sga' && entryData.discountPrice ne null}">
                                                        <format:price priceData="${entryData.totalPrice}" showNAIfPriceError="true"/>
                                                    </c:when>
                                                    <c:when test="${cmsSite.uid eq 'sga' && entryData.discountPrice eq null}">
                                                        NA
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:choose>
                                                            <c:when test="${entryData.isBonusStock and asmMode}">
                                                                <b><spring:theme code="order.entry.bonus.text" /></b>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <format:price priceData="${entryData.totalPrice}" showNAIfPriceError="true"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:otherwise>
                                                </c:choose>
											</span>
										</div>
									</c:when>
	    							<c:otherwise>
										<div class="item__total js-item-total checkout__page__li__item wrap-checkout-price" id="checkout_table_text">
											<span class="item__name checkout__price" id="checkout_table_text">
											<c:choose>
											    <c:when test="${cmsSite.uid eq 'sga' && entryData.isBonusStock}">
                                                    <strong><span class=""><spring:theme code="order.entry.bonus.text"/></span></strong>
                                                </c:when>
                                                <c:when test="${cmsSite.uid eq 'sga' && entryData.discountPrice ne null}">
                                                    <format:price priceData="${entryData.totalPrice}" showNAIfPriceError="true"/>
                                                </c:when>
                                                <c:when test="${cmsSite.uid eq 'sga' && entryData.discountPrice eq null}">
                                                    NA
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${entryData.isBonusStock and asmMode}">
                                                            <b><spring:theme code="order.entry.bonus.text" /></b>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <format:price priceData="${entryData.totalPrice}" showNAIfPriceError="true"/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
											</span>
										</div>
	    							</c:otherwise>
								</c:choose>
                            </ycommerce:testId>   			
                        </li>
	            	</c:if>
	            </td>
	        </tr>
				<tr>
					<c:if test="${not empty entryData.asahiDealTitle and not empty entryData.freeGoodEntryQty}" >
						<td>
							<li class="item__list--item no-border">
								<div class="item__info checkout__page__li__item">
									<b>Deal: </b>${entryData.asahiDealTitle}
								</div>
								<div class="item__price checkout__page__li__item">
									<spring:theme code="sga.deal.price.free"/>
								</div>
								<div class="item__quantity checkout__page__li__item">
									${fn:escapeXml(entryData.freeGoodEntryQty)}
								</div>
								<div class="hidden-xs deal-price-free py-20">
									<spring:theme code="sga.deal.price.free"/>
								</div>
								<div class="hidden-sm hidden-md hidden-lg deal-price-free py-10">
									<spring:theme code="sga.deal.price.free"/>
								</div>
							</li>
						</td>
					</c:if>
				</tr>
