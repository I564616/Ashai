<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/desktop/checkout/multi" %>


<div class="CartItems">
	<div class="cart">
		<div class="cart-headers visible-md-block visible-lg-block clearfix">
			<div class="col-md-5">
				<div class="row">
					<div class="col-md-3 visible-md-block visible-lg-block"><spring:theme code="basket.page.image" /></div>
					<div class="col-md-9 trim-left-5-lg"><spring:theme code="basket.page.product" /></div>
				</div>
			</div>
			<div class="col-md-7">
				<div class="row">
					<div class="col-xs-12 col-md-3 visible-md-block visible-lg-block text-right"><spring:theme code="basket.page.customer.unit.price" /></div>
					<div class="col-md-9">
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
			<c:set var="count" value="${1}"> </c:set>
			<c:set var="free" value="${0}"> </c:set>
			<c:forEach items="${cartData.entries}" var="entry">
				<c:choose>
					<c:when test="${not entry.isFreeGood}">
						<c:if test="${entry.deliveryPointOfService == null}">
							<multi-checkout:deliveryCartItem entry="${entry}" count="${count}" />
						</c:if>
					</c:when>
					<c:otherwise>
						<c:set var="free" value="${free+1}"> </c:set>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>


	
			<c:if test="${free > 0}">
				<div class="cart-headers visible-md-block visible-lg-block clearfix">
					<div class="col-xs-12">
						Bonus Products
					</div>
				</div>
			
				<div class="cart-body">
					<c:forEach items="${cartData.entries}" var="entry">
							<c:if test="${entry.isFreeGood}">
								<multi-checkout:deliveryCartItem entry="${entry}" count="${count}" />
							</c:if>
					</c:forEach>
				</div>
			</c:if>
		</div>
	
</div>
