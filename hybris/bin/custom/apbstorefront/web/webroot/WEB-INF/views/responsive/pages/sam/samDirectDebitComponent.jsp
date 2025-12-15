<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/responsive/account"%>
<%@ taglib prefix="sam-payment" tagdir="/WEB-INF/tags/responsive/account/samPayment"%>
<spring:htmlEscape defaultHtmlEscape="false" />

<c:choose>
	<c:when test="${isNewDirectDebitEnable}">
		<c:set var="submitDirectDebitFormURL" value="${request.contextPath}/directdebit/submitDirectDebit" scope="session" />
		<c:choose>
			<c:when test="${enableDirectDebit}">
				<div class="col-md-12 direct-debit-page">
					<div class="account-directdebit-label">
						<spring:theme code="text.invoices.direct.debit.text.1" />
					</div>
					<div class="account-directdebit-label">
						<spring:theme code="text.invoices.direct.debit.text.2" />
					</div>
					<div class="account-directdebit-label">
						<spring:theme code="text.invoices.direct.debit.text.3" />
					</div>
					<div class="account-directdebit-label">
						<spring:theme code="text.invoices.direct.debit.text.5" />
					</div>
					<div class="account-directdebit-label">
						<spring:theme code="text.invoices.direct.debit.text.6" />
					</div>
					<div class="account-directdebit-label">
						<spring:theme code="text.invoices.direct.debit.text.7" />
					</div>
		
					<form:form id="submitDirectDebitForm" action="${submitDirectDebitFormURL}" method="post" modelAttribute="asahiDirectDebitForm">
						<div class="form-label">
							<spring:theme code="text.invoices.direct.debit.form.auth" />
						</div>
						<div class="row">
							<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
								<div class="form-group">
									<select id="ddTypeSelect" class="form-control">
										<option value="select">
											<spring:theme code="text.invoices.direct.debit.form.select" />
										</option>
										<option value="creditCard">
											<spring:theme code="text.invoices.direct.debit.form.debitcredit" />
										</option>
										<option value="bankAccount">
											<spring:theme code="text.invoices.direct.debit.form.bankacc" />
										</option>
									</select>
									<input id="tokenType" name="asahiDirectDebitPaymentForm.tokenType" class="hidden" value="">
								</div>
							</div>
						</div>
						<br>
						<div id="bankAccountSection" class="" hidden="hidden">
							<div class="row">
								<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
									<div class="form-group">
										<label class="control-label">
											<spring:theme code="text.invoices.direct.debit.form.accname" /></label>&nbsp;<span>
											<spring:theme code="text.invoices.direct.debit.form.shownstatements" /></span>
										<input id="accountName" name="asahiDirectDebitPaymentForm.accountName" class="form-control" type="text" value="" maxlength="75">
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
									<div class="form-group">
										<label class="control-label">
											<spring:theme code="text.invoices.direct.debit.form.accbsb" /></label>
										<input id="bsb" name="asahiDirectDebitPaymentForm.bsb" placeholder="123456" class="form-control input-number-field" type="number" value="" maxlength="6" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);">
									</div>
								</div>
		
								<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
									<div class="form-group">
										<label class="control-label">
											<spring:theme code="text.invoices.direct.debit.form.accnumber" /></label>
										<input id="accountNum" name="asahiDirectDebitPaymentForm.accountNum" placeholder="1234567890" class="form-control input-number-field" type="number" value="" maxlength="10" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);">
									</div>
								</div>
							</div>
		
							<div class="row">
								<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
									<div class="form-group">
										<label class="control-label">
											<spring:theme code="text.invoices.direct.debit.form.suburb" /></label>
										<input id="suburb" name="asahiDirectDebitPaymentForm.suburb" class="form-control" type="text" value="" maxlength="75">
									</div>
								</div>
								<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
									<div class="form-group">
										<label class="control-label">
											<spring:theme code="text.invoices.direct.debit.form.state" /></label>
										<select id="regionCode" name="" class="form-control">
												<option value="select">
													<spring:theme code="text.invoices.direct.debit.form.select" />
												</option>
											<c:forEach var="region" items="${regions}">
												<option value="${region.code}">${region.name}</option>
											</c:forEach>
										</select>
										<input id="region" name="asahiDirectDebitPaymentForm.region" class="hidden" value="">
									</div>
								</div>
							</div>
		
							<div id="ddterms-label" class="form-label">
								<spring:theme code="text.invoices.direct.debit.form.ddaccept" />
							</div><br>
						</div>
		
						<div id="creditCardSection" class="" hidden="hidden">
							<div class="row">
								<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
									<div class="form-group">
										<sam-payment:samPaymentForm/>
									</div>
								</div>
							</div>
							
							<div id="ddterms-label" class="form-label">
								<spring:theme code="text.invoices.direct.debit.form.ccaccept" />
							</div><br>
						</div>
						
						<div class="row">
							<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
								<div class="ddtandc">
									<input id="ddtandcCheckbox" class="" type="checkbox" value=""><span class="form-checkbox-label">
										<spring:theme code="text.invoices.direct.debit.form.ddtandc" /></span>
								</div>
							</div>
						</div>
						
						<div class="row">
							<br>
							<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
								<div class="form-group">
									<label class="control-label">
										<spring:theme code="text.invoices.direct.debit.form.username" /></label>
									<input id="personalName" name="personalName" class="form-control" type="text" value="" maxlength="75">
								</div>
							</div>
							<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
								<div class="form-group">
									<label class="control-label">
										<spring:theme code="text.invoices.direct.debit.form.date" /></label>
									<input id="currentDate" name="currentDate" class="form-control" value="${currentDate}" type="text" readonly>
								</div>
							</div>
						</div>
		
						<div class="row">
							<div class="col-sm-12 col-sm-5 col-md-3 col-lg-3 account-directdebit-btn">
								<button id="directDebitSubmitBtn" class="btn btn-primary btn-block">
									<spring:theme code="text.invoices.direct.debit.download.btn" />
								</button>
							</div>
						</div>
					</form:form>
				</div>
			</c:when>
			<c:otherwise>
				<div class="col-md-12 direct-debit-page">
					<div class="account-directdebit-label">
						<spring:theme code="direct.debit.view.text1" />
					</div>
					<div class="account-directdebit-label">
						<spring:theme code="direct.debit.view.text2" />
					</div>
					<div class="account-directdebit-label">
						<b><spring:theme code="direct.debit.view.text3" /></b>
					</div>
					<div class="account-directdebit-label">
						<spring:theme code="direct.debit.view.text4" />
					</div>
					
					<div class="account-directdebit-label">
						<label class="control-label"><spring:theme code="direct.debit.view.payment.details.text" /></label><br>
					</div>
					
					<c:if test="${directDebit.directDebitPaymentData.tokenType eq 'BANK_ACCOUNT'}">
						<label class="control-label"><spring:theme code="direct.debit.view.accountName.text" />&nbsp;</label>${directDebit.directDebitPaymentData.accountName}<br>
						<label class="control-label"><spring:theme code="direct.debit.view.accountNum.text" />&nbsp;</label>${directDebit.directDebitPaymentData.accountNum}<br>
						<label class="control-label"><spring:theme code="direct.debit.view.bsb.text" />&nbsp;</label>${directDebit.directDebitPaymentData.bsb}<br>
						<label class="control-label"><spring:theme code="direct.debit.view.suburb.text" />&nbsp;</label>${directDebit.directDebitPaymentData.suburb}<br>
						<label class="control-label"><spring:theme code="direct.debit.view.region.text" />&nbsp;</label>${directDebit.directDebitPaymentData.region}<br>
					</c:if>
					<c:if test="${directDebit.directDebitPaymentData.tokenType eq 'DEBIT_CREDIT_CARD'}">
						<label class="control-label"><spring:theme code="direct.debit.view.card.number.text" />&nbsp;</label>${directDebit.directDebitPaymentData.cardNumber}<br>
						<label class="control-label"><spring:theme code="direct.debit.view.expiry.date.text" />&nbsp;</label>${directDebit.directDebitPaymentData.cardExpiry}<br>
						<label class="control-label"><spring:theme code="direct.debit.view.card.type.text" />&nbsp;</label>${directDebit.directDebitPaymentData.cardType}<br>
						<label class="control-label"><spring:theme code="direct.debit.view.name.on.card.text" />&nbsp;</label>${directDebit.directDebitPaymentData.nameOnCard}<br>
					</c:if>
					
					<div class="account-directdebit-label">
						<label class="control-label"><spring:theme code="direct.debit.view.requested.by.text" /></label><br>${directDebit.personalName}<br>
					</div>
					
					<div class="account-directdebit-label">
						<label class="control-label"><spring:theme code="direct.debit.view.requested.on.text" /></label><br>${directDebit.date}<br>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<div class="col-md-12">
			<div class="account-directdebit-label"><spring:theme code="text.invoices.direct.debit.text.1"/></div>
			<div class="account-directdebit-label"><spring:theme code="text.invoices.direct.debit.text.2"/></div>
			<div class="account-directdebit-label"><spring:theme code="text.invoices.direct.debit.text.3"/></div>
			<div class="account-directdebit-label"><spring:theme code="text.invoices.direct.debit.text.5"/></div>
			
			<div class="row">
				<div class="col-sm-12 col-sm-5 col-md-3 col-lg-3 account-directdebit-btn">
					<a href="/storefront/_ui/responsive/common/pdf/DirectDebitForm.pdf" id="b2bUnits" target="_blank" class="liOffcanvas b2bSelect btn btn-primary btn-block"><spring:theme code="text.invoices.direct.debit.download"/></a>
				</div>
			</div>
		</div>
	</c:otherwise>
</c:choose>
