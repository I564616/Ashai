<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="row pdp-section">
	<div class="col-xs-12">
		<ycommerce:testId code="productDetails_content_label">
			<p>${product.description}</p>
		</ycommerce:testId>
	</div>
	<div class="col-md-6">
		<h4 class="h4-alt"><spring:theme code="text.product.detail.tab.about.details" /></h4>
		<table class="full-width">
			<c:if test="${not empty product.code}">
				<tr>
					<td class="half-width"><spring:theme code="text.product.detail.tab.about.sku" /></td>
					<td>${product.leadSkuId}</td>
				</tr>
			</c:if>
			<c:if test="${not empty product.ean}">
				<tr>
					<td class="half-width"><spring:theme code="text.product.detail.tab.about.ean" /></td>
					<td>${product.ean}</td>
				</tr>
			</c:if>
			<c:if test="${not empty product.abv}">
				<tr>
					<td class="half-width"><spring:theme code="text.product.detail.tab.about.abv" /></td>
					<%-- <td>${product.abv}</td> --%>
					<!-- INC0344001: Code fix for displaying alcohol by volume by 1 decimal places -->
					<!--<fmt:parseNumber var="abv" type="number" value="${product.abv}" />-->					
					<fmt:formatNumber var="abv" type="number" minFractionDigits="1" value="${product.abv}"></fmt:formatNumber>
					<td><c:out value="${abv}" /></td>
				</tr>
			</c:if>
			<c:if test="${not empty product.style}">
				<tr>
					<td class="half-width"><spring:theme code="text.product.detail.tab.about.style" /></td>
					<td>${product.style}</td>
				</tr>
			</c:if>
			<c:if test="${not empty product.categoryVariety}">
				<tr>
					<td class="half-width"><spring:theme code="text.product.detail.tab.about.subcategory" /></td>
					<td>${product.categoryVariety}</td>
				</tr>
			</c:if>
			<c:if test="${not empty product.container}">
				<tr>
					<td class="half-width"><spring:theme code="text.product.detail.tab.about.packagetype" /></td>
					<td>${product.container}</td>
				</tr>
			</c:if>
			<c:if test="${not empty product.capacity}">
				<tr>
					<td class="half-width"><spring:theme code="text.product.detail.tab.about.size" /></td>
					<td>${product.capacity}</td>
				</tr>
			</c:if>
		</table>
	</div>
	
	<div class="col-md-6">
		<c:if test="${not empty product.foodMatch}">
			<h4 class="h4-alt"><spring:theme code="text.product.detail.tab.about.foodmatch" /></h4>
			<span>${product.foodMatch}</span>
		</c:if>
		
		<c:if test="${not empty product.findOutMore}">
			<h4 class="h4-alt"><spring:theme code="text.product.detail.tab.about.more" /></h4>
			<span>${product.findOutMore}</span>
		</c:if>
	</div>
</div>

<div class="clearfix">
	<product:productDetailsClassifications product="${product}" />
</div>