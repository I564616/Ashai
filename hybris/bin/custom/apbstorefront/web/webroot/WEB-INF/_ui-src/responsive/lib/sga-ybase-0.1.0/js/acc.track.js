ACC.track = {
	trackAddToCart: function (productCode, quantity, cartData)
	{
		window.mediator.publish('trackAddToCart',{
			productCode: productCode,
			quantity: quantity,
			cartData: cartData
		});
	},
	trackRemoveFromCart: function(productCode, initialCartQuantity)
	{
		window.mediator.publish('trackRemoveFromCart',{
			productCode: productCode,
			initialCartQuantity: initialCartQuantity
		});
	},

	trackUpdateCart: function(productCode, initialCartQuantity, newCartQuantity)
	{
		window.mediator.publish('trackUpdateCart',{
			productCode: productCode,
			initialCartQuantity: initialCartQuantity,
			newCartQuantity: newCartQuantity
		});
	},
	trackAddProductToCart: function(productCode, quantity, label)
	{
		window.mediator.publish('trackAddProductToCart',{
			productCode: productCode,
			quantity: quantity,
			label: label
		});
	},
	//Google tracking for asahiProductClick event
	trackProductClick: function(productDataCode,label)
	{
		window.mediator.publish('trackProductClick',{
			productCode: productDataCode,
			label:label
		});
	},
	//Google tracking for asahiSection1Recommendations,asahiSection2Recommendations,asahiSection3Recommendations event
	trackProductRecommendation: function(section1Product,section2Product,section3Product,label)
	{
		window.mediator.publish('trackProductRecommendation',{
			productData1: section1Product,
			productData2: section2Product,
			productData3: section3Product,
			label:label
		});
	},
	//Google tracking for asahiProductRecommendationAddToCart event
	trackAsahiProductRecommendationAddToCart: function(productDataCode,productQty,label)
	{
		window.mediator.publish('trackProductRecommendationAddToCart',{
			productCode: productDataCode,
			quantityAdded: productQty,
			label:label
		});
	},
	//Google tracking for asahiCheckoutError event
	trackCheckoutError: function(errorMsg,label)
	{
		window.mediator.publish('trackAsahiCheckoutError',{
			errorMsg: errorMsg,
			label:label
		});
	}

};