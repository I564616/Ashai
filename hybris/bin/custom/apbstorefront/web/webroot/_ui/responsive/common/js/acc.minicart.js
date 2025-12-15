ACC.minicart = {
		
		/*Bind minicart on mouseenter and mouseleave
		 * on click it redirect to cart page*/
		bindMiniCart: function () {
			$(document).on({
		        click: function(e) {        
		            e.preventDefault();
		            ACC.colorbox.close();
					var redirectUrl = $(this).data("hrefUrl");
					window.location = redirectUrl;
		        },
		        mouseenter: function(e){
		        	if ($(window).width() >= 1280) {
						e.preventDefault();
						var url = $(this).data("miniCartUrl");
						var cartName = ($(this).find(".js-mini-cart-count").html() !== 0) ? $(this).data("miniCartName") : $(this).data("miniCartEmptyName");
							ACC.minicart.openCartSummary();
						
					}
		        }
		    }, '.js-mini-cart-link');
			
			$(document).on("mouseleave", ".minicart-colorbox",
				function () {
					ACC.colorbox.close();
				}
			);
		},
    
    openCartSummary: function () {
		var asm = document.getElementById('_asm');
        if (asm != null) {
            var asmHeight = asm.offsetHeight;
        } else {
            var asmHeight = 0;
        };
		var totalWhiteSpacePadding = asmHeight + 13;
		
    	var miniCart = $(".js-mini-cart-link");
    	var url = miniCart.data("miniCartUrl");
        var cartName = (miniCart.find(".js-mini-cart-count").html() !== 0) ? miniCart.data("miniCartName"):miniCart.data("miniCartEmptyName");
    	 ACC.colorbox.open(cartName,{
            href: url,
            className: 'minicart-colorbox',
            width:"350px",
            close:'',
            title:'',
            opacity: 0,
            right: 5,
			top: totalWhiteSpacePadding
         });
    },
    /*Update minicart total on every add product into cart from PLP, PDP and SLP  */
    updateMiniCartDisplay: function(){
        var cartItems = $(".js-mini-cart-link").data("miniCartItemsText");
        var miniCartRefreshUrl = $(".js-mini-cart-link").data("miniCartRefreshUrl");
        $.ajax({
            url: miniCartRefreshUrl,
            cache: false,
            type: 'GET',
            success: function(jsonData){
                $(".js-mini-cart-count").html('<span class="nav-items-total">' + jsonData.miniCartCount + '</span>' );
                $(".js-mini-cart-link .js-mini-cart-price").html(jsonData.miniCartPrice);
                
            }
        });
    }

};
$(document).ready(function ()
		{
			ACC.minicart.bindMiniCart();
		});
