<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="row">
	<div class="col-xs-12">
		<h2>
			<spring:theme code="text.cart.delivery.date"/>
		</h2>
        <h3><div class="offset-bottom-small relative clearfix">
            <fmt:formatDate value="${orderData.requestedDeliveryDate}" pattern="EE dd/MM/yyyy"/>
        </div></h3>
	</div>
</div>