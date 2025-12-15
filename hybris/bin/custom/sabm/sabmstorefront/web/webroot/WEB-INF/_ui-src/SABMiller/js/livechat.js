/** Archived Livechat Code (archived March 2025) **/
// /* globals window */
// /* globals document */
// /* globals liveagent */
// /* globals dataLayer */
// /* globals embedded_svc */

// 'use strict';

// rm.livechat = {
//     init : function() {
//         $(document).ready(function() {
//             setTimeout(function(){

//                 rm.livechat.initLiveSnapIn();

//                rm.livechat.registerLiveChatButtonClicks();
//             },2000);
//         });
//     },
	
//     initLiveSnapIn : function(){
    
//     	/*jshint camelcase: false */
    	
//     	var initESW = function(gslbBaseURL) {
//     		embedded_svc.settings.displayHelpButton = true; //Or false
//     		embedded_svc.settings.language = ''; //For example, enter 'en' or 'en-US'

//     		embedded_svc.settings.defaultMinimizedText = 'Chat Now'; //(Defaults to Chat with an Expert)
//     		//embedded_svc.settings.disabledMinimizedText = '...'; //(Defaults to Agent Offline) 
//     		//embedded_svc.settings.widgetHeight = '50%';

//     		//embedded_svc.settings.loadingText = '...'; //(Defaults to Loading)
//     		//embedded_svc.settings.storageDomain = 'yourdomain.com'; //(Sets the domain for your deployment so that visitors can navigate subdomains during a chat session)

//     		// Settings for Live Agent
//     		embedded_svc.settings.avatarImgURL =$('#livechatAvatarImgURL').val();
//     		embedded_svc.settings.prechatBackgroundImgURL = $('#livechatPrechatBackgroundImgURL').val();
//     		embedded_svc.settings.waitingStateBackgroundImgURL = $('#livechatWaitingStateBackgroundImgURL').val();
//     		embedded_svc.settings.smallCompanyLogoImgURL = $('#livechatSmallCompanyLogoImgURL').val();

//     		//embedded_svc.settings.directToButtonRouting = function(prechatFormData) {
//     		// Dynamically changes the button ID based on what the visitor enters in the pre-chat form.
//     		//Returns a valid button ID.
//     		//};

//     		//var sHybrisUID = 'john.doe@test.com'; //${user.uid}; // This will pick up the Hybris logged on users email
//     		console.log('Firstname:'+$('#liveChatCustomDetails').attr('data-firstname'));
//     		console.log('email:'+$('#liveChatCustomDetails').attr('data-email'));  
//     		console.log('DefaultB2BUnit:'+$('#liveChatCustomDetails').attr('data-currentB2Bunit-id'));
//     		console.log('currentB2Bunit-name:'+$('#liveChatCustomDetails').attr('data-currentB2Bunit-name'));
//     		console.log('Case-subject:'+$('#liveChatCustomDetails').attr('data-currentB2Bunit-id') + '-' + $('#liveChatCustomDetails').attr('data-currentB2Bunit-name'));
    		 
    		
    		
//     		embedded_svc.settings.extraPrechatFormDetails = [{
//     		    'label':'First Name',
//     		    'name':'FirstName',
//     		    'value':$('#liveChatCustomDetails').attr('data-firstname'),
//     		    'displayToAgent':true
//     		},
//     		{
//     		    'label':'Last Name',
//     		    'value':$('#liveChatCustomDetails').attr('data-lastname'),
//     		    'displayToAgent':true
//     		}, {
//     		    'label':'Contact E-mail',
//     		    'value':$('#liveChatCustomDetails').attr('data-email'),
//     		    'displayToAgent':true
//     		}, {
//     		    'label':'Case Subject',
//     		    'value':$('#liveChatCustomDetails').attr('data-currentB2Bunit-id') + '-' + $('#liveChatCustomDetails').attr('data-currentB2Bunit-name'),
//     		    'displayToAgent':true
//     		},
//     		{
//     		    'label':'Venue',
//     		    'value':$('#liveChatCustomDetails').attr('data-currentB2Bunit-id') + '-' + $('#liveChatCustomDetails').attr('data-currentB2Bunit-name'),
//     		    'displayToAgent':true
//     		},
//     		{
//     		    'label':'Case Status',
//     		    'value':'New',
//     		    'displayToAgent':true
//     		},
    		
//     		{
//     		    'label':'DefaultB2BUnit',
//     		    'name':'DefaultB2BUnit',
//     		    'value':$('#liveChatCustomDetails').attr('data-currentB2Bunit-id'),
//     		    'displayToAgent':true
//     		},
//     		{
//     		    'label':'PK',
//     		    'name':'PK',
//     		    'value':$('#liveChatCustomDetails').attr('data-userpk'),
//     		    'displayToAgent':true
//     		},
//     		{
//   			  "label": "recordtype",
//   			  "value": "Live_Chat_Case",
//   			  "displayToAgent": true
//   			},
//   			{
//   			  "label": "Origin",
//   			  "value": "Chat",
//   			  "displayToAgent": true
//   			},
//   			{
//   			  "label": "Type",
//   			  "value": "Live Chat",
//   			  "displayToAgent": true
//   			} 
    		
//     		]; 
    		 
    		
//     		  embedded_svc.settings.extraPrechatInfo = [{
//     			  "entityName": "Contact",
//     			  "showOnCreate": true,
//     			  "linkToEntityName": "Case",
//     			  "linkToEntityField": "ContactId",
//     			  "saveToTranscript": "ContactId",
//     			  "entityFieldMaps": [{
//     			    "isExactMatch": false,
//     			    "fieldName": "FirstName",
//     			    "doCreate": true,
//     			    "doFind": true,
//     			    "label": "First Name"
//     			  }, {
//     			    "isExactMatch": false,
//     			    "fieldName": "LastName",
//     			    "doCreate": true,
//     			    "doFind": true,
//     			    "label": "Last Name"
//     			  }, 
    			    
//     			  {
//     			    "isExactMatch": true,
//     			    "fieldName": "Email",
//     			    "doCreate": true,
//     			    "doFind": true,
//     			    "label": "Contact E-mail"
//     			  }]
//     			}, 

//     			{
//     			  "entityName": "Account",
//     			  "linkToEntityName": "Case",
//     			  "linkToEntityField": "AccountId",
//     			  "entityFieldMaps": [{
//     			    "isExactMatch": true,
//     			    "fieldName": "Customer_Number_ZALB__c",
//     			    "doCreate": true,
//     			    "doFind": true,
//     			    "label": "DefaultB2BUnit"
//     			  }]
//     			},

//     			{
//     			  "entityName": "Case",
//     			  "showOnCreate": true,
//     			  "saveToTranscript": "CaseId",
//     			  "entityFieldMaps": [{
//     			    "isExactMatch": false,
//     			    "fieldName": "Subject",
//     			    "doCreate": true,
//     			    "doFind": false,
//     			    "label": "Case Subject"
//     			  }, {
//     			    "isExactMatch": false,
//     			    "fieldName": "Status",
//     			    "doCreate": true,
//     			    "doFind": false,
//     			    "label": "Status"
//     			  },
//     			  {
//     			    "isExactMatch": false,
//     			    "fieldName": "Case Record Type",
//     			    "doCreate": true,
//     			    "doFind": false,
//     			    "label": "recordtype"
//     			  },
//     			  {
//     				    "isExactMatch": false,
//     				    "fieldName": "Type",
//     				    "doCreate": true,
//     				    "doFind": false,
//     				    "label": "Type"
//     				  },
//     				  {
//     				    "isExactMatch": false,
//     				    "fieldName": "Origin",
//     				    "doCreate": true,
//     				    "doFind": false,
//     				    "label": "Origin"
//     				  }
//     			  ]
//     			}]
 

    		
// 			//NEW CODE STARTS
// //embedded_svc.settings.extraPrechatInfo = [{"entityFieldMaps":[{"doCreate":true,"doFind":false,"fieldName":"LastName","isExactMatch":true,"label":"Last Name"},{"doCreate":false,"doFind":false,"fieldName":"FirstName","isExactMatch":true,"label":"First Name"}, {"doCreate":true,"doFind":true,"fieldName":"Email","isExactMatch":true,"label":"Email"}],"entityName":"Contact","saveToTranscript":"Contact","showOnCreate":true}];
// //NEW CODE ENDS

				
//     		embedded_svc.settings.enabledFeatures = ['LiveAgent'];
//     		embedded_svc.settings.entryFeature = 'LiveAgent';
//     		embedded_svc.settings.initialInteractionState = 'WAITING';


// 			embedded_svc.init($('#livechatSandboxHostUrl').val(), 
// 							  $('#livechatInitUrl').val(), 
// 							  gslbBaseURL, 
// 							  $('#livechatOrgId').val(), 
// 							  'CUB_Live_Chat_Snap_In2', 
// 							  {
// 								baseLiveAgentContentURL : $('#livechatBaseLiveAgentContentUrl').val(),
// 								deploymentId : $('#livechatDeploymentId').val(),
// 								buttonId : $('#livechatButtonId').val(),
// 								baseLiveAgentURL : $('#livechatBaseLiveAgentUrl').val(),
// 								eswLiveAgentDevName : $('#livechatEswLiveAgentDevName').val()
// 							  });
// 		};
		
// 		if (!window.embedded_svc) {
// 			var s = document.createElement('script');
// 			s.setAttribute('src', $('#livechatSnapInOrgEswJsFileUrl').val());
// 			s.onload = function() {
// 				initESW(null);
// 			};
// 			document.body.appendChild(s);
// 		} else {
// 			initESW($('#livechatServiceUrl').val());
// 		}
    	
//     		/*jshint camelcase: true */
    	
//     },


//     liveChatGtm : function(){
//         dataLayer.push({'event':'Chatbox','eventCategory': 'Chatbox','eventAction': 'click','eventLabel':'click'});
//     },


   
    
//      registerLiveChatButtonClicks : function () {
//     	 $('.embeddedServiceHelpButton').on('click', function (event){ 
// 		     rm.livechat.liveChatGtm();
// 	     });
//      }
   
// };

// rm.livechat.init();