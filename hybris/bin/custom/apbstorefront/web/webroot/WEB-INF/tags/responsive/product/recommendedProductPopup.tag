<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<!-- Get close icon url-->
<spring:theme code="img.closeIcon" text="/" var="closeIconPath" />
<c:choose>
	<c:when test="${originalContextPath ne null}">
		<c:url value="${closeIconPath}" context="${originalContextPath}" var="closeIconUrl" />
	</c:when>
	<c:otherwise>
		<c:url value="${closeIconPath}" var="closeIconUrl" />
	</c:otherwise>
</c:choose>

<div id="recommendationPopupLayer" class="modal fade recommendation-popup-layer" role="dialog" data-backdrop="false">
    <div class="modal-dialog recommendation-popup-container">
        <div class="modal-content">      
            <div class="modal-body recommendation-popup">
                <div class="recommendation-popup-title">
                    <span><b><spring:theme code="sga.cart.recommendation.popup.title"/></b></span>
                </div>
                <div class="recommendation-popup-label">
                    <span><spring:theme code="sga.cart.recommendation.popup.subtitle"/></span>
                </div>
                <div class="recommendation-popup-items-container container">
                    <div class="wrapper">
                        <ul class="owl-carousel js-owl-carousel js-owl-recommendation-popup js-owl-carousel-reference">
                            
                            <c:forEach items="${productRecommendation}" var="reference">
                                <c:url value="${reference.url}" var="productUrl" /> 
                        
                                <div id="recommendationPopupItem" class="item recommendation-popup-item">
                                    <div class="thumb recommendation-popup-image">
                                        <a href="${productUrl}" class="js-reference-item" data-quickview-title="<spring:theme code="popup.quick.view.select"/>">
                                            <product:productPrimaryReferenceImage product="${reference}" format="product" />
                                        </a>
                                    </div>
                                    <a href="${productUrl}" class="js-reference-item" data-quickview-title="<spring:theme code="popup.quick.view.select"/>"> 
                                        <div class="item-name recommendation-popup-item-name"><b>${fn:escapeXml(reference.apbBrand.name)}</b> ${fn:escapeXml(reference.name)}</div>
                                        <div class="item-name recommendation-popup-item-unitvolume">${fn:escapeXml(reference.unitVolume.name)}</div>
                                        <div class="item-name recommendation-popup-item-packagesize">${fn:escapeXml(reference.packageSize.name)}</div>
                                    </a>
                                </div>                               
                            </c:forEach>
                        </ul>
                    </div>  
                </div>
                <div class="recommendation-popup-close" data-dismiss="modal">
                    <img src="${closeIconUrl}">
                </div>
            </div>
        </div>
    </div>
</div>