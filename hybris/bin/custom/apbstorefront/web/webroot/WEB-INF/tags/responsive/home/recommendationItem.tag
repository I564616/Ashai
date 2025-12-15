<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="title" required="true" type="String" %>
<%@ attribute name="smartRecommendationModel" required="true" type="String" %>

<c:set value="${request.contextPath}" var="context"/>
<c:set var="isModel3" value="false"/>
<c:if test="${smartRecommendationModel eq 'MODEL3'}"><c:set var="isModel3" value="true"/></c:if>

<!--<div class="product-item ${isModel3 ? 'blue' : ''}">-->
    <div class="row">
        <div class="col-xs-12 title title-${isModel3 ? 'blue' : ''}">
            <c:if test="${isModel3}" >
                <img class="title-star" src="${commonResourcePath}/images/Star_White.svg" />
            </c:if>
            <span>${title}</span>
        </div>
    </div>
    <div class="row aligned-row">
        <div class="col-xs-12">
            <a class="thumbnail-img" href="${context}${product.url}" title="${product.name}">
                <c:choose>
                   <c:when test="${fn:length(product.images) == 0}">
                        <theme:image code="img.missingProductImage.responsive.product" title="${product.name}" alt="${product.name}" />
                   </c:when>
                   <c:otherwise>
                        <c:forEach items="${product.images}" var="img">
                            <c:if test="${img.format == 'product'}" >
                                <c:if test="${cmsSite.uid eq 'sga'}" >
                                    <c:if test="${product.newProduct}" >
                                        <div class="new-product-container">
                                            <div class="new-product"><spring:theme code="sga.product.new.identifier"/></div>
                                        </div>
                                    </c:if>

                                    <c:if test="${product.isPromotionActive}" >
                                        <span class="plp-promotion-img"><spring:theme code="sga.product.promotion.image.text"/></span>
                                    </c:if>
                                </c:if>
                                <img class="primaryImage" id="primaryImage" src="${img.url}" title="${product.name}" alt="${product.name}"/>
                            </c:if>
                        </c:forEach>
                   </c:otherwise>
                </c:choose>
            </a>
        </div>
        <div class="col-xs-12 no-padding">
            <div class="details">
                <div class="product-details">
                    <a class="product-name" href="${context}${product.url}">
                        <div id="plpProductCode" class="sga-product-code"><!--${product.code}--></div>
                        <span class="product-brand" title="${product.apbBrand.name} ${product.name}">
                            <b>${product.apbBrand.name}</b>
                            ${product.name}
                        </span>
                        <p>${product.unitVolume.name}</p>
                        <p>${product.packageSize.name}</p>

                    </a>
                </div>

                <!--Only show price to logged in users -->
                <c:if test="${!isNAPGroup}">
                <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
                    <div class="price-details">
                        <div>
                            <c:if test="${product.stock.stockLevelStatus.code == 'lowStock'}" >
                                <span class="pull-left plp_low_stock_status_name">
                                    ${product.stock.stockLevelStatusName}
                                </span>
                            </c:if>

                            <c:if test="${product.stock.stockLevelStatus.code == 'outOfStock'}" >
                                <span class="pull-left plp_no_stock_status_name">
                                    ${product.stock.stockLevelStatusName}
                                </span>
                            </c:if>
                        </div>
                        <span class="price_strike" id="price_ls_${product.code}"></span>
                        <span class="price" id="price_ns_${product.code}">${product.price.formattedValue}</span>
                        <div class="clearfix"></div>
                    </div>
                </sec:authorize>
                </c:if>
            </div>
        </div>
    </div>
    <div class="row clearfix">
        <div class="col-xs-12 col-sm-12">
            <div class="addtocart">
                <div class="addtocart-component">
                    
                    <c:set var="isForceInStock" value="${entry.product.stock.stockLevelStatus.code eq 'inStock' and empty entry.product.stock.stockLevel}"/>
                    <c:choose> 
                        <c:when test="${isForceInStock}">
                            <c:set var="maxQty" value="FORCE_IN_STOCK"/>
                        </c:when>
                        <c:otherwise>	
                            <c:choose>
                                <c:when test="${not empty product.maxQty}">
                                    <c:set var="maxQty" value="${product.maxQty}"/>
                                </c:when>
                                <c:when test="${not empty defaultMaxQuantity}">
                                    <c:set var="maxQty" value="${defaultMaxQuantity}"/>
                                </c:when>
                                <c:when test="${not empty product.stock.stockLevel}" >
                                    <c:set var="maxQty" value="${product.stock.stockLevel}"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="maxQty" value="FORCE_IN_STOCK"/>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise> 	
                    </c:choose>

                    <c:set var="qtyMinus" value="1"/>
                    <c:if test="${not empty product.recommendedQuantity}" >
                        <c:set var="qtyMinus" scope="page" value="${product.recommendedQuantity}"/>
                    </c:if>
                     <c:if test="${!isNAPGroup}">
                    <div>
                        <c:url value="/cart/add" var="addToCartUrl"/>
                        <form id="addRecommendationToCartForm" action="${addToCartUrl}" method="post" class="add_recommendation_to_cart_form addRecommendationToToCartForm${product.code}">
                            <c:if test="${empty showAddToCart ? true : showAddToCart}" >
                                <div class="container">
                                    
                                    <div class="qty-selector input-group js-qty-selector">
                                        <span class="input-group-btn" style="">
                                            <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                                            <button class="btn btn-default js-qty-selector-minus" style="display:inline" type="button" disabled="disabled">
                                                <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
                                            </button>
                                        </span>
                                        <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                                        <input type="text" maxlength="3" style="display:inline" class="form-control js-qty-selector-input" size="1" value="${qtyMinus}" data-max="${maxQty}" data-min="1" name="pdpAddtoCartInput" id="pdpAddtoCartInput" <c:if test="${qtyMinus eq product.recommendedQuantity}" >data-value="${qtyMinus}"</c:if> <c:if test="${product.stock.stockLevelStatus.code == 'outOfStock'}">disabled</c:if> >
                                        <span
                                            class="input-group-btn">
                                            <!--  Removed the style size tag from the button declaration, since style is to be set through CSS. -->
                                            <button class="btn btn-default js-qty-selector-plus" style="display:inline" type="button" <c:if test="${product.stock.stockLevelStatus.code == 'outOfStock'}">disabled</c:if> >
                                                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                            </button>
                                        </span>
                                        <input type="hidden" name="qty" value="${qtyMinus}" id="qty" path="quantity" class="qty js-qty-selector-input">

                                        <!--  Disabled add to cart component if liquour license is not available -->
                                        <input type="hidden" name="productLiquor" value="${product.licenseRequired ? 'true' : 'false'}"/>
                                        <input type="hidden" name="productCodePost" value="${product.code}" id="addProductCode">
                                        <input type="hidden" name="productNamePost" value="${product.name}">
                                        <input type="hidden" name="productPostPrice" value="${product.basePrice.value}">
                                        <input type="hidden" name="${CSRFToken.parameterName}" value="${CSRFToken.token}">
                                        
                                        <c:if test="${accesstype ne 'PAY_ONLY' && !isNAPGroup}">
                                            <button type="submit" id="add-to-cart-button" class="btn btn-primary btn-block list-add-to-cart addToCartButtonPLP addToCartButton addToCartButtonRecommendationItem" <c:if test="${product.stock.stockLevelStatus.code == 'outOfStock'}"> aria-disabled="true" disabled </c:if> >
                                                <img class="add-to-cart-icon" src="/storefront/_ui/responsive/common/images/white_cart.svg"/>
                                            </button>
                                        </c:if>

                                    </div>
                                </div>
                            </c:if>
                        </form>
                    </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
<!--</div>-->