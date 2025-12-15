<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ attribute name="path" required="true" rtexprvalue="true"%>
<%@ attribute name="errorPath" required="false" rtexprvalue="true"%>
<%@ attribute name="errorMessage" required="false" type="java.lang.String"%>
<%@ attribute name="validateModel" required="false" type="java.lang.String"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<spring:bind path="${not empty errorPath ? errorPath : path}">
	<c:choose>
		<c:when test="${not empty status.errorMessages}">
			<div class="form-group has-error">
				<jsp:doBody />
				<div class="help-block">
					<form:errors path="${not empty errorPath ? '' : path}" />
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<div class="form-group" ng-class="${validateModel} ? 'has-error' : ''">
				<jsp:doBody />
				<span id="customer_errorMessage" class="alert-danger error message ng-cloak block" ng-show="${validateModel}">
				    <c:choose>
				        <c:when test="${not empty errorMessage}">
				            ${errorMessage}
				        </c:when>
				        <c:otherwise>
				            <spring:theme code="staff.portal.customer.search.error" />
                        </c:otherwise>
				    </c:choose>

				</span>
			</div>
		</c:otherwise>
	</c:choose>
</spring:bind>
