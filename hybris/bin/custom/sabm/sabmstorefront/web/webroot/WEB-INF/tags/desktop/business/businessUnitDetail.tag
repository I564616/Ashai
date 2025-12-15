<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="business" tagdir="/WEB-INF/tags/desktop/business"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user"%>
<c:url value="/your-business/customer_active" var="customerActiveUrl" scope="session" />
<c:url value="/your-business/remove_customer" var="removeCustomerUrl" scope="session" />
<c:url value="/your-business/unitsdetails/${b2bUnit.uid}" var="unitsdetailsUrl" scope="session" />
<c:url value="/register/createUser/${b2bUnit.uid}" var="createUserUrl" scope="session" />

<c:set value="" var="dealConditionProductCode"/>

  <form action="${removeCustomerUrl}" method="post" id="_sabmCustomerActiveForm" class="_sabmCustomerActiveForm">
		<input type="hidden" id="businessCustomerUid" name ="businessCustomerUid" value=""/>
		<input type="hidden" id="businessCustomerActive" name ="businessCustomerActive" value=""/>
		<input type="hidden" id="businessUnitId" name ="businessUnitId" value="${b2bUnit.uid}"/>
		<input type="hidden" name="CSRFToken" value="${CSRFToken}">
	</form>

  <form action="${createUserUrl}" method="post" id="_sabmcreateUserForm" class="_sabmcreateUserForm">
  <input type="hidden" name="CSRFToken" value="${CSRFToken}">
 </form>

<section ng-controller="formsCtrl" ng-init="init()">
	<div class="row">
	    <div class="col-sm-10">

		    <h2 class="h1">${b2bUnit.name}</h2>
		    <p>
		        <spring:theme code="text.businessUnitDetail.manageaddresses.title1"/>
		    </p>
		    <p>
		       <spring:theme code="text.businessUnitDetail.manageaddresses.title2" arguments="/businessEnquiry" />
		    </p>
		</div>
	</div>
    <div class="margin-top-30 offset-bottom-xxlarge">
       <table class="table sortable table-highlight" data-sort="true" data-page-size="1000">
             <thead>
        <tr class="row-highlight">
                <th data-toggle="true"><spring:theme code="text.businessUnitDetail.addresseslist.line1"/></th>
                <th data-hide="phone,tablet"><spring:theme code="text.businessUnitDetail.addresseslist.line2"/></th>
                <th><spring:theme code="text.businessUnitDetail.addresseslist.town"/></th>
                <th data-hide="phone"><spring:theme code="text.businessUnitDetail.addresseslist.state"/></th>
                <th data-hide="phone"><spring:theme code="text.businessUnitDetail.addresseslist.postcode"/></th>

            </tr>
            </thead>
        	<tbody>
       		<c:choose>
				<c:when test="${not empty b2bUnit.addresses}">
					<c:forEach items="${b2bUnit.addresses}" var="addresse" varStatus="loop">
					<tr>
		                <td><p class="clamp-1 offset-bottom-none">${addresse.line1 }</p></td>
		                <td><p class="clamp-1 offset-bottom-none">${addresse.line2 }</p></td>
		                <td>${addresse.town }</td>
		                <!-- TODO dev please add state -->
		                <td>${addresse.region.isocodeShort }</td>
		                <td>${addresse.postalCode }</td>
	            	</tr>
					</c:forEach>
				</c:when>
			</c:choose>
			</tbody>
        </table>
    </div>
    <h2>${b2bUnit.name}&nbsp;<spring:theme code="text.businessUnitDetail.manageuser.title"/></h2>
    <sec:authorize  access="hasAnyRole('ROLE_B2BADMINGROUP, ROLE_B2BASSISTANTGROUP')">
    	<a href="${createUserUrl}" class="btn btn-primary bde-view-only"><spring:theme code="text.businessUnitDetail.createuser.title"/></a>
	 </sec:authorize>

	<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP, ROLE_B2BASSISTANTGROUP')">
		<c:set var="accessable" scope="page" value="true"/>		
	</sec:authorize>
    <table class="table sortable table-highlight users-table" data-sort="true" data-page-size="1000">
        <thead>
        <tr class="row-highlight">
            <th><spring:theme code="text.businessUnitDetail.userlist.name"/></th>
            <th data-hide="phone,tablet"><spring:theme code="text.businessUnitDetail.userlist.email"/></th>
            
            
            <c:choose>
						<c:when test="${bdeUser}">
							<th class="sendWelcomeEmail">&nbsp;</th>
						</c:when>
						<c:otherwise>
							<th data-type="numeric" data-hide="phone,tablet"><spring:theme code="text.businessUnitDetail.userlist.orderlimit"/></th>
						</c:otherwise>
					</c:choose>
            <th><spring:theme code="text.businessUnitDetail.userlist.onboarding.status" /></th>
            <th><spring:theme code="text.businessUnitDetail.userlist.status"/></th>
            <th data-hide="phone" colspan="2" data-sort-ignore="true"><spring:theme code="text.businessUnitDetail.userlist.actions"/></th>
        </tr>
        </thead>
        <tbody>
         <c:choose>
			<c:when test="${not empty b2bUnit.customers}">
				<c:forEach items="${b2bUnit.customers}" var="customer" varStatus="loop">
				
					<!-- SABMC-1011 -->
					<c:set var="isCustomerEditable" value="true"/>
					<c:if test="${user.isZadp eq false and customer.isZadp eq true}">
							<c:set var="isCustomerEditable" value="false"/>
					</c:if>
				
				<c:set var="greyout" value="false" />
				<c:if test="${customer.displayUid eq user.uid}">
					<c:set var="greyout" value="true" />
				</c:if>

		        <tr>
		        	<c:url value="/register/getUser/${customer.displayUid}" var="editUserUrl"/>
		            <td>
		            	<p class="clamp-1 offset-bottom-none">
		            		<c:choose>
		            			<c:when test="${accessable }">
		            				<c:choose>
										<c:when test="${greyout}">
							            	<span class="bold disabled">
							            		<c:if test="${customer.personalAssistant == 'checked'}"><spring:theme code="text.businessUnitDetail.userlist.admin.label" />&nbsp</c:if>
							            	    <c:if test="${not empty customer.firstName}">${customer.firstName }&nbsp;</c:if>${customer.lastName}
							            	    
							            	</span>
										</c:when>
										<c:otherwise>
											<a href="${editUserUrl}" class="link bde-view-only">
							            		<strong>
							            			<c:if test="${customer.personalAssistant == 'checked'}"><spring:theme code="text.businessUnitDetail.userlist.admin.label" />&nbsp</c:if>
							            		    <c:if test="${not empty customer.firstName}">${customer.firstName }&nbsp;</c:if>${customer.lastName}
							            		</strong>
							            	</a>
										</c:otherwise>
									</c:choose>
		            			</c:when>
		            			<c:otherwise>
		            				<strong>
		            					<c:if test="${customer.personalAssistant == 'checked'}"><spring:theme code="text.businessUnitDetail.userlist.admin.label" />&nbsp</c:if>
		            				    <c:if test="${not empty customer.firstName}">${customer.firstName }&nbsp;</c:if>${customer.lastName}
		            				</strong>
		            			</c:otherwise>
		            		</c:choose>
		            	</p>
		            </td> 
		            <td>
		            	<c:choose>
		            		<c:when test="${accessable }">
		            			<c:choose>
									<c:when test="${greyout}">
										<span class="disabled">${customer.displayUid}</span>
							        </c:when>
							        <c:otherwise>
							        	<a href="${editUserUrl}" class="link bde-view-only">
											${customer.displayUid}
							            </a>
							        </c:otherwise>
								</c:choose>
					        </c:when>
					        <c:otherwise>
					        	${customer.displayUid}
					        </c:otherwise>
					    </c:choose>
		            </td>
		            
		            <c:forEach items="${customer.groups}" var="group">
		            	<c:if test="${group.uid eq 'b2bordercustomer'}">
		            		<c:if test="${not empty customer.orderLimit}">
		            			<fmt:formatNumber var="orderLimit" value="${customer.orderLimit}" pattern="$ #,###" />
		            		</c:if>
		            	</c:if>
		            </c:forEach>
		            
		            <c:choose>
									<c:when test="${bdeUser}">
										<td>
											<c:if test="${customer.active eq true}">
												<span id="${customer.uid}" class="sendWelcomeEmail">
												<c:if test="${customer.welcomeEmailSentStatus && not customer.passwordIsSet}">
			                                        <a href="#"><span class="glyphicon glyphicon-share"></span>  Re-Send</a>
										        </c:if>
										        <c:if test="${not customer.welcomeEmailSentStatus}">
	          											<a href="#"><span class="glyphicon glyphicon-share"></span> Send</a>
										        </c:if>
										        <c:if test="${customer.welcomeEmailSentStatus && customer.passwordIsSet}">
	          											<a href="#"><span class="glyphicon glyphicon-share"></span>  Re-Send</a>
			                                    </c:if>
			                                    </span>
		                                    </c:if>
										</td>
									</c:when>
									<c:otherwise>
										<c:set var="orderLimit" value=""/>
										<c:forEach items="${customer.groups}" var="group">
											<c:if test="${group.uid eq 'b2bordercustomer'}">
												<c:if test="${not empty customer.orderLimit}">
													<fmt:formatNumber var="orderLimit" value="${customer.orderLimit}" pattern="$ #,###" />
												</c:if>
											</c:if>
										</c:forEach>
										<td ${empty orderLimit ? 'data-value="-1"' : ''}>
											<c:choose>
												<c:when test="${greyout}">
													<span class="disabled">${orderLimit}</span>
												</c:when>
												<c:otherwise>
													<a href="${editUrl}" class="bde-view-only">${orderLimit}</a>
												</c:otherwise>
											</c:choose>
										</td>
									</c:otherwise>
								</c:choose>
		            
		            <td>
                		<c:if test="${customer.welcomeEmailSentStatus && not customer.passwordIsSet}">
                          <svg class="icon-email-sent">
                              <use xlink:href="#icon-email-sent"></use>
                          </svg>
                      </c:if>
                      <c:if test="${not customer.welcomeEmailSentStatus}">
                          <svg class="icon-email-not-sent">
                              <use xlink:href="#icon-email-not-sent"></use>
                          </svg>
                      </c:if>

                      <c:if test="${customer.welcomeEmailSentStatus && customer.passwordIsSet}">
                           <svg class="icon-lock">
                               <use xlink:href="#icon-lock"></use>
                           </svg>
                      </c:if>
					</td>
		            <td class="active-inactive" data-value="${customer.active ? '1' : '0'}">
		            	<c:choose> 
				            <c:when test="${accessable}">
				            	<c:choose>
				            		<c:when test="${greyout}">
				            			<span class="disabled">
				            				<span class="current-active ${customer.active ? '' : 'hide'}"><spring:theme code="text.businessUnitDetail.userlist.active" /></span>
			            					<span class="current-inactive ${customer.active ? 'hide' : ''}"><spring:theme code="text.businessUnitDetail.userlist.inactive" /></span>
				            			</span>
				            		</c:when>
				            		<c:otherwise>
				            			<a href="${editUserUrl}" class="bde-view-only">
				            				<span class="current-active ${customer.active ? '' : 'hide'}"><spring:theme code="text.businessUnitDetail.userlist.active" /></span>
			            					<span class="current-inactive ${customer.active ? 'hide' : ''}"><spring:theme code="text.businessUnitDetail.userlist.inactive" /></span>
				            			</a>
				            		</c:otherwise>
				            	</c:choose>
				            </c:when>
				            <c:otherwise>
			            		<span class="bde-view-only">
		            				<span class="current-active ${customer.active ? '' : 'hide'}"><spring:theme code="text.businessUnitDetail.userlist.active" /></span>
	            					<span class="current-inactive ${customer.active ? 'hide' : ''}"><spring:theme code="text.businessUnitDetail.userlist.inactive" /></span>
		            			</span>
			            	</c:otherwise>
				       </c:choose>
		            </td>
		            <td>
		            	<sec:authorize  access="hasAnyRole('ROLE_B2BADMINGROUP, ROLE_B2BASSISTANTGROUP')">
			            	<c:choose>
				            	<c:when test="${greyout || !customer.active}">
			            			<span class="disabled">
			            				<spring:theme code="text.businessUnitDetail.userlist.remove"/>
			            			</span>
			            		</c:when>
			            		<c:otherwise>
                                    <span class="link blue bde-view-only current-active ${customer.active ? '' : 'hide'}" data-active="${customer.active}" ng-click="deleteUser($event,'${customer.uid}', '${b2bUnit.uid}', '/sabmStore/en/your-business/remove_customer')">
                                        <spring:theme code="text.businessUnitDetail.userlist.remove"/>
                                    </span>
                                    <span class="disabled current-inactive ${customer.active ? 'hide' : ''}" style="color: initial;">
                                        <spring:theme code="text.businessUnitDetail.userlist.remove"/>
                                    </span>
			            		</c:otherwise>
			            	</c:choose>
		            	</sec:authorize>
		            </td>
 		        	</tr>

				</c:forEach>
			</c:when>
			</c:choose>
        </tbody>
    </table>

    <div class="definitions">
        <ul class="clearfix">
          <li><p class="h3"><spring:theme code="text.businessUnit.onboarding.definitions" /></p></li>
          <li><span class="status-icon status-invited"></span><span class="description"><spring:theme code="text.businessUnit.onboarding.sent" /></span></li>
          <li><span class="status-icon status-inactive"></span><span class="description"><spring:theme code="text.businessUnit.onboarding.not.sent" /></span></li>
          <li><span class="status-icon status-active"></span><span class="description"><spring:theme code="text.businessUnit.onboarding.passwordSet" /></span></li>
        </ul>
    </div>

    <div id="deleteOrDeactivatePopup" class="delete-or-deactivate mfp-hide">
      <h2 class="h1">Do you want to delete or deactivate this user?</h2>
      <p class="offset-bottom-small">This user only has access to this one business unit. By removing this user they will no longer have access to the portal.</p>
      <p class="offset-bottom-xsmall">Please either delete this user or change their status to inactive.</p>
      <button id="businessUnitDetailTagDeleteUser" ng-click="popDeleteUser('${b2bUnit.uid}'); $event.stopPropagation();" class="btn btn-secondary margin-top-20">Delete this user</button>
      <button
        ng-click="userActivate('/your-business/customer_active','${customer.uid}', false)"
        class="btn btn-primary pull-right"
        >Deactivate this user</button>
    	<br>
      <span class="inline" onclick="$.magnificPopup.close()">Cancel</span>
    </div>
   <user:deleteUserPopup/>
</section>
