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
			return $filter('date')($scope.customDateFormatWithHyphen(date)+ ' (Current)');
		} else {
			return $filter('date')($scope.customDateFormatWithHyphen(date));
		}
	},
	$scope.customDateFormatGeneric = function (timestamp, options, replaceSlashWithHyphen) {
        var dateStr = new Date(parseInt(timestamp)).toLocaleString("en-AU", options);
        if (replaceSlashWithHyphen) {
            dateStr = dateStr.replace(/\//g, '-');
        }
        return dateStr;
    },
    $scope.customDateFormatWithHyphen = function (timestamp) {
        return b.customDateFormatGeneric(timestamp, {
            timeZone: 'Australia/Sydney',
            year: 'numeric',
            month: 'numeric',
            day: 'numeric'
        }, true);
    },
   $scope.customDateFormatWithWeek = function (timestamp) {
        return b.customDateFormatGeneric(timestamp, {
            weekday: 'short',
            timeZone: 'Australia/Sydney',
            year: 'numeric',
            month: 'numeric',
            day: 'numeric'
        }, false);
    },
    $scope.customDateFormat = function (timestamp) {
        return b.customDateFormatGeneric(timestamp, {
            timeZone: 'Australia/Sydney',
            year: 'numeric',
            month: 'numeric',
            day: 'numeric'
        }, false);
    },

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
