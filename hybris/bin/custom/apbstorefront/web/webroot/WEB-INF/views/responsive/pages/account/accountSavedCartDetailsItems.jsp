<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<ul class="item__list" id="top-margin-template">
    <li class="hidden-xs">
        <div class="row">
            <ul class="col-md-12 col-sm-12 item__list--header item__list__cart ">

                <div class="remove-all-template">
                    <div class="col-sm-1 image-tablet-fix col-md-1"><li class="item__image"><spring:theme code="basket.page.item"/></li></div>
                    <div class="col-sm-3 col-md-4 tablet-label-fix"><li class="item__info"></li></div>
                    <div class="col-sm-2 col-md-3 template-desktop-fix-price template-price-tablet-fix no-wrap"><li class="item__price"><spring:theme code="basket.page.price"/></li></div>
                    <div class="col-sm-3 col-md-3 no-padding-desktop-left template-quantity-tablet-fix"><li class="item__quantity"><spring:theme code="basket.page.qty"/></li></div>
					<div class="col-sm-1 col-md-2"></div>
                    <!--<div class="col-sm-1 col-md-2 template-desktop-fix-total no-padding"><li class="item__total"><spring:theme code="basket.page.total"/></li></div>-->
                </div>
            </ul>
        </div>
    </li>
    
	<form:form action="${request.contextPath}/my-account/saved-carts/reorderEntries/updateEntries" class="reorderForm" id ="orderTemplateForm${savedCartData.code}" data-savedcart-id="${fn:escapeXml(savedCartData.code)}" modelAttribute="orderTemplateReorderForm" method="POST">
	<ycommerce:testId code="savedCartDetails_itemBody_section">
        <div class="no-margin-template storefront_table">
		<c:forEach items="${savedCartData.templateEntry}" var="entry" varStatus="loop">
			<spring:url value="/my-account/saved-carts/{/cartCode}/getReadOnlyProductVariantMatrix" var="targetUrl" htmlEscape="false">
				<spring:param name="cartCode" value="${savedCartData.code}"/>
			</spring:url>
			<order:orderTemplateEntryDetails orderEntry="${entry}" order="${savedCartData}" itemIndex="${loop.index}" targetUrl="${targetUrl}"/>
		</c:forEach>
        </div>
	</ycommerce:testId>
	<input type="hidden" name="templateCode" value="${savedCartData.code}" />
	<input id="keepCart" type="hidden" value="" name="keepCart">
	</form:form>
</ul>
