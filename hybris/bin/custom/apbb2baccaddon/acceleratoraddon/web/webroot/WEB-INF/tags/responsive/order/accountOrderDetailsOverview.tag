<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>




<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>

    
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="row">
    <div class="col-xs-12 col-sm-5 col-md-7 col-no-padding">
        <div class="row">
            <div class="col-sm-12 col-md-9 item-wrapper">
                <div class="item-group">
                    <ycommerce:testId code="orderDetail_overviewOrderID_label">
                        <div class="account-section-header user-register__headline secondary-page-title">
                            <span><spring:theme code="text.account.orderHistory.orderNumber"/>&nbsp;${fn:escapeXml(orderData.salesOrderId)}</span>
                        </div>
                    </ycommerce:testId>
                </div>
            </div>
            <div class="col-sm-12 col-md-10 item-wrapper">
            <c:if test="${cmsSite.uid ne 'sga'}">
             <div class="item-group">
                 <ycommerce:testId code="orderDetail_overviewOrderID_label">
                     <span class="item-label"><spring:theme code="text.account.orderHistory.portal.orderNumber"/>${fn:escapeXml(orderData.code)}</span>
                 </ycommerce:testId>
             </div>
             </c:if>
             <div class="item-group">
                 <ycommerce:testId code="orderDetail_overviewOrderStatus_label">
                     <span class="item-label"><spring:theme code="text.account.orderHistory.orderStatus"/><c:if test="${not empty orderData.statusDisplay}">${fn:escapeXml(orderData.statusDisplay)}</c:if></span>
<!--
                        <c:if test="${not empty orderData.statusDisplay}">
                            <span class="item-value">${fn:escapeXml(orderData.statusDisplay)}</span>
                        </c:if>
-->
                 </ycommerce:testId>
             </div>
             <div class="item-group">
                 <ycommerce:testId code="orderDetail_overviewStatusDate_label">
                     <span class="item-label"><spring:theme code="text.account.orderHistory.datePlaced"/><fmt:formatDate value="${order.created}"  pattern ="dd/MM/yyyy" /></span>
                 </ycommerce:testId>
             </div>
             <div class="item-group">
                 <ycommerce:testId code="orderDetail_overviewPlacedBy_label">
                 <c:choose>
                  <c:when test = "${orderData.orderType ne 'Online'}">
                  		<span class="item-label"><spring:theme code="text.account.orderHistory.type"/>&nbsp;${fn:escapeXml(order.orderType)}</span>
                  </c:when>
                 <c:when test="${orderData.bdeOrder && not empty orderData.placedBy}">
						<span class="item-label"><spring:theme code="text.account.orderHistory.type"/>&nbsp;${fn:escapeXml(order.orderType)}<span class="item-label-inner">&nbsp;(<spring:theme code="text.account.order.placedBy.sga" arguments="${orderData.placedByName}"/>)</span></span>
                 </c:when>
                 <c:when test="${not empty orderData.placedBy}">
						<span class="item-label"><spring:theme code="text.account.orderHistory.type"/>&nbsp;${fn:escapeXml(order.orderType)}<span class="item-label-inner">&nbsp;(<spring:theme code="text.account.order.placedBy.apb" arguments="${orderData.placedByName}, ${order.b2bCustomerData.name}"/>)</span></span>
                 </c:when>
                 <c:otherwise>
                 		<span class="item-label"><spring:theme code="text.account.orderHistory.type"/>&nbsp;${fn:escapeXml(order.orderType)}&nbsp;(${fn:escapeXml(order.b2bCustomerData.firstName)}&nbsp;${fn:escapeXml(order.b2bCustomerData.lastName)})</span>
                 </c:otherwise>
                 </c:choose>
                 </ycommerce:testId>
                 
             </div>
             <c:set var="orderCode" value="${orderData.code}" scope="request"/>
             <c:if test = "${orderData.orderType == 'Online' && !isNAPGroup}">
	             <c:choose>
		             	<c:when test="${orderData.isOnlyBonus != null && orderData.isOnlyBonus.booleanValue()}">
			             	<div class="AccountOrderDetailsOverviewComponent-ReorderAction">
									<button class="btn btn-primary btn-template-block reorder-button reorderButtonDetails" disabled="disabled" id="reorderButton">
										<spring:theme code="text.order.reorderbutton"/>
									</button>
								</div>
		             	</c:when>
		             	<c:otherwise>
		             		<action:actions element="div" parentComponent="${component}"/>
		             	</c:otherwise>
	             	</c:choose>
              </c:if>
            </div>
            
         </div>
    </div>


	<c:if test = "${!isNAPGroup}">
    <div class="float-right col-xs-12 col-sm-6 col-md-5 item-action checkoutcartsummary">
        <order:accountOrderDetailOrderTotals order="${orderData}"/>
        
<!--
        <c:set var="orderCode" value="${orderData.code}" scope="request"/>
        <action:actions element="div" parentComponent="${component}"/>
-->
    </div>
    </c:if>
</div>