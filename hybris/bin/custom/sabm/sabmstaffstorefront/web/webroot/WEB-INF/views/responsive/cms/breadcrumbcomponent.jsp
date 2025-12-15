<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="breadcrumb"
	tagdir="/WEB-INF/tags/responsive/nav/breadcrumb"%>

<c:if test="${fn:length(breadcrumbs) > 0}">
	<div class="breadcrumb-section">
		<div class="container">
			<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}" />
		</div>
	</div>
</c:if>
