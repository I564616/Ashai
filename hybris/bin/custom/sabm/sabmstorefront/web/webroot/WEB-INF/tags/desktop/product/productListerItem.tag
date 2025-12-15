<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="productListPosition" required="false" type="java.lang.Integer"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:url value="${product.url}" var="productUrl" />

<c:set value="${product.unit}" var="productUnit" />

<c:if test="${not empty product.uomList}">
    <c:set value="${product.uomList[0].name}" var="productUnit" />
</c:if>

<product:productPackTypeAllowed unit="${productUnit}"/>

<div class="col-sm-4 list-item addtocart-qty productImpressionTag">
	<ycommerce:testId code="test_searchPage_wholeProduct">
	    <div class="<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
            <a href="${productUrl}" title="${product.name}" class="productMainLink js-track-product-link"
                    data-currencycode="${product.price.currencyIso}"
                    data-name="${fn:escapeXml(product.name)}"
                    data-id="${product.code}"
                    data-price="${product.price.value}"
                    data-brand="${fn:escapeXml(product.brand)}"
                    data-category="${fn:escapeXml(categoryData.name)}"
                    data-variant=<c:choose>
								<c:when test="${empty product.uomList}">
					 				"${product.unit}"
					 			</c:when>
					 			<c:otherwise>
					 				"${product.uomList[0].name}"
					 			</c:otherwise>
						   	  </c:choose>
                    data-position="${productListPosition}"
                    data-url="${product.url}"
                    data-actionfield="${fn:escapeXml(requestOrigin)}"
                    data-list="${fn:escapeXml(requestOrigin)}"
                    data-dealsflag="${product.dealsFlag}">
                <div class="thumb">
                    <product:productPrimaryImage product="${product}" format="product" fromPage="listing"/>
                </div>
                <div class="list-item-title">
                    <ycommerce:testId code="searchPage_productName_link_${product.code}">
                        <h3 class="clamp-2">${product.name}</h3>
                        <div class="h3 h3-subheader clamp-1">${product.packConfiguration}</div>
                    </ycommerce:testId>
                </div>
            </a>
            <c:if test="${!isNAPGroup}">
            <ycommerce:testId code="searchPage_price_label_${product.code}">
            <div class="row">
                <div class="col-xs-7 trim-right-5">

                    <div class="list-item-price">
                        <span>
                     		<c:if test="${not empty product.uomList}">
							<spring:theme code="text.product.your.param" arguments="${product.uomList[0].name }"/>
						</c:if>
						<c:if test="${empty product.uomList}">
							<spring:theme code="text.product.your.param" arguments="${product.unit}"/>
						</c:if>
				        </span>
                        <div class="h1">
                            <product:productListerItemPrice product="${product}" />
                        </div>
                    </div>
                </div>
                <div class="col-xs-5 trim-left-5">
                    <div class="list-item-price text-right">
                    <!-- Savings -->
                        <c:if test="${product.savingsPrice.value > 0}">
                            <span>
                                <spring:theme code="product.price.save" />
                            </span>
                            <div class="list-item-saving">

                                    ${product.savingsPrice.formattedValue}

                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
            </ycommerce:testId>
            </c:if>
            <c:if test="${!isNAPGroup}">
            <div class="row">
                <div class="col-xs-12" style="margin-top:-20px;">
                		<!-- Low Stock Flag -->
						<c:if test="${product.cubStockStatus == 'lowStock'}">
							<span class="low-stock-status-label"><spring:theme code="product.page.stockstatus.low"/></span>
						</c:if><br>
                
                	<c:choose>
	                      <c:when test="${not empty product.maxOrderQuantity}">
	                        <span style="color:red; font-weight:700; font-size: 14px;"><spring:theme code="product.page.maxorderquantity" arguments="${product.maxOrderQuantityDays},${product.maxOrderQuantity}" /></span>
	                      </c:when>                     
	                      <c:otherwise>
	                        <span style="color:red;"><br/></span>
	                      </c:otherwise>
                    </c:choose>
                
                    <product:productPriceInfo/>
                </div>
            </div>
            </c:if>
        </div>
        <c:if test="${!isNAPGroup}">
        <div class="<c:if test="${!isProductPackTypeAllowed && !bdeUser}"> disabled-productPackTypeNotAllowed</c:if><c:if test="${product.cubStockStatus == 'outOfStock' && !bdeUser}"> disabled-productOutofStock</c:if>">
            <div class="row list-qty">
                <div class="col-xs-6 trim-right-5">
                    <ul class="select-quantity clearfix">
                        <li class="down">
                            <svg class="icon-minus">
                                <use xlink:href="#icon-minus"></use>
                            </svg>
                        </li>
                        <li><input class="qty-input min-1" type="tel" value="1" data-minqty="1" maxlength="3"  pattern="\d*" ></li>
                        <li class="up">
                            <svg class="icon-plus">
                                <use xlink:href="#icon-plus"></use>
                            </svg>
                        </li>
                    </ul>
                </div>
                <div class="col-xs-6 trim-left-5">
                    <div class="select-list">
                        <c:if test="${not empty product.uomList}">
                            <c:choose>
                                <c:when test="${fn:length(product.uomList) eq 1}">
                                    <div class="select-single">${product.uomList[0].name}</div>
                                </c:when>
                                <c:otherwise>
                                    <div class="select-btn"></div>
                                    <ul class="select-items">
                                    <c:forEach items="${product.uomList}" var="uom">
                                        <li data-value="${uom.code}">${uom.name}</li>
                                    </c:forEach>
                                    </ul>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
        </c:if>
        <c:set var="product" value="${product}" scope="request" />
        <c:set var="addToCartText" value="${addToCartText}" scope="request" />
        <c:set var="addToCartUrl" value="${addToCartUrl}" scope="request" />
        <c:set var="productListPosition" value="${productListPosition}" scope="request" />
        <div class="listAddPickupContainer clearfix actions-container-for-${component.uid}">
            <action:actions element="div" parentComponent="${component}" />
        </div>
	</ycommerce:testId>
</div>
