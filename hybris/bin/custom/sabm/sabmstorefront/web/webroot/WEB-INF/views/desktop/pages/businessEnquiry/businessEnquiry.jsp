<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="service" tagdir="/WEB-INF/tags/desktop/service"%>
<!-- DEV need Update -->
<c:url var="sendUrl" value="/sendRequestEmail" />
<c:url value="/authenticatedContactUs" var="contactUsUrl"/>

<spring:theme code="text.service.request.form.msg.please.select" var="pleaseSelect"/>
<spring:theme code="text.service.request.form.msg.general.inquiry" var="generalInquiry"/>
<spring:theme code="text.service.request.form.msg.general" arguments="${user.firstName} ${user.lastName},${user.email}" var="generalInquiryInfo"/>

<spring:theme code="text.service.request.form.area.business.enquiry" var="businessEnquiry"/>
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


<div class="service-request offset-bottom-medium" ng-controller="serviceCtrl" ng-init="serviceRequestInit()" ng-cloak>
    <%--
    --%>

    <c:choose>
        <c:when test="${enquiryType == 'DELIVERY'}">
            <div class="h1"><spring:theme code="text.support.request.type.delivery.enquiry.title"/></div>
        </c:when>
        <c:when test="${enquiryType == 'KEG'}">
            <div class="h1"><spring:theme code="text.support.request.type.keg.enquiry.title"/></div>
        </c:when>
        <c:when test="${enquiryType == 'PRODUCT'}">
            <div class="h1"><spring:theme code="text.support.request.type.product.enquiry.title"/></div>
        </c:when>
        <c:when test="${enquiryType == 'GENERAL'}">
            <div class="h1"><spring:theme code="text.support.request.type.general.enquiry.title"/></div>
        </c:when>
    </c:choose>

  <%--<p><spring:theme code="text.service.request.description" arguments="${contactUsUrl }"/></p>--%>

        <c:choose>
            <c:when test="${enquiryType == 'DELIVERY'}">
                <p><spring:theme code="text.service.request.form.area.delivery.enquiry.info"/></p>
            </c:when>
            <c:when test="${enquiryType == 'KEG'}">
                <p><spring:theme code="text.service.request.form.area.keg.enquiry.info"/></p>
            </c:when>
            <c:when test="${enquiryType == 'PRODUCT'}">
                <p><spring:theme code="text.service.request.form.area.product.enquiry.info" arguments="/your-business/invoicediscrepancy" /></p>
            </c:when>
            <c:when test="${enquiryType == 'GENERAL'}">
                <p><spring:theme code="text.service.request.form.area.general.enquiry.info"/></p>
            </c:when>
        </c:choose>


  	<form:form id="serviceRequest" name="serviceRequest" novalidate="novalidate">
  		<p><spring:theme code="text.contactus.requiredfields" /></p> 			
  		<service:commonRequestFields/>

  		<div class="row">
  			<div class="col-xs-12 col-md-4 form-group">
  	       			<label><spring:theme code="text.service.request.form.area"/><span class="required">*</span></label>
               		<div class="select">
    					 	<span class="arrow"></span>
               			<select name="request_type" class="cc-expiry-form form-control validate-input" ng-model="sr.type" required>
               				<option value="" disabled selected>${pleaseSelect}</option>
							<c:forEach var="subType" items="${subTypeList}">
								<option value="${subType.code}">${subType.name}</option>
							</c:forEach>
  							</select>
    					</div>
    					<span class="error" ng-show="(notComplete || serviceRequest.request_type.$touched) && serviceRequest.request_type.$invalid">Please select a request type from the list above</span>
  		      	<div>
                </div>
            </div>
  		</div>
  		<div class="clearfix"></div>

  		<!-- Start of Changable Form -->

  		<service:delivery/>
  		<service:deliveryIssue/>

  		
		<service:kegPickup/>
		<service:keg/>
		

  		<service:return/>
		<service:price/>
		<service:product/>
		<service:palletPickup/>

        <service:update/>
		<service:website/>
		<service:general/>
		<service:websiteEnq/>
		
		<service:orderEnquiry/>
		<service:updateExistingEnquiry/>
  				
  		<!-- End of Changable Form -->
  		      	
        	<div class="form-group">
        		<button type="button" ng-click="checkValid(serviceRequest,sr.type)" class="btn btn-primary" id="form-btn"><spring:theme code="text.service.request.form.send"/></button>
        		<span class="error-bold" ng-show="notComplete">
					${msgWebsiteErrors}
        		</span>
        	</div>
  	</form:form>

</div>
