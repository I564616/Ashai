<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<c:if test="${fn:length(breadcrumbs) > 0}">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
</c:if>
