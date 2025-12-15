<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<div class="row no-order-history">
	<div class="col-md-12">
		<p>
			<spring:theme code="text.account.no.orderHistory.text.part.one" />
		</p>
		<p>
			<spring:theme code="text.account.no.orderHistory.text.part.two" />
		</p>
		<p>
			<spring:theme code="text.account.no.orderHistory.solution" />
		</p>
		<ul>
			<li>
				<spring:theme code="text.account.no.orderHistory.solution.one" />
			</li>
			<li>
				<spring:theme code="text.account.no.orderHistory.solution.two" />
			</li>
		</ul>
	</div>
</div>
<div class="row no-order-history">
	<div class="col-xs-12 col-sm-6 col-md-3 col-lg-3">
		<a href="/" class="btn btn-primary btn--continue-checkout" id="no-order-history-btn"><spring:theme code="text.account.no.orderHistory.order.link" /></a>
	</div>
</div>