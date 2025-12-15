<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="pagination" tagdir="/WEB-INF/tags/responsive/nav/pagination" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>

<c:set value="${( request.getParameter('sort') == 'byRecommendedDate' || empty request.getParameter('sort') ) ? 'font-weight-bold' : '' }" var="sortByDate" />
<c:set value="${ request.getParameter('sort') == 'byBrandNameAsc' ? 'font-weight-bold' : '' }" var="sortByAsc" />
<c:set value="${ request.getParameter('sort') == 'byBrandNameDesc' ? 'font-weight-bold' : '' }" var="sortByDesc" />

<c:set var="currentPageItems" value="${(searchPageData.pagination.currentPage + 1) * searchPageData.pagination.pageSize}"/>
<c:set var="upTo" value="${(currentPageItems > searchPageData.pagination.totalNumberOfResults ? searchPageData.pagination.totalNumberOfResults : currentPageItems)}"/>
<c:set var="currentPage" value="${searchPageData.pagination.totalNumberOfResults == 0 ? 0 : searchPageData.pagination.currentPage * searchPageData.pagination.pageSize + 1} - ${upTo}"/>

<spring:url value="recommendation" var="sortByDateQuery" htmlEscape="true">
    <spring:param name="sort" value="byRecommendedDate"/>
</spring:url>
<spring:url value="recommendation" var="sortByBrandNameAsc" htmlEscape="true">
    <spring:param name="sort" value="byBrandNameAsc"/>
</spring:url>
<spring:url value="recommendation" var="sortByBrandNameDesc" htmlEscape="true">
    <spring:param name="sort" value="byBrandNameDesc"/>
</spring:url>
<c:set var="searchUrl">
    <c:choose>
        <c:when test="${request.getParameter('sort') == 'byRecommendedDate'}">
            ${sortByDateQuery}
        </c:when>
        <c:when test="${request.getParameter('sort') == 'byBrandNameAsc'}">
            ${sortByBrandNameAsc}
        </c:when>
        <c:when test="${request.getParameter('sort') == 'byBrandNameDesc'}">
            ${sortByBrandNameDesc}
        </c:when>
    </c:choose>
</c:set>

<div class="account-section-header user-register__headline secondary-page-title"><spring:theme code="text.rep.recommendation.page.title" /></div>
<spring:theme code="text.rep.recommendation.page.heading" arguments="${customerName}" />   <br/>

<div class="pagination-bar-results-top display-flex justify-content-between flex-direction-xs-column mt-35 mt-xs-15 mb-20">
    <div class="flex-direction-xs-row mb-xs-10"><spring:theme code="sga.text.recommendation.sorting.showing" arguments="${currentPage}, ${searchPageData.pagination.totalNumberOfResults}" /></div>
    <c:if test="${searchPageData.pagination.totalNumberOfResults != 0}">
        <div class="flex-direction-xs-row">
            <b><spring:theme code="sga.text.recommendation.sorting.sort.by" /></b>
            <a href="${sortByDateQuery}" class="${sortByDate}"><spring:theme code="sga.text.recommendation.page.sort.byDateAdded" /></a>&nbsp;|&nbsp;
            <a href="${sortByBrandNameAsc}" class="${sortByAsc}"><spring:theme code="sga.text.recommendation.page.sort.byNameAsc" /></a>&nbsp;|&nbsp;
            <a href="${sortByBrandNameDesc}" class="${sortByDesc}"><spring:theme code="sga.text.recommendation.page.sort.byNameDesc" /></a>
        </div>
    </c:if>
</div>
<c:choose>
    <c:when test="${searchPageData.pagination.totalNumberOfResults != 0}">
        <ul class="recommendations item__list">
            <li class="hidden-xs">
                <div class="row">
                    <ul class="col-md-12 col-sm-12 item__list--header item__list__cart ">
                        <div>
                            <div class="col-xs-4 col-sm-4 col-md-4"><li class="item__image"><spring:theme code="rep.recommendation.page.products"/></li></div>
                            <div class="col-xs-8 col-sm-4 col-md-4"><li class="text-nowrap"><spring:theme code="rep.recommendation.page.qty"/></li></div>
                            <div class="col-xs-8 col-sm-2 col-md-2"><li class="text-nowrap"><spring:theme code="rep.recommendation.page.recommendedBy"/></li></div>
                            <div class="col-xs-8 col-sm-2 col-md-2 pr-0">
                                <li id="remove-all-btn" class="text-nowrap text-underline cursor pull-right" onclick="ACC.recommendation.update(this, 'REMOVE_ALL', null, null)">
                                    <b><spring:theme code="basket.page.removeall"/></b>
                                </li>
                            </div>
                        </div>
                    </ul>
                </div>
            </li>

            <c:forEach items="${recommendationData}" var="recommendation" varStatus="loop">
                <c:set value="${loop.index}" var="itemIndex" />
                <c:choose>
                  <c:when test="${cmsSite.uid eq 'sga'}">
                    <c:set var="maxQty" value="999"/>
                    <c:set var="oninput" value='oninput="ACC.recommendation.onChange(this, ${itemIndex});"' />
                    <c:set var="onclick" value='onclick="ACC.recommendation.onChange(this, ${itemIndex});"' />
                  </c:when>
                  <c:otherwise>
                    <c:set var="maxQty" value="100"/>
                    <c:set var="oninput" value="" />
                    <c:set var="onclick" value="" />
                  </c:otherwise>
                </c:choose>

                <li class="item__list--item">
                    <c:url value="${request.contextPath}${recommendation.product.product.url}" var="productUrl" />
                    <div>
                        <!-- image, product name, code, promotions -->
                        <div class="col-xs-12 col-sm-4 col-md-4 display-flex no-padding-left">
                            <div class="item__image inline-block no-padding-left>
                                <a href="${productUrl}">
                                    <product:productPrimaryImage product="${recommendation.product.product}" format="thumbnail" />
                                </a>
                                <br/>
                                <div class="item__removeall visible-xs">
                                    <button id="id="mobile-remove-btn-${itemIndex}"" class="textButton remove-action pr-0" onclick="ACC.recommendation.update(this, 'REMOVE', ${fn:escapeXml(recommendation.product.product.code)}, ${itemIndex})">
                                        <spring:theme code="basket.page.entry.action.REMOVE" />
                                    </button>
                                </div>
                            </div>

                            <div class="row inline-block item__info">
                                <ycommerce:testId code="cart_product_">
                                   <a href="${productUrl}">
                                        <ycommerce:testId code="cart_product_name">
                                            <c:if test= "${cmsSite.uid eq 'sga'}">
                                                <div class = "sga-product-code">
                                                    ${fn:escapeXml(recommendation.product.product.code)}&nbsp;
                                                </div>
                                            </c:if>
                                            <span class="visible-xs" style="font-size: 12px;">Recommended by</span> <span class="visible-xs"><b>${fn:escapeXml(recommendation.recommendedBy)}</b></span>
                                            <span class="item__name"><b>${fn:escapeXml(recommendation.product.product.apbBrand.name)}</b> <span class="font-normal hidden-xs">${fn:escapeXml(recommendation.product.product.name)}</span></span>
                                        </ycommerce:testId>
                                    </a>

                                    <p class="item_cart_product">
                                        <a href="${productUrl}">
                                            <c:if test="${not empty recommendation.product.product.name}"><span class="visible-xs hidden-sm">${fn:escapeXml(recommendation.product.product.name)}</span></c:if>
                                            <c:if test="${not empty recommendation.product.product.unitVolume.name}"><span class="inline-block text-nowrap">${fn:escapeXml(recommendation.product.product.unitVolume.name)}</span></c:if> <br class="hidden-xs"/>
                                            <c:if test="${not empty recommendation.product.product.packageSize.name}"><span class="inline-block text-nowrap">${fn:escapeXml(recommendation.product.product.packageSize.name)}</span></c:if>
                                        </a>
                                    </p>
                                </ycommerce:testId>
                            </div>
                        </div>

                        <div class="clear visible-xs"></div>

                        <!-- Recommend quantity -->
                        <div class="col-xs-12 col-sm-4 col-md-4 recommend-quantity pl-md-5 ">
                            <c:set var="qtyMinus" value="1" />
                            <div class="addtocart-component mt-xs-0">
                                <div class="qty-selector input-group js-keg-qty-selector ml-0">
                                    <span class="input-group-btn" style>
                                        <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                                        <button
                                            class="btn btn-default js-qty-selector-minus disable-spinner"
                                            type="button"
                                            ${onclick}
                                            <c:if test="${recommendation.product.quantity == 0}">disabled="disabled"</c:if>>
                                            <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
                                        </button>
                                    </span>

                                    <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                                    <input
                                        type="text"
                                        maxlength="3"
                                        style="display:inline"
                                        class="form-control js-qty-selector-input"
                                        size="1"
                                        value="${recommendation.product.quantity}"
                                        original-val="0"
                                        data-max="${maxQty}"
                                        data-min="1"
                                        name="templateEntries-${itemIndex}"
                                        id="templateAddtoCartInput-${itemIndex}"
                                        ${oninput} />

                                    <span class="input-group-btn">
                                        <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                                        <button
                                            class="btn btn-default js-qty-selector-plus disable-spinner"
                                            ${onclick}
                                            style="display:inline"
                                            type="button">
                                            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                        </button>
                                    </span>

                                </div>
                                <c:if test="${cmsSite.uid eq 'sga'}">
                                    <div class="input-group block text-left ml-0">
                                        <button
                                            type="button"
                                            id="js-saved-recommendation-${itemIndex}"
                                            data-index="${itemIndex}"
                                            class="cursor px-0 textButton"
                                            style="font-size: 14px;"
                                            disabled="disabled"
                                            onclick="ACC.recommendation.update(this, 'UPDATE', ${fn:escapeXml(recommendation.product.product.code)}, ${itemIndex});">Save Quantity</button>
                                        <div class="save-cart-success success-${itemIndex} pt-0 hidden">Quantity Saved.</div>
                                        <div class="save-cart-success fail-${itemIndex} pt-0 hidden">Quantity Save failed.</div>
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <!-- Recommend by -->
                        <div class="col-sm-2 col-md-2 text-left no-padding-right hidden-xs">
                            <div class="addtocart-component">
                                ${fn:escapeXml(recommendation.recommendedBy)}
                            </div>
                        </div>

                        <!-- Action -->
                        <div class="col-sm-2 col-md-2 float-right no-padding-right hidden-xs">
                            <div class="addtocart-component text-right">
                                <button
                                    type="button"
                                    id="remove-btn-${itemIndex}"
                                    class="textButton remove-action pt-0 pr-0"
                                    onclick="ACC.recommendation.update(this, 'REMOVE', ${fn:escapeXml(recommendation.product.product.code)}, ${itemIndex})">
                                    <spring:theme code="basket.page.entry.action.REMOVE" />
                                </button>
                            </div>
                        </div>

                        <div class="cart-total-update visible-xs">
                            <ycommerce:testId code="cart_totalProductPrice_label">
                                <div id="template-price" class="item__total js-item-total pull-right">
                                    ${orderEntry.templateTotalPrice.formattedValue}
                                </div>
                            </ycommerce:testId>
                        </div>
                    </div> <!-- End of item -->
                    <div class="clear visible-xs"></div>
                </li>
            </c:forEach>
        </ul>
        <div class="clear"></div>

        <div class="account-orderhistory-pagination">
            <c:if test="${searchPageData.pagination.totalNumberOfResults > 0}">
                <div class="pagination-bar text-center">
                    <div class="pagination-toolbar">
                        <div class="sort-refine-bar">
                            <div class="row">
                                <div class="col-xs-12 col-sm-12 col-md-12 pagination-wrap">
                                    <pagination:pageRecommendationPagination
                                        searchUrl="${searchUrl}"
                                        searchPageData="${searchPageData}"
                                        numberPagesShown="${numberPagesShown}"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>
    </c:when>
    <c:otherwise>
        <div class="h3 text-center mt-35"><strong><spring:theme code="sga.text.recommendation.no.products" /></strong></div>
    </c:otherwise>
</c:choose>
     

