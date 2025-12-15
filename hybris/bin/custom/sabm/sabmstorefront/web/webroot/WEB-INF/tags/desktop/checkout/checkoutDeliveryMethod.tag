<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="row">
	<div class="col-xs-12">
		<h2><spring:theme code="text.delivery.method.title"/></h2>

		<p class="offset-bottom-small">
			${cartData.deliveryAddress.title} ${cartData.deliveryAddress.firstName} ${cartData.deliveryAddress.lastName}<br>
			${cartData.b2bUnit.name}<br>
			${cartData.deliveryAddress.line2} ${cartData.deliveryAddress.line1}<br>
			${cartData.deliveryAddress.town} ${cartData.deliveryAddress.region.name} ${cartData.deliveryAddress.postalCode}
		</p>
      <c:if test="${ishasanother eq true}">
		<p><a class="inline regular-popup" id="change-address" href="#changeAddressPopup"><spring:theme code="text.select.another.address.title" text="Select another address"/></a></p>			
		</c:if>
		<%-- <p><span class="inline" id="deliveryInstructions"><spring:theme code="text.add.delivery.instructions.title" text="Add delivery instructions"/></span><spring:theme code="text.add.delivery.instructions.limit"/></p>
		<c:if test="${not empty cartData.deliveryInstructions}">	
		<div class="form-group">
			<textarea class="form-control" rows="3" id="deliveryInstructionsinfo" maxlength="500" >${cartData.deliveryInstructions}	
			</textarea>
		</div>
		</c:if> --%>

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
			<c:if test="${not empty cartData.deliveryMode}">		
			<div class="panel panel-default cub-arranged-block">
				<div class="panel-heading accordion-toggle" role="button" data-toggle="collapse" data-parent="#deliveryMethod" data-target="#CUBarrangedDelivery" aria-expanded="true" aria-controls="CUBarrangedDelivery">
				<c:if test="${not empty shippingCarriers}">
					<input id="CUBarrangedDeliveryRadio" name="deliveryMethod" value="${cubArranged.code }" type="radio" 
					 <c:if test="${cubArrangedFlag ==true}">	
					  checked="checked" 
					  </c:if>
					  >
				</c:if>
					<label class="h3">${cubArranged.name}</label>
				</div>
				<c:choose>
					<c:when test="${not empty cartData.deliveryAddress}">
						<div id="CUBarrangedDelivery" 
			<c:choose>
			 <c:when test="${cubArrangedFlag ==true}">
						 class="panel-collapse collapse" 
				</c:when>
				<c:otherwise>
						class="panel-collapse collapse in" 
				</c:otherwise>
			</c:choose>	
						role="tabpanel">
				</div>
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
					<input id="customerArrangedDeliveryRadio" name="deliveryMethod" value="${customerArranged.code }"  type="radio" ${customerArrangedFlag==true ? 'checked="checked"' : ''}>
					<label class="h3" for="customerArrangedDeliveryRadio">${customerArranged.name}</label>
				</div>
				<div id="customerArrangedDelivery" class="panel-collapse collapse ${customerArrangedFlag==true ? 'in' : ''}"
				role="tabpanel">
					<div class="panel-body">
						<h4><spring:theme code="text.delivery.method.carrier"/></h4>
						 <div class="select-list">
							<div data-value="" class="select-btn sort">${selectDeliveryShippingCarrier}</div>
						</div>
						<spring:theme code="text.deliveryMethod.customerArranged.title" />
					</div>
				</div>
			</div>
			</c:if>
		</div>
	</div>
</div>
	<c:url value="/cart" var="cartUrl" scope="session"/>
<a href="${cartUrl}?checkoutStep=${checkoutStep}" class="btn btn-secondary">Update</a>
<input type="hidden" id="checkoutStep" name="checkoutStep" value="${checkoutStep}"/>
 
<hr class="visible-xs-block visible-sm-block">
