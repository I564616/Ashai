<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="mfp-hide" id="faq-send-question">
    <h2 class="h1"><spring:theme code="text.component.faq.modal.send.question.title"/></h2>
    <p><spring:theme code="text.component.faq.modal.send.question.text"/></p>
    <form:form name="faqSendQuestion">
      <input type="hidden" name="userName" ng-init="faqRequest.name = '${user.name}'">
      <input type="hidden" name="userEmail" ng-init="faqRequest.email = '${user.email}'">
      <input type="hidden" name="currentVenue" ng-init="faqRequest.businessUnit = '${user.currentB2BUnit.name}'">
      <div class="form-group">
        <textarea class="form-control" name="faqQuestion" rows="4" ng-model="faqQuestion" placeholder="<spring:theme code='text.component.faq.modal.send.question.placeholder'/>" required></textarea>
      </div>
      <a class="btn btn-cancel" onclick="$.magnificPopup.close()" role="button"><spring:theme code="text.component.faq.modal.send.question.button.cancel"/></a>
      <a class="btn btn-primary" ng-disabled="faqSendQuestion.$invalid" ng-click="sendFaq(faqSendQuestion)"><spring:theme code="text.component.faq.modal.send.question.button.submit"/></a>
    </form:form>
</div>