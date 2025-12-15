<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="productListPosition" required="false" type="java.lang.Integer"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="row productImpressionTag">
	<div class="col-md-3 visible-md-block visible-lg-block cart-img">
		<a href="${product.url}" class="js-track-product-link"
				data-currencycode="${product.price.currencyIso}"
				data-name="${fn:escapeXml(product.name)}"
				data-id="${product.code}"
				data-price="${product.price.value}"
				data-brand="${fn:escapeXml(product.brand)}"
				data-category=<c:choose>
									<c:when test="${not empty product.categories}">
						 				'${fn:escapeXml(product.categories[fn:length(product.categories) - 1].name)}',
						 			</c:when>   
						 			<c:otherwise>
						 				'',
						 			</c:otherwise>
							   </c:choose>
				data-variant=<c:choose>
								<c:when test="${empty product.uomList}">
					 				"${product.unit}"
					 			</c:when>
					 			<c:otherwise>
					 				"${product.uomList[0].name}"
					 			</c:otherwise>
						   </c:choose>
				data-position="${productListPosition}"
				data-url="${product.url}"
				data-actionfield="${fn:escapeXml(requestOrigin)}"
				data-list="${fn:escapeXml(requestOrigin)}"
            	data-dealsflag="${product.dealsFlag}"><product:productPrimaryImage product="${product}" format="thumbnail"/></a>
	</div>
	<div class="col-md-9 trim-left-5-lg cart-name">
		<div class="itemName">
			<a href="${product.url}" class="js-track-product-link"
				data-currencycode="${product.price.currencyIso}"
				data-name="${fn:escapeXml(product.name)}"
				data-id="${product.code}"
				data-price="${product.price.value}"
				data-brand="${fn:escapeXml(product.brand)}"
				data-category=<c:choose>
									<c:when test="${not empty product.categories}">
						 				'${fn:escapeXml(product.categories[fn:length(product.categories) - 1].name)}',
						 			</c:when>   
						 			<c:otherwise>
						 				'',
						 			</c:otherwise>
							   </c:choose>
				data-variant=<c:choose>
								<c:when test="${empty product.uomList}">
					 				"${product.unit}"
					 			</c:when>
					 			<c:otherwise>
					 				"${product.uomList[0].name}"
					 			</c:otherwise>
						   </c:choose>
				data-position="${productListPosition}"
				data-url="${product.url}"
				data-actionfield="${fn:escapeXml(requestOrigin)}"
				data-list="${fn:escapeXml(requestOrigin)}"
            	data-dealsflag="${product.dealsFlag}">
				<h3 class="clamp-2">${product.name}</h3>
				<div class="h3 h3-subheader clamp-1">${product.packConfiguration}</div>
			</a>
		</div>
		<c:if test="${product.cubStockStatus.code == 'lowStock'}"><div class="low-stock-status-label"><spring:theme code="product.page.stockstatus.low"/></div></c:if>
		<c:if test="${product.cubStockStatus.code == 'outOfStock'}"><div class="out-of-stock-status-label"><spring:theme code="product.grid.outOfStock"/></div></c:if>
	</div>
</div>