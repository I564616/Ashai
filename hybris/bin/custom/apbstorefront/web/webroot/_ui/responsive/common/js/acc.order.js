ACC.order = {

	_autoload: [
	    "backToOrderHistory",
	    "bindMultidProduct",
	    "bindReorderButton"
	],

	backToOrderHistory: function(){
		$(".orderBackBtn > button").on("click", function(){
			var sUrl = $(this).data("backToOrders");
			window.location = sUrl;
		});
	},
	
	bindMultidProduct: function ()
	{
		// link to display the multi-d grid in read-only mode
		$(document).on("click",'.js-show-multiD-grid-in-order', function (event){
			ACC.multidgrid.populateAndShowGrid(this, event, true);
			return false;
		});

		// link to display the multi-d grid in read-only mode
		$(document).on("click",'.showMultiDGridInOrderOverlay', function (event){
			ACC.multidgrid.populateAndShowGridOverlay(this, event);
		});

	},
	
	bindReorderButton: function () 
    {   	
    	$( document ).ready(function ()
    	{
    		$('#keepProductLayer').modal({
        	    show: false	
    		});
    	});
    	
    	$(document).on("click", '.reorder-button', function (e) {
    		e.preventDefault();
    		var reorderForm = $(".reorderForm");
    		var orderCode = $(this).siblings("input[name*='orderCode']").val();
    		var reorderUrl = reorderForm.attr('action') + "?orderCode=" + orderCode;
    		$.ajax({
    		    type: 'post',
    		    url: reorderUrl,
    		    dataType: 'json',
        		beforeSend: function(xhr){
    				var csrfToken = ACC.config.CSRFToken;
    		        xhr.setRequestHeader('x-csrf-token', csrfToken);
    	        },
    		    success: function (data)
    			{
    		    	if(data == "true")
                	{
    		    		$("#keepProductLayer").find("input[name*='orderCode']").val(orderCode);
                		$('#keepProductLayer').modal('show');
                		$('#keepProductLayer').removeClass("cboxElement");
                	}
                	else
                	{
                		ACC.order.reorder(true, orderCode);
                	}
    		    	
    			},
    		    error: function (xmlHttpRequest, errorText, thrownError) {
    	            console.log("Error: "+ thrownError+" "+errorText);
    	           
    	        },
		  });
    		
    	});
    	
    	$(document).on("click", '.clearCartBtn', function (e) {
            e.preventDefault();
            ACC.order.reorder(true);            
    	});
    	
    	$(document).on("click", '.keepCartBtn', function (e) {
            e.preventDefault();
            ACC.order.reorder(false);
    	});
    	 
    },
    
    reorder: function(clearCart, orderCode)
    {
    	if(orderCode != undefined){
    		$("#keepProductLayer").find("input[name*='orderCode']").val(orderCode);
    	}
    	$("#clearCart").val(clearCart);
    	$("#reorderKeepCartForm").submit();
    }
    
};