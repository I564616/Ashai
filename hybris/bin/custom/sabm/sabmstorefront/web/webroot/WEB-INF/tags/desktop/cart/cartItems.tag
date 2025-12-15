<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData"%>
<%@ attribute name="entryOfferInfoData" type="de.hybris.platform.commercefacades.order.data.EntryOfferInfoData"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>

<cart:dealTitleList cartData="${cartData}"/>

<div id="cartItems" class="clear">
	<!-- <a href="#dealsConflictPopup" class="noclose-popup">Launch conflict deals</a> -->
	<input type="hidden" id="cartRemoveUrl" value="${cartRemoveUrl}"> 
	<input type="hidden" id="cartUpdateQuantityUrl" value="${cartUpdateQuantityUrl}">
	<input type="hidden" id="cartUnChangeTime" value='${cartUnChangeTime}' />
	<div class="cart">
	
	<%-- Paid Products --%>
		<div class="cart-headers visible-md-block visible-lg-block clearfix">
			<div class="col-md-4 cart-mob-row-1">
				<div class="row">
					<div class="col-md-3 visible-md-block visible-lg-block"><spring:theme code="basket.page.image" /></div>
					<div class="col-md-9 trim-left-5-lg"><spring:theme code="basket.page.product" /></div>
				</div>
			</div>
			<div class="col-md-8">
				<div class="row">
          <div class="col-md-5 cart-mob-row-2 ">
            <div><spring:theme code="basket.page.customer.unit.price" /></div>
            <div class="text-center">Total <br><spring:theme code="basket.page.customer.unit.wet" /></div>
            <div class="text-center">Total <br><spring:theme code="basket.page.customer.unit.deposit" /></div>
          </div>
					<div class="col-md-7 cart-mob-row-3">
						<div class="row">
							<div class="col-xs-6 col-md-4 trim-right-5-lg text-center">
								<span id="header4" class=""><spring:theme code="basket.page.quantity" /></span>
							</div>
							<div class="col-xs-6 col-md-4 trim-left-5-lg trim-right-5-lg text-center"><spring:theme code="basket.page.unit.of.measure" /></div>
							<div class="col-xs-12 col-md-4 trim-left-5-lg trim-right-5-lg text-center">
								<span id="header5" class=""><spring:theme code="basket.page.subtotal" />
								<br><spring:theme code="basket.page.excludegst" /></span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="cart-body">
			
			<c:set var="free" value="${0}"> </c:set>
			<c:forEach items="${cartData.entries}" var="entry" varStatus="loop">
				<c:choose>
					<c:when test="${not entry.isFreeGood}">
						<cart:cartItem entry="${entry}" index="${loop.index}"  />
					</c:when>
					<c:otherwise>
						<c:set var="free" value="${free+1}"> </c:set>
					</c:otherwise>
				</c:choose>

			</c:forEach>
		</div>

		<%-- Bonus Products --%>
		<c:if test="${free > 0}">
			<div class="cart-headers visible-md-block visible-lg-block clearfix">
				<div class="col-xs-12">
					Bonus Products
				</div>
			</div>
		
			<div class="cart-body">
				<c:forEach items="${cartData.entries}" var="entry" varStatus="loop">
					<c:if test="${entry.isFreeGood}">
						<cart:cartItem entry="${entry}" index="${loop.index}"/>
					</c:if>
				</c:forEach>
			</div>
		</c:if>
	</div>
</div>