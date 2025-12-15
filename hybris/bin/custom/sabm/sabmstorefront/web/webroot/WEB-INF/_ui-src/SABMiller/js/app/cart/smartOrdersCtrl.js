CUB.controller('smartOrdersCtrl', ['$scope','$http','$sce','angularUtils','orderByFilter', function($scope, $http,$sce,angularUtils,orderBy){
	
	$scope.init = function(){
		$scope.orders = JSON.parse($('#smartOrdersData').html());

		$scope.assignNewQty();
		$scope.createHtml();

		if($('.view-only-mode').length){
			$scope.bdeViewOnly = true;
		}
	};

	$scope.assignNewQty = function(){
		for(var i = 0; i < $scope.orders.products.length; i++){
			$scope.orders.products[i].newQty = $scope.orders.products[i].qty;
		}
	};

	$scope.createHtml = function(){
	    $scope.deal=[];

		dealTitle = $('#dealTitle').html();
		for(var i = 0; i < $scope.orders.products.length; i++){
            $scope.deal[i] ="";
		    if($scope.orders.products[i].dealsTitle != null){
                for(var j = 0; j < $scope.orders.products[i].dealsTitle.length; j++){
                    $scope.deal[i]+="<p><span class='deal-title'>"+dealTitle+": </span>"+$scope.orders.products[i].dealsTitle[j]+"</p>"
                }
            }
		}
    };

	$scope.enteredQty = function(event,index){

		$scope.orders.products[index].newQty = event.target.value;
	};

    $scope.setDisable = function(index){
        $scope.orders.products[index].isDisabled = true;
    };

	$scope.addToCart = function(product){
		var data = [{
			"productCodePost": product.code,
			"qty": product.newQty,
            "unit": product.newUnit
		}],
		url = "/sabmStore/en/cart/add/smartOrder";

		$http.post(url, data).success(function(result){
			ACC.product.displayAddToCartPopup(result);
			ACC.minicart.refreshMiniCartCount();
			ACC.common.refreshScreenReaderBuffer();
		}).error(function(){
		      console.log('failure');
		});

	};
	
	$scope.checkAndAddAllToCart= function(){
        if($('#changeDeliveryDatePresent').val() == 'true'){
            $('#outOfStockPopup h2').empty().append(ACC.deliveryPackDate); 
            $("#outOfStockPopup").modal();
            return
        }
        if($('#outOfStockProductsPresent').val() == 'true'){
        	$('#outOfStockPopup h2').empty().append(ACC.outOfStock);
        	$("#outOfStockPopup").modal();
			return
		}
        
        $scope.addAllToCart();
    };

	$scope.addAllToCart = function(){
		var data = [],
			url = "/sabmStore/en/cart/add/smartOrder";

		for(prod in $scope.orders.products){
		    if(( $scope.orders.products[prod].cubStockStatus==null || $scope.orders.products[prod].cubStockStatus.code != 'outOfStock') && ($scope.orders.products[prod].isDisabled==null || $scope.orders.products[prod].isDisabled != true)){
                var item = {
                    "productCodePost": $scope.orders.products[prod].code,
                    "qty": $scope.orders.products[prod].newQty,
                    "unit": $scope.orders.products[prod].newUnit
                }

                data.push(item);
			}
		}

		$http.post(url, data).success(function(result){
			ACC.product.displayAddToCartPopup(result);
			ACC.minicart.refreshMiniCartCount();
			ACC.common.refreshScreenReaderBuffer();
		}).error(function(){
		      console.log('failure');
		});

	};

	$scope.yearAgo = function(){
		var today = new Date(),
			year = today.getFullYear() - 1,
			month = today.getMonth(),
			day = today.getDate(),
			formattedDate = new Date(year,month,day);

			if(month === 2 && day === 29){
				formattedDate = new Date(year,month,day-1);
			}

			if($scope.orders.seeThisTimeLastYearLink){
				$('#smart-date').datepicker('setDate',formattedDate);
			}
	};

	$scope.getData = function(date,type){
		var url;

		if(type == 'specific'){
			console.log('specific');
			url = '/smartOrdersAjax?date=' + date + '&sort=d';
		} else if(type == 'prev'){
			if($scope.orders.previousOrdersLink){
				url = '/smartOrdersAjax?date=' + date + '&sort=p';
			} else {
				return
			}
		} else if(type == 'next') {
			if($scope.orders.nextOrdersLink){
				url = '/smartOrdersAjax?date=' + date + '&sort=n';
			} else {
				return
			}
		}

		$http.get(url).success(function(result){
			$scope.orders = result;
			$scope.assignNewQty();
		}).error(function(){
		      console.log('failure');
		});
	};

    $scope.unitChange = function(event,index,unit) {
        console.log(unit);
        $scope.orders.products[index].newUnit = unit;
        console.log($scope.orders.products);
    };

    $scope.sortSmartOrders = function(sortBy){
        //var products = $scope.orders;
        $scope.productsList = $scope.orders.products;
        // delete $scope.orders['products'];
        if (angular.isDefined(sortBy) || angular.isDefined($scope.selected)) {
            $scope.reverse = true;
            $scope.sortBy = (sortBy ? sortBy : $scope.selected);
            if ($scope.sortBy === 'brand') {
                $scope.reverse = false;
            }
            // Order by selected Sort
            $scope.sortProducts = orderBy($scope.productsList, $scope.sortBy, $scope.reverse);
            $scope.orders['products'] = $scope.sortProducts;
        }
    };
}]);
