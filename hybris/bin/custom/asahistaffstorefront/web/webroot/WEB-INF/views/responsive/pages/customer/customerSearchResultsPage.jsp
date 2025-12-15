<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ page trimDirectiveWhitespaces="true" %>

<c:set var="customerSearchUrl" value="customer-search" />
<%-- To be removed later when sprint 5b will go live --%>
<c:set var="test2EnvUrl" value="https://test.online.cub.com.au/sabmStore/en/login"/> 
<c:set value="${['Online Activity', 'Account Number', 'Account Name', 'Address', 'Suburb', 'Postcode', '']}" var="tHeader" />
<c:url value="/viewOnly" var="viewOnly" />
<jsp:useBean id="statuses" class="java.util.LinkedHashMap"/>
<c:set target="${statuses}" property="INACTIVE" value="activity-icon-darkgrey" /> <%-- primaryAdminStatus = INACTIVE -> GREY --%>
<c:set target="${statuses}" property="RED" value="activity-icon-red" />
<c:set target="${statuses}" property="NO_ORDERS" value="activity-icon-red" /> <%-- primaryAdminStatus = ACTIVE -> customerData.orderingStatus = NO_ORDERS -> RED --%>
<c:set target="${statuses}" property="YELLOW" value="activity-icon-orange" /><!--YELLOW-->
<c:set target="${statuses}" property="GREEN" value="activity-icon-green" /><!--GREEN-->

<c:set var="currentPageItems" value="${(searchPageData.pagination.currentPage + 1) * searchPageData.pagination.pageSize}"/>
<c:set var="upTo" value="${(currentPageItems > searchPageData.pagination.totalNumberOfResults ? searchPageData.pagination.totalNumberOfResults : currentPageItems)}"/>
<c:set var="currentPage" value="${searchPageData.pagination.currentPage * searchPageData.pagination.pageSize + 1} - ${upTo}"/>

<jsp:include page="customerSearchPage.jsp"/>

<div class="form-row-margin mt-25">
    <div class="checkout_subheading row-margin-fix">
        <spring:theme code="staff.portal.customer.searchResults.title" />
    </div>
</div>

<div class="row">
   <div class="col-lg-12">
       <c:if test="${fn:length(customerDatas) > 20}">
            <p><spring:theme code="staff.portal.customer.searchResults.moreThanNumber"/></p>
       </c:if>

       <c:choose>
        <c:when test='${empty customerDatas }'>
          <div class="search-result">
            <!-- If empty result -->
            <div class="empty">
              <p>Sorry, no results were found.</p><br />
              <p>You can:</p>
              <ul class="pl-15">
                <li>Check for typos and spelling errors (they happen to the best of us!) then search again.</li>
                <li>Try broader search terms (e.g. "cafe" rather than "Southern Village Cafe").</li>
                <li>Search using different fields (e.g. "Account Number" rather than "Account Name").</li>
              </ul>
            </div>
          </div>
        </c:when>
        <c:otherwise>
            <div class="form-row-margin mt-15">
              <div class="pull-left mb-xs-10">
                <span>Showing <span class="result-offset">${currentPage}</span> of ${searchPageData.pagination.totalNumberOfResults} Accounts</span>
              </div>
              <div class="pull-right pull-xs-left a-seperator">
                <strong>Sort By:</strong> <a id="sort-asc" role="button" onclick="ACC.customersearch.sort(':customername-asc');">Account Name(A-Z)</a>
                <span class="no-seperator"><a id="sort-desc" role="button" onclick="ACC.customersearch.sort(':customername-desc');">Account Name(Z-A)</a></span>
              </div>
            </div>
            <div class="clear"></div>
            <div class="search-result mt-25">
              <div id="multiaccount-table" class="mb-15 responsive-table">
                <table class="table footable sortable search-results" >
                  <thead>
                    <tr>
                      <c:forEach items="${tHeader}" var="title">
                        <th ${title eq 'Account Name' ? "data-sort-initial='true'" : "data-sort-ignore='true'"}>${title}</th>
                      </c:forEach>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach items="${customerDatas }" var="customerData" end="19">
                      <tr class="search-result-item">
                        <%--Online Activity--%>
                        <td class="status pl-0" data-value="${fn:toUpperCase(customerData.primaryAdminStatus) eq 'ACTIVE' ? customerData.orderingStatus : 'INACTIVE'}">
                          <span class="info-label">Online Activity</span><span class="activity-icon ${fn:toLowerCase(customerData.primaryAdminStatus) eq 'inactive' ? statuses['INACTIVE'] : statuses[customerData.orderingStatus]}"></span>
                        </td>
                        <%--Account Number aka uid--%>
                        <td class="status" data-value="${customerData.uid}">
                          <span class="info-label">Account Number</span><span class="order order-${customerData.uid}">${customerData.uid}</span>
                        </td>
                        <%--Account Name--%>
                        <td>
                          <span class="info-label">Account Name</span>
                          <c:if test="${not empty customerData.name}">${customerData.name}</c:if>
                        </td>
                        <%--Address--%>
                        <td>
                          <span class="info-label">Address</span>
                          <c:if test="${not empty customerData.addressStreetName}">
                            ${customerData.addressStreetName}
                          </c:if>
                        </td>
                        <%--Suburb--%>
                        <td>
                          <c:if test="${not empty customerData.addressSuburb}">${customerData.addressSuburb}</c:if>
                        </td>
                        <%--Postcode--%>
                        <td>
                          <c:if test="${not empty customerData.postCode}">${customerData.postCode}</c:if>
                        </td>
                        <%--Actions--%>
                        <td class="actions a-seperator pr-0">
                          <%--Users Link--%>
                          <span class="info-label"></span>
                          <span>
                            <form:form action="${viewOnly}" method="POST" target="_blank">
                              <a href="#" onclick="$(this).closest('form').submit();return false;">
                                <span class="text-underline">
                                  <strong>
                                    <spring:theme code="staff.portal.customer.searchResults.profilesPage" />
                                  </strong>
                                </span>
                              </a>
                              <input type="hidden" name="uid" value="${customerData.uid}">
                              <input type="hidden" name="landingPage" value="/my-company/organization-management/manage-users">
                              <input type="hidden" name="email" value="${user.uid}">
                            </form:form>
                          </span>
                          <c:if test="${customerData.dealsExists}">
                            <span class="text-underline">
                              <%--Deals Link--%>
                              <a href="<c:url value='/deals/${customerData.uid }'/>">
                              <strong>
                                <spring:theme code="staff.portal.customer.searchResults.deals" />
                              </strong>
                              </a>
                            </span>
                          </c:if>
                          <span class="no-seperator">
                            <%--View Only Link--%>
                            <form:form action="${viewOnly}" method="POST" target="_blank">
                              <a href="#" onclick="$(this).closest('form').submit();return false;">
                                <span class="text-underline">
                                  <strong>
                                    <spring:theme code="staff.portal.customer.searchResults.orders" />
                                  </strong>
                                </span>
                              </a>
                              <input type="hidden" name="uid" value="${customerData.uid}">
                              <input type="hidden" name="email" value="${user.uid}">
                            </form:form>
                          </span>
                        </td>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </div>
              <nav:paginationwithnumbering top="false"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}"
              	searchUrl="${searchUrl}" numberPagesShown="${numberPagesShown}"/>
            </div>
            <div class="definitions mt-25">
              <ul class="status clearfix">
                <li><span class="h3 mr-30">Online Activity Definitions</span>*Based on customer orders in the last 13 weeks</li>
                <li><span class="activity-icon activity-icon-darkgrey"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.nousers.info" /></span></li>
                <li><span class="activity-icon activity-icon-red"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.noorder.info" /></span></li>
                <li><span class="activity-icon activity-icon-orange"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.lessthan70.info" /></span></li>
                <li><span class="activity-icon activity-icon-green"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.morethan70.info" /></span></li>
              </ul>
            </div>
        </c:otherwise>
       </c:choose>
   </div>
</div>