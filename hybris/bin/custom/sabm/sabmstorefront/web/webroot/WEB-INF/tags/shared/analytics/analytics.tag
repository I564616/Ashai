<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="analytics" tagdir="/WEB-INF/tags/shared/analytics" %>


<script type="text/javascript" async src="${staticHostPath}${sharedStaticsContextPath}/js/analyticsmediator.js"></script>

<analytics:enhancedGoogleAnalytics/>
<analytics:googleTagManager/>
<%--
Commented to remove segments from release
--%>
<analytics:segmentAnalytics/>
<analytics:jirafe/>