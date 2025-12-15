<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<li class="La ${component.styleClass} ${dropDownLayout} <c:if test="${not empty component.navigationNode.children}"> parent</c:if>">
	<cms:component component="${component.link}" evaluateRestriction="true"/>
	<c:if test="${not empty component.navigationNode.children}">
		<ul class="Lb">
			<c:forEach items="${component.navigationNode.children}" var="child">
				<c:if test="${child.visible}">
					<li class="Lb">
						<span class="nav-submenu-title">${child.title}</span>
						<c:forEach items="${child.links}" step="${component.wrapAfter}" varStatus="i">
							<ul class="Lc ${i.count < 2 ? 'left_col' : 'right_col'}">
								<c:forEach items="${child.links}" var="childlink" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
									<cms:component component="${childlink}" evaluateRestriction="true" element="li" class="Lc ${i.count < 2 ? 'left_col' : 'right_col'}"/>
								</c:forEach>
							</ul>
						</c:forEach>
					</li>
				</c:if>
			</c:forEach>
		</ul>
	</c:if>
</li>