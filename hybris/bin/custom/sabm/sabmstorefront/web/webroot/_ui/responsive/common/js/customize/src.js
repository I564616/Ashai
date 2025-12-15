var rm = rm || {};
/* globals document */

rm.utilities = (function() {
	'use strict';

	// Open dropdown and close all others on page
	$('.select-btn').on('click',function(){
		var $list = $(this).next('.select-items');
		$list.slideToggle(200);
		$('.select-items').not($list).slideUp();
	});

	$(document).ready(function(){
		var $selectBtn = $('.select-btn');

		// Store value in data-value
		$selectBtn.each(function(){
			$(this).attr('data-value', $(this).innerText);
		});
		
		// Change value of data-value to selection
		$('.select-items li').on('click',function(){
			var $selectBtn = $(this).parent().siblings('.select-btn');

			$selectBtn.attr('data-value', $(this)[0].innerText).text($(this)[0].innerText);
			$('.select-items').hide();
		});
	});

	// Expand filter for mobile

	$('.list-refine-btn').on('click', function(){
		$('.list-filter').slideToggle();
		$(this).toggleClass('open');
	});

	// Quantity Incrementors
		$('.select-quantity .up').on('click',function(){
			var $input = $(this).closest('.select-quantity').find('.qty-input');
			if($input.val() >= 1){
				$input.val(parseInt($input.val()) + 1);
			}
		});

		$('.select-quantity .down').on('click',function(){
			var $input = $(this).closest('.select-quantity').find('.qty-input');
			if($input.val() >= 2){
				$input.val(parseInt($input.val()) - 1);
			}
		});




	

	return {

	};



}());
/* ===== yeoman scripts-hook do-not-remove ===== */


/*jshint unused:false*/

	'use strict';

rm.carousels = {

	init: function(){

		$('.other-packages .slick-slider').slick({
	    	infinite: true,
	    	slidesToShow: 8,
	    	slidesToScroll: 1,
	    	prevArrow: $('.other-packages .slider-prev'),
	    	nextArrow: $('.other-packages .slider-next'),
	    	responsive: [
	    	{
	    		breakpoint: 990,
	    		settings: {
	    			slidesToShow: 6
	    		}
	    	},
	    	{
    			breakpoint: 768,
    			settings: {
    				slidesToShow: 4
			}
	    	},
    		{
    			breakpoint: 480,
    			settings: {
    				slidesToShow: 2
    			}
    		}
	    	]
	    });

    	$('.related-products .slick-slider').slick({
        	infinite: true,
        	slidesToShow: 4,
        	slidesToScroll: 1,
        	prevArrow: $('.related-products .slider-prev'),
        	nextArrow: $('.related-products .slider-next'),
	    	responsive: [
	    	{
	    		breakpoint: 990,
	    		settings: {
	    			slidesToShow: 4
	    		}
	    	},
	    	{
    			breakpoint: 768,
    			settings: {
    				slidesToShow: 2
			}
	    	}
	    	]
        });

		$('.product-images .slick-hero').slick({
	    	infinite: false,
	    	slidesToShow: 1,
	    	slidesToScroll: 1,
	    	arrows: false,
	    	fade: true
	    });

    	$('.product-images .slick-slider-thumbs').slick({
        	infinite: false,
        	slidesToShow: 4,
        	slidesToScroll: 1,
        	asNavFor: '.product-images .slick-hero',
        	focusOnSelect: true,
        	prevArrow: $('.product-images li.slider-prev'),
        	nextArrow: $('.product-images li.slider-next')
        });

	}
};

/*jshint unused:false*/

	'use strict';

rm.modals = {

	init: function(){

      $('.slick-hero').magnificPopup({
      	delegate: 'a',
      	type:'image'
      });

	}
};