<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:set value="" var="Star_Blue_outline" />
<c:set value="" var="Star_Blue" />

<c:if test="${isBdeUser}">
    <c:set value="/storefront/_ui/responsive/common/images/Star_Blue_outline.svg" var="Star_Blue_outline" />
    <c:set value="/storefront/_ui/responsive/common/images/Star_Blue.svg" var="Star_Blue" />
</c:if>

<input type="hidden" name ="CSRFToken" value="${CSRFToken.token}"/>					            
<nav:paginationwithsort top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}" numberPagesShown="${numberPagesShown}"/>

<div id="resultsGrid"  class="product__listing product__grid">
     <c:forEach items="${searchPageData.results}" var="product" varStatus="status">
     	<c:set var="product" value="${product}" scope="request"/>
    </c:forEach> 
</div>



<div id="addToCartTitle" class="display-none">
    <div class="add-to-cart-header">
        <div class="headline">
            <span class="headline-text"><spring:theme code="basket.added.to.basket"/></span>
        </div>
    </div>
</div>

<nav:asahiResultsGridJQueryTemplates starOutLine="${Star_Blue_outline}" starBlue="${Star_Blue}"/>
<div id="productLoaded" class="product-loaded-message hide"><spring:theme code="product.listing.loaded"/></div>
