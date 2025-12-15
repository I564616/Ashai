/* globals document */
/* globals window */
/* globals validate */
/*jshint unused:false*/
/* globals sessionStorage */


	'use strict';

	rm.customer = {
	        loginSubmitted: false,
			valueUserNameIncorrect: $('#username_incorrect').val(),
			valueErrorTopMessage: '<div class="alert negative">'+$('#loginError_topMessage').val()+'</div>',
			profileReceiveCheckbox: $('.personal-profile #confirm1').attr('checked'),
			profileSMSReceiveCheckbox: $('.personal-profile #confirm2').attr('checked'),
			valueCustomerSearchErrorMessage: $('#customerSearchError_essage').val(),
			// this will be used in validate.js
			constraints: {
				from: {
					email: true
				},
				confirmPassword: {
		    		equality: 'password'
		    	},

		    	passwordSecurity:{
					format:{
						pattern: '^(?=.*[0-9].*)(?=.*[a-z].*).{8,}$',
						message: 'Security check failed'
					}
				},
				firstNameSecurity:{
					format:{
						pattern: '[a-zA-Z- ]*$',
						message: 'Security check failed'
					}
				},
				orderLimitSecurity:{
					format:{
						pattern: /^(0|\+?[1-9][0-9]*)$/,
						message: 'Security check failed'
					}
				}
			},

			//check the email address format remove the space at the end
			emailInvalid: function(email)
			{
				email = $.trim(email);
				return validate.isEmpty(email) || validate({from: email}, rm.customer.constraints);
			},



			//check the password format
			passwordInvalid: function(password)
			{
				return validate.isEmpty(password) || validate({passwordSecurity: password}, rm.customer.constraints);
			},

			//check the login password format
			loginPasswordInvalid: function(password)
			{
				return validate.isEmpty(password);
			},

			//go to check the email format and give out the result
			checkLoginUsername: function()
			{
				var username = $('#j_username').val();

				if(rm.customer.emailInvalid(username)){
					//if the email have error, show the error message under the email input field
					$('#username_common').html(rm.customer.valueUserNameIncorrect);
//					$('#j_username').addClass('error-input');
					$('#globalMessages').html(rm.customer.valueErrorTopMessage);
					return false;
				}
				//if the email have no error , remove the error messages
				$('#username_common').html('');
				//$('#j_username').removeClass('error-input');
				$('#globalMessages').html('');
				return true;
			},


			//go to check the password format and give out the result
			checkLoginPassword: function()
			{
				var password = $('#j_password').val();

				if(rm.customer.loginPasswordInvalid(password)){
					return false;
				}
				return true;
			},

			//show the top error message
			showErrorTopMessage: function()
			{
				 $('#globalMessages').html(rm.customer.valueErrorTopMessage);
			},

			//hide the top error message
			hideErrorTopMessage: function()
			{
				$('#globalMessages').html('');
			},

			//bing the blur event for the input field of the email and password

			bindLoginCheckForm: function()
			{
                $('#j_password').popover({
                    placement: 'bottom',
                    html: true,
                    trigger: 'manual',
                    animation: true,
                    offset: 20,
                    delay:{show: 100, hide: 100},
                    template: '<div class="popover popover-loginattempt" ><div class="arrow"></div><div class="popover-content popover-loginattempt-content"></div></div>'
                });
			    if(parseInt($('#loginAttempts').text()) > 1){
                    $('#j_password').popover('toggle');
                    rm.customer.hideErrorTopMessage();
			    }

                if($('#j_password').length > 0){
                    sessionStorage.setItem('ShowWelcome','TRUE');
                }
				$('#j_username').blur(function(){
					rm.customer.checkLoginUsername();
				});
				$('#YT\\.png').click(function(e){
					window.location.href = 'https://www.youtube.com/watch?v=wqqT_i7A_Ks&amp;feature=youtu.be';
				});
				$('#Portal2\\.png').click(function(e){
					window.location.href = window.location.href.split('sabmStore')[0].concat('staffPortal/sabmStore/en/login');
				});
				$('#loginForm .form-actions .btn').click(function(e) {
					rm.customer.hideErrorTopMessage();
		            setTimeout(function() {
		            	if(rm.customer.checkLoginUsername() && rm.customer.checkLoginPassword()){
		            	    rm.customer.submitLoginForm();
						 }else{
							 if(parseInt($('#loginAttempts').text()) <= 1){
					          rm.customer.showErrorTopMessage();
							 }
						 }

		            },100);
		        });
				$('#loginForm .form-control').bind('keypress', function(event) {
					if (event.keyCode === 13) {
						//console.log('keypress');
						rm.customer.hideErrorTopMessage();
			            setTimeout(function() {
			            	if(rm.customer.checkLoginUsername() && rm.customer.checkLoginPassword()){
								rm.customer.submitLoginForm();
							 }else{
								 if(parseInt($('#loginAttempts').text()) <= 1){
								 rm.customer.showErrorTopMessage();
								 }
							 }
			            },100);
					}
				});
			},

			submitLoginForm: function(){
			    if (!rm.customer.loginSubmitted) {
                    rm.customer.loginSubmitted = true;
                    $('#loginForm').submit();
                }
			},

			validateProfileNotificationOpt: function(){

				if($('.personal-profile #confirm1').attr('checked') !== rm.customer.profileReceiveCheckbox){
					return true;
				}

				if($('.personal-profile #confirm2').attr('checked') !== rm.customer.profileSMSReceiveCheckbox){
					return true;
				}

				return false;
			},

			bindProfileCheckBox: function(){
				$('.personal-profile #confirm1, .personal-profile #confirm2').on('change',function(){
					if($('.personal-profile #confirm1').attr('checked')){
						$('.personal-profile #profile_receiveUpdates').val('true');
					}else{
						$('.personal-profile #profile_receiveUpdates').val('false');
					}

					if($('.personal-profile #confirm2').attr('checked')){
						$('.personal-profile #profile_receiveUpdatesForSMS').val('true');
					}else{
						$('.personal-profile #profile_receiveUpdatesForSMS').val('false');
					}

					if ( rm.customer.validateProfileNotificationOpt() ) {
						$('.personal-profile .save-profile').removeAttr('disabled');
						$('.personal-profile .save-profile').removeClass('btn-cancel');
						$('.personal-profile .save-profile').addClass('btn-primary');
					} else {
						$('.personal-profile .save-profile').attr('disabled','true');
						$('.personal-profile .save-profile').removeClass('btn-primary');
						$('.personal-profile .save-profile').addClass('btn-cancel');
					}

				});

				if($('body').hasClass('page-profile')){

					var $custMobileNumber = $('#customerMobileNumber'), $custBusinessPhoneNumber = $('#customerBusinessPhoneNumber');
					var $curMobileNumber = $('#mobileNumberField'), $curBusinessPhoneNumber = $('#businessPhoneNumber');
					var $id, dato, $this;

					var enabledSaveButton = function(e){
						$id = $('#'+$(e).attr('id'));
						$id.css({'color':'#555', 'border': '1px solid #d6d6d6'});
						$('#alert-mobileNumber').addClass('hide');
						$('.personal-profile .save-profile').removeAttr('disabled');
						$('.personal-profile .save-profile').removeClass('btn-cancel');
						$('.personal-profile .save-profile').addClass('btn-primary');
					};

					var disabledSaveButton = function(e){
						$id = $('#'+$(e).attr('id'));
						$id.css({'color':'#ff0000', 'border': '1px solid #ff0000'});
						$('#alert-mobileNumber').removeClass('hide');
						$('.personal-profile .save-profile').attr('disabled','true');
						$('.personal-profile .save-profile').addClass('btn-cancel');
						$('.personal-profile .save-profile').removeClass('btn-primary');
					};

					var reset = function(e){
						$id = $('#'+$(e).attr('id'));
						$id.css({'color':'#555', 'border': '1px solid #d6d6d6'});
						$('#alert-mobileNumber').addClass('hide');
						$('.personal-profile .save-profile').attr('disabled','true');
						$('.personal-profile .save-profile').addClass('btn-cancel');
						$('.personal-profile .save-profile').removeClass('btn-primary');
					};

					if(typeof $curMobileNumber !== 'undefined'){
						$curMobileNumber.css('background','#fff');

						if($custMobileNumber.val().length > 0){
							var mobileArray = $custMobileNumber.val().split('');
							mobileArray.splice(4,0,' ');
							mobileArray.splice(8,0,' ');
							$custMobileNumber.val(mobileArray.join(''));
							$curMobileNumber.val($custMobileNumber.val());
						}

						$curMobileNumber.on('focus',function(){
							$this = $(this);

							if ($this.val().length === 0) {
								$this.val('04');
								$this[0].setSelectionRange(2,2);
							}
							else {
								var val = $this.val();
								var len = val.length;
								$this[0].setSelectionRange(len,len);  // Ensure cursor remains at the end
							}
						}).on('keydown',function(e){

							var key = e.charCode || e.keyCode || 0;
							$this = $(this);

							// Auto-format- do not expose the mask as the user begins to type
							if (key !== 8 && key !== 9) {
								if ($this.val().length === 4) {
									$this.val($this.val() + ' ');
								}
								if ($this.val().length === 8) {
									$this.val($this.val() + ' ');
								}
							}

							// Allow numeric (and tab, backspace, delete) keys only
							return (key === 8 ||
									key === 9 ||
									key === 46 ||
									(key >= 48 && key <= 57) ||
									(key >= 96 && key <= 105));

						}).on('mouseout blur',function(){

							var $this = $(this);
							var val = $this.val().replace(/[ _]/g,'');
							$('#mobileNumber').val(val);

							var mobileNumberPattern = /[0-9 ]$/g;

							if($this.val() === '04' || $this.val() === ''){
								$curMobileNumber.val('');
							}

							if($custMobileNumber.val() !== $curMobileNumber.val()){
								if($curMobileNumber.val().length !== 0){
									if(($curMobileNumber.val().trim().charAt(0) + $curMobileNumber.val().trim().charAt(1) === '04' && $curMobileNumber.val().length === 12 && mobileNumberPattern.test($curMobileNumber.val()))){
										enabledSaveButton($this);
									}else{
										disabledSaveButton($this);
									}

									if($curMobileNumber.val() === '04' || $this.val() === ''){
										$curMobileNumber.val('');
										enabledSaveButton($this);
									}

								}else{
										enabledSaveButton($this);
								}
							}else{
								// if current and original is the same value
								reset($this);
							}
						});

					}

					} /* check if in profile page */
				/*
				if(typeof $curBusinessPhoneNumber !== 'undefined'){

					$curBusinessPhoneNumber.css('background','#fff');
					$curBusinessPhoneNumber.on('mouseout',function(){

						var $this = $(this);

						if($custBusinessPhoneNumber.val() !== $curBusinessPhoneNumber.val()){
							if($curBusinessPhoneNumber.val().length !== 0){
								if($curBusinessPhoneNumber.val().length === 10){
									enabledSaveButton($this);
								}else{
									disabledSaveButton($this);
								}
							}else{
								enabledSaveButton($this);
							}
						}else{
							reset($this);
						}
					});
				} */
			},
			bindProfileRadio: function(){
				$('input[name=defaultUnit]').on('change',function(){
						$('.personal-profile .save-profile').removeAttr('disabled');
						$('.personal-profile .save-profile').removeClass('btn-cancel');
						$('.personal-profile .save-profile').addClass('btn-primary');
				});
			},
			showErrorMessage:function(){
				var flag = false;
		         $('#customerSearch_button').click(function(){

		        	 $('.form-group input').each(function(){
		        		 if($(this).val() !== '' &&  $(this).val().length >= $(this)[0].attributes['ng-minlength'].value){
		        			 flag = true;
		        		 }
		        	 });

		        	 if(flag){
		        		 $('#customer_errorMessage').hide();
		        		 $('#customerSearchForm').submit();
		        	 }else{
		        		 $('#customer_errorMessage').show();
		        	 }
		         });
		         $('#customerSearchForm').bind('keypress', function(event) {
		        	 	if (event.keyCode === 13) {

				        	 $('.form-group input').each(function(){
				        		 if($(this).val() !== '' &&  $(this).val().length >= $(this)[0].attributes['ng-minlength'].value){
				        			 flag = true;
				        		 }
				        	 });

				        	 if(flag){
				        		 $('#customer_errorMessage').hide();
				        		 $('#customerSearchForm').submit();
				        	 }else{
				        		 $('#customer_errorMessage').show();
				        	 }
				         }
					});

			},


			bindDeleteUser: function(){
				$('#confirm-delete-user').on('click',function(){
					var $deleteUserForm = $('#deleteUserForm');
					$deleteUserForm.submit();
				});
			},

			init: function ()
			{
				rm.customer.bindLoginCheckForm();
				rm.customer.bindProfileCheckBox();
				rm.customer.showErrorMessage();
				rm.customer.bindProfileRadio();
				//if there have error, show error under the input filed
				if($('#j_username').length && $('#loginError').val()){
					rm.customer.checkLoginUsername();
				}
				 this.bindDeleteUser();
			},
	};

