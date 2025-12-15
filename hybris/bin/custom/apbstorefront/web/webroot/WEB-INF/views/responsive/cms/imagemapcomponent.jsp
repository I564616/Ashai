<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<div>
	<img title="${fn:escapeXml(media.altText)}" alt="${fn:escapeXml(media.altText)}" src="${media.url}" usemap="#map">
	<map name="map">
		${imageMapHTML}
	</map>
</div>