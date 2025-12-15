<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:url value="/search/" var="searchUrl" />
<spring:url value="/search/autocomplete/{/componentuid}" var="autocompleteUrl" htmlEscape="false">
     <spring:param name="componentuid"  value="${component.uid}"/>
</spring:url>

<div class="ui-front">
	<form name="search_form_${fn:escapeXml(component.uid)}" method="get"
		action="${searchUrl}">
		<div class="input-group">
			<spring:theme code="search.placeholder" var="searchPlaceholder" />

			<ycommerce:testId code="header_search_input">
				<input type="text" id="js-site-search-input"
					class="form-control js-site-search-input" name="text" value=""
                    maxlength="100" placeholder="${searchPlaceholder}"
					data-options='{"autocompleteUrl" : "${autocompleteUrl}","minCharactersBeforeRequest" : "${component.minCharactersBeforeRequest}","waitTimeBeforeRequest" : "${component.waitTimeBeforeRequest}","displayProductImages" : ${component.displayProductImages}}'>
			</ycommerce:testId>

			<span class="input-group-btn" id="search-btn-header"> <ycommerce:testId code="header_search_button">
					<button class="btn btn-link js_search_button" type="submit">		
						<c:choose>
							<c:when test="${cmsSite.uid ne 'sga'}">
								<span id="search-header-icon" class="input-group-addon"><img class="login-inputs" src="/storefront/_ui/responsive/common/images/icon-search-white.svg"  /></span>
							</c:when>
							 <c:otherwise>
								<span id="search-header-icon" class="input-group-addon"><img class="login-inputs" src="/storefront/_ui/responsive/common/images/icon-search.svg"  /></span>
							</c:otherwise>
						</c:choose>
					</button>
				</ycommerce:testId>
			</span>
		</div>
	</form>

</div>
