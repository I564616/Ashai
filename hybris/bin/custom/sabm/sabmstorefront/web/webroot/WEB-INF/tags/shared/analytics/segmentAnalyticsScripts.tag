<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script>

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

<c:if test="${pageType == 'CATEGORY'}">

	// Track - Category page view
	analytics.track('Product Category Viewed', {
	  category: '${fn:escapeXml(categoryData.name)}'
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

/* track product impression when lazy load */
<c:if test="${pageType == 'RECOMMENDATION'  || pageType == 'DEAL'}">
 
 rm.segment = {
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
        
   	}	
};
 
</c:if>

</script>
<%--
PAGE TYPE: ${pageType} --
CART STEP: ${checkoutStep}
CATEGORY: ${fn:escapeXml(product.categories[fn:length(product.categories) - 1].name)}
IMG URL: ${sortedGallery[0].zoom.url}--%>
