<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<ul id="genericCarousel">
<c:forEach items="${component.carouselImageItems}" var="item">
    <li><a href="javascript:void(0)">
        <img src="${item.carouselImage.url}" alt="${item.carouselImageTitle}" />
        <p>
            <strong>${item.carouselImageTitle}</strong>
            ${item.carouselImageDescription}
        </p>
    </a></li>
</c:forEach>
</ul>