<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<c:forEach items="${cartData.entries}" var="entry" varStatus="loop">
	<cart:cartItem index="${loop.index}" entry="${entry}"/>
</c:forEach>