<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="pdf" tagdir="/WEB-INF/tags/responsive/pdf"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<c:url value="/contactus/?register=no" var="contactUsUrl"/>
<div class="user-register__headline">
	<spring:theme code="sga.register.become.customer" />

</div>
<p>
	<spring:theme code="sga.register.become.customer.notification" />
</p>
 
<div class="site-anchor-link" id="sgaContactUsLink">
	<a href="${contactUsUrl}"><spring:theme code="sga.reqeust.registration.contactus.link" /></a>
</div>