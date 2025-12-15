'use strict';


rm.user = {
     
      setupListeners: function() {
    	  
    	  $('.users-table .sendWelcomeEmail').on('click', function(e) {
    		e.preventDefault();
    		var that = $(this);
    		var customerUid = $(that).attr('id');
    		
    		$.ajax({
    			url:'/sabmStore/en/register/sendWelcomeEmail/' + customerUid,
    			type:'POST',
    			success: function(result) {
					console.log('success: ' + JSON.stringify(result));
					
					$.magnificPopup.close();
					$('#sendWelcomeEmailPopup p').html($('#sendWelcomeEmailPopup p').html() + ' ' + customerUid + '.');
					$.magnificPopup.open({
						items:{
					        src: '#sendWelcomeEmailPopup',
					        type:'inline'
						},
				        modal: true
					});				
					
					/*
					if ($(that).text().trim() === 'Send') {
						$(that).text('Re-Send');
					}
					*/
    			},
    			error: function(result) {
					console.log('error: ' + JSON.stringify(result));
				}
    		});
    					
    	});  
      }
      
};

$('document').ready(function() {
	rm.user.setupListeners();
});