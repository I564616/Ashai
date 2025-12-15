
CUB.controller('personalAssistanceCtrl', ['$scope','$http','$window','angularUtils',function($scope,$http,$window,angularUtils) {

	$scope.checkSelected = function (){
		// var selectedType = $scope.pa.type;

		// console.log($scope.account);

		// switch (selectedType) {
		//     case 'account':
		//     	if($scope.account_no.$valid){
		// 			$scope.notComplete = false;
		// 		}
		// 		else{
		// 			$scope.notComplete = true;
		// 		}
		//     	break;
		//     case 'customer':
		//     	if($scope.customer_no.$valid && $scope.customer_name.$valid){
		// 			$scope.notComplete = false;
		// 		}
		// 		else{
		// 			$scope.notComplete = true;
		// 		}
		//     	break;
		//     case 'user':
		//     	if($scope.user_email.$valid){
		// 			$scope.notComplete = false;
		// 		}
		// 		else{
		// 			$scope.notComplete = true;
		// 		}
		//     	break;
		// }

		// $scope.paRequest.$setPristine();
		// $scope.paRequest.$setUntouched();
		// $scope.clearForm();
	}

	$scope.clearForm = function() {
		var inputs = $('#paSearchForm input');

		for (var i = 0; i<inputs.length; i++) {
			if(inputs[i].type == 'text'){
				console.log(inputs[i].type);
				inputs[i].value = '';
			}
		}
	}
		
	$scope.checkValid = function (form) {

		$scope.submitted = true;

		if(form.$valid){
			$("#paSearchForm").submit();
		} else {
			console.log('not valid');
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