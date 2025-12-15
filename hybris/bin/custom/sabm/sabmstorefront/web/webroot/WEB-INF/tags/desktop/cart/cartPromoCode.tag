<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="row form-group cart-promocode hidden">
	<div class="col-md-5">
		<label for="cartPromoCodeInput" class="h3"><spring:theme code="text.cart.promotion.ask" /></label>
	</div>
	<div class="col-md-7 input-icon-group">
		<input class="form-control" id="cartPromoCodeInput" type="text">
		<button type="submit">
			<svg class="icon-arrow-right">
			    <use xlink:href="#icon-arrow-right"></use>    
			</svg>
		</button>
	</div>
</div>
<div class="row hidden">
	<div class="col-xs-12">
		<a class="inline" href="#"><spring:theme code="text.cart.promotion.obtain" /></a>
	</div>
</div>
<div class="row offset-bottom-small">
	<div class="col-xs-12">
		<p><a class="inline" href=" /termsAndConditions" target="_blank"><spring:theme code="text.cart.delivery.terms" /></a></p>
	</div>
	<div class="col-xs-12 magnific-price">
		<p><a class="inline regular-popup" href="#price-conditions"><spring:theme code="text.product.price.conditions" /></a></p>
	</div>
</div>