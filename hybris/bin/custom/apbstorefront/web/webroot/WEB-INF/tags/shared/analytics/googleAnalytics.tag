<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:if test="${not empty googleAnalyticsTrackingId}">
<script type="text/javascript">
/* Google Analytics */

var googleAnalyticsTrackingId = '${ycommerce:encodeJavaScript(googleAnalyticsTrackingId)}';
var _gaq = _gaq || [];
_gaq.push(['_setAccount', googleAnalyticsTrackingId]);
_gaq.push(['_trackPageview']);
<c:choose>
	<c:when test="${pageType == 'PRODUCT'}">
		<c:set var="categories" value="" />
		<c:forEach items="${product.categories}" var="category">
			<c:set var="categories">${categories},${ycommerce:encodeJavaScript(category.name)}</c:set>
		</c:forEach>
		_gaq.push(['_setCustomVar', 2, 'CategoryOfProduct', '${fn:substringAfter(categories, ',')}', 3]);
	</c:when>

	<c:when test="${pageType == 'CATEGORY' || pageType == 'PRODUCTSEARCH'}">
		<c:choose>
			<c:when test="${searchPageData.pagination.totalNumberOfResults > 0}">
				<c:if test="${not empty searchPageData.breadcrumbs}">
					<c:forEach items="${searchPageData.breadcrumbs}" var="breadcrumb">
						<c:set var="pageName" value="${pageType == 'CATEGORY' ? 'category' : 'facet'}"/>
						_gaq.push(['_trackEvent', '${pageName}', '${ycommerce:encodeJavaScript(breadcrumb.facetName)}', '${ycommerce:encodeJavaScript(breadcrumb.facetValueName)}']);
					</c:forEach>
				</c:if>
			</c:when>
			
			<c:otherwise>
				_gaq.push(['_setCustomVar', 1, 'ZeroResults', '${ycommerce:encodeJavaScript(searchPageData.freeTextSearch)}', 3]);
			</c:otherwise>
		</c:choose>
		
		_gaq.push(['_trackPageview']);
	</c:when>

	<c:when test="${pageType == 'ORDERCONFIRMATION'}">
		<c:set var="orderCode" value="${ycommerce:encodeJavaScript(orderData.code)}"/>
		_gaq.push([
	 		 '_addTrans',
	 		 '${orderCode}',
	 		 '${ycommerce:encodeJavaScript(siteName)}',
	 		 '${orderData.totalPrice.value}',
	 		 '${orderData.totalTax.value}',
	 		 '${orderData.deliveryCost.value}',
	 		 '${ycommerce:encodeJavaScript(orderData.deliveryAddress.town)}',
	 		 '${ycommerce:encodeJavaScript(orderData.deliveryAddress.postalCode)}',
	 		 '${ycommerce:encodeJavaScript(orderData.deliveryAddress.country.name)}'
	 	]);
	 	<c:forEach items="${orderData.entries}" var="entry">
	 		_gaq.push([
	 		    '_addItem',
	 			'${orderCode}',
	 			'${ycommerce:encodeJavaScript(entry.product.code)}',
	 			'${ycommerce:encodeJavaScript(entry.product.name)}',
	 			<c:choose>
		 			<c:when test="${not empty entry.product.categories}">
		 				'${ycommerce:encodeJavaScript(entry.product.categories[fn:length(entry.product.categories) - 1].name)}',
		 			</c:when>
		 			<c:otherwise>
		 				'',
		 			</c:otherwise>
	 			</c:choose>
	 			'${entry.product.price.value}',
	 			'${entry.quantity}'
	 		]);
	 	</c:forEach>
	 	_gaq.push(['_trackTrans']);
	</c:when>
	<c:otherwise>
		_gaq.push(['_trackPageview']);
	</c:otherwise>
</c:choose>

(function() {
	var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();


function trackAddToCart_google(productCode, quantityAdded) {
	_gaq.push(['_trackEvent', 'Cart', 'AddToCart', productCode, quantityAdded]);
}

function trackUpdateCart(productCode, initialQuantity, newQuantity) {
	if (initialQuantity != newQuantity) {
		if (initialQuantity > newQuantity) {
			_gaq.push(['_trackEvent', 'Cart', 'RemoveFromCart', productCode, initialQuantity - newQuantity]);
		} else {
			_gaq.push(['_trackEvent', 'Cart', 'AddToCart', productCode, newQuantity - initialQuantity]);
		}
	}
}

function trackRemoveFromCart(productCode, initialQuantity) {
	_gaq.push(['_trackEvent', 'Cart', 'RemoveFromCart', productCode, initialQuantity]);
}

window.mediator.subscribe('trackAddToCart', function(data) {
	if (data.productCode && data.quantity)
	{
		trackAddToCart_google(data.productCode, data.quantity);
	}
});

window.mediator.subscribe('trackUpdateCart', function(data) {
	if (data.productCode && data.initialCartQuantity && data.newCartQuantity)
	{
		trackUpdateCart(data.productCode, data.initialCartQuantity, data.newCartQuantity);
	}
});

window.mediator.subscribe('trackRemoveFromCart', function(data) {
	if (data.productCode && data.initialCartQuantity)
	{
		trackRemoveFromCart(data.productCode, data.initialCartQuantity);
	}
});

window.mediator.subscribe('trackAddProductToCart', function(data) {
	_gaq.push(['_trackEvent', data.label, 'AddToCart', data.productCode, data.quantity]);
});

window.mediator.subscribe('trackProductClick', function(data) {
	_gaq.push(['_trackEvent', data.label, 'asahiProductClick', data.productCode]);
});

window.mediator.subscribe('trackProductRecommendation', function(data) {
	if(data.productData1 != ''){
		_gaq.push(['_trackEvent', data.label, 'asahiSection1Recommendations', data.productData1]);
	}
	if(data.productData2 != ''){
		_gaq.push(['_trackEvent', data.label, 'asahiSection2Recommendations', data.productData2]);
	}
	if(data.productData3 != ''){
		_gaq.push(['_trackEvent', data.label, 'asahiSection3Recommendations', data.productData3]);
	}
	
});

window.mediator.subscribe('trackProductRecommendationAddToCart', function(data) {
	if (data.productCode)
	{
		_gaq.push(['_trackEvent', data.label, 'asahiProductRecommendationAddToCart', data.productCode]);
	}
	
});

window.mediator.subscribe('trackAsahiCheckoutError', function(data) {
	if (data.errorMsg)
	{
		_gaq.push(['_trackEvent', data.label, 'asahiCheckoutError', data.errorMsg]);
	}
	
});

</script>
</c:if>