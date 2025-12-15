<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ attribute name="galleryImages" required="true" type="java.util.List" %>
<%@ attribute name="productUnavailable" required="false" type="java.lang.String"%>

<div class="carousel gallery-carousel js-gallery-carousel">
    <c:forEach items="${galleryImages}" var="container" varStatus="varStatus">
		<c:choose>
			<c:when test="${varStatus.count == 1}" >
				<a href="#" id="" class="item target pdpImageWrapper_${varStatus.count}"><img class="lazyOwl pdpImage_${varStatus.count}" id="" data-src="${container.thumbnail.url}" alt="${fn:escapeXml(container.thumbnail.altText)}"></a>
			</c:when>
			<c:otherwise>
				<a href="#" id="" class="item pdpImageWrapper_${varStatus.count}"><img class="lazyOwl pdpImage_${varStatus.count}" id="" data-src="${container.thumbnail.url}" alt="${fn:escapeXml(container.thumbnail.altText)}"></a>
			</c:otherwise> 
		</c:choose>
    </c:forEach>
</div>