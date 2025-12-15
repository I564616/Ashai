
// Directive to check email address against hybris
CUB.directive('emailChecker', function(){
	return function(scope, element, attrs){
			console.log(scope);
			$(element).on('click',function(){
				var email = $(this).val();
				console.log('clicked');
			});
		}

});