/*jshint unused:false*/
/* globals ACC */
/* globals window */
/* globals invoiceSelectedList */
/* globals trackCart */
/* globals ProductForTrackCart */


'use strict';

rm.responsivetable = {

    //Initiate the functions required by this particular footable
    init: function () {
        this.sortable = $('.sortable');

        if($('#showMSFPopup').length <= 0) {
        	rm.utilities.removeItemFromStorage('invoices');
        }

        this.orderLimitSort();
        this.initTable();
        this.filterListener();
        this.emailInvoices();
        this.sortingAPI(this.sortable);
        this.saveOrderTemplate();

        if($('.page-customerSearchResults').length){
          this.bdePortal();
        }
    },

    initTable: function () {
       this.orderAddtoCartButton($('#order-detail-inputID'));

       $('#addToCartForTemplateFromPopUp').on('click',function(){
	       	rm.responsivetable.orderItemsAddtoCart($('#order-detail-inputID').val());
	   	});
       
        //If we can find a sortableclass then we know we have to make a sortable table other wise there is no need doing all the js stuff required
        if (this.sortable.length <= 0) {
            return false;
        }

        //We know we have a sortable item but what type of table is required. If this is a table that loads in json it need to run along and do that first before it initiates
        if (this.sortable.hasClass('sortable-json')) {
            this.readTableData(this.sortable);
        }

        this.createClearTableListener(this.sortable);

        this.selectedTotal();
        this.numRows();
        this.enableEmailButton();

        if ($('.billing-options').length) {
            this.inCredit();
            $('.billing-options input').change(function () {
                rm.responsivetable.inCredit();
            });
        }

        this.sortable.footable({
            breakpoints: {
                phone: 480,
                tablet: 768,
                desktop: 990
            }
        }).bind({
            'footable_paging': function (e) {
                rm.responsivetable.clearSelected();
            }
        });

        if (this.sortable.hasClass('order-table')) {
            //get the footable sort object
            var footableSort = $('.footable').data('footable-sort');
            //sorting the table by Date Descending
            footableSort.doSort(1, false);
        }

        if($('#showMSFPopup').length > 0) {
        	var arr = rm.utilities.getArrayFromStorage('invoices');

        	if(arr !== null && arr !== undefined) {
	        	for (var index = 0; index < arr.length; ++index) {
	        	    $('#'+arr[index]).click();
	        	}
	            $('label[for="selectedAmount"]').trigger('click');
        	}
        }
    },

    //Go read json and get all the info that this table needs
    readTableData: function (_sortable) {

        var that = this, //used to get around this binding incorrectly to the each loop
            json = this.ajaxGetData(_sortable);

        if(json !== null)
        {
        	// Show number of orders on Order History page
            $('.numItems span').html(json.length);

            if(json.length > 0){
            	$('#noDataError').hide();
            } else {
            	$('#noDataError').show();
            }

            //will display more transactions message if transactions is over 500
            var status=$('.select-btn:not(.sort):not(.header)').attr('data-value');

            //will not show message for "open transactions"
            if(json.length >= 500 && (status==='A' || status==='')){
            	$('#moreTransactions').show();
            }

            // Use 'Order' if only one order
            if(json.length === 1){
                $('.numItem').show();
                $('.numItems').hide();
            } else {
                $('.numItems').show();
                $('.numItem').hide();
            }

            var $bodyTable = _sortable.find('tbody');
            var openAmount = 0;

            var createRow;

        	if(_sortable.hasClass('billing-table')){
        		createRow = that.createRowBillings;
        	}else if(_sortable.hasClass('order-table')){
        		createRow = that.createRowOrders;
        	}

            $.each(json, function(index, item) {
            	var row = createRow(item);

            	$bodyTable.append(row);

                /*if(item.status === 'Open' && item.openAmount !== '') {
                	openAmount += parseFloat(item.openAmount.replace(/\$|,/g, ''));
                }*/
            });
            if(json.hasOwnProperty('__OpenBalance')){
                openAmount = parseFloat(json.__OpenBalance);
                openAmount = isNaN(openAmount) ? 0 : openAmount;
            }

            $('#openBalanceValue').html(rm.utilities.convertDollar(openAmount.toFixed(2)));
        }

        $('body').removeClass('loading');
    },
    orderLimitSort: function (){

        $('.orderLimitBody').each(function(){
            console.log($(this)[0].innerHTML);
            if(!$(this)[0].innerHTML){
                console.log('no text');
            } else {
                console.log('text');
            }
            // console.log( === '');
            // console.log($(this).text());
        });
    },

    ajaxGetData: function (_sortable) {
        var $selectBtn = $('.select-btn:not(.sort):not(.header)');
        var requestData = {
            lineItem: $selectBtn.attr('data-value'),
            forUnit: $('.select-btn[id="forUnit"]').attr('data-value'),
            startDate: $('.form-control[name="start"]').attr('data-value'),
            endDate: $('.form-control[name="end"]').attr('data-value'),
            type: $('.select-btn[id="type"]').attr('data-value')
        };
        var json = null,
            tableDataURL = _sortable.data('url');

        $.ajax({
            'async': false,
            'global': false,
            'url': tableDataURL,
            'data': requestData,
            'method': 'POST',
            'dataType': 'json',
            'success': function (data) {
                //json = data; old feature
                //new feature for SABMC-1091
            	if(_sortable.hasClass('billing-table')){
            		json = data.invoices || [];
                    json.__OpenBalance=data.openBalance;
            	}else{
            		json = data;
            	}
            },
            'error': function () {
                $('#noDataError').show();
                $('.num-rows').hide();
            }
        });
        return json;
    },

    //Go ahead and create the rows required by looping through the json
    createRowBillings: function (item) {
        var row = '<tr><td><div class="checkbox"><input id="' + item.invoiceNumber + '" type="checkbox" name="billing" value="' + item.invoiceNumber + '" data-status="' + item.status + '"><label for="' + item.invoiceNumber + '"></label></div></td><td>' + item.invoiceNumber + '</td><td class="openAmount text-right">' + rm.utilities.convertDollar(item.openAmount) + '</td><td class="footable-po">' + item.purchaseOrderNumber + '</td><td data-value="' + item.transactionDateStamp + '">' + item.transactionDate + '</td><td data-value="' + item.dueDateStamp + '">' + item.dueDate + '</td><td>' + item.branch + '</td><td>' + item.status + '</td><td>' + item.type + '</td><td>' + item.orderNumber + '</td><td>';

    	if(item.printable === true) {
        	row += '<a class="inline" href="billing/invoice/pdf/' + item.invoiceNumber + '" target="_blank">View</a>';
        }
        row += '</td></tr>';

        return $(row);
    },

    createRowOrders: function (item) {
        var row = '',
            actionText = $('#tableActionText').data('text'),
            orderNo = '\'' + item.orderNo + '\'';
        var status = $('#order-status-'+item.status.toLowerCase()).html();
        if(actionText !== undefined)
        	{
        		row = '<tr><td><a href="order/' + item.orderNo + '" class="inline">' + item.sapOrderNo + '</a></td><td data-value="' + item.dateStamp + '">' + item.date + '</td><td data-value="' + item.deliveryDateStamp + '">' + item.deliveryDate + '</td><td><span class="status status-' + item.status.toLowerCase() + '"></span>'+status+'</td><td><a class="btn btn-primary btn-small bde-view-only" onclick="rm.responsivetable.orderAddtoCart('+ orderNo +')"  href="javascript:void(0);">' + actionText + '</a></tr>';
        	}
        else
        	{
        	    row = '<tr><td><a href="order/' + item.orderNo + '" class="inline">' + item.sapOrderNo + '</a></td><td data-value="' + item.dateStamp + '">' + item.date + '</td><td data-value="' + item.deliveryDateStamp + '">' + item.deliveryDate + '</td><td><span class="status status-' + item.status.toLowerCase() + '"></span>'+status+'</tr>';
        	}
        return $(row);
    },

    enableEmailButton: function () {

        $('input[name=billing]').change(function () {
            var selectedDocNos = $('input[name=billing]:checked').map(function () {
                return $(this).val();
            }).get();

            if (selectedDocNos !== '' && selectedDocNos.length > 0) {
                $('#email-invoices').removeClass('disabled');
            } else {
                $('#email-invoices').addClass('disabled');
            }
        });
    },
    emailInvoices: function () {
        $('#email-invoices').click(function () {

        	if($('.view-only-mode').length) {
    			return;
    		}
            var selectedDocNos = $('input[name=billing]:checked').map(function () {
                return $(this).val();
            }).get();
            $.ajax({
                url: $(this).attr('data-url'),
                type: 'POST',
                data: 'docNumList=' + selectedDocNos,
                cache: false,
                success: function (result) {
                    console.log(result);
                },
            });

            $.magnificPopup.open({
            	items: {
                	src: '#mail-invoices-success'
                },
                type: 'inline',
                closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>'
		    });
        });
    },

    orderAddtoCart: function (orderNo) {
    	
    	if($('.view-only-mode').length){
			return;
		}


        $.ajax({
        	url: $('.orderAddToCartUrl').val(),
            type: 'POST',
            data: {'orderCode':orderNo},
            cache: false,
            success: function (result) {
                if (result.addToCartForErrorLayer) {
                    $('#globalMessages').empty();
                    $('#globalMessages').append(result.addToCartForErrorLayer);
                }else if(result.excludedError){
                	$('#outOfStockPopup h2').empty().append(result.excludedError);
                	$('#outOfStockPopup').modal(); 
                } else {
                    $('#globalMessages').empty();
                }
                if (result) {
                	var miniCartCountBeforeRefresh = $('.miniCart .count').text();
                	
                    ACC.product.displayAddToCartPopup(result);
                    ACC.minicart.refreshMiniCartCount();
                    
                    var miniCartCountAfterRefresh = $('.miniCart .count').text();
                    if (miniCartCountBeforeRefresh === miniCartCountAfterRefresh) {
						$('#addToCartLayer').hide();
					}
					
                    ACC.common.refreshScreenReaderBuffer();
                }
            },
            error: function (result) {
                $('#globalMessages').append(result);
                ACC.common.refreshScreenReaderBuffer();
            }
        });
    },

    orderItemsAddtoCart: function(orderNo) {
    	if($('.view-only-mode').length){
			return;
		}
		
    	var entryNumbers = '';
    	
    	$('.table-row').each(function () {
    		if($(this).css('display') !== 'none'){
				if (!$(this).hasClass('disabled-productPackTypeNotAllowed')) {
					var entryNumber = $(this).find('.entryNumber').val();
					if (entryNumbers === '') {
						entryNumbers = entryNumber;
					} else {
						entryNumbers += ',' + entryNumber;
					}
				}
			}
    	});
    	
    	$.ajax({
        	url: $('.orderItemsAddToCartUrl').val(),
            type: 'POST',
            data: {'orderCode':orderNo, 'entries':entryNumbers},
            cache: false,
            success: function (result) {
                if (result.addToCartForErrorLayer) {
                    $('#globalMessages').empty();
                    $('#globalMessages').append(result.addToCartForErrorLayer);
                } else {
                    $('#globalMessages').empty();
                }
                if (result) {
                    ACC.product.displayAddToCartPopup(result);
                    ACC.minicart.refreshMiniCartCount();
                    ACC.common.refreshScreenReaderBuffer();
                }
            },
            error: function (result) {
                $('#globalMessages').append(result);
                ACC.common.refreshScreenReaderBuffer();
            }
        });
    },
    
    orderAddtoCartButton: function (orderNo) {
        $('#order-detail-button-id1,#order-detail-button-id2').on('click', function () {
        
	       /* if($('.disabled-productPackTypeNotAllowed').length > 0) {
	    	    $('#outOfStockPopup h2').empty().append(ACC.deliveryPackDate);
	    		$('#outOfStockPopup').modal();
	    		return;
	    	}*/
        
            rm.responsivetable.orderAddtoCart(orderNo.val());
        });
    },

    //create a listener that clears away the previous seach terms
    createClearTableListener: function (_sortable) {

        var clear = $('.clear-filter'),
            filterStatus = $('.filter-status');

        //if there is no clear then there is no need to attach a listener to it
        if (clear.length <= 0) {
            return false;
        }

        clear.click(function (e) {
            e.preventDefault();
            _sortable.trigger('footable_clear_filter');
            filterStatus.val('');
        });
    },

    sortingAPI: function (table) {
        //get the footable sort object
        var footableSort = table.data('footable-sort');

        $('.columnSort').on('click touchstart',function () {

            //get the index we are wanting to sort by
            var index = $(this).data('index');

            //get the sort order
            var ascending = $(this).data('ascending');

            footableSort.doSort(index, ascending);

            // Return to first page after sort
            $('.pagination a').filter('[data-page="first"]').trigger('click');
        });
    },


    checkForFailPayment: function(){
        if(window.location.href.indexOf('declined=true') > -1) {
        	//alert(invoiceSelectedList.length);
        	if(invoiceSelectedList.length){
	            // If payment failed, reselect invoices previously selected.	        	
	            var invoices = invoiceSelectedList.split(',');
	            //alert(invoices);
	            if(invoices.length){
	                $.each(invoices,function(index,value){                	
	                 var id = '#' + value;
	                 $(id).prop('checked',true);
	                });              
	                rm.responsivetable.totalSelectAll();
	                $('#selectedAmount').click();
	                
	            }
        	}
        }
    },

    selectedTotal: function () {
        var total = 0,
            allCheckboxes = $('#seller-table td .checkbox input'),
            selectAll = $('#selectAllBilling'),
            table = $('#seller-table');

        allCheckboxes.change(function () {
            rm.responsivetable.totalSelectAll();
            rm.responsivetable.inCredit();

            if($(this).prop('checked')) {
                rm.utilities.addItemToArrayStorage('invoices', $(this).val());
            } else {
                rm.utilities.removeItemFromArrayStorage('invoices', $(this).val());
            }
        });

        selectAll.change(function () {
            var rows = $('#seller-table tr:visible td .checkbox input');
            rows.prop('checked', $(this).prop('checked'));
            rm.responsivetable.totalSelectAll();
            rm.responsivetable.inCredit();
        });
    },

    totalSelectAll: function () {
        var selected = $('#seller-table tr:visible td .checkbox input'),
            totalCredit = 0,
            totalDebt = 0;

        selected.each(function () {
            var amount = $(this).closest('tr').find('.openAmount'),
                amountVal = amount.html(),
                valToNum = parseFloat(amountVal.replace(/\$|,/g, '')),
                valToNumFixed = rm.utilities.convertDollar(valToNum.toFixed(2)),
                total = 0;

            if ($(this).is(':checked')) {
                if (valToNum < 0) {

                    totalCredit += valToNum;
                } else {
                    totalDebt += valToNum;
                }
            }

            total = totalDebt + totalCredit;

            $('#selectedAmountValue').html(rm.utilities.convertDollar(total.toFixed(2)));
            $('.amountToPay').html(rm.utilities.convertDollar(total.toFixed(2)));
        });

    },

    clearSelected: function () {
        var checkboxes = $('#seller-table .checkbox input');

        checkboxes.prop('checked', false);
        rm.responsivetable.selectedTotal();
    },

    numRows: function () {
        var options = $('.num-rows .option'),
        that = this;

        options.on('click', function () {
            that.sortable.data('page-size', $(this).data('value'));
            $(this).addClass('active').siblings().removeClass('active');
            that.sortable.trigger('footable_redraw');
        });
    },

    filterListener: function () {
        var that = this;
        $('#billingUpdateFilter').on('click', function (e) {
            e.stopPropagation();
            e.preventDefault();
            that.updateTable(that.createRowBillings);
        });
    },

    resetWhenUpdate: function(){
    	$('label[for="openBalance"]').trigger('click');
    	$('#openBalanceValue').html('$0.00');
    	$('#selectedAmountValue').html('$0.00');
    	$('#selectAllBilling').prop('checked', false);
    	$('#email-invoices').addClass('disabled');
    },

    updateTable: function (createRowFunction) {
    	var that = this;
    	$('body').addClass('loading');
    	that.resetWhenUpdate();

    	setTimeout(function()
    	{
            //get the footable object
            var footable = $('.sortable').data('footable');
            var openAmount = 0;
            that.emptyFootable(footable);
            var json = that.ajaxGetData($('.sortable'));
            console.log(footable);
	        if(json !== null)
	        {
		        $.each(json, function(index, item) {
		            var row = createRowFunction(item);
		            //Adding row using footable plugin
		            footable.appendRow(row);
		            /*if(item.status === 'Open' && item.openAmount !== '') {
	                	openAmount += parseFloat(item.openAmount.replace(/\$|,/g, ''));
	                }*/
		        });
                if(json.hasOwnProperty('__OpenBalance')){
                    openAmount = parseFloat(json.__OpenBalance);
                    openAmount = isNaN(openAmount) ? 0 : openAmount;
                }
		        $('#openBalanceValue').html(rm.utilities.convertDollar(openAmount.toFixed(2)));
	        }

	        that.selectedTotal();
	        that.enableEmailButton();

            // Business Unit sub-title to be driven from the Business Unit filter in the billing and payment page
            // if ($('.billing-filters').length>0) {
                console.log($('#forUnit').data('unit'));
                $('#billingBusinessUnit').text($('#forUnit').attr('data-unit'));
            // }

	        $('body').removeClass('loading');
    	}, 50);
    },

    updateOrderHistory: function () {
        this.updateTable(this.createRowOrders);
    },

    emptyFootable: function (footable) {
    	var selected = $('table tbody tr'),
            table = $('.sortable').data('footable');
        //Removing rows body using footable plugin
        selected.each(function () {
        	table.removeRow(this);
        });
    },

    // Disable 'Pay Amount' Button if in credit
    inCredit: function () {
        var selectedRadio = $('.billing-options input[name=amountToPay]:checked'),
            value = selectedRadio.closest('.row').find('span');

        if (parseInt(rm.utilities.convertDollarToString(value.html()), 10) <= 0) {
            $('#payment-modal-trigger').addClass('disabled');
        } else {
            $('#payment-modal-trigger').removeClass('disabled');
            $('.amountToPay').html(value.html());
        }
    },

    customerActive: function (uid, acticeFlag) {
        $('#businessCustomerActive').attr('value', acticeFlag);
        $('#businessCustomerUid').attr('value', uid);
        $('#_sabmCustomerActiveForm').submit();
    },

    customerRemove: function (uid) {
        $('#businessCustomerUid').attr('value', uid);
        $('#_sabmCustomerActiveForm').submit();
    },

    bdePortal: function () {
      var resizeTimer;

      // Hack to always show hidden row
      $(window).on('resize', function(e) {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function() {
          if($('.footable-toggle').length){
            $('.footable-toggle').click();
          }
        }, 250);
      });

      $(window).resize();
    },
    createUser: function () {
        $('#_sabmcreateUserForm').submit();
    },

	saveOrderTemplate: function() {

		$('.saveTemplateBtn').on('click touchstart', function(e){
			e.stopPropagation();
			e.preventDefault();

			if($('#template-name').val().trim() !== '') {
				$.ajax({
		            async: false,
		            global: false,
					url:'/your-business/saveOrderTemplate',
					type:'POST',
					data:{orderName:$('#template-name').val(),orderCode:$('#order-detail-inputID').val()},
		            dataType: 'json',
					success: function(result) {
						$('#template-name').val('');
						$('.magnific-template-order').magnificPopup('close');
						$('#globalMessages').empty();
						if(result){
							$('#globalMessages').append($('#templateSuccess').html());
						}else{
							$('#globalMessages').append($('#templateError').html());
						}
						rm.utilities.goBackTop();
					},
					error:function(result) {
						console.error(result);
						$('#templateError').show();
					}
				});
			}else{
				$('#empty-msg').removeClass('hidden');
			}

		});
	},

	addAddToCartListener: function(theClassName) {
		var startItemId = '';
		$(theClassName).each(function(i, item) {
			/* traps multiple reloads of the same elements - start */
			if (i === 0) {
				startItemId = item.id;
			} else {
				if (item.id === startItemId) {
					return false;
				}
			}
			/* traps multiple reloads of the same elements - end */

			if (typeof rm.tagManager.ProductForTrackCart !== 'undefined') {
				var productObj = new rm.tagManager.ProductForTrackCart(
						$(this).data('currencycode'),
						$(this).data('name'),
						$(this).data('id'),
						$(this).data('price'),
						$(this).data('brand'),
						$(this).data('category'),
						$(this).data('variant'),
						$(this).data('position'),
						$(this).data('dealsflag'),
						$(this).data('quantity'),
						$(this).data('actionfield'));
				
				if (typeof rm.tagManager.trackCart !== 'undefined') {
					rm.tagManager.trackCart(productObj, 'add');
				}
			}
			
		});
	}
};
