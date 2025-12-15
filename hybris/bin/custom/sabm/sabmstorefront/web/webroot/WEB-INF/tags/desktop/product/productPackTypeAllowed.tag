<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="unit" required="true" type="java.lang.String"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set value="${fn:toUpperCase(unit)}" var="productPackType"/>
<c:if test="${productPackType ne 'KEG'}">
	<c:set value="PACK" var="productPackType"/>
</c:if>

<c:set value="true" var="isProductPackTypeAllowed" scope="request"/>
<c:if test="${!fn:containsIgnoreCase(deliveryDatePackType, productPackType)}">
	<c:set value="false" var="isProductPackTypeAllowed" scope="request"/>
</c:if>
