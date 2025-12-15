<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="service" tagdir="/WEB-INF/tags/desktop/service"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<input type="hidden" class="baseUrl" value="<c:url value="/"/>">
<div class="registration-request offset-bottom-medium" ng-controller="registrationRequestCtrl" ng-init="registrationRequestInit()" ng-cloak>
  	<div class="h1"><spring:theme code="text.registration.request.title"/></div>
	<div><spring:theme code="text.registration.request.description" /></div>
  	<form:form name="registrationRequest" novalidate="novalidate">
  		<div class="row">
 			<div class="col-xs-12 col-md-4 form-group">
		    <label for="rr-firstName"><spring:theme code="text.registration.request.first.name.title"/><span class="required">*</span></label>
		    <input id="rr-firstName" class="form-control" name="firstName" type="text" ng-model="rr.firstName" ng-required="true" >
		    <span class="error" ng-show="(firstNameError || registrationRequest.firstName.$touched) && registrationRequest.firstName.$error.required"><spring:theme code="text.registration.request.error.first.name.message"/></span>
			</div>
		  <div class="col-xs-12 col-md-4 form-group">
		    <label for="rr-lastName"><spring:theme code="text.registration.request.last.name.title"/><span class="required">*</span></label>
		    <input id="rr-lastName" class="form-control" name="lastName" type="text" ng-model="rr.lastName" ng-required="true" >
		    <span class="error" ng-show="(lastNameError || registrationRequest.lastName.$touched) && registrationRequest.lastName.$error.required"><spring:theme code="text.registration.request.error.last.name.message"/></span>
		  </div>
  		</div>
  		<div class="row">
  			<div class="col-xs-12 col-md-4 form-group">
			    <label for="rr-email"><spring:theme code="text.registration.request.email.title"/><span class="required">*</span></label>
			    <input id="rr-email" class="form-control" name="email" type="email" ng-model="rr.email" ng-required="true" >
			    <span class="error" ng-show="(emailError || registrationRequest.email.$touched )&& registrationRequest.email.$error.required"><spring:theme code="text.registration.request.error.email.message"/></span>
			    <span class="error" ng-show="registrationRequest.email.$dirty && registrationRequest.email.$error.email"><spring:theme code="text.registration.request.form.msg.email.valid"/></span>
			  </div>
			  <div class="col-xs-12 col-md-4 form-group">
			    <label for="rr-cubAccount"><spring:theme code="text.registration.request.cub.account.title"/></label>
			    <input id="rr-cubAccount" class="form-control" name="cubAccount" type="text" ng-model="rr.cubAccount" >
			  </div>
  		</div>
  		<div class="row">
  			<div class="col-xs-12 col-md-4 form-group">
			    <label for="rr-accountName"><spring:theme code="text.registration.request.account.name.title"/><span class="required">*</span></label>
			    <input id="rr-accountName" class="form-control" name="accountName" type="text" ng-model="rr.accountName" ng-required="true" >
			    <span class="error" ng-show="(accountNameError || registrationRequest.accountName.$touched) && registrationRequest.accountName.$error.required"><spring:theme code="text.registration.request.error.account.name.message"/></span>
			  </div>
			  <div class="col-xs-12 col-md-4 form-group">
			    <label for="rr-workPhoneNum"><spring:theme code="text.registration.request.work.phone.num.title"/><span class="required">*</span></label>
			    <input id="rr-workPhoneNum" class="form-control" name="workPhoneNum" type="text" ng-pattern="/^\d+$/" ng-model="rr.workPhoneNum" ng-required="true" >
			    <span class="error" ng-show="(workPhoneNumError || registrationRequest.workPhoneNum.$touched) && registrationRequest.workPhoneNum.$error.required"><spring:theme code="text.registration.request.error.work.phone.num.message"/></span>
			    <span class="error" ng-show="registrationRequest.workPhoneNum.$touched && registrationRequest.workPhoneNum.$error.pattern"><spring:theme code="text.registration.request.form.msg.phone.pattern"/></span>
			  </div>
  		</div>
  		<div class="row">
  			<div class="col-xs-12 col-md-4 form-group">
			    <label for="rr-mobilePhoneNum"><spring:theme code="text.registration.request.mobile.phone.num.title"/></label>
			    <input id="rr-mobilePhoneNum" class="form-control" name="mobilePhoneNum" type="text" ng-pattern="/^\d+$/" ng-model="rr.mobilePhoneNum" >
			    <span class="error" ng-show="registrationRequest.mobilePhoneNum.$touched && registrationRequest.mobilePhoneNum.$error.pattern"><spring:theme code="text.registration.request.form.msg.phone.pattern"/></span>
			  </div>
  		</div>
  		<div class="row checkbox-control-group" >
  			<div class="col-xs-12 col-md-4 form-group">
  	       	<label><spring:theme code="text.registration.request.account.type.title"/><span class="required">*</span></label>
  	       	<span class="error" ng-show="accoutTypeError"><spring:theme code="text.registration.request.error.account.type.message"/></span>
  	       		<div class="">
  	       			<div class="checkbox-control">
	  	       			<label for="rr-accoutType1"><spring:theme code="text.registration.request.account.type.business.owner.title"/></label>
	  	       			<input id="rr-accoutType1" class="" name="accoutType1" value="Business Owner" type="checkbox" ng-click="updateCheckBoxChecks(registrationRequest,'accoutType')" ng-model="rr.accoutType1" ></input>
           			</div>
           			<div class="checkbox-control">
           				<label for="rr-accoutType2"><spring:theme code="text.registration.request.account.type.area.manager.title"/></label>
           				<input id="rr-accoutType2" class="" name="accoutType2" value="Area Manager" type="checkbox" ng-click="updateCheckBoxChecks(registrationRequest,'accoutType')" ng-model="rr.accoutType2" ></input>
           			</div>
           			<div class="checkbox-control">
	  	       			<label for="rr-accoutType3"><spring:theme code="text.registration.request.account.type.venue.manager.title"/></label>
	  	       			<input id="rr-accoutType3" class="" name="accoutType3" value="Venue Manager" type="checkbox" ng-click="updateCheckBoxChecks(registrationRequest,'accoutType')" ng-model="rr.accoutType3" ></input>
           			</div>
           			<div class="checkbox-control">
           				<label for="rr-accoutType4"><spring:theme code="text.registration.request.account.type.staff.member.title"/></label>
           				<input id="rr-accoutType4" class="" name="accoutType4" value="Staff Member" type="checkbox" ng-click="updateCheckBoxChecks(registrationRequest,'accoutType')" ng-model="rr.accoutType4" ></input>
           			</div>
    				</div>
  		      <div>
  		    </div> 
  		  </div>		
  		</div>
  		<div class="row checkbox-control-group">
  			<div class="col-xs-12 col-md-4 form-group">
  	       	<label><spring:theme code="text.registration.request.access.type.title"/><span class="required">*</span></label>
  	       	<span class="error" ng-show="accessTypeError"><spring:theme code="text.registration.request.error.access.type.message"/></span>
           		<div class="">
           			<div class="checkbox-control">
	  	       			<label for="rr-accessType1"><spring:theme code="text.registration.request.access.type.ordering.title"/></label>
	  	       			<input id="rr-accessType1" class="" name="accessType1" value="Ordering" type="checkbox" ng-click="updateCheckBoxChecks(registrationRequest,'accessType')" ng-model="rr.accessType1" ></input>
           			</div>
           			<div class="checkbox-control">
	  	       			<label for="rr-accessType2"><spring:theme code="text.registration.request.access.type.view.or.pay.invoices.title"/></label>
	  	       			<input id="rr-accessType2" class="" name="accessType2" value="View Or Pay Invoices" type="checkbox" ng-click="updateCheckBoxChecks(registrationRequest,'accessType')" ng-model="rr.accessType2" ></input>
           			</div>
           			<div class="checkbox-control">
	  	       			<label for="rr-accessType3"><spring:theme code="text.registration.request.access.type.manage.and.set.up.users.title"/></label>
	  	       			<input id="rr-accessType3" class="" name="accessType3" value="Manage and Set up Users" type="checkbox" ng-click="updateCheckBoxChecks(registrationRequest,'accessType')" ng-model="rr.accessType3" ></input>
           			</div>
   				</div>
    			<div>
  		    </div> 
  		  </div>		
  		</div>
  		<div class="row checkbox-control-group">
  			<div class="col-xs-12 col-md-4 form-group checkbox-control-single">
  				<div>
			    <label for="rr-haveMoreAccount"><spring:theme code="text.registration.request.more.than.one.account.title"/></label>
			    <input id="rr-haveMoreAccount" class="" name="haveMoreAccount" type="checkbox" value="Yes" ng-model="rr.haveMoreAccount" >
			    </div>
			      <span><spring:theme code="text.registration.request.more.than.one.account.description"/></span>
			  
			  </div>
  		</div>
  		<div class="clearfix"></div>
  		<div class="row">
  			<div class="col-xs-12 col-md-6 form-group">
  				<c:url var="termsUrl" value="/termsAndConditions"/>
  				<span><spring:theme code="text.registration.terms" arguments="${termsUrl}"/></span>
  			<br>
  			<div class="row checkbox-control-group">
  			<div class="col-xs-12 form-group">
  			<span class="error" ng-show="termsofuseError"><spring:theme code="text.registration.request.error.tc.type.message"/></span>
  	       			<div class="checkbox-control">
	  	       			<td class="half-width"><spring:theme code="text.registration.agree.terms" /></td>
	  	       			<input id="rr-termsofuse1" class="" name="termsofuse1" value="TC" type="checkbox" ng-click="updateCheckBoxChecks(registrationRequest,'termsofuse')" ng-model="rr.termsofuse1" ></input>
           			</div>
         	</div>
         	</div>
      </div>
	      <div class="form-group">
	      	<button type="button" ng-click="checkValid(registrationRequest)" class="btn btn-primary"><spring:theme code="text.registration.send.button"/></button>
	      		<span class="error-bold" ng-show="notComplete">
						<spring:theme code="text.registration.request.form.msg.errors"/></button>
	      		</span>
	      		<span class="error-bold" ng-show="formHaveError">
						<spring:theme code="text.registration.request.form.msg.errors"/></button>
	      		</span>
      	</div>
      </div>

        <!-- Popup dialog -->
        <spring:message code="forgottenPwd.popup.register" var="register" />
        <spring:message code="register.submit" var="title" />
        <user:dialog title="${title}" message="${register}" />
  	</form:form>

</div>