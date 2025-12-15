<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>



 <nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" /> 

<%-- <nav:pagination top="true"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/> --%>

<div class="row productList" id="resultsListRow">
	<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
		<product:productListerItem product="${product}" productListPosition="${status.count}"/>
	</c:forEach>
</div>

<%-- <nav:pagination top="false"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>
<storepickup:pickupStorePopup /> --%>