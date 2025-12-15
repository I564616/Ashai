<%@ attribute name="sorts" required="true" type="java.lang.Object"%>
<%@ attribute name="queryParam" required="true" type="java.lang.String"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:htmlEscape defaultHtmlEscape="false" />

<div class="account-orderhistory-pagination">
    <div class="pagination-bar top">
        <div class="pagination-toolbar">
            <div class="helper clearfix hidden-md hidden-lg"></div>
            <div class="sort-refine-bar">
                <div class="row">
                    <c:if test="${not empty sorts}">
                        <div class="col-xs-12 col-sm-7 col-md-12">
                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-12">
                                    <div class="form-group sort_by">
                                        <label class="control-label " for="sortForm1">
                                            <b><spring:theme code="search.page.sortTitle" /></b>
                                        </label>
                                        <form id="sortForm1" name="sortForm1" method="get">
                                            <!-- Remove for Sort option done as per the VD 34 -->
                                                <ul id="sortOptions1" class="sortby_list">
                                                    <c:forEach items="${sorts}" var="sort">
                                                        <li data-option="${sort}" ${queryParam == sort? 'class="sel"' : ''}>
                                                            ${fn:escapeXml(sort)}
                                                        </li>
                                                    </c:forEach>
                                                </ul>
                                                <input type="hidden" name="sort" value="" />
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>