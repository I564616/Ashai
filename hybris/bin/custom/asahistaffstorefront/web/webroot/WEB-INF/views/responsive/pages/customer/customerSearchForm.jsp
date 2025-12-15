<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:theme code="staff.portal.customer.search.state.act" var="act"/>
<spring:theme code="staff.portal.customer.search.state.nsw" var="nsw"/>
<spring:theme code="staff.portal.customer.search.state.nt" var="nt"/>
<spring:theme code="staff.portal.customer.search.state.qld" var="qld"/>
<spring:theme code="staff.portal.customer.search.state.sa" var="sa"/>
<spring:theme code="staff.portal.customer.search.state.tas" var="tas"/>
<spring:theme code="staff.portal.customer.search.state.vic" var="vic"/>
<spring:theme code="staff.portal.customer.search.state.wa" var="wa"/>

<script >
    sessionStorage.setItem('page', '${form.page}');
    sessionStorage.setItem('sort', '${form.sort}');
    sessionStorage.setItem('accountPayerNumber', '${form.accountPayerNumber}');
    sessionStorage.setItem('customerName', '${form.customerName}');
    sessionStorage.setItem('email', '${form.email}');
    sessionStorage.setItem('address', '${form.address}');
    sessionStorage.setItem('postcode', '${form.postcode}');
    sessionStorage.setItem('expiryDateMonth', '${form.expiryDateMonth}');
</script>
<c:set value="${[{'name': act, 'code': act}, {'name': nsw, 'code': nsw}, {'name': nt, 'code':nt}, {'name':qld, 'code': qld}, {'name':sa, 'code': sa}, {'name':tas, 'code': tas}, {'name':vic, 'code': vic}, {'name':wa, 'code': wa} ]}" var="states" />

<form:form id="customerSearchForm" name="customerSearchForm" modelAttribute="asahiCustomerSearchForm" action="${customerSearchFormAction}" method="POST" ng-controller="customerSearchCtrl" >
    <input type="hidden" id="page" name="page" value="${form.page}" />
    <input type="hidden" id="sort" name="sort" value="${form.sort}" />

    <div class="row">
        <div class="col-sm-6 mb-10">
            <formElement:formInputBox
                idKey="accountNumber"
                inputCSS="ml-0"
                labelKey="Account Number"
                path="accountPayerNumber"
                mandatory="false"
                validateModel="all_blank"
                ngInit="updateData('accountNumber', '${form.accountPayerNumber}')"
                ngModel="accountNumber" />

        </div>
        <div class="col-sm-6 mb-10">
            <formElement:formInputBox
                idKey="accountName"
                inputCSS="ml-0"
                labelKey="Account Name"
                path="customerName"
                mandatory="false"
                validateModel="all_blank"
                ngModel="accountName"
                ngInit="updateData('accountName', '${fn:escapeXml(form.customerName)}')" />
        </div>
    </div>
    <div class="row">
        <div class="col-sm-6 mb-10">
            <formElement:formInputBox
                idKey="email"
                inputCSS="ml-0"
                labelKey="Email"
                path="email"
                mandatory="false"
                validateModel="all_blank"
                ngModel="email"
                ngInit="updateData('email', '${form.email}')" />
        </div>
    </div>

    <div class="row">
        <div class="col-sm-6 mb-10">
            <formElement:formInputBox
                idKey="address"
                inputCSS="form-control ml-0"
                labelKey="staff.portal.customer.search.label.address"
                path="address"
                mandatory="false"
                validateModel="all_blank"
                ngModel="address"
                ngInit="updateData('address', '${form.address}')" />
        </div>
        <div class="col-sm-6 mb-10">
            <formElement:formInputBox
                idKey="suburb"
                inputCSS="ml-0"
                labelKey="staff.portal.customer.search.label.suburb"
                path="suburb"
                mandatory="false"
                validateModel="all_blank"
                ngModel="suburb"
                ngInit="updateData('suburb', '${form.suburb}')" />
        </div>
        <div class="col-sm-6 mb-10">
            <div class="row">
                <div class="col-xs-3 col-sm-6 col-md-4">
                    <div class="form-group" ng-class="all_blank ? 'has-error' : ''">
                         <label class="control-label" for="state"><spring:theme code="staff.portal.customer.search.label.postcode" /></label>
                         <div class="control">
                             <input
                                 type="text"
                                 name="postcode"
                                 id="postcode"
                                 class="form-control ml-0"
                                 tabindex="1"
                                 value="${form.postcode}"
                                 ng-model="postcode"
                                 ng-init="updateData('postcode', '${form.postcode}')"/>
                         </div>
                    </div>
                </div>
            </div>
            <span id="customer_errorMessage" class="alert-danger error message block ng-cloak" ng-show="all_blank"><spring:theme code="staff.portal.customer.search.error" /></span>
        </div>

        <div class="col-sm-6 mb-10">
            <div class="row">
                <div class="col-xs-3 col-sm-6 col-md-4">
                    <div class="form-group" ng-class="all_blank ? 'has-error' : ''">
                         <label class="control-label" for="expiryDateMonth"><spring:theme code="staff.portal.customer.search.label.state" /></label>
                         <div class="control">
                             <select id="expiryDateMonth" name="expiryDateMonth" class="form-control" tabindex="1">
                                <option value=""></option>
                                <c:forEach items="${states}" var="element">
                                   <option value="${element.code}" <c:if test="${form.expiryDateMonth eq element.code}">selected</c:if>>${element.name}</option>
                                </c:forEach>
                             </select>
                         </div>
                    </div>
                </div>
            </div>
            <span class="error message block"><spring:theme code="staff.portal.customer.search.state.error" /></span>
            <span id="customer_errorMessage" class="alert-danger error message block ng-cloak" ng-show="all_blank">
                <spring:theme code="staff.portal.customer.search.error" />&nbsp;<spring:theme code="staff.portal.customer.search.state.error" /></span>
        </div>
    </div>

    <div class="form-group">
        <span id="customer_errorMessage" class="error message" ></span>
    </div>

   <div class="row">
        <div class="col-xs-6 col-sm-4 mt-15">
            <div class="form-group">
                <button
                    id="customerSearch_button"
                    type="submit"
                    class="btn btn-block btn-vd-primary btn-large disable-spinner"
                    onclick=""
                    ng-click="submit($event)">
                    <spring:theme code="staff.portal.customer.search.button.search" />
                </button>
            </div>
        </div>
   </div>
</form:form>

<br />
<br />