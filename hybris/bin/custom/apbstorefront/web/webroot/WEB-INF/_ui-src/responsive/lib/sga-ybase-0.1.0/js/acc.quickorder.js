var ACC = ACC || {}; // make sure ACC is available

if (($("#quickOrder").length > 0) || ($("#checkPage").val() === 'savedCartDetailsPage')) {
    ACC.quickorder = {
        _autoload: [
			"addProductForQuickOrder",
			"initQuickOrderForm",
			"paginationSortBind",
			"maxQtyReachedCheck",
			"updateTotalQuickOrderProductsVal"
        ],
		
		rows: $(".addtocart-component .js-qty-selector-input:not(:disabled)"),

		addProductForQuickOrder: function () {
			
            $(document).on("click", '.quick-order-add-js', function () {
				
				if (!$("#generalErrorMsg").hasClass("hide")) {
					$("#generalErrorMsg").addClass("hide");
				}
				
				if (!$("#stockErrorMsg").hasClass("hide")) {
					$("#stockErrorMsg").addClass("hide");
				}

				var currentRowNumber = (this.id).substring(0, (this.id).indexOf('z'));
				var currentColNumber = (this.id).substring((this.id).indexOf('z') + 1);
				var rowColNumber = (this.id);
				var maxOrderQtyReached = false;
				var numberOfProductsReachedMaxQty = 0;
				var curMaxAllowed = 0;
				
				var numberOfProducts = document.getElementById('numberOfProducts').value;
				
				if (currentRowNumber == 0) {
					for (i = 1; i < (parseInt(numberOfProducts) + 1); i++) {
						
						var currentBoxToCheck = "#" + i + "z" + currentColNumber; 
						if ((($(currentBoxToCheck).length) > 0) && (!$(currentBoxToCheck).hasClass("product-unavailable-container"))) {
							
							var windowSize = $(window).width();
							
							if (windowSize < 768) {
							var currentMaxOrderQty = parseInt($("#B" + i + ".mobile-js-qty" + i).attr("data-max"));
							} else {
								var currentMaxOrderQty = parseInt($("#B" + i + ".desktop-js-qty" + i).attr("data-max"));
							}
							
							var currentQtyVal = parseInt($(currentBoxToCheck).text());
							
							if (currentQtyVal > currentMaxOrderQty) {
								maxOrderQtyReached = true;
								numberOfProductsReachedMaxQty++;
								curMaxAllowed = currentMaxOrderQty;
							} else {
								ACC.quickorder.changeAddToCartInputValue(i, currentColNumber);
							}
						}
					}
					
				} else {
					
					var windowSize = $(window).width();
					
					if (windowSize < 768) {
						var currentMaxOrderQty = parseInt($("#B" + currentRowNumber + ".mobile-js-qty" + currentRowNumber).attr("data-max"));
					} else {
						var currentMaxOrderQty = parseInt($("#B" + currentRowNumber + ".desktop-js-qty" + currentRowNumber).attr("data-max"));
					}
					
					var currentQtyVal = parseInt($(this).text());

					if (currentQtyVal > currentMaxOrderQty) {
						maxOrderQtyReached = true;
						numberOfProductsReachedMaxQty++;
						curMaxAllowed = currentMaxOrderQty;
					} else {
						ACC.quickorder.changeAddToCartInputValue(currentRowNumber, currentColNumber);
					}
				}
				
				if (maxOrderQtyReached) {
					
					if ((numberOfProductsReachedMaxQty == 1) && (currentRowNumber != "0")) {
						$("#allowedQuantity").html(curMaxAllowed);
						$("#stockErrorMsg").removeClass("hide");
						$('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
					} else if ((numberOfProductsReachedMaxQty > 0) && (currentRowNumber == "0")) {
						var errorData = "Oops – looks like at least one of the items from the selected date exceeds the maximum order limit. These products have not been added.";
						$("#generalErrorMsg").html(errorData).removeClass("hide");
						$('.pageBodyContent').animate({
									scrollTop: 0
									}, 1000);
					}

				}
					
			});
        },
		
		initQuickOrderForm: function () {
		
			$(document).on("submit", '.quick-order-form', function (e) {
				e.preventDefault();

				
				var numberOfProducts = $('#numberOfProducts').val();
				
				var quickOrderData = ACC.quickorder.generateQuickOrderJSONComp(numberOfProducts);
				
				if (quickOrderData === "") {
					var errorData = "Oops – you haven’t selected any products to add to your cart! Enter a quantity for one or more products and try again.";
    				$("#generalErrorMsg").html(errorData).removeClass("hide");
					$('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
					
				} else {
					var clearCartBool = $("#isCartEmpty").val();
				
					if (clearCartBool === "false") {
						$('#keepQuickOrderLayer').modal('show');
					} else {
						ACC.quickorder.submitQuickOrderForm("true", quickOrderData);			
					}
				}
			
			});
			
			$(".js-qty-selector-input").on("change paste keyup", function() {
			   if (!$("#generalErrorMsg").hasClass("hide")) {
					$("#generalErrorMsg").addClass("hide");
				}
				
				if (!$("#stockErrorMsg").hasClass("hide")) {
					$("#stockErrorMsg").addClass("hide");
				}
			});
			
			$(document).on("click", '.js-qty-selector-minus', function () {
				if (!$("#generalErrorMsg").hasClass("hide")) {
					$("#generalErrorMsg").addClass("hide");
				}
				
				if (!$("#stockErrorMsg").hasClass("hide")) {
					$("#stockErrorMsg").addClass("hide");
				}
				ACC.quickorder.updateTotalQuickOrderProductsVal(($(this).parent().parent().find(".js-qty-selector-input")), false, true);
			});
			
			$(document).on("click", '.js-qty-selector-plus', function () {
				if (!$("#generalErrorMsg").hasClass("hide")) {
					$("#generalErrorMsg").addClass("hide");
				}
				
				if (!$("#stockErrorMsg").hasClass("hide")) {
					$("#stockErrorMsg").addClass("hide");
				}
				ACC.quickorder.updateTotalQuickOrderProductsVal(($(this).parent().parent().find(".js-qty-selector-input")), true, false);
			});
			
			$(document).on("click", '#backToQuickOrder', function () {
				$("#colorbox").remove();
				$("#cboxOverlay").remove();
			});
			
			$(document).on("click", '.keepCartQuickOrderBtn', function () {

				$("#colorbox").remove();
				$("#cboxOverlay").remove();
				$(".modal").hide();
				
				var numberOfProducts = $('#numberOfProducts').val();
				var quickOrderData = ACC.quickorder.generateQuickOrderJSONComp(numberOfProducts);
				ACC.quickorder.submitQuickOrderForm("false", quickOrderData);	
			});
			
			$(document).on("click", '.clearCartQuickOrderBtn', function () {

				$("#colorbox").remove();
				$("#cboxOverlay").remove();
				$(".modal").hide();
				
				var numberOfProducts = $('#numberOfProducts').val();
				var quickOrderData = ACC.quickorder.generateQuickOrderJSONComp(numberOfProducts);
				ACC.quickorder.submitQuickOrderForm("true", quickOrderData);	
			});
		},
		
		submitQuickOrderForm: function (clearCartBool, quickOrderData) {
			JSONQuickOrderStr = "{\"clearCart\":\"" + clearCartBool + "\",\"quickOrderDataList\":[" + quickOrderData + "]}";
			JSONQuickOrderObj = JSON.parse(JSONQuickOrderStr);
			console.log(JSONQuickOrderObj);

			$.ajax({
				url: $(".quick-order-form").attr('action'),
				type: $(".quick-order-form").attr('method'),
				contentType: 'application/json',
				data: JSON.stringify(JSONQuickOrderObj),
				success: function (data) {
					var url = ACC.config.encodedContextPath + "/cart"
					window.location.replace(url);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					console.log("The following error occurred: " + textStatus, errorThrown);
				}
			});
		},
		
		generateQuickOrderJSONComp: function (numberOfProducts) {
			
			var quickOrderData = "";
			var numberOfProductsToAdd = 0;
			
			for (i = 1; i < (parseInt(numberOfProducts) + 1); i++) {
				var currentProductCode;
				var currentInputVal;
				var windowSize = $(window).width();

				if (($("#B" + i).length) > 0) {
					
					if (windowSize < 768) {
						currentInputVal = $(".mobile-js-qty" + i).val();
					} else {
						currentInputVal = $(".desktop-js-qty"  + i).val();
					}

					if ((parseInt(currentInputVal)) > 0) {
						if (($("#PC" + i).length) > 0) {
							currentProductCode = $("#PC" + i).val();
							numberOfProductsToAdd++;
							
							if (numberOfProductsToAdd == 1) {
								currentCodeValStr = "{\"code\":\"" + currentProductCode + "\",\"quantity\":\"" + currentInputVal + "\"}";
							} else {
								currentCodeValStr = ",{\"code\":\"" + currentProductCode + "\",\"quantity\":\"" + currentInputVal + "\"}";	
							}
							quickOrderData = quickOrderData.concat(currentCodeValStr);
						}
					}
				}
			}
			
			return quickOrderData;
		},
		
		changeAddToCartInputValue: function (currentRowNum, currentColNum) {
			var currentQtyID = "#" + currentRowNum + "z" + currentColNum;
			var currentQtyVal = $(currentQtyID).text();
			var windowSize = $(window).width();
			
			if (windowSize < 768) {
				currentAddToCartInput = $(".mobile-js-qty" + currentRowNum);
			} else {
				currentAddToCartInput = $(".desktop-js-qty" + currentRowNum);
			}
			var currentAddToCartButton = "#A" + currentRowNum;
			
			if (windowSize < 768) {
				currentInputVal = $(".mobile-js-qty" + currentRowNum).val();
			} else {
				currentInputVal = $(".desktop-js-qty" + currentRowNum).val();
			}
			
			var newQtyVal = parseInt(currentQtyVal) + parseInt(currentInputVal);
			
			$(currentAddToCartInput).val(newQtyVal);
			ACC.quickorder.updateTotalQuickOrderProductsVal(currentAddToCartInput, false, false);
			if (newQtyVal < 1) {
				$(currentAddToCartButton).attr('disabled', 'disabled');
			} else {
				$(currentAddToCartButton).removeAttr('disabled');
			}	
			//601973
        },
		
		paginationSortBind: function () {
			var currentPageUrl = window.location.href;
			var sortedOnName = "?sort=name";
            if (currentPageUrl.includes(sortedOnName)) {
				$("#nameSortOption").addClass("sel").siblings().removeClass("sel");
			} else {
				$("#nameSortOption").siblings().addClass("sel");
			}
        },
		
		maxQtyReachedCheck: function () {
			 $(".js-qty-selector-input").on("change paste keyup", function() { 
				var max = parseInt($(this).attr('data-max'));
        		var inputVal = parseInt($(this).val());
				if(inputVal < 0 || inputVal > max){
					$(this).blur();
					$("#allowedQuantity").html(max);
					var stockErrorMsgDiv = $( "#stockErrorMsg" );
					stockErrorMsgDiv.removeClass("hide");
					if (stockErrorMsgDiv.length) {
						$('.pageBodyContent').animate({
							scrollTop: 0
							}, 1000);
					}
				}
			});
			
			$(".js-qty-selector-input").on("change", function() { 
				ACC.quickorder.updateTotalQuickOrderProductsVal(this, false, false);
			});
        },
		
		updateTotalQuickOrderProductsVal: function (htmlObj, isPlusBtn, isMinusBtn) {
			var max = parseInt($(htmlObj).attr('data-max'));
			var inputVal = 0
            for (var i = 0; i < this.rows.length; i ++) {
                inputVal += parseInt(this.rows[i].value);
            }
//			var inputVal = parseInt($(htmlObj).val());
			var originalVal =  parseInt($(htmlObj).attr('original-val'));
			
			if (isPlusBtn) {
				inputVal++;
			} else if (isMinusBtn) {
				inputVal--;
			}

            $(".totalQuickOrderProducts").html(inputVal);
			var newTotalNumOfProds = parseInt(inputVal);

//			if (inputVal > originalVal) {
//				var differenceInQty = inputVal - originalVal;
//				$(htmlObj).attr('original-val', inputVal);
//				var newTotalNumOfProds = currentNumOfProds + differenceInQty;
//
//			} else if (inputVal < originalVal) {
//				var differenceInQty = originalVal - inputVal;
//				$(htmlObj).attr('original-val', inputVal);
//				var newTotalNumOfProds = currentNumOfProds - differenceInQty;
//			}
			
			if (newTotalNumOfProds == 0) {
				$(".totalQuickOrderProducts").parent().attr("disabled","disabled");
			} else if ((newTotalNumOfProds != 0) && (newTotalNumOfProds != null) && ($(".totalQuickOrderProducts").parent().is(":disabled"))) {
				$(".totalQuickOrderProducts").parent().removeAttr("disabled");
			}
//			$(".totalQuickOrderProducts").html(newTotalNumOfProds);
        }
    };
}