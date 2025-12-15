<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="spellingSuggestion" required="true" type="de.hybris.platform.commerceservices.search.facetdata.SpellingSuggestionData" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${not empty spellingSuggestion}">
	<div class="searchSpellingSuggestionPrompt">
		<c:url value="${spellingSuggestion.query.url}" var="spellingSuggestionQueryUrl"/>
		<spring:theme code="search.spellingSuggestion.prompt" />&nbsp;<a href="${spellingSuggestionQueryUrl}" style="text-decoration: underline; font-weight:bold">${fn:escapeXml(spellingSuggestion.suggestion)}</a>?
	</div>
</c:if>
