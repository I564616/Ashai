<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
	<c:if test="${fn:length(breadcrumbs) > 0}">
		<div id="breadcrumb" class="container">
			<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
		</div>
	</c:if>
</sec:authorize>