<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<c:if test="${not empty cartData.entries}">
	<cart:cartPotentialPromotions cartData="${cartData}"/>
</c:if>