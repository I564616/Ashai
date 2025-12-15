/* globals ACC */
/* globals window */
/* globals document*/
/* globals sessionStorage*/

'use strict';


rm.recommendation = {

		createListeners: function() {
		    var bdeViewOnly= false;
		    if($('#bdeUser').length > 0){
                    bdeViewOnly = true;
            }
			$('#recommendationHeaderStar').popover({
                placement: 'bottom',
                html: true,
                trigger: 'manual',
                animation: true,
                delay:{show: 100, hide: 1000},
                template: '<div class="popover popover-recommendationwelcome" ><div class="arrow"></div><div class="popover-content popover-recommendationwelcome-content"></div></div>'
            });
			

			$('#globalMessages .recommendationwelcome-mobile').addClass('hidden');
			$('#cartCtrl #simulationErrors .recommendationwelcome-mobile').addClass('hidden');

			if(parseInt($('.recommendationsCount').text()) > 1 && sessionStorage.getItem('ShowWelcome') === 'TRUE' && !bdeViewOnly){
                $('#recommendationHeaderStar').popover('toggle');
				$('#globalMessages .recommendationwelcome-mobile').removeClass('hidden');
				$('#cartCtrl #simulationErrors .recommendationwelcome-mobile').addClass('hidden');


                sessionStorage.setItem('ShowWelcome','FALSE');
                setTimeout(function ()
                 {
                     $('#recommendationHeaderStar').popover('hide');
					 $('#globalMessages .recommendationwelcome-mobile').addClass('hidden');
					 $('#cartCtrl #simulationErrors .recommendationwelcome-mobile').addClass('hidden');


                 }, 10000);
            }

			// When changing the quantity (Sales Rep page & Customer page)
			$('.recommendationRepRow .select-quantity .up, .recommendationRepRow .select-quantity .down, .recommendationRepRow .select-list .select-items > li, .recommendationCusRow .select-quantity .up, .recommendationCusRow .select-quantity .down, .recommendationCusRow .select-list .select-items > li')
			.on('click touchstart',function() {
				var input = $(this).closest('.product-row').find('.qty-input'),
					min = input.data('minqty');

				setTimeout(function(){
					if(isNaN(input.val()) || parseInt(input.val(), 10) < min) {
						input.val(min);
					}

				},50);

				if (!$(this).hasClass('disabled')){ // for Sales Rep page
					rm.recommendation.enableUpdateRecommendations();
				}
			});

			// When changing the quantity (Sales Rep page & Customer page)
			$('.recommendationRepRow .select-quantity .qty-input, .recommendationCusRow .select-quantity .qty-input').on('keyup',function() {
				var that = $(this),
					min = that.data('minqty');

				setTimeout(function(){
					if(isNaN(that.val()) || parseInt(that.val(), 10) < min) {
						that.val(min);
					}

					if ($(this).hasClass('recommendationRepRow') && $(':focus').is('.qty-input')) { // for Sales Rep page
						rm.recommendation.enableUpdateRecommendations();
					}
				},2000);
			});

			// When deleting a recommendation (Sales Rep page)
			//$('.recommendationRepRow .submitRemoveRecommendation').on('click',function(e) {
			$('.recommendationRepRow .deleteRecommendation').on('click',function(e) {
				e.preventDefault();
				rm.recommendation.deleteRecommendation($(this));
				rm.recommendation.enableUpdateRecommendations();
			});

			// When deleting a recommendation (Customer page) or when rejecting a recommendation (Cart page)
			$('.recommendationCusRow .deleteRecommendation, .recommendation-cart-actions .deleteRecommendation').on('click',function(e) {
				e.preventDefault();
				rm.recommendation.updateRecommendation($(this), 'REJECTED');
                rm.cart.refreshPage();
			});

			// When clicking the "UPDATE RECOMMENDATIONS" button (Sales Rep page)
			$('#update-recommendation-button').on('click',function() {
				if(!$(this).hasClass('disabled')){
					rm.recommendation.updateQuantityOrUnit();
				}
			});

      // Copy number of recommendations to mobile view on load
      $(document).ready(function(){
        var src = $('.recommendation .recommendationsCount').html();
        console.log(src);
        $('.navbar-toggle .recommendationsCount').html(src);
        rm.recommendation.displayRecommendationCount(src);
      });


			/*
			// When clicking on any anchor tag except the "Delete" link on each recommendation (Sales Rep page)
			$('a:not(.recommendationRepRow .submitRemoveRecommendation)').on('mousedown',function(e) {
				e.preventDefault();

				var target = $(this).attr('href');

				if (target !== '#' && target !== '' && target !== null) {
					if (!$('#update-recommendation-button').hasClass('disabled')) {
						$('#unsavedChangesPopup').attr('data-target', target);
						rm.recommendation.unsavedChangesPopup();
					} else {
						window.location.href = target;
					}
				}
			});

			// When clicking "Yes, Save My Changes" button on the unsaved changes popup
			$('.unsaved-changes-popup .btn-primary').one('click',function() {
				rm.recommendation.updateQuantityOrUnit();
				$.magnificPopup.close();
			});
			*/
            $('.addRecommendationAction').on('click',function(){
                var addToCartForm = $(this).closest('.add_to_cart_form');
                var quantityField = $(this).closest('.addtocart-qty');
                var productCode = $('[name=productCodePost]', addToCartForm).val();
                var quantityValue = $('[name=qty]', addToCartForm).val();
                var uom = $('[name=unit]', addToCartForm).val();
                var dataPost = {'productCodePost': productCode,
                							'qty': quantityValue,
                							'unit': uom
                							};
                var recommendationAction = $(this);
                sessionStorage.setItem('ShowWelcome','FALSE');

                $(recommendationAction).find('#recommendationText').html($('#addedText').html());
                if($(recommendationAction).hasClass('adding')){
                  return;
                }
                $(recommendationAction).addClass('adding');

                $.ajax({
                    url:'/sabmStore/en/recommendation/add',
                    type:'POST',
                    dataType: 'json',
                    data: JSON.stringify(dataPost),
                    contentType: 'application/json',
                    success: function(result) {
                    	rm.recommendation.displayAddToRecommendationPopup(result);
                       console.log('recommendations:' + result.recommendationsCount);
                        $(recommendationAction).find('#recommendationStar').removeClass('icon-star-normal').addClass('icon-star-add');

                        setTimeout(function ()
                        {
                            $(recommendationAction).find('#recommendationStar').removeClass('icon-star-add').addClass('icon-star-normal');
                            $(recommendationAction).find('#recommendationText').html($('#addText').html());
                            $(quantityField).find('.qty-input')[0].value = 1;
                            $('[name=qty]', addToCartForm)[0].value = 1;
                            $(recommendationAction).removeClass('adding');
                        }, 5000);


                    },
                    error:function(result) {
                       console.error('results:' + result);
                    }
                });

            });

            // Add to cart (Customer page)
      $('.recommendation-addToOrder').on('click',function(e) {
				e.preventDefault();

				var that = $(this);
				var closestTableRow = $(this).closest('.product-row');
				var recommendationType = $(closestTableRow).find('.recommendationType').val();

				var recommendation = {};

				if (recommendationType === 'PRODUCT') {
					var productCode = $(closestTableRow).find('[name=productCodePost]').val();
					var qty = $(closestTableRow).find('.qty-input').val();
					var unit = $(closestTableRow).find('.select-btn').attr('data-value');

					recommendation.productCodePost = productCode;
					recommendation.qty = qty;
					recommendation.unit = unit;
				}
				else { // if DEAL

					var dealCode = $(closestTableRow).find('[name=dealCodePost]').val();
					var dealProducts = [],
				    	dealProduct = {};

					$(closestTableRow).find('.deal-product-row').each(function () {
						if ($(this).css('display') !== 'none') {
							var dealProductCode = $(this).find('[name=productCodePost]').val();
							var dealProductQty = $(this).find('.qty-input').val();
							//var dealProductUnit = $(this).find('.select-btn').attr('data-value');

							dealProduct.productCodePost = dealProductCode;
							dealProduct.qty = dealProductQty;
							//dealProduct.unit = dealProductUnit;

							dealProducts.push(dealProduct);
						}
					});

					recommendation.dealCode = dealCode;
					recommendation.baseProducts = dealProducts;
				}

				var dataPost = recommendation;

				rm.recommendation.addRecommendationToCart(recommendationType, dataPost).success(function(result) {
                    console.log('success');

                    if(result.addToCartForErrorLayer){
    					$('#globalMessages').empty();
    					$('#globalMessages').append(result.addToCartForErrorLayer);
    				}else{
    					$('#globalMessages').empty();
    				}
    				if (result) {
    					$(closestTableRow).hide();
    					//$(window)[0].location.reload();
                        ACC.product.displayAddToCartPopup(result);
    					rm.recommendation.updateRecommendation(that, 'ACCEPTED');
    					ACC.minicart.refreshMiniCartCount();
    					//ACC.common.refreshScreenReaderBuffer();
    				}

				}).error(function(result) {
                    console.error('error:' + JSON.stringify(result));
				});

            });

            // When clicking "Yes" (Cart page)
            $('.recommendation-cart-actions .recommendation-addToOrder').on('click',function(e) {
				e.preventDefault();

				var that = $(this);
				var closestTableRow = $(this).closest('.cartRecommendations');
				var recommendationType = $(closestTableRow).find('.recommendationType').val();

				var recommendation = {};

				if (recommendationType === 'PRODUCT') {
					var productCode = $(closestTableRow).find('[name=productCodePost]').val();
					var qty = $(closestTableRow).find('[name=qty]').val();
					var unit = $(closestTableRow).find('[name=unit]').val();

					recommendation.productCodePost = productCode;
					recommendation.qty = qty;
					recommendation.unit = unit;
				}
				//else { // if DEAL - the add to cart function defined in dealsCtrl.js is invoked
				//}

				var dataPost = recommendation;

				rm.recommendation.addRecommendationToCart(recommendationType, dataPost).success(function(result) {
					console.log('success');

                    if(result.addToCartForErrorLayer){
    					$('#globalMessages').empty();
    					$('#globalMessages').append(result.addToCartForErrorLayer);
    				}else{
    					$('#globalMessages').empty();
    				}
    				if (result) {
    					$(closestTableRow).hide();
    					ACC.product.displayAddToCartPopup(result);
    					rm.recommendation.updateRecommendation(that, 'ACCEPTED', result);
    					//ACC.minicart.refreshMiniCartCount();
    					//ACC.common.refreshScreenReaderBuffer();

    					if ($('.cartRow').length > 0) {
							rm.recommendation.resetCartForRecommendation(dataPost.productCodePost, 'product');
						} else {
							rm.cart.refreshPage();
						}
    				}

				}).error(function(result) {
					console.error('error:' + JSON.stringify(result));
				});

            });
		},

		displayAddToRecommendationPopup: function (result)
		{

			$('.recommendationCounter').show();
      // $('.recommendation .recommendationsCount').html(result.recommendationsCount);
      // $('.navbar-toggle .recommendationsCount').html(result.recommendationsCount);
      rm.recommendation.displayRecommendationCount(result.recommendationsCount);

			var timeoutId;

			$('#addToRecommendationLayer').remove();


			if ($('#header').is(':visible')) {
				$('#header .recommendation').append(result.addToRecommendationLayer);
			}

			else if ($('#nav').is(':visible')) {
				$('#nav').append(result.addToRecommendationLayer);
			}

			$('#addToRecommendationLayer').show(function(){

				if (typeof timeoutId !== 'undefined')
				{
					clearTimeout(timeoutId);
				}
				timeoutId = setTimeout(function ()
				{
					$('.itemsAddedToRecommendation').hide();
				}, 3000);

			});
		},
		resetCartForRecommendation: function(data,type){

        	$('body').addClass('loading');
        	$.ajax({
        		url:'/cart',
        		type:'GET',
        		success: function(result) {

        			$.magnificPopup.close();
                    $('.cart').html($(result).find('[class=cart]').html());
                    $('#orderTotals').html($(result).find('[id=orderTotals]').html());
                    $('#simulationErrors').html($(result).find('#simulationErrors').html());
                    $('#globalMessage').html($(result).find('[id=globalMessage]').html());

        			rm.cart.quickInit();
        			ACC.minicart.refreshMiniCartCount();
        			$('body').removeClass('loading');
        			rm.recommendation.highlightNewlyAddedToCart(data, type);
        		},
        		error:function() {
        			console.log('error resetting cart');
        			$('body').removeClass('loading');
        		}

        	});
        },

		highlightNewlyAddedToCart: function(data, type){
		    if ($('.cartRow').length > 0) {
		        $('.cartRow').each(function(){
		            var productID = $(this).find('.js-track-product-link').attr('data-id');
		            var productURL = $(this).find('.js-track-product-link').attr('data-url');

		            if(type === 'deal'){
		                for(var range in data.ranges){ // Loop over ranges in deal
                        	for(var prod in data.ranges[range].baseProducts){ // Loop of products in range
                        			var productCodePost = data.ranges[range].baseProducts[prod].productCode;
                        			if(productCodePost === productID){
                        			    $(this).effect('highlight', {color: '#f0f4f9'}, 5000);
                        			    break;
                        			}
                        	}
                        }
		            } else {
		                if(data === productID || productURL.endsWith(data)){
                            $(this).effect('highlight', {color: '#f0f4f9'}, 5000);

                        }
		            }

		        });
		    }
		},

		displayRecommendationCount: function(count) {
		  if (count > 0) {
              $('.recommendation .recommendationsCount').html(count);
              $('.navbar-toggle .recommendationsCount').html(count);
              $('.recommendationsCount').removeClass('hidden');
              $('.navbar-toggle .recommendationsCount').removeClass('hidden');
          } else {
              $('.recommendationsCount').addClass('hidden');
              $('.navbar-toggle .recommendationsCount').addClass('hidden');
          }
		},

		// function to call when clicking the "Delete" link (Sales Rep page)
		deleteRecommendation: function(obj) {
			$('body').addClass('loading');
			//var recommendationId = obj.closest('.table-row').find('.recommendationId').val();
			var recommendationId = obj.closest('.product-row').find('.recommendationId').val();

			//obj.closest('.table-row').hide();
			obj.closest('.product-row').hide();
			var recommendationIdsToDelete = $('#recommendationIdsToDelete').val();
			if(recommendationIdsToDelete === '' || recommendationIdsToDelete === null){
				$('#recommendationIdsToDelete').val(recommendationId);
			}else{
				$('#recommendationIdsToDelete').val(recommendationIdsToDelete + ',' + recommendationId);
			}

			$('body').removeClass('loading');
		},

		// function to call when clicking the "Delete" link (Customer page) or when clicking "No" (Cart page)
		updateRecommendation: function(obj, status) {
			$('body').addClass('loading');

			var closestTableRow = $(obj).closest('.product-row, .cartRecommendations'),
			    recommendationId = $(closestTableRow).find('.recommendationId').val(),
			    recommendationType = $(closestTableRow).find('.recommendationType').val(),
			    recommendations = [],
				recommendation = {};

			recommendation.recommendationId = recommendationId;
			recommendation.recommendationType = recommendationType;
			recommendation.status = status;
			recommendations.push(recommendation);

			var dataPost = {'recommendations': recommendations,
							'recommendationsToDelete': ''
							};

			$.ajax({
				url: '/sabmStore/en/recommendation/update',
				type: 'POST',
				dataType: 'json',
				data: JSON.stringify(dataPost),
	            contentType: 'application/json',
				success: function(result) {
					console.log('success: ' + JSON.stringify(result));
					rm.recommendation.displayRecommendationCount(result);
					if(result === '0'){
					    $('#recommendationCarousel').addClass('hidden');
					}

				},
				error: function(result) {
					console.log('error: ' + JSON.stringify(result));
				}
			});

			$('body').removeClass('loading');
		},

		// function to call when clicking the "UPDATE RECOMMENDATIONS" button (Sales Rep page)
		updateQuantityOrUnit: function() {
			$('body').addClass('loading');

			var recommendationIdsToDelete = $('#recommendationIdsToDelete').val();
			var dataPost = {'recommendations': [],
							'recommendationsToDelete': recommendationIdsToDelete
							};

			$('.product-row').each(function () {
				if($(this).css('display') !== 'none'){
					var recommendationId = $(this).find('.recommendationId').val();
					var recommendationType = $(this).find('.recommendationType').val();

					var recommendation = {};
					recommendation.recommendationId = recommendationId;
					recommendation.recommendationType = recommendationType;

					if (recommendationType === 'PRODUCT') {
						var qty = $(this).find('.qty-input').val();
						var unit = $(this).find('.select-btn').attr('data-value');

						recommendation.quantity = qty;
						recommendation.unit = unit;
					}
					else { // if DEAL
						var dealProducts = [];

						$(this).find('.deal-product-row').each(function () {
							if ($(this).css('display') !== 'none') {
								var dealProductCode = $(this).find('[name=productCodePost]').val();
								var dealProductQty = $(this).find('.qty-input').val();
								//var dealProductUnit = $(this).find('.select-btn').attr('data-value');

								var dealProduct = {};
								dealProduct.productCodePost = dealProductCode;
								dealProduct.qty = dealProductQty;
								//dealProduct.unit = dealProductUnit;

								dealProducts.push(dealProduct);
							}
						});

						recommendation.baseProducts = dealProducts;
					}

					dataPost.recommendations.push(recommendation);

				}
			});

			//$('#globalMessages .successSavingRecommendations').hide();
			//$('#globalMessages .errorSavingRecommendations').hide();
			$('#successSavingRecommendations .successSavingRecommendations').hide();
			$('#errorSavingRecommendations .errorSavingRecommendations').hide();

			$.ajax({
				url:'/sabmStore/en/recommendation/updateRecommendations',
				type:'POST',
				dataType: 'json',
				data: JSON.stringify(dataPost),
	            contentType: 'application/json',
				success: function(result) {
					console.log('success: ' + JSON.stringify(result));
					rm.recommendation.displayRecommendationCount(result);

					if(result) {
						//$('#globalMessages').html($('#successSavingRecommendations').html());
						//$('#globalMessages .successSavingRecommendations').show();

						$('#successSavingRecommendations .successSavingRecommendations').show();

						setTimeout(function() { // hide message after 5 seconds
							$('#successSavingRecommendations .successSavingRecommendations').hide();
						}, 5000);
					} else {
						//$('#globalMessages').html($('#errorSavingRecommendations').html());
						//$('#globalMessages .errorSavingRecommendations').show();
						$('#errorSavingRecommendations .errorSavingRecommendations').show();
					}

					$('body').removeClass('loading');
					rm.recommendation.disableUpdateRecommendations();
				},
				error:function(result) {
					console.log('error: ' + result);
					$('#errorSavingRecommendations').show();
					$('body').removeClass('loading');
				}
			});

			$('#recommendationIdsToDelete').val('');
		},

		addRecommendationToCart: function(recommendationType, dataToPost) {
			return $.ajax({
	            url: (recommendationType === 'PRODUCT') ? '/sabmStore/en/cart/addAjax' : '/sabmStore/en/cart/add/deal',
	            type: 'POST',
	            dataType: 'json',
				data: JSON.stringify(dataToPost),
	            contentType: 'application/json'
	        });
		},

		enableUpdateRecommendations: function() {
			$(window).on('beforeunload', function() {
				return 'You have unsaved changes!';
	        });

			$('#update-recommendation-button').removeClass('disabled');
			//$('.template-actions').addClass('disabled');
			//$('.template-actions .hrefAddtoCart').addClass('notActive');
		},

		disableUpdateRecommendations: function() {
			$(window).off('beforeunload');

			$('#update-recommendation-button').addClass('disabled');
			//$('.template-actions').removeClass('disabled');
			//$('.template-actions .hrefAddtoCart').removeClass('notActive');
		},

		unsavedChangesPopup: function() {
			$.magnificPopup.open({
				items:{
			       src: '#unsavedChangesPopup',
			       type: 'inline'
				},
			   //removalDelay: 500,
			   mainClass: 'mfp-slide',
			   modal: true
			});
		},

		proceedToTarget: function() {
			var target = $('#unsavedChangesPopup').attr('data-target');
			window.location.href = target;
		},

		handleNoRecommendationMessage: function() {
		    if ($('.no-rec-message').length > 0) {
		        if ($('.recommendation-component').length > 0) {
		            console.log('No recommendations but with smart recommendations');
		        } else {
		            $('.no-rec-message').show();
		        }
		    }
		}
};

$('document').ready(function() {
	rm.recommendation.createListeners();
	rm.recommendation.handleNoRecommendationMessage();
});
//Sets equal height on load
$(window).bind('ready', function(){
	if ($('#slider-load').hasClass('slick-initialized')){
		//rm.utilities.setEqualHeight($('.cartRecommendations .product-pick-description h3' ));
		rm.utilities.setEqualHeight($('.cartRecommendations .product-pick .card-content .col-md-9' ));
		rm.utilities.setEqualHeight($('.cartRecommendations .product-pick .product-pick-title' ));
}});
