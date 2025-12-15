				/* globals document */
/* globals validate */

'use strict';

rm.createuser = {

	init: function(){

			//console.log('fired');

			var dependantFormArea = $('.dependant-form-area'), 
			dependantFormAreaSub = $('.dependant-form-area-sub');

            this.setUpDependencyListeners('staff-users', 'user-role', dependantFormArea);
            this.setUpDependencyListeners('place-orders', 'check', dependantFormAreaSub);

            this.setVisible('hide', dependantFormArea);
            // this.setVisible('hide', dependantFormAreaSub);
            
            this.bindCreateUserCheckForm();

      },
      setUpDependencyListeners: function(_itemIDToCheck, _nameOfRadioGroup, _dependantFormArea){
      	var that = this;
  		$(document).on('change', 'input[name="'+_nameOfRadioGroup+'"]', function(){
  			var radioID = _itemIDToCheck;
      		if($('input[id=' + radioID + ']').is(':checked')){
  				that.setVisible('slideDown', _dependantFormArea);
      		}else{
				that.setVisible('slideUp', _dependantFormArea);
      		}
  		});
      },

      setVisible: function(action, dependantArea){
      		// var dependantFormArea = $('.dependant-form-area');
      		switch (action) {
			    case 'hide':
			        dependantArea.hide();
			        break;
			    case 'slideUp':
			        dependantArea.slideUp();
			        break;
			    case 'slideDown':
			        dependantArea.slideDown();
			        break;
			}
      },
      
    //regiter.tag page function
		bindCreateUserCheckForm: function(){
			
			if($('#userRole-hide').val() === 'staff-users'){
				$('#staff-users').attr('checked','checked');
				//call the click envent
				$('.dependant-form-area').show();
				this.setVisible('slideDown', $('.dependant-form-area'));
				if($('#place-orders').attr('checked')){
					$('#place-orders-dependant').show();
				}else{
					$('#place-orders-dependant').hide();
				}
			}else{
				$('#admin').attr('checked','checked');
			}
			
			//set the userRole value when some one has been checked
			var userRole = $('input:radio[name="user-role"]:checked').val();
			$('#userRole-hide').val(userRole);
			
			//set the roleOption value when some one has been checked
			if($('#place-orders').attr('checked')){
				$('#place-orders-hide').val($('#place-orders').val());
			}else{
				$('#place-orders-hide').val('');
			}
			if($('#view-and-pay').attr('checked')){
				$('#view-and-pay-hide').val($('#view-and-pay').val());
			}else{
				$('#view-and-pay-hide').val('');
			}
			
			$('#register-save').attr('disabled','disabled');
			
			rm.createuser.checkSaveButton();
			if($('.editUserPage').val() === 'editUserPage'){
				$('#confirm1').attr('checked','checked');
				$('#confirm2').attr('checked','checked');
			}
			
			//validate first name 
			$('#register_firstName').on('blur',function(){
				rm.createuser.checkFirstName();
				rm.createuser.checkSaveButton();
			});
			
			//validate surname 
			$('#register_surname').on('blur',function(){
				rm.createuser.checksurName();
				rm.createuser.checkSaveButton();
			});
			//validate email address 
			$('#register_email').on('blur',function(){
				rm.createuser.checkEmail();
				rm.createuser.checkSaveButton();
			});
			//validate order limit
			$('#order-limit').on('blur',function(){
				rm.createuser.checkOrderLimit();
				rm.createuser.checkSaveButton();
			});
			
			//the radio click function
			$('input:radio[name="user-role"]').click(function(){
				var userRole = $('input:radio[name="user-role"]:checked').val();
				$('#userRole-hide').val(userRole);
				//when the checkbox 'palce-orders' has been selected,the orderLimit will become visible and activated 
				if(!$('#place-orders').attr('checked')){
					$('#place-orders-dependant').hide();
				}
				rm.createuser.checkSaveButton();
			});
			
			//the checkbox click function
			$('input:checkbox[name="check"]').click(function(){
				//set value to checkbox by checked attribute.
				if($('#place-orders').attr('checked')){
					$('#place-orders-hide').val($('#place-orders').val());
				}else{
					$('#place-orders-hide').val('');
				}
				if($('#view-and-pay').attr('checked')){
					$('#view-and-pay-hide').val($('#view-and-pay').val());
				}else{
					$('#view-and-pay-hide').val('');
				}
				rm.createuser.checkSaveButton();
			});
			//button click event to submit
			$('#register-save').mousedown(function() {
				setTimeout(function() {
					if(rm.createuser.checkFirstName() && rm.createuser.checksurName() && rm.createuser.checkEmail() && rm.createuser.checkOrderLimit()){
						$('#sabmCreateUserForm').submit();
					}
				}, 100);
			});
			$('#showOtherUsers').on('click touchstart',function(){
			     var registerEmail = $('#register_email').val();	
			     $.ajax({
						url:$('.showUsersUrl').val(),
						type:'POST',
						data:{registerEmail:registerEmail},
						success: function(response) {	
							$.magnificPopup.close();
							$('#showOtherUsersPopup').html($('#showOtherUsersPopup', response).html());								
							 $.magnificPopup.open({
									items:{
								        src: '#showOtherUsersPopup',
								        	type:'inline'
									},
							        modal: true
								   });					
							
						},
						error:function(result) {
							console.error(result);
						}
					});	
				}); 						
		},
		
		checkSaveButton: function()
		{
			var flag = false;
			$('#register-save').attr('disabled','disabled');
			if($('#register_firstName').val() !== '' && $('#register_surname').val() !== '' && $('#businessUnit-id-hide').val() !== '' && $('#register_email').val() !== '' && $('#confirm1').attr('checked') && $('#confirm2').attr('checked')){
				if($('#admin').attr('checked')){
					flag = true;
				}else if($('#place-orders').attr('checked') || $('#view-and-pay').attr('checked')){
					flag = true;
				}
				if(flag){
					$('#register-save').removeAttr('disabled');
				}
			}
		},
		

		
		checkFirstName: function()
		{
			var firstName = $('#register_firstName').val();
			if(validate.isEmpty(firstName) || validate({firstNameSecurity: firstName}, rm.customer.constraints)){
				$('.firstName_error').show();
			}else{
				$('.firstName_error').hide();
				return true;
			}
			
			return false;
		},
		
		checksurName: function()
		{
			var surname = $('#register_surname').val();
			if(validate.isEmpty(surname) || validate({firstNameSecurity: surname}, rm.customer.constraints)){
				$('.surName_error').show();
			}else{
				$('.surName_error').hide();
				return true;
			}
			return false;
		},
		checkOrderLimit: function()
		{
			var orderLimit = $('#order-limit').val();
			if(validate({orderLimitSecurity: orderLimit}, rm.customer.constraints)){
				$('.orderLimit_error').show();
			}else{
				$('.orderLimit_error').hide();
				return true;
			}
//			rm.createuser.checkSaveButton();
			return false;
		},
		
		checkEmail: function()
		{
			if($('.createUserPage').val() === 'createUserPage'){
			var email = $('#register_email').val();
			var flag = false;
			if(rm.customer.emailInvalid(email)){
				$('.email_error').show();
				return false;
			}else{
				$('.email_error').hide();
				var url = $('.checkUserUrl').val();
	            $.ajax({
	            	async :false,
	            	cache : true,
	                url : url,
	                type :'get',
	                data :{email:email},
	                success : function(data) {
	                    if(data === 'true'){
	                    	$('.emailCheck_error').show();
	                    	return false;
	                    }else{
	                    	$('.emailCheck_error').hide();
	                    	flag = true;
	                    }
	                },
	                error : function(){
	                	$('.emailCheck_error').hide();
	                	flag = true;
	                }
	            });
	            if(flag){
	            	return true;
	            }else{
	            	return false;
	            }
//				return true;
			}
//			rm.createuser.checkSaveButton();
		}else{
			return true;
		}
	}
	
};