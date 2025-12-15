ACC.savedcards = {

	_autoload: [
	    "bindSavedCardElements",
	    "showPaymentIframe",
	    "POSTCardDetails"
	],

	bindSavedCardElements: function()
	{	
		//submit to add new card
		//max 3 can be added
		$(document).on("click", '#addCCBtn', function (e)
    	{
			var iframe = document.getElementById("addCardIframe");
	        iframe.contentWindow.postMessage('doCheckout', '*');
    	});
		
	},
	
	showPaymentIframe: function()
    {
        var postMessageStrings = false;
        
        try
        { 
        	window.postMessage({
        		toString: function()
        		{ 
        			postMessageStrings = true; 
        		}
        	},"*");
        } catch(e)
        {
        	
        }
        
        function receiveMessage(event)
        {
        	
        	if (event.origin.indexOf("paynow") === -1)  return;

            document.getElementById("addCCBtn").disabled = false;

            var payload = event.data;
            if(payload == null || payload == undefined || payload == ""){
            	
            	
            	
            }
            console.log("before extract "+ JSON.stringify(payload));
            if (typeof event.data == 'string') 
            {
                var pairs = payload.split("&");
                payload = {};
                for (var i = 0; i < pairs.length; i++) 
                {
                    var element = pairs[i];
                    var kv = element.split("=");
                    payload[kv[0]] = kv[1];
                }
            }

            // enable add credit cart button
            document.getElementById("addCCBtn").disabled = false;
            if('errors' in payload){
            	var getIframe = $('.iframe-content');
            	var getIframeheight = getIframe.height();
            	var getError = payload.errors.split(',');
       	    	var totalError = getError.length;
       	    	var getNewHeight = 270;
       	    	if(totalError == 4){ 
       	   			getNewHeight = getNewHeight +  20 * totalError + "px" ;
       	    	}
       	    	else
       	    	{
       	    		getNewHeight = getNewHeight + 25 * totalError + "px" ;
       	    	}
            	
            	getIframe.css('height', getNewHeight);
            	
                //turn off spinner
                $('html').unblock();

                // disable add credit cart button
                document.getElementById("addCCBtn").disabled = true;
            }

            if ('data' in payload) 
            {
                if (payload.data.r == '1') 
                {
                    // Modern browser
                    // Use payload.data.x
                    ACC.savedcards.POSTCardDetails(payload.data);
                }

            } else 
            {
    	        if (payload.r == '1') 
    	        {
    	        	// Old browser don't use payload.data.x
    	        	ACC.savedcards.POSTCardDetails(payload);
    	        }
            }
        }
        
        // Attach Listeners
	    if (window.addEventListener) 
	    {
	        window.addEventListener("message", receiveMessage, false);
	    } else 
	    {
	        window.attachEvent("onmessage", receiveMessage);
	    }
    },
    
    getJSONDataForAddCreditCard: function (response) 
    {
    	var cards = new Object();
    	var cardData = new Object();
    	var cardArr = [];
    	var setDefault = $("#setDefaultCard").is(":checked");
        if (null != response) 
        {
        	var expiryDate = response.card_expiry;
        	if(null != expiryDate)
        	{
        		var splitExpDate = expiryDate.split("/");
        		cardData.expiryMonth = splitExpDate[0];
            	cardData.expiryYear = splitExpDate[1];
        	}
        	cardData.accountHolderName = response.card_holder;
        	cardData.cardType = response.card_type;       	
        	cardData.cardNumber = response.card_number;
        	cardData.saved = true;
        	cardData.displayName = "ABC" + response.card_holder;
        	cardData.token = response.token;
        	cardData.defaultPaymentInfo = setDefault;
            cardArr.push(cardData);
        	
        }
        cards.paymentInfos = cardArr;
        return JSON.stringify(cards);
        
    },
    
    POSTCardDetails: function(response)
    { 
    	// send request to fatzebra to add card in saved card page
		var currentPage = $('#checkPage').val();
		
		if ((currentPage != "paymentdetail") && (currentPage != "directdebit"))  {
			if(response != undefined){
				var addCardUrl = $('input[name=addCardUrl]').val();
				var data = ACC.savedcards.getJSONDataForAddCreditCard(response);

				$.ajax({
					url: addCardUrl,
					type: 'POST',
					contentType: 'application/json',
					data: data,
					async: false,
					beforeSend: function(xhr){
						var csrfToken = ACC.config.CSRFToken;
						xhr.setRequestHeader('x-csrf-token', csrfToken);
					},
					success: function (response) {
						$('.global-alerts').replaceWith($(response).find('.global-alerts'));
						$("#addCreditCardSection").replaceWith($(response).find("#addCreditCardSection"));
						$('.pageBodyContent').animate({
									scrollTop: 0
									}, 1000);
					},
					error: function (jqXHR, textStatus, errorThrown) {
						// log the error to the console
						console.log("The following error occured: " + textStatus, errorThrown);
					}
				});
			}
		}
 	
    }
              
}
