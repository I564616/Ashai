<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="service" tagdir="/WEB-INF/tags/desktop/service" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%--This is the new Contact Us Page --%>
<%@ page trimDirectiveWhitespaces="true" %>

<template:page pageTitle="${pageTitle}">

    <c:url var="sendUrl" value="/serviceRequest"/>
    <c:url value="/businessEnquiry" var="businessEnquiryUrl"/>

    <c:set var="contactUs" value="contactus"/>

    <div class="service-request offset-bottom-medium" ng-controller="contactUsCtrl" ng-init="contactUsInit" ng-cloak>
        <h2 class="title">Support</h2>

        <service:serviceRequestTabs/>

        <div class="tab-items-container">
            <div id="contact-us-tab" class="tab-item">
			    <cms:pageSlot position="ContactUsSlot" var="feature" element="div">
					<c:choose>
	            		<c:when test="${feature.uid eq 'SupportInvoiceDescrepancyComponent_backgroundimage' }">
							<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP,ROLE_B2BINVOICECUSTOMER')">
								<c:if test="${isInvoiceDiscrepancyEnabled}">		
							        <cms:component component="${feature}" evaluateRestriction="true"/>
							     </c:if>
							</sec:authorize>
						</c:when>
						<c:otherwise>
					        <cms:component component="${feature}" evaluateRestriction="true"/>
						</c:otherwise>
					</c:choose>
			    </cms:pageSlot>
			    <service:contactUs/>
            </div>
            <div id="faqs-tab" class="tab-item">
			    <cms:pageSlot position="FaqsContentSlot" var="feature" element="div">
			        <cms:component component="${feature}" evaluateRestriction="true"/>
			    </cms:pageSlot>
            </div>
            <div id="video-tutorials-tab" class="tab-item">
			    <cms:pageSlot position="VideoTutorialsSlot" var="feature" element="div">
			        <cms:component component="${feature}" evaluateRestriction="true"/>
			    </cms:pageSlot>
            </div>
            <div id="learn-more-tab" class="tab-item">
                <p class="desc"><spring:theme code="text.support.learn.more.paragraph"/></p>
                <cms:pageSlot position="LearnMoreSlot" var="feature" element="div">
			        <cms:component component="${feature}" evaluateRestriction="true"/>
			    </cms:pageSlot>
            </div>
        </div>
    </div>

    <cms:pageSlot position="BannerSlot" var="feature" element="div">
        <cms:component component="${feature}" evaluateRestriction="true"/>
    </cms:pageSlot>

</template:page>
