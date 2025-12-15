<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="compressible" tagdir="/WEB-INF/tags/desktop/template/compressible" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/desktop/template/cms" %>
<c:choose>
	<c:when test="${granuleEnabled}">
			<compressible:css/>
	</c:when>
	<c:otherwise>
		<compressible:css/>
	</c:otherwise>
</c:choose>