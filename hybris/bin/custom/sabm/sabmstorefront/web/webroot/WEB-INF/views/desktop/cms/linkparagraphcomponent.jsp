<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<c:set value="true" var="showComponent"/>



<c:if test="${component.uid eq 'BillingAndPayment'}">
    <sec:authorize access="!hasAnyRole('ROLE_B2BADMINGROUP','ROLE_B2BINVOICECUSTOMER')">
        <c:set value="false" var="showComponent"/>
    </sec:authorize>


</c:if>

<c:if test="${component.uid eq 'BusinessEnquiry' or component.uid eq 'ServiceRequest' or component.uid eq 'ContactUs' or component.uid eq 'FAQs'}">
    <c:set value="false" var="showComponent"/>

    <div class="col-sm-4 offset-bottom">
        <p>${content}</p>
        <c:url value="${url}" var="componentUrl"/>
        <a href="${componentUrl}"
           class="link-cta <c:if test="${component.uid eq 'BusinessEnquiry' or component.uid eq 'ContactUs'}"> bde-view-only </c:if>">${linkText}</a>
    </div>
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

		<c:if test="${not empty linkText}">
	        <div class="account-information-item-cta">
	            <c:url value="${url}" var="componentUrl"/>
	            <a data-url="${componentUrl}" href="${componentUrl}" class="link-cta linkParagraphtag">${linkText}</a>
	        </div>
		</c:if>
		
		<c:if test="${not empty linkText2 and isInvoiceDiscrepancyEnabled and !isNAPGroup}">
	        <div class="account-information-item-cta">
	            <c:url value="${url2}" var="componentUrl"/>
	            <a data-url="${componentUrl}" href="${componentUrl}" class="link-cta linkParagraphtag">${linkText2}</a>
	        </div>
		</c:if>

		<c:if test="${not empty linkText3 and isInvoiceDiscrepancyEnabled and !isNAPGroup}">
	        <div class="account-information-item-cta">
	            <c:url value="${url3}" var="componentUrl"/>
	            <a data-url="${componentUrl}" href="${componentUrl}" class="link-cta linkParagraphtag">${linkText3}</a>
	        </div>
        </c:if>

    </div>
</c:if>

