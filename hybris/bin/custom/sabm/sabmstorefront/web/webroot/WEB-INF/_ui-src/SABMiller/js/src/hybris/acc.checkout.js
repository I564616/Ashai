ACC.checkout = {
	spinner: $("<img id='taxesEstimateSpinner' src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),

	bindAll: function ()
	{
		this.bindCheckO();
	},

	bindCheckO: function ()
	{
		var cartEntriesError = false;
		// Alternative checkout flows options
		$('.doFlowSelectedChange').change(function ()
		{
			if ('multistep-pci' == $('#selectAltCheckoutFlow').attr('value'))
			{
				$('#selectPciOption').css('display', '');
			}
			else
			{
				$('#selectPciOption').css('display', 'none');

			}
		});

		$('#estimateTaxesButton').click(function ()
		{
			$('#zipCodewrapperDiv').removeClass("form_field_error");
			$('#countryWrapperDiv').removeClass("form_field_error");

			var countryIso = $('#countryIso').val();
			if (countryIso === "")
			{
				$('#countryWrapperDiv').addClass("form_field_error");
			}
			var zipCode = $('#zipCode').val();
			if (zipCode === "")
			{
				$('#zipCodewrapperDiv').addClass("form_field_error");
			}

			if (zipCode !== "" && countryIso !== "")
			{
				$("#order_totals_container").append(ACC.checkout.spinner);
				$.getJSON("cart/estimate", {zipCode: zipCode, isocode: countryIso  }, function (estimatedCartData)
				{
					$("#estimatedTotalTax").text(estimatedCartData.totalTax.formattedValue)
					$("#estimatedTotalPrice").text(estimatedCartData.totalPrice.formattedValue)
					$(".estimatedTotals").show();
					$(".realTotals").hide();
					$("#taxesEstimateSpinner").remove();

				});
			}
		});
	}

};

$(document).ready(function ()
{
	ACC.checkout.bindAll();
});
