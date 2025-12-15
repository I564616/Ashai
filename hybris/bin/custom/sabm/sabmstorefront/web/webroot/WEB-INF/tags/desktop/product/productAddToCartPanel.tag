<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="allowAddToCart" required="true" type="java.lang.Boolean" %>
<%@ attribute name="isMain" required="true" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<div class="row quantity">
	<c:if test="${product.purchasable}">
	<div class="col-md-6 col-sm-12 offset-bottom-xsmall">
		<div class="row">
			<div class="col-md-7 magnific-price"><a id="priceConditionsTag" href="#price-conditions" class="inline regular-popup"><spring:theme code="text.product.price.conditions"/></a></div>
			<div class="col-md-5"><div class="quantity-label"><spring:theme code="basket.page.quantity"/></div></div>
		</div>
	</div>
	<div class="col-md-6 col-sm-12 summary-value offset-bottom-medium">
		<div class="row">
			<div class="col-xs-6 trim-right-5">
				<ul class="select-quantity">
					<li class="down">
						<svg class="icon-minus">
						    <use xlink:href="#icon-minus"></use>    
						</svg>
					</li>
					<li><input name="qtyInput" maxlength="3" size="1" class="qty-input min-1" type="tel" value="1" data-minqty="1"  pattern="\d*"></li>
					<li class="up">
						<svg class="icon-plus">
						    <use xlink:href="#icon-plus"></use>    
						</svg>
					</li>
				</ul>
			</div>
			<c:if test="${not empty product.uomList}">
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
			</c:if>
		</div>
	</div>
	</c:if>
</div>


	<div id="actions-container-for-${component.uid}">
		<c:if test="${multiDimensionalProduct}" >
			<sec:authorize access="hasAnyRole('ROLE_CUSTOMERGROUP')">
			<c:url value="${product.url}/orderForm" var="productOrderFormUrl"/>
			<!-- DEV TO-DO: What is this? and Remove Share -->
			<a href="${productOrderFormUrl}" id="productOrderButton" ><spring:theme code="order.form" /></a>
			</sec:authorize>
		</c:if>
		<action:actions element="div" parentComponent="${component}"/>
	</div>

