<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="recommendation" tagdir="/WEB-INF/tags/desktop/recommendation" %>
<%@ attribute name="recommendProduct" required="true" type="com.sabmiller.facades.recommendation.data.RecommendationProductData"%>
<%@ attribute name="recommendedBy" required="true" type="java.lang.String"%>
<%@ attribute name="productListPosition" required="false" type="java.lang.Integer"%>
<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="isInPackType" required="false" type="java.lang.Boolean"%>

<c:set var="product" value="${recommendProduct.product}" />

  <div class="deal no-deal addToCartEventTag <c:choose>
	                  <c:when test="${bdeUser}">recommendationRepRow</c:when>
	                  <c:otherwise>recommendationCusRow</c:otherwise>
	                </c:choose>">
    
    <div class="row deal-item-head" >
      <div class="col-xs-12">
        <div class="deal-head-left productImpressionTag">
          <span class="recommendation-name">
            <spring:theme code="text.recommendations.recommendedBy"/>&nbsp;${recommendedBy}
          </span>
          <div class="<c:if test="${!isInPackType}"> disabled-productPackTypeNotAllowed</c:if>">
          <%-- TODO remove deal.code after IPT testing --%>
          <div class="deal-img"><recommendation:recommendationImage product="${product}" productListPosition="${productListPosition}"/></div>

          <div class="deal-title">
            <h2 class="h4" >
              <c:choose>
                  <c:when test="${not empty title}">
                      ${title}
                  </c:when>
                  <c:otherwise>
                      ${product.name}
                      <span>&nbsp;${product.packConfiguration}</span>
                  </c:otherwise>
                </c:choose>
            </h2>
          </div>
          <c:if test="${product.cubStockStatus == 'lowStock'}">
          <div class="low-stock-status-label">
          	<spring:theme code="product.page.stockstatus.low"></spring:theme>
          </div>
          </c:if>
        
          </div>
        </div>

        <div class="deal-head-right">
            <recommendation:recommendationQtyUom product="${product}" recommendProduct="${recommendProduct}" isInPackType="${isInPackType}"/>
        </div>
      </div>
    </div>

    <hr class="hr-1">
  </div>
