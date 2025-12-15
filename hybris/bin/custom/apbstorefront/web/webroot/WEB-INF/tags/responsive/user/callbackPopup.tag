<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>


<!-- Get close icon url-->
<spring:theme code="img.closeIcon" text="/" var="closeIconPath" />
<c:choose>
	<c:when test="${originalContextPath ne null}">
		<c:url value="${closeIconPath}" context="${originalContextPath}" var="closeIconUrl" />
	</c:when>
	<c:otherwise>
		<c:url value="${closeIconPath}" var="closeIconUrl" />
	</c:otherwise>
</c:choose>

<%-- <c:url value="/contactus/send" var="contactusSendUrl" />
<form:form modelAttribute="apbContactUsForm" method="post" enctype="multipart/form-data" action="${contactusSendUrl}"> --%>
<div id="callbackPopupLayer" class="modal fade callback-popup-layer" role="dialog" data-backdrop="false">
    <div class="modal-dialog callback-popup-container">
        <div class="modal-content">      
            <div id="formCallback" class="modal-body callback-popup">
                <form id="callbackForm">
                    <div class="row">
                        <div id="popupError" class="alert alert-danger callback-popup-text">
                            <span><spring:theme code="form.global.error"/></span>
                        </div>
                    </div>
                    <div class="row">
                        <div class="callback-popup-title callback-popup-text">
                            <span><b><spring:theme code="contactus.callback.popup.title"/></b></span>
                        </div>
                    </div>

                    <div class="row">
                        <div class="callback-popup-label callback-popup-text">
                            <span><spring:theme code="contactus.callback.popup.subtitle"/></span>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                            <div id="popupName" class="form-group">
                                <label class="control-label" for="name">
                                    <spring:theme code="contactus.name"/>
                                </label>
                                <input name="name" id="callbackName" class="form-control" maxlength="${inputNameMaxSize}"/>
                                <div class="help-block contactus" id="callbackNameHelpBlock">
                                    <span id="name.errors"><spring:theme code="contactus.name.invalid"/></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                            <div id="popupContactNumber" class="form-group">
                                <label class="control-label" for="contactNumber">
                                    <spring:theme code="contactus.contact.number"/>
                                </label>
                                <input name="contactNumber" id="callbackContactNumber" class="form-control" maxlength="${inputMaxSize}"/>
                                <div class="help-block contactus" id="callbackContactNumberHelpBlock">
                                    <span id="contactNumber.errors"><spring:theme code="contactus.contact.number.invalid"/></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                            <div id="popupFurtherDetail" class="form-group">
                                <label class="control-label" for="furtherDetail">
                                    <spring:theme code="contactus.further.detail"/> 
                                    <span id="optional-text" style="padding-left: 3px;"><spring:theme code="login.optional"/></span>
                                </label>
                                <textarea name="furtherDetail" id="callbackFurtherdetail" class="textarea form-control" maxlength="${furtherDetailsMaxSize}"></textarea>
                            </div>
                        </div>
                    </div>

                    <input id="callbackEnquiryType" name="enquiryType" class="callback-hidden-form-element"/>
                    <input id="callbackAccountNo" name="accountNumber" class="callback-hidden-form-element"/>
                    <input id="callbackCompanyName" name="companyName" class="callback-hidden-form-element"/>
                    <input id="callbackEmailAddress" name="emailAddress" class="callback-hidden-form-element"/>
                    <input id="callbackSalesRepName" name="asahiSalesRepName" class="callback-hidden-form-element"/>
                    <input id="callbackSalesRepEmail" name="asahiSalesRepEmail" class="callback-hidden-form-element"/>

                    <div class="row">
                        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                            <div class="form-actions clearfix">
                                <button id="popupSendButton" type="button"
                                    class="btn btn-default btn-block btn-vd-primary">
                                    <spring:theme code="contactus.callback.popup.send"/>
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>

            <div id="formSuccess" class="modal-body callback-success">
                <div class="row">
                    <div class="callback-success-title callback-success-text">
                        <span><b><spring:theme code="contactus.callback.success.title"/></b></span>
                    </div>
                </div>

                <div class="row">
                    <div class="callback-popup-label callback-success-text">
                        <span><spring:theme code="contactus.callback.success.received.request"/></span>
                    </div>
                </div>

                <div class="row">
                    <div class="callback-popup-label callback-success-text bottom">
                        <span><spring:theme code="contactus.callback.success.receive.callback"/></span>
                    </div>
                </div>
            </div>
            <div class="callback-success-close" data-dismiss="modal">
                <img src="${closeIconUrl}">
            </div>
        </div>
    </div>
</div>
<%-- </form:form> --%>