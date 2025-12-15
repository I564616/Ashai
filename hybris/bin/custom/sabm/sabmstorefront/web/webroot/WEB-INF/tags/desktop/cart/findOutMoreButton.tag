<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>


<c:url value="/cart/findOutMore" var="findOutMoreUrl" scope="session" />

<form action="${findOutMoreUrl}" method="post" id="_sabmFindOutMoreForm" class="_sabmcreateUserForm">
	<input type="hidden" name="CSRFToken" value="${CSRFToken}">
</form>	

<%--Deleted for googleTagManager--%>
<%--<a href="#partiallyQualified" onclick="trackRelaunchPQDModal()" class="noclose-popup btn btn-primary"><spring:theme code="text.cart.button.findmore" /></a>--%>
<a href="#partiallyQualified" class="noclose-popup btn btn-primary"><spring:theme code="text.cart.button.findmore" /></a>

