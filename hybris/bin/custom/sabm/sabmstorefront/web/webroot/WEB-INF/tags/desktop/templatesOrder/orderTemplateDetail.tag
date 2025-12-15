<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="templatesOrder" tagdir="/WEB-INF/tags/desktop/templatesOrder"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<div class="row">
	<div class="editable-field-container clearfix margin-bottom-0 col-md-6 col-sm-6 col-xs-12">
		<form><input type="hidden" name="targetField" id="templateTitleInput"/></form>
		<div>
			<h1 id="templateTitle" class="editable-field editable-field-inline inactive margin-bottom-0 break-word" 
			title="<spring:theme code='text.orderTemplateDetail.click.edit' />">${orderTemplate.name}</h1>
			<span class="glyphicon glyphicon-pencil position-absolute"></span>
		</div>
	</div>
	<div class="col-md-6 col-sm-6 col-xs-12 padding-right-35">
		<div class="template-print text-right">
				<a href="/your-business/ordertemplate/print/${orderTemplate.code}" target="_blank" class="inline">
						<spring:theme code="text.orderTemplateDetail.print.template"/>
				</a>
		</div>
		<div class="template-delete text-right">
				<a href="#delete-template-order" class="regular-popup magnific-delete-template inline">
					<spring:theme code="text.orderTemplateDetail.delete.template"/>
				</a>
		</div>
	</div>
	<div class="row col-xs-12 col-sm-7 drag-and-drop visible-md-inline visible-lg-inline">
        <span class="col-xs-1 col-lg-1">
            	<svg class="icon-drag-drop">
                               <use xlink:href="#icon-drag-drop"></use>
                           </svg>
									
        </span>
        <span class="text">
            <spring:theme code="text.account.order.dragAndDrop" text="Drag and drop" />
            </span>
    </div>
</div>
<input type="hidden" id="removeTemplateEntryNumbers" />
<input type="hidden" id="orderTemplateCode" value="${orderTemplate.code}" />

<div class="row templates-order-table">
	<div class="table-header hidden-xs clearfix">
		<div class="col-sm-6 col-md-7">
			<div class="col-md-3 visible-md-block visible-lg-block">
				<span><spring:theme code="text.orderTemplateDetail.table.header.image"/></span>
			</div>
			<div class="col-md-7 product-title trim-left-small">
				<span>
					<spring:theme code="text.orderTemplateDetail.table.header.product"/>
				</span>
			</div>
		</div>
		<div class="col-sm-6 col-md-5 trim-all">
			<div class="col-md-2 col-sm-2 trim-both">
				<div class="min-stock-format">
					<spring:theme code="text.orderTemplateDetail.table.header.minStockOnHand"/>
				</div>
			</div>
			<div class="col-xs-5 col-sm-8 col-md-8 trim-right text-center">
				<span>
					<spring:theme code="text.orderTemplateDetail.table.header.quantity"/>
				</span>
			</div>
		</div>
	</div>

	<c:if test="${not empty orderTemplate}" >
		<c:forEach items="${orderTemplate.entries}" var="entry" varStatus="loop">
			<product:productPackTypeAllowed unit="${entry.unit.code}"/>
			<div class="table-row clearfix productImpressionTag<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>" data-product-code="${entry.product.code}"  data-order-code="${orderTemplate.code}"  data-entry-number="${entry.entryNumber}">
				<input type="hidden" class="entryNumber" value="${entry.entryNumber}">
				<input type="hidden" class="sequenceNumber" value="${entry.sequenceNumber}">
				<div class="col-xs-12 col-sm-6 col-md-7">
					<div class="">
						<div class="col-md-3 visible-md-block visible-lg-block">
							<a href="${entry.product.url}" class="js-track-product-link" 
								data-currencycode="${entry.product.price.currencyIso}"
								data-name="${fn:escapeXml(entry.product.name)}"
								data-id="${entry.product.code}"
								data-price="${entry.product.price.value}"
								data-brand="${fn:escapeXml(entry.product.brand)}"
								data-category=<c:choose>
													<c:when test="${not empty entry.product.categories}">
										 				"${fn:escapeXml(entry.product.categories[fn:length(entry.product.categories) - 1].name)}"
										 			</c:when>   
										 			<c:otherwise>
										 				""
										 			</c:otherwise>
											   </c:choose>
								data-variant=<c:choose>
												<c:when test="${empty entry.product.uomList}">
									 				"${entry.product.unit}"
									 			</c:when>
									 			<c:otherwise>
									 				"${entry.product.uomList[0].name}"
									 			</c:otherwise>
										   </c:choose>
								data-position="${loop.count}"
								data-url="${entry.product.url}"
								data-actionfield="${fn:escapeXml(requestOrigin)}"
								data-list="${fn:escapeXml(requestOrigin)}"
				            	data-dealsflag="${entry.product.dealsFlag}"><product:productPrimaryImage product="${entry.product}" format="thumbnail"/></a>
						</div>
						<div class="col-md-7 trim-left-small line-height-initial break-word">
							<a href="${entry.product.url}" class="js-track-product-link"
								data-currencycode="${entry.product.price.currencyIso}"
								data-name="${fn:escapeXml(entry.product.name)}"
								data-id="${entry.product.code}"
								data-price="${entry.product.price.value}"
								data-brand="${fn:escapeXml(entry.product.brand)}"
								data-category=<c:choose>
													<c:when test="${not empty entry.product.categories}">
										 				"${fn:escapeXml(entry.product.categories[fn:length(entry.product.categories) - 1].name)}"
										 			</c:when>   
										 			<c:otherwise>
										 				""
										 			</c:otherwise>
											   </c:choose>
								data-variant=<c:choose>
												<c:when test="${empty entry.product.uomList}">
									 				"${entry.product.unit}"
									 			</c:when>
									 			<c:otherwise>
									 				"${entry.product.uomList[0].name}"
									 			</c:otherwise>
										   </c:choose>
								data-position="${loop.count}"
								data-url="${entry.product.url}"
								data-actionfield="${fn:escapeXml(requestOrigin)}"
								data-list="${fn:escapeXml(requestOrigin)}"
				            	data-dealsflag="${entry.product.dealsFlag}"><strong><span>${entry.product.name}</span></strong>
							<span>${entry.product.packConfiguration}</span></a>
							<c:if test="${entry.product.cubStockStatus == 'outOfStock'}">
								<div class="out-of-stock-status-label"><spring:theme code="product.page.stockstatus.oos"/></div>
								<input type="hidden" id="${entry.entryNumber}_isOutOfStock" class="isOutOfStock" value="true">
							</c:if>
							<c:if test="${entry.product.cubStockStatus == 'lowStock'}">
								<div class="low-stock-status-label"><spring:theme code="product.page.stockstatus.low"/></div>
							</c:if>
						</div>


                        <%--<div class="col-xs-6"><a href="" class="removeProductTemplate"><spring:theme code="text.orderTemplateDetail.table.item.remove"/></a></div>--%>
					</div>
				</div>
				<div class="col-xs-12 col-sm-6 col-md-5 trim-all margin-top-10-xs">
					<div>
                        <!-- Minimum Stock on Hand - TODO -->
                        <div class="row col-md-2 col-sm-2 trim-left-small margin-left-auto display-flex offset-bottom-xsmall
									padding-left-5 padding-right-5">
							<div class="hidden-lg hidden-md hidden-sm col-sm-7 col-xs-6 line-height-initial trim-left <c:if test="${entry.product.cubStockStatus == 'outOfStock'}">disabled-productOutofStock</c:if>">
								<spring:theme code="text.orderTemplateDetail.table.header.minStockOnHand"/>
							</div>
								<input type="text" 
									class="form-control col-sm-5 col-xs-2 text-center trim-0 minSOH"
									id="minimumStockOnHand" 
									name="minimumStockOnHand" model="minimumStockOnHand" <c:if test="${entry.product.cubStockStatus == 'outOfStock'}">disabled</c:if> value="${entry.minimumStockOnHand}"
									maxlength="3" oninput="this.value=this.value.replace(/[^0-9]/g,'');">
                        </div>
						<div class="hidden-lg hidden-md hidden-sm col-xs-2 trim-left">
							<span>
								<spring:theme code="text.orderTemplateDetail.table.header.quantity"/>
							</span>
						</div>
						<div class="col-md-10 col-xs-10 trim-right">
							<div class="remove-icon-center">
								<div class="col-xs-6 trim-right-5 <c:if test="${entry.product.cubStockStatus == 'outOfStock'}">disabled-productOutofStock</c:if>">
									<ul class="select-quantity">
										<li class="down">
											<svg class="icon-minus">
											    <use xlink:href="#icon-minus"></use>    
											</svg>
										</li>
										<li>
											<input name="qtyInput" maxlength="3" size="1" class="qty-input qty-counter" type="text"
												value="${entry.quantity}" data-minqty="0">
										</li>
										<li class="up">
											<svg class="icon-plus">
											    <use xlink:href="#icon-plus"></use>    
											</svg>
										</li>
									</ul>
								</div>
								<div class="col-xs-5 trim-left-5 <c:if test="${entry.product.cubStockStatus == 'outOfStock'}">disabled-productOutofStock</c:if>">
									<div class="select-list">
										<c:if test="${not empty entry.product.uomList}">
											<c:choose>
												<c:when test="${fn:length(entry.product.uomList) eq 1}">
													<div class="select-single">${entry.product.uomList[0].name}</div>
												</c:when>
												<c:otherwise>
													<div class="select-btn sort" data-value="${entry.unit.code}">${entry.unit.name}</div>
													<ul class="select-items">
														<c:forEach items="${entry.product.uomList}" var="uom">
															<li data-value="${uom.code}">${uom.name}</li>
														</c:forEach>
													</ul>
												</c:otherwise>
											</c:choose>
										</c:if>
									</div>
								</div>
								<span class="product-remove">
                  <a class="removeProductTemplate" href="">
                    <svg class="icon-cross">
                        <use xlink:href="#icon-cross"></use>    
                    </svg>
                  </a>
								</span>
							</div>
								<c:choose>
					                <c:when test="${!isProductPackTypeAllowed}">
					                    <div class="btn btn-primary btn-invert btn-block btn-changeDeliveryDate disabled-productPackTypeNotAllowed"><spring:theme code="basket.change.delivery.date"/></div>
					                </c:when>
									<c:when test="${not empty entry.suggestedQty && entry.suggestedQty > 0}" >
										<div class="suggested-qty">
											<span><spring:theme code='text.suggested.qty.label' arguments="${entry.suggestedQty}" /></span>
											<i class="icon icon-price-info" rel="tooltip" placement="auto bottom" title="<spring:theme code='text.suggested.qty.tooltip' />"></i>
										</div>
									</c:when>
					            </c:choose>
						</div>
					</div>
				</div>

				<%-- Max order quantity error message --%>
                <div class="col-xs-12">
                    <div class="col-xs-12">
                        <div class="order-error-message order-error-message-${entry.product.code}"></div>
                    </div>
                </div>
			</div>
		</c:forEach>
	</c:if>
</div>
<templatesOrder:orderTemplateDetailActions cssClass="template-actions-bottom"/>
<br>
<templatesOrder:templateOrderPopup templateCode="${orderTemplate.code}"/>
<common:addItemsPopup isAddToCartTemplate="true"/>
