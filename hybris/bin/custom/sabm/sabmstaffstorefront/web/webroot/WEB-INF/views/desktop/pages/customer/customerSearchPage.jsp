<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>


<nav:steps current="1"/>

<div class="row">
  <div class="col-sm-8">
    <h1 class="h1"><spring:theme code="staff.portal.customer.search.title" /></h1>
    <p class="h2"><spring:theme code="staff.portal.customer.search.by" /></p>

	 <c:url value="/doCustomerSearch" var="customerSearchFormAction" scope="request"/>
	 <input id="customerSearch_formAction" type="hidden" value="${customerSearchFormAction }">
    <form:form id="customerSearchForm" modelAttribute="sabmCustomerSearchForm" action="${customerSearchFormAction }" method="POST" class="margin-top-30" >
    
     <div class="row">
       <div class="form-group col-md-6" ng-class="{'has-error' : form.customeremail.$error.minlength}">
         <label for="email"><spring:theme code="staff.portal.customer.search.label.email" /></label>
         <form:input type="text"
         class="form-control"
         path="email"
         name="email"
         ng-model="email"
         id="email"
         ng-minlength="3"/>
       </div>
     </div>
	
     <div class="row">
       <div class="form-group col-md-6" ng-class="{'has-error' : form.accountPayerNumber.$error.minlength}">
         <label for="accountPayerNumber"><spring:theme code="staff.portal.customer.search.label.account" /></label>
         <form:input type="text"
         class="form-control"
         path="accountPayerNumber"
         name="accountPayerNumber"
         ng-model="accountPayerNumber"
         id="accountPayerNumber"
         ng-minlength="3"/>
       </div>

       <div class="form-group col-md-6" ng-class="{'has-error' : form.customerName.$error.minlength}">
         <label for="customerName"><spring:theme code="staff.portal.customer.search.label.name" /></label>
         <form:input type="text"
         class="form-control"
         path="customerName"
         name="customerName"
         ng-model="customerName"
         id="customerName"
         ng-minlength="3"/>
       </div>
     </div>

     <div class="row">
       <div class="form-group col-md-6" ng-class="{'has-error' : form.address.$error.minlength}">
         <label for="address"><spring:theme code="staff.portal.customer.search.label.address" /></label>
         <form:input type="text"
         class="form-control"
         path="address"
         name="address"
         ng-model="address"
         id="address"
         ng-minlength="3"/>
       </div>
       <div class="form-group col-md-6" ng-class="{'has-error' : form.suburb.$error.minlength}">
         <label for="suburb"><spring:theme code="staff.portal.customer.search.label.suburb" /></label>
         <form:input type="text"
         class="form-control"
         path="suburb"
         name="suburb"
         ng-model="suburb"
         id="suburb"
         ng-minlength="3"/>
       </div>
     </div>

     <div class="row">
       <spring:theme code="staff.portal.customer.search.state.select" var="pleaseSelect"/>
       <spring:theme code="staff.portal.customer.search.state.act" var="act"/>
       <spring:theme code="staff.portal.customer.search.state.nsw" var="nsw"/>
       <spring:theme code="staff.portal.customer.search.state.nt" var="nt"/>
       <spring:theme code="staff.portal.customer.search.state.qld" var="qld"/>
       <spring:theme code="staff.portal.customer.search.state.sa" var="sa"/>
       <spring:theme code="staff.portal.customer.search.state.tas" var="tas"/>
       <spring:theme code="staff.portal.customer.search.state.vic" var="vic"/>
       <spring:theme code="staff.portal.customer.search.state.wa" var="wa"/>

       <div class="form-group col-xs-6 col-md-3">
         <label><spring:theme code="staff.portal.customer.search.label.state" /></label>
         <div class="select">
           <span class="arrow"></span>
           <select name="expiryDateMonth" ng-model="expiryDateMonth" class ="cc-expiry-form form-control validate-input" required>
            <option value="" selected>${pleaseSelect}</option>
            <option value="${act}">${act}</option>
            <option value="${nsw}">${nsw}</option>
            <option value="${nt}">${nt}</option>
            <option value="${qld}">${qld}</option>
            <option value="${sa}">${sa}</option>
            <option value="${tas}">${tas}</option>
            <option value="${vic}">${vic}</option>
            <option value="${wa}">${wa}</option>
          </select>
        </div>
      </div>

      <div class="form-group col-xs-6 col-md-2 col-md-push-3" ng-class="{'has-error' : form.postcode.$error.minlength}">
       <label for="postcode"><spring:theme code="staff.portal.customer.search.label.postcode" /></label>

       <form:input type="text" class="form-control" path="postcode" name="postcode" ng-model="postcode" id="postcode" ng-minlength="3"/>
     </div>
   </div>	
   
  <div class="form-group">
     	<span id="customer_errorMessage" class="error message" ><spring:theme code="staff.portal.customer.search.limitNum" /></span>
   </div>
   <div class="row">
     <div class="form-group col-md-6">
       <div class="offset-bottom-large">
         <button id="customerSearch_button" type="button" class="btn btn-primary btn-large btn-flex-fixed"><spring:theme code="staff.portal.customer.search.button.search" /></button>
       </div>
     </div>
   </div>
 </form:form>
</div>
</div>
