<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%@ attribute name="formatAmount" required="true" type="java.lang.Double" %>

<fmt:formatNumber value="${formatAmount*100%100 }" maxFractionDigits="0" var="tempAmount"/>
<c:if test="${tempAmount==0}">
	<fmt:formatNumber value="${formatAmount}" maxFractionDigits="0" var="amount" scope="session"/>
</c:if>	
<c:if test="${tempAmount!=0}">
	<fmt:formatNumber value="${formatAmount}" pattern="0.00" var="amount" scope="session"/>
</c:if>