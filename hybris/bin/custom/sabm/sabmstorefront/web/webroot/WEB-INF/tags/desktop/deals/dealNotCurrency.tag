<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%@ attribute name="formatAmount" required="true" type="java.lang.Double" %>

<fmt:formatNumber value="${formatAmount+0.00001}" maxFractionDigits="2" var="amount" scope="session"/>
