<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<script id="customerData" type="text/json">${ycommerce:generateJson(user)}</script>

<script>

if (typeof $('#customerData').html() !== 'undefined') {
	let custData = JSON.parse($('#customerData').html());
}


var isAuthorized = false;
<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
	isAuthorized = true;
</sec:authorize>

<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')">
	rm.utilities.addItemToStorage('isLoggedIn', 'FALSE');
</sec:authorize>

let searchParams = new URLSearchParams(window.location.search);
let urlHostOriginName = window.location.origin;
let siteUrl = urlHostOriginName + ACC.config.encodedContextPath;
let cartId = '<c:choose><c:when test="${not empty cartData.code}">${cartData.code}</c:when><c:otherwise>${sessionScope.cartCode}</c:otherwise></c:choose>';

rm.segment = {
	
	init: function() {
		
		setTimeout(function(){
			rm.segment.addProductImpressionListener();
		},100);
		
		//track login successful
		if ( isAuthorized && rm.utilities.getItemFromStorage('isLoggedIn') === 'FALSE') {
			rm.utilities.addItemToStorage('isLoggedIn', 'TRUE');
			this.trackIdentity();
			this.trackLogin('success');
		}
		
		//track login failed
		if ( !isAuthorized && searchParams.has('error') ) {
			
			if ( searchParams.get('error') === 'true' ) {
				
				this.trackLogin('failed');
			}
		}


		//track cart viewed
		<c:if test="${pageType == 'CART'}">
			<c:if test="${not empty cartData.entries}">
				if ( $('.cart').length > 0 ) {
                   rm.segment.trackCartViewed();
				}
			</c:if>
		</c:if>
		
		//track order submitted
		$('.doCheckoutBut.processButton').on('click', function(ev){
			ev.preventDefault();
			
			if ( $('#checkoutOrderDetails .cart').length > 0 ) {
				rm.segment.trackOrderSubmitted();
			}
		});


		<c:if test="${pageType == 'ORDERCONFIRMATION'}">

            var sessCheckoutProducts = sessionStorage.getItem('checkoutProducts');
            analytics.track('Order Submitted', {
                affiliation: 'Online Store',
                discount: '${cartData.totalDiscounts.formattedValue}',
                order_id: '${orderData.sapSalesOrderNumber}',
                cart_id: '${cartData.code}',
                currency: 'AUD',
                revenue: '${cartData.subTotal.value}',
                total: '${cartData.totalPrice.value}',
                shipping: '${cartData.deliveryCost.value}',
                tax: '${cartData.gst.value}',
                delivery_date: $('.delivery-header-input').data('selected-date'),
                products: JSON.parse(sessCheckoutProducts)
            });

        </c:if>
		
		//track product added to cart
		<c:if test="${pageType != 'RECOMMENDATION'}">

			$('.add_to_cart_form button.btn').live('click', function() {
				
				if ($(this).closest('.cart-recommendations').length === 0) {
					rm.segment.trackProductAdded(this);
				}
			});
			
		</c:if>
		
		//track recommendation added from recommendation and cart page
		<c:if test="${pageType == 'RECOMMENDATION' || pageType == 'CART'}">
			$('.recommendation-addToOrder').on('click', function() {
				rm.segment.trackRecommendationAdded(this);
			});
		</c:if>
		
		//track reorder product in homepage
		<c:if test="${pageType != 'RECOMMENDATION'}">
			$('.btn-reorder-product').on('click', function(){
				setTimeout(function(){
					
					if ( $('.js-track-order-addtocartpopup').length > 0 ) {
						rm.segment.trackReOrderProductAdded('.js-track-order-addtocartpopup');
					}
					
				}, 300);
			});
		</c:if>
		
		//track search box clicked
		$('#input_SearchBox').on('click', function(){
			rm.segment.trackSearchBox();
		});
		
		
		//track product removed from cart
		$('span.inline.submitRemoveProduct').live('click', function() { 
			
			rm.segment.trackProductRemoved(this);
			
		});
	
		
		//track product quantity edited
		$('.popupCartItem .select-quantity .up, .popupCartItem .select-quantity .down,' +
		  '.cartRow .select-quantity .up, .cartRow .select-quantity .down').live('click touchstart', function(){
			  	
			  	var that = this;
				setTimeout(function(){
				  rm.segment.trackProductQuantityEditedFromCart(that);
				}, 100);
		});
		
		
		//track brand list viewed
		var $brands = [];
		
		if ( $('.brand-grid-item .rotatingBannerTag').length > 0 ) {	
			$('.brand-grid-item .rotatingBannerTag').map(function(item) {
				if ( $(this).length > 0 ) {
											
					var position = item + 1;
					
					$brands.push({
						name: $(this).data('name'),
						position: position,
						category: $(this).data('category')
					});
				}
			});
			
			if ( $brands.length > 0 ) {
				rm.segment.trackBrandListViewed($brands);
			}
		}
		
		$('.js-track-deals-addtocart').live('click', function(){
			rm.segment.trackDealsProductAdded(this);
		});
		
		
		<c:if test="${pageType == 'PRODUCT'}">

		// Track - Product details page view
		analytics.track('Product Viewed', {
		  brand: '${product.brand}',
		  category: '${fn:escapeXml(product.categories[fn:length(product.categories) - 1].name)}',
		  coupon: '${product.dealsFlag}',
		  currency: '${product.price.currencyIso}',
		  hybrisSessionId: '${cookie['JSESSIONID'].value}',
		  image_url: '${sortedGallery[0].zoom.url}',
		  name: '${product.name}',
		  position: '${productListPosition}',
		  price: '${product.price.value}',
		  product_id: '${product.code}',
		  sku: '${product.leadSkuId}',
		  url: '${product.url}',
		  variant: '<c:choose><c:when test="${empty product.uomList}">${product.unit}</c:when><c:otherwise>${product.uomList[0].name}</c:otherwise></c:choose>'
		});

		</c:if>
		
		<%-- Commented the Segment 1.0
		//************************************* START: Segment Tracking 1.0 **********************************
		<c:if test="${pageType == 'REGISTER'}">

		// Track - Customer Registration
		    var suBtn = $('#command').find('button.btn-primary');
		    suBtn.on('click', function() {
		      if ($('#command').hasClass('ng-valid')) {
		        analytics.track('Registration Submitted', {
		          firstName: $('#rr-firstName').val(),
		          lastName: $('#rr-lastName').val(),
		          email: $('#rr-email').val(),
		          accountName: $('#rr-accountName').val(),
		          CUBAccountNumber: $('#rr-cubAccount').val(),
		          workPhone: $('#rr-workPhoneNum').val(),
		          mobilePhone: $('#rr-mobilePhoneNum').val(),
		          businessOwner: $('#rr-accoutType1').prop('checked'),
		          areaManager: $('#rr-accoutType2').prop('checked'),
		          venueOutlet: $('#rr-accoutType3').prop('checked'),
		          staffMember: $('#rr-accoutType4').prop('checked'),
		          ordering: $('#rr-accessType1').prop('checked'),
		          viewPayInvoices: $('#rr-accessType2').prop('checked'),
		          manageSetupUsers: $('#rr-accessType3').prop('checked')
		        });
		      }
		    });

		</c:if>

		<c:if test="${pageType != 'RECOMMENDATION'}">
			
			// Track - Product is clicked
			$('.js-track-product-link').on('click', function(e) {
			  e.preventDefault();
			  var parent = $(this).closest('.product-pick, .list-item');
			  var closestImgUrl = parent.find('.list-item-img > img').attr('src');
			  var closestQty = parent.find('.qty-input').val();
			  var closestVrnt = parent.find('.select-btn, .select-single').text();
			  if ($(this).closest('.cart-recommendations').length = '0') {
			    analytics.track('Product Clicked', {
			      brand: $(this).data("brand"),
			      category: $(this).data("category"),
			      coupon: $(this).data("dealsflag"),
			      currency: $(this).data("currencycode"),
			      hybrisSessionId: '${cookie['JSESSIONID'].value}',
			      image_url: closestImgUrl,
			      name: $(this).data("name"),
			      position: $(this).data("position"),
			      price: $(this).data("price"),
			      product_id: $(this).data("id"),
			      quantity: closestQty + ' ' + closestVrnt,
			      sku: $(this).data("sku"),
			      url: $(this).data("url"),
			      variant: $(this).data("variant")
			    });
			  }
			  window.location = $(this).attr("href");
			});
			
		</c:if>

		<c:if test="${pageType == 'RECOMMENDATION' || pageType == 'CART'}">
			
			// Track - Recommendation is deleted
			$('.deleteRecommendation').on('click', function() {
			  var parent = $(this).closest('.product-row, .product-pick');
			  var closestLink = parent.find('.js-track-product-link');
			  var closestImgUrl = parent.find('.list-item-img > img').attr('src');
			  var closestQty = parent.find('.qty-input').val();
			  var closestVrnt = parent.find('.select-btn, .select-single').text();
			  analytics.track('<c:if test="${pageType == 'CART'}">Cart Page - </c:if>Recommendation Deleted', {
			    brand: closestLink.data("brand"),
			    category: closestLink.data("category"),
			    coupon: closestLink.data("dealsflag"),
			    currency: closestLink.data("currencycode"),
			    hybrisSessionId: '${cookie['JSESSIONID'].value}',
			    image_url: closestImgUrl,
			    isRecommended: closestLink.data("wasonviewport"),
			    name: closestLink.data("name"),
			    position: closestLink.data("position"),
			    price: closestLink.data("price"),
			    product_id: closestLink.data("id"),
			    quantity: <c:if test="${pageType == 'RECOMMENDATION'}">closestQty + ' ' + closestVrnt</c:if><c:if test="${pageType == 'CART'}">closestLink.data("qty") + ' ' + closestLink.data("variant")</c:if>,
			    sku: closestLink.data("sku"),
			    url: closestLink.data("url"),
			    variant: closestLink.data("variant")
			  });
			});
			
			// Track - Recommendation  is clicked
			$('.js-track-product-link').on('click', function(e) {
			  e.preventDefault();
			  var parent = $(this).closest('.product-row, .product-pick');
			  var closestLink = parent.find('.js-track-product-link');
			  var closestImgUrl = $(this).find('.list-item-img > img').attr('src');
			  var closestQty = $(this).closest('.product-row').find('.qty-input').val();
			  var closestVrnt = parent.find('.select-btn, .select-single').text();
			  if ($(this).closest('.cart-recommendations, .deal-items').length > '0') {
			    analytics.track('<c:if test="${pageType == 'CART'}">Cart Page - </c:if>Recommendation Clicked', {
			      brand: $(this).data("brand"),
			      category: $(this).data("category"),
			      coupon: $(this).data("dealsflag"),
			      currency: $(this).data("currencycode"),
			      hybrisSessionId: '${cookie['JSESSIONID'].value}',
			      image_url: closestImgUrl,
			      isRecommended: $(this).data("wasonviewport"),
			      name: $(this).data("name"),
			      position: $(this).data("position"),
			      price: $(this).data("price"),
			      product_id: $(this).data("id"),
			      quantity: <c:if test="${pageType == 'RECOMMENDATION'}">closestQty + ' ' + closestVrnt</c:if><c:if test="${pageType == 'CART'}">closestLink.data("qty") + ' ' + closestLink.data("variant")</c:if>,
			      sku: $(this).data("sku"),
			      url: $(this).data("url"),
			      variant: $(this).data("variant")
			    });
			  }
			  window.location = $(this).attr("href");
			});

		</c:if>

		//************************************* END: Segment Tracking 1.0 **********************************
		 --%>
	},

	trackProductImpressionAndPosition: function($event, $elem){
        var trackImgUrl = $elem.find('.list-item-img > img').attr('src');
        var trackQty = $elem.closest('.product-row').find('.qty-input').val();

        analytics.track($event, {
            brand: $elem.data("brand"),
            category: $elem.data("category"),
            coupon: $elem.data("dealsflag"),
            currency: $elem.data("currencycode"),
            hybrisSessionId: '${cookie['JSESSIONID'].value}',
            image_url: trackImgUrl,
            name: $elem.data("name"),
            position: $elem.data("position"),
            price: $elem.data("price"),
            product_id: $elem.data("id"),
            quantity: trackQty + ' ' + $elem.data("variant"),
            sku: $elem.data("sku"),
            url: $elem.data("url"),
            packaging: $elem.data("variant")
        });
        
   	},
	
	addProductImpressionListener: function() {
		
		if ( $('.productImpressionTag').length > 0 ) {
			var productsData = [];
			$('.productImpressionTag').each(function(){
				
				//if( $(this).length > 0 && $(this).isOnViewPort() ) {
					
	        		var elementWithProductInfo = $(this).find('.js-track-product-link');
	        		
	        		<c:choose>
	        		<c:when test="${pageType == 'RECOMMENDATION' || pageType == 'DEAL'}">
		    			var qty =$(this).parents('.deal-item-head').find('.qty-input').val();
		    			var closestImgUrl = $(this).find('.deal-img img').attr('src');
	        		</c:when>
	        		<c:otherwise>
	    				var closestImgUrl = $(this).find('.list-item-img > img').attr('src');
		    			var qty =$(this).find('.qty-input').val();
	        		</c:otherwise>
	        		</c:choose>
	        		
		        	if (elementWithProductInfo.length > 0) {
		        		
		        		var wasOnViewPort = $(elementWithProductInfo).attr('data-wasonviewport');
		        		//if (wasOnViewPort === undefined || wasOnViewPort !== 'true') { 
		        			
	        				//$(elementWithProductInfo).attr('data-wasonviewport', 'true');
		        			
	        				productsData.push({
				            	'name'		: elementWithProductInfo.data('name'),
				            	'product_id': elementWithProductInfo.data('id'),
				            	'sku'		: elementWithProductInfo.data('id'),
				            	'price'		: elementWithProductInfo.data('price'),
				            	'brand'		: elementWithProductInfo.data('brand'),
				            	'currency'	: 'AUD',
								'image_url'	: closestImgUrl,
				            	'quantity'	: qty,
				            	'category'	: elementWithProductInfo.data('category'),
				            	'packaging'	: elementWithProductInfo.data('variant'),
				            	'list'		: elementWithProductInfo.data('list'),
				            	'position'	: elementWithProductInfo.data('position'),
				            	'coupon'	: elementWithProductInfo.data('dealsflag')
	        				});
	        				
		        		//}
		        	}
				//}
			});
			
			if ( productsData.length > 0 ) {

					rm.segment.trackProductListViewed(productsData);
			}
		}
		
	},
	
	trackIdentity: function() {
		
		analytics.identify('${user.gaUid}', {
		    name: '${user.name}',
		    firstName: '${user.firstName}',
		    lastName: '${user.lastName}',
		    email: '${user.email}',
		    poc_ID: '${user.currentB2BUnit.uid}', // ERP UUID for POC
		    poc_name: '${user.currentB2BUnit.name}', // Name of the business
		    business_unit: 'Australia',
		    staff: '${user.userRole != "admin" ? true : false }',
		    address: {
		    	street: '${user.unit.address.line1}, ${user.unit.address.line2}',
		    	city: '${user.unit.address.town}',
		    	country: '${user.unit.address.country.name}',
		    	postcode: '${user.unit.address.postalCode}',
		    	state: '${user.unit.address.region.name}'
		    },
            rep_name: '${user.unit.bdeUserName !=null ? user.unit.bdeUserName : ""}'
		    
		});
		
	},
	
	trackLogin: function(status) {
		
		if ( status === 'success' ) {
			
			var recommendationCount = $('.global-header-list .recommendationsCount').text();
			var hasDeal = $('.d-content span');
			
		  	analytics.track('Login Successful', {
			    dealCount: (() => {
			      if (hasDeal.length > 0) {
			        return hasDeal.text();
			      } else {
			        return 0;
			      }
			    })(),
			    recommendationCount: (() => {
			      if (parseInt(recommendationCount) > 0) {
			        return recommendationCount;
			      } else {
			        return 0;
			      }
			    })(),
			    userId: '${user.gaUid}'
			});
		  	
		} else if ( status === 'failed' ) {
			
			analytics.track('Login Failed', {
				reason: 'The email or password you entered is incorrect'
			});
			
		}

	},
	
	trackCartViewed: function () {
		
      var cartProducts = [];
      
      $('.cartRow').each(function() {
			var closestLink = $(this).find('.cart-img .js-track-product-link');
			var closestImgUrl = $(this).find('.list-item-img > img').attr('src');
			var closestQty = $(this).find('.qty-input').val();
			var closestVrnt = $(this).find('.select-btn, .select-single').text();
			
			cartProducts.push({
				category: closestLink.data("category"),
				image_url: closestImgUrl,
				name: closestLink.data("name"),
				price: closestLink.data("price"),
				product_id: closestLink.data("id"),
				quantity: closestQty,
				url: closestLink.data("url"),
				brand: closestLink.data("brand"),
				sku: closestLink.data("sku"),
				position: closestLink.data("position"),
				packaging: closestLink.data("variant"),
				coupon: closestLink.data("coupon"),
				currency: 'AUD'
			});
      });
      
      analytics.track('Cart Viewed', {
        affiliation: 'Online Store',
        discount: '${cartData.totalDiscounts.formattedValue}',
		currency: 'AUD',
        cart_id: '${cartData.code}',
        products: cartProducts,
        revenue: '${cartData.totalPrice.value}',
        shipping: '${cartData.deliveryCost.value}',
        tax: '${cartData.gst.value}'
      });

      console.log('Track Cart Viewed Event Called');
  
	},
	
	trackOrderSubmitted: function(){
	      var checkoutProducts = [];
	      $('.cartRow').each(function() {
	        var closestLink = $(this).find('.cart-img .js-track-product-link');
	        var closestImgUrl = $(this).find('.list-item-img > img').attr('src');
	        var closestQty = $(this).find('.qty-input').val();
	        var closestVrnt = $(this).find('.select-btn, .select-single').text();
	        
	        checkoutProducts.push({
				category: closestLink.data("category"),
				image_url: closestImgUrl,
				name: closestLink.data("name"),
				price: closestLink.data("price"),
				product_id: closestLink.data("id"),
				quantity: closestLink.data("quantity"),
				url: closestLink.data("url"),
				brand: closestLink.data("brand"),
				sku: closestLink.data("sku"),
				position: closestLink.data("position"),
				packaging: closestLink.data("variant"),
				coupon: closestLink.data("coupon"),
		        currency: 'AUD'
	        })
	      });
	      
            sessionStorage.setItem('checkoutProducts', JSON.stringify(checkoutProducts));
	},
	
	trackProductAdded: function(ev){
		var parent = $(ev).closest('.product-pick, .list-item, .product-summary, .product-row, .prod-row');
		var closestImgUrl = parent.find('.list-item-img > img').attr('src');
		var closestLink = parent.find('.js-track-product-link');
		var closestQty = parent.find('.qty-input').val();
		var closestVrnt = parent.find('.select-btn, .select-single').text();
		  
		analytics.track('Product Added', {
			cart_id: cartId,
			brand: <c:choose><c:when test="${pageType == 'PRODUCT'}">'${product.brand}'</c:when><c:otherwise>closestLink.data("brand")</c:otherwise></c:choose>,
			category: <c:choose><c:when test="${pageType == 'PRODUCT'}">'${fn:escapeXml(product.categories[fn:length(product.categories) - 1].name)}'</c:when><c:otherwise>closestLink.data("category")</c:otherwise></c:choose>,
			coupon: <c:choose><c:when test="${pageType == 'PRODUCT'}">'${product.dealsFlag}'</c:when><c:otherwise>closestLink.data("dealsflag")</c:otherwise></c:choose>,
			currency: 'AUD',
			image_url: <c:choose><c:when test="${pageType == 'PRODUCT'}">'${sortedGallery[0].zoom.url}'</c:when><c:otherwise>closestImgUrl</c:otherwise></c:choose>,
			name: <c:choose><c:when test="${pageType == 'PRODUCT'}">'${product.name}'</c:when><c:otherwise>closestLink.data("name")</c:otherwise></c:choose>,
			position: <c:choose><c:when test="${pageType == 'PRODUCT'}">'${productListPosition}'</c:when><c:otherwise>closestLink.data("position")</c:otherwise></c:choose>,
			price: <c:choose><c:when test="${pageType == 'PRODUCT'}">'${product.price.value}'</c:when><c:otherwise>closestLink.data("price")</c:otherwise></c:choose>,
			product_id: <c:choose><c:when test="${pageType == 'PRODUCT'}">'${product.code}'</c:when><c:otherwise>closestLink.data("id").toString()</c:otherwise></c:choose>,
			sku: <c:choose><c:when test="${pageType == 'PRODUCT'}">'${product.code}'</c:when><c:otherwise>closestLink.data("id").toString()</c:otherwise></c:choose>,
			quantity: parseInt(closestQty),
			url: siteUrl + <c:choose><c:when test="${pageType == 'PRODUCT'}">'${product.url}'</c:when><c:otherwise>closestLink.data("url")</c:otherwise></c:choose>,
			packaging: <c:choose><c:when test="${pageType == 'PRODUCT'}">'<c:choose><c:when test="${empty product.uomList}">${product.unit}</c:when><c:otherwise>${product.uomList[0].name}</c:otherwise></c:choose>'</c:when><c:otherwise>closestLink.data("variant")</c:otherwise></c:choose>,
			is_suggested: false,
			is_reorder: false,
			is_promotion: <c:choose><c:when test="${pageType == 'PRODUCT'}">'${product.dealsFlag}'</c:when><c:otherwise>closestLink.data("dealsflag")</c:otherwise></c:choose>
		});
	},
	
	trackDealsProductAdded: function(ev){
		
		var parent = $(ev).closest('.deal-item-head');
		var closestImgUrl = parent.find('.deal-img > img').attr('src');
		var closestLink = parent.find('.js-track-product-link');
		var closestQty = parent.find('.qty-input').val();
		var closestVrnt = parent.find('.select-btn, .select-single').text();
		
		analytics.track('Product Added', {
			cart_id: cartId,
			category: closestLink.data("category"),
			currency: 'AUD',
			name: closestLink.data("name"),
			price: closestLink.data("price"),
			product_id: (closestLink.data("id") && closestLink.data("id") !== null) ? closestLink.data("id").toString() : '',
            image_url: closestImgUrl,
			quantity: parseInt(closestQty),
			url: siteUrl + closestLink.data("url"),
			brand: closestLink.data("name"),
			sku: (closestLink.data("id") && closestLink.data("id") !== null) ? closestLink.data("id").toString() : '',
			position: closestLink.data("position"),
			packaging: closestLink.data("variant"),
			coupon: closestLink.data("dealsflag"),
			is_promotion: true,
			is_suggested: false,
			is_reorder: false
		});
	},
	
	trackReOrderProductAdded: function(className) {
		
		analytics.track('Product Added', {
			cart_id: cartId,
			category: $(className).data("category"),
			currency: 'AUD',
			name: $(className).data("name"),
			price: $(className).data("price"),
			product_id: $(className).data("id").toString(),
			quantity: parseInt($(className).data("quantity")),
			url: siteUrl + $(className).data("url"),
			brand: $(className).data("name"),
			sku: $(className).data("sku").toString(),
			position: $(className).data("position"),
			packaging: $(className).data("variant"),
			coupon: $(className).data("dealsflag"),
			is_promotion: $(className).data("ispromotion"),
			is_suggested: $(className).data("issuggested"),
			is_reorder: $(className).data("isreorder")
		});
		
	},
	
	trackProductListViewed: function(products) {
	
		analytics.track('Product List Viewed', {
			category: '${fn:escapeXml(categoryData.name) == null ? fn:escapeXml(pageType) : fn:escapeXml(categoryData.name)}',
			list_id: products[0].list,
			products: products
		});
		
	},
	
	trackSearchBox: function() {
		analytics.track('Search Box Started', {});
	},
	
	trackProductRemoved: function(ev) {
	    var parent = $(ev).closest('.cartRow, .popupCartItem');
	    var closestLink = parent.find('.js-track-product-link');
	    var closestImgUrl = parent.find('.list-item-img > img').attr('src');
	    var closestQty = parent.find('.qty-input').val();
	    var dealIndex = parent.find('.deal-index');
	    var closestPrice = closestLink.data("price");

	    analytics.track('Product Removed', {
	      cart_id: cartId,
	      product_id: closestLink.data("id"),
	      sku: closestLink.data("sku"),
	      category: closestLink.data("category"),
	      name: closestLink.data("name"),
	      brand: closestLink.data("brand"),
	      price: parseInt(closestPrice),
	      quantity: parseInt(closestQty),
	      coupon: closestLink.data("coupon"),
	      packaging: closestLink.data("variant"),
	      position: closestLink.data("position"),
	      url: siteUrl + closestLink.data("url"),
	      image_url: closestImgUrl,
	      currency: closestLink.data("currencycode"),
	      hybrisSessionId: '${cookie['JSESSIONID'].value}',
	      //isRecommended: closestLink.data("wasonviewport"),
	      is_reorder: false,
	      is_promotion: ( dealIndex.length > 0 ) ? true : false,
	      is_suggested: false
	   });
	},
	
	trackProductQuantityEditedFromCart: function(ev) {

	    var parent = $(ev).closest('.cartRow, .popupCartItem');
	    var closestLink = parent.find('.js-track-product-link');
	    var closestImgUrl = parent.find('.list-item-img > img').attr('src');
	    var closestQty = parent.find('.qty-input').val();
	    analytics.track('Quantity Edited', {
	      cart_id: cartId,
	      product_id: closestLink.data("id"),
	      sku: closestLink.data("sku"),
	      category: closestLink.data("category"),
	      name: closestLink.data("name"),
	      brand: closestLink.data("brand"),
	      price: closestLink.data("price"),
	      quantity: parseInt(closestQty),
	      coupon: closestLink.data("coupon"),
	      packaging: closestLink.data("variant"),
	      position: closestLink.data("position"),
	      url: siteUrl + closestLink.data("url"),
	      image_url: closestImgUrl,
	      currency: closestLink.data("currencycode"),
	      hybrisSessionId: '${cookie['JSESSIONID'].value}',
	      isRecommended: closestLink.data("wasonviewport"),
	      is_reorder: false,
	      is_promotion: closestLink.data("coupon"),
	      is_suggested: false
	   });
	},
	
	trackRecommendationAdded: function(ev) {
		<c:if test="${pageType == 'RECOMMENDATION' || pageType == 'CART'}">

		  var parent = $(ev).closest('.product-row, .product-pick');
		  var closestLink = parent.find('.js-track-product-link');
		  var closestImgUrl = parent.find('.list-item-img > img').attr('src');
		  var closestQty = parent.find('.qty-input').val();
		  var closestVrnt = parent.find('.select-btn, .select-single').text();
		  
		  analytics.track('Product Added', {
		    brand: closestLink.data("brand"),
		    category: closestLink.data("category"),
		    coupon: closestLink.data("dealsflag"),
		    currency: closestLink.data("currencycode"),
		    image_url: closestImgUrl,
		    name: closestLink.data("name"),
		    position: closestLink.data("position"),
		    price: closestLink.data("price"),
		    product_id: closestLink.data("id").toString(),
		    quantity: <c:if test="${pageType == 'RECOMMENDATION'}">parseInt(closestQty)</c:if><c:if test="${pageType == 'CART'}">parseInt(closestLink.data("qty"))</c:if> ,
		    sku: closestLink.data("id").toString(),
		    url: closestLink.data("url"),
		    packaging: closestLink.data("variant"),
			is_reorder: false,
			is_promotion: closestLink.data("dealsflag"),
			is_suggested: true
		  });
		</c:if>
	},
	
	trackBrandListViewed: function(brands) {
		
		analytics.track('Brand List Viewed', {
			list_id: '',
			category: '',
			brands: brands
		});
		
	}
}

rm.segment.init();

</script>
<%--
PAGE TYPE: ${pageType} --
CART STEP: ${checkoutStep}
CATEGORY: ${fn:escapeXml(product.categories[fn:length(product.categories) - 1].name)}
IMG URL: ${sortedGallery[0].zoom.url}--%>
