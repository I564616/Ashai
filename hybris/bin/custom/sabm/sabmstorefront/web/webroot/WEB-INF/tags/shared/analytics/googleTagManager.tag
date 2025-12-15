<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:if test="${not empty googleTagManagerId}">
<script type="text/javascript">
try {
	
	
	function getOnDeal(dealFlag) {
		if (dealFlag === 'false') {
			return 'No Deal';
		} else {
			return 'Deal';
		}
	}

	
	function trackCheckoutComplete(orderData){
	    console.log("${orderData.entries}");
	    products = [];
	    isVisited = sessionStorage.getItem("${orderData.code}");
	    console.log(isVisited);
	    if(isVisited != "true"){
	        <c:forEach items="${orderData.entries}" var="orderEntry" varStatus="status">
	                products.push({
	                    'name'			: "${fn:escapeXml(orderEntry.product.name)}",
	                    'id'			: "${orderEntry.product.code}",
	                    'price'			: "${orderEntry.basePrice.value}",
	                    'brand'			: "${fn:escapeXml(orderEntry.product.brand)}",
	                    'category'		: "${fn:escapeXml(orderEntry.product.categories[0].name)}",
	                    'variant'		: "${orderEntry.baseUnit.name}",
	                    'position'		: "${status.count}",
	                    'dimension13'	: getOnDeal(String("${orderEntry.product.dealsFlag}")),
	                    'quantity'		: "${orderEntry.baseQuantity}"
	                })
	         </c:forEach>
	         dataLayer.push({
	             'event':'purchase',
	             'ecommerce': {
	                   'currencyCode':"${orderData.totalPrice.currencyIso}",
	                   'purchase': {
	                       'actionField':{
	                              'id':"${orderData.sapSalesOrderNumber}",
	                              'affiliation': 'Online Store',
	                              'revenue': "${orderData.totalPrice.value}",
	                              'tax': "${orderData.gst.value}",
	                              'shipping':"${orderData.actualDeliveryCost.value}"
	                       },
	                       'products':products
	                   }
	             }
	         });

	         sessionStorage.setItem("${orderData.code}", "true");
	    }
	}


	function capitalizeFirstLetterAndLowerTheRest(theString) {
	    return theString.charAt(0).toUpperCase() + theString.slice(1).toLowerCase();
	}
	
	function getFormattedPageType(thePageType) {
		
		var arrThePageType = thePageType.split('_');
		var theFormattedPageType = '';
		
		arrThePageType.forEach(function(item) {
			theFormattedPageType += capitalizeFirstLetterAndLowerTheRest(item) + ' ';
		});
		
		return theFormattedPageType.substring(0, theFormattedPageType.length - 1);
		
	}

	
/* Google Tag Manager */

<!-- dataLayer object -->

var googleTagManagerId = '${googleTagManagerId}'
dataLayer = [];
var pageType;

<c:choose>
	<c:when test="${pageType == 'PRODUCT'}">
		pageType = 'Product Detail';
	</c:when>

	<c:when test="${pageType == 'PRODUCTSEARCH'}">
		pageType = 'Product Search';
	</c:when>

	<c:when test="${pageType == 'ORDERCONFIRMATION'}">
		pageType = 'Order Confirmation';
	</c:when>

	<c:otherwise> 
		<c:choose>
			<c:when test="${not empty pageType}">
				pageType = getFormattedPageType('${pageType}');
			</c:when>
			
			<c:otherwise>
				pageType = 'Page Not Defined';
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>

<sec:authorize access="hasRole('ROLE_ANONYMOUS')">
	dataLayer = [{
		'pageType' : pageType
	}];
</sec:authorize>

<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">

	dataLayer = [{
		'pageType' : pageType,
		'userId' : '${user.gaUid}',
		'customerId' :'${user.currentB2BUnit.uid}',
		'userSegment' : '${user.currentB2BUnit.subChannelDescription}',
		'userGroup' : '${user.currentB2BUnit.primaryGroupDescription}',
		'userGeo' : '${user.currentB2BUnit.postCode}',
		'staff' : '${impersonate || bdeUser ? 'Yes': 'No'}'
	}];
	
</sec:authorize>

<c:choose>
	<c:when test="${pageType == 'DEAL'}">
		var requestOrigin = 'Home/Deals';
	</c:when>
	<c:otherwise>
		var requestOrigin = '${fn:escapeXml(requestOrigin)}';
	</c:otherwise>
</c:choose>


console.log("pageType=${pageType}");
console.log("requestOrigin=" + requestOrigin);
console.log("searchPageData results length=${fn:length(searchPageData.results)}");
console.log("productData length=${fn:length(productData)}");
console.log("cartData entries length=${fn:length(cartData.entries)}");
console.log("orderData consignments length=${fn:length(orderData.consignments)}");
console.log("orderTemplate entries length=${fn:length(orderTemplate.entries)}");

console.log("checkoutStep=${checkoutStep}");
console.log("banners=${banners}");

var cartDataEntries = [];
<c:if test="${not empty cartData.entries}">
	<c:forEach items="${cartData.entries}" var="entry" varStatus="status">
		cartDataEntries.push({
			'name' 		: '${fn:escapeXml(entry.product.name)}', 
			'id' 		: '${entry.product.code}', 
			'price' 	: '${entry.basePrice.value}', 
			'brand'		: '${fn:escapeXml(entry.product.brand)}', 
			'category' 	: <c:choose>
 							<c:when test="${not empty entry.product.categories}">
				 				'${fn:escapeXml(entry.product.categories[fn:length(entry.product.categories) - 1].name)}',
				 			</c:when>
				 			<c:otherwise>
				 				'',
				 			</c:otherwise>
			 			  </c:choose>
			'variant'	: '${entry.unit.name}',
			'position' 	: '${status.count}', 
			'dealsFlag'	: '${entry.product.dealsFlag}',
			'quantity'	: '${entry.quantity}',
			'isFreeGood' : <c:choose>
								<c:when test="${entry.isFreeGood}">
									'Y'
								</c:when>
					 			<c:otherwise>
					 				'N',
					 			</c:otherwise>
						   </c:choose>
		});
	</c:forEach>
</c:if>

var searchPageDataBreadCrumbs = [];
<c:if test="${searchPageData.pagination.totalNumberOfResults > 0 && not empty searchPageData.breadcrumbs}">
	<c:forEach items="${searchPageData.breadcrumbs}" var="breadcrumb">
		console.log('breadcrumb: ${fn:escapeXml(breadcrumb.facetName)}, ${fn:escapeXml(breadcrumb.facetValueName)}');
		searchPageDataBreadCrumbs.push({'facetValueName' : '${fn:escapeXml(breadcrumb.facetValueName)}'});
	</c:forEach>
</c:if>


<!-- Google Tag Manager -->
(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
	 new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
	 j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
	 'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
	 }) (window,document,'script','dataLayer','${googleTagManagerId}');
<!-- End Google Tag Manager -->

<c:choose>
	<c:when test="${pageType == 'PRODUCT'}">
		dataLayer.push({
                    'pageType':'Product Detail',
                    'event':'productDetail',
                    'ecommerce':{
                        'currencyCode':"${product.price.currencyIso}",
                        'detail':{
                            'actionField': {'list':sessionStorage.getItem("listName")},
                            'products': [{
                                               'name': "${fn:escapeXml(product.name)}",
                                               'id': "${product.code}",
                                               'price': "${product.price.value}",
                                               'brand': "${fn:escapeXml(product.brand)}",
                                               'category': "${fn:escapeXml(product.categories[0].name)}",
                                               'variant': "${product.unit}",
                                               'position': parseInt(sessionStorage.getItem("listOriginPos")),
                                               'dimension13': getOnDeal(String("${product.dealsFlag}")),
                                               'dimension14': "${dealDaysRemaining}",
                                               'dimension15': "${dealValidFrom}",
                                               'dimension16': "${dealValidTo}"
                                        }]
                        }
                    }
                });
	</c:when>

	<c:when test="${pageType == 'CART'}">

		var shippingOption = '${cubArranged.name}';
		<c:if test="${not empty shippingCarriers}">
			shippingOption = '${customerArranged.name}';
		</c:if>

		<c:if test="${checkoutStep eq 1}">
			console.log('at CART load:checkoutStep=${checkoutStep}');
			console.log('at checkoutStep=1');
		
			if (typeof trackOnCheckout !== 'undefined') {
				trackOnCheckout(1, 'Shipping|' + shippingOption);
			}
		</c:if>

		<c:if test="${checkoutStep eq 2}">
			console.log('from Checkout load:checkoutStep=${checkoutStep}');
		
			if (typeof trackOnCheckout !== 'undefined') {
				trackOnCheckout(1, 'Shipping|' + shippingOption);
			}
		</c:if>
		
	</c:when>
	
	<c:when test="${pageType == 'CHECKOUT'}">
		console.log('at CHECKOUT load');
	
		if (typeof trackOnCheckout !== 'undefined') {
			trackOnCheckout(2, 'Payment|Pay on Account');
		}
	</c:when>

	<c:when test="${pageType == 'ORDERCONFIRMATION'}">
		if (typeof trackCheckoutComplete !== 'undefined') {
			trackCheckoutComplete("${orderData}");
		}
	</c:when>

	<c:when test="${pageType == 'PAGE_NOT_FOUND'}">
    		dataLayer.push({
             		'event': '404Page',
             		'eventCategory': 'error',
             		'eventAction':'404 Page',
             		'eventLabel': '${pageUrl}'
             	});
    </c:when>
</c:choose>


<!-- Google Tag Manager functions invoked on page load - START -->

function trackOnCheckout(step, option) {
	console.log('trackOnCheckout step=' + step);
	
	var products = [];
	cartDataEntries.forEach(function(entry) {
	    products.push({
			'name' 			: entry.name, 
			'id' 			: entry.id, 
			'price' 		: entry.price, 
			'brand'			: entry.brand, 
			'category' 		: entry.category,
			'variant'		: entry.variant,
			'position' 		: entry.position, 
			'dimension13' 	: getOnDeal(String(entry.dealsFlag)), 
			'quantity'		: entry.quantity
		});
	    
	    console.log('onCheckout');
	    console.log('name=' + entry.name);
		console.log('id=' + entry.id);
		console.log('price=' + entry.price);
		console.log('brand='	+ entry.brand);
		console.log('category=' + entry.category);
		console.log('variant=' + entry.variant);
		console.log('position=' + entry.position);
		console.log('dimension13=' + getOnDeal(String(entry.dealsFlag)));
		console.log('quantity=' + entry.quantity);
		console.log('step=' + step);
		console.log('option=' + option);
	});
	
	dataLayer.push({
		'event': '${pageType == "CART" ? "Cart Viewed" : "checkout"}',
		'ecommerce': {
			'currencyCode': 'AUD', 
			'checkout': {
				'actionField': {'step': step, 'option': option},
				'products': products
			}/*,
			'eventCallback': function() {
				//document.location = 'checkout.html';
			}*/
		}
	});
	
}



<!-- GTM - Google Tag Manager functions invoked on page load - END -->

} catch(e) { 
	console.log("error: " + e); 
}
</script>
</c:if>
