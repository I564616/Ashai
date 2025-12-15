<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<!--  Google tag manager configuration start -->
<!-- Google tag (gtag.js) -->
<script async src="https://www.googletagmanager.com/gtag/js?id=${googleTagManagerId}"></script>

<script>
   (function(w, d, s, l, i) {
       w[l] = w[l] || [];
       w[l].push({
           'gtm.start': new Date().getTime(),
           event: 'gtm.js'
       });
       var f = d.getElementsByTagName(s)[0],
           j = d.createElement(s),
           dl = l != 'dataLayer' ? '&l=' + l : '';
       j.async = true;
       j.src = 'https://www.googletagmanager.com/gtm.js?id=' + i + dl;
       f.parentNode.insertBefore(j, f);
   })(window, document, 'script', 'dataLayer', '${googleTagManagerId}');
</script>

<c:if test="${not empty googleTagManagerId}">
	<noscript>
		<iframe
			src="https://www.googletagmanager.com/ns.html?id=${googleTagManagerId}"
			height="0" width="0" style="display: none; visibility: hidden"></iframe>
	</noscript>
</c:if>
<c:choose>
    <c:when test="${pageType == 'PRODUCT'}">
    <script type="text/javascript">	
        <!-- Pushing product page view event to the dataLayer -->
            window.dataLayer = window.dataLayer || [];
            window.dataLayer.push({ ecommerce: null });  // Clear the previous ecommerce object.
            window.dataLayer.push({
            pageType:"Product Detail",
            event: "view_item",
            ecommerce: {
	            currency:"${product.price.currencyIso}",
	            items: [{
                        name: "${fn:escapeXml(product.name)}",
                        id: "${product.code}",
                        value: "${product.price.value}",
                        brand: "${fn:escapeXml(product.brand)}",
                        category: "${fn:escapeXml(product.categories[0].name)}",
                        variant: "${product.unit}"
	             }]
	    
	        }
        });
        </script>
	</c:when>
    <c:when test="${pageType == 'CATEGORY'}">
        <!-- Pushing category or product search event to the dataLayer -->
        <c:choose>
            <c:when test="${searchPageData.pagination.totalNumberOfResults > 0}">
                <script type="text/javascript">	
                		var products = [];
                		<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
                		products.push({
    	                    item_name			: "${fn:escapeXml(product.name)}",
    	                    item_id				: "${product.code}"
    	                })
	    			    </c:forEach>
                		window.dataLayer = window.dataLayer || [];
                         window.dataLayer.push({ ecommerce: null });  // Clear the previous ecommerce object.
                         window.dataLayer.push({
                         	event: "view_item_list",
                      	  	ecommerce: {
	                      	  	currency:"${product.price.currencyIso}",
	                      	  	item_list_id: "related_products",
	                       	    item_list_name: "Related products",
	                       	 	searchQuery: "${ycommerce:encodeJavaScript(searchPageData.freeTextSearch)}",
	                         	searchResults: "${searchPageData.pagination.totalNumberOfResults}",
	                         	pageType: "${pageType}",
	                       	   	items: products
                            
                         }});
                       </script>
            </c:when>
        </c:choose>
    </c:when>
	<c:when test="${pageType == 'PRODUCTSEARCH'}">
	<!-- Pushing search page view event to the dataLayer -->
		<c:choose>
            <c:when test="${searchPageData.pagination.totalNumberOfResults > 0}">
                <script type="text/javascript">	
                		var products = [];
                		<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
                		products.push({
    	                    item_name			: "${fn:escapeXml(product.name)}",
    	                    item_id				: "${product.code}"
    	                })
	    			    </c:forEach>
                		window.dataLayer = window.dataLayer || [];
                         window.dataLayer.push({ ecommerce: null });  // Clear the previous ecommerce object.
                         window.dataLayer.push({
                         	event: "view_item_list",
                      	  	ecommerce: {
	                      	  	currency:"${product.price.currencyIso}",
	                      	  	item_list_id: "related_products",
	                       	    item_list_name: "Related products",
	                       	 	searchQuery: "${ycommerce:encodeJavaScript(searchPageData.freeTextSearch)}",
	                         	searchResults: "${searchPageData.pagination.totalNumberOfResults}",
	                         	pageType: "${pageType}",
	                       	   	items: products
                            
                      	  }});
                       </script>
            </c:when>
        </c:choose>
	</c:when>
	<c:when test="${pageType == 'HOME'}">
	<!-- Pushing home page view event to the dataLayer -->
			<script type="text/javascript">	
			window.dataLayer = window.dataLayer || [];
			window.dataLayer.push({ ecommerce: null });  // Clear the previous ecommerce object.
			window.dataLayer.push({
				event: "homePageview",
			    pageType: 'HOME'	
			
                ecommerce: {
                   currency: "${currency}",
                   impressions: [
                       <c:forEach items="${homePageProducts}" var="product" varStatus="status">
                           <c:if test="${status.index > 0}">, </c:if>
                           {
                               'name': "${fn:escapeXml(product.name)}",
                               'id': "${product.code}",
                               'price': "${product.price.value}",
                               'brand': "${fn:escapeXml(product.brand)}",
                               'category': "${fn:escapeXml(product.categories[0].name)}",
                               'variant': "${product.unit}",
                               'list': 'Home Page',
                               'position': ${status.index + 1}
                           }
                       </c:forEach>
                   ]
				}
		    
			});
			</script>
		
	</c:when>
	<c:when test="${pageType == 'ORDERCONFIRMATION'}">
	<!-- Pushing home page view event to the dataLayer -->
		<script type="text/javascript">	
			window.dataLayer = window.dataLayer || [];
			window.dataLayer.push({ ecommerce: null });  // Clear the previous ecommerce object.
			console.log("${orderData.entries}");
		    products = [];
		    isVisited = sessionStorage.getItem("${orderData.code}");
		    console.log(isVisited);
		    
	        <c:forEach items="${orderData.entries}" var="orderEntry" varStatus="status">
	                products.push({
	                    item_name			: "${fn:escapeXml(orderEntry.product.name)}",
	                    item_id			: "${orderEntry.product.code}",
	                    price			: "${orderEntry.basePrice.value}",
	                    brand			: "${fn:escapeXml(orderEntry.product.brand)}",
	                    category		: "${fn:escapeXml(orderEntry.product.categories[0].name)}",
	                    variant		: "${orderEntry.baseUnit.name}",
	                    position		: "${status.count}",
	                    quantity		: "${orderEntry.quantity}"
	                })
	         </c:forEach>
              window.dataLayer.push({
		             event: "purchase",
		             ecommerce: {
		            	 transaction_id: "${orderData.code}",
		            	 value: "${orderData.totalPrice.value}",
		                   currency: "${orderData.totalPrice.currencyIso}",
		                   items: products
		             }
		         });
			</script>
	</c:when>
	<c:when test="${pageType == 'CHECKOUT'}">
			<!-- Pushing checkout page view event to the dataLayer -->
			<script type="text/javascript">	
            window.dataLayer = window.dataLayer || [];
            window.dataLayer.push({ ecommerce: null });  // Clear the previous ecommerce object.
            window.dataLayer.push({
                event: "begin_checkout",
                pageType: "CHECKOUT",
                ecommerce: {
                    currency: "${currency}",
                    checkout: {
                        actionField: { step: 1 }
                    }
                }
            });
        	            </script>
            </c:when>

		<c:when test="${pageType == 'PAGE_NOT_FOUND' || pageType ==''}">
			<!-- Pushing 404 page view event to the dataLayer -->
			<script type="text/javascript">	
    		window.dataLayer = window.dataLayer || [];
            window.dataLayer.push({ ecommerce: null });  // Clear the previous ecommerce object.
            window.dataLayer.push({
             		event: "404Page"",
             		eventCategory: "error",
             		eventAction:"404 Page",
             		eventLabel: "${pageUrl}"
             	});
            	</script>
    </c:when>
    <c:when test="${pageType == 'CART' }">
    		<script type="text/javascript">	
			<!-- Pushing 404 page view event to the dataLayer -->
			window.dataLayer = window.dataLayer || [];
			window.dataLayer.push({ ecommerce: null });  // Clear the previous ecommerce object.
			console.log("${cartData.entries}");
		    products = [];
		    isVisited = sessionStorage.getItem("${cartData.code}");
		    console.log(isVisited);
		    var cartPrice = "${cartData.totalPrice.value}";
		    <c:forEach items="${cartData.entries}" var="orderEntry" varStatus="status">
		        // Initialize a JavaScript variable for price
		        var price = "${orderEntry.basePrice.value}";
	
		        <c:if test="${orderEntry.product.isPromotionActive && orderEntry.basePrice != null && orderEntry.basePrice.formattedValue != '$0.00' && orderEntry.basePrice.formattedValue ne orderEntry.discountPrice.formattedValue}">
		            // If there's an active promotion and the conditions are met, use the discount price if not null
		            <c:if test="${orderEntry.discountPrice != null}">
		                price = "${orderEntry.discountPrice.value}";
		            </c:if>
		        </c:if>
	
		        products.push({
		            item_name: "${fn:escapeXml(orderEntry.product.name)}",
		            item_id: "${orderEntry.product.code}",
		            price: price, // Use the price variable, which is conditionally set
		            brand: "${fn:escapeXml(orderEntry.product.brand)}",
		            category: "${fn:escapeXml(orderEntry.product.categories[0].name)}",
		            variant: "${orderEntry.baseUnit.name}",
		            position: "${status.count}",
		            quantity: "${orderEntry.quantity}"
		        });
		    </c:forEach>
		    <c:choose>
	            <c:when test="${not empty cartData.totalPriceWithTax}">
	            	cartPrice = "${cartData.totalPriceWithTax.value}";
	            </c:when>
	            <c:otherwise>
	            	cartPrice = "${cartData.totalPrice.value}";
	            </c:otherwise>
	        </c:choose>
	        
	        var tax = "0";
	        
	         <c:if test="${cmsSite.uid eq 'sga'}">
	            tax ="${cartData.portalGST.value}";
	         </c:if>
	         
	        var shipping = "0";
	        <c:if test="${not empty cartData.portalFreight}">
                shipping = "${cartData.portalFreight.value}";
            </c:if>
	         
            window.dataLayer.push({
		             event: "view_cart",
		             tax: tax,
		             shipping: shipping,
		             ecommerce: {
		            	 transaction_id: "${cartData.code}",
		            	 value: cartPrice,
	                   		currency: "${cartData.totalPrice.currencyIso}",
		                   items: products
		             }
		         });
            
            
           
            document.addEventListener('DOMContentLoaded', function() {
                // Use document.getElementById to get the button element.
                var checkoutButton = document.getElementById('checkoutButton');
                
                // Add an event listener for the 'click' event.
                checkoutButton.addEventListener('click', function() {
                	window.dataLayer.push({
      		             event: "begin_checkout",
      		          		ecommerce: {
   		            	 transaction_id: "${cartData.code}",
   		            	 value: cartPrice,
   	                   		currency: "${cartData.totalPrice.currencyIso}",
   		                   items: products
   		             }
      		         });
                });
            });

        	
        	
        	
			</script>
    </c:when>
	<c:otherwise>
        <!-- Fallback or default pageview tracking -->
        <script type="text/javascript">	
            window.dataLayer = window.dataLayer || [];
            window.dataLayer.push({ ecommerce: null });  // Clear the previous ecommerce object.
            window.dataLayer.push({
                event: "genericPageview",
                pageType: "${pageType}"
            });
        	</script>
	</c:otherwise>
 </c:choose>

 
  
<script type="text/javascript">	

	function trackAddToCart_google(productCode, quantityAdded) {
	    console.log("add to card first");
	    window.dataLayer.push({
	        ecommerce: null
	    }); // Clear the previous ecommerce object.
	    window.dataLayer.push({
	        event: "add_to_cart",
	        ecommerce: {
	            currency: "AUD",
	            items: [{
	                item_id: productCode,
	                quantity: quantityAdded
	            }]
	        }
	    });
	}
	
	function trackUpdateCart(productCode, initialQuantity, newQuantity) {
	    console.log("add and remove first");
	    if (initialQuantity != newQuantity) {
	        var action = initialQuantity > newQuantity ? 'remove_from_cart' : 'add_to_cart';
	        var quantityChange = Math.abs(newQuantity - initialQuantity);
	        window.dataLayer.push({
	            ecommerce: null
	        }); // Clear the previous ecommerce object.
	        window.dataLayer.push({
	            event: action,
	            ecommerce: {
	                currency: "AUD",
	                items: [{
	                    item_id: productCode,
	                    quantity: quantityChange
	                }]
	            }
	        });
	    }
	}
	
	// Tracks product removals from the cart
	function trackRemoveFromCart(productCode, initialQuantity) {
	    console.log("removeFromCart first");
	    window.dataLayer.push({
	        ecommerce: null
	    }); // Clear the previous ecommerce object.
	    window.dataLayer.push({
	        event: "remove_from_cart",
	        ecommerce: {
	            currency: "AUD",
	            items: [{
	                item_id: productCode,
	                quantity: initialQuantity
	            }]
	        }
	    });
	}
	
	// Subscribes to add to cart events and tracks them
	window.mediator.subscribe('trackAddToCart', function(data) {
	    console.log("trackAddToCart first");
	    if (data.productCode && data.quantity) {
	        window.dataLayer.push({
	            ecommerce: null
	        }); // Clear the previous ecommerce object.
	        window.dataLayer.push({
	            event: "add_to_cart",
	            ecommerce: {
	                currency: "AUD",
	                items: [{
	                    item_id: data.productCode,
	                    quantity: data.quantity
	                }]
	            }
	        });
	    }
	});
	
	// Subscribes to cart update events and tracks them
	window.mediator.subscribe('trackUpdateCart', function(data) {
	    console.log("trackUpdateCart");
	    if (data.productCode && data.initialCartQuantity && data.newCartQuantity) {
	        var action = data.initialCartQuantity > data.newCartQuantity ? 'remove_from_cart' : 'add_to_cart';
	        var quantityChange = Math.abs(data.newCartQuantity - data.initialCartQuantity);
	        window.dataLayer.push({
	            ecommerce: null
	        }); // Clear the previous ecommerce object.
	        window.dataLayer.push({
	            event: action,
	            ecommerce: {
	                currency: "AUD",
	                items: [{
	                    item_id: data.productCode,
	                    quantity: quantityChange
	                }]
	            }
	        });
	    }
	});
	
	// Subscribes to remove from cart events and tracks them
	window.mediator.subscribe('trackRemoveFromCart', function(data) {
	    console.log("trackRemoveFromCart");
	    if (data.productCode && data.initialCartQuantity) {
	        trackRemoveFromCart(data.productCode, data.initialCartQuantity);
	    }
	});
	
	// Tracks product additions to the cart with specific labels
	window.mediator.subscribe('trackAddProductToCart', function(data) {
	    console.log("trackAddProductToCart");
	    window.dataLayer.push({
	        ecommerce: null
	    }); // Clear the previous ecommerce object.
	    window.dataLayer.push({
	        event: "add_to_cart",
	        ecommerce: {
	            currency: "AUD",
	            items: [{
	                item_id: data.productCode,
	                quantity: data.quantity
	            }]
	        }
	    });
	});
	
	// Tracks product clicks
	window.mediator.subscribe('trackProductClick', function(data) {
	    console.log("select_item first");
	    window.dataLayer.push({
	        event: "select_item",
	        label: data.label,
	        productCode: data.productCode
	    });
	});
	
	// Tracks product recommendations
	window.mediator.subscribe('trackProductRecommendation', function(data) {
	    console.log("trackProductRecommendation");
	    ['productData1', 'productData2', 'productData3'].forEach((key, index) => {
	        if (data[key] != '') {
	            window.dataLayer.push({
	                event: 'productRecommendation',
	                label: data.label,
	                section: `asahiSection${index + 1}Recommendations`,
	                productData: data[key]
	            });
	        }
	    });
	});
	
	// Tracks adding recommended products to the cart
	window.mediator.subscribe('trackProductRecommendationAddToCart', function(data) {
	    console.log("trackProductRecommendationAddToCart");
	    if (data.productCode) {
	        window.dataLayer.push({
	            'event': 'recommendationAddToCart',
	            'label': data.label,
	            'productCode': data.productCode
	        });
	    }
	});
	
	// Tracks checkout errors
	window.mediator.subscribe('trackAsahiCheckoutError', function(data) {
	    console.log("trackAsahiCheckoutError");
	    if (data.errorMsg) {
	        window.dataLayer.push({
	            event: 'checkoutError',
	            label: data.label,
	            errorMsg: data.errorMsg
	        });
	    }
	});


</script>  <!--  Google tag manager configuration end -->