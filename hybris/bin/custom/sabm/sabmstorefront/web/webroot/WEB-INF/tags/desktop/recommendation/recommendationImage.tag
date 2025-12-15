<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="productListPosition" required="false" type="java.lang.Integer"%>

<a href="${product.url}" class="js-track-product-link"
        data-currencycode="${product.price.currencyIso}"
        data-name="${fn:escapeXml(product.name)}"
        data-sku="${product.leadSkuId}"
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
        data-actionfield="${requestOrigin}"
        data-list="${requestOrigin}"
        data-dealsflag="${product.dealsFlag}"
        data-isSuggested="true"
        data-isPromotion="${product.dealsFlag}"
        data-isReOrder="false"><product:productPrimaryImage product="${product}" format="thumbnail"/></a>