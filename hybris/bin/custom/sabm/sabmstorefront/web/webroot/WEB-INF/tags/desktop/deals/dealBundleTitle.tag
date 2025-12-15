<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%@ attribute name="dealConditions" required="true" type="java.util.List"%>
<%@ attribute name="dealBenefits" required="true" type="java.util.List"%>
<%@ attribute name="freeGoodFlag" required="true" type="String"%>

<c:set value="0" var="dealBenefitCount"/>
<c:set value="" var="dealBenefitInfo"/>
<c:set value="" var="dealConditionInfo"/>
<spring:theme code="text.deals.bundles.info.buy" var="buy"/>
<spring:theme code="text.deals.bundles.info.off" var="off"/>
<spring:theme code="text.deals.bundles.info.per" var="per"/>
<spring:theme code="text.deals.bundles.info.free" var="free"/>
<spring:theme code="text.deals.bundles.info.of" var="of"/>
<spring:theme code="text.deals.bundles.info.receive" var="receive"/>
<spring:theme code="text.deals.bundles.info.and" var="bundand"/>
<spring:theme code="text.deals.bundles.info.plural"  var="plural"/>

${buy}
<c:set value="${fn:length(dealConditions)}" var="dealConditionCount" />
<%-- add dealCondition info  --%>
<c:forEach items="${dealConditions}" var="dealCondition" varStatus="loop">
	<c:set var="productName" value="${dealCondition.product.name}&nbsp;${dealCondition.product.packConfiguration}" />
	<%--The minQty is greater than 1  then add plural--%>
	<c:if test="${dealCondition.minQty > '1'}">
		<c:set value="${plural}" var="dealunits" />
	</c:if>
	<c:choose>
	<%-- If  dealConditionCount greater than 1 or freeGoodFlag is not empty then add  link for product--%>
		<c:when test="${dealConditionCount > '1' or  not empty freeGoodFlag}">
			<c:set
				value="${dealConditionInfo}<a href='${dealCondition.product.url}'>${dealCondition.minQty }&nbsp;${fn:toLowerCase(dealCondition.unit.name) }${dealunits}&nbsp;${of}&nbsp;${productName }</a>"
				var="dealConditionInfo" />
		</c:when>
		<c:otherwise>
		<c:set value="${dealCondition.product.code}" var="dealConditionProductCode" />
			<c:set
				value="${dealConditionInfo}${dealCondition.minQty }&nbsp;${fn:toLowerCase(dealCondition.unit.name) }${dealunits}&nbsp;${of}&nbsp;${productName }"
				var="dealConditionInfo" />
		</c:otherwise>
	</c:choose>
	<%-- Set the product code , for use in the bellow --%>
	<c:if test="${dealConditionCount == '1' or freeGoodFlag}">
		<c:set value="${dealCondition.product.code}" var="dealConditionProductCode" />
	</c:if>
	<c:if test="${loop.index+1 != dealConditionCount}">
		<c:set value="${dealConditionInfo}&nbsp;${bundand}&nbsp;" var="dealConditionInfo" />
	</c:if>
</c:forEach>
<c:set value="${fn:length(dealBenefits)}" var="dealBenefitCount" />
<%-- add dealBenefitCount info  --%>
<c:forEach items="${dealBenefits}" var="dealBenefit" varStatus="loop">

	<c:choose>
	<%-- If the benefitType is DISCOUNTBENEFIT then display DiscountDealBenefit info --%>
		<c:when test="${dealBenefit.benefitType=='DISCOUNTBENEFIT' }">
			<c:choose>
			<%--If the currency is false then use dealNotCurrency tag --%>
				<c:when test="${dealBenefit.currency==false }">
					<deals:dealNotCurrency formatAmount="${dealBenefit.amount }" />
					<c:set value="${dealBenefitInfo}${amount}% ${off}"
						var="dealBenefitInfo" />
				</c:when>
				<%--If the currency is true then use dealCurrency tag --%>
				<c:when test="${dealBenefit.currency==true }">
					<deals:dealCurrency formatAmount="${dealBenefit.amount }" />
					<c:set
						value="${dealBenefitInfo}$${amount} ${off} ${per} ${fn:toLowerCase(dealBenefit.unit.name)}"
						var="dealBenefitInfo" />
				</c:when>
			</c:choose>
		</c:when>
      <%-- If the benefitType is FREEGOODSBENEFIT then display FreeGoodsDealBenefit info --%> 
		<c:when test="${dealBenefit.benefitType=='FREEGOODSBENEFIT' }">
		<fmt:formatNumber value="${dealBenefit.quantity}" maxFractionDigits="0" var="quantity" />
		<c:choose>
		<%--The quantity is greater than 1  then add plural--%>
		<c:when
			test="${quantity > '1'}">
			 <c:set value="${plural}" var="dealBeneunits" />
		</c:when>
		<c:otherwise>
      <c:set value="" var="dealBeneunits" />
		</c:otherwise>
	    </c:choose>
			<c:choose>
			<%--If the dealBenefit.product.code equal dealCondition.product.code then not display the product info--%>
				<c:when
					test="${dealBenefit.product.code == dealConditionProductCode}">
					<fmt:formatNumber value="${dealBenefit.quantity}"
						maxFractionDigits="0" var="quantity" />
					<c:set
						value="${dealBenefitInfo} ${quantity} ${fn:toLowerCase(dealBenefit.unit.name)}${dealBeneunits} ${free} "
						var="dealBenefitInfo" />
				</c:when>
				<c:otherwise>
					<c:set var="productName" value="${dealBenefit.product.name}&nbsp;${dealBenefit.product.packConfiguration}" />
					<fmt:formatNumber value="${dealBenefit.quantity}"
						maxFractionDigits="0" var="quantity" />
					<c:set
						value="${dealBenefitInfo}<a href='${dealBenefit.product.url}'>${quantity} ${fn:toLowerCase(dealBenefit.unit.name)}${dealBeneunits} ${of} ${productName}</a> ${free}"
						var="dealBenefitInfo" />
				</c:otherwise>
			</c:choose>
		</c:when>
	</c:choose>
	<c:if test="${loop.index+1 != dealBenefitCount}">
		<c:set value="${dealBenefitInfo}&nbsp;${bundand}&nbsp;"
			var="dealBenefitInfo" />
	</c:if>
</c:forEach>

${dealConditionInfo}&nbsp;${receive}${dealBenefitInfo}
