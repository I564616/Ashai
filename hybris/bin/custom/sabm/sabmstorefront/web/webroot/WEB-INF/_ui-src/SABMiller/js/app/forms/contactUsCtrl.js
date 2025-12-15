
CUB.controller('contactUsCtrl', ['$scope','$http','$window','angularUtils',function($scope,$http,$window,angularUtils) {
	
	$scope.contactUsInit = function(){
		$scope.sr = {};
		$scope.delIssueChecks = [];

		$scope.$watch('serviceRequest.$valid',function(validity){
			if(validity){
				$scope.notComplete = false;
			} else {
				console.log('form not valid');
			}
		});
	};
		
	$scope.checkValid = function (form) {
		if(form.$valid){
			$scope.notComplete = false;
			$("#serviceRequestForm").submit();
		} else {
			$scope.notComplete = true;
		}
	};

	$scope.updateDelIssueChecks = function(i){

		if(angularUtils.arrayContains($scope.delIssueChecks,i)){
			$scope.delIssueChecks.splice($scope.delIssueChecks.indexOf(i), 1);
		} else {
			$scope.delIssueChecks.push(i);
		}
    };

}]);