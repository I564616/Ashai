<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose>
   <c:when test="${cmsSite.uid eq 'sga'}">
		<order:sgaOrderTemplateDetails/>
   </c:when>
   <c:otherwise>
		<order:apbOrderTemplateDetails/>
   </c:otherwise>
</c:choose>