ACC.checkoutpayment = {
	_autoload: [
		            "checkoutShowPaymentIframe"
		            ],

	checkoutShowPaymentIframe: function () {
		var postMessageStrings = false;

		try {
			window.postMessage({
				toString: function () {
					postMessageStrings = true;
				}
			}, "*");
		} catch (e) {}
		// Handle events
		function checkoutReceiveMessage(event) {
			if (event.origin.indexOf("paynow") === -1) return;

			var payload = event.data;

			if (typeof event.data == 'string') {
				/* if (/\[object/i.test(event.data)) {
				    alert("Sorry, it looks like there has been a problem communicating with your browsers...");
				} */
				var pairs = payload.split("&");
				payload = {};
				for (var i = 0; i < pairs.length; i++) {
					var element = pairs[i];
					var kv = element.split("=");
					payload[kv[0]] = kv[1];
				}
			}


			if ('data' in payload && payload.data.r == '1') {
				// Modern browser
				// Use payload.data.x
				document.getElementById("checkoutIframe").style.display = "none";
				
				
				var currentPage = $("#checkPage").val();
				
				if (currentPage === "paymentdetail") {
					var checkOutForm = $("#asahiSamPaymentForm");
				} else if (currentPage === "directdebit") { 
					var checkOutForm = $("#submitDirectDebitForm");
				} else {
					var checkOutForm = $("#addCheckoutDetails").closest("form");
				}
				
				
				var cardType = $('input[name=apbCreditCardType]:checked').val();

				/*create and pass the payment response to customer checkout form start*/
				var message = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.message").val(payload.message);
				var cardExpiry = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.cardExpiry").val(payload.data.card_expiry);
				var cardNumber = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.cardNumber").val(payload.data.card_number);

				if ($('input[name="asahiPaymentDetailsForm.CardTypeInfo"]').length) {
					$('input[name="asahiPaymentDetailsForm.CardTypeInfo"]').val(payload.data.card_type);
				} else {
					var CardTypeInfo = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.CardTypeInfo").val(payload.data.card_type);
					checkOutForm.append($(CardTypeInfo));
				}

				var responseCode = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.responseCode").val(payload.data.r);

				if ($('input[name="asahiPaymentDetailsForm.cardToken"]').length) {
					$('input[name="asahiPaymentDetailsForm.cardToken"]').val(payload.data.token);
				} else {
					var cardToken = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.cardToken").val(payload.data.token);
					checkOutForm.append($(cardToken));
				}
				var cardHolder = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.cardHolderName").val(payload.data.card_holder);

				if ($('input[name="asahiCreditCardType"]').length) {
					$('input[name="asahiCreditCardType"]').val(cardType);
				} else {
					if (currentPage !== "directdebit") { 
						var creditCardType = $("<input>").attr("type", "hidden").attr("name", "asahiCreditCardType").val(cardType);
						checkOutForm.append($(creditCardType));
					}
				}
				checkOutForm.append($(message));
				checkOutForm.append($(cardExpiry));
				checkOutForm.append($(cardNumber));
				checkOutForm.append($(responseCode));
				checkOutForm.append($(cardHolder));
				/*create and pass the payment response to customer checkout form end*/

				$(checkOutForm).submit();
			} else if (payload.r == '1') {
				// Old browser don't use payload.data.x
				document.getElementById("checkoutIframe").style.display = "none";
				
				if (currentPage === "paymentdetail") {
					var checkOutForm = $("#asahiSamPaymentForm");
				} else if (currentPage === "directdebit") { 
					var checkOutForm = $("#submitDirectDebitForm");
				} else {
					var checkOutForm = $("#addCheckoutDetails").closest("form");
				}
				
				var cardType = $('input[name=apbCreditCardType]:checked').val();

				/*create and pass the payment response to customer checkout form start*/
				var message = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.message").val(payload.message);
				var cardExpiry = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.cardExpiry").val(payload.card_expiry);
				var cardNumber = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.cardNumber").val(payload.card_number);
				if ($('input[name="asahiPaymentDetailsForm.CardTypeInfo"]').length) {
					$('input[name="asahiPaymentDetailsForm.CardTypeInfo"]').val(payload.card_type);
				} else {
					var CardTypeInfo = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.CardTypeInfo").val(payload.card_type);
					checkOutForm.append($(CardTypeInfo));
				}

				var responseCode = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.responseCode").val(payload.r);

				if ($('input[name="asahiPaymentDetailsForm.cardToken"]').length) {
					$('input[name="asahiPaymentDetailsForm.cardToken"]').val(payload.token);
				} else {
					var cardToken = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.cardToken").val(payload.token);
					checkOutForm.append($(cardToken));
				}
				var cardHolder = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.cardHolderName").val(payload.card_holder);

				if ($('input[name="asahiCreditCardType"]').length) {
					$('input[name="asahiCreditCardType"]').val(cardType);
				} else {
					if (currentPage !== "directdebit") { 
						var creditCardType = $("<input>").attr("type", "hidden").attr("name", "asahiCreditCardType").val(cardType);
						checkOutForm.append($(creditCardType));
					}
				}
				checkOutForm.append($(message));
				checkOutForm.append($(cardExpiry));
				checkOutForm.append($(cardNumber));
				checkOutForm.append($(responseCode));
				checkOutForm.append($(cardHolder));
				/*create and pass the payment response to customer checkout form end*/

				$(checkOutForm).submit();
			} else {
				$('body').unblock();
			}

		}

		// Attach Listeners
		if (window.addEventListener) {
			window.addEventListener("message", checkoutReceiveMessage, false);
		} else {
			window.attachEvent("onmessage", checkoutReceiveMessage);
		}
		/*Click on checkout submit button 
		 * Validate keg returns and Deffered delivery dates shouldn't be empty if both are selected by user*/
		$('.js-custom-checkout-button').click(function (e) {
			e.preventDefault();
			var kegFlag = false;
			//	validate for keg return on confirm and pay button
			if ($('#kegReturns').is(':checked')) {
				$(".js-keg-qty-selector .js-qty-selector-input").each(function (index) {
					var inputVal = parseInt($(this).val());
					if (inputVal > 0) {
						kegFlag = true;
					}
				});
				if (!kegFlag) {
					$('#kegQtyErrorMessage').removeClass('hide');
					$('.pageBodyContent').scrollTop($('#kegQtyErrorMessage').position().top - 20);
					return false;
				} else {
					$('#kegQtyErrorMessage').addClass('hide');
				}
			}
			if ($('#deferred').is(':checked')) {
				var deferredCalVal = $('#deferredCalendar').val();
				if (deferredCalVal == "") {
					$('#deferredCalError').removeClass('hide');
					$('.pageBodyContent').scrollTop($('.delivery-date').position().top + 230);
					return false;
				} else {
					$('#deferredCalError').addClass('hide');
				}
			}
			
			if ($("#deferredCalendar").val() == "") {
				$("#generalErrorMsg").html(ACC.checkoutNoDatesErrorMessage).removeClass("hide").addClass("checkout-error-message");
				$("#finalcheckoutButton").attr("disabled", "disabled");
				$("#addCheckoutDetails").attr("disabled", "disabled");
				$('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
				return false;
			}
			
			$.blockUI({
				message: "<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />",
				overlayCSS: {
					opacity: 0.01
				},
				css: {
					backgroundColor: 'transparent',
					color: 'transparent',
					border: 'none',
				}
			});
			if ($("#paymentMethod").val() === "CARD" && $('.checkout-new-card input[name=cardDetails]').is(':checked')) {
				var iframe = document.getElementById("checkoutIframe");
				iframe.contentWindow.postMessage('doCheckout', '*');
				$('.pageBodyContent').scrollTop($('.checkout-add-new-card').position().top + 140);

			} else {
				$('#checkoutDetailsForm').submit();
			}


		});
			
		$('.js-make-payment-btn').click(function (e) {
			e.preventDefault();
			
			$.blockUI({
				message: "<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />",
				overlayCSS: {
					opacity: 0.01
				},
				css: {
					backgroundColor: 'transparent',
					color: 'transparent',
					border: 'none',
				}
			});
			
			if (!($("#partialPaymentSection").hasClass("hide"))) {
				if ($("#partial-payment-droplist").val() == null) {
					e.preventDefault();
					$("#partial-payment-droplist").addClass("input-field-error");
					$("#reqField").removeClass("hidden");
					$('#generalErrorMsg').html(ACC.formValidationMsg)
					$('#generalErrorMsg').removeClass("hide");
					$('.pageBodyContent').animate({
							scrollTop: 0
						}, 1000);
					$.unblockUI();
				} else {
					if ($("#paymentMethod").val() === "CARD" && $('.checkout-new-card input[name=cardDetails]').is(':checked')) {
						var iframe = document.getElementById("checkoutIframe");
						iframe.contentWindow.postMessage('doCheckout', '*');
						$('.pageBodyContent').scrollTop($('.checkout-add-new-card').position().top + 140);

					} else {
						$('#asahiSamPaymentForm').submit();
					}
				}
			} else {
				if ($("#paymentMethod").val() === "CARD" && $('.checkout-new-card input[name=cardDetails]').is(':checked')) {
					var iframe = document.getElementById("checkoutIframe");
					iframe.contentWindow.postMessage('doCheckout', '*');
					$('.pageBodyContent').scrollTop($('.checkout-add-new-card').position().top + 140);

				} else {
					$('#asahiSamPaymentForm').submit();
				}
			}

		});
	}
}