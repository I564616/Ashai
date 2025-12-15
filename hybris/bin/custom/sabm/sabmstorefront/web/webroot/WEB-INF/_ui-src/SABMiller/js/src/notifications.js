/* globals document */
/* globals window */
/* globals validate */
/*jshint unused:false*/

'use strict';


rm.notifications = {
	init : function() {
		rm.notifications.showNotifications();
		rm.notifications.hideNotification();
		/*rm.notifications.showUnsavedChangesPopup();*/
	},

	showNotifications: function(){
    setTimeout(function() {
      console.log('fired');
		  $('.home-notification-box').slideDown();
    }, 1000);
	},
	hideNotification : function() {

		$('.hide-notification').on('click', function(e) {
			e.preventDefault();
			$.ajax({
				url : '/notification/hide/' + $(this).attr('id'),
				type : 'GET'
			});
			$(this).closest('.home-notification-box').slideUp();
		});
	},
	
	/*invokeUnsavedChangesPopup : function() {
		console.log('onbeforeunload switched ON');
 
		//$(window).on('beforeunload', function() {
			//return 'You have unsaved changes!';
		//});

		$('body').addClass('unsaved-changes');
		
	},
	
	switchOffUnsavedChangesPopup : function() {
		$('body').removeClass('unsaved-changes');
		console.log('onbeforeunload switched OFF');
		//$(window).off('beforeunload');
	},

	showUnsavedChangesPopup : function(){
		console.log($('unsavedNotification').scope());
		$('a').on('click', function(e){
			if ($('body.unsaved-changes').length) {
				console.log('has unsaved changes');
				e.preventDefault();
				$('#unsavedNotification').modal('show');
			}
		});
	}*/
};