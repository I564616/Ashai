<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:if test="${not empty cartData.appliedOrderPromotions}">
	<div class="ui-grid-a itemPromotionBox" data-theme="b" data-role="content">
		<div class="ui-grid-a" data-theme="b">
			<h2>
				<spring:theme code="basket.received.promotions" />
			</h2>
		</div>
		<div class="ui-grid-a" data-theme="b">
			<ycommerce:testId code="cart_recievedPromotions_labels">
				<ul>
					<c:forEach items="${cartData.appliedOrderPromotions}" var="promotion">
						<li class="cart-promotions-applied">${promotion.description}</li>
					</c:forEach>
				</ul>
			</ycommerce:testId>
		</div>
	</div>
</c:if>
