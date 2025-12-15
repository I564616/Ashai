<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="searchUrl" required="true" %>
<%@ attribute name="searchPageData" required="true"
              type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ attribute name="top" required="true" type="java.lang.Boolean" %>
<%@ attribute name="showTopTotals" required="false" type="java.lang.Boolean" %>
<%@ attribute name="supportShowAll" required="true" type="java.lang.Boolean" %>
<%@ attribute name="supportShowPaged" required="true" type="java.lang.Boolean" %>
<%@ attribute name="additionalParams" required="false" type="java.util.HashMap" %>
<%@ attribute name="msgKey" required="false" %>
<%@ attribute name="showCurrentPageInfo" required="false" type="java.lang.Boolean" %>
<%@ attribute name="hideRefineButton" required="false" type="java.lang.Boolean" %>
<%@ attribute name="numberPagesShown" required="false" type="java.lang.Integer" %>
<%@ taglib prefix="pagination" tagdir="/WEB-INF/tags/responsive/nav/pagination" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="themeMsgKey" value="${not empty msgKey ? msgKey : 'search.page'}"/>
<c:set var="showCurrPage" value="${not empty showCurrentPageInfo ? showCurrentPageInfo : false}"/>
<c:set var="hideRefBtn" value="${hideRefineButton ? true : false}"/>
<c:set var="showTotals" value="${empty showTopTotals ? true : showTopTotals}"/>

<c:if test="${searchPageData.pagination.totalNumberOfResults == 0 && top && showTotals}">
    <div class="paginationBar top clearfix">
        <ycommerce:testId code="searchResults_productsFound_label">
            <div class="totalResults"><spring:theme code="${themeMsgKey}.totalResults"
                                                    arguments="${searchPageData.pagination.totalNumberOfResults}"/></div>
        </ycommerce:testId>
    </div>
</c:if>
<c:if test="${searchPageData.pagination.totalNumberOfResults > 0}">
    <div class="pagination-bar ${(top)?"top":"bottom"}">
        <div class="pagination-toolbar">
        	<div class="helper clearfix hidden-md hidden-lg"></div>
                <div class="sort-refine-bar">
                    <div class="row">
           	 			<c:if test="${not empty searchPageData.sorts}">
           	 			
	           	 			<div class="col-xs-12 col-sm-12 col-md-5 prices-text">
	           	 			</div>
	           	 			
	                        <div class="col-xs-12 col-sm-6 col-md-7 ">
	
	                        </div>
	                        
	                        
	                        <div class="col-xs-12 col-sm-12 col-md-12 pagination-wrap">
	                            <pagination:pageSelectionPagination searchUrl="${searchUrl}" searchPageData="${searchPageData}"
	                                                                numberPagesShown="${numberPagesShown}"
	                                                                themeMsgKey="${themeMsgKey}"/>
	                        </div>
           				 </c:if>

   				</div>  
    		</div>
   		</div>
 </div>
</c:if>