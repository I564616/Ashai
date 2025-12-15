<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<script id="addressData" type="text/json">${ycommerce:generateJson(anotherDeliveryAddresses)}</script>

<div id="changeAddressPopup" class="change-Address-Popup mfp-hide">
	<h2 class="h1"><spring:theme code="text.change.delivery.address.title"/></h2>
	<a href="#"><spring:theme code="text.change.delivery.address.manage.default"/></a>
	
	<input type="hidden" class="cartData_Address_id" value="${cartData.deliveryAddress.id }"/>

	<div class="clearfix relative" ng-class="{'well' : address.defaultB2BunitAddress}" ng-repeat="address in addresses track by $index">
		<div class="col-sm-6 offset-bottom-small">
			<h4 ng-show="address.defaultB2BunitAddress"><spring:theme code="text.change.delivery.default.address.info"/></h4>
			<span>{{address.title}} {{address.firstName}} {{address.lastName}}</span><br>
			<span>${cartData.b2bCustomerData.unit.name}</span><br>
			<span>{{address.line2}} {{address.line1}}</span><br>
			<span>{{address.town}}</span><br>
			<span>{{address.region.name}} {{address.postalCode}}</span>
		</div>

		<div class="col-sm-6 btn-wrap text-right btn-deliver-warp">
			<span class="btn btn-primary btn-medium deliverSelectAddress" ng-click="selectAddress($index); $event.stopPropagation();" id="deliverSelectAddress${loop.index}"><spring:theme code="tex.delivery.address.button"/></span>
		</div>
	</div>
	<br>
	<a href="#" class="inline" onclick="$.magnificPopup.close()"><spring:theme code="text.account.profile.cancel"/></a>
</div>

<div id="saveAsDefault" class="setdefault-popup mfp-hide">
	<h2 class="h1"><spring:theme code="text.save.default.delivery.address.title"/></h2>
	<p><spring:theme code="text.save.default.delivery.address.prompt.info"/></p>
	<h3 class="offset-bottom-small">${cartData.b2bCustomerData.unit.name}<br>
	{{addresses[selectedAddress].line2}} {{addresses[selectedAddress].line1}}, <span class="br-xs"></span>{{addresses[selectedAddress].town}}, <span class="br-xs"></span>{{addresses[selectedAddress].postalCode}}</h3>
	<button class="btn btn-primary" ng-click="setDeliveryAddress(true)"><spring:theme code="text.save.default.delivery.address.button.info"/></button>
	<span class="inline btn-height" ng-click="setDeliveryAddress(false)"><spring:theme code="text.no.save.default.delivery.address.button.info"/></span>
	<div class="clearfix"></div>
</div>