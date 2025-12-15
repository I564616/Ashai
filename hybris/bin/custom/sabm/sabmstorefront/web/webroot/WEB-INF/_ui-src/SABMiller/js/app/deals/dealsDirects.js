// Directive for + and - to help check the qty value
CUB.directive('qtySelector', ['$parse', function($parse){
	return {
		restrict: 'A',
		link: function(scope, element, attrs){
			$(element).on('click touchstart',function(){
				setTimeout(function(){ // Wait for jquery to update the val
					scope.base.newQty = $(element).parent('.select-quantity').find('.qty-input').val();

          if($(element).parents('.deal').find('.deal-item-head').hasClass('single')){
            // console.log(scope.$parent);
            scope.$parent.deal.ranges[0].totalQty = scope.calcTotalQty(scope.$parent.deal,scope.$parent.deal.ranges[0]);
          } else {
            scope.$parent.range.totalQty = scope.calcTotalQty(scope.$parent.$parent.deal,scope.$parent.range);
          }
					
					scope.$apply();
				},10);
				
			});
		}
	}
}]);