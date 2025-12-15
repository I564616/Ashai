<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<!-- Get callback icon url -->
<spring:theme code="img.callback" text="/" var="callbackPath" />
<c:choose>
	<c:when test="${originalContextPath ne null}">
		<c:url value="${callbackPath}" context="${originalContextPath}" var="callbackUrl" />
	</c:when>
	<c:otherwise>
		<c:url value="${callbackPath}" var="callbackUrl" />
	</c:otherwise>
</c:choose>

<button type="button" id="callbackPopupButton" style="display:none">
    <img id="callbackButtonImage" src="${callbackUrl}">
    <spring:theme code="contactus.callback.popup.send"/>
</button>