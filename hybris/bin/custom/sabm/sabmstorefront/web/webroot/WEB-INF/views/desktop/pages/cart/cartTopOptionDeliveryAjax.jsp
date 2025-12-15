<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<div>
    <div id="simulationErrors">
		<common:globalMessages/>
	</div>
	<div class="col-md-4 cart-deliverymethod">
		<cart:cartDeliveryMethod />
	</div>
</div>