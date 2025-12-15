
/*jshint unused:false*/
/* globals document*/
	'use strict';

rm.contactus = {
	
	init:function(){
		this.bindElementsChange();
	},
	
	bindElementsChange:function(){
		$('#contactUsSubject, #contactUsMessage').on('keyup',function(){
			rm.contactus.changeButtonStatus();
		});
		
		$('#contactUsSubject, #contactUsMessage').on('blur',function(){
			
			if($.trim($(this).val())===''){
				$(this).next().removeClass('hidden');
			}else{
				$(this).next().addClass('hidden');
			}
		});
	},
	
	changeButtonStatus:function(){
		if($.trim($('#contactUsSubject').val())==='' || $.trim($('#contactUsMessage').val())===''){
			$('#btnSend').attr('disabled','disabled');
		}else{
			$('#btnSend').attr('disabled',false);
		}
	},
	
	
};