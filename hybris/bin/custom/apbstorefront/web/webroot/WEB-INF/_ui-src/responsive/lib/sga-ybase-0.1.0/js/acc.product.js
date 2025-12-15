ACC.product = {

    _autoload: [
        "setUserTimeCookie",
        "bindToAddToCartForm",
        "enableStorePickupButton",
        "enableVariantSelectors",
        "bindFacets"
    ],

    
    setUserTimeCookie: function () {
    	var str = new Date().toString().match(/\(([A-Za-z\s].*)\)/)[1];
    	document.cookie = "asahiUserTimeOffsetCookie" + "=" + new Date().getTimezoneOffset()*60*1000;
    },
    // show filters on mobile and tablet after click on refine button
    bindFacets: function () {
        $(document).on("click", ".js-show-facets", function (e) {
            e.preventDefault();
            $('#hamburger-nav-menu').hide();
            var selectRefinementsTitle = "CLOSE";
			var currentPage = $("#checkPage").val();
            ACC.colorbox.open(selectRefinementsTitle, {
                href: ".js-product-facet",
                top: 60, 
                left: "0",
                inline: true,
                width: "100%",
                height:"100%",
                className: 'facet-colorbox ' + currentPage + '-js',
                onComplete: function () {
                	$('#search-bar-container-fluid').css('height', '50px');
                	$('#cboxClose').html('');
                	$('#cboxClose').html('<img class="menu-close-icon" src="' + ACC.config.commonResourcePath + '/images/close_white.svg" />');
                	
                   $(document).on("click", ".js-product-facet .js-facet-name", function (e) {
                        e.preventDefault();
                        $(this).parents(".js-facet").toggleClass("active");
                        
                    })
                },
                onClosed: function () {
                	$('#search-bar-container-fluid').css('height', '');
                    $(document).off("click", ".js-product-facet .js-facet-name");
                    $('#hamburger-nav-menu').show();
                }
            });
        });
        enquire.register("screen and (min-width:" + screenSmMax + ")", function () {
            $("#cboxClose").click();
        });
    },


    enableAddToCartButton: function () {
        $('.js-enable-btn').each(function () {
            if (!($(this).hasClass('outOfStock') || $(this).hasClass('out-of-stock'))) {
                $(this).removeAttr("disabled");
            }
        });
    },

    enableVariantSelectors: function () {
        $('.variant-select').removeAttr("disabled");
    },

    bindToAddToCartForm: function () {
        var addToCartForm = $('.add_to_cart_form');
        addToCartForm.ajaxForm({
        	beforeSubmit:ACC.product.showRequest,
        	success: ACC.product.bindMiniCart
         });    
        setTimeout(function(){
        	$ajaxCallEvent  = true;
         }, 2000);
     },
     showRequest: function(arr, $form, options) {  
    	 if($ajaxCallEvent)
    		{
    		 $ajaxCallEvent = false;
    		 return true;
    		}   	
    	 return false;
 
    },

    bindToAddToCartStorePickUpForm: function () {
        var addToCartStorePickUpForm = $('#colorbox #add_to_cart_storepickup_form');
        addToCartStorePickUpForm.ajaxForm({success: ACC.product.displayAddToCartPopup});
    },

    enableStorePickupButton: function () {
        $('.js-pickup-in-store-button').removeAttr("disabled");
    },

    displayAddToCartPopup: function (cartResult, statusText, xhr, formElement) {
    	var cartAnalyticsData = cartResult.cartAnalyticsData;
    	if(cartAnalyticsData=== undefined)
    		{
	    		window.location.replace(ACC.config.encodedContextPath + "/login");
    		}
    	else
    	{
	    	$ajaxCallEvent=true;
	        $('#addToCartLayer').remove();
	        if (typeof ACC.minicart.updateMiniCartDisplay == 'function') {
	            ACC.minicart.updateMiniCartDisplay();
	        }
	        var titleHeader = $('#addToCartTitle').html();
	
	        ACC.colorbox.open(titleHeader, {
	            html: cartResult.addToCartLayer,
	            width: "460px"
	        });
	
	        var productCode = $('[name=productCodePost]', formElement).val();
	        var quantityField = $('[name=qty]', formElement).val();
	
	        var quantity = 1;
	        if (quantityField != undefined) {
	            quantity = quantityField;
	        }
	        var cartData = {
	            "cartCode": cartAnalyticsData.cartCode,
	            "productCode": productCode, "quantity": quantity,
	            "productPrice": cartAnalyticsData.productPostPrice,
	            "productName": cartAnalyticsData.productName
	        };
	        ACC.track.trackAddToCart(productCode, quantity, cartData);
    	}
    },
    bindMiniCart: function (cartResult, statusText, xhr, formElement) {
        var url = $(".mini-cart-link.js-mini-cart-link").data("miniCartUrl");
        var cartName = ($(this).find(".js-mini-cart-count").html() != 0) ? $(this).data("miniCartName"):$(this).data("miniCartEmptyName");
        var cartAnalyticsData = cartResult.cartAnalyticsData;
    	if(cartAnalyticsData=== undefined)
    		{
	    		window.location.replace(ACC.config.encodedContextPath + "/login");
    		}
    	else
    	{
    		var cartError = cartResult.cartError;
    		if(cartError != undefined){
    			var priceUpdateFailedErr = $("#priceUpdateFailedErr");
     			  priceUpdateFailedErr.text(cartError);
     			  priceUpdateFailedErr.removeClass("hide");
         		  if (priceUpdateFailedErr.length) {
         			  $(document).scrollTop(priceUpdateFailedErr.offset().top);
                  }
    		}
	    	$ajaxCallEvent=true;
	        $('#addToCartLayer').remove();
	        if (typeof ACC.minicart.updateMiniCartDisplay == 'function') {
	            ACC.minicart.updateMiniCartDisplay();
	        }
	        var titleHeader = $('#addToCartTitle').html();
	
	        var productCode = $('[name=productCodePost]', formElement).val();
	        var quantityField = $('[name=qty]', formElement).val();
	
	        var quantity = 1;
	        if (quantityField != undefined) {
	            quantity = quantityField;
	        }
	        var cartData = {
	            "cartCode": cartAnalyticsData.cartCode,
	            "productCode": productCode, "quantity": quantity,
	            "productPrice": cartAnalyticsData.productPostPrice,
	            "productName": cartAnalyticsData.productName
	        };
	        ACC.track.trackAddToCart(productCode, quantity, cartData);
    	}
      },

    addBonus: function (ele) {
        ACC.product.hideExistingErrorMessages();
        var currentPage = $("#checkPage").val();
        var productCode = ele.dataset['productCode'];
        var productName = ele.dataset['productName'];
        var CSRFToken = ele.dataset['csrfToken'];

        var input = $('.addToCartForm'+productCode).length !== 0 ?$('.addToCartForm'+productCode).find('.qty.js-qty-selector-input') : $('.addtocart-component').find('.js-qty-selector-input');

        if ($(this).hasClass("add-as-bonus")) {
            var inputVal = 1;
        } else {
            var inputVal = parseInt(input.val());
        }

        var self = $(this);

        var overMax = ACC.product.overMaxQty(productCode, inputVal);
        var currentPage = $("#checkPage").val();

        if (overMax) {
            $("#bonusStockErrorMsg").removeClass("hide");
            if ($("#bonusStockErrorMsg").length) {
                $('.pageBodyContent').animate({
                    scrollTop: 0
                }, 1000);
            }
        } else {
            $.ajax({
                type: 'POST',
                url: ele.dataset['addBonusUrl'],
                data: this._serialize(productCode, productName, inputVal),
                success: function (data) {
                    ACC.minicart.updateMiniCartDisplay();
                    if (currentPage === "cartPage") {
                        location.reload();
                    }
                },
                error: function (data) {
                    console.error('Cart Request Error.');
                },
            });
        }

    //            ACC.product.resetBonusButtons(productCode);

        },

    overMaxQty: function (productCode, inputVal) {
        var urlValidate = ACC.config.encodedContextPath + "/getAllowedBonusProducts?productCode=" + productCode;
        var returnVar = false;

        $.ajax({
                url: urlValidate,
                type: "GET",
                dataType: "json",
                async: false,
                cache: false,
                success: function (data) {
                    if (data !== null) {
                        if (parseInt(inputVal) > parseInt(data)) {
                            returnVar = true;
                        }
                        $("#allowedBonusQty").html(data);
                    } else {
                        console.log("Data returned null");
                    }
                },
                error: function (xmlHttpRequest, errorText, thrownError) {
                    console.log("Max Value Check Error: " + thrownError + " " + errorText);
                }
        });
        return returnVar;
    },

    resetBonusButtons: function (productCode) {

        var currentPage = $("#checkPage").val();
        var allowedBonusQty = $("#allowedBonusQty").html();

        if (currentPage === "productGrid" || currentPage === "searchGrid") {
            $('.addToCartForm'+productCode).find('.js-qty-selector-input').val(1);
            $('.addToCartForm'+productCode).find('.js-qty-selector-minus').attr("disabled", "disabled");
            $('.addBonusToCartForm'+productCode).find('.qty.js-qty-selector-input').val(1);

        } else if (currentPage === "productDetails") {
            $('#pdpAddtoCartInput').val(1);
            $("#pdpAddtoCartInput").parents().find('.js-qty-selector-minus').attr("disabled", "disabled");
            $('.addBonusToCartForm'+productCode).find('.qty.js-qty-selector-input').val(1);
        }

        if ((parseInt(allowedBonusQty)) <= 0) {
            $("#" + productCode).find("button").attr("disabled", "disabled");
        }

        $("#" + productCode).find("button").blur();
    },

    hideExistingErrorMessages: function () {

        if (!$("#bonusStockErrorMsg").hasClass("hide")) {
            $("#bonusStockErrorMsg").addClass("hide");
        }
        if (!$("#stockErrorMsg").hasClass("hide")) {
            $("#stockErrorMsg").addClass("hide");
        }
    },

    _serialize: function (code, name, qty) {
        return 'productCodePost=' + code + '&productNamePost=' + name + '&qty=' + qty;
    }
};

$(document).ready(function () {
	$ajaxCallEvent = true;
    ACC.product.enableAddToCartButton();
});