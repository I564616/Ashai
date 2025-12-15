<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>
<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelArguments" required="false" type="java.lang.String"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<div class="account-section-header">
	<%-- <a href="${url}">
		<span class="glyphicon glyphicon-chevron-left"></span>
	</a> --%>
	<div class="manage_users_headline">
	<span>
		<spring:theme code="${labelKey}" arguments="${labelArguments}"/>
	</span>
	</div>
</div>