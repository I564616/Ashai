CUB.controller('notificationsCtrl', ['$location','$scope','$http','$sce','$window','angularUtils','globalMessageService', function($location, $scope, $http,$sce,$window,angularUtils,$timeout, globalMessageService){
    $scope.init = function(){
    	
    	$scope.showMessageAfterPageLoad = sessionStorage.getItem('showMessageAfterPageLoad');
    	
    	
        if($scope.showMessageAfterPageLoad === 'true'){
        	$('#successSavingNotifications .successSavingNotifications').show();
            sessionStorage.setItem('showMessageAfterPageLoad', false);
        }else{
        	$('#successSavingNotifications .successSavingNotifications').hide();
        }

        $scope.isTrackDeliveryOrderFeatureEnabled = $('#isTrackDeliveryOrderFeatureEnabled').val();
        $scope.isInvoiceDiscrepancyEnabled = $('#isInvoiceDiscrepancyEnabled').val();
    	
        $("#mobileNumberShowError").hide();
        $("#mobileNumberInput").hide();

        $scope.learnMore = false;
        $scope.notificationFormValid = false;

        $scope.data = JSON.parse($('#notificationData').html());

        $scope.mobileNumber = $scope.data.mobileNumber;
        $scope.orders = $scope.data.notificationPrefMap['ORDER'];
        $scope.delivery = $scope.data.notificationPrefMap['DELIVERY'];
        $scope.deals = $scope.data.notificationPrefMap['DEAL'];
        
        if($scope.isTrackDeliveryOrderFeatureEnabled){
	        $scope.intransit = $scope.data.notificationPrefMap['INTRANSIT'];
	        $scope.nextInQueue = $scope.data.notificationPrefMap['NEXT_IN_QUEUE'];
	        $scope.updateForETA = $scope.data.notificationPrefMap['UPDATE_FOR_ETA'];
	        $scope.delivered = $scope.data.notificationPrefMap['DELIVERED'];
        }
	    
        if($scope.isInvoiceDiscrepancyEnabled){
        	$scope.creditprocessed = $scope.data.notificationPrefMap['CREDITPROCESSED'];
        }
        
        $scope.showUnsavedChangesPopup();
        
        $scope.showMobileNumberToggle = function(){
            if($scope.mobileNumber == undefined || $scope.mobileNumber == ''){
                $("#showMobileNumberLink").hide();
                $("#showMobileNumberField").show();


            }else{
                $("#showMobileNumberLink").show();
                $("#showMobileNumberField").hide();
            }
        }

        $scope.showMobileNumberToggle()
    }

    $scope.resetNotifications = function(){

        $scope.orders.notificationTypeEnabled = false;
        $scope.deals.notificationTypeEnabled = false;
        $scope.delivery.notificationTypeEnabled = false;
        $scope.intransit.notificationTypeEnabled = false;
        $scope.nextInQueue.notificationTypeEnabled = false;
        $scope.updateForETA.notificationTypeEnabled = false;
        $scope.delivered.notificationTypeEnabled = false;
        $scope.creditprocessed.notificationTypeEnabled = false;
        
        $scope.orders.duration = '15-minutes';
        $scope.orders.emailEnabled = false;
        $scope.orders.smsEnabled = false;

        $scope.deals.duration = 'Monday';
        $scope.deals.emailEnabled = false;

        $scope.delivery.emailEnabled = false;
        $scope.delivery.smsEnabled = false;

        $scope.intransit.emailEnabled = false;
        $scope.intransit.smsEnabled = false;

        $scope.nextInQueue.emailEnabled = false;
        $scope.nextInQueue.smsEnabled = false;

        $scope.updateForETA.emailEnabled = false;
        $scope.updateForETA.smsEnabled = false;

        $scope.delivered.emailEnabled = false;
        $scope.delivered.smsEnabled = false;
        
        $scope.creditprocessed.emailEnabled = false;
        $scope.creditprocessed.smsEnabled = false;

        $scope.notificationFormValid = true;
    }

    $scope.invokePopup = function($event){
        console.log();
    }
    
    $scope.getDurationTimeUnit = function(duration) {
    	
    	var splitDuration = duration.split('-');
    	
    	return splitDuration[1];
    }
    
    $scope.getDropDownLabel = function(label){
        return label.replace('-', ' ');
    };

    $scope.orderFormData=function(){
        var orderData={
            "notificationType" : 'ORDER',
            "notificationEnabled": $('input[id="orders.notificationEnabled"]').val(),
            "emailEnabled":$scope.orders.emailEnabled,
            "smsEnabled": $scope.orders.smsEnabled,
            "emailDuration":$scope.orders.duration,
            "smsDuration":$scope.orders.duration,
            "smsDurationTimeUnit": $scope.getDurationTimeUnit($scope.orders.duration)
        };

        return orderData;
    }

    $scope.dealsFormData=function(){
        var dealsData={
            "notificationType" : 'DEAL',
            "notificationEnabled": $('input[id="deals.notificationEnabled"]').val(),
            "emailEnabled":$scope.deals.emailEnabled,
            "emailDuration":$scope.deals.duration,
            "smsEnabled": $scope.deals.smsEnabled,
            "smsDuration":$scope.deals.duration
        };

        return dealsData;
    }

    $scope.deliveryFormData=function(){
        var deliveryData={
            "notificationType" : 'DELIVERY',
            "notificationEnabled":$('input[id="delivery.notificationEnabled"]').val(),
            "emailEnabled":$scope.delivery.emailEnabled,
            "smsEnabled": $scope.delivery.smsEnabled,
            "emailDuration":'',
            "smsDuration":''
        };

        return deliveryData;
    }

    $scope.intransitFormData=function(){
        var intransit={
            "notificationType" : 'INTRANSIT',
            "notificationEnabled": $('input[id="intransit.notificationEnabled"]').val(),
            "emailEnabled":$scope.intransit.emailEnabled,
            "emailDuration":$scope.intransit.duration,
            "smsEnabled": $scope.intransit.smsEnabled,
            "smsDuration":$scope.intransit.duration
        };

        return intransit;
    }

    $scope.nextInQueueFormData=function(){
        var nextInQueue={
            "notificationType" : 'NEXT_IN_QUEUE',
            "notificationEnabled": $('input[id="nextInQueue.notificationEnabled"]').val(),
            "emailEnabled":$scope.nextInQueue.emailEnabled,
            "emailDuration":$scope.nextInQueue.duration,
            "smsEnabled": $scope.nextInQueue.smsEnabled,
            "smsDuration":$scope.nextInQueue.duration
        };

        return nextInQueue;
    }

    $scope.updateForETAFormData=function(){
        var updateForETA={
            "notificationType" : 'UPDATE_FOR_ETA',
            "notificationEnabled": $('input[id="updateForETA.notificationEnabled"]').val(),
            "emailEnabled":$scope.updateForETA.emailEnabled,
            "emailDuration":$scope.updateForETA.duration,
            "smsEnabled": $scope.updateForETA.smsEnabled,
            "smsDuration":$scope.updateForETA.duration
        };

        return updateForETA;
    }

    $scope.deliveredFormData=function(){
        var delivered={
            "notificationType" : 'DELIVERED',
            "notificationEnabled": $('input[id="delivered.notificationEnabled"]').val(),
            "emailEnabled":$scope.delivered.emailEnabled,
            "emailDuration":$scope.delivered.duration,
            "smsEnabled": $scope.delivered.smsEnabled,
            "smsDuration":$scope.delivered.duration
        };

        return delivered;
    }

    $scope.creditProcessedFormData=function(){
        var creditProcessed={
            "notificationType" : 'CREDITPROCESSED',
            "notificationEnabled": $('input[id="creditprocessed.notificationEnabled"]').val(),
            "emailEnabled":$scope.creditprocessed.emailEnabled,
            "emailDuration":$scope.creditprocessed.duration,
            "smsEnabled": $scope.creditprocessed.smsEnabled,
            "smsDuration":$scope.creditprocessed.duration
        };

        return creditProcessed;
    }

    $scope.checkifFormisUpdated=function(){
        if(angular.isUndefined($scope.originalData)){
            $scope.originalData=JSON.parse($('#notificationData').html());
        }
        
        
        if(angular.isUndefined($scope.originalData.mobileNumber)){
            $scope.originalData.mobileNumber = '';
        }

        if($scope.mobileNumber!=$scope.originalData.mobileNumber){
            return true;
        }

        if( ($scope.orders.emailEnabled!=$scope.originalData.notificationPrefMap['ORDER'].emailEnabled || $scope.orders.smsEnabled!=$scope.originalData.notificationPrefMap['ORDER'].smsEnabled || $scope.orders.duration!=$scope.originalData.notificationPrefMap['ORDER'].duration)  && $scope.originalData.notificationPrefMap['ORDER'] != undefined){
        	return true;
		}
        
        if($scope.deals.emailEnabled!=$scope.originalData.notificationPrefMap['DEAL'].emailEnabled || $scope.deals.duration!=$scope.originalData.notificationPrefMap['DEAL'].duration){
            return true;

        }

        if( ($scope.delivery.emailEnabled!=$scope.originalData.notificationPrefMap['DELIVERY'].emailEnabled || $scope.delivery.smsEnabled!=$scope.originalData.notificationPrefMap['DELIVERY'].smsEnabled) && $scope.originalData.notificationPrefMap['DELIVERY'] != undefined){
            return true;
        }

        if($scope.isTrackDeliveryOrderFeatureEnabled){
	
	        if( ($scope.delivered.emailEnabled!=$scope.originalData.notificationPrefMap['DELIVERED'].emailEnabled || $scope.delivered.smsEnabled!=$scope.originalData.notificationPrefMap['DELIVERED'].smsEnabled) && $scope.originalData.notificationPrefMap['DELIVERED'] != undefined){
	            return true;
	        }
	
	        if( ($scope.intransit.emailEnabled!=$scope.originalData.notificationPrefMap['INTRANSIT'].emailEnabled || $scope.intransit.smsEnabled!=$scope.originalData.notificationPrefMap['INTRANSIT'].smsEnabled) && $scope.originalData.notificationPrefMap['INTRANSIT'] != undefined){
	            return true;
	        }
	
	        if( ($scope.nextInQueue.emailEnabled!=$scope.originalData.notificationPrefMap['NEXT_IN_QUEUE'].emailEnabled || $scope.nextInQueue.smsEnabled!=$scope.originalData.notificationPrefMap['NEXT_IN_QUEUE'].smsEnabled) && $scope.originalData.notificationPrefMap['NEXT_IN_QUEUE'] != undefined){
	            return true;
	        }
	
	
	        if( ($scope.updateForETA.emailEnabled!=$scope.originalData.notificationPrefMap['UPDATE_FOR_ETA'].emailEnabled || $scope.updateForETA.smsEnabled!=$scope.originalData.notificationPrefMap['UPDATE_FOR_ETA'].smsEnabled) && $scope.originalData.notificationPrefMap['UPDATE_FOR_ETA'] != undefined){
	            return true;
	        }
        }
        
        if($scope.isInvoiceDiscrepancyEnabled){
	        if(($scope.creditprocessed.emailEnabled!=$scope.originalData.notificationPrefMap['CREDITPROCESSED'].emailEnabled || $scope.creditprocessed.smsEnabled!=$scope.originalData.notificationPrefMap['CREDITPROCESSED'].smsEnabled) && $scope.originalData.notificationPrefMap['CREDITPROCESSED'] != undefined){
	            return true;
	        }
        }


        return false;
    }

    $scope.invokeUnsavedChangesPopup = function() {

        if($scope.checkifFormisUpdated()){
            $('body').addClass('unsaved-changes');
            $scope.notificationFormValid=true;
        }
        else{
            $('body').removeClass('unsaved-changes');
            $scope.notificationFormValid=false;
        }

    },

        $scope.switchOffUnsavedChangesPopup = function() {
            if($scope.allowRedirect){
                $('body').removeClass('unsaved-changes');
                $scope.notificationFormValid=false;
            }
        },

        $scope.showUnsavedChangesPopup = function(){
            $('a, .highlight-link').on('click', function(e){
                if ($scope.checkifFormisUpdated() && $('body.unsaved-changes').length) {
                    console.log('has unsaved changes');
                    $scope.destinationURL=document.activeElement.href;
                    if($scope.destinationURL == undefined && $(this).attr('data-url')!=undefined){
                        $scope.destinationURL=$(this).attr('data-url');
                    }
                    e.preventDefault();
                    $('#unsavedNotification').modal('show');
                }
            });
        },

        $scope.submitFromPopup =function(){
            $scope.submit();
            if($scope.destinationURL!=undefined){
                if($scope.allowRedirect){
                    $window.location.href=$scope.destinationURL;
                }
            }
        }



    $scope.submit = function(){
        $scope.notificationFormValid = false;

        $scope.forms = [];

        var postData1 = {};
        var postData2 = {};

        $scope.postDatas = function(){

            postData1 = {
                'forms': $scope.forms
            };

            if ( $scope.mobileNumber != '') {
            	postData2 = {
            		'mobileNumber': $scope.mobileNumber
            	}
            }
            
            
            var postData = $.extend({}, postData2, postData1);

            $.ajax({
                url:'/sabmStore/en/your-notifications/save',
                type:'POST',
                dataType: 'json',
                data: JSON.stringify(postData),
                contentType: 'application/json',
                success: function(result){
                
                	if (result) {
                    	sessionStorage.setItem('showMessageAfterPageLoad', true);
                		$("#mobileNumberInput").hide();
                        $window.scrollTo(0, 0);
                    	window.location.reload();
                	} else {
                    	$('#successSavingNotifications .successSavingNotifications').hide();
                		$("#mobileNumberInput").show();
                        window.scrollTo(0, 0);
                	}
                },
                error:function(result){
                    $('#successSavingNotifications .successSavingNotifications').hide();
                }
            });
        }

        if(angular.isUndefined($scope.originalData)){
            $scope.originalData=JSON.parse($('#notificationData').html());
        }

        $scope.checkboxesValidation = function(){
            if( ($scope.deals.emailEnabled!=$scope.originalData.notificationPrefMap['DEAL'].emailEnabled || $scope.deals.duration!=$scope.originalData.notificationPrefMap['DEAL'].duration) && $scope.originalData.notificationPrefMap['DEAL'] != undefined){
                $scope.forms.push($scope.dealsFormData());
            }

            if( ($scope.orders.emailEnabled!=$scope.originalData.notificationPrefMap['ORDER'].emailEnabled || $scope.orders.duration!=$scope.originalData.notificationPrefMap['ORDER'].duration || $scope.orders.smsEnabled!=$scope.originalData.notificationPrefMap['ORDER'].smsEnabled) && $scope.originalData.notificationPrefMap['ORDER'] != undefined){
                $scope.forms.push($scope.orderFormData());
            }

            if( ($scope.delivery.emailEnabled!=$scope.originalData.notificationPrefMap['DELIVERY'].emailEnabled || $scope.delivery.smsEnabled!=$scope.originalData.notificationPrefMap['DELIVERY'].smsEnabled) && $scope.originalData.notificationPrefMap['DELIVERY'] != undefined){
                $scope.forms.push($scope.deliveryFormData());
            }

            if( ($scope.intransit.emailEnabled!=$scope.originalData.notificationPrefMap['INTRANSIT'].emailEnabled || $scope.intransit.smsEnabled!=$scope.originalData.notificationPrefMap['INTRANSIT'].smsEnabled) && $scope.originalData.notificationPrefMap['INTRANSIT'] != undefined){
                $scope.forms.push($scope.intransitFormData());
            }

            if( ($scope.nextInQueue.emailEnabled!=$scope.originalData.notificationPrefMap['NEXT_IN_QUEUE'].emailEnabled || $scope.nextInQueue.smsEnabled!=$scope.originalData.notificationPrefMap['NEXT_IN_QUEUE'].smsEnabled) && $scope.originalData.notificationPrefMap['NEXT_IN_QUEUE'] != undefined){
                $scope.forms.push($scope.nextInQueueFormData());
            }

            if( ($scope.updateForETA.emailEnabled!=$scope.originalData.notificationPrefMap['UPDATE_FOR_ETA'].emailEnabled || $scope.updateForETA.smsEnabled!=$scope.originalData.notificationPrefMap['UPDATE_FOR_ETA'].smsEnabled) && $scope.originalData.notificationPrefMap['UPDATE_FOR_ETA'] != undefined){
                $scope.forms.push($scope.updateForETAFormData());
            }

            if( ($scope.delivered.emailEnabled!=$scope.originalData.notificationPrefMap['DELIVERED'].emailEnabled || $scope.delivered.smsEnabled!=$scope.originalData.notificationPrefMap['DELIVERED'].smsEnabled) && $scope.originalData.notificationPrefMap['DELIVERED'] != undefined){
                $scope.forms.push($scope.deliveredFormData());
            }
            
	        if(($scope.creditprocessed.emailEnabled!=$scope.originalData.notificationPrefMap['CREDITPROCESSED'].emailEnabled || $scope.creditprocessed.smsEnabled!=$scope.originalData.notificationPrefMap['CREDITPROCESSED'].smsEnabled) && $scope.originalData.notificationPrefMap['CREDITPROCESSED'] != undefined){
                $scope.forms.push($scope.creditProcessedFormData());
	        }
            
            $scope.postDatas();
        }

        //mobile phone front end validation
        if($scope.mobileNumber != undefined && $scope.mobileNumber.length !== 0){
            if($scope.mobileNumber != $scope.originalData.mobileNumber){
                if(($scope.mobileNumber.charAt(0) + $scope.mobileNumber.charAt(1)) != '04' || $scope.mobileNumber.length < 12){
                    $('#mobileNumberShowError').show();
                    $('#successSavingNotifications .successSavingNotifications').hide();
                    window.scrollTo(0, 0);
                    $scope.allowRedirect = false;
                }else{
                    $('#mobileNumberShowError').hide();
                    $scope.showMobileNumberToggle();
                    $scope.allowRedirect = true;

                    var mobileNumberArray = $scope.mobileNumber.split('');

                    mobileNumberArray = mobileNumberArray.join('$').replace(/ /g, '').split('$');   
                    
                    postData2 = {
                        'mobileNumber': mobileNumberArray.join('')
                    }

                   // $scope.postDatas();
                    $scope.checkboxesValidation();
                }
            }else{
                $('#mobileNumberShowError').hide();
                $scope.checkboxesValidation();
                $scope.allowRedirect = true;
            }
        }else{
            $('#mobileNumberShowError').hide();
            $scope.checkboxesValidation();
            $scope.allowRedirect = true;
        }
			
    }
    
    
    $('#mobileNumberField')
	.keydown(function (e) {
		var key = e.which || e.charCode || e.keyCode || 0;
		$phone = $(this);

		// Auto-format- do not expose the mask as the user begins to type
		if (key !== 8 && key !== 9) {
			if ($phone.val().length === 4) {
				$phone.val($phone.val() + ' ');
			}
			if ($phone.val().length === 8) {
				$phone.val($phone.val() + ' ');
			}
		}

		// Allow numeric (and tab, backspace, delete) keys only
		return (key == 8 || 
				key == 9 ||
				key == 46 ||
				(key >= 48 && key <= 57) ||
				(key >= 96 && key <= 105));	
	})
	.bind('focus click', function () {
		$phone = $(this);
		
		if ($phone.val().length === 0) {
			$phone.val('04');
		}
		else {
			var val = $phone.val();
			$phone.val('').val(val); // Ensure cursor remains at the end
		}
	})
	.blur(function () {
		$phone = $(this);
		
		if ($phone.val() === '04') {
			$phone.val('');
		}
	});
    
}]);
