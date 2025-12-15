<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>

<%-- Information (confirmation) messages --%>
<c:if test="${not empty accConfMsgs}">
		<c:forEach items="${accConfMsgs}" var="msg">
			<div class="alert positive">
			x	<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
			</div>
		</c:forEach>
</c:if>

<%-- Warning messages --%>
<c:if test="${not empty accInfoMsgs}">
		<c:forEach items="${accInfoMsgs}" var="msg">
			<div class="alert neutral">
			y	<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
			</div>
		</c:forEach>
</c:if>

<%-- Error messages (includes spring validation messages)--%>
<c:if test="${not empty accErrorMsgs}">
		<c:forEach items="${accErrorMsgs}" var="msg">
			<div class="alert negative">
			z	<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
			</div>
		</c:forEach>
</c:if>