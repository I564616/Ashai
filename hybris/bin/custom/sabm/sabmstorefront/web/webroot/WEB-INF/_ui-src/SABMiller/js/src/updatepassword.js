/* globals validate */
/*jshint unused:false*/

'use strict';

rm.updatepassword = {
	constraints : {
		currentPassword : {
			// currentPassword is required
			presence : true
		},
		newPassword : {
			// newPassword is also required
			presence : true,
			// And must be at least 8 characters long
			length : {
				minimum : 8
			},

			format : {
				pattern: '^(?=.*[0-9].*)(?=.*[a-z].*).{8,}$',
				message : 1
			}
		},
		checkNewPassword : {
			// You need to confirm your password
			presence : true,
			// and it needs to be equal to the other password
			equality : {
				attribute : 'newPassword',
				message : 2
			}
		}
	},
	init : function() {
		this.showPasswordText();
//		this.handleForm();
		this.handleInputChange();

		// Fix the browser to remember the password, leading to the password input box automatically filled
		if ($('#updatepasswordform').length) {
			$('#updatepasswordform')[0].reset();
		}
	},
	// form submit
	handleForm : function() {
		// validate form field
        var form = $('#updatepasswordform');
        var errors = validate(form, rm.updatepassword.constraints);
        rm.updatepassword.showErrors(form, errors || {});
        rm.updatepassword.showTopMessage();
        if (!errors) {
            this.isExistingUser();
            return true;
        }
        return false;
	},
	// input value validate
	handleInputChange : function() {
		var form = $('#updatepasswordform');
		$('#updatepasswordform input').each(
			function(index, that) {
				$(that).on('input blur',function(ev) {
					var errors = validate(form,rm.updatepassword.constraints) || {};
					rm.updatepassword.showErrorForInput(that,errors[that.name]);
				});
			});
	},
	// input value validate show error
	showErrorForInput : function(input, error) {
		var formGroup = $(input).closest('.form-group'), message = $(formGroup).find('.message');
		if (error) {
			message.show();
			$(input).css('border', '1px solid red');
			if (error[0] === 2) {
				// checkNewPassword don`t match
				$('.error_empty').hide();
			} else if (error[0] === 1) {
				// checkNewPassword is empty
				$('.error_match').hide();
			} else if (error[0].indexOf('blank') > 0) {
				$('.error_match').hide();
			}
		} else {
			// After the submit of the error
			$(input).closest('.control-group').find('.help-inline').hide();
			message.hide();
			$(input).removeAttr('style');
		}
		rm.updatepassword.showTopMessage();
	},

	// show current password in text
	showPasswordText: function(){
		$('.show-password input[type="checkbox"]').change(function () {
      var input = $(this).parents('.form-group').find('.text');

      if($(this).is(':checked')){
        input.attr('type', 'text');
      } else {
        input.attr('type', 'password');
      }
    });
	},

	//	Control message top display and hide
	showTopMessage : function() {
		var error = false;
		$('#updatepasswordform').find('.help-inline').each(function() {
			if ($(this).css('display') !== 'none') {
				error = true;
				return;
			}
		});
		if (error) {
			$('#topMessage').show();
		}else{
			$('#topMessage').hide();
		}

	},
	// show validate all error
	showErrors : function(form, errors) {
		$('#updatepasswordform input').each(function(index, that) {
			rm.updatepassword.showErrorForInput(that,errors && errors[that.name]);
		});
	},

	isExistingUser: function() {
	    var customer = JSON.parse($('#customerData').text());
        var url = '/sabmStore/en/register/isExistingUser/' + customer.email + '?createUser=false';
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (data === 'TRUE') {
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
                                        $('#updatepasswordform').submit();
                                    }, 100);
                                });
                            },
                        }
                    });
                } else {
                    $('#updatepasswordform').submit();
                }
            },
            error: function (xhr, errorText, thrownError) {
                console.log(thrownError)
            }
        });
	}
};
