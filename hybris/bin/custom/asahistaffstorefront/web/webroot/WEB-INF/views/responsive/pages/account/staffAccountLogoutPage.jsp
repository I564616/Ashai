<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user" %>
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
 	<c:url var="baseUrl" value="${scheme }://${localName }:${localPort }/samlsinglesignon/saml/asahiStaffPortal/sga/en/AUD/customer-search"/>
 	
		    <div id="page-portalLogin" class="item left">
		    	<div class="content-wrap">
		    		<img class="page-hero visible-sm-block visible-md-block visible-lg-block"
		    			src="/staffPortal/_ui/desktop/SABMiller/img/loading-page.png" alt="">
		    		<img class="page-hero visible-xs-block" src="/staffPortal/_ui/desktop/SABMiller/img/loading-page-mobile.png"
		    			alt="">
		    		<div class="clearfix"></div>
		    	</div>
		    </div>
		    <div class="item right">
				<div class="content-wrap">
		        </div>

		        <!-- Content for Error Page -->
				<div class="content-wrap">
					<svg class="icon-blue-cross">
						<use xlink:href="#icon-blue-cross"></use> 
					</svg>

				
		        	<div class="h3">You have been logged out of the BDE portal.</div>
						<c:url value="/login" var="backToLoginPageLink"/>
						<a href="${backToLoginPageLink}" class="btn btn-primary btn-large margin-top-30">Go to Login Page</a>
		        </div>
		        
		        	
		    </div>

</template:page>
