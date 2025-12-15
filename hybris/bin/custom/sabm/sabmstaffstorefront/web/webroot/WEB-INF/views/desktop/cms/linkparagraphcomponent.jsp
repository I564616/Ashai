<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set value="true" var="showComponent" />
<c:if test="${component.uid eq 'BillingAndPayment'}">
	<sec:authorize access="!hasAnyRole('ROLE_B2BADMINGROUP','ROLE_B2BINVOICECUSTOMER')">
		<c:set value="false" var="showComponent" />
	</sec:authorize>
</c:if>
<c:if test="${showComponent}">
	<div class="col-xs-12 col-sm-6 account-information-item">
	     <h2 class="account-information-item-title">
		<svg class="${icon}">
		    <use xlink:href="#${icon}"></use>    
		</svg>
	     ${title}</h2>
	     <div class="account-information-item-description">
	         <p class="account-information-item-content">${content}</p>
	     </div>
	     <div class="account-information-item-cta">
	     	<c:url value="${url}" var="componentUrl" />
	        <a href="${componentUrl}" class="link-cta">${linkText}</a>
	     </div>
	 </div>      
</c:if>