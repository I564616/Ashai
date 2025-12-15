<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:url var="continueBrowsingUrl" value="/" />
<template:page pageTitle="${pageTitle}">
	<cms:pageSlot position="Section1" var="feature">
		<cms:component component="${feature}" element="div" class="section1 cms_disp-img_slot"/>
	</cms:pageSlot>
	<div>
		<div class="row">
			<div class="col-xs-12">
			  <h1>Payment confirmation</h1>
			  <spring:theme code="text.payment.confirmation.description" arguments="${customerData.email }"/>
			  
			  <div class="links-list">
				  <p><a href="/your-business"><spring:theme code="text.payment.confirmation.link.your.business" /></a></p>
			  </div>
			  <br>
			  <div class="row">
			  	<div class="col-xs-12 col-sm-4 col-md-3">
			  		<a href="${continueBrowsingUrl}" class="btn btn-primary"><spring:theme code="text.payment.confirmation.link.continue.browsing" /></a>
			  	</div>
			  </div>
			</div>
		</div>
		<br>
		<br>
		<div class="row">
			<div class="col-xs-12 col-md-5">
			<h2><spring:theme code="text.payment.details.title" /></h2>
			<div class="row">
				<div class="col-xs-6 offset-bottom-small">
					<h3><spring:theme code="text.payment.details.receipt.number" /></h3>
					<span>${invoicePaymentData.receiptNumber}</span>
				</div>
				<div class="col-xs-6 offset-bottom-small">
                    <h3><spring:theme code="text.payment.details.method" /></h3>
                    <c:if test="${invoicePaymentData.paymentInfo != null}">
                        <span>Card payment</span>
		            </c:if>
                    <c:if test="${invoicePaymentData.debitInfo != null}">
                        <span>EFT payment</span>
		            </c:if>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-6 offset-bottom-small">
					<h3><spring:theme code="text.payment.details.amount.paid" /></h3>
                    <fmt:setLocale value="en_AU"/>
					<span><fmt:formatNumber type="currency" currencySymbol="$">${totalInvoiceAmt}</fmt:formatNumber></span>
				</div>

				<div class="col-xs-6 offset-bottom-small">
                    <c:if test="${invoicePaymentData.paymentInfo != null}">
                        <h3><spring:theme code="text.payment.details.card" /></h3>
					  
					   <c:if test="${invoicePaymentData.paymentInfo.cardTypeData != null}">
						 <span>${invoicePaymentData.paymentInfo.cardTypeData.name}</span>
					   </c:if>	

		            </c:if>
		            
<%-- Commenting out as per SABMC-1851
                    <c:if test="${invoicePaymentData.debitInfo != null}">
                        <h3><spring:theme code="text.payment.details.eft" /></h3>
					    <span>Electronic Funds Transfer</span>
		            </c:if> --%>
				</div>
			</div>
			</div>
		</div>
	</div>
	<cms:pageSlot position="Section3" var="feature" element="div" class="section3 cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<nav:backToTop />
</template:page>


