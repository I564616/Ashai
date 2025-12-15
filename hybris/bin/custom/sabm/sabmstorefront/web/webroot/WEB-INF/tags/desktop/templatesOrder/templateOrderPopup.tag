<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="templateCode" required="false" %>
<%@ attribute name="pageName" required="false" %>

<div id="templateSuccess" class="hide"><div class="alert positive"><spring:theme code="create.order.template.success"/></div></div>
<div id="templateError" class="hide"><div class="alert negative"><spring:theme code="create.order.template.error"/></div></div>
<div class="price-popup template-order-popup mfp-hide" id="save-as-template">
    <h2 class="h1"><spring:theme code="cart.page.template.order.popup.title"/></h2>
    <p><spring:theme code="cart.page.template.order.popup.description"/></p>
    <c:url value="/cart/saveOrderTemplate" var="saveTemplateUrl"/>
    <form action="${saveTemplateUrl}" method="post" id="cartSaveTemplateForm">
         <div class="form-group">
           <label for="template-name"><spring:theme code="cart.page.template.order.popup.name"/></label>
           <input type="text" class="form-control" id="template-name" name="orderName" maxlength="255"  required/>
           <span class="error hidden" id="empty-msg"><spring:theme code="cart.page.template.name.epmty" /></span>
           <input type="hidden" name="CSRFToken" value="${CSRFToken}">
           <button type="submit" class="saveTemplateBtn btn btn-primary"><spring:theme code="cart.page.save.as.template"/></button><br>
           <a href="#" class="inline" id="magnific-close"><spring:theme code="cart.page.template.order.popup.cancel" /></a>
         </div>
    </form>
</div>

<div class="price-popup save-to-template mfp-hide" id="save-to-template">
    <h2 class="h1"><spring:theme code="text.pdp.template.order.popup.title"/></h2>
    <div class="templates-order-table">
		<c:url value="/cart/addProductToTemplate" var="addProductTemplate"/>
        <div class="scroller">
            <c:forEach items="${orderTemplates}" var="orderTemplate">
                <c:set value="/your-business/orderTemplateDetail/${orderTemplate.code}" var="urlOrderTemplate"/>
                <div class="row table-row">
                    <div class="col-xs-12 col-sm-6"><a class="title offset-bottom-none" href="${urlOrderTemplate}">${orderTemplate.name}</a></div>
                    <div class="col-xs-12 col-sm-6 text-right template-cta">
                        <form action="${addProductTemplate}" method="post">
                            <input type="hidden" name="orderCode" value="${orderTemplate.code}"/>
                            <input type="hidden" name="productCode" value="${product.code}" />
                            <input type="hidden" id="qty" name="quantity" class="qty" value="1">
                            <input type="hidden" name="fromUnit" class="addToCartUnit">
						<input type="hidden" name="CSRFToken" value="${CSRFToken}">
                            <button type="submit" class="btn btn-primary"><spring:theme code="text.pdp.template.order.popup.add"/></button>
                        </form>
                    </div>
                </div>
                <hr class="hr-1">
            </c:forEach>
        </div>
    </div>
    <div class="form-group">
        <label for="template-name"><spring:theme code="text.pdp.template.order.popup.new.template.title"/></label>
        <div class="row">
        		<c:url value="/cart/saveProductToNewTemplate" var="saveProductToNewTemplate"/>
        <form action="${saveProductToNewTemplate}" method="post">
            <div class="col-sm-8 offset-bottom-xsmall margin-top-3">
                <input type="text" class="form-control" id="template-name"  name="orderName" maxlength="255"  />
                 <input type="hidden" name="productCode" value="${product.code}" />
                 <input type="hidden" name="CSRFToken" value="${CSRFToken}">
                 <input type="hidden" id="qty" name="quantity" class="qty" value="1">
                <input type="hidden" name="fromUnit" class="addToCartUnit">
                <span class="error hidden" id="empty-msg"><spring:theme code="cart.page.template.name.epmty" /></span>
            </div>
            <div class="col-sm-4 text-right">
                <button type="submit" class="createTemplateBtn btn btn-primary"><spring:theme code="text.pdp.template.order.popup.button"/></button>
            </div>
            </form>
        </div>

    </div>
</div>

<div class="price-popup template-order-popup mfp-hide" id="create-new-template">
    <h2 class="h1"><spring:theme code="popup.order.template.title"/></h2>
    <p><spring:theme code="popup.order.template.description"/></p>
    <c:url value="/your-business/createOrderTemplate" var="createOrderTemplateUrl"/>
    <form action="${createOrderTemplateUrl}" method="post" id="createNewTemplateForm">
        <div class="form-group">
            <label for="template-name"><spring:theme code="cart.page.template.order.popup.name"/></label>
            <input type="text" class="form-control" id="create-template-name"  name="templateName" maxlength="255"  required/>
            <input type="hidden" name="pageName" value="${pageName}">
            <span class="error hidden" id="create-empty-msg"><spring:theme code="cart.page.template.name.epmty" /></span>
            <input type="hidden" name="CSRFToken" value="${CSRFToken}">
            <button type="submit" class="saveTemplateBtn btn btn-primary"><spring:theme code="text.button.create.new.template"/></button><br/>
            <a href="javascript:$.magnificPopup.close();" class="btn-block" id="magnific-create-close"><spring:theme code="cart.page.template.order.popup.cancel" /></a>

        </div>
    </form>
</div>

<div class="price-popup mfp-hide" id="delete-template-order">
    <h2 class="h1"><spring:theme code="template.order.deletion.popup.title"/></h2>
    <p><spring:theme code="template.order.deletion.popup.description"/></p>
    <c:url value="/your-business/ordertemplate/delete/${templateCode}" var="deleteTemplateUrl"/>

    <div class="row">
    	<div class="col-xs-6">
    	 	<a href="${deleteTemplateUrl}" class="btn btn-primary"><spring:theme code="template.order.deletion.popup.delete"/></a>
    	</div>
        <div class="col-xs-6">
      		<a href="javascript:$.magnificPopup.close();" class="btn btn-primary" id="magnific-delete-close"><spring:theme code="template.order.deletion.popup.cancel" /></a>
    	</div>
    </div>

</div>


