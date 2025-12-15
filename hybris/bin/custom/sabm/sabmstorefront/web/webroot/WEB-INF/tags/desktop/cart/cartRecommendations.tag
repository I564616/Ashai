<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<div class="row cart-recommendations">
	<cms:pageSlot position="RecommendationContentSlot" var="feature" element="div">
		<cms:component component="${feature}"/>		
	</cms:pageSlot>
</div>