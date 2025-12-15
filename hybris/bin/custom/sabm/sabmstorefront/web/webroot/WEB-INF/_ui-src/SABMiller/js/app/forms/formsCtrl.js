CUB.controller('formsCtrl', ['$scope','$http','$window','globalMessageService',function($scope,$http,$window,globalMessageService) {
	$scope.init = function(){
		$scope.bdeViewOnly = false;
		if($('.view-only-mode').length){
			$scope.bdeViewOnly = true;
		}

	};

	var bdeUserEmailIds = ($('#bdeUserEmailIds').val() !== undefined ? $('#bdeUserEmailIds').val().split(',') : '');
	var customerEmailIds = ($('#customerEmailIds').val() !== undefined ? $('#customerEmailIds').val().split(',') : '');


	if (typeof $('#ccValidationData').html() !== 'undefined') {
		$scope.ccValidationData = JSON.parse($('#ccValidationData').html());
	}

	$('.allowNumericOnly').on('keyup input', function(ev){
		var $this = $(this);
		if (/\D/g.test($this.val())) {
			$this.val($this.val().replace(/\D/g, ''));
		}
	});

	$scope.cardType = '';
	$scope.cvvLength = 0;
	$scope.cardLength = 16;
	$scope.isCardNumberValid = true;

	$('.ccValidation').on('keyup keypress', function(e){

		var input = $(this).val();


		//if (input.length > 0 ) {
			if ( typeof $scope.ccValidationData !== 'undefined' ) {
				//if ( isNumeric(event) ) {

					$scope.cardType = '';
					$scope.cvvLength = 0;
					$scope.cardLength = 16;

			   		angular.forEach($scope.ccValidationData, function(i){

			   			if (typeof i !== 'undefined' ) {
				   			if ( typeof i.digitValidation !== 'undefined' && i.digitValidation.length > 0 ) {
				   				angular.forEach(i.digitValidation, function(x){

				   	   				if ( parseInt($scope.getInputByDigits(e, i.digitValidation.length)) === parseInt(x) && input.length === parseInt(i.lengthValidation)) {

				   	   					$scope.cardType = i.cardType;
				   	   					$scope.cardLength = parseInt(i.lengthValidation);
				   	   					$scope.cvvLength = parseInt(i.cvvValidation);
				   	   					$('#cardType').val(i.cardType);
				   	   					$scope.isCardNumberValid = true;
				   	   				}
				   				});
				   			}

				   			if ( typeof i.seriesValidation !== 'undefined' && i.seriesValidation.length > 0 ) {
				   				angular.forEach(i.seriesValidation, function(y){
				   					var series = y.split(':');
				   					var min = series[0], max = series[1];

					   					if ( parseInt($scope.getInputByDigits(e, min.length)) >= parseInt(min) && parseInt($scope.getInputByDigits(e, max.length)) <= parseInt(max) && input.length === parseInt(i.lengthValidation) ) {

					   						$scope.cardType = i.cardType;
					   	   					$scope.cardLength = parseInt(i.lengthValidation);
					   	   					$scope.cvvLength = parseInt(i.cvvValidation);
					   	   					$('#cardType').val(i.cardType);
					   	   					$scope.isCardNumberValid = true;

				   	   						e.stopImmediatePropagation();
				   							e.preventDefault();
				   							return false;
					   					}
				   				});
				   			}
			   			}

			   		});
				//}
			}
		//}

		$scope.$apply();
		$(this).attr('maxlength', $scope.cardLength);

	});

    $scope.isExpiryDateInvalid = false;
    $('.expiryDateMonth, .expiryDateYear').on('change', function(e){
    	$scope.isExpiryMonthAndYearValid();
    });

    $scope.isExpiryMonthAndYearValid = function(){
        var date=new Date();
		var fullYear = date.getFullYear()+'';
		var shortYear = fullYear.substr(2, 2);
		var month=date.getMonth()+1;
		if(shortYear === $('.expiryDateYear').val() && $('.expiryDateMonth').val() && $('.expiryDateMonth').val() < month){
			$scope.ccForm.expiryDateMonth.$setValidity('expiryMonth', false);
			$scope.ccForm.expiryDateYear.$setValidity('expiryYear', false);
			$scope.isExpiryDateInvalid = true;
			return false;
		}else if($('.expiryDateMonth').val() && $('.expiryDateYear').val()){
			$scope.ccForm.expiryDateMonth.$setValidity('expiryMonth', true);
			$scope.ccForm.expiryDateYear.$setValidity('expiryYear', true);
			$scope.isExpiryDateInvalid = false;
			return true;
		}

		$scope.$apply();
    },

	$scope.emailIds = {
			users:[],
			customers:[]
	};

	for(var i = 0; i < bdeUserEmailIds.length; i++){
		$scope.emailIds['users'].push({'email': bdeUserEmailIds[i]});
	}

	for(var j = 0; j < customerEmailIds.length; j++){
		$scope.emailIds['customers'].push({'email': customerEmailIds[j]});
	}

	$scope.userEmailIds = $scope.emailIds['users'];
	$scope.custEmailIds = $scope.emailIds['customers'];

	var x,y;

	$scope.checkEmailType = function(emailType){
		if(emailType === 'users'){
			x = $scope.userEmail;
			y = $scope.userEmailIds;
		}else if(emailType === 'customers'){
			x = $scope.custEmail;
			y = $scope.custEmailIds;
		}else{
			x = undefined;
		}
	}

	$scope.addNewEmailFor = function(type){

		$scope.checkEmailType(type);

		if(x != undefined){
			if(x.length > 0){

				if(!$scope.isValidEmailAddress(x, type)){
					return false;
				}

				var newObj = {
					email: x,
					//checked: true
				}

				y.push(newObj);

				if(type === 'users'){
					$scope.userEmailCheckEnabled = true;
				}else if(type === 'customers'){
					$scope.custEmailCheckEnabled = true;
				}

				$scope.validateFormTextField();

			}
		}

		$scope.custEmail = "";
		$scope.userEmail = "";
	}

	/* check user input if it's a cub email address */
	$scope.isValidEmailAddress = function(email, type){
		var userEmailPattern = /[a-z0-9](\.?[a-z0-9])@cub\.com\.au$/g;

		var custEmailPattern = /[a-z0-9._%+-]+@(?!cub.com.au)(?!au.ab-inbev.com)(?!au.sabmiller.com)(?!beercollective.com.au)(?!fostersgroup.com.au)[a-z0-9-.]+(\.[a-z])?$/g;


		$('#cubEmailAddressError, #custEmailAddressError').css('color','#ff0000');

		if(type === 'users'){
			if(!userEmailPattern.test(email)){
				$('#cubEmailAddressError').attr('class','visible');
				return false;
			}else{
				$('#cubEmailAddressError').attr('class','invisible');
			}
		}else if(type === 'customers'){
			if(!custEmailPattern.test(email)){
				$('#custEmailAddressError').attr('class','visible');
				return false;
			}else{
				$('#custEmailAddressError').attr('class','invisible');
			}
		}

		return true;
	}

	$scope.isNumeric = function (event, maxlength) {

		var keyCode = event.keyCode || event.charCode || event.which;

			var val = $(event.currentTarget).val();

			if ( parseInt($(event.currentTarget).val().length) >= maxlength ) {

				$(' + input[type="hidden"]', event.currentTarget).val(val);

				if ( keyCode !== 8 ) {
					event.stopImmediatePropagation();
					event.preventDefault();
					return false;
				}
			}

			$(event.currentTarget).attr('maxlength', maxlength);
	}

	$scope.validateFormTextField = function(){
			var userCheckedLength = $('input[id^="userEmail"]:checked').length;
			var custCheckedLength = $('input[id^="custEmail"]:checked').length;

			for(var i=0;i<$scope.userEmailIds.length;i++){
				if($('#userEmail' + i).is(':checked')){
					$scope.userEmailCheckEnabled = true;
				}
			}

			for(var j=0;j<$scope.custEmailIds.length;j++){

				if($('#custEmail' + j).is(':checked')){
					$scope.custEmailCheckEnabled = true;
				}
			}

		if($scope.orderDetailsTextarea != undefined && $scope.orderDetailsTextarea.length > 3){
			$scope.orderDetailTextareaEnabled = true;
		}


		if(userCheckedLength == 0){
			$scope.userEmailCheckEnabled = false;
		}
		if(custCheckedLength == 0){
			$scope.custEmailCheckEnabled = false;
		}

		if($scope.userEmailCheckEnabled && $scope.custEmailCheckEnabled && $scope.orderDetailTextareaEnabled){
			$scope.saveDetailsEnabled = true;
		}else{
			$scope.saveDetailsEnabled = false;
		}

		$scope.orderDetailTextareaEnabled = false;
	}
	/*
	$scope.deleteEmailId = function(id, type){
		$scope.checkEmailType(type);
		y.splice(id,1);

		if(type === 'users'){
			$('#userEmail'+id).prop('checked',false);
		}

		if(type === 'customers'){
			$('#custEmail'+id).prop('checked',false);
		}

		$scope.validateFormTextField();
	} */

	$scope.saveDetails = function(){

		$scope.BdeOrderDetailsForm = {
				users:[],
				customers:[],
				emailText: ''
		};


		$.each($("input[id*='userEmail']:checked"),function(){
		       $scope.BdeOrderDetailsForm['users'].push({'email': $(this).val()});
		});

		$.each($("input[id*='custEmail']:checked"),function(){
			var str = $(this).val();
			var email = str.indexOf(':') > -1 ? str.substring(0,str.indexOf(':')) : str;
			var firstName = str.indexOf(':') > -1 ? str.substring(str.indexOf(':')+1,str.length) : '';
			  $scope.BdeOrderDetailsForm['customers'].push({'email': email,'firstName': firstName});
		});

		$scope.BdeOrderDetailsForm.emailText = $scope.orderDetailsTextarea;


		 $http.post($('#updateBDEOrderDetailsUrl').val(), $scope.BdeOrderDetailsForm).success(function(result, status){
			 $('#bdeOrderModal3').css('visibility','hidden');
			 $('.modal-backdrop').removeClass('modal-backdrop fade in');
			 $('body').removeClass('modal-open');

		 }).error(function(data, status){
		  });
	}

	//BDE Ordering Modal
	$('[id^="bdeOrderModal"]').on('shown.bs.modal',function(){
		$('#bdeOrderModal1, #bdeOrderModal2').find('.modal-content').css('height',$(window).height()*0.3);
		$(this).find('.modal-body').css('height','100%');
		$('body').addClass('modal-open');
	});

	$('#bdeOrderModal1').modal('show');

	$('#bdeOrderCheckbox').on('click',function(){
		if($(this).is(':checked')){
			$('#bdeOrderModal1').css('visibility','hidden');
			$('#bdeOrderModal2').modal('show');
		}
	});

	$('#bdeOrderModal2').on('shown.bs.modal',function(){
		setTimeout(function(){
			$('#bdeOrderModal2').css('visibility','hidden');
			$('#bdeOrderModal3').modal('show');
		},2000);
	});

	$('[id^="bdeOrderModal"]').on('hide.bs.modal',function(){
		window.location.href = 'cart';
	});


	$scope.modalClose = function(){
		$scope.bsb = "";
		$scope.accountNumber = "";
		$scope.accountName = "";
		$scope.agreeTerms = false;
		$scope.cardNumber = "";
		$scope.cardName = "";
		$scope.cvn = "";
		$scope.expiryMonth = "";
		$scope.expiryYear = "";

		$scope.eftForm.$setPristine();
		$scope.eftForm.$setUntouched();
		$scope.ccForm.$setPristine();
		$scope.ccForm.$setUntouched();

		$scope.ccForm.submitted = false;
		$scope.eftForm.submitted = false;
		$scope.$apply();
	};

	$scope.resetCCForm = function(){

		$scope.cardNumber = "";
		$scope.cardName = "";
		$scope.cvn = "";
		$scope.cardType="";
		$scope.expiryDateMonth = "";
		$scope.expiryDateYear = "";
		$scope.ccForm.$setPristine();
		$scope.ccForm.$setUntouched();

		$('#Invalid_Expiry_Date').addClass('ng-hide');

		$scope.ccForm.submitted = false;
	};

	$scope.resetEFTForm = function(){

		$scope.bsb = "";
		$scope.accountNumber = "";
		$scope.accountName = "";
		$scope.agreeTerms = false;
		$scope.cardNumber = "";
		$scope.cardName = "";
		$scope.cvn = "";
		$scope.expiryMonth = "";
		$scope.expiryYear = "";

		$scope.eftForm.$setPristine();
		$scope.eftForm.$setUntouched();

		$('#invalidExpiryDate').addClass('hide');

		$scope.eftForm.submitted = false;

	};

    $scope.ccBillingFormInit = function(){
   		ccForm.submitted = false;
        $scope.payBy = 'CREDIT_CARD';
   	};

    $scope.ccCheckoutFormInit = function(){
   		$scope.continuePayment = false;

        $scope.accountType == 'payByAccount';

   		ccForm.submitted = false;

   		$scope.$watchCollection("[ccForm.$valid,accountType]",function(){
   			var hasPayData = $scope.ccForm.$valid; // Overall form validity

   			// If paying by account, bypass normal form validation
   			if($scope.accountType == 'payByAccount'){
   				hasPayData = true;
   			}

   			$scope.continuePayment = hasPayData;
   		});
   	};

   	/*
   	$scope.validateCardType = function(ev) {

   		var keyCode = ev.charCode || ev.keyCode || 0;

		if ( (keyCode >= 48 && keyCode <= 57) || (keyCode >= 96 && keyCode <= 105) || keyCode === 8 || keyCode === 86 ) {
	   		var input = angular.element(ev.currentTarget).val();
			$scope.cardType = '';

	   		angular.forEach($scope.ccValidationData, function(i){


	   			if ( typeof i.digitValidation !== 'undefined' && i.digitValidation.length > 0 ) {
	   				angular.forEach(i.digitValidation, function(x){

	   					if (typeof input !== 'undefined') {

		   	   				if ( parseInt($scope.getInputByDigits(ev, i.digitValidation.length)) === parseInt(x) && input.length === parseInt(i.lengthValidation)) {
		   	   					$scope.cardType = i.cardType;
		   	   					$scope.cvvLength = i.cvvValidation;

			   	   				if ( keyCode !== 8 ) {
			   	   					return false;
			   	   				}
		   	   				}
	   					}
	   				});
	   			}

	   			if ( typeof i.seriesValidation !== 'undefined' && i.seriesValidation.length > 0 ) {

	   				angular.forEach(i.seriesValidation, function(y){
	   					var series = y.split(':');
	   					var min = series[0], max = series[1];

	   					if ( typeof input !== 'undefined' ) {

		   					if ( parseInt($scope.getInputByDigits(ev, min.length)) >= parseInt(min) && parseInt($scope.getInputByDigits(ev, max.length)) <= parseInt(max) && input.length === parseInt(i.lengthValidation) ) {
		   						$scope.cardType = i.cardType;
		   	   					$scope.cvvLength = i.cvvValidation;

		   						if ( keyCode !== 8 ) {
		   							return false;
		   						}
		   					}
	   					}
	   				});
	   			}

	   		});
		} else {
			return false;
		}
   	}
   	*/



   	//get the input value based on the digits
   	$scope.getInputByDigits = function(ev, digit) {
   		var input = angular.element(ev.currentTarget).val();
   		var i = [];

   		for (var x = 0; x < parseInt(digit); x++ ) {
   			i.push(input[x]);
   		}

   		return i.join('');
   	}

	$scope.serviceRequestInit = function(){

	};

	$scope.userInit = function(){
		$scope.user = JSON.parse($('#userData').html());
		$scope.user.phoneNumber = $('#phoneNumber').val();
		$scope.noInactiveBU = true;


		$scope.$watch('createUser.$valid',function(validity){
			if(validity){
				$scope.notComplete = false;
			}
		});

		$scope.checkForInactiveBU();
	};

	/* validate profile form terms and condition checkbox in edit and create user page.
	 * @author: lester.l.gabriel
	 * */
	$scope.invokeUnsavedChangesPopUp = function(){
		if($scope.checkUserProfileFormIfUpdated()){
            $('body').addClass('unsaved-changes');
			$scope.profileFormValid = true;
		}else{
			$scope.profileFormValid = false;
		}
	}

	$scope.checkUserProfileFormIfUpdated = function(){

		if(typeof $scope.user.agreeTerms !== 'undefined' && typeof $scope.user.ofAge !== 'undefined'){

			if ( $('#register_phoneNumber').val().length > 0 ) {
				var phonenopattern = /^\({0,1}((0)(4)){0,1}\){0,1}(\ ){0,1}[0-9]{2}(\ ){0,1}[0-9]{2}(\ ){0,1}[0-9]{1}(\ ){0,1}[0-9]{3}$/;
				var phoneNoErrorText = $('.phone-number.error');
				//check if mobile number is valid
				if ($scope.isMobileNumberValid== undefined) {
					$('#register_phoneNumber').blur();
				}
				else if($scope.isMobileNumberValid){
					if(phonenopattern.test($('#register_phoneNumber').val())){
						phoneNoErrorText.addClass('hide');
						return true;

					}
					else{
						phoneNoErrorText.removeClass('hide');
						return false;
					}

				}
			} else {
				return true;
			}

		}
	}

	$scope.printForm = function (type) {
		var type = $scope.sr.type,
			form = {};

		form = $scope.sr;
		form.request = $scope[type];
	};

	$scope.$watch('editUser.$valid',function(validity){
		if(validity){
			$scope.notComplete = false;
		}
	});

	$scope.checkForInactiveBU = function(){
		  for(state in $scope.user.states){
			  for(venue in $scope.user.states[state].b2bunits){
				  if(!$scope.user.states[state].b2bunits[venue].active){
					  $scope.noInactiveBU = false;
				  }
			  }
		  }
	};

    $scope.isAllInactive = false;
	$scope.isAllSelected = function(states) {
        for(state in states){
             for(venue in states[state].b2bunits){
                 if(states[state].b2bunits[venue].active){
                     $scope.isAllInactive = true;
                     return true;
                 }
             }
        }
	}

	$scope.openDisableModal = function (states) {
	    $.magnificPopup.open({
           items: {
             src: '#deactivateUserPopup',
             type: 'inline'
           },
           removalDelay: 500,
           mainClass: 'mfp-slide',
           closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>',
        });
	}

    $scope.closeDisableModal = function (toggle, states) {
        $.magnificPopup.close();
        $scope.selectAllState(toggle, states);
    }

	$scope.selectAllState = function(toggle, states) {
		for(var i = 0; i < states.length; i++) {
		    // skip disabled states
			if (!states[i].disabled) {
				states[i].selected = toggle;
			}
			$scope.selectAll(states[i]);
		}
	}

	$scope.selectAll = function (state) {
		if(state.selected){
			for(venue in state.b2bunits){
				if (state.b2bunits[venue].active) {
					state.b2bunits[venue].selected = true;
				}
			}
		} else {
			for(venue in state.b2bunits){
				if (state.b2bunits[venue].active) {
					state.b2bunits[venue].selected = false;
				}
			}
		}
	};

	$scope.someSelected = function () {
	  var permissions = false;
	  for(p in $scope.user.permissions){
	  	if($scope.user.permissions[p]){
	  		permissions = true;
	  	}
	  }

	  return permissions;
	};

	$scope.stateDisabled = function(state){

		for(bu in state.b2bunits){
			if(state.b2bunits[bu].active === true){
				return false;
			}
		}

		return true;
	};

	$scope.stateSelected = function(state){
		for(bu in state.b2bunits){
			if(state.b2bunits[bu].selected !== true){
				return false;
			}
		}
		return true;
	};

	//phone number masking validation
	if($('body').hasClass('page-editUser') || $('body').hasClass('page-createUser')){

	$scope.user = {};
	var $this, dato, val, phoneMobileArray;
	$('#register_phoneNumber').css('background','#fff');

	$('#register_phoneNumber').on('focus',function(){
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
	}).keydown(function(e){
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
		return (key == 8 ||
				key == 9 ||
				key == 46 ||
				(key >= 48 && key <= 57) ||
				(key >= 96 && key <= 105));
	}).blur(function(){
		$this = $(this);
		var phoneNumberErrorText = $('.phone-number.error');
		var mobileNumberPattern = /[0-9 ]$/g;


		if($this.val() === '04' || $this.val() === ''){
			$this.val('');
			$('#mobileNumber').val('');
			phoneNumberErrorText.addClass('hide');
			$scope.isMobileNumberValid = true;
		}else{
			if(($this.val().trim().charAt(0) + $this.val().trim().charAt(1) === '04' && $this.val().length === 12 && mobileNumberPattern.test($this.val()))){
				phoneNumberErrorText.addClass('hide');
				$scope.isMobileNumberValid = true;
			}else{
				phoneNumberErrorText.removeClass('hide');
				$scope.isMobileNumberValid = false;
			}
		}
	});

		/*
	$scope.user = {};
	var $this, dato, val, phoneMobileArray;
	$('#register_phoneNumber').css('background','#fff');
	$('#register_phoneNumber').on('focus',function(){
		$this = $(this);

		if($this.val() === '04__ ___ ___' || $this.val().length === 0){
				$this.val('04__ ___ ___');
				$this[0].setSelectionRange(2,2);
		}else{
			val = $this.val();
			phoneMobileArray = val.split('');
			indexOfUnderscore = phoneMobileArray.indexOf('_');
			$this[0].setSelectionRange(indexOfUnderscore, indexOfUnderscore);
		}

	}).on('keypress',function(myEvento){
		  if ((myEvento.charCode >= 48 && myEvento.charCode <= 57) || myEvento.keyCode === 9 || myEvento.keyCode === 10 || myEvento.keyCode === 13 || myEvento.keyCode === 8 || myEvento.keyCode === 116 || myEvento.keyCode === 46 || (myEvento.keyCode <= 40 && myEvento.keyCode >= 37)) {
		    dato = true;
		  }else{
		    dato = false;
		  }
		  return dato;
	}).on('keyup',function(){

		$this = $(this);

		var myMask = '____ ___ ___';
		var myCaja = document.getElementById('register_phoneNumber');
		var myText = '';
		var myNumbers = [];
		var myOutPut = '';
		var theLastPos = 1;

		myText = myCaja.value;

		  for (var i = 0; i < myText.length; i++) {
		    if (!isNaN(myText.charAt(i)) && myText.charAt(i) !== ' ') {
		      myNumbers.push(myText.charAt(i));
		    }
		  }

		  for (var j = 0; j < myMask.length; j++) {
		    if (myMask.charAt(j) === '_') { //replace "_" by a number
		      if (myNumbers.length === 0){
		        myOutPut = myOutPut + myMask.charAt(j);
		      }else {
		        myOutPut = myOutPut + myNumbers.shift();
		        theLastPos = j + 1; //set caret position
		      }
		    } else {
		      myOutPut = myOutPut + myMask.charAt(j);
		    }
		  }

		  document.getElementById('register_phoneNumber').value = myOutPut;
		  document.getElementById('register_phoneNumber').setSelectionRange(theLastPos, theLastPos);

	}).on('blur',function(){
		$this = $(this);
		val = $this.val().replace(/[ _]/g,'');
		var phoneNumberErrorText = $('.phone-number.error');
		var mobileNumberPattern = /[0-9 ]$/g;

		if($this.val() === '04__ ___ ___' || $this.val() === '____ ___ ___'){
			$this.val('');
			$('#mobileNumber').val('');
			phoneNumberErrorText.addClass('hide');
		}else{
			if(($this.val().trim().charAt(0) + $this.val().trim().charAt(1) === '04' && $this.val().length === 12 && mobileNumberPattern.test($this.val()))){
				phoneNumberErrorText.addClass('hide');
			}else{
				phoneNumberErrorText.removeClass('hide');
				$scope.editUser.$setPristine();
				$scope.editUser.$setUntouched();
			}

			$('#mobileNumber').val(val);
		}

	});
	*/
	} /* check when in create and edit user page */

	$scope.emailChecker = function(email){
		$('body').addClass('loading');
		var url = $('.checkUserUrl').val(),
			email = $('.email-checker').val();

		if($scope.createUser.email.$valid ){
			var data = {};
			data.email = email;
			$http.get(url, {params: data}).success(function(result){
				if(result.self){
					$scope.user.thisZADP=false;
					$scope.user.exists = false;
					$scope.user.self = result.self;
				} else {
					$scope.user.self = result.self;
					if(result.exists){
						if( !result.thisZADP){
							$scope.emailExists = true;
							$scope.user.thisZADP=result.thisZADP;
							$scope.user.exists=result.exists;
						}else{
							$scope.emailExists = true;
							$scope.user = result;
						}
					} else {
						$scope.user.thisZADP=false;
						$scope.user.exists=false;
						$scope.user.states=result.states;
						$scope.user.permissions=result.permissions;
					}
				}

				$('body').removeClass('loading');
			}).error(function(){
			});
		} else {
			$('body').removeClass('loading');
		}
	};

	$scope.userActivate = function(url, cuid, state){
		var data = {};

		data.businessCustomerActive = state;
		data.businessCustomerUid = $('#deleteOrDeactivatePopup').find('.btn-primary').data('cuid');
		data.businessUnitId = $('#deleteOrDeactivatePopup').find('.btn-primary').data('buid');

		$http.post(url, data).success(function(result){

			// Populate global message
			// $scope.$parent.messagetype = result.messageType;
			// $scope.$parent.message = result.message;

			globalMessageService.setMessage(result.messageType,result.message);

			$.magnificPopup.close(); // Close the popup

			// Change from Active to Inactive
			handleInactive();
		}).error(function(){
		});
	};

	$scope.createUserSubmit = function(form){
        var url = $('#sabmCreateUserForm').attr("action");

        if(!$scope.someSelected()){
               form.$valid = false;
        }
        $scope.user.b2bUnitId = $('.b2bUnitId').val();
        if(form.$valid){
            var checkAccountUrl = '/sabmStore/en/register/isExistingUser/' + form.email.$modelValue + '?createUser=true';
            $http.get(checkAccountUrl).success(function (data) {
               if (data === 'TRUE') {
                   $.magnificPopup.open({
                       items: {
                         src: '#forgotpwd-popup',
                         type: 'inline'
                       },
                       callbacks: {
                           open: function() {
                               $('#submit_button').on('click', function () {
                                    $http.post(url, $scope.user).success(function(result){
                                         if (result.messageType === 'good') {
                                                return $window.location.href = $('.baseUrl').val()+result.redirectUrl+'?message='+result.message + '&messageType=' + result.messageType;
                                         } else {
                                                // $scope.$parent.messagetype = result.messageType;
                                                // $scope.$parent.message = result.message;
                                                $.magnificPopup.close();
                                                globalMessageService.setMessage(result.messageType,result.message);
                                                $('html,body').animate({scrollTop:0},'500');
                                         }
                                   }).error(function(){
                                        $.magnificPopup.close();
                                   });
                               })
                           },
                       }
                   });
               } else {
                   $http.post(url, $scope.user).success(function(result){
                        if (result.messageType === 'good') {
                               return $window.location.href = $('.baseUrl').val()+result.redirectUrl+'?message='+result.message + '&messageType=' + result.messageType;
                        } else {
                               // $scope.$parent.messagetype = result.messageType;
                               // $scope.$parent.message = result.message;
                               globalMessageService.setMessage(result.messageType,result.message);

                               $('html,body').animate({scrollTop:0},'500');
                        }
                  }).error(function(){
                  });
               }
           });
           $scope.notComplete = false;
        } else {
               $scope.notComplete = true;
        }
    };

	$scope.deleteUser = function($event, cuid, buid, url){
		var table = $($event.currentTarget).parents('table').data('footable'),
			row = $($event.currentTarget).parents('tr:first'),
			data = {
			'businessCustomerUid': cuid,
			'businessUnitId': buid
		};

		if($scope.bdeViewOnly) {
			return;
		}

        // add class to currentTarget
        $($event.currentTarget).closest('tr').addClass('current-selected');
		$http.post(url, data).success(function(result){
			if(result.openModal){

				$.magnificPopup.open({
					items:{
				        src: '#deleteOrDeactivatePopup',
				        type: 'inline'
					},
			        removalDelay: 500,
            		mainClass: 'mfp-slide',
            		closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>',
            		callbacks: {
            		    close: function() {
                            $('.current-selected').removeClass('current-selected');
                        }
            		}
				});

				// Store the customer ID to access from the modal
				$('#deleteOrDeactivatePopup').find('.btn').attr('data-cuid',data.businessCustomerUid);
				// Add business id attr to button in modal
				$('#deleteOrDeactivatePopup').find('.btn').attr('data-buid',data.businessUnitId);

				var btnPrimary = $('#deleteOrDeactivatePopup').find('.btn-primary');
				$('#businessUnitDetailTagDeleteUser').attr('value',data.businessCustomerUid);
			} else {
				// Populate global message
				globalMessageService.setMessage(result.messageType,result.message);
				// Change from Active to Inactive
                handleInactive();
			}
		}).error(function(){
		    $.magnificPopup.close(); // Close the popup
		    $('.current-selected').removeClass('current-selected');
		});
	};

	$scope.removeUser = function(url){

		var data = {};
		data.businessCustomerUid = $('#deleteOrDeactivatePopup').find('.btn-primary').data('cuid');

		$http.post(url, data).success(function(result){

			// Populate global messasge
			globalMessageService.setMessage(result.messageType,result.message);

			$('.current-selected').remove(); // Remove the user row from table
			$.magnificPopup.close(); // Close the popup
			$('html,body').animate({scrollTop:0},'500'); // Scroll to top to show message

		}).error(function(){
		});
	};

	$scope.editUserSubmit = function(form){
        var url = $('#sabmEditUserForm').attr("action");

        if(!$scope.someSelected()){
            form.$valid = false;
        }
        $scope.user.b2bUnitId = $('.b2bUnitId').val();
        $scope.user.currentEmail = $('.currentEmail').val();
        if(form.$valid){
            var customer = JSON.parse($('#customerData').text())
            var checkAccountUrl = '/sabmStore/en/register/isExistingUser/' + form.email.$modelValue + '?createUser=false';
			$scope.user.active = true; // it is always true
            $http.get(checkAccountUrl).success(function (data) {
               if (data === 'TRUE') {
                   $.magnificPopup.open({
                       items: {
                         src: '#forgotpwd-popup',
                         type: 'inline'
                       },
                       callbacks: {
                           open: function() {
                               $('#submit_button').on('click', function () {
                                    $http.post(url, $scope.user).success(function(result){
                                        if (result.messageType === 'good') {
                                            return $window.location.href = $('.baseUrl').val()+result.redirectUrl+'?message='+result.message + '&messageType=' + result.messageType;
                                        } else {

                                            globalMessageService.setMessage(result.messageType,result.message);

                                            $('html,body').animate({scrollTop:0},'500');
                                        }
                                    }).error(function(){
                                    });
                               })
                           },
                       }
                   });
               } else {
                   $http.post(url, $scope.user).success(function(result){
                        if (result.messageType === 'good') {
                            return $window.location.href = $('.baseUrl').val()+result.redirectUrl+'?message='+result.message + '&messageType=' + result.messageType;
                        } else {

                            globalMessageService.setMessage(result.messageType,result.message);

                            $('html,body').animate({scrollTop:0},'500');
                        }
                    }).error(function(){
                    });
               }
           });

           $scope.notComplete = false;
        } else {
            $scope.notComplete = true;
        }
    };

	$scope.popDeleteUser = function(b2bUnitId) {
		$('#deleteUserPopupCustomerUid').attr('value', $('#businessUnitDetailTagDeleteUser').attr('value'));
		$('#deleteUserPopupB2bUnitUid').attr('value', b2bUnitId);

		$.magnificPopup.open({
			items:{
		        src: '#deleteUserPopup',
		        type: 'inline'
			},
			modal: true,
			removalDelay: 500,
			mainClass: 'mfp-slide'
		});
	};

    function handleInactive() {
        $('.current-selected .current-active').addClass('hide');
        $('.current-selected .current-inactive').removeClass('hide');
        $('.current-selected .active-inactive').attr('data-value', 0);
        // update user status
        $('.current-selected .bde-view-only').attr('data-active', 'false');
        $('.current-selected').removeClass('current-selected');
        $('html,body').animate({scrollTop:0},'500'); // Scroll to top to show message
    }

	$('#confirm-delete-user').on('click',function(){
		var $deleteUserForm = $('#deleteUserForm');
		$deleteUserForm.submit();
	});
}]);
