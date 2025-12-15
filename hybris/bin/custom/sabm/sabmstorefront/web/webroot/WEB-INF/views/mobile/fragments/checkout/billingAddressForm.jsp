<%@ taglib prefix="address" tagdir="/WEB-INF/tags/mobile/address"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:if test="${not empty country}">
	<form:form modelAttribute="sopPaymentDetailsForm">
		<address:billingAddressFormElements regions="${regions}"
		                             country="${country}" tabindex="12"/>
	</form:form>
</c:if>
