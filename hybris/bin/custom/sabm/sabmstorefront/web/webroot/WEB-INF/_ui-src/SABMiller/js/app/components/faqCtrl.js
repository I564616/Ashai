CUB.controller('faqCtrl', ['$scope', '$http', '$log', function($scope, $http, $log) {

  $scope.init = function() {
    $scope.faqData = JSON.parse($('#faqData').html());
    $scope.filterCategories = {};

    $scope.showAllQuestions();

    $scope.faqRequest = {};
    $scope.$watch('faqSendQuestion',function(){
      $scope.faqRequest.request = $scope.faqSendQuestion.faqQuestion;
      $scope.faqRequest.prefcontact = 'Email';
      $scope.faqRequest.type = 'faqs';
    });

    $scope.faqSent = false;
    $scope.faqSentSuccess = false;
    $scope.faqSentError = false;

    $scope.faqPagination();
  },

  $scope.hideExpanded = function() {
    $('.collapse').collapse('hide');
  },

  $scope.sendFaq = function(form) {

    var formObj = {};
    formObj = $scope.faqRequest;

    var dataObj = {
      businessUnit: formObj.businessUnit,
      data: formObj.request,
      emailAddress: formObj.email,
      name: formObj.name,
      preferredContactMethod: formObj.prefcontact,
      requestType: formObj.type
    };

    $http.post("/sabmStore/en/businessEnquiry/send", dataObj)
    .success(function(data, status) {
      $scope.faqSent = true;
      $scope.faqSentSuccess = true;
      setTimeout(function() {
        $('.alert').alert('close');
        $scope.faqSentSuccess = false;
      }, 4000);
      $.magnificPopup.close();
    })
    .error(function(error) {
      $scope.faqSent = true;
      $scope.faqSentError = true;
      setTimeout(function() {
        $('.alert').alert('close');
        $scope.faqSentError = false;
      }, 10000);
      $.magnificPopup.close();
    });

  },

  $scope.faqPagination = function() {

	 if($(window).innerWidth() <= 320){
	    $scope.maxSize = 2;
	 }else{
	    $scope.maxSize = 3;
	 }

	$scope.numPerPage = 10;
	$scope.currentPage = 1;

    $scope.setPage = function (pageNo) {
      //$scope.currentPage = pageNo;
    	console.log('Page No.' + pageNo);
    };

    $scope.totalItems = $scope.faqData.categories.map(function(value, index, array) {
        return (value.questionList.length);
    })

    .reduce(function(previousValue, currentValue, index, array) {
        return previousValue + currentValue
    });

    $scope.pageChanged = function() {
      $log.log('Page changed to: ' + $scope.currentPage);
    };
  },

  $scope.updatePaginationItemCount = function(count){
	  $scope.totalItems = count;
	  $scope.currentPage = 1;
  },

  $scope.showAllQuestions = function(){
      $scope.cats = $scope.faqData.categories;
      $scope.all = [];

      for(var x=0;x<$scope.cats.length;x++){

          $scope.questions = $scope.faqData.categories[x].questionList;

      	for(var y=0;y<$scope.questions.length;y++){
          	$scope.all.push($scope.questions[y]);
          }
      }

      $scope.updatePaginationItemCount($scope.all.length);
  },

  $scope.showQuestionsByCategory = function(index){
	$scope.all = [];

	$scope.questions = $scope.faqData.categories[index].questionList;

  	for(var y=0;y<$scope.questions.length;y++){
      	$scope.all.push($scope.questions[y]);
      }

    $scope.updatePaginationItemCount($scope.all.length);
  },
  
  $scope.$watch('faq.search', function(value) {
	   $scope.searchText = value;
  });

}]);

CUB.filter('slice', function() {
  return function(arr, start, end) {
    return arr.slice(start, end);
  };
});

CUB.filter('highlight', function() {
	return function(input, searchText) {
		if(typeof searchText !== 'undefined' && searchText.length > 2){
         var searchExp = new RegExp('('+searchText+')', 'gi');
         return input.replace(searchExp,'<mark>$1</mark>');
		}else{
			return input;
		}
	};
});
