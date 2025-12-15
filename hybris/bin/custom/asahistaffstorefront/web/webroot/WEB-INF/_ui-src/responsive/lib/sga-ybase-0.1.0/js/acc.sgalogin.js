ACC.sgalogin = {
    redirectPath: '/samlsinglesignon/saml/asahiStaffPortal/sga/en/AUD/customer-search?RelayState=asahiStaffPortal',
    ssoRedirect: function () {
        if($("#ssologin").length > 0) {
            console.log('redirecting...');
            var loc = location;
            var dest = loc.origin + this.redirectPath;

            setTimeout(function() {
                window.location.replace(dest);
            }, 3000);
        }
    }
};

$(window).bind("load", function() {
    ACC.sgalogin.ssoRedirect();
});

var app = angular.module('asahistaffstorefront', []);
app.controller('customerLoginCtrl', function($scope) {
    $scope.usernameInvalid = false;
    $scope.passwordInvalid = false;
    $scope.emailInvalid = false;
    $scope.globalAlert = false;
    $scope.j_username = '';
    $scope.j_password = '';
    var username = $('#loginForm .form-group')[0];

    var constraints = {
        from: {
            email: true
        }
    }

    $scope.submit = function ($event) {
        this.validate();
        if (this.usernameInvalid || this.passwordInvalid) {
            $event.preventDefault();
        }
    };

    $scope.updateData = function (t, e) {
        //debugger
        if (t) {
            this[t] = e;
        }
    };

    $scope.validate = function () {
        var invalidEmail;
        if (this.j_username !== '') {
           invalidEmail = validate({from: this.j_username}, constraints);
        }

        //debugger
        if (this.j_username === '' && this.j_password === '') {
            this.usernameInvalid = true;
            this.passwordInvalid = true;
            this.globalAlert = true;
        } else if (this.j_username === '') {
            this.usernameInvalid = true;
            this.globalAlert = true;
        } else if (invalidEmail) {
            this.usernameInvalid = true;
            this.emailInvalid = true;
            var errMsg = $('input[name=emailFormatError]').val();
            $(username).find('.error').text(errMsg);
            this.globalAlert = true;
        } else if (this.j_password === '') {
            this.passwordInvalid = true;
            this.globalAlert = true;
        }
    }

    $scope.focus = function ($event) {
        var id = $event.target.id;
        if (id.indexOf('username') !== -1) {
            this.usernameInvalid = false;
            var fieldRequired = $('input[name=fieldRequired]').val();
            $(username).find('.error').text(fieldRequired);
        } else if (id.indexOf('password') !== -1) {
            this.passwordInvalid = false;
        }
    }
});