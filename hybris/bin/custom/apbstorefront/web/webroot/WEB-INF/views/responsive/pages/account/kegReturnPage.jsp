<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<template:page pageTitle="${pageTitle}">

	<c:url value="/keg-return/keg-return-request" var="kegReturnUrl" />
	<form:form modelAttribute="apbKegReturnForm" method="post"
		action="${kegReturnUrl}">
<div class="keg-returns-section">
	<div id="kegGlobalErrorMessage"
	class="hide alert alert-danger alert-dismissable"><spring:theme code="form.global.error"/></div>
		<div class="login-section">
			<div class="login-page__headline">
				<spring:theme code="keg.return.heading" />
			</div>
		</div>
		<div class="user-register__body">
			<spring:theme code="keg.return.heading.content" />
		</div>
		<div class="keg-returns-content">
			<user:kegdetailsMyaccount/>
		</div>		
		<div class="row keg-return-pickup-address">
			<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
				<div class="form-group">
					  <label class="control-label" for="${pickupAddress}">
						<spring:theme code="keg.return.pickup.address"/>
					</label>	
					   <select class="form-control" id="pickupAddress" name="pickupAddress">
				            <c:forEach items="${pickupAddress}" var="deliveryAddress" varStatus="status">
				            	<c:choose>
				            		<c:when test="${deliveryAddress.defaultAddress eq 'true'}">
				            			<option id="${status.count}" value="${deliveryAddress.recordId}">Default Address,  ${deliveryAddress.town}</option>
				            		</c:when>
				            		<c:otherwise>
				            			<option id="${status.count}" value="${deliveryAddress.recordId}"> Alt Add ${status.count-1},  ${deliveryAddress.town}</option>
				            		</c:otherwise>
				            	
				            	</c:choose>
				           	 </c:forEach>
    					    </select>  
				</div>
				<div>
					<span id="pickupAddressErrors" class="hide alert alert-danger alert-dismissable">
						<spring:theme code="keg.return.keg.size.mandatory.field"/>
					</span>
				</div>
			</div>
		</div>
			
			<user:unitAddress/>
    
		<div class="row keg-return-comments">
			<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
				<div class="form-group">
					<formElement:formTextArea idKey="kegadditionalComments" 
						labelKey="keg.return.additional.comments" path="kegComments"  labelCSS="control-label"						
						areaCSS="form-control" mandatory="false" maxlength="${inputCommentsMaxSize}" />
				</div>
			</div>
		</div>
		
		<div class="row">
			<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
				<div class="form-actions clearfix">
					<button type="submit" id="keg-returns-submit"
						class="btn btn-default btn-block btn-vd-primary">
						<spring:theme code="keg.return.request.button" />
					</button>
				</div>
			</div>
		</div>
	 </div>
	</form:form>
</template:page>