<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>


<template:page pageTitle="${pageTitle}">
    <div class="row">
        <div id="globalMessages">
            <common:globalMessages/>
        </div>
    </div>
	<div class="row relative">
	    <div class="col-md-6 col-md-push-6 ">
	        <user:forgottenEmail/>
	    </div>
	    <div class="col-md-6 col-md-pull-6 forgotpwd-features ">
            <user:forgottenPwd/>
        </div>
	</div>
</template:page>