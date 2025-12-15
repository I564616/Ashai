<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%@ attribute name="dealPageData" required="true" type="com.sabmiller.facades.deal.DealPageData"%>

<c:forEach items="${dealPageData.facets}" var="facetData">
   <div class="panel-group" role="tablist" aria-multiselectable="true">
   	<input type="hidden" class="facet-code-name" value="${facetData.code}" />
		<div class="panel panel-default">
          <deals:refinementItem facetData="${facetData}"/>
      </div>
   </div>
</c:forEach>

	