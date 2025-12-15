<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>
<div class="row" id="resultsListRow">
	<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
		<product:productListerItem product="${product}" productListPosition="${status.count}"/>
	</c:forEach>
</div>
<nav:backToTop/>
<product:productPricePopup/>