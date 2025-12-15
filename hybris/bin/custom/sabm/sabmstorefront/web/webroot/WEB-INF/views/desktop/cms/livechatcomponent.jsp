<%@ taglib prefix="c" uri="jakarta.tags.core" %>



    <c:set var="showLiveChat" value="${true}" scope="session" />

    <c:if test="${showLiveChat ==true}">

        <input type="hidden" id="liveChatCustomDetails" data-userpk="${user.gaUid}" data-email="${user.uid}"
            data-firstname="${user.firstName}" data-lastname="${user.lastName}"
            data-currentB2Bunit-id="${user.currentB2BUnit.uid}"
            data-currentB2Bunit-name="${user.currentB2BUnit.name}" />

        <!-- HybrisSVOC MIAW Livechat -->
        <input type="hidden" id="livechatAvailableFromHour" value="${livechatAvailableFromHour}" />
        <input type="hidden" id="livechatAvailableFromMin" value="${livechatAvailableFromMin}" />
        <input type="hidden" id="livechatAvailableToHour" value="${livechatAvailableToHour}" />
        <input type="hidden" id="livechatAvailableToMin" value="${livechatAvailableToMin}" />
        <input type="hidden" id="livechatMiawOrgId" value="${livechatMiawOrgId}" />
        <input type="hidden" id="livechatMiawDeploymentName" value="${livechatMiawDeploymentName}" />
        <input type="hidden" id="livechatMiawHostURL" value="${livechatMiawHostURL}" />
        <input type="hidden" id="livechatMiawScrtURL" value="${livechatMiawScrtURL}" />
        <input type="hidden" id="livechatMiawJsFileURL" value="${livechatMiawJsFileURL}" />
        <input type="hidden" id="livechatMiawSiteNameCUB" value="${livechatMiawSiteNameCUB}" />

        <c:if test="${isBDECustomer}">
            <input type="hidden" id="asahiStaffEmail" value="${asahiStaffEmail}" />
        </c:if>

        <script type="text/javascript"
            src="${staticHostPath}${siteStaticsContextPath}/js/livechat.js?${staticsVersion}"></script>

    </c:if>
