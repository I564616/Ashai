<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout/confirmation" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>

<div class="row checkout-confirmation margin-top-30">
	<div class="col-md-4 confirm-orderno">
		<checkout:confirmOrderNo />
	</div>
	<div class="col-md-4 confirm-deliveryDate">
		<checkout:confirmDeliveryDate />
	</div>
</div>
<div>
   <common:amendCancelMessage/>
</div>