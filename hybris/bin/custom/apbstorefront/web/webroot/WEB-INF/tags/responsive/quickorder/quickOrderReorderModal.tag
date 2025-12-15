<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%@ attribute name="savedCart" required="true" type="com.apb.facades.order.data.AsahiQuickOrderData" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<div id="keepQuickOrderLayer" class="modal fade" role="dialog" data-backdrop="false">
    <div class="modal-dialog">
        <div class="modal-content">      
            <div class="modal-body keep-product">
				<div class="reorder-popup-heading">
						<spring:theme code="history.popup.product.in.cart.headline"/>
					</div> 
					<div class="reorder-popup-body">
						<spring:theme code="history.popup.keep.product.in.cart.query"/>
					</div>
					<div class="reorder-clear-btn">
						<button class="keepCartQuickOrderBtn btn btn-primary btn-block"><spring:theme code="history.popup.keep.products.button"/></button>
					</div>
					<div class="reorder-keep-btn">
						<button class="clearCartQuickOrderBtn btn btn-block btn-primary btn-vd-primary" ><spring:theme code="history.popup.clear.cart.button"/></button>
					</div>
					<div class="site-anchor-link">
                    <a id="backToQuickOrder" data-dismiss="modal"><spring:theme code="sga.quickOrder.popup.back.link"/></a> 
				</div>
            </div>
        </div>
    </div>
</div>