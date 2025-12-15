<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="galleryImages" required="true" type="java.util.List" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<spring:theme code="text.addToCart" var="addToCartText"/>

<c:set var="requestOrigin" value="Home/ProductPage" />

<div class="row no-border addToCartEventTag" data-cupdealsRefreshInProgress="${cupdealsRefreshInProgress}" id="product-detail-panel">

    <c:set value="${product.unit}" var="productUnit" />
    <c:if test="${not empty product.uomList}">
        <c:set value="${product.uomList[0].name}" var="productUnit" />
    </c:if>
    <product:productPackTypeAllowed unit="${productUnit}"/>

	<div class="col-sm-12 visible-xs-block product-summary <c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
	    <ycommerce:testId code="productDetails_productNamePrice_label_${product.code}">
  			<h1>${product.name}</h1>
  			<div class="h1 h1-subheader">${product.packConfiguration}</div>
	    </ycommerce:testId>
	    <div class="h1 h1-subheader">${product.summary}</div>
	</div>

    <div class="<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
	    <product:productImagePanel product="${product}" galleryImages="${galleryImages}"/>
	</div>

	<div class="col-sm-6 col-md-offset-2 product-summary addtocart-qty">
	    <div class="<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
            <div class="visible-sm-block visible-md-block visible-lg-block">
                <ycommerce:testId code="productDetails_productNamePrice_label_${product.code}">
                    <h1>${product.name}</h1>
                    <div class="h1 h1-subheader">${product.packConfiguration}</div>
                </ycommerce:testId>
                <div class="h1 h1-subheader">${product.summary}</div>
            </div>

            <ycommerce:testId code="productDetails_productNamePrice_label_${product.code}">
                <product:productPricePanel product="${product}"/>
            </ycommerce:testId>

            <div class="col-md-12 col-md-offset-6 offset-bottom-xsmall" ${user.currentB2BUnit.isDepositApplicable eq true ? 'style="margin-left: 53%"' : 'style="margin-left: 68%"'}>
                <product:productPriceInfo/>
            </div>
            <!-- <product:productPromotionSection product="${product}"/> -->

    <!-- 		<cms:pageSlot position="VariantSelector" var="component" element="div">
                <cms:component component="${component}"/>
            </cms:pageSlot> -->
            <input type="hidden" id="hiddenOrderTemplatesNo" value="${fn:length(orderTemplates)}" />
		</div>
		
		<cms:pageSlot position="AddToCart" var="component" element="div">
			<cms:component component="${component}"/>
		</cms:pageSlot>
	</div>

	<cms:pageSlot position="Section2" var="feature" element="div" class="span-8 section2 cms_disp-img_slot last">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

</div>

<product:productPricePopup />
