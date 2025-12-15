
/*jshint unused:false*/
/* globals document*/
/* globals window*/
'use strict';

rm.serviceRequest = {
	
	init:function(){
		this.bindMessageUpdate();
		this.hideTabNavItems();
		this.bindTabNavMenus();
	},
	//update email send type
	bindMessageUpdate:function(){
		$(document).on('click touchend', '.select-items.service-items li', function() {
			var $selectBtn = $(this).parent().siblings('.select-btn');
			var $selectKey = $(this).parent().siblings('.select-key');
			var $selectType = $(this).parent().siblings('.select-type');
			$selectBtn.text($(this).attr('data-value'));
			$selectKey.val($(this).attr('data-key'));
			$selectType.val($(this).attr('data-value'));
			$('#serviceMessage').val($(this).attr('data-msg'));
		});
		
		$('#serviceMessage').on('blur',function(){
			
			if($.trim($(this).val())===''){
				$(this).next().removeClass('hidden');
			}else{
				$(this).next().addClass('hidden');
			}
		});
	},
	bindTabNavMenus:function(){
		var $serviceRequest = $('.service-request');
		var $tabItemContainer = $('.service-request').find('.tab-items-container');

		if(window.location.hash.substr(1) !== ''){
			var hash = window.location.hash.substr(1);
			
			if(hash === 'contact-details'){
				$('button[data-id="contact-us"]').addClass('active');
				$tabItemContainer.find('#contact-us-tab').show();
				
				//scroll down to the bottom of the page to show the contact us details
				$('html, body').animate({
			        scrollTop: $(document).height()
			    }, 500);
				
			}else{
				$('button[data-id="'+hash+'"]').addClass('active');
				$tabItemContainer.find('#'+hash+'-tab').show();
			}

		}else{
			$('button[data-id="contact-us"]').addClass('active');
			$tabItemContainer.find('#contact-us-tab').show();
		}
				
		$serviceRequest.find('.nav-link').each(function(){
			if($(this).length > 0){
				$(this).live('click',function(){
					
					var $id = $(this).attr('data-id');

			        window.location.hash = $id;
					$serviceRequest.find('.nav-link').removeClass('active');

					/* check if the current tab menu has `active` class */
					if(!$(this).hasClass('active')){
						/* add `active` class to the selected tab menu */
						$(this).addClass('active');
						
						/* display the selected tab item */
						rm.serviceRequest.selectActiveTabMenu($id);
					}
				});
			}
		});
		
	},
	hideTabNavItems:function(){
		var $tabItemContainer = $('.service-request').find('.tab-items-container');
		
		$tabItemContainer.find('.tab-item').each(function(){
			$(this).hide();
		});
	},
	selectActiveTabMenu: function(tabID){
		var $tabItemContainer = $('.service-request').find('.tab-items-container');
		rm.serviceRequest.hideTabNavItems();
		$tabItemContainer.find('#'+tabID+'-tab').show();
	}

};