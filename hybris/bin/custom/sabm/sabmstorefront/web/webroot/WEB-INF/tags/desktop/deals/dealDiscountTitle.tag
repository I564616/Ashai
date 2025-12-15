<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%@ attribute name="dealCondition" required="true" type="com.sabmiller.facades.deal.data.DealConditionData"%>
<%@ attribute name="dealBenefit" required="true" type="com.sabmiller.facades.deal.data.DealBenefitData"%>

<c:set var="title" value="${dealCondition.product.name}" />
<c:set var="subtitle" value="${dealCondition.product.packConfiguration}" />

<c:choose>	
	<c:when test="${dealCondition.minQty<=1 and dealBenefit.currency=='false' }">		
		<deals:dealNotCurrency formatAmount="${dealBenefit.amount }"/>
		<spring:theme code="deal.page.title.discount" arguments="${dealCondition.minQty },${fn:toLowerCase(dealCondition.unit.name)},${title },${subtitle },${amount }"/>
	</c:when>
	<c:when test="${dealCondition.minQty<=1 and dealBenefit.currency=='true' }">
		<deals:dealCurrency formatAmount="${dealBenefit.amount  }"/>
		<spring:theme code="deal.page.title.discount.currency" arguments="${dealCondition.minQty },${fn:toLowerCase(dealCondition.unit.name) },${title},${subtitle },${amount },${fn:toLowerCase(dealCondition.unit.name) }"/>
	</c:when>
	<c:when test="${dealCondition.minQty>=2 and dealBenefit.currency=='false' }">
		<deals:dealNotCurrency formatAmount="${dealBenefit.amount }"/>
		<spring:theme code="deal.page.title.discount.more" arguments="${dealCondition.minQty },${fn:toLowerCase(dealCondition.unit.name)},${title },${subtitle },${amount }"/>
	</c:when>
	<c:when test="${dealCondition.minQty>=2 and dealBenefit.currency=='true' }">
		<deals:dealCurrency formatAmount="${dealBenefit.amount  }"/>
		<spring:theme code="deal.page.title.discount.currency.more" arguments="${dealCondition.minQty },${fn:toLowerCase(dealCondition.unit.name)},${title },${subtitle },${amount },${fn:toLowerCase(dealCondition.unit.name) }"/>
	</c:when>
</c:choose>