/*jshint unused:false*/

'use strict';

/* Tab Component */
rm.tabs = {
	
	init: function(){
		var tabContent = '<div class="tab-content" id="pills-tabContent"></div>';
		var tabCount = $('.tabComponentUID').length;
	    	
		$(tabContent).appendTo('.tabComponent');

		
		$('.tabComponent > #pills-tab').addClass('tab-count-' + tabCount);
		
		$('.tabComponentUID').map(function(){
		  		$('#'+$(this).val()).detach().appendTo('.tabComponent > #pills-tabContent');
		});

		/* display the 1st tab by default */
		$('.tabComponent #pills-tab li.nav-item:first-child').addClass('active');
		$('#pills-tabContent .tab-pane:first-child').addClass('active in');

		$('.tabComponent ul li #pills-tabContent').remove();		
	}
};
