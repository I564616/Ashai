<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true" %>
<%@ attribute name="metaDescription" required="false" %>
<%@ attribute name="metaKeywords" required="false" %>
<%@ attribute name="pageCss" required="false" fragment="true" %>
<%@ attribute name="pageScripts" required="false" fragment="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="addonScripts" tagdir="/WEB-INF/tags/desktop/common/header" %>
<%@ taglib prefix="analytics" tagdir="/WEB-INF/tags/shared/analytics" %>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/shared/debug" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="htmlmeta" uri="http://hybris.com/tld/htmlmeta" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="mock" tagdir="/WEB-INF/tags/mock" %>
<%@ taglib prefix="livechat" tagdir="/WEB-INF/tags/desktop/common/livechat" %>
<%@ taglib prefix="services" tagdir="/WEB-INF/tags/shared/services" %>

<c:set var="impersonate" value="${not empty impersonate}"/>
<c:set var="viewOnly" value="${false}"/>
<c:set var="placeOrderDisable" value="${false}" scope="request"/>
 <c:set var="bdeUser" value="${false}" scope="request"/>
<sec:authorize access="hasAnyRole('ROLE_BDEVIEWONLYGROUP')">
   <c:set var="bdeUser" value="${true}" scope="request"/>
   <c:set var="placeOrderDisable" value="${!isBdeOrderingEnabled}" scope="request"/>
</sec:authorize>

 <spring:eval expression="@asahiConfigurationService.getString('cub.cors.list', 'xxxx')" var="allowedCorsOrigins" scope="application"/>

<!DOCTYPE html>
<html id="CUB" lang="${currentLanguage.isocode}" class="">
<head>
       <title>
              ${not empty pageTitle ? pageTitle : not empty cmsPage.title ? cmsPage.title : 'Accelerator Title'}
       </title>
       <%-- Meta Content --%>
       <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
       <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
       <meta name="viewport" content="width=device-width, initial-scale=1">
       
       <%-- <meta http-equiv="Content-Security-Policy" content="script-src ${staticHostPath} https://abinbevapacsouth--apacsuat.cs5.my.salesforce.com https://static.lightning.force.com https://abinbevapacsouth--apacsuat.cs72.my.salesforce.com https://apacsuat-free-1234f56bff7-1273763c43e-15f582f55fe.cs5.force.com https://apacsuat-free-1234f56bff7-1273763c43e-15f582f55fe.cs72.force.com https://www.merchantsuite.com/ https://code.angularjs.org https://ssl.google-analytics.com/ https://cdn.segment.com/ https://www.googletagmanager.com https://tagmanager.google.com https://www.google-analytics.com/ https://static.hotjar.com https://script.hotjar.com/ https://js-agent.newrelic.com https://bam.nr-data.net  ${baseUrl} https://c.la1-c2-ukb.salesforceliveagent.com https://d.la1-c2-ukb.salesforceliveagent.com https://d.la1-c2-hnd.salesforceliveagent.com https://c.la1-c1cs-ukb.salesforceliveagent.com https://d.la1-c1cs-ukb.salesforceliveagent.com https://d.la1-c1cs-hnd.salesforceliveagent.com https://service.force.com https://abinbevapacsouth--laacc.cs5.my.salesforce.com https://laacc-free-1234f56bff7-1273763c43e-15f582f55fe.cs5.force.com  https://free-1234f56bff7-1273763c43e-15f582f55fe.secure.force.com https://abinbevapacsouth.my.salesforce.com https://abinbevapacsouth--c.ap8.visual.force.com 'unsafe-inline' 'unsafe-eval' "/>
        --%>
        <c:set var="cspMetaContent"
       value="script-src 'self' ${staticHostPath} ${allowedCorsOrigins} ${baseUrl} 'unsafe-inline' 'unsafe-eval'" />
          <meta http-equiv="Content-Security-Policy" content="${cspMetaContent}"/>
         
       <%-- Additional meta tags --%>
       <htmlmeta:meta items="${metatags}"/>

       <%--[if IE 9]>
              <script>
               document.documentElement.className+=' notify-mode';
           </script>
    <![endif]--%>

    <c:set var="appleIcon" scope="session" value="/_ui/desktop/SABMiller/img/"/>
    <link rel="apple-touch-icon" href="${appleIcon}/iPhone&iPodTouch_57x57.png"/>
    <link rel="apple-touch-icon" sizes="72x72" href="${appleIcon}/ipad_72x72.png"/>
    <link rel="apple-touch-icon" sizes="114x114" href="${appleIcon}/iPhone&iPodTouch_retina_114x114.png"/>
    <link rel="apple-touch-icon" sizes="144x144" href="${appleIcon}/ipad_retina_144x144.png"/>
    <link rel="icon" sizes="196x196" href="${appleIcon}/ipad_retina_144x144.png"/>


    <%-- Favourite Icon --%>
       <spring:theme code="img.favIcon" text="/" var="favIconPath"/>
    <link rel="shortcut icon" type="image/x-icon" media="all" href="${favIconPath}" />
       <%-- CSS Files Are Loaded First as they can be downloaded in parallel --%>
       <template:styleSheets/>
       <%-- Inject any additional CSS required by the page --%>
       <jsp:invoke fragment="pageCss"/>
       <analytics:analytics/>    

       <%-- MOCK DATA FOR TESTING
       <mock:deliveryModesMock/> --%>
       
</head> 
<body class="loading ${pageBodyCssClasses} ${cmsPageRequestContextData.liveEdit ? ' yCmsLiveEdit' : ''} language-${currentLanguage.isocode} <c:if test="${impersonate}"> impersonation-mode </c:if> <c:if test="${viewOnly}"> view-only-mode </c:if>" breakpoint="{420:'ngMobile', 768:'ngTablet', 990:'ngDesktop', 1200:'ngDesktopLg'}">
       <!-- Google Tag Manager (noscript) -->
       <noscript>
              <iframe src="https://www.googletagmanager.com/ns.html?id=${googleTagManagerId}" height="0" width="0" style="display:none;visibility:hidden"></iframe>
       </noscript>
       <!-- End Google Tag Manager (noscript) -->
       
       <%-- Inject SVG sprites here --%>
       <template:svg/>

       <noscript> 
              <div class="top-error active noscript">
                     <div class="container">
                           <p><spring:theme code="system.error.noscript" /></p>
                     </div>
              </div> 
       </noscript> 

       <div class="top-error browseup">
              <div class="container">
                     <p><spring:theme code="system.error.browsehappy" /></p>
              </div> 
       </div>

       <%-- Inject the page body here --%>
       <jsp:doBody/>
       <form name="accessiblityForm">
              <input type="hidden" id="accesibility_refreshScreenReaderBufferField" name="accesibility_refreshScreenReaderBufferField" value=""/>
       </form>
       <div id="ariaStatusMsg" class="skip" role="status" aria-relevant="text" aria-live="polite"></div>
       <%-- Load JavaScript required by the site --%>
       <template:javaScript/>

       <%-- Add merchant suite API --%>
       <services:merchantSuiteScripts/>
       
       <%-- Inject any additional JavaScript required by the page --%>
       <jsp:invoke fragment="pageScripts"/>
       <addonScripts:addonScripts/>

       <%-- Livechat code starts--%>
       <livechat:livechat/>

      <script type="text/javascript">
           var scriptSrc = $('#livechatMiawJsFileURL').val();
           var scriptElement = document.createElement('script');
           scriptElement.type = 'text/javascript';
           scriptElement.src = scriptSrc;

           document.head.appendChild(scriptElement);
      </script>

       <%-- Livechat code ends--%>

    <%-- check if segment tracking enabled --%>
    <c:set var="isSegmentEnabled" value="${true}"/>
    <input type="hidden" id="isSegmentEnabled" value="${isSegmentEnabled}" />
    <c:if test="${isSegmentEnabled}">
           <analytics:segmentAnalytics1.1/>
           <%--<analytics:segmentAnalyticsScripts/>--%>
    </c:if>
    
    
    
<script type="text/javascript">
piAId = '865042';
piCId = '21973';
piHostname = 'pi.pardot.com';

(function() {
function async_load(){
var s = document.createElement('script'); s.type = 'text/javascript';
s.src = ('https:' == document.location.protocol ? 'https://pi' : 'http://cdn') + '.pardot.com/pd.js';
var c = document.getElementsByTagName('script')[0]; c.parentNode.insertBefore(s, c);
}
if(window.attachEvent) { window.attachEvent('onload', async_load); }
else { window.addEventListener('load', async_load, false); }
})();
</script>
    
</body>
<debug:debugFooter/>
</html>

