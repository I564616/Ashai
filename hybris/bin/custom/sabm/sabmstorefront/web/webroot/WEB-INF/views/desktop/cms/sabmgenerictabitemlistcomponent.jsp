<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<div class="tabComponent">
	<ul class="nav nav-justified" id="pills-tab" role="tablist">
		<c:forEach items="${component.components}" var="componentItem">
			<cms:component component="${componentItem}" evaluateRestriction="true" element="li" class="nav-item" />
		</c:forEach>
	</ul>
</div>

