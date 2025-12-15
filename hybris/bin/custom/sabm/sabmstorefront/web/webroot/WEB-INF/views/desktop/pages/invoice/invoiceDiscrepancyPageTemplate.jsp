<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>


<c:if test="${isInvoiceDiscrepancyEnabled}">
    <template:page pageTitle="${pageTitle}" >
        <cms:pageSlot position="TopContentSlot" var="feature" element="div">
            <cms:component component="${feature}" evaluateRestriction="true"/>
        </cms:pageSlot>
        <div id="globalMessages">
            <common:globalMessages />
        </div>
        <cms:pageSlot position="BottomContentSlot" var="feature" element="div" >
            <cms:component component="${feature}" evaluateRestriction="true"/>
        </cms:pageSlot>

    </template:page>

</c:if>