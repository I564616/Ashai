// Angular App

var CUB = angular.module('CUB', []);

CUB.controller('formValidationCtrl', ['$scope',function($scope) {
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
}]);