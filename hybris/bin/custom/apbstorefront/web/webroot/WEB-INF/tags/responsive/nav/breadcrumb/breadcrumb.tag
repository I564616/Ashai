<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="breadcrumbs" required="true" type="java.util.List"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:url value="/" var="homeUrl" />

<c:choose>
    <c:when test="${cmsPage.uid eq 'login' || (isForgetPassword != null && isForgetPassword == 'true') || cmsPage.uid eq 'updatePassword' ||
    (isPasswordUpdated !=null && isPasswordUpdated=='true') }">
        <ol id="transparent-background-body-content" class="breadcrumb">
            <li>
                <a href="${homeUrl}"><spring:theme code="breadcrumb.home" /></a>
            </li>

            <c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
                <c:url value="${breadcrumb.url}" var="breadcrumbUrl" />
                <c:choose>
                    <c:when test="${status.last}">
                        <li class="active">${fn:escapeXml(breadcrumb.name)}</li>
                    </c:when>
                    <c:when test="${breadcrumb.url eq '#'}">
                        <li>
                            <a href="#">${fn:escapeXml(breadcrumb.name)}</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li>
                            <a href="${breadcrumbUrl}">${fn:escapeXml(breadcrumb.name)}</a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </ol>
    </c:when>
    <c:otherwise>
        <ol class="breadcrumb">
            <li>
                <a href="${homeUrl}"><spring:theme code="breadcrumb.home" /></a>
            </li>

            <c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
                <c:url value="${breadcrumb.url}" var="breadcrumbUrl" />
                <c:choose>
                    <c:when test="${status.last}">
						<c:choose>
							<c:when test="${cmsPage.uid eq 'paymentdetail'}">
                        		<li class="active lowercase-breadcrumb">${fn:escapeXml(breadcrumb.name)}</li>
							</c:when>
							<c:otherwise>
								<li class="active">${fn:escapeXml(breadcrumb.name)}</li>
							</c:otherwise>
						</c:choose>
                    </c:when>
                    <c:when test="${breadcrumb.url eq '#'}">
                        <li>
                            <a href="#">${fn:escapeXml(breadcrumb.name)}</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li>
                            <a href="${breadcrumbUrl}">${fn:escapeXml(breadcrumb.name)}</a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </ol>
    </c:otherwise>
</c:choose>

