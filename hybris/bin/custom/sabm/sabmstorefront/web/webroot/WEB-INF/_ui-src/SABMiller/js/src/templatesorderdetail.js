/* globals window */

/* globals ACC */
/* globals enquire*/
/* globals ACC */

/*jshint unused:false*/

	'use strict';

rm.templatesOrderDetail = {

	orderCode: $('#orderTemplateCode').val(),

	init: function(){
		rm.utilities.bindtemplateInlineEdit('#templateTitle');
		//for templates order create
		rm.templatesOrderDetail.createListeners();
		ACC.product.addListeners();
		this.enableOrderTemplateDetailDragAndDrop();
	},

	createListeners: function(){
		// Quantity Incrementors
		$('.save-template').on('click',function(){
			if(!$(this).children('button').hasClass('disabled')){
				// rm.templatesOrderDetail.removeAndSaveProduct($(this));
				rm.templatesOrderDetail.updateQuantityOrUnit($(this));
			}
		});
		$('.addToCartForTemplate').on('click',function(){
			if($('.isOutOfStock').val() === 'true'){
				$('#outOfStockPopup h2').empty().append(ACC.outOfStock);
				$('#outOfStockPopup').modal();
				return;
			}
			if(!$(this).children('button').hasClass('disabled')){

		    	if($('.disabled-productPackTypeNotAllowed').length > 0) {
		    	    $('#outOfStockPopup h2').empty().append(ACC.deliveryPackDate);
		    		$('#outOfStockPopup').modal();
		    		return;
		    	}

				// rm.templatesOrderDetail.removeAndSaveProduct($(this));
				rm.templatesOrderDetail.addToCartForTemplate($(this));
			}
		});
		$('#addToCartForTemplateFromPopUp').on('click',function(){
			if(!$(this).children('button').hasClass('disabled')){
				rm.templatesOrderDetail.addToCartForTemplate($(this));
			}
		});

		// Removing a product from template
		$('.removeProductTemplate').on('click',function(e){
			e.preventDefault();
			rm.templatesOrderDetail.removeProduct($(this));
			rm.templatesOrderDetail.enableSaveTemplate();
		});
		// Qty changed
		$('.select-quantity .up, .select-quantity .down, .select-list .select-items > li')
		.on('click touchstart',function() {
			var input = $(this).closest('.row').find('.qty-input'),
				min = input.data('minqty');

			setTimeout(function(){
				if(isNaN(input.val())){
					input.val(min);
				}
			},50);

			if(!$(this).hasClass('disabled')){
				rm.templatesOrderDetail.enableSaveTemplate();
			}
		});

		$('.qty-input')
		.on('keyup',function() {
			var that = $(this),
				min = that.data('minqty');
			setTimeout(function(){
				if(that.val() === ''){
					that.val(min);
				}

				rm.templatesOrderDetail.enableSaveTemplate();
			},2000);
		});


		$('.select-quantity .up, .select-quantity .down, .select-list .select-items > li .qty-counter')
		.on('input oninput change keyup mousemove click focus mousedown', function() {
			var values = $.map($('.qty-counter'), function(item) {
				return parseInt( $(item).val() );
			 });
			console.log(values);
			var sum = values.reduce(function (a, b) {
				return a + b;
			});
			console.log(sum);

 			$('.template-actions .addToCartForTemplate').prop('disabled', sum === 0);
        });

		// Title change
		$('#templateTitleInput').change(function() {
			rm.templatesOrderDetail.enableSaveTemplate();
		});

        // Minimum Stock on Hand
        $('.minSOH').on('keyup',function() {
            var $this = $(this);

            setTimeout(function() {
               rm.templatesOrderDetail.updateMinSOH($this);
            }, 2000);
        });
	},

	removeProduct: function(obj){
		$('body').addClass('loading');
		var entryNumber = obj.closest('.table-row').find('.entryNumber').val();
		if($('#'+entryNumber+'_isOutOfStock').val() === 'true'){
			$('#'+entryNumber+'_isOutOfStock').val('false');
		}
		obj.closest('.table-row').hide();
//		obj.closest('.table-row').removeClass('entryNumber');
		var templateEntryNumber = $('#removeTemplateEntryNumbers').val();
		if(templateEntryNumber === '' || templateEntryNumber === null){
			$('#removeTemplateEntryNumbers').val(entryNumber);
		}else{
			$('#removeTemplateEntryNumbers').val(templateEntryNumber + ',' + entryNumber);
		}

		$('body').removeClass('loading');

	},

	enableSaveTemplate: function(){


        $(window).on('beforeunload', function() {
            return 'You have unsaved changes!';
        });

		$('.save-template').removeClass('disabled');
		$('.template-actions').addClass('disabled');
		$('.template-actions .hrefAddtoCart').addClass('notActive');
	},

	disableSaveTemplate: function(){

		console.log('disableSaveTemplate');

		$(window).off('beforeunload');

		$('.save-template').addClass('disabled');
		$('.template-actions').removeClass('disabled');
		$('.template-actions .hrefAddtoCart').removeClass('notActive');
	},

	//update the quantity or Unit by ajax
	updateQuantityOrUnit: function(obj){
		$('body').addClass('loading');
		var entryNumber = obj.closest('.table-row').find('.entryNumber').val();
		var qty = obj.closest('.table-row').find('.qty-input').val();
		var unit = obj.closest('.table-row').find('.select-btn').attr('data-value');
		var entryNumberForRemove = $('#removeTemplateEntryNumbers').val();
		var dataPost = {'code': rm.templatesOrderDetail.orderCode,
						'name': $('#templateTitle').text(),
						'entries': [],
						'entryNumber': entryNumberForRemove
						};

		$('.table-row').each(function () {
			if($(this).css('display') !== 'none'){
				var entryNumber = $(this).find('.entryNumber').val();
				var qty = $(this).find('.qty-input').val();
				var unit = $(this).find('.select-btn').attr('data-value');
				var entry = {};
				entry.entryNumber = entryNumber;
				entry.quantity = qty;
				entry.unit = unit;
				dataPost.entries.push(entry);
			}

		});

		$('#globalMessages .succesSavingTemplate').hide();
		$('#globalMessages .errorSavingTemplate').hide();

		$.ajax({
			url:'/your-business/orderTemplateDetail/updateTemplate',
			type:'POST',
			dataType: 'json',
			data: JSON.stringify(dataPost),
            contentType: 'application/json',
			success: function(result) {
				console.log(result);
				if(result) {
					var allSequence = [];
					$('.table-row').each(function () {
						if($(this).css('display') !== 'none'){
							var sequenceNumber = $(this).find('.sequenceNumber').val();
							allSequence.push(sequenceNumber);
						}
					});
					allSequence.sort(function(a, b){return a-b;});
					jQuery.each(allSequence, function(index, item) {
					    $('.table-row').each(function () {
							if($(this).css('display') !== 'none'){
								var sequenceNumber = $(this).find('.sequenceNumber').val();
								if(sequenceNumber === item){
									$(this).find('.sequenceNumber').val(index+1);
								}
							}
						});
					});
					$('#globalMessages').html($('#succesSavingTemplate').html());
					$('#globalMessages .succesSavingTemplate').show();
				} else {
					$('#globalMessages').html($('#errorSavingTemplate').html());
					$('#globalMessages .errorSavingTemplate').show();
				}
				$('body').removeClass('loading');
				rm.templatesOrderDetail.disableSaveTemplate();
			},
			error:function(result) {
				$('#errorSavingTemplate').show();
				$('body').removeClass('loading');
			}

		});
		$('#removeTemplateEntryNumbers').val('');
	},

	//Add to order
	addToCartForTemplate: function(obj){
		var entryNumber = obj.closest('.table-row').find('.entryNumber').val();
		var qty = obj.closest('.table-row').find('.qty-input').val();
		var unit = obj.closest('.table-row').find('.select-btn').attr('data-value');
		var entryNumberForRemove = $('#removeTemplateEntryNumbers').val();
		var dataPost = {'code': rm.templatesOrderDetail.orderCode,
						'name': $('#templateTitle').text(),
						'entries': [],
						'entryNumber': entryNumberForRemove
						};

		$('.table-row').each(function () {
			if($(this).css('display') !== 'none'){
				var entryNumber = $(this).find('.entryNumber').val();
				var qty = $(this).find('.qty-input').val();
				var unit = $(this).find('.select-btn').attr('data-value');
				var entry = {};

				if($('#'+entryNumber+'_isOutOfStock').val() !== 'true' && !$(this).hasClass('disabled-productPackTypeNotAllowed')){
				entry.entryNumber = entryNumber;
				entry.quantity = qty;
				entry.unit = unit;
				dataPost.entries.push(entry);

				}
			}
		});

		$('#globalMessages .succesSavingTemplate').hide();
		$('#globalMessages .errorSavingTemplate').hide();

		$.ajax({
			url:'/your-business/orderTemplateDetail/addToCart/' + rm.templatesOrderDetail.orderCode,
			type:'POST',
			dataType: 'json',
			data: JSON.stringify(dataPost),
            contentType: 'application/json',
			success: function(result) {

				if(result.addToCartForErrorLayer){
					$('#globalMessages').empty();
					$('#globalMessages').append(result.addToCartForErrorLayer);
				}else{
					$('#globalMessages').empty();
				}
				if (result) {
					ACC.product.displayAddToCartPopup(result);
					ACC.minicart.refreshMiniCartCount();
					ACC.common.refreshScreenReaderBuffer();
				}
                if (result.hasOwnProperty('orderTemplateMaxOrderError')) {
                    var row = $('.page-orderTemplateDetail .table-row');
                    for(var i = 0; i < row.length; i ++) {
                        var id = row[i].dataset.productCode;
                        var text = '';
                        for (var j = 0; j < result.orderTemplateMaxOrderError.length; j ++) {
                            if (result.orderTemplateMaxOrderError[j][id]) {
                                text = result.orderTemplateMaxOrderError[j][id]
                            }
                        }
                        $('.page-orderTemplateDetail .order-error-message-' + id).text(text);
                    }
                }
			},
			error:function(result) {
				$('#errorSavingTemplate').show();
				$('body').removeClass('loading');
			}

		});
		$('#removeTemplateEntryNumbers').val('');
	},
    updateMinSOH: function(obj) {
        var entryNumber = obj.closest('.table-row').find('.entryNumber').val();
        var minStockOnHand = obj.val();

        $.ajax({
            url:'/your-business/orderTemplateDetail/updateMinStock',
            type:'POST',
            data:{
                orderCode: rm.templatesOrderDetail.orderCode,
                entryNumber: entryNumber,
                minStockOnHand: minStockOnHand
            },
            success: function(result) {
                console.log('Successfully updated Min SOH to', minStockOnHand);
            },
            error: function(result) {
               console.error('Error updating Minimum Stock On Hand!');
            }
        });
    },

    moveOrderTemplateDetail: function(orderCode, entryNumber, newEntryNum){
    	$('body').addClass('loading');
    	$.ajax({
			url:'/your-business/orderTemplateDetail/move',
			type:'POST',
			data:{
				orderCode: orderCode,
				entryNumber: entryNumber,
				newEntryNum: newEntryNum
			},
			success: function(result) {
				// $('.templates-order-table').html($('.templates-order-table', result).html());
				// rm.templatesOrderDetail.init();
				$('body').removeClass('loading');
			},
			error: function(result) {
				$('body').removeClass('loading');
			}
		});
	},

	enableOrderTemplateDetailDragAndDrop: function () {
		var previousIndex = 0;
		$('.templates-order-table').sortable({
			items: 'div.table-row',
			start: function (event, ui) {
				previousIndex = ui.item.index();
			},
			update: function(event, ui) {
				var orderCode = ui.item.data('order-code');
				var entryNumber = ui.item.find('.sequenceNumber').val();
				var index = ui.item.index();
				var isDraggedUp = index < previousIndex;
				var replacedRowIndex = index + (isDraggedUp ? 1 : - 1);
				var newEntryNum = $($('.templates-order-table > div').get(replacedRowIndex))
					.find('.sequenceNumber').val();
				var parseEntryNumber = parseInt(entryNumber);
				var parseNewEntryNum = parseInt(newEntryNum);
				var isUp = parseEntryNumber > parseNewEntryNum;
				var newSequence = isUp ? 1 : -1;
				$('.table-row').each(function () {
					var indexEntryNo = parseInt($(this).find('.sequenceNumber').val());
					if($(this).css('display') !== 'none'){
						if (indexEntryNo === parseEntryNumber)
						{
							$(this).find('.sequenceNumber').val(parseNewEntryNum);
						}
						else if ((indexEntryNo > parseEntryNumber && indexEntryNo <= parseNewEntryNum) || (indexEntryNo >= parseNewEntryNum && indexEntryNo < parseEntryNumber))
						{
							var tempSequenceNumber = indexEntryNo + parseInt(newSequence);
							$(this).find('.sequenceNumber').val(tempSequenceNumber);
						}
					}
				});
				console.log('update: ', orderCode, isDraggedUp, entryNumber, newEntryNum);

				rm.templatesOrderDetail.moveOrderTemplateDetail(orderCode, entryNumber, newEntryNum);
			}
		});
	},
};
