<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${!isNAPGroup}">
<product:productAddToCartPanel product="${product}" allowAddToCart="${empty showAddToCart ? true : showAddToCart}" isMain="true" />
</c:if>