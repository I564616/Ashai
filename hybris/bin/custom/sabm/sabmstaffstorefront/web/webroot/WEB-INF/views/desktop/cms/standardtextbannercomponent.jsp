<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:url value="${link }" var="componentUrl"/>

<div class="text-banner">
	<c:choose>
		<c:when test="${empty componentUrl || componentUrl eq '#'}">
		${content }
		</c:when>
		<c:otherwise>
		  	<a href="${componentUrl }" class="">${content }</a>
		</c:otherwise>
	</c:choose>
</div>

