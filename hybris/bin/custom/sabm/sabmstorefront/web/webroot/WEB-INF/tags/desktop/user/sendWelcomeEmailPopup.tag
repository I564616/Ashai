<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="sendWelcomeEmailPopup" class="send-welcome-email-popup mfp-hide">
  <p><spring:theme code="text.send.welcome.email.info" /></p>
  <br/>
  <div class="col-md-12 btn-wrap">
	<span class="btn btn-primary" onclick="$.magnificPopup.close(); $('#sendWelcomeEmailPopup p').html('<spring:theme code="text.send.welcome.email.info" />')"><spring:theme code="text.send.welcome.email.ok.button" /></span>
  </div>
  <br/>
</div>