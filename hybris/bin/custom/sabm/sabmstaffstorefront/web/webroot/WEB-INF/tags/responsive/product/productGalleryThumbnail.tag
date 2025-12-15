<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ attribute name="galleryImages" required="true" type="java.util.List" %>

<div class="carousel gallery-carousel js-gallery-carousel hidden-xs hidden-sm">
    <c:forEach items="${galleryImages}" var="container" varStatus="varStatus">
        <a href="#" class="item"> <img class="lazyOwl" data-src="${container.thumbnail.url}" alt="${container.thumbnail.altText}"></a>
    </c:forEach>
</div>