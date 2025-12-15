/* globals pageType */
ACC.product

ACC.product = {

	initQuickviewLightbox:function(){
		this.enableAddToCartButton();
		this.bindToAddToCartForm();
	},

	changedeliverydt: function ()
	{
		$('.delivery-header.delivery-header-desktop').mouseenter();
	},

	enableAddToCartButton: function ()
	{
		if($('#productPurchable').val() == 'true' && $('#stockStatus').val()!='outOfStock'){
			$('#addToCartButton').removeAttr("disabled");
		}
		if($('#stockStatus').val() == 'outOfStock'){
			$('#addToCartButton').html('OUT OF STOCK');
		}

	},

	bindToAddToCartForm: function ()
	{
		var addToCartForm = $('.add_to_cart_form');
		addToCartForm.ajaxForm({success: ACC.product.displayAddToCartPopup});
	},

	displayAddToCartPopup: function (cartResult, statusText, xhr, formElement)
	{

		// add max quantity error message
        handleOrderError(cartResult);

		var productCode = $(formElement).children('input[name="productCodePost"]').val(); // Fix to satisfy ACC.track.trackAddToCart() call below. Remove if needed.
		var quantity = parseInt($(formElement).children('input[name="qty"]').val());

		$('#addToCartLayer').remove();

		$('.js-track-product-addtocartpopup').remove();
		$('.js-track-order-addtocartpopup').remove();

		if (typeof ACC.minicart.refreshMiniCartCount == 'function')
		{
			ACC.minicart.refreshMiniCartCount();
		}

		if ($('#header .miniCart').is(':visible')) {
			$("#header .miniCart #miniCartPopup").append(cartResult.addToCartLayer);
		}

		if ($('#nav').is(':visible')) {
			$("#nav").append(cartResult.addToCartLayer);
		}

		/* update both desktop and mobile minicart badge count in the header component and remove `inivisible` class if count > 0 */
		$(".miniCart .count").html(cartResult.totalItemCount).removeClass('hide');
        $(".cart-mobile .count").html(cartResult.totalItemCount).removeClass('hide');

		/* show minicart tooltip then hiden after 2secs */
		$('.minicart-tooltip').fadeIn('slow').delay(2000).fadeOut('slow');
		/*
		$('#addToCartLayer').show(function(){
			ACC.product.popupActions();
			if (typeof timeoutId != 'undefined')
			{
				clearTimeout(timeoutId);
			}
			timeoutId = setTimeout(function ()
			{
				$('#addToCartLayer').hide();
			}, 5000);

		}); */

        if($('#recommendationsCount').html() != ''){
           rm.recommendation.displayRecommendationCount($('#recommendationsCount').html());
        }
		ACC.track.trackAddToCart(productCode, quantity, cartResult.cartData);

		ACC.product.addListeners();

		if ( $('.addToCartEventTag').length === 0 && statusText === 'success' ) {
			rm.responsivetable.addAddToCartListener('.js-track-product-addtocartpopup');
		}

		rm.responsivetable.addAddToCartListener('.js-track-order-addtocartpopup');
	},

	viewCartPopup: function(cartResult){
		var windowHeight = $(window).height(),
			allowedHeight;

		$('#addToCartLayer').remove();

		if ($('#header .miniCart').is(':visible')) {
			$("#header .miniCart #miniCartPopup").append(cartResult.addToCartLayer);
			ACC.product.popupActions();
		}

		if ($('#nav').is(':visible')) {
			$("#nav").append(cartResult.addToCartLayer);
		}

		/* $('#addToCartLayer').show(); */
		ACC.product.popupActions();
		//$('body').css('overflow','hidden');

		//hide minicart badge if cartResult.totalItemCount == 0
		if(cartResult.totalItemCount == 0){
			$(".miniCart .count").addClass('hide');
		}

		ACC.product.addListeners();
	},
	popupActions: function(){
		var windowHeight = $(window).height(),
			allowedHeight = windowHeight - 300, // Minus height of header and button etc at bottom of minicart
			link = $('.miniCart .items');

			$('#addToCartLayer .itemList').css('max-height',allowedHeight);

		//$('#addToCartLayer .close').on('touchend',function (){
		$('#addToCartLayer .close').on('click',function (){
		    var popup = $('#addToCartLayer');
		    popup.hide();
		    link.removeClass('open');
		    $('html').removeClass('overflow-hidden');
		});

		// if(link.hasClass('open')){
			$(document).one('click',function (e){
			    var popup = $('#addToCartLayer');

			    if (!popup.is(e.target) && popup.has(e.target).length === 0) {
			        popup.hide();
			        link.removeClass('open');
			        $('html').removeClass('overflow-hidden');
			    }
			});
		// }
	},

	addListeners: function() {
		$('.js-track-product-link').on('click', function(e) {
			with ($(this)) {
				var productObj = {
					'currencycode' 	: data('currencycode'),
					'name' 			: data('name'),
					'id'			: data('id'),
					'price'			: data('price'),
					'brand'			: data('brand'),
					'category'		: data('category'),
					'variant'		: data('variant'),
					'position'		: data('position'),
					'url'			: data('url'),
					'actionfield'	: data('actionfield')
				};
			};

			if (typeof rm.tagManager.trackProductClick !== 'undefined') {
				rm.tagManager.trackProductClick(productObj);
			}
		});


	}

};

function handleOrderError (result) {
    if (result.hasOwnProperty("maxOrderError")) {
        var errorMsg = result.maxOrderError.message;
        if (errorMsg === '') {
            $(".order-error-message").removeClass("alert").text(errorMsg);
        } else {
            $(".order-error-message").addClass("alert").text(errorMsg);
        }
    }
}

$(document).ready(function ()
{
	with(ACC.product)
	{
		bindToAddToCartForm();
		enableAddToCartButton();
	}
});
