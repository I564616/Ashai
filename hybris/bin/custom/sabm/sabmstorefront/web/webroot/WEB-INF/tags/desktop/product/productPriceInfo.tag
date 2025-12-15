<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<c:set var="imgPath" value="/_ui/desktop/SABMiller/img/" />
<c:if test="${!isNAPGroup}">
<div>
   <span class="price-rule-text">
       <c:choose>
           <c:when test="${user.currentB2BUnit.isDepositApplicable eq true}">
               <spring:theme code="text.product.price.excludes.taxes.deposits" />
           </c:when>
           <c:otherwise>
               <spring:theme code="text.product.price.excludes.taxes" />
           </c:otherwise>
       </c:choose>
   </span>
    <span class="product-item-price magnific-price">
        <a href="#price-conditions" class="regular-popup">
                <svg class="icon-price-info">
				    <use xlink:href="#icon-price-info"></use>
			    </svg>
        </a>
    </span>
</div>

<product:productPricePopup />
</c:if>