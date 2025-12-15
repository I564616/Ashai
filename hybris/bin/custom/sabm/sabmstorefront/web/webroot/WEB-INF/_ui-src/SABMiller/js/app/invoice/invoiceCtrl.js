/* globals sessionStorage */
CUB.controller('invoiceCtrl', ['$scope', '$http', '$window', 'globalMessageService', '$filter', function($scope, $http, $window, globalMessageService, $filter) {
	$scope.invoiceList = JSON.parse($('#invoiceList').html());
	$scope.currentStep = 1;
	$scope.isBtn1Enabled = false; $scope.isBtn2Enabled = false;
	$scope.selected = {}; $scope.discountExpected = {}; $scope.emails = {};
	$scope.typeOfIssueList = ['Price Discrepancy','Freight Discrepancy'];
	$scope.showtypeOfIssueDropdown = false;
	$scope.isInvoiceNumberNotExist = false;
	$scope.isInvoiceNumberDiscrepancySent = false, $scope.showInvoiceNumberDiscrepancySentError = false;
	$scope.isBDEUser = $('#isBDEUserEnabled').val();

	if ( $scope.isBDEUser ) {
		 $scope.isBtn3Enabled = false;
	} else {
		 $scope.isBtn3Enabled = true;
	}

	var currentB2BUnitUID = $('#currentB2BUnitUID').val();

	trackInvoiceDiscrepancyStepProcess($('#stepOneProcessTitle').val());

    $('a:not(.btn-link), .highlight-link').live('click', function(e){
        if ($('body.unsaved-changes').length) {
            $scope.destinationURL=document.activeElement.href;
            if ( $scope.destinationURL == undefined && $(this).attr('data-url')!=undefined ) {
                $scope.destinationURL=$(this).attr('data-url');
            }
            e.preventDefault();
            $scope.showCancelInvoiceDiscrepancyPopup(e);
        }
    });

	$scope.desktopSize = 992;
	$scope.mobileSize = 768;
	$scope.updateTableColumnWhenResize = function(){
		$scope.tableColumn = 1;
		$scope.tableColumnBtnActive = 'right';
		$scope.widthSize = $window.innerWidth;

		if ( $scope.widthSize >= $scope.mobileSize ) {
			$scope.tableColumnMax = 2;
		} else {
			$scope.tableColumnMax = 3;
		}
	}

	$scope.updateTableColumnWhenResize();

	//*** for mobile only ***
	if ( $scope.widthSize < $scope.mobileSize ) {
	    $(".item-data-table .table-body-container").swipe( {
	      swipe: function(event, direction, distance, duration, fingerCount, fingerData) {

	    	  if ( direction === 'right' ) {
	    		  $scope.showTableColumnLeft();
	    	  } else if ( direction === 'left' ) {
	    		  $scope.showTableColumnRight();
	    	  }

	    	  $scope.$apply();
	      },
		   threshold:0,
		   allowPageScroll: "auto",
		   excludedElements: 'input, label, .table-arrow'
	    });
		}

	angular.element($window).bind('resize', function(){
		$scope.updateTableColumnWhenResize();
		$scope.$digest();
   });

	$scope.selectFromDropdown = function($event , val){
		angular.element($event.currentTarget).parents('.custom-dropdown').find('input[type="text"]').html(val).val(val);
	}

	$scope.getInvoiceItemDataFromPopup = function(event){
		$http({
			url: $('#invoiceItemDataUrl').val(),
			method: 'POST',
			data: {'invoiceNumber': $scope.invoiceNumber, 'soldTo': currentB2BUnitUID }
		}).then(function(result){
			$scope.invoiceItemData = result.data;
			$scope.setTotals($scope.invoiceItemData.invoices);
			$scope.freightCharged = $scope.invoiceItemData.freightChargedAmount;
			$scope.invoiceDate = $scope.invoiceItemData.invoiceDate;
			$.magnificPopup.close();

			if ( $scope.typeOfIssue === $scope.typeOfIssueList[1] ) {
				if ( $scope.widthSize >= $scope.mobileSize ) {
					$scope.tableColumn = 2;
				} else {
					$scope.tableColumn = 3;
				}
				$scope.tableColumnBtnActive = 'left';
			}
			$scope.currentStep = $scope.currentStep + 1;
			$scope.trackStepProcess(event);
		});
	}

	$scope.getColumnCount = function(count){
	  return new Array(count);
	}

	$scope.updateTableColumn = function(col, $event){
		$scope.tableColumn = col;

		 if ( col > 1 && col < $scope.tableColumnMax ) {
			$scope.tableColumnBtnActive = 'both';
		 } else if ( col == 1 ) {
			$scope.tableColumnBtnActive = 'right';
		 } else if (col == $scope.tableColumnMax ) {
			$scope.tableColumnBtnActive = 'left';
		 }


	}

	$scope.showTableColumnRight = function(){

		if ( $scope.tableColumn < $scope.tableColumnMax ) {
			$scope.tableColumn = $scope.tableColumn + 1;

			if ( $scope.tableColumn > 1 && $scope.tableColumn < $scope.tableColumnMax ) {
				$scope.tableColumnBtnActive = 'both';
			} else if ( $scope.tableColumn === $scope.tableColumnMax ) {
				$scope.tableColumnBtnActive = 'left';
			}
		}
	}

	$scope.showTableColumnLeft = function(){
		if ( $scope.tableColumn !== 1 ) {
			$scope.tableColumn = $scope.tableColumn - 1;

			if ( $scope.tableColumn > 1 && $scope.tableColumn < $scope.tableColumnMax ) {
					$scope.tableColumnBtnActive = 'both';
			} else if ( $scope.tableColumn === 1 ) {
				$scope.tableColumnBtnActive = 'right';
			}
		}
	}

	//*** START: go to the next step ***
	$scope.goToNextStep = function(event){

		//Step 1
		if ( $scope.currentStep === 1 ) {

			$http({
				url: $('#invoiceValidationUrl').val(),
				method: 'POST',
				data: {'invoiceNumber': $scope.invoiceNumber, 'soldTo': currentB2BUnitUID }
			}).then(function(result){

				var data = result.data

				//validate if invoice number does exist
				if ( data.invoiceInSAP ) {

					//validate if invoice discrepancy already raised
					if ( data.invoiceDiscrepencyRequestRaised ) {

						//show the 'invoice discrepancy already raised' modal
						$.magnificPopup.open({
							items:{
						       src: '#invoiceDiscrepancyAlreadyRaisedPopup',
						       type: 'inline'
							},
						   removalDelay: 500,
						   mainClass: 'mfp-slide',
						   modal: true
						});

					} else {

						//get the invoice item data from ajax call
						$http({
							url: $('#invoiceItemDataUrl').val(),
							method: 'POST',
							data: {'invoiceNumber': $scope.invoiceNumber, 'soldTo': currentB2BUnitUID }
						}).then(function(result){
							$scope.invoiceItemData = result.data;

							$scope.setTotals($scope.invoiceItemData.invoices);

							$scope.freightCharged = $scope.invoiceItemData.freightChargedAmount;
							$scope.invoiceDate = $scope.invoiceItemData.invoiceDate;

							if ( $scope.typeOfIssue === $scope.typeOfIssueList[1] ) {

								if ( $scope.widthSize >= $scope.mobileSize ) {
									$scope.tableColumn = 2;
								} else {
									$scope.tableColumn = 3;
								}
								$scope.tableColumnBtnActive = 'left';
							}
							$scope.currentStep = $scope.currentStep + 1;
							$scope.trackStepProcess(event);
						});
					}

					$scope.isInvoiceNumberNotExist = false;
				} else {
					$scope.isInvoiceNumberNotExist = true;
				}

			});
		}

		//Step 2
		if ( $scope.currentStep === 2 ) {
			$scope.invoiceItemDataObject = [];

			if ( $scope.typeOfIssue === 'Price Discrepancy' ) {
				angular.forEach($scope.invoiceItemData.invoices, function(item){

					if ( $scope.selected[item.itemID] ) {
						$scope.invoiceItemDataObject.push({
							'itemDescriptionLine1': item.itemDescriptionLine1,
							'itemDescriptionLine2': item.itemDescriptionLine2,
							'material': item.material,
							'quantity': item.quantity,
							'discountReceived': item.discount,
							'discountExpected': $scope.removeDollarSign($scope.discountExpected[item.itemID])
						});
					}
				});
			} else if ( $scope.typeOfIssue === 'Freight Discrepancy' ) {
				angular.forEach($scope.invoiceItemData.invoices, function(item){
					$scope.invoiceItemDataObject.push({
						'itemDescriptionLine1': item.itemDescriptionLine1,
						'itemDescriptionLine2': item.itemDescriptionLine2,
						'material': item.material,
						'quantity': item.quantity
					});
				});
			}

			$http.get('/your-business/'+ $('#notificationListUrl').val()).then(function(result){
				$scope.notificationUsers = result.data.customers;
				$scope.notificationUsers = $filter('orderBy')($scope.notificationUsers, 'name');
			});

			$scope.trackStepProcess(event);
		}

		//Step 3
		if ( $scope.currentStep === 3 ) {
			var SABMInvoiceDiscrepancyRequestData = {};
			var invoiceType = ($scope.typeOfIssue === 'Price Discrepancy') ? 'PRICE' : 'FREIGHT';

			if ( $scope.typeOfIssue === 'Price Discrepancy' ) {
				SABMInvoiceDiscrepancyRequestData = {
					'invoiceNumber': $scope.invoiceNumber,
					'invoiceDate': $scope.invoiceDate,
					'soldTo': currentB2BUnitUID,
					'raisedByBDE': ($scope.isBDEUser) ? true : false,
					'invoiceType': invoiceType,
					'invoices': $scope.invoiceItemDataObject,
					'requestDescription': $scope.message,
					'notificationList': (typeof $scope.notificationListObject !== 'undefined') ? $scope.notificationListObject : [$('#curEmail').val()]
				};
			} else if ( $scope.typeOfIssue === 'Freight Discrepancy' ) {
				SABMInvoiceDiscrepancyRequestData = {
					'invoiceNumber': $scope.invoiceNumber,
					'invoiceDate': $scope.invoiceDate,
					'soldTo': currentB2BUnitUID,
					'raisedByBDE': ($scope.isBDEUser) ? true : false,
					'invoiceType': invoiceType,
					'invoices': $scope.invoiceItemDataObject,
					'freightChargedAmount': $scope.freightCharged,
					'freightExpectedAmount': $scope.removeDollarSign($scope.freightExpected),
					'requestDescription': $scope.message,
					'notificationList': (typeof $scope.notificationListObject !== 'undefined') ? $scope.notificationListObject : [$('#curEmail').val()]
				};
			}

			//add a loading screen when the request invoice discrepancy button clicked
			$('.breadcrumb').addClass('inactive');
			rm.utilities.loadingMessage($('.loading-message').data('request'),true);
			$('body').addClass('loading');

			$http({
				url: '/your-business/saveInvoiceDiscrepancyRequest',
				method: 'POST',
				data: SABMInvoiceDiscrepancyRequestData
			}).then(function(result){

				//remove the loading screen when the result loaded
				$('.breadcrumb').removeClass('inactive');
				$('body').removeClass('loading');

				if ( result.data ) {
					$('body').removeClass('unsaved-changes');
					$scope.isInvoiceNumberDiscrepancySent = true;
					$scope.trackStepProcess(event);
				} else {
					$scope.showInvoiceNumberDiscrepancySentError = true;
				}
			});
		}

		if($scope.currentStep !== 1 && $scope.currentStep !== 3){
			$scope.currentStep = $scope.currentStep + 1;
		}
	}
	//END: $scope.goToNextStep

	//START: $scope.goToPrevStep
	//go to the previous form
	$scope.goToPrevStep = function(){
		if ( $scope.currentStep > 1 ) {
			$scope.currentStep = $scope.currentStep - 1;
		}
	}
	//END: $scope.goToPrevStep

	//START: $scope.validateForm
	// validate form field data if there's a changes made.
	//when valid, the next button will be enabled
	$scope.validateForm = function(){

		$('body').addClass('unsaved-changes');

		$scope.invoiceNumber = $('#invoiceNumber').val();
		$scope.typeOfIssue = $('#typeOfIssue').val();

		//Step 1
		if ( $scope.currentStep === 1 ) {
			if ( typeof $scope.invoiceNumber !== 'undefined' && $scope.typeOfIssue !== '' ) {
				if ( $scope.form.invoiceNumber.$valid ) {
					$scope.isBtn1Enabled = true;


				} else {
					$scope.isBtn1Enabled = false;
				}
			} else {
				$scope.isBtn1Enabled = false;
			}
		}

		//Step 2
		if ( $scope.currentStep === 2 ) {

			if ( $scope.typeOfIssue === 'Price Discrepancy' ) {
				$scope.discountExpectedInput = [];
				$scope.selectedInput = [];

				angular.forEach($scope.invoiceItemData.invoices, function(item){

					if ( $scope.discountExpected[item.itemID] ) {
						$scope.discountExpectedInput.push($scope.discountExpected[item.itemID]);
					}

					if ( $scope.selected[item.itemID] ) {
						$scope.selectedInput.push($scope.selected[item.itemID]);
					}
				});

				if ( $scope.selectedInput.length > 0 ) {
					if ( $scope.discountExpectedInput.length === $scope.selectedInput.length ) {
						$scope.isBtn2Enabled = true;
					} else {
						$scope.isBtn2Enabled = false;
					}
				} else {
					$scope.isBtn2Enabled = false;
				}
			} else if ($scope.typeOfIssue === 'Freight Discrepancy' ) {
				if ( $scope.freightExpected ) {
					$scope.isBtn2Enabled = true;
				} else {
					$scope.isBtn2Enabled = false;
				}
			}
		}

		//Step 3
		if ( $scope.currentStep === 3 ) {
			$scope.notificationListObject = [];
			$scope.userRoleListObject = [];
			$scope.selectedNonBDEObject = [];

			if ( $('.notification-list .selected input[type="checkbox"]').is(':checked') ) {
				$scope.notificationListObject.push($('#curEmail').val());
				$scope.userRoleListObject.push($('#curUserRole').val());
			} else {
				//Do Nothing
			}

			angular.forEach($scope.notificationUsers, function(i){
				if ( $scope.emails[i.uid] ) {
					$scope.notificationListObject.push(i.uid);
					$scope.userRoleListObject.push(i.userRole);
				}
			});

			angular.forEach($scope.userRoleListObject, function (item) {

				//check if there's a non-bde email selected
				if ( item.indexOf('bde-user') == -1 ) {
					$scope.selectedNonBDEObject.push(item);
				}

			});

			if ( $scope.selectedNonBDEObject.length > 0 ) {
				$scope.isBtn3Enabled = true;
			} else {
				$scope.isBtn3Enabled = false;
			}

		}
	}
	//END: $scope.validateForm

	//START: $scope.showCancelInvoiceDiscrepancyPopup
	$scope.showCancelInvoiceDiscrepancyPopup = function($event){
		$event.preventDefault();
		$.magnificPopup.open({
			items:{
		       src: '#cancelInvoiceDiscrepancyPopup',
		       type: 'inline'
			},
		   removalDelay: 500,
		   mainClass: 'mfp-slide',
		   modal: true,
           callbacks: {
	           	open: function(){
	          	},
            	close: function(){
              	}
           }
		});
	}
	//END: $scope.showCancelInvoiceDiscrepancyPopup


	$scope.cancelForm = function(){
		$scope.resetFormFieldData();
		if ( $('body.unsaved-changes').length ) {
			window.location.href=$scope.destinationURL;
		} else {
			window.location.href=$('.cancel-invoice-discrepancy').attr('href');
		}
		$('body').removeClass('unsaved-changes');
        $.magnificPopup.close();
	};

	//reset all the value in the formfield
	$scope.resetFormFieldData = function(){
		$scope.currentStep = 1;
		$scope.form.$setPristine();
		$scope.typeOfIssue = '';
		$scope.invoiceNumber = '';
		$scope.isBtn1Enabled = false;
		$scope.isBtn2Enabled = false;
	}

	$scope.checkAll = function(){
		angular.forEach($scope.invoiceItemData.invoices, function(i){
			$scope.selected[i.itemID] = $scope.selectAll;
		});
	}


	$scope.setTotals = function(item){
		var qty = 0, unitPrice = 0, discount = 0, amount = 0, containerDeposit = 0, wet = 0, exGST = 0, localFreight = 0, lucexGST = 0;

		if ( !angular.isUndefined(item) ) {
			for ( var x = 0; x < item.length; x++ ) {
				qty += isNaN(item[x].quantity) || item[x].quantity == null ? 0 : parseInt(item[x].quantity);
				discount += isNaN(item[x].discount) || item[x].discount == null ? 0 : parseFloat(item[x].discount);
				amount += isNaN(item[x].amount) || item[x].amount == null ? 0 : parseFloat(item[x].amount);
				containerDeposit += isNaN(item[x].containerDeposit) || item[x].containerDeposit == null ? 0 : parseFloat(item[x].containerDeposit);
				wet += isNaN(item[x].wet) || item[x].wet == null ? 0 : parseFloat(item[x].wet);
				exGST += isNaN(item[x].totalExGST) || item[x].totalExGST == null ? 0 : parseFloat(item[x].totalExGST);
				localFreight += isNaN(item[x].localFreight) || item[x].localFreight == null ? 0 : parseFloat(item[x].localFreight);
			}
		}

		$scope.totalQty = $filter('number')(qty, 0);
		$scope.totalDiscount = $filter('number')(discount, 2);
		$scope.totalAmount = $filter('number')(amount, 2);
		$scope.totalContainerDeposit = $filter('number')(containerDeposit, 2);
		$scope.totalWet = $filter('number')(wet, 2);
		$scope.totalExGST = $filter('number')(exGST, 2);
		$scope.totalLocalFreight = $filter('number')(localFreight, 2);
	}


	$scope.appendDollarSign = function(event) {

		var curTarget = angular.element(event.currentTarget);

		if ( curTarget.val() === '' || curTarget.val()[0] != '$') {
			curTarget.val('$');
		}

		var val = curTarget.val();
		curTarget.val('').val(val); // Ensure cursor remains at the end
	}

	$scope.removeDollarSign = function(scope) {
		if ( scope.indexOf('$') != -1 ) {
			scope = scope.replace(/\$/g,'');
		}

		return scope;
	}

	$scope.trackStepProcess = function(ev){

		if ( typeof angular.element(ev.currentTarget).attr('isclicked') === 'undefined' ) {
			angular.element(ev.currentTarget).attr('isclicked', true);
			var title = angular.element(ev.currentTarget).attr('data-title');
			trackInvoiceDiscrepancyStepProcess(title);
		}
	}
}]);

//add restrictions to only allowed [0-9.$]
function isNumber(evt) {
    evt = (evt) ? evt : window.event;
    console.log(evt.keyCode);

    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if ( (charCode >= 48 && charCode <= 57) || charCode == 36 || charCode == 46) {
    	return true;
    } else {
    	return false;
    }
}

/* Raised Invoice Discrepancies*/
CUB.controller('raisedInvoiceDiscrepancyCtrl', ['$scope', '$http', '$log', '$window', '$filter', 'globalMessageService', function($scope, $http, $log, $window, $filter, globalMessageService) {

	$scope.desktopSize = 992;
	$scope.mobileSize = 768;
	$scope.trackResize = function(){
		$scope.widthSize = $window.innerWidth;
	}

	$scope.checkOrientation = function(){
		if ( $window.matchMedia("(orientation: portrait)").matches ) {
			$scope.orientation = 'Portait';
		}

		if ( $window.matchMedia("(orientation: landscape)").matches ) {
			$scope.orientation = 'Landscape';
		}
	}

	$scope.trackResize();
	$scope.checkOrientation();

	angular.element($window).bind('resize', function(){
		$scope.trackResize();
		$scope.checkOrientation();
	});

	$scope.today = new Date();
	$scope.raisedInvoicesList =  JSON.parse($('#raisedInvoicesList').html());

	$scope.raisedInvoicesList.map(function(item, index){
		var date = item.raisedDate.split('/');
		item.raisedOn = new Date(date[2]+'-'+date[1]+'-'+date[0]);

		var excludeDollarSign = item.expectedTotalAmount.replace('$',''); //remove the $ in the amount
		item.expectedTotal = parseFloat(excludeDollarSign);

		return item;
	});

	$scope.raisedInvoicesList = $filter('orderBy')($scope.raisedInvoicesList, '-raisedOn');

	//console.log($scope.raisedInvoicesList);
	$scope.viewInvoiceDiscrepancyByIDPopup = function(invoiceNo){

		var i = $scope.raisedInvoicesList.map(function(item) { return item.invoiceNumber; }).indexOf(invoiceNo);

		$.magnificPopup.open({
			items:{
		       src: '#invoiceDiscrepancyAlreadyRaisedPopup' + invoiceNo,
		       type: 'inline'
			},
		   removalDelay: 500,
		   mainClass: 'mfp-slide',
		   modal: true,
		   callbacks: {
			   open: function(){
				   $scope.modalData = $scope.raisedInvoicesList[i];
			   }
		   }
		});
	}

	$scope.referenceNumberCoverter = function(refNo){
		var referenceNumber = 0;

		if ( refNo <= 9 ) {
			referenceNumber = '000' + refNo;
		} else if ( refNo <= 99 ) {
			referenceNumber = '00' + refNo;
		} else if ( refNo <= 999 ) {
			referenceNumber = '0' + refNo;
		} else {
			referenceNumber = refNo;
		}

		return referenceNumber;

	}

	$('#raisedInvoiceDiscrepancyUpdateFilter').on('click',function(){
		 var requestData = {
			startDate: $('.invoicedate-start').attr('data-value'),
			endDate: $('.invoicedate-end').attr('data-value'),
			forUnit: $('#forUnit').attr('data-value')
		 };

	       $.ajax({
	            'global': false,
	            'url': '/your-business/raisedinvoicediscrepancybydaterange',
	            'data': requestData,
	            'method': 'POST',
	            'dataType': 'json',
	            'success': function (data) {
	            	$scope.raisedInvoicesList = data;
	            	$scope.$apply();
	            }
	        });
	});


  $scope.faqPagination = function() {

	$scope.numPerPage = 25;
	$scope.currentPage = 1;

    $scope.setPage = function (pageNo) {
    	$log.log('Page No.' + pageNo);
    };

    $scope.totalItems = $scope.raisedInvoicesList.length;

    $log.log($scope.totalItems);

    $scope.pageChanged = function() {
      $log.log('Page changed to: ' + $scope.currentPage);
	    };
  }

  $scope.faqPagination();


  //Sorting
  $scope.sortColumn = 'raisedOn'; //by default
  $scope.reverseSort = true;

  $scope.sortData = function(col) {
	  $scope.reverseSort =  ( col === $scope.sortColumn ) ? !$scope.reverseSort : false;
	  $scope.sortColumn = col;

	  $scope.getSortClass(col);
  }

  $scope.getSortClass = function(col) {
	  if ( col === $scope.sortColumn ) {
		  return $scope.reverseSort ? 'footable-sorted' : 'footable-sorted-desc';
	  }

	  return '';
  }

}]);
