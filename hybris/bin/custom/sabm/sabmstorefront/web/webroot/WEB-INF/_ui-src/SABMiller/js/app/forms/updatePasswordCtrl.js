CUB.controller('updatePasswordCtrl', ['$scope',function($scope) {
  $scope.init = function(){
    $scope.updatePassword.rules = {
      'characters': false,
      'numbers': false,
      'show': false
    };

  }
	$scope.checkNewPassword = function(){
    var chars = /[A-Za-z]\w{7,}/g;
    var nums = /[0-9]\w{0,}/g;

    console.log($scope.updatePassword.rules.numbers);

    if(chars.test($scope.updatePassword.newPassword.$modelValue)){
      $scope.updatePassword.rules.characters = true;
    } else {
      $scope.updatePassword.rules.characters = false;
    }

    if(nums.test($scope.updatePassword.newPassword.$modelValue)){
      $scope.updatePassword.rules.numbers = true;
    } else {
      $scope.updatePassword.rules.numbers = false;
    }

	};


}]);
