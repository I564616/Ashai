<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="templatesOrder" tagdir="/WEB-INF/tags/desktop/templatesOrder"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<c:url value="/your-business/orders" var="ordersRecentUrl" />
<c:url value="/your-business/orderAdd/" var="orderAddToCartUrl" scope="session" />
<input type="hidden" class="orderAddToCartUrl" value="${orderAddToCartUrl }">
<c:url value="/your-business/addTemplates/" var="addTemplatesUrl" />
<input type="hidden" class="addToTemplate-hide" value="${addTemplatesUrl}">
<section class="product-orders offset-bottom-small">
	<div class="row product-orders-and-template">
	<br/>
		<div class="col-sm-6">
			<h2 class="product-orders-title"><spring:theme code="text.home.title.ordersRecent" /></h2>
			<c:choose>
				<c:when test="${not empty orderHistoryList}">
					<c:forEach items="${orderHistoryList}" var="orderhistory" end="${maxOrderHistoryCount-1}">
						<c:url value="/your-business/order/${orderhistory.orderNo}" var="orderInLine" />
						<div class="product-orders-item">
							<div class="col-xs-6 col-md-4 col-lg-4 trim-right trim-left show-on-top btn-height">
								${orderhistory.date} - <a href="${orderInLine}" class="inline">${orderhistory.sapOrderNo}</a>
							</div>
							<div class="col-xs-6 col-md-5 col-lg-4 trim-left-5 trim-right btn-height">
								<span class="status status-${fn:toLowerCase(orderhistory.status)}"></span>
								<%-- <spring:theme code="text.account.order.status.display.${orderhistory.status}" text="${orderhistory.status}" /> --%>
								<c:choose>
									<c:when test="${isTrackDeliveryOrderFeatureEnabled and fn:toLowerCase(orderhistory.status) == 'dispatched'}">
										<spring:theme code="text.account.order.status.display.beingDispatched" />
									</c:when>								
									<c:otherwise>
										<spring:theme code="text.account.order.status.display.${orderhistory.status}" text="${orderhistory.status}" />
									</c:otherwise>
								</c:choose>
							</div>
							
							<div class="col-sm-12 col-md-3 col-lg-4 trim-both">
							
							<div class="clearfix"></div>
								<c:if test="${!isNAPGroup}">
								<a class="btn btn-primary btn-block bde-view-only btn-reorder-product" onclick="rm.responsivetable.orderAddtoCart('${orderhistory.orderNo}')" href="javascript:void(0);"><spring:theme code="text.orderTemplate.table.actions.cart" /></a>
							    </c:if>
							</div>
						</div>
					</c:forEach>
					<div class="product-orders-cta">
						<a href="${ordersRecentUrl}" class="link-cta ">
						<spring:theme code="text.home.linkText.viewAllOrdersRecent" /></a>
					</div>
				</c:when>
				<c:otherwise>
					<span class="btn-height"><spring:theme code="text.account.orderHistory.emptyOrderHistory" /></span>
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${!isNAPGroup}">
		<div class="col-sm-6">
			<h2 class="product-orders-title">
				<spring:theme code="text.home.title.yourTemplates" />
			</h2>
			
			<c:forEach items="${orderTemplates}" var="orderTemplate" end="${maxOrderTemplateCount-1}" varStatus="status">
				<c:url value="/your-business/orderTemplateDetail/${orderTemplate.code}" var="orderTemplateUrl" />
				<div class="product-orders-item">
					<div class="col-sm-12 col-md-9 col-lg-8 btn-height trim-both">
						<a href="${orderTemplateUrl}" class="inline">						
							<c:choose>
								<c:when test="${fn:length(orderTemplate.name) > 30}">
									<c:out value="${fn:substring(orderTemplate.name, 0, 30)}..." escapeXml="false" />
								</c:when>								
								<c:otherwise>
									${orderTemplate.name}
								</c:otherwise>
							</c:choose>
						</a>
						<%-- <c:forEach items="${orderTemplate.entries}" var="entry">
							<c:if test="${entry.product.cubStockStatus.code == 'outOfStock'}">
								<input type="hidden" id="${orderTemplate.code}_isOutOfStock" value="true"/>
							</c:if>
						</c:forEach> --%>

					</div>
					
					<div class="col-sm-12 col-md-3 col-lg-4 trim-both">
						<a href="javascript:void(0);" onclick="rm.templatesOrder.addToTemplate('${orderTemplate.code}')" class="btn btn-primary btn-block bde-view-only"><spring:theme code="basket.add.to.basket" /></a>
					</div> 
				</div>
				
				
				
			</c:forEach>
			<c:if test="${fn:length(orderTemplates) <= (maxOrderTemplateCount-1)}">
				<div class="product-orders-item">
					<div class="col-sm-12 col-md-9 col-lg-8 trim-both">
						<span class="btn-height"><spring:theme code="text.homepage.create.new.template" /></span>
					</div>
					<div class="col-sm-12 col-md-3 col-lg-4 magnific-template-order trim-both">
						<a href="#create-new-template" class="btn btn-secondary btn-block bde-view-only"><spring:theme code="text.button.create" /></a>
					</div>
				</div>
			</c:if>
			<c:if test="${not empty orderTemplates}">
				<div class="product-orders-cta">
					<a href="/your-business/ordertemplates" class="link-cta "><spring:theme code="text.home.linkText.viewAllTemplates" /></a>
				</div>
			</c:if>
		</div>
		</c:if>
	</div>
</section>
<c:set value="Home" var="pageName"/>
<templatesOrder:templateOrderPopup pageName="${pageName}"/>
<common:addItemsPopup/>
