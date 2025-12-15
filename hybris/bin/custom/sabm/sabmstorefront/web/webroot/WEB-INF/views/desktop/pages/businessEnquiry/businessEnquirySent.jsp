<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!-- DEV need Update -->
<%--<c:url var="sendUrl" value="/sendRequestEmail" />--%>
<%--<c:url value="/authenticatedContactUs" var="contactUsUrl"/>--%>

<spring:theme code="text.service.request.form.msg.please.select" var="pleaseSelect"/>
<spring:theme code="text.service.request.form.msg.general.inquiry" var="generalInquiry"/>
<spring:theme code="text.service.request.form.msg.general" arguments="${user.firstName} ${user.lastName},${user.email}" var="generalInquiryInfo"/>


<spring:theme code="text.service.request.form.area.delivery.enquiry" var="deliveryEnquiry"/>
<spring:theme code="text.service.request.form.area.return.request" var="returnRequest"/>
<spring:theme code="text.service.request.form.area.keg.complaint" var="kegComplaint"/>
<spring:theme code="text.service.request.form.area.warehouse.complaint" var="warehouseComplaint"/>
<spring:theme code="text.service.request.form.area.pricing.enquiry" var="pricingEnquiry"/>
<spring:theme code="text.service.request.form.area.ullage" var="ullage"/>
<spring:theme code="text.service.request.form.area.asset.request" var="assetRequest"/>
<spring:theme code="text.service.request.form.area.asset.maintenance" var="assetMaintenance"/>
<spring:theme code="text.service.request.form.area.product.enquiry" var="productEnquiry"/>
<spring:theme code="text.service.request.form.area.empty.pickpup" var="emptyPickup"/>
<spring:theme code="text.service.request.form.area.updte.options" var="updteOptions"/>
<spring:theme code="text.service.request.form.area.empty.pallet" var="emptyPallet"/>
<spring:theme code="text.service.request.form.area.website.errors" var="websiteErrors"/>
<spring:theme code="text.service.request.form.area.website.enquiries" var="websiteEnquiries"/>

<spring:theme code="text.service.request.form.msg.placeholder" var="msgPlaceholder"/>
<spring:theme code="text.service.request.form.msg.delivery.enquiry" var="msgDeliveryEnquiry"/>
<spring:theme code="text.service.request.form.msg.return.request" var="msgReturnRequest"/>
<spring:theme code="text.service.request.form.msg.keg.complaint" var="msgKegComplaint"/>
<spring:theme code="text.service.request.form.msg.warehouse.complaint" var="msgWarehouseComplaint"/>
<spring:theme code="text.service.request.form.msg.pricing.enquiry" var="msgPricingEnquiry"/>
<spring:theme code="text.service.request.form.msg.ullage" var="msgUllage"/>
<spring:theme code="text.service.request.form.msg.asset.request" var="msgAssetRequest"/>
<spring:theme code="text.service.request.form.msg.asset.maintenance" var="msgAssetMaintenance"/>
<spring:theme code="text.service.request.form.msg.product.enquiry" var="msgProductEnquiry"/>
<spring:theme code="text.service.request.form.msg.empty.pickpup" var="msgEmptyPickup"/>
<spring:theme code="text.service.request.form.msg.updte.options" var="msgUpdteOptions"/>
<spring:theme code="text.service.request.form.msg.empty.pallet" var="msgEmptyPallet"/>
<spring:theme code="text.service.request.form.msg.website.errors" var="msgWebsiteErrors"/>
<spring:theme code="text.service.request.form.msg.website.enquiries" var="msgWebsiteEnquiries"/>


<div class="row">
	<div class="col-md-8 service-request offset-bottom-medium">
		<h1 class="offset-bottom-small"><spring:theme code="text.business.request.sent.title"/></h1>
		<p class="offset-bottom-small"><spring:theme code="text.business.request.sent.p1"/></p>
		<p class="offset-bottom-small"><spring:theme code="text.business.request.sent.p2"/></p>
	  <br/>
		<a href="<c:url value="/Beer/c/10" />" class="btn btn-primary offset-bottom-small"><spring:theme
				code="text.business.request.sent.button.continue"/></a>
		<a href="<c:url value="/businessEnquiry" />" class="btn btn-secondary"><spring:theme
				code="text.business.request.sent.button.raise.another"/></a>

	</div>
</div>

