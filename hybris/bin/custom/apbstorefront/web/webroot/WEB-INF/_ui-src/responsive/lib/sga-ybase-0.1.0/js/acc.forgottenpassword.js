ACC.forgottenpassword = {

	_autoload: [
		"bindLink",
		"onKeydown"
	],

	bindLink: function(){
		$(document).on("click",".js-password-forgotten",function(e){
			e.preventDefault();

			ACC.colorbox.open(
				$(this).data("cboxTitle"),
				{
					href: $(this).data("link"),
					width:"350px",
					fixed: true,
					top: 150,
					onOpen: function()
					{
						$('#validEmail').remove();
					},
					onComplete: function(){
						$('form#forgottenPwdForm').ajaxForm({
							success: function(data)
							{
								if ($(data).closest('#validEmail').length)
								{
									
									if ($('#validEmail').length === 0)
									{
										$(".forgotten-password").replaceWith(data);
										ACC.colorbox.resize();
									}
								}
								else
								{
									$("#forgottenPwdForm .control-group").replaceWith($(data).find('.control-group'));
									ACC.colorbox.resize();
								}
							}
						});
					}
				}
			);
		});
	},

    onKeydown: function () {
        var self = this;
        if ($('#asahiForgottenPwdForm input[name=email]').length !== 0) {
            $('#asahiForgottenPwdForm input[name=email]').keydown(function(e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
//                    setTimeout(function () {
//                        $.colorbox.close()
//                    });
//                    $('#forgorpwd-template').modal({show: true});
//                    $('#forgorpwd-template').removeClass("cboxElement");
                    self.openModal();
                }
             });

            $(document).on('focus', '#asahiForgottenPwdForm input[name=email]', function () {
                 var alert = $('.alert.alert-danger.alert-dismissable');
                 var helpBlock = $('.help-block.contactus');
                 if (alert.length !== 0 && helpBlock.length !== 0) {
                     $('#asahiForgottenPwdForm .form-group').removeClass('has-error');
                     alert.remove();
                     helpBlock.remove();
                 }
            });
        }
    },

	openModal: function () {
        // check if asahiForgottenPwdForm exist
        if ($('#asahiForgottenPwdForm').length !== 0) {
            var email = $('input[name=email]').val();
            var forgottenPwdEmailNull = $('#forgottenPwdEmailNull').val();
            var forgottenErrorMessage = $('#forgottenErrorMessage').val();
            var forgottenPwdEmailInvalid = $('#forgottenPwdEmailInvalid').val();

            this._removeGlobalError();
            if (email !== '') {
                var isValidEmail = this._validateEmail(email);
                if (isValidEmail) {
                    $('#forgorpwd-template').modal({show: true});
                    $('#forgorpwd-template').removeClass("cboxElement");
                } else {
                   this._createGlobalMsg(forgottenErrorMessage);
                    this._createErrorMsg(forgottenPwdEmailInvalid);
                }
            } else {
                this._createGlobalMsg(forgottenErrorMessage);
                this._createErrorMsg(forgottenPwdEmailNull);
            }
        }
    },

    _validateEmail: function (email) {
        var regex = /^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?$/; //Email Validation as per RFC2822 standards. Straight from .net helpfiles.
        return regex.test(email);
    },

    _createErrorMsg: function (text) {
        var elementString = '<div class="help-block contactus" id="contactus"><span id="email.errors">' + text + '</span></div>';
        $('#asahiForgottenPwdForm .form-group').addClass('has-error').append(elementString);
    },

    _createGlobalMsg: function (text) {
        var elementString = '<div class="alert alert-danger alert-dismissable">' + text + '</div>';
        $('.global-alerts').append(elementString);
    },

    _removeGlobalError() {
        if ($('.global-alerts').children().length !== 0) {
            $('.global-alerts').children().remove();
        }

        var helpBlock = $('.help-block.contactus');
        if (helpBlock.length !== 0) {
            $('#asahiForgottenPwdForm .form-group').removeClass('has-error');
            helpBlock.remove();
        }
    }
};