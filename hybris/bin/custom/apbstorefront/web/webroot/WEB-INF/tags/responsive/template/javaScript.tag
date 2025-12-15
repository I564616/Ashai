<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/responsive/template/cms" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>

<c:url value="/" var="siteRootUrl"/>

<template:javaScriptVariables/>

<c:choose>
	<c:when test="${wro4jEnabled}">
	  	<script type="text/javascript" src="${contextPath}/wro/all_responsive.js"></script>
	  	<script type="text/javascript" src="${contextPath}/wro/addons_responsive.js"></script>
	</c:when>
	<c:otherwise>
		<c:choose>
            <c:when test="${cmsSite.uid eq 'sga'}">
                <%-- jquery --%>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery-3.5.1.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery-migrate-3.0.0.min.js"></script>
				

				<%-- bootstrap --%>
				<script type="text/javascript" src="${commonResourcePath}/bootstrap/dist/js/bootstrap.min.js"></script>

				<%-- plugins --%>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/enquire.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/Imager.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.blockUI-2.66.js"></script>
                <script type="text/javascript" src="${commonResourcePath}/js-sga/spinningloader.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.colorbox-min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.form.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.hoverIntent.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.pstrength.custom-1.2.0.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.syncheight.custom.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.tabs.custom.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery-ui-1.11.2.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.zoom.custom.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/owl.carousel.custom.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.tmpl-1.0.0pre.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.currencies.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.waitforimages.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.slideviewer.custom.1.2.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/waypoints.min.1.1.5.js"></script>

				<%-- Custom ACC JS --%>

				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.orderhistory.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.global.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.address.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.autocomplete.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.carousel.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.cart.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.cartitem.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.checkout.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.checkoutaddress.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.checkoutsteps.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.cms.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.colorbox.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.common.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.forgottenpassword.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.hopdebug.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.imagegallery.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.langcurrencyselector.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.minicart.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.navigation.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.order.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.paginationsort.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.payment.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.paymentDetails.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.pickupinstore.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.product.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.productDetail.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.quickview.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.ratingstars.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.refinements.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.silentorderpost.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.tabs.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.termsandconditions.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.track.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.storefinder.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.futurelink.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.productorderform.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.savedcarts.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.multidgrid.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.quickorder.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.quote.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.invoicespayments.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.directdebit.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.orderpayaccess.js"></script>

				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.csv-import.js"></script>

				<script type="text/javascript" src="${commonResourcePath}/js-sga/_autoload.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/registration.js"></script>


				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.asahiproductlisting.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/companydetails.js"></script>

				<script type="text/javascript" src="${commonResourcePath}/js-sga/contactus.js"></script>
				<!-- Manage Keg Returns -->
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.kegreturns.js"></script>

				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.checkoutpayment.js"></script>	
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.savedcards.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.password.js"></script>

                <!-- new registered 20/01/2022 -->
                <script type="text/javascript" src="${commonResourcePath}/js-sga/acc.usernotifications.js"></script>
                <script type="text/javascript" src="${commonResourcePath}/js-sga/acc.manageprofiles.js"></script>

                <!-- new registered 01/06/2022 -->
                <script type="text/javascript" src="${commonResourcePath}/js-sga/acc.dialog.js"></script>

                <<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.adhoc.js"></script>
				<%-- Custom SGA ACC JS --%>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.sgalogin.js"></script>

				<%-- SGA ACC Staff Checkout JS --%>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.staffcheckout.js"></script>

				<%-- SGA Homepage Recommendations Component--%>
				<script type="text/javascript" src="${commonResourcePath}/js-sga/acc.recommendation.js"></script>
                <script type="text/javascript" src="${commonResourcePath}/js-sga/acc.planogram.js"></script>

				<%-- Cms Action JavaScript files --%>
				<c:forEach items="${cmsActionsJsFiles}" var="actionJsFile">
					<script type="text/javascript" src="${commonResourcePath}/js-sga/cms/${actionJsFile}"></script>
				</c:forEach>

				<%-- AddOn JavaScript files --%>
				<c:forEach items="${addOnJavaScriptPaths}" var="addOnJavaScript">
					<script type="text/javascript" src="${addOnJavaScript}"></script>
				</c:forEach>
            </c:when>
            <c:otherwise>
						<%-- jquery --%>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery-3.5.1.min.js"></script>

				<%-- bootstrap --%>
				<script type="text/javascript" src="${commonResourcePath}/bootstrap/dist/js/bootstrap.min.js"></script>

				<%-- plugins --%>
				<script type="text/javascript" src="${commonResourcePath}/js/enquire.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/Imager.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.blockUI-2.66.js"></script>
                <script type="text/javascript" src="${commonResourcePath}/js/spinningloader.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.colorbox-min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.form.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.hoverIntent.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.pstrength.custom-1.2.0.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.syncheight.custom.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.tabs.custom.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery-ui-1.11.2.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.zoom.custom.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/owl.carousel.custom.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.tmpl-1.0.0pre.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.currencies.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.waitforimages.min.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/jquery.slideviewer.custom.1.2.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/waypoints.min.1.1.5.js"></script>

				<%-- Custom ACC JS --%>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.global.js?v=1.0"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.address.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.autocomplete.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.carousel.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.cart.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.cartitem.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.checkout.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.checkoutaddress.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.checkoutsteps.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.cms.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.colorbox.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.common.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.forgottenpassword.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.hopdebug.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.imagegallery.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.langcurrencyselector.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.minicart.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.navigation.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.order.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.paginationsort.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.payment.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.paymentDetails.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.pickupinstore.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.product.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.productDetail.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.quickview.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.ratingstars.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.refinements.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.silentorderpost.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.tabs.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.termsandconditions.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.track.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.storefinder.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.futurelink.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.productorderform.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.savedcarts.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.multidgrid.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.quickorder.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.quote.js"></script>

				<script type="text/javascript" src="${commonResourcePath}/js/acc.csv-import.js"></script>

				<script type="text/javascript" src="${commonResourcePath}/js/_autoload.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/registration.js?v=1"></script>


				<script type="text/javascript" src="${commonResourcePath}/js/acc.asahiproductlisting.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/companydetails.js"></script>

				<script type="text/javascript" src="${commonResourcePath}/js/contactus.js"></script>

                <!-- new registered 01/06/2022 -->
                <script type="text/javascript" src="${commonResourcePath}/js/acc.dialog.js"></script>

                <%--Manage Keg Returns--%>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.kegreturns.js"></script>

				<script type="text/javascript" src="${commonResourcePath}/js/acc.checkoutpayment.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.savedcards.js"></script>
				<script type="text/javascript" src="${commonResourcePath}/js/acc.password.js"></script>

				<%-- Cms Action JavaScript files --%>
				<c:forEach items="${cmsActionsJsFiles}" var="actionJsFile">
					<script type="text/javascript" src="${commonResourcePath}/js/cms/${actionJsFile}"></script>
				</c:forEach>

				<%-- AddOn JavaScript files --%>
				<c:forEach items="${addOnJavaScriptPaths}" var="addOnJavaScript">
					<script type="text/javascript" src="${addOnJavaScript}"></script>
				</c:forEach>
            </c:otherwise>
        </c:choose>
		
	</c:otherwise>
</c:choose>


<cms:previewJS cmsPageRequestContextData="${cmsPageRequestContextData}" />
