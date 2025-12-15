

var CUB = angular.module('CUB', ['ngAnimate', 'NgSwitchery', 'breakpointApp', 'ngSanitize', 'ui.bootstrap', 'ngTouch'])

.service('angularUtils',  function() {
	return { 
		arrayContains: function(array, item){

				for (var i in array) {
					if (array[i] === item) {
						return true;
					}
				}
				return false;

		}
	}
})
.service('globalMessageService', function(){
	var currentMessageType = '';
	var currentMessage = '';
	return {
		setMessage: function(messageType, message) {
			currentMessageType = messageType;
			currentMessage = message;
		},
		getMessageType: function() {
			return currentMessageType
		},
		getMessage: function() {
			return currentMessage
		},
		watch: function() {
			return {
				currentMessageType: currentMessageType,
				currentMessage: currentMessage
			}
		}
	}
});

CUB.config(["$httpProvider", function(provider) {
  provider.defaults.headers.post['CSRFToken'] = ACC.config.CSRFToken;
}]);
