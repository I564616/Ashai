<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>


<template:page pageTitle="${pageTitle}">

	<div class="row">
		<div id="globalMessages" class="col-sm-8">
			<common:globalMessages/>
		</div>
	</div>
	
	<c:set var="req" value="${pageContext.request}"/>
	<c:set var="scheme" value="${req.scheme}"/>
	<c:set var="localName" value="${req.localName}"/>
	<c:set var="localPort" value="${req.localPort}"/>
	<c:url var="baseUrl" value="${scheme }://${localName }:${localPort }/samlsinglesignon/saml/staffPortal/sabmStore/en/customer-search"/>
	
		    <div id="ssologin" class="item left">
				<img class="page-hero visible-sm-block visible-md-block visible-lg-block" src="/staffPortal/_ui/desktop/SABMiller/img/loading-page.png" alt="Friends drinking beer">
				<img class="page-hero visible-xs-block" src="/staffPortal/_ui/desktop/SABMiller/img/loading-page-mobile.png" alt="Friends drinking beer">
				<div class="clearfix"></div>       
		    </div>
		    <div class="item right">
				<div class="content-wrap">
					<svg class="icon-loader">
						<use xlink:href="#icon-loader"></use> 
					</svg>
				
		        	<div class="h2">Just a moment.<br>
		        	We're redirecting you to the portal.</div>
		        </div>

		        <!-- Content for Error Page -->
				<!-- <div class="content-wrap">
					<svg class="icon-blue-cross">
						<use xlink:href="#icon-blue-cross"></use> 
					</svg>

				
		        	<div class="h3">There seems to be a problem with your credentials. Please click the button below to try again. If the issue persists please call the Service Desk on 2244.</div>
		        </div> -->
		    </div>
	    
	
	
	
<!-- 	<div class="row"> -->
<%-- 		<c:url value="/j_spring_security_check" var="loginActionUrl" /> --%>
<%-- 		<user:login actionNameKey="login.login" action="${loginActionUrl}"/> --%>
<!-- 	</div> -->

</template:page>
