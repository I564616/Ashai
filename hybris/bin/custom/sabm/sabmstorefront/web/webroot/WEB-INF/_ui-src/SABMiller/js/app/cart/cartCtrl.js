CUB.controller('cartCtrl', ['$scope','$http','$sce','angularUtils','$timeout', 'globalMessageService', function($scope, $http,$sce,angularUtils,$timeout, globalMessageService){


	$scope.init = function(pageLoad){
		$scope.addresses = JSON.parse($('#addressData').html());
		$scope.cartDealsData = JSON.parse($('#cartDealsData').html());

		$scope.selectedFree = {};
		$scope.selectedAddress = 0;
		$scope.partialDeals = [];
		$scope.selectedDeal = {
			selected: "deal_1"
		};

		$scope.partialPopup = false;
		$scope.freePopup = false;
		$scope.conflictPopup = false;

			$timeout(function(){
				if(!$.isEmptyObject($scope.cartDealsData)){
					if($scope.cartDealsData.conflict){
						$scope.dealsConflictData =  $scope.cartDealsData.conflict;
						if(pageLoad && $scope.dealsConflictData.length){ // If Conflicts array is not empty
						    $scope.conflictPopup = true;

	                        $scope.selectedDeal = {
	                            selected: $scope.cartDealsData.conflict[0].code
	                        };

							$.magnificPopup.open({
								items:{
							       src: '#dealsConflictPopup',
							       type: 'inline'
								},
						       modal: true,
							   removalDelay: 500,
							   mainClass: 'mfp-slide',
				               callbacks: {
				                	close: function(){
				                		if ($scope.orderSimulate === 'conflict'){
				                			rm.cart.getRecalculatedData();
				                		}
				                  	},
				                  	open: function(){
				                  		//trackConflictModalLaunch(); // GA Track Conflict Modal Launch
				                  		rm.tagManager.addDealsImpressionAndPosition('Viewed','ConflictDeal');
				                  	}
				               }
							});
						}
					}
					if($scope.cartDealsData.free){
						$scope.dealsFreeData =  $scope.cartDealsData.free;
						$scope.selectedFree = {
							selected: $scope.cartDealsData.free.selected
						};
						if (pageLoad && !$.isEmptyObject($scope.dealsFreeData)){ // If Free Products array is not empty
							$scope.freePopup = true;

							$.magnificPopup.open({
								items:{
							       src: '#chooseFreePopup',
							       type: 'inline'
								},
							   removalDelay: 500,
							   mainClass: 'mfp-slide',
							   modal: true,
				               callbacks: {
				                	close: function(){
				                		if ($scope.orderSimulate === 'free'){
				                			rm.cart.getRecalculatedData();
				                		}
				                  	}
				               }
							});

						}
					}
					if($scope.cartDealsData.partial){
						$scope.partialQualDeals = $scope.cartDealsData.partial;
						var deals = $scope.partialQualDeals.deals
						var conflicts = $scope.partialQualDeals.conflicts;

						if(pageLoad && ((typeof deals !== 'undefined' && deals.length > 0) || (typeof conflicts !== 'undefined' && conflicts.length > 0))){
								$scope.partialPopup = true;

								$.magnificPopup.open({
									items:{
								       src: '#partiallyQualified',
								       type: 'inline'
									},
							       modal: true,
								   removalDelay: 500,
								   mainClass: 'mfp-slide',
								   callbacks: {
					                	open: function(){
					                	    // Deleted for googleTagManager
											//trackPQDModalLaunch(); // GA Track PQD Modal Launch

					 					   rm.tagManager.addDealsImpressionAndPosition('Viewed','PartiallyQualifiedDeal');

					                  	},
					                  	close: function(){
					 					   //rm.tagManager.addDealsImpressionAndPosition('partiallyQualified','partiallyQualifiedModalRejected');
					                  	}
				               		}
								});
						}
					}
				}

				// Run order simulate if there are no popups
				if(pageLoad && $scope.partialPopup === false && $scope.freePopup === false && $scope.conflictPopup === false && $('#cartCtrl').data('calculation')){
					rm.cart.getRecalculatedData();
                }
				if(pageLoad){
					$scope.popupOrderSimulate();
				}
            },10);



	};

	$scope.dealInit = function(deal) {
    	deal.firstBaseProduct = deal.ranges[0].baseProducts[0];
	};

	$scope.chooseFreeProduct = function(dealCode){

		var data = {
				dealCode: dealCode
			};

			$http.post('/sabmStore/en/cart/showOtherFreeGoods', data).success(function(result){
				$scope.dealsFreeData = result;
				$scope.selectedFree.selected = dealCode;
				$.magnificPopup.open({
					items:{
				       src: '#chooseFreePopup',
				       type: 'inline'
					},
				   removalDelay: 500,
				   mainClass: 'mfp-slide',
				   modal: true,
	               callbacks: {
	                	close: function(){
	                		rm.cart.getRecalculatedData();
	                  	}
	               }
				});
			}).error(function(data, status){
			});
	}

	$scope.partialQualInit = function(){
		$scope.qualified = false;
	};

	$scope.getHtml = function(html) {
	      return $sce.trustAsHtml(html)
	}

	$scope.partialQualsValid = function(){
		var deals = [];

		if($scope.partialQualDeals){
			if (!$.isEmptyObject($scope.partialQualDeals)){
				for(deal in $scope.partialQualDeals.deals){
					if($scope.partialQualDeals.deals[deal].selected){
						for(range in $scope.partialQualDeals.deals[deal].ranges){
							var selectRange =$scope.partialQualDeals.deals[deal].ranges[range];
							selectRange.totalQty= $scope.calcTotalQty(selectRange);
							if(!$scope.rangeValid(selectRange)){
								return false;
							}
						}
						deals.push($scope.partialQualDeals.deals[deal]);
					} else {
						dealsSelected = true;
					}
				}

				for(conflict in $scope.partialQualDeals.conflicts){
					if($scope.partialQualDeals.conflicts[conflict].active){
						for(range in $scope.partialQualDeals.conflicts[conflict].active.ranges){
							var selectRange = $scope.partialQualDeals.conflicts[conflict].active.ranges[range];
							selectRange.totalQty =  $scope.calcTotalQty(selectRange);
							if(!$scope.rangeValid(selectRange)){
								return false;
							}
						}
						deals.push($scope.partialQualDeals.conflicts[conflict].active);
					}
				}

				if(deals.length != 0){
					$scope.partialDeals = deals;
					return true;
				} else {
					return false;
				}
			}
		}
	};

	// Run order simulate after the last Popup in cart - SABMC-903
	$scope.popupOrderSimulate = function(){

		if($scope.partialPopup === true){
			// Order Simulate on Partial
			$('#partiallyQualified .btn-secondary').on('click',function(){
				rm.cart.getRecalculatedData();
			});
		} else if($scope.freePopup === false) {
			// Order Simulate on Conflict
			$scope.orderSimulate = 'conflict';
		} else {
			// Order Simulate on Free
			$scope.orderSimulate = 'free';
		}
	};

	$scope.rangeValid = function(range){

		if(range.minQty > 0){
			if(range.minQty > range.totalQty){
				return false;
			}
		}

		return true;
	};

	$scope.calcTotalQty = function(range){
		var totalQty = 0;
		for(prod in range.baseProducts){
			if(range.baseProducts[prod].newQty){
				totalQty += parseInt(range.baseProducts[prod].newQty);
			}else {
				totalQty += parseInt(range.baseProducts[prod].qty);
			}
		}
		return totalQty;
	};

	$scope.resizeDealsData = {
		dealsCheckbox:function(deal, elemId){
			if(!deal||!deal.code){return;}
		    var defaultCartData =JSON.parse($('#cartDealsData').html());
			if(!deal.selected){
				var defDeals = defaultCartData.partial.deals;
				for(var j=0;j< defDeals .length;j++) {
					if(deal.code == defDeals[j].code){
						deal.ranges = defDeals[j].ranges;
					}
				}
			}

			// added for GTM - start
			var checkboxElemId = '#' + elemId;
			if (!$(checkboxElemId).hasClass('wasclicked')) {
				$(checkboxElemId).addClass('wasclicked');
				rm.tagManager.addProductImpressionListener();
			}
		},
		conflictRadio:function(deal, elemId){
			if(!deal||!deal.code){return;}
		    var defaultCartData =JSON.parse($('#cartDealsData').html());
			if(!deal.selected){
				var defConflicts = defaultCartData.partial.conflicts;
				for(var j=0;j<defConflicts.length;j++) {
					for(var k=0;k<defConflicts[j].conflict.length;k++) {
						var defDeal =defConflicts[j].conflict[k];
						if(!defDeal){continue;}
						if(deal.code == defDeal.code){
							deal.ranges = defDeal.ranges;
						}
					}
				}
			}

			// added for GTM - start
			var checkboxElemId = '#' + elemId;
			if (!$(checkboxElemId).hasClass('wasclicked')) {
				$(checkboxElemId).addClass('wasclicked');
				rm.tagManager.addProductImpressionListener();
			}
		}
	}

	$scope.addFreeToCart = function(selected){

		var data = {"dealCode": selected};

		$http.post('/cart/selectDealCart', data).then(function successCallback(response) {
				$.magnificPopup.close();
				// rm.cart.resetCart();
			}, function errorCallback(response) {
				$.magnificPopup.close();
			});
	};

	$scope.addPartialQualToCart = function() {
		var data = [];
		for(obj in $scope.partialDeals){
			var deal = {},
				baseProducts = [];

			deal.dealCode = $scope.partialDeals[obj].code;

			// Loop through ranges of deal
			for(range in $scope.partialDeals[obj].ranges){
				// Loop through products in range
				for(prod in $scope.partialDeals[obj].ranges[range].baseProducts){
					// If product has more than 0 quantity, add it to the cart/list
					if($scope.partialDeals[obj].ranges[range].baseProducts[prod].newQty > 0){
						var baseProd = {};

						baseProd.productCodePost = $scope.partialDeals[obj].ranges[range].baseProducts[prod].productCode;
						baseProd.qty = $scope.partialDeals[obj].ranges[range].baseProducts[prod].newQty;

						baseProducts.push(baseProd);
					}
				}
			}

			deal.baseProducts = baseProducts;
			data.push(deal);
		}

		$.magnificPopup.close();

		$http.post('/cart/add/partialdeal', data).success(function(result, status){
			ACC.product.displayAddToCartPopup(result);
			ACC.minicart.refreshMiniCartCount();
			ACC.common.refreshScreenReaderBuffer();

			rm.cart.getRecalculatedData(); // Run order simulate on add to cart - SABMC-903
			// Deleted for googleTagManager
			//trackPQDAdded(data.length); // GA track number of deals added to Cart from PQD modal
			//rm.cart.resetCart();

			$scope.cleanPQDarray(data);

		}).error(function(data, status){

		});
	};

	$scope.cleanPQDarray = function(deals){
		for(deal in deals){
			for(pqd in $scope.partialQualDeals.deals){
				if(deals[deal].dealCode === $scope.partialQualDeals.deals[pqd].code){
					$scope.partialQualDeals.deals.splice(pqd,1);
				}
			}

			for(conflicts in $scope.partialQualDeals.conflicts){
				for(conflict in $scope.partialQualDeals.conflicts[conflicts]){
					if(deals[deal].dealCode === $scope.partialQualDeals.conflicts[conflicts][conflict].code){
						$scope.partialQualDeals.conflicts.splice(conflicts,1);
						break;
					}
				}
			}
		}
	};

	$scope.selectAddress = function(i){
		$scope.selectedAddress = i;
		if($scope.addresses[i].defaultAddress) {
			$scope.setDeliveryAddress(false);
		}else if($scope.addresses[i].defaultB2BunitAddress){
			$scope.setDeliveryAddress(false);
		}
		else {
			$.magnificPopup.open({
				items:{
			       src: '#saveAsDefault',
			       type: 'inline'
				},
				modal: true,
				removalDelay: 500,
				mainClass: 'mfp-slide'
			});

		}
	};

	$scope.setDeliveryAddress = function(status){
		var data = {addressId:$scope.addresses[$scope.selectedAddress].id,defaultAddress:status};
        if ($('.loading-message').length > 0) {
            rm.utilities.loadingMessage($('.loading-message').data('login') || "",true);
        }
        $('body').addClass('loading');
		$http.post($('#updateSABMdeliveryAddressUrl').val(), data).success(function(result, status) {
			$('.selected-delivery-address').html($('.selected-delivery-address', result).html());
			$('#globalMessage').html($('#simulationErrors', result).html());
			$scope.addresses = JSON.parse($('#addressData', result).html());
			window.location.reload();
			rm.utilities.goBackTop();
			rm.cart.showRecalculate();
		}).error(function(result, status){
		});

		$.magnificPopup.close()
	};

}]);
