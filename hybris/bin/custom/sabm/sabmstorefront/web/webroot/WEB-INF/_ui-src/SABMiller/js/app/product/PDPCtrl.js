CUB.controller('PDPCtrl', ['$scope','$http','$sce','angularUtils','globalMessageService', function($scope, $http,$sce,angularUtils,$timeout, globalMessageService){

	$scope.$on('breakpointChange', function(event, breakpoint, oldClass) {
	  console.log('Entering:' + breakpoint.class);
	  console.log('Leaving:' + oldClass);
	  console.log('windowSize' + breakpoint.windowSize);
	});

	$scope.init = function(){
		console.log('here');
	};

	$scope.initSlider = function(slider,id){
		var elem = $('h3[id="' + id + '"]');
		if(slider === 'otherPack'){
			var anchor = $('#tab-anchor');

			if($scope.breakpoint.windowSize < 990){
				$scope.slideOtherPack = !$scope.slideOtherPack;
			} else {
				$scope.tab = 2;
			}

			setTimeout(function(){
				if($('.other-packages .slick-slider').length){
					$('.other-packages .slick-slider').get(0).slick.setPosition();
				}
			},10);

		} else if(slider === 'related'){
			if($scope.breakpoint.windowSize < 990){
				$scope.slideRelated = !$scope.slideRelated;
			} else {
				$scope.tab = 4;
			}

			setTimeout(function(){
				if($('.related-products .slick-slider').length){
					$('.related-products .slick-slider').get(0).slick.setPosition();
				}
			},10);
		}
	};

	$scope.accordionClick = function(id,scope){
		var elem = $('h3[id="' + id + '"]');

		$scope[scope] = !$scope[scope];
	};

	$scope.changeTab = function(tab){
		var anchor = $('#tab-anchor');

		$scope.tab = tab;
	};

	$scope.trackPDPOtherPackOptions = function(tab){
		if (typeof rm.tagManager.trackPDPOtherPackOptions !== 'undefined') {
			rm.tagManager.trackPDPOtherPackOptions();
		}
    };

    $scope.trackPDPPackConfiguration = function(tab){
    	if (typeof rm.tagManager.trackPDPPackConfiguration !== 'undefined') {
    		rm.tagManager.trackPDPPackConfiguration();
    	}
    };

}]);
