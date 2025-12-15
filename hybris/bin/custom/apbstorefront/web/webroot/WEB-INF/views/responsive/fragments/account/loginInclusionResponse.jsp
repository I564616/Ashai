<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

{
"response":{
	"success":"${response.success}",
	"errors": [
		<c:forEach items="${response.errors}" var="error" varStatus="status">
			{
                <c:choose><c:when test="${error.errorCode ne 'exclude_product_error'}"> "error":"<spring:theme code='${error.error}' htmlEscape="true"/>" </c:when>
                <c:otherwise> "error":"<c:out value='${error.error}' escapeXml="false"/>" </c:otherwise></c:choose>,
				"errorCode":"${error.errorCode}"
			}<c:if test="${not status.last}">,</c:if>
		</c:forEach>
		]
	}
}
