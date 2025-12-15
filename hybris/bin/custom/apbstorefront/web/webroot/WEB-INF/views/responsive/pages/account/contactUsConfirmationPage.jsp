<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<template:page pageTitle="${pageTitle}">
    <div class="login-page__headline"">
       <spring:theme code="sga.contactus.confirmation.title" />
    </div>
	<div class="contactus-confirmation">
		<div><spring:theme code="sga.contactus.confirmation.text.line.1" /><br /><br />
			<spring:theme code="sga.contactus.confirmation.text.line.2" /><br />
			
			<%-- Get correct variable name for enquiryID --%>
			<c:if test="${enquiryId != '' && enquiryId ne null}">
			<spring:theme code="sga.contactus.confirmation.text.line.3" arguments="${enquiryId}" /> 
			</c:if>
			<%-- Check auth --%>
			<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
				<c:if test="${enquiriesPageAvailable}">
				 	<spring:theme code="sga.contactus.confirmation.text.line.3.authenticated" />  
				 	<a href="/storefront/sga/en/AUD/my-account/enquiries"><spring:theme code="sga.contactus.confirmation.text.myenquiries.link.text" /></a>
				 </c:if>
			</sec:authorize>
			
		</div><br />
		<div>
			<spring:theme code="sga.contactus.confirmation.text.line.4" />
		</div>
		<div><br />
			<spring:theme code="sga.contactus.confirmation.urgent.enquiries" />
		</div><br />
		<div>
			<spring:theme code="sga.contactus.confirmation.text.line.5" /> 
			 <a href=""./"" class=""site-anchor-link""><spring:theme code="sga.contactus.confirmation.albconnect.link.text" /></a>
		</div>
	</div>
</template:page>