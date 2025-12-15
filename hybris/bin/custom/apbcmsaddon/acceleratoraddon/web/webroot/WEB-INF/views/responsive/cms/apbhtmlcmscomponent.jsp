<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:choose>
    <c:when test="${useCustomized}">
        <c:out value="${htmlContent}" escapeXml="false"/>
    </c:when>
    <c:otherwise>
        <c:if test="${not empty banners}">
            <c:import url="/WEB-INF/views/addons/apbcmsaddon/responsive/pages/slideshow.jsp"/>
        </c:if>
    </c:otherwise>
</c:choose>