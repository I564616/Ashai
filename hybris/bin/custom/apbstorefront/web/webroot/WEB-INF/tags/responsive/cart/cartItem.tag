<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="entry" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryData" %>
<%-- <%@ attribute name="pakagetype" required="true" type="com.apb.facades.product.data.PackageTypeData" %> --%>
<%@ attribute name="index" required="false" type="java.lang.Integer"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="grid" tagdir="/WEB-INF/tags/responsive/grid" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>



<%--
    Represents single cart item on cart page
 --%>
 


<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="qtyMinus" value="1" />
<c:set var="errorStatus" value="<%= de.hybris.platform.catalog.enums.ProductInfoStatus.valueOf(\"ERROR\") %>" />
<c:set var="entryNumber" value="${entry.entryNumber}"/>
<c:if test="${empty index}">
    <c:set property="index" value="${entryNumber}"/>
</c:if>

<c:set var="isForceInStock" value="${entry.product.stock.stockLevelStatus.code eq 'inStock' and empty entry.product.stock.stockLevel}"/>
<c:choose> 
  <c:when test="${isForceInStock}">
    <c:set var="maxQty" value="FORCE_IN_STOCK"/>
  </c:when>
  <c:otherwise>	
  	<c:choose>
  		<c:when test="${not empty entry.product.maxQty}">
  			<c:set var="maxQty" value="${entry.product.maxQty}"/>
  		</c:when>
 		<c:when test="${not empty defaultMaxQuantity}">
 			<c:set var="maxQty" value="${defaultMaxQuantity}"/>
 		</c:when>
 		<c:otherwise>
 			<c:set var="maxQty" value="FORCE_IN_STOCK"/>
 		</c:otherwise>
  	</c:choose>
  </c:otherwise> 	
</c:choose>
<c:if test="${not empty entry}">

        <c:if test="${not empty entry.statusSummaryMap}" >
            <c:set var="errorCount" value="${entry.statusSummaryMap.get(errorStatus)}"/>
            <c:if test="${not empty errorCount && errorCount > 0}" >
                <div class="notification has-error">
                    <spring:theme code="basket.error.invalid.configuration" arguments="${errorCount}"/>
                    <a href="<c:url value="/cart/${entry.entryNumber}/configuration/${ycommerce:encodeUrl(entry.configurationInfos[0].configuratorType)}" />" >
                        <spring:theme code="basket.error.invalid.configuration.edit"/>
                    </a>
                </div>
            </c:if>
        </c:if>
        <c:set var="showEditableGridClass" value=""/>
        <c:url value="${entry.product.url}" var="productUrl"/>
	
		<c:choose>
			<c:when test="${cmsSite.uid eq 'apb' and asmMode ne null and asmMode eq 'true' and entry.isBonusStock}">
				<li class="item__list--item bonus-stock-cart">
			</c:when>
			<c:otherwise>
				<li class="item__list--item no-border">
			</c:otherwise>
		</c:choose>
			
            <%-- product image --%>
            <!--  For Mobile-->
            <div class="hidden-sm hidden-md hidden-lg">
				<div class="media">
				  <div class="item__image media-left no-padding-left ${entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()?'product-unavailable-image':''}">
				      <a href="${productUrl}"><product:productPrimaryImage product="${entry.product}" format="thumbnail"/></a>
				      <c:if test="${entry.product.isPromotionActive}">
				      	<div class="cart-promotion-img"><spring:theme code="sga.product.promotion.image.text"/></div>
				      </c:if>
				      <div class="item__removeall cart-remove-box">
             			<form:form id="cartEntryActionForm" action="" method="post" >
             			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                             <%-- Build entry numbers string for execute action -- Start --%>
                             
                            <c:choose>
					            <c:when test="${entry.entryNumber eq -1}"> <%-- for multid entry --%>
					                <c:forEach items="${entry.entries}" var="subEntry" varStatus="stat">
						    			<c:set var="actionFormEntryNumbers" value="${stat.first ? '' : actionFormEntryNumbers.concat(';')}${subEntry.entryNumber}" />
						    		</c:forEach>
					            </c:when>
					            <c:otherwise>
					                <c:set var="actionFormEntryNumbers" value="${entry.entryNumber}" />
					            </c:otherwise>
					        </c:choose>
					        <%-- Build entry numbers string for execute action -- End --%>
                            <c:forEach var="entryAction" items="${entry.supportedActions}">
                                <c:url value="/cart/entry/execute/${entryAction}" var="entryActionUrl"/>
                                <div class="js-execute-entry-action-button" id="actionEntry_${fn:escapeXml(entryNumber)}"
                                    data-entry-action-url="${entryActionUrl}"
                                    data-entry-action="${fn:escapeXml(entryAction)}"
                                    data-entry-product-code="${fn:escapeXml(entry.product.code)}"
                                    data-entry-initial-quantity="${entry.quantity}"
                                    data-action-entry-numbers="${actionFormEntryNumbers}">
                                    <a href="#"><spring:theme code="basket.page.entry.action.${entryAction}"/></a>
                                </div>
                            </c:forEach>
                       </form:form>
                         </div>
                      
				      
				  </div>
				  <div class="media-body">
				    <!-- <h4 class="media-heading">Media heading</h4> -->
				    <div class="item__info">
				    	<c:if test="${cmsSite.uid eq 'sga'}">
	                         <div class = "sga-product-code">${entry.product.code}
	                       <c:if test= "${entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()}">
                             <span class="product-unavailable-text"> -- <spring:theme code="sga.product.unavailable" /></span>
                            </c:if></div>
                        </c:if>
 						 <ycommerce:testId code="cart_product_name">
	                   		 <span class="item__name brand__name">${fn:escapeXml(entry.product.apbBrand.name)}</span>
	                	</ycommerce:testId>
	                	 
		                <ycommerce:testId code="cart_product_">
		                	<a href="${productUrl}"><span class="item__name">${fn:escapeXml(entry.product.name)}</span></a>
			                	<div id="unitVolume">${fn:escapeXml(entry.product.unitVolume.name)}</div>
	                			&nbsp;&nbsp;&nbsp;&nbsp;
	                			<div id="packSize">${fn:escapeXml(entry.product.packageSize.name)}</div>
                	
                			<c:if test ="${cmsSite.uid ne 'sga'}">
	                			<div class="cart-page-out-of-stock"> <c:if
										test="${entry.productOutOfStock}">
										<spring:theme htmlEscape="false" code="basket.page.product.out.of.stock" />
									</c:if>
								</div>
                			</c:if>
							
		                </ycommerce:testId>
		                </div>
		                <div class="item__price">
			                <%-- <span class="visible-xs"><spring:theme code="basket.page.itemPrice"/>: </span> --%>
			                <c:choose>
			                	<c:when test="${cmsSite.uid eq 'sga'}">
			                		<c:if test="${entry.product.isPromotionActive && entry.basePrice !=null && entry.basePrice.formattedValue != '$0.00' && entry.basePrice.formattedValue ne entry.discountPrice.formattedValue}">
										<span class="price_strike pull-left"><format:price priceData="${entry.basePrice}" showNAIfPriceError="true" priceUpdated="${entry.calculated}"/></span>&nbsp;
                					</c:if>
                					<c:if test="${entry.discountPrice !=null}">
                						<format:price priceData="${entry.discountPrice}" showNAIfPriceError="true" priceUpdated="${entry.calculated}"/>
                					</c:if>
                					<c:if test="${entry.discountPrice eq null }">
                						NA
                					</c:if>
			                	</c:when>
			                	<c:otherwise>
			                	 <c:choose>
			                		  <c:when test="${entry.isBonusStock.booleanValue() and asmMode ne null and asmMode eq 'true'}">
	                        			<strong><span class=""><spring:theme code="order.entry.bonus.text"/></span></strong>
	                        		</c:when>
	                        	<c:otherwise>
	                        		<format:price priceData="${entry.basePrice}" displayFreeForZero="true" showNAIfPriceError="true" priceUpdated="${entry.calculated}"/>
			                		</c:otherwise>
			                	 </c:choose>
			                	</c:otherwise>
			                </c:choose>
           			 	</div>
		                <div class="item__price">
							<c:choose>

								<c:when test="${entry.product.stock.stockLevelStatus eq 'lowStock'}">
										<span class="cart_low_stock_status_name"> ${entry.product.stock.stockLevelStatusName} </span>
								</c:when>
								<c:when test="${entry.product.stock.stockLevelStatus eq 'outOfStock'}">
										<span class="cart_no_stock_status_name"> ${entry.product.stock.stockLevelStatusName} </span>
								</c:when>
							</c:choose>
						</div>
						
           			 	<div class="item__quantity">
                    <c:if test="${not entry.product.multidimensional}" >
                        <c:url value="/cart/update" var="cartUpdateFormAction" />
                       <form:form id="updateCartForm${entryNumber}" action="${cartUpdateFormAction}" method="post" modelAttribute="updateQuantityForm${entry.entryNumber}"
                                   class="js-qty-form${entryNumber} pull-left"
                                    data-cart='{"cartCode" : "${fn:escapeXml(cartData.code)}","productPostPrice":"${entry.basePrice.value}","productName":"${fn:escapeXml(entry.product.name)}"}'>
                        	 <input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
                            <input type="hidden" name="productCode" value="${fn:escapeXml(entry.product.code)}"/>
                            <input type="hidden" name="initialQuantity" value="${entry.quantity}"/>
                            
                            <div class="qty-selector input-group js-cart-qty-selector">
											<span class="input-group-btn">
												<button  class="btn btn-default js-cart-qty-selector-minus ${entry.product.stock.stockLevelStatus eq 'outOfStock' || entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()?'product-unavailable-container disabled':''}" type="button" ><span class="glyphicon glyphicon-minus" aria-hidden="true" ></span></button>
											</span>

												<span id="updateCartInput">
													<form:input onfocus="this.value = this.value;" cssClass="form-control js-cart-qty-selector-input js-update-entry-quantity-input ${entry.product.stock.stockLevelStatus eq 'outOfStock' || entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()?'product-unavailable-container':''}" disabled="${not entry.updateable}" type="text" size="1" data-max="${maxQty}" data-min="1" id="quantity_${entryNumber}"  path="quantity" />
												</span>						

											<span class="input-group-btn">
												<button  class="btn btn-default js-cart-qty-selector-plus ${entry.product.stock.stockLevelStatus eq 'outOfStock' || entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()?'product-unavailable-container disabled':''}" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
											</span>
									</div>
		                   
								 </form:form>
								 
								 
								
                    </c:if>		                 
                        <div class="clearfix"></div>
                        <div class="cart-total-update" >
										
							<c:choose>
								<c:when test="${!entry.isBonusStock}">
									<a href="javascript:void(0)" class="cart-total-update-js pull-left"><spring:theme code="basket.page.product.update.link" /></a>
								</c:when>
								<c:otherwise>
<!--									<a href="javascript:void(0)" id="${fn:escapeXml(entry.product.code)}" class="cart-total-update-js cart-bonus-update-js pull-left"><spring:theme code="basket.page.product.update.link" /></a><br>-->
									<button class="btn" type="submit">FFF</button>
								</c:otherwise>
							</c:choose>

		            		<ycommerce:testId code="cart_totalProductPrice_label">
                            <div class="item__total js-item-total pull-right">
                            		<c:choose>
				                			<c:when test="${cmsSite.uid eq 'sga' && entry.discountPrice !=null}">
				                				<format:price priceData="${entry.totalPrice}" displayFreeForZero="false" showNAIfPriceError="true" priceUpdated="${entry.calculated}"/> 
				                			</c:when>
				                			<c:when test="${cmsSite.uid eq 'sga' && entry.discountPrice eq null}">
				                				NA
				                			</c:when>
				                			<c:otherwise>
				                				 <c:choose>
			                		  				<c:when test="${entry.isBonusStock.booleanValue() and asmMode ne null and asmMode eq 'true'}">
	                        						<strong><span class=""><spring:theme code="order.entry.bonus.text"/></span></strong>
	                        					</c:when>
	                        					<c:otherwise>
	                        							<format:price priceData="${entry.totalPrice}" displayFreeForZero="false" showNAIfPriceError="true" priceUpdated="${entry.calculated}"/> 
				                					</c:otherwise>
			                					 </c:choose>
				                				</c:otherwise>			                			
			                		</c:choose>
                            </div>
                        </ycommerce:testId>
                        <div class="clearfix"></div>
                        <span class="updatedProductMessage hide"></span>
                        
		            	</div>
		            </div>
		            
		           
  				  </div>
  				    
			</div>
			
			
			
			
            </div>
             <!--For Desktop-->
            
             <div class="hidden-xs">
                 <div class="col-sm-1 col-md-1 cart-values image-tablet-fix no-padding-left">
                <div class="item__image no-padding-left ${entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()?'product-unavailable-image':''}">
                <!-- Switch between the image format for APB & SGA -->
                <c:choose>
                	<c:when test="${cmsSite.uid eq 'sga'}">
                		<a href="${productUrl}"><product:productPrimaryImage product="${entry.product}" format="cartIcon"/></a>
                		<c:if test="${entry.product.isPromotionActive}">
				      		<div class="cart-promotion-img"><spring:theme code="sga.product.promotion.image.text"/></div>
				      	</c:if>
                	</c:when>
                	<c:otherwise>
                		<a href="${productUrl}"><product:productPrimaryImage product="${entry.product}" format="thumbnail"/></a>
                	</c:otherwise>
                </c:choose>
                    
                 </div></div>
             <%-- product name, code, promotions --%>
            <div class="col-sm-3 col-md-4 cart-values no-padding">
            <div class="row no-padding-tablet-top item__info">
                <ycommerce:testId code="cart_product_">
                	<a href="${productUrl}">
                		<c:if test="${cmsSite.uid eq 'sga'}">
	                         <div class = "sga-product-code">${entry.product.code}
	                          <c:if test= "${entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()}">
                              <span class="product-unavailable-text"> -- <spring:theme code="sga.product.unavailable" /></span>
                            </c:if></div>
                        </c:if>
                       
                		<ycommerce:testId code="cart_product_name">
                    		<span class="item__name brand__name">${fn:escapeXml(entry.product.apbBrand.name )}</span>
                    		 
               	 	</ycommerce:testId>
                		<span class="item__name product__name__title">${fn:escapeXml(entry.product.name)}</span>
                	</a> 
                	<c:if test ="${not empty  entry.product.unitVolume.name || entry.product.packageSize.name }">
	                	<a href="${productUrl}">
							<div id="unitVolume">${fn:escapeXml(entry.product.unitVolume.name)}</div>
		                	<div id="packSize">${fn:escapeXml(entry.product.packageSize.name)}</div>
		                </a>
	                </c:if>

                	<c:if test ="${cmsSite.uid ne 'sga'}">
						<div class="cart-page-out-of-stock"> <c:if
								test="${entry.productOutOfStock}">
								<spring:theme htmlEscape="false" code="basket.page.product.out.of.stock" />
							</c:if>
						</div>
					</c:if>
					
					<c:if test="${cmsSite.uid eq 'apb' and asmMode ne null and asmMode eq 'true'}">
						<!-- bonus Stock -->
						<c:url value="/cart/add?action=addBonus" var="bonusActionUrl" />
						<c:url value="/cart/add?action=bonus" var="setbonusActionUrl" />
						
						<c:if test="${!entry.isBonusStock}">								
								<form id="addBonusToCartForm${fn:escapeXml(entry.product.code)}" action="${setbonusActionUrl}" method="post" class="add_bonus_to_cart_form">
									<div class="addToCartForm${fn:escapeXml(entry.product.code)}">
										<input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="cartqty_${entryNumber} qty js-bonus-qty-input js-qty-selector-input" value="${entry.quantity}">
									</div>
									<input type="hidden" class="bonus-stock-plp" name="productCodePost" value="${fn:escapeXml(entry.product.code)}" id="${fn:escapeXml(entry.product.code)}" />
									<input type="hidden" name="productNamePost" value="${fn:escapeXml(entry.product.name)}"/>
									<input type="hidden" name ="${CSRFToken.parameterName}" value="${CSRFToken.token}"/>
									<div class="js-execute-bonus-action-cart" id="bonusAction_${fn:escapeXml(entryNumber)}">
										<button class="site-anchor-link" <c:if test="${entry.isBonusLineAvailable || entry.product.active eq false}"> disabled="disabled" id="disabled-link"</c:if> type="submit">
											<spring:theme code="apb.bonus.stock.cart.set" />
										</button>
									</div>
								</form>

								&nbsp; | &nbsp;

								<form id="addBonusToCartForm${fn:escapeXml(entry.product.code)}" action="${bonusActionUrl}" method="post" class="add_bonus_to_cart_form add-as-bonus">
									<div class="addToCartForm${fn:escapeXml(entry.product.code)}">
										<input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty js-qty-selector-input" value="1">
									</div>
									<input type="hidden" class="bonus-stock-plp" name="productCodePost" value="${fn:escapeXml(entry.product.code)}" id="${fn:escapeXml(entry.product.code)}" />
									<input type="hidden" name="productNamePost" value="${fn:escapeXml(entry.product.name)}"/>
									<input type="hidden" name ="${CSRFToken.parameterName}" value="${CSRFToken.token}"/>
									<div class="js-execute-bonus-action-cart" id="bonusAction_${fn:escapeXml(entryNumber)}">
										<button class="site-anchor-link" <c:if test="${entry.isBonusLineAvailable || entry.product.active eq false}"> disabled="disabled" id="disabled-link"</c:if> type="submit">
											<spring:theme code="apb.bonus.stock.cart.add" />
										</button>
									</div>
								</form>
							</c:if>
							
					</c:if>
                </ycommerce:testId>
				


                <c:if test="${ycommerce:doesPotentialPromotionExistForOrderEntryOrOrderEntryGroup(cartData, entry)}">
                    <c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
                        <c:set var="displayed" value="false"/>
                        <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                            <c:if test="${not displayed && ycommerce:isConsumedByEntry(consumedEntry,entry) && not empty promotion.description}">
                                <c:set var="displayed" value="true"/>

                                    <div class="promo">
                                         <ycommerce:testId code="cart_potentialPromotion_label">
                                             ${ycommerce:sanitizeHTML(promotion.description)}
                                         </ycommerce:testId>
                                    </div>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </c:if>
                <c:if test="${ycommerce:doesAppliedPromotionExistForOrderEntryOrOrderEntryGroup(cartData, entry)}">
                    <c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
                        <c:set var="displayed" value="false"/>
                        <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                            <c:if test="${not displayed && ycommerce:isConsumedByEntry(consumedEntry,entry) }">
                                <c:set var="displayed" value="true"/>
                                <div class="promo">
                                    <ycommerce:testId code="cart_appliedPromotion_label">
                                        ${ycommerce:sanitizeHTML(promotion.description)}
                                    </ycommerce:testId>
                                </div>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </c:if>

                <c:if test="${entry.product.configurable}">
                    <div class="hidden-xs hidden-sm">
                        <spring:url value="/cart/{/entryNumber}/configuration/{/configuratorType}" var="entryConfigUrl" htmlEscape="false">
                            <spring:param name="entryNumber"  value="${entry.entryNumber}"/>
                            <spring:param name="configuratorType"  value="${entry.configurationInfos[0].configuratorType}" />
                        </spring:url>
                        <div class="item__configurations">
                            <c:forEach var="config" items="${entry.configurationInfos}">
                                <c:set var="style" value=""/>
                                <c:if test="${config.status eq errorStatus}">
                                    <c:set var="style" value="color:red"/>
                                </c:if>
                                <div class="item__configuration--entry" style="${style}">
                                    <div class="row">
                                        <div class="item__configuration--name col-sm-4">
                                                ${fn:escapeXml(config.configurationLabel)}
                                                <c:if test="${not empty config.configurationLabel}">:</c:if>

                                        </div>
                                        <div class="item__configuration--value col-sm-8">
                                                ${fn:escapeXml(config.configurationValue)}
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        <c:if test="${not empty entry.configurationInfos}">
                            <div class="item__configurations--edit">
                                <a class="btn" href="${entryConfigUrl}"><spring:theme code="basket.page.change.configuration"/></a>
                            </div>
                        </c:if>
                    </div>
                </c:if>
				
            </div>
					<div>
						<span class="stock_pull" id="price_ns_{{= code}}"></span>
						<span class="price_strike pull-right" id="price_ls_{{= code}}"></span>
						<c:choose>
							<c:when test="${entry.product.stock.stockLevelStatus eq 'lowStock'}">
								<span class="pull-left cart_low_stock_status_name"> ${entry.product.stock.stockLevelStatusName} </span>
							</c:when>
							<c:when test="${entry.product.stock.stockLevelStatus eq 'outOfStock'}">
								<span class="pull-left cart_no_stock_status_name"> ${entry.product.stock.stockLevelStatusName} </span>
							</c:when>
						</c:choose>

						<div class="clearfix"></div>

					</div>
                </div>
				
				<%--ADDED  SKU ID --%>
				<%-- <div class="item__skuid">
					<span class="visible-xs visible-sm"><spring:theme code="basket.page.skuid"/>: </span>
               ${fn:escapeXml(entry.product.code)}	
            </div> --%>
            <%-- ADDED SKU ID  --%>
                
            <%-- price --%>
            <div class="col-sm-1 col-md-2 price-tablet-fix cart-values no-padding">
            <div class="item__price">
<!--               <%--  <span class="visible-sm"><spring:theme code="basket.page.itemPrice"/>: </span> --%>-->
                <%-- SGA BONUS ITEM --%>
               <c:choose>
	               	<c:when test="${cmsSite.uid eq 'sga'}">
	               	    <c:choose>
	               	        <c:when test="${entry.isBonusStock.booleanValue()}">
	               	            <strong><span class=""><spring:theme code="order.entry.bonus.text"/></span></strong>
	               	        </c:when>
	               	        <c:otherwise>
	               	            <c:if test="${entry.product.isPromotionActive && entry.basePrice !=null && entry.basePrice.formattedValue != '$0.00' && entry.basePrice.formattedValue ne entry.discountPrice.formattedValue}">
                                            <span class="price_strike pull-left"><format:price priceData="${entry.basePrice}" showNAIfPriceError="true" priceUpdated="${entry.calculated}"/></span>&nbsp;
                                        </c:if>
                                <c:if test="${entry.discountPrice !=null}">
                                    <format:price priceData="${entry.discountPrice}" showNAIfPriceError="true" priceUpdated="${entry.calculated}"/>
                                </c:if>
                                <c:if test="${entry.discountPrice eq null }">
                                N/A
                                </c:if>
	               	        </c:otherwise>
	               	    </c:choose>
	               	</c:when>
	               	<c:otherwise>
	               		 <c:choose>
                             <c:when test="${entry.isBonusStock.booleanValue() and asmMode ne null and asmMode eq 'true'}">
                                <strong><span class=""><spring:theme code="order.entry.bonus.text"/></span></strong>
                             </c:when>
                            <c:otherwise>
                                <format:price priceData="${entry.basePrice}" displayFreeForZero="true" showNAIfPriceError="true" priceUpdated="${entry.calculated}"/>
                            </c:otherwise>
                         </c:choose>
	                </c:otherwise>
               </c:choose>
            </div></div>
            <!-- UOM  -->
            <%--  <div class="item__price">
                <span class="visible-xs visible-sm"><spring:theme code="basket.page.uom"/>: </span>
               ${entry.product.packageType.name}
            </div> --%>
                
            <%-- quantity --%>
            <div class="col-sm-3 col-md-2 no-padding cart-quantity cart-values">
                <div class="item__quantity hidden-xs">
                        <c:if test="${not entry.product.multidimensional}" >
                            <c:url value="/cart/update" var="cartUpdateFormAction" />
                           <form:form id="updateCartForm${entryNumber}" action="${cartUpdateFormAction}" method="post" modelAttribute="updateQuantityForm${entry.entryNumber}"
                                       class="js-qty-form${entryNumber}"
                                        data-cart='{"cartCode" : "${fn:escapeXml(cartData.code)}","productPostPrice":"${entry.basePrice.value}","productName":"${fn:escapeXml(entry.product.name)}"}'>
                                 <input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
                                <input type="hidden" name="productCode" value="${fn:escapeXml(entry.product.code)}"/>
                                <input type="hidden" name="initialQuantity" value="${entry.quantity}"/>

                                <div class="qty-selector input-group js-cart-qty-selector">
                                                <span class="input-group-btn">
                                                    <button  class="btn btn-default js-cart-qty-selector-minus ${entry.product.stock.stockLevelStatus eq 'outOfStock' || entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()?'product-unavailable-container disabled':''}" type="button" ><span class="glyphicon glyphicon-minus" aria-hidden="true" ></span></button>
                                                </span>
												<c:choose>
													<c:when test="${!entry.isBonusStock}">
														<span id="updateCartInput">
															<form:input  cssClass="form-control js-cart-qty-selector-input js-update-entry-quantity-input ${entry.product.stock.stockLevelStatus eq 'outOfStock' || entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()?'product-unavailable-container':''}" disabled="${not entry.updateable}" type="text" size="1" data-max="${maxQty}" data-min="1" id="quantity_${entryNumber}"  path="quantity" onfocus="this.value = this.value;" />
														</span>	
													</c:when>
													<c:otherwise>
														<span id="updateCartInput">
															<form:input  cssClass="form-control js-bonusproduct-cart-input js-cart-qty-selector-input js-update-entry-quantity-input ${entry.product.stock.stockLevelStatus eq 'outOfStock' || entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()?'product-unavailable-container':''}" disabled="${not entry.updateable}" type="text" size="1" data-max="${maxQty}" data-min="1" id="quantity_${entryNumber}"  path="quantity" onfocus="this.value = this.value;" />
														</span>	
													</c:otherwise>
												</c:choose>			
                                                <span class="input-group-btn">
                                                    <button  class="btn btn-default js-cart-qty-selector-plus ${entry.product.stock.stockLevelStatus eq 'outOfStock' || entry.product.isExcluded != null && entry.product.isExcluded.booleanValue()?'product-unavailable-container disabled':''}" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
                                                </span>
                                        </div>

                                     </form:form>

                        </c:if>
                        <div class="cart-total-update" >					
							<c:choose>
								<c:when test="${!entry.isBonusStock}">
									<a href="javascript:void(0)" class="cart-total-update-js pull-left"><spring:theme code="basket.page.product.update.link" /></a><br>
								</c:when>
								<c:otherwise>
									<a href="javascript:void(0)" class="cart-total-update-js cart-bonus-update-js pull-left"><spring:theme code="basket.page.product.update.link" />
									<input type="hidden" class="initialQuantity" name="initialQuantity" value="${entry.quantity}"/>
									<input type="hidden" class="productQty" name="productQty" value="${fn:escapeXml(entry.product.code)}"/>
									</a><br>
									
								</c:otherwise>
							</c:choose>
                          
                            <p class="updatedProductMessage hide"></p>
                        </div>
                        <%-- Removed the multi dimentional part start--%>

                                <%-- <c:otherwise>
                                    <c:url value="/cart/updateMultiD" var="cartUpdateMultiDFormAction" />
                                    <form:form id="updateCartForm${entryNumber}" action="${cartUpdateMultiDFormAction}" method="post" class="js-qty-form${entryNumber}" modelAttribute="updateQuantityForm${entryNumber}">
                                        <input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
                                        <input type="hidden" name="productCode" value="${fn:escapeXml(entry.product.code)}"/>
                                        <input type="hidden" name="initialQuantity" value="${entry.quantity}"/>
                                        <label class="visible-xs visible-sm"><spring:theme code="basket.page.qty"/>:</label>
                                        <span class="qtyValue"><c:out value="${entry.quantity}" /></span>
                                        <input type="hidden" name="quantity" value="0"/>
                                        <ycommerce:testId code="cart_product_updateQuantity">
                                            <div id="QuantityProduct${entryNumber}" class="updateQuantityProduct"></div>
                                        </ycommerce:testId>
                                    </form:form>
                                </c:otherwise>
                            </c:choose> --%>
                    </div>
                </div>

					<%-- Removed the multi dimentional part end--%>
                        
            <%-- total --%>
            <div class="col-sm-1 col-md-2 cart-values no-padding">
                <ycommerce:testId code="cart_totalProductPrice_label">
                    <div class="item__total js-item-total hidden-xs">
                    	<c:choose>
                    	    <%-- SGA BONUS TOTAL --%>
                    	    <c:when test="${cmsSite.uid eq 'sga' && entry.isBonusStock.booleanValue()}">
                                <strong><span class=""><spring:theme code="order.entry.bonus.text"/></span></strong>
                            </c:when>
	                		<c:when test="${cmsSite.uid eq 'sga' && entry.discountPrice !=null}">
	                			<format:price priceData="${entry.totalPrice}" displayFreeForZero="false" showNAIfPriceError="true" priceUpdated="${entry.calculated}"/>
	                		</c:when>
	                		<c:when test="${cmsSite.uid eq 'sga' && entry.discountPrice eq null}">
	                			NA
	                		</c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${entry.isBonusStock.booleanValue() and asmMode ne null and asmMode eq 'true'}">
                                        <strong><span class=""><spring:theme code="order.entry.bonus.text"/></span></strong>
                                    </c:when>
                                    <c:otherwise>
                                         <format:price priceData="${entry.totalPrice}" displayFreeForZero="false" showNAIfPriceError="true" priceUpdated="${entry.calculated}"/>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
	                	</c:choose>
                    </div> 
                </ycommerce:testId>
            </div>
                
            <div class="col-sm-1 col-md-1 float-right cart-quantity cart-values no-padding-right}"> 
            <div class="float-right">
                <form:form id="cartEntryActionForm" action="" method="post" >
                     <%-- Build entry numbers string for execute action -- Start --%>
                 <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <c:choose>
                        <c:when test="${entry.entryNumber eq -1}"> <%-- for multid entry --%>
                            <c:forEach items="${entry.entries}" var="subEntry" varStatus="stat">
                                <c:set var="actionFormEntryNumbers" value="${stat.first ? '' : actionFormEntryNumbers.concat(';')}${subEntry.entryNumber}" />
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:set var="actionFormEntryNumbers" value="${entry.entryNumber}" />
                        </c:otherwise>
                    </c:choose>
                    <%-- Build entry numbers string for execute action -- End --%>
                    <c:forEach var="entryAction" items="${entry.supportedActions}">
                        <c:url value="/cart/entry/execute/${entryAction}" var="entryActionUrl"/>
                        <div class="js-execute-entry-action-button text-right" id="actionEntry_${fn:escapeXml(entryNumber)}"
                            data-entry-action-url="${entryActionUrl}"
                            data-entry-action="${fn:escapeXml(entryAction)}"
                            data-entry-product-code="${fn:escapeXml(entry.product.code)}"
                            data-entry-initial-quantity="${entry.quantity}"
                            data-action-entry-numbers="${actionFormEntryNumbers}">
                            <a href="javascript:void(0)"><spring:theme code="basket.page.entry.action.${entryAction}"/></a>
                        </div>
                    </c:forEach>

             </form:form>
                        </div></div>

            <%-- <div class="item__quantity__total visible-xs visible-sm">
            
            	Removed the multi dimentional part start
                <c:if test="${entry.product.multidimensional}" >
                    <ycommerce:testId code="cart_product_updateQuantity">
                        <c:set var="showEditableGridClass" value="js-show-editable-grid"/>
                    </ycommerce:testId>
                </c:if>
                Removed the multi dimentional part end
                <div class="details ${showEditableGridClass}" data-index="${entryNumber}" data-read-only-multid-grid="${not entry.updateable}">
                    <div class="qty">
                    	
                        
                            <c:if test="${not entry.product.multidimensional}" >
                                <c:url value="/cart/update" var="cartUpdateFormAction" />
                                 <form:form id="updateCartForm${entryNumber}" action="${cartUpdateFormAction}" method="post" modelAttribute="updateQuantityForm${entry.entryNumber}"
                                   class="js-qty-form${entryNumber}"
                                    data-cart='{"cartCode" : "${fn:escapeXml(cartData.code)}","productPostPrice":"${entry.basePrice.value}","productName":"${fn:escapeXml(entry.product.name)}"}'>
			                        	 <input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
			                            <input type="hidden" name="productCode" value="${fn:escapeXml(entry.product.code)}"/>
			                            <input type="hidden" name="initialQuantity" value="${entry.quantity}"/>
			                            
			                            <div class="qty-selector input-group js-qty-selector">
														<span class="input-group-btn">
															<button  class="btn btn-default js-qty-selector-minus" type="submit" ><span class="glyphicon glyphicon-minus" aria-hidden="true" ></span></button>
														</span>
															<form:label cssClass="visible-xs visible-sm" path="quantity" for="quantity${entry.entryNumber}"></form:label>
															<span id="updateCartInput">
																<form:input  cssClass="form-control js-qty-selector-input" disabled="${not entry.updateable}" type="text" size="1" data-max="${maxQty}" data-min="1" id="quantity_${entryNumber}"  path="quantity" />
															</span>						
														<span class="input-group-btn">
															<button  class="btn btn-default js-qty-selector-plus" type="submit"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
														</span>
												</div>
		                   
										</form:form>
                                
                            </c:if>
                            
                            Removed the multi dimentional part start
	                            <c:otherwise>
	                                <c:url value="/cart/updateMultiD" var="cartUpdateMultiDFormAction" />
	                                <form:form id="updateCartForm${entryNumber}" action="${cartUpdateMultiDFormAction}" method="post" class="js-qty-form${entryNumber}" modelAttribute="updateQuantityForm${entryNumber}">
	                                    <input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
	                                    <input type="hidden" name="productCode" value="${fn:escapeXml(entry.product.code)}"/>
	                                    <input type="hidden" name="initialQuantity" value="${entry.quantity}"/>
	                                    <label><spring:theme code="basket.page.qty"/>:</label>
	                                    <span class="qtyValue"><c:out value="${entry.quantity}" /></span>
	                                    <input type="hidden" name="quantity" value="0"/>
	                                    <ycommerce:testId code="cart_product_updateQuantity">
	                                        <div id="QuantityProduct${entryNumber}" class="updateQuantityProduct"></div>
	                                    </ycommerce:testId>
	                                </form:form>
	                            </c:otherwise>
	                        </c:choose>
	                        
	                        <c:if test="${entry.product.multidimensional}" >
	                            <ycommerce:testId code="cart_product_updateQuantity">
	                                <span class="glyphicon glyphicon-chevron-right"></span>
	                            </ycommerce:testId>
	                        </c:if>
                        Removed the multi dimentional part end
                        
                        <ycommerce:testId code="cart_totalProductPrice_label">
                            <div class="item__total js-item-total">
                                <format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/>
                            </div>
                        </ycommerce:testId>
                    </div>
               
                </div>
            </div> --%>
            </div>
        </li>

        

        
 </c:if>