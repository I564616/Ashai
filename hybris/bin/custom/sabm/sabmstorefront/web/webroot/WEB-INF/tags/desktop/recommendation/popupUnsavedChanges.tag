<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div id="unsavedChangesPopup" class="unsaved-changes-popup mfp-hide">
	<div class="legend">
		<span style="text-align:center;"><h2 class="h3 offset-bottom-small"><spring:theme code="text.recommendations.modal.pop.unsaved.changes.confirmation.question" /></h2></span>
		<h3></h3>
		
		<button class="btn btn-primary margin-top-10"><spring:theme code="text.recommendations.modal.pop.unsaved.changes.response.yes" /></button>
		<span onclick="rm.recommendation.proceedToTarget()" class="inline"><spring:theme code="text.recommendations.modal.pop.unsaved.changes.response.no" /></span>
		<br><br><br>
		<p style="text-align:center;"><span onclick="$.magnificPopup.close()"><spring:theme code="text.recommendations.modal.pop.unsaved.changes.response.goback" /></span></p>
		<div class="clearfix"></div>
	</div>
</div>