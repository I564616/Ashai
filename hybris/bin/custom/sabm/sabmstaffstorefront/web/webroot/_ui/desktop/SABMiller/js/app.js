
/* ===== Modules ===== */



var CUB = angular.module('CUB', ['ngAnimate', 'NgSwitchery', 'breakpointApp', 'ngSanitize', 'ui.bootstrap'])

.service('angularUtils',  function() {
	return { 
		arrayContains: function(array, item){

				for (var i in array) {
					if (array[i] === item) {
						return true;
					}
				}
				return false;

		}
	}
})
.service('globalMessageService', function(){
	var currentMessageType = '';
	var currentMessage = '';
	return {
		setMessage: function(messageType, message) {
			currentMessageType = messageType;
			currentMessage = message;
		},
		getMessageType: function() {
			return currentMessageType
		},
		getMessage: function() {
			return currentMessage
		},
		watch: function() {
			return {
				currentMessageType: currentMessageType,
				currentMessage: currentMessage
			}
		}
	}
});

CUB.config(["$httpProvider", function(provider) {
  provider.defaults.headers.post['CSRFToken'] = ACC.config.CSRFToken;
}]);

/* ===== Controllers ===== */

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
	
	$('.allowNumericOnly').on('keyup', function(ev){
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
			console.log('invalid expiry month and year');
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
			console.log("str =" +str); 
			console.log("email =" +email); 
			console.log("firstName =" +firstName);
			  $scope.BdeOrderDetailsForm['customers'].push({'email': email,'firstName': firstName});
		});
		
		$scope.BdeOrderDetailsForm.emailText = $scope.orderDetailsTextarea;
	
		
		 $http.post($('#updateBDEOrderDetailsUrl').val(), $scope.BdeOrderDetailsForm).success(function(result, status){ 
			 $('#bdeOrderModal3').css('visibility','hidden');
			 $('.modal-backdrop').removeClass('modal-backdrop fade in');
			 $('body').removeClass('modal-open');
			
		 }).error(function(data, status){
			  console.log('error');
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
		console.log('called');
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
		console.log($scope.user);
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

		console.log(form);
	};

	$scope.$watch('editUser.$valid',function(validity){
		if(validity){
			$scope.notComplete = false;
		}
	});

	$scope.checkForInactiveBU = function(){
		console.log('made it');
		  for(state in $scope.user.states){
			  for(venue in $scope.user.states[state].b2bunits){
				  if(!$scope.user.states[state].b2bunits[venue].active){
					  $scope.noInactiveBU = false;
				  }
			  }
		  }
	};

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
	  var permissions = false,
			units = false;

	  // If the length of states and b2bunits is 1, set the b2bunit attribute selected to true
	  if ($scope.user.states.length == 1 && $scope.user.states[0].b2bunits.length == 1) {
		  $scope.user.states[0].b2bunits[0].selected=true;
		  units = true;
	  } else {
		  for(state in $scope.user.states){
			  for(venue in $scope.user.states[state].b2bunits){
				  if($scope.user.states[state].b2bunits[venue].selected){
					  units = true;
				  }
			  }
		  }
	  }


	  for(p in $scope.user.permissions){
	  	if($scope.user.permissions[p]){
	  		permissions = true;
	  	}
	  }

	  if(permissions && units){
	  	return true
	  } else {
	  	return false;
	  }
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
				console.log(result.self);
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
				console.log('failure');
			});
		} else {
			$('body').removeClass('loading');
		}
	}; 

	$scope.userActivate = function(url,cuid,state){
		var data = {};

		data.businessCustomerActive = state;
		data.businessCustomerUid = $('#deleteOrDeactivatePopup').find('.btn-primary').data('cuid');

		$http.post(url, data).success(function(result){

			// Populate global message
			// $scope.$parent.messagetype = result.messageType;
			// $scope.$parent.message = result.message;

			globalMessageService.setMessage(result.messageType,result.message);

			$.magnificPopup.close(); // Close the popup

			// Change from Active to Inactive
			$('.current-selected .current-active').addClass('hide');
			$('.current-selected .current-inactive').removeClass('hide');
			$('.current-selected .active-inactive').attr('data-value', 0);
			$('.current-selected').removeClass('current-selected');

			$('html,body').animate({scrollTop:0},'500'); // Scroll to message
		}).error(function(){
			console.log('failure');
		});
	};

	$scope.createUserSubmit = function(form){
        var url = $('#sabmCreateUserForm').attr("action");

        if(!$scope.someSelected()){
               form.$valid = false;
        }
        $scope.user.b2bUnitId = $('.b2bUnitId').val();
        if(form.$valid){
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
                     console.log('failure');
               });
               $scope.notComplete = false;
        } else {
               $scope.notComplete = true;
        }
	};

	$scope.deleteUser = function($event,cuid,buid,url){
		var table = $($event.currentTarget).parents('table').data('footable'),
			row = $($event.currentTarget).parents('tr:first'),
			data = {
			'businessCustomerUid': cuid,
			'businessUnitId': buid
		};

		if($scope.bdeViewOnly) {
			return;
		}


		$http.post(url, data).success(function(result){
			if(result.openModal){

				$.magnificPopup.open({
					items:{
				       src: '#deleteOrDeactivatePopup',
				       type: 'inline'
					},
			        removalDelay: 500,
            		mainClass: 'mfp-slide',
            		closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>'
				});

				// Store the customer ID to access from the modal
				$('#deleteOrDeactivatePopup').find('.btn').attr('data-cuid',data.businessCustomerUid);
				$('#businessUnitDetailTagDeleteUser').attr('value',data.businessCustomerUid);

				// Label row to be removed or deactivated
				$($event.currentTarget).closest('tr').addClass('current-selected');

			} else {
				// Populate global message
				globalMessageService.setMessage(result.messageType,result.message);

				table.removeRow(row);  // Remove the user row from table
				$.magnificPopup.close(); // Close the popup
				$('html,body').animate({scrollTop:0},'500'); // Scroll to top to show message
			}
		}).error(function(){
			console.log('failure');
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
			console.log('failure');
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
			$http.post(url, $scope.user).success(function(result){
				if (result.messageType === 'good') {
					return $window.location.href = $('.baseUrl').val()+result.redirectUrl+'?message='+result.message + '&messageType=' + result.messageType;
				} else {

					globalMessageService.setMessage(result.messageType,result.message);

					$('html,body').animate({scrollTop:0},'500');
				}
			}).error(function(){
				console.log('failure');
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

	$('#confirm-delete-user').on('click',function(){
		var $deleteUserForm = $('#deleteUserForm');
		$deleteUserForm.submit();
	});
}]);
CUB.controller('dealsCtrl', ['$element','$scope','$http','$sce', '$window', '$filter','angularUtils','orderByFilter', function($element,$scope, $http, $sce, $window, $filter, angularUtils, orderBy){
	var data = JSON.parse($('#dealsData').html());
	var availDates = $('.delivery-header-input').attr('data-enabled-dates');
	var selectedDate = $('.delivery-header-input').attr('data-selected-timestamp');
	
	$scope.init = function(sort){
		if(sort){
		for(var d in data){

			data[d].brands = data[d].brands.sort();
		}

		$scope.deals = orderBy(data,[$scope.sortingBrands,$scope.sortingProductName, $scope.sortingQty]);
		} else {
			$scope.deals = data;
		}

		$scope.dealsActivated = [];
		$scope.dealToggle = true;  // Initialize each deal toggle
		$scope.pageInactive = false;
		if($('.bd-portal').length){
			$scope.userStatus = $('#dealPageWrapper').attr('data-status');
		}

		if($('.view-only-mode').length){
			$scope.bdeViewOnly = true;
		}
	};

	$scope.filterInit = function () {
		$scope.enabledDates = JSON.parse(availDates);
		$scope.limitfilter = {
			brands: 5,
			categories: 5,
			badges: 5
		}; // Set Filter Limit #

		$scope.showing = {
			brands: false,
			categories: false,
			badges: false
		};

		$scope.filterDate = selectedDate;
		$scope.listAllBrands();
		$scope.listAllCategories();
		$scope.listAllBadges();
		$scope.hasBadges();
	};

	$scope.dealInit = function(deal,toggle){
		deal.dealToggle = toggle;
		deal.multiplier = 0;
		deal.daysRemain = $scope.daysRemain(deal);
		var dealFirstBaseProduct;
		deal.dealImage='';
		deal.dealImageTitle='';
		deal.dealImageUrl='';


    if(deal.ranges.length === 1 && deal.ranges[0].baseProducts.length === 1 && deal.selectableProducts.length === 0){
      console.log('single');
      deal.single = true;
      dealFirstBaseProduct = deal.ranges[0].baseProducts[0];

    } else {
      console.log('multi');
      deal.single = false;
      var ranges = orderBy(deal.ranges,'title');
      var baseProducts = orderBy(ranges[0].baseProducts,'title');
      dealFirstBaseProduct = baseProducts[0];
    }

    if(dealFirstBaseProduct){
    	deal.firstBaseProduct = dealFirstBaseProduct;
        if(dealFirstBaseProduct.image){
    	deal.dealImage = dealFirstBaseProduct.image;

    	if(dealFirstBaseProduct.image.title){
    		deal.dealImageTitle=dealFirstBaseProduct.image.title
    		}
    	}
        deal.dealImageUrl=dealFirstBaseProduct.url;
        }

	};

	$scope.rangeInit = function(deal,range){
      range.multiply = 0;
      range.totalQty = $scope.calcTotalQty(deal,range);
	};

  $scope.clearFilter = function() {
	    $scope.brandsChecked = [];
	    $scope.categoriesChecked = [];
	    $scope.badgesChecked = [];

	$scope.calculateCategoriesMap();
	$scope.calculateBadgesMap();
	$scope.calculateBrandsMap();

	$('.facet-check').each(function(){$(this).attr('checked',false)});
}

	$scope.getHtml = function(html) {
	      return $sce.trustAsHtml(html)
	}

	$scope.currentDate = function() {
		var now = new Date();
		now.setHours(0);
		now.setMinutes(0);
		now.setSeconds(0);
		return Date.parse(now);
	};
	$scope.togglePDPLimit = {
		limit: 3,
		open: false
	}; // Initialize deal limi toggle on PDP

	$scope.brandsMap = {};
	$scope.categoriesMap = {};

	$scope.brandsOnPage = []; // Brands represented on deals page
	// $scope.sortedBrands = orderBy($scope.brandsOnPage,$scope.filterSorting);
	// console.log($scope.brandsOnPage);

	$scope.brandsChecked = []; // Brands selected using filter
	$scope.brandsFilters = []; // Brands showing in filter

	$scope.categoriesOnPage = []; // Categories represented on deals page
	$scope.categoriesChecked = []; // Categories selected using filter
	$scope.categoriesFilters = []; // Categories showing in filter

	$scope.badgesOnPage = []; // Badges represented on deals page
	$scope.badgesChecked = []; // Badges selected using filter


	// =================================================
	// ==== SORTING
	// =================================================

	$scope.sortingBrands = function(item){
		if(item.ranges.length === 1){
			return item.brands[0];
		} else {
			var ranges = item.ranges;
			var sortedRange = orderBy(ranges,'title');
			return sortedRange[0].title;
		}
	}; 
	$scope.sortingProductName = function(item){
		if(item.ranges.length === 1){
			return item.ranges[0].baseProducts[0].title;
		} else {
			var ranges = item.ranges;
			var sortedRange = orderBy(ranges,'title');
			return sortedRange[0].baseProducts[0].title;
		}
	};

	$scope.sortingQty = function(item){
		if(item.ranges.length === 1){
			return item.ranges[0].minQty;
		} else {
			var ranges = item.ranges;
			var sortedRange = orderBy(ranges,'title');
			return sortedRange[0].minQty;
		}
	};

	$scope.brandSorting = function(item){
		return $scope.brandsMap[item];
	}
	$scope.categorySorting = function(item){
		return $scope.categoriesMap[item];
	}
	$scope.badgeSorting = function(item){
		return $scope.badgesMap[item];
	}

	// =================================================
	// ==== ADD TO CART
	// =================================================

	$scope.addToCart = function(deal,page){
	    var that = $('#recommendation-addToOrder-' + deal.code);
	    if(that.length == 0 || !that.hasClass('disabled')){
	            if (that.length > 0) {
            		    that.addClass('disabled');
            		}
        		var data = {
        			"dealCode": deal.code
        		};

        		var products = [];

        		deal.dealToggle = false;

        		for(range in deal.ranges){ // Loop over ranges in deal
        			for(prod in deal.ranges[range].baseProducts){ // Loop of products in range

        				if ($('.cartRecommendations').length > 0) { // User cannot modify the quantity in Cart page, so no checking for NEW quantity is required
        					var baseProd = {};
        					baseProd.productCodePost = deal.ranges[range].baseProducts[prod].productCode;
        					baseProd.qty = deal.ranges[range].baseProducts[prod].qty;
        					products.push(baseProd);
        				}
        				else {
        					if(deal.ranges[range].baseProducts[prod].newQty > 0){ // Only add products with qty more than zero
        						var baseProd = {};
        						baseProd.productCodePost = deal.ranges[range].baseProducts[prod].productCode;
        						baseProd.qty = deal.ranges[range].baseProducts[prod].newQty;
        						products.push(baseProd);
        					}
        				}
        			}
        		}

        		if(deal.selectableProducts){
        			if(deal.selectableProducts.length != 0){
        				data.dealCode = deal.selectedItem;
        			}
        		}

        		data.baseProducts = products;//update data.product to data.baseProducts
        		//the dealCurrentUrl define in the dealsRow
        		$scope.dealCurrentUrl = $('.dealCurrentUrl').val();

        		$http.post($scope.dealCurrentUrl, data).success(function(result, status){

                    var errorParHtml = $('#simulationErrors');

                    if(result.remainingValue){
                    	deal.remainingValue = result.remainingValue
                    } else if(result.remainingQty){
                    	deal.remainingQty = result.remainingQty;
                    }

                    if(result.addToCartForErrorLayer){
                        errorParHtml.html(result.addToCartForErrorLayer);
                    }

        		    // Deleted for googleTagManager
        		    // Google Analytics tracking
        		    /*if(page === 'deals'){
        		    	trackAddDealDeals();
        		    } else if('pdp'){
        		    	trackAddDealPDP();
        		    }*/

        			ACC.product.displayAddToCartPopup(result);

        			// Deals Recommendation - Start
        			var that = $('#recommendation-addToOrder-' + deal.code);
        			if (that.length > 0) {
        				rm.recommendation.updateRecommendation(that, 'ACCEPTED');
        				if ($('.cartRecommendations').length > 0) {
        					if ($('.cartRow').length > 0) {
        						rm.recommendation.resetCartForRecommendation(deal,'deal');
        					} else {
        						rm.cart.refreshPage();
        					}
        				}


        			}
        			//rm.recommendation.displayRecommendationCount(result.recommendationsCount);
        			// Deals Recommendation - End

        			ACC.minicart.refreshMiniCartCount();
        			ACC.common.refreshScreenReaderBuffer();

        		}).error(function(data, status){
        			console.log('error');
        		});
	    }

	};

	$scope.addToRecommendation = function(deal,page){
    		var data = {
    			"dealCode": deal.code
    		};
        var recommendationAction = $('#addRecommendation'+deal.code);

    		var products = [];

        if($(recommendationAction).hasClass('adding')){
          return;
        }
        $(recommendationAction).addClass('adding');

        deal.dealToggle = false;

    		for(range in deal.ranges){ // Loop over ranges in deal
    			for(prod in deal.ranges[range].baseProducts){ // Loop of products in range

    				//if(deal.ranges[range].baseProducts[prod].newQty > 0){ All products in the complex deal should be added to recommendation
    					var baseProd = {};
    					baseProd.productCodePost = deal.ranges[range].baseProducts[prod].productCode;
    					baseProd.qty = deal.ranges[range].baseProducts[prod].newQty;
    					baseProd.unit = deal.ranges[range].baseProducts[prod].uomCode;
    					products.push(baseProd);
    				//}
    			}
    		}

    		if(deal.selectableProducts){
    			if(deal.selectableProducts.length != 0){
    				data.dealCode = deal.selectedItem;
    			}
    		}

    		data.baseProducts = products;//update data.product to data.baseProducts
    		//the dealCurrentUrl define in the dealsRow
    		$scope.recommendationCurrentUrl = $('.recommendationCurrentUrl').val();

    		$http.post($scope.recommendationCurrentUrl, data).success(function(result, status){
            $(recommendationAction).find('#recommendationStar').removeClass('icon-star-normal').addClass('icon-star-add');
            $(recommendationAction).find('#recommendationText').html($('#addedText').html());
            setTimeout(function ()
            {
                $(recommendationAction).find('#recommendationStar').removeClass('icon-star-add').addClass('icon-star-normal');
                $(recommendationAction).find('#recommendationText').html($('#addText').html());
                for(range in deal.ranges){ // Loop over ranges in deal
                  for(prod in deal.ranges[range].baseProducts){ // Loop of products in range
                    if(deal.ranges[range].baseProducts[prod].newQty > 0){
                            deal.ranges[range].baseProducts[prod].newQty=deal.ranges[range].baseProducts[prod].qty;
                  $('#qty-'+deal.code+'-'+deal.ranges[range].baseProducts[prod].productCode)[0].value=deal.ranges[range].baseProducts[prod].qty;
                    }
                  }
                }
                $(recommendationAction).removeClass('adding');
            }, 5000);
            rm.recommendation.displayAddToRecommendationPopup(result);
    		}).error(function(data, status){
    			console.log('error');
    		});
    	};



	// =================================================
	// ==== TOGGLE FUNCTIONS
	// =================================================

	// Show/Hide individual deals
	$scope.toggleThis = function(deal, obj){
		deal.dealToggle = !deal.dealToggle;

		// added for GTM - start
		if (!$(obj.currentTarget).hasClass('wasclicked')) {
			$(obj.currentTarget).addClass('wasclicked');
			rm.tagManager.addProductImpressionListener();
		}
		// added for GTM - end
	};

	// Show/Hide all and override individual deal show/hide
	$scope.collapsedAllFunc = function(){
		$scope.collapsedAll = !$scope.collapsedAll;

		for(i=0 ; i < $scope.deals.length ; i++){
				$scope.deals[i].dealToggle = $scope.collapsedAll;
		}
	};

	// Show all deals on PDP
	$scope.limitdealsPDP = function(deals){
		if($scope.togglePDPLimit.limit == 3){
			$scope.togglePDPLimit.limit = deals.length;
			$scope.togglePDPLimit.open = true;
		} else {
			$scope.togglePDPLimit.limit = 3;
			$scope.togglePDPLimit.open = false;
		}
	};

	// =================================================
	// ==== FILTER
	// =================================================

	// Side Bar Filter Functions for ng-repeat
	$scope.brandFilterFilter = function(brand){
		if($scope.brandsMap[brand]){
			return true;
		} else {
			if(angularUtils.arrayContains($scope.brandsChecked, brand)){
				$scope.brandsChecked.splice($scope.brandsChecked.indexOf(brand), 1);
			}
		}
	};

	$scope.categoryFilterFilter = function(category){
		if($scope.categoriesMap[category]){
			return true;
		} else {
			if(angularUtils.arrayContains($scope.categoriesChecked, category)){
				$scope.categoriesChecked.splice($scope.categoriesChecked.indexOf(category), 1);
			}
		}
	};

	$scope.badgesFilterFilter = function(badge){
		if($scope.badgesMap[badge]){
			return true;
		} else {
			if(angularUtils.arrayContains($scope.badgesChecked, badge)){
				$scope.badgesChecked.splice($scope.badgesChecked.indexOf(badge), 1);
			}
		}
	};

	// Compile a list of brands/categories/badges on page and push to $scope
	$scope.listAllBrands = function(){
		//for(i=0 ; i < $scope.deals.length ; i++){
		for(i=$scope.deals.length-1 ; i >=0; i--){
			for(j=0; j < $scope.deals[i].brands.length ; j++){
				if(!angularUtils.arrayContains($scope.brandsOnPage,$scope.deals[i].brands[j])){
					$scope.brandsOnPage.push($scope.deals[i].brands[j]);
				}
			}
		}
		$scope.calculateBrandsMap();
	};

	$scope.listAllCategories = function(){
		for(i=0 ; i < $scope.deals.length ; i++){
			if($scope.deals[i].categories !== null && $scope.deals[i].categories !== undefined) {
				for(j=0; j < $scope.deals[i].categories.length ; j++){
					if(!angularUtils.arrayContains($scope.categoriesOnPage,$scope.deals[i].categories[j])){
						$scope.categoriesOnPage.push($scope.deals[i].categories[j]);
					}
				}
			}
		}
		$scope.calculateCategoriesMap();
	};

	$scope.listAllBadges = function(){
		for(i=0 ; i < $scope.deals.length ; i++){
			if($scope.deals[i].badges !== null && $scope.deals[i].badges !== undefined) {
				for(j=0; j < $scope.deals[i].badges.length ; j++){
					if(!angularUtils.arrayContains($scope.badgesOnPage,$scope.deals[i].badges[j])){
						$scope.badgesOnPage.push($scope.deals[i].badges[j]);
					}
				}
			}
		}
		$scope.calculateBadgesMap();
	};

	// Custom filter to Add/Remove listed brands/categories/badges from above compiled arrays
	$scope.addRemoveBrands = function(brand){
		if(angularUtils.arrayContains($scope.brandsChecked, brand)){
			$scope.brandsChecked.splice($scope.brandsChecked.indexOf(brand), 1);
		} else {
			$scope.brandsChecked.push(brand);
		}
		$scope.calculateCategoriesMap();
		$scope.calculateBadgesMap();
	};

	$scope.addRemoveBadges = function(badge){
		if(angularUtils.arrayContains($scope.badgesChecked, badge)){
			$scope.badgesChecked.splice($scope.badgesChecked.indexOf(badge), 1);
		} else {
			$scope.badgesChecked.push(badge);
		}
		$scope.calculateCategoriesMap();
		$scope.calculateBrandsMap();
	};

	$scope.addRemoveCategories = function(category){
		if(angularUtils.arrayContains($scope.categoriesChecked, category)){
			$scope.categoriesChecked.splice($scope.categoriesChecked.indexOf(category), 1);
		} else {
			$scope.categoriesChecked.push(category);
		}
		$scope.calculateBrandsMap();
		$scope.calculateBadgesMap();
	};

	// Custom filters to Add/Remove categories from selected category/brands/badges filter arrays
	$scope.calculateCategoriesMap = function(){
		$scope.categoriesMap = {};

		for(i=0 ; i < $scope.deals.length ; i++){
			if($scope.dealDatesFilter($scope.deals[i]) && $scope.checkBadges($scope.deals[i]) && $scope.checkBrands($scope.deals[i])){
				if($scope.deals[i].categories !== null && $scope.deals[i].categories !== undefined) {
					for(z=0; z < $scope.deals[i].categories.length ; z++){
						if($scope.categoriesMap[$scope.deals[i].categories[z]] !== null && $scope.categoriesMap[$scope.deals[i].categories[z]] !== undefined){
							$scope.categoriesMap[$scope.deals[i].categories[z]] = 1 + $scope.categoriesMap[$scope.deals[i].categories[z]];
						} else {
							$scope.categoriesMap[$scope.deals[i].categories[z]] = 1;
						}
					}
				}
			}
		}
	};

	$scope.calculateBadgesMap = function(){
		$scope.badgesMap = {};

		for(i=0 ; i < $scope.deals.length ; i++){
			if($scope.dealDatesFilter($scope.deals[i]) && $scope.checkCategories($scope.deals[i]) && $scope.checkBrands($scope.deals[i])){
				if($scope.deals[i].badges !== null && $scope.deals[i].badges !== undefined) {
					for(z=0; z < $scope.deals[i].badges.length ; z++){
						if($scope.badgesMap[$scope.deals[i].badges[z]] !== null && $scope.badgesMap[$scope.deals[i].badges[z]] !== undefined){
							$scope.badgesMap[$scope.deals[i].badges[z]] = 1 + $scope.badgesMap[$scope.deals[i].badges[z]];
						} else {
							$scope.badgesMap[$scope.deals[i].badges[z]] = 1;
						}
					}
				}
			}
		}
	};


	$scope.calculateBrandsMap = function(){
		$scope.brandsMap = {};

		for(i=0 ; i < $scope.deals.length ; i++){
			if($scope.dealDatesFilter($scope.deals[i]) && $scope.checkBadges($scope.deals[i]) && $scope.checkCategories($scope.deals[i])){
				if($scope.deals[i].brands !== null && $scope.deals[i].brands !== undefined) {
					for(z=0; z < $scope.deals[i].brands.length ; z++){
						if($scope.brandsMap[$scope.deals[i].brands[z]] !== null && $scope.brandsMap[$scope.deals[i].brands[z]] !== undefined){
							$scope.brandsMap[$scope.deals[i].brands[z]] = 1 + $scope.brandsMap[$scope.deals[i].brands[z]];
						} else {
							$scope.brandsMap[$scope.deals[i].brands[z]] = 1;
						}
					}
				}
			}
		}
	};

	// Check to see if badges/categores/brands exist
	$scope.checkBadges = function(deal){
		if(deal.badges === null || deal.badges === undefined) {
			if($scope.badgesChecked.length === 0) {
				return true;
			} else {
				return false;
			}
		}
		for(j=0; j < deal.badges.length ; j++){
			if(angularUtils.arrayContains($scope.badgesChecked, deal.badges[j]) || $scope.badgesChecked.length === 0){
				return true;
			}
		}
		return false;
	}

	$scope.checkBrands = function(deal){
		if(deal.brands === null || deal.brands === undefined) {
			return true;
		}
		for(j=0; j < deal.brands.length ; j++){
			if(angularUtils.arrayContains($scope.brandsChecked, deal.brands[j]) || $scope.brandsChecked.length === 0){
				return true;
			}
		}
		return false;
	}

	$scope.checkCategories = function(deal){
		if(deal.categories === null || deal.categories === undefined) {
			return true;
		}
		for(j=0; j < deal.categories.length ; j++){
			if(angularUtils.arrayContains($scope.categoriesChecked, deal.categories[j]) || $scope.categoriesChecked.length === 0){
				return true;
			}
		}
		return false;
	}

	$scope.hasBadges = function(){
		if($scope.badgesMap !== null && $scope.badgesMap !== undefined){
			console.log($scope.badgesMap);
			for(i = 0; i < 8; i++){
				if($scope.badgesMap[i] !== undefined && i !== 7){
					return true;
				}
			}
		}
		return false;
	};

	$scope.badges = function(deal,i){
		if(angularUtils.arrayContains(deal.badges,i)){
			return true;
		}
		return false;
	};

	// Check if the deal has a brand/category/badge selected in the brand filter
	$scope.dealBrandsFilter = function(deal){
		if($scope.brandsChecked.length > 0){
			for(i in $scope.brandsChecked){
				if(angularUtils.arrayContains(deal.brands,$scope.brandsChecked[i])){
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	};

	$scope.dealCategoriesFilter = function(deal){
		if($scope.categoriesChecked.length > 0){
			for(i in $scope.categoriesChecked){
				if(angularUtils.arrayContains(deal.categories,$scope.categoriesChecked[i])){
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	};

	$scope.dealBadgesFilter = function(deal){
		if($scope.badgesChecked.length > 0){
			for(i in $scope.badgesChecked){
				if(angularUtils.arrayContains(deal.badges,$scope.badgesChecked[i])){
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	};

	// Custom filter for date picker. Show deal if selected date is within range
	$scope.dealDatesFilter = function(deal){
		var filter = parseInt($scope.filterDate);

		if(filter >= deal.validFrom && filter <= deal.validTo){
			return true;
		}
		return false;
	};

	$scope.dealExpiredFilter = function(deal){
		var filter = parseInt($scope.currentDate());
		if(filter <= deal.validTo){
			return true;
		}
		return false;
	};

	$scope.formatDates = function(date){
		if(date === parseInt(selectedDate)){
			return $filter('date')(date, 'dd-MM-yyyy') + ' (Current)';
		} else {
			return $filter('date')(date, 'dd-MM-yyyy');
		}
	}

	$scope.deliveryDateChange = function(){
		var filter = parseInt($scope.filterDate);

		if(selectedDate != filter){
			$scope.pageInactive = true;
		}else{

			$scope.pageInactive = false;
		}
		$scope.calculateCategoriesMap();
		$scope.calculateBrandsMap();
		$scope.calculateBadgesMap();
	};

	// =================================================
	// ==== DEAL VALIDATION
	// =================================================

	// Caculate the total number of units in any given range
	$scope.calcTotalQty = function(deal,range){
		var totalQty = 0;

		deal.valid = true;

		if(range){
			for(prod in range.baseProducts){
				if(range.baseProducts[prod].newQty){
					totalQty += parseInt(range.baseProducts[prod].newQty);
				}else {
					if(range.baseProducts[prod].newQty == ""){
						deal.valid = false;
					} else{
						totalQty += parseInt(range.baseProducts[prod].qty);
					}
				}
			}
		}

		// Calculate the deals mulitplier quantity for proportional deals
		if(deal.freeProducts || deal.selectedProducts){
			if(totalQty != 0 && range.minQty != 0){
				range.multiply = Math.floor(totalQty / range.minQty);
				var arr = [];
				for(r in deal.ranges){
					if(deal.ranges[r].multiply !=undefined){
						arr.push(deal.ranges[r].multiply);
					}
				}
				deal.multiplier = Math.min.apply(Math, arr); // Get smallest multiplier of ranges and assign to deal
			}
		}
		return totalQty;
	};

	// Check if the number entered in the qty input is valid
	$scope.calcInput = function(deal, range, base){
		var minQty = base.qty;
		range.totalQty = $scope.calcTotalQty(deal,range)

		setTimeout(function(){ // Wait for user to stop typing before checking min qty
			if(base.newQty < minQty || base.newQty === ""){
				base.newQty = minQty; // Reset back to min qty if user enters something less
				range.totalQty = $scope.calcTotalQty(deal,range)
				$scope.$apply();
			}
		},2000);
	};

	// Check if the range meets the min qty required
	$scope.rangeValid = function(range,deal){
		if(range.minQty > 0){
			if(range.minQty > range.totalQty){
				range.rangeIsValid = false;
				return false;
			}
		}

		range.rangeIsValid = true;

		return true;
	};

	// Check if the deal meets min requirements
	$scope.dealValid = function(deal){
		if(deal.selectableProducts != 0) {
			for(i in deal.ranges){
				if(!$scope.rangeValid(deal.ranges[i])){
					return false;
				}
			}

			if(deal.selectedItem){
				return true;
			}

		} else {
			for(i in deal.ranges){
				if(!$scope.rangeValid(deal.ranges[i],deal)){
					return false;
				}
			}

			return true;
		}
	};

	$scope.mapQty = function(deal){
		var totalQty = deal.ranges[0].totalQty;

		if(deal.freeProducts){
			if(deal.freeProducts.length != 0){
				for(prod in deal.freeProducts){ // Loop through free products
					var qtyArr = Object.keys(deal.freeProducts[prod].qty),
						sorted = qtyArr.sort(function(a, b) {
						  return a - b;
						}),
						smallest = Math.min.apply(Math, qtyArr), // Retrieve the smallest of the array
						numFree = deal.freeProducts[prod].qty[smallest];

					if(deal.freeProducts[prod].proportionalFreeGood === true){ // If its a proportional deal
						if($scope.dealValid(deal)){
							numFree = (deal.freeProducts[prod].qty[smallest] * deal.multiplier); // Use smallest of array to mulitply
						} else {
							numFree = (deal.freeProducts[prod].qty[smallest]);
						}
					} else { // Else use normal map
						for(i = 0; i < sorted.length ; i++){ // Loop through free product qty object
							if(totalQty >= sorted[i]){
								numFree = deal.freeProducts[prod].qty[sorted[i]];
							}
						}
					}
				}
			}
		}
		if(deal.selectableProducts){
			if(deal.selectableProducts.length != 0){
				for(prod in deal.selectableProducts){ // Loop through free products
					var qtyArr = Object.keys(deal.selectableProducts[prod].qty),
						sorted = qtyArr.sort(function(a, b) {
						  return a - b;
						}),
						smallest = Math.min.apply(Math, qtyArr), // Retrieve the smallest of the array
						numFree = deal.selectableProducts[prod].qty[smallest];

					if(deal.selectableProducts[prod].proportionalFreeGood === true){ // If its a proportional deal
						if(deal.ranges[0].totalQty < deal.ranges[0].minQty){
							numFree = (deal.selectableProducts[prod].qty[smallest]);
						} else {
							numFree = (deal.selectableProducts[prod].qty[smallest] * deal.multiplier);
						}
					} else { // Else use normal map
						for(key in deal.selectableProducts[prod].qty){ // Loop through free product qty object
							if(totalQty >= key){
								numFree = deal.selectableProducts[prod].qty[key];
							}
						}
					}
				}
			}
		}
		return numFree;
	};

	$scope.daysRemain = function(deal){
		var d = new Date(),
			ms = d.getTime(),
			toDate = deal.validTo,
			delta = Math.abs(toDate - ms) / 1000,
			days = Math.floor(delta / 86400);

		return days + 1;
	};

	// =================================================
	// ==== BD PORTAL SPECIFIC
	// =================================================

	$scope.checkActive = function(deal){
		var dealObj = {
			status: deal.active,
			dealConditionNumber: deal.code
		};

		if($scope.dealsActivated.length != 0){
			var contains = -1;

			for(i in $scope.dealsActivated){
				if($scope.dealsActivated[i].dealConditionNumber === dealObj.dealConditionNumber){
					contains = i;
					break;
				}
			}

			if(contains>=0) {
				$scope.dealsActivated.splice(contains, 1);
			} else {
				$scope.dealsActivated.push(dealObj);
			}
		} else {
			$scope.dealsActivated.push(dealObj);
		}
	};

	$scope.addChangesDeal = function() {
		var data = {};

		data.uid = $('#uid').val();
		$scope.specificDealCurrentUrl = $('#specificDealCurrentUrl').val();
		$scope.specificDealNextUrl = $('#specificDealNextUrl').val();
		data.conditions = $scope.dealsActivated;
		data.CSRFToken = ACC.config.CSRFToken;
		data.saveChanges = false;

		$http.post($scope.specificDealCurrentUrl, data).success(function(result, status) {
			if (result.code === '1') {
				return $window.location.href = $scope.specificDealNextUrl;
			} else {
				console.log(result.message);
			}
		}).error(function(result, status){
			console.log('error');
		});
	};

	$scope.confirmDealsChanges = function() {
		var data = {};
		data.uid = $('#uid').val();
		$scope.specificDealCurrentUrl = $('#specificDealCurrentUrl').val();
		data.conditions = $scope.dealsActivated;
		data.CSRFToken = ACC.config.CSRFToken;
		data.saveChanges = true;
		$http.post($scope.specificDealCurrentUrl, data).success(function(result, status) {
			if (result.code === '1') {
				$("#confirmDealsChangesLink").click();
			} else {
				console.log(result.message);
			}
		}).error(function(result, status){
			console.log('error');
		});
	};

}]);
CUB.controller('messagesCtrl', ['$scope','$http','globalMessageService',function($scope,$http,globalMessageService) {
	
	$scope.$watch(function() {
	
		return globalMessageService.watch()
	
	}, function(prev, next) {
		if (typeof next !== 'undefined') {
		      $scope.message = globalMessageService.getMessage();
		      $scope.messageType = globalMessageService.getMessageType();
		}
	},true);

}]);

/* ===== Directives ===== */

// Directive for + and - to help check the qty value
CUB.directive('qtySelector', ['$parse', function($parse){
	return {
		restrict: 'A',
		link: function(scope, element, attrs){
			$(element).on('click touchstart',function(){
				setTimeout(function(){ // Wait for jquery to update the val
					scope.base.newQty = $(element).parent('.select-quantity').find('.qty-input').val();

          if($(element).parents('.deal').find('.deal-item-head').hasClass('single')){
            // console.log(scope.$parent);
            scope.$parent.deal.ranges[0].totalQty = scope.calcTotalQty(scope.$parent.deal,scope.$parent.deal.ranges[0]);
          } else {
            scope.$parent.range.totalQty = scope.calcTotalQty(scope.$parent.$parent.deal,scope.$parent.range);
          }
					
					scope.$apply();
				},10);
				
			});
		}
	}
}]);
/**
 * Module to use Switchery as a directive for angular.
 * @TODO implement Switchery as a service, https://github.com/abpetkov/switchery/pull/11
 */
angular.module('NgSwitchery', [])
    .directive('uiSwitch', ['$window', '$timeout','$log', '$parse', function($window, $timeout, $log, $parse) {

        /**
         * Initializes the HTML element as a Switchery switch.
         *
         * $timeout is in place as a workaround to work within angular-ui tabs.
         *
         * @param scope
         * @param elem
         * @param attrs
         * @param ngModel
         */
        function linkSwitchery(scope, elem, attrs, ngModel) {
            if(!ngModel) return false;
            var options = {};
            try {
                options = $parse(attrs.uiSwitch)(scope);
            }
            catch (e) {}

            var switcher;

            attrs.$observe('disabled', function(value) {
              if (!switcher) {
                return;
              }

              if (value) {
                switcher.disable();
              }
              else {
                switcher.enable();
              }
            });

            function initializeSwitch() {
              $timeout(function() {
                // Remove any old switcher
                if (switcher) {
                  angular.element(switcher.switcher).remove();
                }
                // (re)create switcher to reflect latest state of the checkbox element
                switcher = new $window.Switchery(elem[0], options);
                var element = switcher.element;
                element.checked = scope.initValue;
                if (attrs.disabled) {
                  switcher.disable();
                }

                switcher.setPosition(false);
                element.addEventListener('change',function(evt) {
                    scope.$apply(function() {
                        ngModel.$setViewValue(element.checked);
                    })
                })
              }, 0);
            }
            initializeSwitch();
          }

        return {
            require: 'ngModel',
            restrict: 'AE',
            scope : {
              initValue : '=ngModel'
            },
            link: linkSwitchery
        }
    }]);
angular.module('breakpointApp',[])
  .directive('breakpoint', ['$window', '$rootScope', function($window, $rootScope){
    return {
        restrict:"A",
        link:function(scope, element, attr){
            scope.breakpoint = {class:'', windowSize:$window.innerWidth }; // Initialise Values

            var breakpoints = (scope.$eval(attr.breakpoint));

            angular.element($window).bind('resize', setWindowSize);

            scope.$watch('breakpoint.windowSize', function(windowWidth, oldValue){
                setClass(windowWidth);
            }); 

            scope.$watch('breakpoint.class', function(newClass, oldClass) {
                if (newClass != oldClass) broadcastEvent(oldClass);
            });

            function broadcastEvent (oldClass) {
                $rootScope.$broadcast('breakpointChange', scope.breakpoint, oldClass);
            }

            function setWindowSize (){
                scope.breakpoint.windowSize = $window.innerWidth;
                if(!scope.$$phase) scope.$apply();
            }

            function setClass(windowWidth){
                var breakpointClass = breakpoints[Object.keys(breakpoints)[0]];
                for (var breakpoint in breakpoints){
                    if (breakpoint < windowWidth) breakpointClass = breakpoints[breakpoint];
                    element.removeClass(breakpoints[breakpoint]);
                }
                element.addClass(breakpointClass);
                scope.breakpoint.class  = breakpointClass;
                if(!scope.$$phase) scope.$apply();
            }
        }
    };
}]);