<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:url value="/recommendation" var="recommendationUrl"/>
<a href="${recommendationUrl}">
	<span class="icon-mask">
		<svg class="icon-star01" id="recommendationHeaderStar" data-toggle="popover" data-content="<p><b>Welcome! We have some recommendations for you!</b></p>">
			<use xlink:href="#icon-star01"></use>
		</svg>
		<c:if test="${recommendationsCount > 0}">
		<span class="badge hidden-xs">${recommendationsCount}</span>
		</c:if>
	</span>
	<label class="recommendations-text">
    <spring:theme code="text.recommendations.header"/>
    <c:if test="${recommendationsCount > 0}">
    <span class="badge visible-xs-inline-block">${recommendationsCount}</span>
    </c:if>
  </label>
</a>