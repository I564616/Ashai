ACC.listquantityaddtocartaction = {
		_autoload: [
			"apbInitPageEvents"
			],
			/**
			 * Request add to cart from PLP page
			 */ 
		    apbInitPageEvents: function () {
		    	
		        $(document).on("submit", '.add_to_cart_form', function (e) {
		        	if(! ACC.asahiproductlisting.isAnonymousUser){
                    e.preventDefault();
                    var self = $(this);
					var redirectUrl = ACC.config.encodedContextPath + "/login";
                  $.ajax({
                      type: $(this).attr('method'),
                      url: $(this).attr('action'),
                      data: $(this).serialize(),
                      success: function (data, textStatus, xhr) 
                      {
						 if (data.status == 504) {
								window.location = redirectUrl;
						  }
						  
                    	  var cartAnalyticsData = data.cartAnalyticsData;
                    	  var label = "ProductListing";
                    	  //data-max each product is 100
                    	  //update data-max qty after add to cart
                          var input = self.parents(".addtocart-component").find('.js-qty-selector-input');
                          var inputVal = parseInt(input.val());
                          var max = input.attr('data-max');
                          var availableStocks = max - inputVal;
                          input.attr("data-max", availableStocks);
                          ACC.productDetail.checkQtySelector(input, "reset");
                          if(availableStocks < 1)
                          {
                        	  var listAddToCartButton = $(input).parents(".addtocart-component").find(".list-add-to-cart");
                        	  listAddToCartButton.attr("disabled", "disabled");
                          }
                          var cartError = data.cartError;
                   		  if(cartError !== ""){
                   			  var priceUpdateFailedErr = $("#priceUpdateFailedErr");
                   			  priceUpdateFailedErr.text(cartError);
                   			  priceUpdateFailedErr.removeClass("hide");
	                   		  if (priceUpdateFailedErr.length) {
	                   			$('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
	                          }
                   		  }
                   		  
                   		  if($('#sgaSite').length){
                   			  ACC.track.trackAddProductToCart(cartAnalyticsData.productCode, inputVal, label);
                   		  }
                   		  //update minicart total amount
                          ACC.minicart.updateMiniCartDisplay();
                      },
                      error: function (data, textStatus, xhr) 
                      {
                          console.error('Add to Cart Error.');
                		  if (data.status == 504) {
								window.location = redirectUrl;
						  }
                      },
                  });
		        }
		        	else{
		        		var input = $(this).parents(".addtocart-component").find('.js-qty-selector-input');
                        var inputVal = parseInt(input.val());
                          var max = input.attr('data-max');
                          var availableStocks = max - inputVal;
                          input.attr("data-max", availableStocks);
                          ACC.productDetail.checkQtySelector(input, "reset");
                          if(availableStocks < 1){
                              var listAddToCartButton = $(input).parents(".addtocart-component").find(".list-add-to-cart");
                              listAddToCartButton.attr("disabled", "disabled");
                          }
                          ACC.minicart.updateMiniCartDisplay();
		        		
		        	}
                     
          });

		    } 
    }
    


