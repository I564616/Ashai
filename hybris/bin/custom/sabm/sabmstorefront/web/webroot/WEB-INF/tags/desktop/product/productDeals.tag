<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>


<section class="deals-listing clearfix">
	<c:forEach items="${product.deals }" var="deal" varStatus="loop">
		<c:if test="${deal.dealType == 'DISCOUNT' and fn:length(deal.dealConditionGroupData.dealConditions) == 1 and fn:length(deal.dealConditionGroupData.dealBenefits) == 1}">
			<c:set value="${deal.dealConditionGroupData.dealConditions[0] }" var="dealCondition"/>
			<c:set value="${deal.dealConditionGroupData.dealBenefits[0] }" var="dealBenefit"/>
			<div class="deal-row deal-item clearfix">
				<div class="row">
			        <div class="col-xs-12 col-sm-2">
			        	<div class="row">
			        		<div class="col-xs-3"><div class="badge badge-red badge-md pull-left"><spring:theme code="text.deals.deals"/></div></div>
			        	</div>
			        </div>
			        <div class="col-sm-10 trim-left-lg">
			        	<div class="row">
			        		<div class="col-xs-12"><div class="deal-info margin-top-10-xs offset-bottom-x-small">
			        			<h3>
			        				<deals:dealDiscountTitle dealCondition="${dealCondition }" dealBenefit="${dealBenefit }"/>
			        			</h3>
			        			<span class="readmore-2">
			        				${deal.description}
			        			</span>
			            	</div></div>
			        	</div>
			            <div class="row">
			                <div class="col-xs-12 col-sm-5 col-md-6 col-md-offset-0 offset-bottom-small">
			                	<span class="deal-date"><spring:theme code="deal.page.valid.time" arguments="${deal.validFrom},${deal.validTo}"/></span>
			                </div>
			                <div class="col-xs-12 col-sm-7 col-md-6">
			                    <div class="row">
			                    	<div class="col-xs-6 col-sm-5 col-md-4 trim-left-5-lg trim-right-5-lg">
			                    	
			                    		<c:set value="discount" var="parentSection"/>
											<c:set value="${dealCondition.minQty}" var="dealMinQty"/>
											<c:if test="${dealCondition.conditionType eq 'PRODUCTCONDITION'}">
												<c:url value="/cart/add" var="addToCartUrl" />
												<ycommerce:testId code="searchPage_addToCart_button_${dealCondition.product.code}">
													<form:form id="discountDealAddToCartForm${dealCondition.product.code}${loop.index}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
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
															<input class="qty-input" type="tel" value="${dealMinQty}" data-minqty="${dealMinQty}" maxlength="3"  pattern="\d*">
														</c:when>
														<c:when test="${parentSection=='bundle'}">
															<input type="hidden" class="minQty" value="1">
															<input class="qty-input" type="tel" value="1" data-minqty="1" maxlength="3"  pattern="\d*">
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
			                        <div class="col-xs-6 col-sm-2 col-md-4 trim-right-5-lg deal-date">
			                        	${dealCondition.unit.name }
			                        </div>
			                        <div class="col-xs-12 col-sm-5 col-md-4 trim-left-5-lg margin-top-20-xs"><span class="btn btn-primary btn-block addToCartButton"><spring:theme code="basket.add.to.basket"/></span></div>
			                    </div>
			                </div>
			            </div>
			        </div>
			    </div>
			</div>
		</c:if>
	</c:forEach>
	
	<c:forEach items="${product.deals }" var="deal" varStatus="dealStatus">
		<c:if test="${deal.dealType == 'BOGOF' or fn:length(deal.dealConditionGroupData.dealConditions) > 1 or fn:length(deal.dealConditionGroupData.dealBenefits) > 1}">
			<c:set value="0" var="dealBenefitCount"/>
			<c:set value="" var="dealBenefitInfo"/>
			<c:set value="" var="dealConditionInfo"/>
			<c:set value="${deal.dealConditionGroupData.dealConditions }" var="dealConditions"/>
			<c:set value="${deal.dealConditionGroupData.dealBenefits }" var="dealBenefits"/>
			<div class="deal-row bundle-deal clearfix">
	        	<div class=" deal-item row">
	        		<div class="col-xs-12 col-sm-2"><div class="badge badge-red badge-md pull-left"><spring:theme code="text.deals.deals"/></div></div>
	        		<div class="col-xs-12 col-sm-10 margin-top-10-xs">
	        			<div class="row">
		        			<div class="deal-info"><h3>
								<deals:dealBundleTitle dealBenefits="${dealBenefits }" dealConditions="${dealConditions }" freeGoodFlag=""/>															        			
			        			</h3>
			        			<span class="readmore-2">
		        					${deal.description }
		        				</span>
		            		</div>
	            		</div>
	            		<div class="row">
	            			<div class="col-xs-12 col-sm-5 col-md-6 col-md-offset-0 offset-bottom-small trim-left-md trim-left-lg">
	            				<span class="deal-date"><spring:theme code="deal.page.valid.time" arguments="${deal.validFrom},${deal.validTo}"/></span>
	            			</div>
			                <div class="col-xs-12 col-sm-7 col-md-6 margin-top-10-xs">
			                    <div class="row">
			                    	<div class="col-xs-6 col-sm-5 col-md-4 trim-left-lg trim-right-5-lg">
			                    	
			                    	<c:set value="bundle" var="parentSection"/>
			                    	<c:set value="${deal.dealConditionGroupData.dealConditions[0]}" var="dealCondition"/>
			                           <c:set value="${dealCondition.minQty}" var="dealMinQty"/>
									<c:if test="${dealCondition.conditionType eq 'PRODUCTCONDITION'}">
										<c:url value="/cart/add" var="addToCartUrl" />
										<ycommerce:testId code="searchPage_addToCart_button_${dealCondition.product.code}">
											<form:form id="bundleDealAddToCartForm${dealCondition.product.code}${dealStatus.index}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
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
													<input class="qty-input" type="tel" value="${dealMinQty}" data-minqty="${dealMinQty}" maxlength="3"  pattern="\d*">
												</c:when>
												<c:when test="${parentSection=='bundle'}">
													<input type="hidden" class="minQty" value="1">
													<input class="qty-input" type="tel" value="1" data-minqty="1" maxlength="3"  pattern="\d*">
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
			                        <div class="col-xs-6 col-sm-2 col-md-4 trim-right-5-lg deal-date">
			                        	<spring:theme code="text.deals.list.no.deals" />
			                        </div>
			                        <div class="col-xs-12 col-sm-5 col-md-4 trim-left-5-lg margin-top-10-xs"><span class="btn btn-primary btn-block addToCartButton"><spring:theme code="basket.add.to.basket"/></span></div>
			                    </div>
			                </div>
	            			
	            		</div>
	            		<div class="row pdp-bundle-detail accordion-inner">
	            			<deals:bundleDetail deal="${deal}" dealStatusIndex="${dealStatus.index}"/>
	            		</div>
	            	</div>
	            	
	        	</div>
		        
				
				
			</div>
		</c:if>
	</c:forEach>
<input type="hidden" id="showMore" value="<spring:theme code="deal.page.show.more"/>">
<input type="hidden" id="showLess" value="<spring:theme code="deal.page.show.less"/>"/>
<div class="pull-right btn-inline toggle-deals"><span id="hiddenDeals"></span>
<svg class="icon-arrow-down"><use xlink:href="#icon-arrow-down"></use></svg>
</div>
</section>