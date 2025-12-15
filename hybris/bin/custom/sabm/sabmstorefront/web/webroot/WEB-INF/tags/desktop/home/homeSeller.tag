<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="count" required="true" type="java.lang.String"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:url value="${product.url}" var="productUrl"/>
<c:set value="Home/Best Sellers" var="actionField"/>
<div class="productImpressionTag">
	<a href="${productUrl}" class="js-track-product-link"
        	data-currencycode="${product.price.currencyIso}"
			data-name="${fn:escapeXml(product.name)}"
			data-id="${product.code}"
			data-price="${product.price.value}"
			data-brand="${fn:escapeXml(product.brand)}"
			data-category=<c:choose>
                       		<c:when test="${not empty product.categories}">
                       			"${fn:escapeXml(product.categories[fn:length(product.categories) - 1].name)}"
                       		</c:when>
                       		<c:otherwise>
                       			""
                       		</c:otherwise>
                          </c:choose>
			data-variant="${product.unit}"
			data-position="${count}"
			data-url="${product.url}"
			data-actionfield="${actionField}"
            data-list="${actionField}"
            data-dealsflag="${product.dealsFlag}">
		<div class="slick-img-wrap">
			<product:productPrimaryImage product="${product}" format="product"/>
		</div> 
    	<h3 class="clamp-2">${product.name}</h3>
    	<div class="h3 h3-subheader clamp-2">${product.packConfiguration}</div>
	</a>
</div>