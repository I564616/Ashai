<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:url var="reorderUrl" value="/history/reorder" scope="page"/>

<div class="row">
	<div class="col-xs-12 col-sm-12 col-md-7 col-lg-7">
		<form:form action="${reorderUrl}" class="reorderForm" modelAttribute="reorderForm">
			<button type="submit" class="btn btn-primary btn-vd-primary btn-block re-order reorder-button" <c:if test="${orderData.allProductExcluded != null && orderData.allProductExcluded.booleanValue()}">disabled="disabled"</c:if>">
				<spring:theme code="text.order.reorderbutton"/>
			</button>
			<input type="hidden" name="orderCode" value="${fn:escapeXml(orderCode)}" />
		</form:form>
	</div>
</div>

<div id="keepProductLayer" class="modal fade" role="dialog" data-backdrop="false">
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
                    <a class="" data-dismiss="modal"><spring:theme code="history.popup.back.to.history.details.link"/></a> 
				</div>
            </div>
        </div>
    </div>
</div>
