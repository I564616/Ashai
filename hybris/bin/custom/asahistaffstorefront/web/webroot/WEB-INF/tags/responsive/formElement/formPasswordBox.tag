<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="idKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean"%>
<%@ attribute name="labelCSS" required="false" type="java.lang.String"%>
<%@ attribute name="inputCSS" required="false" type="java.lang.String"%>
<%@ attribute name="errorPath" required="false" type="java.lang.String"%>
<%@ attribute name="placeholder" required="false" type="java.lang.String"%>
<%@ attribute name="icon" required="false" type="java.lang.String"%>
<%@ attribute name="ngModel" required="false" type="java.lang.String"%>
<%@ attribute name="ngInit" required="false" type="java.lang.String"%>
<%@ attribute name="ngFocus" required="false" type="java.lang.String"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="errorMessage" required="false" type="java.lang.String"%>
<%@ attribute name="validateModel" required="false" type="java.lang.String"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<template:errorSpanField path="${path}" errorPath="${errorPath}" validateModel="${validateModel}" errorMessage="${errorMessage}">
	<ycommerce:testId code="LoginPage_Item_${idKey}">
		<label class="control-label ${fn:escapeXml(labelCSS)}" for="${fn:escapeXml(idKey)}">
			<spring:theme code="${labelKey}" />
			<c:if test="${mandatory != null && mandatory == false}">
				<span>&nbsp;<spring:theme code="login.optional" /></span>
			</c:if>
		</label>

		 <spring:theme code="${placeholder}" var="placeHolderMessage" htmlEscape="false"/>

        <c:if test="${not empty icon}"><span class="login-page-icon"><img src="${icon}"></span></c:if>
		<form:password
		    cssClass="${fn:escapeXml(inputCSS)}"
		    id="${idKey}"
		    path="${path}"
		    placeholder="${placeHolderMessage}"
		    autocomplete="off"
		    ng-init="${ngInit}"
		    ng-model="${ngModel}"
		    ng-focus="${ngFocus}"
		    value="${value}" />
	</ycommerce:testId>
</template:errorSpanField>
