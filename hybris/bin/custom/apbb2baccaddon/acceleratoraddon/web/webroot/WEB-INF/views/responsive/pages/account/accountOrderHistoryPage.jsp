<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="searchUrl" value="/my-account/orders?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/>
<c:url var="reorderUrl" value="/history/reorder" scope="page"/>
<div id ="orderHistoryFormError" class="hidden">
	<div class="alert alert-danger alert-dismissable">
		<spring:theme code="form.global.error"/>
	</div>
</div>
<div class="account-section-header user-register__headline secondary-page-title">
	<spring:theme code="text.account.orderHistory" />
</div>

<b2b-order:orderListing searchUrl="${searchUrl}" messageKey="text.account.orderHistory.page"></b2b-order:orderListing>

<div id="keepProductLayer" class="modal fade" role="dialog" data-backdrop="false" >
    <div class="modal-dialog">
        <div class="modal-content">      
            <div class="modal-body keep-product">
            	<form:form action="${reorderUrl}/keepCart" id="reorderKeepCartForm" modelAttribute="reorderKeepCartForm">
	                <div class="reorder-popup-heading">
						<spring:theme code="history.popup.product.in.cart.headline"/>
					</div> 
					<div class="reorder-popup-body">
						<spring:theme code="history.popup.keep.product.in.cart.query"/>
					</div>
					<div class="reorder-clear-btn">
						<button class="keepCartBtn btn btn-primary btn-block"><spring:theme code="history.popup.keep.products.button"/></button>
					</div>
					<div class="reorder-keep-btn">
						<button class="clearCartBtn btn btn-block btn-primary btn-vd-primary"><spring:theme code="history.popup.clear.cart.button"/></button>
					</div>
					<input type="hidden" name="orderCode" id="reorderCode" value="${fn:escapeXml(orderCode)}" />
					<input type="hidden" name="clear" id="clearCart" value="true" />
				</form:form>
				<div class="site-anchor-link">
                    <a class="" data-dismiss="modal"><spring:theme code="history.popup.back.to.history.link"/></a> 
				</div>
            </div>
        </div>
    </div>
</div>