/* globals document */
/* globals window */
/*jshint devel:true*/
/*jshint unused:false*/

	'use strict';
	
	var changingQuantityFlag = false;
rm.customUI = {

	init: function(){
		this.dropdowns();
		this.tooltips();
		this.filterShowMore();
		this.accordionToggle();
		this.QtyIncrementors();

		$('.nav-tabs a').click(function (e) {
		  e.preventDefault();
		  $(this).tab('show');
		});

		/* modified as this was breaking the mobile nav*/
		// $('.collapse').not('.global-navigation .collapse').collapse();
        // commented out above line as it was affecting global Bootstrap Collapse's behaviour + its intent wasn't clear/testable

	},
	tooltips: function(){
		$('[data-toggle="tooltip"]').tooltip({
			trigger: 'hover click',
			placement: 'auto',
			html: true
		});

		/**SAB-1567 hide tooltip**/
		$('body').on('touchstart', function(e){
			$('[data-toggle="tooltip"]').each(function () {
				// hide any open tooltips when the anywhere else in the body is clicked
				if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.tooltip').has(e.target).length === 0) {
					$(this).tooltip('hide');
				}////end if
			});
		});

	},
	QtyIncrementors: function(){
		// Quantity Incrementors ,Change for cart page

		$('.qty-input').each(function(){
			var input = $(this);
			// rm.customUI.checkChangeable(input);

			input.on('keyup change',function() {
				rm.customUI.checkChangeable(input);
			}).keyup();
		});

		$(document).on('touchstart click','.select-quantity .up:not(.cart-entry)',function(e){                  
            if (!changingQuantityFlag) {
            	changingQuantityFlag = true;
                setTimeout(function(){ changingQuantityFlag = false; }, 150);
                // do something
                
                  e.stopPropagation();
                  e.preventDefault();
                  
                  var $input = $(this).closest('.select-quantity').find('.qty-input');
                  if($input.val() == ''){
               		$input.val(0);
                      }

                  
                  var $input = $(this).closest('.select-quantity').find('.qty-input');
                  if($input.val() < 999){
                        var $qty = $(this).closest('.addtocart-qty').find('.qty');
                        $input.val(parseInt($input.val()) + 1);

                              //update qty when adding to cart - add by Bonnie 10.26
                              $qty.val($input.val());

                        }                 
                        rm.customUI.checkChangeable($input);
              }
      });

      // Change for cart page
      $(document).on('click touchstart','.select-quantity .down:not(.cart-entry)',function(e){
            if (!changingQuantityFlag) {
            	changingQuantityFlag = true;
                setTimeout(function(){ changingQuantityFlag = false; }, 150);
                
                e.stopPropagation();
                  e.preventDefault();
                  var $input = $(this).closest('.select-quantity').find('.qty-input'),
                        minQty = $input.data('minqty');

                  if($input.val() >= (minQty + 1)){
                        var $qty = $(this).closest('.addtocart-qty').find('.qty');
                        $input.val(parseInt($input.val()) - 1);

                        //update qty when adding to cart - add by Bonnie 10.26
                        $qty.val($input.val());

                  }
                  rm.customUI.checkChangeable($input);
            }
            
      });
      
      $(document).on('blur','.qty-input',function(){
       	  var $input = $(this).closest('.select-quantity').find('.qty-input');
       	  if($input.val() == ''){
       		$input.val();
              }  
       	$('.checkoutButton').hide();
		$('.doCartBut').show();
    		});

      
		$(document).on('blur','.qty-input',function(){
			var $hiddenQty = $(this).closest('.addtocart-qty').find('.qty');
			$hiddenQty.val($(this).val());
		}).on('keyup','.qty-input.min-1',function(){
			if($(this).val() === '0'){
				$(this).val('1');
			}
		});
	},

	checkChangeable: function(item){
		var qtyValue = item.val(),
			minqty = item.data('minqty'),
			down = item.parents('.select-quantity').find('.down');

		setTimeout(function(){ // Wait for Angular to set the min property
			if(qtyValue < (minqty + 1)){
				down.addClass('disabled');
			} else {
				down.removeClass('disabled');
			}
		},10);
	},
	dropdowns: function(){

		$(document).on('click',function(){
			$('.select-items').hide();
		});
		// Open dropdown and close all others on page
		$(document).on('click','.select-btn',function(e){

			e.stopPropagation();

			var $list = $(this).next('.select-items');

			$list.toggle();
			$('.select-items').not($list).hide();
		});

		$(document).ready(function(){
			var $selectSingle = $('.select-single:not(.sort):not(.header):not(.js-expiry-date):not(.js-billingunit)');

			// Store value in data-value
			$selectSingle.each(function(){
				var $hiddenField = $(this).closest('.addtocart-qty').find('.addToCartUnit');

				// Set hidden field value on load
				$hiddenField.val($(this).text());
			});

			var $selectBtn = $('.select-btn:not(.sort):not(.header):not(.js-expiry-date):not(.js-billingunit)');

			// Store value in data-value
			$selectBtn.each(function(){
				var $firstItem = $(this).next('ul').children('li').first(),
				    $hiddenField = $(this).closest('.addtocart-qty').find('.addToCartUnit');

				// Set hidden field value and button value on load
				$hiddenField.val($firstItem.attr('data-value'));
				$(this).text($firstItem.html());
			});
			console.log('hit');
			// Change value of hidden field to selection

			//$(document).on('click touchend', '.select-items:not(.sort) li:not(.cart-entry)',function(event){
			var touchmoved;
			$(document).on('click touchend', '.select-items:not(.sort):not(.header):not(.js-expiry-date) li:not(.cart-entry)',function(e){
				e.preventDefault();
			    var $selectBtn = $(this).parent().siblings('.select-btn'),
		        $hiddenField = $(this).closest('.addtocart-qty').find('.addToCartUnit');

				$hiddenField.val($(this).attr('data-value'));
				$selectBtn.text($(this).text());
				$selectBtn.attr('data-value', $(this).attr('data-value'));
				if(touchmoved !== true){
					$(this).parent().hide();
		        }

				// Business Unit sub-title to be driven from the Business Unit filter in the billing and payment page
				var billingFilter = $(this).closest('.billing-filters');
				if (billingFilter.length>0) {
					$('#forUnit').attr('data-unit',$(this).attr('data-text'));
					// $('#billingBusinessUnit').text($(this).attr('data-text'));
				}
				e.stopPropagation();
			}).on('touchmove', function(e){
			    touchmoved = true;
			}).on('touchstart', function(){
			    touchmoved = false;
			});

			$('.select-btn.sort').text($('.select-items.sort li[data-selected="selected"]').html());

			// Change value of hidden field to selection
			$('.select-items.sort li').on('click',function(){
			    $('#sortHiddenField').val($(this).attr('data-value'));
			    $('#sort_form').submit();
			});
		});
	},
	filterShowMore: function(){
		var $itemBlocks = $('.list-filter .panel-group');

		$itemBlocks.each(function(){
			var $items = $(this).find('li'),
				$hiddenItems = $(this).find('li:gt(4)'),
				$showMore = $(this).find('.more');

			$hiddenItems.hide();

			if($items.length >= 5){
				$showMore.show();
			}

			$showMore.on('click',function(){
				$hiddenItems.show();
				$showMore.hide();
			});
		});
	},
	accordionToggle: function(){
		$(document).on('click', '.accordion-toggle', function(event) {
			event.stopPropagation();

			var $body = $(this).next('.panel-collapse');

		    $('.panel-collapse').not($body).removeClass('in');
		});
	}
};
