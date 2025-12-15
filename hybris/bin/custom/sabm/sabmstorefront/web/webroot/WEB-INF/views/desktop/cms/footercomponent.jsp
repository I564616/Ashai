<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
<div class="main-footer">
	<ul class="main-footer-list flex-center-tablet space-between">
		<c:forEach items="${navigationNodes}" var="node">
			<c:if test="${node.visible}">
				<c:forEach items="${node.links}" var="childlink" >
					<li class="links <c:if test="${fn:toLowerCase(childlink.linkName) == 'business enquiry' || fn:toLowerCase(childlink.linkName) == 'contact us'}">bde-view-only</c:if>">
						<cms:component component="${childlink}" evaluateRestriction="true"/>
					</li>
				</c:forEach>
			</c:if>
		</c:forEach>
	</ul>
</div>
</sec:authorize>
<div class="copyright">${notice}</div>
