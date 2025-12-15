<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>

<div class="col-md-12 direct-debit-page">
	<div class="login-page__headline">
		<spring:theme code="direct.debit.confirmation.text1" />
	</div>

	<div class="account-directdebit-label">
		<spring:theme code="direct.debit.confirmation.text2"/> &nbsp;<b>${accountName}</b>.
	</div>

	<div class="account-directdebit-label">
		<spring:theme code="direct.debit.confirmation.text3" />
	</div>

	<div class="account-directdebit-label">
		<spring:theme code="direct.debit.confirmation.text4" />
	</div>

	<br>
	<div class="account-directdebit-label">
		<a class="site-anchor-link" href="${request.contextPath}/directdebit">
			<spring:theme code="direct.debit.confirmation.link.to.page" /></a>
	</div>
</div>