/*jshint unused:false*/
/* globals enquire*/
/* globals document */
/* globals validate */
/* globals window */
/* globals alert */
/*globals MerchantSuite*/
'use strict';

rm.billing = {
	init:function(){
		this.assignTokenforPayment();
		this.setupMonthYearPayment();
		this.changeBillingOptions();
		this.setSelectedListeners();
        rm.utilities.merchantServiceFee();        
	},

    setSelectedListeners: function(){
    	$(document).on('change', 'input[name="billing"]', function(){
            $('label[for="selectedAmount"]').trigger('click');
            document.getElementById('selectedAmount').setAttribute('checked', 'checked');

        });
    },
    
    generateTokenForPayment: function(funct){
        var invoicesValue = '', amountValue = '';
        if ($('#selectedAmount').is(':checked')) {
            var selectedInvoices = [];
            $('#seller-table tbody input[name="billing"]:checked').each(function (index) {
                selectedInvoices.push($(this).val());
            });
            invoicesValue = selectedInvoices.join(',');
            amountValue = rm.utilities.convertDollarToString($('#selectedAmountValue').text());
        } else {
            var allOpenInvoices = [];
            $('#seller-table tbody input[name="billing"]').each(function (index) {
                if ($(this).attr('data-status') === 'Open') {
                    allOpenInvoices.push($(this).val());
                }
            });
            invoicesValue = allOpenInvoices.join(',');
            amountValue = rm.utilities.convertDollarToString($('#openBalanceValue').text());
        }

        var sendInfo = {invoices: invoicesValue, amount: amountValue, currencyIso: 'AUD', paymentTypeSelected:  $('input[name="accountType"]').val() };

        funct(sendInfo);

    },
    allBillingInvoices : [],
	assignTokenforPayment: function(){

		window.setBillingCheckoutDisable = 0;

		$.post( 'billing/invoices', function( data ) {
			for(var i=0; i< data.length; i++) {
				if (data[i].status === 'Open'){
                    rm.billing.allBillingInvoices.push(data[i].invoiceNumber);
				}
			}
		});
		
        $('#billingButtonEFTPayment').on('click', function () {
            if ($('#makeEFTPaymentForm').hasClass('ng-valid')) {
            	
            	var EFTData = [];
                rm.billing.generateTokenForPayment(function (info) {

                	EFTData.push({
            			'userAccountBSB': $('#makeEFTPaymentForm #bsb').val(),
            			'userAccountName': $('#makeEFTPaymentForm #nameOnAccount').val(),
            			'userAccountNumber': $('#makeEFTPaymentForm #accountNumber').val(),
            			'amount': info.amount,
            			'currencyIso': info.currencyIso,
            			'invoices': info.invoices	
	            	});
            	
	            	$.magnificPopup.close();
	        		rm.utilities.showOverlay(true);
	        		
	            	$.ajax({
						url: '/your-business/billing/payByEFT',
						data: EFTData[0],
		                dataType: 'json',
		                type: 'POST'
	            	}).done(function(res){
	            		window.location.href = res;
	            		rm.utilities.showOverlay(false);
	
	            	});
                });
            }
        });

        var msfData = [], msfData1 = [];
        $('#billingButtonCCPayment').on('click', function () {
        	msfData = []; msfData1 = [];
        	
        	//check if all the required field not empty
            if ($('#makePaymentForm').hasClass('ng-valid')) {
            	
            	//check the expiry date is a future date and valid
                rm.billing.generateTokenForPayment(function (info) {
            		var cardType = $('#cardType').val();
            		var data = {
            			'invoices': info.invoices,
            			'currencyIso': info.currencyIso,
            			'amount': info.amount,
            			'cardType': cardType
            		};
                    
            		msfData.push({
            			'cardNumber': $('#makePaymentForm #cardNumber').val(),
            			'cardHolderName': $('#makePaymentForm #nameOnCard').val(),
            			'securityCode': $('#makePaymentForm #securityCode').val(),
            			'expiryDateMonth': $('.expiryDateMonth').val(),
        				'expiryDateYear': ( $('.expiryDateMonth').val() === '99' ) ? $('#expiryDateYearHidden').val() : $('.expiryDateYear').val()
            		});

            		$.magnificPopup.close();
            		rm.utilities.showOverlay(true);

            		$.ajax({
    					url: '/your-business/billing/payByCard',
    					data: data,
    	                dataType: 'json',
    	                type: 'POST'
                	}).done(function(res){

                		if ( res.error === null) {

                		msfData1.push({
                			'authKey': res.authKey,
                			'paymentUrl': res.paymentUrl
                		});
                		
                		$.extend(true, msfData, msfData1);

	            		console.log(msfData);

                		$('#invalidExpiryDate').addClass('hide');

	            		//pass data to MSF modal
	            		$('#amount').text('$'+parseFloat(res.displayAmount).toFixed(2));
	            		$('#totalAmount').text('$'+parseFloat(res.displayTotalAmount).toFixed(2));
	            		$('#surcharge').text('$'+parseFloat(res.displaySurcharge).toFixed(2));
	            		$('#msf').text('$'+parseFloat(res.displaySurcharge).toFixed(2));

                		rm.utilities.showOverlay(false);
                		rm.utilities.showCheckoutMSFPopup();

                		} else {
                            var billing = '/your-business/billing?';
                            window.location.replace(billing + res.error);
                		}
                		
                	});	
                });
            }
        });
        
        $('.billing-msf-popup-button').on('click', function(){
            
    		$.magnificPopup.close();
    		rm.utilities.showOverlay(true);
        	
        	MerchantSuite.BaseUrl = msfData[0].paymentUrl;
    		
        	MerchantSuite.ProcessPayment({
        		AuthKey: msfData[0].authKey,
        		CardNumber: msfData[0].cardNumber,
        		CVN: msfData[0].securityCode,
        		ExpiryMonth: msfData[0].expiryDateMonth,
        		ExpiryYear: msfData[0].expiryDateYear,
        		CardHolderName: msfData[0].cardHolderName,
        		CallbackFunction: function(res) {
        			
        			var billingUrl = '/your-business/billing?';
        			if ( res.AjaxResponseType === 0 ) {
        				if ( res.ApiResponseCode === 0 ) {
                			window.location.href = res.RedirectionUrl;
            			} else { 
            				window.location.replace(billingUrl + 'declined=true');
            			}
        			} else if ( res.AjaxResponseType === 1 ) {
        				window.location.replace(billingUrl + 'invalidCard=true');
        			} else if ( res.AjaxResponseType === 2 ) {
        				window.location.replace(billingUrl + 'paymentError=true');
        			} else {
        				window.location.replace(billingUrl + 'gatewayError=true');
        			}
        		
        		}
        	});
        });
	},

    verifyMonthAndYear:function(){
        var date=new Date();
		var fullYear = date.getFullYear()+'';
		var shortYear = fullYear.substr(2, 2);
		var month=date.getMonth()+1;
		if(shortYear === $('.expiryDateYear').val() && $('.expiryDateMonth').val() && $('.expiryDateMonth').val() < month){
			$('#invalidExpiryDate').removeClass('hide');
			$('#billingButtonCCPayment').attr('disabled','disabled');
			return false;
		}else if($('.expiryDateMonth').val() && $('.expiryDateYear').val()){
			$('#invalidExpiryDate').addClass('hide');
			$('#billingButtonCCPayment').removeAttr('disabled');
			return true;
		}
    },
    
	paymentToWestpac: function (sendInfo, funct){
		$.ajax({
	        url: 'billing/pay',
	        data: sendInfo,
	        dataType: 'json',
	        type: 'POST'
		}).done(function(response) {
			var communityCodeData 	= response.communityCode;
        	var tokenData			= response.token;
        	var urlData				= response.url;
        	var ignoreDuplicateData = response.ignoreDuplicate;
        	var errorData 			= response.error;
        	if(errorData === null || errorData === undefined){
	        	$('#makePaymentForm, #makeEFTPaymentForm').attr('action', urlData);
	        	$('.billingCommunityCodeInput').attr('value', communityCodeData);
	        	$('.billingTokenInput').attr('value', tokenData);
	        	$('.ignoreDuplicateInput').attr('value', ignoreDuplicateData);
	        	window.setBillingCheckoutDisable = 1;
        	}else{
	        	window.errorOnTokenRequest = errorData;
	        	window.setBillingCheckoutDisable = 1;
        	}
            // call the anonymous method passed in as async return.
            funct();

		});
	},
	waitingProcessingPage: function(){
		$('document').ready(function() {
			$('body').addClass('loading');
			rm.billing.checkProcessingJSON();
		});
	},
	checkProcessingJSON: function(){
		var url = window.location.pathname;
		var value = url.substring(url.lastIndexOf('/') + 1);
		$.get('/sabmStore/en/your-business/billing/pay/waitJson/' + value, function(result) {
	        if(result.length > 0) {
	        	window.location.replace(result);
	        } else{
	        	setTimeout(function(){
	        		rm.billing.checkProcessingJSON();
	        	}, 3000);
	        }
	    });
	},
	setupMonthYearPayment : function(){
		$('.expiry .js-expiry-year').append(rm.billing.getYearLiEle());
	},
	getYearLiEle:function() {
		var currentData = new Date();
		var fullYear = currentData.getFullYear()+'';
		var shortYear = fullYear.substr(2, 2);
		var liHtml = '';
		for (var i = 0; i <= 15; i++) {
			liHtml+='<option value="'+(parseInt(shortYear,10)+i)+'">'+(parseInt(shortYear,10)+i)+'</option>';
		}
		return liHtml;
	},
	changeBillingOptions:function() {
		//$('.billing-options #selectedAmount').attr('checked',false);
		//$('.billing-options #openBalance').attr('checked',true);
		$('#billingDropdownFilter .select-items li').on('click touchstart', function(){
			$('.billing-options #selectedAmount').attr('checked',true);
			$('.billing-options #openBalance').attr('checked',false);
		});
	}
};
