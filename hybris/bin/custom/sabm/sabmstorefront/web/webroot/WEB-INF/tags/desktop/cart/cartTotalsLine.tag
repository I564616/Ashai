<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="cssClass" required="false" type="java.lang.String"%>
<%@ attribute name="forceShow" required="false" type="java.lang.Boolean"%>
<%@ attribute name="priceData" required="true" type="de.hybris.platform.commercefacades.product.data.PriceData" %>
<%@ attribute name="discount" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<c:if test="${forceShow or not empty priceData and (priceData.value > 0 or (discount and priceData.value < 0))}">
	<div class="row">
		<div class="${cssClass}"><spring:theme code="${label}" /></div>
		<div class="col-xs-4">${priceData.formattedValue}</div>
	</div>
</c:if>