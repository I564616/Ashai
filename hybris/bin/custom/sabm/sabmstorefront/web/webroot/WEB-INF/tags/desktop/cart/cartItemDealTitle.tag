<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="entry" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryData"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<div id="js-see-deal-title" class="deal-title">
	<c:forEach items="${entry.dealTitle}" var="dealTitle" varStatus="loop">
		<span>${dealTitle.dealTitle}</span>
		<c:if test="${not loop.last}"><br></c:if>
	</c:forEach>
</div>
<c:if test='${entry.isLimitedExceed}'>
	<span>All qty in excess of the deal maximum will not be eligible for any discounts from CUB</span>
</c:if>