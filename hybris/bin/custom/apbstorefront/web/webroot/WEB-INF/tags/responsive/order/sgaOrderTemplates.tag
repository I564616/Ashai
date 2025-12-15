<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>

<spring:url value="/my-account/saved-carts/" var="savedCartsLink" htmlEscape="false"/>
<c:set var="searchUrl" value="/my-account/saved-carts?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/>
<c:set var="quickOrderTemplateCount" value="0"/>

<div class="account-section-header user-register__headline secondary-page-title">
    <spring:theme code="text.account.savedCarts"/>
</div>

<p class="account-orderhistory-label"><label:message messageCode="order.template.list.heading.apb"/></p>

<div class="quick-order-section">
    <div class="checkout_subheading"><spring:theme code="sga.quick.order.section.heading"/></div>

    <div class="row no-margin">
        <div class="quick-order-section-subheading section-subheading">
            <spring:theme code="sga.quick.order.section.subheading"/>
        </div>
    </div>

    <div class="account-section-content">
        <div class="account-overview-table saved__carts__overview--table">
            <table class="responsive-table">
                <thead>
                    <tr class="responsive-table-head hidden-xs">
                        <th><spring:theme code="text.account.savedCart.orderName" /></th>
                        <th></th>
                        <th></th>
                        <th><spring:theme code="text.account.savedCart.orderQuantity" /></th>
                        <th></th>
                        <th></th>
                    </tr>
                </thead>
                <c:forEach items="${searchPageData.results}" var="savedCart" varStatus="loop">
                    <c:if test="${savedCart.isQuickOrder}" >
                        <c:set var="quickOrderTemplateCount" value="${quickOrderTemplateCount + 1}"/>
                        <tr id="row-${loop.index}" class="responsive-table-item">
                            <td class="hidden-sm hidden-md hidden-lg">
                                <spring:theme code='text.account.savedCart.name'/>
                            </td>
                            <td class="responsive-table-cell saved-cart-name">
                                <ycommerce:testId code="savedCarts_name_link">
                                    <a href="${savedCartsLink}${ycommerce:encodeUrl(savedCart.code)}"
                                    class="responsive-table-link js-saved-cart-name">
                                        ${fn:escapeXml(savedCart.name)}
                                    </a>
                                </ycommerce:testId>
                            </td>

                            <td class="responsive-table-cell">
                            </td>
                            <td></td>

                            <td class="hidden-sm hidden-md hidden-lg">
                                <spring:theme code='text.account.savedCart.description'/>
                            </td>
                            <td class="responsive-table-cell saved-cart-description">
                                <ycommerce:testId code="savedCarts_description_label">
                                    <span class="js-saved-cart-description">
                                        ${savedCart.totalOrderedQty}
                                    </span>
                                </ycommerce:testId>
                            </td>
                            
                            <td></td>
                            <td class="hidden-xs">
                                <div style="height:50px;"></div>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
            </table>  
        </div>
    </div>  
</div>

<div class="order-templates-section">
    <div class="checkout_subheading"><spring:theme code="sga.order.templates.section.heading"/></div>
    <div class="row no-margin">
        <div class="order-templates-section-subheading section-subheading">
            <spring:theme code="sga.order.templates.section.subheading"/>
        </div>
    </div>

    <div class="account-orderhistory-pagination">
        <c:choose>
           <c:when test="${quickOrderTemplateCount > 0}">
                <nav:quickorderpaginationwithdisplay quickOrderTemplateCount="${quickOrderTemplateCount}" top="true" msgKey="text.account.savedCarts.page" showCurrentPageInfo="true" hideRefineButton="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" numberPagesShown="${numberPagesShown}"/>
           </c:when>
           <c:otherwise>
                <nav:paginationwithdisplay top="true" msgKey="text.account.savedCarts.page" showCurrentPageInfo="true" hideRefineButton="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" numberPagesShown="${numberPagesShown}"/>
           </c:otherwise>
        </c:choose>
    </div>
    <c:if test="${(fn:length(searchPageData.results) - quickOrderTemplateCount) == 0}">
        <div class="account-section-content content-empty">
            <ycommerce:testId code="savedCarts_noOrders_label">
                <spring:theme code="text.account.savedCarts.noSavedCarts"/>
            </ycommerce:testId>
        </div>
    </c:if>
    <c:if test="${(fn:length(searchPageData.results) - quickOrderTemplateCount) > 0}">
        <div class="account-section-content">
            <div class="account-overview-table saved__carts__overview--table c">
                <c:set var="cartIdRowMapping" value=''/>
                <table class="responsive-table">
                    <thead>
                        <tr class="responsive-table-head hidden-xs">
                            <th>
                                <spring:theme code="text.account.savedCart.orderName"/>
                            </th>
                            <th>
                                <spring:theme code="text.account.savedCart.createdBy"/>
                            </th>
                            <th>
                                <spring:theme code="text.account.savedCart.dateCreated"/>
                            </th>
                            <th>
                                <spring:theme code="text.account.savedCart.orderQuantity"/>
                            </th>
                            <th></th>
                            <th></th>
                        </tr>
                    </thead>
                    <c:forEach items="${searchPageData.results}" var="savedCart" varStatus="loop">
                        <c:if test="${!savedCart.isQuickOrder}" >
                            <c:choose>
                                <c:when test="${savedCart.importStatus eq 'PROCESSING' }">
                                    <c:set var="importCartIsProcessing" value="true"/>
                                    <c:set var="cartIdRowMapping" value="${cartIdRowMapping}${fn:escapeXml(savedCart.code)}:${loop.index},"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="importCartIsProcessing" value="false"/>
                                </c:otherwise>
                            </c:choose>
                            <tr id="row-${loop.index}" class="responsive-table-item">
                                <td class="hidden-sm hidden-md hidden-lg">
                                    <spring:theme code='text.account.savedCart.name'/>
                                </td>
                                <td class="responsive-table-cell saved-cart-name">
                                    <ycommerce:testId code="savedCarts_name_link">
                                        <a href="${savedCartsLink}${ycommerce:encodeUrl(savedCart.code)}" class="responsive-table-link js-saved-cart-name ${importCartIsProcessing ? 'not-active' : '' }">
                                        ${fn:escapeXml(savedCart.name)}
                                        </a>
                                    </ycommerce:testId>
                                </td>
                                <td class="hidden-sm hidden-md hidden-lg">
                                    <spring:theme code='text.account.savedCart.id'/>
                                </td>
                                <td class="responsive-table-cell">
                                    <ycommerce:testId code="savedCarts_id_label">
                                        ${fn:escapeXml(savedCart.savedBy.name)}
                                    </ycommerce:testId>
                                </td>
                                <td class="hidden-sm hidden-md hidden-lg">
                                    <spring:theme code='text.account.savedCart.dateSaved'/>
                                </td>
                                <td class="responsive-table-cell">
                                    <div class="js-saved-cart-date ${importCartIsProcessing ? 'hidden' : '' }">
                                        <ycommerce:testId code="savedCarts_created_label">
                                            ${savedCart.createdDate}
                                        </ycommerce:testId>
                                    </div>
                                </td>
                                <td class="hidden-sm hidden-md hidden-lg">
                                    <spring:theme code='text.account.savedCart.description'/>
                                </td>
                                <td class="responsive-table-cell saved-cart-description">
                                    <ycommerce:testId code="savedCarts_description_label">
                                        <span class="js-saved-cart-description">
                                            <c:choose>
                                                <c:when test="${importCartIsProcessing}">
                                                    <span class="file-importing js-file-importing">
                                                    <img src="${commonResourcePath}/images/3dots.gif" width="25" height="25"/>
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    ${savedCart.totalOrderedQty}
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </ycommerce:testId>
                                </td>
                                <td class="responsive-table-cell restore-item-column text-center">
                                    <ycommerce:testId code="savedCarts_restore_link">
                                        <a href="#" class="js-restore-saved-cart restore-item-link ${importCartIsProcessing || fn:length(savedCart.entries) < 1 ? 'hidden' : '' }" data-savedcart-id="${fn:escapeXml(savedCart.code)}" data-restore-popup-title="<spring:theme code='text.account.savedcart.restore.popuptitle'/>">
                                            <span class="hidden-xs">
                                                <spring:theme code='text.account.savedCart.restore'/>
                                            </span>
                                            <span class="glyphicon glyphicon-share-alt visible-xs"></span>
                                        </a>
                                    </ycommerce:testId>
                                </td>
                                <td class="responsive-table-cell" id="reorder-button-template-list-page">
                                    <ycommerce:testId
                                        code="savedCarts_delete_link">
                                        <%-- <button type="submit" class="btn btn-primary btn-block re-order reorder-order-listing-template-button" data-savedcart-id="${fn:escapeXml(savedCart.code)}" <c:if test="${savedCart.allProductExcluded != null && savedCart.allProductExcluded.booleanValue()}"><c:out value="disabled"/></c:if>>
                                            <spring:theme code="text.account.savedCarts.edit.template"/>
                                            </button> --%>
                                        <a href="${savedCartsLink}${ycommerce:encodeUrl(savedCart.code)}" class="btn btn-primary btn-block">
                                            <spring:theme code="text.account.savedCarts.edit.template"/>
                                        </a>
                                        <cart:savedCartReorderModalForList savedCart="${savedCart}"/>
                                    </ycommerce:testId>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </table>
                <div class="js-uploading-saved-carts-update" data-id-row-mapping="${cartIdRowMapping}" data-refresh-cart="${refreshSavedCart}" data-refresh-interval="${refreshSavedCartInterval}"></div>
            </div>
            <div class="account-orderhistory-pagination">
                <nav:paginationwithnumbering top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" numberPagesShown="${numberPagesShown}"/>
            </div>
        </div>
    </c:if>
</div>