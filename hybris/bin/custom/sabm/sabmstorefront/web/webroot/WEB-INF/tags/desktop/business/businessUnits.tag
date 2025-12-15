<%@ taglib prefix="business" tagdir="/WEB-INF/tags/desktop/business"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:url value="/your-business/unitsdetails/" var="toManageUserUrl"
	scope="session" />
<c:url value="/register/createUser/${rootB2bUnit.uid}" var="createUserUrl" scope="session" />

<div class="row">
	<div class="col-xs-12">
		<h1>
			<spring:theme code="text.yourBusiness.businessUnits.title" />
		</h1>
		<h2 class="h1">${rootB2bUnit.name}</h2>
		<p>
			<spring:theme
				code="text.businessunits.businessunitstable.description" />
		</p>
		<p>
			<spring:theme
				code="text.businessunits.businessunitstable.note" />
		</p>

		<table class="table sortable users-table" data-sort="true" data-page-size="1000">
			<thead>
				<tr class="row-highlight">
					<th data-toggle="true"><spring:theme
							code="text.businessunits.businessunitstable.headers.venue" /></th>
					<th data-type="numeric"><spring:theme
							code="text.businessunits.businessunitstable.headers.activeusers" /></th>
					<th data-hide="phone,tablet"><spring:theme
							code="text.businessunits.businessunitstable.headers.towncity" /></th>
					<th data-hide="phone"><spring:theme
							code="text.businessunits.businessunitstable.headers.state" /></th>
					<th data-hide="phone,tablet"><spring:theme
							code="text.businessunits.businessunitstable.headers.postcode" /></th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${not empty b2bUnits}">
						<c:forEach items="${b2bUnits}" var="b2bUnit" varStatus="loop">
							<tr>
								<c:if test="${b2bUnit.name ne rootB2bUnit.name}">
									<c:url value="/your-business/unitsdetails/${b2bUnit.uid}" var="unitsDetailsUrl"/>
									<td><a href="${unitsDetailsUrl }"><strong>${b2bUnit.name}</strong></a></td>
									<td><a href="${unitsDetailsUrl }">${b2bUnit.activeUsers}</a></td>
									<c:choose>
										<c:when test="${not empty b2bUnit.contactAddress}">
											<td><a href="${unitsDetailsUrl }">${b2bUnit.contactAddress.town}</a></td>
											<td><a href="${unitsDetailsUrl }">${b2bUnit.contactAddress.region.isocode}</a></td>
											<td><a href="${unitsDetailsUrl }">${b2bUnit.contactAddress.postalCode}</a></td>
										</c:when>
										<c:otherwise>
											<td><a href="${unitsDetailsUrl }">${b2bUnit.addresses.get(0).town}</a></td>
											<td><a href="${unitsDetailsUrl }">${b2bUnit.addresses.get(0).region.isocode}</a></td>
											<td><a href="${unitsDetailsUrl }">${b2bUnit.addresses.get(0).postalCode}</a></td>
										</c:otherwise>
									</c:choose>
								</c:if>
							</tr>
						</c:forEach>
					</c:when>
				</c:choose>
			</tbody>
		</table>

		<h2 class="h1">
			<spring:theme code="text.yourBusiness.businessUnits.users" />
		</h2>
		<p>
			<spring:theme code="text.businessunits.userstable.description" />
		</p>

		<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP, ROLE_B2BASSISTANTGROUP')">
			<c:set var="accessable" scope="page" value="true"/>		
		</sec:authorize>			

		<div class="row">
			<div class="col-sm-6 margin-top-20">
				<c:if test="${accessable}">
					<a href="<c:url value="/register/createUser"/>" class="btn btn-primary bde-view-only"><spring:theme code="text.businessUnitDetail.createuser.title"/></a>
				</c:if>
			</div>
			<div class="col-sm-3 col-sm-offset-3 form-group margin-top-20">
				<div class="input-icon-group">
						<input id="userFilter" type="text" class="form-control" />
						<div class="input-group-btn">
							<svg class="icon-search">
							    <use xlink:href="#icon-search"></use>
							</svg>
						</div>
					</div>
			</div>
		</div>

		<table data-filter="#userFilter" class="table sortable users-table" data-page-size="1000">
			<thead>
				<tr class="row-highlight">
					<th data-toggle="true"><spring:theme code="text.businessUnitDetail.userlist.name" /></th>
					<th data-hide="phone,tablet"><spring:theme
							code="text.businessUnitDetail.userlist.email" /></th>
					<c:choose>
						<c:when test="${bdeUser}">
							<th class="sendWelcomeEmail">&nbsp;</th>
						</c:when>
						<c:otherwise>
							<th data-type="numeric" data-hide="phone" class="orderLimitHead"><spring:theme
									code="text.businessUnitDetail.userlist.orderlimit" /></th>
						</c:otherwise>
					</c:choose>
					<th><spring:theme code="text.businessUnitDetail.userlist.onboarding.status" /></th>
					<th><spring:theme code="text.businessUnitDetail.userlist.status" /></th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${not empty customers}">
						<c:forEach items="${customers}" var="customer" varStatus="loop">
							
							<!-- SABMC-1011 -->
							<c:set var="greyout" value="false" />
							<c:if test="${customer.displayUid eq user.uid}">
								<c:set var="greyout" value="true" />
							</c:if>
						
							<c:url value="/register/getUser/${customer.uid}" var="editUrl"/>
							<tr>
								<td>
									<c:choose>
										<c:when test="${accessable}">
											<c:choose>
												<c:when test="${greyout}">
													<span class="clamp-1 disabled">
														<strong>
															<c:if test="${customer.personalAssistant == 'checked'}"><spring:theme code="text.businessUnitDetail.userlist.admin.label"/></c:if>
															<c:if test="${not empty customer.firstName}">${customer.firstName }
															</c:if>${customer.lastName}
														</strong>
													</span>
												</c:when>
												<c:otherwise>
													<a href="${editUrl}" class="bde-view-only">
														<span class="clamp-1">
															<strong>
																<c:if test="${customer.personalAssistant == 'checked'}"><spring:theme code="text.businessUnitDetail.userlist.admin.label"/></c:if>
																<c:if test="${not empty customer.firstName}">${customer.firstName }
																</c:if>${customer.lastName}
															</strong>
														</span>
													</a>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<span class="clamp-1">
												<strong>
													<c:if test="${customer.personalAssistant == 'checked'}"><spring:theme code="text.businessUnitDetail.userlist.admin.label"/></c:if>
													<c:if test="${not empty customer.firstName}">${customer.firstName }
													</c:if>${customer.lastName}
												</strong>
											</span>
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${accessable}">
											<c:choose>
												<c:when test="${greyout}">
													<span class="disabled">${customer.email}</span>
												</c:when>
												<c:otherwise>
													<a href="${editUrl}" class="bde-view-only">${customer.email}</a>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											${customer.email}
										</c:otherwise>
									</c:choose>
								</td>
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
								<td>
									<c:choose>
										<c:when test="${accessable}">
											<c:choose>
												<c:when test="${greyout}">
													<span class="disabled">
														<c:if test="${customer.active eq true}">
															<span class="current-active"><spring:theme code="text.businessUnitDetail.userlist.active" /></span>
														</c:if>
														<c:if test="${customer.active eq false}">
															<span class="current-inactive"><spring:theme code="text.businessUnitDetail.userlist.inactive" /></span>
														</c:if>
													</span>
												</c:when>
												<c:otherwise>
													<a href="${editUrl}" class="bde-view-only">
														<c:if test="${customer.active eq true}">
															<span class="current-active"><spring:theme code="text.businessUnitDetail.userlist.active" /></span>
														</c:if>
														<c:if test="${customer.active eq false}">
															<span class="current-inactive"><spring:theme code="text.businessUnitDetail.userlist.inactive" /></span>
														</c:if>
													</a>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:if test="${customer.active eq true}">
												<span class="current-active"><spring:theme code="text.businessUnitDetail.userlist.active" /></span>
											</c:if>
											<c:if test="${customer.active eq false}">
												<span class="current-inactive"><spring:theme code="text.businessUnitDetail.userlist.inactive" /></span>
											</c:if>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</c:when>
				</c:choose>
			</tbody>
		</table>
	</div>
</div>


<div class="definitions">
    <ul class="clearfix">
      <li><p class="h3"><spring:theme code="text.businessUnit.onboarding.definitions" /></p></li>
      <li><span class="status-icon status-invited"></span><span class="description"><spring:theme code="text.businessUnit.onboarding.sent" /></span></li>
      <li><span class="status-icon status-inactive"></span><span class="description"><spring:theme code="text.businessUnit.onboarding.not.sent" /></span></li>
      <li><span class="status-icon status-active"></span><span class="description"><spring:theme code="text.businessUnit.onboarding.passwordSet" /></span></li>
    </ul>
</div>