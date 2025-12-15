ACC.savedcarts = {

    _autoload: [
        ["bindRestoreSavedCartClick", $(".js-restore-saved-cart").length != 0],
        ["bindDeleteSavedCartLink", $('.js-delete-saved-cart').length != 0],
        ["bindDeleteConfirmLink", $('.js-delete-saved-order-template').length != 0],
        ["bindDeleteOrderTemplateEntryLink", $('.js-ordertemplate_entry_delete').length != 0],
        ["bindSaveCartForm", $(".js-save-cart-link").length != 0 || $(".js-update-saved-cart").length != 0],
        ["bindUpdateUploadingSavedCarts", $(".js-uploading-saved-carts-update").length != 0],
        ["bindDeleteOrderTemplateAllEntriesLink", $(".js-ordertemplate_all_entry_delete").length != 0],
        "bindReorderTemplateButton",
        "bindReorderListingTemplateButton",
        ["bindReorderOrderTemplateLink", $(".js-reorder-order-template").length != 0],
        
        "bindSaveCartSubmit",
        "updateOrderTemplateDOM"
    ],
    
    $savedCartRestoreBtn: {},
    $currentCartName: {},
    
    updateOrderTemplateDOM:function(){
    	String.prototype.includes = function (str) {
    		  var returnValue = false;

    		  if (this.indexOf(str) !== -1) {
    		    returnValue = true;
    		  }

    		  return returnValue;
    		}
        var templatePageURL = ($("#checkPage").val() === "savedCartDetailsPage");

    },

    bindRestoreSavedCartClick: function () {
    	
    		
        $(".js-restore-saved-cart").click(function (event) {
                    
            event.preventDefault();
            var popupTitle = $(this).data('restore-popup-title');
            var cartId = $(this).data('savedcart-id');
            var url = ACC.config.encodedContextPath +'/my-account/saved-carts/'+cartId+'/restore';

            $.get(url).done(function (data) {
                ACC.colorbox.open(popupTitle, {
                    html: data,
                    width: 500,
                    onComplete: function () {
                        ACC.common.refreshScreenReaderBuffer();
                        ACC.savedcarts.bindRestoreModalHandlers();
                        ACC.savedcarts.bindPostRestoreSavedCartLink();
                    },
                    onClosed: function () {
                        ACC.common.refreshScreenReaderBuffer();
                    }
                });
            });
        });
    },

    bindRestoreModalHandlers: function () {

        ACC.savedcarts.$savedCartRestoreBtn= $('.js-save-cart-restore-btn');
        ACC.savedcarts.$currentCartName= $('.js-current-cart-name');

        $(".js-prevent-save-active-cart").on('change', function (event) {
            if($(this).prop('checked') === true){
                ACC.savedcarts.$currentCartName.attr('disabled', 'disabled');
                ACC.savedcarts.$savedCartRestoreBtn.removeAttr('disabled');
            } else {
                ACC.savedcarts.$currentCartName.removeAttr('disabled');
                var inputVal = ACC.savedcarts.$currentCartName.val();
                if (inputVal == "" && inputVal.length === 0) {
                    ACC.savedcarts.$savedCartRestoreBtn.attr('disabled', 'disabled');
                }
            }
        });

        ACC.savedcarts.$currentCartName.on('focus', function (event) {
            $('.js-restore-current-cart-form').removeClass('has-error');
            $('.js-restore-error-container').html('');
        });

        ACC.savedcarts.$currentCartName.on('blur', function (event) {
            if (this.value == "" && this.value.length === 0) {
                ACC.savedcarts.$savedCartRestoreBtn.attr('disabled', 'disabled');
            } else {
                ACC.savedcarts.$savedCartRestoreBtn.removeAttr('disabled');
            }
        });
    },

    bindPostRestoreSavedCartLink: function () {
        var keepRestoredCart = true;
        var preventSaveActiveCart = false;

        $(document).on("click", '.js-keep-restored-cart', function (event) {
            keepRestoredCart = $(this).prop('checked');
        });

        $(document).on("click", '.js-prevent-save-active-cart', function (event) {
            preventSaveActiveCart = $(this).prop('checked');
        });

        $(document).on("click", '.js-save-cart-restore-btn', function (event) {
        	
            event.preventDefault();
            var cartName = $('#activeCartName').val();
            var url = $(this).data('restore-url');
            var postData = {preventSaveActiveCart: preventSaveActiveCart, keepRestoredCart: keepRestoredCart, cartName: cartName};
            $.post(url, postData).done(function (result, data, status) {
                if (result == "200") {
                    var url = ACC.config.encodedContextPath + "/cart"
                    window.location.replace(url);
                } else {
                    var errorMsg = status.responseText.slice(1, -1);
                    $('.js-restore-current-cart-form').addClass('has-error');
                    $('.js-restore-error-container').html(errorMsg);
                    $('.js-savedcart_restore_confirm_modal').colorbox.resize();
                }
            });
        });

        $(document).on("click", '.js-cancel-restore-btn', function (event) {
            ACC.colorbox.close();
        });
    },

    bindDeleteSavedCartLink: function () {
        $(document).on("click", '.js-delete-saved-cart', function (event) {
            event.preventDefault();
            var cartId = $(this).data('savedcart-id');
            var popupTitle = $(this).data('delete-popup-title');

            ACC.colorbox.open(popupTitle, {
                inline: true,
                className: "js-savedcart_delete_confirm_modal",
                href: "#popup_confirm_savedcart_delete_" + cartId,
                width: '500px',
                onComplete: function () {
                    $(this).colorbox.resize();
                }
            });
        });
    },

    bindReorderOrderTemplateLink: function () {
        $(document).on("click", '.js-reorder-order-template', function (event) {
            event.preventDefault();
            var cartId = $(this).data('savedcart-id');
            var popupTitle = $(this).data('delete-popup-title');

            ACC.colorbox.open(popupTitle, {
                inline: true,
                className: "js-ordertemplate_reorder_confirm_modal",
                href: "#popup_confirm_ordertemplate_reorder" + cartId,
                width: '500px',
                onComplete: function () {
                    $(this).colorbox.resize();
                }
            });
        });
    },
    
    bindDeleteConfirmLink: function () {
        $(document).on("click", '.js-delete-saved-order-template', function (event) {
            event.preventDefault();
            var cartId = $(this).data('savedcart-id');
            var url = ACC.config.encodedContextPath + '/my-account/saved-carts/' + cartId + '/deleteOrderTemplate';
            $.ajax({
                url: url,
                type: 'DELETE',
                success: function (response) {
                    ACC.colorbox.close();
                    var url = ACC.config.encodedContextPath + "/my-account/saved-carts"
                    window.location.replace(url);
                }
            });
        });

        $(document).on("click", '.js-savedcart_delete_confirm_cancel', function (event) {
            ACC.colorbox.close();
        });
    },
    
    bindDeleteOrderTemplateEntryLink: function () {
        $(document).on("click", '.js-ordertemplate_entry_delete', function (event) {
            event.preventDefault();
            var templateId = $(this).data('ordertemplate-id');
            var templateEntryPk = $(this).data('ordertemplateentry-id');
            var sort = $(this).data('sort');
            var query = sort ? '?sort=' + sort : '';
            var url = ACC.config.encodedContextPath + '/my-account/saved-carts/' + templateEntryPk + '/deleteOrderTemplateEntry';
            $.ajax({
                url: url,
                type: 'DELETE',
                success: function (response) {
                    ACC.colorbox.close();
                    var url = ACC.config.encodedContextPath + "/my-account/saved-carts/" + templateId + query;
                    window.location.replace(url);
                }
            });
        });
    },
    
    bindDeleteOrderTemplateAllEntriesLink: function () {
        $(document).on("click", '.js-ordertemplate_all_entry_delete', function (event) {
            event.preventDefault();
            var templateId = $(this).data('ordertemplate-id');
            var url = ACC.config.encodedContextPath + '/my-account/saved-carts/' + templateId + '/deleteAllEntriesForOrderTemplate';
            $.ajax({
                url: url,
                type: 'DELETE',
                success: function (response) {
                    ACC.colorbox.close();
                    var url = ACC.config.encodedContextPath + "/my-account/saved-carts/" + templateId
                    window.location.replace(url);
                }
            });
        });
    },
    
    bindSaveCartSubmit: function(){
    	var form = $('#saveCartForm');
    	form.on('submit', function(e){
    		e.preventDefault();
    		 
    		 $.ajax({
                 type: $(this).attr('method'),
                 url: $(this).attr('action'),
                 data: $(this).serialize(),
                 success: function (data) {
                    
                     console.log(data);
                     var successKey = data.successKey;
                     var errorKey = data.errorKey;
                     var showMsg = $('#saveCartShowMsg')
                     if(successKey){
                    	 showMsg.removeClass('save-cart-error');
                    	 showMsg.html(data.message).addClass('save-cart-success');
                    	
                     }
                     else if(errorKey){
                    	 showMsg.html(data.message).addClass('save-cart-error');
                    	
                  	 
                     }
                     $('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
                     $('#saveCartName').val('');
                     
                      
                 },
                 error: function (data) {
                     console.log('An error occurred.');
                     console.log(data);
                 },
             });
    		
    	})
    	
    	
    },
    
    bindSaveCartForm: function ()
	{
    	ACC.savedcarts.charactersLeftInit();
		var form = $('#saveCartForm');
		var saveCart = false;
        var showSaveCartFormCallback = function () {
        var title = $('#saveCart').data("saveCartTitle");
          
            ACC.colorbox.open(title, {
                href: "#saveCart",
                inline: true,
                width: "620px",
                onOpen: function () {
                    if ($('#saveCartName').val()) {
                        ACC.savedcarts.disableSaveCartButton(false);
                    }
                },
                onComplete: function () {
                    $(this).colorbox.resize();
                    saveCart = false;
                },
                onClosed: function () {

                    if (saveCart) {
                        form.submit();
                    }
                    document.getElementById("saveCartForm").reset();
                    ACC.savedcarts.disableSaveCartButton(true);
                    ACC.savedcarts.charactersLeftInit();
                }
            });
        };

        $(document).on("click",".js-save-cart-link, .js-update-saved-cart",function(e){
            e.preventDefault();
            ACC.common.checkAuthenticationStatusBeforeAction(showSaveCartFormCallback);
		});
		
		$(document).on("click",'#saveCart #cancelSaveCartButton', function (e) {
			e.preventDefault();
			$.colorbox.close();
		});
		
		$('#saveCartName').keyup(function() {		
			// enable the save cart button
	 		$('#saveCart #saveCartButton').prop('disabled', this.value.trim() == "" ? true : false);  	
			// limit the text length 
            var maxchars = 255;
			var value=$('#localized_val').attr('value');
			var tlength = $(this).val().length;
			remain = maxchars - parseInt(tlength);
        	$('#remain').text(value+' : '+remain);
		});
		
         $('#saveCartDescription').keyup(function() {
			var maxchars = 255;
			var value=$('#localized_val').attr('value');
			var tlength = $(this).val().length;
			remain = maxchars - parseInt(tlength);
        	$('#remainTextArea').text(value+' : '+remain);
		});
		
		$(document).on("click",'#saveCart #saveCartButton', function (e) {
			e.preventDefault();
			saveCart = true;
			$.colorbox.close();
		});
	},
	
	charactersLeftInit: function() {
	    $('#remain').text($('#localized_val').attr('value')+' : 255');
     	$('#remainTextArea').text($('#localized_val').attr('value')+' : 255');
	},
	
	disableSaveCartButton: function(value) {
		$('#saveCart #saveCartButton').prop('disabled', value);
	},
	

	bindUpdateUploadingSavedCarts : function() {
		var cartIdRowMapping = $(".js-uploading-saved-carts-update").data("idRowMapping");
		var refresh = $(".js-uploading-saved-carts-update").data("refreshCart");
		if (cartIdRowMapping && refresh) {
			var interval = $(".js-uploading-saved-carts-update").data("refreshInterval");
			var arrCartIdAndRow = cartIdRowMapping.split(',');
			var mapCartRow = new Object();
			var cartCodes = [];
			for (i = 0; i < arrCartIdAndRow.length; i++) {
				var arrValue = arrCartIdAndRow[i].split(":");
				if (arrValue != "") {
					mapCartRow[arrValue[0]] = arrValue[1];
					cartCodes.push(arrValue[0]);
				}
			}

			if (cartCodes.length > 0) {
				setTimeout(function() {ACC.savedcarts.refreshWorker(cartCodes, mapCartRow, interval)}, interval);
			}	
		}
	},
	
	bindReorderTemplateButton: function () 
    {   	
		$( document ).ready(function ()
		    	{
		    		$('#keepTemplateProductLayer').modal({
		        	    show: false	
		    		});
		});
		$(document).on("click", '.reorder-order-template-button', function (e) {
    		e.preventDefault();
    		var reorderForm = $(".reorderForm");
    		var orderCode = $(this).data('savedcart-id');
    		var reorderUrl = ACC.config.encodedContextPath + "/history/reorder" + "?orderCode=" + orderCode;
            $.ajax({
    		    type: 'POST',
    		    url: reorderUrl,
    		    dataType: 'json',
    		    success: function (data)
    			{
    		    	if(data == "true")
                	{
    		    		var $hiddenInput = $('<input/>',{type:'hidden',id:"templateCode",value:orderCode});
    		    		$hiddenInput.appendTo('#keepTemplateProductLayer');
                		$('#keepTemplateProductLayer').modal('show');
                		$('#keepTemplateProductLayer').removeClass("cboxElement");
                	}
                	else
                	{
                		 e.preventDefault();
                		 $('input#keepCart').val(true);
                 		var pk = $('#entryPK').val();
                 		
                  		var formTemplateId = "#orderTemplateForm" + orderCode;
                  		$.ajax({
                  		type: $(formTemplateId).attr('method'),
                         url: $(formTemplateId).attr('action'),
                         data: $(formTemplateId).serialize(),
                         success: function (data) {
                         	var url = ACC.config.encodedContextPath + "/cart/"
                             window.location.replace(url);
                             
                         },
                         error: function (data) {
                             console.log('An error occurred.');
                         },
                     }); 
                	}
    		    	
    			},
    		    error: function (xmlHttpRequest, errorText, thrownError) {
    	            console.log("Error: "+ thrownError+" "+errorText);
    	           
    	        },
		  });
    		
    	});
		
    	$(document).on("click", '.clearTemplateCartBtn', function (e) {
    		e.preventDefault();
    		$('input#keepCart').val(false);
    		
    		var templateId =$('#templateCode').val();
    		var pk = $('#entryPK').val();
    		
     		var formTemplateId = "#orderTemplateForm" + templateId;
     		
     		$.ajax({
     		type: $(formTemplateId).attr('method'),
            url: $(formTemplateId).attr('action'),
            data: $(formTemplateId).serialize(),
            success: function (data) {
            	var url = ACC.config.encodedContextPath + "/cart/"
                window.location.replace(url);
                
            },
            error: function (data) {
                console.log('An error occurred.');
            },
        });   
    	});
    	
    	
    	$(document).on("click", '.keepTemplateCartBtn', function (e) {
    		e.preventDefault();
    		
    		$('input#keepCart').val(true);
    		
    		var templateId =$('#templateCode').val();
    		var pk = $('#entryPK').val();
    		
     		var formTemplateId = "#orderTemplateForm" + templateId;
     		
     		$.ajax({
     		type: $(formTemplateId).attr('method'),
            url: $(formTemplateId).attr('action'),
            data: $(formTemplateId).serialize(),
            success: function (data) {
            	var url = ACC.config.encodedContextPath + "/cart/"
                window.location.replace(url);
                
            },
            error: function (data) {
                console.log('An error occurred.');
            },
        }); 
    	
    	});
    	
    	 
    },

    bindSaveOrderTemplate: function (ele) {
        var form = $('form[id*="orderTemplateForm"]');
        var url = ACC.config.encodedContextPath + '/my-account/saved-carts/updateTemplate';
        var index = ele.dataset['index'];
        var success = $('.page-savedCartDetailsPage .save-cart-success.success-' + index);
        var fail = $('.page-savedCartDetailsPage .save-cart-success.fail-' + index);
        $(form).find('input[name=keepCart]').attr('value', true);

       $.ajax({
           type: 'POST',
           url,
           data: $(form).serialize(),
           success: function (res) {
               if (res) {
                    success.removeClass('hidden');
                    fail.addClass('hidden');
                    ele.setAttribute('disabled', true);
               } else {
                    success.addClass('hidden');
                    fail.removeClass('hidden');
                    ele.setAttribute('disabled', true);
               }
           },
           error: function (data) {
               if (data.status !== 200) {
                    success.addClass('hidden');
                    fail.removeClass('hidden');
                    ele.setAttribute('disabled', true);
               }
           }
       });
    },

    onChange: function (ele, index) {
        // desktop
        var save = $('#js-saved-order-template-' + index);
        var inputValue = parseInt($('#templateAddtoCartInput-' + index).val());

        var success = $('.page-savedCartDetailsPage .save-cart-success.success-' + index);
        var fail = $('.page-savedCartDetailsPage .save-cart-success.fail-' + index);

        // Get correct value after increment/decrement for click event.
        // input event will get correct value without adjustment
        if (ele.className.indexOf('minus') !== -1) {
            inputValue -= 1;
        } else if (ele.className.indexOf('plus') !== -1) {
            inputValue += 1;
        }

        // remove disable when quantity is 0
//        if (inputValue === 0) {
//            save.attr('disabled', true);
//        }

        if (save[0].disabled) {
            save.attr('disabled', false);
        }

        // Error Handling
        if (!success.hasClass('hidden')) {
            success.addClass('hidden');
        } else if (!fail.hasClass('hidden')) {
            fail.addClass('hidden');
        }
    },

    bindReorderListingTemplateButton: function () 
    {   
		$( document ).ready(function ()
		    	{
		    		$('#keepTemplateListingProductLayer').modal({
		        	    show: false	
		    		});
		});
		$(document).on("click", '.reorder-order-listing-template-button', function (e) {
    		e.preventDefault();
    		var reorderForm = $(".reorderForm");
    		var orderCode = $(this).data('savedcart-id');
    		var reorderUrl = ACC.config.encodedContextPath + "/history/reorder" + "?orderCode=" + orderCode;
            $.ajax({
    		    type: 'POST',
    		    url: reorderUrl,
    		    dataType: 'json',
    		    success: function (data)
    			{
    		    	if(data == "true")
                	{
    		    		var $hiddenInput = $('<input/>',{type:'hidden',id:"templateCode",value:orderCode});
    		    		$hiddenInput.appendTo('#keepTemplateListingProductLayer');
                		$('#keepTemplateListingProductLayer').modal('show');
                		$('#keepTemplateListingProductLayer').removeClass("cboxElement");
                	}
                	else
                	{
                		 e.preventDefault();
                         var url = ACC.config.encodedContextPath + '/my-account/saved-carts/' + orderCode + '/reorderEntries/overrideTemplate';
                         $.ajax({
                             url: url,
                             type: 'POST',
                             success: function (response) {
                                 ACC.colorbox.close();
                                 var url = ACC.config.encodedContextPath + "/cart/"
                                 window.location.replace(url);
                             }
                         });  
                	}
    		    	
    			},
    		    error: function (xmlHttpRequest, errorText, thrownError) {
    	            console.log("Error: "+ thrownError+" "+errorText);
    	           
    	        },
		  });
    		
    	});
		
		$(document).on("click", '.clearListTemplateCartBtn', function (e) {
   		 e.preventDefault();
   		 var templateId =$('#templateCode').val();
            var url = ACC.config.encodedContextPath + '/my-account/saved-carts/' + templateId + '/reorderEntries/overrideTemplate';
            $.ajax({
                url: url,
                type: 'POST',
                success: function (response) {
                    ACC.colorbox.close();
                    var url = ACC.config.encodedContextPath + "/cart/"
                    window.location.replace(url);
                }
            });           
		});
		
		$(document).on("click", '.keepListTemplateCartBtn', function (e) {
   		 e.preventDefault();
   		 var templateId =$('#templateCode').val();
            var url = ACC.config.encodedContextPath + '/my-account/saved-carts/' + templateId + '/reorderEntries/keepTemplate';
            $.ajax({
                url: url,
                type: 'POST',
                success: function (response) {
                    ACC.colorbox.close();
                    var url = ACC.config.encodedContextPath + "/cart/"
                    window.location.replace(url);
                }
            });
		});
  
    },
    
	refreshWorker : function(cartCodes, mapCartRow, interval) {
		$.ajax({
			dataType : "json",
			url : ACC.config.encodedContextPath	+ '/my-account/saved-carts/uploadingCarts',
			data : {
				cartCodes : cartCodes
			},
			type : "GET",
			traditional : true,
			success : function(data) {
				if (data != undefined) {
					var hidden = "hidden";
					var rowId = "#row-";
					for (i = 0; i < data.length; i++) {
						var cart = data[i];

						var index = $.inArray(cart.code, cartCodes);
						if (index > -1) {
							cartCodes.splice(index, 1)
						}	
						var rowIdIndex = mapCartRow[cart.code]; 
						if (rowIdIndex != undefined) {
							var rowSelector = rowId + rowIdIndex;
							$(rowSelector + " .js-saved-cart-name").removeClass("not-active");
							$(rowSelector + " .js-saved-cart-date").removeClass("hidden");
							$(rowSelector + " .js-file-importing").remove();
							$(rowSelector + " .js-saved-cart-description").text(cart.description);
							var numberOfItems = cart.entries.length;
							$(rowSelector + " .js-saved-cart-number-of-items").text(numberOfItems);
							$(rowSelector + " .js-saved-cart-total").text(cart.totalPrice.formattedValue);
							if (numberOfItems > 0) {
								$(rowSelector + " .js-restore-saved-cart").removeClass(hidden);
							}
							$(rowSelector + " .js-delete-saved-cart").removeClass(hidden);
						}
					}
				};
				
				if (cartCodes.length > 0) {
					setTimeout(function() {ACC.savedcarts.refreshWorker(cartCodes, mapCartRow, interval)}, interval);
				}
			}
		})
	}
}