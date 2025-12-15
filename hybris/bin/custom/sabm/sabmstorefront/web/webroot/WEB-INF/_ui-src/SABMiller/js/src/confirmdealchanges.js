
'use strict';

rm.confirmdealchanges = {
		addEmailItems1:'<li><div class="checkbox"><input name ="emails" id="checkEmail',
		addEmailItems2:'" class="hidden" type="text" value="',
		addEmailItems3:'"><input name="toEmails" id="check',
		addEmailItems4:'" type="checkbox" checked><label for="check',
		addEmailItems5:'">',
		addEmailItems6:'</label></div></li>',
		emailSize:$('.list-checkbox .checkbox').size(),
		
		bindSelectEmail: function(){
			$('.list-checkbox .checkbox').each(function(index){
				var $thisCheckBox = $('#check'+index);
				
				$thisCheckBox.on('change',function(){
					if($thisCheckBox.is(':checked')){
						$('#checkEmail'+index).attr('name','emails');
					}else{
						$('#checkEmail'+index).removeAttr('name');
					}
				});
			});
		},
		
		bindAddEmail: function(){
			$('#add-email_button').on('click',function(){
				var inputEmail = $('#add-email-filed').val();
				if(!rm.customer.emailInvalid(inputEmail)){
					var selectFiled = '';
					
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems1+rm.confirmdealchanges.emailSize;
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems2+inputEmail;
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems3+rm.confirmdealchanges.emailSize;
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems4+rm.confirmdealchanges.emailSize;
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems5+inputEmail;
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems6;
					
					rm.confirmdealchanges.emailSize = parseInt(rm.confirmdealchanges.emailSize) + 1;
					
					if($('.list-checkbox').html() === ''){
						$('.list-checkbox').addClass('list-checkbox-bordered');
					}					
					$('.list-checkbox').append(selectFiled);
					rm.confirmdealchanges.bindSelectEmail();
					rm.confirmdealchanges.checkSaveButton();
					$('#confirmSend_button').removeAttr('disabled');
					$('#add-email-filed').val('');
					$('#email-error').hide();
				}else{
					$('#email-error').show();
				}
			});
			
		},
		checkSaveButton: function(){
			//if the checkBox is clicked  then judge selected case
			$('input:checkbox[name="toEmails"]').click(function(){
				var flag = false;
				$('#confirmSend_button').attr('disabled','disabled');
				$('.list-checkbox .checkbox').each(function(index){
					var $thisCheckBox = $('#check'+index);
						if($thisCheckBox.is(':checked')){
							flag = true;					
					}
				});
				var $thisCheckBox = $('#checkSendToMe');
				if($thisCheckBox.is(':checked')){
					flag = true;
				}

				if(flag){
					$('#confirmSend_button').removeAttr('disabled');
				}				
			});	
	
		},
		checkSendEmailConfirmation: function(){
			$('input:checkbox[name="sendToMe"]').click(function(){
				//var flag = false;
				$('#confirmSend_button').attr('disabled','disabled');
				
				var $thisCheckBox = $('#checkSendToMe');
				if($thisCheckBox.is(':checked')){
					$('#confirmSend_button').removeAttr('disabled');
				}
				var flag = false;
				$('.list-checkbox .checkbox').each(function(index){
					var $thisCheckBox = $('#check'+index);
						if($thisCheckBox.is(':checked')){
							flag = true;					
					}
				});
				if(flag){
					$('#confirmSend_button').removeAttr('disabled');
				}
				
			});	
	
		},
			
		init: function ()
		{
			rm.confirmdealchanges.bindAddEmail();
		    rm.confirmdealchanges.bindSelectEmail();
			rm.confirmdealchanges.checkSaveButton();
			rm.confirmdealchanges.checkSendEmailConfirmation();
		},
};
