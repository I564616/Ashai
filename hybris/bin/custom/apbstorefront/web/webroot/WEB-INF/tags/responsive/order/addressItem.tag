<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="address" required="true" type="de.hybris.platform.commercefacades.user.data.AddressData" %>
<%@ attribute name="storeAddress" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:if test="${not storeAddress }">
	<c:if test="${not empty address.companyName}">
	   ${address.companyName}<br>
	</c:if>
	<c:if test="${not empty address.title}">
	    ${fn:escapeXml(address.title)}&nbsp;
	</c:if>
	<c:if test="${not empty address.firstName}">
		${fn:escapeXml(address.firstName)}&nbsp;${fn:escapeXml(address.lastName)}
	</c:if>
</c:if>
	<c:if test="${not empty address.line1}">
		<br> ${fn:escapeXml(address.line1)}&nbsp;${fn:escapeXml(address.line2)}
	</c:if>
		
 <c:if test="${not empty address.town}">
	<br>
	${fn:escapeXml(address.town)}&nbsp;${fn:escapeXml(address.region.name)}
	</c:if>
	<c:if test="${not empty address.country.name}">
	<br>
	${fn:escapeXml(address.postalCode)}
	</c:if>
<br />
${fn:escapeXml(address.phone)}
