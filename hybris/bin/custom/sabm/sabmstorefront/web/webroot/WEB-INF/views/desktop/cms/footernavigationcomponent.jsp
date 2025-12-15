<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/desktop/common/footer"  %>

<div class="main-footer">
	<ul class="main-footer-list flex-center-tablet space-between">
			<c:if test="${navigationNode.visible}">
				<c:forEach items="${navigationNode.links}" var="childlink" >
					<li class="links <c:if test="${fn:toLowerCase(childlink.linkName) == 'business enquiry' || fn:toLowerCase(childlink.linkName) == 'contact us'}">bde-view-only</c:if>">
						<cms:component component="${childlink}" evaluateRestriction="true" element="li"/>
					</li>
				</c:forEach>
			</c:if>		
	</ul>
</div>
<div class="copyright">${notice}
<div>
<spring:theme code="footer.sabm.paragraph" />
</div>
</div>