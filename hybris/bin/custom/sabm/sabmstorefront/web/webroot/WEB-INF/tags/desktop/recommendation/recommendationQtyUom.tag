<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="recommendProduct" required="true" type="com.sabmiller.facades.recommendation.data.RecommendationProductData"%>
<%@ attribute name="isInPackType" required="false" type="java.lang.Boolean"%>

<div class="item-qty clearfix">
<div class="<c:if test="${!isInPackType}"> disabled-productPackTypeNotAllowed</c:if>">
  <ul class="select-quantity select-quantity-fixed">
      <li class="down">
          <svg class="icon-minus">
              <use xlink:href="#icon-minus"></use>
          </svg>
      </li>
      <li>
       	 <input name="qtyInput" maxlength="3" size="1" class="qty-input qty-counter" type="text"
              value="${recommendProduct.quantity}" data-initQty="${recommendProduct.quantity}" data-minqty="${recommendProduct.minQuantity}" "${product.cubStockStatus == 'outOfStock' ? 'disabled' : ''}">
       </li>
      <li class="up">
          <svg class="icon-plus">
              <use xlink:href="#icon-plus"></use>
          </svg>
      </li>
  </ul>

  <div class="item-uom">
      <div class="select-list">
          <c:if test="${not empty product.uomList}">
              <c:choose>
                  <c:when test="${fn:length(product.uomList) eq 1}">
                      <div class="select-single recommendProductUom" data-initUom="${product.uomList[0].code}" data-value="${product.uomList[0].code}">${product.uomList[0].name}</div>
                  </c:when>
                  <c:otherwise>
                    <div class="select-btn sort recommendProductUom" data-initUom="${recommendProduct.unit.code}" data-value="${recommendProduct.unit.code}">${recommendProduct.unit.name}</div> 
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
<div class="recommendations-cta">
  <c:if test="${!bdeUser}">
    <input type="hidden" name="productCodePost" value="${product.code}" />
    <c:choose>
        <c:when test="${!isInPackType}">
            <div class="btn btn-primary btn-invert btn-block btn-changeDeliveryDate pull-right"><spring:theme code="basket.change.delivery.date"/></div>
        </c:when>
        <c:otherwise>
			<form action="" class="add_to_cart_form">
		       <button type="submit" id="recommendation-addToOrder-button-id" class="recommendation-addToOrder btn btn-primary pull-right"><spring:theme code="basket.add.to.basket"/></button>
		   </form>
        </c:otherwise>
    </c:choose>
    </c:if>
  <%--<div class="text-right"><span class="inline deleteRecommendationCus"><spring:theme code="text.iconCartRemove"/></span></div> --%>
  <div class="delete"><span class="inline deleteRecommendation deleteRecommendationCus" onclick="rm.tagManager.trackRecommendation('remove', 'RcmndRejectedPage | ${product.name}')"><spring:theme code="text.iconCartRemove"/></span></div>
</div>
