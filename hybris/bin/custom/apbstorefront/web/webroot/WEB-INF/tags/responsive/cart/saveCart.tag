<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>

<%--
    ~ /*
    ~  * [y] hybris Platform
    ~  *
    ~  * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
    ~  * All rights reserved.
    ~  *
    ~  * This software is the confidential and proprietary information of SAP
    ~  * ("Confidential Information"). You shall not disclose such Confidential
    ~  * Information and shall use it only in accordance with the terms of the
    ~  * license agreement you entered into with SAP.
    ~  *
    ~  */
--%>

<spring:htmlEscape defaultHtmlEscape="true" />

<!-- Updated the page for cart page customization ACP-25-->
<div class="row desktop-row-fix">
	<div class="col-xs-12 col-sm-12 col-md-4">
	<a href="${request.contextPath}/my-account/saved-carts" id="savedCartHeading" class="importSavedCart">
    	<label:message messageCode="basket.import.products.from.template.apb"/>
	</a>
	</div>
	<div class="clearfix visible-xs"></div>
	<div class="col-xs-12 col-sm-12 col-md-8">
		<spring:url value="/cart/save" var="actionUrl" htmlEscape="false"/>
		<cart:saveCartModal titleKey="text.save.cart.title" actionUrl="${actionUrl}" messageKey="basket.save.cart.info.msg"/>
	</div>
	<div class="clearfix visible-xs"></div>
</div>


