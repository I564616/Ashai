<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>

<div class="row cart-delivery-payment">
	<div class="col-md-4 cart-deliverydate">
		<checkout:checkoutDeliveryDate />
	</div>
	<div class="col-md-4 cart-deliverymethod">
		<checkout:checkoutDeliveryMethod />
	</div>
	<div class="col-md-4 cart-paymentoptions">
		<checkout:checkoutPaymentOptions />
	</div>
</div>