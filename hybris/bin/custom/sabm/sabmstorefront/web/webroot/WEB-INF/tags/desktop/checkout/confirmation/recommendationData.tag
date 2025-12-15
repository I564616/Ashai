<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<c:if test="${not empty allRecommendedProductsInCart}">
    <div class="recommended-products" style="display:none;" data-order-id="${orderData.sapSalesOrderNumber}">
        <c:forEach var="orderEntry" items="${orderData.entries}" >
            <c:set var="product" value="${orderEntry.product}"/>
            <c:if test="${allRecommendedProductsInCart.get(product.baseProduct) ne null}">
                <div class="recommended-products-purchased"
                                data-currencycode="${product.price.currencyIso}"
                                data-name="${fn:escapeXml(product.name)}"
                                data-id="${product.baseProduct}"
                                data-price="${orderEntry.basePrice.value}"
                                data-quantity="${orderEntry.quantity}"
                                data-brand="${fn:escapeXml(product.brand)}"
                                data-category=<c:choose>
                                                    <c:when test="${not empty product.categories}">
                                                         '${fn:escapeXml(product.categories[fn:length(product.categories) - 1].name)}'
                                                     </c:when>
                                                     <c:otherwise>
                                                         ''
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
                                data-position="${orderEntry.entryNumber}"
                                data-url="${product.url}"
                                data-dealsflag="${product.dealsFlag}"
                                data-recommendation-model="${allRecommendedProductsInCart.get(product.baseProduct)}"
                                data-recommendation-group="${smartRecommendationGroup}">
                </div>
            </c:if>
        </c:forEach>
    </div>
</c:if>