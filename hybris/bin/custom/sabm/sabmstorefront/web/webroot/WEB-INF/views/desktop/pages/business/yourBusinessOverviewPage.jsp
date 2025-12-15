<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="business" tagdir="/WEB-INF/tags/desktop/business"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<template:page pageTitle="${pageTitle}">

<c:url value="/your-business/profile" var="personalDetailsUrl"/>
<c:url value="/your-business/ordertemplates" var="templateOrdersUrl"/>
<c:url value="/your-business/businessunits" var="businessUnitsUrl"/>
<c:url value="/your-business/billing" var="billingUrl"/>
<c:url value="/your-business/orders" var="orderHistoryUrl"/>
<c:url value="/serviceRequest" var="serviceRequestUrl"/>
<c:url value="/businessEnquiry" var="businessEnquiryUrl"/>
<c:url value="/authenticatedContactUs" var="contactUsUrl"/>
<c:url value="/your-business/update-password" var="updatePasswordUrl"/>
<c:url value="/your-notifications" var="notificationsUrl"/>
<c:url value="" var="faqUrl"/>


<c:url value="/your-business/invoicediscrepancy" var="invoiceDiscrepancyUrl"/>
<c:url value="/your-business/raisedinvoicediscrepancy" var="raisedInvoiceDiscrepancyUrl"/>

<cms:pageSlot position="Section1" var="feature" element="div">
	<cms:component component="${feature}"/>
</cms:pageSlot>

<%--<business:notifications />--%>

<h1><spring:theme code="text.page.business.title"/></h1>
<section class="standard border-divider offset-bottom-large">
    <div class="row">
        <div class="col-sm-6 offset-bottom">
            <h2>
               <svg class="icon-business">
                   <use xlink:href="#icon-business"></use>    
               </svg>
               <spring:theme code="text.page.business.manageuser.title"/></h2>
            <p><spring:theme code="text.page.business.manageuser.description"/></p>
            <a href="${businessUnitsUrl }" class="link-cta"><spring:theme code="text.page.business.manageuser.linkname"/></a>
            <p style="margin-top:45px;"><spring:theme code="text.page.business.accountNumber"/>&nbsp;<span style="font-weight:bold;"><fmt:parseNumber value="${customerData.unit.uid}"/></span></p>
        </div>
        <div class="col-sm-6 offset-bottom">
            <h2>
                <svg class="icon-profile01">
                    <use xlink:href="#icon-profile01"></use>    
                </svg>
            <spring:theme code="text.page.business.profile.title"/></h2>
            <ul class="list-unstyled">
                <li>
                    <spring:theme code="text.page.business.profile.name"/>: ${fn:escapeXml(title.name)}&nbsp;${fn:escapeXml(customerData.firstName)}&nbsp;${fn:escapeXml(customerData.lastName)}
                </li>
                <li>
                    <spring:theme code="text.page.business.profile.email"/>: ${fn:escapeXml(customerData.displayUid)}
                </li>
            </ul>

            <a href="${personalDetailsUrl }" class="link-cta bde-view-only"><spring:theme code="text.page.business.profile.linkname"/></a><br>
            <sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP,ROLE_B2BORDERCUSTOMER')">
            	<a href="${updatePasswordUrl}" class="link-cta bde-view-only"><spring:theme code="text.page.business.update.password.linkname"/></a><br>
            	<a href="${notificationsUrl}" class="link-cta bde-view-only"><spring:theme code="text.homepage.header.dropdown.notifications"/></a>
            </sec:authorize>
        </div>
    </div>
</section>

<section class="standard border-divider offset-bottom-large" >
     <div class="row" id ="standardSection">
     	<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP,ROLE_B2BORDERCUSTOMER')">
			<c:set value="true" var="showTemplate" />
		</sec:authorize>
		<sec:authorize ifAllGranted="ROLE_B2BORDERCUSTOMER,ROLE_B2BINVOICECUSTOMER">
			<c:set value="true" var="showTemplate" />
		</sec:authorize>
    	<c:if test="${showTemplate && !isNAPGroup}">
	        <div class="col-sm-4 offset-bottom">
	            <h2>
                <svg class="icon-templates">
                    <use xlink:href="#icon-templates"></use>    
                </svg>
                <spring:theme code="text.page.business.template.title"/></h2>
	            <p><spring:theme code="text.page.business.template.description"/></p>
	            <a href="${templateOrdersUrl }" class="link-cta"><spring:theme code="text.page.business.template.linkname"/></a>
	        </div>
        </c:if>
        
        <sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP,ROLE_B2BINVOICECUSTOMER')">
        <div class="col-sm-4 offset-bottom">
            <h2>
            <svg class="icon-invoice">
                <use xlink:href="#icon-invoice"></use>    
            </svg>
            <spring:theme code="text.page.business.billing.title"/></h2>
            <p><spring:theme code="text.page.business.billing.description"/></p>
            <a href="${billingUrl}" class="link-cta"><spring:theme code="text.page.business.billing.linkname"/> </a> <br />


            <c:if test="${isInvoiceDiscrepancyEnabled && !isNAPGroup}">
                <a href="${invoiceDiscrepancyUrl}" class="link-cta"><spring:theme code="text.page.business.invoiceDiscrepancy.linkname"/> </a> <br />
                <a href="${raisedInvoiceDiscrepancyUrl}" class="link-cta"><spring:theme code="text.page.business.raisedInvoiceDiscrepancy.linkname"/> </a>
            </c:if>
        </div>
        </sec:authorize>
        <sec:authorize access="hasAnyRole('ROLE_B2BORDERCUSTOMER,ROLE_B2BADMINGROUP')">
        <div class="col-sm-4 offset-bottom">
            <h2>
            <span id="icon-order-history"></span>
            <spring:theme code="text.page.business.order.history.title"/></h2>
            <p><spring:theme code="text.page.business.order.history.description"/></p>
            <a href="${orderHistoryUrl }" class="link-cta"><spring:theme code="text.page.business.order.history.linkname"/></a>
        </div>
        </sec:authorize>
    </div>
</section>

<section>
    <div class="row">
        <div class="col-sm-12">
            <h2><spring:theme code="text.page.business.need.a.hand.title"/></h2>
        </div>
     </div>
     <cms:pageSlot position="Section3" var="feature" element="div" class="row">
	  	<cms:component component="${feature}"/>
	  </cms:pageSlot>

</section>

</template:page>