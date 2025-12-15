<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%@ attribute name="savedCart" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<div id="keepTemplateListingProductLayer" class="modal fade" role="dialog" data-backdrop="false">
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
						<button class="keepListTemplateCartBtn btn btn-primary btn-block" data-savedcart-id="${savedCart.code}"><spring:theme code="history.popup.keep.products.button"/></button>
					</div>
					<div class="reorder-keep-btn">
						<button class="clearListTemplateCartBtn btn btn-block btn-primary btn-vd-primary" data-savedcart-id="${fn:escapeXml(savedCart.code)}"><spring:theme code="history.popup.clear.cart.button"/></button>
					</div>
					<div class="site-anchor-link">
                    <a class="" data-dismiss="modal"><spring:theme code="order.templates.reorder.popup.back.link"/></a> 
				</div>
            </div>
        </div>
    </div>
</div>