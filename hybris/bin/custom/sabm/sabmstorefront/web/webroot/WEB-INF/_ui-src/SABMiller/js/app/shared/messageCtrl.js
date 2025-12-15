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