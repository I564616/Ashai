<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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


<%-- <link rel="stylesheet" href="${commonResourcePath}/blueprint/print.css" type="text/css" media="print" /> --%>
<style type="text/css" media="print">
	@IMPORT url("${commonResourcePath}/blueprint/print.css");
</style>

<cms:previewCSS cmsPageRequestContextData="${cmsPageRequestContextData}" />
