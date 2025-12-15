<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>


<template:page pageTitle="${pageTitle}">

	<div class="login-section">
		<div class="login-page__headline">
			<spring:theme code="account.register.heading" />
		</div>
	</div>
	<div class="user-register__body">
		<!---This class has not been defined in registration.less yet-->
		<c:choose>
		    <c:when test="${cmsSite.uid eq 'sga'}">
		       <spring:theme code="sga.account.register.customer" />
		    </c:when>    
	    	<c:otherwise>
	        	<spring:theme code="account.register.customer" />
	    	</c:otherwise>
		</c:choose>
		
		
	</div>
	<input type="hidden" id="customerType" value="${customer}">
	<input type="hidden" id="requestCustomerType" value="${requestCustomer}">
	<div class="row">
		<div class="col-md-3 col-sm-6">
			<div class="col-md-6 col-sm-6 no-padding">
			<div id="u8442" class="ax_default droplist">
				<br> 
				<c:choose>
					<c:when test="${cmsSite.uid eq 'sga'}">	
						<select id="customer-select">   <!--		Removing class="form-control" for SGA as it causes screen issues on smaller screen sizes on windows computer on Chrome.		-->
							<option value="">Please select</option>
							<option value="true">Yes</option>
							<option value="false">No</option>
						</select>
					</c:when>
					<c:otherwise>
						<select class="form-control" id="customer-select">
							<option value="">Please select</option>
							<option value="true">Yes</option>
							<option value="false">No</option>
						</select>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		</div>
	</div>

	<!-- For Self Registration -->
	<div id="self-registration" style="display: none">
		<div class="register__section">
			<c:url value="/register/self-customer" var="registerActionUrl" />
			<user:register actionNameKey="register.submit"
				action="${registerActionUrl}" />
		</div>
	</div>
	

	<!-- For Request for Registration -->
	<div id="request-registration" style="display: none">
		<!-- c:url value="/register/request-register" var="reqRegisterActionUrl" / -->
		<!-- user:registerRequest action="${reqRegisterActionUrl}"
			actionNameKey="request-register.submit" / -->
        <user:newUserRegisterApb/>
	</div>
	
	<!--  Request Registration For SGA-->
	<div id="sga-request-registration" style="display: none">
		<user:newUserRegisterSga/>
	</div>
	
</template:page>