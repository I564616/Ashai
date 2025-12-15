<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>



<c:url var="trackMyDeliveryPath" value="/trackorders?code=${orderData.sapSalesOrderNumber}"/>
<c:url var="yourNotificationPath" value="/your-notifications" />

<div class="row order-received">
	<div class="col-xs-12">
		<div class="track-my-delivery-panel" onclick="window.location.href='${trackMyDeliveryPath}'">
			<span class="icon"></span>
				<div class="header-text">
				<h2><spring:theme code="text.orderreceived.trackyourdelivery.header" /></h2>
				
				<p><spring:theme code="text.orderreceived.trackyourdelivery.description" /></p>
				</div>
			<span class="arrowright"></span>
			
			<div class="footer-text">
				<div class="hr-block"></div>
			
				<p>
				<span class="icon-notification"></span>
				<spring:theme code="text.orderreceived.trackyourdelivery.footer.link" arguments="${yourNotificationPath}" />
				</p>
			</div>
		</div>
	</div>
</div>