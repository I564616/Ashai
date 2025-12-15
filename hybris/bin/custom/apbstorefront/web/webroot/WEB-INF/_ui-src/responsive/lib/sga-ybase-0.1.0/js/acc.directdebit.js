ACC.directdebit = {

    _autoload: [
        "bindDirectDebitPage",
		"bindDirectDebitSubmitForm"
    ],

    bindDirectDebitPage: function () {
		
		$("#ddTypeSelect").change(function () {
			var ddTypeVal = $("#ddTypeSelect").val();
			
			if (ddTypeVal === "select") {
				if (!$("#bankAccountSection").is(":hidden")) {
					$("#bankAccountSection").slideUp(500);
				}
				
				if (!$("#creditCardSection").is(":hidden")) {
					$("#creditCardSection").slideUp(500);
				}
				
				$("#tokenType").val("");
				
			} else if (ddTypeVal === "creditCard") {
				if (!$("#bankAccountSection").is(":hidden")) {
					$("#bankAccountSection").slideUp(500);
				}
				
				if ($("#creditCardSection").is(":hidden")) {
					$("#creditCardSection").slideDown(500);
				}
				
				$("#tokenType").val("DEBIT_CREDIT_CARD");
				
				// Clearing Bank Account Error when Credit Card is selected.
				ACC.directdebit.clearErrorMessages("CLEAR_ONLY_BANK_ERRORS");
				
			} else if (ddTypeVal === "bankAccount") {
				
				if (!$("#creditCardSection").is(":hidden")) {
					$("#creditCardSection").slideUp(500);
				}
				
				if ($("#bankAccountSection").is(":hidden")) {
					$("#bankAccountSection").slideDown(500);
				}
				
				$("#tokenType").val("BANK_ACCOUNT");
				
				// Clearing Bank Account Disabled fields when Bank Account is selected again.
				ACC.directdebit.clearErrorMessages("ENABLE_BANK_FIELDS");
			}
		});
		
		$("#regionCode").change(function () {
			var regionVal = $("#regionCode").val();
			
			if (regionVal === "select") {
				regionVal = "";
			}
			$("#submitDirectDebitForm").find("#region").val(regionVal);
		});
		
		$(document).on("click", '#ddtandcCheckbox', function (e) {
			if ($(this).is(':checked')) {
				$(this).val("true");
			} else {
				$(this).val("");
			}
		});
		
		var invalidChars = [
		  "-",
		  "+",
		  "e",
		];
		
		$(".input-number-field").keydown(function(e) {
			if (invalidChars.includes(e.key)) {
				e.preventDefault();
			  }
		});

		$(".input-number-field").on('paste', function (e) {
			if (e.originalEvent.clipboardData.getData('Text').match(/[^\d]/)) {
				e.preventDefault();
			}
		});
    },
	
	clearErrorMessages: function (tokenTypeVal) {

		var fieldNames = [];

		if (tokenTypeVal === "") {
			fieldNames = ["tokenType", "ddtandcCheckbox", "personalName", "currentDate"];
			// Form won't submit since tokenType is empty. Only being used for validation. @SM

		} else if (tokenTypeVal === "BANK_ACCOUNT") {
			fieldNames = ["tokenType", "accountName", "bsb", "accountNum", "suburb", "region", "ddtandcCheckbox", "personalName", "currentDate"];

		} else if (tokenTypeVal === "DEBIT_CREDIT_CARD") {
			fieldNames = ["tokenType", "ddtandcCheckbox", "personalName", "currentDate"];
			
		} else if (tokenTypeVal === "CLEAR_ONLY_BANK_ERRORS") {
			fieldNames = ["accountName", "bsb", "accountNum", "suburb", "region"];
		}

		var numOfErrors = $(".has-error").length;
		var currentField = 0;
		
		if (tokenTypeVal === "ENABLE_BANK_FIELDS") {
				$("#bankAccountSection").find("input").removeAttr("disabled");
			
		} else {
			for (currentField in fieldNames) {
				var hasErrorAlready = $("#" + fieldNames[currentField]).parent().hasClass("has-error")

				if (tokenTypeVal === "CLEAR_ONLY_BANK_ERRORS") {
					if (hasErrorAlready) {
						$("#" + fieldNames[currentField]).parent().removeClass("has-error").find(".help-block").remove();
						numOfErrors = numOfErrors - 1;
					}

				} else {
					if (($("#" + fieldNames[currentField]).val() === "") && !(hasErrorAlready)) {
						ACC.directdebit.addErrorMessage(currentField, fieldNames);
						
						numOfErrors = numOfErrors + 1;

					} else if(fieldNames[currentField] === 'accountName' && $("#accountName").val() != '' & $("#accountName").val().length > 33){
						if(hasErrorAlready){
							$("#" + fieldNames[currentField]).parent().removeClass("has-error").find(".help-block").remove();
							numOfErrors = numOfErrors - 1;
						}
						ACC.directdebit.addErrorMessage("accountName", fieldNames);
						numOfErrors = numOfErrors + 1;
					}
					else if (!($("#" + fieldNames[currentField]).val() === "") && (hasErrorAlready)) {
						// Clear the error message if no longer empty.

						$("#" + fieldNames[currentField]).parent().removeClass("has-error").find(".help-block").remove();
						numOfErrors = numOfErrors - 1;

					}
				}
			}
		}
	},
	
	addErrorMessage: function (currentField, fieldNames) {
		var errorMessageStr = "This is a required field.";
		
		if(currentField === 'accountName'){
			errorMessageStr = ACC.directDebitErrorMessageAccName32Character;
			$("#" + currentField).parent().addClass("has-error").append("<div class=\"help-block\">" + errorMessageStr + "</div>");
		}
		
 	   else {
			if (fieldNames[currentField] == "accountName") {
				errorMessageStr = ACC.directDebitErrorMessageAccName;

			} else if (fieldNames[currentField] == "bsb") {
				errorMessageStr = ACC.directDebitErrorMessageBSB;

			} else if (fieldNames[currentField] == "accountNum") {
				errorMessageStr = ACC.directDebitErrorMessageAccNum;

			} else if (fieldNames[currentField] == "suburb") {
				errorMessageStr = ACC.directDebitErrorMessageSuburb;

			} else if (fieldNames[currentField] == "region") {
				errorMessageStr = ACC.directDebitErrorMessageState;

			} else if (fieldNames[currentField] == "personalName") {
				errorMessageStr = ACC.directDebitErrorMessageName;

			} else if (fieldNames[currentField] == "ddtandcCheckbox") {
				errorMessageStr = ACC.directDebitErrorMessageTandC;
			}
			$("#" + fieldNames[currentField]).parent().addClass("has-error").append("<div class=\"help-block\">" + errorMessageStr + "</div>");
		}
	},
	
	bindDirectDebitSubmitForm: function () {
		 
		$(document).on("click", '#directDebitSubmitBtn', function (e) {
			e.preventDefault();
			 
			var tokenTypeVal = $("#submitDirectDebitForm").find("#tokenType").val();
			
			ACC.directdebit.clearErrorMessages(tokenTypeVal);
			
			var numOfErrors = $(".has-error").length;
			 
			if (numOfErrors > 0) {
				$("#generalErrorMsg").html(ACC.globalErrorMessage).removeClass("hide");
				$('.pageBodyContent').animate({
					scrollTop: 0
					}, 1000);
			} else {
				if (tokenTypeVal === "DEBIT_CREDIT_CARD") {
					var iframe = document.getElementById("checkoutIframe");
					iframe.contentWindow.postMessage('doCheckout', '*');
					$("#bankAccountSection").find("input").prop('disabled', true);
					$('.pageBodyContent').scrollTop($('.checkout-add-new-card').position().top + 140);
				} else {
					$("#submitDirectDebitForm").submit();
				}
			}
		});
		 
    }
};