<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>

<div class="row deals-listing">
	<div class="col-xs-12">

				<c:forEach items="${cartData.dealsTitleMap}" var="dealTitle" varStatus="dealsloop">
					<c:if test="${dealTitle.value == 1}">
					<hr class="offset-bottom-small margin-top-0">
					<h3>The items in your cart have qualified for the following deals:</h3> 
					</c:if>
					<div class="title">
						<span class="bold text-blue">Deal&nbsp;&nbsp;&nbsp;</span>
						<span class="deal-index">${dealTitle.value}</span>
						<span class="deal-desc">${dealTitle.key}</span>
					</div>
				</c:forEach>
		
	</div>
</div>