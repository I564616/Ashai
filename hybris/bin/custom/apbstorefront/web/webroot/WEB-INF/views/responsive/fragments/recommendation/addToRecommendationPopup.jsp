<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
{
"addToRecommendationLayer":"<spring:escapeBody javaScriptEscape="true">

<div >
	<span> 
		<c:if test="${isRecommendationAdded eq true}">
				<spring:theme code="text.recommendations.item.Added"/>
		</c:if>
	</span>
</div>
</spring:escapeBody>",
"recommendationsCount":"${recommendationsCount}"
}


