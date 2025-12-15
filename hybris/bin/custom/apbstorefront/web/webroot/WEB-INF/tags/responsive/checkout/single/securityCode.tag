<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spring:url value="/my-account/saved-cards" var="savedCardsUrl" htmlEscape="false"/>

<div class="card-security-code">
<div class="security-code-heading">
	<span class="glyphicon facet__arrow"></span>
	<spring:theme code="checkout.security.code.heading" />
</div>
<div class="security-code-content">
	<div class="media">
		<div class="media-left">
			<img src="${commonResourcePath}/images/CVV.png"/>
		</div>
		<div class="media-body">
			<spring:theme code="checkout.security.code.imagecontent" />
		</div>
	</div>
</div>
</div>