<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div id="loseDealPopup" class="lose-deal-popup mfp-hide lose-deal-minicart">
	<div class="legend">
		<h2 class="h1 offset-bottom-small"><spring:theme code="text.car.modal.pop.lose.deal.title" /></h2>
		<p class="offset-bottom-small"><spring:theme code="text.car.modal.pop.lose.deal.del.detail" /></p>

		<h3></h3>
		
		<button class="btn btn-primary margin-top-10" onclick="rm.tagManager.addDealsImpressionAndPosition('Clicked', 'LoseDeal', 'RejectDeal');"><spring:theme code="text.car.modal.pop.lose.deal.title.yes" /></button>
		<span onclick="rm.cart.resetCart(); rm.tagManager.addDealsImpressionAndPosition('Clicked', 'LoseDeal', 'ApplyChosenDeal'); $.magnificPopup.close();" class="inline"><spring:theme code="text.car.modal.pop.lose.deal.title.no" /></span>
		<div class="clearfix"></div>
	</div>
</div>


<div id="loseDealPopupReduce" class="deal-reduce lose-deal-popup mfp-hide lose-deal-minicart">
	<h2 class="h1 offset-bottom-small"><spring:theme code="text.car.modal.pop.lose.deal.title" /></h2>
	<p class="offset-bottom-small"><spring:theme code="text.car.modal.pop.lose.deal.title.detail" /></p>

	<h3></h3>
	
	<button class="btn btn-primary margin-top-10"><spring:theme code="text.car.modal.pop.lose.deal.title.yes" /></button>
	<span onclick="rm.cart.resetCart()" class="inline"><spring:theme code="text.car.modal.pop.lose.deal.title.no" /></span>
	<div class="clearfix"></div>
</div>