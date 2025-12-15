<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="compressible" tagdir="/WEB-INF/tags/mobile/template/compressible" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/mobile/template/cms" %>

<c:choose>
	<c:when test="${granuleEnabled}">
			<compressible:css/>
	</c:when>
	<c:otherwise>
		<compressible:css/>
	</c:otherwise>
</c:choose>

<cms:previewCSS cmsPageRequestContextData="${cmsPageRequestContextData}" />
