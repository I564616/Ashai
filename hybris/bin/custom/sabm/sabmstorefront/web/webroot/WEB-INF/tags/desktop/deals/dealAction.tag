<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="deal" required="true" type="com.sabmiller.facades.deal.data.DealData"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ attribute name="parentSection" required="true" type="java.lang.String" %>
<%@ attribute name="dealStatusIndex" required="true" type="java.lang.String"%>
<%@ attribute name="prefix" required="true" type="java.lang.String"%>

<div class="row actions">
	<div class="col-md-5 col-sm-5 col-xs-12 valid-date"><spring:theme code="deal.page.valid.time" arguments="${deal.validFrom},${deal.validTo}"/></div>
	<div class="col-md-4 col-sm-4 col-xs-12 trim-right-5-lg uom-area">
		<div class="row">
			<div class="col-md-6 col-sm-8 col-xs-6 "> 
			
				<c:set value="${deal.dealConditionGroupData.dealConditions[0]}" var="dealCondition"/>
				<c:set value="${dealCondition.minQty}" var="dealMinQty"/>
				<c:if test="${dealCondition.conditionType eq 'PRODUCTCONDITION'}">
					<c:url value="/cart/add" var="addToCartUrl" />
					<ycommerce:testId code="searchPage_addToCart_button_${dealCondition.product.code}">
						<form:form id="${prefix}AddToCartForm${dealCondition.product.code}${dealStatusIndex}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
							<input type="hidden" name="productCodePost" value="${dealCondition.product.code}" />
							<input type="hidden" name="unit" value="${dealCondition.unit.code}"/>
							<input type="hidden" name="qty" class="qty" value="${dealMinQty}" base-qty="${dealMinQty}"/>
						</form:form>
					</ycommerce:testId>
				</c:if>
			
				<input type="hidden" class="dealType" value="${parentSection}">
				<ul class="select-quantity select-quantity-sm">
					<li class="down disabled">
						<svg class="icon-minus">
						    <use xlink:href="#icon-minus"></use>    
						</svg>
					</li>
					<li>
						<c:choose>
							<c:when test="${parentSection=='discount'}">
								<input type="hidden" class="minQty" value="${dealMinQty}">
								<input class="qty-input"  type="tel" value="${dealMinQty}" data-minqty="${dealMinQty}" maxlength="3"  pattern="\d*">
							</c:when>
							<c:when test="${parentSection=='bundle'}">
								<input type="hidden" class="minQty" value="1">
								<input class="qty-input"  type="tel" value="1" data-minqty="1" maxlength="3"  pattern="\d*">
							</c:when>
						</c:choose>
					</li>
					<li class="up">
						<svg class="icon-plus">
						    <use xlink:href="#icon-plus"></use>    
						</svg>
					</li>
				</ul>
			</div>
			
			<div class="col-md-6 col-sm-4 col-xs-6 flex-center-tablet deal-text-cases trim-left-5 trim-right-5">
				<c:if test="${parentSection=='discount'}">
					${deal.dealConditionGroupData.dealConditions[0].unit.name}
				</c:if>
				<c:if test="${parentSection=='bundle'}">
					<spring:theme code="text.deals.list.no.deals" />
				</c:if>
			</div>
		</div>
	</div>
	<div class="col-md-3 col-sm-3  col-xs-12">
		<span class="btn btn-primary btn-block addToCartButton"><spring:theme code="basket.add.to.basket" /></span>
	</div>
</div>
