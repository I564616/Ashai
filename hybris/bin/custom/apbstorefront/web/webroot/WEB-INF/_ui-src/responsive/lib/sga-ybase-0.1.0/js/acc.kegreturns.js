ACC.kegreturns = {
		 _autoload: [
		             "initPageEvents",
		             "pickupAddressEvents"
		         ],

		         /* method for separate quantity selector for keg returns page */
		         
		         checkQtySelector: function (self, mode) {
		             var input = $(self).parents(".js-keg-qty-selector").find(".js-qty-selector-input");
		             var inputVal = parseInt(input.val());
		             var max = input.attr('data-max');
		             var minusBtn = $(self).parents(".js-keg-qty-selector").find(".js-qty-selector-minus");
		             var plusBtn = $(self).parents(".js-keg-qty-selector").find(".js-qty-selector-plus");

		             $(self).parents(".js-keg-qty-selector").find(".btn").removeAttr("disabled");

		             if (mode == "minus") {
		                 if (inputVal != 0) {
		                 	ACC.kegreturns.updateQtyValue(self, inputVal - 1)
		                 	if (inputVal-1 == 0) {
		                 		minusBtn.attr("disabled", "disabled");
		                     }
		                     
		                 } else {
		                 	minusBtn.attr("disabled", "disabled");
		                 }
		             } else if (mode == "reset") {
		             	ACC.kegreturns.updateQtyValue(self, 0);
		                 minusBtn.attr("disabled", "disabled");
		                 if(1 >= max){
		             		plusBtn.attr("disabled", "disabled");
		             	}

		             } else if (mode == "plus") {
		             	if(max == "FORCE_IN_STOCK") {
		             		ACC.kegreturns.updateQtyValue(self, inputVal + 1)
		             	} else if (inputVal <= max) {
		                     ACC.kegreturns.updateQtyValue(self, inputVal + 1)
		                     if (inputVal + 1 == max) {
		                         plusBtn.attr("disabled", "disabled")
		                     }
		                 } else {
		                     plusBtn.attr("disabled", "disabled")
		                 }
		             } else if (mode == "input") {
		             	if (inputVal < 0) {
		                 	ACC.kegreturns.checkQtySelector(self, "reset");
		                 } else if(inputVal == 0) {
		                 	minusBtn.attr("disabled", "disabled");
		                 	if(inputVal == max){
		                 		plusBtn.attr("disabled", "disabled");
		                 	}
		                 }else if(max == "FORCE_IN_STOCK" && inputVal > 1) {
		                 	ACC.kegreturns.updateQtyValue(self, inputVal)
		                 } else if (inputVal == max) {
		                     plusBtn.attr("disabled", "disabled")
		                 } else if (inputVal > max) {
		                     plusBtn.attr("disabled", "disabled")
		                 }
		             } else if (mode == "focusout") {
		             	if (isNaN(inputVal)){
		             		ACC.kegreturns.checkQtySelector(self, "reset");
		                     minusBtn.attr("disabled", "disabled");
		                 	if(0 == max){
		                 		plusBtn.attr("disabled", "disabled");
		                 	}
		             	} else if(inputVal >= max) {
		                     plusBtn.attr("disabled", "disabled");
		                 } else if(inputVal == 0){
		                 	minusBtn.attr("disabled", "disabled");
		                 }
		             }
		         },

		         updateQtyValue: function (self, value) {
		             var input = $(self).parents(".js-keg-qty-selector").find(".js-qty-selector-input");
		             var addtocartQty = $(self).parents(".addtocart-component").find("#addToCartForm").find(".js-qty-selector-input");
		             var configureQty = $(self).parents(".addtocart-component").find("#configureForm").find(".js-qty-selector-input");
		             input.val(value);
		             addtocartQty.val(value);
		             configureQty.val(value);
		             var max = input.attr('data-max');
		         },

		         initPageEvents: function () {

		         	$( ".js-qty-selector-input" ).each(function( index ) {
		                 var inputVal = $(this).val();
		                 var minusBtn = $(this).parents(".js-keg-qty-selector").find(".js-qty-selector-minus");
		                 var max = $(this).attr('data-max');
		                 var plusBtn = $(this).parents(".js-keg-qty-selector").find(".js-qty-selector-plus");
		                 
		                 if(inputVal == 0){
		                 	minusBtn.attr("disabled", "disabled");
		                 }
		                 var strInputVal = parseInt(inputVal);
		                 var strMaxVal = parseInt(max);
		                 
		                 if(strInputVal >= strMaxVal){
		                 	plusBtn.attr("disabled", "disabled");
		                 }
		                }); 

		             $(document).on("click", '.js-keg-qty-selector .js-qty-selector-minus', function () {
		                 ACC.kegreturns.checkQtySelector(this, "minus");
		             })
		             
		             $(document).on("click", '.product-item .thumb', function () {
		             	var input = $(this).parents(".product-item").find(".js-qty-selector-input");
		             	ACC.kegreturns.checkQtySelector(input, "reset");
		             	minusBtn.attr("disabled", "disabled");
		             	if(1 == max){
		             		plusBtn.attr("disabled", "disabled");
		             	}
		             })

		             $(document).on("click", '.js-keg-qty-selector .js-qty-selector-plus', function () {
		                 ACC.kegreturns.checkQtySelector(this, "plus");
		             })

		             $(document).on("keydown", '.js-keg-qty-selector .js-qty-selector-input', function (e) {

		                 if (($(this).val() != " " && ((e.which >= 48 && e.which <= 57 ) || (e.which >= 96 && e.which <= 105 ))  ) || e.which == 8 || e.which == 46 || e.which == 37 || e.which == 39 || e.which == 9) {
		                 }
		                 else if (e.which == 38) {
		                     ACC.kegreturns.checkQtySelector(this, "plus");
		                 }
		                 else if (e.which == 40) {
		                     ACC.kegreturns.checkQtySelector(this, "minus");
		                 }
		                 else {
		                     e.preventDefault();
		                 }
		             })

		             $(document).on("keyup", '.js-keg-qty-selector .js-qty-selector-input', function (e) {
		             	var isValid = ACC.kegreturns.resetInvalidInput(this);
		             	if(isValid == true){
		             		ACC.kegreturns.checkQtySelector(this, "input");
		                     ACC.kegreturns.updateQtyValue(this, $(this).val());
		             	}   
		             })
		             
		             $(document).on("focusout", '.js-keg-qty-selector .js-qty-selector-input', function (e) {
		             	var isValid = ACC.kegreturns.resetInvalidInput(this);
		             	if(isValid == true){
		             		ACC.kegreturns.checkQtySelector(this, "focusout");
		                     ACC.kegreturns.updateQtyValue(this, $(this).val());
		             	}   
		             })
					 /* validate keg returns 
					  * user should select at least one qty from keg return before submit */                 
					 $('#keg-returns-submit').click(function(e){
						 ACC.kegreturns.validateKegReturns();
						 var getPickupAddress = $('#pickupAddress').val();
					    if(getPickupAddress === null || getPickupAddress === ""){
					   	 $('#pickupAddressErrors').removeClass('hide');
					   	$('#kegGlobalErrorMessage').removeClass('hide');
					   	$('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
					   	 return false;
					    }
					    else{
					   	 $('#pickupAddressErrors').addClass('hide');
					   	$('#kegGlobalErrorMessage').addClass('hide');
					    }
					    if(!ACC.kegreturns.validateKegReturns()){
							 e.preventDefault();
							 $('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
						 }; 
					 })
					                    
		         },
		         
		         resetInvalidInput: function (self) {
		         	var input = $(self).parents(".js-keg-qty-selector").find(".js-qty-selector-input");
		             var inputVal = parseInt(input.val());
		             var max = input.attr('data-max');
		             
		             if(inputVal < 0 || inputVal > max){
		             	var stockErrorMsgDiv = $( "#qtyKegErrorMsg" );
		             	stockErrorMsgDiv.removeClass("hide");
						if (stockErrorMsgDiv.length) {
							$('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
						} 
						ACC.kegreturns.updateQtyValue(input, 0);
		             	var minusBtn = $(self).parents(".js-keg-qty-selector").find(".js-qty-selector-minus");
		             	minusBtn.attr("disabled", "disabled");
		             	return false;
		             }
		         	return true;
		         },
		         validateKegReturns:function(){
		        	 var flag = false;  
		        	 $( ".js-keg-qty-selector .js-qty-selector-input" ).each(function() {
					     var inputVal = parseInt($(this).val());
					     if (inputVal > 0) {
					           flag = true;
					      }
					    
					    }); 
					    if(!flag){
					    	 $('#kegQtyErrorMessage').removeClass('hide');
					    	 $('#kegGlobalErrorMessage').removeClass('hide');
					         return false; 
					     }
					    else{
					    	$('#kegQtyErrorMessage').addClass('hide');
					    	 $('#kegGlobalErrorMessage').addClass('hide');
					    	return true;					    
					    	}
		        	 
		         },
		         pickupAddressEvents: function () {
		        	$('#pickupAddress').change(function(){ 
		 			var selectedValue = $(this).find(":selected").val();
		 			$("#selectedAddressRecordId").val(selectedValue);
		 			$('.address-data').each(function(element) {
		 				var recordId = $(this).find('.address-data-index').val();
		 				if(recordId === selectedValue){
		 					$(this).removeClass("hide");
		 					var selectedAddress = $(this).find('.selected-address-data').html();
		 					
		 				}else{
		 					$(this).addClass("hide");
		 				}
		 			});
		 		});
			     $('#pickupAddress').trigger('change');
		      }
}
