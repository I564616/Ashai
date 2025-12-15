<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<c:choose>
   <c:when test="${cmsSite.uid eq 'sga'}">
		<order:sgaOrderTemplates/>
   </c:when>
   <c:otherwise>
		<order:apbOrderTemplates/>
   </c:otherwise>
</c:choose>