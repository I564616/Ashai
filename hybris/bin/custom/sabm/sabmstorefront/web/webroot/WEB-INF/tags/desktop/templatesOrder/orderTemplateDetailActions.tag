<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="templatesOrder" tagdir="/WEB-INF/tags/desktop/templatesOrder"%>
<%@ attribute name="cssClass" required="false" %>
<%@ attribute name="bottom" required="false" %>
<c:set var="templateOrdersUrl" value="../ordertemplates" />
<c:set var="imgPath" value="/_ui/desktop/SABMiller/img/" />

<div class="row template-actions">
	<div class="col-xs-12 col-sm-5 template-actions-btns text-right">
				<button class="btn btn-secondary save-template disabled"><spring:theme code="text.orderTemplateDetail.actions.save" /></button>
				<c:url value="/your-business/addTemplates/" var="addTemplatesUrl" />
				<input type="hidden" class="addToTemplate-hide" value="${addTemplatesUrl}">
				<button class="btn btn-secondary addToCartForTemplate"><spring:theme code="basket.add.to.basket" /></button>
	</div>
</div>


