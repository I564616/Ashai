<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="pa" tagdir="/WEB-INF/tags/desktop/personalAssistanceService"%>
<!-- DEV need Update -->
<c:url var="sendUrl" value="/sendRequestEmail" />
<c:url value="/authenticatedContactUs" var="contactUsUrl"/>

<c:url var="paSearchUrl" value="/paSearch/results" />

<spring:theme code="text.personal.assistance.search" var="searchTitle"/>
<spring:theme code="text.personal.assistance.form.msg.search.for" var="searchFor"/>
<spring:theme code="text.personal.assistance.form.msg.please.select" var="pleaseSelect"/>

<spring:theme code="text.personal.assistance.form.area.option.account" var="account"/>
<spring:theme code="text.personal.assistance.form.area.option.customer" var="customer"/>
<spring:theme code="text.personal.assistance.form.area.option.user" var="user"/>

<spring:theme code="text.personal.assistance.form.msg.error" var="formError"/>

<div class="service-request offset-bottom-medium" ng-controller="personalAssistanceCtrl" ng-cloak>
  <div class="h1">${searchTitle}</div>  	
  	<form:form name="paRequest" novalidate="novalidate" action="${paSearchUrl }" method="POST" id="paSearchForm">
  		<div class="row">
  			<div class="col-xs-12 col-md-4 form-group">
  	       			<label>${searchFor}</label>
               		<div class="select">
    					 	<span class="arrow"></span>
               				<select name="request_type" class="form-control validate-input" ng-change="submitted = false" ng-model="pa.type" required>
	               				<option value="" disabled selected>${pleaseSelect}</option>
	               				<option value="account">${account}</option>
	               				<option value="customer">${customer}</option>
	               				<option value="user">${user}</option>
  							</select>
    					</div>
  		      	<div>
  		    </div> 
  		  </div>		
  		</div>
  		<div class="clearfix"></div>

  		<!-- Start of Changable Form -->

  		<pa:account/>
  		<pa:customer/>
  		<pa:user/>
  				
  		<!-- End of Changable Form -->
  		      	
       	<div class="form-group" ng-hide="pa.type==null">
       		<button type="button" ng-click="checkValid(paRequest)" class="btn btn-primary"><spring:theme code="text.personal.assistance.form.search"/></button>
                    <span class="error-bold" ng-show="pa.type == 'account' && (account_no.length < 3 || account_no.length == undefined) && submitted && !paRequest.account_no.$error.pattern">
                      ${formError}
                    </span>
                    <span ng-show="pa.type == 'customer' && submitted && !paRequest.customer_no.$error.pattern">
                      <span ng-show="(customer_no.length < 3 || customer_no.length == undefined) || (customer_name.length < 3 || customer_name.length == undefined)">
                          <span class="error-bold" ng-show="paRequest.customer_no.$invalid || paRequest.customer_name.$invalid">
                        ${formError}
                          </span> 
                      </span>  
                    </span>
                    <span class="error-bold" ng-show="pa.type == 'user' && (user_email.length < 3 || user_email.length == undefined) && submitted && !paRequest.user_email.$error.email">
                      ${formError}
                    </span>
       	</div>
  			
  		
  		
  	</form:form>

</div>
