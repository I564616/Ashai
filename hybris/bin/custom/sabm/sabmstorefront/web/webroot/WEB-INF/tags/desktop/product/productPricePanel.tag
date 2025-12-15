<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<c:if test="${!isNAPGroup}">
<div class="row product-price-panel text-right">
	<!-- Wholesale price -->
	<c:if test="${product.savingsPrice.value > 0}">
	
	<div class="col-xs-12 offset-bottom-xsmall">
		<span>
			<c:if test="${not empty product.uomList}">
				<spring:theme code="text.product.wholesale.param" arguments="${product.uomList[0].name}"/>&nbsp;
			</c:if>
			<c:if test="${empty product.uomList}">
				<spring:theme code="text.product.wholesale.param" arguments="${product.unit}"/>&nbsp;
			</c:if>
		</span>
		<span class="bold strike">${product.basePrice.formattedValue}</span>
	</div>
</c:if>
	<!-- Everyday price -->
	<div class="col-xs-12">

	</div>

	<!-- Your case price -->
	<div class="col-xs-12 price-yourPrice offset-bottom-xsmall margin-top-10">
		<span class="price-label">
			<c:if test="${not empty product.uomList}">
				<spring:theme code="text.product.your.param" arguments="${product.uomList[0].name }"/>
			</c:if>
			<c:if test="${empty product.uomList}">
				<spring:theme code="text.product.your.param" arguments="${product.unit}"/>
			</c:if>
		</span>
		<span class="h1">&nbsp;${product.price.formattedValue}</span>
	</div>
	<!-- Savingss -->
	<c:if test="${product.savingsPrice.value > 0}">
	<div class="col-xs-12 price-save offset-bottom-small">
		<span>Save</span>&nbsp;
		<span>${product.savingsPrice.formattedValue}</span>
	</div>
	</c:if>
	
	<c:if test="${product.cubStockStatus == 'lowStock' || not empty product.maxOrderQuantity}">
		<div class="col-xs-12 low-stock-status-label offset-bottom-xsmall margin-top-10">
			<c:if test="${product.cubStockStatus == 'lowStock'}">
				<spring:theme code="product.page.stockstatus.low"/>
			</c:if>
			<c:if test="${product.cubStockStatus == 'lowStock' && not empty product.maxOrderQuantity}">
				<br>
			</c:if>
			<c:if test="${not empty product.maxOrderQuantity}">
				<span style="color:red;font-weight:700;"><spring:theme code="product.page.maxorderquantity" arguments="${product.maxOrderQuantityDays},${product.maxOrderQuantity}" /></span>
			</c:if>
		</div>
	</c:if>
</div>
</c:if>


