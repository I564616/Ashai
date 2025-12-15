/* globals window */

/*jshint unused:false*/
/* globals ACC */
/* globals enquire*/


	'use strict';

rm.createordertemplate = {
	
	init: function(){
		this.createNewOrderTemplate();
	},
	
	createNewOrderTemplate: function() {
		
		
		$('.magnific-template-order').on('click',function(){
			$('#create-empty-msg').addClass('hidden');
		});
		
		$('#create-new-template .saveTemplateBtn').on('click touchstart', function(e){
			e.stopPropagation();
			
			if($('#create-template-name').val().trim() === '') {
				$('#create-empty-msg').removeClass('hidden');
				e.preventDefault();
			}
		});
	}
};