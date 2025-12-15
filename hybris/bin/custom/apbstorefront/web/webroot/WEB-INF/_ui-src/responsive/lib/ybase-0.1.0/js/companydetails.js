var compBillAddress="";
ACC.companydetails = {
		/* Same Invoice check box */
		sameInvoiceustForm : function() {
			 $('#sameasInvoiceAddress').click(function(){
				 sameAsInovice();
			});
			 sameAsInovice();
			 setDataOnKeyUp();
		},

		/* Same Invoice check box */
		submitRequest : function() {
			$('#topErrorMsg').hide();
			$(document).on('click','a.company-remove-link', function(){
				var delAddress = $(this).closest('.dynamic-delivery-address');
				var delId = delAddress.attr('data-del-id');
				removeAddress(delId);
				delAddress.hide();
				var inputDate = delAddress.find('.company-input-date');
				if(inputDate.hasClass('valid')){
					inputDate.removeClass('valid').addClass('invalid');
				}
				inputDate.addClass('noDateValid');
				var inputDel = delAddress.find('.dynamic-input-field');
				if(inputDel.hasClass('valid')){
					inputDel.removeClass('valid').addClass('invalid');
				}
				
			})
			ACC.companydetails.validateForm();
			 $('#submit-change-button').click(function(){			
				 var error_free=true;
				 var form_data=$("#apbCompanyDetailsForm");
				 var error_date_element=$('.company-date-error');
				 var inputAccountNumber = $('#accountNumber');
				 var inputAccountName = $('#acccountName');
				 var inputTrading = $('#tradingName');
				 var inputABN= $('#abn');
				 var inputBillingAddress = $('#companyBillingAddress');
				 var inputEmailAddress =$('#companyEmailAddress');
				 var error_element_trading = inputTrading.parent().parent().find('p');
				 var error_element_abn = inputABN.parent().parent().find('p');
				 var error_element_billing = inputBillingAddress.parent().parent().find('p');
				 var error_element_emailaddress = inputEmailAddress.parent().parent().find('p');
				 
				 /* TradingName validation */
				 var tradingName = $("#tradingName");
				 if(tradingName.val()=="")
					 {
					 $('.tradingName').removeClass("company-error").addClass("company-error_show");
					 tradingName.focus();
					 error_free=false;
					 }
				 else{
					 $('.tradingName').removeClass("company-error_show").addClass("company-error");
				 }
				 
				 /* Account Number validation */
				 if(inputAccountNumber.val()=="")
				 {
					 $('.accountNumber').removeClass("company-error").addClass("company-error_show");
					 inputAccountNumber.focus();
					 error_free=false;
					 }
				 else
				 {
					 $('.accountNumber').removeClass("company-error_show").addClass("company-error");
				 }
				 
				 /* Account Name Validation */
				 if(inputAccountName.val()=="")
				 {
					 $('.acccountName').removeClass("company-error").addClass("company-error_show");
					 inputAccountName.focus();
					 error_free=false;
					 }
				 else
				 {
					 $('.acccountName').removeClass("company-error_show").addClass("company-error");
				 }
				 
				 /*ABN validation */
				 var abn = $("#abn");
				 if(abn.val()=="" || !validateAbnNumber(abn.val()))
					 {
					 $('.abn').removeClass("company-error").addClass("company-error_show");
					 abn.focus();
					 error_free=false;
					 }
				 else{
					 $('.abn').removeClass("company-error_show").addClass("company-error");
				 }
				 /* Company Mobile */
				 var companyBillingAddress = $("#companyBillingAddress");
				 if(companyBillingAddress.val()=="")
					 {
					 $('.companyBillingAddress').removeClass("company-error").addClass("company-error_show");
					 companyBillingAddress.focus();
					 error_free=false;
					 }
				 else{
					 $('.companyBillingAddress').removeClass("company-error_show").addClass("company-error");
				 }
				 
				 /* Company Mobile */
				 var compMobile = $("#companyMobilePhone");
				 if(!validateMobileNumber(compMobile.val()) && !compMobile.val()=="")
					 {
					 $('.mobilePhoneError').removeClass("company-error").addClass("company-error_show");
					 compMobile.focus();
					 error_free=false;
					 }
				 else{
					 $('.mobilePhoneError').removeClass("company-error_show").addClass("company-error");
				 }
				 /* companyPhone */
				 var companyPhone = $("#companyPhone");
				 if(!validateMobileNumber(companyPhone.val()) && !companyPhone.val()=="")
					 {
					 $('.companyPhone').removeClass("company-error").addClass("company-error_show");
					 companyPhone.focus();
					 error_free=false;
					 }
				 else{
					 $('.companyPhone').removeClass("company-error_show").addClass("company-error");
				 }
				 
				 /* Company Fax */
				 var companyFax = $("#companyFax");
				 if(!validateMobileNumber(companyFax.val()) && !companyFax.val()=="")
					 {
					 $('.companyFaxError').removeClass("company-error").addClass("company-error_show");
					 companyFax.focus();
					 error_free=false;
					 }
				 else{
					 $('.companyFaxError').removeClass("company-error_show").addClass("company-error");
				 }
				 
				 if(inputEmailAddress.hasClass('valid') || !validateEmailAddress(inputEmailAddress.val())){
					
					 error_element_emailaddress.removeClass("company-error").addClass("company-error_show");
					 error_free = false;
				 }
				 else{
					 error_element_emailaddress.removeClass("company-error_show").addClass("company-error");
				 }
				
			
				 form_data.find('.dynamic-input-field' ).each(function(){
					 var error_element = $(this).parent().find('p');
					 if($(this).hasClass('valid')){
						 console.log('valid form');
						 error_element.removeClass("company-error").addClass("company-error_show");
						 $(this).focus();
						 error_free = false;
					 }
					 else{
						 error_element.removeClass("company-error_show").addClass("company-error");
					 }
				 })
								
				 $('.dynamic-delivery-address').each(function(){
					 if ( $(this).css('display') == 'none')
					    {
					    	$(this).find("input[type=text]").removeClass('valid');
					    }
					 /* Validate delivery timeframe
					  * Delivery time to is always greater than Delivery from 
					  * i.e Delivery From = 10:12 and Delivery To = 12:14
					  * * */
					 var delId= $(this).attr('data-del-id');
						$(this).closest('.dynamic-delivery-address').addClass('del-add-' + delId);
						var delAdd = $('.del-add-'+ delId);
						var validDate = delAdd.find('.company-input-date');
						var selTimefromHH = $('.del-add-'+ delId).find('input[id="timefrom-HH"]');
						var selTimefromMM = $('.del-add-'+ delId).find('input[id="timefrom-MM"]');
						var selTimeToHH= $('.del-add-'+ delId).find('input[id="timeTo-HH"]');
						var selTimeToMM =$('.del-add-'+ delId).find('input[id="timeTo-MM"]'); 
						var timefromHH = parseInt(selTimefromHH.val());
						var timefromMM = parseInt(selTimefromMM.val());
						var timeToHH= parseInt(selTimeToHH.val());
						var timeToMM = parseInt(selTimeToMM.val()); 
						var error_element_TimefromHH = selTimefromHH.parent().parent().find('.input-date-1');
						var error_element_TimefromMM = selTimefromMM.parent().parent().find('.input-date-2');
						var error_element_TimeToHH = selTimeToHH.parent().parent().find('.input-date-3');
						var error_element_TimeToMM = selTimeToMM.parent().parent().find('.input-date-4');
						
						if(selTimefromHH.hasClass('valid')){
							 console.log('valid form');
							 error_element_TimefromHH.removeClass("company-error").addClass("company-error_show");
							 $(this).focus();
							 error_free = false;
						 }
						 else{
							 error_element_TimefromHH.removeClass("company-error_show").addClass("company-error");
						 }
						if(selTimefromMM.hasClass('valid')){
							 console.log('valid form');
							 error_element_TimefromMM.removeClass("company-error").addClass("company-error_show");
							 $(this).focus();
							 error_free = false;
						 }
						 else{
							 error_element_TimefromMM.removeClass("company-error_show").addClass("company-error");
						 }
						if(selTimeToHH.hasClass('valid')){
							 console.log('valid form');
							 error_element_TimeToHH.removeClass("company-error").addClass("company-error_show");
							 $(this).focus();
							 error_free = false;
						 }
						 else{
							 error_element_TimeToHH.removeClass("company-error_show").addClass("company-error");
						 }
						if(selTimeToMM.hasClass('valid')){
							 console.log('valid form');
							 error_element_TimeToMM.removeClass("company-error").addClass("company-error_show");
							 $(this).focus();
							 error_free = false;
						 }
						 else{
							 error_element_TimeToMM.removeClass("company-error_show").addClass("company-error");
						 }
						
						/*
						 * Delivery time to is always greater than Delivery from 
					     * i.e Delivery From = 10:12 and Delivery To = 12:14
					     * */
						if(!validDate.hasClass('noDateValid')){
							if(!timeToHH>=0 && !timeToMM>=0 && !timefromHH>=0 && !timefromMM>=0){
								if(timefromHH > timeToHH){
										$(this).find(".deliveryDateError").removeClass("company-error").addClass("company-error_show");
										$(this).focus();
										$(document).scrollTop($(this).offset().top);
										error_free = false;
									
								}else if(timeToHH==timefromHH && timefromMM>=timeToMM ){
										
										$(this).find(".deliveryDateError").removeClass("company-error").addClass("company-error_show");
										$(this).focus();
										$(document).scrollTop($(this).offset().top);
										error_free = false;
										}
										else{
											$(this).find(".deliveryDateError").removeClass("company-error_show").addClass("company-error");
										}
							}
							else{
								$(this).find(".deliveryDateError").removeClass("company-error_show").addClass("company-error");
							}
						}
	   
					});
				 //If we any error while validation it stops for submit.
				 if (!error_free){
						event.preventDefault(); 
						 $('#topErrorMsg').show();
						 $('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);					}
					else{
						console.log("Validate Form!");
						$('#removebeforeSubmit').remove();
					}
				
			});
		
			
		},
		// Method on page load and also on key up to validate all mandatory fields
		validateForm:function(){
			var inputAddress = $('.dynamic-input-field');
			var inputdate = $('.company-input-date');
			var inputTrading = $('#tradingName')
			var inputABN= $('#abn');
			var inputBillingAddress = $('#companyBillingAddress');
			var inputEmailAddress =$('#companyEmailAddress');
			var compMobile = $("#companyMobilePhone");
			var companyPhone = $("#companyPhone");
			var companyEmailAddress = $("#companyEmailAddress");
			var isDefaultAddress= $('#defaultAddressCheck').val();
			var separator = $("#emailSeparator").val();
            var invalidSeparatorMsg = $(".invalid-separator");
            var companyEmail = $(".company-email").find(".form-group");
            var submitChangeButton = $("#submit-change-button");

			if(inputTrading.val()==""){
				inputTrading.removeClass("invalid").addClass("valid");
			}
			else{
				inputTrading.removeClass("valid").addClass("invalid");
				
			}
			if(inputABN.val()==""){
				inputABN.removeClass("invalid").addClass("valid");
				
			}
			else{
				inputABN.removeClass("valid").addClass("invalid");
			}
			if(inputBillingAddress.val()==""){
				inputABN.removeClass("invalid").addClass("valid");
			}
			else{
				inputBillingAddress.removeClass("valid").addClass("invalid");
			}
			if(inputEmailAddress.val()==""){
				inputEmailAddress.removeClass("invalid").addClass("valid");
				
			}
			else{
				inputEmailAddress.removeClass("valid").addClass("invalid");
				
			}
			
			//-----------------------------------------------
			
			inputTrading.on('input', function(){
				if($(this).val() == ""){
					console.log('invalid form');
					$(this).removeClass("invalid").addClass("valid");
					
				}
				else{
					$(this).removeClass("valid").addClass("invalid");
				}
				
			})
			
			inputABN.on('input', function(){
				if($(this).val() == ""){
					console.log('invalid form');
					$(this).removeClass("invalid").addClass("valid");
					
				}
				else{
					$(this).removeClass("valid").addClass("invalid");
				}
				
			})
			/* */
			inputBillingAddress.on('input', function(){
				if($(this).val() == ""){
					console.log('invalid form');
					$(this).removeClass("invalid").addClass("valid");
					
				}
				else{
					$(this).removeClass("valid").addClass("invalid");
				}
				
			})
			inputEmailAddress.on('input', function(){
				if($(this).val() == ""){
					console.log('invalid form');
					$(this).removeClass("invalid").addClass("valid");
					
				}
				else{
					$(this).removeClass("valid").addClass("invalid");
				}
				
			})
			
			// email separator check
            inputEmailAddress.on('blur', function (e) {
                var emailStr = $(this).val()
                // check separator
                if (!isValidSeparator(emailStr, separator)) {
                    invalidSeparatorMsg.removeClass('hidden');
                    companyEmail.addClass("has-separator-error");
                    submitChangeButton.attr("disabled", true);
                } else {
                    invalidSeparatorMsg.append("");
                    submitChangeButton.removeAttr("disabled");
                }
            });

            inputEmailAddress.on('focus', function () {
                if (invalidSeparatorMsg) {
                    invalidSeparatorMsg.addClass('hidden');
                    companyEmail.removeClass("has-separator-error");
                }
            });

			inputAddress.each(function(){
				if(isDefaultAddress !== "false"){
						if($(this).val() == ""){
						console.log('invalid form');
						$(this).removeClass("invalid").addClass("valid");
					}
					else{
						$(this).removeClass("valid").addClass("invalid");
					}
				}
				else{
					$(this).removeClass("valid").addClass("invalid");
					
				}
				
				$(this).on('input', function(){
					if($(this).val() == ""){
						console.log('invalid form');
						$(this).removeClass("invalid").addClass("valid");
					}
					else{
						$(this).removeClass("valid").addClass("invalid");
					}
					
				})
				
			
			}),
			inputdate.each(function(){
				if(isDefaultAddress !== "false"){
					if($(this).val() == ""){
					console.log('invalid form');
					$(this).removeClass("invalid").addClass("valid");
				}
				else{
					$(this).removeClass("valid").addClass("invalid");
				}
					
				}
				else{
					$(this).removeClass("valid").addClass("invalid");
					
				}
				
				$(this).on('input', function(){
					if($(this).val() == ""){
						console.log('invalid form');
						$(this).removeClass("invalid").addClass("valid");
					}
					else{
						$(this).removeClass("valid").addClass("invalid");
					}
				});
			});
		
		}
};
$(document).ready(function() {
	with (ACC.companydetails) {
		loadCompanyDetails();
		sameInvoiceustForm();
		populateDeliveryAddressOnLoad();
		submitRequest();
		
	}
});

	$(".new-address-dynamic").on("click", "a.company-remove-link", function () {
	    $(this).closest('.newDynamicAddress').hide();
	});
	// Add new address when click on add new address button
	var addClick = $('#counter').val();
	
	$("#additional-address").click(function () {
		var isDefaultAddress= $('#defaultAddressCheck').val();
		
		var counter = addClick++;
		if(isDefaultAddress=="false"){
			counter = counter +1;
		}
	    var $temp_address =$('.dynamic-delivery-address:last').removeClass('cloned').clone(true).show().addClass('cloned').addClass('newForm');
	    $temp_address.find("input").val("");
	    $temp_address.appendTo('.add-delivery-address');
	    $temp_address.find("textarea").val("");
	    var clonedForm = $('.cloned');
	    clonedForm.attr('data-del-id', counter);
	    var removeLink = "<a class='company-remove-link' href='javascript:void(0)'>Remove</a>";
	    var addAdditionalAddress = "ADDITIONAL DELIVERY ADDRESS (#" + counter +")";
	    clonedForm.find('.hr-cloned').remove();

	    clonedForm.find('.add-additional-address-text').addClass('checkout_subheading row-margin-fix').html(addAdditionalAddress + removeLink);

	    clonedForm.find('input[id="deliveryAddress2"]').attr('name', "apbCompanyDeliveryAddressForm[" + counter +"].deliveryAddress").removeClass('invalid').addClass('valid'); 
	    
	    clonedForm.find('input[id="timefrom-HH"]').attr('name', "apbCompanyDeliveryAddressForm[" + counter +"].deliveryTimeFrameFromHH").removeClass('invalid').addClass('valid');
	    clonedForm.find('input[id="timefrom-HH"]').attr('placeholder', "HH"); 

	    clonedForm.find('input[id="timefrom-MM"]').attr('name', "apbCompanyDeliveryAddressForm[" + counter +"].deliveryTimeFrameFromMM").removeClass('invalid').addClass('valid');
	    clonedForm.find('input[id="timefrom-MM"]').attr('placeholder', "MM"); 
	    
	    clonedForm.find('input[id="timeTo-HH"]').attr('name', "apbCompanyDeliveryAddressForm[" + counter +"].deliveryTimeFrameToHH").removeClass('invalid').addClass('valid');
	    clonedForm.find('input[id="timeTo-HH"]').attr('placeholder', "HH"); 
	    
	    clonedForm.find('input[id="timeTo-MM"]').attr('name', "apbCompanyDeliveryAddressForm[" + counter +"].deliveryTimeFrameToMM").removeClass('invalid').addClass('valid'); 
	    clonedForm.find('input[id="timeTo-MM"]').attr('placeholder', "MM"); 
	    clonedForm.find('.del-error').removeClass('company-error_show').addClass('company-error');
	    clonedForm.find('.input-date-1').removeClass('company-error_show').addClass('company-error');
	    clonedForm.find('.input-date-2').removeClass('company-error_show').addClass('company-error');
	    clonedForm.find('.input-date-3').removeClass('company-error_show').addClass('company-error');
	    clonedForm.find('.input-date-4').removeClass('company-error_show').addClass('company-error');
	    
	    clonedForm.find('textarea[id="deliveryInstruction"]').attr('name', "apbCompanyDeliveryAddressForm[" + counter +"].deliveryInstruction"); 
	    clonedForm.find('input[id="deliveryCalendar"]'). attr('name', "apbCompanyDeliveryAddressForm[" + counter + "].deliveryCalendar"); 
	    clonedForm.find('input[id="removeRequestAddress"]').val('0').attr('name', "apbCompanyDeliveryAddressForm[" + counter + "].removeRequestAddress");
	    clonedForm.find('input[id="changeRequestAddress"]').val('0').attr('name', "apbCompanyDeliveryAddressForm[" + counter + "].changeRequestAddress");
	    
	    clonedForm.find('input[id="changeRequestAddressOnAddbutton"]').val('102').attr('name', "apbCompanyDeliveryAddressForm[" + counter + "].changeRequestAddressOnAddbutton");
	    
	  
	});
        
/*set readonly property account number and account name on page load */
function loadCompanyDetails()
{
	$('#accountNumber').attr('readonly', 'readonly');
	$('#acccountName').attr('readonly', 'readonly');
}
/* on load set company billing address */
function populateDeliveryAddressOnLoad()
{
	compBillAddress = $("#companyBillingAddress").val();
	if($('#sameasInvoiceAddress')[0]!=null && $('#sameasInvoiceAddress')[0].checked )
    {  
		$("#deliveryAddress").val(compBillAddress);
    }
}
/* call When clicked on remove link */
function removeAddress(val)
{
	var i=0;
	 $('input[id="removeRequestAddress"]').each(function(index, value){
	       var existingId = $(this).attr("name");
	       var sp = existingId.split("[");
	       var sp2 = sp[1].split("].");
	       var indexPosition = sp2[0];
	       if(indexPosition === val)
    	   {
		       $(this).attr('value',"1");
    	   }
    i++;
 });
}
function sameAsInovice()
{
	if($('#sameasInvoiceAddress')[0]!=null)
		{
		var sameInvFlag = $('#sameasInvoiceAddress')[0].checked;
	 	if($('#sameasInvoiceAddress')[0]!=null && $('#sameasInvoiceAddress')[0].checked )
	     {  
	 		var companyBillingAdd = $("#companyBillingAddress").val();
	 		//Add when we don;t have default address 
	 		$('.dynamic-delivery-address').filter(':visible').eq(0).find("#deliveryAddress2").val(companyBillingAdd).removeClass('valid').addClass('invalid');
		
	     }
	 	else{
	 		 $('#deliveryAddress2').val($("#deliveryAddress0").val());
	 	}

	 	var companyBilling = $('#companyBillingAddress').val();
	 	var deliveryAddress = $('#change-address').find('#deliveryAddress2').val();
	 	
	 	if(companyBilling == deliveryAddress){
	 		$('.dynamic-delivery-address').filter(':visible').eq(0).find('#changeRequestAddress').val(1);
		}
		else{
			$('.dynamic-delivery-address').filter(':visible').eq(0).find('#changeRequestAddress').val(0);
		}
	}
}

/* current data feed in default delivery address on keyup */
function setDataOnKeyUp()
{

		if ($('#sameasInvoiceAddress')[0] != null
			&& $('#sameasInvoiceAddress')[0].checked) {
			
		$('#companyBillingAddress').keyup(function() {
			if ($('#sameasInvoiceAddress')[0] != null
					&& $('#sameasInvoiceAddress')[0].checked) {
				$('.dynamic-delivery-address').filter(':visible').eq(0).find("#deliveryAddress2").val($(this).val());
			}
		});
		
	 	 
	}
		else{
			$('#deliveryAddress2').val($("#deliveryAddress0").val());
		}
}


/* Delivery Address fields, default changeRequestAddress value=0, after changed changeRequestAddress value=1 */

$('.dynamic-delivery-address').on('keyup change', 'input, textarea', function(){
	var delId= $(this).closest('.dynamic-delivery-address').attr('data-del-id');
	$(this).closest('.dynamic-delivery-address').addClass('del-add-' + delId);
	$('.del-add-'+ delId).find('#changeRequestAddress').val(1);
});
// validate mobile number
function validateMobileNumber(v)
{
	var mobileNo = $('input[name="mobileRegexPattern"]').val();
	var mobileRegex = new RegExp(mobileNo);
	return mobileRegex.test$.trim((v));
}
// validate email address
function validateEmailAddress(v)
{
	var email = $('input[name="emailRegexPattern"]').val();
	var emailRegex = new RegExp(email);
	return emailRegex.test($.trim(v));
}

// validate abn number
function validateAbnNumber(v)
{
	var abn = $('input[name="abnRegexPattern"]').val();
	var abnRegex = new RegExp(abn);
	return abnRegex.test($.trim(v));
}

// validate multiple email separators
function isValidSeparator (emails, separator)
{
    var separatorRegex = new RegExp(/[\w\s().-]+/, 'g');
    var specialCharsRegex = new RegExp(/[\\!#$;^&%*()=\/\|:<>\?,{}\[\]]/, 'g');

    var validSeparator = separator.replace(separatorRegex, '');

    // check separators
    var matched = emails.trim().match(specialCharsRegex);
    var group = []
    if (matched) {
    	// if multiple emails
        if (matched.length > 1) {
        	// group separators
            matched.forEach(function (i) {
            	if (group.indexOf(i) === -1) {
                    group.push(i);
                }
            });

            // validate if multiple separators in use,
            // only one separator is allowed
            if (group.length === 1) {
            	return matched.length === matched.filter(function (i) { return validSeparator.indexOf(i) !== -1; }).length;
            }
            return false;
        } else {
        	var matchedStr = matched.join('');
            return validSeparator.length > 1 ? validSeparator.indexOf(matchedStr) !== -1 : matchedStr === validSeparator;
        }
    }
    return true;
}