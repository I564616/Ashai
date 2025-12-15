<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<div class="ui-grid-a">
	<svg class="TBD-LUKE">
		<use xlink:href="#TBD-LUKE"></use>    
	</svg>
	<div class="ui-block-a">
		<spring:theme code="text.cart.dealtag.title"/>
	</div>
	<div class="ui-block-b">
		<cart:findOutMoreButton />
	</div>
</div>
