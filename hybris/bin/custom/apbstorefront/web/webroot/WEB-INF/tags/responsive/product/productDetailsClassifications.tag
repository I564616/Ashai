<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<div class="product-classifications">
	<ycommerce:testId code="productDetails_content_label">
	<div class="productDescriptionText">
			${ycommerce:sanitizeHTML(product.productDetail)}
	</div>
</ycommerce:testId>
</div>