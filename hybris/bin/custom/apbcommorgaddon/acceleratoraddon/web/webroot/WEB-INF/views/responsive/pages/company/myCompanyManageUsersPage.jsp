<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/apbcommorgaddon/responsive/common" %>

<spring:htmlEscape defaultHtmlEscape="false" />

<spring:url value="/my-company/organization-management/manage-users/create" var="manageUsersUrl" htmlEscape="false"/>
<c:set var="searchUrl" value="/my-company/organization-management/manage-users?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/>

<jsp:useBean id="additionalParams" class="java.util.HashMap"/>
<c:set target="${additionalParams}" property="user" value="${param.user}" />

<template:page pageTitle="${pageTitle}">
    <div class="account-section">
        <org-common:listHeadline url="${manageUsersUrl}" labelKey="text.company.manageusers.label" urlTestId="User_AddUser_button"/>

        <c:choose>
            <c:when test="${not empty searchPageData.results}">
                <div class="account-section-content">

                    <div class="account-overview-list hidden-sm hidden-md hidden-lg">
                        <c:forEach items="${searchPageData.results}" var="user">
                    	    <ul class="manage-user-list profiles-mobile-table">
                    		  <spring:url value="/my-company/organization-management/manage-users/details/"
                                            var="viewUserUrl" htmlEscape="false">
                                    <spring:param name="user" value="${user.uid}"/>
                                </spring:url>
                                <spring:url value="/my-company/organization-management/manage-units/details/"
                                            var="viewUnitUrl" htmlEscape="false">
                                    <spring:param name="unit" value="${user.unit.uid}"/>
                                </spring:url>
                                <spring:url value="/my-company/organization-management/manage-users/sendWelcomeEmail/"
                                            var="mobileResendEmailUrl" htmlEscape="false">
                                    <spring:param name="user" value="${user.uid}"/>
                                </spring:url>
	                    		<li>
                                    <span class="responsive-table-head">
                                        <spring:theme code="text.company.column.name.name"/>:
                                    </span>
                                </li>
                                <li>
	                    			<span><ycommerce:testId code="my-company_username_label">
                                            <a href="${viewUserUrl}" class="responsive-table-link">
                                                <c:set var = "manageuserdisplayname" value="${fn:escapeXml(user.firstName)} ${fn:escapeXml(user.lastName)}"></c:set>
                                                <c:choose>
                                                    <c:when test="${fn:length(manageuserdisplayname) > 25}">
                                                        <c:out value="${fn:substring(manageuserdisplayname, 0, 25)}.." escapeXml="false"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:out value="${manageuserdisplayname}" escapeXml="false"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </a>
                                        </ycommerce:testId>
                                     </span>
	                    		</li>

	                    		<c:choose>
                                    <c:when test="${cmsSite.uid eq 'sga'}">
                                        <li>
                                            <span class="responsive-table-head">
                                                Email:
                                            </span>
                                        </li>
                                        <li>
                                            <span>${user.uid}</span>
                                        </li>
                                        <li>
                                            <span class="responsive-table-head">
                                                <spring:theme code="text.manage.profiles.permission"/>:
                                            </span>
                                        </li>
                                        <li>
                                            <c:if test="${user.samAccess eq 'ORDER_ONLY'}">
                                                <spring:theme code="manage.admin.sip.access.order.only.message" />
                                            </c:if>
                                            <c:if test="${user.samAccess eq 'PAY_ONLY'}">
                                                <c:if test="${user.pendingApproval eq false && user.accessDenied eq false}">
                                                    <spring:theme code="manage.admin.sip.access.pay.only.message" />
                                                </c:if>
                                                <c:if test="${user.pendingApproval eq true && user.accessDenied eq false}">
                                                    <spring:theme code="manage.admin.sip.access.pay.pending.message" />
                                                </c:if>
                                                <c:if test="${user.accessDenied eq true}">
                                                    None
                                                </c:if>
                                            </c:if>
                                            <c:if test="${user.samAccess eq 'PAY_AND_ORDER'}">
                                                <c:if test="${user.pendingApproval eq false && user.accessDenied eq false}">
                                                    <spring:theme code="manage.admin.sip.access.order.pay.message" />
                                                </c:if>
                                                <c:if test="${user.pendingApproval eq true && user.accessDenied eq false}">
                                                    <spring:theme code="manage.admin.sip.access.order.only.message" /> <br>
                                                    <spring:theme code="manage.admin.sip.access.pay.pending.message" />
                                                </c:if>
                                                <c:if test="${user.accessDenied eq true}">
                                                    <spring:theme code="manage.admin.sip.access.order.only.message" />
                                                </c:if>
                                            </c:if>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li>
                                            <span class="responsive-table-head">
                                                <spring:theme code="text.company.column.roles.name"/>:
                                            </span>
                                        </li>
                                        <li>
                                            <span> <ycommerce:testId code="my-company_user_roles_label">
                                                    ${user.asahiRole.name}
                                                </ycommerce:testId></span>
                                        </li>
                                        <li>
                                            <span class="responsive-table-head">
                                                <spring:theme code="text.company.column.access.name"/>:
                                            </span>
                                        </li>
                                        <li>
                                            <span>
                                                <ycommerce:testId code="my-company_user_roles_label">
                                                    <c:choose>
                                                        <c:when test="${user.isAdminUser}">
                                                            <spring:theme code="b2busergroup.b2badmingroup.name"/><br/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <spring:theme code="b2busergroup.b2bcustomergroup.name"/><br/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </ycommerce:testId>
                                            </span>
                                       </li>
                                    </c:otherwise>
                                </c:choose>
	                           <li>
                                    <span class="responsive-table-head">
                                        <spring:theme code="text.company.status.title"/>:
                                    </span>
                               </li>
                                <li>
	                    		<span>
                                    <ycommerce:testId code="costCenter_status_label">
                                            <c:choose>
                                                <c:when test="${user.active}">
                                                    <span><spring:theme code="text.company.status.active.true"/></span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="account-status-inactive"><spring:theme code="text.company.status.active.false"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </ycommerce:testId>
                                </span>
	                    		</li>
                                <c:if test="${cmsSite.uid eq 'sga' && displayWelcomeEmailLinks eq true && user.displayWelcomeEmailLink}">
                                    <li></li>
                                    <div class="btn-link text-underline cursor disable-spinner"
                                        onclick="ACC.manageProfiles.sendEmail('${mobileResendEmailUrl}');"><strong>Resend Welcome Email</strong></div>
                                </c:if>
                    	    </ul>
                    	</c:forEach>
                    </div>

                    <div class="account-overview-table hidden-xs">
                        <table class="responsive-table">
                            <tr class="responsive-table-head hidden-xs">
                                <th><spring:theme code="text.company.column.name.name"/></th>
                                <c:if test="${cmsSite.uid ne 'sga'}">
                                	<th><spring:theme code="text.company.column.roles.name"/></th>
                                    <th><spring:theme code="text.company.column.access.name"/></th>
                                </c:if>
								<c:if test="${cmsSite.uid eq 'sga'}">
								    <th>Email</th>
                                	<th><spring:theme code="text.manage.profiles.permission"/></th>
                                </c:if>
                                <%-- <th><spring:theme code="text.company.column.parentUnit.name"/></th>
                                <th><spring:theme code="text.company.manageUser.user.costCenter"/></th> --%>
                                <th><spring:theme code="text.company.status.title"/></th>
                                <c:if test="${cmsSite.uid eq 'sga' && displayWelcomeEmailLinks eq true}">
                                    <th></th>
                                </c:if>
                            </tr>
                            <c:forEach items="${searchPageData.results}" var="user">
                                <spring:url value="/my-company/organization-management/manage-users/details/"
                                            var="viewUserUrl" htmlEscape="false">
                                    <spring:param name="user" value="${user.uid}"/>
                                </spring:url>
                                <spring:url value="/my-company/organization-management/manage-units/details/"
                                            var="viewUnitUrl" htmlEscape="false">
                                    <spring:param name="unit" value="${user.unit.uid}"/>
                                </spring:url>
                                <spring:url value="/my-company/organization-management/manage-users/sendWelcomeEmail/"
                                            var="resendEmailUrl" htmlEscape="false">
                                    <spring:param name="user" value="${user.uid}"/>
                                </spring:url>

                                <tr class="responsive-table-item">
                                    <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.name.name"/></td>
                                    <td class="responsive-table-cell manage-profile-padding">
                                        <ycommerce:testId code="my-company_username_label">
                                            <a href="${viewUserUrl}" class="responsive-table-link">
                                            <c:set var = "manageuserdisplayname" value="${fn:escapeXml(user.firstName)} ${fn:escapeXml(user.lastName)}"></c:set>
                                            <c:choose>
												<c:when test="${fn:length(manageuserdisplayname) > 25}">
													<c:out value="${fn:substring(manageuserdisplayname, 0, 25)}.." escapeXml="false"/>
												</c:when>
												<c:otherwise>
													<c:out value="${manageuserdisplayname}" escapeXml="false"/>
												</c:otherwise>
											</c:choose>
                                            </a>
                                        </ycommerce:testId>
                                    </td>
                                    <c:if test="${cmsSite.uid ne 'sga'}">
                                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.roles.name"/></td>
                                        <td class="responsive-table-cell manage-profile-padding">
                                            <ycommerce:testId code="my-company_user_roles_label">
                                                ${user.asahiRole.name}
                                            </ycommerce:testId>
                                        </td>
                                    </c:if>

                                    <c:choose>
                                        <c:when test="${cmsSite.uid eq 'sga'}">
                                            <td>${user.uid}</td>
                                        </c:when>
                                        <c:otherwise>
                                            <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.access.name"/></td>
                                            <td class="responsive-table-cell manage-profile-padding">
                                                <span>
                                                    <ycommerce:testId code="my-company_user_roles_label">
                                                        <c:choose>
                                                            <c:when test="${user.isAdminUser}">
                                                                <spring:theme code="b2busergroup.b2badmingroup.name"/><br/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <spring:theme code="b2busergroup.b2bcustomergroup.name"/><br/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </ycommerce:testId>
                                                </span>
                                            </td>
                                        </c:otherwise>
                                    </c:choose>

									<c:if test="${cmsSite.uid eq 'sga'}">
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.manage.profiles.permission"/></td>
										<td class="responsive-table-cell manage-profile-padding">
											<c:if test="${user.samAccess eq 'ORDER_ONLY'}">
												<spring:theme code="manage.admin.sip.access.order.only.message" />
											</c:if>
											<c:if test="${user.samAccess eq 'PAY_ONLY'}">
												<c:if test="${user.pendingApproval eq false && user.accessDenied eq false}">
													<spring:theme code="manage.admin.sip.access.pay.only.message" />
												</c:if>
												<c:if test="${user.pendingApproval eq true && user.accessDenied eq false}">
													<spring:theme code="manage.admin.sip.access.pay.pending.message" />
												</c:if>
												<c:if test="${user.accessDenied eq true}">
													None
												</c:if>
											</c:if>
											<c:if test="${user.samAccess eq 'PAY_AND_ORDER'}">
												<c:if test="${user.pendingApproval eq false && user.accessDenied eq false}">
													<spring:theme code="manage.admin.sip.access.order.pay.message" />
												</c:if>
												<c:if test="${user.pendingApproval eq true && user.accessDenied eq false}">
													<spring:theme code="manage.admin.sip.access.order.only.message" /> <br>
													<spring:theme code="manage.admin.sip.access.pay.pending.message" />
												</c:if>
												<c:if test="${user.accessDenied eq true}">
													<spring:theme code="manage.admin.sip.access.order.only.message" />
												</c:if>
											</c:if>
										</td>
                                    </c:if>

                                    <%-- <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.parentUnit.name"/></td>
                                    <td class="responsive-table-cell manage-profile-padding">
                                        <ycommerce:testId code="my-company_user_unit_label">
                                            <a href="${viewUnitUrl}" class="responsive-table-link">${fn:escapeXml(user.unit.name)}</a>
                                        </ycommerce:testId>
                                    </td>
                                    <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.manageUser.user.costCenter"/></td>
                                    <td class="responsive-table-cell manage-profile-padding">
                                        <ycommerce:testId code="my-company_user_costcenter_label">
                                            <c:forEach items="${user.unit.costCenters}" var="costCenter">
                                                <spring:url value="/my-company/organization-management/manage-costcenters/view/"
                                                            var="viewCostCenterUrl" htmlEscape="false">
                                                    <spring:param name="costCenterCode" value="${costCenter.code}"/>
                                                </spring:url>
                                                <a href="${viewCostCenterUrl}" class="responsive-table-link">${fn:escapeXml(costCenter.code)}</a><br/>
                                            </c:forEach>
                                        </ycommerce:testId>
                                    </td> --%>

                                    <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.status.title"/></td>
                                    <td class="responsive-table-cell manage-profile-padding">
                                        <ycommerce:testId code="costCenter_status_label">
                                            <c:choose>
                                                <c:when test="${user.active}">
                                                    <span><spring:theme code="text.company.status.active.true"/></span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="account-status-inactive"><spring:theme code="text.company.status.active.false"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </ycommerce:testId>
                                    </td>
                                    <c:if test="${cmsSite.uid eq 'sga' && displayWelcomeEmailLinks eq true}">
                                        <td class="text-right text-nowrap">
                                            <c:if test="${user.displayWelcomeEmailLink}">
                                                <div class="btn-link text-underline cursor disable-spinner"
                                                    onclick="ACC.manageProfiles.sendEmail('${resendEmailUrl}');"><strong>Resend Welcome Email</strong></div>
                                            </c:if>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>

                    <div class="account-orderhistory-pagination manage-users-bottom-pagination">
                        <nav:paginationwithnumbering top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"
                                        searchPageData="${searchPageData}" hideRefineButton="true"
                                        searchUrl="${searchUrl}" msgKey="text.company.manageUser.pageAll"
                                        additionalParams="${additionalParams}" numberPagesShown="${numberPagesShown}"/>

                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="row">
                    <div class="col-md-6 col-md-push-3">
                        <div class="account-section-content content-empty">
                            <spring:theme code="text.company.manageUser.noUser"/>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</template:page>
