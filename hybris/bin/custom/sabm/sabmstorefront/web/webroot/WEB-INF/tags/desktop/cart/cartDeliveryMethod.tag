<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="row">
	<div class="col-xs-12">
		<h2><spring:theme code="text.delivery.method.title"/></h2>
		<p class="offset-bottom-small selected-delivery-address">
			${cartData.deliveryAddress.title} ${cartData.deliveryAddress.firstName} ${cartData.deliveryAddress.lastName}<br>
			${cartData.b2bUnit.name}<br>
			${cartData.deliveryAddress.line2} ${cartData.deliveryAddress.line1}<br>
			${cartData.deliveryAddress.town} ${cartData.deliveryAddress.region.name} ${cartData.deliveryAddress.postalCode}
		</p>
		<c:if test="${ishasanother eq true}">
			<p><a class="inline regular-popup" id="change-address" href="#changeAddressPopup"><spring:theme code="text.select.another.address.title" text="Select another address"/></a></p>			
		</c:if>

		<%-- <p><span class="inline" id="deliveryInstructions"><spring:theme code="text.add.delivery.instructions.title" text="Add delivery instructions"/></span></p>
		<div class="form-group delivery-instructions">
			<textarea class="form-control" rows="3" placeholder="<spring:theme code="text.add.delivery.instructions.limit"/>" id="deliveryInstructionsinfo" maxlength="500" >${cartData.deliveryInstructions}</textarea>
		</div> --%>
		<div 
			<c:choose>
				<c:when test="${not empty shippingCarriers}">
		      class="panel-group radio" 
				</c:when>
				<c:otherwise>
				class="panel-group"
				</c:otherwise>
			</c:choose>
		id="deliveryMethod" role="tablist" aria-multiselectable="true">
			<c:url value="/cart/updateSABMdelivery" var="updateSABMdeliveryUrl" />
   		<c:url value="/cart/updateDeliveryInstructions" var="updateDeliveryInstructionsUrl" />
   		<c:url value="/cart/updateSABMdeliveryAddress" var="updateSABMdeliveryAddressUrl" />
			<input type="hidden" id="updateSABMdeliveryUrl" value="${updateSABMdeliveryUrl}">
			<input type="hidden" id="updateDeliveryInstructionsUrl" value="${updateDeliveryInstructionsUrl}">
			<input type="hidden" id="updateSABMdeliveryAddressUrl" value="${updateSABMdeliveryAddressUrl}">
			<c:if test="${not empty cartData.deliveryMode}">		
				<div class="panel panel-default cub-arranged-block">
					<c:choose>
						<c:when test="${not empty shippingCarriers}">
							<div class="panel-heading accordion-toggle" role="button" data-toggle="collapse" data-parent="#deliveryMethod" data-target="#CUBarrangedDelivery" aria-expanded="true" aria-controls="CUBarrangedDelivery">
								<input id="CUBarrangedDeliveryRadio" name="deliveryMethod" value="${cubArranged.code }" type="radio" ${cubArrangedFlag ? 'checked="checked"' : ''}>
								<label class="h3"  for="CUBarrangedDeliveryRadio">${cubArranged.name}</label>
							</div>
						</c:when>
						<c:otherwise>
							<label class="h3"  for="CUBarrangedDeliveryRadio">${cubArranged.name}</label>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${not empty cartData.deliveryAddress}">
							<div id="CUBarrangedDelivery" class="panel-collapse collapse ${cubArrangedFlag ? '' : 'in'}" role="tabpanel"></div>
						</c:when>
						<c:otherwise>
							<div id="CUBarrangedDelivery" class="panel-collapse collapse" role="tabpanel">
								<div class="panel-body"><p></p></div>
							</div>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>
			<c:if test="${not empty shippingCarriers}">	
				<div class="panel panel-default">
					<div class="panel-heading accordion-toggle" role="button" data-toggle="collapse" data-parent="#deliveryMethod" data-target="#customerArrangedDelivery" aria-expanded="false" aria-controls="customerArrangedDelivery">
						<input id="customerArrangedDeliveryRadio" name="deliveryMethod" value="${customerArranged.code }"  type="radio" ${customerArrangedFlag ? 'checked="checked"' : ''}>
						<label class="h3" for="customerArrangedDeliveryRadio">${customerArranged.name}</label>
					</div>
					<div id="customerArrangedDelivery" class="panel-collapse collapse ${customerArrangedFlag ? 'in' : ''}" role="tabpanel">
						<div class="panel-body">
							<h4><spring:theme code="text.delivery.method.carrier"/></h4>
							 <div class="select-list">
								<c:choose>
									<c:when test="${not empty selectDeliveryShippingCarrier}">
										<div data-value="" class="select-btn sort">${selectDeliveryShippingCarrier}</div>
									</c:when>
									<c:otherwise>
										<div data-value="" class="select-btn sort">Select</div>
									</c:otherwise>
								</c:choose>						    				
							    <ul class="select-items">
							    	<c:forEach items="${shippingCarriers}" var="carriers" varStatus="loop">
							      		<li data-value="${carriers.code}">${carriers.description}</li>
							      	</c:forEach>
							    </ul>
							</div>
							<spring:theme code="text.deliveryMethod.customerArranged.title" />
						</div>
					</div>
				</div>
	     	</c:if>
		</div>
	</div>
</div>
<cart:popupChangeAddress />
<cart:deliveryAddressPopup />