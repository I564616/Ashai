<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="count" required="true" type="java.lang.String"%>
<%@ attribute name="productListName" required="false" type="java.lang.String"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="variant" required="false" type="de.hybris.platform.commercefacades.product.data.VariantOptionData"%>
<%@ attribute name="smartRecommendationModel" required="false" type="java.lang.String"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<c:url var="productUrl" value="${product.url}"/>
<c:set var="name" value="${product.name}"/>
<c:set var="packConfiguration" value="${product.packConfiguration}"/>
<c:set var="code" value="${product.code}"/>
<c:set var="stockLevelStatusCode" value="${product.stock.stockLevelStatus.code}"/>
<c:set var="purchasable" value="${product.purchasable}"/>
<c:set var="savings" value="${product.savingsPrice}"/>

<c:set var="priceCurrencyIso" value="${product.price.currencyIso}"/>
<c:set var="priceValue" value="${product.price.value}"/>
<c:set var="brand" value="${product.brand}"/>
<c:set var="categories" value="${product.categories}"/>
<c:set var="unit" value="${product.unit}"/>
<c:set var="dealsFlag" value="${product.dealsFlag}"/>
<c:set var="cubStockStatus" value="${product.cubStockStatus}"/>
<c:set value="${product.unit}" var="productUnit" />
<c:set value="${product.uomList}" var="uomList" />
<c:set var="requestOrigin" value="Home/ProductPage" />

<c:if test="${not empty product.uomList}">
    <c:set value="${product.uomList[0].name}" var="productUnit" />
</c:if>

<c:if test="${not empty variant}">
    <c:url var="productUrl" value="${variant.url}"/>
    <c:set var="name" value="${variant.name}"/>
    <c:set var="packConfiguration" value="${variant.packConfiguration}"/>
    <c:set var="code" value="${variant.code}"/>
    <c:set var="stockLevelStatusCode" value="${variant.stock.stockLevelStatus.code}"/>
    <c:set var="purchasable" value="${variant.purchasable}"/>
    <c:set var="savings" value="${variant.savingsPrice}"/>
    <c:set var="priceCurrencyIso" value="${variant.priceData.currencyIso}"/>
	<c:set var="priceValue" value="${variant.priceData.value}"/>

	<c:if test="${not empty variant.uomList}">
        <c:set value="${variant.uomList[0].name}" var="productUnit" />
    </c:if>

    <c:set value="${variant.uomList}" var="uomList" />

	<c:set var="cubStockStatus" value="${variant.cubStockStatus}"/>
	
	<%-- NOTE: variant does not have brand, categories, unit and dealsFlag
	<c:set var="brand" value="${variant.brand}"/>
	<c:set var="categories" value="${variant.categories}"/>
	<c:set var="unit" value="${variant.unit}"/>
	<c:set var="dealsFlag" value="${variant.dealsFlag}"/>  --%>
</c:if>

<product:productPackTypeAllowed unit="${productUnit}"/>

<div class="product-pick addToCartEventTag addtocart-qty productImpressionTag recommendation-highlight">
    <div class="product-image  <c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
        <a href="${productUrl}" class="js-track-product-link"
        	data-currencycode="${priceCurrencyIso}"
			data-name="${fn:escapeXml(name)}"
			data-id="${code}"
			data-price="${priceValue}"
			data-brand="${fn:escapeXml(brand)}"
			data-category=<c:choose>
                       		<c:when test="${not empty categories}">
                       			'${fn:escapeXml(categories[fn:length(categories) - 1].name)}'
                       		</c:when>
                       		<c:otherwise>
                       			''
                       		</c:otherwise>
                       </c:choose>
			data-variant="${unit}"
			data-position="${count}"
			data-url="${productUrl}"
			data-actionfield="${productListName}"
			data-list="${productListName}"
			data-dealsflag="${dealsFlag}">
            <h3 class="product-pick-title">${title}</h3>
			 <c:choose>
               <c:when test="${not empty variant}">
                   <product:variantOptionImage variantOptionData="${variant}" format="product"/>
               </c:when>
               <c:otherwise>
                   <product:productPrimaryImage product="${product}" format="product" fromPage="listing"/>
               </c:otherwise>
            </c:choose>
			</a>
    </div>
    <hr>
    <div class="product-info">
        <div class="<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if> product-info-content">
            <div class="product-pick-description">
                <a href="${productUrl}" class="js-track-product-link"
                    data-currencycode="${priceCurrencyIso}"
                    data-name="${fn:escapeXml(name)}"
                    data-id="${code}"
                    data-price="${priceValue}"
                    data-brand="${fn:escapeXml(brand)}"
                    data-category=<c:choose>
                                <c:when test="${not empty categories}">
                                    '${fn:escapeXml(categories[fn:length(categories) - 1].name)}'
                                </c:when>
                                <c:otherwise>
                                    ''
                                </c:otherwise>
                           </c:choose>
                    data-variant="${unit}"
                    data-position="${count}"
                    data-url="${productUrl}"
                    data-actionfield="${productListName}"
                    data-list="${productListName}"
                    data-dealsflag="${dealsFlag}"><h3 class="clamp-2">  ${name}</h3></a>
                <div class="h3 h3-subheader clamp-1">${empty packConfiguration ? '&nbsp;' : packConfiguration}</div>

            </div>
             <c:if test="${!isNAPGroup}">
            <div class="product-item-price">

                <div class="list-item-price">
                    <%-- <span>
                            <c:if test="${not empty product.uomList}">
                        <spring:theme code="text.product.your.param" arguments="${product.uomList[0].name }"/>
                    </c:if>
                    <c:if test="${empty product.uomList}">
                        <spring:theme code="text.product.your.param" arguments="${product.unit}"/>
                    </c:if>
                    </span> --%>
                    <c:choose>
                        <c:when test="${not empty variant}">
                            <div class="h1"><format:price priceData="${variant.priceData}"/></div>
                        </c:when>
                        <c:otherwise>

                        <div class="h1"><product:productListerItemPrice product="${product}" /></div>

                        </c:otherwise>
                    </c:choose>
                </div>
                <c:if test="${savings.value > 0}">
                    <div class="col-xs-5 trim-left-5" style="padding-right: 0 !important">
                        <div class="list-item-price text-right">
                        <!-- Savings -->
                                <span>
                                    <spring:theme code="product.price.save" />
                                </span>
                                <div class="list-item-saving">

                                        ${savings.formattedValue}

                                </div>
                        </div>
                    </div>
                </c:if>
                <product:productPriceInfo/>
            </div>
            <div class="hidden">
                <div class="col-xs-12" style="margin-top:-20px;">
                	<c:if test="${cubStockStatus == 'lowStock'}">
						<span class="low-stock-status-label"><spring:theme code="product.page.stockstatus.low"/></span>
					</c:if><br>
						<c:choose>
	                      <c:when test="${not empty product.maxOrderQuantity}">
	                        <span style="color:red;font-weight:700;"><spring:theme code="product.page.maxorderquantity" arguments="${product.maxOrderQuantityDays},${product.maxOrderQuantity}" /></span>
	                      </c:when>                     
	                      <c:otherwise>
	                        <span style="color:red;"><br/></span>
	                      </c:otherwise>
                    </c:choose>
                </div>
            </div>
            </c:if>
        </div>
    </div>
    <div class="qty-selectors">
        <div class="row product-pick-selectors <c:if test="${cubStockStatus eq 'outOfStock'}"> disabled-productOutofStock</c:if>">
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
                            <c:if test="${not empty uomList}">
                                <c:choose>
                                    <c:when test="${fn:length(uomList) eq 1}">
                                        <div class="select-single">${uomList[0].name}</div>
                                    </c:when>
                                    <c:otherwise>
                                        <div data-value="" class="select-btn"></div>
                                        <ul class="select-items dropdown-overflow">
                                            <c:forEach items="${uomList}" var="uom">
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
    <div class="add-to-cart">
        <c:if test="${!isNAPGroup}">
		<c:set var="buttonType">submit</c:set>
       	<c:url value="/cart/add" var="addToCartUrl" />
		<form:form id="cubPicksAddToCartForm${count}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
			<input type="hidden" name="productCodePost" value="${code}" />
			<input type="hidden" name="qty" class="qty" value="1">
			<input type="hidden" name="unit" class="addToCartUnit" value="">
			<input type="hidden" name="listOriginPos" value="${count}"/>
			<input type="hidden" name="smartRecommendationModel" value="${smartRecommendationModel}"/>
			<c:choose>
                <c:when test="${!isProductPackTypeAllowed}">
                    <div class="btn btn-primary btn-invert btn-block btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>
                </c:when>
                <c:otherwise>
			  	  <button type="${buttonType}" class="btn btn-primary btn-block bde-view-only
                    <c:if test="${stockLevelStatusCode eq 'outOfStock' || cubStockStatus eq 'outOfStock'}">out-of-stock</c:if>"
                    <c:if test="${ not purchasable || stockLevelStatusCode eq 'outOfStock' || cubStockStatus eq 'outOfStock'}"> disabled="disabled" aria-disabled="true"</c:if>>
                    <svg class="icon-cart-new">
                        <use xlink:href="#icon-cart-new"></use>    
                    </svg>  
                    <div>
                        <c:choose>
                        <c:when test="${cubStockStatus eq 'outOfStock'}">
                        <spring:theme code="pickup.out.of.stock"/>
                        </c:when>
                        <c:otherwise>
                        <spring:theme code="basket.add.to.basket"/>
                        </c:otherwise>
                        </c:choose>
                    </div>
				 </button>
			  </c:otherwise>
            </c:choose>
		</form:form>
        </c:if>
    </div>
</div>
