<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ attribute name="path" required="true" rtexprvalue="true"%>
<%@ attribute name="errorPath" required="false" rtexprvalue="true"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:bind path="${not empty errorPath ? errorPath : path}">

   <c:set value="form-group" var="regFormGroup"/>
 	<c:if test="${cmsPage.labelOrId eq 'register'}">
	 	 <c:set value="form-group" var="regFormGroup"/>
<!--        Changing it to form-group to change back to bootstrap classes, and fixing alignment issues between input fields on registration page. @SM-->
	</c:if> 
	  
	<c:choose>
		<c:when test="${not empty status.errorMessages}">
			<div class="form-group has-error"> 
				<jsp:doBody />
				<div class="help-block contactus" id="contactus">
					<c:choose>
                    <c:when test="${fn:contains(path, 'abnAccountId')}">
                        <form:errors path="${not empty errorPath ? '' : path}" htmlEscape="false" /><c:out value="${customerCareAccountAbn}"/>
                    </c:when>
                    <c:otherwise>
                        <form:errors path="${not empty errorPath ? '' : path}" /><c:out value="${customerCareAccountAbn}"/>
                    </c:otherwise>      
                </c:choose> 
				
					
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<div class="${regFormGroup}">
				<jsp:doBody />
			</div>
		</c:otherwise>
	</c:choose>
</spring:bind>
