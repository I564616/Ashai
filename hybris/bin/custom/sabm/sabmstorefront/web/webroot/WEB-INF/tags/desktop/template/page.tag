<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true" %>
<%@ attribute name="pageCss" required="false" fragment="true" %>
<%@ attribute name="pageScripts" required="false" fragment="true" %>
<%@ attribute name="hideHeaderLinks" required="false" %>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header" %>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/desktop/common/footer" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<template:master pageTitle="${pageTitle}">

	<jsp:attribute name="pageCss">
		<jsp:invoke fragment="pageCss"/>
	</jsp:attribute>
 
	<jsp:attribute name="pageScripts">
		<jsp:invoke fragment="pageScripts"/>
	</jsp:attribute>

	<jsp:body>

		<div id="overlay">
			<svg class="icon-loader">
				<use xlink:href="#icon-loader"></use> 
			</svg>
		</div>
		<div class="container-lg main-content" data-currency-iso-code="${currentCurrency.isocode}">
		<div class="loading-message" 
		data-login="<spring:theme code="text.loadingMessage.login" />"
		data-simulate="<spring:theme code="text.loadingMessage.simulate" />"
		data-checkout="<spring:theme code="text.loadingMessage.checkout" />"
		data-confirm="<spring:theme code="text.loadingMessage.confirm" />"
		data-request="<spring:theme code="text.loadingMessage.request" />" >
			<div class="loading-content">
				<!-- <img src="/_ui/desktop/SABMiller/img/spinner.gif" alt="Loading Image"> -->
				<svg class="icon-loader">
					<use xlink:href="#icon-loader"></use> 
				</svg>
				<div id="loadingText" class="loading-text"><p id="loadingTextP"></p></div>
			</div>
		</div>

		<script>
			var CUPRefreshFlag = "<c:out value='${cupRefreshInProgress}' />",
				message = "<spring:theme code="text.loadingMessage.login" />",
				home = document.getElementsByClassName("page-homepage").length,
				text = document.getElementById('loadingText'),
				textP = document.getElementById('loadingTextP');

				console.log(CUPRefreshFlag);

			if(home && CUPRefreshFlag == 'true'){
			    textP.innerHTML = message;
			    text.style.display = "block";
			}
		</script>
		<header:header hideHeaderLinks="${hideHeaderLinks}"/>
			<div class="container container-main">
				<div ng-controller="messagesCtrl" ng-cloak>
					<div class="global-message {{messageType}}" ng-show="messageType">{{message}}</div>
				</div>
				<spring:theme code="text.skipToContent" var="skipToContent"/>
				<a href="#skip-to-content" class="skiptocontent" data-role="none">${skipToContent}</a>
				<spring:theme code="text.skipToNavigation" var="skipToNavigation"/>
				<a href="#skiptonavigation" class="skiptonavigation" data-role="none">${skipToNavigation}</a>
				<a id="skiptonavigation"></a>
				
				<header:bottomHeader />
				
			
				<a id="skip-to-content"></a>
					<jsp:doBody/>
			</div>
			<div class="light-grey-backround footer-wrap">
				<div class="container">
					<footer:footer/>
				</div>
			</div>
		</div>
	</jsp:body>
	
</template:master>
