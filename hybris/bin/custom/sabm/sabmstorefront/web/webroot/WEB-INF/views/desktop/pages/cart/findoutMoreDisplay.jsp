<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<%-- SABMC-634 -- The notification box should not be shown if the user has visited the deals page during their session  --%>
<c:if test="${not empty deals or hasUpcomingDeals eq true}">
	<c:choose>	
		<c:when test="${cartDealsData.partial ne null && ((not empty cartDealsData.partial.deals) or (not empty cartDealsData.partial.conflicts))}">
		<%-- SABMC-634 -- To be --If there are still partially qualified deals for the customer then the notification box in SABMC-400 should be displayed instead   
			See deals you are close to qualifying for --%>
			<div class="col-md-12 margin-top-20 offset-bottom-medium cart-cta" ng-show="partialQualDeals.deals.length != 0 || partialQualDeals.conflicts.length != 0">
				<div class="cart-notify">
					<div class="icon">
						<svg class="icon-dollar"><use xlink:href="#icon-dollar"></use></svg>
					</div>
					<div class="info single">
						<spring:theme code="text.cart.dealtag.title" />
					</div>
					<cart:findOutMoreButton />
					<div class="clearfix"></div>
				</div>
			</div>
		</c:when>
		<c:when test="${visitedDealsPage ne true}">
		<%-- SABMC-634 -- To be - notification box in SABMC-634 should be displayed 
			Visit the deal page to see great offers available to you.
			Don't worry the products you've already selected will still be here when you're ready to checkout. --%>		
			<div class="col-md-12 margin-top-20 offset-bottom-medium cart-cta">
				<div class="cart-notify">
					<div class="icon">
						<svg class="icon-dollar"><use xlink:href="#icon-dollar"></use></svg>
					</div>
					<div class="info">
						<spring:theme code="text.cart.dealtag.title2" /><br/>
						<spring:theme code="text.cart.dealtag.title3" />						
					</div>
					<c:url value="/deals" var="findoutmore" />
					<%--Deleted for googleTagManager--%>
					<%--<a href="${findoutmore}" onclick="trackDealsPageFromCart()" class="btn btn-primary"><spring:theme code="text.cart.button.findmore" /></a>--%>
					<a href="${findoutmore}" class="btn btn-primary"><spring:theme code="text.cart.button.findmore" /></a>
					<div class="clearfix"></div>
				</div>
			</div>
		</c:when>
	</c:choose>
</c:if>
		<div id="minFreight">
		<c:if test="${cartData.freightSurcharge.value > 0}">
		<%-- SABMC-1831 -- Display a prompt to the customer for minimum freight pricing to encourage upsell --%>		
			<div class="col-md-12 margin-top-20 offset-bottom-medium cart-cta">
				<div class="cart-notify">
					<div class="icon">
						<svg class="icon-warning"><use xlink:href="#icon-warning"></use></svg>
					</div>
					<div class="info single short">
					<spring:theme code="text.cart.freight.discount" arguments="${cartData.freightSurcharge.formattedValue}"/>			
					</div>
					<c:url value="/Beer/c/10" var="beerpage" />
					<a href="${beerpage}" class="btn btn-primary"><spring:theme code="text.cart.continue.shopping" /></a>
					<div class="clearfix"></div>
				</div>
			</div>
		</c:if>
		</div>


