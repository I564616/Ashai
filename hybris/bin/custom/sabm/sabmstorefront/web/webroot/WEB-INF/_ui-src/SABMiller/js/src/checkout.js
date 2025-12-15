/*jshint unused:false*/
/* globals window */
/* globals validate */
/* globals document */
/* globals trackOnCheckoutOption */
/* globals trackOnCheckout */
/* globals trackCheckoutError */
/*globals MerchantSuite*/
/*globals test*/
'use strict';
    
rm.checkout = {
    init:function() {
        this.payByCard();
        this.checkoutTimer();
        rm.utilities.merchantServiceFee();
		rm.responsivetable.saveOrderTemplate();

		
		var msfData = [], msfData1 = [];
        $('.doCheckoutBut.processButton').on('click', function(e){
        	msfData = []; msfData1 = [];
        	
        	e.preventDefault();

            var step = 3;

            if($('#payByCard').is(':checked') || $('.card-carddetailsOnlca').length){
            	if (typeof rm.tagManager.trackOnCheckout !== 'undefined') {
            		rm.tagManager.trackOnCheckout(step, 'Payment|Pay by Card');
            	}
            	
            	//commented current functionality
            	/* 
                if($('#ccFormId').hasClass('ng-valid')){
                    $('body').addClass('loading');
                    $('#customPONumberId').val($('#poNumber').val());
		        	if(rm.checkout.verifyMonthAndYear()){
                        rm.utilities.loadingMessage($('.loading-message').data('confirm'),true);
                    	$('#ccFormId').submit();
                	}
				} */
            	
                if ( $('#ccFormId').hasClass('ng-valid') ) {
	
	        		msfData.push({
	        			'cardNumber': $('#cardNumber').val(),
	        			'cardHolderName': $('#nameOnCard').val(),
	        			'securityCode': $('#securityCode').val(),
	        			'expiryDateMonth': $('.expiryDateMonth').val(),
	    				'expiryDateYear': ( $('.expiryDateMonth').val() === '99' ) ? $('#expiryDateYearHidden').val() : $('.expiryDateYear').val()
	        		});
	            	
	        		var cardType = $('#cardType').val();
	        		var poNumber = $('#poNumber').val();
	        		
	        		console.log(cardType);
	        		$.magnificPopup.close();
	                rm.utilities.showOverlay(true);
	                
	            	$.ajax({
						url: '/checkout/payByCard',
						data: {'cardType': cardType, 'poNumber':poNumber},
		                dataType: 'json',
		                type: 'POST'
	            	}).done(function(res){
	            		
	            		if( res.error === null) {
	            		
		            		msfData1.push({
		            			'authKey': res.authKey,
		            			'paymentUrl': res.paymentUrl
		            		});
		            		
		            		$.extend(true, msfData, msfData1);
		            		
		            		console.log(msfData);
		            		
		            		//pass data to MSF modal
		            		$('#amount').text('$'+parseFloat(res.displayAmount).toFixed(2));
		            		$('#totalAmount').text('$'+parseFloat(res.displayTotalAmount).toFixed(2));
		            		$('#surcharge').text('$'+parseFloat(res.displaySurcharge).toFixed(2));
		            		$('#msf').text('$'+parseFloat(res.displaySurcharge).toFixed(2));
		            					            		
		                    rm.utilities.showOverlay(false);
		            		rm.utilities.showCheckoutMSFPopup();
	            		} else {
	            			
	                        var checkout = '/checkout?';
	                        window.location.replace(checkout + res.error);
	            		}
	             	}); 
                }
                
            } else {
            	if (typeof rm.tagManager.trackOnCheckout !== 'undefined') {
            		rm.tagManager.trackOnCheckout(step, 'Payment|Pay on Account');
            	}
            	
                $('body').addClass('loading');
                var checkoutUrl = $('.doCheckoutBut.processButton').attr('data-checkout-po-url');
                rm.utilities.loadingMessage($('.loading-checkout').data('confirm'),true);
                window.location = checkoutUrl +'?poNumber=' + $('#poNumber').val();
            }
        });
        
        $('.checkout-msf-popup-button').on('click', function(){
    
    		$.magnificPopup.close();
            rm.utilities.showOverlay(true);
            
            MerchantSuite.BaseUrl = msfData[0].paymentUrl;
            
            console.log(msfData);
            
        	MerchantSuite.ProcessPayment({
        		AuthKey: msfData[0].authKey,
        		CardNumber: msfData[0].cardNumber,
        		CVN: msfData[0].securityCode,
        		ExpiryMonth: msfData[0].expiryDateMonth,
        		ExpiryYear: msfData[0].expiryDateYear,
        		CardHolderName: msfData[0].cardHolderName,
        		CallbackFunction: function(res) {
        			
        			var checkoutUrl = '/checkout?';
        			if ( res.AjaxResponseType === 0 ) {
        				if ( res.ApiResponseCode === 0 ) {
                			window.location.href = res.RedirectionUrl;
            			} else { 
            				window.location.replace(checkoutUrl + 'declined=true');
            			}
        			} else if ( res.AjaxResponseType === 1 ) {
        				window.location.replace(checkoutUrl + 'invalidCard=true');
        			} else if ( res.AjaxResponseType === 2 ) {
        				window.location.replace(checkoutUrl + 'paymentError=true');
        			} else {
        				window.location.replace(checkoutUrl + 'gatewayError=true');
        			}
        		}
        	});
        });
        
        this.clamp();
        this.deliveryModesVisibility();

    },
    
    //update for title wrap
    clamp: function(){
        rm.utilities.needClamp('checkoutClamp-2',2,'clamp-2');
        rm.utilities.needClamp('checkoutClamp-1',1,'clamp-1');
    },

    verifyMonthAndYear:function(){
        var date=new Date();
		var fullYear = date.getFullYear()+'';
		var shortYear = fullYear.substr(2, 2);
		var month=date.getMonth()+1;
		if(shortYear === $('.expiryDateYear').val() && $('.expiryDateMonth').val() && $('.expiryDateMonth').val() < month){
			$('#Invalid_Expiry_Date').removeClass('ng-hide');
			$('.doCheckoutBut').attr('disabled','disabled');
			return false;
		}else if($('.expiryDateMonth').val() && $('.expiryDateYear').val()){
			$('#Invalid_Expiry_Date').addClass('ng-hide');
			$('.doCheckoutBut').removeAttr('disabled');
			return true;
		}
    },

    payByCard: function(){
        var westpacData = JSON.parse($('#westpacResponse').html());

        window.setPaymentCheckoutDisable = 0;
        var that = this;

        rm.billing.setupMonthYearPayment();
        if(!westpacData){
            if($('#payByAccount').is(':checked')){
                $('.doCheckoutBut.continueCheckout').removeAttr('disabled');
                $('.doCheckoutBut.continueCheckout').removeClass('disabled');
            }

            $('.cart-paymentoptions input[type="radio"]').on('change', function(){
            	var step =  $('input[name="checkoutStep"]').val();

                if($('#payByCard').is(':checked')){
                	if (typeof rm.tagManager.trackOnCheckoutOption !== 'undefined') {
                		rm.tagManager.trackOnCheckoutOption(step, 'Payment|Pay by Card');
                	}
                	
                    //that.getCCToken();
                    $('.card-carddetails').slideDown();
                    $('.doCheckoutBut.processButton').attr('data-checkout-url','#');
                } else {
                	if (typeof rm.tagManager.trackOnCheckoutOption !== 'undefined') {
                		rm.tagManager.trackOnCheckoutOption(step, 'Payment|Pay on Account');
                	}
                	
                    $('.card-carddetails').slideUp();
                    $('.doCheckoutBut.processButton').attr('data-checkout-url','checkout/placeOrderByAccount');
                    $('.doCheckoutBut.continueCheckout').removeAttr('disabled');
                    $('.doCheckoutBut.continueCheckout').removeClass('disabled');
                }
            });
        } else {
            if(westpacData.error === null || westpacData.error === undefined){
                $('#ccFormId').attr('action', westpacData.url);
                $('#communityCodeId').attr('value', westpacData.communityCode);
                $('#tokenNumber').attr('value', westpacData.token);
                $('#ignoreDuplicateId').attr('value', westpacData.ignoreDuplicate);
            } else {
                window.errorOnTokenRequest = westpacData.error;
                var cart = '/sabmStore/en/cart?';
                window.location.replace(cart + window.errorOnTokenRequest);
            }
        }
    },

    getCCToken: function(){
        if($('#tokenNumber').val() === null || $('#tokenNumber').val() === '') {
            $('body').addClass('loading');
            $.ajax({
                url: '/checkout/beforePayment',
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                type: 'GET'
            }).done(function(response) {
                if(response.error === null || response.error === undefined){
                    $('#ccFormId').attr('action', response.url);
                    $('#communityCodeId').attr('value', response.communityCode);
                    $('#tokenNumber').attr('value', response.token);
                    $('#ignoreDuplicateId').attr('value', response.ignoreDuplicate);
                }else{
                    window.errorOnTokenRequest = response.error;
                    var cart = '/sabmStore/en/cart?';
                    window.location.replace(cart + window.errorOnTokenRequest);
                }
                $('body').removeClass('loading');
            });
        }
    },

    checkoutTimer: function(){
        setTimeout(function(){
           window.location.href='/cart?cartTimeout=1';
        }, 5 * 60 * 1000);
    },

    waitingProcessingPage: function(){
        $('document').ready(function() {
            $('body').addClass('loading');
            rm.checkout.checkProcessingJSON();
        });
    },

    checkProcessingJSON: function(){
        var url = window.location.pathname;
        var value = url.substring(url.lastIndexOf('/') + 1);
        $.get('/checkout/sop/processingJson/' + value, function(result) {
            if(result.length > 0) {
                window.location.replace(result);
            } else{
                setTimeout(function(){
                    rm.checkout.checkProcessingJSON();
                }, 3000);
            }
        });
    },
    
    webHookPaymentDoneWaitingPage: function(){ 
        $('document').ready(function() {        	
            $('body').addClass('loading');
            rm.checkout.checkProcessingWebHookTransaction();
        });
    },

    checkProcessingWebHookTransaction: function(){
        var url = window.location.pathname;
        var value = url.substring(url.lastIndexOf('/') + 1);
        $.get('/checkout/sop/processingPostbackResult/' + value, function(result) {        	
            if(result.length > 0) {
                window.location.replace(result);
            } else{
                setTimeout(function(){
                    rm.checkout.checkProcessingWebHookTransaction();
                }, 3000);
            }
        });
    },

    errorMessaging: function(element, message){
        if(message !== '') {
            $(element).addClass('error-input').attr('placeholder', message);
        } else {
            $(element).removeClass('error-input');
        }

    },

    getUrlParameter:function (sParam) {
        var sPageURL = decodeURIComponent(window.location.search.substring(1)),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;

        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');

            if (sParameterName[0] === sParam) {
                return sParameterName[1] === undefined ? true : sParameterName[1];
            }
        }
    },

    /*
    *   Show/Hide CUB Arranged radio button
    *   @author: nolan.b.trazo@accenture.com
    */
    deliveryModesVisibility: function() {
        var data = rm.datepickers.getDeliveryData();
        if (typeof data !== 'undefined') {
            if (data.cubArrangedEnabled === false) {
                $('.cart-deliverymethod .cub-arranged-block').hide();
            }
        }
    }
};
