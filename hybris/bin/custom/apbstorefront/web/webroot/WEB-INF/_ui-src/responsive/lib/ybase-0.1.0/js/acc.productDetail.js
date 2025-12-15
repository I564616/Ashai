ACC.productDetail = {

    _autoload: [
        "initPageEvents",
        "bindVariantOptions"
        ],

        /** Add product quantity component for  PLP and PDP
         * @param {object} self instance of your input box 
         *  mode is plus, minus, input, reset
         * 
         */
          
    checkQtySelector: function (self, mode) {
        var input = $(self).parents(".js-qty-selector").find(".js-qty-selector-input");
        var inputVal = parseInt(input.val());
        var max = input.attr('data-max');
        var minusBtn = $(self).parents(".js-qty-selector").find(".js-qty-selector-minus");
        var plusBtn = $(self).parents(".js-qty-selector").find(".js-qty-selector-plus");
        var productliquor = $(self).parents(".js-qty-selector").find("input[name='productLiquor']").val();
        // if unit don't have liquor license then disabled add to cart button disabled 
        if(productliquor === "true"){
        	$(self).parents(".js-qty-selector").find(".btn").attr("disabled");
        }
        else{
        	$(self).parents(".js-qty-selector").find(".btn").removeAttr("disabled");
        }
        // if qty value 0 then minus btn disabled 
        if (mode === "minus") {
            if (inputVal != 1) {
            	ACC.productDetail.updateQtyValue(self, inputVal - 1)
            	if (inputVal - 1 == 1) {
            		minusBtn.attr("disabled", "disabled");
                }
                
            } else {
            	minusBtn.attr("disabled", "disabled");
            }
        } else if (mode === "reset") {
        	ACC.productDetail.updateQtyValue(self, 1);
            minusBtn.attr("disabled", "disabled");
            if(1 >= max){
        		plusBtn.attr("disabled", "disabled");
        	}

        } 
        /*increment qty when user click plus btn*/
        else if (mode === "plus") {
        	if(max == "FORCE_IN_STOCK") {
        		ACC.productDetail.updateQtyValue(self, inputVal + 1)
        	} else if (inputVal <= max) {
                ACC.productDetail.updateQtyValue(self, inputVal + 1)
                if (inputVal + 1 == max) {
                    plusBtn.attr("disabled", "disabled")
                }
            } else {
                plusBtn.attr("disabled", "disabled")
            }
        } else if (mode === "input") {
        	if (inputVal < 1) {
            	ACC.productDetail.checkQtySelector(self, "reset");
            } else if(inputVal == 1) {
            	minusBtn.attr("disabled", "disabled");
            	if(inputVal == max){
            		plusBtn.attr("disabled", "disabled");
            	}
            }else if(max == "FORCE_IN_STOCK" && inputVal > 1) {
            	ACC.productDetail.updateQtyValue(self, inputVal)
            } else if (inputVal == max) {
                plusBtn.attr("disabled", "disabled")
            } else if (inputVal > max) {
                plusBtn.attr("disabled", "disabled")
            }
        } else if (mode === "focusout") {
        	if (isNaN(inputVal)){
        		ACC.productDetail.checkQtySelector(self, "reset");
                minusBtn.attr("disabled", "disabled");
            	if(1 == max){
            		plusBtn.attr("disabled", "disabled");
            	}
        	} else if(inputVal >= max) {
                plusBtn.attr("disabled", "disabled");
            } else if(inputVal == 1){
            	minusBtn.attr("disabled", "disabled");
            }
        }

    },

    updateQtyValue: function (self, value) {
    	// update value in input field  everytime when user do plus and minus
        var input = $(self).parents(".js-qty-selector").find(".js-qty-selector-input");
        var addtocartQty = $(self).parents(".addtocart-component").find("#addToCartForm").find(".js-qty-selector-input");
        var configureQty = $(self).parents(".addtocart-component").find("#configureForm").find(".js-qty-selector-input");
        input.val(value);
        addtocartQty.val(value);
        configureQty.val(value);
        var max = input.attr('data-max');
    },

    initPageEvents: function () {
    //selecting all the content of quantity box so that user should be able to update quantity without deleting the existing value. 
    	$(document).on("focus", 'input.js-qty-selector-input', function (e) {
    		var input = $(this);
    		setTimeout(function() {
    	        input.select();
    	    });
    	});
    	
    	// If price error on PDP show error notification
    	var isAnonymousUser = $("input[name='isAnonymousUser']").val();
    	if($("input[name='pdpPriceError']").val()  == 'true')
		{
			$("#priceUpdateFailedErr").removeClass("hide");
		}
    	
    	ACC.productDetail.disabledPlusMinusBtn();

        $(document).on("click", '.js-qty-selector .js-qty-selector-minus', function () {
            ACC.productDetail.checkQtySelector(this, "minus");
        })
        
      
        
        $(document).on("click", '.product-item .thumb', function () {
        	var input = $(this).parents(".product-item").find(".js-qty-selector-input");
        	ACC.productDetail.checkQtySelector(input, "reset");
        	minusBtn.attr("disabled", "disabled");
        	if(1 == max){
        		plusBtn.attr("disabled", "disabled");
        	}
        })

        $(document).on("click", '.js-qty-selector .js-qty-selector-plus', function () {
            ACC.productDetail.checkQtySelector(this, "plus");
        })

        $(document).on("keydown", '.js-qty-selector .js-qty-selector-input', function (e) {

            if (($(this).val() != " " && ((e.which >= 48 && e.which <= 57 ) || (e.which >= 96 && e.which <= 105 ))  ) || e.which == 8 || e.which == 46 || e.which == 37 || e.which == 39 || e.which == 9) {
            }
            else if (e.which == 38) {
                ACC.productDetail.checkQtySelector(this, "plus");
            }
            else if (e.which == 40) {
                ACC.productDetail.checkQtySelector(this, "minus");
            }
            else {
                e.preventDefault();
            }
        })

        $(document).on("keyup", '.js-qty-selector .js-qty-selector-input', function (e) {
        	
        	var isValid = ACC.productDetail.resetInvalidInput(this);
        	if(isValid == true){
        		ACC.productDetail.checkQtySelector(this, "input");
                ACC.productDetail.updateQtyValue(this, $(this).val());
        	}   
        })
        
        $(document).on("focusout", '.js-qty-selector .js-qty-selector-input', function (e) {
        	var isValid = ACC.productDetail.resetInvalidInput(this);
        	if(isValid == true){
        		ACC.productDetail.checkQtySelector(this, "focusout");
                ACC.productDetail.updateQtyValue(this, $(this).val());
        	}   
        })

        $("#Size").change(function () {
            changeOnVariantOptionSelection($("#Size option:selected"));
        });

        $("#variant").change(function () {
            changeOnVariantOptionSelection($("#variant option:selected"));
        });

        $(".selectPriority").change(function () {
            window.location.href = $(this[this.selectedIndex]).val();
        });
        
        $('#stockErrorMsg').on('close.bs.alert', function (e) {
        	e.preventDefault();
        	$(this).toggleClass("hide");
        });
        
        function changeOnVariantOptionSelection(optionSelected) {
            window.location.href = optionSelected.attr('value');
        }
    
    },
    bindAddToCartSubmit:function(){
            $('#addToCartForm').on('submit', function(e){
                e.preventDefault();
                var self = $(this);
                 $.ajax({
                     type: $(this).attr('method'),
                     url: $(this).attr('action'),
                     data: $(this).serialize(),
                     success: function (data) {
                        
                          var cartError = data.cartError;
                          if(cartError == undefined)
                          {
                        	  window.location.replace(ACC.config.encodedContextPath + "/login");           
                          }
                          else if(cartError != ""){
                              var priceUpdateFailedErr = $("#priceUpdateFailedErr");
                              priceUpdateFailedErr.text(cartError);
                              priceUpdateFailedErr.removeClass("hide");
                              if (priceUpdateFailedErr.length) {
                                $('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
                             }
                          }
                          else{
                                var input = self.parents(".addtocart-component").find("#pdpAddtoCartInput");
                                var inputVal = parseInt(input.val());
                                var max = input.attr('data-max');
                                var availableStocks = max - inputVal;
                                input.attr("data-max", availableStocks);
                                ACC.productDetail.checkQtySelector(input, "reset");
                                if(availableStocks < 1){
                                    var pdpAddToCartButton = $(input).parents(".addtocart-component").find(".pdpAddToCartButton");
                                    pdpAddToCartButton.attr("disabled", "disabled");
                                }
                               ACC.minicart.updateMiniCartDisplay();
                          }
                        
                   
                          
                     },
                     error: function (data) {
                         console.log('An error occurred in adding to Cart.');
                         console.log(data);
                     },
                 });
                
            })
        },
	
    disabledPlusMinusBtn:function(){
    	$( ".js-qty-selector-input").each(function( index ) {
            var inputVal = $(this).val();
            var minusBtn = $(this).parents(".js-qty-selector").find(".js-qty-selector-minus");
            var max = $(this).attr('data-max');
            var plusBtn = $(this).parents(".js-qty-selector").find(".js-qty-selector-plus");
            
            if(inputVal == 1){
            	minusBtn.attr("disabled", "disabled");
            }
            var strInputVal = parseInt(inputVal);
            var strMaxVal = parseInt(max);
            
            if(strInputVal >= strMaxVal){
            	plusBtn.attr("disabled", "disabled");
            }
            if(max < 1){
            	  var listAddToCartButton = $(this).parents(".addtocart-component").find(".list-add-to-cart");
            	  listAddToCartButton.attr("disabled", "disabled");        	
            }
           }); 
    	
    },

    bindVariantOptions: function () {
        ACC.productDetail.bindCurrentStyle();
        ACC.productDetail.bindCurrentSize();
        ACC.productDetail.bindCurrentType();
    },

    bindCurrentStyle: function () {
        var currentStyle = $("#currentStyleValue").data("styleValue");
        var styleSpan = $(".styleName");
        if (currentStyle != null) {
            styleSpan.text(": " + currentStyle);
        }
    },

    bindCurrentSize: function () {
        var currentSize = $("#currentSizeValue").data("sizeValue");
        var sizeSpan = $(".sizeName");
        if (currentSize != null) {
            sizeSpan.text(": " + currentSize);
        }
    },

    bindCurrentType: function () {
        var currentSize = $("#currentTypeValue").data("typeValue");
        var sizeSpan = $(".typeName");
        if (currentSize != null) {
            sizeSpan.text(": " + currentSize);
        }
    },
    
    resetInvalidInput: function (self) {
    	var input = $(self).parents(".js-qty-selector").find(".js-qty-selector-input");
        var inputVal = parseInt(input.val(), 10);
        var max = input.attr('data-max');
        
        if(inputVal < 1 || inputVal > max){
        	$("#allowedQuantity").html(max);
        	var stockErrorMsgDiv = $( "#stockErrorMsg" );
        	stockErrorMsgDiv.removeClass("hide");
        	if (stockErrorMsgDiv.length) {
				$('.pageBodyContent').animate({
					scrollTop: 0
					}, 1000);
			}        	
			ACC.productDetail.updateQtyValue(input, 1);
        	var minusBtn = $(self).parents(".js-qty-selector").find(".js-qty-selector-minus");
        	minusBtn.attr("disabled", "disabled");
        	return false;
        }
    	return true;
    }
        
};

$(document).ready(function ()
{
	ACC.productDetail.bindAddToCartSubmit();

});