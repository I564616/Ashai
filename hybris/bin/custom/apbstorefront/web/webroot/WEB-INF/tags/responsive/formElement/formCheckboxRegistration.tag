<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="idKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean"%>
<%@ attribute name="labelCSS" required="false" type="java.lang.String"%>
<%@ attribute name="inputCSS" required="false" type="java.lang.String"%>
<%@ attribute name="tabindex" required="false" type="java.lang.String"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<template:errorSpanField path="${path}">
	<spring:theme code="${idKey}" var="themeIdKey" />
	<c:set value="checkboxId" var="checkboxId" />
	<c:set value="false" var="termConditionFlag" />
	<c:if test="${cmsPage.labelOrId eq 'register'}">
		<c:set value="checkboxId" var="checkboxId" />
		<c:set value="true" var="termConditionFlag" />
	</c:if>
	<div class="checkbox" id="${checkboxId}">
   <c:url value="/termsAndConditions" var="termsAndConditions"/>
   <c:url value="/termsAndLegal" var="termsAndLegal"/>
		<label class="control-label ${labelCSS}" for="${themeIdKey}">
			<form:checkbox cssClass="${inputCSS}" id="${themeIdKey}"
				path="${path}" tabindex="${tabindex}" /> <span class="term-condition-text"><spring:theme
				code="${labelKey}" /> </span>  
					<spring:theme code="register.terms.and.conditions.description" />
					<a href="${termsAndConditions}" class="password-forgotten"
						target="_blank" id="termCondOther"> 
						<spring:theme code="register.terms.and.condition.link" /></a> 
			
					<a href="${termsAndLegal}" class="hidden site-anchor-link"
						target="_blank" id="termLondOther"> 
						<spring:theme code="register.terms.and.legal.link" /></a> 

			<c:if test="${mandatory != null && mandatory == true}">
				<span class="mandatory"> <spring:theme code="login.required"
						var="loginrequiredText" />
				</span>
			</c:if> <span class="skip"><form:errors path="${path}" /></span>
		</label>
	</div>

</template:errorSpanField>
