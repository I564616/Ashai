
/*jshint unused:false*/
/* globals enquire*/
/* globals window */
/* globals trackPDPSaveToTemplate */
/* globals trackPDPPriceConditions */

'use strict';

rm.productdetails = {

	init:function(){
		//this.checkIfCupDealsLoaded();
		this.createOrderTemplate();
	},

	checkIfCupDealsLoaded: function(){
		var inProgress = $('#product-detail-panel').attr('data-cupdealsRefreshInProgress');

		if(inProgress === 'true')
		{
			rm.utilities.sapCall = true;

			//$('body').addClass('loading');
			setInterval(function(){
				$.ajax({
				url:'/b2bunit/checkCupDealsRefreshStatus',
				type:'GET',
				success: function(result) {
					var jsonObj = JSON.parse(result);
					if(jsonObj.status === 'false')
					{
						window.location.reload();
					}
				},
				error:function() {
					console.log('Error occured while checking deal refresh status');
				}
			});
			}, 5000);
		}
	},
	createOrderTemplate: function() {
		$('#save-to-template-pdp').on('click',function(){
			$('#save-to-template .error').addClass('hidden');
			
			if (typeof rm.tagManager.trackPDPSaveToTemplate!=='undefined') {
				rm.tagManager.trackPDPSaveToTemplate();
			}
		});
		$('#priceConditionsTag').on('click',function(){
			if (typeof rm.tagManager.trackPDPPriceConditions!=='undefined') {
				rm.tagManager.trackPDPPriceConditions();
			}
        });
		$('.createTemplateBtn').on('click touchstart', function(e){
			if($('#template-name').val().trim() === '') {
				$('#empty-msg').removeClass('hidden');
				return false;
			}
		});
	}
};
