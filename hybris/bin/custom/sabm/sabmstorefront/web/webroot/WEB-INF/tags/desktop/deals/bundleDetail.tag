<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ attribute name="deal" required="true" type="com.sabmiller.facades.deal.data.DealData"%>
<%@ attribute name="dealStatusIndex" required="true" type="java.lang.String"%>


<div class="panel-group" role="tablist" aria-multiselectable="true">
	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="header-item${dealStatusIndex}">
			<span>
				<a class="js-toggleDetails collapsed" role="button" data-toggle="collapse" href="#deal-item${dealStatusIndex}" aria-expanded="true">
					<span class="hidden"><spring:theme code="text.deals.list.hide.details" /></span>
					<span><spring:theme code="text.deals.list.view.details" /></span>
				</a>
			</span>
		</div>
		
		<ycommerce:testId code="deals_item1_links">
			<div id="deal-item${dealStatusIndex}" class="topFacetValues panel-collapse collapse in" role="tabpanel">
				<div class="description">
					${deal.description}
				</div>
		        
				<div class="panel-body deal-item-table">
					<div class="row title">
						<!-- DEV need UPDATE-->
						<span class="col-md-3 col-sm-3 col-xs-6 trim-left"><spring:theme code="text.deals.list.title.item" /></span>
						<span class="col-md-5 col-sm-4 hidden-xs">&nbsp;</span>							
						<span class="col-md-2 col-sm-2 col-xs-3"><spring:theme code="text.deals.list.title.quantity" /></span>
						<span class="col-md-2 col-sm-3 col-xs-3 trim-right"><spring:theme code="text.deals.list.title.save" /></span>
					</div>
					<c:forEach items="${deal.dealConditionGroupData.dealConditions}" var="dealCondition">
									<c:url value="${dealCondition.product.url}" var="productUrl"></c:url>
					<c:set var="dealConditionDisplayFalg" value="" />			
				   <c:forEach items="${deal.dealConditionGroupData.dealBenefits}" var="dealBenefit">
				   		<c:choose>
							<c:when test="${dealBenefit.benefitType == 'FREEGOODSBENEFIT'}">
							<c:if test="${dealCondition.product.code  eq  dealBenefit.product.code }   ">
							<c:set var="dealConditionDisplayFalg" value="true" />		
				          </c:if> 
							</c:when>
							<c:otherwise>
							<c:set var="dealConditionDisplayFalg" value="true" />		
							</c:otherwise>
							</c:choose>			   
				   </c:forEach>
				   <c:if test="${empty dealConditionDisplayFalg }">			   
								<div class="row details">
									<a href="${productUrl}">
										<div class="col-md-3 col-sm-3 hidden-xs">
											<product:productPrimaryImage product="${dealCondition.product}" format="thumbnail"/>
										</div>
									</a>
									<div class="col-md-5 col-sm-4 col-xs-6 trim-left">
										<a href="${productUrl}">
											<span class="title"><strong>${dealCondition.product.name}</strong></span>
											<span class="subTitle">${dealCondition.product.packConfiguration}</span>
										</a>
									</div>	
									<span class="col-md-2 col-sm-2 col-xs-3"><fmt:parseNumber integerOnly="true" value="${dealCondition.minQty}" /></span>
									<span class="col-md-2 col-sm-3 col-xs-3"><spring:theme code="text.deals.bundles.info.rod"/></span>
								</div>											   
				   </c:if>
					</c:forEach>
					
					
					<c:forEach items="${deal.dealConditionGroupData.dealBenefits}" var="dealBenefit">
						<c:choose>
							<c:when test="${dealBenefit.benefitType == 'FREEGOODSBENEFIT'}">
								<c:url value="${dealBenefit.product.url}" var="productUrl"></c:url>
								<div class="row details">
									<a href="${productUrl}">
										<div class="col-md-3 col-sm-3 hidden-xs">
											<product:productPrimaryImage product="${dealBenefit.product}" format="thumbnail"/>
										</div>
									</a>
									<div class="col-md-5 col-sm-4 col-xs-6 trim-left">
										<a href="${productUrl}">
											<span class="title"><strong>${dealBenefit.product.name}</strong></span>
											<span class="subTitle">${dealBenefit.product.packConfiguration}</span>
										</a>
									</div>	
									<span class="col-md-2 col-sm-2 col-xs-3"><fmt:parseNumber integerOnly="true" value="${dealBenefit.quantity}" /></span>
									<span class="col-md-2 col-sm-3 col-xs-3"><spring:theme code="deal.page.free" /></span>
								</div>
							</c:when>
							<c:when test="${dealBenefit.benefitType == 'DISCOUNTBENEFIT'}">
								<c:if test="${deal.dealConditionGroupData.dealConditions[0].conditionType == 'PRODUCTCONDITION'}">
									<c:set value="${deal.dealConditionGroupData.dealConditions[0].product}" var="conditionProduct"/>
									<c:url value="${conditionProduct.url}" var="productUrl"></c:url>
									<div class="row details">
										<a href="${productUrl}">
											<div class="col-md-3 col-sm-3 hidden-xs">
												<product:productPrimaryImage product="${conditionProduct}" format="thumbnail"/>
											</div>
										</a>
										<div class="col-md-5 col-sm-4 col-xs-6">
											<a href="${productUrl}">
												<span class="title"><strong>${conditionProduct.name}</strong></span>
												<span class="subTitle">${conditionProduct.packConfiguration}</span>
											</a>
										</div>							
										<span class="col-md-2 col-sm-2 col-xs-3">${deal.dealConditionGroupData.dealConditions[0].minQty}</span>
										<c:choose>
											<c:when test="${dealBenefit.currency==false}">
												<deals:dealNotCurrency formatAmount="${dealBenefit.amount}" />
												<fmt:formatNumber value="${dealBenefit.amount}"
													maxFractionDigits="0" var="amount" />
												<c:set value="${amount}%" var="dealBenefitInfo" />
											</c:when>
											<c:when test="${dealBenefit.currency==true}">
												<fmt:formatNumber value="${dealBenefit.amount}" pattern="0.00" var="amount" scope="session"/>
												<c:set value="$${amount}" var="dealBenefitInfo" />
											</c:when>
										</c:choose>
										<span class="col-md-2 col-sm-3 col-xs-3">${dealBenefitInfo}</span>
									</div>
								</c:if>
							</c:when>
						</c:choose>
					</c:forEach>
				</div>
			</div>
		</ycommerce:testId>
		
	</div>
</div>