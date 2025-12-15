<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="row">
	<div class="col-xs-12">
		<h2>
			<spring:theme code="text.cart.delivery.date"/>
		</h2>
		<form>
			<div class="form-group offset-bottom-small relative clearfix">
				<svg class="icon-calendar">
				    <use xlink:href="#icon-calendar"></use>    
				</svg>
				<input class="form-control cart-datepicker" data-date-format="DD dd/mm/yyyy" data-date-autoclose="true" data-date-orientation="bottom left" type="text" readonly="readonly" data-value="" data-provide="datepicker">
			</div>
		</form>
		<p><spring:theme code="text.cart.delivery.cutoff.message"/></p>
		<p class="availability-day ${deliveryDatePackType}">
			<c:if test="${deliveryDatePackType eq 'KEG'}">
				<spring:theme code="text.cart.delivery.availability.keg" />
			</c:if>
			<c:if test="${deliveryDatePackType eq 'PACK'}">
				<spring:theme code="text.cart.delivery.availability.pack" />
			</c:if>
		</p>
	</div>
</div>