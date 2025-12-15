<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="element" required="true" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="parentComponent" required="false" type="de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<c:forEach items="${actions}" var="action" varStatus="idx">
	<c:if test="${action.visible}">
		<${element} data-index="${idx.index + 1}" class="${styleClass} ${parentComponent.uid}-${action.uid}-Id">
			<cms:component component="${action}" parentComponent="${parentComponent}" evaluateRestriction="true"/>
		</${element}>
	</c:if>
</c:forEach>
