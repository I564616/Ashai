<%--This is the new Contact Us Page --%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="service" tagdir="/WEB-INF/tags/desktop/service"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<c:url var="sendUrl" value="/serviceRequest" />
<c:url value="/businessEnquiry" var="businessEnquiryUrl"/>

<c:set var="contactUs" value="contactus"/>

<div class="service-request offset-bottom-medium" ng-controller="contactUsCtrl" ng-init="contactUsInit" ng-cloak>
	<h2 class="title">Support</h2>

	<!-- support page tabs navigation
		@author: lester.l.gabriel
	 -->
	<service:serviceRequestTabs/>	
	
	<div class="tab-items-container">
		<div id="contact-us" class="tab-item">
			<service:contactUs/>
		</div>
		<div id="faqs" class="tab-item">
			FAQs Component Here!
		</div>
		<div id="video-tutorials" class="tab-item">
			Video Lists Component Here!
		</div>
		<div id="learn-more" class="tab-item">
			Learn More Here!
		</div>
	</div>
</div>
