<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%> 
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>

<c:set var="customerSearchUrl" value="customer-search" />
<%-- To be removed later when sprint 5b will go live --%>
<c:set var="test2EnvUrl" value="https://test.online.cub.com.au/sabmStore/en/login"/> 
<c:url value="/viewOnly" var="viewOnly" />
<nav:steps current="2"/>
<div class="row">
   <div class="col-lg-12">
       <h1 class="h1"><spring:theme code="staff.portal.customer.searchResults.title" /></h1>

       <p class="h2">
        <spring:theme code="staff.portal.customer.searchResults.subTitle" />
        <c:if test="${not empty form.accountPayerNumber }">
            <spring:theme code="staff.portal.customer.searchResults.symbol" arguments="${form.accountPayerNumber }"/>
        </c:if>
        <c:if test="${not empty form.customerName }">
            <spring:theme code="staff.portal.customer.searchResults.symbol" arguments="${form.customerName }"/>
        </c:if>
        <c:if test="${not empty form.address }">
            <spring:theme code="staff.portal.customer.searchResults.symbol" arguments="${form.address }"/>
        </c:if>
        <c:if test="${not empty form.suburb }">
            <spring:theme code="staff.portal.customer.searchResults.symbol" arguments="${form.suburb }"/>
        </c:if>
        <c:if test="${not empty form.expiryDateMonth }">
            <spring:theme code="staff.portal.customer.searchResults.symbol" arguments="${form.expiryDateMonth }"/>
        </c:if>
                <c:if test="${not empty form.postcode }">
            <spring:theme code="staff.portal.customer.searchResults.symbol" arguments="${form.postcode }"/>
        </c:if>
       </p>

       <c:if test="${fn:length(customerDatas) > 20}">
            <p><spring:theme code="staff.portal.customer.searchResults.moreThanNumber"/></p>
       </c:if>

       <c:choose>
        <c:when test='${empty customerDatas }'>
            <p class="h3 offset-bottom-medium"><spring:theme code="staff.portal.customer.searchResults.noResults.message"/></p>
            <p><spring:theme code="staff.portal.customer.searchResults.noResults"/></p>

        </c:when>
        <c:otherwise>

        <table class="table footable sortable search-results">
          <thead>
            <tr>
              <th data-toggle="true">Onboarding</th>
              <th data-toggle="true">Ordering</th>
              <th data-toggle="true" data-sort-initial="true">Customer #</th>
              <th data-sort-ignore="true" data-hide="phone">Address</th>
              <th data-sort-ignore="true" data-hide="phone,tablet"></th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${customerDatas }" var="customerData" end="19">
            <tr class="search-result-item">
              <%--Status--%>
              <td class="status" data-value="${customerData.primaryAdminStatus}">
                <span class="status-${customerData.primaryAdminStatus}"></span> 
              </td>
              <td class="status" data-value="${customerData.orderingStatus}">
                <span class="order order-${customerData.orderingStatus}"></span>
              </td>
              <%--User ID--%>
              <td>
                ${customerData.uid}
              </td>

              <%--Address--%>
              <td>
                <c:if test="${not empty customerData.name}">${customerData.name}</c:if>
                <c:if test="${not empty customerData.addressStreetName}"><spring:theme code="staff.portal.customer.searchResults.shortbar" />${customerData.addressStreetName}</c:if>
                <c:if test="${not empty customerData.addressSuburb}"><spring:theme code="staff.portal.customer.searchResults.comma" />${customerData.addressSuburb}</c:if>
                <c:if test="${not empty customerData.postCode}"><spring:theme code="staff.portal.customer.searchResults.comma" />${customerData.postCode}</c:if>
              </td>

              <td class="actions">
                <%--Users Link--%>
                <span>
                  <form:form action="${viewOnly}" method="POST" target="_blank">
                    <a href="#" onclick="$(this).closest('form').submit();return false;"> <span><strong><spring:theme code="staff.portal.customer.searchResults.usersPage" /></strong></span></a>
                    <input type="hidden" name="uid" value="${customerData.uid}">
                    <input type="hidden" name="landingPage" value="/your-business/businessunits">
                      <input type="hidden" name="email" value="${user.uid}">
                  </form:form>
                </span>|<c:if test="${customerData.dealsExists}"><span>
                <%--Deals Link--%>
                  <a href="<c:url value="/deals/specific/${customerData.uid }"/>">
                    <strong>
                        <spring:theme code="staff.portal.customer.searchResults.deals" />
                    </strong>
                  </a>
                </span>|</c:if><span>
                <%--View Only Link--%>
                  <form:form action="${viewOnly}" method="POST" target="_blank">
                    <a href="#" onclick="$(this).closest('form').submit();return false;"> <span><strong><spring:theme code="staff.portal.customer.searchResults.view" /></strong></span></a>
                    <input type="hidden" name="uid" value="${customerData.uid}">
                    <input type="hidden" name="email" value="${user.uid}">
                  </form:form>
                </span>
              </td>
            </tr>
            </c:forEach>
          </tbody>
        </table>


        </c:otherwise>
       </c:choose>
   </div>
</div>

  <div class="row">
     <div class="form-group col-md-6">
       <div class="offset-bottom-large">
       	 <a href="${customerSearchUrl}" class="btn btn-primary btn-large btn-flex-fixed"><spring:theme code="staff.portal.customer.searchResults.back.search" /> </a>
       </div>
     </div>
  </div>
  <div class="definitions">
      <ul class="status clearfix">
        <li><p class="h3"><spring:theme code="staff.portal.customer.searchResults.status.title" /></p></li>
        <li><span class="status-icon status-inactive"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.inactive.info" /></span></li>
        <li><span class="status-icon status-invited"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.invited.info" /></span></li>
        <li><span class="status-icon status-active"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.active.info" /></span></li>
        <li><span class="status-icon status-order_placed"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.placed.info" /></span></li>
      </ul>
  </div>

  <div class="definitions">
      <ul class="status clearfix">
        <li><p class="h3 inline">Online Ordering activity&nbsp;</p>[last 13 weeks] - Percentage of orders placed Online vs Other methods by the venue</li>
        <li><span class="status-icon order order-GREEN"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.morethan80.info" /></span></li>
        <li><span class="status-icon order order-YELLOW"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.80to60.info" /></span></li>
        <li><span class="status-icon order order-RED"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.lessthan60.info" /></span></li>
        <li><span class="status-icon order order-NO_WEB_ORDERS"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.zero.info" /></span></li>
        <li><span class="status-icon order order-NO_ORDERS"></span><span class="description"><spring:theme code="staff.portal.customer.searchResults.status.noorder.info" /></span></li>
      </ul>
  </div>