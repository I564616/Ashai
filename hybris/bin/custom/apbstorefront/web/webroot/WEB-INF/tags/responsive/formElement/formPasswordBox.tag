<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="idKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean"%>
<%@ attribute name="labelCSS" required="false" type="java.lang.String"%>
<%@ attribute name="inputCSS" required="false" type="java.lang.String"%>
<%@ attribute name="errorPath" required="false" type="java.lang.String"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:htmlEscape defaultHtmlEscape="true" />
        <c:set value="" var="pwd"/>
    <c:if test="${cmsPage.labelOrId eq 'login' || cmsPage.labelOrId eq 'checkout-login'}">
        <c:set value="PASSWORD" var="pwd"/>
    </c:if>
  
     <c:if test="${cmsPage.labelOrId eq 'updatePassword'}">
	     <c:if test="${idKey eq 'password'}">
	     	  <c:set value="NEW PASSWORD" var="pwd"/>
	     </c:if>
	     <c:if test="${idKey eq 'updatePwd.checkPwd'}">
	     	    <c:set  value="CONFIRM NEW PASSWORD" var="pwd"/>
	      </c:if>
    </c:if>
    
    
<template:errorSpanField path="${path}" errorPath="${errorPath}">
	<ycommerce:testId code="LoginPage_Item_${idKey}">
		<label class="control-label ${labelCSS}" for="${idKey}">
			<spring:theme code="${labelKey}" />
			<c:if test="${mandatory != null && mandatory == false}">
				<span class="font-weight-normal italic">&nbsp;<spring:theme code="login.optional" /></span>
			</c:if>
		</label>
       
        <c:choose>
            <c:when test="${cmsPage.uid eq 'login' || cmsPage.uid eq 'checkout-login' || (isForgetPassword != null && isForgetPassword == 'true')  
                    || (isPasswordUpdated !=null && isPasswordUpdated=='true')}">
<!--                Password field is different for login page.-->
                <div class="input-group" id="login-inputs">
					<span id="login-page-icon" class="input-group-addon"><img class="login-inputs" src="/storefront/_ui/responsive/common/images/icon-lock-asahigrey.svg"  /></span>
                    <form:password cssClass="${inputCSS}" id="${idKey}" path="${path}" placeholder="${pwd}" autocomplete="off" />
                </div>
            </c:when>
            <c:otherwise>
                <form:password cssClass="${inputCSS}" id="${idKey}" path="${path}" placeholder="${pwd}" autocomplete="off"/>
            </c:otherwise>
        </c:choose>
	</ycommerce:testId>
</template:errorSpanField>
