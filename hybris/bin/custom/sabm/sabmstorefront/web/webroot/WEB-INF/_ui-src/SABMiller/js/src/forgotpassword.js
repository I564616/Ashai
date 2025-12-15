/* globals document */
/* globals window */
/* globals ACC */
/*jshint unused:false*/
/*author yuxiao.wang*/

'use strict';
rm.forgotpassword = {

	init : function() {
		this.bindForgotPasswordInput($('#forgottenPwd_email'));
		this.bindForgotPasswordDocument();
	},

	//show the error message and change color
	show : function() {
		$('#forgottenPwd_email').css('border', '1px solid red');
		$('#forgottenPwd_label').css('color', 'red');
		$('#invalidEmail').show();
		$('#emailNotFound').hide();
	},

	//hide the error message and change color
	hide : function() {
		$('#forgottenPwd_email').removeAttr('style');
		$('#forgottenPwd_label').removeAttr('style');
		$('#invalidEmail').hide();
		$('#emailNotFound').hide();
	},

	bindForgotPasswordDocument : function() {
		//Enter event
		$('#forgottenPwd_email').bind('keypress', function(event) {
			if (event.keyCode === 13) {
				var email = $('#forgottenPwd_email');
				//add validate to email by yuxiao
				if (!rm.customer.emailInvalid(email.val())) {
				    rm.forgotpassword.emailPresentSubmit(email.val());
					return false;
				} else {
					rm.forgotpassword.show();
					return false;
				}
			}
		});
	},

	/* keep it. Does not need it now, might be used in the future
	emailPresent: function(email)
    {
        email = $.trim(email);
        $.get('/login/pw/forgot/validateEmail',{email:email},function(returned)
            {
                if (returned.valueOf() === 'INVALID') {

                    $('#forgottenPwd_email').css('border', '1px solid red');
                    $('#forgottenPwd_label').css('color', 'red');
                    $('#emailNotFound').show();
                } else {
                    $('#forgottenPwd_email').removeAttr('style');
                    $('#forgottenPwd_label').removeAttr('style');
                    $('#invalidEmail').hide();
                    $('#emailNotFound').hide();
                }
            });
    },
    */

	emailPresentSubmit: function(email)
    {
        email = email || $.trim($('#forgottenPwd_email').val());
        if (!rm.customer.emailInvalid(email)) {
            $.magnificPopup.open({
                items: {
                  src: '#forgotpwd-popup',
                  type: 'inline'
                },
                callbacks: {
                    open: function() {
                        $('#submit_button').mousedown(function(e) {
                            $.magnificPopup.close();
                            setTimeout(function() {
                                $('#forgottenPwdForm').submit();
                            }, 100);
                        });
                    },
                }
            });
        } else {
            rm.forgotpassword.show();
        }
    },
	bindForgotPasswordInput : function(input) {
		//the blur event of input
		input.blur(function() {
			//add validate to email by yuxiao
			if (!rm.customer.emailInvalid(input.val())) {
				rm.forgotpassword.hide();
			} else {
				rm.forgotpassword.show();
			}
		});

        input.focus(function() {
            rm.forgotpassword.hide();
        });
	}
};
