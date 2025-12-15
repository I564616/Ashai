/* globals window */

/*jshint unused:false*/
/* globals ACC */
/* globals angular */


	'use strict';

	rm.deals = {
	minOrderQty: 1,
	maxOrderQty: 999,
	specificDate: $('.hid-spcific-day').val(),
	rangeFromDate: $('.hid-range-from-day').val(),
	rangeToDate: $('.hid-range-to-day').val(),
	scope:1,
	init: function(){
		this.datepicker();
		this.expand();
		this.toggleDetails();
		this.bindAddtoCart();
		this.bindFilterCheckbox();
		this.checkIfDealsLoaded();
	},
		 
	toggleDetails:function(){
		$('.js-toggleDetails').on('click',function(){
			$(this).children('span').toggleClass('hidden');
		});
	},
	
	//Add the date picker to the date filter
	datepicker: function(){
		var specificDateSelector = $('#datepicker-specific-day');
		if($('.hid-spcific-day').val() !== ''){
			specificDateSelector.datepicker('setValue',null);
			rm.deals.specificDate = $('.hid-spcific-day').val();
			specificDateSelector.datepicker('setStartDate',new Date());
		}else{
			specificDateSelector.datepicker();
			specificDateSelector.datepicker('setStartDate',new Date());
		}
		specificDateSelector.on('changeDate', function() {
		    if(rm.deals.specificDate === specificDateSelector.datepicker('getDate')){
		    	$('.deal-filter .hid-spcific-day').val('');
		    } else{
    	    	$('.deal-filter .hid-spcific-day').val(
    			    	specificDateSelector.datepicker('getDate').getTime()
    	    	);
    	    	angular.element('#dealPageWrapper').scope().deliveryDateChange();
		    }
		    // rm.deals.submitUserChange('specific');
		});
	},
	
	//submit user's selected filter items 
	submitUserChange: function(type) {
		var $form = $('#filter-form');
		//check date type
		if(type === 'range'){
			var fromDate = $form.find('.hid-range-from-day').val();
			var toDate = $form.find('.hid-range-to-day').val();
			if(fromDate !=='' && toDate !==''){
				rm.deals.checkDateBeforeSubmit(type);
				rm.deals.checkCheckboxBeforeSubmit();
				$form.submit();
			}else if(fromDate ==='' && toDate ==='' && rm.deals.rangeToDate !== '' && rm.deals.rangeFromDate !== ''){
				rm.deals.checkDateBeforeSubmit(type);
				rm.deals.checkCheckboxBeforeSubmit();
				$form.submit();
			}
		}else{
			rm.deals.checkDateBeforeSubmit(type);
			rm.deals.checkCheckboxBeforeSubmit();
			$form.submit();
		}
	},
	
	// check the date value ,if the date value is not empty,commit the form with the date.
	checkDateBeforeSubmit: function(type){
		if(type === 'specific'){
			rm.deals.checkSpecificDateBeforeSubmit();
		}else if(type === 'range'){
			rm.deals.checkRangeDateBeforeSubmit();
		}else if(type === 'checkbox' && !rm.deals.checkSpecificDateBeforeSubmit()){
			rm.deals.checkRangeDateBeforeSubmit();
		}
	},
	
	//check the Specific date
	checkSpecificDateBeforeSubmit: function(){
		if($('.deal-filter .hid-spcific-day').val() !== ''){
			$('.deal-filter .hid-spcific-day').attr('name', 'sd');
			$('.deal-filter .hid-range-from-day').removeAttr('name');
			$('.deal-filter .hid-range-to-day').removeAttr('name');
			return true;
		}else{
			return false;
		}
	},
	
	//check the range date of the filter
	checkRangeDateBeforeSubmit: function(){
		if($('.deal-filter .hid-range-from-day').val() !== '' &&  $('.deal-filter .hid-range-to-day').val() !== ''){
			$('.deal-filter .hid-range-from-day').attr('name','fd');
			$('.deal-filter .hid-range-to-day').attr('name','td');
			$('.deal-filter .hid-spcific-day').removeAttr('name');
			return true;
		}else{
			return false;
		}
	},
	
	//check the checkbox of the category or brand filter
	checkCheckboxBeforeSubmit: function(){
		$('.deal-filter .panel-group .checkbox').each(function(){
			var $thisCheckbox = $(this).find('.facet-check');
			if($thisCheckbox.attr('checked')){
				var inputName = $(this).closest('.panel-group').find('.facet-code-name').val();
				$(this).find('.facet-value').attr('name',inputName);
			}
		});
	},
	
	//add the bind to the checkbox
	bindFilterCheckbox: function(){
		$('.deal-filter .panel-group .checkbox').on('change',function(){
			rm.deals.submitUserChange('checkbox');
		});
	},
	
	expand: function() {
		var list = $('.deal-row');
		var hidden = 0;
		var isHidden;

		list.each(function(){
			if($(this).index() > 2) {
				$(this).addClass('deal-overflow');
				hidden ++;
			}
		});

		// $('#hiddenDeals').text(hidden);

		if(hidden >= 1) {
			$('.toggle-deals').show();
		}

		$('.toggle-deals').on('click',function(){
			$('.deal-overflow').toggleClass('open');
			$('.toggle-deals').toggleClass('open');
			rm.deals.moreDealsText(hidden);
		});

		rm.deals.moreDealsText(hidden);
	},
	moreDealsText: function(hidden){
		if($('.toggle-deals').hasClass('open')){
			$('#hiddenDeals').text($('#showLess').val());
		} else {
			$('#hiddenDeals').text($('#showMore').val());
		}

	}, 
	increaseOnly: function(upitem, downitem) {
		upitem.removeClass('disabled');
		downitem.addClass('disabled');
	},
	reduceOnly: function(upitem, downitem) {
		upitem.addClass('disabled');
		downitem.removeClass('disabled');
	},
	
	// calculate the base quantity of the product
	calculateQty: function(obj,qty){
		var $formQty = obj.closest('.row').find('.qty');
		var dealType = obj.closest('.row').find('.dealType').val();
		
		if($formQty.length > 0){
			if(dealType === 'discount'){
				$formQty.val(qty);
			}else if(dealType === 'bundle'){
				$formQty.val($formQty.attr('base-qty')*qty);
			}
		}
	},
	
	// bind the add to cart button
	bindAddtoCart: function(){
		$('.deal-item .addToCartButton').on('click',function(){
			var $addToCartForm = $(this).closest('.deal-item').find('.add_to_cart_form');
			if($addToCartForm.length > 0){
				$.ajax({
					url:$addToCartForm.attr('action'),
					type:'POST',
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
	},
	
	checkIfDealsLoaded: function(){
		var isDealLoadInProgress = $('#dealPageWrapper').attr('data-dealsLoadInProgress');
		
		if(isDealLoadInProgress === 'true')
		{
			rm.utilities.sapCall = true;
			
			$('body').addClass('loading');
			setInterval(function(){
				$.ajax({
				url:'/b2bunit/checkDealsRefreshStatus',
				type:'GET',
				success: function(result) {
					var jsonObj = JSON.parse(result);
					if(jsonObj.status === 'false')
					{
						window.location.reload();
					}
				},
				error:function() {
					console.log('Error occured while checking deal refresh status');
				}
			});
			}, 5000);
		}
	}
	
};