<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:if test="${not empty googleAnalyticsTrackingId}">
<!-- Google Analytics -->
<script>
window.ga=window.ga||function(){(ga.q=ga.q||[]).push(arguments)};ga.l=+new Date;
ga('create', '${googleAnalyticsTrackingId}', 'auto');
ga('send', 'pageview');

</script>
<script async src='https://www.google-analytics.com/analytics.js'></script>

<script>
window.ga = window.ga || [];

function trackCreditDiscrepancyLink(ev) {
	ev.preventDefault();
	
	if ( $(ev.target).hasClass('data-link') ) {
		var action = $(ev.target).attr('data-action'), label = $(ev.target).attr('data-label');
		
		window.ga('send', {
			hitType:'event',
			eventCategory: 'Page Body',
			eventAction: action,
			eventLabel: label,
			hitCallback: function() {
				window.location.href = $(ev.target).attr('href');
			}
		});	    
	}	
}

function trackInvoiceDiscrepancyStepProcess(title) {
	
	window.ga('send', {
		hitType:'event',
		eventCategory: 'InvoiceDiscrepancyVirtualPageview',
		eventAction: 'Track Invoice Step Process',
		eventLabel: title,
		hitCallback: function() {
			console.log('GA Sent');
		}
	});	    
}

</script>
<!-- End Google Analytics -->
</c:if>