ACC.checkoutsteps = {

	_autoload: [
		"permeateLinks",
		"displayDeferredDelivery",
		"displayStandardDelivery"
	],
			
	permeateLinks: function() {

		$(document).on("click",".js-checkout-step",function(e){
			e.preventDefault();
			window.location=$(this).closest("a").attr("href")
		})
	},
	
	displayDeferredDelivery: function() {
		$('input:radio[id="deferred"]').change(function(){
		    $(".deffered-delivery-dates").css("display", "block");
		});	
		
		$('input:radio[id="standard"]').change(function(){
	    	$(".deffered-delivery-dates").css("display", "none");
		});	
	},
	
	displayStandardDelivery: function() {
		$('input:radio[id="standard"]').change(function(){
		    $(".cut-off-message").css("display", "block");
		});	
		
		$('input:radio[id="deferred"]').change(function(){
	    	$(".cut-off-message").css("display", "none");
		});	
	}

	
};