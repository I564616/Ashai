<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="urlTestId" required="false" type="java.lang.String"%>


<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/apbcommorgaddon/responsive/common" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<div class="account-section-header">
	
	<div class="manage_users_headline">
	<spring:theme code="${labelKey}"/></div>
	<div class="account-section-header-add">
	<div class="row">
	<div class="col-xs-12 col-sm-6 col-md-5 no-padding-left">
			<div class="manage-users-results-pagination">
				<nav:paginationwithresults top="true" supportShowPaged="${isShowPageAllowed}" showCurrentPageInfo="true"  supportShowAll="${isShowAllAllowed}"
                                searchPageData="${searchPageData}" hideRefineButton="true"
                                searchUrl="${searchUrl}" msgKey="text.company.manageUser.pageAll"
                                additionalParams="${additionalParams}" numberPagesShown="${numberPagesShown}"/>
             </div>  
		<ycommerce:testId code="${urlTestId}">
			 <a href="${url}" class="button add"><spring:theme code="text.company.addNew.button"/></a>
				</div>
			<div class="col-xs-12 col-sm-6 col-md-4 col-md-offset-3 manage-users-top-pagination">
				<div class="">
				 <nav:paginationonlysort top="true" supportShowPaged="${isShowPageAllowed}" showCurrentPageInfo="true"  supportShowAll="${isShowAllAllowed}"
                                        searchPageData="${searchPageData}" hideRefineButton="true"
                                        searchUrl="${searchUrl}" msgKey="text.company.manageUser.pageAll"
                                        additionalParams="${additionalParams}" numberPagesShown="${numberPagesShown}"/>  
				</div>
				<div class="clearfix"></div>
			</div>
			</div>
		</ycommerce:testId>
	</div>
</div>