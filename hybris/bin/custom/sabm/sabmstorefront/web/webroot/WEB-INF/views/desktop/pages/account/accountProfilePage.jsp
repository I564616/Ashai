<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>


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


<div class="container personal-profile offset-bottom-large">

	<div id="alert-mobileNumber" class="alert negative hide"><spring:theme code="text.notification.mobileNumber.errorMessage"/></div>

		<h1><spring:theme code="profile.title" text="Your Profile"/></h1>
	
		<c:set var="isPrimaryAdminZadp" value="${(customerData.isZadp eq true) and (customerData.primaryAdmin eq true)}"/>
	 
		<c:if test="${isPrimaryAdminZadp}">
			<c:url value="/businessEnquiry" var="businessEnquiry"/>
			<p class="offset-bottom-small"><spring:theme code="profile.primaryAdmin.text" arguments="${businessEnquiry}"/></p>
		</c:if>

		<c:if test="${not isPrimaryAdminZadp}">
			<p class="offset-bottom-small"><spring:theme code="profile.change.profile.contects.list.text"/></p>
			<ul>
				<c:forEach items="${assistants}" var="assistant">
					<li class="offset-bottom-xxsmall">
						<c:choose>
							<c:when test="${empty assistant.firstName and empty assistant.lastName}">
								<spring:theme code="profile.change.profile.contects.list.item.noname" arguments="${assistant.email},${assistant.email}" />
							</c:when>
							<c:otherwise>
								<spring:theme code="profile.change.profile.contects.list.item" arguments="${assistant.firstName},${assistant.lastName},${assistant.email},${assistant.email}" />
							</c:otherwise>
						</c:choose>
					</li>
				</c:forEach>
			</ul>
		</c:if>
	
	<section class="offset-bottom-small margin-top-40">
		<c:url value="/your-business/receiveUpdates" var="updateReceiveUpdatesUrl"/>
		 <input id="profile_save_success_topMessage" type="hidden" value="<spring:theme code="login.error.account.not.found.title"/>">
	    <form:form action="${updateReceiveUpdatesUrl}" method="post" id="updateProfileForm">
	        <div class="row">
	            <div class="col-sm-6 col-md-4">
	                <div class="form-group has-error">
	                    <label for="firstName1"><spring:theme code="profile.firstName" text="First Name"/></label>
	                    <input type="text" class="form-control" id="firstName1" value="${customerData.firstName}" disabled>
	                </div>
	            </div>
	            <div class="col-sm-6 col-md-4">
	                <div class="form-group">
	                    <label for="surname1"><spring:theme code="profile.lastName" text="Surname"/></label>
	                    <input type="text" class="form-control" id="surname1"  value="${customerData.lastName}" disabled>
	                </div>
	            </div>
	        </div>
	        <div class="row">
	            <div class="col-sm-6 col-md-4">
	                <div class="form-group">
	                    <label for="exampleInputEmail1"><spring:theme code="profile.email" text="Email"/></label>
	                    <input type="email" class="form-control" id="exampleInputEmail1" placeholder="Email" value="${customerData.displayUid}" disabled>
	                </div>
	            </div>
	            <div class="col-sm-6 col-md-4">
	                <div class="form-group">
				        <label for="mobileNumber"><spring:theme code="profile.mobileNumber" text="Mobile Number"/></label>
				        <input type="hidden" id="customerMobileNumber" value="${customerData.mobileNumber}" />
		                <input type="text" class="form-control" autocomplete="off" maxlength="12" id="mobileNumberField" placeholder="E.g: 0491 570 156" />
				        <input type="hidden" name="mobileNumber" id="mobileNumber" value="${customerData.mobileNumber}" />
				        <input type="hidden" name="notificationField" id="notificationField" value="${notifications}" />
				    </div>         
	            </div>
	        </div>
	        
	        <!---  
	        <div class="row">
	            <div class="col-sm-6 col-md-4">
					<div class="form-group">
						<label for="preferredContactNumber"><spring:theme code="profile.preferredContactNumber" text="Preferred Office Contact Number" /></label>
				        <input type="hidden" id="customerBusinessPhoneNumber" value="${customerData.businessContactNumber}" />
						<input type="text" class="form-control" autocomplete="off" id="businessPhoneNumber" name="businessPhoneNumber" value="${customerData.businessContactNumber}"/>
					</div>
	            </div>
			</div>		   
			--->
			<c:if test="${!isNAPGroup}">
			<sec:authorize access="hasAnyRole('ROLE_B2BORDERCUSTOMER')">
		        <div class="row margin-top-30">
		            <div class="col-sm-5">
		                <h3><spring:theme code="profile.order.limit" text="Order limit"/></h3>
		                <p style="margin: 0">
		                    $<fmt:formatNumber value="${customerData.orderLimit}" pattern="#,#00"/>&nbsp;${customerData.currency.isocode}
		                </p>
		            </div>
		        </div>
	        </sec:authorize>
	        </c:if>
	        <div class="row margin-top-20">
	            <div class="col-sm-12">
	                <div class="checkbox">
	                 	<input id="profile_receiveUpdates" type="hidden" name="receiveUpdates" value="${customerData.receiveUpdates ? 'true' : 'false'}">
	                    <input id="confirm1" type="checkbox" ${customerData.receiveUpdates ? 'checked="checked"' : ''}>
	                    <label for="confirm1"> 
	                    		<spring:theme code="profile.receive.updates.description" text=""/>
	                    </label>
	                </div>

	                <div class="checkbox offset-bottom-large">
	                 	<input id="profile_receiveUpdatesForSMS" type="hidden" name="receiveUpdatesForSms" value="${customerData.receiveUpdatesForSms ? 'true' : 'false'}">
	                    <input id="confirm2" type="checkbox" ${customerData.receiveUpdatesForSms ? 'checked="checked"' : ''}>
	                    <label for="confirm2">
	                    		<spring:theme code="profile.receive.sms.updates.description" text=""/>
	                    </label>
	                </div>

	                <div>
	                	<c:set var="defaultUnit" value="${customerData.defaultB2bUnit.uid }"></c:set>
	                	<c:set var="b2bUnits" value="${customerData.branches }"></c:set>
			            <div>
			                <h3><spring:theme code="text.b2bunit.default" text=""/></h3>
			            </div>
			            <div>
			            	<c:if test="${fn:length(b2bUnits)>1}">
			            	<p><spring:theme code="text.b2bunit.default.select" text=""/><br>
			            	<spring:theme code="text.b2bunit.default.select.note" text=""/></p><br/>
			            	</c:if>
			            </div>
			            <div>       		
			              <c:choose>
									<c:when test="${fn:length(b2bUnits)<=1}">
									<ul>
										<c:forEach items="${b2bUnits }" var="b2bUnit" >   	
					            			<li class="margin-top-10 offset-bottom-xsmall">
					            				<label>${b2bUnit.name }</label>
					            			</li>
					            		</c:forEach>
									</c:when>
									<c:otherwise>
									<ul class="radio">
										<li class="offset-bottom-small">
			            				<input type="radio" name="defaultUnit" id="previous" value="previous" ${customerData.remenberPreviousUnit ? 'checked="checked"':'' }  />
			            				<label for="previous"><spring:theme code="text.b2bunit.default.previous.b2bunit" text=""/></label>
			            			</li>
										<c:forEach items="${b2bUnits }" var="b2bUnit" >   	
					            			<li class="offset-bottom-xsmall">
					            				<input type="radio" name="defaultUnit" id="${b2bUnit.uid }" value="${b2bUnit.uid }"  ${customerData.remenberPreviousUnit || (b2bUnit.uid != defaultUnit) ? '': 'checked="checked"'}/>
					            				<label for="${b2bUnit.uid }">${b2bUnit.name }</label><br/>
					            			</li>
					            		</c:forEach>
									</c:otherwise>
								</c:choose>
			            	</ul>
			            </div>
			        </div>
	                <div class="margin-top-30">
						<button type="submit"
							class="btn btn-cancel save-profile offset-bottom-small"
							disabled="disabled">
							<spring:theme code="profile.submit" text="SAVE" />
						</button>
						<br>

						<c:url value="/your-business" var="yourBusinessUrl" />
	                	<a href="${yourBusinessUrl}" class="inline"><spring:theme code="text.account.profile.cancel" /></a>
	                </div>

	            </div>
	        </div>

	        <spring:message code="forgottenPwd.popup.edituser" var="edituser" />
            <spring:message code="popup.profile.title" var="title" />
            <user:dialog title="${title}" message="${edituser}" />
	   </form:form>
	</section>
</div>