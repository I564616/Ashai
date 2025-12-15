<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>

<%--Recommendation welcome message--%>
<div class="recommendationwelcome-mobile hidden">
	<span>
		<b><spring:theme code="text.recommendations.recommended.welcome.message"/></b>
		<spring:theme code="text.recommendations.recommended.welcome.details"/>
	</span>
 </div>

<%-- Information (confirmation) messages --%>
<c:if test="${not empty accConfMsgs}">
	<c:forEach items="${accConfMsgs}" var="msg">
		<div class="alert positive"><spring:theme code="${msg.code}" arguments="${msg.attributes}"/></div>
	</c:forEach>
</c:if>
<%-- Warning messages --%>
<c:if test="${not empty accInfoMsgs}">
	<c:forEach items="${accInfoMsgs}" var="msg">
		<div class="alert neutral"><spring:theme code="${msg.code}" arguments="${msg.attributes}"/></div>
	</c:forEach>
</c:if>
<%-- Error messages (includes spring validation messages)--%>
<c:if test="${not empty accErrorMsgs}">
    <c:forEach items="${accErrorMsgs}" var="msg">
        <c:choose>
            <c:when test="${msg.code == 'basket.page.cutofftime.error.message' && not empty param.cutoffTimeoutError}">
            </c:when>
            <c:otherwise>
                <div class="alert negative"><spring:theme code="${msg.code}" arguments="${msg.attributes}"/></div>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</c:if>

<%-- Max order quantity error message --%>
<div class="order-error-message"></div>