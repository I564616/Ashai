<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>

<div class="product-details">

	<%-- <product:productReviewSummary product="${product}" showLinks="true" /> --%>

</div>
<div class="detail">
	<section class="row no-border">
		<div class="col-md-6 col-lg-6 product-images">
			<product:productImagePanel galleryImages="${galleryImages}"
				product="${product}" />
		</div>
		<div class="col-md-6 col-lg-6 product-summary">
			<ycommerce:testId
				code="productDetails_productNamePrice_label_${product.code}">

				<h1>${product.name}</h1>

			</ycommerce:testId>
			<%-- <product:productPromotionSection product="${product}" /> --%>
			<div class="sub-header">
				<!-- the following sub header is display purpose will be replaced by the real data later-->
				4 x 6 x 375ml Bottles
			</div>
			<ycommerce:testId
				code="productDetails_productNamePrice_label_${product.code}">
				<product:productPricePanel product="${product}" />
			</ycommerce:testId>

			<div class="description">${product.summary}</div>

			<cms:pageSlot position="VariantSelector" var="component">
				<cms:component component="${component}" />
			</cms:pageSlot>

			<cms:pageSlot position="AddToCart" var="component">
				<cms:component component="${component}" />
			</cms:pageSlot>



		</div>
	</section>
</div>


