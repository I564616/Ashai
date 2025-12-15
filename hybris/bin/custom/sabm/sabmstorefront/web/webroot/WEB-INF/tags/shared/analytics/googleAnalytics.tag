<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:if test="${not empty googleAnalyticsTrackingId}">
<script type="text/javascript">
/* Google Analytics */

var googleAnalyticsTrackingId = '${googleAnalyticsTrackingId}';
var _gaq = _gaq || [];
_gaq.push(['_setAccount', googleAnalyticsTrackingId]);


<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
	   _gaq.push(['_setCustomVar',4,'Customer ID','${user.gaUid}',2]);
	   _gaq.push(['_setCustomVar',3,'Visitor ID','${user.currentB2BUnit.uid}',1]);
</sec:authorize>
<c:choose>
	<c:when test="${pageType == 'PRODUCT'}">
		<c:set var="categories" value="" />
		<c:forEach items="${product.categories}" var="category">
			<c:set var="categories">${categories},${category.name}</c:set>
		</c:forEach>
		_gaq.push(['_setCustomVar',2,'CategoryOfProduct','${fn:escapeXml(fn:substringAfter(categories, ','))}',3]);
		_gaq.push(['_trackPageview']);
		</c:when>

	<c:when test="${pageType == 'CATEGORY'}">
		<c:choose>
			<c:when test="${searchPageData.pagination.totalNumberOfResults > 0}">
				<c:if test="${not empty searchPageData.breadcrumbs}">
					<c:forEach items="${searchPageData.breadcrumbs}" var="breadcrumb">
						_gaq.push(['_trackEvent','category','${fn:escapeXml(breadcrumb.facetName)}','${fn:escapeXml(breadcrumb.facetValueName)}']);
					</c:forEach>
				</c:if>
			</c:when>
			
			<c:otherwise>
				_gaq.push(['_setCustomVar',1,'ZeroResults','${fn:escapeXml(searchPageData.freeTextSearch)}',3]);
			</c:otherwise>
		</c:choose>
		_gaq.push(['_setCustomVar',2,'rootCategory','${fn:escapeXml(categoryData.name)}',3]);
		   _gaq.push(['_setSiteSpeedSampleRate', 5]);
		   _gaq.push(['_trackPageview']);
	</c:when>
	
	<c:when test="${pageType == 'PRODUCTSEARCH'}">
		<c:choose>
			<c:when test="${searchPageData.pagination.totalNumberOfResults > 0}">
				<c:if test="${not empty searchPageData.breadcrumbs}">
					<c:forEach items="${searchPageData.breadcrumbs}" var="breadcrumb">
						_gaq.push(['_trackEvent','facet','${fn:escapeXml(breadcrumb.facetName)}','${fn:escapeXml(breadcrumb.facetValueName)}']);
					</c:forEach>
				</c:if>
			</c:when>
			
			<c:otherwise>
				_gaq.push(['_setCustomVar',1,'ZeroResults','${fn:escapeXml(searchPageData.freeTextSearch)}',3]);
			</c:otherwise>
		</c:choose>
		   _gaq.push(['_setSiteSpeedSampleRate', 5]);
		   _gaq.push(['_trackPageview']);
	</c:when>
	<c:when test="${pageType == 'ORDERCONFIRMATION'}">
		_gaq.push([
	 		 '_addTrans',
	 		 '${orderData.code}',
	 		 '${siteName}',
	 		 '${orderData.totalPrice.value}',
	 		 '${orderData.totalTax.value}',
	 		 '${orderData.deliveryCost.value}',
	 		 '${orderData.deliveryAddress.town}',
	 		 '${orderData.deliveryAddress.postalCode}',
	 		 '${orderData.deliveryAddress.country.name}'
	 	]);
	 	<c:forEach items="${orderData.entries}" var="entry">
	 		_gaq.push([
	 		    '_addItem',
	 			'${orderData.code}',
	 			'${entry.product.code}',
	 			'${fn:escapeXml(entry.product.name)}',
	 			<c:choose>
		 			<c:when test="${not empty entry.product.categories}">
		 				'${fn:escapeXml(entry.product.categories[fn:length(entry.product.categories) - 1].name)}',
		 			</c:when>
		 			<c:otherwise>
		 				'',
		 			</c:otherwise>
	 			</c:choose>	 			
	 			'${entry.basePrice.value}',
	 			'${entry.quantity}'
	 		]);
	 	</c:forEach>
	 	
	 	if(!checkIsDuplicateTransaction('${orderData.code}')){
	 		_gaq.push(['_trackTrans']);
	 	}
	 
	 	
	</c:when>
	<c:otherwise>
	   _gaq.push(['_setSiteSpeedSampleRate', 5]);
	   _gaq.push(['_trackPageview']);
	</c:otherwise>
</c:choose>

function checkIsDuplicateTransaction(ref) {
	var cookiename = 'gaSaleSent_' + ref;
    var transCookie = document.cookie.split(';');
    for(var i=0; i<transCookie.length; i++) {
        var c = transCookie[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(cookiename + '=') != -1){
        	return true;
        }
    }
    
    var d = new Date();
    d.setTime(d.getTime() + (90*24*60*60*1000));
    var expires = "expires="+d.toGMTString();
    document.cookie = cookiename + '=1; ' + expires;
    
    return false;  
}

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

function trackPQDModalLaunch() {
	_gaq.push(['_trackEvent', 'Cart', 'PQDModalLaunch', '${user.currentB2BUnit.uid}']);
}

function trackConflictModalLaunch() {
	_gaq.push(['_trackEvent', 'Cart', 'ConflictModalLaunch', '${user.currentB2BUnit.uid}']);
}

function trackPQDAdded(numDeals) {
	_gaq.push(['_trackEvent', 'Cart', 'PQDAddedToCart', '${user.currentB2BUnit.uid}', numDeals]);
}

function trackDealsInConflictModal(numConflicts) {
	_gaq.push(['_trackEvent', 'Cart', 'DealsInConflict', '${user.currentB2BUnit.uid}', numConflicts]);
}

function trackDismissPQD() {
	_gaq.push(['_trackEvent', 'Cart', 'dismissPQD', '${user.currentB2BUnit.uid}']);
}

function trackAddDealPDP() {
	_gaq.push(['_trackEvent', 'PDP', 'addDealPDP', '${user.currentB2BUnit.uid}']);
}

function trackAddDealDeals() {
	_gaq.push(['_trackEvent', 'Deals', 'addDealDeals', '${user.currentB2BUnit.uid}']);
}

function trackDealsPageFromCart() {
	_gaq.push(['_trackEvent', 'Cart', 'goToDealsPageFromCart', '${user.currentB2BUnit.uid}']);
}

function trackRelaunchPQDModal() {
	_gaq.push(['_trackEvent', 'Cart', 'trackRelaunchPQDModal', '${user.currentB2BUnit.uid}']);
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
</script>
</c:if>