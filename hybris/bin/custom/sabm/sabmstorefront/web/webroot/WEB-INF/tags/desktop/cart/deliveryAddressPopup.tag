<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div id="deliveryAddressPopup" class="change-Address-Popup  mfp-hide">
	<h1><spring:theme code="text.save.default.delivery.address.title"/></h1>
	<p><spring:theme code="text.save.default.delivery.address.prompt.info"/></p>
	 
	<div class="col-md-6">
				 <h3 id ="delivery_b2bUnit_name"></h3>
				 <h3 id ="delivery_address_info"></h3><br>
	</div>
	<div class="col-md-6 btn-wrap">
	<a href="#"  id ="saveNoDefault"><u><spring:theme code="text.no.save.default.delivery.address.button.info"/></u></a>
	<input type="hidden" class="seelcted_Delivery_Address_id" value=""/>
	<input type="hidden" class="cart_Delivery_Address_id" value=""/>
   <span class="btn btn-primary btn-medium" id ="saveDefault"><spring:theme code="text.save.default.delivery.address.button.info"/></span>
	</div>	
</div>