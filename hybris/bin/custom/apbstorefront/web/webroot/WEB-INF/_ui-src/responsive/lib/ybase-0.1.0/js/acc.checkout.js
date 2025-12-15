ACC.checkout = {

	_autoload: [
		"bindCheckO",
		"bindForms",
		"bindSavedPayments",
		"bindSingleStepCheckoutElements",
		"kegReturnOnLoad",
		"disableCalendarDates",
		"securityCodeContent",
        "resetPaymentMethod"
	],

	/**
	 * Event Delegation on Checkout page
	 */
	bindForms:function(){
		$(document).on("click","#addressSubmit",function(e){
			e.preventDefault();
			$('#addressForm').submit();	
		})
		
		$(document).on("click","#deliveryMethodSubmit",function(e){
			e.preventDefault();
			$('#selectDeliveryMethodForm').submit();	
		})
		
		ACC.checkout.showSurchageConfiguration();
		
	},
	kegReturnOnLoad:function(){
		 if ($('#kegReturns')[0] != null && $('#kegReturns')[0].checked) {
			 	$('.keg-returns-content').removeClass('hide');
			}
			else
			{
				$('.keg-returns-content').removeClass('show');
			} 
	},

	bindSavedPayments:function(){
		$(document).on("click",".js-saved-payments",function(e){
			e.preventDefault();
			var title = $("#savedpaymentstitle").html();
			$.colorbox({
				href: "#savedpaymentsbody",
				inline:true,
				maxWidth:"100%",
				opacity:0.7,
				title: title,
				close:'<span class="glyphicon glyphicon-remove"></span>',
				onComplete: function(){
				}
			});
		})
	},

	bindCheckO: function ()
	{
		var cartEntriesError = false;
		// Alternative checkout flows options
		$('.doFlowSelectedChange').change(function ()
		{
			if ('multistep-pci' == $('#selectAltCheckoutFlow').val())
			{
				$('#selectPciOption').show();
			}
			else
			{
				$('#selectPciOption').hide();
			}
		});



		$('.js-continue-shopping-button').click(function ()
		{
			var checkoutUrl = $(this).data("continueShoppingUrl");
			window.location = checkoutUrl;
		});
		
		$('.js-create-quote-button').click(function ()
		{
			$(this).prop("disabled", true);
			var createQuoteUrl = $(this).data("createQuoteUrl");
			window.location = createQuoteUrl;
		});

		
		$('.expressCheckoutButton').click(function()
		{
			document.getElementById("expressCheckoutCheckbox").checked = true;
		});
		
		$(document).on("input",".confirmGuestEmail,.guestEmail",function(){
			  
			  var orginalEmail = $(".guestEmail").val();
			  var confirmationEmail = $(".confirmGuestEmail").val();
			  
			  if(orginalEmail === confirmationEmail){
			    $(".guestCheckoutBtn").removeAttr("disabled");
			  }else{
			     $(".guestCheckoutBtn").attr("disabled","disabled");
			  }
		});
		
		$('.js-continue-checkout-button').click(function ()
		{
			var checkoutUrl = $(this).data("checkoutUrl");
			
			cartEntriesError = ACC.pickupinstore.validatePickupinStoreCartEntires();
			if (!cartEntriesError)
			{
				var expressCheckoutObject = $('.express-checkout-checkbox');
				if(expressCheckoutObject.is(":checked"))
				{
					window.location = expressCheckoutObject.data("expressCheckoutUrl");
				}
				else
				{
					var flow = $('#selectAltCheckoutFlow').val();
					if ( flow === undefined || flow === '' || flow === 'select-checkout')
					{
						// No alternate flow specified, fallback to default behaviour
						window.location = checkoutUrl;
					}
					else
					{
						// Fix multistep-pci flow
						if ('multistep-pci' == flow)
						{
						flow = 'multistep';
						}
						var pci = $('#selectPciOption').val();

						// Build up the redirect URL
						var redirectUrl = checkoutUrl + '/select-flow?flow=' + flow + '&pci=' + pci;
						window.location = redirectUrl;
					}
				}
			}
			
			return false;
		});
		},
	
	bindSingleStepCheckoutElements: function ()
	{
		$('#address-select-dropdown').change(function(){ 
			var selectedValue = $(this).find(":selected").val();
			$("#selectedAddressRecordId").val(selectedValue);
			$( '.address-data' ).each(function( index, element ) {
				var recordId = $(element).find('.address-data-index').val();
				var deliveryInstruction = $(element).find('.delivery-instruction').val();
				if(recordId === selectedValue){
					$(element).removeClass("hide");
					var selectedAddress = $(element).find('.selected-address-data').html();
					$("#deliveryInstruction").val(deliveryInstruction);
					if($('#kegReturns').is(":checked")){
						$(".selectedPickUpAddress").html(selectedAddress);
					}
					else{
						$(".selectedPickUpAddress").html("");
					}
				}else{
					$(element).addClass("hide");
				}
			});
			
			//// Add get request to change delivery info data
			ACC.checkout.updateDataForAddress(selectedValue);
		});
		
		$(".deliveryInstructionInput").focusout(function() {
			var deliveryInstruction = $(this).val();
			$("#deliveryInstruction").val(deliveryInstruction);
		});
		
		$('#checkoutDetailsForm input').on('change', function() {
		   var deliveryType = $('input[name=deliveryDateType]:checked', '#checkoutDetailsForm').attr('id'); 
		   $("#deliveryMethodType").val(deliveryType);
		   if(deliveryType === 'standard'){
			   $("#deffered-delivery-dropdown").addClass("hide");
		   }else{
			   $("#deffered-delivery-dropdown").removeClass("hide");
		   }
		});
		
		$('#deffered-delivery-dropdown').change(function(){ 
			var selectedValue = $(this).find(":selected").val();
			$("#deferredDeliveryDate").val(selectedValue);
		});
		
		$('#checkoutDetailsForm input[name=paymentType]').on('change', function() {
		   var paymentType = $('input[name=paymentType]:checked', '#checkoutDetailsForm').val(); 
		   $("#paymentMethod").val(paymentType);
		   if($("#paymentMethod").val()==="CARD"){
				$('.checkout-card-payment-section').removeClass('hide');
				$('.cart-include-surcharge').removeClass('hide');
				ACC.checkout.addNewCardComponent();
			}
			else{
				$('.checkout-new-card input[name=cardDetails]').prop('checked', false);
				$('.checkout-card-payment-section').addClass('hide');
				$('.cart-include-surcharge').addClass('hide');
				ACC.checkout.updateSurcharge(null, $("#paymentMethod").val());
				
			}
		});
		
		$('.radiobuttons_paymentselection').find("input[type='radio']:checked").each(function(i, element) {
		      var hiddenVal = $(element).val();
		      $("." + $(element).attr("class") + "[type='hidden']").val(hiddenVal);
		})
		if($('.radiobuttons_paymentselection').find("input[name='creditBlockError']").val() !== undefined && $('.radiobuttons_paymentselection').find("input[name='creditBlockError']").val() != ''){
			$('#PaymentTypeSelection_ACCOUNT').prop("disabled", true);
			$('#PaymentTypeSelection_CARD').prop("checked", true);
			$("#paymentMethod").val("CARD");
		}
		if($('.radiobuttons_paymentselection').find("input[name='creditCardError']").val() !== undefined && $('.radiobuttons_paymentselection').find("input[name='creditCardError']").val() != ''){
			$('#PaymentTypeSelection_ACCOUNT').prop("checked", false);
			$('#PaymentTypeSelection_CARD').prop("checked", true);
			$("#paymentMethod").val("CARD");
		}
		//Cards list should be visible if already saved
		//If not use new card
		if($("#paymentMethod").val()=== "CARD"){
			$('.checkout-card-payment-section').removeClass('hide');
			$('.cart-include-surcharge').removeClass('hide');
			ACC.checkout.addNewCardComponent();
		}
		else{
			$('.checkout-card-payment-section').addClass('hide');
		}
		//validate keg returns on checkout page
		$('#kegReturns').change(function(){
			if($(this).is(":checked")){
				$('.keg-returns-content').removeClass('hide');
				$( '.address-data').each(function( index, element ) {
					if(!$(this).hasClass('hide')){
						var selectedAddress = $(this).find('.selected-address-data').html();
						$(".selectedPickUpAddress").html(selectedAddress);
					}
				})
			}
			else{
				$('.keg-returns-content').addClass('hide');
			}
		})
		
		$("#checkout_links_view_all").click(function(){
	    	var rowsToToggle = $('.checkoutCartShowToggle');
	    	rowsToToggle.each(function (item) {
	    		$(this).removeClass("hide");
			});
	    	if(rowsToToggle.length)
	    	{
	    		$(this).addClass("hide");
		    	$("#checkout_links_collapse").removeClass("hide");
	    	}
	    });
	    
		$("#checkout_links_collapse").click(function(){
	    	var rowsToToggle = $('.checkoutCartShowToggle');
	    	rowsToToggle.each(function (item) {
	    		$(this).addClass("hide");
			});
	    	$("#checkout_links_view_all").removeClass("hide");
	    	$("#checkout_links_collapse").addClass("hide");
	    	$(this).addClass("hide");
	    	$(this).prop("disabled", true);
	    });
	},
	//show iframe component on checkout page
	//if cards > 3 then use new card component radio should not be visible 
	// if cards  > 3 then we have manage cards link should be visible
	
	addNewCardComponent:function(){
		var numberOfCards = parseInt($('#numberOfCards').val());
		var maxNoOfCards = parseInt($('#maxNumberOfCards').val());
		var paymentMethod = $("#paymentMethod").val();
		if($('.checkout-card-list').length > 0){
			$('.card-payment-content').find('.checkout-card-list:first').find('input[name=cardDetails]').attr('checked', 'checked').prop('checked', true);

		}
		//maxNoOfCards are configurable
		//Here maxNoOfCards is 3
		if(numberOfCards == 0 || numberOfCards < maxNoOfCards){
			$('.checkout-card-payment-section').find('.accountActions-bottom').addClass('hide');
			var insertRadio = "<div class='checkout-new-card'><input name='cardDetails' type='radio' class='checkout-new-card-radio' id='checkout-card-item' value='addNewCard'/> <label for='checkout-card-item'>Use a new Card</label></div>"
			if($('.checkout-new-card').length === 0){	
				if($('.checkout-card-list').length > 0){		
					$('.checkout-card-payment-section').find('.checkout-card-list').last().after(insertRadio);
				}
				else{
					$('.checkout-card-payment-section').find('.card-payment-content').after(insertRadio);
					$('.checkout-new-card input[name=cardDetails]').attr('checked',true);
					
					$('.checkout-add-new-card').removeClass('hide');
					$('.checkout-add-new-card input[name=apbCreditCardType]').first().attr('checked', true);
				}
			}
			
		}
		else{
			$('.checkout-card-payment-section').find('.accountActions-bottom').removeClass('hide');
		}
		if($('input[name=cardDetails]:checked').val() !== 'addNewCard')
			{
			
				var cardType = $('input[name=cardDetails]:checked').val().toUpperCase();
				var token = $('input[name=cardDetails]:checked').data("token");
				if(paymentMethod === "ACCOUNT")
				{
					ACC.checkout.updateSurcharge(null, paymentMethod);
				}else
				{
					ACC.checkout.updateSurcharge(cardType, paymentMethod);
					ACC.checkout.bindFormValues(cardType,token);
				}
			
		
		}
		else{
			var newCardType = $('.checkout-add-new-card input[name=apbCreditCardType]:checked').val();
			ACC.checkout.updateSurcharge(newCardType, paymentMethod);
		}
		$('input[name=cardDetails]').on('change', function(){
			
			if($(this).val()==='addNewCard'){
				$('.checkout-add-new-card').removeClass('hide');
				$('.checkout-add-new-card input[name=apbCreditCardType]').first().attr('checked', true);
			}
			else{
				$('.checkout-add-new-card').addClass('hide');
			}
			if($(this).val()!=='addNewCard'){
				var cardType = $(this).val().toUpperCase();
				var token = $(this).data("token");
				//if Account option selected surcharge will not charge to customer
				if(paymentMethod === "ACCOUNT")
				{
					ACC.checkout.updateSurcharge(null, paymentMethod);
				}else
				{
					ACC.checkout.updateSurcharge(cardType, paymentMethod);
				}
				ACC.checkout.bindFormValues(cardType, token);
			
			}
			else{
				var newCardType = $('.checkout-add-new-card input[name=apbCreditCardType]:checked').val();
				ACC.checkout.updateSurcharge(newCardType, paymentMethod);
			}
			
		})
		$('.checkout-add-new-card input[type=radio]').on('change', function(){
			var cardType = $(this).val();
			ACC.checkout.updateSurcharge(cardType, paymentMethod);
			
		})
		$('.card_label').on('click', function(){
			$(this).prev('input[type="radio"]').trigger('click');
			
		})
		
	},
	bindFormValues:function(cardType, token){
		var checkOutForm = $("#addCheckoutDetails").closest("form");
		  	
		  	if($('input[name="asahiPaymentDetailsForm.CardTypeInfo"]').length)
	  		{
		  		$('input[name="asahiPaymentDetailsForm.CardTypeInfo"]').val(cardType);
	  		}else
	  		{
	  			var CardTypeInfo = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.CardTypeInfo").val(cardType);
	  			checkOutForm.append($(CardTypeInfo));
	  		}
		  	
		  	if($('input[name="asahiPaymentDetailsForm.cardToken"]').length)
  		{
		  		$('input[name="asahiPaymentDetailsForm.cardToken"]').val(token);
  		}else
  		{
  			var cardToken = $("<input>").attr("type", "hidden").attr("name", "asahiPaymentDetailsForm.cardToken").val(token);
  			checkOutForm.append($(cardToken));
  		}
		 
		 	if($('input[name="asahiCreditCardType"]').length)
  		{
		 		$('input[name="asahiCreditCardType"]').val(cardType);
  		}else
  		{
  			var creditCardType = $("<input>").attr("type", "hidden").attr("name", "asahiCreditCardType").val(cardType);
   			checkOutForm.append($(creditCardType));
  		}
			
	},
	//request to update credit card surcharge in cart
	//different surcharge for different cards i.e mastercard, visa and AMEX
	updateSurcharge:function(type, paymentMethod){
		
		var method = "POST";
        $.ajax({
            url: ACC.config.encodedContextPath + '/checkout/single/updateCreditSurcharge',
            data:{cardType: type, paymentMethod: paymentMethod},
            dataType : "json",
            type: method,
            success: function (data, textStatus, xhr) {
            	if(data != null){
            		ACC.checkout.updateTotalAfterSurcharge(data);
            	}
            },
            error: function (xhr, textStatus, error) {
               console.log('gettingError');
            }
        });
        
        ACC.checkout.showSurchageConfiguration();
        
	},
	
	updateDataForAddress: function(recordId){
		
		var method = "POST";
        $.ajax({
            url: ACC.config.encodedContextPath + '/checkout/single/changeDeliveryAddress',
            data:{'recordId': recordId},
            type:method,
            success: function (data, textStatus, xhr) {
            	if(data != null){
            		ACC.checkout.updateDefferedDeliveryDates(data);
            	}
            },
            error: function (xhr, textStatus, error) {
               console.error('gettingError while setting deffered delivery dates for selected delivery address.');
            }
        });
		
	},
	
	updateDefferedDeliveryDates: function(data){
		var deferredDeliveryOptions = data.deferredDeliveryOptions;
		$("select#deffered-del-selectdates")
	    .find('option')
	    .remove()
	    .end();
		
		deferredDeliveryOptions.forEach(function(item) {
				$("select#deffered-del-selectdates").append( $("<option>")
				    .val(item)
				    .html(item)
				);
		});
		ACC.checkout.disableCalendarDates();
		
	},
	
	updateTotalAfterSurcharge:function(data){
		var getTotal = data.totalPriceWithTax.formattedValue;
		$('.cart__top--amount').html('');
		$('.cart__top--amount').html(getTotal);
		
	},
	//Add datepicker for deferred delivery
	//Disable dates from which are unavailable for deferred delivery dates from datepicker
	disableCalendarDates:function(){
		var availableDates = new Array();
	    $("#deffered-del-selectdates > option").each(function() {
	        availableDates.push(this.value);
	    });
	    function available(date) {
	    	var dd = (date.getDate() < 10 ? '0' : '') + date.getDate();
	    	var mm = ((date.getMonth() + 1) < 10 ? '0' : '') + (date.getMonth() + 1);
	    	var yyyy = date.getFullYear();
	    	  dmy = dd + "/" + mm + "/" + yyyy;
	    	  if ($.inArray(dmy, availableDates) != -1) {
	    	    return [true, "","Available"];
	    	  } else {
	    	    return [false,"","Unavailable"];
	    	  }
	    	}
	    $('#deferredCalendar').datepicker({
	    	dateFormat: 'dd/mm/yy',
			numberOfMonths: [ 1, 2 ],
	    	beforeShowDay: available});
	    $('.showDeferredCal').click(function(){
	    	 $('#deferredCalendar').datepicker('show');
	    })
		
	},
	//Show security code panel before iframe form
	securityCodeContent:function(){
		$('.security-code-heading').on('click', function(){
			$(this).toggleClass('active');
			$('.security-code-content').slideToggle('slow');
		})
	},
	
	/**
	 * Show content of credit card selection section based on if surcharge on credit card allowed for site.
	 */
	showSurchageConfiguration:function()
	{
        var isSurchargeAdded = $('input[name="isSurchargeAdded"]').val();
        if(isSurchargeAdded == 'true')
        {	
        	$(".add-card-type").removeClass("hide"); 
        	$(".card-with-surcharge").removeClass("hide");
        	$(".card-without-surcharge").addClass("hide");
        }else
        {
        	$(".add-card-type").addClass("hide");
        	$(".card-with-surcharge").addClass("hide");
        	$(".card-without-surcharge").removeClass("hide");
        }
	
	},
    resetPaymentMethod: function () {
        if($('#PaymentTypeSelection_CARD').is(':checked') && $('#PaymentTypeSelection_ACCOUNT').prop("disabled") != true) {
            $(this).prop('checked', false);
            $('#PaymentTypeSelection_ACCOUNT').prop('checked', true);
            $("#paymentMethod").val('ACCOUNT');
            $('.checkout-new-card input[name=cardDetails]').prop('checked', false);
            $('.checkout-card-payment-section').addClass('hide');
            $('.cart-include-surcharge').addClass('hide');
            ACC.checkout.updateSurcharge(null, $("#paymentMethod").val());
        }
    }
}

$(window).load(function(){
    if($('#checkoutPossible').length > 0 && $('#checkoutPossible').val() == 'enabled'){
        if($('#updateIframe').val() != '' && $('#updateIframe').val() === 'true') {
            localStorage.setItem("updateIframe", true);
            $('#addCheckoutDetails').attr('disabled', false);
        }
        else if(localStorage.getItem("updateIframe") != null && localStorage.getItem("updateIframe")) {
            $(window).block({
                message: "<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />",
                overlayCSS: {
                    opacity: 0.01
                },
                centerY: false,
                css: {
                    top: '40%',
                    backgroundColor: 'transparent',
                    color: 'transparent',
                    border: 'none',
                }
            });
            $('#iframePaymentSection').load(location.href + " #iframePaymentSection>*", "", function(){
                localStorage.removeItem("updateIframe");
                $('#addCheckoutDetails').attr('disabled', false);
                $(window).unblock();
            });
        }
        else
            $('#addCheckoutDetails').attr('disabled', false);
    } else if ($('#hasBonusStockProductOnly').length && $('#hasBonusStockProductOnly').val() === 'true') {
		// Only bonus products in cart hence enable checkout
		$("#addCheckoutDetails").removeAttr("disabled");
		$("#finalcheckoutButton").removeAttr("disabled");
	}

    $('.deliveryInstructionInput').on('input', function(){
        var regExp = new RegExp($('#restrictedPattern').val(), 'g');
        var filteredText = $(this).val().replace(regExp, '');
        $(this).val(filteredText);
    });

    if($('#delivery-instructions-checkout').length > 0) {
        var patString = '';
        var restrictedPatterns = $('#restrictedPattern').val();
        for (var i = 1; i < restrictedPatterns.length - 1; i++) {
            patString += restrictedPatterns.charAt(i) + ' ';
        }
        var messageString = "<p><b>Delivery Instructions</b> -  Characters " + patString + "are <u><b>not</b></u> accepted</p>";
        $('.deliveryInstructionHeader').append(messageString);
    }
});