<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" >
<c:if test="${not empty defaultB2BUnitCode && showUnitDetail}">
	<div class="addPaddingMultiAccount add-nav-bottom-padding"><a href="<c:url value='/multiAccount'/>"><strong>${defaultB2BUnitCode} - ${fn:substring(defaultB2BUnitName, 0, 20)}
		<c:if test="${fn:length(defaultB2BUnitName) gt 20}">...</c:if>
		</strong></a>&nbsp;&nbsp;|</div>
</c:if>
</sec:authorize>