<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<cart:cartTotals cartData="${cartData}" showTaxEstimate="${taxEstimationEnabled}" isCart="true"/>
<cart:ajaxCartTotals/>