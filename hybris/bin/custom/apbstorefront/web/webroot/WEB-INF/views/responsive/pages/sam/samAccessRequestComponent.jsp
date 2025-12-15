<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sam-payment" tagdir="/WEB-INF/tags/responsive/account/samPayment"%>
<%@ taglib prefix="single-checkout" tagdir="/WEB-INF/tags/responsive/checkout/single"%>
<c:set var="samPaymentDetailsUrl" value="${request.contextPath}/invoice/payment" scope="session" />
<c:url value="/paymentHistory" var="paymentHistoryLink" scope="session" />
<c:url value="/invoice" var="backToInvoicePage"/>	

	<div class="col-md-12 col-sm-12 col-xs-12 col-lg-12">
		<c:if test="${accessType eq 'ORDER_ONLY'}">
			<div class="login-page__headline">
				Thanks for requesting access!
			</div>
			<br>
				You now can:
				<br>
				<ul class="registerContent">
					<li>Quickly and conveniently place orders</li>
					<li>View your order history</li>
					<li>Manage your account and</li>
					<li>Update your details</li>
				</ul>
				<br>
				<span> Should you need further support call us on <strong>1300 127 244</strong>, (9am - 6pm, Monday - Friday). Alternatively, you can call your Territory Manager.</span>
				<br><br>
				<span>We hope you enjoy using ALB Connect.</span>
				<br><br>
				<span>Cheers,
				<br><br>Asahi Lifestyle Beverages (formerly Schweppes Australia)</span>
				<br><br>
				<br><br>
				<a class="site-anchor-link" href="${request.contextPath}/">Start shopping</a>
		</c:if>
		<c:if test="${accessType eq 'PAY_ONLY'}">
			<div class="login-page__headline">
				Thanks for requesting access!
			</div>
			<br />
            <br />
            <p>An email has been sent to <a href="${emailID}"><strong><c:out value="${emailID}" /></strong></a> (the email address we have on our records) to approve your access. Once your access has been approved, you will
            be able to view invoices and statements or make payments for <span class="text-uppercase">${tradingName}<span>.</p>
			<br>
            <p>Please <a href="/storefront/sga/en/AUD/contactus" class="text-underline"><strong>contact us</strong></a> if the mailbox is unattended or if you require further support.</p>
            <br />
            <br />
				<a class="site-anchor-link" href="${request.contextPath}/">Continue shopping</a>
		</c:if>
	</div>
