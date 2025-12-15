<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>
<%@ attribute name="cancelUrl" required="true" type="java.lang.String"%>
<%@ attribute name="displayTextMsgKey" required="true" type="java.lang.String"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<a id="backButton" class="back_button" href="${cancelUrl}">
      
        <spring:theme code="${displayTextMsgKey}"/>
   
</a>
