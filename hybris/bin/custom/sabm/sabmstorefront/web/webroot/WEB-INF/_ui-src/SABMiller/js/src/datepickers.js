/*jshint unused:false*/
/* globals document*/
/* globals window */
/* globals angular*/
/* globals trackTopNavEvent*/
	'use strict';

rm.datepickers = {
	calendarDeltaTime:1123200000,//13 days in milliseconds
	init:function(){

		this.datePickers();
		this.updateDeliveryDatePicker();
		this.dealDeliveryDatePicker();
		this.getDeliveryData();
		this.getPublicHolidaysData();
		this.defaultCalendarPicker();
	},

	datePickers: function() {

	    var eighteenMonthsBefore = new Date();
    	eighteenMonthsBefore.setMonth(eighteenMonthsBefore.getMonth() - 18);

		$('.basic-datepicker').datepicker({
			autoclose: true,
			orientation: 'bottom left',
			format: 'dd/mm/yyyy'
		});

		/*
		// Billing Page
		$('.billing-payment .input-daterange').datepicker({
		    format: 'dd/mm/yyyy',
		    orientation: 'bottom left',
		    autoclose: true
		}); */


		$('.billing-payment .billingdate-start').datepicker({
		    format: 'dd/mm/yyyy',
			autoclose: true,
		    orientation: 'bottom left'
		}).on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			// data-value is always updated
            $(this).attr('data-value',rm.datepickers.convertDate(date));
            $(this).attr('data-selectDate', date);

            var toDate = $('.billing-payment .billingdate-end').attr('data-selectDate');

            if ( rm.datepickers.validateFromToDate(toDate, date) ) {
                // when date range is correct, update toDate data-value
                $('.billing-payment .billingdate-end').attr('data-value', rm.datepickers.convertDate(new Date(toDate)));
                $('#billingUpdateFilter').attr('disabled', false);
                $('.billing-payment .billingdate-end').css('border', '1px solid #ccc');
            } else {
                $('#billingUpdateFilter').attr('disabled', 'disabled');
                $('.billing-payment .billingdate-end').css('border', '1px solid #f00');
            }
		});

		$('.billing-payment .billingdate-end').datepicker({
		    format: 'dd/mm/yyyy',
			autoclose: true,
		    orientation: 'bottom left'
		}).on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
            $(this).attr('data-selectDate', date);

            var fromDate = $('.billing-payment .billingdate-start').attr('data-selectDate');

			if ( rm.datepickers.validateFromToDate(date, fromDate) ) {
				$(this).attr('data-value',rm.datepickers.convertDate(date));
				$('#billingUpdateFilter').attr('disabled', false);
				$(this).css('border', '1px solid #ccc');
			} else {
				$(this).attr('data-value','');
				$('#billingUpdateFilter').attr('disabled', 'disabled');
				$(this).css('border', '1px solid #f00');
			}
		});

		$('.raised-invoice-discrepancy .invoicedate-start').datepicker({
		    format: 'dd/mm/yyyy',
			autoclose: true,
		    orientation: 'bottom left'
		}).on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			$(this).attr('data-selectDate', date);

			$(this).attr('data-value',rm.datepickers.convertDate(date));

			var toDate = $('.raised-invoice-discrepancy .invoicedate-end').attr('data-selectDate');

			if ( rm.datepickers.validateFromToDate(toDate, date) ) {
                // when date range is correct, update toDate data-value
                $('.raised-invoice-discrepancy .invoicedate-end').attr('data-value', rm.datepickers.convertDate(new Date(toDate)));
                $('#raisedInvoiceDiscrepancyUpdateFilter').attr('disabled', false);
                $('.raised-invoice-discrepancy .invoicedate-end').css('border', '1px solid #ccc');
            } else {
                $('#raisedInvoiceDiscrepancyUpdateFilter').attr('disabled', 'disabled');
                $('.raised-invoice-discrepancy .invoicedate-end').css('border', '1px solid #f00');
            }
		});

		$('.raised-invoice-discrepancy .invoicedate-end').datepicker({
		    format: 'dd/mm/yyyy',
			autoclose: true,
		    orientation: 'bottom left'
		}).on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			$(this).attr('data-selectDate', date);

			var fromDate = $('.raised-invoice-discrepancy .invoicedate-start').attr('data-selectDate');

			if ( rm.datepickers.validateFromToDate(date, fromDate) ) {
				$(this).attr('data-value',rm.datepickers.convertDate(date));
				$('#raisedInvoiceDiscrepancyUpdateFilter').attr('disabled', false);
				$(this).css('border', '1px solid #ccc');
			} else {
				$(this).attr('data-value','');
				$('#raisedInvoiceDiscrepancyUpdateFilter').attr('disabled', 'disabled');
				$(this).css('border', '1px solid #f00');
			}
		});

        $('#smart-date').datepicker('setStartDate',eighteenMonthsBefore);
		$('#smart-date').datepicker().on('changeDate',function(e){
			var date = $(this).datepicker('getDate'),
				year = date.getFullYear(),
				month = date.getMonth() + 1,
				day = date.getDate(),
				formattedDate = year+'-'+month+'-'+day;

			angular.element('#smartCtrl').scope().getData(formattedDate, 'specific');
			// angular.element('#smartCtrl').scope().$apply();
		});

		// Order History Page
		$('.order-history .input-daterange').datepicker({
		    format: 'dd/mm/yyyy - DD',
		    orientation: 'bottom left',
		    autoclose: true
		});

		$('#orderDate').datepicker({
		    format: 'D dd/mm/yyyy',
		    orientation: 'bottom left',
		    autoclose: true,
		    container: '.input-calendar-container'
		});

		var todayDate = new Date();
		var threeMonthBefore = new Date();
		threeMonthBefore.setMonth(threeMonthBefore.getMonth() - 3);

		$('.order-history .orderdate-end').datepicker('setDate', todayDate);
		$('.order-history .orderdate-end').attr('data-value',rm.datepickers.convertDate(todayDate));
		$('.order-history .orderdate-start').datepicker('setDate', threeMonthBefore);
		$('.order-history .orderdate-start').datepicker('setStartDate',eighteenMonthsBefore);
		$('.order-history .orderdate-start').attr('data-value',rm.datepickers.convertDate(threeMonthBefore));

		$('.order-history .orderdate-end').datepicker('setStartDate',threeMonthBefore);

		$('.order-history .orderdate-start').datepicker().on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			$(this).attr('data-value',rm.datepickers.convertDate(date));
			$('.order-history .orderdate-end').datepicker('setStartDate',date);
			rm.responsivetable.updateOrderHistory();
		});

		$('.order-history .orderdate-end').datepicker().on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			$(this).attr('data-value',rm.datepickers.convertDate(date));
			rm.responsivetable.updateOrderHistory();
		});

		// Cart Page
		$('.page-cartPage .cart-datepicker').datepicker()
			.on('changeDate',function(e){
				var date = $(this).datepicker('getDate');

				var date2 = $('.global-header .delivery-header-input').datepicker('getDate');
				if(date2 === null || date.valueOf() !== date2.valueOf()){
					console.log('update header date');
					console.log(date);
					$('.global-header .delivery-header-input').datepicker('setDate', date);
				}
			});


		// Header Mobile
		$('.delivery-header-mobile').find('.delivery-header-input').datepicker({
            container: '.mobile-calendar-container'
        }).each(function(){
			$(this).on('changeDate',function(e){
				var date = $(this).datepicker('getDate'),
					dateValue = rm.datepickers.convertDate(date),
				 	prev = $(this).attr('data-prev-value'),
					selectedDeliveryMode = $('input:radio[name=deliverymodes]:checked').val(),
					selectedDeliveryModeAttr = $('input:radio[name=deliverymodes]:checked').attr('data-delivery-mode'),
					selectedCarrier = $('.customer-carriers .select-btn').attr('data-value'),
					defaultDeliveryMode = $('.delivery-modes').attr('data-default-delivery');

				$(this).attr('data-value', dateValue);

				if (dateValue && dateValue !== prev || selectedDeliveryModeAttr !== defaultDeliveryMode) {
					var selectedDeliveryDatePackType = rm.datepickers.getDeliveryDatePackType($(this));

					rm.utilities.loadingMessage($('.loading-message').data('login'),true);
			    	$('body').addClass('loading');

			    	$.ajax({
			    		//sabmStore/en/cart/updateDeliveryDate"
						url:$(this).attr('data-update-url'),
						type:'POST',
						data:{'deliveryDate':dateValue, 'deliveryDatePackType':selectedDeliveryDatePackType},
						success: function() {
							window.location.reload();
						},
						error:function(result) {
							$('body').removeClass('loading');
						}
					});
					$.ajax({
						//sabmStore/en/cart/updateSABMdelivery"
                        url:$(this).attr('data-update-cart-url'),
                        type:'POST',
                        data:{'delmodeCode':selectedDeliveryModeAttr,'carrierCode':selectedCarrier},
                        success: function() {
                             console.log('setting selected data in calendar in cart');
                        },
                        error:function(result) {
                            console.error(result);
                        }
                    });
			    }

			});

			$(this).on('click',function(e){
				if (typeof rm.tagManager.trackTopNavEvent !== 'undefined') {
					rm.tagManager.trackTopNavEvent('Delivery date',$(this).attr('data-update-url'));
				}
            });
		});

        // Header desktop
    	$('.delivery-header-desktop').find('.delivery-header-input').datepicker({container: '#deliveryDatepickerHeader'}).each(function(){
			$(this).on('changeDate',function(e){
				var date = $(this).datepicker('getDate'),
					dateValue = rm.datepickers.convertDate(date),
				 	prev = $(this).attr('data-prev-value'),
					selectedDeliveryMode = $('input:radio[name=deliverymodes]:checked').val(),
					selectedDeliveryModeAttr = $('input:radio[name=deliverymodes]:checked').attr('data-delivery-mode'),
					selectedCarrier = $('.customer-carriers .select-btn').attr('data-value'),
					defaultDeliveryMode = $('.delivery-modes').attr('data-default-delivery');

				$(this).attr('data-value', dateValue);

				if (dateValue && dateValue !== prev || selectedDeliveryModeAttr !== defaultDeliveryMode) {
					var selectedDeliveryDatePackType = rm.datepickers.getDeliveryDatePackType($(this));

					rm.utilities.loadingMessage($('.loading-message').data('login'),true);
			    	$('body').addClass('loading');
			    	$.ajax({
			    		//sabmStore/en/cart/updateDeliveryDate"
						url:$(this).attr('data-update-url'),
						type:'POST',
						data:{'deliveryDate':dateValue, 'deliveryDatePackType':selectedDeliveryDatePackType},
						success: function() {
							window.location.reload();
						},
						error:function(result) {
							$('body').removeClass('loading');
						}
					});
					$.ajax({
						//sabmStore/en/cart/updateSABMdelivery"
                        url:$(this).attr('data-update-cart-url'),
                        type:'POST',
                        data:{'delmodeCode':selectedDeliveryModeAttr,'carrierCode':selectedCarrier},
                        success: function() {
                             console.log('setting selected data in calendar in cart');
                        },
                        error:function(result) {
                            console.error(result);
                        }
                    });
			    }

			});
			$(this).on('click',function(e){
				if (typeof rm.tagManager.trackTopNavEvent !== 'undefined') {
					rm.tagManager.trackTopNavEvent('Delivery date',$(this).attr('data-update-url'));
				}
            });
    	});

        $(document).on('click touchend', '.basic-datepicker', function(){
        	$(this).blur();
        	//$(this).focus();
        });
	},

	validateFromToDate: function (to, from) {

		var toDate = new Date(to);
		var fromDate = new Date(from);

		if ( toDate >= fromDate ) {
			return true;
		} else {
			return false;
		}

	},

	getDeliveryDatePackType: function(obj) {
		return obj.hasClass('pack') ? 'PACK' : $(obj).hasClass('keg') ? 'KEG' : $(obj).hasClass('pack-keg') ? 'PACK_KEG' : '';
	},

	convertDate: function(date){
		if(date !== null) {
			var d = '0' + date.getDate(),
				m = '0' + (date.getMonth() + 1),
				y = date.getFullYear(),
				newDate = y+m.slice(-2)+d.slice(-2);

			return newDate;
		} else {
			return '';
		}
	},

	convertDateWithMinus: function(date){
		if(date !== null) {
			var d = '0' + date.getDate(),
				m = '0' + (date.getMonth() + 1),
				y = date.getFullYear(),
				newDate = y + '-' + m.slice(-2) + '-' + d.slice(-2);

			return newDate;
		} else {
			return '';
		}
	},

	updateDeliveryDatePicker: function(){

		var convertDateDDMMYY = function(date){
			if(date !== null) {
				var d = '0' + date.getDate(),
					m = '0' + (date.getMonth() + 1),
					y = date.getFullYear(),
					newDate = d.slice(-2)+'-'+m.slice(-2)+'-'+y.toString().substring(2, 4);
				return newDate;
			} else {
				return '';
			}
		};

		var selectedDate = $('.global-header .delivery-header-input').attr('data-selected-date');

		var startDate = new Date();
		var endDate = new Date(startDate.valueOf() + rm.datepickers.calendarDeltaTime);

		$('.global-header .delivery-header-input').datepicker('setStartDate', startDate);
		$('.global-header .delivery-header-input').datepicker('setEndDate', endDate);

		$('.mobile-head .delivery-header-input').datepicker('setStartDate', startDate);
		$('.mobile-head .delivery-header-input').datepicker('setEndDate', endDate);

		$('.page-cartPage .cart-datepicker').datepicker('setStartDate', startDate);
		$('.page-cartPage .cart-datepicker').datepicker('setEndDate', endDate);

		if(selectedDate) {
			//remove HHMMSS to pass end date check
			//var tempDate = new Date(parseInt($('.global-header .delivery-header-input').attr('data-selected-date')));
			//var date = new Date(tempDate.getFullYear(),tempDate.getMonth(),tempDate.getDate());
			var dateString = $('.global-header .delivery-header-input').attr('data-selected-date');
			var date = $.datepicker.parseDate('d/m/yy', dateString);
			$('.global-header .delivery-header-input').attr('data-prev-value', rm.datepickers.convertDate(date));
			$('.mobile-head .delivery-header-input').attr('data-prev-value', rm.datepickers.convertDate(date));

			$('.global-header .delivery-header-input').datepicker('setDate', date);
			$('.page-cartPage .cart-datepicker').datepicker('setDate', date);
			$('.mobile-head .delivery-header-input').datepicker('setDate', date);
		}
	},

	dealDeliveryDatePicker: function() {
		var convertDateYYMMDD = function(date){
			if(date !== null) {
				var d = '0' + date.getDate(),
					m = '0' + (date.getMonth() + 1),
					y = date.getFullYear(),
					newDate = y.toString()+'-'+m.slice(-2)+'-'+d.slice(-2);

				return newDate;
			} else {
				return '';
			}
		};

		var startDate = new Date();
		var endDate = new Date(startDate.valueOf() + rm.datepickers.calendarDeltaTime);

		$('.calendars #datepicker-specific-day').datepicker('setStartDate', startDate);
		$('.calendars #datepicker-specific-day').datepicker('setEndDate', endDate);

		$('.calendars #date-range-from').datepicker('setStartDate', startDate);
		$('.calendars #date-range-from').datepicker('setEndDate', endDate);

		$('.calendars #date-range-to').datepicker('setStartDate', startDate);
		$('.calendars #date-range-to').datepicker('setEndDate', endDate);

		var disabledDates = $('.calendars #datepicker-specific-day').attr('data-disabled-dates');
		if(disabledDates) {
			var disabledDatesArray = JSON.parse(disabledDates);
			var disabledDatesObj = [];

			for(var i=0, len = disabledDatesArray.length; i<len; i++){
				disabledDatesObj[i] =  convertDateYYMMDD(new Date(parseInt(disabledDatesArray[i])));
			}
			$('.calendars #datepicker-specific-day').datepicker('setDatesDisabled', disabledDatesObj);
		}
	},

	/*
	* 	Get json data from DOM
	*	@author: nolan.b.trazo@accenture.com
	*/
	getDeliveryData: function() {
		// check if #deliveryDatesData json exist

		if ($('#deliveryDatesData').length > 0) {
			var deliveryData = JSON.parse($('#deliveryDatesData').html());
			if (typeof deliveryData !== 'undefined' && deliveryData!==null && deliveryData.length !== 0) {
				return deliveryData;
			} else {
				console.log('Delivery data not found!');
			}
		} else {
			console.log('#deliveryDatesData not exist');
		}
	},
	/*
	* 	Get public holidays data from DOM
	*	@author: lester.l.gabriel
	*/
	getPublicHolidaysData: function() {
		// check if #publicHolidayData json exist
		if($('#publicHolidayData').html() !== 'null'){
			if ($('#publicHolidayData').length > 0) {
				var publicHolidayData = JSON.parse($('#publicHolidayData').html());
				if (typeof publicHolidayData !== 'undefined' && publicHolidayData!==null && publicHolidayData.length !== 0) {
					return publicHolidayData;
				} else {
					console.log('publicHolidayData not found!');
				}
			} else {
				console.log('publicHolidayData not exist');
			}
		}
	},


	/*
	* 	Populate delivery modes and customer carrier
	*	@author: nolan.b.trazo@accenture.com
	*/
	calendarDeliveryModes: function() {
		var data = rm.datepickers.getDeliveryData(),
			customerStatus = '',
			cubStatus = '',
			selectedCus = '',
			selectedCUB = '',
			disabledCarrier = '',
			hideCarrier = '',
			defaultDeliveryMode = '';

		// check if data is defined
		if (typeof data !== 'undefined') {
			if (data.customerArrangedEnabled === true) {
				customerStatus = 'active';
			}

			if (data.cubArrangedEnabled === true && data.customerArrangedEnabled === true) {
				cubStatus = 'active';
			}

            if (data.cubArrangedEnabled === false) {
                $('.cart-deliverymethod .cub-arranged-block').hide();
            }

			if (data.customerOwned === true) {
				selectedCus = 'checked';
				defaultDeliveryMode = 'Customer-Arranged-Delivery';
			} else {
				selectedCUB = 'checked';
				hideCarrier = 'hide';
				defaultDeliveryMode = 'CUB-Arranged-Delivery';
			}

			if (data.shippingCarriers && data.shippingCarriers.length > 0) {
				var carriers = [],
					showCarriers = 'disabled';
				var multipleCarrierClass='';

				if (data.shippingCarriers && data.shippingCarriers.length !== 1) {
					for (var i = 0; i < data.shippingCarriers.length; i++) {
						if (data.shippingCarriers[i].customerOwned === true) {
							carriers[i] = '<li data-value="'+data.shippingCarriers[i].code+'" onclick="rm.datepickers.saveShippingCarrier(&apos;'+data.shippingCarriers[i].code+'&apos;,&apos;'+data.shippingCarriers[i].description+'&apos;);">'+data.shippingCarriers[i].description+'</li>';
						}
					}
					if (data.customerOwned === true) {
						showCarriers = 'active';
					} else {
						showCarriers = '';
					}
				} else if (data.shippingCarriers && data.shippingCarriers.length === 1) {
					disabledCarrier = 'disabled';
					if (cubStatus !== 'active') {
						customerStatus = '';
					}
				}

				var template = '<div class="delivery-modes '+customerStatus+'" data-default-delivery="'+defaultDeliveryMode+'">'+
								'<p class="error-msg">Please select dispatch date to proceed or click <span onclick="rm.datepickers.closeCalendarPicker();">cancel</span> to reset</p>'+
								'<p>Select a delivery method:</p>'+
								'<ul>'+
									'<li id="CUBArranged" class=" '+cubStatus+'">'+
										'<input onclick="rm.datepickers.updateCalendarPicker(this.value);rm.datepickers.calendarUnsavedChanges();" type="radio" name="deliverymodes" data-delivery-mode="CUB-Arranged-Delivery" value="CUB_DELIVERY" id="cub-arranged" '+selectedCUB+'><label for="cub-arranged">CUB Arranged</label><div class="radio-button"></div>'+
									'</li>'+
									'<li id="customerArranged" class=" '+customerStatus+'">'+
										'<input onclick="rm.datepickers.updateCalendarPicker(this.value);rm.datepickers.calendarUnsavedChanges();" type="radio" name="deliverymodes" data-delivery-mode="Customer-Arranged-Delivery" value="CUSTOMER_DELIVERY" id="customer-arranged" '+selectedCus+'><label for="customer-arranged">Customer Arranged</label><div class="radio-button"></div>'+
									'</li>'+
								'</ul>'+
								'<div class="select-list customer-carriers '+customerStatus+' '+hideCarrier+'">'+
								'<div data-value="'+data.selectedCarrier.code+'" class="select-btn '+showCarriers+'">'+data.selectedCarrier.description+'</div>'+
				                        '<ul class="select-items dropdown-overflow '+showCarriers+' '+disabledCarrier+'">'+
											carriers.join('')+
										'</ul>'+
									'</div>'+
								'</div>'+
								'</div>';
				return template;
			}
		}
	},

	/*
	* 	Default calendar highlighted days base on dates from JSON
	*	@author: nolan.b.trazo@accenture.com
	*/
	defaultCalendarPicker: function() {
		var data = rm.datepickers.getDeliveryData(),
			publicHolidayData = rm.datepickers.getPublicHolidaysData(),
			clientTimezone = new Date().getTimezoneOffset(),
			kegDatesArr = [],
			packDatesArr = [],
			publicHolidayDatesArr = [],
			packKegDatesArr = [],
			deliveryType,
			cutoffTime,
			packTypesArr = [];

		// check if data is defined
		if (typeof data !== 'undefined') {
			var deliveryDates = data.deliveryDatesData;
			// check if customer has shipping carriers
			if (data.customerOwned === true) {
				deliveryType = 'CUSTOMER_DELIVERY';
			} else {
				deliveryType = 'CUB_DELIVERY';
			}

			if (deliveryDates) {
				for(var i=0; i < deliveryDates.length; i++) {
					if (deliveryDates[i].mode === deliveryType) {
						if (deliveryDates[i].packType === 'KEG') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var k=0; k < deliveryDates[i].dateList.length; k++) {
									kegDatesArr[k] = this.resetTime(deliveryDates[i].dateList[k]);
								}
								packTypesArr.push('KEG');
							}
						}
						if (deliveryDates[i].packType === 'PACK') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var p=0; p < deliveryDates[i].dateList.length; p++) {
									packDatesArr[p] = this.resetTime(deliveryDates[i].dateList[p]);
								}
								packTypesArr.push('PACK');
							}
						}
						if (deliveryDates[i].packType === 'PACK_KEG') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var pk=0; pk < deliveryDates[i].dateList.length; pk++) {
									packKegDatesArr[pk] = this.resetTime(deliveryDates[i].dateList[pk]);
								}
								packTypesArr.push('PACK_KEG');
							}
						}
					}
				}

				//check if publicHolidayData is not undefined
				if(typeof publicHolidayData !== 'undefined'){
					if (publicHolidayData.length > 0) {
						for(var x=0; x < publicHolidayData.length; x++) {
							publicHolidayDatesArr[x] = this.resetTime(publicHolidayData[x]);
						}

						//pass the public holidays array in the bootstrap-datepicker.js
						$('.global-header .delivery-header-input').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('.mobile-head .delivery-header-input').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('.page-cartPage .cart-datepicker').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('#datepicker-specific-day').datepicker('setPublicHolidayDates', publicHolidayDatesArr);

						packTypesArr.push('PUBLIC_HOLIDAY');
					}
				}

				if(typeof $('#cutofftime') !== 'undefined'){
                    cutoffTime = $('#cutofftime').val();
                    //pass the public holidays array in the bootstrap-datepicker.js
                    $('.global-header .delivery-header-input').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('.mobile-head .delivery-header-input').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('.page-cartPage .cart-datepicker').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('#datepicker-specific-day').datepicker('setCutoffTime', this.insertDate(cutoffTime));

                    packTypesArr.push('CUTOFF-TIME');
                }

				// Pass dates to bootstrap-datepicker.js
				$('.global-header .delivery-header-input').datepicker('setKegDates', kegDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setKegDates', kegDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setKegDates', kegDatesArr);
				$('#datepicker-specific-day').datepicker('setKegDates', kegDatesArr);

				$('.global-header .delivery-header-input').datepicker('setPackDates', packDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setPackDates', packDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setPackDates', packDatesArr);
				$('#datepicker-specific-day').datepicker('setPackDates', packDatesArr);

				$('.global-header .delivery-header-input').datepicker('setPackKegDates', packKegDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setPackKegDates', packKegDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setPackKegDates', packKegDatesArr);
				$('#datepicker-specific-day').datepicker('setPackKegDates', packKegDatesArr);

				$('.global-header .delivery-header-input').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('.mobile-head .delivery-header-input').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('#datepicker-specific-day').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
			}
		}
	},

	/*
	* 	Update calendar highlighted days base on dates from JSON
	*	@author: nolan.b.trazo@accenture.com
	*/
	updateCalendarPicker: function(deliveryType) {

		var data = rm.datepickers.getDeliveryData(),
			publicHolidayData = rm.datepickers.getPublicHolidaysData(),
			clientTimezone = new Date().getTimezoneOffset(),
			kegDatesArr = [],
			packDatesArr = [],
			publicHolidayDatesArr = [],
			cutoffTime,
			packKegDatesArr = [],
			packTypesArr = [];

		// check if data is defined
		if (typeof data !== 'undefined') {
			var deliveryDates = data.deliveryDatesData;
			if (deliveryDates) {
				for(var i=0; i < deliveryDates.length; i++) {
					if (deliveryDates[i].mode === deliveryType) {
						if (deliveryDates[i].packType === 'KEG') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var k=0; k < deliveryDates[i].dateList.length; k++) {
									kegDatesArr[k] = this.resetTime(deliveryDates[i].dateList[k]);
								}
								packTypesArr.push('KEG');
							}
						}
						if (deliveryDates[i].packType === 'PACK') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var p=0; p < deliveryDates[i].dateList.length; p++) {
									packDatesArr[p] = this.resetTime(deliveryDates[i].dateList[p]);
								}
								packTypesArr.push('PACK');
							}
						}
						if (deliveryDates[i].packType === 'PACK_KEG') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var pk=0; pk < deliveryDates[i].dateList.length; pk++) {
									packKegDatesArr[pk] = this.resetTime(deliveryDates[i].dateList[pk]);
								}
								packTypesArr.push('PACK_KEG');
							}
						}
					}
				}

				//check if publicHolidayData is not undefined
				if(typeof publicHolidayData !== 'undefined'){
					if (publicHolidayData.length > 0) {
						for(var x=0; x < publicHolidayData.length; x++) {
							publicHolidayDatesArr[x] = this.resetTime(publicHolidayData[x]);
						}

						//pass the public holidays array in the bootstrap-datepicker.js
						$('.global-header .delivery-header-input').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('.mobile-head .delivery-header-input').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('.page-cartPage .cart-datepicker').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('#datepicker-specific-day').datepicker('setPublicHolidayDates', publicHolidayDatesArr);

						packTypesArr.push('PUBLIC_HOLIDAY');
					}
				}

				if(typeof $('#cutofftime') !== 'undefined'){
                    cutoffTime = $('#cutofftime').val();
                    //pass the public holidays array in the bootstrap-datepicker.js
                    $('.global-header .delivery-header-input').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('.mobile-head .delivery-header-input').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('.page-cartPage .cart-datepicker').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('#datepicker-specific-day').datepicker('setCutoffTime', this.insertDate(cutoffTime));

                    packTypesArr.push('CUTOFF-TIME');
                }

				// Pass dates to bootstrap-datepicker.js
				$('.global-header .delivery-header-input').datepicker('setKegDates', kegDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setKegDates', kegDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setKegDates', kegDatesArr);
				$('#datepicker-specific-day').datepicker('setKegDates', kegDatesArr);

				$('.global-header .delivery-header-input').datepicker('setPackDates', packDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setPackDates', packDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setPackDates', packDatesArr);
				$('#datepicker-specific-day').datepicker('setPackDates', packDatesArr);

				$('.global-header .delivery-header-input').datepicker('setPackKegDates', packKegDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setPackKegDates', packKegDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setPackKegDates', packKegDatesArr);
				$('#datepicker-specific-day').datepicker('setPackKegDates', packKegDatesArr);

				$('.global-header .delivery-header-input').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('.mobile-head .delivery-header-input').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('#datepicker-specific-day').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);

				$('.global-header .delivery-header-input').datepicker('removeActiveDay');
				$('.mobile-head .delivery-header-input').datepicker('removeActiveDay');
				$('.page-cartPage .cart-datepicker').datepicker('removeActiveDay');
				$('#datepicker-specific-day').datepicker('removeActiveDay');
			}
		}

		$('.datepicker-days.delivery-calendar tbody').find('td').removeClass('active');
		rm.header.showDatepickerDaysBorderRadius();

	},

	/*
	* 	Trigger function when user clicked delivery modes or customer carrier is change
	*	@author: nolan.b.trazo@accenture.com
	*/
	calendarUnsavedChanges: function() {
		// Add class to determine if there's unsaved changes
		$('.datepicker').addClass('unsaved-changes');
		$('a:not(.navbar-toggle):not(.navbar-close), .highlight-link').unbind('click');

		$('button').attr('disabled', 'disabled');

		$('.delivery-header').addClass('open');

		if($('.delivery-header-desktop').hasClass('open')){
			$('.cart-datepicker').attr('disabled','disabled');
		}

		/* show delivery method error msg when there's unsaved changes */
		$('.delivery-modes .error-msg').addClass('show');
	},

	/*
	* 	Close calendar and reset calendar data when cancel/close(x) is clicked
	*	@author: nolan.b.trazo@accenture.com
	*/
	closeCalendarPicker: function() {
		$('.datepicker').removeClass('unsaved-changes');
		$('a:not(.navbar-toggle):not(.navbar-close), .highlight-link').unbind('click');

		$('input[data-provide="datepicker"], .basic-datepicker, .billing-payment, .cart-datepicker, .delivery-header-input, #datepicker-specific-day, .order-history').datepicker('hide');
		$('.datepicker').remove();

		$('.cart-datepicker').attr('disabled',false);

		$('button').attr('disabled', false);

		// reset to the default calendar data
		rm.datepickers.defaultCalendarPicker();

		$('.delivery-header').removeClass('open');
	},

	/*
	* 	Sync/Show calendar when delivery modes or customer carriers is change in Cart Page
	*	@author: nolan.b.trazo@accenture.com
	*/
	syncCalendarPicker: function(selected) {
		// show calendar in header for desktop and calendar in cart page for mobile
		var windowWidth = $(window).width();
		if (selected === 'deliveryMode') {
			if (windowWidth < 768) {
				$('.page-cartPage .cart-datepicker').datepicker('show');
			} else {
				$('.global-header .delivery-header-input').datepicker('show');
			}
		}

		// Replace the Delivery data to the updated data/Get updated JSON response
		$.ajax({
			//sabmStore/en/cart/updateDeliveryDate"
			url:'/sabmStore/en/view/DeliveryDatepickerComponentController/getUpdatedDeliveryDateConfig',
			type:'GET',
			success: function(data) {
				var updatedDeliveryData = JSON.stringify(data);
				//replace the old data with the updated json response
				$('#deliveryDatesData').empty().append(updatedDeliveryData);
			},
			complete: function() {
				// Reset calendar data after ajax finish to load
				rm.datepickers.defaultCalendarPicker();
				rm.header.showDatepickerDaysBorderRadius();

				// only triggers unsaved notification when delivery mode is change
				if (selected === 'deliveryMode') {
					rm.datepickers.calendarUnsavedChanges();
				}
				// Remove active day
				$('.global-header .delivery-header-input').datepicker('removeActiveDay');
				$('.mobile-head .delivery-header-input').datepicker('removeActiveDay');
				$('.page-cartPage .cart-datepicker').datepicker('removeActiveDay');
				$('#datepicker-specific-day').datepicker('removeActiveDay');
			},
			error:function(result) {
				$('body').removeClass('loading');
			}
		});
	},

	/*
	*	Save customer shipping carrier when carrier is changed
	*	@author: nolan.b.trazo@accenture.com
	*/
	saveShippingCarrier: function(code,desc) {
		if (code && desc) {
			$.ajax({
				url:'/sabmStore/en/cart/updateSABMdelivery',
				type:'POST',
				data:{'delmodeCode':'Customer-Arranged-Delivery','carrierCode':code},
				success: function() {
					console.log('Shipping carrier saved!');
				},
				complete: function() {
					$('#customerArrangedDelivery .select-btn').text(desc);
				},
				error:function(result) {
					console.error(result);
				}
			}).always(function() {
				$('body').removeClass('loading');
			});
		}
	},

	 resetTime: function (timestamp) {
        var options = {
            timeZone: 'Australia/Sydney',
            year: 'numeric',
            month: 'numeric',
            day: 'numeric'
        };
        var localString = new Date(parseInt(timestamp)).toLocaleString("en-AU", options);
        var date = localString.split(',')[0].split('/');
        var year = date[2];
        var month = this.zeroPrefix(date[1]);
        var day = this.zeroPrefix(date[0]);
        return new Date(year + '-' + month + '-' + day);
    },

    zeroPrefix: function (number) {
        return number.toString().length > 1 ? number : '0' + number;
    },

    insertDate: function (str) {
        var data = JSON.parse(str);
        var regex = new RegExp('(^[^\\d]+)\<([\\w\\s\\W]+)\'\>', 'i');
        var div = document.createElement('div');
        var p = document.createElement('p');
        var b = document.createElement('b');
        div.textContent = data.text;
        p.style = data.styling;
        b.textContent = this.converter(data.cutofftime, data.plantcutofftimezone);
        p.append(b);
        div.append(p);
        return div;
    },

    converter: function(date, timezoneOffset) {
        var currentDate = new Date();
        // use Sydney/Melbourne time as reference: Monday 13 Jun 2022 03:10 PM
        // this will be always Sydney/Melbourne timezone
        // and do conversion
        var fullDate = this.getFullDate(date, currentDate.getFullYear());
        return this.getNewDate(fullDate, timezoneOffset);
    },

    getNewDate: function (date, timezoneOffset) {
        // convert date&time based on given timezone offset
        var localeDate = this.addTimezone(date, timezoneOffset);
        var options = { weekday: 'long', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric', hour12: true };
        var formatted = localeDate.toLocaleString(undefined, options).split(',');
        var hour12 = formatted[formatted.length - 1]; // extract am/pm
        formatted[formatted.length - 1] = hour12.toUpperCase(); // upper case 'am/pm'
        return formatted.join(' ');
    },

    addTimezone: function(date, timezoneOffset) {
        return new Date(date + ' GMT' + timezoneOffset);
    },

    getFullDate: function (date, year) {
        return date.replace(/\d\d:\d\d/i, function(match, p1) {
            return year + ' ' + match;
        });
    }
};
