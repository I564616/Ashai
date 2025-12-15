<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="quickorder" tagdir="/WEB-INF/tags/responsive/quickorder" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<c:url var="quickOrderUrl" value="/quickOrder/cart/addQuickOrder" scope="page"/>
	
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:theme code="product.grid.confirmQtys.message" var="gridConfirmMessage"/>

<spring:url value="/my-account/saved-carts/" var="savedCartsLink" htmlEscape="false"/>
<c:set var="searchUrl" value="/my-account/saved-carts?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/>	

<c:set value="" var="gridClass" />
<c:if test="${cmsSite.uid eq 'sga'}">
    <c:set value="mr-sm-10" var="gridClass" />
</c:if>

<template:page pageTitle="${pageTitle}">
<div id="quickOrder" class="account-section" data-grid-confirm-message="${gridConfirmMessage}">
	<div class="account-section-content quickorderpage">
		<div class="account-section-header user-register__headline secondary-page-title">
			<spring:theme code="text.quickOrder.header" />
		</div>
		<div class="account-orderhistory-label">
			<c:choose>
				<c:when test="${quickOrderData ne null && not empty quickOrderData}">
					<spring:theme code="sga.quickorder.previous.orders.message" arguments="${noOfOrders}, ${noOfMonths}" />
				</c:when>
				<c:otherwise>
					<spring:theme code="sga.quickorder.yettoplace.orders.message" />
					<br><br><spring:theme code="sga.quickorder.onceplaced.orders.message" arguments="${noOfOrders}, ${noOfMonths}" />
				</c:otherwise>
			</c:choose>
		</div><br>
		<c:if test="${quickOrderData ne null && not empty quickOrderData}">


			<div class="cart-actions--print checkoutcartsummary quickordersummary">
				<div class="cart__actions border cart-bottom-btn">
					<div class="row no-margin">
						<div class="col-xs-12 col-sm-5 col-md-3 col-md-offset-9 col-sm-offset-7 ">
							<form action="${quickOrderUrl}" class="quick-order-form" method="post">
								<button id="" type="submit" class="btn btn-primary btn-block" disabled="disabled">
										<img class="add-to-cart-icon" src="/storefront/_ui/responsive/common/images/white_cart.svg"  />&nbsp;&nbsp;
										<spring:theme code="text.quickOrder.addtocart" text="Add to Cart"/>
										<c:set var="testNum" value="${quickOrderData.entries}" />
										&nbsp;(<span class="totalQuickOrderProducts">0</span>)
								</button>
								<input type="hidden" name="numberOfProducts" id="numberOfProducts" value="${fn:length(quickOrderData.entries )}" />
								<input type="hidden" name="isCartEmpty" id="isCartEmpty" value="${isCartEmpty}" />
								<input type="hidden" name ="${CSRFToken.parameterName}" value="${CSRFToken.token}"/>
							</form>
						</div>
					</div>
				</div>
			</div>
	
			<div class="pagination-bar  top ">
				<div class="pagination-toolbar">
					<div class="helper clearfix hidden-md hidden-lg"></div>
					<div class="sort-refine-bar">
						<div class="row">
							<div class="col-xs-12 col-sm-12 col-md-12">
								<div class="row">
									<div class="col-xs-12 col-sm-12 col-md-12">
										<div class="prices-text">
											<span><spring:theme code="sga.text.quickOrder.product.showing" arguments="${fn:length(quickOrderData.entries)}" /></span>
										</div>
										<div class="form-group sort_by">
											<label class="control-label " for="sortForm1"><spring:theme code="sga.text.quickOrder.sorting.sort.by" /></label>
											<form id="sortForm1" name="sortForm1" method="get" action="">
												<ul id="sortOptions1" class="sortby_list">
													<li data-option="orderDate">
														<spring:theme code="sga.text.quickOrder.sorting.products.last.ordered" /> </li>
													<li id="nameSortOption" data-option="name">
														<spring:theme code="sga.text.quickOrder.sorting.product.name" /> </li>
												</ul>
												<input type="hidden" name="sort" value="orderDate">
											</form>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>		

			<div class="account-section-content ${gridClass}">
				<div class="account-overview-table saved__carts__overview--table c">
					<c:set var="cartIdRowMapping" value='' />
					<table class="responsive-table">
						<thead>
							<tr class="responsive-table-head hidden-xs">
								<th class="quickorder-product">
								</th>
								<c:forEach items="${quickOrderData.dateRange}" var="date" varStatus="loopCol">
									<th class="quickorder-date-heading">
										<a href="#" id="0z${loopCol.index + 1}" class="quick-order-add-js site-anchor-link <c:if test="${quickOrderData.allProductExcluded != null && quickOrderData.dateRangeExcluded[date]}">product-unavailable-container" style="opacity: 0.5</c:if>"<u>${date}
									</u></a>
									</th>
								</c:forEach>
								<th class="quickorder-addtocart-heading">

								</th>
							</tr>
						</thead>
						<c:forEach items="${quickOrderData.entries}" var="entry" varStatus="loopRow">
						<c:choose>
							<c:when test="${cmsSite.uid eq 'sga' && entry.stock != null && entry.stock.stockLevelStatus != null && entry.stock.stockLevelStatus.code eq 'outOfStock'}">
								<c:set var="isOutofStock" value="true" />
							</c:when>
							<c:otherwise>
								<c:set var="isOutofStock" value="false" />
							</c:otherwise>
						</c:choose>
							<tr class="quick-order-table">
								<td class="quickorder-product">
									<c:url value="${request.contextPath}${entry.url}" var="productUrl" />
									<a <c:if test= "$!{entry.isExcluded != null && entry.isExcluded.booleanValue()}"> href="${productUrl}" </c:if>>
										<div class="thumb">
											<img class="quick-order-product-image" src="${entry.image.url}" alt="${fn:escapeXml(entry.image.altText)}" title="${fn:escapeXml(entry.image.altText)}" />
											<c:if test="${entry.dealsFlag eq true}">
												<div class="plp-deals-img deals-thumb">Deal</div>
												<div id="sga-deals-tooltip-content" class="hide hidden">
													<c:forEach items="${entry.dealsTitle}" var="title">
														<div class="item"><b>Deal: </b>${title}</div>
													</c:forEach>
												</div>
											</c:if>
										</div>
										<div class="quickorder-product-details">
											<div class="sga-product-code">${fn:escapeXml(entry.code)}
												<input type="hidden" name="sgaProductCode" id="PC${loopRow.index + 1}" value="${fn:escapeXml(entry.code)}" />
												<c:if test= "${entry.isExcluded != null && entry.isExcluded.booleanValue()}">
											  		<span class="product-unavailable-text"> &mdash; <spring:theme code="sga.product.unavailable" /></span>
												</c:if>
											</div>
											<div><b>${fn:escapeXml(entry.brand)}&nbsp;</b>${fn:escapeXml(entry.name)}<br> 
											${fn:escapeXml(entry.portalUnitVolume)} <span class="hidden-sm hidden-md hidden-lg">&nbsp; | &nbsp;${fn:escapeXml(entry.packageSize.name)}</span></div>
											<div class="hidden-xs">${fn:escapeXml(entry.packageSize.name)}</div>
											<div class="hidden-sm hidden-md hidden-lg"><b><spring:theme code="sga.text.quickOrder.page.lastordered" /></b>&nbsp;${fn:escapeXml(entry.lastOrdered)}</div>
											<div class="hidden-sm hidden-md hidden-lg"><b><spring:theme code="sga.text.quickOrder.page.qty" /></b>&nbsp;${fn:escapeXml(entry.quantity)}</div>
										<c:choose>

												<c:when test="${entry.stock !=null && entry.stock.stockLevelStatus eq 'lowStock'}">
														<span class="cart_low_stock_status_name"> ${entry.stock.stockLevelStatusName} </span>
												</c:when>
												<c:when test="${entry.stock !=null && entry.stock.stockLevelStatus eq 'outOfStock'}">
														<span class="cart_no_stock_status_name"> ${entry.stock.stockLevelStatusName} </span>
												</c:when>
											</c:choose>
										</div>
									</a>
									<div class="hidden-sm hidden-md hidden-lg">
										<c:set var="qtyMinus" value="1" />
										<div class="addtocart-component add-to-cart-template-fix">
											<div class="qty-selector input-group js-keg-qty-selector <c:if test= "${entry.isExcluded != null && entry.isExcluded.booleanValue()}">product-unavailable-container</c:if>">
												<span class="input-group-btn" style>
											<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
												<button class="btn btn-default js-qty-selector-minus" style="display:inline" type="button" disabled="disabled"><span class="glyphicon glyphicon-minus" aria-hidden="true"></span></button>
												</span>
												<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
												<input type="text" id="B${loopRow.index + 1}" maxlength="3" style="display:inline" <c:if test= "${(entry.isExcluded != null && entry.isExcluded.booleanValue()) || isOutofStock}">disabled="disabled"</c:if> <c:if test="${quickOrderData.allProductExcluded != null && quickOrderData.allProductExcluded.booleanValue()}">disabled="disabled"</c:if> class="form-control js-qty-selector-input mobile-js-qty${loopRow.index + 1}" size="1" value="0" data-max="${fn:escapeXml(entry.maxQty)}" original-val="0" data-min="1" name="templateEntries[${itemIndex}].qty" id="templateAddtoCartInput" />
												<span class="input-group-btn">
											<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
												<button id="C${loopRow.index + 1}" class="btn btn-default js-qty-selector-plus" <c:if test= "${(entry.isExcluded != null && entry.isExcluded.booleanValue()) || isOutofStock}">disabled="disabled"</c:if> style="display:inline" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
												</span>
												<input type="hidden" name="templateEntries[${itemIndex}].entryPK" id="entryPK" value="${orderEntry.pk}" />
											</div>
										</div>
									</div>
								</td>
								
								<c:forEach items="${quickOrderData.dateRange}" var="date" varStatus="loopCol2">
									<td class="quickorder-date hidden-xs">
										<c:choose>
											<c:when test="${(entry.dateRange).containsKey(date)}">
												<a href="#" <c:if test= "${entry.isExcluded != null && entry.isExcluded.booleanValue()}">class="product-unavailable-container" style="opacity: 0.5""</c:if> id="${loopRow.index + 1}z${loopCol2.index + 1}" class="quick-order-add-js site-anchor-link"><u>${entry.dateRange[date]}</u></a>	
											</c:when>
											<c:otherwise>
												&mdash;
											</c:otherwise>
										</c:choose>
									</td>
								</c:forEach>

								<td class="quickorder-addtocart hidden-xs">
									<c:set var="qtyMinus" value="1" />
									<div class="addtocart-component add-to-cart-template-fix">
										<div class="qty-selector input-group js-keg-qty-selector <c:if test= "${entry.isExcluded != null && entry.isExcluded.booleanValue()}">product-unavailable-container</c:if>">
											<span class="input-group-btn" style>
										<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
											<button id="A${loopRow.index + 1}" class="btn btn-default js-qty-selector-minus" style="display:inline" type="button" disabled="disabled"><span class="glyphicon glyphicon-minus" aria-hidden="true"></span></button>
											</span>
											<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
											<input type="text" id="B${loopRow.index + 1}" maxlength="3" style="display:inline" <c:if test= "${(entry.isExcluded != null && entry.isExcluded.booleanValue()) || isOutofStock}">disabled="disabled"</c:if> <c:if test="${quickOrderData.allProductExcluded != null && quickOrderData.allProductExcluded.booleanValue()}">disabled="disabled"</c:if> class="form-control js-qty-selector-input desktop-js-qty${loopRow.index + 1}" size="1" value="0" data-max="${fn:escapeXml(entry.maxQty)}" original-val="0" data-min="1" name="templateEntries[${itemIndex}].qty" id="templateAddtoCartInput" />
											<span class="input-group-btn">
										<!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
											<button id="C${loopRow.index + 1}" class="btn btn-default js-qty-selector-plus" <c:if test= "${(entry.isExcluded != null && entry.isExcluded.booleanValue()) || isOutofStock}">disabled="disabled"</c:if> style="display:inline" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
											</span>
											<input type="hidden" name="templateEntries[${itemIndex}].entryPK" id="entryPK" value="${orderEntry.pk}" />
										</div>
									</div>
								</td>
								
							</tr>
						</c:forEach>
					</table>
					<div class="js-uploading-saved-carts-update" data-id-row-mapping="${cartIdRowMapping}" data-refresh-cart="${refreshSavedCart}" data-refresh-interval="${refreshSavedCartInterval}"></div>

				</div>
			</div>
			<br>
			<div class="cart-actions--print checkoutcartsummary quickordersummary">
				<div class="cart__actions border cart-bottom-btn">
					<div class="row no-margin">
						<div class="col-xs-12 col-sm-5 col-md-3 col-md-offset-9 col-sm-offset-7 ">
							<form action="${quickOrderUrl}" class="quick-order-form" method="post">
								<button id="" type="submit" class="btn btn-primary btn-block" disabled="disabled">
										<img class="add-to-cart-icon" src="/storefront/_ui/responsive/common/images/white_cart.svg"  />&nbsp;&nbsp;
										<spring:theme code="text.quickOrder.addtocart" text="Add to Cart"/>
										<c:set var="testNum" value="${quickOrderData.entries}" />
										&nbsp;(<span class="totalQuickOrderProducts">0</span>)
								</button>
								<input type="hidden" name="numberOfProducts" id="numberOfProducts" value="${fn:length(quickOrderData.entries )}" />
								<input type="hidden" name="isCartEmpty" id="isCartEmpty" value="true" /> 					<!-- ${isCartEmpty} -->
								<input type="hidden" name ="${CSRFToken.parameterName}" value="${CSRFToken.token}"/>
							</form>
						</div>
					</div>
				</div>
			</div>
		</c:if>
	</div>
</div>

<quickorder:quickOrderReorderModal savedCart="${quickOrderData}"/>

</template:page>
