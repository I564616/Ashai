/* globals window */
/* globals ACC */
/* globals angular */


'use strict';


rm.cart = {

	tenMinutesInterval: null,

	init:function() {
		this.recalculate();
        this.sabmCheckOut();
        this.clearCart();
        this.saveOrderTemplate();
        this.deliveryListener();
        this.changeDelivery();
        this.chooseUpdateFunction();
        this.quickInit();
	},

	quickInit: function(){
		this.bingCartEntry();
		this.removeItemBind();
		this.chooseFreeProduct();
		this.seeDeal();
		rm.modals.init();
		rm.cart.updatable=true;

		ACC.product.addListeners();
	},

	deliveryListener: function(){
		$('#deliveryInstructions').on('click',function(){
			$('.delivery-instructions').slideDown(300);
		});
	},
	//show "See deal" on mobile
	seeDeal: function(){
		$('.see-deal-link').on('click',function(){
			var link = $(this),
				content = $(this).closest('.row').find('div#js-see-deal-title');

			link.toggleClass('open');
			if(link.hasClass('open')){
				content.show();
			} else {
				content.hide();
			}
		});
	},
	removeItemBind: function(){

		$('.submitRemoveProduct').on('click', function (event){ // On Delete
			event.preventDefault();

			$('#checkoutNotAllowed .checkoutNotAllowed').hide();
			$('.checkoutButton').removeClass('disabled');

			var prodid = $(this).data('index'),
				form = $('#updateCartForm' + prodid),
				cartQuantity = form.find('input[name=quantity]'),
				entryNumber = form.find('input[name=entryNumber]').val(),
				unit = form.find('input[name=unit]').val(),
				elementWithProductData = $(this).closest('.cartRow').find('.js-track-product-link');

			if (typeof rm.tagManager.ProductForTrackCart !== 'undefined') {
				var productObj = new rm.tagManager.ProductForTrackCart(
						$(this).data('currencycode'),
						$(this).data('name'),
						$(this).data('id'),
						$(this).data('price'),
						$(this).data('brand'),
						$(this).data('category'),
						$(this).data('uomlist')[unit],
						$(this).data('position'),
						$(this).data('dealsflag'),
						cartQuantity.val(),
						$(this).data('actionfield'));

				if (typeof rm.tagManager.trackCart !== 'undefined') {
					rm.tagManager.trackCart(productObj, 'remove');
				}
			}

			cartQuantity.attr('value',0);

			$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
				$('body').removeClass('loading');
				if(result.isLost){
						//$('#loseDealPopup h3').text(result.title);

						var titles = result.title;
						var resultTitleHtml = '';
                   	 	var productsInLoseDealModal = [];
						if(titles){
							 for (var i = 0; i < titles.length; i++) {
								 resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

                            	 productsInLoseDealModal.push({
         			        		'name'		: elementWithProductData.data('name'),
         			            	'id'		: elementWithProductData.data('id'),
         			            	'price'		: elementWithProductData.data('price'),
         			            	'brand'		: elementWithProductData.data('brand'),
         			            	'category'	: elementWithProductData.data('category'),
         			            	'variant'	: elementWithProductData.data('variant'),
         			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
         			            	'position'	: (i+1),
         			            	'dealsFlag'	: true
         			        	});
	                        }
				        }
						$('#loseDealPopup h3').html(resultTitleHtml);

						rm.cart.loseDealPopup(form,'remove');

                        if (productsInLoseDealModal.length > 0) {
             				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
             					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
             				}
             	        }

						$('#loseDealPopup').attr('data-item', prodid);
						$('#loseDealPopup').attr('data-qty', cartQuantity.val());
				} else {
						if($('.cartRow').length > 1){
							rm.cart.resetCart();
						} else {
							rm.cart.refreshPage();
						}

						rm.cart.showRecalculate();
						$('#totals-section').addClass('hidden');
				}
			});
		});
	},

	chooseFreeProduct: function(){
		$('.chooseFreeProduct').on('click', function (e){ // On Delete
			e.preventDefault();

			angular.element('#cartCtrl').scope().chooseFreeProduct($(this).data('deal-code'));
		});
	},

	loseDealPopup: function(form,modal){

		if(modal === 'remove'){
			$.magnificPopup.open({
				items:{
			       src: '#loseDealPopup',
			       type: 'inline'
				},
			   removalDelay: 500,
			   mainClass: 'mfp-slide',
			   modal: true,
			   callbacks:{
				   open: function(){
					   rm.tagManager.addDealsImpressionAndPosition('Viewed', 'LoseDeal');
				   },
				   close: function(){
				   }
			   }
			});
		} else {
			$.magnificPopup.open({
				items:{
			       src: '#loseDealPopupReduce',
			       type: 'inline'
				},
			   removalDelay: 500,
			   mainClass: 'mfp-slide',
			   modal: true
			});
		}

	},

	resetCart: function(){

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
			},
			error:function() {
				console.log('error resetting cart');
				$('body').removeClass('loading');
			}

		});
	},

	checkLoseDeal: function(entry, qty,unit){
		var data = {
			entryNumber: entry,
			quantity: qty.val(),
			uom: unit
		};

		$('body').addClass('loading');

		return $.ajax({
			url:'/sabmStore/en/cart/isLostDeal',
			type:'POST',
			data:data,
			success: function (result) {
			    if (result.hasOwnProperty('newQty') && result.newQty !== null) {
			        var text = result.newQty.indexOf('maxOrderQuantityExceeded:') !== -1 ? result.newQty.split(':')[1] : '';
                    $('.order-error-message-' + entry).text(text);
			    }
            },
            error: function (error) {
                // close block ui
                $.unblockUI();
            }
		});
	},

	// show Re-calculate button
	showRecalculate: function() {
		if($('.checkoutButton').attr('data-sap-disable') !== 'true') {
			$('.checkoutButton').hide();
			$('.doCartBut').show();
		}
	},
	// show Checkout button
	showCheckout: function() {
		$('.checkoutButton').show();
		$('.doCartBut').hide();
	},
	getRecalculatedData: function(){
		if($('.checkoutButton').attr('data-sap-disable') !== 'true') {
			$('.breadcrumb').addClass('inactive');

			rm.utilities.loadingMessage($('.loading-message').data('simulate'),true);
			$('body').addClass('loading');
			rm.utilities.sapCall = true;

	    	$.ajax({
				url:'/cart/orderSimulation',
				type:'POST',
				success: function(result) {
					if ($(result).find('[class=cart-body]').length > 0) {
						$('#cartDealsData').html($(result).find('#cartDealsData').html()); // Replace cart angular data

						$('.deals-listing').html($(result).find('.deals-listing').html());
						$('.cart').html($(result).find('[class=cart]').html()); // Replace cart body
	                    $('#orderTotals').html($(result).find('[id=orderTotals]').html()); // Replace totals table
	                    $('#deal-notification-bad').html($(result).find('[id=deal-notification-bad]').html());
	                    $('#deal-notification-bad').attr('class', $(result).find('[id=deal-notification-bad]').attr('class'));
	                    $('#deal-notification-auto').html($(result).find('[id=deal-notification-auto]').html());
	                    $('#deal-notification-auto').attr('class', $(result).find('[id=deal-notification-auto]').attr('class'));

	                    $('#minFreight').html($(result).find('#minFreight').html()); // Replace cart angular data
						$('#simulationErrors').append($(result).find('[id=simulationErrors]').html());
	                    $('#globalMessage').html($(result).find('[id=globalMessage]').html());

						if(!$('#globalMessage .alert.server-error').not('.hidden').length && !$('#simulationErrors .alert.negative').length) {
							rm.cart.showCheckout();
						} else {
							rm.cart.showRecalculate();
							rm.utilities.goBackTop();
						}
						//angular.element('#cartCtrl').scope().init(false); // Rerun angular cart controller
						rm.cart.quickInit();
						//ACC.minicart.refreshMiniCartCount();
						$('.miniCart .count').html($('.miniCart .count', result).html());
						$('.cart-mobile .count').html($('.cart-mobile .count', result).html());

						$('.breadcrumb').removeClass('inactive');

						$('.doCartBut').text('Recalculate');
						$('#totals-section').removeClass('hidden');
						$('body').removeClass('loading');
					} else {
						rm.cart.refreshPage();
					}

                },
				error:function() {
					$('.breadcrumb').removeClass('inactive');
					$('body').removeClass('loading');
					$('#globalMessage .alert').removeClass('hidden');
					$('.doCartBut').text('Recalculate');
					rm.cart.showRecalculate();
					rm.utilities.goBackTop();
					rm.cart.removeItemBind();
				}

			});
		}


	},

	// Re-calculate button add click event
	recalculate : function() {
		$('.doCartBut').on('click touch', function() {
			//$('.doCartBut').text('Calculating..');
			//$('#simulationErrors').html('');
			//rm.cart.getRecalculatedData();
			rm.cart.refreshPage();
		});
	},

	// clear cart add click event
	clearCart: function() {
			$('.clear-cart-popup .btn-primary').on('click', function() { // This is firing twice in some cases
					var $productElements = $('span.inline.submitRemoveProduct:not(.visible-xs-block):not(.visible-sm-block)'),
						productElement,
						productObj,
						prodid,
						form,
						cartQuantity,
						unit;

					for (var i=0; i<$productElements.length; i++) {
						productElement = $productElements[i];

						prodid = $(productElement).data('index');
						form = $('#updateCartForm' + prodid);
						cartQuantity = form.find('input[name=quantity]');
						unit = form.find('input[name=unit]').val();

						if (typeof rm.tagManager.ProductForTrackCart !== 'undefined') {
							productObj = new rm.tagManager.ProductForTrackCart(
									$(productElement).data('currencycode'),
									$(productElement).data('name'),
									$(productElement).data('id'),
									$(productElement).data('price'),
									$(productElement).data('brand'),
									$(productElement).data('category'),
									$(productElement).data('position'),
									$(productElement).data('dealsflag'),
									cartQuantity.val(),
									$(productElement).data('actionfield'));

							if (typeof rm.tagManager.trackCart !== 'undefined') {
								rm.tagManager.trackCart(productObj, 'remove');
							}
						}

					}

					window.location.href = $('#cartClearUrl').val();
			});
	},
    /**
     * levelNumber: number of parents level up
     */
    hidePrice: function($obj) {

    	$obj.closest('.cartRow').find('.total').html('&mdash;');
    	$obj.closest('.cartRow').find('.text-normal').hide();
    	// hide Price summary table
    	$('#totals-section').addClass('hidden');
    },

	bingCartEntry: function(){
		rm.utilities.needClamp('cartItemClamp-2',2,'clamp-2');
		rm.utilities.needClamp('cartItemClamp-1',1,'clamp-1');
		// Quantity Incrementors
		$('.cartRow .select-quantity .up').on('click touchstart',function(){
			if(rm.cart.updatable){
				var $input = $(this).closest('.select-quantity').find('.qty-input');

				if($input.val() < 999){
					var entryLoopIndex = $(this).closest('.cartRow').find('.entry-loop-index').val();
					var $newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
					$newQuantity.val(parseInt($input.val()) + 1);

					rm.cart.updateQuantityOrUnit($(this), false);
					rm.cart.showRecalculate();
				}
			}

			//hidePrice
			rm.cart.hidePrice($(this));

		});

		$('.cartRow .select-quantity .down').on('click touchstart',function(){
			if(rm.cart.updatable && !$(this).hasClass('disabled')){
				var that = $(this),
					prodid = that.closest('.cartRow').data('index'),
					$input = that.closest('.select-quantity').find('.qty-input'),
					form = $('#updateCartForm' + prodid),
					cartQuantity = form.find('input[name=quantity]'),
					// fromIndex = form.find('input[name=fromIndex]').val(),
					entryNumber = form.find('input[name=entryNumber]').val(),
					unit = form.find('input[name=unit]').val(),
					elementWithProductData = $(that).closest('.cartRow').find('.js-track-product-link');

				cartQuantity.val(parseInt($input.val())-1);
	            rm.cart.hidePrice($(this));

				$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
					$('body').removeClass('loading');
					if(result.isLost){
                            //$('#loseDealPopupReduce h3').text(result.title);
                             var titles = result.title;
                             var resultTitleHtml = '';
                        	 var productsInLoseDealModal = [];
                             if(titles){
                                 for (var i = 0; i < titles.length; i++) {
                                     resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

                                	 productsInLoseDealModal.push({
             			        		'name'		: elementWithProductData.data('name'),
             			            	'id'		: elementWithProductData.data('id'),
             			            	'price'		: elementWithProductData.data('price'),
             			            	'brand'		: elementWithProductData.data('brand'),
             			            	'category'	: elementWithProductData.data('category'),
             			            	'variant'	: elementWithProductData.data('variant'),
             			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
             			            	'position'	: (i+1),
             			            	'dealsFlag'	: true
             			        	});
                                 }
                            }
                            $('body').removeClass('loading');

                            $('#loseDealPopupReduce h3').html(resultTitleHtml);
                            $input.val(parseInt($input.val()));
                            rm.cart.loseDealPopup(form,'reduce');

                            if (productsInLoseDealModal.length > 0) {
                 				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
                 					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
                 				}
                 	        }

                            //$('#loseDealPopupReduce').attr('data-item', entryNumber);
                            $('#loseDealPopupReduce').attr('data-item', prodid);
                            $('#loseDealPopupReduce').attr('data-qty', $input.val());
                    } else {
                    		$('body').removeClass('loading');
                    		console.log(form.find('.base-quantity'));
                    		rm.cart.changeBaseQuantity(result.newQty,that);
                            form.find('#initialQuantity' + prodid).val(parseInt($input.val()));
                    		// rm.cart.resetCart();
                            rm.cart.showRecalculate();

                            var entryLoopIndex = that.closest('.cartRow').find('.entry-loop-index').val();
                            $('#maxorderqty'+entryLoopIndex).html('');
                    }

				});
			}
		});

		// Quantity input. If the user input 0,set the quantity to 1.
		$('.cartRow .select-quantity input').each(function(){
			if(rm.cart.updatable){
				var that = $(this);
				var typingTimer;                //timer identifier
				var doneTypingInterval = 1500;  //time in ms, 5 second for example

				//on keyup, start the countdown
				$(this).on('keyup', function () {
				  clearTimeout(typingTimer);

				  typingTimer = setTimeout(doneTyping, doneTypingInterval);

				});

				//on keydown, clear the countdown
				$(this).on('keydown', function (event) {
					if(event.keyCode === 13){
						return false;
					}
					clearTimeout(typingTimer);
				});

				//user is "finished typing," do something
				var doneTyping = function () {
				  var prodid = that.closest('.cartRow').data('index'),
				  	form = $('#updateCartForm' + prodid),
				  	cartQuantity = form.find('input[name=quantity]'),
				  	tempQuantity = parseInt(that.val(),10),
				  	//fromIndex = form.find('input[name=fromIndex]').val(),
				  	entryNumber = form.find('input[name=entryNumber]').val(),
				  	unit = form.find('input[name=unit]').val(),
				  	notIsLost = false,
					elementWithProductData = $(that).closest('.cartRow').find('.js-track-product-link');

				  	rm.cart.hidePrice(that);

	  				if(isNaN(tempQuantity)){
	  					notIsLost = false;
	  				  	tempQuantity = cartQuantity.val();
	  				  	that.val(tempQuantity);
	  				}else if(tempQuantity <= 0){
	  					notIsLost = true;
	  					that.val(parseInt(1));
	  				}else{
	  					that.val(tempQuantity);
	  				}

	  				cartQuantity.val(tempQuantity);

	  				if(!notIsLost){
	  					$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
	  						$('body').removeClass('loading');
	  						if(result.isLost){
	  								//$('#loseDealPopupReduce h3').text(result.title);
	  								var titles = result.title;
	  								 var resultTitleHtml = '';
	  								var productsInLoseDealModal = [];
	  								 if(titles){
	  									 for (var i = 0; i < titles.length; i++) {
	  										 resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

	  										 productsInLoseDealModal.push({
	  	             			        		'name'		: elementWithProductData.data('name'),
	  	             			            	'id'		: elementWithProductData.data('id'),
	  	             			            	'price'		: elementWithProductData.data('price'),
	  	             			            	'brand'		: elementWithProductData.data('brand'),
	  	             			            	'category'	: elementWithProductData.data('category'),
	  	             			            	'variant'	: elementWithProductData.data('variant'),
	  	             			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
	  	             			            	'position'	: (i+1),
	  	             			            	'dealsFlag'	: true
	  	             			        	 });
	                                      }
	  						        }
	  								$('#loseDealPopupReduce h3').html(resultTitleHtml);

	  								rm.cart.loseDealPopup(form,'reduce');

	  	                            if (productsInLoseDealModal.length > 0) {
	  	                 				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
	  	                 					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
	  	                 				}
	  	                 	        }

	  								$('#loseDealPopupReduce').attr('data-item', prodid);
	  								$('#loseDealPopupReduce').attr('data-qty', that.val());
	  						} else {
	  							var entryLoopIndex,
	  								$newQuantity;

	  							var tempCartEntryQty = cartQuantity.val();
	  						    that.val(tempCartEntryQty);

	  							entryLoopIndex = that.closest('.cartRow').find('.entry-loop-index').val();
	  							$newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
	  							$newQuantity.val(that.val());
	  							form.find('.base-quantity span').text(result.newQty);
	  							rm.customUI.checkChangeable(that);
	  							rm.cart.showRecalculate();
	  							if(parseInt(tempCartEntryQty) > parseInt(result.newQty))
	  							{
	  								console.log("Inside If parseInt(tempCartEntryQty) > parseInt(result.newQty) ");
	  								$('#maxorderqty'+entryLoopIndex).html('<span class="error">The maximum quantity available of this product to order is ' + result.newQty+ '</span>');
	  						     }else{
	  						    	console.log("Inside else parseInt(tempCartEntryQty) > parseInt(result.newQty) ");
	  						            $('#maxorderqty'+entryLoopIndex).html('');
	  						     }

	  						}
	  					});
	  				}
				};
			}
		});

		// Change value of hidden field to selection
		$('.cartRow .select-items li').on('click', function(){
			if(rm.cart.updatable){
				var that = $(this),
					prodid = that.closest('.cartRow').data('index'),
					input = that.closest('.cartRow').find('.qty-input'),
					form = $('#updateCartForm' + prodid),
					cartQuantity = form.find('input[name=quantity]'),
					entryNumber = form.find('input[name=entryNumber]').val(),
					entryLoopIndex = that.closest('.cartRow').find('.entry-loop-index').val(),
					unit = that.data('value'),
					selectBtn = that.closest('.select-list').find('.select-btn'),
					$updateEntryUnit = that.closest('.cartRow').find('#updateEntryUnit'+entryLoopIndex),
					elementWithProductData = $(that).closest('.cartRow').find('.js-track-product-link');

					selectBtn.text($(this).text());

					$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
						$updateEntryUnit.val(that.attr('data-value'));

						$('body').removeClass('loading');

						$('.select-items').hide();

						if(result.isLost){
							// $('#loseDealPopupReduce h3').text(result.title);

							var titles = result.title;
							var resultTitleHtml = '';
							var productsInLoseDealModal = [];
							if(titles){
								 for (var i = 0; i < titles.length; i++) {
									 resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

									 productsInLoseDealModal.push({
             			        		'name'		: elementWithProductData.data('name'),
             			            	'id'		: elementWithProductData.data('id'),
             			            	'price'		: elementWithProductData.data('price'),
             			            	'brand'		: elementWithProductData.data('brand'),
             			            	'category'	: elementWithProductData.data('category'),
             			            	'variant'	: elementWithProductData.data('variant'),
             			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
             			            	'position'	: (i+1),
             			            	'dealsFlag'	: true
             			        	 });
		                        }
					        }
							$('#loseDealPopupReduce h3').html(resultTitleHtml);
							rm.cart.loseDealPopup(form,'reduce');

                            if (productsInLoseDealModal.length > 0) {
                 				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
                 					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
                 				}
                 	        }

							$('#loseDealPopupReduce').attr('data-item', prodid);
							$('#loseDealPopupReduce').attr('data-qty', input.val());
						} else {
							form.find('.base-quantity span').text(result.newQty);
							// rm.cart.resetCart();
							rm.cart.hidePrice($(that));
							rm.cart.showRecalculate();
						}
					});
			}
		});
	},

	chooseUpdateFunction: function(){
		$('.lose-deal-popup .btn-primary').one('click',function(){
			var modal = $(this).closest('.lose-deal-popup'),
				item = modal.attr('data-item'),
				form = $('#updateCartForm' + item),
				entryLoopIndex = form.closest('.cartRow').find('.entry-loop-index').val(),
				initialQuantity = $('#initialQuantity'+entryLoopIndex).val(),
				cartQuantity = form.find('input[name=quantity]').val(),
				$elementWithProductData = form.closest('.cartRow').find('.js-track-product-link');

			    console.log('binding click');
				rm.cart.updateQuantityOrUnit(form, true);

				if (typeof rm.tagManager.ProductForTrackCart !== 'undefined') {
					var productObj = new rm.tagManager.ProductForTrackCart(
							$elementWithProductData.data('currencycode'),
							$elementWithProductData.data('name'),
							$elementWithProductData.data('id'),
							$elementWithProductData.data('price'),
							$elementWithProductData.data('brand'),
							$elementWithProductData.data('category'),
							$elementWithProductData.data('variant'),
							$elementWithProductData.data('position'),
							true,
							(initialQuantity !== cartQuantity ? parseInt(initialQuantity) - parseInt(cartQuantity) : initialQuantity),
							$elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal');

					if (typeof rm.tagManager.trackCart !== 'undefined') {
						rm.tagManager.trackCart(productObj, 'remove');
					}
				}
		});

	},

	refreshPage: function(){
        setTimeout(function (){
            $(window)[0].location.reload();
        }, 500);
	},

	//update the quantity or Unit by ajax
	updateQuantityOrUnit: function(obj, refresh){
		var entryLoopIndex = obj.closest('.cartRow').find('.entry-loop-index').val();
		var $input = obj.closest('.select-quantity').find('.qty-input');
		var $form = $('#updateCartForm'+entryLoopIndex);
		var $selectBtn = obj.parent().siblings('.select-btn');

		var $initialQuantity = $('#initialQuantity'+entryLoopIndex);
		var $initialUnit = $('#initialUnit'+entryLoopIndex);
		var $newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
		var $newUnit = $('#updateEntryUnit'+entryLoopIndex);
		var $displayQuantity = $('#displayQuantity'+entryLoopIndex);
		//if the quantity is not change and the unit is not change will not do the ajax

		console.log($initialQuantity.val() + ' ' + $newQuantity.val());
		if($initialQuantity.val() !== $newQuantity.val() || $initialUnit.val() !== $newUnit.val()){

			$('body').addClass('loading');
			rm.cart.hidePrice(obj);
			rm.cart.updatable = false;
			$input.attr('disabled', true);
			$.ajax({
				url:$('#cartUpdateQuantityUrl').val(),
				type:'POST',
				data:$form.serialize(),
				success: function(result) {
					if(result !== null){
					    // render max quantity order error message
                        var text = result.indexOf('maxOrderQuantityExceeded:') !== -1 ? result.split(':')[1] : '';
                        $('.order-error-message-' + entryLoopIndex).text(text);

						$initialQuantity.val($newQuantity.val());
						$initialUnit.val($newUnit.val());
						$input.removeAttr('disabled');
						$displayQuantity.val($newQuantity.val());
						$selectBtn.text(obj.text());
						$selectBtn.attr('data-value',obj.attr('data-value'));
						rm.cart.changeBaseQuantity(result,obj);
						rm.cart.updatable = true;

						var tempCartEntryQty = $newQuantity.val();
                        $initialQuantity.val(parseInt(result));
                        $displayQuantity.val(parseInt(tempCartEntryQty));
                        if(parseInt(tempCartEntryQty) > parseInt(result)){
                        	console.log("Inside If parseInt(tempCartEntryQty) > parseInt(result)");
                            $('#maxorderqty'+entryLoopIndex).html('<span class="error">The maximum quantity available of this product to order is ' + result + '</span>');
                        }

						rm.cart.showRecalculate();
						rm.cart.removeItemBind();
						ACC.minicart.refreshMiniCartCount();
						$.magnificPopup.close();
					}
				},
				complete:function(){
					if(refresh) {
						//rm.cart.getRecalculatedData();
						rm.cart.refreshPage();
					} else {
						$('body').removeClass('loading');
					}
				}
			});
		}
	},

	//if customer changed the quantity or the unit the baseQuantity will changed.
	changeBaseQuantity: function(quantity,obj){
		var productBaseUnit = obj.closest('.cartRow').find('.entry-product-unit').val();
		var productBasePluralUnit = obj.closest('.cartRow').find('.entry-product-plural-unit').val();
		var $section = obj.closest('.cartRow').find('.base-quantity');
		var baseUOM = obj.closest('.cartRow').find('.base-quantity').attr('data-base-unit');
		var currentUOM = obj.closest('.cartRow').find('.select-btn.sort').attr('data-value');
		//var input = obj.closest('.cartRow').find('.qty-input').val();

		if(baseUOM === currentUOM) {
			$section.hide();
		} else {
			$section.show();
		}

        var qtyHtml = '<span>' + quantity + '</span>' + '&nbsp;';
		if(parseInt(quantity) === 1){
			$section.html(qtyHtml + productBaseUnit);
		}else if(parseInt(quantity) > 1){
			console.log(parseInt(quantity));
			$section.html(qtyHtml + productBasePluralUnit);
		}
	},

	//if customer select the address will changed.
	changeDelivery: function(){
		// Customer carriers
		$('#customerArrangedDelivery .select-items li').on('click touchstart', function(){

			 $('body').addClass('loading');
			 var shippingCarrierCode = $(this).attr('data-value');
		     $.ajax({
					url:$('#updateSABMdeliveryUrl').val(),
					type:'POST',
					data:{delmodeCode:null,carrierCode:shippingCarrierCode},
					success: function() {
						rm.cart.showRecalculate();
					},
					complete: function() {
						// Sync/Show calendar after ajax finish to load
						rm.datepickers.syncCalendarPicker('carrier');
					},
					error:function(result) {
						console.error(result);
					}
				}).always(function() {
					$('body').removeClass('loading');
				});
		});
		// Delivery methods
		$('#deliveryMethod input[name="deliveryMethod"]').on('change', function(){
			$('body').addClass('loading');

			$('.datepicker').remove();

			var shippingOption = 'CUB Arranged';
			var shippingCarrierCode = $('#customerArrangedDelivery .select-list .select-btn').attr('data-value');
			if(shippingCarrierCode===''){
				shippingCarrierCode = $('#customerArrangedDelivery .select-items li').attr('data-value');
				shippingOption = 'Customer Arranged';
			}

			var step =  $('input[name="checkoutStep"]').val();
			if (typeof rm.tagManager.trackOnCheckoutOption !== 'undefined') {
				rm.tagManager.trackOnCheckoutOption(step, 'Shipping|' + shippingOption);
			}

			 var deliveryModeCode = this.value;
		     $.ajax({
					url:$('#updateSABMdeliveryUrl').val(),
					type:'POST',
					data:{addressId:null,delmodeCode:deliveryModeCode,carrierCode:shippingCarrierCode},
					success: function() {
						rm.cart.showRecalculate();
					},
					complete: function() {
						// Sync/Show calendar after ajax finish to load
						rm.datepickers.syncCalendarPicker('deliveryMode');
					},
					error:function(result) {
						console.error(result);
					}
				}).always(function() {
					$('body').removeClass('loading');
				});

		});
	},

	sabmCheckOut: function() {
	    $('document').ready(function(){
	        $('div', '#simulationErrors').each(function(){
	        	if (typeof rm.tagManager.trackCheckoutError !== 'undefined') {
	        		rm.tagManager.trackCheckoutError($(this).html());
	        	}
	        });
	        $('div', '#globalMessage').each(function(){
	        	if (typeof rm.tagManager.trackCheckoutError !== 'undefined') {
	        		rm.tagManager.trackCheckoutError($(this).html());
	        	}
            });

		    if($('#cartCtrl').length >0){
		        $('div.cart-deliverydate .form-control.cart-datepicker').datepicker()
		        .on('changeDate',function(){
		        	var date = $(this).datepicker('getDate');

		        	var formattedDate = $.datepicker.formatDate('DD dd/mm/yy', date);
		        	var step =  $('input[name="checkoutStep"]').val();
		        	if (typeof rm.tagManager.trackOnCheckoutOption !== 'undefined') {
		        		rm.tagManager.trackOnCheckoutOption(step, 'Delivery Date|' + formattedDate);
		        	}
		        });
		    }

		    if ($('#checkoutNotAllowed').length > 0) {
		    	if (!rm.cart.areAllProductPackTypesAllowed()) {
		    		$('#checkoutNotAllowed .checkoutNotAllowed').show();
		    		$('.checkoutButton').addClass('disabled');
		    	}
		    	else {
		    		$('#checkoutNotAllowed .checkoutNotAllowed').hide();
		    		$('.checkoutButton').removeClass('disabled');
		    	}
		    }
	    });

		$('.checkoutButton').on('click', function(){
			if (!rm.cart.areAllProductPackTypesAllowed()) {
				$('#checkoutNotAllowed .checkoutNotAllowed').show();
				$('.checkoutButton').addClass('disabled');
			}
			else {
				$('#checkoutNotAllowed .checkoutNotAllowed').hide();
				$('.checkoutButton').removeClass('disabled');
				rm.utilities.loadingMessage($('.loading-message').data('checkout'),true);
				$('.cartdDeliveryInstructions').attr('value', $('#deliveryInstructionsinfo').val());
				$('._sabmcheckoutForm').submit();
			}
		});
	},

	// check if pack types of all products in the cart are allowed for the selected delivery date
	areAllProductPackTypesAllowed: function() {
		if ($('div.cartRow .disabled-productPackTypeNotAllowed').length > 0) {
			return false;
		} else {
			return true;
		}
	},

	saveOrderTemplate: function() {

		$('.saveTemplateBtn').on('click touchstart', function(e){
			e.stopPropagation();
			e.preventDefault();

			if($('#template-name').val().trim() !== '') {

				var dataPost = {'name':$('#template-name').val()};
				$.ajax({
					url:'/cart/saveOrderTemplate',
					type:'POST',
                    contentType: 'application/json; charset=utf-8',
					data: JSON.stringify(dataPost),
					success: function(response) {
						$('#globalMessage').html($('#simulationErrors', response).html());
						$('#template-name').val('');
						$('.magnific-template-order').magnificPopup('close');

						rm.utilities.goBackTop();
					},
					error:function(result) {
						console.error(result);
					}
				});
			}else{
				$('#empty-msg').removeClass('hidden');
			}
		});
	},

    findOutMore:function () {
        $('#_sabmFindOutMoreForm').submit();
    }
};

