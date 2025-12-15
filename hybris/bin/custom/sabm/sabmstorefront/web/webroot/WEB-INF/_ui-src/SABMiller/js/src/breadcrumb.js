
/*jshint unused:false*/
/* globals enquire*/
	'use strict';

rm.breadcrumb = {
	
	init:function(){
		this.updateMobileBreadcrumb();
	},
	
	updateMobileBreadcrumb:function(){
		var screenMobileMax = '767px';
		var screenTablet = '768px';
		enquire.register('screen and (max-width:'+screenMobileMax+')',function(){
			var lastLi = $('.breadcrumb li').last();
			
			//var firstLi = $('.breadcrumb li').first().
			$('.breadcrumb li').each(function(){
				if (($(this) === lastLi) || ($(this).html() !==lastLi.prev().html())){
					$(this).addClass('hidden');
				}
			});
		});
		enquire.register('screen and (min-width:'+screenTablet+')',function(){
			$('.breadcrumb li').each(function(){
				$(this).removeClass('hidden');
			});
		});
	}
};