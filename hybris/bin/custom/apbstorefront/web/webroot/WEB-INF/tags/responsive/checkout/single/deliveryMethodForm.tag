<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script>
var cutOffDate="${deliveryInfoData.cutOffDate}";
</script>

<c:choose>
	<c:when test="${cmsSite.uid ne 'sga'}">
		<div class="delivery-date">
			<div class="checkout-delivery-date-radio">
			<input type="radio" name="deliveryDateType" value="text" data-id="deliverymessage" id="standard" checked>
			<label for='standard'><spring:theme code="checkout.summary.standard.delivery" /></label>
			</div>
			<c:if test="${not empty deliveryInfoData && not deliveryInfoData.disableDeferredDelivery}">
			<div class="checkout-delivery-date-radio">
					 <input type="radio" name="deliveryDateType" value="dropdown" data-id="deffered-delivery-dropdown" id="deferred" class="margin-left" >
					 <label for='deferred'><spring:theme code="checkout.summary.deferred.delivery" /></label><br>
					 </div>
			</c:if>

			<div class="row">
			<div class="col-md-12 col-sm-12">
				<div class="cut-off-message" >
					<c:if test="${not empty deliveryInfoData && not empty deliveryInfoData.deliveryCutOff}">

							<c:if test="${deliveryInfoData.deliveryCutOff eq 'BeforeCutOff'}">

									<p id="deffered-delivery-text"><span style="font-weight:normal"><spring:theme code="delivery.message.before.cutoff"
																		  arguments="${deliveryInfoData.cutOffDate},${deliveryInfoData.deliveryDays},${deliveryInfoData.maxDeliveryDays}"/></span></p>
							</c:if>
							  <c:if test="${deliveryInfoData.deliveryCutOff eq 'AfterCutOff'}">
											<p><span style="font-weight:normal"><spring:theme code="delivery.message.after.cutoff"
																		  arguments="${deliveryInfoData.deliveryDays},${deliveryInfoData.maxDeliveryDays}"/></span></p>
							 </c:if>
					</c:if>
				</div>
			</div>
		</div>
		</div>

		<div class="row">
			<div class="deffered-delivery-dates" style="display:none">
				<div id="deffered-delivery-message">
					<spring:theme code="deferred.delivery.message" />
				</div>
				<div class="col-md-3 col-sm-6">
					<select class="form-control hide" id="deffered-del-selectdates">
						<c:forEach items="${deliveryInfoData.deferredDeliveryOptions}" var="deferredDateOption" varStatus="status">
							<option>${deferredDateOption}</option>
						</c:forEach>
					</select>
					<div id="deferredCalError" class="hide alert alert-danger alert-dismissable">
						<spring:theme code="checkout.payment.deferred.error.message" />
					</div>
					<div class="input-group">
						<input type="text" id="deferredCalendar" class="form-control" name="deliveryMethod.deferredDeliveryDate" readonly="readonly">
						<span class="input-group-addon showDeferredCal"><i class="glyphicon glyphicon-calendar"></i></span>
					</div>
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="delivery-date hide">
			<c:if test="${not empty deliveryInfoData}">
				<div class="checkout-delivery-date-radio">
					<input type="radio" name="deliveryDateType" value="dropdown" data-id="deffered-delivery-dropdown" id="deferred" class="margin-left">
					<label for='deferred'><spring:theme code="checkout.summary.deferred.delivery" /></label><br>
				</div>
			</c:if>
		</div>
		<br>
		<div class="row">
			<div class="col-md-12 col-sm-12">
				<p><strong><spring:theme code="checkout.summary.delivery.date" /></strong><br><spring:theme code="checkout.summary.delivery.date.message" /></p>
			</div>
		</div>
		<div class="row">
			<div class="col-md-3 col-sm-6">
				<div class="deffered-delivery-dates">
				  <select class="form-control hide" id="deffered-del-selectdates">
						<c:forEach items="${deliveryInfoData.deferredDeliveryOptions}" var="deferredDateOption" varStatus="status">
							<option>${deferredDateOption}</option>
						</c:forEach>
					</select> 
					<div id="deferredCalError"
			class="hide alert alert-danger alert-dismissable"><spring:theme code="checkout.payment.deferred.error.message"/></div>
					<div class="input-group">
					<input type="text" id="deferredCalendar" class="form-control" name="deliveryMethod.deferredDeliveryDate" readonly="readonly">
					<span class="input-group-addon showDeferredCal"><i class="glyphicon glyphicon-calendar"></i></span>
					</div>
				</div>
			</div>
		</div>
		<br>
	</c:otherwise>
</c:choose>

<input type="hidden" id="deliveryMethodType" name="deliveryMethod.deliveryType" value="standard" />



