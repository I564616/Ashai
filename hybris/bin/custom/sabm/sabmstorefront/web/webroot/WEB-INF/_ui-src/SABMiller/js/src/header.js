/* globals window */
/* globals document */
/* globals ACC */

'use strict';

rm.header = {
	originalWidth: 0,
	init : function() {
		this.dropdowns();
		this.topNavMenu();
		this.dealsCounter();
		this.removeHeaderLink();
		this.minicart();
		this.chooseUpdateFunction();
		rm.header.minicart.updatable=true;
	},
	minicart : function() {
		var link = $('.miniCart .items');
		var timeoutId;
		if(!$('body').hasClass('view-only-mode')){

			$('.global-header-list .miniCart').on('mouseenter',function(){
				if(!link.hasClass('open')){
			        $.ajax({
			            url:'/sabmStore/en/cart/view',
			            type:'POST',
			            success: function(result) {
			                ACC.product.viewCartPopup(result);
			                link.addClass('open');
			                $('.viewCartPopup .row.list-qty').hide();
			                $('.viewCartPopup .minicart-delete-item .inline.submitRemoveProduct').hide();
			                $('.select-btn').show();

			               // var $selectBtn = $('.select-btn:not(.sort):not(.header):not(.js-expiry-date):not(.js-billingunit)');

			    			// Store value in data-value
			                /* $selectBtn.each(function(){
			    				var $firstItem = $(this).next('ul').children('li').first();
			    				$(this).text($firstItem.html());
			    			});
							*/
			    			rm.header.removeItemBind();
			    			rm.header.bingCartEntry();

			            },
			            error:function(result) {
			                console.log('error'+result.responseText);
			            }
			        });
			    } else {
			        $('#addToCartLayer .close').click();
			    }
			}).on('mouseleave',function(){
				$('#addToCartLayer').hide();
				link.removeClass('open');
				$('body').css('overflow','auto');
			});

		    if (typeof timeoutId !== 'undefined')
			{
				clearTimeout(timeoutId);
			}
			/* timeoutId = setTimeout(function ()
			{
				$('#addToCartLayer').hide();
			}, 5000); */
		    /*
			link.on('click',function(){
				var that = $(this);

			    if(!$(this).hasClass('open')){
			        $.ajax({
			            url:'/sabmStore/en/cart/view',
			            type:'POST',
			            success: function(result) {
			                ACC.product.viewCartPopup(result);
			                that.addClass('open');

			                $('.viewCartPopup .row.list-qty').hide();
			                $('.viewCartPopup .minicart-delete-item .inline.submitRemoveProduct').hide();
			                $('.select-btn').show();

			                var $selectBtn = $('.select-btn:not(.sort):not(.header):not(.js-expiry-date):not(.js-billingunit)');

			    			// Store value in data-value
			    			$selectBtn.each(function(){
			    				var $firstItem = $(this).next('ul').children('li').first();
			    				$(this).text($firstItem.html());
			    			});

			    			rm.header.removeItemBind();
			    			rm.header.bingCartEntry();
			            },
			            error:function(result) {
			                console.log('error'+result.responseText);
			            }
			        });
			    } else {
			        $('#addToCartLayer .close').click();
			    }
				if (typeof timeoutId !== 'undefined')
				{
					clearTimeout(timeoutId);
				}
				timeoutId = setTimeout(function ()
				{
					$('#addToCartLayer').hide();
					link.removeClass('open');
					$('html').removeClass('overflow-hidden');
				}, 5000);
			}); */

		}
	},

	topNavMenu: function(){
		$(document).ready(function() {

			var $selectBtn = $('.select-btn.header');
			$selectBtn.on('click',function(){
				if (typeof rm.tagManager.trackTopNavEvent !== 'undefined') {
					rm.tagManager.trackTopNavEvent('Account Info',window.location.href);
				}
			});

			$('.btn-changeDeliveryDate').on('click',function(){
				$('.delivery-header.delivery-header-desktop').mouseenter();
			});

			/* display header datepicker by default */
			/* show/hide header datepicker on mouseenter/mouseleave */
			$('.delivery-header.delivery-header-desktop').on('mouseenter',function(){
				/* checking if there's no datepicker opened */
				if($('.datepicker').length === 0){
					$('.delivery-header-input', $(this)).datepicker('show');
				}
			}).on('mouseleave', function(){
				if(!$('.delivery-header').hasClass('open')){
					$('.datepicker', $(this)).remove();
				}
			});

			$('.delivery-header.delivery-header-mobile').on('click',function(){
				$('.delivery-header-input', $(this)).datepicker('show');
			});

			$('.cart-datepicker').on('click',function(){
				$('.global-header-list .datepicker').remove();
				rm.header.showDatepickerDaysBorderRadius();
			});

			/* add hyperlink to the a tag when the data-disabled attribute is false */
			$('.sub-menu .sub-menu-item a').each(function(){
				if(typeof($(this).data('disabled')) !==  'undefined'){
					if($(this).data('disabled') === false){
						$(this).attr('href' , $(this).data('href'));
					}else{
						$(this).addClass('disabled');
					}
				}
			});

			var windowHeight = $(window).height(),
			allowedHeight = windowHeight - 300;

			var globalHeaderList = '.global-header-list';
			$(globalHeaderList +' .select-list .select-items').css('max-height',allowedHeight);

		});
	},

	dropdowns : function() {
		$(document).ready(function() {
			/*
			var $selectBtn = $('.select-btn.header');

			// Store value in data-value
			$selectBtn.each(function() {
				if(!$('#userSelectBusinessUnit').val()) {
					var $firstItem = $(this).next('ul').children('li').first();
					$(this).text($firstItem.attr('data-value'));
				}
			}); */

			$(document).on('click touchend', '.select-items.header li', function() {
				//var $selectBtn = $(this).parent().siblings('.select-btn');

				if(!($(this).hasClass('bde-view-only') && $(this).parents('.view-only-mode').length)){
					if ($(this).attr('data-value')) {
						//click the 'li' area to trigger the 'a' label
						$('a',this)[0].click();
						//$selectBtn.text($(this).attr('data-value'));
					} else {
						if (!$('.unsaved-changes').length) {
							window.location=$(this).attr('data-url');
						}
					}
				}
			});
			rm.header.originalWidth = $('div.select-list').innerWidth();
		});
	},

	dealsCounter : function(){
		var pop = document.getElementById('dealsAudio');
		$(document).ready(function(){
			var playAnimation = true;
			var curLoginURL = window.location.href;
			if(curLoginURL.indexOf('login')>=0 || curLoginURL.indexOf('paSearch')>=0){
				rm.utilities.addItemToStorage('PlayAnimation','TRUE');
				playAnimation = true;
			}

			if(rm.utilities.getItemFromStorage('PlayAnimation') !== null){
				if(rm.utilities.getItemFromStorage('PlayAnimation') === 'FALSE'){
					playAnimation = false;
				}
			}
			if(playAnimation && !$('.d-content .d-content-trans').hasClass('no_transform') && $(window).width() > 768 && $('.d-content').length){
				setTimeout(function(){
					$('.d-content .d-content-trans').addClass('ready');
					console.log('loading');
					setTimeout(function(){
						pop.play();
						rm.utilities.addItemToStorage('PlayAnimation','FALSE');
					},50);
				},1000);
			}
		});
	},


	removeHeaderLink: function() {
		$(document).ready(function() {
			if(window.location.href.indexOf('paSearch') > -1){
				var headlist = $('.global-header-list');

				headlist.addClass('text-right');
				headlist.find('ul').addClass('hidden');
				$('body').addClass('paSearch');
				$('.siteLogo').find('a').removeAttr('href');
				$('.js-only-signout').removeClass('hidden');
				$('#breadcrumb ol li:first').remove();
	        }

		});
	},

	removeItemBind: function(){

		$('.submitRemoveProduct').on('click', function (event){ // On Delete mini cart
			event.preventDefault();

			var prodid = $(this).data('index'),
				form = $('#updateMiniCartForm' + prodid),
				cartQuantity = form.find('input[name=quantity]'),
				entryNumber = form.find('input[name=entryNumber]').val(),
				unit = form.find('input[name=unit]').val(),
				elementWithProductData = $(this).closest('.popupCartItem').find('.js-track-product-link');
				cartQuantity.attr('value',0);

			$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
				$('body').removeClass('loading');
				if(result.isLost){
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
						if($('.popupCartItem').length > 1){
							//rm.cart.resetCart();
							rm.header.resetMiniCart();
						} else {
							rm.cart.refreshPage();
						}
				}
			});
		});
	},

		chooseUpdateFunction: function(){
		$('.lose-deal-minicart .btn-primary').one('click',function(){
			var modal = $(this).closest('.lose-deal-minicart'),
				item = modal.attr('data-item'),
				form = $('#updateMiniCartForm' + item),
				entryLoopIndex = form.closest('.popupCartItem').find('.entry-loop-index').val(),
				initialQuantity = $('#initialQuantity'+entryLoopIndex).val(),
				cartQuantity = form.find('input[name=quantity]').val(),
				$elementWithProductData = form.closest('.popupCartItem').find('.js-track-product-link');

				rm.header.updateQuantityOrUnit(form);

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

	bingCartEntry: function(){
		//rm.utilities.needClamp('cartItemClamp-2',2,'clamp-2');
		//rm.utilities.needClamp('cartItemClamp-1',1,'clamp-1');
		// Quantity Incrementors
		$('.popupCartItem .select-quantity .up').on('click touchstart',function(){

			if(rm.header.minicart.updatable){
				var $input = $(this).closest('.select-quantity').find('.qty-input');

				if($input.val() < 999){
					/*var entryLoopIndex = $(this).closest('.cartRow').find('.entry-loop-index').val();*/
					var entryLoopIndex = $(this).closest('.popupCartItem').find('.entry-loop-index').val();
					var $newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
					$newQuantity.val(parseInt($input.val()) + 1);
					rm.header.updateQuantityOrUnit($(this));
				}
			}

		});



		$('.popupCartItem .select-quantity .down').on('click touchstart',function(){
			if(rm.header.minicart.updatable && !$(this).hasClass('disabled')){
				var that = $(this),
					prodid = that.closest('.popupCartItem').data('index'),
					$input = that.closest('.select-quantity').find('.qty-input'),
					//form = $('#updateCartForm' + prodid),
					form = $('#updateMiniCartForm' + prodid),
					cartQuantity = form.find('input[name=quantity]'),
					entryNumber = form.find('input[name=entryNumber]').val(),
					unit = form.find('input[name=unit]').val(),
					elementWithProductData = $(that).closest('.popupCartItem').find('.js-track-product-link');

				cartQuantity.val(parseInt($input.val())-1);

				$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
					$('body').removeClass('loading');
					if(result.isLost){
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
                    		rm.header.changeBaseQuantity(result.newQty,that);
                    		rm.header.displayUpdatedQuantityAndUnit(cartQuantity.val(), that);
                            form.find('#initialQuantity' + prodid).val(parseInt($input.val()));
    						ACC.minicart.refreshMiniCartCount();
                    }

				});
			}
		});

		// Quantity input. If the user input 0,set the quantity to 1.
		$('.popupCartItem .select-quantity input').each(function(){
			if(rm.header.minicart.updatable){
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
				  //var prodid = that.closest('.cartRow').data('index'),
				  var prodid = that.closest('.popupCartItem').data('index'),
				  	//form = $('#updateCartForm' + prodid),
				  	form = $('#updateMiniCartForm' + prodid),
				  	cartQuantity = form.find('input[name=quantity]'),
				  	tempQuantity = parseInt(that.val(),10),
				  	entryNumber = form.find('input[name=entryNumber]').val(),
				  	unit = form.find('input[name=unit]').val(),
				  	notIsLost = false,
					elementWithProductData = $(that).closest('.popupCartItem').find('.js-track-product-link');

	  				if(isNaN(tempQuantity)){
	  					notIsLost = true;
	  					that.val(parseInt(1));
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

	  							entryLoopIndex = that.closest('.popupCartItem').find('.entry-loop-index').val();
	  							$newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
	  							$newQuantity.val(that.val());
	  							//form.find('.base-quantity span').text(result.newQty);
	                    		rm.header.changeBaseQuantity(result.newQty,that);
	                    		rm.header.displayUpdatedQuantityAndUnit($newQuantity.val(), that);
	  							rm.customUI.checkChangeable(that);
	  							ACC.minicart.refreshMiniCartCount();
	  						}
	  					});
	  				}
				};
			}
		});

		// Change value of hidden field to selection
		$('.popupCartItem .select-items li').on('click', function(){
			if(rm.header.minicart.updatable){
				var that = $(this),
					prodid = that.closest('.popupCartItem').data('index'),
					input = that.closest('.popupCartItem').find('.qty-input'),
					form = $('#updateMiniCartForm' + prodid),
					cartQuantity = form.find('input[name=quantity]'),
					entryNumber = form.find('input[name=entryNumber]').val(),
					entryLoopIndex = that.closest('.popupCartItem').find('.entry-loop-index').val(),
					unit = that.data('value'),
					selectBtn = that.closest('.select-list').find('.select-btn'),
					$updateEntryUnit = that.closest('.popupCartItem').find('#updateEntryUnit'+entryLoopIndex),
					elementWithProductData = $(that).closest('.popupCartItem').find('.js-track-product-link');

					selectBtn.text($(this).text());

					$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
						$updateEntryUnit.val(that.attr('data-value'));

						$('body').removeClass('loading');

						$('.select-items').hide();

						if(result.isLost){
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
							//form.find('.base-quantity span').text(result.newQty);
                    		rm.header.changeBaseQuantity(result.newQty,that);
                    		rm.header.displayUpdatedQuantityAndUnit(cartQuantity.val(), that);
							// rm.cart.resetCart();
							ACC.minicart.refreshMiniCartCount();
						}
					});
			}
		});

		$('.popupCartItem .minicart-update-item').each(function(){
			if(rm.header.minicart.updatable){
				$(this).on('click', function() {
					// restrict all other items from being editable
					$('.minicart-update-item').show();
					$('.viewCartPopup .row.list-qty').hide();
					$('.viewCartPopup .minicart-delete-item').children('.inline.submitRemoveProduct').hide();

					// only one item (the one being clicked on) should be editable at a time
					$(this).hide();
					$(this).next('.viewCartPopup .row.list-qty').show();
					$(this).nextAll('.viewCartPopup .minicart-delete-item').children('.inline.submitRemoveProduct').first().show();
				});
			}
		});
	},


	//update the quantity or Unit by ajax
	updateQuantityOrUnit: function(obj){

		var entryLoopIndex = obj.closest('.popupCartItem').find('.entry-loop-index').val();
		var $input = obj.closest('.select-quantity').find('.qty-input');
		//var $form = $('#updateCartForm'+entryLoopIndex);
		var $form = $('#updateMiniCartForm'+entryLoopIndex);
		var $selectBtn = obj.parent().siblings('.select-btn');

		var $initialQuantity = $('#initialQuantity'+entryLoopIndex);
		var $initialUnit = $('#initialUnit'+entryLoopIndex);
		var $newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
		var $newUnit = $('#updateEntryUnit'+entryLoopIndex);
		var $displayQuantity = $('#displayQuantity'+entryLoopIndex);
		//if the quantity is not change and the unit is not change will not do the ajax

		//console.log($initialQuantity.val() + ' ' + $newQuantity.val());
		if($initialQuantity.val() !== $newQuantity.val() || $initialUnit.val() !== $newUnit.val()){

			$('body').addClass('loading');
			rm.header.minicart.updatable = false;
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
						rm.header.changeBaseQuantity(result,obj);
						rm.header.displayUpdatedQuantityAndUnit($newQuantity.val(), obj);
						rm.header.minicart.updatable = true;
						//rm.header.removeItemBind();
						ACC.minicart.refreshMiniCartCount();
						$.magnificPopup.close();
					}
				},
				complete:function(){
					$('body').removeClass('loading');
				}
			});
		}
	},

	//if customer changed the quantity or the unit the baseQuantity will changed.
	changeBaseQuantity: function(quantity,obj){
		var productBaseUnit = obj.closest('.popupCartItem').find('.entry-product-unit').val();
		var productBasePluralUnit = obj.closest('.popupCartItem').find('.entry-product-plural-unit').val();
		var $section = obj.closest('.popupCartItem').find('.base-quantity');
		var baseUOM = obj.closest('.popupCartItem').find('.base-quantity').attr('data-base-unit');
		var currentUOM = obj.closest('.popupCartItem').find('.select-btn.sort').attr('data-value');
		//var input = obj.closest('.cartRow').find('.qty-input').val();

		if(baseUOM === currentUOM) {
			$section.hide();
		} else {
			$section.show();
		}

		if(parseInt(quantity) === 1){
			$section.html(quantity+'&nbsp;'+productBaseUnit);
		}else if(parseInt(quantity) > 1){
			console.log(parseInt(quantity));
			$section.html(quantity+'&nbsp;'+productBasePluralUnit);
		}
	},

	// display updated quantity and unit beside the product name in the minicart popup
	displayUpdatedQuantityAndUnit: function(quantity, obj){
		var entryLoopIndex = obj.closest('.popupCartItem').find('.entry-loop-index').val();

		var unitName = '';
		var selectBtn = obj.closest('.popupCartItem').find('.select-list').find('.select-btn');
		if (selectBtn.length === 0) {
			var selectSingle = obj.closest('.popupCartItem').find('.select-list').find('.select-single');
			unitName = selectSingle.html().trim().toLowerCase();
		} else {
			unitName = selectBtn.html().trim().toLowerCase();
		}

		if(parseInt(quantity) === 1){
			$('#itemQuantityAndUnit'+entryLoopIndex).html(quantity+'&nbsp;'+unitName);
		} else if(parseInt(quantity) > 1){
			$('#itemQuantityAndUnit'+entryLoopIndex).html(quantity+'&nbsp;'+unitName+'s');
		}
	},

	resetMiniCart: function(){

		$('body').addClass('loading');

		$.ajax({
            url:'/sabmStore/en/cart/view',
            type:'POST',
            success: function(result) {
                ACC.product.viewCartPopup(result);
                //that.addClass('open');

                /* added by mb - start */
                $('.viewCartPopup .row.list-qty').hide();
                $('.viewCartPopup .minicart-delete-item .inline.submitRemoveProduct').hide();
                $('.select-btn').show();

                var $selectBtn = $('.select-btn:not(.sort):not(.header):not(.js-expiry-date):not(.js-billingunit)');

    			// Store value in data-value
    			$selectBtn.each(function(){
    				var $firstItem = $(this).next('ul').children('li').first();

    				$(this).text($firstItem.html());
    			});

    			rm.header.removeItemBind();
    			rm.header.bingCartEntry();
                /* added by mb - end */

				ACC.minicart.refreshMiniCartCount();
				$('body').removeClass('loading');
            },
            error:function(result) {
                $('body').html(result);
                console.error('error');
				$('body').removeClass('loading');
            }
        });
	},

	carouselBannerClick: function(ev){
	    if($('.carouselBannerTag').length > 0){
        	if(typeof rm.tagManager.trackPromotionClick!=='undefined'){
        		rm.tagManager.trackPromotionClick($(ev).data('id'),$(ev).data('url'), $(ev).data('position'), $(ev).data('type'));
        		if (!$(ev).data('isexternal')) {
        		    window.location.href=$(ev).data('url');
        		}
        	}
	    }
	},

	showDatepickerDaysBorderRadius: function(){
		for(var z=1;z<=7;z++){
			if($('.datepicker-days.delivery-calendar tr:nth-child('+z+') td:not(.disabled)').length === 1){
				$('.datepicker-days.delivery-calendar tr:nth-child('+z+') td:not(.disabled)').first().css('border-radius','15px');
			}else{
				$('.datepicker-days.delivery-calendar tr:nth-child('+z+') td:not(.disabled)').first().css('border-radius','15px 0 0 15px');
				$('.datepicker-days.delivery-calendar tr:nth-child('+z+') td:not(.disabled)').last().css('border-radius','0 15px 15px 0');
			}
		}
	}

};
