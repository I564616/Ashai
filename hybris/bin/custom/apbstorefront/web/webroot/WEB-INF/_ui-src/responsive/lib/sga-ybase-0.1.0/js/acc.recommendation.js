ACC.recommendation = {
    _autoload: [
        "loadCount",
        "initRecommendationsOnHomepage"
    ],

    addUrl: ACC.config.encodedContextPath + '/recommendation/add',
    updateUrl: ACC.config.encodedContextPath + '/recommendation/updateRecommendations',

    loadCount: function () {
        var self = this;
        // check sessionStorage
        var hasCountStored = sessionStorage.getItem('recommendations-count');
        if (hasCountStored) {
            $('.recommendation-count').text(hasCountStored);
        } else {
            if (isBDECUSTOMERGROUP) {
                $.ajax({
                    url: ACC.config.encodedContextPath + '/recommendation/getTotalCount',
                    type: 'GET',
                    dataType: 'json',
                    contentType: 'application/json',
                    success: function (response) {
                        if (response !== '') {
                            self.updateCount(response)
                        }
                    },
                    error: function (error) {
                        console.log(error);
                    }
                });
            };
        }
    },

    add: function (ele) {
        var self = this;
        // disabled add recommendation action
        if (ele.className.indexOf('added') !== -1) { return ; }
        var productCode = $(ele).data('productCode');
        var qty = $('.addToCartForm' + productCode).find('input[name=pdpAddtoCartInput]').val();
        var data = {
            productCode,
            qty
        };
        $.ajax({
            url: this.addUrl,
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            contentType: 'application/json',
            success: function (response) {
                self.addedAction(ele, true);
                self.updateCount(response.recommendationsCount);

                $('.recommendation-tooltip').fadeIn('slow').delay(2000).fadeOut('slow');
                if (document.body.clientWidth < parseInt(screenXsMax.replace('px', '')) ) {
                    $('.mobile-recommendation-tooltip').fadeIn('slow').delay(2000).fadeOut('slow');
                }

                setTimeout(function () {
                    self.addedAction(ele, false);
                }, 5000);
            },
            error: function (error) {
                console.log(error);
            }
        });
    },

    update: function (ele, action, productCode, index) {
        var self = this;
        var success = $('.page-repRecommendation .save-cart-success.success-' + index);
        var fail = $('.page-repRecommendation .save-cart-success.fail-' + index);
        var input = $('input[name=templateEntries-' + ele.dataset['index'] + ']');
        var quantity = input.val();
        var data = {
            productCode,
            quantity,
            action
        };
        if (action === 'REMOVE_ALL') {
            data = { action }
        } else if ( action === 'REMOVE' ) {
            data = {
                productCode,
                action
            }
        }

        $.ajax({
            url: this.updateUrl,
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            contentType: 'application/json',
            success: function (res) {
                if (action === 'UPDATE') {
                    self.updateCount(res);
                    if (res) {
                        success.removeClass('hidden');
                        fail.addClass('hidden');
                        ele.setAttribute('disabled', true);
                    } else {
                        success.addClass('hidden');
                        fail.removeClass('hidden');
                        ele.setAttribute('disabled', true);
                    }

                } else {
                    var count = action === 'REMOVE'? res : 0;
                    self.updateCount(count);
                    window.location.reload();
                }
            },
            error: function (error) {
                success.addClass('hidden');
                fail.removeClass('hidden');
                ele.setAttribute('disabled', true);
            }
        });
    },

    onChange: function (ele, index) {
        // desktop
        var save = $('#js-saved-recommendation-' + index);
        var inputValue = parseInt($('#templateAddtoCartInput-' + index).val());

        var success = $('.page-repRecommendation .save-cart-success.success-' + index);
        var fail = $('.page-repRecommendation .save-cart-success.fail-' + index);

        // Get correct value after increment/decrement for click event.
        // input event will get correct value without adjustment
        if (ele.className.indexOf('minus') !== -1) {
            inputValue -= 1;
        } else if (ele.className.indexOf('plus') !== -1) {
            inputValue += 1;
        }

        if (inputValue === 0) {
            save.attr('disabled', true);
        }

        if (save[0].disabled && inputValue !== 0) {
            save.attr('disabled', false);
        }

        // Error Handling
        if (!success.hasClass('hidden')) {
            success.addClass('hidden');
        } else if (!fail.hasClass('hidden')) {
            fail.addClass('hidden');
        }
    },

    updateCount: function (count) {
        sessionStorage.setItem('recommendations-count', count);
        $('.recommendation-count').text(count);
    },

    addedAction: function (ele, action) {
        if (action) {
            $(ele).addClass('added');
            $(ele).find('#star-blue-outline').addClass('hidden');
            $(ele).find('#star-blue').removeClass('hidden');
            $(ele).find('.recommendation-action-text').text('Added');
        } else {
            $(ele).removeClass('added');
            $(ele).find('#star-blue-outline').removeClass('hidden');
            $(ele).find('#star-blue').addClass('hidden');
            $(ele).find('.recommendation-action-text').text('Add');
        }
    },

    // Above code is only for TMs/Reps to add recommendations for customers.
    //Below code is for recommendations component on SGA homepage for all customers
    initRecommendationsOnHomepage: function () {
         var form = $('.add_recommendation_to_cart_form');
        
        //track Asahi recommedations on homepage - Start
        
        var section1Recommendation = $('.recommendation-component').find('.recommendations-section-1');
        var section2Recommendation = $('.recommendation-component').find('.recommendations-section-2');
        var section3Recommendation = $('.recommendation-component').find('.recommendations-section-3');
        var section1Product='';
        var section2Product='';
        var section3Product='';
        var recommendedProducts = [];
        if(section1Recommendation.length){
        //Adding section1Product for asahiProductRecommendation tracking
        section1Product = $(section1Recommendation).find(".add_recommendation_to_cart_form #addProductCode").val();
        recommendedProducts.push(section1Product);
        }
        if(section2Recommendation.length){
        //Adding section2Product for asahiProductRecommendation tracking
        section2Product = $(section2Recommendation).find(".add_recommendation_to_cart_form #addProductCode").val();
        recommendedProducts.push(section2Product);
        }
        if(section3Recommendation.length){
        //Adding section3Product for asahiProductRecommendation tracking
        section3Product = $(section3Recommendation).find(".add_recommendation_to_cart_form #addProductCode").val();
        recommendedProducts.push(section3Product);
        }
        
        if(recommendedProducts.length){
    	ACC.track.trackProductRecommendation(section1Product,section2Product,section3Product,"asahiProductRecommendation");
    	}
    	
    	//track Asahi recommedations on homepage - End
    	
        form.on('submit', function (e) {
            e.preventDefault();
            var productCode = $(this).find("#addProductCode").val();
            var productQty = $(this).find("#qty").val();
            var quantity = 1;
	        if (productQty != undefined) {
	            quantity = productQty;
	        }
	        //track Asahi recommedations Addtocart
            ACC.track.trackAsahiProductRecommendationAddToCart(productCode,quantity,"asahiProductRecommendationAddToCart");
            
            ACC.recommendation.sendAddToCartAjax(this);
        });

        $( document ).ready(function() {
            $('.recommendation-component .js-qty-selector .js-qty-selector-input').each(function () {
                ACC.productDetail.checkQtySelector($(this), "focusout");
            });
        });
    },

    sendAddToCartAjax: function (form) {
        var isInt = function (value) {
            //checks if value is a number(both int and string)
            return !isNaN(value) && 
                   parseInt(Number(value)) == value && 
                   !isNaN(parseInt(value, 10));
          };
        var self = $(form);

        $.ajax({
            type: self.attr('method'),
            url: self.attr('action'),
            data: self.serialize(),
            success: function (data) {
                //data-max each product is either 100 or FORCE_IN_STOCK
                //update data-max qty after add to cart
                var input = self.parents(".addtocart-component").find('.js-qty-selector-input');
                var inputVal = parseInt(input.val());
                var max = input.attr('data-max');
                if (isInt(max)) {
                    var availableStocks = max - inputVal;
                } else {
                    var availableStocks = max;
                }
                
                input.attr("data-max", availableStocks);

                if (!input.attr('data-value')) {
                    // Reset qty to 1 if no rep recommended qty
                    ACC.productDetail.checkQtySelector(input, "reset");
                } else if (isInt(input.attr('data-value'))) {
                    // Reset to rep recommended quantity
                    ACC.productDetail.updateQtyValue(input, input.attr('data-value'));
                    ACC.productDetail.checkQtySelector(input, "focusout");
                }
                
                if (availableStocks < 1) {
                    var listAddToCartButton = $(input).parents(".addtocart-component").find(".list-add-to-cart");
                    listAddToCartButton.attr("disabled", "disabled");
                }
                var cartError = data.cartError;
                if (cartError !== "") {
                    var priceUpdateFailedErr = $("#generalErrorMsg");
                    priceUpdateFailedErr.text(cartError);
                    priceUpdateFailedErr.removeClass("hide");
                    if (priceUpdateFailedErr.length) {
                        $('.pageBodyContent').animate({
                        scrollTop: 0
                        }, 1000);
                    }
                }
                //update minicart total amount
                ACC.minicart.updateMiniCartDisplay();
            },
            error: function (data) {
                console.error('Cart Request Error.');
            },
        });
    }
}