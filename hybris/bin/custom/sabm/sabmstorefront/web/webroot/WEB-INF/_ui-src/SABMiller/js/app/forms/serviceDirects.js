
// Directive for + and - to help check the qty value
CUB.directive('qtySelectorService', ['$parse','$timeout', function($parse,$timeout){
	return {
		restrict: 'A',
		link: function(scope, element, attrs){
			$(element).on('click touchstart',function(){
				$timeout(function(){ // Wait for jquery to update the val
					var form = $(element).parent('.select-quantity').find('.qty-input').attr('data-scope'),
						val = $(element).parent('.select-quantity').find('.qty-input').attr('data-val');

					
					scope[form][val] = $(element).parent('.select-quantity').find('.qty-input').val();

				},10);
				
			});
		}
	}
}]);