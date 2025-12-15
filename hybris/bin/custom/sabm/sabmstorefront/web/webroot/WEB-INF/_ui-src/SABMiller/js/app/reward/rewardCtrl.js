CUB.controller('rewardsCtrl', ['$scope', function($scope) {

	$scope.init = function() {
        $scope.b2bUnits = JSON.parse($('#b2bUnits').html());
        $scope.user = JSON.parse($('#user').html());

        $scope.selectedUsersQty = 1;
        $scope.pointsAllToOneVenue = 'false';
        $scope.userCanAccessRewards = false;
        $scope.contactMobile = $scope.user.mobileNumber;
        $scope.contactLandline = $scope.user.businessContactNumber;

        $scope.pointAccrual();
        $scope.userSelect();
        $scope.venueCount();
    };

    $scope.pointAccrual = function() {
        angular.forEach($scope.b2bUnits, function(venue) {
            if(venue.rewardAllToOneVenue){
                $scope.pointsAllToOneVenue = 'true';
                $scope.b2bUnitForPointsAllToOneVenue = venue.name;
                $scope.b2bUnitForPointsAllToOneVenueId = venue.uid;
            }
        });
    },

    $scope.userSelect = function() {
       $scope.selectedUsers = [];
       angular.forEach($scope.b2bUnits, function(venue) {
            angular.forEach(venue.customers, function(user) {
                if(user.canAccessRewards){
                    $scope.selectedUsers.push(
                        user.uid+'|'+venue.uid
                    );
                };
            });
       });
       $scope.selectedUsersQty = $scope.selectedUsers.length + 1;
       $scope.selectedUsers = $scope.selectedUsers.toString();
    },

    $scope.venueCount = function() {
        if($.isEmptyObject($scope.b2bUnits) || $scope.b2bUnits.length <= 1) {
            $scope.venueSingle = true;
        } else {
            $scope.venueMultiple = true;
        }
    },

    $scope.venueSearch = function() {
        $scope.allVenues = [];
        angular.forEach($scope.b2bUnits, function(venue) {
            $scope.allVenues.push({
                value: venue.name,
                uid: venue.uid
            });
        });
        $('#venue-search')
        .on('tokenfield:edittoken', function (e) {
            e.preventDefault();
        })
        .on('tokenfield:removedtoken', function (e) {
            $('#b2bUnitForPointsAllToOneVenue').val('');
        })
        .tokenfield({
            autocomplete: {
                appendTo: '.search.venues',
                source: $scope.allVenues,
                select: function(event, ui) {
                    $('#b2bUnitForPointsAllToOneVenue').val(ui.item.uid);
                }
            },
            limit: 1,
            showAutocompleteOnFocus: true
        });
    }

}]);
