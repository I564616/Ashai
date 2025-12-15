<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<div class="slider-nav-wrap pull-right">
	<ul class="slider-nav">
		<li class="slider-prev">
		    <svg class="icon-arrow-left">
		        <use xlink:href="#icon-arrow-left"></use>    
		    </svg>
		</li>
		<li class="slider-next">
		    <svg class="icon-arrow-right">
		        <use xlink:href="#icon-arrow-right"></use>    
		    </svg>
		</li>
	</ul>
</div>

<div class="">
<div class="row product-pick offset-bottom-none">
        <c:choose>
            <c:when
                test="${not empty productReferences and component.maximumNumberProducts > 0}">
                <div class="offset-left-small">
                    <div class="slick-slider clearfix">
                        <c:forEach end="${component.maximumNumberProducts}"
                            items="${productReferences}" var="productReference" varStatus="status">
                            <c:set value="${productReference.target}" var="product"/>
                            <c:url value="${product.url}" var="productDetailPageUrl" />
                            <c:set value="${product.unit}" var="productUnit" />

                            <c:if test="${not empty product.uomList}">
                                <c:set value="${product.uomList[0].name}" var="productUnit" />
                            </c:if>

                            <product:productPackTypeAllowed unit="${productUnit}"/>

                            <div class="col-xs-12 col-sm-6 col-md-3 addtocart-qty slider-height">
                                <div class="<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
                                    <a href="${productDetailPageUrl}" class="popup scrollerProduct">
                                        <div>
                                            <product:productPrimaryImage product="${product}" format="product"/>
                                        </div>

                                        <c:if test="${component.displayProductTitles}">
                                            <ycommerce:testId code="productDetails_productNamePrice_label_${product.code}">
                                            <div class="product-pick-description">
                                                <h3 class="clamp-2 text-margin-right trim-left-important">${product.name}</h3>
                                                <div class="h3 h3-subheader clamp-1 text-margin-right trim-left-important">${product.packConfiguration}</div>
                                            </div>
                                            </ycommerce:testId>
                                        </c:if>
                                    </a>
                                    <c:if test="${!isNAPGroup}">
                                    <div class="product-item-price">
                                        <div class="col-xs-7 trim-right-5 trim-left-important">
                                            <div class="list-item-price">
                                                <span>
                                                    <spring:theme code="text.product.your.param" arguments="${productUnit}"/>
                                                </span>
                                                <div class="h1"><product:productListerItemPrice product="${product}" /></div>
                                            </div>
                                        </div>
                                        <!--Saving-->
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
                                                        <c:if test="${product.cubStockStatus.code == 'lowStock'}">
														<span class="low-stock-status-label"><spring:theme code="product.page.stockstatus.low"/></span>
													</c:if> 
                                                    </div>
                                                  </div>                               
                                            <div class="row">
                                                <div class="col-xs-12">
                                                    <product:productPriceInfo/>
                                                </div>
                                            </div>
                                    </div>
                                   <div class="row product-pick-selectors">
                                        <div class="col-xs-6 trim-right-5">
                                            <ul class="select-quantity">
                                                <li class="down disabled">
                                                    <svg class="icon-minus">
                                                        <use xlink:href="#icon-minus"></use>
                                                    </svg>
                                                </li>
                                                <li><input name="qtyInput" maxlength="3" size="1" class="qty-input min-1" type="tel" value="1" data-minqty="1" pattern="\d*"></li>
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
                                                                <div data-value="" class="select-btn"></div>
                                                                <ul class="select-items dropdown-overflow">
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
                                    </c:if>
                                </div>
                                <c:if test="${!isNAPGroup}">
                                <c:set var="buttonType">submit</c:set>
                                <c:url value="/cart/add" var="addToCartUrl" />
                                <form:form id="cubPicksAddToCartForm${status.count}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
                                    <input type="hidden" name="productCodePost" value="${product.code}" />
                                    <input type="hidden" name="qty" class="qty" value="1">
                                    <input type="hidden" name="unit" class="addToCartUnit" value="">

                                    <c:choose>
                                        <c:when test="${!isProductPackTypeAllowed}">
                                            <div class="btn btn-primary btn-invert btn-block btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>
                                        </c:when>
                                        <c:otherwise>
                                    <button type="${buttonType}" class="btn btn-primary btn-block bde-view-only <c:if test="
                                    ${not product.purchasable || product.stock.stockLevelStatus.code eq 'outOfStock' || product.cubStockStatus.code eq 'outOfStock'}">out-of-stock</c:if>"
                                        <c:if test="${not product.purchasable || product.stock.stockLevelStatus.code eq 'outOfStock' || product.cubStockStatus.code eq 'outOfStock'}"> disabled="disabled" aria-disabled="true"</c:if>>
                                        <c:choose>
                                            <c:when test="${product.cubStockStatus.code eq 'outOfStock'}">
                                                <spring:theme code="pickup.out.of.stock"/>
                                            </c:when>
                                            <c:otherwise>
                                                <spring:theme code="basket.add.to.basket"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </button>
                                        </c:otherwise>
                                    </c:choose>

                                </form:form>
                                </c:if>
                            </div>

                        </c:forEach>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <component:emptyComponent />
            </c:otherwise>
        </c:choose>
</div>
</div>
