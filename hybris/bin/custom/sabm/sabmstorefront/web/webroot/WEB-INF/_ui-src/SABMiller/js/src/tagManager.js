/* globals document */
/* globals window */
/* globals dataLayer */
/* globals requestOrigin */
/* globals searchPageDataBreadCrumbs */
/* globals getOnDeal */
/* globals sessionStorage */

'use strict';

$.fn.isOnViewPort = function() {
    var win = $(window);

    var viewPort = {
        top : win.scrollTop(),
        left : win.scrollLeft()
    };
    viewPort.right = viewPort.left + win.width();
    viewPort.bottom = viewPort.top + win.height();

    var bounds = this.offset();
    bounds.right = bounds.left + this.outerWidth();
    bounds.bottom = bounds.top + this.outerHeight();

    return (!(viewPort.right < bounds.left || viewPort.left > bounds.right || viewPort.bottom < bounds.top || viewPort.top > bounds.bottom));
};

$(document).ready(function ()
{


	if ($('.addToCartEventTag').length > 0 ) {
		$('.add_to_cart_form [type="submit"]').on('click', function(){
		    var addToCartEventTag = $(this).parents('.addToCartEventTag');
		    if ($('.recommendation-highlight').has($(this)).length > 0) {
		        var recommendationGroup = $('.recommendation-component').data('smart-recommendation-group');
		        var componentSlot = $('.recommendation-component').data('recommendation-component-slot');
                var componentPosition = $('.recommendation-component').data('recommendation-component-position');
		        rm.tagManager.trackSmartRecommendationAddToCart(addToCartEventTag, recommendationGroup, componentSlot, componentPosition);
            }
			rm.tagManager.trackCartItems(addToCartEventTag);
		});
	}

	if ($('.recommendation-component').length > 0) {
	    var recommendedProducts = [];
	    var recommendationGroup = $('.recommendation-component').data('smart-recommendation-group');
	    var componentSlot = $('.recommendation-component').data('recommendation-component-slot');
	    var componentPosition = $('.recommendation-component').data('recommendation-component-position');
	    var products = $('.recommendation-component').find('.product-pick-description .js-track-product-link');
	    $.each(products, function() {
            var product = {
                'name'          : $(this).data('name'),
                'id'            : $(this).data('id'),
                'price'         : $(this).data('price'),
                'brand'         : $(this).data('brand'),
                'category'      : $(this).data('category'),
                'variant'       : $(this).data('variant'),
                'list'          : $(this).data('list'),
                'actionfield'   : $(this).data('actionfield'),
                'position'      : $(this).data('position'),
                'dealsFlag'     : $(this).data('dealsflag'),
                'dimension7'    : recommendationGroup,
                'dimension8'    : $(this).closest('.productImpressionTag').data('smart-recommendation-model')
            };
            recommendedProducts.push(product);
        });
        rm.tagManager.trackSmartRecommendationImpression(recommendedProducts);

        $('.js-track-product-link').on('click', function() {
            rm.tagManager.trackSmartRecommendationProductView($(this), recommendationGroup, componentSlot, componentPosition);
        });

    }

    if ($('.recommended-products').length > 0) {
        var orderedRecommendedProducts = [];
        var productList = $('.recommended-products').find('.recommended-products-purchased');
        var orderId = $('.recommended-products').data('order-id');
        $.each(productList, function() {
            var smartRecommendationModel = rm.tagManager.getSmartRecommendationModel($(this).data('recommendation-model'));
            var product = {
                'name'          : $(this).data('name'),
                'id'            : $(this).data('id'),
                'price'         : $(this).data('price'),
                'quantity'      : $(this).data('quantity'),
                'brand'         : $(this).data('brand'),
                'category'      : $(this).data('category'),
                'variant'       : $(this).data('variant'),
                'list'          : $(this).data('list'),
                'actionfield'   : $(this).data('actionfield'),
                'position'      : $(this).data('position'),
                'dealsFlag'     : $(this).data('dealsflag'),
                'dimension7'    : $(this).data('recommendation-group'),
                'dimension8'    : smartRecommendationModel
            };
            orderedRecommendedProducts.push(product);
        });
        rm.tagManager.trackSmartRecommendationOrdered(orderedRecommendedProducts, orderId);
    }
	
	//track support page tabs
	if ( $('.service-request .nav-item .nav-link').length > 0 ) {
		$('.service-request .nav-item .nav-link').each(function(){
			$(this).on('click', function(){
				if(!$(this).hasClass('active')){
					var eventLabel = $(this).data('label') + ' Tab';
					
					if(typeof rm.tagManager.trackSupportTabClick!==undefined){
						rm.tagManager.trackSupportTabClick(eventLabel);
					}
				}
			});
		});
	}	
	
	//track chosen deal modal option 
	$('.deals-conflict-popup .btn-apply-deal').on('click', function(){
		rm.tagManager.addDealsImpressionAndPosition('Clicked', 'ConflictDeal', 'ApplyChosenDeal');
	});
	
	//commented gtm event for invoice discrepancy
	/*
	//track invoice discrepancy links
	if ( $('#invoice-discrepancy a.data-link').length > 0 ) {
		$('#invoice-discrepancy a.data-link').each(function(){
			$(this).on('click', function(){
				var action = $(this).attr('data-action'), label = $(this).attr('data-label');
				rm.tagManager.trackInvoiceDiscrepancyLinkClick(action, label);
				//e.preventDefault();
			});
		});
	}	*/
	
	if($('#searchEmptyPage').length >0 && typeof rm.tagManager.trackEmptySearchResult !== 'undefined'){
		rm.tagManager.trackEmptySearchResult($('#searchText').html());
    }
	
    if($('.rotatingBannerTag').length >0){
        var promotions = [];

        var length = $('.rotatingBannerTag').length/2;
        $('.rotatingBannerTag').each(function(index){
            var parentClass = $(this).parent().hasClass('brand-grid-item');

           if((index < length && !parentClass) || parentClass){

               promotions.push({
                   'id'			: $(this).data('alttext'),
                   'name'			: $(this).data('alttext'),
                   'creative'		: $(this).data('type')+ $(this).data('position'),
                   'position'		: 'slot'+$(this).data('position'),
               });
           }
        });
        
        if (promotions.length > 0 && typeof rm.tagManager.trackPromotionExpression!=='undefined') {
        	rm.tagManager.trackPromotionExpression(promotions);
        }
        
        $('.rotatingBannerTag').on('click', function(){
        	if(typeof rm.tagManager.trackPromotionClick!=='undefined'){
        		rm.tagManager.trackPromotionClick($(this).data('alttext'),$(this).data('url'), $(this).data('position'), $(this).data('type'));
        	}
		});
    }
    if($('#imagelinkTag').length >0){
        $('#imagelinkTag').on('click', function(){
        	if(typeof rm.tagManager.trackShopLikeAGenius!=='undefined'){
        		rm.tagManager.trackShopLikeAGenius($(this).data('url'));
        	}
    	});
    }
    if($('.linkParagraphtag').length >0){
        $('.linkParagraphtag').on('click', function(){
        	if(typeof rm.tagManager.trackYourBusinessBilling!=='undefined'){
        		rm.tagManager.trackYourBusinessBilling($(this).data('url'));
        	}
    	});
    }
    
    if($('#dealPageWrapper').length >0){
    	$('.js-track-deals-addtocart').on('click', function() {
    		var dealsRowItems = $(this).closest('.deal').find('.row.deal-item-head, .row.deal-item-body').find('.base-item');
    	 	rm.tagManager.trackCartItems(dealsRowItems);
    	});

    }
    
    
    
    if($('.carouselBannerTag').length > 0){
		var carousels = [];

		$('.carouselBannerTag').each(function(){
    		
			carousels.push({
	            'id'			: $(this).data('id'),
	            'name'			: $(this).data('name'),
	            'creative'		: $(this).data('type')+$(this).data('position'),
	            'position'		: 'slot'+$(this).data('position')
            });
    	});
    	
		/*By AM team commenting default push of all slides
    
        if (carousels.length > 0 && typeof rm.tagManager.trackPromotionExpression!=='undefined') {
        	rm.tagManager.trackPromotionExpression(carousels);
        }
  By hypercare
        /*
        $('#carouselBannerTag').on('click', function(){
        	if(typeof rm.tagManager.trackPromotionClick!=='undefined'){
        		rm.tagManager.trackPromotionClick($(this).data('id'),$(this).data('url'), $(this).data('position'), $(this).data('type'));
        		//window.location.href=$(this).data('url');
        	}
        	
        	if(typeof rm.tagManager.trackCarouselBanner!=='undefined'){
        		var label = $(this).data('id') || $(this).data('name');  //$(this).data('type') + $(this).data('position');
        		rm.tagManager.trackCarouselBanner(label);
        		window.location.href=$(this).data('url');
        	}

        }); */
		
		
		
    }
    
    /*
    // Partially Qualified Deals Modal - Add to Cart - Start 
    if($('#partiallyQualified').length >0){
    	$('.js-track-deals-addtocart').on('click', function() {
    		var dealsRowItems = $('#partiallyQualified').find('.js-track-deal-row').find('.row.base-rows');
    		rm.tagManager.trackCartItems(dealsRowItems);
    	});
    }
    // Partially Qualified Deals Modal - Add to Cart - End
    */
    
    // for Best Sellers (in Home page) and Recommendations (in Cart page) Carousels - Start
    if ($('.related-products li.slider-prev, .related-recommendations li.slider-prev').length > 0) {
    	$('.related-products li.slider-prev, .related-recommendations li.slider-prev').on('click', function() {
    		var visibleElements = $('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide.slick-active');
    		var visibleElementsLen = visibleElements.length;
    		
    		if (visibleElementsLen > 0) {
    			
    			var firstOfTheCurrentVisibleElements = visibleElements.eq(0);
    			var ndxOfTheFirstOfTheCurrentVisibleElements = parseInt(firstOfTheCurrentVisibleElements.attr('data-slick-index'));
    			
    			var nextToBeVisibleElement = $('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide[data-slick-index="' + (ndxOfTheFirstOfTheCurrentVisibleElements - 1) + '"]');
    			var elementWithProductInfo = nextToBeVisibleElement.find('.js-track-product-link');
    			
    			if (elementWithProductInfo.length > 0) {
    				var wasOnViewPort = $(elementWithProductInfo).attr('data-wasonviewport');
    				
	        		if (wasOnViewPort === undefined || wasOnViewPort !== 'true') { // check if element was not on view port previously
	        			$(elementWithProductInfo).attr('data-wasonviewport', 'true');
	        			
	    				var product = [{
			            	'name'		: elementWithProductInfo.data('name'),
			            	'id'		: elementWithProductInfo.data('id'),
			            	'price'		: elementWithProductInfo.data('price'),
			            	'brand'		: elementWithProductInfo.data('brand'),
			            	'category'	: elementWithProductInfo.data('category'),
			            	'variant'	: elementWithProductInfo.data('variant'),
			            	'list'		: elementWithProductInfo.data('list'),
			            	'position'	: elementWithProductInfo.data('position'),
			            	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
			            }];
	    				
	    				if (typeof rm.tagManager.trackProductImpressionAndPosition!=='undefined') {
	    					rm.tagManager.trackProductImpressionAndPosition(product);
	    				}
	        		}
        		}
    	    }
    	});
    }
    
    if ($('.related-products li.slider-next, .related-recommendations li.slider-next').length > 0) {
    	$('.related-products li.slider-next, .related-recommendations li.slider-next').on('click', function() {
    		var visibleElements = $('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide.slick-active');
    		var visibleElementsLen = visibleElements.length;
    		
    		if (visibleElementsLen > 0) {
    			
    			var lastOfTheCurrentVisibleElements = visibleElements.eq(visibleElementsLen - 1);
    			var ndxOfTheLastOfTheCurrentVisibleElements = parseInt(lastOfTheCurrentVisibleElements.attr('data-slick-index'));
    			
    			var nextToBeVisibleElement = $('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide[data-slick-index="' + (ndxOfTheLastOfTheCurrentVisibleElements + 1) + '"]');
    			var elementWithProductInfo = nextToBeVisibleElement.find('.js-track-product-link');
    			
    			if (elementWithProductInfo.length > 0) {
    				var wasOnViewPort = $(elementWithProductInfo).attr('data-wasonviewport');
    				
	        		if (wasOnViewPort === undefined || wasOnViewPort !== 'true') { // check if element was not on view port previously
	        			$(elementWithProductInfo).attr('data-wasonviewport', 'true');
	        			
	    				var product = [{
			            	'name'		: elementWithProductInfo.data('name'),
			            	'id'		: elementWithProductInfo.data('id'),
			            	'price'		: elementWithProductInfo.data('price'),
			            	'brand'		: elementWithProductInfo.data('brand'),
			            	'category'	: elementWithProductInfo.data('category'),
			            	'variant'	: elementWithProductInfo.data('variant'),
			            	'list'		: elementWithProductInfo.data('list'),
			            	'position'	: elementWithProductInfo.data('position'),
			            	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
			            }];
	    				if(typeof rm.tagManager.trackProductImpressionAndPosition!=='undefined'){
	    					rm.tagManager.trackProductImpressionAndPosition(product);
	    				}
	        		}
        		}
    	    }
    	});
    }
    // for Best Sellers (in Home page) and Recommendations (in Cart page) Carousels - End
    
    rm.tagManager.addProductImpressionListener();
    
    var scrollTimeout = null;
    $(window).scroll(function() {
        if (scrollTimeout) {
        	clearTimeout(scrollTimeout);
        }
        
        scrollTimeout = setTimeout(rm.tagManager.addProductImpressionListener(), 500);
    });
    
});


rm.tagManager = {

	trackProductImpressionAndPosition: function(productList) {
		
			var products = [];
			productList.forEach(function(product) {
				products.push({
					'name' 			: product.name,
					'id'			: product.id,
					'price'			: product.price,
					'brand'			: product.brand,
					'category'		: product.category,
					'variant'		: product.variant,
					'list'			: (product.list !== undefined) ? product.list : requestOrigin,
					'position'		: product.position,
					'dimension13'	: getOnDeal(String(product.dealsFlag))
				});	
			});
			
			rm.tagManager.pushProductImpressionAndPosition(products);
			
	},
	
	trackDealsImpressionAndPosition: function(event, dealsList) {
		
		var deals = [];
		dealsList.forEach(function(product) {
			deals.push({
				'name' 			: product.name,
				'id'			: product.id,
				'price'			: product.price,
				'brand'			: product.brand,
				'category'		: product.category,
				'variant'		: product.variant,
				'list'			: (product.list !== undefined) ? product.list : requestOrigin,
				'position'		: product.position,
				'dimension13'	: getOnDeal(String(product.dealsFlag))
			});	
		});
		
		console.log('track deals impression and position:' + event);

		
		rm.tagManager.pushDealsImpressionAndPosition(event, deals);
		
	},
	
	trackSearchPopupInteractions: function(elementWithProductInfo) {
		var product = {
        	'name'		: elementWithProductInfo.data('name'),
        	'id'		: elementWithProductInfo.data('id'),
        	'price'		: elementWithProductInfo.data('price'),
        	'brand'		: elementWithProductInfo.data('brand'),
        	'category'	: elementWithProductInfo.data('category'),
        	'variant'	: elementWithProductInfo.data('variant'),
        	'list'		: elementWithProductInfo.data('list'),
        	'actionfield'		: elementWithProductInfo.data('actionfield'),
        	'position'	: elementWithProductInfo.data('position'),
        	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
        };
		rm.tagManager.trackProductClick(product);
	},
	
	trackAddtoCartFromSearchPopup : function(addtoCartForm){
		var qty = parseInt(addtoCartForm.find('[name="qty"]').val());
		var code = addtoCartForm.find('[name="productCodePost"]').val();
		var product = {
        	'name': addtoCartForm.data('name'),
        	'id':code,
        	'qty':qty,
        	'list': addtoCartForm.data('list'),
        	'variant':addtoCartForm.data('variant'),
        	'position':addtoCartForm.data('position'),
        	'dealsflag':addtoCartForm.data('dealsflag'),
        	'actionfield':addtoCartForm.data('actionfield')
        };
		if (typeof rm.tagManager.trackCart!=='undefined') {
				rm.tagManager.trackCart(product, 'add');
			}
	},
		
	
	trackProductImpressionAndPositionForAdditionalResults: function(results) {
		
		var products = [];
		
		results.forEach(function(product, ndx) {
			var category = '';
			if (product.categories !== null) {
				category = product.categories[product.categories.length - 1].name;
			}
			
			products.push({
				'name' 			: product.name,
				'id'			: product.code,
				'price'			: (product.price !== null) ? product.price.value : '',
				'brand'			: product.brand,
				'category'		: rm.tagManager.escapeHtml(category),
				'variant'		: (product.unit !== null) ? product.unit.name : '',
				'list'			: requestOrigin,
				'position'		: ndx + 1,
				'dimension13'	: getOnDeal(product.dealsFlag)
			});	
			
		});
		
		rm.tagManager.pushProductImpressionAndPosition(products);
		
	},
	
	
	pushProductImpressionAndPosition: function(products) {
		
		var productImpression = {
			'event': 'impressionPushed',
			'ecommerce': {
				'currencyCode': 'AUD',
				'impressions': products
			}
		};
		
	    var okToPush = rm.tagManager.isProductImpressionAndPositionOKToPush(productImpression);
	    
	    if (okToPush) {
			dataLayer.push(productImpression);
	    }
	},
	
	pushDealsImpressionAndPosition: function(event, deals) {
		
		var dealsImpression = {
			'event': event,
			'ecommerce': {
				'currencyCode': 'AUD',
				'impressions': deals
			}
		};
		
		console.log('push deals impression and position:' + event);
		
	    var okToPush = rm.tagManager.isProductImpressionAndPositionOKToPush(dealsImpression);
	    
	    if (okToPush) {
			dataLayer.push(dealsImpression);
	    }
	},
	
	isProductImpressionAndPositionOKToPush: function(productImpressionObj) {

		var okToPush = false;
		
		// check/search if group of product impressions is already in the dataLayer and
		// return the results
		var result = $.grep(dataLayer, function(item) { 
			return item.event === productImpressionObj.event && JSON.stringify(item.ecommerce) === JSON.stringify(productImpressionObj.ecommerce);
	    });
	    
	    // if group of product impressions was not present in the dataLayer, check each product impression if it
	    // is present in one of the product impressions that is already in the dataLayer
	    if (result.length === 0) {
	    	okToPush = true;
	    	
	    	result = $.grep(dataLayer, function(item) { 
	    		return item.event === productImpressionObj.event;
	        });
	    	
	        if (result.length > 0) {
	    	    $.each(productImpressionObj.ecommerce.impressions, function(productImpressionObjKey, productImpressionObjValue) {
	    	    	
	    	    	$.each(result, function(resultKey, resultValue) {
	    	    		$.each(resultValue.ecommerce.impressions, function(resultImpressionKey, resultImpressionValue) {
		    	    		if (JSON.stringify(resultImpressionValue) === JSON.stringify(productImpressionObjValue)) {
		    	    			okToPush = false;
		    	    			return okToPush;
		    	    		}
	    	    		});
	    	    	});
	    	    	
	    		}); 
	        }
	    }
	    
	    return okToPush;
	},
	
	
	trackOnCheckoutOption: function(step, checkoutOption) {
		console.log('onCheckoutOption');
		console.log('step=' + step);
		console.log('option=' + checkoutOption);
		
		dataLayer.push({
			'event': 'checkoutOption',
			'ecommerce': {
				'checkout_option': {
					'actionField': {'step': step, 'option': checkoutOption}
				}
			}
		});
	},
	
	
	trackEmptySearchResult: function(searchText) {
		dataLayer.push({
				'event': 'zeroSearch',
				'eventCategory': 'error',
				'eventAction':'0 Search Results',
				'eventLabel': searchText
			});
	},

	trackPDPSaveToTemplate:	function() {
		 dataLayer.push({
		 		'event': 'productPageClick',
		 		'eventCategory': 'Products',
		 		'eventAction':'Click',
		 		'eventLabel': 'Save to Template'
		 	});
	},

	trackPDPOtherPackOptions: function() {
		 dataLayer.push({
		 		'event': 'productPageClick',
		 		'eventCategory': 'Products',
		 		'eventAction':'Click',
		 		'eventLabel': 'Other pack options'
		 	});
	},

	trackPDPPackConfiguration: function() {
		 dataLayer.push({
		 		'event': 'productPageClick',
		 		'eventCategory': 'Products',
		 		'eventAction':'Click',
		 		'eventLabel': 'Pack configuration'
		 	});
	},

	trackPDPPriceConditions: function() {
		  dataLayer.push({
		  		'event': 'productPageClick',
		  		'eventCategory': 'Products',
		  		'eventAction':'Click',
		  		'eventLabel': 'Price conditions'
		  	});
	},

	trackListingFilter:	function(filter) {
	    dataLayer.push({
	    		'event': 'refine',
	    		'eventCategory': 'Product Listing',
	    		'eventAction':'filter by',
	    		'eventLabel': filter
	    	});
	},

	trackTopNavEvent: function(eventAction, url) {
	    dataLayer.push({
	        'event': 'gaEvent',
	        'eventCategory': 'Top Nav',
	        'eventAction': eventAction,
	        'eventLabel': url
	    });
	 },

	 trackShopLikeAGenius: function(url) {
	      if(url.indexOf('smartOrders') !== -1){
	           dataLayer.push({
	              'event': 'gaEvent',
	              'eventCategory': 'Page Body',
	              'eventAction': 'Genius',
	              'eventLabel': url
	          });
	      }
	      return true;
	 },
	 
	 trackYourBusinessBilling: function(url) {
		 if(url.indexOf('your-business') !== -1){
            if(url.indexOf('/billing') !== -1){
                 dataLayer.push({
                     'event': 'gaEvent',
                     'eventCategory': 'Page Body',
                     'eventAction': 'Billing and Payment',
                     'eventLabel': url
                 });
            } else {
                dataLayer.push({
                    'event': 'gaEvent',
                    'eventCategory': 'Page Body',
                    'eventAction': 'Your Business',
                    'eventLabel': url
                });
            }

		 }
		 return true;
	 },

	 
	 trackCheckoutError: function(msg) {
        dataLayer.push({
             'event': 'checkoutError',
             'text': msg
         });
     },

     
     trackPromotionClick: function(promoName, promoUrl, position, type) {
       dataLayer.push({
           'event': 'promotionClick',
           'ecommerce': {
                 'promoClick': {
                     'promotions':[{
                         'id': promoName,
                         'name': promoName,
                         'creative': type+position,
                         'position': 'slot'+position
                     }]
                 }
           },
           'eventCallback': function() {}
       });
     },
     
     //tracking slick view
     trackPromotionExpressionView: function(promoName, promoUrl, position, type) {
         dataLayer.push({
        	 'event': 'promotionView',
             'ecommerce': {
                   'promoView': {
                  	 'promotions':[{
                       'id': promoName,
                       'name': promoName,
                       'creative': type+position,
                       'position': 'slot'+position
                   }]
         }
             }
         });
       },

     
     trackPromotionExpression: function(promos) {
       dataLayer.push({
    	   'event': 'promoView',
           'ecommerce': {
                 'promoView': {
                     'promotions':promos
                 }
           }
       });
     },
     
     
	 trackBillingPayment: function(url) {
	    if(url.indexOf('your-business') !== -1){
	         dataLayer.push({
	            'event': 'GA',
	            'eventCategory': 'Page Body',
	            'eventAction': 'Your Business',
	            'eventLabel': url
	        });
	    }
	    return true;
	 },
	 
	 trackProductClick: function(productObj) {
		var categories = productObj.category;
		
		searchPageDataBreadCrumbs.forEach(function(breadCrumb) {
			categories += '/' + breadCrumb.facetValueName;
		});
		
		sessionStorage.setItem('listName', productObj.actionfield);
		sessionStorage.setItem('listOriginPos', productObj.position);
		
		dataLayer.push({
			'event': 'productClick',
			'ecommerce': {
				'currencyCode': productObj.currencycode,
				'click': {
					'actionField': {'list': productObj.actionfield}, 
					'products': [{
						'name': productObj.name, // Name or ID is required.
						'id': productObj.id,
						'price': productObj.price,
						'brand': productObj.brand,
						'category': categories, // composed of product root category and breadcrumb categories
						'variant': productObj.variant,
						'position': productObj.position
					}]
				}
			},
			
			'eventCallback': function() {}
		});
	},

	
	trackCartItems: function(productElements) {
		productElements.each(function() {
			var elementWithProductInfo = $(this).find('.js-track-product-link');
			var qty = parseInt($(this).find('.qty-input').val());
			
 			if (elementWithProductInfo.length > 0 && qty > 0) {
 				
 				if (typeof rm.tagManager.ProductForTrackCart !== 'undefined') {
 				
 					var productObj = new rm.tagManager.ProductForTrackCart(
     						elementWithProductInfo.data('currencycode'),
     						elementWithProductInfo.data('name'),
     						elementWithProductInfo.data('id'),
     						elementWithProductInfo.data('price'),
     						elementWithProductInfo.data('brand'),
     						elementWithProductInfo.data('category'),
     						elementWithProductInfo.data('variant'),
     						elementWithProductInfo.data('position'),
     						elementWithProductInfo.data('dealsflag'),
     						qty,
     						elementWithProductInfo.data('actionfield'));

     				if (typeof rm.tagManager.trackCart!=='undefined') {
     					rm.tagManager.trackCart(productObj, 'add');
     				}
 				}
 				
     		}
		});
	},
	
	trackCart: function(productObj, actionName) {
		// Adding/removing a product to/from the shopping cart
		var eventName = '';
		if (actionName !== undefined && actionName !== null) {
			if (actionName === 'add') {
				eventName = 'addToCart';
			} 
			else if (actionName === 'remove') {
				eventName = 'removeFromCart';
			}
		}
		
		if (eventName !== '') {
			var trackCartEventJson = {
	   			'event': eventName,
	   			'ecommerce': {
	   				'currencyCode': productObj.currencycode
	   			}
	   		};
	   			    
	   		trackCartEventJson.ecommerce[actionName] = {
				'actionField': {'list': productObj.actionfield},
				'products': [{ 
					'name'			: productObj.name,
					'id'			: productObj.id,
					'price'			: productObj.price,
					'brand'			: productObj.brand,
					'category'		: productObj.category, 	
					'variant'		: productObj.variant, 	
					'position'		: productObj.position, 	
					'dimension13'	: getOnDeal(String(productObj.dealsflag)), // Deal or No Deal
					'quantity'		: productObj.quantity
				}]
			};
	   		
	   		dataLayer.push(trackCartEventJson);
		}
		
		console.log('track cart:' + JSON.stringify(dataLayer));
	},
	 
	ProductForTrackCart: function(currencycode, name, id, price, brand, category, variant, position, dealsflag, quantity, actionfield) {
		this.currencycode = currencycode;
		this.name = name;
		this.id = id;
		this.price = price;
		this.brand = brand;
		this.category = category;
		this.variant = variant;
		this.position = position;
		this.dealsflag = dealsflag;
		this.quantity = quantity;
		this.actionfield = actionfield;
	},
	
	escapeHtml: function(text) {
		  var characters = {
		    '&': '&amp;',
		    '"': '&quot;',
		    '\'': '&#039;',
		    '<': '&lt;',
		    '>': '&gt;'
		  };
		  
		  return (text + '').replace(/[<>&"']/g, function(m) {
		    return characters[m];
		  });
	},
		
	addProductImpressionListener: function() {
		
		// for product listing Carousels such as Best Sellers in Home page and Recommendations in Cart page
		if ($('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide.slick-active').length > 0) {
	        var productsInCarousel = [];
	        $('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide.slick-active').each(function() {
	        	
	        	if ($(this).length > 0 && $(this).isOnViewPort()) {
	        		var elementWithProductInfo = $(this).find('.js-track-product-link');
		        	
		        	if (elementWithProductInfo.length > 0) {
		        		var wasOnViewPort = $(elementWithProductInfo).attr('data-wasonviewport');
		        		
		        		if (wasOnViewPort === undefined || wasOnViewPort !== 'true') { // check if element was not on view port previously
		        			$(elementWithProductInfo).attr('data-wasonviewport', 'true');
		        			
		        			productsInCarousel.push({
				            	'name'		: elementWithProductInfo.data('name'),
				            	'id'		: elementWithProductInfo.data('id'),
				            	'price'		: elementWithProductInfo.data('price'),
				            	'brand'		: elementWithProductInfo.data('brand'),
				            	'category'	: elementWithProductInfo.data('category'),
				            	'variant'	: elementWithProductInfo.data('variant'),
				            	'list'		: elementWithProductInfo.data('list'),
				            	'position'	: elementWithProductInfo.data('position'),
				            	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
				            });
		        		}
	        		}
	        	
	        	}
	        });
	        
	        if (productsInCarousel.length > 0) {
	        	if(typeof rm.tagManager.trackProductImpressionAndPosition!=='undefined'){
	        		rm.tagManager.trackProductImpressionAndPosition(productsInCarousel);
	        	}
	        }
	    }


		// for product listings such as CUB Picks, Category, Order History, Order Templates, Deals listings, Partially Qualified Deals modal
		if ($('div.productImpressionTag:not(.bestSellerTag, .recommendationsTag):not(.slick-slide):not(.slick-active)').length > 0) {
	        var products = [];
	        $('div.productImpressionTag:not(.bestSellerTag, .recommendationsTag):not(.slick-slide):not(.slick-active)').each(function() {
	        	
	        	if ($(this).length > 0 && $(this).isOnViewPort()) {
	        		var elementWithProductInfo = $(this).find('.js-track-product-link');
		        	
		        	if (elementWithProductInfo.length > 0) {
		        		var wasOnViewPort = $(elementWithProductInfo).attr('data-wasonviewport');

		        		if (wasOnViewPort === undefined || wasOnViewPort !== 'true') { // check if element was not on view port previously
		        			
		        			if ($('#dealPageWrapper').length === 0 || 
		        				($('#dealPageWrapper').length > 0 && 
		        						($(this).closest('.js-track-deal-row').find('.row.deal-item-head.single').length > 0 || // for deals with single base product
		        						 $(this).closest('.js-track-deal-row').find('.js-track-show-details').hasClass('wasclicked')) // for deals with multiple base products
		        				) ||
		        				// for Partially Qualified Deals Modal
		        				($('#partiallyQualified').length > 0 && 
		        				   $(this).closest('.js-track-deal-row').find('.js-track-show-details').hasClass('wasclicked'))
		        			   )
		        			{ 
		        			
		        				$(elementWithProductInfo).attr('data-wasonviewport', 'true');
			        			
					            products.push({
					            	'name'		: elementWithProductInfo.data('name'),
					            	'id'		: elementWithProductInfo.data('id'),
					            	'price'		: elementWithProductInfo.data('price'),
					            	'brand'		: elementWithProductInfo.data('brand'),
					            	'category'	: elementWithProductInfo.data('category'),
					            	'variant'	: elementWithProductInfo.data('variant'),
					            	'list'		: elementWithProductInfo.data('list'),
					            	'position'	: elementWithProductInfo.data('position'),
					            	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
					            });
					            /*
					        	var isSegmentEnabled = ( $('#isSegmentEnabled').val() === 'true' ) ? true : false;

					            if ( isSegmentEnabled ) {
					            	
						            //Segment in recommendation page
						            if($('body.page-recommendationCusPage').length > 0){
						        		rm.segment.trackProductImpressionAndPosition('Recommendation Viewed', elementWithProductInfo);
		        					}
	
						            //Segment in deals page
						            if($('body.page-deals').length > 0){
						        		rm.segment.trackProductImpressionAndPosition('Deals Viewed', elementWithProductInfo);
						            }
						            
					            }*/
					            
		        			}
		        		
		        		}
	        		}
	        	}
	        	
	        });
	        
	        
	        if (products.length > 0) {
	        	if(typeof rm.tagManager.trackProductImpressionAndPosition!=='undefined'){
	        		rm.tagManager.trackProductImpressionAndPosition(products);
	        	}
	        }
	    }
		
		
		// Conflicting Deals Modal - Start 
	    if ($('#dealsConflictPopup').length > 0) {
	  		setTimeout(function() {
		        var productsInConflictDealsModal = [];
		        
		        $('div.deal-option.productImpressionTag').each(function() { 
		        	
		        	if ($(this).length > 0 && setTimeout(function() {$(this).isOnViewPort();}, 0)) {
			        	var elementWithProductInfo = $(this).find('.js-track-product-link');
			        	
			        	if (elementWithProductInfo.length > 0) {
			        		var wasOnViewPort = $(elementWithProductInfo).attr('data-wasonviewport');
			
			        		if (wasOnViewPort === undefined || wasOnViewPort !== 'true') { // check if element was not on view port previously
			        			
			        			$(elementWithProductInfo).attr('data-wasonviewport', 'true');
			        			
			        			productsInConflictDealsModal.push({
					        		'name'		: elementWithProductInfo.data('name'),
					            	'id'		: elementWithProductInfo.data('productcode'),
					            	'price'		: elementWithProductInfo.data('price'),
					            	'brand'		: elementWithProductInfo.data('brand'),
					            	'category'	: elementWithProductInfo.data('category'),
					            	'variant'	: elementWithProductInfo.data('variant'),
					            	'list'		: elementWithProductInfo.data('list'),
					            	'position'	: elementWithProductInfo.data('position'),
					            	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
					        	});
			        		}
			        	}
		        	}
		        });
		        
				if (productsInConflictDealsModal.length > 0 && typeof rm.tagManager.trackProductImpressionAndPosition!=='undefined') {
					rm.tagManager.trackProductImpressionAndPosition(productsInConflictDealsModal);
				}
	  		}, 0);
	    }
	    // Conflicting Deals Modal - End
	},

	 trackCarouselBanner: function(label) {
         dataLayer.push({
            'event': 'gaEvent',
            'eventCategory': 'Ecommerce',
            'eventAction': 'promotionClick',
            'eventLabel': label
        });
	      return true;
	 }, 

	 trackRecommendation: function(action, label) {
		 
		 if ( action === 'remove' ) {
	       dataLayer.push({
	          'event': 'gaEvent',
	          'eventCategory': 'Page Body',
	          'eventAction': 'Clicked',
	          'eventLabel': label
	      });
		 }

	      return true;
	 },

	 trackDealsModal: function(type) {
       dataLayer.push({
          'event': 'gaEvent',
          'eventCategory': 'Modal',
          'eventAction': 'Viewed',
          'eventLabel': type
      });

      return true;
	 },

	 trackChosenDealsClick: function(type, label) {
	       dataLayer.push({
	          'event': 'gaEvent',
	          'eventCategory': 'Modal',
	          'eventAction': 'Clicked',
	          'eventLabel': type +' | '+ label
	      });

	      return true;
		 },

	addDealsImpressionAndPosition: function(action, type, label){
		
		if ( action === 'Viewed' ) {
			if ( typeof rm.tagManager.trackDealsModal !== 'undefined' ) {
				rm.tagManager.trackDealsModal(type);
			}
		} else if ( action === 'Clicked' ) {
			if ( typeof rm.tagManager.trackChosenDealsClick !== 'undefined' ) {
				rm.tagManager.trackChosenDealsClick(type, label);
			}
		}
	},
	
	trackCarouselClick: function(carouselObj) {
		var obj = carouselObj;

		dataLayer.push({
			   'event':'carouselBannerClick',
			   'ecommerce': {
			     'promoClick': {
			       'promotions': [{
			         'id': obj.altText === '' ? 'Carousel Banner Image' : obj.altText,
			         'name': obj.altText === '' ? 'Carousel Banner Image' : obj.altText,
			         'creative': obj.type+obj.position,
			         'position': 'slot'+obj.position
			      }]
			    }
			  },
			  'eventCallback': function() {
				  window.location.href = obj.url;
			  }
		});
		
        console.log('carousel tagging sent' + JSON.stringify(dataLayer));
	},

	trackSupportTabClick: function(label) {
	    dataLayer.push({
	        'event': 'gaEvent',
	        'eventCategory': 'Page Body',
	        'eventAction': 'navigationClick',
	        'eventLabel': label
	    });
	},
	
	trackInvoiceDiscrepancyLinkClick: function(action, label) {
	    dataLayer.push({
	        'event': 'gaEvent',
	        'eventCategory': 'Page Body',
	        'eventAction': action,
	        'eventLabel': label
	    });
	 },
	 
	trackInvoiceDiscrepancyStepProcess: function(title) {
	    dataLayer.push({
	        'event': 'VirtualPageview',
	        'virtualPageURL': '/your-business/invoicediscrepancy',
	        'virtualPageTitle': title,
	    });
	 },

    trackSmartRecommendationImpression: function(recommendedProducts) {
        var smartRecommendationImpression = {
            'event': 'smartRecommendationImpression',
            'ecommerce': {
                'currencyCode': 'AUD',
        		'impressions': recommendedProducts
            }
        };

        var okToPush = rm.tagManager.isProductImpressionAndPositionOKToPush(smartRecommendationImpression);
        if (okToPush) {
            dataLayer.push(smartRecommendationImpression);
        }
    },

	trackSmartRecommendationAddToCart: function (productElement, recommendationGroup, componentSlot, componentPosition) {
	    var elementWithProductInfo = productElement.find('.js-track-product-link');
        var qty = parseInt(productElement.find('.qty-input').val());

        if (elementWithProductInfo.length > 0 && qty > 0) {
            var product = {
                'name'                  : elementWithProductInfo.data('name'),
                'id'                    : elementWithProductInfo.data('id'),
                'price'                 : elementWithProductInfo.data('price'),
                'quantity'              : qty,
                'brand'                 : elementWithProductInfo.data('brand'),
                'category'              : elementWithProductInfo.data('category'),
                'variant'               : elementWithProductInfo.data('variant'),
                'list'                  : elementWithProductInfo.data('list'),
                'actionfield'           : elementWithProductInfo.data('actionfield'),
                'position'              : elementWithProductInfo.data('position'),
                'dealsFlag'             : elementWithProductInfo.data('dealsflag'),
                'dimension7'            : recommendationGroup,
                'dimension8'            : elementWithProductInfo.closest('.productImpressionTag').data('smart-recommendation-model')
            };
            var products = [];
            products.push(product);

            var trackSmartRecommendationAddToCartJson = {
                'event': 'smartRecommendationAddToCart',
                'componentSlot' : componentSlot,
                'componentPosition' : componentPosition,
                'ecommerce': {
                    'currencyCode'  : 'AUD'
                }
            };

            trackSmartRecommendationAddToCartJson.ecommerce.add = {
                'actionField'   : {'action': 'add'},
                'products'      : products
            };

            dataLayer.push(trackSmartRecommendationAddToCartJson);

        }
	},

	trackSmartRecommendationProductView: function (productElement, recommendationGroup, componentSlot, componentPosition) {
	    var product = {
            'name'                  : productElement.data('name'),
            'id'                    : productElement.data('id'),
            'price'                 : productElement.data('price'),
            'brand'                 : productElement.data('brand'),
            'category'              : productElement.data('category'),
            'variant'               : productElement.data('variant'),
            'list'                  : productElement.data('list'),
            'actionfield'           : productElement.data('actionfield'),
            'position'              : productElement.data('position'),
            'dimension7'            : recommendationGroup,
            'dimension8'            : productElement.closest('.productImpressionTag').data('smart-recommendation-model')
        };

        var trackRecommendationViewEventJson = {
            'event': 'smartRecommendationProductView',
            'componentSlot' : componentSlot,
            'componentPosition' : componentPosition,
            'ecommerce': {
                'currencyCode'  : 'AUD'
            }
        };

        var products = [];
        products.push(product);

        trackRecommendationViewEventJson.ecommerce.click = {
            'actionField'   : {'action': 'click'},
            'products'       : products
        };

        dataLayer.push(trackRecommendationViewEventJson);
	},

	trackSmartRecommendationOrdered: function (products, orderId) {
        var trackRecommendationOrderEventJson = {
            'event': 'smartRecommendationOrdered',
            'ecommerce': {
                'currencyCode'  : 'AUD'
            }
        };

        trackRecommendationOrderEventJson.ecommerce.purchase = {
            'actionField'   : {'id': orderId.toString()},
            'products'      : products
        };

        dataLayer.push(trackRecommendationOrderEventJson);
	},

	getSmartRecommendationModel: function (smartRecommendationModel) {
	    if (smartRecommendationModel === 'MODEL1') {
	        return 'M1';
	    }
	    if (smartRecommendationModel === 'MODEL2') {
	        return 'M2';
	    }
        if (smartRecommendationModel === 'MODEL3') {
	        return 'M3';
	    }
	    return '';
	}

};
