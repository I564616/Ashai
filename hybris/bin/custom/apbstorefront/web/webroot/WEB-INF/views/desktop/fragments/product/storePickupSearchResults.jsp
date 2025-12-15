<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="pagination" tagdir="/WEB-INF/tags/desktop/storepickup/pagination" %>

<c:url var="pickupInStoreUrl" value="/store-pickup/${searchPageData.product.code}/pointOfServices"/>

<storepickup:pickupStoreResults searchPageData="${searchPageData}" cartPage="${cartPage}" entryNumber="${entryNumber}"/>


	<div class="searchPOSPaging clearfix">
		<c:if test="${(searchPageData.pagination.totalNumberOfResults gt 0)}">
			<pagination:storePickupPageSelectPagination pickupInStoreUrl="${pickupInStoreUrl}" searchPageData="${searchPageData}" numberPagesShown="${numberPagesShown}"/>
		</c:if>
		<c:if test="${(searchPageData.pagination.totalNumberOfResults eq 0)}">
			<spring:theme code="search.no.results"/>
		</c:if>
	</div>
