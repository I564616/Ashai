<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<div class="row cart-delivery-payment">
	<div class="col-md-4 cart-deliverydate">
		<cart:cartDeliveryDate />
	</div>
	<div class="col-md-4 cart-deliverymethod">
		<cart:cartDeliveryMethod />
	</div>
	<div class="col-md-4 cart-paymentoptions">
		<cart:cartPaymentOptions />
	</div>
</div>