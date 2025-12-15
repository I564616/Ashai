ACC.cartitem = {

	_autoload: [
		"bindCartItem",
		"bindEditQuantityCartItemButtons"
	],
	spinner: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),
	submitTriggered: false,

	bindCartItem: function ()
	{
		$(document).on("click",".js-execute-entry-action-button", function (e)
		{
			e.preventDefault();
			var entryAction = $(this).data("entryAction");
			var entryActionUrl =  $(this).data("entryActionUrl");
			var entryProductCode =  $(this).data("entryProductCode");
			var entryInitialQuantity =  $(this).data("entryInitialQuantity");
			var actionEntryNumbers =  $(this).data("actionEntryNumbers");
			var redirectUrl = pageContextPath + "/login";
			if(entryAction == 'REMOVE')
			{
				ACC.track.trackRemoveFromCart(entryProductCode.toString(), entryInitialQuantity);
			}

			var cartEntryActionForm = $("#cartEntryActionForm");
			var entryNumbers = actionEntryNumbers.toString().split(';');
			entryNumbers.forEach(function(entryNumber) {
				var entryNumbersInput = $("<input>").attr("type", "hidden").attr("name", "entryNumbers").val(entryNumber);
				cartEntryActionForm.append($(entryNumbersInput));
			});
			cartEntryActionForm.attr('action', entryActionUrl);
			$.ajax({
	            type: cartEntryActionForm.attr('method'),
	            url: cartEntryActionForm.attr('action'),
	            data: cartEntryActionForm.serialize(),
	            success: function (data, textStatus, xhr) {
	            	if(xhr.getResponseHeader('REQUIRES_AUTH')==='1'){
	            		window.location = redirectUrl;
	            	}
	            	if($(data).find('.item__list__cart').children().length === 0){
	            		window.location.href = window.location.href.replace("/update", "");
	            	}
	            	else{
	            	ACC.cartitem .updateCartSections(data);
	            	}
	            },
	            error: function (xhr, status, error) {
	            	if(xhr.status==403){
	            		window.location = redirectUrl;
	            	}
	            },
	        });
		});

		$('.js-update-entry-quantity-input').on("blur", function (e)
		{
			ACC.cartitem.checkQtySelector(this, "input");

		}).on("keyup", function (e)
		{
			ACC.cartitem.checkQtySelector(this, "input");
		}
		).on("keydown", function (e)
		{
			ACC.cartitem.checkQtySelector(this, "input");
		}
		);
		
		$( ".js-update-entry-quantity-input" ).change(function() {
			ACC.cartitem.checkQtySelector(this, "input");
		});
		
		// submitting the form on quantity update ACP-25 start
		$('#updateCartInput > input').on("blur", function(e){
			ACC.cartitem.checkQtySelector(this, "input");
		})
		// submitting the form on quantity update ACP-25 start

	},

	handleUpdateQuantity: function (elementRef)
	{

		$(elementRef).parents(".js-cart-qty-selector").find(".btn").removeAttr("disabled");
		var form = $(elementRef).closest('form');

		var productCode = form.find('input[name=productCode]').val();
        var initialCartQuantity = form.find('input[name=initialQuantity]').val();
        var newCartQuantity = form.find('input[name=quantity]').val();
        var deltaBonusQty = parseInt(newCartQuantity) - parseInt(initialCartQuantity);

        if ((elementRef.hasClass("js-bonusproduct-cart-input")) && (deltaBonusQty > 0)) {

            var overMax = ACC.product.overMaxQty(productCode, deltaBonusQty);

            if (!overMax) {
                ACC.track.trackUpdateCart(productCode, initialCartQuantity, newCartQuantity);
                sessionStorage.setItem("maxBonusProductsReached", "false");
                form.submit();
                return true;

            } else {

                var allowedBonusQty = $("#allowedBonusQty").html();
                var maxAllowedQty = parseInt(initialCartQuantity) + parseInt(allowedBonusQty);

                ACC.track.trackUpdateCart(productCode, initialCartQuantity, maxAllowedQty);
                form.find(".js-bonusproduct-cart-input").val(maxAllowedQty);
                sessionStorage.setItem("maxBonusProductsReached", "true");
                form.submit();
                return true;
            }
        } else {

            if (initialCartQuantity != newCartQuantity) {
                ACC.track.trackUpdateCart(productCode, initialCartQuantity, newCartQuantity);
                sessionStorage.setItem("maxBonusProductsReached", "false");
                form.submit();
                return true;
            }
            var max = $(elementRef).data("max");
            var inputVal = $(elementRef).val();
            var minusBtn = $(elementRef).parents(".js-cart-qty-selector").find(".js-cart-qty-selector-minus");

            var plusBtn = $(elementRef).parents(".js-cart-qty-selector").find(".js-cart-qty-selector-plus");

            if( inputVal <= 1) {
                minusBtn.attr("disabled", "disabled");
            }
            if( inputVal >= max) {
                plusBtn.attr("disabled", "disabled");
            }
            $.unblockUI();
            return false;
        }

	},
	
	bindEditQuantityCartItemButtons: function ()
	{
		$(".js-cart-qty-selector-minus, .js-cart-qty-selector-plus").on("keyup keydown", function (e)
		{
			var inputElement = $(this).parents(".js-cart-qty-selector").find('.js-update-entry-quantity-input');
			ACC.cartitem.checkQtySelector(inputElement, "input");
		});
        
        $(document).on("focusout", '.js-cart-qty-selector .js-cart-qty-selector-input', function (e) {
        	ACC.cartitem.checkQtySelector(this, "focusout");
            ACC.cartitem.updateQtyValue(this, $(this).val());
        });
        
    	$( ".js-cart-qty-selector-input" ).each(function( index ) {
            var inputVal = $(this).val();
            var minusBtn = $(this).parents(".js-cart-qty-selector").find(".js-cart-qty-selector-minus");
            var max = $(this).data("max");
            var plusBtn = $(this).parents(".js-cart-qty-selector").find(".js-cart-qty-selector-plus");
    
            if(inputVal <= 1){
            	minusBtn.attr("disabled", "disabled");
            }
            if(inputVal >= max){
            	plusBtn.attr("disabled", "disabled");
            }
            var updateLink = $(this).closest('.item__quantity').find('.cart-total-update-js');
        	updateLink.fadeTo("fast", .5).removeAttr("href").attr("disabled", "disabled"); 
        	
        	var updatedProductEntryNo = $('[name="updatedEntryNumber"]').val();
        	var currentProductEntryNo = $(this).closest('.item__quantity').find('[name="entryNumber"]').val();
        	if(updatedProductEntryNo == currentProductEntryNo){
        		var updatedMessage = $('[name="updatedQtyMsg"]').val();
        		var updatedMessageElement = $(this).closest('.item__quantity').find('.updatedProductMessage');
        		var isMessageTypeError = $('[name="isMessageTypeError"]').val(); 
        		updatedMessageElement.removeClass("hide");
        		if(isMessageTypeError == 'true'){
        			updatedMessageElement.addClass("item-text-red");
        		}else{
        			updatedMessageElement.addClass("item-text-green");
        		}
        		updatedMessageElement.html(updatedMessage);
        		var updateMsgTimeout = $('[name="updateMsgTimeout"]').val();
        		setTimeout(function() { $('.updatedProductMessage').hide(); }, updateMsgTimeout);
        		var scroll = $(this).offset().top;
        		$('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
        		$(this).focus();
        	}
        	
           }); 

        $(document).on("click", '.closeUpdateMsg', function () {
        	var updatedMessageElement = $(this).closest('.item__quantity').find('.updatedProductMessage');
        	updatedMessageElement.addClass("hide");
        })
        
        $(document).on("click", '.js-cart-qty-selector .js-cart-qty-selector-minus', function () {
            ACC.cartitem.checkQtySelector(this, "minus");
        })
        
        $(document).on("click", '.js-cart-qty-selector .js-cart-qty-selector-plus', function () {
            ACC.cartitem.checkQtySelector(this, "plus");
        })

        $(document).on("keydown", '.js-cart-qty-selector .js-cart-qty-selector-input', function (e) {

            if (($(this).val() != " " && ((e.which >= 48 && e.which <= 57 ) || (e.which >= 96 && e.which <= 105 ))  ) || e.which == 8 || e.which == 46 || e.which == 37 || e.which == 39 || e.which == 9) {
            }
            else if (e.which == 38) {
                ACC.cartitem.checkQtySelector(this, "plus");
            }
            else if (e.which == 40) {
                ACC.cartitem.checkQtySelector(this, "minus");
            }
            else {
                e.preventDefault();
            }
        });
        
        $('#generalErrorMsg').on('close.bs.alert', function (e) {
        	e.preventDefault();
        	$(this).toggleClass("hide");
        });
        
        $(document).on("click", '.cart-total-update-js', function () {
        	var inputElement = $(this).closest('.item__quantity').find('.js-update-entry-quantity-input');
        	ACC.cartitem.handleUpdateQuantity(inputElement);
        });
        
	},
	
checkQtySelector: function (self, mode) {
    	   	
        var input = $(self).parents(".js-cart-qty-selector").find(".js-cart-qty-selector-input");
        var inputVal = parseInt(input.val());
        var max = input.data("max");
        var minusBtn = $(self).parents(".js-cart-qty-selector").find(".js-cart-qty-selector-minus");
        var plusBtn = $(self).parents(".js-cart-qty-selector").find(".js-cart-qty-selector-plus");

        $(self).parents(".js-cart-qty-selector").find(".btn").removeAttr("disabled");

        if (mode == "minus") 
        {      	
            if (inputVal != 1)
            {
            	ACC.cartitem.updateQtyValue(self, inputVal - 1)
            	if (inputVal - 1 == 1) 
            	{
            		minusBtn.attr("disabled", "disabled");   
                }    
            } else 
            {
            	minusBtn.attr("disabled", "disabled");    
            }
        } else if (mode == "plus") {
        	if(max == "FORCE_IN_STOCK") 
        	{
        		ACC.cartitem.updateQtyValue(self, inputVal + 1)
        	} else if (inputVal <= max) 
        	{
                ACC.cartitem.updateQtyValue(self, inputVal + 1)
                if (inputVal + 1 == max) 
                {
                    plusBtn.attr("disabled", "disabled")
                }
            } else 
            {
                plusBtn.attr("disabled", "disabled")
            }
        } else if (mode == "input") 
        {         
            if(inputVal <= 1)
            {
            	minusBtn.attr("disabled", "disabled"); 
            	 ACC.cartitem.updateQtyValue(self, inputVal)
            } else if(max == "FORCE_IN_STOCK" && inputVal > 1) 
            {
            	ACC.cartitem.updateQtyValue(self, inputVal)
            } else if (inputVal > max) 
            {
                plusBtn.attr("disabled", "disabled");
            } else if(inputVal <= max)
            {
            	if(inputVal == max)
            	{
            		plusBtn.attr("disabled", "disabled")
            	}
            	ACC.cartitem.updateQtyValue(self, inputVal)
            } 
        } else if (mode == "focusout") 
        {
        	if (isNaN(inputVal)){
        		ACC.cartitem.showErrorForInvalidInput(input);
        		ACC.cartitem.checkQtySelector(input, "input");
        	} else if(inputVal >= max) {
                plusBtn.attr("disabled", "disabled");
            } else if(inputVal == 1){
            	minusBtn.attr("disabled", "disabled");
            }
        }

    },
    
    showErrorForInvalidInput: function (input) {
    	$("#generalErrorMsg").removeClass("hide");
    	var errorData = "<button class=\"close generalErrorCloseBtn\" aria-hidden=\"true\" data-dismiss=\"alert\" type=\"button\">&times;</button>" 
    		+ zeroQtyErr;
    	$("#generalErrorMsg").html(errorData);
    	var form = $(input).closest('form');
    	var initialCartQuantity = form.find('input[name=initialQuantity]').val();
    	$(input).val(initialCartQuantity);
        var max = input.data("max");
    	if(initialCartQuantity == 1){
    		var minusBtn = $(input).parents(".js-cart-qty-selector").find(".js-cart-qty-selector-minus");
    		minusBtn.attr("disabled", "disabled");
    	}
    	if(initialCartQuantity == max){
    		var plusBtn = $(input).parents(".js-cart-qty-selector").find(".js-cart-qty-selector-plus");
    		plusBtn.attr("disabled", "disabled");
    	}
    	if ($("#generalErrorMsg").length) 
    	{
    		$("body").scrollTop($("#generalErrorMsg").offset().top);
    	}
        
    },
    
    updateQtyValue: function (self, value) {
        var input = $(self).parents(".js-cart-qty-selector").find(".js-cart-qty-selector-input");
        input.val(value);
        var form = $(input).closest('form');
    	var initialCartQuantity = form.find('input[name=initialQuantity]').val();
    	var updateLink = $(self).closest('.item__quantity').find('.cart-total-update-js');
    	if(initialCartQuantity != value)
		{
    		updateLink.removeAttr("disabled").attr("href", "javascript:void(0)").fadeTo("fast", 1);
		}else {
			updateLink.fadeTo("fast", .5).attr("disabled", "disabled").removeAttr("href");
		}
        
    },
    
	updateCartSections: function(data){
		
		 $('.js-cart-top-totals').replaceWith($(data).find('.js-cart-top-totals'));
         $('.js-cart-totals').replaceWith($(data).find('.js-cart-totals'));
         $('#itemTable').replaceWith($(data).find('#itemTable'));
         $('.item__list--header').replaceWith($(data).find('.item__list--header'));
         $('.global-alerts').replaceWith($(data).find('.global-alerts'));
         $('.item__list__cart').append($(data).find('#emptyCart'));
         $('.top-checkout-btn').replaceWith($(data).find('.top-checkout-btn'));
         $('.bottom-checkout-btn').replaceWith($(data).find('.bottom-checkout-btn'));
         ACC.minicart.updateMiniCartDisplay();
		 ACC.checkout.bindCheckO();
	}
	
};

$(window).on('load', function() {
	$(document).on("click", "#removeAllProducts", function (e) {
		e.preventDefault();
		var url = $(this).attr('data-removeAllProductsUrl');
		var redirectUrl = pageContextPath + "/login";
		$.ajax({
			type: 'POST',
			url: url,
			success: function (data, textStatus, xhr) {
				if (xhr.getResponseHeader('REQUIRES_AUTH') === '1') {
					window.location = redirectUrl;
				}
				window.location = pageContextPath + "/cart";
			},
			error: function (xhr, status, error) {
				if (xhr.status == 403) {
					window.location = redirectUrl;
				}
			},
		});
	});
});