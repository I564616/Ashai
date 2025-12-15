<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="idKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean"%>
<%@ attribute name="labelCSS" required="false" type="java.lang.String"%>
<%@ attribute name="inputCSS" required="false" type="java.lang.String"%>
<%@ attribute name="optionalTextCSS" required="false" type="java.lang.String"%>
<%@ attribute name="placeholder" required="false" type="java.lang.String"%>
<%@ attribute name="tabindex" required="false" rtexprvalue="true"%>
<%@ attribute name="autocomplete" required="false" type="java.lang.String"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="readonly" required="false" type="java.lang.Boolean"%>
<%@ attribute name="maxlength" required="false" type="java.lang.Integer"%>
<%@ attribute name="info" required="false" type="java.lang.String"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<template:errorSpanField path="${path}">
	<ycommerce:testId code="LoginPage_Item_${idKey}">
		<label class="control-label ${labelCSS}" for="${idKey}">
			<spring:theme code="${labelKey}" />
			<c:if test="${mandatory != null && mandatory == false}">
				<span id="optional-text ${optionalTextCSS}" class="font-weight-normal italic"><spring:theme code="login.optional" /></span>
			</c:if>
		</label>

		<%-- Info dialog added --%>
		<c:if test="${not empty info}">
		    <span class="payment-info-icon" title="${info}">i</span>
		</c:if>

		<spring:theme code="${placeholder}" var="placeHolderMessage" />
        
        <c:choose>
            <c:when test="${cmsPage.uid eq 'login' ||cmsPage.uid eq 'checkout-login' || (isForgetPassword != null && isForgetPassword == 'true')||cmsPage.uid eq 'updatePassword'}">
                <!--                Input field is different for login page.-->
                <div class="input-group" id="login-inputs">
					<span id="login-page-icon" class="input-group-addon"><img class="login-inputs" src="/storefront/_ui/responsive/common/images/icon-email-asahigrey.svg"  /></span>
                      <form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
                                tabindex="${tabindex}" autocomplete="on" placeholder="${placeHolderMessage}"
                                disabled="${disabled}" readonly="${readonly}" maxlength="${maxlength}"/>
                </div>
            </c:when>
            <c:otherwise>
                <form:input cssClass="${inputCSS} form-control" id="${idKey}" path="${path}"
                        tabindex="${tabindex}" autocomplete="${autocomplete}" placeholder="${placeHolderMessage}"
                        disabled="${disabled}" readonly="${readonly}" maxlength="${maxlength}"/>
            </c:otherwise>
        </c:choose>						
	</ycommerce:testId>
</template:errorSpanField>
