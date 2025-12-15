<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<c:url value="/register/checkUser" var="checkUserUrl" scope="request"/>
<input type="hidden" class="checkUserUrl" value="${checkUserUrl }">

<c:url value="/register/createUser/${b2bUnitid }" var="createUserUrl" scope="request"/>
<input type="hidden" class="createUserUrl" value="${createUserUrl }">

<c:url value="createUserPage" var="createUserPage" scope="request"/>
<input type="hidden" class="createUserPage" value="${createUserPage }">
 
<input type="hidden" class="baseUrl" value="<c:url value="/"/>">
<input type="hidden" class="b2bUnitId" value="${b2bUnitid }">

<script id="userData" type="text/json">${ycommerce:generateCustomerJson(customerJson)}</script>

<h1><spring:theme code="register.new.customer" /></h1>
<section class="standard">

    <form:form id="sabmCreateUserForm" name="createUser" method="post" modelAttribute="sabmCreateUserForm" action="${action}" novalidate="novalidate">
        <input type="hidden" name="CSRFToken" value="${CSRFToken}">
        <div class="row">
            <div class="col-sm-4">
                <div class="form-group">
                   <label><spring:theme code="register.firstName" /></label>
                   <form:input type="text" maxlength="255" ng-model="user.firstName" name="firstName" path="firstName" class="form-control" id="register_firstName" ng-pattern="/^[a-zA-Z\s\-]*$/" required="required"/>
                    <span ng-show="createUser.firstName.$error.required && createUser.firstName.$touched" class="error"><spring:theme code="register.Name.invalid" /></span>
                    <span ng-show="createUser.firstName.$error.pattern" class="error"><spring:theme code="register.Name.invalid" /></span> 
                </div>
            </div>

            <div class="col-sm-4">
                <div class="form-group">
                    <label><spring:theme code="register.lastName" /></label>
                    <form:input type="text" maxlength="255" ng-model="user.surName" name="surName" path="surName" class="form-control" id="register_surName" ng-pattern="/^[a-zA-Z\s\-\']*$/" required="required"/>
                    <span ng-show="createUser.surName.$error.required && createUser.surName.$touched" class="error"><spring:theme code="register.Name.invalid" /></span>
                    <span ng-show="createUser.surName.$error.pattern" class="error"><spring:theme code="register.Name.invalid" /></span>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-4">
                <div class="form-group">
                    <label emailChecker><spring:theme code="register.email" /></label>
                    <%-- <form:input type="email" id="register_email" maxlength="255" ng-model="user.email" ng-pattern="/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/" name="email" path="email" ng-blur="emailChecker(user.email)" class="form-control email-checker" required="required"/> --%>
                    <form:input type="email" id="register_email" maxlength="255" ng-model="user.email" ng-pattern="/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/" name="email" path="email" ng-blur="emailChecker(user.email)" class="form-control email-checker" required="required"/>
                    <div ng-show="createUser.email.$touched">
                      <span ng-show="createUser.email.$error.pattern" class="error"><spring:theme code="register.createUser.email.invalid" /></span>
                      <span ng-show="createUser.email.$error.required" class="error"><spring:theme code="register.createUser.email.invalid" /></span>
                    </div>
                </div>
            </div>
            <div class="col-sm-4">
                <div class="form-group">
                    <label><spring:theme code="profile.mobileNumber" /></label>
                    <input type="hidden" id="mobileNumber" />
                    <form:input type="text" maxlength="12" ng-model="user.phoneNumber" name="phoneNumber" placeholder="E.g: 0491 570 156" path="" ng-blur="invokeUnsavedChangesPopUp()" class="form-control" id="register_phoneNumber" />
                    <span class="phone-number error hide"><spring:theme code="register.phone.invalid" /></span>
                </div>
            </div>
        </div>
        <div class="row">
          <div class="col-xs-12">
                <!-- Error exists in other ZADP -->
                <div ng-show="user.exists && !user.thisZADP" class="error-bold error"><spring:theme code="register.email.invalid.checkout" /></div>
                <!-- Error not active -->
                <div ng-show="!user.active && user.thisZADP"  class="error-bold error"><spring:theme code="register.error.not.active" arguments="#administratorsForPopup"/></div>
                <!-- User Exists in this ZADP -->
                <div ng-show="user.active && user.exists && user.thisZADP" class="error-bold error-bold-blue error"><spring:theme code="register.createUser.exists.in.bu" /></div>
                
                <div ng-show="user.self" class="error-bold error-bold error"><spring:theme code="register.createUser.email.self" /></div>
          </div>
        </div>

      <!-- Permissions -->
       <div class="row margin-top-30">
            <div class="col-sm-12">
                <h3><spring:theme code="register.permissions" /></h3>
                <p class="offset-bottom-small">
                	<spring:theme code="register.permissions.describe" />
                </p>
                <div>
                	<input type="hidden" id="personal-assistant-hide" name="personalAssistant" value="user"/>
	                <input type="hidden" id="place-orders-hide" name="canPlaceOrder" value="order"/>
	                <input type="hidden" id="view-and-pay-hide" name="canViewPayInvoice" value="invoice"/>
                    <ul id="checkboxes" class="list-checkbox checkbox">
                    	<li>
                            <input id="personal-assistant" ng-model="user.permissions.pa" name="pa" type="checkbox" value="user" ${customerData.personalAssistant }>
                            <label for="personal-assistant">
                            <spring:theme code="register.permissions.create.edit.user" />
                            </label>
                        </li>
                        <li>
                            <input id="view-and-pay" ng-model="user.permissions.pay" name="payInvoice" type="checkbox" value="invoice" ${customerData.canViewPayInvoice }>
                            <label for="view-and-pay">
                            <spring:theme code="register.permissions.pay.invoices" />
                            </label>
                        </li>
                        <li>
                            <input id="place-orders" ng-model="user.permissions.orders" name="orders" type="checkbox" value="order" ${customerData.canPlaceOrder }>
                            <label for="place-orders">
                            <spring:theme code="register.permissions.place.orders" />
                            </label>
                        </li>
                    </ul>
                    <div class="toggle-slide">
                         <div class="form-group toggle-body" ng-show="user.permissions.orders">
                             <div class="row">
                                 <div class="col-sm-4">
                                   <label for="order-limit" class="sr-only"><spring:theme code="register.orderLimit" /></label>
                                   <p>
                                     <spring:theme code="register.orderLimit" />
                                   </p>
                                   <spring:theme var="place" code="register.roles.staffUsers.placeholder" />
                                   <form:input type="text" ng-model="user.permissions.orderLimit" ng-pattern="/^\d+$/" path="orderLimit" class="form-control" name="orderLimit" id="order-limit" value="${customerData.orderLimit }" placeholder="${place}" />
                                 </div>
                             </div>
                             <span class="error" ng-show="createUser.orderLimit.$error.pattern"><spring:theme code="register.orderLimit.invalid" /></span>
                         </div>
                     </div>
	        	    </div>
            </div>
        </div>

        <!-- Business Units -->
        <div class="row margin-top-30">
            <div class="col-sm-12">

               <h3><spring:theme code="register.businessUnit" /></h3>
               <p id="businessUnit-name" ng-show="user.states.length > 1 || user.states[0].b2bunits.length > 1" class="offset-bottom-medium"><spring:theme code="register.businessUnit.select" /><br>
               <span ng-show="user.active && user.exists && user.thisZADP"><spring:theme code="register.businessUnit.greyed.out" /></span></p>


              <div ng-show="user.states.length == 1 && user.states[0].b2bunits.length == 1" class="offset-bottom-small">{{user.states[0].b2bunits[0].name}}</div>
              
              <div ng-show="user.states.length > 1 || user.states[0].b2bunits.length > 1">
                 <p class="">
                   ${businessUnit.name}&nbsp;
                   <a class="collapse-heading collapse-heading-link" role="button" data-toggle="collapse1" href="#bu-collapse" aria-expanded="false" aria-controls="bu-nsw-collapse">
                     <svg class="icon-arrow-right icon-inline">
                         <use xlink:href="#icon-arrow-right"></use>
                     </svg>
                   </a>
                 </p>

                 <div class="collapse1 clearfix in" id="bu-collapse">

                   <div class="checkbox clearfix">
                     <div class="bu-selector">
                      
                       <ul class="list-v-standard">

                         <li ng-repeat="state in user.states" ng-class="{disabled : state.disabled}" ng-init="state.disabled = stateDisabled(state)">                        
                             <input id="bu-{{state.isocode}}" type="checkbox" name="check" ng-change="selectAll(state)" ng-model="state.selected" ng-init="state.selected=stateSelected(state)" ng-disabled="state.disabled"/>
                             <label for="bu-{{state.isocode}}" class="bu-selector-label">
                               {{state.isocode}}
                             </label>
                             <a class="collapse-heading collapse-heading-link" role="button" data-toggle="collapse2" href="#bu-{{state.isocode}}-collapse" aria-expanded="false" aria-controls="bu-{{state.isocode}}-collapse">
                               <svg class="icon-arrow-right icon-inline">
                                   <use xlink:href="#icon-arrow-right"></use>
                               </svg>
                             </a>
                             <div class="dependant-sub clearfix collapse2 in" id="bu-{{state.isocode}}-collapse">
                               <ul class="list-v-standard">
                                 <li ng-repeat="venue in state.b2bunits" ng-class="{disabled : !venue.active}">
                                     <input id="bu-{{state.isocode}}-{{$index}}" type="checkbox" ng-disabled="!venue.active" name="check" ng-model="venue.selected" ng-change="state.selected=stateSelected(state)"/> <!-- Venue Checkbox -->
                                     <label for="bu-{{state.isocode}}-{{$index}}">
                                       <span class="clamp-1 n-text">
                                        {{venue.name}}
                                      </span>
                                     </label>
                                  </li>
                                  </ul>
                             </div>
                         </li>
                       </ul>
                     </div>
                   </div>
                </div>
              </div>

            </div>
        </div>
        <hr>
        <div class="row margin-top-20">
            <div class="col-sm-6">
                <h3 class="offset-bottom-small"><spring:theme code="register.conditions" /></h3>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-12">
            <ul class="list-checkbox checkbox">
                <li>
                    <input id="agreeTerms" type="checkbox" ng-model="user.agreeTerms" ng-change="invokeUnsavedChangesPopUp()" name="check" value="agreeTerms" required />
                    <label for="agreeTerms">
                        <spring:theme code="register.conditions.agree" />&nbsp;<a href='<c:url value="/termsAndConditions"/>' class="inline"><spring:theme code="register.conditions.onlineOrdering" /></a>
                    </label>
                </li>
                <li>
                    <input id="ofAge" type="checkbox" ng-model="user.ofAge" ng-change="invokeUnsavedChangesPopUp()" name="check" value="ofAge" required />
                    <label for="ofAge">
                    <spring:theme code="register.conditions.create.age" />
                    </label>
                </li>
            </ul>
                <ul class="list-button margin-top-30">
                    <li>
                      <!-- Submit Button -->
                        <button id="register-save" type="button" ng-disabled="!profileFormValid || user.exists && (!user.active || !user.thisZADP)" ng-click="createUserSubmit(createUser)" class="btn btn-primary"><spring:theme code="register.save" /></button>
                        <div ng-show="notComplete" class="error-bold error"><spring:theme code="register.error" /></div>
                    </li>
                </ul>
                <p class="margin-top-20">
                	<c:choose>
                		<c:when test="${not empty b2bUnitid}">
                			<c:url value="/your-business/unitsdetails/${b2bUnitid }" var="cancelUrl"/>
                		</c:when>
                		<c:otherwise>
                			<c:url value="/your-business/businessunits" var="cancelUrl"/>
                		</c:otherwise>
                	</c:choose>
                    <a href="${cancelUrl }" class="inline"><spring:theme code="register.cancel" /></a>
                </p>
            </div>
        </div>

       <!-- Popup dialog -->
       <spring:message code="forgottenPwd.popup.register" var="register" />
       <spring:message code="text.yourBusiness.businessUnits.title" var="title" />
       <user:dialog title="${title}" message="${register}" />
    </form:form>

    <div id="administratorsForPopup" class="admins-for-popup mfp-hide">
      <h2 class="h1"><spring:theme code="register.permissions.administrators" arguments="{{user.email}}"/></h2>
      <p><spring:theme code="register.popup.administrator" /></p>
      <ul>
        <li class="offset-bottom-xsmall" ng-repeat="admin in user.admins"><span>{{admin.name}};</span> <a href="mailto:{{admin.email}}" class="inline"><p class="break-word">{{admin.email}}</p></a></li>
      </ul>
      <button onclick="$.magnificPopup.close()" class="btn btn-primary margin-top-40"><spring:theme code="register.popup.administrator.ok"/></button>
    </div>
</section>
