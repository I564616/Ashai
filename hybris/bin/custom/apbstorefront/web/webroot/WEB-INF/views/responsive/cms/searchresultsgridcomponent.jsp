<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<div id="showSpinner"></div>
<div class="product__list--wrapper">

    <nav:paginationwithsort top="true"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>

    <div class="results">
        <h1  style="font-weight:100; margin-bottom:20px;"><spring:theme code="search.page.searchText" arguments="${searchPageData.pagination.totalNumberOfResults}"/> <span style="font-weight: bold;">${searchPageData.freeTextSearch}</span></h1>
    </div>
    <nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" />
    
    <ul class="product__listing product__grid" id="resultsGrid">
        <c:forEach items="${searchPageData.results}" var="product" varStatus="status">
        	<c:set var="product" value="${product}" scope="request"/>
        </c:forEach>
    </ul>

    <div id="addToCartTitle" class="display-none">
        <div class="add-to-cart-header">
            <div class="headline">
                <span class="headline-text"><spring:theme code="basket.added.to.basket"/></span>
            </div>
        </div>
    </div>
	
	<nav:asahiResultsGridJQueryTemplates />
	<div id="productLoaded" class="product-loaded-message hide"><spring:theme code="search.product.listing.loaded"/></div>
</div>