<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="styleClass" required="true" type="java.lang.String" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<spring:htmlEscape defaultHtmlEscape="true"/>
<spring:theme code="search.nav.selectRefinements.title" var="selectRefinements"/>

<button class="${styleClass} disable-spinner" data-select-refinements-title="${selectRefinements}">
    <spring:theme code="search.nav.refine.button"/> 
    <c:if test="${fn:length(searchPageData.breadcrumbs) > 0}">   | <span class="facet-applied-filter"><spring:theme code="mobile.facet.clearall.filter.count" arguments="${fn:length(searchPageData.breadcrumbs)}"/>   </span> </c:if> 
</button>
