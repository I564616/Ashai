/* globals document */
/* globals window */
/* globals validate */
/*jshint unused:false*/

'use strict';
rm.resetpassword = {
	bindALl : function() {
		this.bindBlur($('.js_password'));
		this.bindMatch($('.checkPwd'));
		this.bindButtonClick($('#updatePwd_button'));
	},
//  validate the password confirm match
	checkMatch : function(password,confirmPassword){
		if(validate.isEmpty(password) || validate.isEmpty(confirmPassword)||
				validate({password: password, confirmPassword: confirmPassword}, rm.customer.constraints)){
			return true;
		}
		return false;
	},
	
//	Control message top display and hide
	showTopMessage : function() {
		if ($('.error_security').css('display') === 'none' && $('.error_match').css('display') === 'none') {
			$('#topMessage').hide();
		}else{
			$('#topMessage').show();
		}

	},
	
// Show the error message of security error	
	showSecurityError : function(){
		$('.error_security').show();
		$('#globalMessages').hide();
		$('.help-inline').hide();
		$('.js_password').css('border', '1px solid red');
		$('#pwd label').css('color', 'red');
	},

// Hide the error message of security error	
	hideSecurityError : function(){
		$('.error_security').hide();
		$('.js_password').removeAttr('style');
		$('#pwd label').removeAttr('style');
	},
	
// Show not match error
	showNotMatchError: function(){
		$('.error_match').show();
		$('.help-inline').hide();
		$('.checkPwd').css('border', '1px solid red');
		$('#confirmPwd label').css('color', 'red');
		$('#globalMessages').hide();
	},
	
// Hide not match error
	hideNotMatchError: function(){
		$('.error_match').hide();
		$('.checkPwd').removeAttr('style');
		$('#confirmPwd label').removeAttr('style');
	},
	
//	the blur event of password input
	bindBlur : function(password) {
		password.blur(function() {
			if (!rm.customer.passwordInvalid(password.val())) {
				rm.resetpassword.hideSecurityError();
			} else {
				rm.resetpassword.showSecurityError();
			}
			rm.resetpassword.showTopMessage();
		});

	},
	
//	the blur event of confirm password input
	bindMatch : function(checkPwd) {
		//the match event of input
		checkPwd.blur(function() {
			if (!rm.resetpassword.checkMatch($('.js_password').val(),checkPwd.val())) {
				rm.resetpassword.hideNotMatchError();
			} else {
				rm.resetpassword.showNotMatchError();
			}
			rm.resetpassword.showTopMessage();
		});

	},
	
//	button click event to submit by ajax
    bindButtonClick : function(button) {
        button.mousedown(function(e) {
            setTimeout(function() {
            	if (rm.customer.passwordInvalid($('.js_password').val())) {
            		rm.resetpassword.showSecurityError();
                } 
                if(rm.resetpassword.checkMatch($('.js_password').val(),$('.checkPwd').val())){
                	rm.resetpassword.showNotMatchError();
                }
                if(!rm.customer.passwordInvalid($('.js_password').val()) && !rm.resetpassword.checkMatch($('.js_password').val(),$('.checkPwd').val())){
                	rm.resetpassword.hideSecurityError();
                	rm.resetpassword.hideNotMatchError();
                	$('#updatePwdForm').submit();
                }
                rm.resetpassword.showTopMessage();
            }, 100);
        });
    },


	init : function() {
		rm.resetpassword.bindALl();
	},
    
};
