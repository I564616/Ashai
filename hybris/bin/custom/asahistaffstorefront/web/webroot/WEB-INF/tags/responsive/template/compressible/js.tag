<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- j query 1.11.2 --%>
<script type="text/javascript" src="/asahiStaffPortal/_ui/responsive/Asahi/js/libs/jquery-1.12.3.min.js"></script>
<%-- j query query 2.1.7 --%>
<script type="text/javascript" src="/asahiStaffPortal/_ui/responsive/Asahi/js/libs/jquery.query-2.1.7.js"></script>
<%-- jquery tabs dependencies --%>
<script type="text/javascript" src="/asahiStaffPortal/_ui/responsive/Asahi/js/libs/jquery-ui-1.12.0-rc.1.min.js"></script>
<%-- Cms Action JavaScript files --%>
<c:forEach items="${cmsActionsJsFiles}" var="actionJsFile">
	<c:if test="${actionJsFile != 'vieworderaction.js' }">
    	<script type="text/javascript" src="${commonResourcePath}/js/cms/${actionJsFile}"></script>
    </c:if>
</c:forEach>
<%-- AddOn JavaScript files --%>
<c:forEach items="${addOnJavaScriptPaths}" var="addOnJavaScript">
    <script type="text/javascript" src="${addOnJavaScript}"></script>
</c:forEach>

<script type="text/javascript" src="/asahiStaffPortal/_ui/responsive/Asahi/js/libs/angular.min.js"></script>
<script type="text/javascript" src="/asahiStaffPortal/_ui/responsive/Asahi/js/libs/angular-animate.min.js"></script>
<script type="text/javascript" src="/asahiStaffPortal/_ui/responsive/Asahi/js/libs/angular-sanitize.min.js"></script>
<script type="text/javascript" src="/asahiStaffPortal/_ui/responsive/Asahi/js/libs/angular-ui-bootstrap.min.js"></script>

<script type="text/javascript" src="/asahiStaffPortal/_ui/responsive/Asahi/js/app.js"></script>
<script type="text/javascript" src="/asahiStaffPortal/_ui/responsive/Asahi/js/plugins.js"></script>
<script type="text/javascript" src="/asahiStaffPortal/_ui/responsive/Asahi/js/src.js"></script>
<script type="text/javascript" src="/asahiStaffPortal/_ui/responsive/Asahi/js/main.js"></script>
