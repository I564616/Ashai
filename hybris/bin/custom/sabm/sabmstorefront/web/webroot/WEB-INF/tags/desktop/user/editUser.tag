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



<c:url value="/register/checkUser/" var="checkUserUrl" scope="request"/>
<input type="hidden" class="checkUserUrl" value="${checkUserUrl }">

<c:url value="/register/getUser/${b2bUnitid }" var="getUser" scope="request"/>
<input type="hidden" class="getUser" value="${getUser }">
<input type="hidden" class="baseUrl" value="<c:url value="/"/>">
<input type="hidden" class="b2bUnitId" value="${b2bUnitid }">

<script id="userData" type="text/json">${ycommerce:generateCustomerJson(customerJson)}</script>
<%-- <user:dummyData/> --%>
 
<h1><spring:theme code="register.customer.edit" /></h1>

<div id="notificationModal" class="notification-modal">
    <!-- Modal content -->
    <div class="notification-modal-content">
        <div class="notification-modal-header">
            <h2>Notifications</h2>
        </div>
        <div class="modal-body">
            <p><spring:theme code="text.notification.your.sms.notifiction.set"/></p>
            <p><spring:theme code="text.notification.your.sms.notifiction.confirmation"/></p>
        </div>
        <div class="notification-modal-footer">
            <button type="button" class="notification-btn notification-btn-primary" id="confirmModal">Confirm</button>
            <button type="button" class="notification-btn notification-btn-cancel" id="cancelModal">Cancel</button>
        </div>
    </div>
</div>


<section class="standard">
    <form:form id="sabmEditUserForm" ng-model="editUser" name="editUser" method="post"  action="${action}">

           <input type="hidden" name="CSRFToken" value="${CSRFToken}">
     <div class="row">
            <div class="col-sm-4">
                <div class="form-group">
                   <label><spring:theme code="register.firstName" /></label>
                      <c:choose>
                        <c:when test="${!bdeUser && customerData.primaryAdmin}">
                            <input type="text" maxlength="255" ng-model="user.firstName" name="firstName" class="form-control" id="register_firstName" readonly="true" required="required"/>
                        </c:when>
                        <c:otherwise>
                       		<input type="text" maxlength="255" ng-model="user.firstName" name="firstName" class="form-control" id="register_firstName" required="required"/>
                        </c:otherwise>
                      </c:choose>                    
                </div>
            </div>

            <div class="col-sm-4">
                <div class="form-group">
                    <label><spring:theme code="register.lastName" /></label>
                    <c:choose>
                        <c:when test="${!bdeUser && customerData.primaryAdmin  }">
                            <input type="text" maxlength="255" ng-model="user.surName" name="surName" class="form-control" id="register_surname" readonly="true" required="required"/>
                        </c:when>
                        <c:otherwise>
                            <input type="text" maxlength="255" ng-model="user.surName" name="surName" class="form-control" id="register_surname" required="required"/>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
<input type="hidden" class="currentEmail" value="${customerData.email }">

        <div class="row">
            <div class="col-sm-4">
                <div class="form-group">
                    <label emailChecker><spring:theme code="register.email" /></label>
                    <c:choose>
                        <c:when test="${ !bdeUser}">
                            <input type="text" id="register_email" readonly="true" ng-model="user.email" name="email" class="form-control"/>
                        </c:when>
                        <c:otherwise>
                            <%-- <input type="email" id="register_email" maxlength="255" ng-model="user.email" ng-pattern="/^([\w!#$%&'*+-\/=\?\^_`{\|}~])+(\.[\w!#$%&'*+-\/=\?\^_`{\|}~]+)*@([\w-])+((\.\w+)+)$/" name="email" path="email"  class="form-control email-checker" required="required"/> --%>
                            <input type="text" id="register_email" readonly="true" ng-model="user.email" name="email" class="form-control"/>
                    <%-- <div ng-show="editUser.email.$touched">
                      <span ng-show="editUser.email.$error.pattern" class="error"><spring:theme code="register.createUser.email.invalid" /></span>
                      <span ng-show="editUser.email.$error.required" class="error"><spring:theme code="register.createUser.email.invalid" /></span>
                    </div> --%>
                        </c:otherwise>
                    </c:choose>
                    
                </div>
            </div>
            <div class="col-sm-4">
                <div class="form-group">
                    <label><spring:theme code="profile.mobileNumber" /></label>
                    <input type="hidden" id="mobileNumber"/>  
                    <input type="hidden" id="phoneNumber" value="${customerData.mobileNumber}"/> 
                    <input type="text" ng-model="user.phoneNumber" ng-blur="invokeUnsavedChangesPopUp()" maxlength="12" name="phoneNumber" placeholder="E.g: 0491 570 156" class="form-control" id="register_phoneNumber" path="phoneNumber" autofocus/>
                    <input type="hidden" name="notificationField" id="notificationField" value="${notifications}" />
                    <span class="phone-number error hide"><spring:theme code="register.phone.invalid" /></span>
                </div>
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
                            <ul id="checkboxes" class="list-checkbox checkbox">
                                <li>
                                    <input id="personal-assistant" ng-model="user.permissions.pa" name="pa" type="checkbox" value="user">
                                    <label for="personal-assistant">
                                    <spring:theme code="register.permissions.create.edit.user" />
                                    </label>
                                </li>
                                <li>
                                    <input id="view-and-pay" ng-model="user.permissions.pay" name="payInvoice" type="checkbox" value="invoice">
                                    <label for="view-and-pay">
                                    <spring:theme code="register.permissions.pay.invoices" />
                                    </label>
                                </li>
                                <li>
                                    <input id="place-orders" ng-model="user.permissions.orders" name="orders" type="checkbox" value="order">
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
                                           <input type="text" ng-model="user.permissions.orderLimit" class="form-control" ng-pattern="/^\d+$/" name="orderLimit" id="order-limit" placeholder="${place}" />
                                         </div>
                                     </div>
                                     <span class="orderLimit_error error" style="display: none"><spring:theme code="register.orderLimit.invalid" /></span>
                                 </div>
                             </div>
                            </div>
                    </div>
                </div>


                <!-- Business Units -->
                <div class="row margin-top-30">
                    <div class="col-sm-12">

                       <h3><spring:theme code="register.businessUnit" /></h3>
                       <p id="businessUnit-name" style="margin-bottom: 10px;">
                            <spring:theme code="register.businessUnit.select" />
                            <span ng-show="!noInactiveBU" style="display: block; padding-top: 5px;"><spring:theme code="register.businessUnit.greyed.out" /></span>
							<c:if test="${bdeUser}">
							<span ng-show="!noInactiveBU" style="display: block; padding-top: 15px;"><spring:theme code="register.businessUnit.customersearch" /></span>
							</c:if>
                            <span style="display: block; padding-top: 25px;"><spring:theme code="register.businessUnit.revoke" /></span>
                        </p>
                       <input type="hidden" id="businessUnit-id-hide" name="businessUnit" value="${businessUnit.uid }"/>

                        <div ng-init="isAllSelected(user.states)">
                            <div ng-show="isAllInactive">
                                <b><u><span style="cursor:pointer;" ng-click="selectAllState(true, user.states)"><spring:message code="register.editUser.disable.button.text.enable" /></span></u></b> |
                                <b><u><span style="cursor:pointer;" ng-click="openDisableModal(user.states)"><spring:message code="register.editUser.disable.button.text.disable" /></span></u></b>
                                <br />
                            </div>
                        </div>

                        <div>
                            <p class="margin-top-10">
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
                                    <li ng-repeat="state in user.states" ng-class="{disabled : state.disabled}" ng-init="state.disabled = stateDisabled(state)">  <!-- State Checkbox -->
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
                                               <input id="bu-{{state.isocode}}-{{$index}}" type="checkbox" ng-disabled="!venue.active" name="check" ng-model="venue.selected" ng-change="state.selected=stateSelected(state); isAllSelected(user.states)"/> <!-- Venue Checkbox -->
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



        <div class="row">
            <div class="col-sm-4">
               <%-- <h3 class="offset-bottom-small"><spring:theme code="register.customer.edit.status"/></h3>
               
               <ul class="list-radio radio offset-bottom-small">
                   <li class="offset-bottom-xsmall">
                       <input id="userActive" type="radio" ng-model="user.active" name="radioActive" ng-value="true">
                       <label for="userActive">
                           <spring:theme code="text.businessUnitDetail.userlist.active" />
                       </label>
                   </li>
                   <li>
                       <input id="userInactive" type="radio" ng-model="user.active" name="radioActive" ng-value="false">
                       <label for="userInactive">
                       <spring:theme code="text.businessUnitDetail.userlist.inactive" />
                       </label>
                   </li>
               </ul> --%>

               <a href="#deleteUserPopup" class="regular-popup delete-user-launch inline"><spring:theme code="text.businessUnitDetail.userlist.delete" /></a>
            </div>
        </div>

        <%-- <p><a class="inline" id="editpage-remove-user" href="#remove-user"><spring:theme code="text.business.unit.delete.user.link.title" text="Delete Customer"/></a></p> --%>

        <div class="row margin-top-20">
            <div class="col-sm-6">
                <h3><spring:theme code="register.conditions" /></h3>
                <p style="margin-bottom: 10px;">
                            <spring:theme code="register.conditions.checkbox.message" />
                </p>
                
            </div>
        </div>
        <div class="row">
            <div class="col-sm-12">
            <ul class="list-checkbox checkbox">
                <li>
                    <input id="agreeTerms" type="checkbox" ng-change="invokeUnsavedChangesPopUp()" ng-model="user.agreeTerms" name="check" value="agreeTerms" required="required">
                    <label for="agreeTerms">
                        <spring:theme code="register.conditions.agree" /><a href='<c:url value="/termsAndConditions"/>' class="inline">&nbsp;<spring:theme code="register.conditions.onlineOrdering" /></a>
                    </label>
                </li>
                <li>
                    <input id="ofAge" type="checkbox" ng-model="user.ofAge" ng-change="invokeUnsavedChangesPopUp()" name="check" value="ofAge" required="required">
                    <label for="ofAge">
                    <spring:theme code="register.conditions.create.age" />
                    </label>
                </li>
            </ul>
                <ul class="list-button margin-top-30">
                    <li>
                      <!-- Submit Button -->
                        <button ng-disabled="!profileFormValid" id="register-save" type="button" class="btn btn-primary"><spring:theme code="register.save" /></button>

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
        <spring:message code="forgottenPwd.popup.edituser" var="edituser" />
        <spring:message code="text.yourBusiness.businessUnits.title" var="title" />
        <user:dialog title="${title}" message="${edituser}"/>
    </form:form>

	<user:deleteUserPopup/>
	<user:disableUserPopup/>
</section>
<%-- <user:popupDeleteCustomer /> --%>
