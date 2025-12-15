<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ attribute name="current" required="true" %>

<ul class="list-badge">
	<c:forEach items="1,2,3,4" var="num">
		<c:choose>
			<c:when test='${current lt num}'>
				<c:set var="cssClass" value="future" />
			</c:when>
			<c:when test='${current eq num}'>
				<c:set var="cssClass" value="current" />
			</c:when>
			<c:when test='${current gt num}'>
				<c:set var="cssClass" value="past" />
			</c:when>
		</c:choose>
		<li>
			<div class="badge badge-xsm badge-${cssClass}">
				${num}
			</div>
		</li>
	</c:forEach>
</ul>


