
CUB.controller('registrationRequestCtrl', ['$scope','$http','$window','angularUtils',function($scope,$http,$window,angularUtils) {

	$scope.registrationRequestInit = function(){
		$scope.rr = {};
		$scope.accoutTypeError = false;
		$scope.accessTypeError = false;
		$scope.formHaveError = false;
		$scope.termsofuseError = false;
		$scope.firstNameError=false;
		$scope.lastNameError=false;
		$scope.emailError=false;
		$scope.accountNameError=false;
		$scope.workPhoneNumError=false;


		$scope.$watch('registrationRequest.$valid',function(validity){
			if(validity){
				$scope.notComplete = false;
			} else {
				console.log('form not valid');
			}
		});
	};

	$scope.checkValid = function (form) {
		var formObj = {};

		formObj = $scope.rr;

		if(form.$valid){
			$scope.notComplete = false;
		} else {
			if(formObj.firstName===undefined){
				$scope.firstNameError=true;
			}else{
				$scope.firstNameError=false;
			}
			$scope.notComplete = true;
		}

		$scope.updateCheckBoxChecks(form,"accoutType");
		$scope.updateCheckBoxChecks(form,"accessType");
		$scope.updateCheckBoxChecks(form,"termsofuse");
		$scope.updateInputFields(form,"firstName");
		$scope.updateInputFields(form,"lastName");
		$scope.updateInputFields(form,"email");
		$scope.updateInputFields(form,"accountName");
		$scope.updateInputFields(form,"workPhoneNum");


		if($scope.accoutTypeError||$scope.accessTypeError||$scope.termsofuseError){
			$scope.notComplete = true;
		}else{

			$scope.notComplete = false;
		}

		if(!($scope.notComplete))
		{
			var workPhoneNum = '';
			var mobilePhoneNum = '';
			if(formObj.workPhoneNum)
			{
				workPhoneNum = formObj.workPhoneNum.toString();
			}
			if(formObj.mobilePhoneNum)
			{
				mobilePhoneNum = formObj.mobilePhoneNum.toString();
			}

			var accountType=[];
			if(formObj.accoutType1){
				accountType[accountType.length] = $('#rr-accoutType1').val();
			}
			if(formObj.accoutType2){
				accountType[accountType.length] = $('#rr-accoutType2').val();
			}
			if(formObj.accoutType3){
				accountType[accountType.length] = $('#rr-accoutType3').val();
			}
			if(formObj.accoutType4){
				accountType[accountType.length] = $('#rr-accoutType4').val();
			}

			var accessType=[];
			if(formObj.accessType1){
				accessType[accessType.length] = $('#rr-accessType1').val();
			}
			if(formObj.accessType2){
				accessType[accessType.length] = $('#rr-accessType2').val();
			}
			if(formObj.accessType3){
				accessType[accessType.length] = $('#rr-accessType3').val();
			}
			if(formObj.accessType4){
				accessType[accessType.length] = $('#rr-accessType4').val();
			}

            if (form.$valid) {
                $.magnificPopup.open({
                    items: {
                      src: '#forgotpwd-popup',
                      type: 'inline'
                    },
                    callbacks: {
                        open: function() {
                            $('#submit_button').click(function(e) {
                                var dataObj = {
                                    firstName:formObj.firstName,
                                    lastName:formObj.lastName,
                                    email: formObj.email,
                                    cubAccount: formObj.cubAccount,
                                    accountName: formObj.accountName,
                                    workPhoneNum: workPhoneNum,
                                    mobilePhoneNum: mobilePhoneNum,
                                    accoutType: accountType,
                                    accessType: accessType,
                                    haveMoreAccount: formObj.haveMoreAccount,
                                };
                                $('body').addClass('loading');
                                $http.post("/register/registration-form", dataObj).success(function(data) {
                                    if(data=="OK"){
                                        $scope.formHaveError = false;
                                        $window.location = "/register/registration-form?submitted=true";
                                    }else{
                                        $scope.formHaveError = true;
                                        console.log('failure');
                                    }
                                    $('body').removeClass('loading');
                                }).error(function(error){
                                    $('body').removeClass('loading');
                                    console.log('failure');
                                });
                            });
                        },
                    }
                });
            }
		}

	};
	$scope.updateInputFields = function(form,type){
		var formObj = {};
		formObj = $scope.rr;
		if(type=="firstName"){
			if(formObj.firstName===undefined){
				$scope.firstNameError=true;
			}else{
				$scope.firstNameError=false;
			}
		}
		if(type=="lastName"){
			if(formObj.lastName===undefined){
				$scope.lastNameError=true;
			}else{
				$scope.lastNameError=false;
			}
		}
		if(type=="email"){
			if(formObj.email===undefined){
				$scope.emailError=true;
			}else{
				$scope.emailError=false;
			}
		}
		if(type=="accountName"){
			if(formObj.accountName===undefined){
				$scope.accountNameError=true;
			}else{
				$scope.accountNameError=false;
			}
		}
		if(type=="workPhoneNum"){
			if(formObj.workPhoneNum===undefined){
				$scope.workPhoneNumError=true;
			}else{
				$scope.workPhoneNumError=false;
			}
		}


	}
	$scope.updateCheckBoxChecks = function(form,type){
		var formObj = {};
		formObj = $scope.rr;
		if(type=="accoutType"){
			if(formObj.accoutType1||formObj.accoutType2||formObj.accoutType3||formObj.accoutType4){
				$scope.accoutTypeError = false;
			}else{
				$scope.accoutTypeError = true;
			}
		}else if(type=="accessType"){
			if(formObj.accessType1||formObj.accessType2||formObj.accessType3||formObj.accessType4){
				$scope.accessTypeError = false;
			}else{
				$scope.accessTypeError = true;
			}
		}
		else if(type=="termsofuse"){
			if(formObj.termsofuse1){
				$scope.termsofuseError = false;
			}else{
				$scope.termsofuseError = true;
			}
		}
    };

}]);
