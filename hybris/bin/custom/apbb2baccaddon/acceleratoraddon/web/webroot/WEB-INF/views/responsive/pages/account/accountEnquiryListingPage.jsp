<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="searchUrl" value="/my-account/enquiries?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/>
<div id ="enquiryListingFormError" class="hidden">
	<div class="alert alert-danger alert-dismissable">
		<spring:theme code="form.global.error"/>
	</div>
</div>
<div class="account-section-header user-register__headline secondary-page-title">
	<spring:theme code="text.account.myEnquiries" />
</div>

<b2b-order:enquiryListing searchUrl="${searchUrl}" messageKey="text.account.myEnquiries.page"></b2b-order:enquiryListing>