<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:if test="${not empty zoomImageUrl}">
	<img src="${zoomImageUrl}" id="primaryImage" alt="${product.name}" title="${product.name}" producturl="${product.url}"/>
</c:if>
