<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>


<c:set value="${['Online Activity', 'Account Number', 'Account Name', 'Address', 'Suburb', 'Postcode']}" var="tHeader" />

<div class="row">
    <div class="col-sm-8">
        <h1 class="h2 bold login-page__headline"><spring:theme code="staff.portal.customer.search.title" /></h1>
        <p class="h5 mt-25 mb-30"><spring:theme code="staff.portal.customer.search.text" /></p>

	    <c:url value="/doCustomerSearch" var="customerSearchFormAction" scope="request"/>
	    <input id="customerSearch_formAction" type="hidden" value="${customerSearchFormAction }">

        <jsp:include page="customerSearchForm.jsp" />
    </div>
</div>

<div class="account-orderhistory-pagination">
    <nav:paginationwithdisplay top="true" msgKey="text.multi.account.unit.page" showCurrentPageInfo="true" hideRefineButton="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" numberPagesShown="${numberPagesShown}" />
</div>

