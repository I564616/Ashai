/* globals window */

/*jshint unused:false*/
/* globals ACC */

	'use strict';

rm.cubpicks = {

	init: function(){
		//this.checkIfCupPicksCupLoaded();
		ACC.product.addListeners();
	},

	checkIfCupPicksCupLoaded: function(){
		var isCupInProgress = $('#cubPickSection').attr('data-cupRefreshInProgress');

		if(isCupInProgress === 'true')
		{
			//$('#cubPickSection').addClass('loading');
			console.log('Cup loading is in progress');

			setInterval(function(){
				$.ajax({
				url:'/b2bunit/checkCupRefreshStatus',
				type:'GET',
				success: function(result) {
					var jsonObj = JSON.parse(result);
					if(jsonObj.status === 'false')
					{
						window.location.reload();
						//$('#cubPickSection').removeClass('loading');
					}
				},
				error:function() {
					console.log('Error occured while checking deal refresh status');
				}
			});
			}, 5000);
		}
	}
};
