<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> 

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:url var="deliveryDateUpdateUrl" value="/cart/updateDeliveryDate" />
<c:url var="updateSABMdeliveryUrl"  value="/cart/updateSABMdelivery" />

<script id="deliveryDatesData" type="text/json">${ycommerce:generateJson(deliveryDatesConfigData)}</script> 
<script id="publicHolidayData" type="text/json">${ycommerce:generateJson(publicHolidayData)}</script> 
<%-- <input type="hidden" id="cutofftime" value="<spring:theme code='text.cutoff.deliverydate'/>&nbsp;<p style='margin-left:40px;color:red;'><b>${cutofftime}</b></p>" /> --%>

<%-- Get timezone --%>
<spring:message code="Australian.Eastern.Daylight.Time" var="AUDaylightTime" />
<c:set value="${empty plantcutofftimezone ? AUDaylightTime : plantcutofftimezone}" var="timezoneCode"/>
<c:set var="now" value="<%=new java.util.Date()%>"/>
<fmt:timeZone value="${plantcutofftimezone}">
    <fmt:formatDate value="${now}" type="both" pattern="Z" timeZone="${timezoneCode}" var="timezoneoffset" />
</fmt:timeZone>
<input type="hidden" id="cutofftime" value='{"text": "<spring:theme code='text.cutoff.deliverydate'/>", "styling": "margin-left:40px;color:red;", "cutofftime": "${cutofftime}", "plantcutofftimezone": "${timezoneoffset}"}' />

<a href="javascript:void(0)">	
	<svg class="icon-calendar01">
  	  <use xlink:href="#icon-calendar01"></use>    
	</svg>
	
	<label>


	<fmt:parseDate value="${selectedDeliveryDate}" pattern="dd/MM/yy" var="parsedDate"/>
	<fmt:formatDate value="${parsedDate}" pattern="EEE dd MMM" />
	<input data-provide="datepicker" data-date-format="D dd M" data-date-autoclose="true" data-value="" data-disabled-dates="${disabledDates}" data-enabled-dates="${enabledDates}" data-selected-date="${selectedDeliveryDate}" data-selected-timestamp="${selectedDeliveryTimestamp}" data-update-cart-url="${updateSABMdeliveryUrl}" data-update-url="${deliveryDateUpdateUrl}" class="delivery-header-input input-textonly hide" ng-model="headerDate" readonly="readonly">
	</label>
</a>

<div id="deliveryDatepickerHeader"></div>