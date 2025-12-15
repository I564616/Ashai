ACC.placeorder = {

	bindAll: function ()
	{
		this.bindPlaceOrder();
		this.updatePlaceOrderButton();
	},

	bindPlaceOrder: function ()
	{
		$(".placeOrderWithSecurityCode").on("click", function ()
		{
			ACC.common.blockFormAndShowProcessingMessage($(this));
			$(".securityCodeClass").val($("#SecurityCode").val());
			$("#placeOrderForm1").submit();
		});
	},

	updatePlaceOrderButton: function ()
	{
		
		$(".place-order").removeAttr("disabled");
		// need rewrite /  class changes
	}
};

$(document).ready(function ()
{
	ACC.placeorder.bindAll();
});


