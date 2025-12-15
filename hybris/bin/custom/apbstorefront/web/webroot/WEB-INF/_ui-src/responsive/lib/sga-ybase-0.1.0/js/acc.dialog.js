ACC.dialog = {

    _autoload: [
        "onFocus",
        "keypressEvent"
    ],

    formGlobalError: $('#formGlobalError').val(),
    currentPasswordInvalid: $('#currentPasswordInvalid').val(),
    updatePwdInvalid: $('#updatePwdInvalid').val(),
    validationCheckPwdEquals: $('#validationCheckPwdEquals').val(),
    firstNameInvalid: $('#firstNameInvalid').val(),
    lastNameInvalid: $('#lastNameInvalid').val(),
    emailBlank: $('#emailBlank').val(),
    emailInvalid: $('#emailInvalid').val(),

    // validation error messages
    validationErrMsg: (function (inputs) {
        if (inputs.length !== 0) {
            var errors = {};
            for(var i = 0; i < inputs.length; i++) {
                errors[inputs[i].dataset.alias] = inputs[i].value;
            }
            return errors;
        }
        return null;
    })($('#validation-error-messages input[type=hidden]')),

    formId: (function (items) {
        var formId = ''
        for(var i = 0; i < items.length; i++) {
            var form = items[i];
            if (form.getAttribute('data-alias') && form.getAttribute('data-alias') === 'account-check') {
                formId = form.id
            }
        }
        return formId;
    })($('form')),
    inputs: function () {
        return $('#' + this.formId + ' .form-group input')
    },

    keypressEvent: function () {
        var pathName = window.location.pathname;
        var self = this;
        if (pathName.indexOf('register') !== -1) {
            // close colorbox
            $.colorbox.remove();
            $(document).keypress(function (e) {
                if (e.keyCode === 13) {
                    e.preventDefault();
                    self.openRegisterModal();
                }
            });
        }

        if (pathName.indexOf('update-profile') !== -1 ||
            pathName.indexOf('/manage-users/edit') !== -1 ||
            pathName.indexOf('/manage-users/create') !== -1) {
            var email = pathName.indexOf('update-profile') !== -1 ? 'emailAddress' : 'email'
            $.colorbox.remove();
            $(document).keypress(function (e) {
                if (e.keyCode === 13) {
                    e.preventDefault();
                    self.openProfileModal(email);
                }
            });
        }

        if (pathName.indexOf('/manage-users/resetpassword') !== -1) {
            var email = window.location.search.replace('?user=', '')
            $.colorbox.remove();
            $(document).keypress(function (e) {
                if (e.keyCode === 13) {
                    e.preventDefault();
                    self.openPwdModal(email);
                }
            });
        }
    },

    openRegisterModal() {
        if ($('#' + this.formId).length !== 0) {
            var fields = $('#' + this.formId + ' .form-group').find('input, select');
            var isFieldEmpty = {};
            for(var i = 0; i < fields.length; i++) {
                var field = fields[i];
                var id = field.id;
                var name = field.name;
                if (field.value === '' || (field.getAttribute('type') === 'checkbox' && !field.checked) ) {
                    var errorMsg = this.validationErrMsg[this._getMatchedId(id)];
                    if (id === 'register.email.confirm') {
                        errorMsg = this.validationErrMsg['register.email.match.invalid'];
                    } else if (id === 'register.checkPwd') {
                        errorMsg = this.validationErrMsg['password'];
                    }
                    this._createInvalidMsg(name, errorMsg);
                    isFieldEmpty[id] = true;
                } else {
                    isFieldEmpty[id] = false;
                }
            }
            this._removeGlobalError();
            var isFormValid = this._isFormValid(isFieldEmpty);
            if (isFormValid) {
                this._openModal();
            } else {
                // add global error message
                ACC.forgottenpassword._createGlobalMsg(this.formGlobalError);

                $(".pageBodyContent").animate({ scrollTop: 0 }, "slow");
            }
            this.onFocus();
        }
    },

    /* Update password Modal */
    openPwdModal(emailId) {
        // check if asahiForgottenPwdForm exist
        if ($('#' + this.formId).length !== 0) {

            var currentPassword, newPassword, checkNewPassword;
            var inputs = this.inputs();
            var errors = {};
            var isFieldEmpty = {};

            for(var i = 0; i < inputs.length; i++) {
                var name = inputs[i].name;
                var value = inputs[i].value;
                var errorMsg = this.updatePwdInvalid;
                isFieldEmpty[name] = value === '';

                // collect each field value and error message
                errors[name] = {
                    value,
                    errMsg: name === 'currentPassword' ? this.currentPasswordInvalid : this.updatePwdInvalid
                }

                // show error messages if it is empty field
                if (value === '') {
                    this._createInvalidMsg(name, errors[name].errMsg);
                }
            }

            // check password and confirm password field
            if (errors['newPassword'].value !== errors['checkNewPassword'].value) {
                var i = 0;
                for(key in errors) {
                    i++;
                    if (key === 'checkNewPassword') {
                        this._createInvalidMsg(key, this.validationCheckPwdEquals);
                    }
                }
                isFieldEmpty['checkNewPassword'] = true;

            } else if (errors['newPassword'].value !== '' &&
                errors['checkNewPassword'].value !== '' &&
                errors['newPassword'].value === errors['checkNewPassword'].value) {
                this._removeError('checkNewPassword');
                isFieldEmpty['checkNewPassword'] = false;
            }

            var isFormValid = this._isFormValid(isFieldEmpty);
            
            this._removeGlobalError();
            if (isFormValid) {
                this._isExistingUser(emailId);
            } else {
                // add global error message
                ACC.forgottenpassword._createGlobalMsg(this.formGlobalError);
            }
        }
    },

    openProfileModal(email) {
        if ($('#' + this.formId).length !== 0) {
            var isCreatePage = window.location.pathname.indexOf('create') !== -1;
            var firstName = $('input[name=firstName]').val();
            var lastName = $('input[name=lastName]').val();
            var email = $('input[name=' + email + ']').val();
            var isEmailValid = ACC.forgottenpassword._validateEmail(email);
            var validation = firstName !== '' && lastName !== '';
            var query = "?createUser=false";
            if (isCreatePage) {
                validation = firstName !== '' && lastName !== '' && email !== '' && isEmailValid;
                query = "?createUser=true";
            }
            
            this._removeGlobalError();
            if (validation) {
                this._isExistingUser(email, query);
            } else {
                var self = this;
                // set up errors for each field
                var errors = [
                    {
                        id: 'firstName',
                        field: firstName,
                        errMsg: this.firstNameInvalid
                    },
                    {
                        id: 'lastName',
                        field: lastName,
                        errMsg: this.lastNameInvalid
                    }
                ]

                if (isCreatePage) {
                    var emailError = {
                         id: 'email',
                         field: email,
                         errMsg: ''
                    }

                    if (email === '') { emailError.errMsg = this.emailBlank }
                    if (email !== '' && !isEmailValid) { emailError.errMsg = this.emailInvalid }
                    errors.push(emailError);
                }

                // add validation message for each field
                for(var i = 0; i < errors.length; i ++) {
                    if (errors[i].field === '' || (errors[i].id === 'email' && errors[i].field !== '' && !isEmailValid)) {
                        this._createInvalidMsg(errors[i].id, errors[i].errMsg);
                    }
                }

                // add global error message
                ACC.forgottenpassword._createGlobalMsg(this.formGlobalError);

                $(".pageBodyContent").animate({ scrollTop: 0 }, "slow");
            }
        }
    },

    onFocus() {
        if (this.formId) {
            var formGroups = $('#' + this.formId + ' .form-group');
            var self = this;
            for(var i = 0; i < formGroups.length; i++) {
                var fields = $(formGroups[i]).find('input, select');
                var name = fields.attr('name');
                var type = fields.attr('type');
                var nodeName = fields[0].nodeName;
                if (nodeName === 'SELECT') {
                    $('#' + this.formId + ' select[name="' + name + '"]').on('change', this._removeError);
                } else {
                    var inputName = $('#' + this.formId + ' input[name="' + name + '"]');
                    var eventType = type === 'checkbox' ? 'change' : 'focus';
                    inputName.on(eventType, this._removeError);
                }
            }
        }
    },

    _openModal: function () {
        $('#forgorpwd-template').modal({show: true});
        $('#forgorpwd-template').removeClass("cboxElement");
    },

    _createInvalidMsg: function (id, text) {
        var elementString = '<div class="help-block contactus" id="contactus"><span id="' + id + '.errors">' + text + '</span></div>';
        var field = $('#' + this.formId + ' .form-group input[name="' + id + '"], .form-group select[name="' + id + '"]');
        if (!$(field).closest('.form-group').hasClass('has-error')) {
            $(field).closest('.form-group').addClass('has-error');
            // To check if targeted input is wrapped by label
            if ($(field).parent().hasClass('control-label')) {
                $(field).parent().append(elementString);
            } else {
                $(field).after(elementString)
            } 
        }
    },

    _removeError: function (e) {
        var target = e.target || '#' + this.formId + ' input[name=' + e + ']';
        var hasError = $(target).closest('.has-error');
        if (hasError) {
            var helpBlock = hasError.find('.help-block');
            hasError.removeClass('has-error');
            helpBlock.remove();
        }
    },

    _isExistingUser(emailId, query) {
        if (!query) { query = '?createUser=false'; }
        var targetUrl = ACC.config.contextPath;
        var api_url = window.location.pathname.indexOf('update-password') === -1 && window.location.pathname.indexOf('update-profile') === -1 ? '/my-company/organization-management/manage-users' : '/my-account';
        var url = targetUrl + api_url + '/isExistingUser/' + emailId + query;
        var self = this;
        var data = false;
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (data === 'TRUE') {
                    self._openModal();
                } else {
                    $('#' + self.formId).submit();
                }
            },
            error: function (xhr, errorText, thrownError) {
                console.log(thrownError)
            }
        });
    },

    _getMatchedId(id) {
        var matched = id
        for(var key in this.validationErrMsg) {
            if (id !== key && id.indexOf(key) !== -1) {
                matched = key;
            }
        }
        return matched;
    },

    _isFormValid(items) {
        var arr = [];
        for(var key in items) {
            if (items[key]) {
                arr.push(key);
            }
        }
        return arr.length === 0;
    },

    _removeGlobalError() {
        if ($('.global-alerts').children().length !== 0) {
            $('.global-alerts').children().remove();
        }
    }
};