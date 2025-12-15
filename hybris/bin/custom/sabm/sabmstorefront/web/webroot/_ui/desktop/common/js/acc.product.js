ACC.product = {
	
	initQuickviewLightbox:function(){
		this.enableAddToCartButton();
		this.bindToAddToCartForm();
	},
	
	enableAddToCartButton: function ()
	{
		if($('#productPurchable').val() == 'true'){
			$('#addToCartButton').removeAttr("disabled");
		}
		
	},
	
	bindToAddToCartForm: function ()
	{
		var addToCartForm = $('.add_to_cart_form');
		addToCartForm.ajaxForm({success: ACC.product.displayAddToCartPopup});
	},

	displayAddToCartPopup: function (cartResult, statusText, xhr, formElement)
	{
		$('#addToCartLayer').remove();
		
		if (typeof ACC.minicart.refreshMiniCartCount == 'function')
		{
			ACC.minicart.refreshMiniCartCount();
		}
		
		if ($('#header').is(':visible')) {
			$("#header .container").append(cartResult.addToCartLayer);
		}
		
		if ($('#nav').is(':visible')) {
			$("#nav").append(cartResult.addToCartLayer);
		}
		
		$(".miniCart .count").html(cartResult.totalItemCount);

		$('#addToCartLayer').fadeIn(function(){
			$.colorbox.close();
			if (typeof timeoutId != 'undefined')
			{
				clearTimeout(timeoutId);
			}
			timeoutId = setTimeout(function ()
			{
				$('#addToCartLayer').fadeOut();
			}, 5000);
			
		});
        rm.recommendation.displayRecommendationCount(cartResult.recommendationsCount);
		ACC.track.trackAddToCart(productCode, quantity, cartResult.cartData);
	},

	addToRecommendation: function() {
        $('.addRecommendationText').on('click',function(){
        	var $addToCartForm = $(this).closest('.deal-item').find('.add_to_cart_form');
        	if($addToCartForm.length > 0){
        		$.ajax({
        			url:'/sabmStore/en/recommendation/add',
        			type:'GET',
        			data:$addToCartForm.serialize(),
        			success: function(result) {
        				ACC.product.displayAddToCartPopup(result,null,null,$addToCartForm);
        			},
        			error:function(result) {
        				console.error(result);
        			}
        		});
        	}

        });
	}



};

$(document).ready(function ()
{
	with(ACC.product)
	{
		bindToAddToCartForm();
		enableAddToCartButton();
	}
});

