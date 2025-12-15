<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!-- Mouldy Keg Note Block -->
<div class="row">
  <div class="col-xs-12">
    <div class="alert alert-info text-left" style="line-height: 1.5; font-size: 14px;">
      <p>
        <strong>Note:</strong>
        <span>
          If your keg has a mouldy valve, 
		  <a href="${mouldyKegPdfUrl}" target="_blank" class="alert-link">click here</a>
          for instructions on how to clean it effectively and some general information regarding the likely cause for the mould. 
          For further information, please call <strong>1300 127 244</strong>.
        </span>
      </p>
    </div>
  </div>
</div>


<div class="row">
  <div class="col-xs-12 col-md-4 form-group">
    <label for="sr-name">Name<span class="required">*</span></label>


    <c:set var="customerName" value=""/>
    <c:set var="customerEmail" value=""/>
    <c:if test="${not empty customer }">
    <c:set var="customerName" value="${customer.firstName } ${customer.lastName }"/>
    <c:set var="customerEmail" value="${customer.email }"/>
    </c:if>

    <input id="sr-name" class="form-control" name="name" type="text" ng-model="sr.name" ng-init="sr.name = '${customerName }'" ng-required="true" >
    <span class="error" ng-show="serviceRequest.name.$touched && serviceRequest.name.$error.required"><spring:theme code="text.service.request.form.msg.fullname"/></span>
  </div>
  <div class="col-xs-12 col-md-4 form-group">
    <label for="sr-business-unit">Business unit<span class="required">*</span></label>
    <c:if test="${not empty user.branches and fn:length(user.branches) >= 1}">
      <div class="select">
        <span class="arrow"></span>
        <select id="sr-business-unit" class="form-control" name="business_unit" ng-model="sr.bu" ng-required="true">
          <option value="" disabled selected>Select</option>
          <c:forEach items="${user.branches}" var="b2bUnit">
            <option value="${b2bUnit.name}--${b2bUnit.uid}">${b2bUnit.name}</option>
          </c:forEach>
        </select>
      </div>
    </c:if>
    <%-- <c:if test="${not empty user.branches and fn:length(user.branches) < 2}">
      <c:forEach items="${user.branches}" var="b2bUnit">
        <input id="sr-business-unit" class="form-control" name="business_unit" type="text" ng-model="sr.bu" ng-init="sr.bu = '${(b2bUnit.name).replace('\'', '\\\'')}'" readonly> 
      </c:forEach>
    </c:if>
    <span class="error" ng-show="serviceRequest.business_unit.$touched && serviceRequest.business_unit.$error.required"><spring:theme code="text.service.request.form.msg.business.unit"/></span>
   --%>
   <span class="error" ng-show="(notComplete || serviceRequest.business_unit.$touched) && ( serviceRequest.business_unit.$invalid)"><spring:theme code="text.service.request.form.msg.business.unit"/></span>

  </div>
</div>

<div class="row">
  <div class="col-xs-12 col-md-4 form-group">
    <label for="sr-preferred-contact">Preferred contact method<span class="required">*</span></label>
    <div class="select">
      <span class="arrow"></span>
      <select id="sr-preferred-contact" name="preferred_contact" class ="form-control" ng-model="sr.prefcontact" ng-required="true">
        <option value="" disabled selected>Select</option>
        <option value="Email">Email</option>
      </select>
    </div>
   <%--  <span class="error" ng-show="serviceRequest.preferred_contact.$touched && serviceRequest.preferred_contact.$error.required"><spring:theme code="text.service.request.form.msg.prefered.method"/></span>
   --%>
   <span class="error" ng-show="(notComplete || serviceRequest.preferred_contact.$touched) && ( serviceRequest.preferred_contact.$invalid)"><spring:theme code="text.service.request.form.msg.prefered.method"/></span>
   </div>  

<div class="col-xs-12 col-md-4 form-group" ng-if="sr.prefcontact == 'Email'">
    <label for="sr-email">Customer Email Address<span class="required">*</span></label>
    <input id="sr-email" class="form-control" name="email" type="email" value="${customerEmail }" ng-model="sr.email" ng-init="sr.email = '${fn:replace(customerEmail, "'", "\\'")}'" ng-required="true">
    <span class="error" ng-show="serviceRequest.email.$touched && serviceRequest.email.$error.required"><spring:theme code="text.service.request.form.msg.email.required"/></span>
    <span class="error" ng-show="serviceRequest.email.$dirty && serviceRequest.email.$error.email"><spring:theme code="text.service.request.form.msg.email.valid"/></span>
	<p class="help-block">*Ensure the email address matches an active User ID on the account</p>

</div>
</div>
 